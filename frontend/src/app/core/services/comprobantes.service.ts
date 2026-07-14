import { Injectable, inject } from '@angular/core';
import { Observable, forkJoin, map } from 'rxjs';

import { Comprobante, EntityId } from '../models/factuec.models';
import { ApiService } from './api.service';
import { ComprobanteResponseDto } from './backend-api.models';
import { mapComprobante } from './backend-mappers';
import { ClientesService } from './clientes.service';

@Injectable({
  providedIn: 'root'
})
export class ComprobantesService {
  private readonly apiService = inject(ApiService);
  private readonly clientesService = inject(ClientesService);

  list(): Observable<Comprobante[]> {
    return forkJoin({
      comprobantes: this.apiService.get<ComprobanteResponseDto[]>('/comprobantes'),
      clientes: this.clientesService.list()
    }).pipe(map(({ comprobantes, clientes }) => comprobantes.map((comprobante) => mapComprobante(comprobante, clientes))));
  }

  getById(id: EntityId): Observable<Comprobante | undefined> {
    return forkJoin({
      comprobante: this.apiService.get<ComprobanteResponseDto>(`/comprobantes/${id}`),
      clientes: this.clientesService.list()
    }).pipe(map(({ comprobante, clientes }) => mapComprobante(comprobante, clientes)));
  }

  createDraft(payload: unknown): Observable<Comprobante> {
    return forkJoin({
      comprobante: this.apiService.post<ComprobanteResponseDto>('/comprobantes/facturas/borrador', payload),
      clientes: this.clientesService.list()
    }).pipe(map(({ comprobante, clientes }) => mapComprobante(comprobante, clientes)));
  }

  emitirFactura(payload: unknown): Observable<Comprobante> {
    return forkJoin({
      comprobante: this.apiService.post<ComprobanteResponseDto>('/comprobantes/facturas/emitir', payload),
      clientes: this.clientesService.list()
    }).pipe(map(({ comprobante, clientes }) => mapComprobante(comprobante, clientes)));
  }

  downloadRide(id: EntityId): Observable<Blob> {
    return this.apiService.getBlob(`/comprobantes/${id}/ride`);
  }

  downloadXml(id: EntityId): Observable<Blob> {
    return this.apiService.getBlob(`/comprobantes/${id}/xml`);
  }
}
