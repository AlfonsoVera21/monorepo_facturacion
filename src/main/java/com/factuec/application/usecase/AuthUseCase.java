package com.factuec.application.usecase;

import com.factuec.application.dto.auth.AuthResponse;
import com.factuec.application.dto.auth.LoginRequest;
import com.factuec.application.dto.auth.RefreshTokenRequest;
import com.factuec.application.dto.auth.UserMeResponse;
import com.factuec.shared.exception.ApiException;
import com.factuec.shared.security.JwtService;
import com.factuec.shared.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class AuthUseCase {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public AuthUseCase(AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return buildResponse(principal);
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        if (!jwtService.isRefreshToken(request.refreshToken())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Refresh token invalido");
        }
        String username = jwtService.parse(request.refreshToken()).getSubject();
        UserPrincipal principal = (UserPrincipal) userDetailsService.loadUserByUsername(username);
        return buildResponse(principal);
    }

    public UserMeResponse me() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UserPrincipal user)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }
        return new UserMeResponse(user.id(), user.username(), user.email(), user.fullName(), user.roles(), user.permissions());
    }

    private AuthResponse buildResponse(UserPrincipal principal) {
        return new AuthResponse(
                principal.id(),
                principal.username(),
                principal.roles(),
                jwtService.generateAccessToken(principal),
                jwtService.generateRefreshToken(principal),
                jwtService.accessTokenExpiresAt(),
                jwtService.refreshTokenExpiresAt());
    }
}
