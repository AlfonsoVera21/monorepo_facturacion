package com.factuec.application.usecase;

import com.factuec.application.dto.reporte.ReporteConteoResponse;
import com.factuec.application.dto.reporte.ReporteEstadoComprobanteResponse;
import com.factuec.application.dto.reporte.ReporteVentasResponse;
import com.factuec.domain.enums.EstadoComprobante;
import com.factuec.infrastructure.persistence.entity.ComprobanteEntity;
import com.factuec.infrastructure.persistence.repository.ClienteRepository;
import com.factuec.infrastructure.persistence.repository.ComprobanteRepository;
import com.factuec.infrastructure.persistence.repository.ProductoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportesUseCase {
    private final ComprobanteRepository comprobanteRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;

    public ReportesUseCase(ComprobanteRepository comprobanteRepository,
                           ClienteRepository clienteRepository,
                           ProductoRepository productoRepository) {
        this.comprobanteRepository = comprobanteRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public ReporteVentasResponse ventas(UUID empresaId, LocalDate desde, LocalDate hasta) {
        LocalDate start = desde == null ? LocalDate.now().withDayOfMonth(1) : desde;
        LocalDate end = hasta == null ? LocalDate.now() : hasta;
        List<ComprobanteEntity> comprobantes = comprobanteRepository.findVentas(empresaId, start, end);
        BigDecimal subtotal = comprobantes.stream()
                .map(comprobante -> comprobante.getSubtotal0().add(comprobante.getSubtotalIva()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal iva = comprobantes.stream().map(ComprobanteEntity::getIvaTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal total = comprobantes.stream().map(ComprobanteEntity::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new ReporteVentasResponse(empresaId, start, end, comprobantes.size(), subtotal, iva, total);
    }

    @Transactional(readOnly = true)
    public List<ReporteEstadoComprobanteResponse> comprobantes(UUID empresaId) {
        return Arrays.stream(EstadoComprobante.values())
                .map(estado -> new ReporteEstadoComprobanteResponse(
                        estado,
                        comprobanteRepository.countByEmpresaIdAndEstadoInterno(empresaId, estado)))
                .toList();
    }

    @Transactional(readOnly = true)
    public ReporteConteoResponse clientes(UUID empresaId) {
        long total = empresaId == null ? clienteRepository.count() : clienteRepository.findByEmpresaId(empresaId).size();
        return new ReporteConteoResponse("clientes", total);
    }

    @Transactional(readOnly = true)
    public ReporteConteoResponse productos(UUID empresaId) {
        long total = empresaId == null ? productoRepository.count() : productoRepository.findByEmpresaId(empresaId).size();
        return new ReporteConteoResponse("productos", total);
    }
}
