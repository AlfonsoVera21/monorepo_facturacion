package com.factuec.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.factuec.domain.enums.TarifaIva;
import com.factuec.domain.model.InvoiceLine;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class InvoiceCalculatorTest {
    private final InvoiceCalculator calculator = new InvoiceCalculator();

    @Test
    void calculatesTotalsWithIvaAndDiscount() {
        var totals = calculator.calculate(List.of(
                new InvoiceLine("P001", "Servicio", new BigDecimal("2"), new BigDecimal("10.00"), new BigDecimal("1.00"), TarifaIva.IVA_12),
                new InvoiceLine("P002", "Producto", new BigDecimal("1"), new BigDecimal("5.00"), BigDecimal.ZERO, TarifaIva.IVA_0)));

        assertThat(totals.subtotalIva()).isEqualByComparingTo("19.00");
        assertThat(totals.subtotal0()).isEqualByComparingTo("5.00");
        assertThat(totals.descuentoTotal()).isEqualByComparingTo("1.00");
        assertThat(totals.ivaTotal()).isEqualByComparingTo("2.28");
        assertThat(totals.total()).isEqualByComparingTo("26.28");
    }

    @Test
    void rejectsDiscountGreaterThanLineSubtotal() {
        assertThatThrownBy(() -> calculator.calculate(List.of(
                new InvoiceLine("P001", "Servicio", BigDecimal.ONE, BigDecimal.ONE, new BigDecimal("2.00"), TarifaIva.IVA_12))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("descuento");
    }
}
