package com.factuec.application.usecase;

import com.factuec.application.dto.user.RoleResponse;
import com.factuec.application.dto.user.UserRequest;
import com.factuec.application.dto.user.UserResponse;
import com.factuec.domain.enums.RoleName;
import com.factuec.infrastructure.persistence.entity.PermissionEntity;
import com.factuec.infrastructure.persistence.entity.RoleEntity;
import com.factuec.infrastructure.persistence.entity.UserEntity;
import com.factuec.infrastructure.persistence.repository.RoleRepository;
import com.factuec.infrastructure.persistence.repository.UserRepository;
import com.factuec.shared.exception.BusinessException;
import com.factuec.shared.exception.ResourceNotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserManagementUseCase {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserManagementUseCase(UserRepository userRepository,
                                 RoleRepository roleRepository,
                                 PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream()
                .sorted(Comparator.comparing(UserEntity::getUsername))
                .map(this::toUserResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> listRoles() {
        return roleRepository.findAll().stream()
                .sorted(Comparator.comparing(role -> role.getName().name()))
                .map(this::toRoleResponse)
                .toList();
    }

    @Transactional
    public UserResponse createUser(UserRequest request) {
        if (request.password() == null || request.password().isBlank()) {
            throw new BusinessException("Password requerido para crear usuario");
        }
        userRepository.findByUsername(request.username()).ifPresent(user -> {
            throw new BusinessException("Username ya registrado");
        });
        userRepository.findByEmail(request.email()).ifPresent(user -> {
            throw new BusinessException("Email ya registrado");
        });

        UserEntity entity = new UserEntity();
        entity.setUsername(request.username());
        entity.setEmail(request.email());
        entity.setFullName(request.fullName());
        entity.setPasswordHash(passwordEncoder.encode(request.password()));
        entity.setActive(request.active());
        entity.setRoles(resolveRoles(request.roles()));
        return toUserResponse(userRepository.save(entity));
    }

    @Transactional
    public UserResponse updateUser(UUID id, UserRequest request) {
        UserEntity entity = findUser(id);
        userRepository.findByUsername(request.username())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(user -> {
                    throw new BusinessException("Username ya registrado");
                });
        userRepository.findByEmail(request.email())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(user -> {
                    throw new BusinessException("Email ya registrado");
                });

        entity.setUsername(request.username());
        entity.setEmail(request.email());
        entity.setFullName(request.fullName());
        entity.setActive(request.active());
        entity.setRoles(resolveRoles(request.roles()));
        if (request.password() != null && !request.password().isBlank()) {
            entity.setPasswordHash(passwordEncoder.encode(request.password()));
        }
        return toUserResponse(userRepository.save(entity));
    }

    @Transactional
    public void deactivateUser(UUID id) {
        UserEntity entity = findUser(id);
        entity.setActive(false);
        userRepository.save(entity);
    }

    private UserEntity findUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    private Set<RoleEntity> resolveRoles(Set<RoleName> roles) {
        return roles.stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + roleName)))
                .collect(Collectors.toSet());
    }

    private UserResponse toUserResponse(UserEntity entity) {
        return new UserResponse(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getFullName(),
                entity.isActive(),
                entity.getRoles().stream()
                        .sorted(Comparator.comparing(role -> role.getName().name()))
                        .map(this::toRoleResponse)
                        .toList(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    private RoleResponse toRoleResponse(RoleEntity entity) {
        return new RoleResponse(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPermissions().stream()
                        .map(PermissionEntity::getName)
                        .sorted()
                        .toList());
    }
}
