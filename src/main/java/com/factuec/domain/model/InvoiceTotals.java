package com.factuec.domain.model;

import java.math.BigDecimal;

public record InvoiceTotals(
        BigDecimal subtotal0,
        BigDecimal subtotalIva,
        BigDecimal descuentoTotal,
        BigDecimal ivaTotal,
        BigDecimal iceTotal,
        BigDecimal total
) {
}
