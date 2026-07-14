package com.factuec.application.dto.user;

import com.factuec.domain.enums.RoleName;
import java.util.List;
import java.util.UUID;

public record RoleResponse(
        UUID id,
        RoleName name,
        String description,
        List<String> permissions
) {
}
