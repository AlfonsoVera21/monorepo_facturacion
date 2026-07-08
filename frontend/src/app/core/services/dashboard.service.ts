import { Injectable } from '@angular/core';
import { Observable, delay, of } from 'rxjs';

import { Comprobante, DashboardMetric } from '../models/factuec.models';
import { COMPROBANTES, DASHBOARD_METRICS, FIRMA } from './mock-data';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  getMetrics(): Observable<DashboardMetric[]> {
    return of(DASHBOARD_METRICS).pipe(delay(120));
  }

  getLatestComprobantes(): Observable<Comprobante[]> {
    return of(COMPROBANTES.slice(0, 5)).pipe(delay(120));
  }

  getSignatureHealth(): Observable<typeof FIRMA> {
    return of(FIRMA).pipe(delay(120));
  }
}
