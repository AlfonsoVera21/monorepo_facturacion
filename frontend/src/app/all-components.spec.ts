import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { App } from './app';
import { AppLayoutComponent } from './core/layout/app-layout/app-layout.component';
import { HeaderComponent } from './core/layout/header/header.component';
import { SidebarComponent } from './core/layout/sidebar/sidebar.component';
import { AuthService } from './core/services/auth.service';
import { ClientesService } from './core/services/clientes.service';
import { ComprobantesService } from './core/services/comprobantes.service';
import { ConfiguracionService } from './core/services/configuracion.service';
import { DashboardService } from './core/services/dashboard.service';
import { EmpresaService } from './core/services/empresa.service';
import { FirmaElectronicaService } from './core/services/firma-electronica.service';
import { ProductosService } from './core/services/productos.service';
import { ReportesService } from './core/services/reportes.service';
import { SriService } from './core/services/sri.service';
import { UsuariosService } from './core/services/usuarios.service';
import { Cliente, Comprobante, DashboardMetric, Empresa, Establecimiento, Producto, Role, User } from './core/models/factuec.models';
import { LoginComponent } from './features/auth/login/login.component';
import { ClientesComponent } from './features/clientes/clientes.component';
import { ComprobanteDetailComponent } from './features/comprobantes/detail/comprobante-detail.component';
import { FacturaFormComponent } from './features/comprobantes/form/factura-form.component';
import { ComprobantesListComponent } from './features/comprobantes/list/comprobantes-list.component';
import { ConfiguracionComponent } from './features/configuracion/configuracion.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { EmpresaComponent } from './features/empresa/empresa.component';
import { FirmaElectronicaComponent } from './features/firma-electronica/firma-electronica.component';
import { ProductosComponent } from './features/productos/productos.component';
import { ReportesComponent } from './features/reportes/reportes.component';
import { SriMonitorComponent } from './features/sri/monitor/sri-monitor.component';
import { UsuariosComponent } from './features/usuarios/usuarios.component';
import { AlertCardComponent } from './shared/components/alert-card/alert-card.component';
import { BreadcrumbComponent } from './shared/components/breadcrumb/breadcrumb.component';
import { ConfirmDialogComponent } from './shared/components/confirm-dialog/confirm-dialog.component';
import { EmptyStateComponent } from './shared/components/empty-state/empty-state.component';
import { LoadingSkeletonComponent } from './shared/components/loading-skeleton/loading-skeleton.component';
import { MetricCardComponent } from './shared/components/metric-card/metric-card.component';
import { PageHeaderComponent } from './shared/components/page-header/page-header.component';
import { StatusBadgeComponent } from './shared/components/status-badge/status-badge.component';
import { TimelineComponent } from './shared/components/timeline/timeline.component';

describe('App', () => {
  it('crea el componente raiz', async () => {
    await TestBed.configureTestingModule({ imports: [App], providers: [provideRouter([])] }).compileComponents();
    expect(TestBed.createComponent(App).componentInstance).toBeTruthy();
  });

  it('renderiza el router outlet principal', async () => {
    await TestBed.configureTestingModule({ imports: [App], providers: [provideRouter([])] }).compileComponents();
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('router-outlet')).toBeTruthy();
  });
});

describe('AlertCardComponent', () => {
  it('crea la tarjeta de alerta', async () => {
    await TestBed.configureTestingModule({ imports: [AlertCardComponent] }).compileComponents();
    expect(TestBed.createComponent(AlertCardComponent).componentInstance).toBeTruthy();
  });

  it('renderiza titulo, descripcion y accion configurados', async () => {
    await TestBed.configureTestingModule({ imports: [AlertCardComponent] }).compileComponents();
    const fixture = TestBed.createComponent(AlertCardComponent);
    fixture.componentRef.setInput('title', 'Aviso');
    fixture.componentRef.setInput('description', 'Mensaje fiscal');
    fixture.componentRef.setInput('actionLabel', 'Resolver');
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Aviso');
    expect(fixture.nativeElement.textContent).toContain('Mensaje fiscal');
    expect(fixture.nativeElement.querySelector('button')?.textContent).toContain('Resolver');
  });
});

describe('BreadcrumbComponent', () => {
  it('crea el breadcrumb', async () => {
    await TestBed.configureTestingModule({ imports: [BreadcrumbComponent], providers: [provideRouter([])] }).compileComponents();
    expect(TestBed.createComponent(BreadcrumbComponent).componentInstance).toBeTruthy();
  });

  it('muestra inicio y los items recibidos', async () => {
    await TestBed.configureTestingModule({ imports: [BreadcrumbComponent], providers: [provideRouter([])] }).compileComponents();
    const fixture = TestBed.createComponent(BreadcrumbComponent);
    fixture.componentRef.setInput('items', [{ label: 'Comprobantes', link: '/comprobantes' }, { label: 'Detalle' }]);
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Inicio');
    expect(fixture.nativeElement.textContent).toContain('Comprobantes');
    expect(fixture.nativeElement.textContent).toContain('Detalle');
  });
});

describe('ConfirmDialogComponent', () => {
  it('no renderiza dialogo cuando esta cerrado', async () => {
    await TestBed.configureTestingModule({ imports: [ConfirmDialogComponent] }).compileComponents();
    const fixture = TestBed.createComponent(ConfirmDialogComponent);
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('[role="dialog"]')).toBeNull();
  });

  it('emite confirmacion cuando se presiona confirmar', async () => {
    await TestBed.configureTestingModule({ imports: [ConfirmDialogComponent] }).compileComponents();
    const fixture = TestBed.createComponent(ConfirmDialogComponent);
    fixture.componentRef.setInput('open', true);
    fixture.componentRef.setInput('confirmLabel', 'Salir');
    const spy = jest.fn();
    fixture.componentInstance.confirmed.subscribe(spy);
    fixture.detectChanges();
    fixture.nativeElement.querySelector('.btn--primary').click();
    expect(spy).toHaveBeenCalledTimes(1);
  });
});

describe('EmptyStateComponent', () => {
  it('crea el estado vacio', async () => {
    await TestBed.configureTestingModule({ imports: [EmptyStateComponent] }).compileComponents();
    expect(TestBed.createComponent(EmptyStateComponent).componentInstance).toBeTruthy();
  });

  it('muestra titulo y descripcion personalizados', async () => {
    await TestBed.configureTestingModule({ imports: [EmptyStateComponent] }).compileComponents();
    const fixture = TestBed.createComponent(EmptyStateComponent);
    fixture.componentRef.setInput('title', 'Sin datos');
    fixture.componentRef.setInput('description', 'Carga informacion real');
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Sin datos');
    expect(fixture.nativeElement.textContent).toContain('Carga informacion real');
  });
});

describe('LoadingSkeletonComponent', () => {
  it('crea el skeleton de carga', async () => {
    await TestBed.configureTestingModule({ imports: [LoadingSkeletonComponent] }).compileComponents();
    expect(TestBed.createComponent(LoadingSkeletonComponent).componentInstance).toBeTruthy();
  });

  it('renderiza la cantidad de filas solicitada', async () => {
    await TestBed.configureTestingModule({ imports: [LoadingSkeletonComponent] }).compileComponents();
    const fixture = TestBed.createComponent(LoadingSkeletonComponent);
    fixture.componentRef.setInput('rows', 3);
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelectorAll('.loading-skeleton span')).toHaveLength(3);
  });
});

describe('MetricCardComponent', () => {
  it('crea la tarjeta metrica', async () => {
    await TestBed.configureTestingModule({ imports: [MetricCardComponent] }).compileComponents();
    expect(TestBed.createComponent(MetricCardComponent).componentInstance).toBeTruthy();
  });

  it('renderiza valor y tendencia solo cuando existen', async () => {
    await TestBed.configureTestingModule({ imports: [MetricCardComponent] }).compileComponents();
    const fixture = TestBed.createComponent(MetricCardComponent);
    fixture.componentRef.setInput('title', 'Ventas');
    fixture.componentRef.setInput('value', '$10.00');
    fixture.componentRef.setInput('trend', '+5%');
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Ventas');
    expect(fixture.nativeElement.textContent).toContain('$10.00');
    expect(fixture.nativeElement.textContent).toContain('+5%');
  });
});

describe('PageHeaderComponent', () => {
  it('crea el encabezado de pagina', async () => {
    await TestBed.configureTestingModule({ imports: [PageHeaderComponent], providers: [provideRouter([])] }).compileComponents();
    expect(TestBed.createComponent(PageHeaderComponent).componentInstance).toBeTruthy();
  });

  it('renderiza accion cuando label y link existen', async () => {
    await TestBed.configureTestingModule({ imports: [PageHeaderComponent], providers: [provideRouter([])] }).compileComponents();
    const fixture = TestBed.createComponent(PageHeaderComponent);
    fixture.componentRef.setInput('title', 'Listado');
    fixture.componentRef.setInput('actionLabel', 'Nuevo');
    fixture.componentRef.setInput('actionLink', '/nuevo');
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Listado');
    expect(fixture.nativeElement.querySelector('a')?.textContent).toContain('Nuevo');
  });
});

describe('StatusBadgeComponent', () => {
  it('crea el badge de estado', async () => {
    await TestBed.configureTestingModule({ imports: [StatusBadgeComponent] }).compileComponents();
    expect(TestBed.createComponent(StatusBadgeComponent).componentInstance).toBeTruthy();
  });

  it('normaliza etiqueta y clase css', async () => {
    await TestBed.configureTestingModule({ imports: [StatusBadgeComponent] }).compileComponents();
    const fixture = TestBed.createComponent(StatusBadgeComponent);
    fixture.componentRef.setInput('status', 'NO_AUTORIZADO');
    fixture.detectChanges();
    const badge = fixture.nativeElement.querySelector('.status-badge');
    expect(badge.textContent).toContain('NO AUTORIZADO');
    expect(badge.classList.contains('status-badge--no-autorizado')).toBe(true);
  });
});

describe('TimelineComponent', () => {
  it('crea la linea de tiempo', async () => {
    await TestBed.configureTestingModule({ imports: [TimelineComponent] }).compileComponents();
    expect(TestBed.createComponent(TimelineComponent).componentInstance).toBeTruthy();
  });

  it('renderiza items con icono por defecto y estado opcional', async () => {
    await TestBed.configureTestingModule({ imports: [TimelineComponent] }).compileComponents();
    const fixture = TestBed.createComponent(TimelineComponent);
    fixture.componentRef.setInput('items', [{ title: 'Creado', timestamp: 'Hoy', status: 'OK' }]);
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('Creado');
    expect(fixture.nativeElement.textContent).toContain('OK');
    expect(fixture.nativeElement.textContent).toContain('check');
  });
});

describe('AppLayoutComponent', () => {
  const providers = () => [
    provideRouter([]),
    { provide: AuthService, useValue: authService() },
    { provide: SriService, useValue: sriService() },
    { provide: Router, useValue: routerMock() }
  ];

  it('crea el layout principal', async () => {
    await TestBed.configureTestingModule({ imports: [AppLayoutComponent], providers: providers() }).compileComponents();
    expect(TestBed.createComponent(AppLayoutComponent).componentInstance).toBeTruthy();
  });

  it('abre sidebar y alterna estado colapsado', async () => {
    await TestBed.configureTestingModule({ imports: [AppLayoutComponent], providers: providers() }).compileComponents();
    const component = TestBed.createComponent(AppLayoutComponent).componentInstance as any;
    component.openSidebar();
    component.toggleCollapsed();
    expect(component.sidebarOpen()).toBe(true);
    expect(component.collapsed()).toBe(true);
  });
});

describe('HeaderComponent', () => {
  it('crea el header y carga ambiente SRI', async () => {
    await TestBed.configureTestingModule({
      imports: [HeaderComponent],
      providers: [{ provide: AuthService, useValue: authService() }, { provide: SriService, useValue: sriService('PRODUCCION') }, { provide: Router, useValue: routerMock() }]
    }).compileComponents();
    const fixture = TestBed.createComponent(HeaderComponent);
    fixture.detectChanges();
    expect((fixture.componentInstance as any).ambiente()).toBe('PRODUCCION');
  });

  it('confirma cierre de sesion con modal antes de navegar', async () => {
    const auth = authService();
    const router = routerMock();
    await TestBed.configureTestingModule({
      imports: [HeaderComponent],
      providers: [{ provide: AuthService, useValue: auth }, { provide: SriService, useValue: sriService() }, { provide: Router, useValue: router }]
    }).compileComponents();
    const component = TestBed.createComponent(HeaderComponent).componentInstance as any;
    component.requestLogout();
    expect(component.logoutDialogOpen()).toBe(true);
    component.confirmLogout();
    expect(auth.logout).toHaveBeenCalledTimes(1);
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });
});

describe('SidebarComponent', () => {
  it('crea el sidebar con navegacion principal', async () => {
    await TestBed.configureTestingModule({ imports: [SidebarComponent], providers: [provideRouter([])] }).compileComponents();
    const fixture = TestBed.createComponent(SidebarComponent);
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelectorAll('.sidebar__link')).toHaveLength(10);
  });

  it('emite evento al colapsar', async () => {
    await TestBed.configureTestingModule({ imports: [SidebarComponent], providers: [provideRouter([])] }).compileComponents();
    const fixture = TestBed.createComponent(SidebarComponent);
    const spy = jest.fn();
    fixture.componentInstance.toggleCollapsed.subscribe(spy);
    fixture.detectChanges();
    fixture.nativeElement.querySelector('.sidebar__collapse').click();
    expect(spy).toHaveBeenCalledTimes(1);
  });
});

describe('LoginComponent', () => {
  it('crea el login con ambiente de pruebas activo', async () => {
    await TestBed.configureTestingModule({ imports: [LoginComponent], providers: [{ provide: AuthService, useValue: authService() }, { provide: Router, useValue: routerMock() }] }).compileComponents();
    const component = TestBed.createComponent(LoginComponent).componentInstance as any;
    expect(component.selectedEnvironment()).toBe('PRUEBAS');
  });

  it('cambia ambiente y alterna visibilidad de contrasena', async () => {
    await TestBed.configureTestingModule({ imports: [LoginComponent], providers: [{ provide: AuthService, useValue: authService() }, { provide: Router, useValue: routerMock() }] }).compileComponents();
    const component = TestBed.createComponent(LoginComponent).componentInstance as any;
    component.setEnvironment('PRODUCCION');
    component.togglePasswordVisibility();
    expect(component.selectedEnvironment()).toBe('PRODUCCION');
    expect(component.showPassword()).toBe(true);
  });
});

describe('DashboardComponent', () => {
  it('crea dashboard y carga metricas', async () => {
    await TestBed.configureTestingModule({ imports: [DashboardComponent], providers: [provideRouter([]), { provide: DashboardService, useValue: dashboardService() }] }).compileComponents();
    const fixture = TestBed.createComponent(DashboardComponent);
    fixture.detectChanges();
    expect((fixture.componentInstance as any).metrics()).toHaveLength(1);
  });

  it('mantiene datos de barras definidos para la vista', async () => {
    await TestBed.configureTestingModule({ imports: [DashboardComponent], providers: [provideRouter([]), { provide: DashboardService, useValue: dashboardService() }] }).compileComponents();
    const component = TestBed.createComponent(DashboardComponent).componentInstance as any;
    expect(component.barData).toHaveLength(6);
    expect(component.barData[0].label).toBe('ENE');
  });
});

describe('ConfiguracionComponent', () => {
  it('crea configuracion y parchea valores del backend', async () => {
    await TestBed.configureTestingModule({ imports: [ConfiguracionComponent], providers: [{ provide: ConfiguracionService, useValue: configuracionService() }] }).compileComponents();
    const fixture = TestBed.createComponent(ConfiguracionComponent);
    fixture.detectChanges();
    expect((fixture.componentInstance as any).settingsForm.controls.ambiente.value).toBe('PRUEBAS');
  });

  it('conserva mfa desactivado segun respuesta real', async () => {
    await TestBed.configureTestingModule({ imports: [ConfiguracionComponent], providers: [{ provide: ConfiguracionService, useValue: configuracionService() }] }).compileComponents();
    const fixture = TestBed.createComponent(ConfiguracionComponent);
    fixture.detectChanges();
    expect((fixture.componentInstance as any).settingsForm.controls.mfaObligatorio.value).toBe(false);
  });
});

describe('ClientesComponent', () => {
  it('crea clientes y calcula totales cargados', async () => {
    await TestBed.configureTestingModule({ imports: [ClientesComponent], providers: [{ provide: ClientesService, useValue: clientesService([cliente()]) }, { provide: EmpresaService, useValue: empresaService() }] }).compileComponents();
    const fixture = TestBed.createComponent(ClientesComponent);
    fixture.detectChanges();
    expect((fixture.componentInstance as any).totalClientes()).toBe('1');
  });

  it('muestra advertencia al crear cliente sin empresa', async () => {
    await TestBed.configureTestingModule({ imports: [ClientesComponent], providers: [{ provide: ClientesService, useValue: clientesService([]) }, { provide: EmpresaService, useValue: empresaService(null) }] }).compileComponents();
    const component = TestBed.createComponent(ClientesComponent).componentInstance as any;
    component.ngOnInit();
    component.newCliente();
    expect(component.feedback()?.tone).toBe('warning');
  });
});

describe('ProductosComponent', () => {
  it('crea productos y calcula servicios', async () => {
    await TestBed.configureTestingModule({ imports: [ProductosComponent], providers: [{ provide: ProductosService, useValue: productosService([producto()]) }, { provide: EmpresaService, useValue: empresaService() }] }).compileComponents();
    const fixture = TestBed.createComponent(ProductosComponent);
    fixture.detectChanges();
    expect((fixture.componentInstance as any).servicios()).toBe('1');
  });

  it('muestra advertencia al crear producto sin empresa', async () => {
    await TestBed.configureTestingModule({ imports: [ProductosComponent], providers: [{ provide: ProductosService, useValue: productosService([]) }, { provide: EmpresaService, useValue: empresaService(null) }] }).compileComponents();
    const component = TestBed.createComponent(ProductosComponent).componentInstance as any;
    component.ngOnInit();
    component.newProducto();
    expect(component.feedback()?.tone).toBe('warning');
  });
});

describe('EmpresaComponent', () => {
  it('crea empresa sin defaults quemados cuando no hay backend', async () => {
    await TestBed.configureTestingModule({ imports: [EmpresaComponent], providers: [{ provide: EmpresaService, useValue: empresaService(null) }] }).compileComponents();
    const component = TestBed.createComponent(EmpresaComponent).componentInstance as any;
    expect(component.empresaForm.controls.ruc.value).toBe('');
  });

  it('carga empresa y establecimientos desde servicio', async () => {
    await TestBed.configureTestingModule({ imports: [EmpresaComponent], providers: [{ provide: EmpresaService, useValue: empresaService(empresa(), [establecimiento()]) }] }).compileComponents();
    const fixture = TestBed.createComponent(EmpresaComponent);
    fixture.detectChanges();
    expect((fixture.componentInstance as any).empresaId()).toBe('empresa-1');
    expect((fixture.componentInstance as any).establecimientos()).toHaveLength(1);
  });
});

describe('FirmaElectronicaComponent', () => {
  it('crea firma electronica y selecciona certificado disponible', async () => {
    await TestBed.configureTestingModule({ imports: [FirmaElectronicaComponent], providers: [{ provide: EmpresaService, useValue: empresaService() }, { provide: FirmaElectronicaService, useValue: firmaService() }] }).compileComponents();
    const fixture = TestBed.createComponent(FirmaElectronicaComponent);
    fixture.detectChanges();
    expect((fixture.componentInstance as any).firmaForm.controls.rutaSegura.value).toBe('/cert/firma.p12');
  });

  it('advierte si se intenta guardar sin empresa', async () => {
    await TestBed.configureTestingModule({ imports: [FirmaElectronicaComponent], providers: [{ provide: EmpresaService, useValue: empresaService(null) }, { provide: FirmaElectronicaService, useValue: firmaService() }] }).compileComponents();
    const component = TestBed.createComponent(FirmaElectronicaComponent).componentInstance as any;
    component.saveFirma();
    expect(component.feedback()?.tone).toBe('warning');
  });
});

describe('ReportesComponent', () => {
  it('crea reportes y carga ventas mensuales', async () => {
    await TestBed.configureTestingModule({ imports: [ReportesComponent], providers: [{ provide: ReportesService, useValue: reportesService() }] }).compileComponents();
    const fixture = TestBed.createComponent(ReportesComponent);
    fixture.detectChanges();
    expect((fixture.componentInstance as any).ventas()).toHaveLength(1);
  });

  it('inicia filtros con rango base', async () => {
    await TestBed.configureTestingModule({ imports: [ReportesComponent], providers: [{ provide: ReportesService, useValue: reportesService() }] }).compileComponents();
    const component = TestBed.createComponent(ReportesComponent).componentInstance as any;
    expect(component.filtrosForm.controls.tipo.value).toBe('VENTAS');
  });
});

describe('SriMonitorComponent', () => {
  it('crea monitor y carga estado SRI', async () => {
    await TestBed.configureTestingModule({ imports: [SriMonitorComponent], providers: [provideRouter([]), { provide: SriService, useValue: sriService() }] }).compileComponents();
    const fixture = TestBed.createComponent(SriMonitorComponent);
    fixture.detectChanges();
    expect((fixture.componentInstance as any).status()?.ambienteDefault).toBe('PRUEBAS');
  });

  it('convierte numeros a texto', async () => {
    await TestBed.configureTestingModule({ imports: [SriMonitorComponent], providers: [provideRouter([]), { provide: SriService, useValue: sriService() }] }).compileComponents();
    const component = TestBed.createComponent(SriMonitorComponent).componentInstance as any;
    expect(component.numberText(7)).toBe('7');
  });
});

describe('UsuariosComponent', () => {
  it('crea usuarios y carga roles', async () => {
    await TestBed.configureTestingModule({ imports: [UsuariosComponent], providers: [{ provide: UsuariosService, useValue: usuariosService([user()], [role()]) }] }).compileComponents();
    const fixture = TestBed.createComponent(UsuariosComponent);
    fixture.detectChanges();
    expect((fixture.componentInstance as any).activeUsers()).toBe('1');
    expect((fixture.componentInstance as any).roleCount()).toBe('1');
  });

  it('abre formulario de nuevo usuario limpio', async () => {
    await TestBed.configureTestingModule({ imports: [UsuariosComponent], providers: [{ provide: UsuariosService, useValue: usuariosService([], []) }] }).compileComponents();
    const component = TestBed.createComponent(UsuariosComponent).componentInstance as any;
    component.newUser();
    expect(component.showForm()).toBe(true);
    expect(component.userForm.controls.username.value).toBe('');
  });
});

describe('ComprobantesListComponent', () => {
  it('crea listado y calcula metricas reales', async () => {
    await TestBed.configureTestingModule({ imports: [ComprobantesListComponent], providers: [provideRouter([]), { provide: ComprobantesService, useValue: comprobantesService([comprobante({ estado: 'AUTORIZADO', fechaEmision: today(), total: 15 })]) }] }).compileComponents();
    const fixture = TestBed.createComponent(ComprobantesListComponent);
    fixture.detectChanges();
    expect((fixture.componentInstance as any).autorizadosHoy()).toBe('1');
  });

  it('filtra por cliente y reinicia paginacion', async () => {
    await TestBed.configureTestingModule({ imports: [ComprobantesListComponent], providers: [provideRouter([]), { provide: ComprobantesService, useValue: comprobantesService([comprobante({ cliente: cliente({ razonSocial: 'Acme' }) })]) }] }).compileComponents();
    const fixture = TestBed.createComponent(ComprobantesListComponent);
    fixture.detectChanges();
    const component = fixture.componentInstance as any;
    component.filtersForm.patchValue({ cliente: 'acme' });
    component.applyFilters();
    expect(component.totalItems()).toBe(1);
    expect(component.currentPage()).toBe(1);
  });
});

describe('ComprobanteDetailComponent', () => {
  it('crea detalle y carga comprobante por id de ruta', async () => {
    const service = { getById: jest.fn(() => of(comprobante())) };
    await TestBed.configureTestingModule({ imports: [ComprobanteDetailComponent], providers: [provideRouter([]), activatedRoute('abc'), { provide: ComprobantesService, useValue: service }] }).compileComponents();
    const fixture = TestBed.createComponent(ComprobanteDetailComponent);
    fixture.detectChanges();
    expect(service.getById).toHaveBeenCalledWith('abc');
  });

  it('no consulta servicio si no existe id en ruta', async () => {
    const service = { getById: jest.fn(() => of(comprobante())) };
    await TestBed.configureTestingModule({ imports: [ComprobanteDetailComponent], providers: [provideRouter([]), activatedRoute(null), { provide: ComprobantesService, useValue: service }] }).compileComponents();
    const fixture = TestBed.createComponent(ComprobanteDetailComponent);
    fixture.detectChanges();
    expect(service.getById).not.toHaveBeenCalled();
  });
});

describe('FacturaFormComponent', () => {
  it('crea factura y calcula subtotal iva total', async () => {
    await TestBed.configureTestingModule({ imports: [FacturaFormComponent], providers: facturaProviders() }).compileComponents();
    const component = TestBed.createComponent(FacturaFormComponent).componentInstance as any;
    expect(component.subtotal()).toBe(10);
    expect(component.iva()).toBe(1.5);
    expect(component.total()).toBe(11.5);
  });

  it('no elimina la ultima linea de detalle', async () => {
    await TestBed.configureTestingModule({ imports: [FacturaFormComponent], providers: facturaProviders() }).compileComponents();
    const component = TestBed.createComponent(FacturaFormComponent).componentInstance as any;
    component.removeItem(0);
    expect(component.detalles.length).toBe(1);
  });
});

function authService() {
  return {
    user: jest.fn(() => user()),
    login: jest.fn(() => of(user())),
    logout: jest.fn()
  };
}

function routerMock() {
  return { navigate: jest.fn() };
}

function sriService(ambienteDefault = 'PRUEBAS') {
  return {
    getStatus: jest.fn(() => of({
      ambienteDefault,
      uptime: 'LIVE',
      online: true,
      pendingQueue: 0,
      authorizedToday: 0,
      errorsToday: 0
    })),
    getMensajes: jest.fn(() => of([]))
  };
}

function dashboardService(metrics: DashboardMetric[] = [{ title: 'Ventas', value: '$1', icon: 'payments' }]) {
  return {
    getMetrics: jest.fn(() => of(metrics)),
    getLatestComprobantes: jest.fn(() => of([comprobante()]))
  };
}

function configuracionService() {
  return {
    getGeneralSettings: jest.fn(() => of({
      serieDefault: 'Configurada por establecimiento',
      ambiente: 'PRUEBAS',
      correoNotificaciones: 'no-reply@factuec.local',
      enviarRideAutomatico: false,
      reintentosSri: '2',
      mfaObligatorio: false,
      sriMockEnabled: false,
      firmaMockEnabled: false
    }))
  };
}

function clientesService(clientes: Cliente[]) {
  return { list: jest.fn(() => of(clientes)), save: jest.fn(() => of(clientes[0] ?? cliente())), delete: jest.fn(() => of(void 0)) };
}

function productosService(productos: Producto[]) {
  return { list: jest.fn(() => of(productos)), save: jest.fn(() => of(productos[0] ?? producto())), delete: jest.fn(() => of(void 0)) };
}

function empresaService(current: Empresa | null = empresa(), establecimientos: Establecimiento[] = []) {
  return {
    getEmpresa: jest.fn(() => of(current)),
    getEstablecimientos: jest.fn(() => of(establecimientos)),
    saveEmpresa: jest.fn(() => of(current ?? empresa())),
    saveEstablecimiento: jest.fn(() => of(establecimiento())),
    createPuntoEmision: jest.fn(() => of({ id: 'punto-1', codigo: '001', nombre: 'Punto', secuencialFactura: 0, activo: true }))
  };
}

function firmaService() {
  return {
    listCertificados: jest.fn(() => of([{ nombreArchivo: 'firma.p12', rutaSegura: '/cert/firma.p12', tamanoBytes: 1, modificadoEn: today() }])),
    getCurrent: jest.fn(() => of(undefined)),
    getAuditTrail: jest.fn(() => of([])),
    saveFirma: jest.fn(() => of({ id: 'firma-1', empresaId: 'empresa-1', archivoNombre: 'firma.p12', titular: 'Certificado', emisor: '/cert/firma.p12', fechaVencimiento: today(), estado: 'VALIDO', diasRestantes: 1 }))
  };
}

function reportesService() {
  return { getVentasMensuales: jest.fn(() => of([{ mes: 'Jul', ventas: 10 }])) };
}

function usuariosService(users: User[], roles: Role[]) {
  return {
    listUsers: jest.fn(() => of(users)),
    listRoles: jest.fn(() => of(roles)),
    saveUser: jest.fn(() => of(users[0] ?? user())),
    deactivateUser: jest.fn(() => of(void 0))
  };
}

function comprobantesService(comprobantes: Comprobante[]) {
  return {
    list: jest.fn(() => of(comprobantes)),
    getById: jest.fn(() => of(comprobantes[0] ?? comprobante())),
    createDraft: jest.fn(() => of(comprobantes[0] ?? comprobante())),
    emitirFactura: jest.fn(() => of(comprobantes[0] ?? comprobante()))
  };
}

function facturaProviders() {
  return [
    provideRouter([]),
    { provide: EmpresaService, useValue: empresaService(empresa(), [establecimiento()]) },
    { provide: ClientesService, useValue: clientesService([cliente()]) },
    { provide: ProductosService, useValue: productosService([producto()]) },
    { provide: ComprobantesService, useValue: comprobantesService([comprobante()]) }
  ];
}

function activatedRoute(id: string | null) {
  return { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: jest.fn(() => id) } } } };
}

function user(overrides: Partial<User> = {}): User {
  return {
    id: 'user-1',
    name: 'Admin User',
    email: 'admin@factuec.local',
    username: 'admin',
    role: role(),
    status: 'ACTIVO',
    ...overrides
  };
}

function role(overrides: Partial<Role> = {}): Role {
  return { id: 'role-1', name: 'Administrador', description: 'Admin', permissions: ['DASHBOARD'], ...overrides };
}

function empresa(overrides: Partial<Empresa> = {}): Empresa {
  return {
    id: 'empresa-1',
    ruc: '0999999999001',
    razonSocial: 'Empresa Test',
    nombreComercial: 'Empresa Test',
    direccionMatriz: 'Direccion',
    obligadoContabilidad: false,
    ambiente: 'PRUEBAS',
    tipoEmision: 'NORMAL',
    ...overrides
  };
}

function establecimiento(overrides: Partial<Establecimiento> = {}): Establecimiento {
  return {
    id: 'establecimiento-1',
    codigo: '001',
    nombre: 'Matriz',
    direccion: 'Direccion',
    activo: true,
    puntosEmision: [{ id: 'punto-1', codigo: '001', nombre: 'Punto 001', secuencialFactura: 0, activo: true }],
    ...overrides
  };
}

function cliente(overrides: Partial<Cliente> = {}): Cliente {
  return {
    id: 'cliente-1',
    tipoIdentificacion: 'RUC',
    identificacion: '0999999999001',
    razonSocial: 'Cliente Test',
    email: 'cliente@test.local',
    telefono: '',
    direccion: '',
    estado: 'ACTIVO',
    totalVentasMes: 0,
    ...overrides
  };
}

function producto(overrides: Partial<Producto> = {}): Producto {
  return {
    id: 'producto-1',
    codigo: 'SERV-001',
    nombre: 'Servicio Test',
    categoria: 'Servicios',
    tipo: 'SERVICIO',
    precioUnitario: 10,
    tarifaIva: '15%',
    stock: 0,
    estado: 'ACTIVO',
    ...overrides
  };
}

function comprobante(overrides: Partial<Comprobante> = {}): Comprobante {
  return {
    id: 'comprobante-1',
    tipo: 'FACTURA',
    numero: '001-001-000000001',
    fechaEmision: today(),
    cliente: cliente(),
    identificacion: '0999999999001',
    subtotal: 10,
    iva: 1.5,
    total: 11.5,
    estado: 'BORRADOR',
    ambiente: 'PRUEBAS',
    detalles: [],
    pagos: [],
    mensajesSri: [],
    ...overrides
  };
}

function today(): string {
  const date = new Date();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${date.getFullYear()}-${month}-${day}`;
}
