import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';

import { AuditLog, FirmaElectronica } from '../../core/models/factuec.models';
import { FirmaElectronicaService } from '../../core/services/firma-electronica.service';
import { AlertCardComponent } from '../../shared/components/alert-card/alert-card.component';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-firma-electronica',
  imports: [ReactiveFormsModule, PageHeaderComponent, StatusBadgeComponent, AlertCardComponent],
  templateUrl: './firma-electronica.component.html',
  styleUrl: './firma-electronica.component.scss'
})
export class FirmaElectronicaComponent implements OnInit {
  private readonly firmaService = inject(FirmaElectronicaService);
  private readonly formBuilder = inject(FormBuilder);

  protected readonly firma = signal<FirmaElectronica | undefined>(undefined);
  protected readonly logs = signal<AuditLog[]>([]);
  protected readonly firmaForm = this.formBuilder.nonNullable.group({
    archivo: ['firma_v2026.p12'],
    password: [''],
    passwordRef: ['FACTUEC_CERT_PASSWORD']
  });

  ngOnInit(): void {
    this.firmaService.getCurrent().subscribe((firma) => this.firma.set(firma));
    this.firmaService.getAuditTrail().subscribe((logs) => this.logs.set(logs));
  }
}
