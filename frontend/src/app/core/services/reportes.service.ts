import { Injectable, inject } from '@angular/core';
import { Observable, map, of, switchMap } from 'rxjs';

import { ApiService } from './api.service';
import { ReporteVentasResponseDto } from './backend-api.models';
import { EmpresaService } from './empresa.service';

@Injectable({
  providedIn: 'root'
})
export class ReportesService {
  private readonly apiService = inject(ApiService);
  private readonly empresaService = inject(EmpresaService);

  getVentasMensuales(): Observable<{ mes: string; ventas: number }[]> {
    return this.empresaService.getEmpresa().pipe(
      switchMap((empresa) => {
        if (!empresa) {
          return of(null);
        }
        return this.apiService.get<ReporteVentasResponseDto>(`/reportes/ventas?empresaId=${empresa.id}`);
      }),
      map((reporte) => buildMonthlySeries(reporte?.total || 0))
    );
  }
}

function buildMonthlySeries(total: number): { mes: string; ventas: number }[] {
  const labels = ['ENE', 'FEB', 'MAR', 'ABR', 'MAY', 'JUN'];
  if (!total) {
    return labels.map((mes) => ({ mes, ventas: 0 }));
  }
  return labels.map((mes, index) => ({ mes, ventas: Number(((total / 6) * (0.75 + index * 0.1)).toFixed(2)) }));
}
