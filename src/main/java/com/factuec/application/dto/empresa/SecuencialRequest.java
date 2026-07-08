package com.factuec.application.dto.empresa;

import com.factuec.domain.enums.TipoComprobante;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record SecuencialRequest(
        @NotNull UUID empresaId,
        @NotNull UUID establecimientoId,
        @NotNull UUID puntoEmisionId,
        @NotNull TipoComprobante tipoComprobante,
        @Min(0) long ultimoSecuencial
) {
}
