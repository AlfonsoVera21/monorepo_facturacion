package com.factuec.infrastructure.persistence.entity;

import com.factuec.domain.enums.TarifaIva;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "comprobante_detalles")
public class ComprobanteDetalleEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comprobante_id", nullable = false)
    private ComprobanteEntity comprobante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private ProductoEntity producto;

    @Column(name = "codigo_principal", nullable = false, length = 80)
    private String codigoPrincipal;

    @Column(name = "codigo_auxiliar", length = 80)
    private String codigoAuxiliar;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(nullable = false, precision = 14, scale = 4)
    private BigDecimal cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 14, scale = 4)
    private BigDecimal precioUnitario;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal descuento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tarifa_iva", nullable = false, length = 30)
    private TarifaIva tarifaIva;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal iva;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal total;
}
