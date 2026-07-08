package com.factuec.application.dto.firma;

import com.factuec.domain.enums.EstadoFirma;
import java.time.LocalDate;
import java.util.UUID;

public record FirmaElectronicaResponse(
        UUID id,
        UUID empresaId,
        String nombreArchivo,
        String rutaSegura,
        String passwordSecretRef,
        LocalDate fechaEmision,
        LocalDate fechaVencimiento,
        EstadoFirma estado
) {
}
