package com.factuec.application.dto.empresa;

import java.util.UUID;

public record PuntoEmisionResponse(
        UUID id,
        UUID establecimientoId,
        String codigo,
        String nombre,
        boolean activo
) {
}
