# FactuEC Frontend

Aplicacion Angular 22 standalone para el sistema de facturacion electronica FactuEC. La interfaz replica la maqueta de Stitch del proyecto `591213973848654623` con layout corporativo, sidebar fijo/colapsable, header con ambiente SRI, tablas densas y formularios reactivos.

## Requisitos

- Node.js 22+
- npm 10+

## Comandos

```bash
npm install
npm start
```

La app queda disponible en:

```text
http://localhost:4200
```

Para compilar:

```bash
npm run build
```

## Integracion backend

La URL base esta en `src/environments/environment.ts`:

```ts
apiUrl: 'http://localhost:8080/api'
```

Para desarrollo local, levanta primero el backend desde la raiz del monorepo:

```bash
export JWT_SECRET="change-this-local-secret-with-at-least-32-bytes"
export ADMIN_PASSWORD="change-this-admin-password"
export CORS_ALLOWED_ORIGINS="http://localhost:4200,http://127.0.0.1:4200"
docker compose up --build
```

Credenciales iniciales:

```text
usuario: admin
password: valor de ADMIN_PASSWORD
```

Servicios conectados al API real:

- `AuthService`
- `DashboardService`
- `ComprobantesService`
- `ClientesService`
- `ProductosService`
- `EmpresaService`
- `FirmaElectronicaService`
- `SriService`
- `ReportesService`

Servicios que aun usan datos locales porque el backend no expone endpoint dedicado:

- `UsuariosService`
- `ConfiguracionService`
