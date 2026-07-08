import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-loading-skeleton',
  templateUrl: './loading-skeleton.component.html',
  styleUrl: './loading-skeleton.component.scss'
})
export class LoadingSkeletonComponent {
  @Input() rows = 4;
  protected readonly rowKeys = Array.from({ length: 8 }, (_, index) => index);
}
