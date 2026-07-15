package com.factuec.infrastructure.xml;

import com.factuec.application.port.out.XmlGeneratorPort;
import com.factuec.domain.enums.TarifaIva;
import com.factuec.infrastructure.persistence.entity.ComprobanteDetalleEntity;
import com.factuec.infrastructure.persistence.entity.ComprobanteEntity;
import com.factuec.shared.exception.BusinessException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Service
public class FacturaXmlGenerator implements XmlGeneratorPort {
    private static final DateTimeFormatter SRI_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public String generateFactura(ComprobanteEntity comprobante) {
        validate(comprobante);
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element factura = document.createElement("factura");
            factura.setAttribute("id", "comprobante");
            factura.setAttribute("version", "1.1.0");
            document.appendChild(factura);

            appendInfoTributaria(document, factura, comprobante);
            appendInfoFactura(document, factura, comprobante);
            appendDetalles(document, factura, comprobante);

            return render(document);
        } catch (Exception exception) {
            throw new BusinessException("No se pudo generar XML de factura: " + exception.getMessage());
        }
    }

    @Override
    public String generateGuiaRemision(ComprobanteEntity comprobante) {
        validateGuiaRemision(comprobante);
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element guia = document.createElement("guiaRemision");
            guia.setAttribute("id", "comprobante");
            guia.setAttribute("version", "1.1.0");
            document.appendChild(guia);

            appendInfoTributaria(document, guia, comprobante);
            appendInfoGuiaRemision(document, guia, comprobante);
            appendDestinatarios(document, guia, comprobante);

            return render(document);
        } catch (Exception exception) {
            throw new BusinessException("No se pudo generar XML de guia de remision: " + exception.getMessage());
        }
    }

    private void appendInfoTributaria(Document document, Element factura, ComprobanteEntity comprobante) {
        Element infoTributaria = append(document, factura, "infoTributaria", null);
        append(document, infoTributaria, "ambiente", comprobante.getAmbiente().sriCode());
        append(document, infoTributaria, "tipoEmision", comprobante.getTipoEmision().sriCode());
        append(document, infoTributaria, "razonSocial", comprobante.getEmpresa().getRazonSocial());
        append(document, infoTributaria, "nombreComercial", defaultText(comprobante.getEmpresa().getNombreComercial(), comprobante.getEmpresa().getRazonSocial()));
        append(document, infoTributaria, "ruc", comprobante.getEmpresa().getRuc());
        append(document, infoTributaria, "claveAcceso", comprobante.getClaveAcceso());
        append(document, infoTributaria, "codDoc", comprobante.getTipoComprobante().sriCode());
        append(document, infoTributaria, "estab", comprobante.getEstablecimiento().getCodigo());
        append(document, infoTributaria, "ptoEmi", comprobante.getPuntoEmision().getCodigo());
        append(document, infoTributaria, "secuencial", String.format("%09d", comprobante.getSecuencial()));
        append(document, infoTributaria, "dirMatriz", comprobante.getEmpresa().getDireccionMatriz());
    }

    private void appendInfoFactura(Document document, Element factura, ComprobanteEntity comprobante) {
        Element infoFactura = append(document, factura, "infoFactura", null);
        append(document, infoFactura, "fechaEmision", SRI_DATE.format(comprobante.getFechaEmision()));
        append(document, infoFactura, "dirEstablecimiento", comprobante.getEstablecimiento().getDireccion());
        if (comprobante.getEmpresa().getContribuyenteEspecial() != null && !comprobante.getEmpresa().getContribuyenteEspecial().isBlank()) {
            append(document, infoFactura, "contribuyenteEspecial", comprobante.getEmpresa().getContribuyenteEspecial());
        }
        append(document, infoFactura, "obligadoContabilidad", comprobante.getEmpresa().isObligadoContabilidad() ? "SI" : "NO");
        append(document, infoFactura, "tipoIdentificacionComprador", comprobante.getCliente().getTipoIdentificacion().sriCode());
        append(document, infoFactura, "razonSocialComprador", comprobante.getCliente().getRazonSocial());
        append(document, infoFactura, "identificacionComprador", comprobante.getCliente().getIdentificacion());
        append(document, infoFactura, "direccionComprador", defaultText(comprobante.getCliente().getDireccion(), "NA"));
        append(document, infoFactura, "totalSinImpuestos", money(comprobante.getSubtotal0().add(comprobante.getSubtotalIva())));
        append(document, infoFactura, "totalDescuento", money(comprobante.getDescuentoTotal()));

        Element totalConImpuestos = append(document, infoFactura, "totalConImpuestos", null);
        Map<TarifaIva, BigDecimal> bases = new LinkedHashMap<>();
        Map<TarifaIva, BigDecimal> valores = new LinkedHashMap<>();
        for (ComprobanteDetalleEntity detalle : comprobante.getDetalles()) {
            bases.merge(detalle.getTarifaIva(), detalle.getSubtotal(), BigDecimal::add);
            valores.merge(detalle.getTarifaIva(), detalle.getIva(), BigDecimal::add);
        }
        bases.forEach((tarifa, base) -> {
            Element totalImpuesto = append(document, totalConImpuestos, "totalImpuesto", null);
            append(document, totalImpuesto, "codigo", "2");
            append(document, totalImpuesto, "codigoPorcentaje", tarifa.sriCode());
            append(document, totalImpuesto, "baseImponible", money(base));
            append(document, totalImpuesto, "valor", money(valores.getOrDefault(tarifa, BigDecimal.ZERO)));
        });

        append(document, infoFactura, "propina", "0.00");
        append(document, infoFactura, "importeTotal", money(comprobante.getTotal()));
        append(document, infoFactura, "moneda", "DOLAR");

        Element pagos = append(document, infoFactura, "pagos", null);
        Element pago = append(document, pagos, "pago", null);
        append(document, pago, "formaPago", comprobante.getFormaPago().sriCode());
        append(document, pago, "total", money(comprobante.getTotal()));
        if (comprobante.getPlazo() != null) {
            append(document, pago, "plazo", comprobante.getPlazo().toString());
            append(document, pago, "unidadTiempo", defaultText(comprobante.getTiempo(), "dias"));
        }
    }

    private void appendDetalles(Document document, Element factura, ComprobanteEntity comprobante) {
        Element detalles = append(document, factura, "detalles", null);
        for (ComprobanteDetalleEntity item : comprobante.getDetalles()) {
            Element detalle = append(document, detalles, "detalle", null);
            append(document, detalle, "codigoPrincipal", item.getCodigoPrincipal());
            if (item.getCodigoAuxiliar() != null && !item.getCodigoAuxiliar().isBlank()) {
                append(document, detalle, "codigoAuxiliar", item.getCodigoAuxiliar());
            }
            append(document, detalle, "descripcion", item.getDescripcion());
            append(document, detalle, "cantidad", quantity(item.getCantidad()));
            append(document, detalle, "precioUnitario", quantity(item.getPrecioUnitario()));
            append(document, detalle, "descuento", money(item.getDescuento()));
            append(document, detalle, "precioTotalSinImpuesto", money(item.getSubtotal()));
            Element impuestos = append(document, detalle, "impuestos", null);
            Element impuesto = append(document, impuestos, "impuesto", null);
            append(document, impuesto, "codigo", "2");
            append(document, impuesto, "codigoPorcentaje", item.getTarifaIva().sriCode());
            append(document, impuesto, "tarifa", money(item.getTarifaIva().percentage()));
            append(document, impuesto, "baseImponible", money(item.getSubtotal()));
            append(document, impuesto, "valor", money(item.getIva()));
        }
    }

    private void appendInfoGuiaRemision(Document document, Element guia, ComprobanteEntity comprobante) {
        Element infoGuia = append(document, guia, "infoGuiaRemision", null);
        append(document, infoGuia, "dirEstablecimiento", comprobante.getEstablecimiento().getDireccion());
        append(document, infoGuia, "dirPartida", comprobante.getGuiaDirPartida());
        append(document, infoGuia, "razonSocialTransportista", comprobante.getGuiaRazonSocialTransportista());
        append(document, infoGuia, "tipoIdentificacionTransportista", comprobante.getGuiaTipoIdentificacionTransportista().sriCode());
        append(document, infoGuia, "rucTransportista", comprobante.getGuiaIdentificacionTransportista());
        appendIfPresent(document, infoGuia, "rise", comprobante.getGuiaRise());
        append(document, infoGuia, "obligadoContabilidad", comprobante.getEmpresa().isObligadoContabilidad() ? "SI" : "NO");
        appendIfPresent(document, infoGuia, "contribuyenteEspecial", comprobante.getEmpresa().getContribuyenteEspecial());
        append(document, infoGuia, "fechaIniTransporte", SRI_DATE.format(comprobante.getGuiaFechaIniTransporte()));
        append(document, infoGuia, "fechaFinTransporte", SRI_DATE.format(comprobante.getGuiaFechaFinTransporte()));
        append(document, infoGuia, "placa", comprobante.getGuiaPlaca());
    }

    private void appendDestinatarios(Document document, Element guia, ComprobanteEntity comprobante) {
        Element destinatarios = append(document, guia, "destinatarios", null);
        Element destinatario = append(document, destinatarios, "destinatario", null);
        append(document, destinatario, "identificacionDestinatario", comprobante.getCliente().getIdentificacion());
        append(document, destinatario, "razonSocialDestinatario", comprobante.getCliente().getRazonSocial());
        append(document, destinatario, "dirDestinatario", defaultText(comprobante.getGuiaDestinatarioDireccion(), comprobante.getCliente().getDireccion()));
        append(document, destinatario, "motivoTraslado", comprobante.getGuiaMotivoTraslado());
        appendIfPresent(document, destinatario, "docAduaneroUnico", comprobante.getGuiaDocAduaneroUnico());
        appendIfPresent(document, destinatario, "codEstabDestino", comprobante.getGuiaCodEstabDestino());
        appendIfPresent(document, destinatario, "ruta", comprobante.getGuiaRuta());
        appendIfPresent(document, destinatario, "codDocSustento", comprobante.getGuiaCodDocSustento());
        appendIfPresent(document, destinatario, "numDocSustento", comprobante.getGuiaNumDocSustento());
        appendIfPresent(document, destinatario, "numAutDocSustento", comprobante.getGuiaNumAutDocSustento());
        if (comprobante.getGuiaFechaEmisionDocSustento() != null) {
            append(document, destinatario, "fechaEmisionDocSustento", SRI_DATE.format(comprobante.getGuiaFechaEmisionDocSustento()));
        }

        Element detalles = append(document, destinatario, "detalles", null);
        for (ComprobanteDetalleEntity item : comprobante.getDetalles()) {
            Element detalle = append(document, detalles, "detalle", null);
            append(document, detalle, "codigoInterno", item.getCodigoPrincipal());
            appendIfPresent(document, detalle, "codigoAdicional", item.getCodigoAuxiliar());
            append(document, detalle, "descripcion", item.getDescripcion());
            append(document, detalle, "cantidad", quantity6(item.getCantidad()));
        }
    }

    private Element append(Document document, Element parent, String name, String value) {
        Element element = document.createElement(name);
        if (value != null) {
            element.setTextContent(value);
        }
        parent.appendChild(element);
        return element;
    }

    private void appendIfPresent(Document document, Element parent, String name, String value) {
        if (value != null && !value.isBlank()) {
            append(document, parent, name, value);
        }
    }

    private String render(Document document) throws Exception {
        StringWriter writer = new StringWriter();
        var transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        return writer.toString();
    }

    private void validate(ComprobanteEntity comprobante) {
        if (comprobante == null || comprobante.getEmpresa() == null || comprobante.getCliente() == null
                || comprobante.getDetalles() == null || comprobante.getDetalles().isEmpty()) {
            throw new BusinessException("Factura incompleta para generar XML");
        }
        if (comprobante.getClaveAcceso() == null || comprobante.getClaveAcceso().length() != 49) {
            throw new BusinessException("Clave de acceso invalida para generar XML");
        }
    }

    private void validateGuiaRemision(ComprobanteEntity comprobante) {
        if (comprobante == null || comprobante.getEmpresa() == null || comprobante.getCliente() == null
                || comprobante.getEstablecimiento() == null || comprobante.getPuntoEmision() == null
                || comprobante.getDetalles() == null || comprobante.getDetalles().isEmpty()) {
            throw new BusinessException("Guia de remision incompleta para generar XML");
        }
        if (comprobante.getClaveAcceso() == null || comprobante.getClaveAcceso().length() != 49) {
            throw new BusinessException("Clave de acceso invalida para generar XML");
        }
        if (isBlank(comprobante.getGuiaDirPartida())
                || isBlank(comprobante.getGuiaRazonSocialTransportista())
                || comprobante.getGuiaTipoIdentificacionTransportista() == null
                || isBlank(comprobante.getGuiaIdentificacionTransportista())
                || comprobante.getGuiaFechaIniTransporte() == null
                || comprobante.getGuiaFechaFinTransporte() == null
                || isBlank(comprobante.getGuiaPlaca())
                || isBlank(comprobante.getGuiaMotivoTraslado())) {
            throw new BusinessException("Guia de remision incompleta para generar XML");
        }
        if (comprobante.getFechaEmision() != null && comprobante.getGuiaFechaIniTransporte().isBefore(comprobante.getFechaEmision())) {
            throw new BusinessException("La fecha de inicio de transporte no puede ser menor a la fecha de emision");
        }
        if (comprobante.getGuiaFechaFinTransporte().isBefore(comprobante.getGuiaFechaIniTransporte())) {
            throw new BusinessException("La fecha fin de transporte no puede ser menor a la fecha de inicio");
        }
    }

    private String money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String quantity(BigDecimal value) {
        return value.setScale(4, RoundingMode.HALF_UP).toPlainString();
    }

    private String quantity6(BigDecimal value) {
        return value.setScale(6, RoundingMode.HALF_UP).toPlainString();
    }

    private String defaultText(String value, String fallback) {
        if (value != null && !value.isBlank()) {
            return value;
        }
        if (fallback != null && !fallback.isBlank()) {
            return fallback;
        }
        return "NA";
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
