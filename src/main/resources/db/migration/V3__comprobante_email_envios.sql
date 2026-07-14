create extension if not exists pgcrypto;

create table comprobante_email_envios (
    id uuid primary key,
    comprobante_id uuid not null references comprobantes(id) on delete cascade,
    destinatario varchar(160),
    asunto varchar(250),
    estado varchar(30) not null,
    intentos integer not null default 0,
    ultimo_error text,
    ultimo_intento_at timestamptz,
    enviado_at timestamptz,
    created_at timestamptz not null,
    updated_at timestamptz not null,
    constraint uk_comprobante_email_envio_comprobante unique (comprobante_id)
);

create index idx_comprobante_email_envios_estado on comprobante_email_envios(estado, created_at);

insert into comprobante_email_envios (
    id,
    comprobante_id,
    destinatario,
    asunto,
    estado,
    intentos,
    ultimo_error,
    ultimo_intento_at,
    enviado_at,
    created_at,
    updated_at
)
select
    gen_random_uuid(),
    c.id,
    nullif(btrim(cl.correo), ''),
    'Comprobante electronico ' || c.numero_completo,
    case
        when cl.correo is null or btrim(cl.correo) = '' then 'SIN_CORREO'
        else 'PENDIENTE'
    end,
    0,
    case
        when cl.correo is null or btrim(cl.correo) = '' then 'Cliente sin correo configurado'
        else 'Factura autorizada registrada para envio'
    end,
    null,
    null,
    now(),
    now()
from comprobantes c
join clientes cl on cl.id = c.cliente_id
where c.estado_sri = 'AUTORIZADO'
on conflict do nothing;
