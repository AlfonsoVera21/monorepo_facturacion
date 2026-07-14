import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';

import { EntityId, Producto, TipoProducto } from '../../core/models/factuec.models';
import { EmpresaService } from '../../core/services/empresa.service';
import { ProductoPayload, ProductosService, TarifaIvaBackend } from '../../core/services/productos.service';
import { MetricCardComponent } from '../../shared/components/metric-card/metric-card.component';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

type FeedbackTone = 'success' | 'danger' | 'warning';

@Component({
  selector: 'app-productos',
  imports: [ReactiveFormsModule, PageHeaderComponent, MetricCardComponent, StatusBadgeComponent],
  templateUrl: './productos.component.html',
  styleUrl: './productos.component.scss'
})
export class ProductosComponent implements OnInit {
  private readonly productosService = inject(ProductosService);
  private readonly empresaService = inject(EmpresaService);
  private readonly formBuilder = inject(FormBuilder);

  protected readonly productos = signal<Producto[]>([]);
  protected readonly empresaId = signal<EntityId | null>(null);
  protected readonly showForm = signal(false);
  protected readonly editingId = signal<EntityId | null>(null);
  protected readonly saving = signal(false);
  protected readonly feedback = signal<{ tone: FeedbackTone; message: string } | null>(null);
  protected readonly totalItems = computed(() => this.productos().length.toString());
  protected readonly servicios = computed(() => this.productos().filter((producto) => producto.tipo === 'SERVICIO').length.toString());
  protected readonly productosActivos = computed(() => this.productos().filter((producto) => producto.estado === 'ACTIVO').length.toString());
  protected readonly stockTotal = computed(() => this.formatStock(
    this.productos()
      .filter((producto) => producto.tipo === 'PRODUCTO')
      .reduce((total, producto) => total + Number(producto.stock || 0), 0)
  ));

  protected readonly productoForm = this.formBuilder.nonNullable.group({
    codigoPrincipal: ['SERV-001', Validators.required],
    nombre: ['Servicio de prueba', Validators.required],
    descripcion: ['Servicio de prueba para facturacion electronica'],
    tipo: ['SERVICIO', Validators.required],
    precioUnitario: [10, [Validators.required, Validators.min(0)]],
    tarifaIva: ['IVA_15', Validators.required],
    stock: [0],
    categoria: ['Servicios'],
    activo: [true]
  });

  ngOnInit(): void {
    this.empresaService.getEmpresa().subscribe((empresa) => this.empresaId.set(empresa?.id ?? null));
    this.loadProductos();
  }

  protected newProducto(): void {
    if (!this.empresaId()) {
      this.feedback.set({ tone: 'warning', message: 'Primero crea la empresa emisora en la pantalla Empresa.' });
      return;
    }
    this.editingId.set(null);
    this.productoForm.reset({
      codigoPrincipal: `SERV-${String(this.productos().length + 1).padStart(3, '0')}`,
      nombre: 'Servicio de prueba',
      descripcion: 'Servicio de prueba para facturacion electronica',
      tipo: 'SERVICIO',
      precioUnitario: 10,
      tarifaIva: 'IVA_15',
      stock: 0,
      categoria: 'Servicios',
      activo: true
    });
    this.showForm.set(true);
  }

  protected editProducto(producto: Producto): void {
    this.editingId.set(producto.id);
    this.productoForm.reset({
      codigoPrincipal: producto.codigo,
      nombre: producto.nombre,
      descripcion: producto.categoria,
      tipo: producto.tipo,
      precioUnitario: producto.precioUnitario,
      tarifaIva: this.toBackendTarifa(producto.tarifaIva),
      stock: producto.stock || 0,
      categoria: producto.categoria,
      activo: producto.estado === 'ACTIVO'
    });
    this.showForm.set(true);
  }

  protected cancelForm(): void {
    this.showForm.set(false);
    this.editingId.set(null);
  }

  protected saveProducto(): void {
    const empresaId = this.empresaId();
    if (!empresaId) {
      this.feedback.set({ tone: 'warning', message: 'Primero crea la empresa emisora.' });
      return;
    }
    if (this.productoForm.invalid) {
      this.productoForm.markAllAsTouched();
      this.feedback.set({ tone: 'danger', message: 'Completa codigo, nombre, precio e IVA.' });
      return;
    }

    this.saving.set(true);
    this.feedback.set(null);
    this.productosService.save(this.editingId(), this.toPayload(empresaId)).pipe(
      finalize(() => this.saving.set(false))
    ).subscribe({
      next: () => {
        this.showForm.set(false);
        this.editingId.set(null);
        this.feedback.set({ tone: 'success', message: 'Producto o servicio guardado correctamente.' });
        this.loadProductos();
      },
      error: () => this.feedback.set({ tone: 'danger', message: 'No se pudo guardar el producto. Revisa que el codigo no exista previamente.' })
    });
  }

  protected deleteProducto(producto: Producto): void {
    this.productosService.delete(producto.id).subscribe({
      next: () => {
        this.feedback.set({ tone: 'success', message: 'Producto inactivado.' });
        this.loadProductos();
      },
      error: () => this.feedback.set({ tone: 'danger', message: 'No se pudo inactivar el producto.' })
    });
  }

  private loadProductos(): void {
    this.productosService.list().subscribe((items) => this.productos.set(items));
  }

  private toPayload(empresaId: EntityId): ProductoPayload {
    const value = this.productoForm.getRawValue();
    return {
      empresaId,
      codigoPrincipal: value.codigoPrincipal,
      nombre: value.nombre,
      descripcion: value.descripcion || undefined,
      tipo: value.tipo as TipoProducto,
      precioUnitario: Number(value.precioUnitario || 0),
      tarifaIva: value.tarifaIva as TarifaIvaBackend,
      stock: Number(value.stock || 0),
      categoria: value.categoria || undefined,
      activo: value.activo
    };
  }

  private toBackendTarifa(tarifa: Producto['tarifaIva']): TarifaIvaBackend {
    if (tarifa === '12%') {
      return 'IVA_12';
    }
    if (tarifa === '0%') {
      return 'IVA_0';
    }
    if (tarifa === 'NO_OBJETO') {
      return 'NO_OBJETO_IVA';
    }
    if (tarifa === 'EXENTO') {
      return 'EXENTO_IVA';
    }
    return 'IVA_15';
  }

  protected formatStock(value: number | undefined): string {
    const stock = Number(value || 0);
    return new Intl.NumberFormat('es-EC', {
      minimumFractionDigits: stock % 1 === 0 ? 0 : 2,
      maximumFractionDigits: 2
    }).format(stock);
  }
}
