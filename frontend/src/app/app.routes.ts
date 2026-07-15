import { Routes } from '@angular/router';

import { AppLayoutComponent } from './core/layout/app-layout/app-layout.component';
import { authChildGuard, authGuard } from './core/guards/auth.guard';
import { LoginComponent } from './features/auth/login/login.component';

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent,
    title: 'FactuEC - Iniciar Sesion'
  },
  {
    path: '',
    component: AppLayoutComponent,
    canActivate: [authGuard],
    canActivateChild: [authChildGuard],
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'dashboard'
      },
      {
        path: 'dashboard',
        loadComponent: () => import('./features/dashboard/dashboard.component').then((m) => m.DashboardComponent),
        title: 'FactuEC - Dashboard'
      },
      {
        path: 'comprobantes',
        loadComponent: () => import('./features/comprobantes/list/comprobantes-list.component').then((m) => m.ComprobantesListComponent),
        title: 'FactuEC - Comprobantes'
      },
      {
        path: 'comprobantes/nuevo/factura',
        loadComponent: () => import('./features/comprobantes/form/factura-form.component').then((m) => m.FacturaFormComponent),
        title: 'FactuEC - Nueva Factura'
      },
      {
        path: 'comprobantes/nuevo/guia-remision',
        loadComponent: () => import('./features/comprobantes/guia-remision-form/guia-remision-form.component').then((m) => m.GuiaRemisionFormComponent),
        title: 'FactuEC - Nueva Guia de Remision'
      },
      {
        path: 'comprobantes/:id',
        loadComponent: () => import('./features/comprobantes/detail/comprobante-detail.component').then((m) => m.ComprobanteDetailComponent),
        title: 'FactuEC - Detalle Comprobante'
      },
      {
        path: 'clientes',
        loadComponent: () => import('./features/clientes/clientes.component').then((m) => m.ClientesComponent),
        title: 'FactuEC - Clientes'
      },
      {
        path: 'choferes',
        loadComponent: () => import('./features/choferes/choferes.component').then((m) => m.ChoferesComponent),
        title: 'FactuEC - Choferes'
      },
      {
        path: 'productos',
        loadComponent: () => import('./features/productos/productos.component').then((m) => m.ProductosComponent),
        title: 'FactuEC - Productos'
      },
      {
        path: 'inventario',
        loadComponent: () => import('./features/inventario/inventario.component').then((m) => m.InventarioComponent),
        title: 'FactuEC - Inventario'
      },
      {
        path: 'empresa',
        loadComponent: () => import('./features/empresa/empresa.component').then((m) => m.EmpresaComponent),
        title: 'FactuEC - Empresa'
      },
      {
        path: 'firma-electronica',
        loadComponent: () => import('./features/firma-electronica/firma-electronica.component').then((m) => m.FirmaElectronicaComponent),
        title: 'FactuEC - Firma Electronica'
      },
      {
        path: 'sri/monitor',
        loadComponent: () => import('./features/sri/monitor/sri-monitor.component').then((m) => m.SriMonitorComponent),
        title: 'FactuEC - Monitor SRI'
      },
      {
        path: 'reportes',
        loadComponent: () => import('./features/reportes/reportes.component').then((m) => m.ReportesComponent),
        title: 'FactuEC - Reportes'
      },
      {
        path: 'usuarios',
        loadComponent: () => import('./features/usuarios/usuarios.component').then((m) => m.UsuariosComponent),
        title: 'FactuEC - Usuarios'
      },
      {
        path: 'configuracion',
        loadComponent: () => import('./features/configuracion/configuracion.component').then((m) => m.ConfiguracionComponent),
        title: 'FactuEC - Configuracion'
      }
    ]
  },
  {
    path: '**',
    redirectTo: 'dashboard'
  }
];
