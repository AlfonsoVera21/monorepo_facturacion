package com.factuec.application.dto.comprobante;

import com.factuec.domain.enums.TarifaIva;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record FacturaDetalleRequest(
        UUID productoId,
        String codigoPrincipal,
        String descripcion,
        @NotNull @DecimalMin("0.0001") BigDecimal cantidad,
        @DecimalMin("0.00") BigDecimal precioUnitario,
        @DecimalMin("0.00") BigDecimal descuento,
        TarifaIva tarifaIva
) {
}
