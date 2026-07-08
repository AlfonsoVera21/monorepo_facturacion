package com.factuec.infrastructure.persistence.entity;

import com.factuec.domain.enums.AmbienteSri;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "empresas")
public class EmpresaEntity extends BaseEntity {
    @Column(nullable = false, unique = true, length = 13)
    private String ruc;

    @Column(name = "razon_social", nullable = false, length = 300)
    private String razonSocial;

    @Column(name = "nombre_comercial", length = 300)
    private String nombreComercial;

    @Column(name = "direccion_matriz", nullable = false, length = 500)
    private String direccionMatriz;

    @Column(name = "obligado_contabilidad", nullable = false)
    private boolean obligadoContabilidad;

    @Column(name = "contribuyente_especial", length = 20)
    private String contribuyenteEspecial;

    @Column(length = 120)
    private String regimen;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AmbienteSri ambiente;

    @Column(name = "logo_path", length = 500)
    private String logoPath;

    @Column(nullable = false)
    private boolean activo = true;
}
