import {Component} from '@angular/core';
import {BillsTableComponent} from "../../../components/bills/bills-table/bills-table.component";
import {CurrencyPipe, DatePipe, Location} from "@angular/common";
import {Fieldset} from "primeng/fieldset";
import {MainLayoutComponent} from "../../../layouts/main-layout/main-layout.component";
import {
  CreateUpdateInsuranceSubmissionRequest,
  InsuranceSubmissionWithBills
} from '../../../../services/billkeeper-ws/submission/model';
import {SubmissionWsService} from '../../../../services/billkeeper-ws/submission/submission-ws.service';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {LayoutService} from '../../../../services/layout.service';
import {ToastMessageService} from '../../../../services/toast-message.service';
import {
  markAsPaidSubmissionButtonVisible,
  markAsReimbursedSubmissionButtonVisible,
  submissionStatusBadge,
  submissionStatusToString
} from "../../../../services/utils";
import {Bill, BillStatus} from '../../../../services/billkeeper-ws/bill/model';
import {ValidationService} from '../../../../services/validation.service';
import {Button} from 'primeng/button';
import {Tooltip} from 'primeng/tooltip';
import {
  TopBarWithBackButtonComponent
} from '../../../components/layout/top-bar-with-back-button/top-bar-with-back-button.component';
import {BillWsService} from '../../../../services/billkeeper-ws/bill/bill-ws.service';
import {
  CopyToClipboardIconComponent
} from "../../../components/commun/copy-to-clipboard-icon/copy-to-clipboard-icon.component";
import {Badge} from 'primeng/badge';
import {ValueLoadingOrNsComponent} from '../../../components/commun/value-loading-or-ns/value-loading-or-ns.component';
import {EditNameDialogComponent} from '../../../components/commun/edit-name-dialog/edit-name-dialog.component';

@Component({
  selector: 'app-submission-detail-page',
  imports: [
    BillsTableComponent,
    DatePipe,
    Fieldset,
    MainLayoutComponent,
    CurrencyPipe,
    Button,
    Tooltip,
    TopBarWithBackButtonComponent,
    RouterLink,
    CopyToClipboardIconComponent,
    Badge,
    ValueLoadingOrNsComponent,
    EditNameDialogComponent
  ],
  templateUrl: './submission-detail-page.component.html',
  styleUrl: './submission-detail-page.component.scss'
})
export class SubmissionDetailPageComponent {

  protected readonly markAsPaidSubmissionButtonVisible = markAsPaidSubmissionButtonVisible;
  protected readonly markAsReimbursedSubmissionButtonVisible = markAsReimbursedSubmissionButtonVisible;
  protected readonly submissionStatusBadge = submissionStatusBadge;
  protected readonly submissionStatusToString = submissionStatusToString;

  submission: InsuranceSubmissionWithBills = {
    bills: []
  };
  showAddEclaimIdDialog = false;

  constructor(
    private submissionWsService: SubmissionWsService,
    private billWsService: BillWsService,
    private activatedRoute: ActivatedRoute,
    private layoutService: LayoutService,
    private validationService: ValidationService,
    private toastMessageService: ToastMessageService,
    private location: Location

  ) {
  }

  async ngOnInit() {
    try {
      this.submission.id = await this.activatedRoute.snapshot.params['submissionId'];
      await this.loadSubmission();
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

  async loadSubmission() {
    try {
      await this.layoutService.withPageLoading(async () => {
        this.submission = await this.submissionWsService.getSubmission(this.submission.id!);
      });
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

  onRemoveBillFromSubmission(bill: Bill) {
    try {
      this.validationService.showConfirmationDialog(async () => {
        const request: CreateUpdateInsuranceSubmissionRequest = {
          billIds: this.submission.bills
            .filter(b => b.id && b.id !== bill.id)
            .map(b => b.id!)
        }
        await this.submissionWsService.updateSubmission(this.submission.id!, request);
        await this.loadSubmission();
      }, "Are you sure you want to remove bill from submission?");
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

  async onMarkSubmissionAsPaid() {
    try {
      for (const bill of this.submission.bills) {
        await this.billWsService.markBillAsPaid(bill);
      }
      await this.loadSubmission();
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

  async onMarkSubmissionAsReimbursed() {
    try {
      for (const bill of this.submission.bills) {
        await this.billWsService.updateBillStatus(bill, BillStatus.REIMBURSED);
      }
      await this.loadSubmission();
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

  async onDeleteSubmission() {
    try {
      this.validationService.showConfirmationDialog(async () => {
        await this.submissionWsService.deleteSubmission(this.submission.id!);
        this.location.back()
      });
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

  async onValidateEclaimIdValue(value: string) {
    try {
      const request: CreateUpdateInsuranceSubmissionRequest = {
        eClaimId: value
      }
      await this.submissionWsService.updateSubmission(this.submission!.id!, request);
      this.showAddEclaimIdDialog = false;
      await this.loadSubmission();
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }
}
