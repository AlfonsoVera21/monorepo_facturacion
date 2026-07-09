import { Injectable } from '@angular/core';
import { Observable, delay, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ReportesService {
  getVentasMensuales(): Observable<{ mes: string; ventas: number }[]> {
    return of([
      { mes: 'ENE', ventas: 28400 },
      { mes: 'FEB', ventas: 32150 },
      { mes: 'MAR', ventas: 29880 },
      { mes: 'ABR', ventas: 37420 },
      { mes: 'MAY', ventas: 45120 },
      { mes: 'JUN', ventas: 50650 }
    ]).pipe(delay(120));
  }
}
