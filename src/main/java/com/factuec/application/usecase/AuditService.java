package com.factuec.application.usecase;

import com.factuec.domain.enums.AuditAction;
import com.factuec.infrastructure.persistence.entity.AuditLogEntity;
import com.factuec.infrastructure.persistence.repository.AuditLogRepository;
import com.factuec.infrastructure.persistence.repository.UserRepository;
import com.factuec.shared.security.UserPrincipal;
import java.util.UUID;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuditService {
    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public AuditService(AuditLogRepository auditLogRepository, UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    public void log(AuditAction action, String entityType, UUID entityId, String message, String metadata) {
        AuditLogEntity log = new AuditLogEntity();
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setMessage(message);
        log.setMetadata(metadata);
        Object principal = SecurityContextHolder.getContext().getAuthentication() == null
                ? null
                : SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal user) {
            userRepository.findById(user.id()).ifPresent(log::setUser);
        }
        auditLogRepository.save(log);
    }
}
