package com.factuec.interfaces.rest.controller;

import com.factuec.application.dto.reporte.ReporteConteoResponse;
import com.factuec.application.dto.reporte.ReporteEstadoComprobanteResponse;
import com.factuec.application.dto.reporte.ReporteVentasResponse;
import com.factuec.application.usecase.ReportesUseCase;
import com.factuec.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reportes")
@Tag(name = "Reportes")
public class ReporteController {
    private final ReportesUseCase reportesUseCase;

    public ReporteController(ReportesUseCase reportesUseCase) {
        this.reportesUseCase = reportesUseCase;
    }

    @GetMapping("/ventas")
    @PreAuthorize("hasAnyRole('ADMIN','CONTABILIDAD','CONSULTA','SOPORTE')")
    ApiResponse<ReporteVentasResponse> ventas(@RequestParam UUID empresaId,
                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ApiResponse.ok(reportesUseCase.ventas(empresaId, desde, hasta));
    }

    @GetMapping("/comprobantes")
    @PreAuthorize("hasAnyRole('ADMIN','CONTABILIDAD','CONSULTA','SOPORTE')")
    ApiResponse<List<ReporteEstadoComprobanteResponse>> comprobantes(@RequestParam UUID empresaId) {
        return ApiResponse.ok(reportesUseCase.comprobantes(empresaId));
    }

    @GetMapping("/clientes")
    @PreAuthorize("hasAnyRole('ADMIN','CONTABILIDAD','CONSULTA','SOPORTE')")
    ApiResponse<ReporteConteoResponse> clientes(@RequestParam(required = false) UUID empresaId) {
        return ApiResponse.ok(reportesUseCase.clientes(empresaId));
    }

    @GetMapping("/productos")
    @PreAuthorize("hasAnyRole('ADMIN','CONTABILIDAD','CONSULTA','SOPORTE')")
    ApiResponse<ReporteConteoResponse> productos(@RequestParam(required = false) UUID empresaId) {
        return ApiResponse.ok(reportesUseCase.productos(empresaId));
    }
}
