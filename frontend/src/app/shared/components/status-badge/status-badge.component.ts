import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-status-badge',
  templateUrl: './status-badge.component.html',
  styleUrl: './status-badge.component.scss'
})
export class StatusBadgeComponent {
  @Input({ required: true }) status = '';

  protected get normalized(): string {
    return this.status.toLowerCase().replaceAll('_', '-');
  }

  protected get label(): string {
    return this.status.replaceAll('_', ' ');
  }
}
