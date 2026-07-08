package com.factuec.infrastructure.persistence.repository;

import com.factuec.infrastructure.persistence.entity.AuditLogEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLogEntity, UUID> {
}
