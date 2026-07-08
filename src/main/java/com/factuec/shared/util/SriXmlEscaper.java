package com.factuec.shared.util;

public final class SriXmlEscaper {
    private SriXmlEscaper() {
    }

    public static String clean(String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }
}
