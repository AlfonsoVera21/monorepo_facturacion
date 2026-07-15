import { Component, OnInit, computed, inject, signal } from '@angular/core';
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
  protected readonly timeline = computed<TimelineItem[]>(() => {
    const item = this.comprobante();
    if (!item) {
      return [];
    }

    const events: TimelineItem[] = [
      { title: `Comprobante ${item.numero}`, timestamp: item.fechaEmision, icon: 'receipt_long', status: item.estado }
    ];

    item.mensajesSri.forEach((message, index) => {
      events.push({
        title: message.mensaje,
        timestamp: message.informacionAdicional || message.fecha,
        icon: this.iconForStatus(message.estado),
        status: message.codigo || `SRI ${index + 1}`
      });
    });

    return events;
  });

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.comprobantesService.getById(id).subscribe((item) => this.comprobante.set(item));
    }
  }

  protected openRide(item: Comprobante): void {
    this.comprobantesService.downloadRide(item.id).subscribe((pdf) => this.openBlob(pdf, `${item.numero}.pdf`));
  }

  protected downloadXml(item: Comprobante): void {
    this.comprobantesService.downloadXml(item.id).subscribe((xml) => this.downloadBlob(xml, `${item.numero}.xml`));
  }

  protected documentLabel(item: Comprobante): string {
    const labels: Record<Comprobante['tipo'], string> = {
      FACTURA: 'Factura',
      NOTA_CREDITO: 'Nota de Credito',
      NOTA_DEBITO: 'Nota de Debito',
      GUIA_REMISION: 'Guia de Remision',
      RETENCION: 'Retencion',
      LIQUIDACION_COMPRA: 'Liquidacion de Compra'
    };
    return labels[item.tipo];
  }

  private iconForStatus(status: Comprobante['estado']): string {
    if (status === 'AUTORIZADO') {
      return 'done_all';
    }
    if (status === 'RECHAZADO' || status === 'DEVUELTO' || status === 'ERROR') {
      return 'error';
    }
    return 'sync';
  }

  private openBlob(blob: Blob, fileName: string): void {
    const file = new Blob([blob], { type: 'application/pdf' });
    const url = URL.createObjectURL(file);
    const opened = window.open(url, '_blank', 'noopener');
    if (!opened) {
      this.downloadBlob(file, fileName);
    }
    setTimeout(() => URL.revokeObjectURL(url), 60_000);
  }

  private downloadBlob(blob: Blob, fileName: string): void {
    const url = URL.createObjectURL(blob);
    const anchor = document.createElement('a');
    anchor.href = url;
    anchor.download = fileName;
    anchor.click();
    URL.revokeObjectURL(url);
  }
}
