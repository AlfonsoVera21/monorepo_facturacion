import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { environment } from '../../../environments/environment';

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;

  get<T>(path: string): Observable<T> {
    return this.http.get<ApiResponse<T>>(this.buildUrl(path)).pipe(map((response) => this.unwrap(response)));
  }

  getBlob(path: string): Observable<Blob> {
    return this.http.get(this.buildUrl(path), { responseType: 'blob' });
  }

  post<T>(path: string, body: unknown): Observable<T> {
    return this.http.post<ApiResponse<T>>(this.buildUrl(path), body).pipe(map((response) => this.unwrap(response)));
  }

  put<T>(path: string, body: unknown): Observable<T> {
    return this.http.put<ApiResponse<T>>(this.buildUrl(path), body).pipe(map((response) => this.unwrap(response)));
  }

  delete<T>(path: string): Observable<T> {
    return this.http.delete<ApiResponse<T>>(this.buildUrl(path)).pipe(map((response) => this.unwrap(response)));
  }

  private buildUrl(path: string): string {
    return `${this.apiUrl}${path.startsWith('/') ? path : `/${path}`}`;
  }

  private unwrap<T>(response: ApiResponse<T>): T {
    if (!response.success) {
      throw new Error(response.message || 'Error al consumir API FactuEC');
    }
    return response.data;
  }
}
