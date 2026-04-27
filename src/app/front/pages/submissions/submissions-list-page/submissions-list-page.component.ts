import {Component} from '@angular/core';
import {MainLayoutComponent} from "../../../layouts/main-layout/main-layout.component";
import {TableModule} from "primeng/table";
import {
  CreateUpdateInsuranceSubmissionRequest,
  InsuranceSubmissionWithBills
} from '../../../../services/billkeeper-ws/submission/model';
import {SubmissionWsService} from '../../../../services/billkeeper-ws/submission/submission-ws.service';
import {LayoutService} from '../../../../services/layout.service';
import {SubmissionsTableComponent} from '../../../components/submissions/submissions-table/submissions-table.component';
import {ToastMessageService} from '../../../../services/toast-message.service';
import {BillWsService} from '../../../../services/billkeeper-ws/bill/bill-ws.service';
import {ValidationService} from '../../../../services/validation.service';
import {TopBarComponent} from "../../../components/layout/top-bar/top-bar.component";
import {FloatLabel} from 'primeng/floatlabel';
import {IconField} from 'primeng/iconfield';
import {InputIcon} from 'primeng/inputicon';
import {InputText} from 'primeng/inputtext';
import {FormsModule} from '@angular/forms';
import {EditNameDialogComponent} from '../../../components/commun/edit-name-dialog/edit-name-dialog.component';
import {
  SubmissionsFilterComponent
} from '../../../components/submissions/submissions-filter/submissions-filter.component';
import {
  BillReimbursementDialogComponent
} from '../../../components/bills/bill-reimbursement-dialog/bill-reimbursement-dialog.component';
import {Bill, BillStatus, UpdateBillReimbursementRequest} from '../../../../services/billkeeper-ws/bill/model';
import {CurrencyPipe} from '@angular/common';

@Component({
  selector: 'app-submissions-list-page',
  imports: [
    MainLayoutComponent,
    TableModule,
    SubmissionsTableComponent,
    TopBarComponent,
    FloatLabel,
    IconField,
    InputIcon,
    InputText,
    FormsModule,
    EditNameDialogComponent,
    SubmissionsFilterComponent,
    BillReimbursementDialogComponent,
    CurrencyPipe
  ],
  templateUrl: './submissions-list-page.component.html',
  styleUrl: './submissions-list-page.component.scss'
})
export class SubmissionsListPageComponent {

  submissions: InsuranceSubmissionWithBills[] = [];
  filteredSubmissions: InsuranceSubmissionWithBills[] = [];
  searchKeyword: string | undefined;
  showAddEclaimIdDialog = false;
  editedSubmission: InsuranceSubmissionWithBills | undefined;
  dialogReimbursedBills: Bill[] = [];
  showAddReimbursedAmountDialog: boolean = false;
  selectedSubmissions: InsuranceSubmissionWithBills[] = [];
  totalSelectedSubmissionsValue: number = 0;

  constructor(
    private submissionWsService: SubmissionWsService,
    private billWsService: BillWsService,
    private layoutService: LayoutService,
    private toastMessageService: ToastMessageService,
    private validationService: ValidationService
  ) {
  }

  async ngOnInit() {
    await this.loadSubmissions();
  }

  private async loadSubmissions() {
    try {
      await this.layoutService.withPageLoading(async () => {
        this.submissions = await this.submissionWsService.getAllSubmissions();
        this.searchKeyword = undefined;
        this.filteredSubmissions = this.submissions;
      });
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

  applySearchFilter() {
    if (!this.searchKeyword) {
      this.filteredSubmissions = this.submissions;
    }
    this.filteredSubmissions = this.submissions
      .filter(submission => submission.eClaimId?.includes(this.searchKeyword!) || submission.name?.toLowerCase().includes(this.searchKeyword!.toLowerCase()));
  }

  async onMarkSubmissionAsPaid(submission: InsuranceSubmissionWithBills) {
    try {
      for (const bill of submission.bills) {
        await this.billWsService.markBillAsPaid(bill)
      }
      await this.loadSubmissions();
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

  async onMarkSubmissionAsReimbursed(submission: InsuranceSubmissionWithBills) {
    try {
      for (const bill of submission.bills) {
        await this.billWsService.updateBillStatus(bill, BillStatus.REIMBURSED);
      }
      await this.loadSubmissions();
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

  async onMarkSubmissionAsReimbursementInProgress(submission: InsuranceSubmissionWithBills) {
    this.dialogReimbursedBills = submission.bills;
    this.showAddReimbursedAmountDialog = true;
  }

  async onValidateBillsReimbursement(bills: Bill[]) {
    try {
      for (const bill of bills) {
        const request: UpdateBillReimbursementRequest = {
          reimbursedAmount: bill.reimbursedAmount,
          reimbursementDateTime: bill.reimbursementDateTime
        }
        await this.billWsService.updateBillReimbursement(bill.id!, request);
        await this.billWsService.updateBillStatus(bill, BillStatus.REIMBURSEMENT_IN_PROGRESS);
      }
      await this.loadSubmissions();
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

  async onDeleteSubmission(submission: InsuranceSubmissionWithBills) {
    try {
      this.validationService.showConfirmationDialog(async () => {
        await this.submissionWsService.deleteSubmission(submission.id!);
        await this.loadSubmissions();
      });
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

  async onAddEclaimId(submission: InsuranceSubmissionWithBills) {
    this.editedSubmission = submission;
    this.showAddEclaimIdDialog = true;
  }

  async onValidateEclaimIdValue(value: string) {
    try {
      const request: CreateUpdateInsuranceSubmissionRequest = {
        eClaimId: value
      }
      await this.submissionWsService.updateSubmission(this.editedSubmission!.id!, request);
      this.showAddEclaimIdDialog = false;
      this.editedSubmission = undefined;
      await this.loadSubmissions();
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

  onSubmissionFilterChange(submissions: InsuranceSubmissionWithBills[]) {
    this.filteredSubmissions = submissions;
  }

  onSubmissionCheckboxChange(submission: InsuranceSubmissionWithBills) {
    if (this.selectedSubmissions.includes(submission)) {
      this.selectedSubmissions = this.selectedSubmissions.filter(s => s !== submission);
    } else {
      this.selectedSubmissions.push(submission);
    }
    this.totalSelectedSubmissionsValue = this.selectedSubmissions.reduce((acc, curr) => acc + (curr.totalUsdAmount ?? 0), 0);
  }
}
