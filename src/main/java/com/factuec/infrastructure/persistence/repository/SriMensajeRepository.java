package com.factuec.infrastructure.persistence.repository;

import com.factuec.infrastructure.persistence.entity.SriMensajeEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SriMensajeRepository extends JpaRepository<SriMensajeEntity, UUID> {
    List<SriMensajeEntity> findByComprobanteId(UUID comprobanteId);
}
