package com.factuec.infrastructure.persistence.repository;

import com.factuec.infrastructure.persistence.entity.ProductoEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<ProductoEntity, UUID> {
    List<ProductoEntity> findByEmpresaId(UUID empresaId);

    Optional<ProductoEntity> findByIdAndEmpresaId(UUID id, UUID empresaId);

    boolean existsByEmpresaIdAndCodigoPrincipal(UUID empresaId, String codigoPrincipal);
}
