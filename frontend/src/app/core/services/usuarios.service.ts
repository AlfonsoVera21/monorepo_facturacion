import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';

import { EntityId, Role, User } from '../models/factuec.models';
import { ApiService } from './api.service';
import { RoleResponseDto, UserResponseDto } from './backend-api.models';

export interface UserPayload {
  username: string;
  email: string;
  fullName: string;
  password?: string;
  active: boolean;
  roles: string[];
}

@Injectable({
  providedIn: 'root'
})
export class UsuariosService {
  private readonly apiService = inject(ApiService);

  listUsers(): Observable<User[]> {
    return this.apiService.get<UserResponseDto[]>('/users').pipe(
      map((users) => users.map((user) => this.mapUser(user)))
    );
  }

  listRoles(): Observable<Role[]> {
    return this.apiService.get<RoleResponseDto[]>('/roles').pipe(
      map((roles) => roles.map((role) => this.mapRole(role)))
    );
  }

  createUser(payload: UserPayload): Observable<User> {
    return this.apiService.post<UserResponseDto>('/users', payload).pipe(map((user) => this.mapUser(user)));
  }

  updateUser(id: EntityId, payload: UserPayload): Observable<User> {
    return this.apiService.put<UserResponseDto>(`/users/${id}`, payload).pipe(map((user) => this.mapUser(user)));
  }

  saveUser(id: EntityId | null, payload: UserPayload): Observable<User> {
    return id ? this.updateUser(id, payload) : this.createUser(payload);
  }

  deactivateUser(id: EntityId): Observable<void> {
    return this.apiService.delete<void>(`/users/${id}`);
  }

  private mapUser(dto: UserResponseDto): User {
    const role = this.mapRole(dto.roles[0] || {
      id: 'NO_ROLE',
      name: 'SIN_ROL',
      description: 'Sin rol asignado',
      permissions: []
    });
    return {
      id: dto.id,
      name: dto.fullName,
      email: dto.email,
      username: dto.username,
      role,
      status: dto.active ? 'ACTIVO' : 'INACTIVO'
    };
  }

  private mapRole(dto: RoleResponseDto): Role {
    return {
      id: dto.id,
      name: this.formatRoleName(dto.name),
      description: dto.description || 'Rol configurado en backend',
      permissions: dto.permissions || []
    };
  }

  private formatRoleName(name: string): string {
    const map: Record<string, string> = {
      ADMIN: 'Administrador',
      EMISOR: 'Emisor',
      CONTABILIDAD: 'Contabilidad',
      CONSULTA: 'Consulta',
      SOPORTE: 'Soporte'
    };
    return map[name] || name.replaceAll('_', ' ');
  }
}
