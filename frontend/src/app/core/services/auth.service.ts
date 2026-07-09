import { Injectable, computed, signal } from '@angular/core';
import { Observable, delay, of, tap } from 'rxjs';

import { User } from '../models/factuec.models';
import { CURRENT_USER } from './mock-data';

export interface LoginCredentials {
  username: string;
  password: string;
  remember: boolean;
  environment: 'PRUEBAS' | 'PRODUCCION';
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly tokenKey = 'factuec_access_token';
  private readonly userKey = 'factuec_user';
  private readonly tokenSignal = signal<string | null>(localStorage.getItem(this.tokenKey));
  private readonly userSignal = signal<User | null>(this.readUser());

  readonly token = computed(() => this.tokenSignal());
  readonly user = computed(() => this.userSignal());
  readonly isLoggedIn = computed(() => Boolean(this.tokenSignal() && this.userSignal()));

  login(credentials: LoginCredentials): Observable<User> {
    const token = `mock-jwt-${credentials.environment.toLowerCase()}-${Date.now()}`;

    return of({ ...CURRENT_USER }).pipe(
      delay(350),
      tap((user) => {
        localStorage.setItem(this.tokenKey, token);
        localStorage.setItem(this.userKey, JSON.stringify(user));
        this.tokenSignal.set(token);
        this.userSignal.set(user);
      })
    );
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userKey);
    this.tokenSignal.set(null);
    this.userSignal.set(null);
  }

  private readUser(): User | null {
    const raw = localStorage.getItem(this.userKey);

    if (!raw) {
      return null;
    }

    try {
      return JSON.parse(raw) as User;
    } catch {
      localStorage.removeItem(this.userKey);
      return null;
    }
  }
}
