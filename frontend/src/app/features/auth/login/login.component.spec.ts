import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';

import { AuthService } from '../../../core/services/auth.service';
import { LoginComponent } from './login.component';

describe('LoginComponent', () => {
  it('alterna la visibilidad de la contrasena desde el boton del ojo', async () => {
    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        { provide: AuthService, useValue: { login: jest.fn() } },
        { provide: Router, useValue: { navigate: jest.fn() } }
      ]
    }).compileComponents();

    const fixture = TestBed.createComponent(LoginComponent);
    const component = fixture.componentInstance as unknown as {
      showPassword: () => boolean;
      togglePasswordVisibility: () => void;
    };

    expect(component.showPassword()).toBe(false);
    component.togglePasswordVisibility();
    expect(component.showPassword()).toBe(true);
    component.togglePasswordVisibility();
    expect(component.showPassword()).toBe(false);
  });
});
