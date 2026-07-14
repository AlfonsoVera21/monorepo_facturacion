package com.factuec.application.dto.user;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String email,
        String fullName,
        boolean active,
        List<RoleResponse> roles,
        Instant createdAt,
        Instant updatedAt
) {
}
