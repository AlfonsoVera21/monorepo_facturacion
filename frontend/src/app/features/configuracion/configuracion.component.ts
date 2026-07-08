import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';

import { ConfiguracionService } from '../../core/services/configuracion.service';
import { AlertCardComponent } from '../../shared/components/alert-card/alert-card.component';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';

@Component({
  selector: 'app-configuracion',
  imports: [ReactiveFormsModule, PageHeaderComponent, AlertCardComponent],
  templateUrl: './configuracion.component.html',
  styleUrl: './configuracion.component.scss'
})
export class ConfiguracionComponent implements OnInit {
  private readonly configuracionService = inject(ConfiguracionService);
  private readonly formBuilder = inject(FormBuilder);

  protected readonly settingsForm = this.formBuilder.nonNullable.group({
    serieDefault: ['001-001'],
    ambiente: ['PRODUCCION'],
    correoNotificaciones: ['notificaciones@soltec.ec'],
    enviarRideAutomatico: [true],
    reintentosSri: ['3'],
    mfaObligatorio: [true]
  });

  ngOnInit(): void {
    this.configuracionService.getGeneralSettings().subscribe((settings) => this.settingsForm.patchValue(settings));
  }
}
