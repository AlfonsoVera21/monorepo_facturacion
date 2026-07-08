import {
  AuditLog,
  Cliente,
  Comprobante,
  DashboardMetric,
  Empresa,
  Establecimiento,
  FirmaElectronica,
  Producto,
  Role,
  SriMensaje,
  User
} from '../models/factuec.models';

export const ROLES: Role[] = [
  {
    id: 1,
    name: 'Administrador',
    description: 'Acceso completo a configuracion fiscal, usuarios y emision.',
    permissions: ['DASHBOARD', 'FACTURAS', 'CLIENTES', 'PRODUCTOS', 'SRI', 'REPORTES', 'USUARIOS', 'CONFIGURACION']
  },
  {
    id: 2,
    name: 'Contador',
    description: 'Gestion operativa de comprobantes, clientes, productos y reportes.',
    permissions: ['DASHBOARD', 'FACTURAS', 'CLIENTES', 'PRODUCTOS', 'SRI', 'REPORTES']
  },
  {
    id: 3,
    name: 'Soporte',
    description: 'Revision de errores SRI y trazabilidad tecnica.',
    permissions: ['DASHBOARD', 'SRI', 'COMPROBANTES_LECTURA']
  }
];

export const CURRENT_USER: User = {
  id: 1,
  name: 'Juan Delgado',
  email: 'admin@factuec.local',
  username: 'admin',
  empresaId: 1,
  role: ROLES[0],
  status: 'ACTIVO'
};

export const USERS: User[] = [
  CURRENT_USER,
  {
    id: 2,
    name: 'Maria Fernanda Ruiz',
    email: 'contabilidad@soltec.ec',
    username: 'mruiz',
    empresaId: 1,
    role: ROLES[1],
    status: 'ACTIVO'
  },
  {
    id: 3,
    name: 'Diego Paredes',
    email: 'soporte@factuec.local',
    username: 'dparedes',
    empresaId: 1,
    role: ROLES[2],
    status: 'ACTIVO'
  },
  {
    id: 4,
    name: 'Andrea Castillo',
    email: 'auditoria@soltec.ec',
    username: 'acastillo',
    empresaId: 1,
    role: ROLES[1],
    status: 'INACTIVO'
  }
];

export const EMPRESA: Empresa = {
  id: 1,
  ruc: '1790000000001',
  razonSocial: 'SOLUCIONES TECNOLOGICAS S.A.',
  nombreComercial: 'SOLTEC',
  direccionMatriz: 'Av. Amazonas y Naciones Unidas, Quito',
  obligadoContabilidad: true,
  ambiente: 'PRODUCCION',
  tipoEmision: 'NORMAL'
};

export const ESTABLECIMIENTOS: Establecimiento[] = [
  {
    id: 1,
    codigo: '001',
    nombre: 'MATRIZ QUITO',
    direccion: 'Av. Amazonas y Naciones Unidas',
    activo: true,
    puntosEmision: [
      { id: 1, codigo: '001', nombre: 'CAJA 001', secuencialFactura: 452, activo: true },
      { id: 2, codigo: '002', nombre: 'CAJA 002', secuencialFactura: 128, activo: true }
    ]
  },
  {
    id: 2,
    codigo: '002',
    nombre: 'SUCURSAL GUAYAQUIL',
    direccion: 'Malecon 2000 y 9 de Octubre',
    activo: true,
    puntosEmision: [{ id: 3, codigo: '001', nombre: 'CAJA 001', secuencialFactura: 89, activo: true }]
  }
];

export const CLIENTES: Cliente[] = [
  {
    id: 1,
    tipoIdentificacion: 'RUC',
    identificacion: '1792456789001',
    razonSocial: 'CORPORACION MULTIMEDIA C.A.',
    email: 'info@multimedia.com.ec',
    telefono: '022 456 789',
    direccion: 'Av. Republica y Eloy Alfaro, Quito',
    estado: 'ACTIVO',
    totalVentasMes: 12390
  },
  {
    id: 2,
    tipoIdentificacion: 'CEDULA',
    identificacion: '1712345678',
    razonSocial: 'ANDRADE MOREIRA RAUL VINICIO',
    email: 'raul.andrade@gmail.com',
    telefono: '099 876 5432',
    direccion: 'La Carolina, Quito',
    estado: 'ACTIVO',
    totalVentasMes: 890
  },
  {
    id: 3,
    tipoIdentificacion: 'RUC',
    identificacion: '0912345678001',
    razonSocial: 'SERVICIOS LOGISTICOS GUAYAS S.A.',
    email: 'ventas@logisguayas.net',
    telefono: '042 111 222',
    direccion: 'Via Daule km 8.5, Guayaquil',
    estado: 'INACTIVO',
    totalVentasMes: 0
  },
  {
    id: 4,
    tipoIdentificacion: 'PASAPORTE',
    identificacion: 'E987654321',
    razonSocial: 'JOHNSON & BROTHERS LLC',
    email: 'contracts@johnson.us',
    telefono: '+1 415 555 0101',
    direccion: 'Market Street, San Francisco',
    estado: 'ACTIVO',
    totalVentasMes: 4200
  },
  {
    id: 5,
    tipoIdentificacion: 'RUC',
    identificacion: '1790016919001',
    razonSocial: 'CORPORACION FAVORITA C.A.',
    email: 'info@favorita.com.ec',
    telefono: '023 999 700',
    direccion: 'Sangolqui, Ecuador',
    estado: 'ACTIVO',
    totalVentasMes: 24850
  }
];

export const PRODUCTOS: Producto[] = [
  {
    id: 1,
    codigo: 'PROD-001',
    nombre: 'Laptop Dell Latitude 5420',
    categoria: 'Electronicos / Computacion',
    tipo: 'PRODUCTO',
    precioUnitario: 850,
    tarifaIva: '15%',
    stock: 18,
    estado: 'ACTIVO'
  },
  {
    id: 2,
    codigo: 'SERV-042',
    nombre: 'Consultoria Contable Anual',
    categoria: 'Servicios / Profesionales',
    tipo: 'SERVICIO',
    precioUnitario: 1200,
    tarifaIva: 'NO_OBJETO',
    estado: 'ACTIVO'
  },
  {
    id: 3,
    codigo: 'PROD-982',
    nombre: 'Resmas Papel A4 (Caja 10u)',
    categoria: 'Suministros / Oficina',
    tipo: 'PRODUCTO',
    precioUnitario: 45.5,
    tarifaIva: '0%',
    stock: 6,
    estado: 'ACTIVO'
  },
  {
    id: 4,
    codigo: 'PROD-551',
    nombre: 'Monitor LG 27" 4K UHD',
    categoria: 'Electronicos / Display',
    tipo: 'PRODUCTO',
    precioUnitario: 349.99,
    tarifaIva: '15%',
    stock: 0,
    estado: 'INACTIVO'
  },
  {
    id: 5,
    codigo: 'SERV-109',
    nombre: 'Mantenimiento de Servidores Cloud',
    categoria: 'IT / Infraestructura',
    tipo: 'SERVICIO',
    precioUnitario: 85,
    tarifaIva: '15%',
    estado: 'ACTIVO'
  }
];

export const SRI_MENSAJES: SriMensaje[] = [
  {
    id: 1,
    fecha: '2026-07-08 14:22:10',
    comprobanteNumero: '001-001-000000452',
    codigo: '43',
    mensaje: 'Clave de acceso duplicada',
    informacionAdicional: 'El comprobante ya fue procesado previamente.',
    estado: 'RECHAZADO'
  },
  {
    id: 2,
    fecha: '2026-07-08 14:15:05',
    comprobanteNumero: '001-002-000008912',
    codigo: '70',
    mensaje: 'Error en firma electronica',
    informacionAdicional: 'Certificado digital no valido o expirado.',
    estado: 'RECHAZADO'
  },
  {
    id: 3,
    fecha: '2026-07-08 13:58:44',
    comprobanteNumero: '005-001-000000122',
    codigo: '35',
    mensaje: 'RUC del receptor no existe',
    informacionAdicional: 'Verifique los datos del cliente.',
    estado: 'DEVUELTO'
  },
  {
    id: 4,
    fecha: '2026-07-08 12:30:12',
    comprobanteNumero: '001-001-000000451',
    codigo: '52',
    mensaje: 'Servicio SRI fuera de linea',
    informacionAdicional: 'Intento fallido de conexion por timeout.',
    estado: 'ERROR'
  }
];

export const COMPROBANTES: Comprobante[] = [
  {
    id: 452,
    tipo: 'FACTURA',
    numero: '001-001-000000452',
    fechaEmision: '2026-07-08',
    cliente: CLIENTES[1],
    identificacion: CLIENTES[1].identificacion,
    subtotal: 215,
    iva: 32.25,
    total: 247.25,
    estado: 'AUTORIZADO',
    claveAcceso: '0807202601179000000000120010010000004521234567812',
    numeroAutorizacion: '0807202601179000000000120',
    fechaAutorizacion: '2026-07-08 14:35:12',
    ambiente: 'PRODUCCION',
    detalles: [
      { id: 1, codigo: 'SERV-001', descripcion: 'Suscripcion Mensual FactuEC Pro', cantidad: 1, precioUnitario: 45, descuento: 0, tarifaIva: '15%', subtotal: 45 },
      { id: 2, codigo: 'MOD-API', descripcion: 'Modulo de Integracion API REST', cantidad: 1, precioUnitario: 120, descuento: 0, tarifaIva: '15%', subtotal: 120 },
      { id: 3, codigo: 'SUPPORT-H', descripcion: 'Horas Soporte Tecnico Premium', cantidad: 2, precioUnitario: 25, descuento: 0, tarifaIva: '15%', subtotal: 50 }
    ],
    pagos: [{ id: 1, formaPago: 'TARJETA DE CREDITO', valor: 247.25, plazo: 0, unidadTiempo: 'DIAS' }],
    mensajesSri: []
  },
  {
    id: 45,
    tipo: 'FACTURA',
    numero: '001-001-000000045',
    fechaEmision: '2026-07-08',
    cliente: CLIENTES[0],
    identificacion: CLIENTES[0].identificacion,
    subtotal: 1086.96,
    iva: 163.04,
    total: 1250,
    estado: 'AUTORIZADO',
    ambiente: 'PRODUCCION',
    detalles: [],
    pagos: [],
    mensajesSri: []
  },
  {
    id: 12,
    tipo: 'RETENCION',
    numero: '001-002-000000012',
    fechaEmision: '2026-07-08',
    cliente: CLIENTES[4],
    identificacion: CLIENTES[4].identificacion,
    subtotal: 45.22,
    iva: 0,
    total: 45.22,
    estado: 'RECHAZADO',
    ambiente: 'PRODUCCION',
    detalles: [],
    pagos: [],
    mensajesSri: [SRI_MENSAJES[1]]
  },
  {
    id: 44,
    tipo: 'FACTURA',
    numero: '001-001-000000044',
    fechaEmision: '2026-07-07',
    cliente: CLIENTES[1],
    identificacion: CLIENTES[1].identificacion,
    subtotal: 271.7,
    iva: 40.75,
    total: 312.45,
    estado: 'ENVIADO',
    ambiente: 'PRODUCCION',
    detalles: [],
    pagos: [],
    mensajesSri: []
  },
  {
    id: 43,
    tipo: 'FACTURA',
    numero: '001-001-000000043',
    fechaEmision: '2026-07-07',
    cliente: CLIENTES[2],
    identificacion: CLIENTES[2].identificacion,
    subtotal: 2131.13,
    iva: 319.67,
    total: 2450.8,
    estado: 'AUTORIZADO',
    ambiente: 'PRODUCCION',
    detalles: [],
    pagos: [],
    mensajesSri: []
  }
];

export const FIRMA: FirmaElectronica = {
  id: 1,
  empresaId: 1,
  archivoNombre: 'firma_v2026.p12',
  titular: 'JUAN PEREZ SOLORZANO',
  emisor: 'SECURITY DATA S.A.',
  fechaVencimiento: '2027-09-15',
  estado: 'VALIDO',
  diasRestantes: 434
};

export const AUDIT_LOGS: AuditLog[] = [
  {
    id: 1,
    fecha: '2026-07-08 09:15',
    usuario: 'admin',
    accion: 'ACTUALIZO_FIRMA',
    entidad: 'FirmaElectronica',
    descripcion: 'Se actualizo la firma electronica de la empresa.',
    ip: '10.0.0.15'
  },
  {
    id: 2,
    fecha: '2026-07-07 18:22',
    usuario: 'mruiz',
    accion: 'EMITIO_FACTURA',
    entidad: 'Comprobante',
    descripcion: 'Factura 001-001-000000452 autorizada por SRI.',
    ip: '10.0.0.22'
  }
];

export const DASHBOARD_METRICS: DashboardMetric[] = [
  { title: 'Total Facturado Mes', value: '$45,230.50', icon: 'payments', trend: '+12%', tone: 'primary' },
  { title: 'Autorizados', value: '1,248', icon: 'verified', tone: 'success' },
  { title: 'Pendientes', value: '14', icon: 'pending_actions', tone: 'warning' },
  { title: 'Rechazados', value: '3', icon: 'dangerous', tone: 'danger' }
];
