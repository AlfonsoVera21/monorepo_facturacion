import { Component, OnInit, inject, signal } from '@angular/core';

import { Role, User } from '../../core/models/factuec.models';
import { UsuariosService } from '../../core/services/usuarios.service';
import { MetricCardComponent } from '../../shared/components/metric-card/metric-card.component';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-usuarios',
  imports: [PageHeaderComponent, MetricCardComponent, StatusBadgeComponent],
  templateUrl: './usuarios.component.html',
  styleUrl: './usuarios.component.scss'
})
export class UsuariosComponent implements OnInit {
  private readonly usuariosService = inject(UsuariosService);

  protected readonly users = signal<User[]>([]);
  protected readonly roles = signal<Role[]>([]);

  ngOnInit(): void {
    this.usuariosService.listUsers().subscribe((users) => this.users.set(users));
    this.usuariosService.listRoles().subscribe((roles) => this.roles.set(roles));
  }
}
