import { Injectable } from '@angular/core';
import { Observable, delay, of } from 'rxjs';

import { SriMensaje } from '../models/factuec.models';
import { SRI_MENSAJES } from './mock-data';

export interface SriStatus {
  uptime: string;
  online: boolean;
  pendingQueue: number;
  authorizedToday: number;
  errorsToday: number;
}

@Injectable({
  providedIn: 'root'
})
export class SriService {
  getStatus(): Observable<SriStatus> {
    return of({
      uptime: '99.8%',
      online: true,
      pendingQueue: 8,
      authorizedToday: 1248,
      errorsToday: 14
    }).pipe(delay(120));
  }

  getMensajes(): Observable<SriMensaje[]> {
    return of(SRI_MENSAJES).pipe(delay(160));
  }
}
