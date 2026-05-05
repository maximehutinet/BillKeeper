import {Bill} from '../../../../services/billkeeper-ws/bill/model';
import {MenuItem} from 'primeng/api';

export interface BillTableRow {
  checked: boolean;
  bill: Bill;
  menuItems: MenuItem[];
}
