package com.factuec.infrastructure.persistence.entity;

import com.factuec.domain.enums.EstadoSri;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "sri_mensajes")
public class SriMensajeEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comprobante_id", nullable = false)
    private ComprobanteEntity comprobante;

    @Column(length = 80)
    private String identificador;

    @Column(nullable = false, length = 1000)
    private String mensaje;

    @Column(name = "informacion_adicional", length = 1500)
    private String informacionAdicional;

    @Column(length = 80)
    private String tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_sri", nullable = false, length = 30)
    private EstadoSri estadoSri;
}
