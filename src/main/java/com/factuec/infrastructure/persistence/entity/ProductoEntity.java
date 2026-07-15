package com.factuec.infrastructure.persistence.entity;

import com.factuec.domain.enums.TarifaIva;
import com.factuec.domain.enums.TipoProducto;
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
@Table(name = "productos",
        uniqueConstraints = @UniqueConstraint(name = "uk_producto_empresa_codigo", columnNames = {"empresa_id", "codigo_principal"}))
public class ProductoEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private EmpresaEntity empresa;

    @Column(name = "codigo_principal", nullable = false, length = 80)
    private String codigoPrincipal;

    @Column(name = "codigo_auxiliar", length = 80)
    private String codigoAuxiliar;

    @Column(nullable = false, length = 250)
    private String nombre;

    @Column(length = 1000)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoProducto tipo;

    @Column(name = "precio_unitario", nullable = false, precision = 14, scale = 4)
    private BigDecimal precioUnitario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tarifa_iva", nullable = false, length = 30)
    private TarifaIva tarifaIva;

    @Column(name = "ice_porcentaje", precision = 8, scale = 2)
    private BigDecimal icePorcentaje;

    @Column(precision = 14, scale = 4)
    private BigDecimal stock;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidad_medida", nullable = false, length = 40)
    private UnidadMedidaInventario unidadMedida = UnidadMedidaInventario.UNIDAD;

    @Column(name = "stock_minimo", nullable = false, precision = 14, scale = 4)
    private BigDecimal stockMinimo = BigDecimal.ZERO;

    @Column(name = "peso_promedio_kg", precision = 14, scale = 4)
    private BigDecimal pesoPromedioKg;

    @Column(nullable = false)
    private boolean palletizable = false;

    @Column(name = "unidades_por_pallet", precision = 14, scale = 4)
    private BigDecimal unidadesPorPallet;

    @Column(name = "requiere_refrigeracion", nullable = false)
    private boolean requiereRefrigeracion = false;

    @Column(length = 120)
    private String categoria;

    @Column(nullable = false)
    private boolean activo = true;
}
