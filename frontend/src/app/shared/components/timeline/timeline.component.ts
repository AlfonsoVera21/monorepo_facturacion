import { Component, Input } from '@angular/core';

export interface TimelineItem {
  title: string;
  timestamp: string;
  status?: string;
  icon?: string;
}

@Component({
  selector: 'app-timeline',
  templateUrl: './timeline.component.html',
  styleUrl: './timeline.component.scss'
})
export class TimelineComponent {
  @Input() items: TimelineItem[] = [];
}
