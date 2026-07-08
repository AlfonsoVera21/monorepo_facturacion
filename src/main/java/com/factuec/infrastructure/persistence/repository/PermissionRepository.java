package com.factuec.infrastructure.persistence.repository;

import com.factuec.infrastructure.persistence.entity.PermissionEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<PermissionEntity, UUID> {
}
