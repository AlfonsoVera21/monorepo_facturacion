package com.factuec.shared.security;

import com.factuec.config.FactuEcProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final FactuEcProperties properties;
    private final SecretKey key;

    public JwtService(FactuEcProperties properties) {
        this.properties = properties;
        this.key = Keys.hmacShaKeyFor(properties.jwt().secret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(UserPrincipal user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(properties.jwt().accessTokenTtl());
        return Jwts.builder()
                .subject(user.username())
                .claim("uid", user.id().toString())
                .claim("roles", user.roles())
                .claim("typ", "access")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(UserPrincipal user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(properties.jwt().refreshTokenTtl());
        return Jwts.builder()
                .subject(user.username())
                .claim("uid", user.id().toString())
                .claim("typ", "refresh")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(key)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isRefreshToken(String token) {
        return "refresh".equals(parse(token).get("typ", String.class));
    }

    public Instant accessTokenExpiresAt() {
        return Instant.now().plus(properties.jwt().accessTokenTtl());
    }

    public Instant refreshTokenExpiresAt() {
        return Instant.now().plus(properties.jwt().refreshTokenTtl());
    }
}
