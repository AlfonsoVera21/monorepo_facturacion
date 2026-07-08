package com.factuec.shared.security;

import com.factuec.infrastructure.persistence.entity.RoleEntity;
import com.factuec.infrastructure.persistence.entity.UserEntity;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public record UserPrincipal(
        UUID id,
        String username,
        String email,
        String fullName,
        String password,
        boolean enabled,
        Set<GrantedAuthority> authorities
) implements UserDetails {

    public static UserPrincipal from(UserEntity user) {
        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .flatMap(UserPrincipal::roleAuthorities)
                .collect(Collectors.toSet());
        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getPasswordHash(),
                user.isActive(),
                authorities);
    }

    private static Stream<GrantedAuthority> roleAuthorities(RoleEntity role) {
        Stream<GrantedAuthority> roleAuthority = Stream.of(new SimpleGrantedAuthority("ROLE_" + role.getName().name()));
        Stream<GrantedAuthority> permissions = role.getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getName()));
        return Stream.concat(roleAuthority, permissions);
    }

    public Set<String> roles() {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> authority.substring("ROLE_".length()))
                .collect(Collectors.toSet());
    }

    public Set<String> permissions() {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> !authority.startsWith("ROLE_"))
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
