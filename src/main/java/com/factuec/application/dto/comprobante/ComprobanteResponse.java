package com.factuec.application.dto.comprobante;

import com.factuec.domain.enums.AmbienteSri;
import com.factuec.domain.enums.EstadoComprobante;
import com.factuec.domain.enums.EstadoSri;
import com.factuec.domain.enums.FormaPago;
import com.factuec.domain.enums.TipoComprobante;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ComprobanteResponse(
        UUID id,
        UUID empresaId,
        UUID clienteId,
        TipoComprobante tipoComprobante,
        String numeroCompleto,
        long secuencial,
        LocalDate fechaEmision,
        AmbienteSri ambiente,
        String claveAcceso,
        EstadoComprobante estadoInterno,
        EstadoSri estadoSri,
        BigDecimal subtotal0,
        BigDecimal subtotalIva,
        BigDecimal descuentoTotal,
        BigDecimal ivaTotal,
        BigDecimal iceTotal,
        BigDecimal total,
        FormaPago formaPago,
        Integer plazo,
        String tiempo,
        String numeroAutorizacion,
        Instant fechaAutorizacion,
        String mensajesSri,
        List<FacturaDetalleResponse> detalles
) {
}
