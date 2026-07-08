package com.factuec.application.dto.auth;

import java.util.Set;
import java.util.UUID;

public record UserMeResponse(
        UUID id,
        String username,
        String email,
        String fullName,
        Set<String> roles,
        Set<String> permissions
) {
}
