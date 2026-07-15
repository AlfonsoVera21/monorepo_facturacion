import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormArray, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { DatePickerModule } from 'primeng/datepicker';
import { InputNumberModule } from 'primeng/inputnumber';
import { InputTextModule } from 'primeng/inputtext';
import { SelectModule } from 'primeng/select';
import { finalize } from 'rxjs';

import { Chofer, Cliente, Comprobante, Empresa, Establecimiento, Producto, TipoIdentificacion } from '../../../core/models/factuec.models';
import { ClientesService } from '../../../core/services/clientes.service';
import { ChoferesService } from '../../../core/services/choferes.service';
import { ComprobantesService } from '../../../core/services/comprobantes.service';
import { EmpresaService } from '../../../core/services/empresa.service';
import { ProductosService } from '../../../core/services/productos.service';
import { BreadcrumbComponent } from '../../../shared/components/breadcrumb/breadcrumb.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';

type FeedbackTone = 'success' | 'danger' | 'warning';

@Component({
  selector: 'app-guia-remision-form',
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
  templateUrl: './guia-remision-form.component.html',
  styleUrl: './guia-remision-form.component.scss'
})
export class GuiaRemisionFormComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly empresaService = inject(EmpresaService);
  private readonly clientesService = inject(ClientesService);
  private readonly choferesService = inject(ChoferesService);
  private readonly productosService = inject(ProductosService);
  private readonly comprobantesService = inject(ComprobantesService);
  private readonly router = inject(Router);

  protected readonly empresa = signal<Empresa | null>(null);
  protected readonly establecimientos = signal<Establecimiento[]>([]);
  protected readonly clientes = signal<Cliente[]>([]);
  protected readonly choferes = signal<Chofer[]>([]);
  protected readonly productos = signal<Producto[]>([]);
  protected readonly feedback = signal<{ tone: FeedbackTone; message: string } | null>(null);
  protected readonly emitting = signal(false);
  protected readonly lastComprobante = signal<Comprobante | null>(null);
  protected readonly tipoIdentificacionOptions = [
    { label: 'RUC', value: 'RUC' },
    { label: 'Cedula', value: 'CEDULA' },
    { label: 'Pasaporte', value: 'PASAPORTE' }
  ];
  protected readonly documentoSustentoOptions = [
    { label: 'Sin sustento', value: '' },
    { label: 'Factura', value: '01' },
    { label: 'Liquidacion de compra', value: '03' },
    { label: 'Nota de credito', value: '04' },
    { label: 'Nota de debito', value: '05' }
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
  protected readonly choferOptions = computed(() => [
    { label: 'Manual', value: '' },
    ...this.choferes()
      .filter((chofer) => chofer.estado === 'ACTIVO')
      .map((chofer) => ({
        label: `${chofer.identificacion} - ${this.nombreChofer(chofer)}${chofer.placaVehiculo ? ` (${chofer.placaVehiculo})` : ''}`,
        value: String(chofer.id)
      }))
  ]);
  protected readonly productoOptions = computed(() => [
    { label: 'Manual', value: '' },
    ...this.productos().map((producto) => ({
      label: `${producto.codigo} - ${producto.nombre} (${this.unidadLabel(producto.unidadMedida)})`,
      value: String(producto.id)
    }))
  ]);

  protected readonly guiaForm = this.formBuilder.nonNullable.group({
    fechaEmision: [this.today(), Validators.required],
    clienteId: ['', Validators.required],
    establecimientoId: ['', Validators.required],
    puntoEmisionId: ['', Validators.required],
    transporte: this.formBuilder.nonNullable.group({
      choferId: [''],
      dirPartida: ['', Validators.required],
      razonSocialTransportista: ['', Validators.required],
      tipoIdentificacionTransportista: ['RUC' as TipoIdentificacion, Validators.required],
      identificacionTransportista: ['', Validators.required],
      rise: [''],
      fechaIniTransporte: [this.today(), Validators.required],
      fechaFinTransporte: [this.today(), Validators.required],
      placa: ['', Validators.required]
    }),
    destino: this.formBuilder.nonNullable.group({
      destinatarioDireccion: [''],
      motivoTraslado: ['Venta', Validators.required],
      ruta: [''],
      codDocSustento: [''],
      numDocSustento: [''],
      numAutDocSustento: [''],
      fechaEmisionDocSustento: ['']
    }),
    detalles: this.formBuilder.array([this.createDetail('', 'P001', 'Producto transportado', 1)])
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
    this.choferesService.list().subscribe((choferes) => this.choferes.set(choferes));
    this.clientesService.list().subscribe((clientes) => {
      this.clientes.set(clientes);
      if (clientes[0]) {
        this.selectCliente(String(clientes[0].id));
      }
    });
    this.productosService.list().subscribe((productos) => {
      this.productos.set(productos);
      if (productos[0]) {
        this.selectProducto(0, String(productos[0].id));
      }
    });
  }

  protected get detalles(): FormArray {
    return this.guiaForm.controls.detalles;
  }

  protected totalItems(): number {
    return this.detalles.length;
  }

  protected totalCantidad(): number {
    return this.detalles.controls.reduce((total, group) => total + Number(group.get('cantidad')?.value || 0), 0);
  }

  protected puntosDisponibles(): Establecimiento['puntosEmision'] {
    const establecimientoId = this.guiaForm.controls.establecimientoId.value;
    return this.establecimientos().find((item) => String(item.id) === establecimientoId)?.puntosEmision || [];
  }

  protected selectEstablecimiento(establecimientoId: string): void {
    const establecimiento = this.establecimientos().find((item) => String(item.id) === establecimientoId);
    const puntos = establecimiento?.puntosEmision || [];
    this.guiaForm.patchValue({
      establecimientoId,
      puntoEmisionId: puntos[0] ? String(puntos[0].id) : ''
    });
    if (establecimiento && !this.guiaForm.controls.transporte.controls.dirPartida.value) {
      this.guiaForm.controls.transporte.patchValue({ dirPartida: establecimiento.direccion });
    }
  }

  protected selectCliente(clienteId: string): void {
    const cliente = this.clientes().find((item) => String(item.id) === clienteId);
    this.guiaForm.patchValue({ clienteId });
    if (cliente) {
      this.guiaForm.controls.destino.patchValue({ destinatarioDireccion: cliente.direccion });
    }
  }

  protected selectProducto(index: number, productoId: string): void {
    const producto = this.productos().find((item) => String(item.id) === productoId);
    const group = this.detalles.at(index);
    if (!producto || !group) {
      return;
    }
    group.patchValue({
      productoId: String(producto.id),
      codigoInterno: producto.codigo,
      descripcion: producto.nombre,
      codigoAdicional: this.unidadLabel(producto.unidadMedida)
    });
  }

  protected selectChofer(choferId: string): void {
    const chofer = this.choferes().find((item) => String(item.id) === choferId);
    this.guiaForm.controls.transporte.patchValue({ choferId });
    if (!chofer) {
      return;
    }
    this.guiaForm.controls.transporte.patchValue({
      razonSocialTransportista: this.nombreChofer(chofer),
      tipoIdentificacionTransportista: chofer.tipoIdentificacion,
      identificacionTransportista: chofer.identificacion,
      placa: chofer.placaVehiculo
    });
  }

  protected addItem(): void {
    const product = this.productos()[0];
    this.detalles.push(this.createDetail(
      product ? String(product.id) : '',
      product?.codigo || 'P001',
      product?.nombre || 'Producto transportado',
      1
    ));
  }

  protected removeItem(index: number): void {
    if (this.detalles.length > 1) {
      this.detalles.removeAt(index);
    }
  }

  protected emitir(): void {
    if (this.guiaForm.invalid || !this.empresa()) {
      this.guiaForm.markAllAsTouched();
      this.feedback.set({ tone: 'danger', message: 'Completa empresa, destinatario, transporte y detalles de traslado.' });
      return;
    }

    this.emitting.set(true);
    this.feedback.set(null);
    this.comprobantesService.emitirGuiaRemision(this.toPayload())
      .pipe(finalize(() => this.emitting.set(false)))
      .subscribe({
        next: (comprobante) => {
          this.lastComprobante.set(comprobante);
          const rejectedBySri = comprobante.estado === 'DEVUELTO' || comprobante.estado === 'RECHAZADO' || comprobante.estado === 'ERROR';
          const sriMessage = comprobante.mensajesSri[0]?.mensaje;
          this.feedback.set({
            tone: rejectedBySri ? 'warning' : 'success',
            message: rejectedBySri
              ? `Guia enviada, pero SRI respondio ${comprobante.estado}. ${sriMessage || 'Revisa los mensajes SRI del comprobante.'}`
              : `Guia enviada al SRI pruebas. Estado: ${comprobante.estado}.`
          });
          void this.router.navigate(['/comprobantes', comprobante.id]);
        },
        error: (error) => this.feedback.set({
          tone: 'danger',
          message: this.apiErrorMessage(error, 'No se pudo emitir la guia. Revisa firma activa, datos de transporte y autorizacion SRI.')
        })
      });
  }

  private toPayload(): unknown {
    const value = this.guiaForm.getRawValue();
    const { choferId: _choferId, ...transporte } = value.transporte;
    return {
      empresaId: this.empresa()?.id,
      clienteId: value.clienteId,
      establecimientoId: value.establecimientoId,
      puntoEmisionId: value.puntoEmisionId,
      fechaEmision: value.fechaEmision,
      ...transporte,
      ...value.destino,
      fechaEmisionDocSustento: value.destino.fechaEmisionDocSustento || undefined,
      detalles: value.detalles.map((detalle) => ({
        productoId: detalle.productoId || undefined,
        codigoInterno: detalle.codigoInterno,
        codigoAdicional: detalle.codigoAdicional || undefined,
        descripcion: detalle.descripcion,
        cantidad: Number(detalle.cantidad || 0)
      }))
    };
  }

  private nombreChofer(chofer: Chofer): string {
    return `${chofer.nombres} ${chofer.apellidos}`.trim();
  }

  private unidadLabel(unidad: Producto['unidadMedida']): string {
    const labels: Record<Producto['unidadMedida'], string> = {
      KILOGRAMO: 'Kg',
      GRAMO: 'g',
      LIBRA: 'lb',
      QUINTAL: 'Quintal',
      TONELADA: 'Ton',
      CAJA: 'Caja',
      SACO: 'Saco',
      BULTO: 'Bulto',
      PALLET: 'Pallet',
      GAVETA: 'Gaveta',
      CANASTILLA: 'Canastilla',
      UNIDAD: 'Unidad'
    };
    return labels[unidad] || 'Unidad';
  }

  private createDetail(productoId: string, codigoInterno: string, descripcion: string, cantidad: number) {
    return this.formBuilder.nonNullable.group({
      productoId: [productoId],
      codigoInterno: [codigoInterno, Validators.required],
      codigoAdicional: [''],
      descripcion: [descripcion, Validators.required],
      cantidad: [cantidad, [Validators.required, Validators.min(0.000001)]]
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

  private today(): string {
    const now = new Date();
    return `${now.getFullYear()}-${this.pad(now.getMonth() + 1)}-${this.pad(now.getDate())}`;
  }

  private pad(value: number): string {
    return value.toString().padStart(2, '0');
  }
}
