package com.factuec.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.factuec.domain.enums.EstadoComprobante;
import org.junit.jupiter.api.Test;

class EstadoComprobantePolicyTest {
    private final EstadoComprobantePolicy policy = new EstadoComprobantePolicy();

    @Test
    void allowsExpectedEmissionFlowTransitions() {
        assertThat(policy.canMove(EstadoComprobante.BORRADOR, EstadoComprobante.GENERADO)).isTrue();
        assertThat(policy.canMove(EstadoComprobante.GENERADO, EstadoComprobante.FIRMADO)).isTrue();
        assertThat(policy.canMove(EstadoComprobante.FIRMADO, EstadoComprobante.ENVIADO)).isTrue();
        assertThat(policy.canMove(EstadoComprobante.ENVIADO, EstadoComprobante.AUTORIZADO)).isTrue();
    }

    @Test
    void rejectsInvalidTransition() {
        assertThatThrownBy(() -> policy.assertCanMove(EstadoComprobante.AUTORIZADO, EstadoComprobante.FIRMADO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no permitida");
    }
}
