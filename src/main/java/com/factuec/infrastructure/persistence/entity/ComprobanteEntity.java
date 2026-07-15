package com.factuec.infrastructure.persistence.entity;

import com.factuec.domain.enums.AmbienteSri;
import com.factuec.domain.enums.EstadoComprobante;
import com.factuec.domain.enums.EstadoSri;
import com.factuec.domain.enums.FormaPago;
import com.factuec.domain.enums.TipoComprobante;
import com.factuec.domain.enums.TipoEmision;
import com.factuec.domain.enums.TipoIdentificacion;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "comprobantes",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_comprobante_numero_empresa", columnNames = {"empresa_id", "tipo_comprobante", "numero_completo"}),
                @UniqueConstraint(name = "uk_comprobante_clave_acceso", columnNames = {"clave_acceso"})
        })
public class ComprobanteEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private EmpresaEntity empresa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteEntity cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "establecimiento_id", nullable = false)
    private EstablecimientoEntity establecimiento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "punto_emision_id", nullable = false)
    private PuntoEmisionEntity puntoEmision;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_creador_id")
    private UserEntity usuarioCreador;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comprobante", nullable = false, length = 40)
    private TipoComprobante tipoComprobante;

    @Column(nullable = false)
    private long secuencial;

    @Column(name = "numero_completo", nullable = false, length = 17)
    private String numeroCompleto;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AmbienteSri ambiente;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_emision", nullable = false, length = 20)
    private TipoEmision tipoEmision;

    @Column(name = "clave_acceso", nullable = false, length = 49)
    private String claveAcceso;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_interno", nullable = false, length = 30)
    private EstadoComprobante estadoInterno;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_sri", nullable = false, length = 30)
    private EstadoSri estadoSri;

    @Column(name = "subtotal_0", nullable = false, precision = 14, scale = 2)
    private BigDecimal subtotal0;

    @Column(name = "subtotal_iva", nullable = false, precision = 14, scale = 2)
    private BigDecimal subtotalIva;

    @Column(name = "descuento_total", nullable = false, precision = 14, scale = 2)
    private BigDecimal descuentoTotal;

    @Column(name = "iva_total", nullable = false, precision = 14, scale = 2)
    private BigDecimal ivaTotal;

    @Column(name = "ice_total", nullable = false, precision = 14, scale = 2)
    private BigDecimal iceTotal;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pago", nullable = false, length = 60)
    private FormaPago formaPago;

    @Column
    private Integer plazo;

    @Column(length = 30)
    private String tiempo;

    @Column(name = "xml_generado", columnDefinition = "text")
    private String xmlGenerado;

    @Column(name = "xml_firmado", columnDefinition = "text")
    private String xmlFirmado;

    @Column(name = "numero_autorizacion", length = 80)
    private String numeroAutorizacion;

    @Column(name = "fecha_autorizacion")
    private Instant fechaAutorizacion;

    @Column(name = "mensajes_sri", columnDefinition = "text")
    private String mensajesSri;

    @Column(name = "guia_dir_partida", length = 300)
    private String guiaDirPartida;

    @Column(name = "guia_razon_social_transportista", length = 300)
    private String guiaRazonSocialTransportista;

    @Enumerated(EnumType.STRING)
    @Column(name = "guia_tipo_identificacion_transportista", length = 30)
    private TipoIdentificacion guiaTipoIdentificacionTransportista;

    @Column(name = "guia_identificacion_transportista", length = 20)
    private String guiaIdentificacionTransportista;

    @Column(name = "guia_rise", length = 40)
    private String guiaRise;

    @Column(name = "guia_fecha_ini_transporte")
    private LocalDate guiaFechaIniTransporte;

    @Column(name = "guia_fecha_fin_transporte")
    private LocalDate guiaFechaFinTransporte;

    @Column(name = "guia_placa", length = 20)
    private String guiaPlaca;

    @Column(name = "guia_destinatario_direccion", length = 300)
    private String guiaDestinatarioDireccion;

    @Column(name = "guia_motivo_traslado", length = 300)
    private String guiaMotivoTraslado;

    @Column(name = "guia_doc_aduanero_unico", length = 20)
    private String guiaDocAduaneroUnico;

    @Column(name = "guia_cod_estab_destino", length = 3)
    private String guiaCodEstabDestino;

    @Column(name = "guia_ruta", length = 300)
    private String guiaRuta;

    @Column(name = "guia_cod_doc_sustento", length = 2)
    private String guiaCodDocSustento;

    @Column(name = "guia_num_doc_sustento", length = 17)
    private String guiaNumDocSustento;

    @Column(name = "guia_num_aut_doc_sustento", length = 49)
    private String guiaNumAutDocSustento;

    @Column(name = "guia_fecha_emision_doc_sustento")
    private LocalDate guiaFechaEmisionDocSustento;

    @OneToMany(mappedBy = "comprobante", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComprobanteDetalleEntity> detalles = new ArrayList<>();

    @OneToMany(mappedBy = "comprobante", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComprobantePagoEntity> pagos = new ArrayList<>();

    public void addDetalle(ComprobanteDetalleEntity detalle) {
        detalles.add(detalle);
        detalle.setComprobante(this);
    }

    public void addPago(ComprobantePagoEntity pago) {
        pagos.add(pago);
        pago.setComprobante(this);
    }
}
