package com.factuec.infrastructure.persistence.entity;

import com.factuec.domain.enums.TipoIdentificacion;
import com.factuec.domain.enums.UnidadMedidaInventario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "choferes",
        uniqueConstraints = @UniqueConstraint(name = "uk_chofer_empresa_identificacion", columnNames = {"empresa_id", "identificacion"}))
public class ChoferEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private EmpresaEntity empresa;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_identificacion", nullable = false, length = 30)
    private TipoIdentificacion tipoIdentificacion;

    @Column(nullable = false, length = 20)
    private String identificacion;

    @Column(nullable = false, length = 160)
    private String nombres;

    @Column(length = 160)
    private String apellidos;

    @Column(nullable = false, length = 60)
    private String licencia;

    @Column(length = 40)
    private String telefono;

    @Column(length = 160)
    private String correo;

    @Column(name = "placa_vehiculo", length = 20)
    private String placaVehiculo;

    @Column(name = "tipo_vehiculo", length = 120)
    private String tipoVehiculo;

    @Column(precision = 14, scale = 4)
    private BigDecimal capacidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidad_capacidad", length = 40)
    private UnidadMedidaInventario unidadCapacidad;

    @Column(name = "transporta_refrigerado", nullable = false)
    private boolean transportaRefrigerado = false;

    @Column(nullable = false)
    private boolean activo = true;
}
