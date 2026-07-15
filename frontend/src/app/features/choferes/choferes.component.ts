import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';

import { Chofer, EntityId, TipoIdentificacion, UnidadMedidaInventario } from '../../core/models/factuec.models';
import { ChoferPayload, ChoferesService } from '../../core/services/choferes.service';
import { EmpresaService } from '../../core/services/empresa.service';
import { MetricCardComponent } from '../../shared/components/metric-card/metric-card.component';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

type FeedbackTone = 'success' | 'danger' | 'warning';

@Component({
  selector: 'app-choferes',
  imports: [ReactiveFormsModule, PageHeaderComponent, MetricCardComponent, StatusBadgeComponent],
  templateUrl: './choferes.component.html',
  styleUrl: './choferes.component.scss'
})
export class ChoferesComponent implements OnInit {
  private readonly choferesService = inject(ChoferesService);
  private readonly empresaService = inject(EmpresaService);
  private readonly formBuilder = inject(FormBuilder);

  protected readonly choferes = signal<Chofer[]>([]);
  protected readonly empresaId = signal<EntityId | null>(null);
  protected readonly showForm = signal(false);
  protected readonly editingId = signal<EntityId | null>(null);
  protected readonly saving = signal(false);
  protected readonly feedback = signal<{ tone: FeedbackTone; message: string } | null>(null);
  protected readonly totalChoferes = computed(() => this.choferes().length.toString());
  protected readonly activos = computed(() => this.choferes().filter((chofer) => chofer.estado === 'ACTIVO').length.toString());
  protected readonly refrigerados = computed(() => this.choferes().filter((chofer) => chofer.transportaRefrigerado).length.toString());
  protected readonly capacidadTotal = computed(() => this.formatQuantity(
    this.choferes().reduce((total, chofer) => total + Number(chofer.capacidad || 0), 0)
  ));
  protected readonly unidadOptions: Array<{ label: string; value: UnidadMedidaInventario }> = [
    { label: 'Kilogramos', value: 'KILOGRAMO' },
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

  protected readonly choferForm = this.formBuilder.nonNullable.group({
    tipoIdentificacion: ['CEDULA' as TipoIdentificacion, Validators.required],
    identificacion: ['', Validators.required],
    nombres: ['', Validators.required],
    apellidos: [''],
    licencia: ['', Validators.required],
    telefono: [''],
    correo: ['', Validators.email],
    placaVehiculo: [''],
    tipoVehiculo: ['Camion'],
    capacidad: [0],
    unidadCapacidad: ['KILOGRAMO' as UnidadMedidaInventario],
    transportaRefrigerado: [false],
    activo: [true]
  });

  ngOnInit(): void {
    this.empresaService.getEmpresa().subscribe((empresa) => this.empresaId.set(empresa?.id ?? null));
    this.loadChoferes();
  }

  protected newChofer(): void {
    if (!this.empresaId()) {
      this.feedback.set({ tone: 'warning', message: 'Primero crea la empresa emisora en la pantalla Empresa.' });
      return;
    }
    this.editingId.set(null);
    this.choferForm.reset({
      tipoIdentificacion: 'CEDULA',
      identificacion: '',
      nombres: '',
      apellidos: '',
      licencia: '',
      telefono: '',
      correo: '',
      placaVehiculo: '',
      tipoVehiculo: 'Camion',
      capacidad: 0,
      unidadCapacidad: 'KILOGRAMO',
      transportaRefrigerado: false,
      activo: true
    });
    this.showForm.set(true);
  }

  protected editChofer(chofer: Chofer): void {
    this.editingId.set(chofer.id);
    this.choferForm.reset({
      tipoIdentificacion: chofer.tipoIdentificacion,
      identificacion: chofer.identificacion,
      nombres: chofer.nombres,
      apellidos: chofer.apellidos,
      licencia: chofer.licencia,
      telefono: chofer.telefono,
      correo: chofer.correo,
      placaVehiculo: chofer.placaVehiculo,
      tipoVehiculo: chofer.tipoVehiculo || 'Camion',
      capacidad: chofer.capacidad || 0,
      unidadCapacidad: chofer.unidadCapacidad || 'KILOGRAMO',
      transportaRefrigerado: chofer.transportaRefrigerado,
      activo: chofer.estado === 'ACTIVO'
    });
    this.showForm.set(true);
  }

  protected cancelForm(): void {
    this.showForm.set(false);
    this.editingId.set(null);
  }

  protected saveChofer(): void {
    const empresaId = this.empresaId();
    if (!empresaId) {
      this.feedback.set({ tone: 'warning', message: 'Primero crea la empresa emisora.' });
      return;
    }
    if (this.choferForm.invalid) {
      this.choferForm.markAllAsTouched();
      this.feedback.set({ tone: 'danger', message: 'Completa identificacion, nombres y licencia.' });
      return;
    }

    this.saving.set(true);
    this.feedback.set(null);
    this.choferesService.save(this.editingId(), this.toPayload(empresaId)).pipe(
      finalize(() => this.saving.set(false))
    ).subscribe({
      next: () => {
        this.showForm.set(false);
        this.editingId.set(null);
        this.feedback.set({ tone: 'success', message: 'Chofer guardado correctamente.' });
        this.loadChoferes();
      },
      error: () => this.feedback.set({ tone: 'danger', message: 'No se pudo guardar el chofer. Revisa que la identificacion no exista previamente.' })
    });
  }

  protected deleteChofer(chofer: Chofer): void {
    this.choferesService.delete(chofer.id).subscribe({
      next: () => {
        this.feedback.set({ tone: 'success', message: 'Chofer inactivado.' });
        this.loadChoferes();
      },
      error: () => this.feedback.set({ tone: 'danger', message: 'No se pudo inactivar el chofer.' })
    });
  }

  protected nombreCompleto(chofer: Chofer): string {
    return `${chofer.nombres} ${chofer.apellidos}`.trim();
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

  private loadChoferes(): void {
    this.choferesService.list().subscribe((items) => this.choferes.set(items));
  }

  private toPayload(empresaId: EntityId): ChoferPayload {
    const value = this.choferForm.getRawValue();
    return {
      empresaId,
      tipoIdentificacion: value.tipoIdentificacion as TipoIdentificacion,
      identificacion: value.identificacion,
      nombres: value.nombres,
      apellidos: value.apellidos || undefined,
      licencia: value.licencia,
      telefono: value.telefono || undefined,
      correo: value.correo || undefined,
      placaVehiculo: value.placaVehiculo || undefined,
      tipoVehiculo: value.tipoVehiculo || undefined,
      capacidad: Number(value.capacidad || 0) || undefined,
      unidadCapacidad: value.unidadCapacidad as UnidadMedidaInventario,
      transportaRefrigerado: value.transportaRefrigerado,
      activo: value.activo
    };
  }
}
