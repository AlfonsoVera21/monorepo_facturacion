package com.factuec.infrastructure.persistence.repository;

import com.factuec.infrastructure.persistence.entity.EstablecimientoEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstablecimientoRepository extends JpaRepository<EstablecimientoEntity, UUID> {
    List<EstablecimientoEntity> findByEmpresaId(UUID empresaId);

    Optional<EstablecimientoEntity> findByIdAndEmpresaId(UUID id, UUID empresaId);
}
