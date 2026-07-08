import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { Comprobante } from '../../../core/models/factuec.models';
import { ComprobantesService } from '../../../core/services/comprobantes.service';
import { BreadcrumbComponent } from '../../../shared/components/breadcrumb/breadcrumb.component';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';
import { TimelineComponent, TimelineItem } from '../../../shared/components/timeline/timeline.component';

@Component({
  selector: 'app-comprobante-detail',
  imports: [RouterLink, BreadcrumbComponent, StatusBadgeComponent, TimelineComponent],
  templateUrl: './comprobante-detail.component.html',
  styleUrl: './comprobante-detail.component.scss'
})
export class ComprobanteDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly comprobantesService = inject(ComprobantesService);

  protected readonly comprobante = signal<Comprobante | undefined>(undefined);
  protected readonly timeline: TimelineItem[] = [
    { title: 'Comprobante Creado', timestamp: '08 Jul, 14:30:05', icon: 'check' },
    { title: 'Firmado Electronicamente', timestamp: '08 Jul, 14:30:08', icon: 'check' },
    { title: 'Enviado al SRI', timestamp: '08 Jul, 14:30:15', icon: 'check' },
    { title: 'Recibido por SRI', timestamp: '08 Jul, 14:30:22', icon: 'check' },
    { title: 'Autorizado', timestamp: '08 Jul, 14:35:12', icon: 'done_all', status: 'EXITO' }
  ];

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.comprobantesService.getById(id).subscribe((item) => this.comprobante.set(item));
  }
}
