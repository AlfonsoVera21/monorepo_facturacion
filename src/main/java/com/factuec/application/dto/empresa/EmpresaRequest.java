package com.factuec.application.dto.empresa;

import com.factuec.domain.enums.AmbienteSri;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record EmpresaRequest(
        @NotBlank @Pattern(regexp = "\\d{13}") String ruc,
        @NotBlank String razonSocial,
        String nombreComercial,
        @NotBlank String direccionMatriz,
        boolean obligadoContabilidad,
        String contribuyenteEspecial,
        String regimen,
        @NotNull AmbienteSri ambiente,
        String logoPath,
        Boolean activo
) {
}
