import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';

import { Comprobante, DashboardMetric } from '../models/factuec.models';
import { ComprobantesService } from './comprobantes.service';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private readonly comprobantesService = inject(ComprobantesService);

  getMetrics(): Observable<DashboardMetric[]> {
    return this.comprobantesService.list().pipe(
      map((comprobantes) => {
        const totalMes = comprobantes.reduce((total, item) => total + item.total, 0);
        const autorizados = comprobantes.filter((item) => item.estado === 'AUTORIZADO').length;
        const pendientes = comprobantes.filter((item) => ['BORRADOR', 'GENERADO', 'FIRMADO', 'ENVIADO'].includes(item.estado)).length;
        const rechazados = comprobantes.filter((item) => ['RECHAZADO', 'DEVUELTO', 'ERROR'].includes(item.estado)).length;

        return [
          { title: 'Total Facturado Mes', value: `$${totalMes.toFixed(2)}`, icon: 'payments', trend: '', tone: 'primary' },
          { title: 'Autorizados', value: autorizados.toString(), icon: 'verified', tone: 'success' },
          { title: 'Pendientes', value: pendientes.toString(), icon: 'pending_actions', tone: 'warning' },
          { title: 'Rechazados', value: rechazados.toString(), icon: 'dangerous', tone: 'danger' }
        ] satisfies DashboardMetric[];
      })
    );
  }

  getLatestComprobantes(): Observable<Comprobante[]> {
    return this.comprobantesService.list().pipe(map((comprobantes) => comprobantes.slice(0, 5)));
  }
}
