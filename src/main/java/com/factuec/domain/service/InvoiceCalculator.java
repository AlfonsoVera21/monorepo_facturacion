package com.factuec.domain.service;

import com.factuec.domain.model.InvoiceLine;
import com.factuec.domain.model.InvoiceTotals;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class InvoiceCalculator {
    private static final int MONEY_SCALE = 2;
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100.00");

    public InvoiceTotals calculate(List<InvoiceLine> lines) {
        if (lines == null || lines.isEmpty()) {
            throw new IllegalArgumentException("La factura debe tener al menos un detalle");
        }

        BigDecimal subtotal0 = BigDecimal.ZERO;
        BigDecimal subtotalIva = BigDecimal.ZERO;
        BigDecimal descuentoTotal = BigDecimal.ZERO;
        BigDecimal ivaTotal = BigDecimal.ZERO;

        for (InvoiceLine line : lines) {
            validateLine(line);
            BigDecimal quantity = line.cantidad().setScale(4, RoundingMode.HALF_UP);
            BigDecimal unitPrice = line.precioUnitario().setScale(4, RoundingMode.HALF_UP);
            BigDecimal discount = money(line.descuento());
            BigDecimal taxableBase = quantity.multiply(unitPrice).subtract(discount);
            if (taxableBase.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("El descuento no puede superar el subtotal del detalle");
            }

            if (line.tarifaIva().hasIva()) {
                subtotalIva = subtotalIva.add(taxableBase);
                ivaTotal = ivaTotal.add(taxableBase.multiply(line.tarifaIva().percentage()).divide(ONE_HUNDRED, 6, RoundingMode.HALF_UP));
            } else {
                subtotal0 = subtotal0.add(taxableBase);
            }
            descuentoTotal = descuentoTotal.add(discount);
        }

        subtotal0 = money(subtotal0);
        subtotalIva = money(subtotalIva);
        descuentoTotal = money(descuentoTotal);
        ivaTotal = money(ivaTotal);
        BigDecimal iceTotal = BigDecimal.ZERO.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        BigDecimal total = money(subtotal0.add(subtotalIva).add(ivaTotal).add(iceTotal));

        return new InvoiceTotals(subtotal0, subtotalIva, descuentoTotal, ivaTotal, iceTotal, total);
    }

    private void validateLine(InvoiceLine line) {
        if (line == null) {
            throw new IllegalArgumentException("Detalle de factura invalido");
        }
        if (line.cantidad() == null || line.cantidad().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        if (line.precioUnitario() == null || line.precioUnitario().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser negativo");
        }
        if (line.tarifaIva() == null) {
            throw new IllegalArgumentException("La tarifa IVA es requerida");
        }
    }

    private BigDecimal money(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        }
        return value.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }
}
