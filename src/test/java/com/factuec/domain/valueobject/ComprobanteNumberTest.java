package com.factuec.domain.valueobject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ComprobanteNumberTest {

    @Test
    void formatsComprobanteNumber() {
        ComprobanteNumber number = new ComprobanteNumber("001", "002", 42);

        assertThat(number.formatted()).isEqualTo("001-002-000000042");
        assertThat(number.serie()).isEqualTo("001002");
        assertThat(number.secuencialSri()).isEqualTo("000000042");
    }

    @Test
    void rejectsInvalidSerie() {
        assertThatThrownBy(() -> new ComprobanteNumber("1", "002", 1))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
