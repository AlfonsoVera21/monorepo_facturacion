package com.factuec.infrastructure.persistence.entity;

import com.factuec.domain.enums.EstadoFirma;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "firmas_electronicas")
public class FirmaElectronicaEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private EmpresaEntity empresa;

    @Column(name = "nombre_archivo", nullable = false, length = 250)
    private String nombreArchivo;

    @Column(name = "ruta_segura", nullable = false, length = 600)
    private String rutaSegura;

    @Column(name = "password_secret_ref", length = 250)
    private String passwordSecretRef;

    @Column(name = "fecha_emision")
    private LocalDate fechaEmision;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoFirma estado;
}
