import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-metric-card',
  templateUrl: './metric-card.component.html',
  styleUrl: './metric-card.component.scss'
})
export class MetricCardComponent {
  @Input({ required: true }) title = '';
  @Input({ required: true }) value = '';
  @Input() icon = 'analytics';
  @Input() trend = '';
  @Input() tone: 'primary' | 'success' | 'warning' | 'danger' | 'neutral' = 'primary';
}
