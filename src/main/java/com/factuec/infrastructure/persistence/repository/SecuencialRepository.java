package com.factuec.infrastructure.persistence.repository;

import com.factuec.domain.enums.TipoComprobante;
import com.factuec.infrastructure.persistence.entity.SecuencialEntity;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface SecuencialRepository extends JpaRepository<SecuencialEntity, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<SecuencialEntity> findByEmpresaIdAndEstablecimientoIdAndPuntoEmisionIdAndTipoComprobante(
            UUID empresaId,
            UUID establecimientoId,
            UUID puntoEmisionId,
            TipoComprobante tipoComprobante);
}
