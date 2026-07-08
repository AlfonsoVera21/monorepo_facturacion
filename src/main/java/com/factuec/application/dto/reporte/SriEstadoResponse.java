package com.factuec.application.dto.reporte;

public record SriEstadoResponse(
        String ambienteDefault,
        boolean mockEnabled,
        String recepcionPruebasUrl,
        String autorizacionPruebasUrl,
        String recepcionProduccionUrl,
        String autorizacionProduccionUrl
) {
}
