package com.factuec.infrastructure.xml;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.factuec.domain.enums.AmbienteSri;
import com.factuec.domain.enums.EstadoComprobante;
import com.factuec.domain.enums.EstadoSri;
import com.factuec.domain.enums.FormaPago;
import com.factuec.domain.enums.TarifaIva;
import com.factuec.domain.enums.TipoComprobante;
import com.factuec.domain.enums.TipoEmision;
import com.factuec.domain.enums.TipoIdentificacion;
import com.factuec.infrastructure.persistence.entity.ClienteEntity;
import com.factuec.infrastructure.persistence.entity.ComprobanteDetalleEntity;
import com.factuec.infrastructure.persistence.entity.ComprobanteEntity;
import com.factuec.infrastructure.persistence.entity.EmpresaEntity;
import com.factuec.infrastructure.persistence.entity.EstablecimientoEntity;
import com.factuec.infrastructure.persistence.entity.PuntoEmisionEntity;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class FacturaXmlGeneratorTest {
    private final FacturaXmlGenerator generator = new FacturaXmlGenerator();

    @Test
    void generaXmlDeGuiaRemisionConEstructuraSri() {
        String xml = generator.generateGuiaRemision(guiaRemision());

        assertTrue(xml.contains("<guiaRemision id=\"comprobante\" version=\"1.1.0\">"));
        assertTrue(xml.contains("<codDoc>06</codDoc>"));
        assertTrue(xml.contains("<infoGuiaRemision>"));
        assertTrue(xml.contains("<razonSocialTransportista>Transportes Test</razonSocialTransportista>"));
        assertTrue(xml.contains("<fechaIniTransporte>14/07/2026</fechaIniTransporte>"));
        assertTrue(xml.contains("<fechaFinTransporte>15/07/2026</fechaFinTransporte>"));
        assertTrue(xml.contains("<destinatarios>"));
        assertTrue(xml.contains("<identificacionDestinatario>0999999999001</identificacionDestinatario>"));
        assertTrue(xml.contains("<codigoInterno>P001</codigoInterno>"));
        assertTrue(xml.contains("<cantidad>2.500000</cantidad>"));
    }

    private ComprobanteEntity guiaRemision() {
        EmpresaEntity empresa = new EmpresaEntity();
        empresa.setRuc("0999999999001");
        empresa.setRazonSocial("Empresa Test");
        empresa.setNombreComercial("Empresa Test");
        empresa.setDireccionMatriz("Direccion matriz");
        empresa.setObligadoContabilidad(false);
        empresa.setAmbiente(AmbienteSri.PRUEBAS);

        ClienteEntity cliente = new ClienteEntity();
        cliente.setEmpresa(empresa);
        cliente.setTipoIdentificacion(TipoIdentificacion.RUC);
        cliente.setIdentificacion("0999999999001");
        cliente.setRazonSocial("Cliente Test");
        cliente.setDireccion("Direccion destino");

        EstablecimientoEntity establecimiento = new EstablecimientoEntity();
        establecimiento.setEmpresa(empresa);
        establecimiento.setCodigo("001");
        establecimiento.setNombre("Matriz");
        establecimiento.setDireccion("Direccion establecimiento");

        PuntoEmisionEntity puntoEmision = new PuntoEmisionEntity();
        puntoEmision.setEstablecimiento(establecimiento);
        puntoEmision.setCodigo("001");
        puntoEmision.setNombre("Punto 001");

        ComprobanteEntity comprobante = new ComprobanteEntity();
        comprobante.setEmpresa(empresa);
        comprobante.setCliente(cliente);
        comprobante.setEstablecimiento(establecimiento);
        comprobante.setPuntoEmision(puntoEmision);
        comprobante.setTipoComprobante(TipoComprobante.GUIA_REMISION);
        comprobante.setSecuencial(1);
        comprobante.setNumeroCompleto("001-001-000000001");
        comprobante.setFechaEmision(LocalDate.of(2026, 7, 14));
        comprobante.setAmbiente(AmbienteSri.PRUEBAS);
        comprobante.setTipoEmision(TipoEmision.NORMAL);
        comprobante.setClaveAcceso("1407202606099999999900110010010000000011234567818");
        comprobante.setEstadoInterno(EstadoComprobante.GENERADO);
        comprobante.setEstadoSri(EstadoSri.PENDIENTE);
        comprobante.setFormaPago(FormaPago.SIN_UTILIZACION_SISTEMA_FINANCIERO);
        comprobante.setSubtotal0(BigDecimal.ZERO);
        comprobante.setSubtotalIva(BigDecimal.ZERO);
        comprobante.setDescuentoTotal(BigDecimal.ZERO);
        comprobante.setIvaTotal(BigDecimal.ZERO);
        comprobante.setIceTotal(BigDecimal.ZERO);
        comprobante.setTotal(BigDecimal.ZERO);
        comprobante.setGuiaDirPartida("Bodega matriz");
        comprobante.setGuiaRazonSocialTransportista("Transportes Test");
        comprobante.setGuiaTipoIdentificacionTransportista(TipoIdentificacion.RUC);
        comprobante.setGuiaIdentificacionTransportista("0999999999001");
        comprobante.setGuiaFechaIniTransporte(LocalDate.of(2026, 7, 14));
        comprobante.setGuiaFechaFinTransporte(LocalDate.of(2026, 7, 15));
        comprobante.setGuiaPlaca("ABC1234");
        comprobante.setGuiaDestinatarioDireccion("Direccion destino");
        comprobante.setGuiaMotivoTraslado("Venta");
        comprobante.setGuiaRuta("Quito - Guayaquil");
        comprobante.setGuiaCodDocSustento("01");
        comprobante.setGuiaNumDocSustento("001-001-000000001");
        comprobante.setGuiaNumAutDocSustento("1407202601099999999900110010010000000011234567811");
        comprobante.setGuiaFechaEmisionDocSustento(LocalDate.of(2026, 7, 14));
        comprobante.addDetalle(detalle());
        return comprobante;
    }

    private ComprobanteDetalleEntity detalle() {
        ComprobanteDetalleEntity detalle = new ComprobanteDetalleEntity();
        detalle.setCodigoPrincipal("P001");
        detalle.setDescripcion("Producto Test");
        detalle.setCantidad(new BigDecimal("2.5"));
        detalle.setPrecioUnitario(BigDecimal.ZERO);
        detalle.setDescuento(BigDecimal.ZERO);
        detalle.setTarifaIva(TarifaIva.IVA_0);
        detalle.setSubtotal(BigDecimal.ZERO);
        detalle.setIva(BigDecimal.ZERO);
        detalle.setTotal(BigDecimal.ZERO);
        return detalle;
    }
}
