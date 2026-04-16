import {Bill} from '../bill/model';
import {User} from '../user/model';

export enum SubmissionStatus {
  OPEN = "OPEN",
  CLOSED = "CLOSED"
}

export interface InsuranceSubmission {
  id?: string;
  dateTime?: Date;
  name?: string;
  eClaimId?: string;
  status?: SubmissionStatus;
  user?: User;
}

export interface InsuranceSubmissionWithBills extends InsuranceSubmission {
  bills: Bill[];
  totalUsdAmount?: number;
  reimbursedAmount?: number;
}

export interface CreateUpdateInsuranceSubmissionRequest {
  name?: string;
  eClaimId?: string;
  billIds?: string[];
}
