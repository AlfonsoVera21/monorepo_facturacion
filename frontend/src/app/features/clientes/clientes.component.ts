import { Component, OnInit, inject, signal } from '@angular/core';

import { Cliente } from '../../core/models/factuec.models';
import { ClientesService } from '../../core/services/clientes.service';
import { MetricCardComponent } from '../../shared/components/metric-card/metric-card.component';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-clientes',
  imports: [PageHeaderComponent, MetricCardComponent, StatusBadgeComponent],
  templateUrl: './clientes.component.html',
  styleUrl: './clientes.component.scss'
})
export class ClientesComponent implements OnInit {
  private readonly clientesService = inject(ClientesService);

  protected readonly clientes = signal<Cliente[]>([]);

  ngOnInit(): void {
    this.clientesService.list().subscribe((items) => this.clientes.set(items));
  }
}
