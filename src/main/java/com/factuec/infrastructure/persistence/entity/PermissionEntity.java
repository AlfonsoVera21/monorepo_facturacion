package com.factuec.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "permissions")
public class PermissionEntity extends BaseEntity {
    @Column(nullable = false, unique = true, length = 120)
    private String name;

    @Column(length = 250)
    private String description;
}
