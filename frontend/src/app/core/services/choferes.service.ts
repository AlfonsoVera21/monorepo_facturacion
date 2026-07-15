import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';

import { Chofer, EntityId, TipoIdentificacion, UnidadMedidaInventario } from '../models/factuec.models';
import { ApiService } from './api.service';
import { ChoferResponseDto } from './backend-api.models';
import { mapChofer } from './backend-mappers';

export interface ChoferPayload {
  empresaId: EntityId;
  tipoIdentificacion: TipoIdentificacion;
  identificacion: string;
  nombres: string;
  apellidos?: string;
  licencia: string;
  telefono?: string;
  correo?: string;
  placaVehiculo?: string;
  tipoVehiculo?: string;
  capacidad?: number;
  unidadCapacidad?: UnidadMedidaInventario;
  transportaRefrigerado: boolean;
  activo: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ChoferesService {
  private readonly apiService = inject(ApiService);

  list(): Observable<Chofer[]> {
    return this.apiService.get<ChoferResponseDto[]>('/choferes').pipe(map((choferes) => choferes.map(mapChofer)));
  }

  create(payload: ChoferPayload): Observable<Chofer> {
    return this.apiService.post<ChoferResponseDto>('/choferes', payload).pipe(map(mapChofer));
  }

  update(id: EntityId, payload: ChoferPayload): Observable<Chofer> {
    return this.apiService.put<ChoferResponseDto>(`/choferes/${id}`, payload).pipe(map(mapChofer));
  }

  save(id: EntityId | null, payload: ChoferPayload): Observable<Chofer> {
    return id ? this.update(id, payload) : this.create(payload);
  }

  delete(id: EntityId): Observable<void> {
    return this.apiService.delete<void>(`/choferes/${id}`);
  }
}
