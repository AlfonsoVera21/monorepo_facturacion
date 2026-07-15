alter table productos
    add column unidad_medida varchar(40) not null default 'UNIDAD',
    add column stock_minimo numeric(14,4) not null default 0,
    add column peso_promedio_kg numeric(14,4),
    add column palletizable boolean not null default false,
    add column unidades_por_pallet numeric(14,4),
    add column requiere_refrigeracion boolean not null default false;

create table choferes (
    id uuid primary key,
    empresa_id uuid not null references empresas(id),
    tipo_identificacion varchar(30) not null,
    identificacion varchar(20) not null,
    nombres varchar(160) not null,
    apellidos varchar(160),
    licencia varchar(60) not null,
    telefono varchar(40),
    correo varchar(160),
    placa_vehiculo varchar(20),
    tipo_vehiculo varchar(120),
    capacidad numeric(14,4),
    unidad_capacidad varchar(40),
    transporta_refrigerado boolean not null default false,
    activo boolean not null default true,
    created_at timestamptz not null,
    updated_at timestamptz not null,
    constraint uk_chofer_empresa_identificacion unique (empresa_id, identificacion)
);

create index idx_choferes_empresa on choferes(empresa_id);
