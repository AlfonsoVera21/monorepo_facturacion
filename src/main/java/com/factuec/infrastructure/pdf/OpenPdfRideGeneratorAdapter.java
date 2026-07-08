package com.factuec.infrastructure.pdf;

import com.factuec.application.port.out.RideGeneratorPort;
import com.factuec.infrastructure.persistence.entity.ComprobanteDetalleEntity;
import com.factuec.infrastructure.persistence.entity.ComprobanteEntity;
import com.factuec.shared.exception.BusinessException;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Service;

@Service
public class OpenPdfRideGeneratorAdapter implements RideGeneratorPort {

    @Override
    public byte[] generateRide(ComprobanteEntity comprobante) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, out);
            document.open();

            Font title = new Font(Font.HELVETICA, 15, Font.BOLD);
            Font bold = new Font(Font.HELVETICA, 10, Font.BOLD);
            Font normal = new Font(Font.HELVETICA, 9);

            Paragraph header = new Paragraph("RIDE - " + comprobante.getTipoComprobante().name(), title);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);
            document.add(new Paragraph(" "));

            document.add(new Paragraph(comprobante.getEmpresa().getRazonSocial(), bold));
            document.add(new Paragraph("RUC: " + comprobante.getEmpresa().getRuc(), normal));
            document.add(new Paragraph("Direccion matriz: " + comprobante.getEmpresa().getDireccionMatriz(), normal));
            document.add(new Paragraph("Comprobante: " + comprobante.getNumeroCompleto(), normal));
            document.add(new Paragraph("Clave de acceso: " + comprobante.getClaveAcceso(), normal));
            document.add(new Paragraph("Autorizacion: " + value(comprobante.getNumeroAutorizacion()), normal));
            document.add(new Paragraph("Fecha autorizacion: " + value(comprobante.getFechaAutorizacion()), normal));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Cliente: " + comprobante.getCliente().getRazonSocial(), bold));
            document.add(new Paragraph("Identificacion: " + comprobante.getCliente().getIdentificacion(), normal));
            document.add(new Paragraph("Direccion: " + value(comprobante.getCliente().getDireccion()), normal));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(new float[]{1.2f, 4f, 1.2f, 1.4f, 1.4f});
            table.setWidthPercentage(100);
            addHeader(table, "Codigo");
            addHeader(table, "Descripcion");
            addHeader(table, "Cant.");
            addHeader(table, "P. Unit.");
            addHeader(table, "Total");
            for (ComprobanteDetalleEntity detalle : comprobante.getDetalles()) {
                addCell(table, detalle.getCodigoPrincipal(), normal);
                addCell(table, detalle.getDescripcion(), normal);
                addCell(table, money(detalle.getCantidad()), normal);
                addCell(table, money(detalle.getPrecioUnitario()), normal);
                addCell(table, money(detalle.getTotal()), normal);
            }
            document.add(table);
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Subtotal 0%: " + money(comprobante.getSubtotal0()), normal));
            document.add(new Paragraph("Subtotal IVA: " + money(comprobante.getSubtotalIva()), normal));
            document.add(new Paragraph("Descuento: " + money(comprobante.getDescuentoTotal()), normal));
            document.add(new Paragraph("IVA: " + money(comprobante.getIvaTotal()), normal));
            document.add(new Paragraph("Total: " + money(comprobante.getTotal()), bold));
            document.add(new Paragraph("Forma de pago: " + comprobante.getFormaPago().name(), normal));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Documento generado electronicamente. Consulte su autorizacion en el SRI.", normal));

            document.close();
            return out.toByteArray();
        } catch (Exception exception) {
            throw new BusinessException("No se pudo generar RIDE/PDF: " + exception.getMessage());
        }
    }

    private void addHeader(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, new Font(Font.HELVETICA, 9, Font.BOLD)));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addCell(PdfPTable table, String text, Font font) {
        table.addCell(new Phrase(value(text), font));
    }

    private String money(BigDecimal value) {
        return value == null ? "0.00" : value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String value(Object value) {
        return value == null ? "NA" : value.toString();
    }
}
