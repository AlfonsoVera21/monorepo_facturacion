import { Injectable, computed, inject, signal } from '@angular/core';
import { Observable, map, switchMap, tap } from 'rxjs';

import { User } from '../models/factuec.models';
import { ApiService } from './api.service';
import { AuthResponseDto, UserMeResponseDto } from './backend-api.models';
import { mapUser } from './backend-mappers';

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
  private readonly refreshTokenKey = 'factuec_refresh_token';
  private readonly userKey = 'factuec_user';
  private readonly apiService = inject(ApiService);
  private readonly tokenSignal = signal<string | null>(localStorage.getItem(this.tokenKey));
  private readonly userSignal = signal<User | null>(this.readUser());

  readonly token = computed(() => this.tokenSignal());
  readonly user = computed(() => this.userSignal());
  readonly isLoggedIn = computed(() => Boolean(this.tokenSignal() && this.userSignal()));

  login(credentials: LoginCredentials): Observable<User> {
    return this.apiService.post<AuthResponseDto>('/auth/login', {
      username: credentials.username,
      password: credentials.password
    }).pipe(
      tap((auth) => {
        localStorage.setItem(this.tokenKey, auth.accessToken);
        localStorage.setItem(this.refreshTokenKey, auth.refreshToken);
        this.tokenSignal.set(auth.accessToken);
      }),
      switchMap(() => this.loadCurrentUser())
    );
  }

  loadCurrentUser(): Observable<User> {
    return this.apiService.get<UserMeResponseDto>('/auth/me').pipe(
      map((response) => {
        const user = mapUser(response);
        localStorage.setItem(this.userKey, JSON.stringify(user));
        this.userSignal.set(user);
        return user;
      })
    );
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.refreshTokenKey);
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
