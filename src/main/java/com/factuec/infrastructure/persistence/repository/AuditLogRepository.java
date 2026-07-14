package com.factuec.infrastructure.persistence.repository;

import com.factuec.infrastructure.persistence.entity.AuditLogEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLogEntity, UUID> {
    @EntityGraph(attributePaths = "user")
    List<AuditLogEntity> findTop50ByEntityTypeOrderByCreatedAtDesc(String entityType);
}
