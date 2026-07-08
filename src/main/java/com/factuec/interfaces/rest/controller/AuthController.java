package com.factuec.interfaces.rest.controller;

import com.factuec.application.dto.auth.AuthResponse;
import com.factuec.application.dto.auth.LoginRequest;
import com.factuec.application.dto.auth.RefreshTokenRequest;
import com.factuec.application.dto.auth.UserMeResponse;
import com.factuec.application.usecase.AuthUseCase;
import com.factuec.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth")
public class AuthController {
    private final AuthUseCase authUseCase;

    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    @PostMapping("/login")
    ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authUseCase.login(request));
    }

    @PostMapping("/refresh")
    ApiResponse<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.ok(authUseCase.refresh(request));
    }

    @GetMapping("/me")
    ApiResponse<UserMeResponse> me() {
        return ApiResponse.ok(authUseCase.me());
    }
}
