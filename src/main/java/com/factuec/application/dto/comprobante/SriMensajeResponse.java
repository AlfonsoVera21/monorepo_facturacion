package com.factuec.application.dto.comprobante;

public record SriMensajeResponse(
        String identificador,
        String mensaje,
        String informacionAdicional,
        String tipo
) {
}
