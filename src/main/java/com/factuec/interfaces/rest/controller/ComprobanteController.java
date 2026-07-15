package com.factuec.interfaces.rest.controller;

import com.factuec.application.dto.comprobante.ComprobanteResponse;
import com.factuec.application.dto.comprobante.FacturaRequest;
import com.factuec.application.dto.comprobante.GuiaRemisionRequest;
import com.factuec.application.usecase.ComprobanteUseCase;
import com.factuec.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comprobantes")
@Tag(name = "Comprobantes")
public class ComprobanteController {
    private final ComprobanteUseCase comprobanteUseCase;

    public ComprobanteController(ComprobanteUseCase comprobanteUseCase) {
        this.comprobanteUseCase = comprobanteUseCase;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMISOR','CONTABILIDAD','CONSULTA','SOPORTE')")
    ApiResponse<List<ComprobanteResponse>> list(@RequestParam(required = false) UUID empresaId) {
        return ApiResponse.ok(comprobanteUseCase.list(empresaId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMISOR','CONTABILIDAD','CONSULTA','SOPORTE')")
    ApiResponse<ComprobanteResponse> get(@PathVariable UUID id) {
        return ApiResponse.ok(comprobanteUseCase.get(id));
    }

    @PostMapping("/facturas/borrador")
    @PreAuthorize("hasAnyRole('ADMIN','EMISOR','CONTABILIDAD')")
    ApiResponse<ComprobanteResponse> guardarBorrador(@Valid @RequestBody FacturaRequest request) {
        return ApiResponse.ok("Borrador guardado", comprobanteUseCase.guardarBorrador(request));
    }

    @PostMapping("/facturas/emitir")
    @PreAuthorize("hasAnyRole('ADMIN','EMISOR','CONTABILIDAD')")
    ApiResponse<ComprobanteResponse> emitir(@Valid @RequestBody FacturaRequest request) {
        return ApiResponse.ok("Factura emitida", comprobanteUseCase.emitirFactura(request));
    }

    @PostMapping("/guias-remision/emitir")
    @PreAuthorize("hasAnyRole('ADMIN','EMISOR','CONTABILIDAD')")
    ApiResponse<ComprobanteResponse> emitirGuiaRemision(@Valid @RequestBody GuiaRemisionRequest request) {
        return ApiResponse.ok("Guia de remision emitida", comprobanteUseCase.emitirGuiaRemision(request));
    }

    @PostMapping("/{id}/reenviar-sri")
    @PreAuthorize("hasAnyRole('ADMIN','EMISOR','CONTABILIDAD','SOPORTE')")
    ApiResponse<ComprobanteResponse> reenviarSri(@PathVariable UUID id) {
        return ApiResponse.ok("Comprobante reenviado", comprobanteUseCase.reenviarSri(id));
    }

    @PostMapping("/{id}/consultar-autorizacion")
    @PreAuthorize("hasAnyRole('ADMIN','EMISOR','CONTABILIDAD','SOPORTE')")
    ApiResponse<ComprobanteResponse> consultarAutorizacion(@PathVariable UUID id) {
        return ApiResponse.ok("Autorizacion consultada", comprobanteUseCase.consultarAutorizacion(id));
    }

    @GetMapping("/{id}/xml")
    @PreAuthorize("hasAnyRole('ADMIN','EMISOR','CONTABILIDAD','CONSULTA','SOPORTE')")
    ResponseEntity<byte[]> xml(@PathVariable UUID id) {
        String xml = comprobanteUseCase.downloadXml(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(id + ".xml")
                        .build()
                        .toString())
                .body(xml.getBytes(StandardCharsets.UTF_8));
    }

    @GetMapping("/{id}/ride")
    @PreAuthorize("hasAnyRole('ADMIN','EMISOR','CONTABILIDAD','CONSULTA','SOPORTE')")
    ResponseEntity<byte[]> ride(@PathVariable UUID id) {
        byte[] pdf = comprobanteUseCase.downloadRide(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(id + ".pdf")
                        .build()
                        .toString())
                .body(pdf);
    }

    @PostMapping("/{id}/enviar-correo")
    @PreAuthorize("hasAnyRole('ADMIN','EMISOR','CONTABILIDAD')")
    ApiResponse<Void> enviarCorreo(@PathVariable UUID id) {
        comprobanteUseCase.enviarCorreo(id);
        return ApiResponse.ok("Envio de correo procesado", null);
    }

    @PostMapping("/correos/pendientes/procesar")
    @PreAuthorize("hasAnyRole('ADMIN','EMISOR','CONTABILIDAD','SOPORTE')")
    ApiResponse<Integer> procesarCorreosPendientes() {
        return ApiResponse.ok("Correos pendientes procesados", comprobanteUseCase.procesarCorreosPendientes());
    }
}
