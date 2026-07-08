package com.factuec.config;

import com.factuec.domain.enums.RoleName;
import com.factuec.infrastructure.persistence.entity.UserEntity;
import com.factuec.infrastructure.persistence.repository.RoleRepository;
import com.factuec.infrastructure.persistence.repository.UserRepository;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminBootstrapRunner implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(AdminBootstrapRunner.class);

    private final FactuEcProperties properties;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminBootstrapRunner(FactuEcProperties properties,
                                UserRepository userRepository,
                                RoleRepository roleRepository,
                                PasswordEncoder passwordEncoder) {
        this.properties = properties;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        var bootstrap = properties.bootstrap();
        if (!bootstrap.adminEnabled()) {
            return;
        }
        if (userRepository.findByUsername(bootstrap.adminUsername()).isPresent()) {
            return;
        }
        if (bootstrap.adminPassword() == null || bootstrap.adminPassword().isBlank()) {
            log.warn("ADMIN_PASSWORD no configurado; no se creo usuario administrador inicial");
            return;
        }
        var adminRole = roleRepository.findByName(RoleName.ADMIN)
                .orElseThrow(() -> new IllegalStateException("Rol ADMIN no existe"));

        UserEntity user = new UserEntity();
        user.setUsername(bootstrap.adminUsername());
        user.setEmail(bootstrap.adminEmail());
        user.setFullName("Administrador FactuEc");
        user.setPasswordHash(passwordEncoder.encode(bootstrap.adminPassword()));
        user.setActive(true);
        user.setRoles(Set.of(adminRole));
        userRepository.save(user);
        log.info("Usuario administrador inicial creado: {}", bootstrap.adminUsername());
    }
}
