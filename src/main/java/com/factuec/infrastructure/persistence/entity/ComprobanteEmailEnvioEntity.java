package com.factuec.infrastructure.persistence.entity;

import com.factuec.domain.enums.EstadoEnvioCorreo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "comprobante_email_envios",
        indexes = {
                @Index(name = "idx_comprobante_email_envios_estado", columnList = "estado, created_at")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_comprobante_email_envio_comprobante", columnNames = "comprobante_id")
        })
public class ComprobanteEmailEnvioEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comprobante_id", nullable = false)
    private ComprobanteEntity comprobante;

    @Column(length = 160)
    private String destinatario;

    @Column(length = 250)
    private String asunto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoEnvioCorreo estado;

    @Column(nullable = false)
    private int intentos;

    @Column(name = "ultimo_error", columnDefinition = "text")
    private String ultimoError;

    @Column(name = "ultimo_intento_at")
    private Instant ultimoIntentoAt;

    @Column(name = "enviado_at")
    private Instant enviadoAt;
}
