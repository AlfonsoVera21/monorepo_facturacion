import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-alert-card',
  templateUrl: './alert-card.component.html',
  styleUrl: './alert-card.component.scss'
})
export class AlertCardComponent {
  @Input() title = '';
  @Input() description = '';
  @Input() icon = 'warning';
  @Input() tone: 'info' | 'warning' | 'danger' | 'success' = 'info';
  @Input() actionLabel = '';
}
