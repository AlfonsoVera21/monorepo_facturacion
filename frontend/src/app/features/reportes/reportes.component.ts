import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';

import { ReportesService } from '../../core/services/reportes.service';
import { MetricCardComponent } from '../../shared/components/metric-card/metric-card.component';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-reportes',
  imports: [ReactiveFormsModule, PageHeaderComponent, MetricCardComponent, StatusBadgeComponent],
  templateUrl: './reportes.component.html',
  styleUrl: './reportes.component.scss'
})
export class ReportesComponent implements OnInit {
  private readonly reportesService = inject(ReportesService);
  private readonly formBuilder = inject(FormBuilder);

  protected readonly ventas = signal<{ mes: string; ventas: number }[]>([]);
  protected readonly filtrosForm = this.formBuilder.nonNullable.group({
    desde: ['2026-01-01'],
    hasta: ['2026-07-08'],
    establecimiento: ['TODOS'],
    tipo: ['VENTAS']
  });

  ngOnInit(): void {
    this.reportesService.getVentasMensuales().subscribe((ventas) => this.ventas.set(ventas));
  }
}
