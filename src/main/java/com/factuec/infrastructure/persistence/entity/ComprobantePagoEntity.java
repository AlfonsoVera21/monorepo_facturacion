package com.factuec.infrastructure.persistence.entity;

import com.factuec.domain.enums.FormaPago;
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
@Table(name = "comprobante_pagos")
public class ComprobantePagoEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comprobante_id", nullable = false)
    private ComprobanteEntity comprobante;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pago", nullable = false, length = 60)
    private FormaPago formaPago;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal total;

    @Column
    private Integer plazo;

    @Column(length = 30)
    private String tiempo;
}
