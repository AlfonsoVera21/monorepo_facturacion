package com.factuec.shared.util;

import com.factuec.domain.enums.AmbienteSri;
import com.factuec.domain.enums.TipoComprobante;
import com.factuec.domain.enums.TipoEmision;
import com.factuec.domain.valueobject.ComprobanteNumber;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
public class SriAccessKeyGenerator {
    private static final DateTimeFormatter SRI_DATE = DateTimeFormatter.ofPattern("ddMMyyyy");

    public String generate(LocalDate fechaEmision,
                           TipoComprobante tipoComprobante,
                           String ruc,
                           AmbienteSri ambiente,
                           ComprobanteNumber comprobanteNumber,
                           String codigoNumerico,
                           TipoEmision tipoEmision) {
        if (fechaEmision == null || tipoComprobante == null || ruc == null || ambiente == null
                || comprobanteNumber == null || codigoNumerico == null || tipoEmision == null) {
            throw new IllegalArgumentException("Todos los campos son requeridos para generar clave de acceso");
        }
        if (!ruc.matches("\\d{13}")) {
            throw new IllegalArgumentException("El RUC debe tener 13 digitos");
        }
        if (!codigoNumerico.matches("\\d{8}")) {
            throw new IllegalArgumentException("El codigo numerico debe tener 8 digitos");
        }

        String base = SRI_DATE.format(fechaEmision)
                + tipoComprobante.sriCode()
                + ruc
                + ambiente.sriCode()
                + comprobanteNumber.serie()
                + comprobanteNumber.secuencialSri()
                + codigoNumerico
                + tipoEmision.sriCode();
        return base + modulo11(base);
    }

    public int modulo11(String value) {
        int factor = 2;
        int sum = 0;
        for (int i = value.length() - 1; i >= 0; i--) {
            sum += Character.digit(value.charAt(i), 10) * factor;
            factor = factor == 7 ? 2 : factor + 1;
        }
        int remainder = sum % 11;
        int digit = 11 - remainder;
        if (digit == 11) {
            return 0;
        }
        if (digit == 10) {
            return 1;
        }
        return digit;
    }
}
