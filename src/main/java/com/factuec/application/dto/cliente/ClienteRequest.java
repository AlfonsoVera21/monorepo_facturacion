package com.factuec.application.dto.cliente;

import com.factuec.domain.enums.TipoIdentificacion;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ClienteRequest(
        @NotNull UUID empresaId,
        @NotNull TipoIdentificacion tipoIdentificacion,
        @NotBlank String identificacion,
        @NotBlank String razonSocial,
        String nombreComercial,
        @Email String correo,
        String telefono,
        String direccion,
        String ciudad,
        String provincia,
        Boolean activo
) {
}
