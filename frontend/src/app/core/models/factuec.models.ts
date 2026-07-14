export type AmbienteSri = 'PRUEBAS' | 'PRODUCCION';

export type EstadoComprobante =
  | 'BORRADOR'
  | 'GENERADO'
  | 'FIRMADO'
  | 'ENVIADO'
  | 'AUTORIZADO'
  | 'RECHAZADO'
  | 'DEVUELTO'
  | 'ANULADO'
  | 'ERROR';

export type TipoComprobante = 'FACTURA' | 'NOTA_CREDITO' | 'RETENCION';

export type TipoIdentificacion = 'RUC' | 'CEDULA' | 'PASAPORTE' | 'CONSUMIDOR_FINAL';

export type TipoProducto = 'PRODUCTO' | 'SERVICIO';

export type EntityId = string | number;

export interface Role {
  id: EntityId;
  name: string;
  description: string;
  permissions: string[];
}

export interface User {
  id: EntityId;
  name: string;
  email: string;
  username: string;
  avatarUrl?: string;
  empresaId?: EntityId;
  role: Role;
  status: 'ACTIVO' | 'INACTIVO' | 'BLOQUEADO';
}

export interface Empresa {
  id: EntityId;
  ruc: string;
  razonSocial: string;
  nombreComercial: string;
  direccionMatriz: string;
  obligadoContabilidad: boolean;
  ambiente: AmbienteSri;
  tipoEmision: 'NORMAL' | 'INDISPONIBILIDAD';
}

export interface Establecimiento {
  id: EntityId;
  codigo: string;
  nombre: string;
  direccion: string;
  activo: boolean;
  puntosEmision: PuntoEmision[];
}

export interface PuntoEmision {
  id: EntityId;
  codigo: string;
  nombre: string;
  secuencialFactura: number;
  activo: boolean;
}

export interface Cliente {
  id: EntityId;
  tipoIdentificacion: TipoIdentificacion;
  identificacion: string;
  razonSocial: string;
  email: string;
  telefono: string;
  direccion: string;
  estado: 'ACTIVO' | 'INACTIVO';
  totalVentasMes: number;
}

export interface Producto {
  id: EntityId;
  codigo: string;
  nombre: string;
  categoria: string;
  tipo: TipoProducto;
  precioUnitario: number;
  tarifaIva: '15%' | '12%' | '0%' | 'EXENTO' | 'NO_OBJETO';
  stock?: number;
  estado: 'ACTIVO' | 'INACTIVO';
}

export interface ComprobanteDetalle {
  id: EntityId;
  codigo: string;
  descripcion: string;
  cantidad: number;
  precioUnitario: number;
  descuento: number;
  tarifaIva: '15%' | '12%' | '0%' | 'EXENTO' | 'NO_OBJETO';
  subtotal: number;
}

export interface ComprobantePago {
  id: EntityId;
  formaPago: string;
  valor: number;
  plazo: number;
  unidadTiempo: 'DIAS' | 'MESES';
}

export interface SriMensaje {
  id: EntityId;
  fecha: string;
  comprobanteNumero: string;
  codigo: string;
  mensaje: string;
  informacionAdicional: string;
  estado: EstadoComprobante;
}

export interface Comprobante {
  id: EntityId;
  tipo: TipoComprobante;
  numero: string;
  fechaEmision: string;
  cliente: Cliente;
  identificacion: string;
  subtotal: number;
  iva: number;
  total: number;
  estado: EstadoComprobante;
  claveAcceso?: string;
  numeroAutorizacion?: string;
  fechaAutorizacion?: string;
  ambiente: AmbienteSri;
  detalles: ComprobanteDetalle[];
  pagos: ComprobantePago[];
  mensajesSri: SriMensaje[];
}

export interface FirmaElectronica {
  id: EntityId;
  empresaId: EntityId;
  archivoNombre: string;
  titular: string;
  emisor: string;
  fechaVencimiento: string;
  estado: 'VALIDO' | 'POR_VENCER' | 'EXPIRADO' | 'INVALIDO';
  diasRestantes: number;
}

export interface AuditLog {
  id: EntityId;
  fecha: string;
  usuario: string;
  accion: string;
  entidad: string;
  descripcion: string;
  ip: string;
}

export interface DashboardMetric {
  title: string;
  value: string;
  icon: string;
  trend?: string;
  tone?: 'primary' | 'success' | 'warning' | 'danger' | 'neutral';
}
