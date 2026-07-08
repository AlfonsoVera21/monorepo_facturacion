package com.factuec.application.dto.empresa;

import com.factuec.domain.enums.TipoComprobante;
import java.util.UUID;

public record SecuencialResponse(
        UUID id,
        UUID empresaId,
        UUID establecimientoId,
        UUID puntoEmisionId,
        TipoComprobante tipoComprobante,
        long ultimoSecuencial
) {
}
