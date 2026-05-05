import {InsuranceSubmissionWithBills} from '../../../../services/billkeeper-ws/submission/model';
import {MenuItem} from 'primeng/api';

export interface SubmissionTableRow {
  id: string | undefined;
  submission: InsuranceSubmissionWithBills;
  menuItems: MenuItem[];
}
