package com.factuec.domain.model;

import com.factuec.domain.enums.TarifaIva;
import java.math.BigDecimal;

public record InvoiceLine(
        String codigoPrincipal,
        String descripcion,
        BigDecimal cantidad,
        BigDecimal precioUnitario,
        BigDecimal descuento,
        TarifaIva tarifaIva
) {
}
