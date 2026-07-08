package com.factuec.domain.enums;

public enum FormaPago {
    SIN_UTILIZACION_SISTEMA_FINANCIERO("01"),
    COMPENSACION_DE_DEUDAS("15"),
    TARJETA_DE_DEBITO("16"),
    DINERO_ELECTRONICO("17"),
    TARJETA_PREPAGO("18"),
    TARJETA_DE_CREDITO("19"),
    OTROS_CON_SISTEMA_FINANCIERO("20"),
    ENDOSO_TITULOS("21");

    private final String sriCode;

    FormaPago(String sriCode) {
        this.sriCode = sriCode;
    }

    public String sriCode() {
        return sriCode;
    }
}
