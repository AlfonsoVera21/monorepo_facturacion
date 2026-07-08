package com.factuec.infrastructure.sri;

public record SriResponseMessage(
        String identificador,
        String mensaje,
        String informacionAdicional,
        String tipo
) {
}
