import { Component, OnInit, inject, signal } from '@angular/core';

import { Producto } from '../../core/models/factuec.models';
import { ProductosService } from '../../core/services/productos.service';
import { MetricCardComponent } from '../../shared/components/metric-card/metric-card.component';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-productos',
  imports: [PageHeaderComponent, MetricCardComponent, StatusBadgeComponent],
  templateUrl: './productos.component.html',
  styleUrl: './productos.component.scss'
})
export class ProductosComponent implements OnInit {
  private readonly productosService = inject(ProductosService);

  protected readonly productos = signal<Producto[]>([]);

  ngOnInit(): void {
    this.productosService.list().subscribe((items) => this.productos.set(items));
  }
}
