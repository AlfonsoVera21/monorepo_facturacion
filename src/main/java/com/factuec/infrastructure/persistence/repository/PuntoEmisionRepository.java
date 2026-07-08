package com.factuec.infrastructure.persistence.repository;

import com.factuec.infrastructure.persistence.entity.PuntoEmisionEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PuntoEmisionRepository extends JpaRepository<PuntoEmisionEntity, UUID> {
    List<PuntoEmisionEntity> findByEstablecimientoId(UUID establecimientoId);

    Optional<PuntoEmisionEntity> findByIdAndEstablecimientoId(UUID id, UUID establecimientoId);
}
