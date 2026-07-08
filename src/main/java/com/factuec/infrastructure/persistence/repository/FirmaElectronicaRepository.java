package com.factuec.infrastructure.persistence.repository;

import com.factuec.domain.enums.EstadoFirma;
import com.factuec.infrastructure.persistence.entity.FirmaElectronicaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FirmaElectronicaRepository extends JpaRepository<FirmaElectronicaEntity, UUID> {
    List<FirmaElectronicaEntity> findByEmpresaId(UUID empresaId);

    Optional<FirmaElectronicaEntity> findFirstByEmpresaIdAndEstadoOrderByFechaVencimientoDesc(UUID empresaId, EstadoFirma estado);
}
