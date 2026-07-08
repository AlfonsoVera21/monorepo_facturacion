import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';

import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly loading = signal(false);
  protected readonly selectedEnvironment = signal<'PRUEBAS' | 'PRODUCCION'>('PRUEBAS');

  protected readonly loginForm = this.formBuilder.nonNullable.group({
    username: ['admin@factuec.local', [Validators.required]],
    password: ['factuec-demo', [Validators.required, Validators.minLength(6)]],
    remember: [true]
  });

  protected setEnvironment(environment: 'PRUEBAS' | 'PRODUCCION'): void {
    this.selectedEnvironment.set(environment);
  }

  protected submit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.authService
      .login({
        ...this.loginForm.getRawValue(),
        environment: this.selectedEnvironment()
      })
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe(() => void this.router.navigate(['/dashboard']));
  }
}
