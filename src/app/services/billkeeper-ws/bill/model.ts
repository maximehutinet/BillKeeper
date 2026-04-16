import {Beneficiary} from '../beneficiary/model';
import {User} from '../user/model';

export interface Bill {
  id?: string;
  dateTime?: Date;
  name?: string;
  amount?: number;
  user?: User;
  currency?: Currency;
  serviceDateTime?: Date;
  paidDateTime?: Date;
  provider?: string;
  status?: BillStatus;
  beneficiary?: Beneficiary;
  submissionId?: string;
  parsingJobStatus?: ParsingJobStatus;
  reimbursementDateTime?: Date;
  reimbursedAmount?: number;
}

export enum Currency {
  CHF = "CHF",
  EURO = "EUR"
}

export enum BillStatus {
  TO_PAY = "TO_PAY",
  TO_FILE = "TO_FILE",
  FILING_IN_PROGRESS = "FILING_IN_PROGRESS",
  FILED = "FILED",
  REIMBURSEMENT_IN_PROGRESS = "REIMBURSEMENT_IN_PROGRESS",
  REIMBURSED = "REIMBURSED",
  REJECTED = "REJECTED"
}

export enum ParsingJobStatus {
  IN_PROGRESS = "IN_PROGRESS",
  SUCCESS = "SUCCESS",
  FAILED = "FAILED"
}


