package com.factuec.infrastructure.persistence.repository;

import com.factuec.infrastructure.persistence.entity.ClienteEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<ClienteEntity, UUID> {
    List<ClienteEntity> findByEmpresaId(UUID empresaId);

    Optional<ClienteEntity> findByIdAndEmpresaId(UUID id, UUID empresaId);

    boolean existsByEmpresaIdAndIdentificacion(UUID empresaId, String identificacion);
}
