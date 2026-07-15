import {
  Cliente,
  Chofer,
  Comprobante,
  ComprobanteDetalle,
  Empresa,
  Establecimiento,
  FirmaElectronica,
  Producto,
  PuntoEmision,
  Role,
  SriMensaje,
  User
} from '../models/factuec.models';
import {
  ClienteResponseDto,
  ChoferResponseDto,
  ComprobanteResponseDto,
  EmpresaResponseDto,
  EstablecimientoResponseDto,
  FacturaDetalleResponseDto,
  FirmaElectronicaResponseDto,
  ProductoResponseDto,
  PuntoEmisionResponseDto,
  SriMensajeResponseDto,
  UserMeResponseDto
} from './backend-api.models';

export function mapRole(roleName: string, permissions: string[] = []): Role {
  return {
    id: roleName,
    name: roleName.replace('ROLE_', '').replaceAll('_', ' '),
    description: 'Rol sincronizado desde el backend',
    permissions
  };
}

export function mapUser(dto: UserMeResponseDto): User {
  const primaryRole = dto.roles?.[0] || 'ADMIN';
  return {
    id: dto.id,
    name: dto.fullName || dto.username,
    email: dto.email,
    username: dto.username,
    role: mapRole(primaryRole, dto.permissions || []),
    status: 'ACTIVO'
  };
}

export function mapEmpresa(dto: EmpresaResponseDto): Empresa {
  return {
    id: dto.id,
    ruc: dto.ruc,
    razonSocial: dto.razonSocial,
    nombreComercial: dto.nombreComercial || dto.razonSocial,
    direccionMatriz: dto.direccionMatriz,
    obligadoContabilidad: dto.obligadoContabilidad,
    ambiente: dto.ambiente,
    tipoEmision: 'NORMAL'
  };
}

export function mapPuntoEmision(dto: PuntoEmisionResponseDto): PuntoEmision {
  return {
    id: dto.id,
    codigo: dto.codigo,
    nombre: dto.nombre,
    secuencialFactura: 0,
    activo: dto.activo
  };
}

export function mapEstablecimiento(dto: EstablecimientoResponseDto, puntosEmision: PuntoEmision[] = []): Establecimiento {
  return {
    id: dto.id,
    codigo: dto.codigo,
    nombre: dto.nombre,
    direccion: dto.direccion,
    activo: dto.activo,
    puntosEmision
  };
}

export function mapCliente(dto: ClienteResponseDto): Cliente {
  return {
    id: dto.id,
    tipoIdentificacion: dto.tipoIdentificacion,
    identificacion: dto.identificacion,
    razonSocial: dto.razonSocial,
    email: dto.correo || '',
    telefono: dto.telefono || '',
    direccion: dto.direccion || '',
    estado: dto.activo ? 'ACTIVO' : 'INACTIVO',
    totalVentasMes: 0
  };
}

export function mapProducto(dto: ProductoResponseDto): Producto {
  return {
    id: dto.id,
    codigo: dto.codigoPrincipal,
    nombre: dto.nombre,
    categoria: dto.categoria || dto.descripcion || 'Sin categoria',
    tipo: dto.tipo,
    precioUnitario: Number(dto.precioUnitario || 0),
    tarifaIva: mapTarifaIva(dto.tarifaIva),
    stock: dto.stock === undefined || dto.stock === null ? undefined : Number(dto.stock),
    unidadMedida: dto.unidadMedida || 'UNIDAD',
    stockMinimo: Number(dto.stockMinimo || 0),
    pesoPromedioKg: dto.pesoPromedioKg === undefined || dto.pesoPromedioKg === null ? undefined : Number(dto.pesoPromedioKg),
    palletizable: Boolean(dto.palletizable),
    unidadesPorPallet: dto.unidadesPorPallet === undefined || dto.unidadesPorPallet === null ? undefined : Number(dto.unidadesPorPallet),
    requiereRefrigeracion: Boolean(dto.requiereRefrigeracion),
    estado: dto.activo ? 'ACTIVO' : 'INACTIVO'
  };
}

export function mapChofer(dto: ChoferResponseDto): Chofer {
  return {
    id: dto.id,
    tipoIdentificacion: dto.tipoIdentificacion,
    identificacion: dto.identificacion,
    nombres: dto.nombres,
    apellidos: dto.apellidos || '',
    licencia: dto.licencia,
    telefono: dto.telefono || '',
    correo: dto.correo || '',
    placaVehiculo: dto.placaVehiculo || '',
    tipoVehiculo: dto.tipoVehiculo || '',
    capacidad: dto.capacidad === undefined || dto.capacidad === null ? undefined : Number(dto.capacidad),
    unidadCapacidad: dto.unidadCapacidad,
    transportaRefrigerado: dto.transportaRefrigerado,
    estado: dto.activo ? 'ACTIVO' : 'INACTIVO'
  };
}

export function mapComprobante(dto: ComprobanteResponseDto, clientes: Cliente[] = []): Comprobante {
  const cliente = clientes.find((item) => item.id === dto.clienteId) || createPlaceholderCliente(dto.clienteId);
  const detalles = (dto.detalles || []).map(mapDetalle);

  return {
    id: dto.id,
    tipo: mapTipoComprobante(dto.tipoComprobante),
    numero: dto.numeroCompleto,
    fechaEmision: dto.fechaEmision,
    cliente,
    identificacion: cliente.identificacion,
    subtotal: Number(dto.subtotal0 || 0) + Number(dto.subtotalIva || 0),
    iva: Number(dto.ivaTotal || 0),
    total: Number(dto.total || 0),
    estado: dto.estadoInterno,
    claveAcceso: dto.claveAcceso,
    numeroAutorizacion: dto.numeroAutorizacion,
    fechaAutorizacion: dto.fechaAutorizacion,
    ambiente: dto.ambiente,
    guiaDirPartida: dto.guiaDirPartida,
    guiaRazonSocialTransportista: dto.guiaRazonSocialTransportista,
    guiaTipoIdentificacionTransportista: dto.guiaTipoIdentificacionTransportista,
    guiaIdentificacionTransportista: dto.guiaIdentificacionTransportista,
    guiaRise: dto.guiaRise,
    guiaFechaIniTransporte: dto.guiaFechaIniTransporte,
    guiaFechaFinTransporte: dto.guiaFechaFinTransporte,
    guiaPlaca: dto.guiaPlaca,
    guiaDestinatarioDireccion: dto.guiaDestinatarioDireccion,
    guiaMotivoTraslado: dto.guiaMotivoTraslado,
    guiaDocAduaneroUnico: dto.guiaDocAduaneroUnico,
    guiaCodEstabDestino: dto.guiaCodEstabDestino,
    guiaRuta: dto.guiaRuta,
    guiaCodDocSustento: dto.guiaCodDocSustento,
    guiaNumDocSustento: dto.guiaNumDocSustento,
    guiaNumAutDocSustento: dto.guiaNumAutDocSustento,
    guiaFechaEmisionDocSustento: dto.guiaFechaEmisionDocSustento,
    detalles,
    pagos: [
      {
        id: `${dto.id}-pago`,
        formaPago: dto.formaPago || 'SIN_UTILIZACION_SISTEMA_FINANCIERO',
        valor: Number(dto.total || 0),
        plazo: dto.plazo || 0,
        unidadTiempo: 'DIAS'
      }
    ],
    mensajesSri: parseSriMensajes(dto)
  };
}

export function mapFirma(dto: FirmaElectronicaResponseDto): FirmaElectronica {
  return {
    id: dto.id,
    empresaId: dto.empresaId,
    archivoNombre: dto.nombreArchivo,
    titular: 'Certificado cargado',
    emisor: dto.rutaSegura,
    fechaVencimiento: dto.fechaVencimiento || '',
    estado: dto.estado === 'ACTIVA' ? 'VALIDO' : dto.estado === 'VENCIDA' ? 'EXPIRADO' : 'INVALIDO',
    diasRestantes: dto.fechaVencimiento ? daysUntil(dto.fechaVencimiento) : 0
  };
}

function mapDetalle(dto: FacturaDetalleResponseDto): ComprobanteDetalle {
  return {
    id: dto.id,
    codigo: dto.codigoPrincipal,
    descripcion: dto.descripcion,
    cantidad: Number(dto.cantidad || 0),
    precioUnitario: Number(dto.precioUnitario || 0),
    descuento: Number(dto.descuento || 0),
    tarifaIva: mapTarifaIva(dto.tarifaIva),
    subtotal: Number(dto.subtotal || 0)
  };
}

function mapTarifaIva(tarifa: ProductoResponseDto['tarifaIva']): Producto['tarifaIva'] {
  const map: Record<ProductoResponseDto['tarifaIva'], Producto['tarifaIva']> = {
    IVA_0: '0%',
    IVA_5: '0%',
    IVA_12: '12%',
    IVA_15: '15%',
    NO_OBJETO_IVA: 'NO_OBJETO',
    EXENTO_IVA: 'EXENTO'
  };
  return map[tarifa] || '15%';
}

function mapTipoComprobante(tipo: ComprobanteResponseDto['tipoComprobante']): Comprobante['tipo'] {
  return tipo;
}

function createPlaceholderCliente(clienteId: string): Cliente {
  return {
    id: clienteId,
    tipoIdentificacion: 'RUC',
    identificacion: clienteId.slice(0, 13),
    razonSocial: 'Cliente sincronizado',
    email: '',
    telefono: '',
    direccion: '',
    estado: 'ACTIVO',
    totalVentasMes: 0
  };
}

function parseSriMensajes(dto: ComprobanteResponseDto): SriMensaje[] {
  if (dto.mensajes?.length) {
    return dto.mensajes.map((message, index) => mapSriMensaje(dto, message, index));
  }

  if (!dto.mensajesSri) {
    return [];
  }

  return [
    {
      id: `${dto.id}-sri`,
      fecha: dto.fechaAutorizacion || dto.fechaEmision,
      comprobanteNumero: dto.numeroCompleto,
      codigo: dto.estadoSri || dto.estadoInterno,
      mensaje: dto.mensajesSri,
      informacionAdicional: '',
      estado: dto.estadoInterno
    }
  ];
}

function mapSriMensaje(dto: ComprobanteResponseDto, message: SriMensajeResponseDto, index: number): SriMensaje {
  const normalized = normalizeSriMessage(message);
  return {
    id: `${dto.id}-sri-${index}`,
    fecha: dto.fechaAutorizacion || dto.fechaEmision,
    comprobanteNumero: dto.numeroCompleto,
    codigo: normalized.codigo || dto.estadoSri || dto.estadoInterno,
    mensaje: normalized.mensaje,
    informacionAdicional: normalized.informacionAdicional,
    estado: dto.estadoInterno
  };
}

function normalizeSriMessage(message: SriMensajeResponseDto): { codigo?: string; mensaje: string; informacionAdicional: string } {
  const raw = message.informacionAdicional || '';
  const sriError = extractSriError(raw);
  if (sriError) {
    return sriError;
  }

  if (raw.includes('<RespuestaRecepcionComprobante')) {
    const estado = extractTag(raw, 'estado');
    return {
      codigo: estado || message.tipo || message.identificador,
      mensaje: estado === 'RECIBIDA' ? 'Comprobante recibido por SRI' : message.mensaje,
      informacionAdicional: estado ? `Estado recepcion SRI: ${estado}` : ''
    };
  }

  if (raw.includes('<RespuestaAutorizacionComprobante')) {
    const numeroComprobantes = extractTag(raw, 'numeroComprobantes');
    return {
      codigo: numeroComprobantes === '0' ? 'NO_AUTORIZADO' : message.tipo || message.identificador,
      mensaje: numeroComprobantes === '0' ? 'Autorizacion no encontrada en SRI' : message.mensaje,
      informacionAdicional: numeroComprobantes === '0'
        ? 'SRI respondio numeroComprobantes=0 para la clave consultada.'
        : ''
    };
  }

  return {
    codigo: message.identificador || message.tipo,
    mensaje: message.mensaje,
    informacionAdicional: message.informacionAdicional || ''
  };
}

function extractSriError(raw: string): { codigo?: string; mensaje: string; informacionAdicional: string } | null {
  const structured = raw.match(/<mensaje>\s*<identificador>([\s\S]*?)<\/identificador>\s*<mensaje>([\s\S]*?)<\/mensaje>\s*(?:<informacionAdicional>([\s\S]*?)<\/informacionAdicional>)?/);
  if (structured) {
    return {
      codigo: structured[1]?.trim(),
      mensaje: structured[2]?.trim() || 'Mensaje SRI',
      informacionAdicional: structured[3]?.trim() || ''
    };
  }

  const identificador = extractTag(raw, 'identificador');
  const informacionAdicional = extractTag(raw, 'informacionAdicional');
  if (!identificador && !informacionAdicional) {
    return null;
  }
  return {
    codigo: identificador,
    mensaje: 'Mensaje SRI',
    informacionAdicional: informacionAdicional || ''
  };
}

function extractTag(raw: string, tag: string): string | undefined {
  const match = raw.match(new RegExp(`<${tag}>([\\s\\S]*?)</${tag}>`));
  return match?.[1]?.trim();
}

function daysUntil(date: string): number {
  const now = new Date();
  const target = new Date(`${date}T00:00:00`);
  return Math.max(0, Math.ceil((target.getTime() - now.getTime()) / 86_400_000));
}
