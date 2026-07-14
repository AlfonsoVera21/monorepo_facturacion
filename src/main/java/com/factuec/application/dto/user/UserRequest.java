package com.factuec.application.dto.user;

import com.factuec.domain.enums.RoleName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record UserRequest(
        @NotBlank String username,
        @NotBlank @Email String email,
        @NotBlank String fullName,
        @Size(min = 8) String password,
        boolean active,
        @NotEmpty Set<RoleName> roles
) {
}
