package com.factuec.infrastructure.persistence.repository;

import com.factuec.domain.enums.EstadoEnvioCorreo;
import com.factuec.infrastructure.persistence.entity.ComprobanteEmailEnvioEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComprobanteEmailEnvioRepository extends JpaRepository<ComprobanteEmailEnvioEntity, UUID> {
    Optional<ComprobanteEmailEnvioEntity> findByComprobanteId(UUID comprobanteId);

    List<ComprobanteEmailEnvioEntity> findByEstadoOrderByCreatedAtAsc(EstadoEnvioCorreo estado);
}
