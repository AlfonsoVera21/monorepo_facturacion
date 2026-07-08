# FactuEc API

Backend Spring Boot 3 / Java 21 para facturacion electronica Ecuador, organizado con arquitectura hexagonal:

- `domain`: reglas de negocio, enums tributarios, value objects y calculos.
- `application`: DTOs, casos de uso y puertos.
- `infrastructure`: JPA, XML, firma, SRI SOAP, PDF/RIDE, email y storage.
- `interfaces`: controladores REST para Angular.

## Requisitos

- Java 21.
- Maven 3.9+ o Docker.
- PostgreSQL 16+.

## Arranque con Docker

```bash
export JWT_SECRET="change-this-local-secret-with-at-least-32-bytes"
export ADMIN_PASSWORD="change-this-admin-password"
docker compose up --build
```

Swagger queda en:

```text
http://localhost:8080/swagger-ui.html
```

Usuario admin inicial local:

```text
usuario: admin
password: valor de ADMIN_PASSWORD
```

## Arranque local

```bash
docker compose up -d postgres
export JWT_SECRET="change-this-local-secret-with-at-least-32-bytes"
export ADMIN_PASSWORD="change-this-admin-password"
export SRI_MOCK_ENABLED=true
export SIGNATURE_MOCK_ENABLED=true
mvn spring-boot:run
```

## Variables principales

- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- `JWT_SECRET`, `JWT_ACCESS_TTL`, `JWT_REFRESH_TTL`
- `SRI_RECEPCION_PRUEBAS_URL`, `SRI_AUTORIZACION_PRUEBAS_URL`
- `SRI_RECEPCION_PRODUCCION_URL`, `SRI_AUTORIZACION_PRODUCCION_URL`
- `SRI_MOCK_ENABLED`
- `SIGNATURE_MOCK_ENABLED`
- `FACTUEC_XML_PATH`, `FACTUEC_PDF_PATH`, `FACTUEC_CERTIFICATES_PATH`
- `SMTP_ENABLED`, `SMTP_HOST`, `SMTP_PORT`, `SMTP_USERNAME`, `SMTP_PASSWORD`, `SMTP_FROM`

## Endpoints principales

- Auth: `POST /api/auth/login`, `POST /api/auth/refresh`, `GET /api/auth/me`
- Empresas: `GET|POST /api/empresas`, `GET|PUT /api/empresas/{id}`
- Configuracion emision: `/api/establecimientos`, `/api/puntos-emision`, `/api/secuenciales`
- Clientes: `GET|POST /api/clientes`, `GET|PUT|DELETE /api/clientes/{id}`
- Productos: `GET|POST /api/productos`, `GET|PUT|DELETE /api/productos/{id}`
- Facturas: `POST /api/comprobantes/facturas/borrador`, `POST /api/comprobantes/facturas/emitir`
- Comprobantes: reenvio SRI, autorizacion, descarga XML, descarga RIDE y envio por correo.
- Firmas: `POST /api/firmas`, `GET /api/firmas/empresa/{empresaId}`, `PUT /api/firmas/{id}`
- SRI: estado, errores y reenvio de pendientes.
- Reportes: ventas, comprobantes, clientes y productos.

## Firma electronica

`XadesBesSignatureAdapter` valida XML, certificado PKCS#12 (`.p12/.pfx`), password y vencimiento. La password no se guarda en texto plano: `firmas_electronicas.password_secret_ref` debe apuntar al nombre de una variable de entorno que contiene la clave real.

La implementacion actual genera XMLDSig enveloped con certificado del contribuyente y deja el adaptador preparado para completar XAdES-BES estricto. Para produccion se recomienda agregar/configurar `xades4j` y mapear `SignedProperties` dentro de `infrastructure/signature/XadesBesSignatureAdapter`.

En desarrollo puede usarse:

```bash
export SIGNATURE_MOCK_ENABLED=true
```

## SRI

Los adaptadores `SriSoapReceptionAdapter` y `SriSoapAuthorizationAdapter` usan URLs configurables por ambiente. En `local`, `SRI_MOCK_ENABLED=true` permite probar el flujo completo sin llamar servicios externos.

Para produccion:

```bash
export SPRING_PROFILES_ACTIVE=prod
export SRI_MOCK_ENABLED=false
export SIGNATURE_MOCK_ENABLED=false
```

## Pruebas

```bash
mvn test
```
