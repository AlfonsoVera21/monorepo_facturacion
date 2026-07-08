create table permissions (
    id uuid primary key,
    name varchar(120) not null unique,
    description varchar(250),
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create table roles (
    id uuid primary key,
    name varchar(40) not null unique,
    description varchar(250),
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create table role_permissions (
    role_id uuid not null references roles(id) on delete cascade,
    permission_id uuid not null references permissions(id) on delete cascade,
    primary key (role_id, permission_id)
);

create table users (
    id uuid primary key,
    username varchar(80) not null unique,
    email varchar(160) not null unique,
    password_hash varchar(255) not null,
    full_name varchar(160) not null,
    active boolean not null default true,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create table user_roles (
    user_id uuid not null references users(id) on delete cascade,
    role_id uuid not null references roles(id) on delete cascade,
    primary key (user_id, role_id)
);

create table empresas (
    id uuid primary key,
    ruc varchar(13) not null unique,
    razon_social varchar(300) not null,
    nombre_comercial varchar(300),
    direccion_matriz varchar(500) not null,
    obligado_contabilidad boolean not null,
    contribuyente_especial varchar(20),
    regimen varchar(120),
    ambiente varchar(20) not null,
    logo_path varchar(500),
    activo boolean not null default true,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create table establecimientos (
    id uuid primary key,
    empresa_id uuid not null references empresas(id),
    codigo varchar(3) not null,
    nombre varchar(200) not null,
    direccion varchar(500) not null,
    activo boolean not null default true,
    created_at timestamptz not null,
    updated_at timestamptz not null,
    constraint uk_establecimiento_empresa_codigo unique (empresa_id, codigo)
);

create table puntos_emision (
    id uuid primary key,
    establecimiento_id uuid not null references establecimientos(id),
    codigo varchar(3) not null,
    nombre varchar(200) not null,
    activo boolean not null default true,
    created_at timestamptz not null,
    updated_at timestamptz not null,
    constraint uk_punto_establecimiento_codigo unique (establecimiento_id, codigo)
);

create table secuenciales (
    id uuid primary key,
    empresa_id uuid not null references empresas(id),
    establecimiento_id uuid not null references establecimientos(id),
    punto_emision_id uuid not null references puntos_emision(id),
    tipo_comprobante varchar(40) not null,
    ultimo_secuencial bigint not null default 0,
    version bigint not null default 0,
    created_at timestamptz not null,
    updated_at timestamptz not null,
    constraint uk_secuencial_punto_tipo unique (empresa_id, establecimiento_id, punto_emision_id, tipo_comprobante)
);

create table clientes (
    id uuid primary key,
    empresa_id uuid not null references empresas(id),
    tipo_identificacion varchar(30) not null,
    identificacion varchar(20) not null,
    razon_social varchar(300) not null,
    nombre_comercial varchar(300),
    correo varchar(160),
    telefono varchar(40),
    direccion varchar(500),
    ciudad varchar(120),
    provincia varchar(120),
    activo boolean not null default true,
    created_at timestamptz not null,
    updated_at timestamptz not null,
    constraint uk_cliente_empresa_identificacion unique (empresa_id, identificacion)
);

create table productos (
    id uuid primary key,
    empresa_id uuid not null references empresas(id),
    codigo_principal varchar(80) not null,
    codigo_auxiliar varchar(80),
    nombre varchar(250) not null,
    descripcion varchar(1000),
    tipo varchar(20) not null,
    precio_unitario numeric(14,4) not null,
    tarifa_iva varchar(30) not null,
    ice_porcentaje numeric(8,2),
    stock numeric(14,4),
    categoria varchar(120),
    activo boolean not null default true,
    created_at timestamptz not null,
    updated_at timestamptz not null,
    constraint uk_producto_empresa_codigo unique (empresa_id, codigo_principal)
);

create table comprobantes (
    id uuid primary key,
    empresa_id uuid not null references empresas(id),
    cliente_id uuid not null references clientes(id),
    establecimiento_id uuid not null references establecimientos(id),
    punto_emision_id uuid not null references puntos_emision(id),
    usuario_creador_id uuid references users(id),
    tipo_comprobante varchar(40) not null,
    secuencial bigint not null,
    numero_completo varchar(17) not null,
    fecha_emision date not null,
    ambiente varchar(20) not null,
    tipo_emision varchar(20) not null,
    clave_acceso varchar(49) not null,
    estado_interno varchar(30) not null,
    estado_sri varchar(30) not null,
    subtotal_0 numeric(14,2) not null,
    subtotal_iva numeric(14,2) not null,
    descuento_total numeric(14,2) not null,
    iva_total numeric(14,2) not null,
    ice_total numeric(14,2) not null,
    total numeric(14,2) not null,
    forma_pago varchar(60) not null,
    plazo integer,
    tiempo varchar(30),
    xml_generado text,
    xml_firmado text,
    numero_autorizacion varchar(80),
    fecha_autorizacion timestamptz,
    mensajes_sri text,
    created_at timestamptz not null,
    updated_at timestamptz not null,
    constraint uk_comprobante_numero_empresa unique (empresa_id, tipo_comprobante, numero_completo),
    constraint uk_comprobante_clave_acceso unique (clave_acceso)
);

create table comprobante_detalles (
    id uuid primary key,
    comprobante_id uuid not null references comprobantes(id) on delete cascade,
    producto_id uuid references productos(id),
    codigo_principal varchar(80) not null,
    codigo_auxiliar varchar(80),
    descripcion varchar(500) not null,
    cantidad numeric(14,4) not null,
    precio_unitario numeric(14,4) not null,
    descuento numeric(14,2) not null,
    tarifa_iva varchar(30) not null,
    subtotal numeric(14,2) not null,
    iva numeric(14,2) not null,
    total numeric(14,2) not null,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create table comprobante_pagos (
    id uuid primary key,
    comprobante_id uuid not null references comprobantes(id) on delete cascade,
    forma_pago varchar(60) not null,
    total numeric(14,2) not null,
    plazo integer,
    tiempo varchar(30),
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create table firmas_electronicas (
    id uuid primary key,
    empresa_id uuid not null references empresas(id),
    nombre_archivo varchar(250) not null,
    ruta_segura varchar(600) not null,
    password_secret_ref varchar(250),
    fecha_emision date,
    fecha_vencimiento date,
    estado varchar(30) not null,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create table sri_mensajes (
    id uuid primary key,
    comprobante_id uuid not null references comprobantes(id) on delete cascade,
    identificador varchar(80),
    mensaje varchar(1000) not null,
    informacion_adicional varchar(1500),
    tipo varchar(80),
    estado_sri varchar(30) not null,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create table audit_logs (
    id uuid primary key,
    action varchar(80) not null,
    entity_type varchar(120) not null,
    entity_id uuid,
    user_id uuid references users(id),
    message varchar(1000) not null,
    metadata text,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create index idx_establecimientos_empresa on establecimientos(empresa_id);
create index idx_puntos_establecimiento on puntos_emision(establecimiento_id);
create index idx_clientes_empresa on clientes(empresa_id);
create index idx_productos_empresa on productos(empresa_id);
create index idx_comprobantes_empresa_fecha on comprobantes(empresa_id, fecha_emision);
create index idx_comprobantes_estado on comprobantes(estado_interno, estado_sri);
create index idx_sri_mensajes_comprobante on sri_mensajes(comprobante_id);
create index idx_audit_logs_entity on audit_logs(entity_type, entity_id);
