package com.factuec.application.usecase;

import com.factuec.application.dto.firma.CertificadoDisponibleResponse;
import com.factuec.application.dto.firma.FirmaElectronicaRequest;
import com.factuec.application.dto.firma.FirmaElectronicaResponse;
import com.factuec.application.mapper.FirmaElectronicaMapper;
import com.factuec.config.FactuEcProperties;
import com.factuec.domain.enums.AuditAction;
import com.factuec.domain.enums.EstadoFirma;
import com.factuec.infrastructure.persistence.entity.EmpresaEntity;
import com.factuec.infrastructure.persistence.entity.FirmaElectronicaEntity;
import com.factuec.infrastructure.persistence.repository.FirmaElectronicaRepository;
import com.factuec.shared.exception.BusinessException;
import com.factuec.shared.exception.ResourceNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.cert.CertificateExpiredException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FirmaElectronicaUseCase {
    private final FirmaElectronicaRepository firmaRepository;
    private final EmpresaUseCase empresaUseCase;
    private final FirmaElectronicaMapper firmaMapper;
    private final FactuEcProperties properties;
    private final AuditService auditService;

    public FirmaElectronicaUseCase(FirmaElectronicaRepository firmaRepository,
                                   EmpresaUseCase empresaUseCase,
                                   FirmaElectronicaMapper firmaMapper,
                                   FactuEcProperties properties,
                                   AuditService auditService) {
        this.firmaRepository = firmaRepository;
        this.empresaUseCase = empresaUseCase;
        this.firmaMapper = firmaMapper;
        this.properties = properties;
        this.auditService = auditService;
    }

    @Transactional
    public FirmaElectronicaResponse create(FirmaElectronicaRequest request) {
        EmpresaEntity empresa = empresaUseCase.findEntity(request.empresaId());
        FirmaElectronicaEntity entity = firmaMapper.toEntity(request);
        entity.setEmpresa(empresa);
        validateActiveSignature(entity);
        FirmaElectronicaEntity saved = firmaRepository.save(entity);
        auditService.log(AuditAction.FIRMA, "FirmaElectronica", saved.getId(), "Firma electronica registrada", null);
        return firmaMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<FirmaElectronicaResponse> findByEmpresa(UUID empresaId) {
        return firmaRepository.findByEmpresaId(empresaId).stream().map(firmaMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<CertificadoDisponibleResponse> listCertificadosDisponibles() {
        Path certificatesPath = Path.of(properties.storage().certificatesPath());
        if (!Files.isDirectory(certificatesPath)) {
            return List.of();
        }
        try (var files = Files.list(certificatesPath)) {
            return files
                    .filter(Files::isRegularFile)
                    .filter(this::isPkcs12File)
                    .map(this::toCertificadoDisponible)
                    .sorted(Comparator.comparing(CertificadoDisponibleResponse::nombreArchivo))
                    .toList();
        } catch (Exception exception) {
            throw new BusinessException("No se pudo leer la carpeta de certificados: " + exception.getMessage());
        }
    }

    @Transactional
    public FirmaElectronicaResponse update(UUID id, FirmaElectronicaRequest request) {
        FirmaElectronicaEntity entity = firmaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Firma electronica no encontrada"));
        firmaMapper.update(request, entity);
        if (!entity.getEmpresa().getId().equals(request.empresaId())) {
            entity.setEmpresa(empresaUseCase.findEntity(request.empresaId()));
        }
        validateActiveSignature(entity);
        FirmaElectronicaEntity saved = firmaRepository.save(entity);
        auditService.log(AuditAction.FIRMA, "FirmaElectronica", saved.getId(), "Firma electronica actualizada", null);
        return firmaMapper.toResponse(saved);
    }

    private void validateActiveSignature(FirmaElectronicaEntity firma) {
        if (firma.getEstado() != EstadoFirma.ACTIVA || properties.signature().mockEnabled()) {
            return;
        }
        String rutaSegura = firma.getRutaSegura();
        if (rutaSegura == null || rutaSegura.isBlank()) {
            throw new BusinessException("Ruta segura de firma requerida");
        }
        String lowerName = rutaSegura.toLowerCase();
        if (!lowerName.endsWith(".p12") && !lowerName.endsWith(".pfx")) {
            throw new BusinessException("La firma electronica debe ser un archivo .p12 o .pfx");
        }

        Path certificatePath = Path.of(rutaSegura);
        if (!Files.isRegularFile(certificatePath) || !Files.isReadable(certificatePath)) {
            throw new BusinessException("No se puede leer la firma electronica en: " + rutaSegura);
        }

        String passwordSecretRef = firma.getPasswordSecretRef();
        if (passwordSecretRef == null || passwordSecretRef.isBlank()) {
            throw new BusinessException("passwordSecretRef es requerido para firma real");
        }
        String password = System.getenv(passwordSecretRef);
        if (password == null || password.isBlank()) {
            throw new BusinessException("La variable de entorno " + passwordSecretRef + " no contiene la clave de la firma");
        }

        validatePkcs12(certificatePath, password.toCharArray());
    }

    private boolean isPkcs12File(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();
        return fileName.endsWith(".p12") || fileName.endsWith(".pfx");
    }

    private CertificadoDisponibleResponse toCertificadoDisponible(Path path) {
        try {
            return new CertificadoDisponibleResponse(
                    path.getFileName().toString(),
                    path.toAbsolutePath().normalize().toString(),
                    Files.size(path),
                    Files.getLastModifiedTime(path).toInstant());
        } catch (Exception exception) {
            return new CertificadoDisponibleResponse(
                    path.getFileName().toString(),
                    path.toAbsolutePath().normalize().toString(),
                    0,
                    Instant.EPOCH);
        }
    }

    private void validatePkcs12(Path certificatePath, char[] password) {
        try (var inputStream = Files.newInputStream(certificatePath)) {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(inputStream, password);
            String alias = Collections.list(keyStore.aliases()).stream()
                    .filter(candidate -> {
                        try {
                            return keyStore.isKeyEntry(candidate);
                        } catch (Exception exception) {
                            return false;
                        }
                    })
                    .findFirst()
                    .orElse(null);
            if (alias == null) {
                throw new BusinessException("Certificado invalido: no contiene llave privada");
            }
            X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);
            certificate.checkValidity();
        } catch (CertificateExpiredException exception) {
            throw new BusinessException("Firma vencida");
        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BusinessException("No se pudo validar la firma electronica: " + exception.getMessage());
        }
    }
}
