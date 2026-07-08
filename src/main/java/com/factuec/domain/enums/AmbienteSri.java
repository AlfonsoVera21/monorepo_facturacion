package com.factuec.domain.enums;

public enum AmbienteSri {
    PRUEBAS("1"),
    PRODUCCION("2");

    private final String sriCode;

    AmbienteSri(String sriCode) {
        this.sriCode = sriCode;
    }

    public String sriCode() {
        return sriCode;
    }
}
