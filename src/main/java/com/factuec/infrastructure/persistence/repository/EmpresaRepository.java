package com.factuec.infrastructure.persistence.repository;

import com.factuec.infrastructure.persistence.entity.EmpresaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepository extends JpaRepository<EmpresaEntity, UUID> {
    Optional<EmpresaEntity> findByRuc(String ruc);
}
