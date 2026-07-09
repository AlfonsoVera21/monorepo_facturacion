# FactuEC Frontend

Aplicacion Angular 22 standalone para el sistema de facturacion electronica FactuEC. La interfaz replica la maqueta de Stitch del proyecto `591213973848654623` con layout corporativo, sidebar fijo/colapsable, header con ambiente SRI, tablas densas, formularios reactivos y datos mock preparados para API REST.

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

Los servicios de dominio usan mocks por ahora y ya estan separados por responsabilidad:

- `AuthService`
- `DashboardService`
- `ComprobantesService`
- `ClientesService`
- `ProductosService`
- `EmpresaService`
- `FirmaElectronicaService`
- `SriService`
- `ReportesService`
- `UsuariosService`
- `ConfiguracionService`

Para conectar Spring Boot, reemplaza las respuestas mock por llamadas a `ApiService` o `HttpClient` conservando los modelos en `src/app/core/models/factuec.models.ts`.
