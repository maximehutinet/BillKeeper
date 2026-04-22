import {BillStatus} from './billkeeper-ws/bill/model';
import {InsuranceSubmissionWithBills, SubmissionStatus} from './billkeeper-ws/submission/model';

export function billStatusToString(status: BillStatus | undefined): string {
  switch (status) {
    case BillStatus.TO_PAY:
      return "To pay";
    case BillStatus.TO_FILE:
      return "To file";
    case BillStatus.FILING_IN_PROGRESS:
      return "Filing in progress";
    case BillStatus.FILED:
      return "Filed";
    case BillStatus.REIMBURSEMENT_IN_PROGRESS:
      return "Reimbursement in progress";
    case BillStatus.REIMBURSED:
      return "Reimbursed";
    case BillStatus.REJECTED:
      return "Rejected";
    default:
      return "Status missing";
  }
}

export function billStatusBadge(status: BillStatus | undefined): "info" | "success" | "warn" | "danger" | "secondary" | "contrast" {
  switch (status) {
    case BillStatus.TO_PAY:
      return "danger";
    case BillStatus.TO_FILE:
      return "warn";
    case BillStatus.FILING_IN_PROGRESS:
      return "secondary";
    case BillStatus.FILED:
      return "info";
    case BillStatus.REIMBURSEMENT_IN_PROGRESS:
      return "secondary";
    case BillStatus.REIMBURSED:
      return "success";
    case BillStatus.REJECTED:
      return "danger";
    default:
      return "danger";
  }
}

export function submissionStatusToString(status: SubmissionStatus): string {
  switch (status) {
    case SubmissionStatus.OPEN:
      return "Open";
    case SubmissionStatus.CLOSED:
      return "Closed";
    default:
      return "Status missing";
  }
}

export function submissionStatusBadge(status: SubmissionStatus): "info" | "success" | "warn" | "danger" | "secondary" | "contrast" {
  switch (status) {
    case SubmissionStatus.OPEN:
      return "info";
    case SubmissionStatus.CLOSED:
      return "success";
    default:
      return "danger";
  }
}

export function markAsPaidSubmissionButtonVisible(submission: InsuranceSubmissionWithBills): boolean {
  return submission.bills.filter(bill => !bill.paidDateTime).length === submission.bills.length;
}

export function markAsReimbursementInProgressSubmissionButtonVisible(submission: InsuranceSubmissionWithBills): boolean {
  return submission.bills.filter(bill => bill.status === BillStatus.FILED).length === submission.bills.length;
}

export function markAsReimbursedSubmissionButtonVisible(submission: InsuranceSubmissionWithBills): boolean {
  return submission.bills.filter(bill => bill.status === BillStatus.REIMBURSEMENT_IN_PROGRESS).length === submission.bills.length;
}

export function loadEnvironment() {
  let keycloakUrl = '';
  let keycloakRealm = '';
  let keycloakClientId = '';
  let serverUrl = '';

  try {
    const xhr = new XMLHttpRequest();
    xhr.open('GET', '/assets/configuration.json?time=' + Date.now(), false);
    xhr.send(null);

    if (xhr.status === 200) {
      const envProperties = JSON.parse(xhr.responseText);
      keycloakUrl = envProperties.keycloakConfiguration.url;
      keycloakRealm = envProperties.keycloakConfiguration.realm;
      keycloakClientId = envProperties.keycloakConfiguration.clientId;
      serverUrl = envProperties.serverUrl;
    }
  } catch (error) { }
  return { keycloakUrl, keycloakRealm, keycloakClientId, serverUrl };
}

export function parseDayMonthYearDate(date: string): Date {
  const [day, month, year] = date
    .split('/')
    .map(Number);
  return new Date(Date.UTC(year, month - 1, day));
}

export function parseDateToDayMonthYear(date: Date | string): string {
  const dateValue = typeof date === "string" ? new Date(date) : date;
  if (isNaN(dateValue.getTime())) throw new Error(`Invalid date: ${date}`);

  const day = String(dateValue.getUTCDate()).padStart(2, '0');
  const month = String(dateValue.getUTCMonth() + 1).padStart(2, '0');
  const year = dateValue.getUTCFullYear();
  return `${day}/${month}/${year}`;
}
