package com.factuec.application.dto.empresa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;

public record EstablecimientoRequest(
        @NotNull UUID empresaId,
        @NotBlank @Pattern(regexp = "\\d{3}") String codigo,
        @NotBlank String nombre,
        @NotBlank String direccion,
        Boolean activo
) {
}
