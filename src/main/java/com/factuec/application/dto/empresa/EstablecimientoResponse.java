package com.factuec.application.dto.empresa;

import java.util.UUID;

public record EstablecimientoResponse(
        UUID id,
        UUID empresaId,
        String codigo,
        String nombre,
        String direccion,
        boolean activo
) {
}
