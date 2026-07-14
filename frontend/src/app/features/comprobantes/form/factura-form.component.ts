import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormArray, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { DatePickerModule } from 'primeng/datepicker';
import { InputNumberModule } from 'primeng/inputnumber';
import { InputTextModule } from 'primeng/inputtext';
import { SelectModule } from 'primeng/select';
import { finalize } from 'rxjs';

import { Cliente, Comprobante, Empresa, Establecimiento, Producto } from '../../../core/models/factuec.models';
import { ClientesService } from '../../../core/services/clientes.service';
import { ComprobantesService } from '../../../core/services/comprobantes.service';
import { EmpresaService } from '../../../core/services/empresa.service';
import { ProductosService, TarifaIvaBackend } from '../../../core/services/productos.service';
import { BreadcrumbComponent } from '../../../shared/components/breadcrumb/breadcrumb.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';

type FeedbackTone = 'success' | 'danger' | 'warning';

@Component({
  selector: 'app-factura-form',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    DatePickerModule,
    InputNumberModule,
    InputTextModule,
    SelectModule,
    BreadcrumbComponent,
    PageHeaderComponent
  ],
  templateUrl: './factura-form.component.html',
  styleUrl: './factura-form.component.scss'
})
export class FacturaFormComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly empresaService = inject(EmpresaService);
  private readonly clientesService = inject(ClientesService);
  private readonly productosService = inject(ProductosService);
  private readonly comprobantesService = inject(ComprobantesService);
  private readonly router = inject(Router);

  protected readonly empresa = signal<Empresa | null>(null);
  protected readonly establecimientos = signal<Establecimiento[]>([]);
  protected readonly clientes = signal<Cliente[]>([]);
  protected readonly productos = signal<Producto[]>([]);
  protected readonly feedback = signal<{ tone: FeedbackTone; message: string } | null>(null);
  protected readonly savingDraft = signal(false);
  protected readonly emitting = signal(false);
  protected readonly lastComprobante = signal<Comprobante | null>(null);
  protected readonly monedaOptions = [{ label: 'USD - Dolar Americano', value: 'USD' }];
  protected readonly ivaOptions = [
    { label: '15%', value: 'IVA_15' },
    { label: '12%', value: 'IVA_12' },
    { label: '0%', value: 'IVA_0' },
    { label: 'Exento', value: 'EXENTO_IVA' },
    { label: 'No Objeto', value: 'NO_OBJETO_IVA' }
  ];
  protected readonly formaPagoOptions = [
    { label: 'Sin utilizacion del sistema financiero', value: 'SIN_UTILIZACION_SISTEMA_FINANCIERO' },
    { label: 'Otros con sistema financiero', value: 'OTROS_CON_SISTEMA_FINANCIERO' },
    { label: 'Tarjeta de credito', value: 'TARJETA_DE_CREDITO' },
    { label: 'Tarjeta de debito', value: 'TARJETA_DE_DEBITO' }
  ];
  protected readonly establecimientoOptions = computed(() => this.establecimientos().map((establecimiento) => ({
    label: `${establecimiento.codigo} - ${establecimiento.nombre}`,
    value: String(establecimiento.id)
  })));
  protected readonly puntoEmisionOptions = computed(() => this.puntosDisponibles().map((punto) => ({
    label: `${punto.codigo} - ${punto.nombre}`,
    value: String(punto.id)
  })));
  protected readonly clienteOptions = computed(() => this.clientes().map((cliente) => ({
    label: `${cliente.identificacion} - ${cliente.razonSocial}`,
    value: String(cliente.id)
  })));
  protected readonly productoOptions = computed(() => [
    { label: 'Manual', value: '' },
    ...this.productos().map((producto) => ({
      label: `${producto.codigo} - ${producto.nombre}`,
      value: String(producto.id)
    }))
  ]);

  protected readonly facturaForm = this.formBuilder.nonNullable.group({
    fechaEmision: [new Date().toISOString().slice(0, 10), Validators.required],
    moneda: ['USD', Validators.required],
    clienteId: ['', Validators.required],
    establecimientoId: ['', Validators.required],
    puntoEmisionId: ['', Validators.required],
    detalles: this.formBuilder.array([this.createDetail('', 'SERV-001', 'Servicio de prueba', 1, 10, 'IVA_15')]),
    pago: this.formBuilder.nonNullable.group({
      formaPago: ['SIN_UTILIZACION_SISTEMA_FINANCIERO'],
      valor: [11.5, Validators.min(0)],
      plazo: [0, Validators.min(0)],
      unidadTiempo: ['dias']
    })
  });

  ngOnInit(): void {
    this.empresaService.getEmpresa().subscribe((empresa) => {
      this.empresa.set(empresa);
      if (!empresa) {
        this.feedback.set({ tone: 'warning', message: 'Primero crea la empresa emisora y su firma antes de emitir.' });
        return;
      }
      this.empresaService.getEstablecimientos(empresa.id).subscribe((establecimientos) => {
        this.establecimientos.set(establecimientos);
        const first = establecimientos[0];
        if (first) {
          this.selectEstablecimiento(String(first.id));
        }
      });
    });
    this.clientesService.list().subscribe((clientes) => {
      this.clientes.set(clientes);
      if (clientes[0]) {
        this.facturaForm.patchValue({ clienteId: String(clientes[0].id) });
      }
    });
    this.productosService.list().subscribe((productos) => {
      this.productos.set(productos);
      if (productos[0]) {
        this.selectProducto(0, String(productos[0].id));
      }
    });
  }

  protected subtotal(): number {
    return this.detalles.controls.reduce((total, group) => total + this.lineSubtotal(group.getRawValue()), 0);
  }

  protected iva(): number {
    return this.detalles.controls.reduce((total, group) => {
      const line = group.getRawValue();
      const rate = line.tarifaIva === 'IVA_15' ? 0.15 : line.tarifaIva === 'IVA_12' ? 0.12 : 0;
      return total + this.lineSubtotal(line) * rate;
    }, 0);
  }

  protected total(): number {
    return this.subtotal() + this.iva();
  }

  protected get detalles(): FormArray {
    return this.facturaForm.controls.detalles;
  }

  protected addItem(): void {
    const product = this.productos()[0];
    this.detalles.push(this.createDetail(
      product ? String(product.id) : '',
      product?.codigo || 'SERV-001',
      product?.nombre || 'Servicio de prueba',
      1,
      product?.precioUnitario || 10,
      product ? this.toBackendTarifa(product.tarifaIva) : 'IVA_15'
    ));
  }

  protected removeItem(index: number): void {
    if (this.detalles.length > 1) {
      this.detalles.removeAt(index);
    }
  }

  protected lineSubtotal(line: { cantidad: number; precioUnitario: number; descuento: number }): number {
    const rawSubtotal = Number(line.cantidad || 0) * Number(line.precioUnitario || 0);
    const discount = Number(line.descuento || 0);
    return rawSubtotal - discount;
  }

  protected puntosDisponibles(): Establecimiento['puntosEmision'] {
    const establecimientoId = this.facturaForm.controls.establecimientoId.value;
    return this.establecimientos().find((item) => String(item.id) === establecimientoId)?.puntosEmision || [];
  }

  protected selectEstablecimiento(establecimientoId: string): void {
    const puntos = this.establecimientos().find((item) => String(item.id) === establecimientoId)?.puntosEmision || [];
    this.facturaForm.patchValue({
      establecimientoId,
      puntoEmisionId: puntos[0] ? String(puntos[0].id) : ''
    });
  }

  protected selectProducto(index: number, productoId: string): void {
    const producto = this.productos().find((item) => String(item.id) === productoId);
    const group = this.detalles.at(index);
    if (!producto || !group) {
      return;
    }
    group.patchValue({
      productoId: String(producto.id),
      codigo: producto.codigo,
      descripcion: producto.nombre,
      precioUnitario: producto.precioUnitario,
      tarifaIva: this.toBackendTarifa(producto.tarifaIva)
    });
  }

  protected saveDraft(): void {
    this.submit(false);
  }

  protected emitir(): void {
    this.submit(true);
  }

  private submit(emitir: boolean): void {
    if (this.facturaForm.invalid || !this.empresa()) {
      this.facturaForm.markAllAsTouched();
      this.feedback.set({ tone: 'danger', message: 'Completa empresa, cliente, local, punto de emision y detalle.' });
      return;
    }

    const loading = emitir ? this.emitting : this.savingDraft;
    loading.set(true);
    this.feedback.set(null);
    const request$ = emitir
      ? this.comprobantesService.emitirFactura(this.toPayload())
      : this.comprobantesService.createDraft(this.toPayload());

    request$.pipe(finalize(() => loading.set(false))).subscribe({
      next: (comprobante) => {
        this.lastComprobante.set(comprobante);
        const rejectedBySri = comprobante.estado === 'DEVUELTO' || comprobante.estado === 'RECHAZADO' || comprobante.estado === 'ERROR';
        const sriMessage = comprobante.mensajesSri[0]?.mensaje;
        this.feedback.set({
          tone: rejectedBySri ? 'warning' : 'success',
          message: emitir && rejectedBySri
            ? `Factura enviada, pero SRI respondio ${comprobante.estado}. ${sriMessage || 'Revisa los mensajes SRI del comprobante.'}`
            : emitir
            ? `Factura enviada al SRI pruebas. Estado: ${comprobante.estado}.`
            : 'Borrador guardado correctamente.'
        });
        if (emitir) {
          void this.router.navigate(['/comprobantes', comprobante.id]);
        }
      },
      error: (error) => this.feedback.set({
        tone: 'danger',
        message: this.apiErrorMessage(error, emitir
          ? 'No se pudo emitir. Revisa firma activa, autorizacion SRI en pruebas y datos tributarios.'
          : 'No se pudo guardar el borrador.')
      })
    });
  }

  private apiErrorMessage(error: unknown, fallback: string): string {
    if (typeof error === 'object' && error !== null && 'error' in error) {
      const body = (error as { error?: { message?: unknown } }).error;
      if (typeof body?.message === 'string' && body.message.trim()) {
        return body.message;
      }
    }
    if (error instanceof Error && error.message.trim()) {
      return error.message;
    }
    return fallback;
  }

  private toPayload(): unknown {
    const value = this.facturaForm.getRawValue();
    return {
      empresaId: this.empresa()?.id,
      clienteId: value.clienteId,
      establecimientoId: value.establecimientoId,
      puntoEmisionId: value.puntoEmisionId,
      fechaEmision: value.fechaEmision,
      formaPago: value.pago.formaPago,
      plazo: Number(value.pago.plazo || 0),
      tiempo: value.pago.unidadTiempo,
      detalles: value.detalles.map((detalle) => ({
        productoId: detalle.productoId || undefined,
        codigoPrincipal: detalle.codigo,
        descripcion: detalle.descripcion,
        cantidad: Number(detalle.cantidad || 0),
        precioUnitario: Number(detalle.precioUnitario || 0),
        descuento: Number(detalle.descuento || 0),
        tarifaIva: detalle.tarifaIva
      }))
    };
  }

  private createDetail(productoId: string, codigo: string, descripcion: string, cantidad: number, precioUnitario: number, tarifaIva: TarifaIvaBackend) {
    return this.formBuilder.nonNullable.group({
      productoId: [productoId],
      codigo: [codigo, Validators.required],
      descripcion: [descripcion, Validators.required],
      cantidad: [cantidad, [Validators.required, Validators.min(0.01)]],
      precioUnitario: [precioUnitario, [Validators.required, Validators.min(0)]],
      descuento: [0, [Validators.min(0), Validators.max(100)]],
      tarifaIva: [tarifaIva, Validators.required]
    });
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
