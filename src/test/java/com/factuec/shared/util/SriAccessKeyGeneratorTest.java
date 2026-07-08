package com.factuec.shared.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.factuec.domain.enums.AmbienteSri;
import com.factuec.domain.enums.TipoComprobante;
import com.factuec.domain.enums.TipoEmision;
import com.factuec.domain.valueobject.ComprobanteNumber;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class SriAccessKeyGeneratorTest {
    private final SriAccessKeyGenerator generator = new SriAccessKeyGenerator();

    @Test
    void generatesFortyNineDigitAccessKeyWithModulo11() {
        String clave = generator.generate(
                LocalDate.of(2026, 7, 8),
                TipoComprobante.FACTURA,
                "1790012345001",
                AmbienteSri.PRUEBAS,
                new ComprobanteNumber("001", "001", 1),
                "12345678",
                TipoEmision.NORMAL);

        assertThat(clave).hasSize(49).containsOnlyDigits();
        String base = clave.substring(0, 48);
        assertThat(Character.digit(clave.charAt(48), 10)).isEqualTo(generator.modulo11(base));
    }
}
