import { Injectable, inject } from '@angular/core';
import { Observable, map, of, switchMap } from 'rxjs';

import { AuditLog, EntityId, FirmaElectronica } from '../models/factuec.models';
import { ApiService } from './api.service';
import { AuditLogResponseDto, CertificadoDisponibleResponseDto, FirmaElectronicaResponseDto } from './backend-api.models';
import { mapFirma } from './backend-mappers';
import { EmpresaService } from './empresa.service';

export type CertificadoDisponible = CertificadoDisponibleResponseDto;

export interface FirmaElectronicaPayload {
  empresaId: EntityId;
  nombreArchivo: string;
  rutaSegura: string;
  passwordSecretRef: string;
  fechaEmision?: string;
  fechaVencimiento?: string;
  estado: 'ACTIVA' | 'VENCIDA' | 'REVOCADA' | 'INACTIVA';
}

@Injectable({
  providedIn: 'root'
})
export class FirmaElectronicaService {
  private readonly apiService = inject(ApiService);
  private readonly empresaService = inject(EmpresaService);

  getCurrent(): Observable<FirmaElectronica | undefined> {
    return this.empresaService.getEmpresa().pipe(
      switchMap((empresa) => {
        if (!empresa) {
          return of([]);
        }
        return this.apiService.get<FirmaElectronicaResponseDto[]>(`/firmas/empresa/${empresa.id}`);
      }),
      map((firmas) => firmas.map(mapFirma)[0])
    );
  }

  listCertificados(): Observable<CertificadoDisponible[]> {
    return this.apiService.get<CertificadoDisponibleResponseDto[]>('/firmas/certificados');
  }

  saveFirma(id: EntityId | undefined, payload: FirmaElectronicaPayload): Observable<FirmaElectronica> {
    const request$ = id
      ? this.apiService.put<FirmaElectronicaResponseDto>(`/firmas/${id}`, payload)
      : this.apiService.post<FirmaElectronicaResponseDto>('/firmas', payload);
    return request$.pipe(map(mapFirma));
  }

  getAuditTrail(): Observable<AuditLog[]> {
    return this.apiService.get<AuditLogResponseDto[]>('/audit-logs?entityType=FirmaElectronica').pipe(
      map((logs) => logs.map((log) => ({
        id: log.id,
        fecha: log.fecha,
        usuario: log.usuario,
        accion: log.accion,
        entidad: log.entidad,
        descripcion: log.descripcion,
        ip: log.ip
      })))
    );
  }
}
