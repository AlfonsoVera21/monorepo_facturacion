import { Injectable } from '@angular/core';
import { Observable, delay, map, of } from 'rxjs';

import { Comprobante } from '../models/factuec.models';
import { COMPROBANTES } from './mock-data';

@Injectable({
  providedIn: 'root'
})
export class ComprobantesService {
  list(): Observable<Comprobante[]> {
    return of(COMPROBANTES).pipe(delay(160));
  }

  getById(id: number): Observable<Comprobante | undefined> {
    return this.list().pipe(map((items) => items.find((item) => item.id === id)));
  }

  createDraft(payload: Partial<Comprobante>): Observable<Comprobante> {
    const draft: Comprobante = { ...COMPROBANTES[0], ...payload, id: Date.now(), estado: 'BORRADOR' };
    return of(draft).pipe(delay(250));
  }
}
