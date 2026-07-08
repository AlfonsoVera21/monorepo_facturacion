import { Component, OnInit, inject, signal } from '@angular/core';

import { SriMensaje } from '../../../core/models/factuec.models';
import { SriService, SriStatus } from '../../../core/services/sri.service';
import { MetricCardComponent } from '../../../shared/components/metric-card/metric-card.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-sri-monitor',
  imports: [PageHeaderComponent, MetricCardComponent, StatusBadgeComponent],
  templateUrl: './sri-monitor.component.html',
  styleUrl: './sri-monitor.component.scss'
})
export class SriMonitorComponent implements OnInit {
  private readonly sriService = inject(SriService);

  protected readonly status = signal<SriStatus | undefined>(undefined);
  protected readonly mensajes = signal<SriMensaje[]>([]);

  ngOnInit(): void {
    this.sriService.getStatus().subscribe((status) => this.status.set(status));
    this.sriService.getMensajes().subscribe((mensajes) => this.mensajes.set(mensajes));
  }

  protected numberText(value: number): string {
    return value.toString();
  }
}
