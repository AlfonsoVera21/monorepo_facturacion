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

            StringWriter writer = new StringWriter();
            var transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(document), new StreamResult(writer));
            return writer.toString();
        } catch (Exception exception) {
            throw new BusinessException("No se pudo generar XML de factura: " + exception.getMessage());
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

    private Element append(Document document, Element parent, String name, String value) {
        Element element = document.createElement(name);
        if (value != null) {
            element.setTextContent(value);
        }
        parent.appendChild(element);
        return element;
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

    private String money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String quantity(BigDecimal value) {
        return value.setScale(4, RoundingMode.HALF_UP).toPlainString();
    }

    private String defaultText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
