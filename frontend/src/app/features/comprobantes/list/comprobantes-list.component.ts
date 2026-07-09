import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { Comprobante } from '../../../core/models/factuec.models';
import { ComprobantesService } from '../../../core/services/comprobantes.service';
import { BreadcrumbComponent } from '../../../shared/components/breadcrumb/breadcrumb.component';
import { MetricCardComponent } from '../../../shared/components/metric-card/metric-card.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-comprobantes-list',
  imports: [ReactiveFormsModule, RouterLink, BreadcrumbComponent, PageHeaderComponent, MetricCardComponent, StatusBadgeComponent],
  templateUrl: './comprobantes-list.component.html',
  styleUrl: './comprobantes-list.component.scss'
})
export class ComprobantesListComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly comprobantesService = inject(ComprobantesService);

  protected readonly comprobantes = signal<Comprobante[]>([]);
  protected readonly filtersForm = this.formBuilder.nonNullable.group({
    fechaDesde: ['2026-07-01'],
    cliente: [''],
    tipo: ['TODOS'],
    estado: ['TODOS']
  });

  ngOnInit(): void {
    this.comprobantesService.list().subscribe((items) => this.comprobantes.set(items));
  }

  protected typeLabel(tipo: Comprobante['tipo']): string {
    return tipo.replace('_', ' ');
  }
}
