package com.factuec.application.dto.comprobante;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record GuiaRemisionDetalleRequest(
        UUID productoId,
        String codigoInterno,
        String codigoAdicional,
        String descripcion,
        @NotNull @DecimalMin("0.000001") BigDecimal cantidad
) {
}
