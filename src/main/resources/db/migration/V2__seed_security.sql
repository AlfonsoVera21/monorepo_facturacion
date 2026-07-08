insert into permissions (id, name, description, created_at, updated_at) values
('10000000-0000-0000-0000-000000000001', 'EMPRESAS_WRITE', 'Crear y actualizar empresas', now(), now()),
('10000000-0000-0000-0000-000000000002', 'FACTURAS_EMIT', 'Emitir comprobantes electronicos', now(), now()),
('10000000-0000-0000-0000-000000000003', 'REPORTES_READ', 'Consultar reportes', now(), now()),
('10000000-0000-0000-0000-000000000004', 'SRI_RETRY', 'Reenviar comprobantes al SRI', now(), now());

insert into roles (id, name, description, created_at, updated_at) values
('20000000-0000-0000-0000-000000000001', 'ADMIN', 'Administrador del sistema', now(), now()),
('20000000-0000-0000-0000-000000000002', 'EMISOR', 'Usuario emisor de comprobantes', now(), now()),
('20000000-0000-0000-0000-000000000003', 'CONTABILIDAD', 'Usuario contable', now(), now()),
('20000000-0000-0000-0000-000000000004', 'CONSULTA', 'Usuario de consulta', now(), now()),
('20000000-0000-0000-0000-000000000005', 'SOPORTE', 'Usuario de soporte', now(), now());

insert into role_permissions (role_id, permission_id)
select '20000000-0000-0000-0000-000000000001', id from permissions;

insert into role_permissions (role_id, permission_id) values
('20000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000002'),
('20000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000002'),
('20000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000003'),
('20000000-0000-0000-0000-000000000004', '10000000-0000-0000-0000-000000000003'),
('20000000-0000-0000-0000-000000000005', '10000000-0000-0000-0000-000000000004');
