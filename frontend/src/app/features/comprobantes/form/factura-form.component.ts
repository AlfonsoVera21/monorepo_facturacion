import { Component, inject } from '@angular/core';
import { FormArray, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { BreadcrumbComponent } from '../../../shared/components/breadcrumb/breadcrumb.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';

@Component({
  selector: 'app-factura-form',
  imports: [ReactiveFormsModule, BreadcrumbComponent, PageHeaderComponent],
  templateUrl: './factura-form.component.html',
  styleUrl: './factura-form.component.scss'
})
export class FacturaFormComponent {
  private readonly formBuilder = inject(FormBuilder);

  protected readonly facturaForm = this.formBuilder.nonNullable.group({
    secuencial: ['001-001-000000125', Validators.required],
    fechaEmision: ['2026-07-08', Validators.required],
    moneda: ['USD', Validators.required],
    guiaRemision: [''],
    clienteBusqueda: ['Corporacion Favorita C.A.'],
    razonSocial: ['Consumidor Final', Validators.required],
    identificacion: ['9999999999999', Validators.required],
    correo: ['sin_correo@factuec.com', [Validators.required, Validators.email]],
    detalles: this.formBuilder.array([this.createDetail('SERV-109', 'Mantenimiento de Servidores Cloud', 1, 450, '15%')]),
    pago: this.formBuilder.nonNullable.group({
      formaPago: ['SIN UTILIZACION DEL SISTEMA FINANCIERO'],
      valor: [504, Validators.min(0)],
      plazo: [0, Validators.min(0)],
      unidadTiempo: ['DIAS']
    })
  });

  protected subtotal(): number {
    return this.detalles.controls.reduce((total, group) => total + this.lineSubtotal(group.getRawValue()), 0);
  }

  protected iva(): number {
    return this.detalles.controls.reduce((total, group) => {
      const line = group.getRawValue();
      const rate = line.tarifaIva === '15%' ? 0.15 : line.tarifaIva === '12%' ? 0.12 : 0;
      return total + this.lineSubtotal(line) * rate;
    }, 0);
  }

  protected total(): number {
    return this.subtotal() + this.iva();
  }

  protected get detalles(): FormArray {
    return this.facturaForm.controls.detalles;
  }

  protected addItem(): void {
    this.detalles.push(this.createDetail('PROD-001', 'Laptop Dell Latitude 5420', 1, 850, '15%'));
  }

  protected removeItem(index: number): void {
    if (this.detalles.length > 1) {
      this.detalles.removeAt(index);
    }
  }

  protected lineSubtotal(line: { cantidad: number; precioUnitario: number; descuento: number }): number {
    const rawSubtotal = Number(line.cantidad || 0) * Number(line.precioUnitario || 0);
    const discount = rawSubtotal * (Number(line.descuento || 0) / 100);
    return rawSubtotal - discount;
  }

  private createDetail(codigo: string, descripcion: string, cantidad: number, precioUnitario: number, tarifaIva: string) {
    return this.formBuilder.nonNullable.group({
      codigo: [codigo, Validators.required],
      descripcion: [descripcion, Validators.required],
      cantidad: [cantidad, [Validators.required, Validators.min(0.01)]],
      precioUnitario: [precioUnitario, [Validators.required, Validators.min(0)]],
      descuento: [0, [Validators.min(0), Validators.max(100)]],
      tarifaIva: [tarifaIva, Validators.required]
    });
  }
}
