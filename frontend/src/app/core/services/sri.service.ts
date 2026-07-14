import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';

import { SriMensaje } from '../models/factuec.models';
import { ApiService } from './api.service';
import { SriEstadoResponseDto } from './backend-api.models';

export interface SriStatus {
  ambienteDefault: string;
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
  private readonly apiService = inject(ApiService);

  getStatus(): Observable<SriStatus> {
    return this.apiService.get<SriEstadoResponseDto>('/sri/estado').pipe(
      map((estado) => ({
        ambienteDefault: estado.ambienteDefault,
        uptime: estado.mockEnabled ? 'MOCK' : 'LIVE',
        online: true,
        pendingQueue: 0,
        authorizedToday: 0,
        errorsToday: 0
      }))
    );
  }

  getMensajes(): Observable<SriMensaje[]> {
    return this.apiService.get<string[]>('/sri/errores').pipe(
      map((errores) =>
        errores.map((mensaje, index) => ({
          id: `sri-error-${index}`,
          fecha: new Date().toISOString().slice(0, 19).replace('T', ' '),
          comprobanteNumero: 'N/A',
          codigo: `SRI-${index + 1}`,
          mensaje,
          informacionAdicional: 'Mensaje de referencia retornado por el backend.',
          estado: 'ERROR'
        }))
      )
    );
  }
}
