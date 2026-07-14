import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';

import { EntityId, Role, User } from '../../core/models/factuec.models';
import { UserPayload, UsuariosService } from '../../core/services/usuarios.service';
import { MetricCardComponent } from '../../shared/components/metric-card/metric-card.component';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

type FeedbackTone = 'success' | 'danger' | 'warning';

@Component({
  selector: 'app-usuarios',
  imports: [ReactiveFormsModule, PageHeaderComponent, MetricCardComponent, StatusBadgeComponent],
  templateUrl: './usuarios.component.html',
  styleUrl: './usuarios.component.scss'
})
export class UsuariosComponent implements OnInit {
  private readonly usuariosService = inject(UsuariosService);
  private readonly formBuilder = inject(FormBuilder);

  protected readonly users = signal<User[]>([]);
  protected readonly roles = signal<Role[]>([]);
  protected readonly showForm = signal(false);
  protected readonly editingId = signal<EntityId | null>(null);
  protected readonly saving = signal(false);
  protected readonly feedback = signal<{ tone: FeedbackTone; message: string } | null>(null);
  protected readonly activeUsers = computed(() => this.users().filter((user) => user.status === 'ACTIVO').length.toString());
  protected readonly roleCount = computed(() => this.roles().length.toString());
  protected readonly blockedUsers = computed(() => this.users().filter((user) => user.status === 'BLOQUEADO').length.toString());

  protected readonly userForm = this.formBuilder.nonNullable.group({
    username: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    fullName: ['', Validators.required],
    password: [''],
    role: ['ADMIN', Validators.required],
    active: [true]
  });

  ngOnInit(): void {
    this.load();
  }

  protected newUser(): void {
    this.editingId.set(null);
    this.userForm.reset({
      username: '',
      email: '',
      fullName: '',
      password: '',
      role: 'ADMIN',
      active: true
    });
    this.showForm.set(true);
  }

  protected editUser(user: User): void {
    this.editingId.set(user.id);
    this.userForm.reset({
      username: user.username,
      email: user.email,
      fullName: user.name,
      password: '',
      role: this.toBackendRole(user.role.name),
      active: user.status === 'ACTIVO'
    });
    this.showForm.set(true);
  }

  protected cancelForm(): void {
    this.showForm.set(false);
    this.editingId.set(null);
  }

  protected saveUser(): void {
    if (this.userForm.invalid) {
      this.userForm.markAllAsTouched();
      this.feedback.set({ tone: 'danger', message: 'Completa nombre, username, correo y rol.' });
      return;
    }
    if (!this.editingId() && !this.userForm.controls.password.value) {
      this.feedback.set({ tone: 'danger', message: 'La contrasena es requerida para crear usuario.' });
      return;
    }

    this.saving.set(true);
    this.feedback.set(null);
    this.usuariosService.saveUser(this.editingId(), this.toPayload()).pipe(
      finalize(() => this.saving.set(false))
    ).subscribe({
      next: () => {
        this.feedback.set({ tone: 'success', message: 'Usuario guardado desde backend.' });
        this.showForm.set(false);
        this.editingId.set(null);
        this.load();
      },
      error: () => this.feedback.set({ tone: 'danger', message: 'No se pudo guardar el usuario. Revisa username, correo y contrasena.' })
    });
  }

  protected deactivateUser(user: User): void {
    this.usuariosService.deactivateUser(user.id).subscribe({
      next: () => {
        this.feedback.set({ tone: 'success', message: 'Usuario inactivado.' });
        this.load();
      },
      error: () => this.feedback.set({ tone: 'danger', message: 'No se pudo inactivar el usuario.' })
    });
  }

  private load(): void {
    this.usuariosService.listUsers().subscribe((users) => this.users.set(users));
    this.usuariosService.listRoles().subscribe((roles) => this.roles.set(roles));
  }

  private toPayload(): UserPayload {
    const value = this.userForm.getRawValue();
    return {
      username: value.username,
      email: value.email,
      fullName: value.fullName,
      password: value.password || undefined,
      active: value.active,
      roles: [value.role]
    };
  }

  private toBackendRole(roleName: string): string {
    const map: Record<string, string> = {
      Administrador: 'ADMIN',
      Emisor: 'EMISOR',
      Contabilidad: 'CONTABILIDAD',
      Consulta: 'CONSULTA',
      Soporte: 'SOPORTE'
    };
    return map[roleName] || roleName.toUpperCase();
  }
}
