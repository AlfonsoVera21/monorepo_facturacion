package com.factuec.infrastructure.persistence.repository;

import com.factuec.infrastructure.persistence.entity.ChoferEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChoferRepository extends JpaRepository<ChoferEntity, UUID> {
    List<ChoferEntity> findByEmpresaId(UUID empresaId);

    boolean existsByEmpresaIdAndIdentificacion(UUID empresaId, String identificacion);
}
