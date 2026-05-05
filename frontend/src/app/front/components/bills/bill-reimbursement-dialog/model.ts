import {Currency} from '../../../../services/billkeeper-ws/bill/model';

export interface BillReimbursementRow {
  id: string;
  name?: string;
  amount?: number;
  currency?: Currency;
  reimbursementDateTime: string;
  reimbursedAmount?: number;
}
