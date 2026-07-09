import { Component, EventEmitter, Output, computed, inject } from '@angular/core';
import { Router } from '@angular/router';

import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {
  @Output() openSidebar = new EventEmitter<void>();

  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly user = computed(() => this.authService.user());

  protected logout(): void {
    this.authService.logout();
    void this.router.navigate(['/login']);
  }
}
