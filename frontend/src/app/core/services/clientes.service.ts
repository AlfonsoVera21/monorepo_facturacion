import { Injectable } from '@angular/core';
import { Observable, delay, of } from 'rxjs';

import { Cliente } from '../models/factuec.models';
import { CLIENTES } from './mock-data';

@Injectable({
  providedIn: 'root'
})
export class ClientesService {
  list(): Observable<Cliente[]> {
    return of(CLIENTES).pipe(delay(140));
  }
}
