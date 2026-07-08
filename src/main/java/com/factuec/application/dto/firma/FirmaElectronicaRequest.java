package com.factuec.application.dto.firma;

import com.factuec.domain.enums.EstadoFirma;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public record FirmaElectronicaRequest(
        @NotNull UUID empresaId,
        @NotBlank String nombreArchivo,
        @NotBlank String rutaSegura,
        String passwordSecretRef,
        LocalDate fechaEmision,
        LocalDate fechaVencimiento,
        @NotNull EstadoFirma estado
) {
}
