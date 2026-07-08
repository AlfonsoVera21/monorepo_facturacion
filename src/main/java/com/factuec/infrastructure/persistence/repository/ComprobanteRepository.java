package com.factuec.infrastructure.persistence.repository;

import com.factuec.domain.enums.EstadoComprobante;
import com.factuec.infrastructure.persistence.entity.ComprobanteEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ComprobanteRepository extends JpaRepository<ComprobanteEntity, UUID> {
    List<ComprobanteEntity> findByEmpresaIdOrderByFechaEmisionDescCreatedAtDesc(UUID empresaId);

    Optional<ComprobanteEntity> findByClaveAcceso(String claveAcceso);

    List<ComprobanteEntity> findByEstadoInterno(EstadoComprobante estadoInterno);

    @Query("""
            select c from ComprobanteEntity c
            where c.empresa.id = :empresaId
              and c.fechaEmision between :desde and :hasta
            order by c.fechaEmision desc, c.createdAt desc
            """)
    List<ComprobanteEntity> findVentas(@Param("empresaId") UUID empresaId,
                                       @Param("desde") LocalDate desde,
                                       @Param("hasta") LocalDate hasta);

    long countByEmpresaIdAndEstadoInterno(UUID empresaId, EstadoComprobante estadoInterno);
}
