import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { DatePickerModule } from 'primeng/datepicker';
import { InputTextModule } from 'primeng/inputtext';
import { SelectModule } from 'primeng/select';

import { Comprobante } from '../../../core/models/factuec.models';
import { ComprobantesService } from '../../../core/services/comprobantes.service';
import { BreadcrumbComponent } from '../../../shared/components/breadcrumb/breadcrumb.component';
import { MetricCardComponent } from '../../../shared/components/metric-card/metric-card.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-comprobantes-list',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    DatePickerModule,
    InputTextModule,
    SelectModule,
    BreadcrumbComponent,
    PageHeaderComponent,
    MetricCardComponent,
    StatusBadgeComponent
  ],
  templateUrl: './comprobantes-list.component.html',
  styleUrl: './comprobantes-list.component.scss'
})
export class ComprobantesListComponent implements OnInit {
  private readonly pageSize = 10;
  private readonly formBuilder = inject(FormBuilder);
  private readonly comprobantesService = inject(ComprobantesService);

  protected readonly comprobantes = signal<Comprobante[]>([]);
  protected readonly currentPage = signal(1);
  protected readonly appliedFilters = signal({
    fechaDesde: this.firstDayOfCurrentMonth(),
    cliente: '',
    tipo: 'TODOS',
    estado: 'TODOS'
  });
  protected readonly filtersForm = this.formBuilder.nonNullable.group({
    fechaDesde: [this.appliedFilters().fechaDesde],
    cliente: [''],
    tipo: ['TODOS'],
    estado: ['TODOS']
  });
  protected readonly tipoOptions = [
    { label: 'Todos los tipos', value: 'TODOS' },
    { label: 'Factura', value: 'FACTURA' },
    { label: 'Nota de Credito', value: 'NOTA_CREDITO' },
    { label: 'Retencion', value: 'RETENCION' }
  ];
  protected readonly estadoOptions = [
    { label: 'Todos los estados', value: 'TODOS' },
    { label: 'Autorizado', value: 'AUTORIZADO' },
    { label: 'En procesamiento', value: 'ENVIADO' },
    { label: 'Rechazado', value: 'RECHAZADO' },
    { label: 'Devuelto', value: 'DEVUELTO' }
  ];
  protected readonly filteredComprobantes = computed(() => {
    const filters = this.appliedFilters();
    return this.comprobantes().filter((item) => {
      const matchDate = !filters.fechaDesde || item.fechaEmision >= filters.fechaDesde;
      const search = filters.cliente.trim().toLowerCase();
      const matchCliente = !search
        || item.cliente.razonSocial.toLowerCase().includes(search)
        || item.identificacion.toLowerCase().includes(search);
      const matchTipo = filters.tipo === 'TODOS' || item.tipo === filters.tipo;
      const matchEstado = filters.estado === 'TODOS' || item.estado === filters.estado;
      return matchDate && matchCliente && matchTipo && matchEstado;
    });
  });
  protected readonly pageItems = computed(() => {
    const start = (this.currentPage() - 1) * this.pageSize;
    return this.filteredComprobantes().slice(start, start + this.pageSize);
  });
  protected readonly totalItems = computed(() => this.filteredComprobantes().length);
  protected readonly totalPages = computed(() => Math.max(1, Math.ceil(this.totalItems() / this.pageSize)));
  protected readonly pageNumbers = computed(() => Array.from({ length: this.totalPages() }, (_, index) => index + 1));
  protected readonly showingFrom = computed(() => this.totalItems() === 0 ? 0 : ((this.currentPage() - 1) * this.pageSize) + 1);
  protected readonly showingTo = computed(() => Math.min(this.currentPage() * this.pageSize, this.totalItems()));
  protected readonly autorizadosHoy = computed(() => {
    const today = this.today();
    return this.comprobantes().filter((item) => item.fechaEmision === today && item.estado === 'AUTORIZADO').length.toString();
  });
  protected readonly rechazados = computed(() => this.comprobantes()
    .filter((item) => ['RECHAZADO', 'DEVUELTO', 'ERROR'].includes(item.estado))
    .length
    .toString());
  protected readonly ventasMes = computed(() => this.money(this.comprobantes()
    .filter((item) => item.fechaEmision.startsWith(this.currentMonthPrefix()))
    .reduce((total, item) => total + item.total, 0)));
  protected readonly totalDocumentos = computed(() => this.comprobantes().length.toString());

  ngOnInit(): void {
    this.comprobantesService.list().subscribe((items) => this.comprobantes.set(items));
  }

  protected applyFilters(): void {
    this.appliedFilters.set(this.filtersForm.getRawValue());
    this.currentPage.set(1);
  }

  protected clearFilters(): void {
    const filters = {
      fechaDesde: this.firstDayOfCurrentMonth(),
      cliente: '',
      tipo: 'TODOS',
      estado: 'TODOS'
    };
    this.filtersForm.reset(filters);
    this.appliedFilters.set(filters);
    this.currentPage.set(1);
  }

  protected goToPage(page: number): void {
    this.currentPage.set(Math.min(Math.max(page, 1), this.totalPages()));
  }

  protected typeLabel(tipo: Comprobante['tipo']): string {
    return tipo.replace('_', ' ');
  }

  protected openRide(comprobante: Comprobante): void {
    this.comprobantesService.downloadRide(comprobante.id).subscribe((pdf) => this.openBlob(pdf, `${comprobante.numero}.pdf`));
  }

  protected downloadXml(comprobante: Comprobante): void {
    this.comprobantesService.downloadXml(comprobante.id).subscribe((xml) => this.downloadBlob(xml, `${comprobante.numero}.xml`));
  }

  private firstDayOfCurrentMonth(): string {
    const now = new Date();
    return `${now.getFullYear()}-${this.pad(now.getMonth() + 1)}-01`;
  }

  private today(): string {
    const now = new Date();
    return `${now.getFullYear()}-${this.pad(now.getMonth() + 1)}-${this.pad(now.getDate())}`;
  }

  private currentMonthPrefix(): string {
    const now = new Date();
    return `${now.getFullYear()}-${this.pad(now.getMonth() + 1)}`;
  }

  private pad(value: number): string {
    return value.toString().padStart(2, '0');
  }

  private money(value: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 2
    }).format(value);
  }

  private openBlob(blob: Blob, fileName: string): void {
    const file = new Blob([blob], { type: 'application/pdf' });
    const url = URL.createObjectURL(file);
    const opened = window.open(url, '_blank', 'noopener');
    if (!opened) {
      this.downloadBlob(file, fileName);
    }
    setTimeout(() => URL.revokeObjectURL(url), 60_000);
  }

  private downloadBlob(blob: Blob, fileName: string): void {
    const url = URL.createObjectURL(blob);
    const anchor = document.createElement('a');
    anchor.href = url;
    anchor.download = fileName;
    anchor.click();
    URL.revokeObjectURL(url);
  }
}
