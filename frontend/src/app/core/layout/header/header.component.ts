import { Component, EventEmitter, OnInit, Output, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';

import { AuthService } from '../../services/auth.service';
import { SriService } from '../../services/sri.service';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-header',
  imports: [ConfirmDialogComponent],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent implements OnInit {
  @Output() openSidebar = new EventEmitter<void>();

  private readonly authService = inject(AuthService);
  private readonly sriService = inject(SriService);
  private readonly router = inject(Router);

  protected readonly user = computed(() => this.authService.user());
  protected readonly ambiente = signal('PRUEBAS');
  protected readonly profileMenuOpen = signal(false);
  protected readonly logoutDialogOpen = signal(false);

  ngOnInit(): void {
    this.sriService.getStatus().subscribe((status) => this.ambiente.set(status.ambienteDefault || 'PRUEBAS'));
  }

  protected toggleProfileMenu(): void {
    this.profileMenuOpen.update((open) => !open);
  }

  protected closeProfileMenu(): void {
    this.profileMenuOpen.set(false);
  }

  protected navigateTo(path: string): void {
    this.closeProfileMenu();
    void this.router.navigate([path]);
  }

  protected requestLogout(): void {
    this.closeProfileMenu();
    this.logoutDialogOpen.set(true);
  }

  protected cancelLogout(): void {
    this.logoutDialogOpen.set(false);
  }

  protected confirmLogout(): void {
    this.logoutDialogOpen.set(false);
    this.authService.logout();
    void this.router.navigate(['/login']);
  }
}
