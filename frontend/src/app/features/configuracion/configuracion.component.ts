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
    serieDefault: [''],
    ambiente: ['PRUEBAS'],
    correoNotificaciones: [''],
    enviarRideAutomatico: [false],
    reintentosSri: [''],
    mfaObligatorio: [false]
  });

  ngOnInit(): void {
    this.configuracionService.getGeneralSettings().subscribe((settings) => {
      this.settingsForm.patchValue({
        serieDefault: settings.serieDefault,
        ambiente: settings.ambiente,
        correoNotificaciones: settings.correoNotificaciones,
        enviarRideAutomatico: settings.enviarRideAutomatico,
        reintentosSri: settings.reintentosSri,
        mfaObligatorio: settings.mfaObligatorio
      });
    });
  }
}
