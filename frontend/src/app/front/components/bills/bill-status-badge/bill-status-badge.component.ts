import {Component, Input} from '@angular/core';
import {Bill, BillStatus} from '../../../../services/billkeeper-ws/bill/model';
import {Badge} from 'primeng/badge';
import {billStatusBadge, billStatusToString} from '../../../../services/utils';
import {RouterLink} from '@angular/router';
import {Tooltip} from 'primeng/tooltip';

@Component({
  selector: 'app-bill-status-badge',
  imports: [
    Badge,
    RouterLink,
    Tooltip
  ],
  templateUrl: './bill-status-badge.component.html',
  styleUrl: './bill-status-badge.component.scss'
})
export class BillStatusBadgeComponent {

  protected readonly BillStatus = BillStatus;
  protected readonly billStatusToString = billStatusToString;
  protected readonly billStatusBadge = billStatusBadge;

  @Input()
  bill: Bill = {};

}
