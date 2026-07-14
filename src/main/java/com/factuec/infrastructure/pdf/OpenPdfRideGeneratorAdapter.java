package com.factuec.infrastructure.pdf;

import com.factuec.application.port.out.RideGeneratorPort;
import com.factuec.domain.enums.TarifaIva;
import com.factuec.infrastructure.persistence.entity.ComprobanteDetalleEntity;
import com.factuec.infrastructure.persistence.entity.ComprobanteEntity;
import com.factuec.infrastructure.persistence.entity.ComprobantePagoEntity;
import com.factuec.shared.exception.BusinessException;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.Barcode128;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;

@Service
public class OpenPdfRideGeneratorAdapter implements RideGeneratorPort {
    private static final Color BORDER = new Color(20, 23, 31);
    private static final Color MUTED = new Color(82, 92, 110);
    private static final Color SURFACE = new Color(247, 249, 252);
    private static final Color ACCENT = new Color(0, 35, 111);
    private static final ZoneId ECUADOR = ZoneId.of("America/Guayaquil");
    private static final DateTimeFormatter SRI_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter SRI_DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Override
    public byte[] generateRide(ComprobanteEntity comprobante) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 28, 28, 24, 24);
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();

            document.add(topSection(comprobante, writer));
            document.add(spacer(5));
            document.add(customerSection(comprobante));
            document.add(spacer(4));
            document.add(detailsTable(comprobante));
            document.add(spacer(5));
            document.add(summarySection(comprobante));
            document.add(spacer(8));
            document.add(footer());

            document.close();
            return out.toByteArray();
        } catch (Exception exception) {
            throw new BusinessException("No se pudo generar RIDE/PDF: " + exception.getMessage());
        }
    }

    private PdfPTable topSection(ComprobanteEntity comprobante, PdfWriter writer) throws Exception {
        PdfPTable top = new PdfPTable(new float[]{1.08f, 0.92f});
        top.setWidthPercentage(100);

        PdfPTable issuer = new PdfPTable(1);
        issuer.setWidthPercentage(100);
        issuer.addCell(noBorderCell(comprobante.getEmpresa().getRazonSocial(), font(18, Font.BOLD, ACCENT), Element.ALIGN_CENTER, 8));
        issuer.addCell(noBorderCell(value(comprobante.getEmpresa().getNombreComercial(), comprobante.getEmpresa().getRazonSocial()),
                font(11, Font.BOLD, BORDER), Element.ALIGN_CENTER, 4));
        issuer.addCell(noBorderCell("Representacion impresa del documento electronico", font(8, Font.NORMAL, MUTED),
                Element.ALIGN_CENTER, 12));
        issuer.addCell(labelValueCell("Direccion Matriz:", comprobante.getEmpresa().getDireccionMatriz()));
        issuer.addCell(labelValueCell("Direccion Sucursal:", comprobante.getEstablecimiento().getDireccion()));
        issuer.addCell(labelValueCell("Contribuyente Especial Nro:", value(comprobante.getEmpresa().getContribuyenteEspecial(), "NA")));
        issuer.addCell(labelValueCell("Obligado a llevar contabilidad:", comprobante.getEmpresa().isObligadoContabilidad() ? "SI" : "NO"));
        issuer.addCell(labelValueCell("Regimen:", value(comprobante.getEmpresa().getRegimen(), "GENERAL")));
        top.addCell(box(issuer, 12));

        PdfPTable auth = new PdfPTable(1);
        auth.setWidthPercentage(100);
        auth.addCell(noBorderCell("R.U.C.:    " + comprobante.getEmpresa().getRuc(), font(14, Font.BOLD, BORDER), Element.ALIGN_LEFT, 4));
        auth.addCell(noBorderCell(documentLabel(comprobante), font(19, Font.BOLD, BORDER), Element.ALIGN_LEFT, 10));
        auth.addCell(noBorderCell("No.    " + comprobante.getNumeroCompleto(), font(11, Font.BOLD, BORDER), Element.ALIGN_LEFT, 12));
        auth.addCell(noBorderCell("NUMERO DE AUTORIZACION", font(9, Font.BOLD, BORDER), Element.ALIGN_LEFT, 5));
        auth.addCell(noBorderCell(value(comprobante.getNumeroAutorizacion(), "SIN AUTORIZACION"), font(7, Font.BOLD, BORDER), Element.ALIGN_LEFT, 14));
        auth.addCell(noBorderCell("AMBIENTE:    " + comprobante.getAmbiente().name(), font(10, Font.BOLD, BORDER), Element.ALIGN_LEFT, 8));
        auth.addCell(noBorderCell("EMISION:    " + comprobante.getTipoEmision().name(), font(10, Font.BOLD, BORDER), Element.ALIGN_LEFT, 10));
        auth.addCell(noBorderCell("CLAVE DE ACCESO", font(13, Font.NORMAL, BORDER), Element.ALIGN_LEFT, 6));
        auth.addCell(barcodeCell(comprobante.getClaveAcceso(), writer));
        auth.addCell(noBorderCell(comprobante.getClaveAcceso(), font(7, Font.BOLD, BORDER), Element.ALIGN_CENTER, 0));
        top.addCell(box(auth, 12));

        return top;
    }

    private PdfPTable customerSection(ComprobanteEntity comprobante) {
        PdfPTable table = new PdfPTable(new float[]{1.25f, 3.1f, 1f, 1.35f});
        table.setWidthPercentage(100);
        table.addCell(labelCell("Razon Social / Nombres y Apellidos:"));
        table.addCell(valueCell(comprobante.getCliente().getRazonSocial()));
        table.addCell(labelCell("Identificacion:"));
        table.addCell(valueCell(comprobante.getCliente().getIdentificacion()));
        table.addCell(labelCell("Fecha Emision:"));
        table.addCell(valueCell(SRI_DATE.format(comprobante.getFechaEmision())));
        table.addCell(labelCell("Guia Remision:"));
        table.addCell(valueCell(""));
        table.addCell(labelCell("Direccion:"));
        table.addCell(spanCell(value(comprobante.getCliente().getDireccion(), "NA"), 3));
        table.addCell(labelCell("Correo:"));
        table.addCell(spanCell(value(comprobante.getCliente().getCorreo(), "NA"), 3));
        return bordered(table);
    }

    private PdfPTable detailsTable(ComprobanteEntity comprobante) {
        PdfPTable table = new PdfPTable(new float[]{1.2f, 1f, 0.8f, 3.1f, 1f, 1f, 1f, 1f});
        table.setWidthPercentage(100);
        table.setHeaderRows(1);

        addHeader(table, "Cod. Principal");
        addHeader(table, "Cod. Auxiliar");
        addHeader(table, "Cantidad");
        addHeader(table, "Descripcion");
        addHeader(table, "Precio con Desc.");
        addHeader(table, "Precio Unitario");
        addHeader(table, "Descuento");
        addHeader(table, "Precio Total");

        for (ComprobanteDetalleEntity detalle : comprobante.getDetalles()) {
            addCell(table, detalle.getCodigoPrincipal(), Element.ALIGN_CENTER);
            addCell(table, value(detalle.getCodigoAuxiliar(), ""), Element.ALIGN_CENTER);
            addCell(table, quantity(detalle.getCantidad()), Element.ALIGN_RIGHT);
            addCell(table, detalle.getDescripcion(), Element.ALIGN_LEFT);
            addCell(table, money(unitAfterDiscount(detalle)), Element.ALIGN_RIGHT);
            addCell(table, quantity(detalle.getPrecioUnitario()), Element.ALIGN_RIGHT);
            addCell(table, money(detalle.getDescuento()), Element.ALIGN_RIGHT);
            addCell(table, money(detalle.getSubtotal()), Element.ALIGN_RIGHT);
        }
        return table;
    }

    private PdfPTable summarySection(ComprobanteEntity comprobante) {
        PdfPTable wrapper = new PdfPTable(new float[]{1.45f, 0.75f});
        wrapper.setWidthPercentage(100);
        wrapper.addCell(leftSummary(comprobante));
        wrapper.addCell(totalsTable(comprobante));
        return wrapper;
    }

    private PdfPCell leftSummary(ComprobanteEntity comprobante) {
        PdfPTable stack = new PdfPTable(1);
        stack.setWidthPercentage(100);

        PdfPTable additional = new PdfPTable(new float[]{0.9f, 1.7f});
        additional.setWidthPercentage(100);
        additional.addCell(sectionTitle("Informacion Adicional", 2));
        additional.addCell(labelCell("Estado SRI:"));
        additional.addCell(valueCell(comprobante.getEstadoSri().name()));
        additional.addCell(labelCell("Autorizacion:"));
        additional.addCell(valueCell(value(comprobante.getNumeroAutorizacion(), "NA")));
        additional.addCell(labelCell("Fecha autorizacion:"));
        additional.addCell(valueCell(authorizationDate(comprobante)));
        additional.addCell(labelCell("Observacion:"));
        additional.addCell(valueCell("Consulte la validez del comprobante en los servicios del SRI."));
        stack.addCell(noBorderElementCell(bordered(additional), 0, 0, 12, 8));

        PdfPTable payments = new PdfPTable(new float[]{2.3f, 0.9f, 0.8f, 0.9f});
        payments.setWidthPercentage(100);
        payments.addCell(paymentHeader("Forma Pago"));
        payments.addCell(paymentHeader("Valor"));
        payments.addCell(paymentHeader("Plazo"));
        payments.addCell(paymentHeader("Tiempo"));
        ComprobantePagoEntity pago = firstPayment(comprobante);
        payments.addCell(paymentCell(paymentLabel(pago == null ? comprobante.getFormaPago().name() : pago.getFormaPago().name()), Element.ALIGN_CENTER));
        payments.addCell(paymentCell(money(pago == null ? comprobante.getTotal() : pago.getTotal()), Element.ALIGN_RIGHT));
        payments.addCell(paymentCell(String.valueOf(pago == null ? value(comprobante.getPlazo(), 0) : value(pago.getPlazo(), 0)), Element.ALIGN_CENTER));
        payments.addCell(paymentCell(value(pago == null ? comprobante.getTiempo() : pago.getTiempo(), "DIAS").toUpperCase(), Element.ALIGN_CENTER));
        stack.addCell(noBorderElementCell(bordered(payments), 0, 0, 0, 0));

        return noBorderElementCell(stack, 0, 0, 0, 8);
    }

    private PdfPCell totalsTable(ComprobanteEntity comprobante) {
        PdfPTable totals = new PdfPTable(new float[]{1.5f, 0.8f});
        totals.setWidthPercentage(100);
        addTotal(totals, "SUBTOTAL 15%", subtotalFor(comprobante, TarifaIva.IVA_15), false);
        addTotal(totals, "SUBTOTAL 12%", subtotalFor(comprobante, TarifaIva.IVA_12), false);
        addTotal(totals, "SUBTOTAL 0%", subtotalFor(comprobante, TarifaIva.IVA_0), false);
        addTotal(totals, "SUBTOTAL No objeto de IVA", subtotalFor(comprobante, TarifaIva.NO_OBJETO_IVA), false);
        addTotal(totals, "SUBTOTAL Exento de IVA", subtotalFor(comprobante, TarifaIva.EXENTO_IVA), false);
        addTotal(totals, "SUBTOTAL SIN IMPUESTOS", comprobante.getSubtotal0().add(comprobante.getSubtotalIva()), false);
        addTotal(totals, "TOTAL Descuento", comprobante.getDescuentoTotal(), false);
        addTotal(totals, "ICE", comprobante.getIceTotal(), false);
        addTotal(totals, "IVA 15%", ivaFor(comprobante, TarifaIva.IVA_15), false);
        addTotal(totals, "IVA 12%", ivaFor(comprobante, TarifaIva.IVA_12), false);
        addTotal(totals, "PROPINA", BigDecimal.ZERO, false);
        addTotal(totals, "VALOR TOTAL", comprobante.getTotal(), true);
        addTotal(totals, "VALOR A PAGAR", comprobante.getTotal(), true);
        return noBorderElementCell(totals, 0, 0, 0, 0);
    }

    private Paragraph footer() {
        Paragraph footer = new Paragraph("Documento generado electronicamente. Representacion impresa del comprobante autorizado por el SRI.",
                font(8, Font.NORMAL, MUTED));
        footer.setAlignment(Element.ALIGN_CENTER);
        return footer;
    }

    private PdfPCell barcodeCell(String code, PdfWriter writer) throws Exception {
        if (code == null || code.isBlank()) {
            return noBorderCell("SIN CLAVE DE ACCESO", font(8, Font.BOLD, BORDER), Element.ALIGN_CENTER, 2);
        }
        Barcode128 barcode = new Barcode128();
        barcode.setCodeType(Barcode128.CODE128);
        barcode.setCode(code);
        barcode.setBarHeight(32f);
        barcode.setX(0.58f);
        barcode.setFont(null);
        Image image = barcode.createImageWithBarcode(writer.getDirectContent(), Color.BLACK, Color.BLACK);
        image.scaleToFit(220, 36);
        PdfPCell cell = noBorderElementCell(image, 0, 0, 2, 2);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

    private PdfPTable bordered(PdfPTable content) {
        PdfPTable wrapper = new PdfPTable(1);
        wrapper.setWidthPercentage(100);
        wrapper.addCell(box(content, 4));
        return wrapper;
    }

    private PdfPCell box(PdfPTable table, float padding) {
        PdfPCell cell = new PdfPCell(table);
        cell.setBorderColor(BORDER);
        cell.setBorderWidth(1.2f);
        cell.setPadding(padding);
        return cell;
    }

    private PdfPCell labelValueCell(String label, String value) {
        PdfPTable row = new PdfPTable(new float[]{0.75f, 1.7f});
        row.setWidthPercentage(100);
        row.addCell(noBorderCell(label, font(8, Font.BOLD, BORDER), Element.ALIGN_LEFT, 2));
        row.addCell(noBorderCell(value, font(8, Font.NORMAL, BORDER), Element.ALIGN_LEFT, 2));
        return noBorderElementCell(row, 0, 0, 2, 0);
    }

    private PdfPCell labelCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font(8, Font.BOLD, BORDER)));
        cell.setBorderColor(BORDER);
        cell.setPadding(3);
        return cell;
    }

    private PdfPCell valueCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(value(text, ""), font(8, Font.NORMAL, BORDER)));
        cell.setBorderColor(BORDER);
        cell.setPadding(3);
        return cell;
    }

    private PdfPCell spanCell(String text, int colspan) {
        PdfPCell cell = valueCell(text);
        cell.setColspan(colspan);
        return cell;
    }

    private PdfPCell sectionTitle(String text, int colspan) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font(10, Font.BOLD, BORDER)));
        cell.setColspan(colspan);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(SURFACE);
        cell.setBorderColor(BORDER);
        cell.setPadding(5);
        return cell;
    }

    private void addHeader(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font(8, Font.BOLD, BORDER)));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorderColor(BORDER);
        cell.setBackgroundColor(SURFACE);
        cell.setPadding(4);
        table.addCell(cell);
    }

    private void addCell(PdfPTable table, String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(value(text, ""), font(7, Font.NORMAL, BORDER)));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorderColor(BORDER);
        cell.setPadding(4);
        table.addCell(cell);
    }

    private PdfPCell paymentHeader(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font(9, Font.NORMAL, BORDER)));
        cell.setBorderColor(BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        return cell;
    }

    private PdfPCell paymentCell(String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(value(text, ""), font(9, Font.NORMAL, BORDER)));
        cell.setBorderColor(BORDER);
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(6);
        return cell;
    }

    private void addTotal(PdfPTable table, String label, BigDecimal value, boolean emphasis) {
        Font font = emphasis ? font(8, Font.BOLD, BORDER) : font(8, Font.NORMAL, BORDER);
        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        labelCell.setBorderColor(BORDER);
        labelCell.setPadding(4);
        PdfPCell valueCell = new PdfPCell(new Phrase(money(value), font));
        valueCell.setBorderColor(BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valueCell.setPadding(4);
        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private PdfPCell noBorderCell(String text, Font font, int alignment, float paddingBottom) {
        PdfPCell cell = new PdfPCell(new Phrase(value(text, ""), font));
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setHorizontalAlignment(alignment);
        cell.setPaddingBottom(paddingBottom);
        return cell;
    }

    private PdfPCell noBorderElementCell(Element element, float top, float right, float bottom, float left) {
        PdfPCell cell = new PdfPCell();
        cell.addElement(element);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setPaddingTop(top);
        cell.setPaddingRight(right);
        cell.setPaddingBottom(bottom);
        cell.setPaddingLeft(left);
        return cell;
    }

    private Paragraph spacer(float height) {
        Paragraph paragraph = new Paragraph(" ");
        paragraph.setSpacingAfter(height);
        return paragraph;
    }

    private String documentLabel(ComprobanteEntity comprobante) {
        return comprobante.getTipoComprobante().name().replace('_', ' ');
    }

    private ComprobantePagoEntity firstPayment(ComprobanteEntity comprobante) {
        return comprobante.getPagos() == null || comprobante.getPagos().isEmpty() ? null : comprobante.getPagos().get(0);
    }

    private BigDecimal unitAfterDiscount(ComprobanteDetalleEntity detalle) {
        if (detalle.getCantidad() == null || detalle.getCantidad().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return detalle.getSubtotal().divide(detalle.getCantidad(), 4, RoundingMode.HALF_UP);
    }

    private BigDecimal subtotalFor(ComprobanteEntity comprobante, TarifaIva tarifa) {
        return comprobante.getDetalles().stream()
                .filter(detalle -> detalle.getTarifaIva() == tarifa)
                .map(ComprobanteDetalleEntity::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal ivaFor(ComprobanteEntity comprobante, TarifaIva tarifa) {
        return comprobante.getDetalles().stream()
                .filter(detalle -> detalle.getTarifaIva() == tarifa)
                .map(ComprobanteDetalleEntity::getIva)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String authorizationDate(ComprobanteEntity comprobante) {
        return comprobante.getFechaAutorizacion() == null
                ? "NA"
                : SRI_DATE_TIME.format(comprobante.getFechaAutorizacion().atZone(ECUADOR));
    }

    private String paymentLabel(String value) {
        return value(value, "NA").replace('_', ' ');
    }

    private String money(BigDecimal value) {
        return number(value, 2);
    }

    private String quantity(BigDecimal value) {
        return number(value, 4);
    }

    private String number(BigDecimal value, int scale) {
        return value == null ? "0.00" : value.setScale(scale, RoundingMode.HALF_UP).toPlainString();
    }

    private String value(Object value) {
        return value(value, "NA");
    }

    private String value(Object value, Object fallback) {
        if (value == null) {
            return String.valueOf(fallback);
        }
        String text = value.toString();
        return text.isBlank() ? String.valueOf(fallback) : text;
    }

    private Font font(float size, int style, Color color) {
        Font font = new Font(Font.HELVETICA, size, style);
        font.setColor(color);
        return font;
    }
}
