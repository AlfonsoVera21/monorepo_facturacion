package com.factuec.infrastructure.persistence.entity;

import com.factuec.domain.enums.TipoComprobante;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "secuenciales",
        uniqueConstraints = @UniqueConstraint(name = "uk_secuencial_punto_tipo",
                columnNames = {"empresa_id", "establecimiento_id", "punto_emision_id", "tipo_comprobante"}))
public class SecuencialEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private EmpresaEntity empresa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "establecimiento_id", nullable = false)
    private EstablecimientoEntity establecimiento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "punto_emision_id", nullable = false)
    private PuntoEmisionEntity puntoEmision;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comprobante", nullable = false, length = 40)
    private TipoComprobante tipoComprobante;

    @Column(name = "ultimo_secuencial", nullable = false)
    private long ultimoSecuencial = 0L;

    @Version
    @Column(nullable = false)
    private long version;

    public long nextValue() {
        ultimoSecuencial += 1;
        return ultimoSecuencial;
    }
}
