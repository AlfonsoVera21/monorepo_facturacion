import { Injectable } from '@angular/core';
import { Observable, delay, of } from 'rxjs';

import { Role, User } from '../models/factuec.models';
import { ROLES, USERS } from './mock-data';

@Injectable({
  providedIn: 'root'
})
export class UsuariosService {
  listUsers(): Observable<User[]> {
    return of(USERS).pipe(delay(120));
  }

  listRoles(): Observable<Role[]> {
    return of(ROLES).pipe(delay(120));
  }
}
