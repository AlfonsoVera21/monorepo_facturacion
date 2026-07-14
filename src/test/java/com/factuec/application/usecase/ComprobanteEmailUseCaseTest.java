package com.factuec.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.factuec.application.port.out.EmailPort;
import com.factuec.application.port.out.RideGeneratorPort;
import com.factuec.config.FactuEcProperties;
import com.factuec.domain.enums.EstadoEnvioCorreo;
import com.factuec.domain.enums.EstadoSri;
import com.factuec.infrastructure.persistence.entity.ClienteEntity;
import com.factuec.infrastructure.persistence.entity.ComprobanteEmailEnvioEntity;
import com.factuec.infrastructure.persistence.entity.ComprobanteEntity;
import com.factuec.infrastructure.persistence.repository.ComprobanteEmailEnvioRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class ComprobanteEmailUseCaseTest {
    private final ComprobanteEmailEnvioRepository emailEnvioRepository = mock(ComprobanteEmailEnvioRepository.class);
    private final RideGeneratorPort rideGeneratorPort = mock(RideGeneratorPort.class);
    private final EmailPort emailPort = mock(EmailPort.class);
    private final AuditService auditService = mock(AuditService.class);

    @Test
    void registraPendienteCuandoSmtpEstaDeshabilitado() {
        ComprobanteEmailUseCase useCase = useCase(false);
        ComprobanteEntity comprobante = comprobanteAutorizado("cliente@test.ec");
        when(emailEnvioRepository.findByComprobanteId(comprobante.getId())).thenReturn(Optional.empty());

        useCase.registrarYEnviarAutorizado(comprobante);

        ArgumentCaptor<ComprobanteEmailEnvioEntity> captor = ArgumentCaptor.forClass(ComprobanteEmailEnvioEntity.class);
        verify(emailEnvioRepository).save(captor.capture());
        verify(rideGeneratorPort, never()).generateRide(any());
        verify(emailPort, never()).sendComprobante(any(), any());
        assertEquals(EstadoEnvioCorreo.PENDIENTE, captor.getValue().getEstado());
        assertEquals("cliente@test.ec", captor.getValue().getDestinatario());
        assertEquals("SMTP deshabilitado; envio pendiente", captor.getValue().getUltimoError());
    }

    @Test
    void enviaCorreoCuandoSmtpEstaHabilitado() {
        ComprobanteEmailUseCase useCase = useCase(true);
        ComprobanteEntity comprobante = comprobanteAutorizado("cliente@test.ec");
        byte[] ride = new byte[] {1, 2, 3};
        when(emailEnvioRepository.findByComprobanteId(comprobante.getId())).thenReturn(Optional.empty());
        when(rideGeneratorPort.generateRide(comprobante)).thenReturn(ride);

        useCase.registrarYEnviarAutorizado(comprobante);

        ArgumentCaptor<ComprobanteEmailEnvioEntity> captor = ArgumentCaptor.forClass(ComprobanteEmailEnvioEntity.class);
        verify(emailPort).sendComprobante(comprobante, ride);
        verify(emailEnvioRepository).save(captor.capture());
        assertEquals(EstadoEnvioCorreo.ENVIADO, captor.getValue().getEstado());
        assertEquals(1, captor.getValue().getIntentos());
        assertNotNull(captor.getValue().getEnviadoAt());
        assertNull(captor.getValue().getUltimoError());
    }

    private ComprobanteEmailUseCase useCase(boolean smtpEnabled) {
        FactuEcProperties properties = new FactuEcProperties(
                null,
                null,
                null,
                null,
                null,
                new FactuEcProperties.Mail(smtpEnabled, "no-reply@test.ec"),
                null,
                null);
        return new ComprobanteEmailUseCase(emailEnvioRepository, rideGeneratorPort, emailPort, properties, auditService);
    }

    private ComprobanteEntity comprobanteAutorizado(String correo) {
        ClienteEntity cliente = new ClienteEntity();
        cliente.setCorreo(correo);

        ComprobanteEntity comprobante = new ComprobanteEntity();
        comprobante.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        comprobante.setCliente(cliente);
        comprobante.setNumeroCompleto("001-001-000000001");
        comprobante.setEstadoSri(EstadoSri.AUTORIZADO);
        return comprobante;
    }
}
