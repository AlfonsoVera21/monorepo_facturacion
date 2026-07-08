package com.factuec.infrastructure.persistence.entity;

import com.factuec.domain.enums.TipoIdentificacion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "clientes",
        uniqueConstraints = @UniqueConstraint(name = "uk_cliente_empresa_identificacion", columnNames = {"empresa_id", "identificacion"}))
public class ClienteEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private EmpresaEntity empresa;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_identificacion", nullable = false, length = 30)
    private TipoIdentificacion tipoIdentificacion;

    @Column(nullable = false, length = 20)
    private String identificacion;

    @Column(name = "razon_social", nullable = false, length = 300)
    private String razonSocial;

    @Column(name = "nombre_comercial", length = 300)
    private String nombreComercial;

    @Column(length = 160)
    private String correo;

    @Column(length = 40)
    private String telefono;

    @Column(length = 500)
    private String direccion;

    @Column(length = 120)
    private String ciudad;

    @Column(length = 120)
    private String provincia;

    @Column(nullable = false)
    private boolean activo = true;
}
