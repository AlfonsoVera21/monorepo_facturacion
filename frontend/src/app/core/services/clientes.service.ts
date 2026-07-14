import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';

import { Cliente, EntityId, TipoIdentificacion } from '../models/factuec.models';
import { ApiService } from './api.service';
import { ClienteResponseDto } from './backend-api.models';
import { mapCliente } from './backend-mappers';

export interface ClientePayload {
  empresaId: EntityId;
  tipoIdentificacion: TipoIdentificacion;
  identificacion: string;
  razonSocial: string;
  nombreComercial?: string;
  correo?: string;
  telefono?: string;
  direccion?: string;
  ciudad?: string;
  provincia?: string;
  activo: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ClientesService {
  private readonly apiService = inject(ApiService);

  list(): Observable<Cliente[]> {
    return this.apiService.get<ClienteResponseDto[]>('/clientes').pipe(map((clientes) => clientes.map(mapCliente)));
  }

  create(payload: ClientePayload): Observable<Cliente> {
    return this.apiService.post<ClienteResponseDto>('/clientes', payload).pipe(map(mapCliente));
  }

  update(id: EntityId, payload: ClientePayload): Observable<Cliente> {
    return this.apiService.put<ClienteResponseDto>(`/clientes/${id}`, payload).pipe(map(mapCliente));
  }

  save(id: EntityId | null, payload: ClientePayload): Observable<Cliente> {
    return id ? this.update(id, payload) : this.create(payload);
  }

  delete(id: EntityId): Observable<void> {
    return this.apiService.delete<void>(`/clientes/${id}`);
  }
}
