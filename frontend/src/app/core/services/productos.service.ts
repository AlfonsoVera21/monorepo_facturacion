import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';

import { EntityId, Producto, TipoProducto } from '../models/factuec.models';
import { ApiService } from './api.service';
import { ProductoResponseDto } from './backend-api.models';
import { mapProducto } from './backend-mappers';

export type TarifaIvaBackend = ProductoResponseDto['tarifaIva'];

export interface ProductoPayload {
  empresaId: EntityId;
  codigoPrincipal: string;
  codigoAuxiliar?: string;
  nombre: string;
  descripcion?: string;
  tipo: TipoProducto;
  precioUnitario: number;
  tarifaIva: TarifaIvaBackend;
  icePorcentaje?: number;
  stock?: number;
  categoria?: string;
  activo: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ProductosService {
  private readonly apiService = inject(ApiService);

  list(): Observable<Producto[]> {
    return this.apiService.get<ProductoResponseDto[]>('/productos').pipe(map((productos) => productos.map(mapProducto)));
  }

  create(payload: ProductoPayload): Observable<Producto> {
    return this.apiService.post<ProductoResponseDto>('/productos', payload).pipe(map(mapProducto));
  }

  update(id: EntityId, payload: ProductoPayload): Observable<Producto> {
    return this.apiService.put<ProductoResponseDto>(`/productos/${id}`, payload).pipe(map(mapProducto));
  }

  save(id: EntityId | null, payload: ProductoPayload): Observable<Producto> {
    return id ? this.update(id, payload) : this.create(payload);
  }

  delete(id: EntityId): Observable<void> {
    return this.apiService.delete<void>(`/productos/${id}`);
  }
}
