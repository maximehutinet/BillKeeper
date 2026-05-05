import {Component, Input} from '@angular/core';
import {Skeleton} from 'primeng/skeleton';

@Component({
  selector: 'app-value-loading-or-ns',
  imports: [
    Skeleton
  ],
  templateUrl: './value-loading-or-ns.component.html',
  styleUrl: './value-loading-or-ns.component.scss'
})
export class ValueLoadingOrNsComponent {

  @Input()
  value: any | undefined;

  @Input()
  loading = false;

}
