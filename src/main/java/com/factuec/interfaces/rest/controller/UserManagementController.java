package com.factuec.interfaces.rest.controller;

import com.factuec.application.dto.user.RoleResponse;
import com.factuec.application.dto.user.UserRequest;
import com.factuec.application.dto.user.UserResponse;
import com.factuec.application.usecase.UserManagementUseCase;
import com.factuec.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Usuarios y roles")
public class UserManagementController {
    private final UserManagementUseCase userManagementUseCase;

    public UserManagementController(UserManagementUseCase userManagementUseCase) {
        this.userManagementUseCase = userManagementUseCase;
    }

    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('ADMIN','SOPORTE')")
    ApiResponse<List<UserResponse>> listUsers() {
        return ApiResponse.ok(userManagementUseCase.listUsers());
    }

    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        return ApiResponse.ok("Usuario creado", userManagementUseCase.createUser(request));
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<UserResponse> updateUser(@PathVariable UUID id, @Valid @RequestBody UserRequest request) {
        return ApiResponse.ok("Usuario actualizado", userManagementUseCase.updateUser(id, request));
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<Void> deactivateUser(@PathVariable UUID id) {
        userManagementUseCase.deactivateUser(id);
        return ApiResponse.ok("Usuario inactivado", null);
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAnyRole('ADMIN','SOPORTE')")
    ApiResponse<List<RoleResponse>> listRoles() {
        return ApiResponse.ok(userManagementUseCase.listRoles());
    }
}
