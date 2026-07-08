package com.factuec.domain.valueobject;

public record ComprobanteNumber(String establecimiento, String puntoEmision, long secuencial) {

    public ComprobanteNumber {
        if (establecimiento == null || !establecimiento.matches("\\d{3}")) {
            throw new IllegalArgumentException("El codigo de establecimiento debe tener 3 digitos");
        }
        if (puntoEmision == null || !puntoEmision.matches("\\d{3}")) {
            throw new IllegalArgumentException("El codigo de punto de emision debe tener 3 digitos");
        }
        if (secuencial < 1 || secuencial > 999_999_999L) {
            throw new IllegalArgumentException("El secuencial debe estar entre 1 y 999999999");
        }
    }

    public String formatted() {
        return establecimiento + "-" + puntoEmision + "-" + String.format("%09d", secuencial);
    }

    public String serie() {
        return establecimiento + puntoEmision;
    }

    public String secuencialSri() {
        return String.format("%09d", secuencial);
    }
}
