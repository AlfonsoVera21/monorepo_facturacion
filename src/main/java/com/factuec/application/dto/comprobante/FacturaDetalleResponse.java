package com.factuec.application.dto.comprobante;

import com.factuec.domain.enums.TarifaIva;
import java.math.BigDecimal;
import java.util.UUID;

public record FacturaDetalleResponse(
        UUID id,
        UUID productoId,
        String codigoPrincipal,
        String codigoAuxiliar,
        String descripcion,
        BigDecimal cantidad,
        BigDecimal precioUnitario,
        BigDecimal descuento,
        TarifaIva tarifaIva,
        BigDecimal subtotal,
        BigDecimal iva,
        BigDecimal total
) {
}
