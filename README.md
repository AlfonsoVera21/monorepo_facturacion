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
export CORS_ALLOWED_ORIGINS="http://localhost:4200,http://127.0.0.1:4200"
export SRI_AMBIENTE_DEFAULT=PRUEBAS
export SRI_MOCK_ENABLED=false
export SIGNATURE_MOCK_ENABLED=false
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
export CORS_ALLOWED_ORIGINS="http://localhost:4200,http://127.0.0.1:4200"
export SRI_AMBIENTE_DEFAULT=PRUEBAS
export SRI_MOCK_ENABLED=false
export SIGNATURE_MOCK_ENABLED=false
export FACTUEC_CERTIFICATES_PATH="./storage/certificates"
export FIRMA_ELECTRONICA_PASSWORD="password-real-de-tu-firma"
mvn spring-boot:run
```

## Variables principales

- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- `JWT_SECRET`, `JWT_ACCESS_TTL`, `JWT_REFRESH_TTL`
- `ADMIN_PASSWORD`, `ADMIN_USERNAME`, `ADMIN_EMAIL`
- `CORS_ALLOWED_ORIGINS`
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

`XadesBesSignatureAdapter` genera firma XAdES-BES sobre el XML del comprobante y valida certificado PKCS#12 (`.p12/.pfx`), password y vencimiento. La password no se guarda en texto plano: `firmas_electronicas.password_secret_ref` debe apuntar al nombre de una variable de entorno que contiene la clave real.

Para Docker, coloca tu firma en:

```text
/Users/alfonsoverarojas/Documents/api_facturacion/storage/certificates/tu_firma.p12
```

Dentro del contenedor esa misma firma se registra con esta ruta:

```text
/app/storage/certificates/tu_firma.p12
```

En `.env`, configura la clave real de la firma:

```dotenv
FIRMA_ELECTRONICA_PASSWORD="password-real-de-tu-firma"
```

Registra la firma activa para la empresa usando `passwordSecretRef`, no la password directa:

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  --data '{"username":"admin","password":"change-this-admin-password"}' \
  | jq -r '.data.accessToken')

EMPRESA_ID="uuid-de-tu-empresa"

curl -X POST http://localhost:8080/api/firmas \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  --data '{
    "empresaId": "'"$EMPRESA_ID"'",
    "nombreArchivo": "tu_firma.p12",
    "rutaSegura": "/app/storage/certificates/tu_firma.p12",
    "passwordSecretRef": "FIRMA_ELECTRONICA_PASSWORD",
    "fechaEmision": "2026-01-01",
    "fechaVencimiento": "2027-01-01",
    "estado": "ACTIVA"
  }'
```

## SRI

Los adaptadores `SriSoapReceptionAdapter` y `SriSoapAuthorizationAdapter` usan URLs configurables por ambiente. Para probar contra el ambiente de pruebas del SRI:

```bash
export SRI_AMBIENTE_DEFAULT=PRUEBAS
export SRI_MOCK_ENABLED=false
export SIGNATURE_MOCK_ENABLED=false
```

La empresa emisora debe estar creada o actualizada con:

```json
{
  "ambiente": "PRUEBAS"
}
```

Para volver a pruebas sin llamar al SRI externo:

```bash
export SRI_MOCK_ENABLED=true
export SIGNATURE_MOCK_ENABLED=true
```

Para produccion, cambia explicitamente:

```bash
export SPRING_PROFILES_ACTIVE=prod
export SRI_AMBIENTE_DEFAULT=PRODUCCION
export SRI_MOCK_ENABLED=false
export SIGNATURE_MOCK_ENABLED=false
```

## Pruebas

```bash
mvn test
```
