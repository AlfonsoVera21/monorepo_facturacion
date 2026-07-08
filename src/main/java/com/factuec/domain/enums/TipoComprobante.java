package com.factuec.domain.enums;

public enum TipoComprobante {
    FACTURA("01"),
    NOTA_CREDITO("04"),
    NOTA_DEBITO("05"),
    GUIA_REMISION("06"),
    RETENCION("07"),
    LIQUIDACION_COMPRA("03");

    private final String sriCode;

    TipoComprobante(String sriCode) {
        this.sriCode = sriCode;
    }

    public String sriCode() {
        return sriCode;
    }
}
