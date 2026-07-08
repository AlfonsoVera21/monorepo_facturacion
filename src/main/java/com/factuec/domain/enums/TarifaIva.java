package com.factuec.domain.enums;

import java.math.BigDecimal;

public enum TarifaIva {
    IVA_0("0", BigDecimal.ZERO),
    IVA_5("5", new BigDecimal("5.00")),
    IVA_12("2", new BigDecimal("12.00")),
    IVA_15("4", new BigDecimal("15.00")),
    NO_OBJETO_IVA("6", BigDecimal.ZERO),
    EXENTO_IVA("7", BigDecimal.ZERO);

    private final String sriCode;
    private final BigDecimal percentage;

    TarifaIva(String sriCode, BigDecimal percentage) {
        this.sriCode = sriCode;
        this.percentage = percentage;
    }

    public String sriCode() {
        return sriCode;
    }

    public BigDecimal percentage() {
        return percentage;
    }

    public boolean hasIva() {
        return percentage.compareTo(BigDecimal.ZERO) > 0;
    }
}
