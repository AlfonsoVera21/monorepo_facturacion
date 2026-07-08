import { Injectable } from '@angular/core';
import { Observable, delay, of } from 'rxjs';

import { AuditLog, FirmaElectronica } from '../models/factuec.models';
import { AUDIT_LOGS, FIRMA } from './mock-data';

@Injectable({
  providedIn: 'root'
})
export class FirmaElectronicaService {
  getCurrent(): Observable<FirmaElectronica> {
    return of(FIRMA).pipe(delay(120));
  }

  getAuditTrail(): Observable<AuditLog[]> {
    return of(AUDIT_LOGS.filter((log) => log.entidad === 'FirmaElectronica')).pipe(delay(120));
  }
}
