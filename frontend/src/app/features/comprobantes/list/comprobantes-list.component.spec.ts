import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { Comprobante } from '../../../core/models/factuec.models';
import { ComprobantesService } from '../../../core/services/comprobantes.service';
import { ComprobantesListComponent } from './comprobantes-list.component';

describe('ComprobantesListComponent', () => {
  it('calcula metricas desde comprobantes reales entregados por el servicio', async () => {
    const today = isoDate(new Date());
    const comprobantes = [
      comprobante({ id: '1', fechaEmision: today, estado: 'AUTORIZADO', total: 10 }),
      comprobante({ id: '2', fechaEmision: today, estado: 'RECHAZADO', total: 5.5 })
    ];
    const service = { list: jest.fn(() => of(comprobantes)) };

    await TestBed.configureTestingModule({
      imports: [ComprobantesListComponent],
      providers: [
        provideRouter([]),
        { provide: ComprobantesService, useValue: service }
      ]
    }).compileComponents();

    const fixture = TestBed.createComponent(ComprobantesListComponent);
    fixture.detectChanges();
    const component = fixture.componentInstance as unknown as {
      autorizadosHoy: () => string;
      rechazados: () => string;
      totalDocumentos: () => string;
      totalItems: () => number;
      ventasMes: () => string;
    };

    expect(service.list).toHaveBeenCalledTimes(1);
    expect(component.totalItems()).toBe(2);
    expect(component.autorizadosHoy()).toBe('1');
    expect(component.rechazados()).toBe('1');
    expect(component.totalDocumentos()).toBe('2');
    expect(component.ventasMes()).toBe('$15.50');
  });
});

function comprobante(overrides: Partial<Comprobante>): Comprobante {
  return {
    id: 'comprobante',
    tipo: 'FACTURA',
    numero: '001-001-000000001',
    fechaEmision: isoDate(new Date()),
    cliente: {
      id: 'cliente',
      tipoIdentificacion: 'RUC',
      identificacion: '0999999999001',
      razonSocial: 'Cliente Test',
      email: 'cliente@test.local',
      telefono: '',
      direccion: '',
      estado: 'ACTIVO',
      totalVentasMes: 0
    },
    identificacion: '0999999999001',
    subtotal: 0,
    iva: 0,
    total: 0,
    estado: 'BORRADOR',
    ambiente: 'PRUEBAS',
    detalles: [],
    pagos: [],
    mensajesSri: [],
    ...overrides
  };
}

function isoDate(date: Date): string {
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${date.getFullYear()}-${month}-${day}`;
}
