import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';

import { AmbienteSri, Empresa, Establecimiento, EntityId } from '../../core/models/factuec.models';
import { EmpresaPayload, EmpresaService } from '../../core/services/empresa.service';
import { AlertCardComponent } from '../../shared/components/alert-card/alert-card.component';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

type FeedbackTone = 'success' | 'danger' | 'warning';

@Component({
  selector: 'app-empresa',
  imports: [ReactiveFormsModule, PageHeaderComponent, StatusBadgeComponent, AlertCardComponent],
  templateUrl: './empresa.component.html',
  styleUrl: './empresa.component.scss'
})
export class EmpresaComponent implements OnInit {
  private readonly empresaService = inject(EmpresaService);
  private readonly formBuilder = inject(FormBuilder);

  protected readonly empresa = signal<Empresa | null>(null);
  protected readonly establecimientos = signal<Establecimiento[]>([]);
  protected readonly empresaId = computed(() => this.empresa()?.id ?? null);
  protected readonly showEstablecimientoForm = signal(false);
  protected readonly editingEstablecimientoId = signal<EntityId | null>(null);
  protected readonly selectedEstablecimientoId = signal<EntityId | null>(null);
  protected readonly savingEmpresa = signal(false);
  protected readonly savingEstablecimiento = signal(false);
  protected readonly savingPunto = signal(false);
  protected readonly feedback = signal<{ tone: FeedbackTone; message: string } | null>(null);
  protected readonly selectedEstablecimiento = computed(() => {
    const selectedId = this.selectedEstablecimientoId();
    return this.establecimientos().find((item) => item.id === selectedId) ?? null;
  });

  protected readonly empresaForm = this.formBuilder.nonNullable.group({
    ruc: ['', [Validators.required, Validators.pattern(/^\d{13}$/)]],
    razonSocial: ['', Validators.required],
    nombreComercial: [''],
    direccionMatriz: ['', Validators.required],
    obligadoContabilidad: [false],
    ambiente: ['PRUEBAS', Validators.required],
    tipoEmision: ['NORMAL']
  });

  protected readonly establecimientoForm = this.formBuilder.nonNullable.group({
    codigo: ['', [Validators.required, Validators.pattern(/^\d{3}$/)]],
    nombre: ['', Validators.required],
    direccion: ['', Validators.required],
    activo: [true]
  });

  protected readonly puntoForm = this.formBuilder.nonNullable.group({
    establecimientoId: [''],
    codigo: ['001', [Validators.required, Validators.pattern(/^\d{3}$/)]],
    nombre: ['PUNTO DE EMISION 001', Validators.required],
    ultimoSecuencial: [0, [Validators.required, Validators.min(0)]],
    activo: [true]
  });

  ngOnInit(): void {
    this.empresaService.getEmpresa().subscribe((empresa) => {
      if (empresa) {
        this.empresa.set(empresa);
        this.empresaForm.patchValue(empresa);
        this.loadEstablecimientos(empresa.id);
      }
    });
  }

  protected saveEmpresa(): void {
    if (this.empresaForm.invalid) {
      this.empresaForm.markAllAsTouched();
      this.feedback.set({ tone: 'danger', message: 'Completa RUC, razon social y direccion matriz antes de guardar.' });
      return;
    }

    this.savingEmpresa.set(true);
    this.feedback.set(null);
    this.empresaService.saveEmpresa(this.empresaId(), this.toEmpresaPayload()).pipe(
      finalize(() => this.savingEmpresa.set(false))
    ).subscribe({
      next: (empresa) => {
        this.empresa.set(empresa);
        this.empresaForm.patchValue(empresa);
        this.feedback.set({ tone: 'success', message: 'Empresa guardada. Ya puedes registrar sus establecimientos.' });
        this.loadEstablecimientos(empresa.id);
      },
      error: () => this.feedback.set({ tone: 'danger', message: 'No se pudo guardar la empresa. Revisa que el RUC no exista previamente.' })
    });
  }

  protected newEstablecimiento(): void {
    if (!this.empresaId()) {
      this.feedback.set({ tone: 'warning', message: 'Primero guarda la empresa para poder crear establecimientos.' });
      return;
    }
    const nextCode = this.nextCode(this.establecimientos().map((item) => item.codigo));
    this.editingEstablecimientoId.set(null);
    this.establecimientoForm.reset({
      codigo: nextCode,
      nombre: nextCode === '001' ? 'MATRIZ' : `ESTABLECIMIENTO ${nextCode}`,
      direccion: this.empresaForm.controls.direccionMatriz.value,
      activo: true
    });
    this.showEstablecimientoForm.set(true);
  }

  protected editEstablecimiento(establecimiento: Establecimiento): void {
    this.editingEstablecimientoId.set(establecimiento.id);
    this.establecimientoForm.reset({
      codigo: establecimiento.codigo,
      nombre: establecimiento.nombre,
      direccion: establecimiento.direccion,
      activo: establecimiento.activo
    });
    this.showEstablecimientoForm.set(true);
  }

  protected cancelEstablecimiento(): void {
    this.showEstablecimientoForm.set(false);
    this.editingEstablecimientoId.set(null);
  }

  protected saveEstablecimiento(): void {
    const empresaId = this.empresaId();
    if (!empresaId) {
      this.feedback.set({ tone: 'warning', message: 'Guarda primero la empresa.' });
      return;
    }
    if (this.establecimientoForm.invalid) {
      this.establecimientoForm.markAllAsTouched();
      this.feedback.set({ tone: 'danger', message: 'El establecimiento requiere codigo de 3 digitos, nombre y direccion.' });
      return;
    }

    const value = this.establecimientoForm.getRawValue();
    this.savingEstablecimiento.set(true);
    this.feedback.set(null);
    this.empresaService.saveEstablecimiento(this.editingEstablecimientoId(), {
      empresaId,
      codigo: value.codigo,
      nombre: value.nombre,
      direccion: value.direccion,
      activo: value.activo
    }).pipe(
      finalize(() => this.savingEstablecimiento.set(false))
    ).subscribe({
      next: (establecimiento) => {
        this.showEstablecimientoForm.set(false);
        this.editingEstablecimientoId.set(null);
        this.selectedEstablecimientoId.set(establecimiento.id);
        this.puntoForm.patchValue({ establecimientoId: String(establecimiento.id) });
        this.feedback.set({ tone: 'success', message: 'Establecimiento guardado. Ahora agrega su punto de emision.' });
        this.loadEstablecimientos(empresaId);
      },
      error: () => this.feedback.set({ tone: 'danger', message: 'No se pudo guardar el establecimiento.' })
    });
  }

  protected preparePunto(establecimiento: Establecimiento): void {
    this.selectedEstablecimientoId.set(establecimiento.id);
    this.puntoForm.reset({
      establecimientoId: String(establecimiento.id),
      codigo: this.nextCode(establecimiento.puntosEmision.map((item) => item.codigo)),
      nombre: `PUNTO DE EMISION ${this.nextCode(establecimiento.puntosEmision.map((item) => item.codigo))}`,
      ultimoSecuencial: 0,
      activo: true
    });
  }

  protected savePunto(): void {
    const empresaId = this.empresaId();
    const establecimientoId = this.selectedEstablecimientoId();
    if (!empresaId || !establecimientoId) {
      this.feedback.set({ tone: 'warning', message: 'Selecciona un establecimiento para agregar el punto de emision.' });
      return;
    }
    if (this.puntoForm.invalid) {
      this.puntoForm.markAllAsTouched();
      this.feedback.set({ tone: 'danger', message: 'El punto requiere codigo de 3 digitos, nombre y secuencial inicial.' });
      return;
    }

    const value = this.puntoForm.getRawValue();
    this.savingPunto.set(true);
    this.feedback.set(null);
    this.empresaService.createPuntoEmision(empresaId, {
      establecimientoId,
      codigo: value.codigo,
      nombre: value.nombre,
      ultimoSecuencial: Number(value.ultimoSecuencial || 0),
      activo: value.activo
    }).pipe(
      finalize(() => this.savingPunto.set(false))
    ).subscribe({
      next: () => {
        this.feedback.set({ tone: 'success', message: 'Punto de emision creado con secuencial de factura.' });
        this.loadEstablecimientos(empresaId);
      },
      error: () => this.feedback.set({ tone: 'danger', message: 'No se pudo crear el punto de emision.' })
    });
  }

  protected discardChanges(): void {
    const empresa = this.empresa();
    if (empresa) {
      this.empresaForm.patchValue(empresa);
      return;
    }
    this.empresaForm.reset({
      ruc: '',
      razonSocial: '',
      nombreComercial: '',
      direccionMatriz: '',
      obligadoContabilidad: false,
      ambiente: 'PRUEBAS',
      tipoEmision: 'NORMAL'
    });
  }

  private loadEstablecimientos(empresaId: EntityId): void {
    this.empresaService.getEstablecimientos(empresaId).subscribe((items) => this.establecimientos.set(items));
  }

  private toEmpresaPayload(): EmpresaPayload {
    const value = this.empresaForm.getRawValue();
    return {
      ruc: value.ruc,
      razonSocial: value.razonSocial,
      nombreComercial: value.nombreComercial,
      direccionMatriz: value.direccionMatriz,
      obligadoContabilidad: value.obligadoContabilidad,
      ambiente: value.ambiente as AmbienteSri,
      activo: true
    };
  }

  private nextCode(codes: string[]): string {
    const next = codes
      .map((code) => Number(code))
      .filter((code) => Number.isFinite(code))
      .reduce((max, code) => Math.max(max, code), 0) + 1;
    return String(next).padStart(3, '0');
  }
}
