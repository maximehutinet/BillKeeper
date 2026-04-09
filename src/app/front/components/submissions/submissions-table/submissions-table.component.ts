import {Component, EventEmitter, Input, Output} from '@angular/core';
import {BillsTableComponent} from "../../bills/bills-table/bills-table.component";
import {Button} from "primeng/button";
import {CurrencyPipe, DatePipe} from "@angular/common";
import {Ripple} from "primeng/ripple";
import {TableModule} from "primeng/table";
import {InsuranceSubmissionWithBills} from '../../../../services/billkeeper-ws/submission/model';
import {
  markAsPaidSubmissionButtonVisible,
  markAsReimbursedSubmissionButtonVisible,
  markAsReimbursementInProgressSubmissionButtonVisible,
  submissionStatusBadge,
  submissionStatusToString
} from '../../../../services/utils';
import {RouterLink} from '@angular/router';
import {ValueLoadingOrNsComponent} from '../../commun/value-loading-or-ns/value-loading-or-ns.component';
import {SubmissionTableRow} from './model';
import {MenuItem} from 'primeng/api';
import {Menu} from 'primeng/menu';
import {Badge} from 'primeng/badge';

@Component({
  selector: 'app-submissions-table',
  imports: [
    BillsTableComponent,
    Button,
    DatePipe,
    Ripple,
    TableModule,
    CurrencyPipe,
    RouterLink,
    ValueLoadingOrNsComponent,
    Menu,
    Badge
  ],
  templateUrl: './submissions-table.component.html',
  styleUrl: './submissions-table.component.scss'
})
export class SubmissionsTableComponent {

  protected readonly submissionStatusToString = submissionStatusToString;
  protected readonly submissionStatusBadge = submissionStatusBadge;

  tableRows: SubmissionTableRow[] = [];

  @Input()
  set submissions(submissions: InsuranceSubmissionWithBills[]) {
    this.buildTableRows(submissions);
  }

  @Output()
  onMarkAsPaid: EventEmitter<InsuranceSubmissionWithBills> = new EventEmitter();

  @Output()
  onMarkAsReimbursed: EventEmitter<InsuranceSubmissionWithBills> = new EventEmitter();

  @Output()
  onMarkAsReimbursementInProgress: EventEmitter<InsuranceSubmissionWithBills> = new EventEmitter();

  @Output()
  onDeleteSubmission: EventEmitter<InsuranceSubmissionWithBills> = new EventEmitter();

  @Output()
  onAddEclaimId: EventEmitter<InsuranceSubmissionWithBills> = new EventEmitter();

  private buildTableRows(submissions: InsuranceSubmissionWithBills[]) {
    this.tableRows = submissions.map(submission => {
      return {
        id: submission.id,
        submission: submission,
        menuItems: [{
          label: 'Actions',
          items: this.getMenuItems(submission)
        }]
      }
    });
  }

  private getMenuItems(submission: InsuranceSubmissionWithBills): MenuItem[] {
    let items: MenuItem[] = [
      {
        label: 'Edit',
        icon: 'pi pi-pencil',
        routerLink: '/submissions/' + submission.id + '/edit'
      }
    ];
    if (!submission.eClaimId) {
      items.push({
        label: 'Add eclaimId',
        icon: 'pi pi-plus',
        command: () => this.onAddEclaimId.emit(submission)
      });
    }
    if (markAsPaidSubmissionButtonVisible(submission)) {
      items.push({
        label: 'Mark bills as paid',
        icon: 'pi pi-dollar',
        command: () => this.onMarkAsPaid.emit(submission)
      });
    }
    if (markAsReimbursementInProgressSubmissionButtonVisible(submission)) {
      items.push({
        label: 'Mark bills as reimbursement in progress',
        icon: 'pi pi-spinner-dotted',
        command: () => this.onMarkAsReimbursementInProgress.emit(submission)
      });
    }
    if (markAsReimbursedSubmissionButtonVisible(submission)) {
      items.push({
        label: 'Mark bills as reimbursed',
        icon: 'pi pi-check-circle',
        command: () => this.onMarkAsReimbursed.emit(submission)
      });
    }
    if (!submission.eClaimId) {
      items.push({
        label: 'Delete',
        icon: 'pi pi-trash',
        command: () => this.onDeleteSubmission.emit(submission),
        styleClass: 'menu-warning'
      });
    }
    return items;
  }
}
