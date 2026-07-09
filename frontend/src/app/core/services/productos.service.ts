import { Injectable } from '@angular/core';
import { Observable, delay, of } from 'rxjs';

import { Producto } from '../models/factuec.models';
import { PRODUCTOS } from './mock-data';

@Injectable({
  providedIn: 'root'
})
export class ProductosService {
  list(): Observable<Producto[]> {
    return of(PRODUCTOS).pipe(delay(140));
  }
}
