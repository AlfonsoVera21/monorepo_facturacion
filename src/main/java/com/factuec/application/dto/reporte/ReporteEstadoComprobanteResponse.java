package com.factuec.application.dto.reporte;

import com.factuec.domain.enums.EstadoComprobante;

public record ReporteEstadoComprobanteResponse(
        EstadoComprobante estado,
        long total
) {
}
