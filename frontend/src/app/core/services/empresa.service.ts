import { Injectable, inject } from '@angular/core';
import { Observable, forkJoin, map, of, switchMap } from 'rxjs';

import { AmbienteSri, Empresa, Establecimiento, EntityId, PuntoEmision } from '../models/factuec.models';
import { ApiService } from './api.service';
import { EmpresaResponseDto, EstablecimientoResponseDto, PuntoEmisionResponseDto } from './backend-api.models';
import { mapEmpresa, mapEstablecimiento, mapPuntoEmision } from './backend-mappers';

export interface EmpresaPayload {
  ruc: string;
  razonSocial: string;
  nombreComercial: string;
  direccionMatriz: string;
  obligadoContabilidad: boolean;
  ambiente: AmbienteSri;
  activo: boolean;
}

export interface EstablecimientoPayload {
  empresaId: EntityId;
  codigo: string;
  nombre: string;
  direccion: string;
  activo: boolean;
}

export interface PuntoEmisionPayload {
  establecimientoId: EntityId;
  codigo: string;
  nombre: string;
  activo: boolean;
  ultimoSecuencial: number;
}

@Injectable({
  providedIn: 'root'
})
export class EmpresaService {
  private readonly apiService = inject(ApiService);

  getEmpresas(): Observable<Empresa[]> {
    return this.apiService.get<EmpresaResponseDto[]>('/empresas').pipe(map((empresas) => empresas.map(mapEmpresa)));
  }

  getEmpresa(): Observable<Empresa | null> {
    return this.getEmpresas().pipe(map((empresas) => empresas[0] || null));
  }

  createEmpresa(payload: EmpresaPayload): Observable<Empresa> {
    return this.apiService.post<EmpresaResponseDto>('/empresas', payload).pipe(map(mapEmpresa));
  }

  updateEmpresa(id: EntityId, payload: EmpresaPayload): Observable<Empresa> {
    return this.apiService.put<EmpresaResponseDto>(`/empresas/${id}`, payload).pipe(map(mapEmpresa));
  }

  saveEmpresa(id: EntityId | null, payload: EmpresaPayload): Observable<Empresa> {
    return id ? this.updateEmpresa(id, payload) : this.createEmpresa(payload);
  }

  getEstablecimientos(empresaId?: EntityId): Observable<Establecimiento[]> {
    const source = empresaId
      ? this.apiService.get<EstablecimientoResponseDto[]>(`/establecimientos?empresaId=${empresaId}`)
      : this.getEmpresa().pipe(
          switchMap((empresa) => {
            if (!empresa) {
              return of([]);
            }
            return this.apiService.get<EstablecimientoResponseDto[]>(`/establecimientos?empresaId=${empresa.id}`);
          })
        );

    return source.pipe(
      switchMap((establecimientos) => {
        if (!establecimientos.length) {
          return of([]);
        }
        return forkJoin(
          establecimientos.map((establecimiento) =>
            this.apiService.get<PuntoEmisionResponseDto[]>(`/puntos-emision?establecimientoId=${establecimiento.id}`).pipe(
              map((puntos) => mapEstablecimiento(establecimiento, puntos.map(mapPuntoEmision)))
            )
          )
        );
      })
    );
  }

  createEstablecimiento(payload: EstablecimientoPayload): Observable<Establecimiento> {
    return this.apiService.post<EstablecimientoResponseDto>('/establecimientos', payload).pipe(
      map((establecimiento) => mapEstablecimiento(establecimiento, []))
    );
  }

  updateEstablecimiento(id: EntityId, payload: EstablecimientoPayload): Observable<Establecimiento> {
    return this.apiService.put<EstablecimientoResponseDto>(`/establecimientos/${id}`, payload).pipe(
      map((establecimiento) => mapEstablecimiento(establecimiento, []))
    );
  }

  saveEstablecimiento(id: EntityId | null, payload: EstablecimientoPayload): Observable<Establecimiento> {
    return id ? this.updateEstablecimiento(id, payload) : this.createEstablecimiento(payload);
  }

  createPuntoEmision(empresaId: EntityId, payload: PuntoEmisionPayload): Observable<PuntoEmision> {
    return this.apiService.post<PuntoEmisionResponseDto>('/puntos-emision', {
      establecimientoId: payload.establecimientoId,
      codigo: payload.codigo,
      nombre: payload.nombre,
      activo: payload.activo
    }).pipe(
      switchMap((punto) =>
        this.apiService.post('/secuenciales', {
          empresaId,
          establecimientoId: payload.establecimientoId,
          puntoEmisionId: punto.id,
          tipoComprobante: 'FACTURA',
          ultimoSecuencial: payload.ultimoSecuencial
        }).pipe(map(() => mapPuntoEmision(punto)))
      )
    );
  }
}
