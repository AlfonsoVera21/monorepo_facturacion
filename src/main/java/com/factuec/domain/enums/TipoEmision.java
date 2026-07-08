package com.factuec.domain.enums;

public enum TipoEmision {
    NORMAL("1");

    private final String sriCode;

    TipoEmision(String sriCode) {
        this.sriCode = sriCode;
    }

    public String sriCode() {
        return sriCode;
    }
}
