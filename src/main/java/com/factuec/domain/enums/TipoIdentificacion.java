package com.factuec.domain.enums;

public enum TipoIdentificacion {
    RUC("04"),
    CEDULA("05"),
    PASAPORTE("06"),
    CONSUMIDOR_FINAL("07");

    private final String sriCode;

    TipoIdentificacion(String sriCode) {
        this.sriCode = sriCode;
    }

    public String sriCode() {
        return sriCode;
    }
}
