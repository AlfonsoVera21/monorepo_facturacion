import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { ApiService } from './api.service';
import { ConfiguracionGeneralResponseDto } from './backend-api.models';

@Injectable({
  providedIn: 'root'
})
export class ConfiguracionService {
  private readonly apiService = inject(ApiService);

  getGeneralSettings(): Observable<ConfiguracionGeneralResponseDto> {
    return this.apiService.get<ConfiguracionGeneralResponseDto>('/configuracion/general');
  }
}
