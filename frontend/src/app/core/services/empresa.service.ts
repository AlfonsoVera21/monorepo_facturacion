import { Injectable } from '@angular/core';
import { Observable, delay, of } from 'rxjs';

import { Empresa, Establecimiento } from '../models/factuec.models';
import { EMPRESA, ESTABLECIMIENTOS } from './mock-data';

@Injectable({
  providedIn: 'root'
})
export class EmpresaService {
  getEmpresa(): Observable<Empresa> {
    return of(EMPRESA).pipe(delay(120));
  }

  getEstablecimientos(): Observable<Establecimiento[]> {
    return of(ESTABLECIMIENTOS).pipe(delay(120));
  }
}
