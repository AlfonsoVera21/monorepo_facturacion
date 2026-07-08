package com.factuec.application.dto.reporte;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ReporteVentasResponse(
        UUID empresaId,
        LocalDate desde,
        LocalDate hasta,
        long comprobantes,
        BigDecimal subtotal,
        BigDecimal iva,
        BigDecimal total
) {
}
