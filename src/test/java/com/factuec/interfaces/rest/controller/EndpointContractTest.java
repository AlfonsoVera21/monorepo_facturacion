package com.factuec.interfaces.rest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.factuec.application.usecase.AuthUseCase;
import com.factuec.application.usecase.ChoferUseCase;
import com.factuec.application.usecase.ClienteUseCase;
import com.factuec.application.usecase.ComprobanteUseCase;
import com.factuec.application.usecase.EmisionConfigUseCase;
import com.factuec.application.usecase.EmpresaUseCase;
import com.factuec.application.usecase.FirmaElectronicaUseCase;
import com.factuec.application.usecase.ProductoUseCase;
import com.factuec.application.usecase.ReportesUseCase;
import com.factuec.application.usecase.UserManagementUseCase;
import com.factuec.config.FactuEcProperties;
import com.factuec.infrastructure.persistence.repository.AuditLogRepository;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class EndpointContractTest {
    private static final UUID ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID EMPRESA_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID ESTABLECIMIENTO_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final UUID PUNTO_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private static final UUID CLIENTE_ID = UUID.fromString("55555555-5555-5555-5555-555555555555");

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        AuditLogRepository auditLogRepository = mock(AuditLogRepository.class);
        AuthUseCase authUseCase = mock(AuthUseCase.class);
        ChoferUseCase choferUseCase = mock(ChoferUseCase.class);
        ClienteUseCase clienteUseCase = mock(ClienteUseCase.class);
        ComprobanteUseCase comprobanteUseCase = mock(ComprobanteUseCase.class);
        EmisionConfigUseCase emisionConfigUseCase = mock(EmisionConfigUseCase.class);
        EmpresaUseCase empresaUseCase = mock(EmpresaUseCase.class);
        FirmaElectronicaUseCase firmaUseCase = mock(FirmaElectronicaUseCase.class);
        ProductoUseCase productoUseCase = mock(ProductoUseCase.class);
        ReportesUseCase reportesUseCase = mock(ReportesUseCase.class);
        UserManagementUseCase userManagementUseCase = mock(UserManagementUseCase.class);

        when(auditLogRepository.findTop50ByEntityTypeOrderByCreatedAtDesc(anyString())).thenReturn(List.of());
        when(comprobanteUseCase.downloadXml(any(UUID.class))).thenReturn("<factura/>");
        when(comprobanteUseCase.downloadRide(any(UUID.class))).thenReturn(new byte[] {1, 2, 3});
        when(comprobanteUseCase.reenviarPendientes()).thenReturn(List.of());
        when(firmaUseCase.findByEmpresa(any(UUID.class))).thenReturn(List.of());
        when(firmaUseCase.listCertificadosDisponibles()).thenReturn(List.of());
        when(reportesUseCase.comprobantes(any(UUID.class))).thenReturn(List.of());
        when(userManagementUseCase.listUsers()).thenReturn(List.of());
        when(userManagementUseCase.listRoles()).thenReturn(List.of());

        FactuEcProperties properties = properties();

        mockMvc = MockMvcBuilders.standaloneSetup(
                new AuditLogController(auditLogRepository),
                new AuthController(authUseCase),
                new ChoferController(choferUseCase),
                new ClienteController(clienteUseCase),
                new ComprobanteController(comprobanteUseCase),
                new ConfiguracionController(properties),
                new EmisionConfigController(emisionConfigUseCase),
                new EmpresaController(empresaUseCase),
                new FirmaElectronicaController(firmaUseCase),
                new ProductoController(productoUseCase),
                new ReporteController(reportesUseCase),
                new SriController(properties, comprobanteUseCase),
                new UserManagementController(userManagementUseCase)
        ).build();
    }

    @ParameterizedTest(name = "{0} acepta una solicitud valida")
    @MethodSource("endpoints")
    void endpointAcceptsValidRequest(EndpointCase endpoint) throws Exception {
        mockMvc.perform(endpoint.validRequest())
                .andExpect(status().is2xxSuccessful());
    }

    @ParameterizedTest(name = "{0} rechaza un metodo no permitido")
    @MethodSource("endpoints")
    void endpointRejectsUnsupportedMethod(EndpointCase endpoint) throws Exception {
        mockMvc.perform(endpoint.unsupportedRequest())
                .andExpect(status().isMethodNotAllowed());
    }

    private static Stream<EndpointCase> endpoints() {
        return Stream.of(
                endpoint("GET /api/audit-logs", get("/api/audit-logs").param("entityType", "FirmaElectronica"), "/api/audit-logs"),

                endpoint("POST /api/auth/login", postJson("/api/auth/login", loginJson()), "/api/auth/login"),
                endpoint("POST /api/auth/refresh", postJson("/api/auth/refresh", refreshJson()), "/api/auth/refresh"),
                endpoint("GET /api/auth/me", get("/api/auth/me"), "/api/auth/me"),

                endpoint("GET /api/clientes", get("/api/clientes").param("empresaId", EMPRESA_ID.toString()), "/api/clientes"),
                endpoint("POST /api/clientes", postJson("/api/clientes", clienteJson()), "/api/clientes"),
                endpoint("GET /api/clientes/{id}", get("/api/clientes/{id}", ID), "/api/clientes/{id}", ID),
                endpoint("PUT /api/clientes/{id}", putJson("/api/clientes/{id}", clienteJson(), ID), "/api/clientes/{id}", ID),
                endpoint("DELETE /api/clientes/{id}", delete("/api/clientes/{id}", ID), "/api/clientes/{id}", ID),

                endpoint("GET /api/choferes", get("/api/choferes").param("empresaId", EMPRESA_ID.toString()), "/api/choferes"),
                endpoint("POST /api/choferes", postJson("/api/choferes", choferJson()), "/api/choferes"),
                endpoint("GET /api/choferes/{id}", get("/api/choferes/{id}", ID), "/api/choferes/{id}", ID),
                endpoint("PUT /api/choferes/{id}", putJson("/api/choferes/{id}", choferJson(), ID), "/api/choferes/{id}", ID),
                endpoint("DELETE /api/choferes/{id}", delete("/api/choferes/{id}", ID), "/api/choferes/{id}", ID),

                endpoint("GET /api/comprobantes", get("/api/comprobantes").param("empresaId", EMPRESA_ID.toString()), "/api/comprobantes"),
                endpoint("GET /api/comprobantes/{id}", get("/api/comprobantes/{id}", ID), "/api/comprobantes/{id}", ID),
                endpoint("POST /api/comprobantes/facturas/borrador", postJson("/api/comprobantes/facturas/borrador", facturaJson()), "/api/comprobantes/facturas/borrador"),
                endpoint("POST /api/comprobantes/facturas/emitir", postJson("/api/comprobantes/facturas/emitir", facturaJson()), "/api/comprobantes/facturas/emitir"),
                endpoint("POST /api/comprobantes/guias-remision/emitir", postJson("/api/comprobantes/guias-remision/emitir", guiaRemisionJson()), "/api/comprobantes/guias-remision/emitir"),
                endpoint("POST /api/comprobantes/{id}/reenviar-sri", post("/api/comprobantes/{id}/reenviar-sri", ID), "/api/comprobantes/{id}/reenviar-sri", ID),
                endpoint("POST /api/comprobantes/{id}/consultar-autorizacion", post("/api/comprobantes/{id}/consultar-autorizacion", ID), "/api/comprobantes/{id}/consultar-autorizacion", ID),
                endpoint("GET /api/comprobantes/{id}/xml", get("/api/comprobantes/{id}/xml", ID), "/api/comprobantes/{id}/xml", ID),
                endpoint("GET /api/comprobantes/{id}/ride", get("/api/comprobantes/{id}/ride", ID), "/api/comprobantes/{id}/ride", ID),
                endpoint("POST /api/comprobantes/{id}/enviar-correo", post("/api/comprobantes/{id}/enviar-correo", ID), "/api/comprobantes/{id}/enviar-correo", ID),
                endpoint("POST /api/comprobantes/correos/pendientes/procesar", post("/api/comprobantes/correos/pendientes/procesar"), "/api/comprobantes/correos/pendientes/procesar"),

                endpoint("GET /api/configuracion/general", get("/api/configuracion/general"), "/api/configuracion/general"),

                endpoint("GET /api/establecimientos", get("/api/establecimientos").param("empresaId", EMPRESA_ID.toString()), "/api/establecimientos"),
                endpoint("POST /api/establecimientos", postJson("/api/establecimientos", establecimientoJson()), "/api/establecimientos"),
                endpoint("PUT /api/establecimientos/{id}", putJson("/api/establecimientos/{id}", establecimientoJson(), ESTABLECIMIENTO_ID), "/api/establecimientos/{id}", ESTABLECIMIENTO_ID),
                endpoint("GET /api/puntos-emision", get("/api/puntos-emision").param("establecimientoId", ESTABLECIMIENTO_ID.toString()), "/api/puntos-emision"),
                endpoint("POST /api/puntos-emision", postJson("/api/puntos-emision", puntoEmisionJson()), "/api/puntos-emision"),
                endpoint("PUT /api/puntos-emision/{id}", putJson("/api/puntos-emision/{id}", puntoEmisionJson(), PUNTO_ID), "/api/puntos-emision/{id}", PUNTO_ID),
                endpoint("POST /api/secuenciales", postJson("/api/secuenciales", secuencialJson()), "/api/secuenciales"),

                endpoint("GET /api/empresas", get("/api/empresas"), "/api/empresas"),
                endpoint("POST /api/empresas", postJson("/api/empresas", empresaJson()), "/api/empresas"),
                endpoint("GET /api/empresas/{id}", get("/api/empresas/{id}", EMPRESA_ID), "/api/empresas/{id}", EMPRESA_ID),
                endpoint("PUT /api/empresas/{id}", putJson("/api/empresas/{id}", empresaJson(), EMPRESA_ID), "/api/empresas/{id}", EMPRESA_ID),

                endpoint("POST /api/firmas", postJson("/api/firmas", firmaJson()), "/api/firmas"),
                endpoint("GET /api/firmas/empresa/{empresaId}", get("/api/firmas/empresa/{empresaId}", EMPRESA_ID), "/api/firmas/empresa/{empresaId}", EMPRESA_ID),
                endpoint("GET /api/firmas/certificados", get("/api/firmas/certificados"), "/api/firmas/certificados"),
                endpoint("PUT /api/firmas/{id}", putJson("/api/firmas/{id}", firmaJson(), ID), "/api/firmas/{id}", ID),

                endpoint("GET /api/productos", get("/api/productos").param("empresaId", EMPRESA_ID.toString()), "/api/productos"),
                endpoint("POST /api/productos", postJson("/api/productos", productoJson()), "/api/productos"),
                endpoint("GET /api/productos/{id}", get("/api/productos/{id}", ID), "/api/productos/{id}", ID),
                endpoint("PUT /api/productos/{id}", putJson("/api/productos/{id}", productoJson(), ID), "/api/productos/{id}", ID),
                endpoint("DELETE /api/productos/{id}", delete("/api/productos/{id}", ID), "/api/productos/{id}", ID),

                endpoint("GET /api/reportes/ventas", get("/api/reportes/ventas").param("empresaId", EMPRESA_ID.toString()), "/api/reportes/ventas"),
                endpoint("GET /api/reportes/comprobantes", get("/api/reportes/comprobantes").param("empresaId", EMPRESA_ID.toString()), "/api/reportes/comprobantes"),
                endpoint("GET /api/reportes/clientes", get("/api/reportes/clientes").param("empresaId", EMPRESA_ID.toString()), "/api/reportes/clientes"),
                endpoint("GET /api/reportes/productos", get("/api/reportes/productos").param("empresaId", EMPRESA_ID.toString()), "/api/reportes/productos"),

                endpoint("GET /api/sri/estado", get("/api/sri/estado"), "/api/sri/estado"),
                endpoint("GET /api/sri/errores", get("/api/sri/errores"), "/api/sri/errores"),
                endpoint("POST /api/sri/reenviar-pendientes", post("/api/sri/reenviar-pendientes"), "/api/sri/reenviar-pendientes"),

                endpoint("GET /api/users", get("/api/users"), "/api/users"),
                endpoint("POST /api/users", postJson("/api/users", userJson()), "/api/users"),
                endpoint("PUT /api/users/{id}", putJson("/api/users/{id}", userJson(), ID), "/api/users/{id}", ID),
                endpoint("DELETE /api/users/{id}", delete("/api/users/{id}", ID), "/api/users/{id}", ID),
                endpoint("GET /api/roles", get("/api/roles"), "/api/roles")
        );
    }

    private static EndpointCase endpoint(String name, MockHttpServletRequestBuilder validRequest, String unsupportedPath, Object... uriVariables) {
        return new EndpointCase(name, validRequest, patch(unsupportedPath, uriVariables));
    }

    private static MockHttpServletRequestBuilder postJson(String path, String json, Object... uriVariables) {
        return post(path, uriVariables).contentType(APPLICATION_JSON).content(json);
    }

    private static MockHttpServletRequestBuilder putJson(String path, String json, Object... uriVariables) {
        return put(path, uriVariables).contentType(APPLICATION_JSON).content(json);
    }

    private static String loginJson() {
        return """
                {"username":"admin","password":"change-this-admin-password"}
                """;
    }

    private static String refreshJson() {
        return """
                {"refreshToken":"refresh-token"}
                """;
    }

    private static String clienteJson() {
        return """
                {"empresaId":"%s","tipoIdentificacion":"RUC","identificacion":"0999999999001","razonSocial":"Cliente Test","correo":"cliente@test.local","activo":true}
                """.formatted(EMPRESA_ID);
    }

    private static String choferJson() {
        return """
                {"empresaId":"%s","tipoIdentificacion":"CEDULA","identificacion":"0912345678","nombres":"Carlos","apellidos":"Mora","licencia":"LIC-12345","telefono":"0999999999","correo":"chofer@test.local","placaVehiculo":"ABC1234","tipoVehiculo":"Camion refrigerado","capacidad":1200,"unidadCapacidad":"KILOGRAMO","transportaRefrigerado":true,"activo":true}
                """.formatted(EMPRESA_ID);
    }

    private static String empresaJson() {
        return """
                {"ruc":"0999999999001","razonSocial":"Empresa Test","nombreComercial":"Empresa Test","direccionMatriz":"Direccion matriz","obligadoContabilidad":false,"ambiente":"PRUEBAS","activo":true}
                """;
    }

    private static String establecimientoJson() {
        return """
                {"empresaId":"%s","codigo":"001","nombre":"MATRIZ","direccion":"Direccion matriz","activo":true}
                """.formatted(EMPRESA_ID);
    }

    private static String puntoEmisionJson() {
        return """
                {"establecimientoId":"%s","codigo":"001","nombre":"PUNTO 001","activo":true}
                """.formatted(ESTABLECIMIENTO_ID);
    }

    private static String secuencialJson() {
        return """
                {"empresaId":"%s","establecimientoId":"%s","puntoEmisionId":"%s","tipoComprobante":"FACTURA","ultimoSecuencial":0}
                """.formatted(EMPRESA_ID, ESTABLECIMIENTO_ID, PUNTO_ID);
    }

    private static String firmaJson() {
        return """
                {"empresaId":"%s","nombreArchivo":"firma.p12","rutaSegura":"/app/storage/certificates/firma.p12","passwordSecretRef":"FIRMA_ELECTRONICA_PASSWORD","estado":"ACTIVA"}
                """.formatted(EMPRESA_ID);
    }

    private static String productoJson() {
        return """
                {"empresaId":"%s","codigoPrincipal":"P001","nombre":"Producto Test","tipo":"PRODUCTO","precioUnitario":1.00,"tarifaIva":"IVA_15","stock":25,"unidadMedida":"KILOGRAMO","stockMinimo":5,"pesoPromedioKg":1,"palletizable":true,"unidadesPorPallet":48,"requiereRefrigeracion":true,"activo":true}
                """.formatted(EMPRESA_ID);
    }

    private static String facturaJson() {
        return """
                {"empresaId":"%s","clienteId":"%s","establecimientoId":"%s","puntoEmisionId":"%s","fechaEmision":"2026-07-13","formaPago":"SIN_UTILIZACION_SISTEMA_FINANCIERO","detalles":[{"codigoPrincipal":"P001","descripcion":"Producto Test","cantidad":1,"precioUnitario":1.00,"descuento":0,"tarifaIva":"IVA_15"}]}
                """.formatted(EMPRESA_ID, CLIENTE_ID, ESTABLECIMIENTO_ID, PUNTO_ID);
    }

    private static String guiaRemisionJson() {
        return """
                {"empresaId":"%s","clienteId":"%s","establecimientoId":"%s","puntoEmisionId":"%s","fechaEmision":"2026-07-13","dirPartida":"Bodega matriz","razonSocialTransportista":"Transportes Test","tipoIdentificacionTransportista":"RUC","identificacionTransportista":"0999999999001","fechaIniTransporte":"2026-07-13","fechaFinTransporte":"2026-07-14","placa":"ABC1234","destinatarioDireccion":"Direccion destino","motivoTraslado":"Venta","ruta":"Quito - Guayaquil","codDocSustento":"01","numDocSustento":"001-001-000000001","numAutDocSustento":"0607202601099999999900110010010000000011234567811","fechaEmisionDocSustento":"2026-07-13","detalles":[{"codigoInterno":"P001","descripcion":"Producto Test","cantidad":1}]}
                """.formatted(EMPRESA_ID, CLIENTE_ID, ESTABLECIMIENTO_ID, PUNTO_ID);
    }

    private static String userJson() {
        return """
                {"username":"emisor","email":"emisor@test.local","fullName":"Usuario Emisor","password":"password-123","active":true,"roles":["EMISOR"]}
                """;
    }

    private static FactuEcProperties properties() {
        return new FactuEcProperties(
                new FactuEcProperties.Jwt("change-this-local-secret-with-at-least-32-bytes", Duration.ofMinutes(30), Duration.ofDays(7)),
                new FactuEcProperties.Sri(
                        "PRUEBAS",
                        "https://celcer.sri.gob.ec/recepcion?wsdl",
                        "https://celcer.sri.gob.ec/autorizacion?wsdl",
                        "https://cel.sri.gob.ec/recepcion?wsdl",
                        "https://cel.sri.gob.ec/autorizacion?wsdl",
                        false),
                new FactuEcProperties.Storage("./storage/xml", "./storage/pdf", "./storage/certificates"),
                new FactuEcProperties.Signature(false),
                new FactuEcProperties.Soap(Duration.ofSeconds(10), Duration.ofSeconds(30), 2),
                new FactuEcProperties.Mail(false, "no-reply@factuec.local"),
                new FactuEcProperties.Bootstrap(true, "admin", "admin@factuec.local", "change-this-admin-password"),
                new FactuEcProperties.Cors("http://localhost:4200")
        );
    }

    private record EndpointCase(
            String name,
            MockHttpServletRequestBuilder validRequest,
            MockHttpServletRequestBuilder unsupportedRequest
    ) {
        @Override
        public String toString() {
            return name;
        }
    }
}
