import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';

import { Cliente, EntityId, TipoIdentificacion } from '../../core/models/factuec.models';
import { ClientePayload, ClientesService } from '../../core/services/clientes.service';
import { EmpresaService } from '../../core/services/empresa.service';
import { MetricCardComponent } from '../../shared/components/metric-card/metric-card.component';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

type FeedbackTone = 'success' | 'danger' | 'warning';

@Component({
  selector: 'app-clientes',
  imports: [ReactiveFormsModule, PageHeaderComponent, MetricCardComponent, StatusBadgeComponent],
  templateUrl: './clientes.component.html',
  styleUrl: './clientes.component.scss'
})
export class ClientesComponent implements OnInit {
  private readonly clientesService = inject(ClientesService);
  private readonly empresaService = inject(EmpresaService);
  private readonly formBuilder = inject(FormBuilder);

  protected readonly clientes = signal<Cliente[]>([]);
  protected readonly empresaId = signal<EntityId | null>(null);
  protected readonly showForm = signal(false);
  protected readonly editingId = signal<EntityId | null>(null);
  protected readonly saving = signal(false);
  protected readonly feedback = signal<{ tone: FeedbackTone; message: string } | null>(null);
  protected readonly totalClientes = computed(() => this.clientes().length.toString());
  protected readonly activos = computed(() => this.clientes().filter((cliente) => cliente.estado === 'ACTIVO').length.toString());
  protected readonly inactivos = computed(() => this.clientes().filter((cliente) => cliente.estado !== 'ACTIVO').length.toString());

  protected readonly clienteForm = this.formBuilder.nonNullable.group({
    tipoIdentificacion: ['RUC', Validators.required],
    identificacion: ['', Validators.required],
    razonSocial: ['', Validators.required],
    correo: ['cliente@correo.com', Validators.email],
    telefono: [''],
    direccion: [''],
    ciudad: [''],
    provincia: [''],
    activo: [true]
  });

  ngOnInit(): void {
    this.empresaService.getEmpresa().subscribe((empresa) => this.empresaId.set(empresa?.id ?? null));
    this.loadClientes();
  }

  protected newCliente(): void {
    if (!this.empresaId()) {
      this.feedback.set({ tone: 'warning', message: 'Primero crea la empresa emisora en la pantalla Empresa.' });
      return;
    }
    this.editingId.set(null);
    this.clienteForm.reset({
      tipoIdentificacion: 'RUC',
      identificacion: '',
      razonSocial: '',
      correo: 'cliente@correo.com',
      telefono: '',
      direccion: '',
      ciudad: '',
      provincia: '',
      activo: true
    });
    this.showForm.set(true);
  }

  protected editCliente(cliente: Cliente): void {
    this.editingId.set(cliente.id);
    this.clienteForm.reset({
      tipoIdentificacion: cliente.tipoIdentificacion,
      identificacion: cliente.identificacion,
      razonSocial: cliente.razonSocial,
      correo: cliente.email,
      telefono: cliente.telefono,
      direccion: cliente.direccion,
      ciudad: '',
      provincia: '',
      activo: cliente.estado === 'ACTIVO'
    });
    this.showForm.set(true);
  }

  protected cancelForm(): void {
    this.showForm.set(false);
    this.editingId.set(null);
  }

  protected saveCliente(): void {
    const empresaId = this.empresaId();
    if (!empresaId) {
      this.feedback.set({ tone: 'warning', message: 'Primero crea la empresa emisora.' });
      return;
    }
    if (this.clienteForm.invalid) {
      this.clienteForm.markAllAsTouched();
      this.feedback.set({ tone: 'danger', message: 'Completa identificacion y razon social del cliente.' });
      return;
    }

    this.saving.set(true);
    this.feedback.set(null);
    this.clientesService.save(this.editingId(), this.toPayload(empresaId)).pipe(
      finalize(() => this.saving.set(false))
    ).subscribe({
      next: () => {
        this.showForm.set(false);
        this.editingId.set(null);
        this.feedback.set({ tone: 'success', message: 'Cliente guardado correctamente.' });
        this.loadClientes();
      },
      error: () => this.feedback.set({ tone: 'danger', message: 'No se pudo guardar el cliente. Revisa identificacion y correo.' })
    });
  }

  protected deleteCliente(cliente: Cliente): void {
    this.clientesService.delete(cliente.id).subscribe({
      next: () => {
        this.feedback.set({ tone: 'success', message: 'Cliente inactivado.' });
        this.loadClientes();
      },
      error: () => this.feedback.set({ tone: 'danger', message: 'No se pudo inactivar el cliente.' })
    });
  }

  private loadClientes(): void {
    this.clientesService.list().subscribe((items) => this.clientes.set(items));
  }

  private toPayload(empresaId: EntityId): ClientePayload {
    const value = this.clienteForm.getRawValue();
    return {
      empresaId,
      tipoIdentificacion: value.tipoIdentificacion as TipoIdentificacion,
      identificacion: value.identificacion,
      razonSocial: value.razonSocial,
      nombreComercial: value.razonSocial,
      correo: value.correo || undefined,
      telefono: value.telefono || undefined,
      direccion: value.direccion || undefined,
      ciudad: value.ciudad || undefined,
      provincia: value.provincia || undefined,
      activo: value.activo
    };
  }
}
