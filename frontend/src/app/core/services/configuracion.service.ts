import { Injectable } from '@angular/core';
import { Observable, delay, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ConfiguracionService {
  getGeneralSettings(): Observable<Record<string, string | boolean>> {
    return of({
      serieDefault: '001-001',
      ambiente: 'PRODUCCION',
      correoNotificaciones: 'notificaciones@soltec.ec',
      enviarRideAutomatico: true,
      reintentosSri: '3',
      mfaObligatorio: true
    }).pipe(delay(120));
  }
}
