package com.factuec.application.usecase;

import com.factuec.application.dto.comprobante.ComprobanteResponse;
import com.factuec.application.dto.comprobante.FacturaDetalleRequest;
import com.factuec.application.dto.comprobante.FacturaDetalleResponse;
import com.factuec.application.dto.comprobante.FacturaRequest;
import com.factuec.application.dto.comprobante.GuiaRemisionDetalleRequest;
import com.factuec.application.dto.comprobante.GuiaRemisionRequest;
import com.factuec.application.dto.comprobante.SriMensajeResponse;
import com.factuec.application.port.out.RideGeneratorPort;
import com.factuec.application.port.out.SignaturePort;
import com.factuec.application.port.out.SriAuthorizationPort;
import com.factuec.application.port.out.SriReceptionPort;
import com.factuec.application.port.out.XmlGeneratorPort;
import com.factuec.config.FactuEcProperties;
import com.factuec.domain.enums.AuditAction;
import com.factuec.domain.enums.EstadoComprobante;
import com.factuec.domain.enums.EstadoFirma;
import com.factuec.domain.enums.EstadoSri;
import com.factuec.domain.enums.FormaPago;
import com.factuec.domain.enums.TarifaIva;
import com.factuec.domain.enums.TipoComprobante;
import com.factuec.domain.enums.TipoEmision;
import com.factuec.domain.model.InvoiceLine;
import com.factuec.domain.model.InvoiceTotals;
import com.factuec.domain.service.InvoiceCalculator;
import com.factuec.domain.valueobject.ComprobanteNumber;
import com.factuec.infrastructure.persistence.entity.ClienteEntity;
import com.factuec.infrastructure.persistence.entity.ComprobanteDetalleEntity;
import com.factuec.infrastructure.persistence.entity.ComprobanteEntity;
import com.factuec.infrastructure.persistence.entity.ComprobantePagoEntity;
import com.factuec.infrastructure.persistence.entity.EmpresaEntity;
import com.factuec.infrastructure.persistence.entity.EstablecimientoEntity;
import com.factuec.infrastructure.persistence.entity.FirmaElectronicaEntity;
import com.factuec.infrastructure.persistence.entity.ProductoEntity;
import com.factuec.infrastructure.persistence.entity.PuntoEmisionEntity;
import com.factuec.infrastructure.persistence.entity.SecuencialEntity;
import com.factuec.infrastructure.persistence.entity.SriMensajeEntity;
import com.factuec.infrastructure.persistence.repository.ClienteRepository;
import com.factuec.infrastructure.persistence.repository.ComprobanteRepository;
import com.factuec.infrastructure.persistence.repository.EstablecimientoRepository;
import com.factuec.infrastructure.persistence.repository.FirmaElectronicaRepository;
import com.factuec.infrastructure.persistence.repository.ProductoRepository;
import com.factuec.infrastructure.persistence.repository.PuntoEmisionRepository;
import com.factuec.infrastructure.persistence.repository.SecuencialRepository;
import com.factuec.infrastructure.persistence.repository.SriMensajeRepository;
import com.factuec.infrastructure.persistence.repository.UserRepository;
import com.factuec.infrastructure.signature.FirmaConfig;
import com.factuec.infrastructure.sri.SriAuthorizationResult;
import com.factuec.infrastructure.sri.SriReceptionResult;
import com.factuec.infrastructure.sri.SriResponseMessage;
import com.factuec.shared.exception.BusinessException;
import com.factuec.shared.exception.ResourceNotFoundException;
import com.factuec.shared.security.UserPrincipal;
import com.factuec.shared.util.SriAccessKeyGenerator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ComprobanteUseCase {
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100.00");

    private final EmpresaUseCase empresaUseCase;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final EstablecimientoRepository establecimientoRepository;
    private final PuntoEmisionRepository puntoEmisionRepository;
    private final SecuencialRepository secuencialRepository;
    private final ComprobanteRepository comprobanteRepository;
    private final FirmaElectronicaRepository firmaRepository;
    private final SriMensajeRepository sriMensajeRepository;
    private final UserRepository userRepository;
    private final InvoiceCalculator invoiceCalculator;
    private final SriAccessKeyGenerator accessKeyGenerator;
    private final XmlGeneratorPort xmlGeneratorPort;
    private final SignaturePort signaturePort;
    private final SriReceptionPort sriReceptionPort;
    private final SriAuthorizationPort sriAuthorizationPort;
    private final RideGeneratorPort rideGeneratorPort;
    private final ComprobanteEmailUseCase comprobanteEmailUseCase;
    private final FactuEcProperties properties;
    private final AuditService auditService;

    public ComprobanteUseCase(EmpresaUseCase empresaUseCase,
                              ClienteRepository clienteRepository,
                              ProductoRepository productoRepository,
                              EstablecimientoRepository establecimientoRepository,
                              PuntoEmisionRepository puntoEmisionRepository,
                              SecuencialRepository secuencialRepository,
                              ComprobanteRepository comprobanteRepository,
                              FirmaElectronicaRepository firmaRepository,
                              SriMensajeRepository sriMensajeRepository,
                              UserRepository userRepository,
                              InvoiceCalculator invoiceCalculator,
                              SriAccessKeyGenerator accessKeyGenerator,
                              XmlGeneratorPort xmlGeneratorPort,
                              SignaturePort signaturePort,
                              SriReceptionPort sriReceptionPort,
                              SriAuthorizationPort sriAuthorizationPort,
                              RideGeneratorPort rideGeneratorPort,
                              ComprobanteEmailUseCase comprobanteEmailUseCase,
                              FactuEcProperties properties,
                              AuditService auditService) {
        this.empresaUseCase = empresaUseCase;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
        this.establecimientoRepository = establecimientoRepository;
        this.puntoEmisionRepository = puntoEmisionRepository;
        this.secuencialRepository = secuencialRepository;
        this.comprobanteRepository = comprobanteRepository;
        this.firmaRepository = firmaRepository;
        this.sriMensajeRepository = sriMensajeRepository;
        this.userRepository = userRepository;
        this.invoiceCalculator = invoiceCalculator;
        this.accessKeyGenerator = accessKeyGenerator;
        this.xmlGeneratorPort = xmlGeneratorPort;
        this.signaturePort = signaturePort;
        this.sriReceptionPort = sriReceptionPort;
        this.sriAuthorizationPort = sriAuthorizationPort;
        this.rideGeneratorPort = rideGeneratorPort;
        this.comprobanteEmailUseCase = comprobanteEmailUseCase;
        this.properties = properties;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<ComprobanteResponse> list(UUID empresaId) {
        List<ComprobanteEntity> comprobantes = empresaId == null
                ? comprobanteRepository.findAll()
                : comprobanteRepository.findByEmpresaIdOrderByFechaEmisionDescCreatedAtDesc(empresaId);
        return comprobantes.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ComprobanteResponse get(UUID id) {
        return toResponse(findEntity(id));
    }

    @Transactional
    public ComprobanteResponse guardarBorrador(FacturaRequest request) {
        ComprobanteEntity comprobante = buildFactura(request, EstadoComprobante.BORRADOR);
        comprobante.setEstadoSri(EstadoSri.PENDIENTE);
        ComprobanteEntity saved = comprobanteRepository.save(comprobante);
        auditService.log(AuditAction.CREACION_FACTURA, "Comprobante", saved.getId(), "Borrador de factura creado", null);
        return toResponse(saved);
    }

    @Transactional
    public ComprobanteResponse emitirFactura(FacturaRequest request) {
        ComprobanteEntity comprobante = buildFactura(request, EstadoComprobante.GENERADO);
        comprobante.setXmlGenerado(xmlGeneratorPort.generateFactura(comprobante));
        comprobante = comprobanteRepository.save(comprobante);
        auditService.log(AuditAction.CREACION_FACTURA, "Comprobante", comprobante.getId(), "Factura generada", null);

        String xmlFirmado = signaturePort.firmarXml(comprobante.getXmlGenerado(), resolveFirma(comprobante.getEmpresa().getId()));
        comprobante.setXmlFirmado(xmlFirmado);
        comprobante.setEstadoInterno(EstadoComprobante.FIRMADO);
        auditService.log(AuditAction.FIRMA, "Comprobante", comprobante.getId(), "Factura firmada", null);

        SriReceptionResult reception = sriReceptionPort.enviarComprobante(comprobante.getAmbiente(), xmlFirmado);
        comprobante.setEstadoInterno(EstadoComprobante.ENVIADO);
        comprobante.setEstadoSri(reception.estado());
        saveMessages(comprobante, reception.mensajes(), reception.estado());
        auditService.log(AuditAction.ENVIO_SRI, "Comprobante", comprobante.getId(), "Factura enviada al SRI", reception.estado().name());

        if (reception.estado() != EstadoSri.RECIBIDA) {
            comprobante.setEstadoInterno(reception.estado() == EstadoSri.DEVUELTA
                    ? EstadoComprobante.DEVUELTO
                    : EstadoComprobante.ERROR);
            return toResponse(comprobanteRepository.save(comprobante));
        }

        SriAuthorizationResult authorization = sriAuthorizationPort.consultarAutorizacion(comprobante.getAmbiente(), comprobante.getClaveAcceso());
        applyAuthorization(comprobante, authorization);
        saveMessages(comprobante, authorization.mensajes(), authorization.estado());
        ComprobanteEntity saved = comprobanteRepository.save(comprobante);
        auditService.log(AuditAction.AUTORIZACION, "Comprobante", saved.getId(), "Autorizacion consultada", authorization.estado().name());
        comprobanteEmailUseCase.registrarYEnviarAutorizado(saved);
        return toResponse(saved);
    }

    @Transactional
    public ComprobanteResponse emitirGuiaRemision(GuiaRemisionRequest request) {
        ComprobanteEntity comprobante = buildGuiaRemision(request, EstadoComprobante.GENERADO);
        comprobante.setXmlGenerado(xmlGeneratorPort.generateGuiaRemision(comprobante));
        comprobante = comprobanteRepository.save(comprobante);
        auditService.log(AuditAction.CREACION_GUIA_REMISION, "Comprobante", comprobante.getId(), "Guia de remision generada", null);

        String xmlFirmado = signaturePort.firmarXml(comprobante.getXmlGenerado(), resolveFirma(comprobante.getEmpresa().getId()));
        comprobante.setXmlFirmado(xmlFirmado);
        comprobante.setEstadoInterno(EstadoComprobante.FIRMADO);
        auditService.log(AuditAction.FIRMA, "Comprobante", comprobante.getId(), "Guia de remision firmada", null);

        SriReceptionResult reception = sriReceptionPort.enviarComprobante(comprobante.getAmbiente(), xmlFirmado);
        comprobante.setEstadoInterno(EstadoComprobante.ENVIADO);
        comprobante.setEstadoSri(reception.estado());
        saveMessages(comprobante, reception.mensajes(), reception.estado());
        auditService.log(AuditAction.ENVIO_SRI, "Comprobante", comprobante.getId(), "Guia de remision enviada al SRI", reception.estado().name());

        if (reception.estado() != EstadoSri.RECIBIDA) {
            comprobante.setEstadoInterno(reception.estado() == EstadoSri.DEVUELTA
                    ? EstadoComprobante.DEVUELTO
                    : EstadoComprobante.ERROR);
            return toResponse(comprobanteRepository.save(comprobante));
        }

        SriAuthorizationResult authorization = sriAuthorizationPort.consultarAutorizacion(comprobante.getAmbiente(), comprobante.getClaveAcceso());
        applyAuthorization(comprobante, authorization);
        saveMessages(comprobante, authorization.mensajes(), authorization.estado());
        ComprobanteEntity saved = comprobanteRepository.save(comprobante);
        auditService.log(AuditAction.AUTORIZACION, "Comprobante", saved.getId(), "Autorizacion consultada", authorization.estado().name());
        comprobanteEmailUseCase.registrarYEnviarAutorizado(saved);
        return toResponse(saved);
    }

    @Transactional
    public ComprobanteResponse reenviarSri(UUID id) {
        ComprobanteEntity comprobante = findEntity(id);
        if (comprobante.getXmlFirmado() == null || comprobante.getXmlFirmado().isBlank()) {
            throw new BusinessException("El comprobante no tiene XML firmado para reenviar");
        }
        SriReceptionResult reception = sriReceptionPort.enviarComprobante(comprobante.getAmbiente(), comprobante.getXmlFirmado());
        comprobante.setEstadoInterno(EstadoComprobante.ENVIADO);
        comprobante.setEstadoSri(reception.estado());
        saveMessages(comprobante, reception.mensajes(), reception.estado());
        auditService.log(AuditAction.REENVIO, "Comprobante", id, "Comprobante reenviado al SRI", reception.estado().name());
        return toResponse(comprobanteRepository.save(comprobante));
    }

    @Transactional
    public ComprobanteResponse consultarAutorizacion(UUID id) {
        ComprobanteEntity comprobante = findEntity(id);
        SriAuthorizationResult authorization = sriAuthorizationPort.consultarAutorizacion(comprobante.getAmbiente(), comprobante.getClaveAcceso());
        applyAuthorization(comprobante, authorization);
        saveMessages(comprobante, authorization.mensajes(), authorization.estado());
        auditService.log(AuditAction.AUTORIZACION, "Comprobante", id, "Autorizacion consultada", authorization.estado().name());
        ComprobanteEntity saved = comprobanteRepository.save(comprobante);
        comprobanteEmailUseCase.registrarYEnviarAutorizado(saved);
        return toResponse(saved);
    }

    @Transactional
    public List<ComprobanteResponse> reenviarPendientes() {
        return comprobanteRepository.findByEstadoInterno(EstadoComprobante.DEVUELTO).stream()
                .filter(comprobante -> comprobante.getXmlFirmado() != null && !comprobante.getXmlFirmado().isBlank())
                .map(comprobante -> reenviarSri(comprobante.getId()))
                .toList();
    }

    @Transactional
    public String downloadXml(UUID id) {
        ComprobanteEntity comprobante = findEntity(id);
        String xml = comprobante.getXmlFirmado() != null ? comprobante.getXmlFirmado() : comprobante.getXmlGenerado();
        if (xml == null || xml.isBlank()) {
            throw new BusinessException("El comprobante no tiene XML generado");
        }
        auditService.log(AuditAction.DESCARGA_XML, "Comprobante", id, "Descarga de XML", null);
        return xml;
    }

    @Transactional
    public byte[] downloadRide(UUID id) {
        ComprobanteEntity comprobante = findEntity(id);
        byte[] ride = rideGeneratorPort.generateRide(comprobante);
        auditService.log(AuditAction.DESCARGA_PDF, "Comprobante", id, "Descarga de RIDE", null);
        return ride;
    }

    @Transactional
    public void enviarCorreo(UUID id) {
        ComprobanteEntity comprobante = findEntity(id);
        comprobanteEmailUseCase.enviarAhora(comprobante);
    }

    @Transactional
    public int procesarCorreosPendientes() {
        return comprobanteEmailUseCase.procesarPendientes();
    }

    private ComprobanteEntity buildFactura(FacturaRequest request, EstadoComprobante estado) {
        EmpresaEntity empresa = empresaUseCase.findEntity(request.empresaId());
        ClienteEntity cliente = clienteRepository.findByIdAndEmpresaId(request.clienteId(), request.empresaId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado para la empresa"));
        EstablecimientoEntity establecimiento = establecimientoRepository.findByIdAndEmpresaId(request.establecimientoId(), request.empresaId())
                .orElseThrow(() -> new ResourceNotFoundException("Establecimiento no encontrado para la empresa"));
        PuntoEmisionEntity puntoEmision = puntoEmisionRepository.findByIdAndEstablecimientoId(request.puntoEmisionId(), request.establecimientoId())
                .orElseThrow(() -> new ResourceNotFoundException("Punto de emision no encontrado para el establecimiento"));

        long secuencial = nextSecuencial(empresa, establecimiento, puntoEmision, TipoComprobante.FACTURA);
        ComprobanteNumber number = new ComprobanteNumber(establecimiento.getCodigo(), puntoEmision.getCodigo(), secuencial);
        LocalDate fechaEmision = request.fechaEmision() == null ? LocalDate.now() : request.fechaEmision();
        String codigoNumerico = String.format("%08d", ThreadLocalRandom.current().nextInt(100_000_000));
        String claveAcceso = accessKeyGenerator.generate(
                fechaEmision,
                TipoComprobante.FACTURA,
                empresa.getRuc(),
                empresa.getAmbiente(),
                number,
                codigoNumerico,
                TipoEmision.NORMAL);

        ComprobanteEntity comprobante = new ComprobanteEntity();
        comprobante.setEmpresa(empresa);
        comprobante.setCliente(cliente);
        comprobante.setEstablecimiento(establecimiento);
        comprobante.setPuntoEmision(puntoEmision);
        comprobante.setTipoComprobante(TipoComprobante.FACTURA);
        comprobante.setSecuencial(secuencial);
        comprobante.setNumeroCompleto(number.formatted());
        comprobante.setFechaEmision(fechaEmision);
        comprobante.setAmbiente(empresa.getAmbiente());
        comprobante.setTipoEmision(TipoEmision.NORMAL);
        comprobante.setClaveAcceso(claveAcceso);
        comprobante.setEstadoInterno(estado);
        comprobante.setEstadoSri(EstadoSri.PENDIENTE);
        comprobante.setFormaPago(request.formaPago());
        comprobante.setPlazo(request.plazo());
        comprobante.setTiempo(request.tiempo());
        setAuthenticatedCreator(comprobante);

        List<ComprobanteDetalleEntity> detalles = request.detalles().stream()
                .map(detalle -> toDetalle(detalle, request.empresaId()))
                .toList();
        InvoiceTotals totals = invoiceCalculator.calculate(detalles.stream()
                .map(detalle -> new InvoiceLine(
                        detalle.getCodigoPrincipal(),
                        detalle.getDescripcion(),
                        detalle.getCantidad(),
                        detalle.getPrecioUnitario(),
                        detalle.getDescuento(),
                        detalle.getTarifaIva()))
                .toList());
        comprobante.setSubtotal0(totals.subtotal0());
        comprobante.setSubtotalIva(totals.subtotalIva());
        comprobante.setDescuentoTotal(totals.descuentoTotal());
        comprobante.setIvaTotal(totals.ivaTotal());
        comprobante.setIceTotal(totals.iceTotal());
        comprobante.setTotal(totals.total());
        detalles.forEach(comprobante::addDetalle);

        ComprobantePagoEntity pago = new ComprobantePagoEntity();
        pago.setFormaPago(request.formaPago());
        pago.setTotal(totals.total());
        pago.setPlazo(request.plazo());
        pago.setTiempo(request.tiempo());
        comprobante.addPago(pago);
        return comprobante;
    }

    private ComprobanteEntity buildGuiaRemision(GuiaRemisionRequest request, EstadoComprobante estado) {
        EmpresaEntity empresa = empresaUseCase.findEntity(request.empresaId());
        ClienteEntity cliente = clienteRepository.findByIdAndEmpresaId(request.clienteId(), request.empresaId())
                .orElseThrow(() -> new ResourceNotFoundException("Destinatario no encontrado para la empresa"));
        EstablecimientoEntity establecimiento = establecimientoRepository.findByIdAndEmpresaId(request.establecimientoId(), request.empresaId())
                .orElseThrow(() -> new ResourceNotFoundException("Establecimiento no encontrado para la empresa"));
        PuntoEmisionEntity puntoEmision = puntoEmisionRepository.findByIdAndEstablecimientoId(request.puntoEmisionId(), request.establecimientoId())
                .orElseThrow(() -> new ResourceNotFoundException("Punto de emision no encontrado para el establecimiento"));

        LocalDate fechaEmision = request.fechaEmision() == null ? LocalDate.now() : request.fechaEmision();
        validateTransportDates(fechaEmision, request.fechaIniTransporte(), request.fechaFinTransporte());

        long secuencial = nextSecuencial(empresa, establecimiento, puntoEmision, TipoComprobante.GUIA_REMISION);
        ComprobanteNumber number = new ComprobanteNumber(establecimiento.getCodigo(), puntoEmision.getCodigo(), secuencial);
        String codigoNumerico = String.format("%08d", ThreadLocalRandom.current().nextInt(100_000_000));
        String claveAcceso = accessKeyGenerator.generate(
                fechaEmision,
                TipoComprobante.GUIA_REMISION,
                empresa.getRuc(),
                empresa.getAmbiente(),
                number,
                codigoNumerico,
                TipoEmision.NORMAL);

        ComprobanteEntity comprobante = new ComprobanteEntity();
        comprobante.setEmpresa(empresa);
        comprobante.setCliente(cliente);
        comprobante.setEstablecimiento(establecimiento);
        comprobante.setPuntoEmision(puntoEmision);
        comprobante.setTipoComprobante(TipoComprobante.GUIA_REMISION);
        comprobante.setSecuencial(secuencial);
        comprobante.setNumeroCompleto(number.formatted());
        comprobante.setFechaEmision(fechaEmision);
        comprobante.setAmbiente(empresa.getAmbiente());
        comprobante.setTipoEmision(TipoEmision.NORMAL);
        comprobante.setClaveAcceso(claveAcceso);
        comprobante.setEstadoInterno(estado);
        comprobante.setEstadoSri(EstadoSri.PENDIENTE);
        comprobante.setFormaPago(FormaPago.SIN_UTILIZACION_SISTEMA_FINANCIERO);
        comprobante.setSubtotal0(BigDecimal.ZERO);
        comprobante.setSubtotalIva(BigDecimal.ZERO);
        comprobante.setDescuentoTotal(BigDecimal.ZERO);
        comprobante.setIvaTotal(BigDecimal.ZERO);
        comprobante.setIceTotal(BigDecimal.ZERO);
        comprobante.setTotal(BigDecimal.ZERO);
        comprobante.setGuiaDirPartida(request.dirPartida());
        comprobante.setGuiaRazonSocialTransportista(request.razonSocialTransportista());
        comprobante.setGuiaTipoIdentificacionTransportista(request.tipoIdentificacionTransportista());
        comprobante.setGuiaIdentificacionTransportista(request.identificacionTransportista());
        comprobante.setGuiaRise(request.rise());
        comprobante.setGuiaFechaIniTransporte(request.fechaIniTransporte());
        comprobante.setGuiaFechaFinTransporte(request.fechaFinTransporte());
        comprobante.setGuiaPlaca(request.placa());
        comprobante.setGuiaDestinatarioDireccion(defaultText(request.destinatarioDireccion(), cliente.getDireccion()));
        comprobante.setGuiaMotivoTraslado(request.motivoTraslado());
        comprobante.setGuiaDocAduaneroUnico(request.docAduaneroUnico());
        comprobante.setGuiaCodEstabDestino(request.codEstabDestino());
        comprobante.setGuiaRuta(request.ruta());
        comprobante.setGuiaCodDocSustento(request.codDocSustento());
        comprobante.setGuiaNumDocSustento(request.numDocSustento());
        comprobante.setGuiaNumAutDocSustento(request.numAutDocSustento());
        comprobante.setGuiaFechaEmisionDocSustento(request.fechaEmisionDocSustento());
        setAuthenticatedCreator(comprobante);

        request.detalles().stream()
                .map(detalle -> toDetalleGuia(detalle, request.empresaId()))
                .forEach(comprobante::addDetalle);
        return comprobante;
    }

    private long nextSecuencial(EmpresaEntity empresa, EstablecimientoEntity establecimiento, PuntoEmisionEntity puntoEmision, TipoComprobante tipoComprobante) {
        SecuencialEntity secuencial = secuencialRepository
                .findByEmpresaIdAndEstablecimientoIdAndPuntoEmisionIdAndTipoComprobante(
                        empresa.getId(),
                        establecimiento.getId(),
                        puntoEmision.getId(),
                        tipoComprobante)
                .orElseGet(() -> {
                    SecuencialEntity entity = new SecuencialEntity();
                    entity.setEmpresa(empresa);
                    entity.setEstablecimiento(establecimiento);
                    entity.setPuntoEmision(puntoEmision);
                    entity.setTipoComprobante(tipoComprobante);
                    entity.setUltimoSecuencial(0);
                    return entity;
                });
        long next = secuencial.nextValue();
        secuencialRepository.save(secuencial);
        return next;
    }

    private ComprobanteDetalleEntity toDetalle(FacturaDetalleRequest request, UUID empresaId) {
        ProductoEntity producto = request.productoId() == null ? null : productoRepository.findById(request.productoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        if (producto != null && !producto.getEmpresa().getId().equals(empresaId)) {
            throw new BusinessException("El producto no pertenece a la empresa emisora");
        }
        String codigo = producto != null ? producto.getCodigoPrincipal() : request.codigoPrincipal();
        String descripcion = producto != null ? producto.getNombre() : request.descripcion();
        BigDecimal precio = request.precioUnitario() != null
                ? request.precioUnitario()
                : producto != null ? producto.getPrecioUnitario() : null;
        var tarifa = request.tarifaIva() != null
                ? request.tarifaIva()
                : producto != null ? producto.getTarifaIva() : null;
        if (codigo == null || codigo.isBlank() || descripcion == null || descripcion.isBlank() || precio == null || tarifa == null) {
            throw new BusinessException("Cada detalle requiere producto o codigo, descripcion, precio y tarifa IVA");
        }

        BigDecimal cantidad = request.cantidad().setScale(4, RoundingMode.HALF_UP);
        BigDecimal descuento = request.descuento() == null ? BigDecimal.ZERO : request.descuento().setScale(2, RoundingMode.HALF_UP);
        BigDecimal subtotal = cantidad.multiply(precio).subtract(descuento).setScale(2, RoundingMode.HALF_UP);
        if (subtotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("El descuento no puede superar el subtotal del detalle");
        }
        BigDecimal iva = subtotal.multiply(tarifa.percentage()).divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP);

        ComprobanteDetalleEntity detalle = new ComprobanteDetalleEntity();
        detalle.setProducto(producto);
        detalle.setCodigoPrincipal(codigo);
        detalle.setCodigoAuxiliar(producto != null ? producto.getCodigoAuxiliar() : null);
        detalle.setDescripcion(descripcion);
        detalle.setCantidad(cantidad);
        detalle.setPrecioUnitario(precio);
        detalle.setDescuento(descuento);
        detalle.setTarifaIva(tarifa);
        detalle.setSubtotal(subtotal);
        detalle.setIva(iva);
        detalle.setTotal(subtotal.add(iva).setScale(2, RoundingMode.HALF_UP));
        return detalle;
    }

    private ComprobanteDetalleEntity toDetalleGuia(GuiaRemisionDetalleRequest request, UUID empresaId) {
        ProductoEntity producto = request.productoId() == null ? null : productoRepository.findById(request.productoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        if (producto != null && !producto.getEmpresa().getId().equals(empresaId)) {
            throw new BusinessException("El producto no pertenece a la empresa emisora");
        }
        String codigo = producto != null ? producto.getCodigoPrincipal() : request.codigoInterno();
        String descripcion = producto != null ? producto.getNombre() : request.descripcion();
        if (codigo == null || codigo.isBlank() || descripcion == null || descripcion.isBlank()) {
            throw new BusinessException("Cada detalle de guia requiere producto o codigo interno y descripcion");
        }

        ComprobanteDetalleEntity detalle = new ComprobanteDetalleEntity();
        detalle.setProducto(producto);
        detalle.setCodigoPrincipal(codigo);
        detalle.setCodigoAuxiliar(producto != null ? producto.getCodigoAuxiliar() : request.codigoAdicional());
        detalle.setDescripcion(descripcion);
        detalle.setCantidad(request.cantidad().setScale(6, RoundingMode.HALF_UP));
        detalle.setPrecioUnitario(BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP));
        detalle.setDescuento(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        detalle.setTarifaIva(TarifaIva.IVA_0);
        detalle.setSubtotal(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        detalle.setIva(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        detalle.setTotal(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        return detalle;
    }

    private void validateTransportDates(LocalDate fechaEmision, LocalDate fechaIniTransporte, LocalDate fechaFinTransporte) {
        if (fechaIniTransporte == null || fechaFinTransporte == null) {
            throw new BusinessException("La guia requiere fecha de inicio y fin de transporte");
        }
        if (fechaIniTransporte.isBefore(fechaEmision)) {
            throw new BusinessException("La fecha de inicio de transporte no puede ser menor a la fecha de emision");
        }
        if (fechaFinTransporte.isBefore(fechaIniTransporte)) {
            throw new BusinessException("La fecha fin de transporte no puede ser menor a la fecha de inicio");
        }
    }

    private void applyAuthorization(ComprobanteEntity comprobante, SriAuthorizationResult authorization) {
        comprobante.setEstadoSri(authorization.estado());
        comprobante.setNumeroAutorizacion(authorization.numeroAutorizacion());
        comprobante.setFechaAutorizacion(authorization.fechaAutorizacion());
        if (authorization.xmlAutorizado() != null && !authorization.xmlAutorizado().isBlank()) {
            comprobante.setXmlFirmado(authorization.xmlAutorizado());
        }
        if (authorization.estado() == EstadoSri.AUTORIZADO) {
            comprobante.setEstadoInterno(EstadoComprobante.AUTORIZADO);
        } else if (authorization.estado() == EstadoSri.NO_AUTORIZADO) {
            comprobante.setEstadoInterno(EstadoComprobante.RECHAZADO);
        }
        comprobante.setMensajesSri(joinMessages(authorization.mensajes()));
    }

    private void saveMessages(ComprobanteEntity comprobante, List<SriResponseMessage> messages, EstadoSri estadoSri) {
        if (messages == null) {
            return;
        }
        for (SriResponseMessage message : messages) {
            SriMensajeEntity entity = new SriMensajeEntity();
            entity.setComprobante(comprobante);
            entity.setIdentificador(message.identificador());
            entity.setMensaje(limit(message.mensaje() == null ? estadoSri.name() : message.mensaje(), 1000));
            entity.setInformacionAdicional(limit(message.informacionAdicional(), 1500));
            entity.setTipo(message.tipo());
            entity.setEstadoSri(estadoSri);
            sriMensajeRepository.save(entity);
        }
        comprobante.setMensajesSri(joinMessages(messages));
    }

    private String joinMessages(List<SriResponseMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            return null;
        }
        return String.join(" | ", messages.stream()
                .map(message -> (message.identificador() == null ? "" : message.identificador() + ": ") + message.mensaje())
                .toList());
    }

    private String limit(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

    private String defaultText(String value, String fallback) {
        if (value != null && !value.isBlank()) {
            return value;
        }
        if (fallback != null && !fallback.isBlank()) {
            return fallback;
        }
        return "NA";
    }

    private FirmaConfig resolveFirma(UUID empresaId) {
        FirmaElectronicaEntity firma = firmaRepository
                .findFirstByEmpresaIdAndEstadoOrderByFechaVencimientoDesc(empresaId, EstadoFirma.ACTIVA)
                .orElse(null);
        if (firma == null) {
            if (properties.signature().mockEnabled()) {
                return new FirmaConfig(null, null, null, true);
            }
            throw new BusinessException("La empresa no tiene firma electronica activa");
        }
        if (firma.getFechaVencimiento() != null && firma.getFechaVencimiento().isBefore(LocalDate.now())) {
            throw new BusinessException("Firma vencida");
        }
        String password = firma.getPasswordSecretRef() == null ? null : System.getenv(firma.getPasswordSecretRef());
        if (!properties.signature().mockEnabled() && (password == null || password.isBlank())) {
            throw new BusinessException("Password de firma no configurado en variable segura");
        }
        return new FirmaConfig(
                Path.of(firma.getRutaSegura()),
                password == null ? null : password.toCharArray(),
                firma.getFechaVencimiento(),
                properties.signature().mockEnabled());
    }

    private ComprobanteEntity findEntity(UUID id) {
        return comprobanteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comprobante no encontrado"));
    }

    private void setAuthenticatedCreator(ComprobanteEntity comprobante) {
        Object principal = SecurityContextHolder.getContext().getAuthentication() == null
                ? null
                : SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal user) {
            userRepository.findById(user.id()).ifPresent(comprobante::setUsuarioCreador);
        }
    }

    private ComprobanteResponse toResponse(ComprobanteEntity entity) {
        return new ComprobanteResponse(
                entity.getId(),
                entity.getEmpresa().getId(),
                entity.getCliente().getId(),
                entity.getTipoComprobante(),
                entity.getNumeroCompleto(),
                entity.getSecuencial(),
                entity.getFechaEmision(),
                entity.getAmbiente(),
                entity.getClaveAcceso(),
                entity.getEstadoInterno(),
                entity.getEstadoSri(),
                entity.getSubtotal0(),
                entity.getSubtotalIva(),
                entity.getDescuentoTotal(),
                entity.getIvaTotal(),
                entity.getIceTotal(),
                entity.getTotal(),
                entity.getFormaPago(),
                entity.getPlazo(),
                entity.getTiempo(),
                entity.getNumeroAutorizacion(),
                entity.getFechaAutorizacion(),
                entity.getMensajesSri(),
                entity.getId() == null
                        ? List.of()
                        : sriMensajeRepository.findByComprobanteId(entity.getId()).stream().map(this::toSriMensajeResponse).toList(),
                entity.getDetalles().stream().map(this::toDetalleResponse).toList(),
                entity.getGuiaDirPartida(),
                entity.getGuiaRazonSocialTransportista(),
                entity.getGuiaTipoIdentificacionTransportista(),
                entity.getGuiaIdentificacionTransportista(),
                entity.getGuiaRise(),
                entity.getGuiaFechaIniTransporte(),
                entity.getGuiaFechaFinTransporte(),
                entity.getGuiaPlaca(),
                entity.getGuiaDestinatarioDireccion(),
                entity.getGuiaMotivoTraslado(),
                entity.getGuiaDocAduaneroUnico(),
                entity.getGuiaCodEstabDestino(),
                entity.getGuiaRuta(),
                entity.getGuiaCodDocSustento(),
                entity.getGuiaNumDocSustento(),
                entity.getGuiaNumAutDocSustento(),
                entity.getGuiaFechaEmisionDocSustento());
    }

    private SriMensajeResponse toSriMensajeResponse(SriMensajeEntity mensaje) {
        return new SriMensajeResponse(
                mensaje.getIdentificador(),
                mensaje.getMensaje(),
                mensaje.getInformacionAdicional(),
                mensaje.getTipo());
    }

    private FacturaDetalleResponse toDetalleResponse(ComprobanteDetalleEntity detalle) {
        return new FacturaDetalleResponse(
                detalle.getId(),
                detalle.getProducto() == null ? null : detalle.getProducto().getId(),
                detalle.getCodigoPrincipal(),
                detalle.getCodigoAuxiliar(),
                detalle.getDescripcion(),
                detalle.getCantidad(),
                detalle.getPrecioUnitario(),
                detalle.getDescuento(),
                detalle.getTarifaIva(),
                detalle.getSubtotal(),
                detalle.getIva(),
                detalle.getTotal());
    }
}
