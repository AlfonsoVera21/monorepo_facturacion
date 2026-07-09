import { Component, OnInit, inject, signal } from '@angular/core';

import { Comprobante, DashboardMetric } from '../../core/models/factuec.models';
import { DashboardService } from '../../core/services/dashboard.service';
import { AlertCardComponent } from '../../shared/components/alert-card/alert-card.component';
import { MetricCardComponent } from '../../shared/components/metric-card/metric-card.component';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-dashboard',
  imports: [PageHeaderComponent, MetricCardComponent, StatusBadgeComponent, AlertCardComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  private readonly dashboardService = inject(DashboardService);

  protected readonly metrics = signal<DashboardMetric[]>([]);
  protected readonly latestComprobantes = signal<Comprobante[]>([]);
  protected readonly barData = [
    { label: 'ENE', className: 'bar-chart__bar--40' },
    { label: 'FEB', className: 'bar-chart__bar--48' },
    { label: 'MAR', className: 'bar-chart__bar--44' },
    { label: 'ABR', className: 'bar-chart__bar--58' },
    { label: 'MAY', className: 'bar-chart__bar--72' },
    { label: 'JUN', className: 'bar-chart__bar--84' }
  ];

  ngOnInit(): void {
    this.dashboardService.getMetrics().subscribe((metrics) => this.metrics.set(metrics));
    this.dashboardService.getLatestComprobantes().subscribe((items) => this.latestComprobantes.set(items));
  }
}
