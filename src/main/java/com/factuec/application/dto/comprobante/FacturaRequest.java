package com.factuec.application.dto.comprobante;

import com.factuec.domain.enums.FormaPago;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record FacturaRequest(
        @NotNull UUID empresaId,
        @NotNull UUID clienteId,
        @NotNull UUID establecimientoId,
        @NotNull UUID puntoEmisionId,
        LocalDate fechaEmision,
        @NotNull FormaPago formaPago,
        Integer plazo,
        String tiempo,
        @Valid @NotEmpty List<FacturaDetalleRequest> detalles
) {
}
