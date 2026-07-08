package com.factuec.application.dto.auth;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record AuthResponse(
        UUID userId,
        String username,
        Set<String> roles,
        String accessToken,
        String refreshToken,
        Instant accessTokenExpiresAt,
        Instant refreshTokenExpiresAt
) {
}
