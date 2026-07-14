package com.factuec.application.dto.firma;

import java.time.Instant;

public record CertificadoDisponibleResponse(
        String nombreArchivo,
        String rutaSegura,
        long tamanoBytes,
        Instant modificadoEn
) {
}
