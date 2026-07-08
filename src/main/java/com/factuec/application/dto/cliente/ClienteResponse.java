package com.factuec.application.dto.cliente;

import com.factuec.domain.enums.TipoIdentificacion;
import java.util.UUID;

public record ClienteResponse(
        UUID id,
        UUID empresaId,
        TipoIdentificacion tipoIdentificacion,
        String identificacion,
        String razonSocial,
        String nombreComercial,
        String correo,
        String telefono,
        String direccion,
        String ciudad,
        String provincia,
        boolean activo
) {
}
