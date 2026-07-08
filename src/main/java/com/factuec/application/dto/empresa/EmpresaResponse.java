package com.factuec.application.dto.empresa;

import com.factuec.domain.enums.AmbienteSri;
import java.util.UUID;

public record EmpresaResponse(
        UUID id,
        String ruc,
        String razonSocial,
        String nombreComercial,
        String direccionMatriz,
        boolean obligadoContabilidad,
        String contribuyenteEspecial,
        String regimen,
        AmbienteSri ambiente,
        String logoPath,
        boolean activo
) {
}
