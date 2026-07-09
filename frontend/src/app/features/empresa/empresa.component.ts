import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';

import { Establecimiento } from '../../core/models/factuec.models';
import { EmpresaService } from '../../core/services/empresa.service';
import { AlertCardComponent } from '../../shared/components/alert-card/alert-card.component';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-empresa',
  imports: [ReactiveFormsModule, PageHeaderComponent, StatusBadgeComponent, AlertCardComponent],
  templateUrl: './empresa.component.html',
  styleUrl: './empresa.component.scss'
})
export class EmpresaComponent implements OnInit {
  private readonly empresaService = inject(EmpresaService);
  private readonly formBuilder = inject(FormBuilder);

  protected readonly establecimientos = signal<Establecimiento[]>([]);
  protected readonly empresaForm = this.formBuilder.nonNullable.group({
    ruc: [''],
    razonSocial: [''],
    nombreComercial: [''],
    direccionMatriz: [''],
    obligadoContabilidad: [true],
    ambiente: ['PRODUCCION'],
    tipoEmision: ['NORMAL']
  });

  ngOnInit(): void {
    this.empresaService.getEmpresa().subscribe((empresa) => this.empresaForm.patchValue(empresa));
    this.empresaService.getEstablecimientos().subscribe((items) => this.establecimientos.set(items));
  }
}
