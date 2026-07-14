package com.factuec.application.dto.audit;

import java.time.Instant;
import java.util.UUID;

public record AuditLogResponse(
        UUID id,
        Instant fecha,
        String usuario,
        String accion,
        String entidad,
        String descripcion,
        String ip
) {
}
