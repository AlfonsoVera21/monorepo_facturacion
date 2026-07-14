package com.factuec.application.usecase;

import com.factuec.application.port.out.EmailPort;
import com.factuec.application.port.out.RideGeneratorPort;
import com.factuec.config.FactuEcProperties;
import com.factuec.domain.enums.AuditAction;
import com.factuec.domain.enums.EstadoEnvioCorreo;
import com.factuec.domain.enums.EstadoSri;
import com.factuec.infrastructure.persistence.entity.ComprobanteEmailEnvioEntity;
import com.factuec.infrastructure.persistence.entity.ComprobanteEntity;
import com.factuec.infrastructure.persistence.repository.ComprobanteEmailEnvioRepository;
import com.factuec.shared.exception.BusinessException;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ComprobanteEmailUseCase {
    private static final int MAX_ERROR_LENGTH = 4000;

    private final ComprobanteEmailEnvioRepository emailEnvioRepository;
    private final RideGeneratorPort rideGeneratorPort;
    private final EmailPort emailPort;
    private final FactuEcProperties properties;
    private final AuditService auditService;

    public ComprobanteEmailUseCase(ComprobanteEmailEnvioRepository emailEnvioRepository,
                                   RideGeneratorPort rideGeneratorPort,
                                   EmailPort emailPort,
                                   FactuEcProperties properties,
                                   AuditService auditService) {
        this.emailEnvioRepository = emailEnvioRepository;
        this.rideGeneratorPort = rideGeneratorPort;
        this.emailPort = emailPort;
        this.properties = properties;
        this.auditService = auditService;
    }

    @Transactional
    public void registrarYEnviarAutorizado(ComprobanteEntity comprobante) {
        if (comprobante.getEstadoSri() != EstadoSri.AUTORIZADO) {
            return;
        }
        procesarEnvio(comprobante, false);
    }

    @Transactional
    public void enviarAhora(ComprobanteEntity comprobante) {
        if (comprobante.getEstadoSri() != EstadoSri.AUTORIZADO) {
            throw new BusinessException("Solo se puede enviar correo de comprobantes autorizados");
        }
        procesarEnvio(comprobante, true);
    }

    @Transactional
    public int procesarPendientes() {
        int procesados = 0;
        procesados += procesarPorEstado(EstadoEnvioCorreo.PENDIENTE);
        procesados += procesarPorEstado(EstadoEnvioCorreo.ERROR);
        return procesados;
    }

    private int procesarPorEstado(EstadoEnvioCorreo estado) {
        List<ComprobanteEmailEnvioEntity> envios = emailEnvioRepository.findByEstadoOrderByCreatedAtAsc(estado);
        int procesados = 0;
        for (ComprobanteEmailEnvioEntity envio : envios) {
            if (envio.getComprobante().getEstadoSri() == EstadoSri.AUTORIZADO) {
                procesarEnvio(envio.getComprobante(), envio, false);
                procesados++;
            }
        }
        return procesados;
    }

    private void procesarEnvio(ComprobanteEntity comprobante, boolean forzarReenvio) {
        ComprobanteEmailEnvioEntity envio = emailEnvioRepository.findByComprobanteId(comprobante.getId())
                .orElseGet(() -> nuevoEnvio(comprobante));
        procesarEnvio(comprobante, envio, forzarReenvio);
    }

    private void procesarEnvio(ComprobanteEntity comprobante, ComprobanteEmailEnvioEntity envio, boolean forzarReenvio) {
        sincronizarDatos(envio, comprobante);

        if (envio.getEstado() == EstadoEnvioCorreo.ENVIADO && !forzarReenvio) {
            emailEnvioRepository.save(envio);
            return;
        }

        String destinatario = normalizar(comprobante.getCliente().getCorreo());
        if (destinatario == null) {
            envio.setEstado(EstadoEnvioCorreo.SIN_CORREO);
            envio.setUltimoError("Cliente sin correo configurado");
            emailEnvioRepository.save(envio);
            auditService.log(AuditAction.ENVIO_CORREO, "Comprobante", comprobante.getId(),
                    "Envio de correo omitido", "Cliente sin correo configurado");
            return;
        }

        if (!properties.mail().enabled()) {
            if (envio.getEstado() != EstadoEnvioCorreo.ENVIADO) {
                envio.setEstado(EstadoEnvioCorreo.PENDIENTE);
            }
            envio.setUltimoError("SMTP deshabilitado; envio pendiente");
            emailEnvioRepository.save(envio);
            auditService.log(AuditAction.ENVIO_CORREO, "Comprobante", comprobante.getId(),
                    "Envio de correo pendiente", "SMTP deshabilitado");
            return;
        }

        intentarEnviar(comprobante, envio, forzarReenvio);
    }

    private ComprobanteEmailEnvioEntity nuevoEnvio(ComprobanteEntity comprobante) {
        ComprobanteEmailEnvioEntity envio = new ComprobanteEmailEnvioEntity();
        envio.setComprobante(comprobante);
        envio.setEstado(EstadoEnvioCorreo.PENDIENTE);
        return envio;
    }

    private void sincronizarDatos(ComprobanteEmailEnvioEntity envio, ComprobanteEntity comprobante) {
        envio.setComprobante(comprobante);
        envio.setDestinatario(normalizar(comprobante.getCliente().getCorreo()));
        envio.setAsunto("Comprobante electronico " + comprobante.getNumeroCompleto());
    }

    private void intentarEnviar(ComprobanteEntity comprobante, ComprobanteEmailEnvioEntity envio, boolean forzarReenvio) {
        Instant now = Instant.now();
        envio.setIntentos(envio.getIntentos() + 1);
        envio.setUltimoIntentoAt(now);
        try {
            byte[] ride = rideGeneratorPort.generateRide(comprobante);
            emailPort.sendComprobante(comprobante, ride);
            envio.setEstado(EstadoEnvioCorreo.ENVIADO);
            envio.setEnviadoAt(now);
            envio.setUltimoError(null);
            emailEnvioRepository.save(envio);
            auditService.log(AuditAction.ENVIO_CORREO, "Comprobante", comprobante.getId(),
                    "Comprobante enviado por correo", envio.getDestinatario());
        } catch (RuntimeException exception) {
            envio.setEstado(EstadoEnvioCorreo.ERROR);
            envio.setUltimoError(limit(exception.getMessage(), MAX_ERROR_LENGTH));
            emailEnvioRepository.save(envio);
            auditService.log(AuditAction.ENVIO_CORREO, "Comprobante", comprobante.getId(),
                    "Error al enviar correo", limit(exception.getMessage(), MAX_ERROR_LENGTH));
            if (forzarReenvio) {
                throw exception;
            }
        }
    }

    private String normalizar(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String limit(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}
