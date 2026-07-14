package com.factuec.application.dto.configuracion;

public record ConfiguracionGeneralResponse(
        String serieDefault,
        String ambiente,
        String correoNotificaciones,
        boolean enviarRideAutomatico,
        String reintentosSri,
        boolean mfaObligatorio,
        boolean sriMockEnabled,
        boolean firmaMockEnabled
) {
}
