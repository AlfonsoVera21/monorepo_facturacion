import { AmbienteSri, EstadoComprobante, TipoIdentificacion, TipoProducto, UnidadMedidaInventario } from '../models/factuec.models';

export interface AuthResponseDto {
  userId: string;
  username: string;
  roles: string[];
  accessToken: string;
  refreshToken: string;
  accessTokenExpiresAt: string;
  refreshTokenExpiresAt: string;
}

export interface UserMeResponseDto {
  id: string;
  username: string;
  email: string;
  fullName: string;
  roles: string[];
  permissions: string[];
}

export interface RoleResponseDto {
  id: string;
  name: string;
  description: string;
  permissions: string[];
}

export interface UserResponseDto {
  id: string;
  username: string;
  email: string;
  fullName: string;
  active: boolean;
  roles: RoleResponseDto[];
  createdAt: string;
  updatedAt: string;
}

export interface EmpresaResponseDto {
  id: string;
  ruc: string;
  razonSocial: string;
  nombreComercial?: string;
  direccionMatriz: string;
  obligadoContabilidad: boolean;
  ambiente: AmbienteSri;
  activo: boolean;
}

export interface EstablecimientoResponseDto {
  id: string;
  empresaId: string;
  codigo: string;
  nombre: string;
  direccion: string;
  activo: boolean;
}

export interface PuntoEmisionResponseDto {
  id: string;
  establecimientoId: string;
  codigo: string;
  nombre: string;
  activo: boolean;
}

export interface ClienteResponseDto {
  id: string;
  empresaId: string;
  tipoIdentificacion: TipoIdentificacion;
  identificacion: string;
  razonSocial: string;
  nombreComercial?: string;
  correo?: string;
  telefono?: string;
  direccion?: string;
  ciudad?: string;
  provincia?: string;
  activo: boolean;
}

export interface ProductoResponseDto {
  id: string;
  empresaId: string;
  codigoPrincipal: string;
  codigoAuxiliar?: string;
  nombre: string;
  descripcion?: string;
  tipo: TipoProducto;
  precioUnitario: number;
  tarifaIva: 'IVA_0' | 'IVA_5' | 'IVA_12' | 'IVA_15' | 'NO_OBJETO_IVA' | 'EXENTO_IVA';
  stock?: number;
  unidadMedida?: UnidadMedidaInventario;
  stockMinimo?: number;
  pesoPromedioKg?: number;
  palletizable?: boolean;
  unidadesPorPallet?: number;
  requiereRefrigeracion?: boolean;
  categoria?: string;
  activo: boolean;
}

export interface ChoferResponseDto {
  id: string;
  empresaId: string;
  tipoIdentificacion: TipoIdentificacion;
  identificacion: string;
  nombres: string;
  apellidos?: string;
  licencia: string;
  telefono?: string;
  correo?: string;
  placaVehiculo?: string;
  tipoVehiculo?: string;
  capacidad?: number;
  unidadCapacidad?: UnidadMedidaInventario;
  transportaRefrigerado: boolean;
  activo: boolean;
}

export interface FacturaDetalleResponseDto {
  id: string;
  productoId?: string;
  codigoPrincipal: string;
  codigoAuxiliar?: string;
  descripcion: string;
  cantidad: number;
  precioUnitario: number;
  descuento: number;
  tarifaIva: ProductoResponseDto['tarifaIva'];
  subtotal: number;
  iva: number;
  total: number;
}

export interface SriMensajeResponseDto {
  identificador?: string;
  mensaje: string;
  informacionAdicional?: string;
  tipo?: string;
}

export interface ComprobanteResponseDto {
  id: string;
  empresaId: string;
  clienteId: string;
  tipoComprobante: 'FACTURA' | 'NOTA_CREDITO' | 'RETENCION' | 'NOTA_DEBITO' | 'GUIA_REMISION' | 'LIQUIDACION_COMPRA';
  numeroCompleto: string;
  secuencial: number;
  fechaEmision: string;
  ambiente: AmbienteSri;
  claveAcceso?: string;
  estadoInterno: EstadoComprobante;
  estadoSri?: string;
  subtotal0: number;
  subtotalIva: number;
  descuentoTotal: number;
  ivaTotal: number;
  iceTotal: number;
  total: number;
  formaPago?: string;
  plazo?: number;
  tiempo?: string;
  numeroAutorizacion?: string;
  fechaAutorizacion?: string;
  mensajesSri?: string;
  mensajes?: SriMensajeResponseDto[];
  detalles: FacturaDetalleResponseDto[];
  guiaDirPartida?: string;
  guiaRazonSocialTransportista?: string;
  guiaTipoIdentificacionTransportista?: TipoIdentificacion;
  guiaIdentificacionTransportista?: string;
  guiaRise?: string;
  guiaFechaIniTransporte?: string;
  guiaFechaFinTransporte?: string;
  guiaPlaca?: string;
  guiaDestinatarioDireccion?: string;
  guiaMotivoTraslado?: string;
  guiaDocAduaneroUnico?: string;
  guiaCodEstabDestino?: string;
  guiaRuta?: string;
  guiaCodDocSustento?: string;
  guiaNumDocSustento?: string;
  guiaNumAutDocSustento?: string;
  guiaFechaEmisionDocSustento?: string;
}

export interface FirmaElectronicaResponseDto {
  id: string;
  empresaId: string;
  nombreArchivo: string;
  rutaSegura: string;
  passwordSecretRef?: string;
  fechaEmision?: string;
  fechaVencimiento?: string;
  estado: 'ACTIVA' | 'VENCIDA' | 'REVOCADA' | 'INACTIVA';
}

export interface CertificadoDisponibleResponseDto {
  nombreArchivo: string;
  rutaSegura: string;
  tamanoBytes: number;
  modificadoEn: string;
}

export interface AuditLogResponseDto {
  id: string;
  fecha: string;
  usuario: string;
  accion: string;
  entidad: string;
  descripcion: string;
  ip: string;
}

export interface SriEstadoResponseDto {
  ambienteDefault: string;
  mockEnabled: boolean;
  recepcionPruebasUrl: string;
  autorizacionPruebasUrl: string;
  recepcionProduccionUrl: string;
  autorizacionProduccionUrl: string;
}

export interface ConfiguracionGeneralResponseDto {
  serieDefault: string;
  ambiente: AmbienteSri;
  correoNotificaciones: string;
  enviarRideAutomatico: boolean;
  reintentosSri: string;
  mfaObligatorio: boolean;
  sriMockEnabled: boolean;
  firmaMockEnabled: boolean;
}

export interface ReporteVentasResponseDto {
  empresaId: string;
  desde: string;
  hasta: string;
  comprobantes: number;
  subtotal: number;
  iva: number;
  total: number;
}

export interface ReporteEstadoComprobanteResponseDto {
  estado: EstadoComprobante;
  total: number;
}
