import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';

import { AuditLog, Empresa, FirmaElectronica } from '../../core/models/factuec.models';
import { EmpresaService } from '../../core/services/empresa.service';
import { CertificadoDisponible, FirmaElectronicaService } from '../../core/services/firma-electronica.service';
import { AlertCardComponent } from '../../shared/components/alert-card/alert-card.component';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

type FeedbackTone = 'success' | 'danger' | 'warning';

@Component({
  selector: 'app-firma-electronica',
  imports: [ReactiveFormsModule, PageHeaderComponent, StatusBadgeComponent, AlertCardComponent],
  templateUrl: './firma-electronica.component.html',
  styleUrl: './firma-electronica.component.scss'
})
export class FirmaElectronicaComponent implements OnInit {
  private readonly firmaService = inject(FirmaElectronicaService);
  private readonly empresaService = inject(EmpresaService);
  private readonly formBuilder = inject(FormBuilder);

  protected readonly empresa = signal<Empresa | null>(null);
  protected readonly firma = signal<FirmaElectronica | undefined>(undefined);
  protected readonly certificados = signal<CertificadoDisponible[]>([]);
  protected readonly logs = signal<AuditLog[]>([]);
  protected readonly saving = signal(false);
  protected readonly feedback = signal<{ tone: FeedbackTone; message: string } | null>(null);
  protected readonly firmaForm = this.formBuilder.nonNullable.group({
    nombreArchivo: ['', Validators.required],
    rutaSegura: ['', Validators.required],
    passwordRef: ['FIRMA_ELECTRONICA_PASSWORD', Validators.required],
    fechaEmision: [''],
    fechaVencimiento: [''],
    estado: ['ACTIVA', Validators.required]
  });

  ngOnInit(): void {
    this.empresaService.getEmpresa().subscribe((empresa) => this.empresa.set(empresa));
    this.firmaService.listCertificados().subscribe((certificados) => {
      this.certificados.set(certificados);
      if (certificados.length && !this.firmaForm.controls.rutaSegura.value) {
        this.selectCertificado(certificados[0].rutaSegura);
      }
    });
    this.firmaService.getCurrent().subscribe((firma) => {
      this.firma.set(firma);
      if (firma) {
        this.firmaForm.patchValue({
          nombreArchivo: firma.archivoNombre,
          rutaSegura: firma.emisor,
          passwordRef: 'FIRMA_ELECTRONICA_PASSWORD',
          fechaVencimiento: firma.fechaVencimiento || '',
          estado: firma.estado === 'VALIDO' || firma.estado === 'POR_VENCER' ? 'ACTIVA' : 'INACTIVA'
        });
      }
    });
    this.firmaService.getAuditTrail().subscribe((logs) => this.logs.set(logs));
  }

  protected selectCertificado(rutaSegura: string): void {
    const certificado = this.certificados().find((item) => item.rutaSegura === rutaSegura);
    this.firmaForm.patchValue({
      rutaSegura,
      nombreArchivo: certificado?.nombreArchivo || this.firmaForm.controls.nombreArchivo.value
    });
  }

  protected saveFirma(): void {
    const empresa = this.empresa();
    if (!empresa) {
      this.feedback.set({ tone: 'warning', message: 'Primero crea la empresa emisora con ambiente PRUEBAS.' });
      return;
    }
    if (this.firmaForm.invalid) {
      this.firmaForm.markAllAsTouched();
      this.feedback.set({ tone: 'danger', message: 'Selecciona un certificado y confirma la referencia del secreto.' });
      return;
    }

    const value = this.firmaForm.getRawValue();
    this.saving.set(true);
    this.feedback.set(null);
    this.firmaService.saveFirma(this.firma()?.id, {
      empresaId: empresa.id,
      nombreArchivo: value.nombreArchivo,
      rutaSegura: value.rutaSegura,
      passwordSecretRef: value.passwordRef,
      fechaEmision: value.fechaEmision || undefined,
      fechaVencimiento: value.fechaVencimiento || undefined,
      estado: 'ACTIVA'
    }).pipe(
      finalize(() => this.saving.set(false))
    ).subscribe({
      next: (firma) => {
        this.firma.set(firma);
        this.feedback.set({ tone: 'success', message: 'Firma registrada y validada por el backend.' });
      },
      error: () => this.feedback.set({ tone: 'danger', message: 'No se pudo validar la firma. Revisa la clave en FIRMA_ELECTRONICA_PASSWORD y el archivo .p12/.pfx.' })
    });
  }
}
