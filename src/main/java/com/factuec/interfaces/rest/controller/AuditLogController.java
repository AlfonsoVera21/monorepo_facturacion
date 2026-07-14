package com.factuec.interfaces.rest.controller;

import com.factuec.application.dto.audit.AuditLogResponse;
import com.factuec.infrastructure.persistence.entity.AuditLogEntity;
import com.factuec.infrastructure.persistence.repository.AuditLogRepository;
import com.factuec.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit-logs")
@Tag(name = "Auditoria")
public class AuditLogController {
    private final AuditLogRepository auditLogRepository;

    public AuditLogController(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SOPORTE')")
    ApiResponse<List<AuditLogResponse>> findByEntity(@RequestParam String entityType) {
        return ApiResponse.ok(auditLogRepository.findTop50ByEntityTypeOrderByCreatedAtDesc(entityType)
                .stream()
                .map(this::toResponse)
                .toList());
    }

    private AuditLogResponse toResponse(AuditLogEntity entity) {
        String usuario = entity.getUser() == null ? "Sistema" : entity.getUser().getUsername();
        return new AuditLogResponse(
                entity.getId(),
                entity.getCreatedAt(),
                usuario,
                entity.getAction().name(),
                entity.getEntityType(),
                entity.getMessage(),
                "N/D");
    }
}
