import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';

import { EntityId, Producto, UnidadMedidaInventario } from '../../core/models/factuec.models';
import { EmpresaService } from '../../core/services/empresa.service';
import { ProductoPayload, ProductosService, TarifaIvaBackend } from '../../core/services/productos.service';
import { MetricCardComponent } from '../../shared/components/metric-card/metric-card.component';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

type FeedbackTone = 'success' | 'danger' | 'warning';

@Component({
  selector: 'app-inventario',
  imports: [ReactiveFormsModule, PageHeaderComponent, MetricCardComponent, StatusBadgeComponent],
  templateUrl: './inventario.component.html',
  styleUrl: './inventario.component.scss'
})
export class InventarioComponent implements OnInit {
  private readonly productosService = inject(ProductosService);
  private readonly empresaService = inject(EmpresaService);
  private readonly formBuilder = inject(FormBuilder);

  protected readonly productos = signal<Producto[]>([]);
  protected readonly empresaId = signal<EntityId | null>(null);
  protected readonly showForm = signal(false);
  protected readonly editingId = signal<EntityId | null>(null);
  protected readonly saving = signal(false);
  protected readonly feedback = signal<{ tone: FeedbackTone; message: string } | null>(null);
  protected readonly inventario = computed(() => this.productos().filter((producto) => producto.tipo === 'PRODUCTO'));
  protected readonly stockTotal = computed(() => this.formatQuantity(
    this.inventario().reduce((total, producto) => total + Number(producto.stock || 0), 0)
  ));
  protected readonly stockCritico = computed(() => this.inventario()
    .filter((producto) => Number(producto.stock || 0) <= Number(producto.stockMinimo || 0))
    .length
    .toString());
  protected readonly palletizables = computed(() => this.inventario().filter((producto) => producto.palletizable).length.toString());
  protected readonly refrigerados = computed(() => this.inventario().filter((producto) => producto.requiereRefrigeracion).length.toString());
  protected readonly unidadOptions: Array<{ label: string; value: UnidadMedidaInventario }> = [
    { label: 'Kilogramos', value: 'KILOGRAMO' },
    { label: 'Gramos', value: 'GRAMO' },
    { label: 'Libras', value: 'LIBRA' },
    { label: 'Quintales', value: 'QUINTAL' },
    { label: 'Toneladas', value: 'TONELADA' },
    { label: 'Cajas', value: 'CAJA' },
    { label: 'Sacos', value: 'SACO' },
    { label: 'Bultos', value: 'BULTO' },
    { label: 'Pallets', value: 'PALLET' },
    { label: 'Gavetas', value: 'GAVETA' },
    { label: 'Canastillas', value: 'CANASTILLA' },
    { label: 'Unidades', value: 'UNIDAD' }
  ];

  protected readonly inventarioForm = this.formBuilder.nonNullable.group({
    codigoPrincipal: ['FRU-001', Validators.required],
    nombre: ['Banano Cavendish', Validators.required],
    categoria: ['Frutas'],
    descripcion: ['Fruta fresca para traslado'],
    precioUnitario: [0, [Validators.required, Validators.min(0)]],
    tarifaIva: ['IVA_0' as TarifaIvaBackend, Validators.required],
    stock: [0],
    unidadMedida: ['KILOGRAMO' as UnidadMedidaInventario, Validators.required],
    stockMinimo: [0],
    pesoPromedioKg: [1],
    palletizable: [true],
    unidadesPorPallet: [48],
    requiereRefrigeracion: [false],
    activo: [true]
  });

  ngOnInit(): void {
    this.empresaService.getEmpresa().subscribe((empresa) => this.empresaId.set(empresa?.id ?? null));
    this.loadInventario();
  }

  protected newItem(): void {
    if (!this.empresaId()) {
      this.feedback.set({ tone: 'warning', message: 'Primero crea la empresa emisora en la pantalla Empresa.' });
      return;
    }
    this.editingId.set(null);
    this.inventarioForm.reset({
      codigoPrincipal: `FRU-${String(this.inventario().length + 1).padStart(3, '0')}`,
      nombre: 'Banano Cavendish',
      categoria: 'Frutas',
      descripcion: 'Fruta fresca para traslado',
      precioUnitario: 0,
      tarifaIva: 'IVA_0',
      stock: 0,
      unidadMedida: 'KILOGRAMO',
      stockMinimo: 0,
      pesoPromedioKg: 1,
      palletizable: true,
      unidadesPorPallet: 48,
      requiereRefrigeracion: false,
      activo: true
    });
    this.showForm.set(true);
  }

  protected editItem(producto: Producto): void {
    this.editingId.set(producto.id);
    this.inventarioForm.reset({
      codigoPrincipal: producto.codigo,
      nombre: producto.nombre,
      categoria: producto.categoria,
      descripcion: producto.categoria,
      precioUnitario: producto.precioUnitario,
      tarifaIva: this.toBackendTarifa(producto.tarifaIva),
      stock: producto.stock || 0,
      unidadMedida: producto.unidadMedida,
      stockMinimo: producto.stockMinimo || 0,
      pesoPromedioKg: producto.pesoPromedioKg || 0,
      palletizable: producto.palletizable,
      unidadesPorPallet: producto.unidadesPorPallet || 0,
      requiereRefrigeracion: producto.requiereRefrigeracion,
      activo: producto.estado === 'ACTIVO'
    });
    this.showForm.set(true);
  }

  protected cancelForm(): void {
    this.showForm.set(false);
    this.editingId.set(null);
  }

  protected saveItem(): void {
    const empresaId = this.empresaId();
    if (!empresaId) {
      this.feedback.set({ tone: 'warning', message: 'Primero crea la empresa emisora.' });
      return;
    }
    if (this.inventarioForm.invalid) {
      this.inventarioForm.markAllAsTouched();
      this.feedback.set({ tone: 'danger', message: 'Completa codigo, nombre y unidad de medida.' });
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
        this.feedback.set({ tone: 'success', message: 'Item de inventario guardado correctamente.' });
        this.loadInventario();
      },
      error: () => this.feedback.set({ tone: 'danger', message: 'No se pudo guardar el item. Revisa que el codigo no exista previamente.' })
    });
  }

  protected deleteItem(producto: Producto): void {
    this.productosService.delete(producto.id).subscribe({
      next: () => {
        this.feedback.set({ tone: 'success', message: 'Item de inventario inactivado.' });
        this.loadInventario();
      },
      error: () => this.feedback.set({ tone: 'danger', message: 'No se pudo inactivar el item.' })
    });
  }

  protected formatUnidad(unidad: UnidadMedidaInventario | undefined): string {
    return this.unidadOptions.find((item) => item.value === unidad)?.label || 'Unidades';
  }

  protected formatQuantity(value: number | undefined): string {
    const quantity = Number(value || 0);
    return new Intl.NumberFormat('es-EC', {
      minimumFractionDigits: quantity % 1 === 0 ? 0 : 2,
      maximumFractionDigits: 2
    }).format(quantity);
  }

  protected stockStatus(producto: Producto): 'Critico' | 'Disponible' {
    return Number(producto.stock || 0) <= Number(producto.stockMinimo || 0) ? 'Critico' : 'Disponible';
  }

  private loadInventario(): void {
    this.productosService.list().subscribe((items) => this.productos.set(items));
  }

  private toPayload(empresaId: EntityId): ProductoPayload {
    const value = this.inventarioForm.getRawValue();
    return {
      empresaId,
      codigoPrincipal: value.codigoPrincipal,
      nombre: value.nombre,
      descripcion: value.descripcion || undefined,
      tipo: 'PRODUCTO',
      precioUnitario: Number(value.precioUnitario || 0),
      tarifaIva: value.tarifaIva as TarifaIvaBackend,
      stock: Number(value.stock || 0),
      unidadMedida: value.unidadMedida as UnidadMedidaInventario,
      stockMinimo: Number(value.stockMinimo || 0),
      pesoPromedioKg: Number(value.pesoPromedioKg || 0) || undefined,
      palletizable: value.palletizable,
      unidadesPorPallet: Number(value.unidadesPorPallet || 0) || undefined,
      requiereRefrigeracion: value.requiereRefrigeracion,
      categoria: value.categoria || 'Frutas',
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
}
