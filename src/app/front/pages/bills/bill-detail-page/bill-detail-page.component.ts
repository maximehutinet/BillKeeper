import {Component} from '@angular/core';
import {BillStatusBadgeComponent} from "../../../components/bills/bill-status-badge/bill-status-badge.component";
import {Button} from "primeng/button";
import {CommentComponent} from "../../../components/comments/comment/comment.component";
import {CurrencyPipe, DatePipe} from "@angular/common";
import {DocumentsViewerComponent} from "../../../components/documents/documents-viewer/documents-viewer.component";
import {
  EditCommentDialogComponent
} from "../../../components/comments/edit-comment-dialog/edit-comment-dialog.component";
import {EditNameDialogComponent} from "../../../components/commun/edit-name-dialog/edit-name-dialog.component";
import {Fieldset} from "primeng/fieldset";
import {FormsModule} from "@angular/forms";
import {MainLayoutComponent} from "../../../layouts/main-layout/main-layout.component";
import {ActivatedRoute, Router, RouterLink} from "@angular/router";
import {Tooltip} from "primeng/tooltip";
import {
  Bill,
  BillStatus,
  ParsingJobStatus,
  UpdateBillReimbursementRequest
} from '../../../../services/billkeeper-ws/bill/model';
import {BillDocument} from '../../../../services/billkeeper-ws/document/model';
import {BillComment} from '../../../../services/billkeeper-ws/comment/model';
import {BillWsService} from '../../../../services/billkeeper-ws/bill/bill-ws.service';
import {DocumentWsService} from '../../../../services/billkeeper-ws/document/document-ws.service';
import {CommentWsService} from '../../../../services/billkeeper-ws/comment/comment-ws.service';
import {LayoutService} from '../../../../services/layout.service';
import {ValidationService} from '../../../../services/validation.service';
import {MessageType, ToastMessageService} from '../../../../services/toast-message.service';
import {
  TopBarWithBackButtonComponent
} from '../../../components/layout/top-bar-with-back-button/top-bar-with-back-button.component';
import {
  CopyToClipboardIconComponent
} from '../../../components/commun/copy-to-clipboard-icon/copy-to-clipboard-icon.component';
import {ValueLoadingOrNsComponent} from '../../../components/commun/value-loading-or-ns/value-loading-or-ns.component';
import {
  AddCommentTextareaComponent
} from '../../../components/comments/add-comment-textarea/add-comment-textarea.component';
import {CommentWithTaggedUsers} from '../../../components/comments/add-comment-textarea/model';
import {
  LeftRightNavigationArrowsComponent
} from '../../../components/commun/left-right-navigation-arrows/left-right-navigation-arrows.component';
import {SubmissionWsService} from '../../../../services/billkeeper-ws/submission/submission-ws.service';
import {
  LeftRightNavigation,
  NavigationIndex,
  NavigationLink
} from '../../../components/commun/left-right-navigation-arrows/model';
import {combineLatest} from 'rxjs';
import {
  BillReimbursementDialogComponent
} from '../../../components/bills/bill-reimbursement-dialog/bill-reimbursement-dialog.component';

@Component({
  selector: 'app-bill-detail-page',
  imports: [
    BillStatusBadgeComponent,
    Button,
    CommentComponent,
    CurrencyPipe,
    DatePipe,
    DocumentsViewerComponent,
    EditCommentDialogComponent,
    EditNameDialogComponent,
    Fieldset,
    FormsModule,
    MainLayoutComponent,
    RouterLink,
    Tooltip,
    CurrencyPipe,
    TopBarWithBackButtonComponent,
    CopyToClipboardIconComponent,
    ValueLoadingOrNsComponent,
    AddCommentTextareaComponent,
    LeftRightNavigationArrowsComponent,
    BillReimbursementDialogComponent
  ],
  templateUrl: './bill-detail-page.component.html',
  styleUrl: './bill-detail-page.component.scss'
})
export class BillDetailPageComponent {

  protected readonly BillStatus = BillStatus;
  protected readonly ParsingJobStatus = ParsingJobStatus;

  bill: Bill = {};
  documents: BillDocument[] = [];
  comments: BillComment[] = [];
  editComment: BillComment = {
    id: "",
    content: "",
    dateTime: new Date()
  };
  editCommentDialogVisible = false;
  editDocumentDescriptionDialogVisible = false;
  editedDocument: BillDocument = {};
  private dragCounter = 0;
  submissionNavigation: LeftRightNavigation | undefined;
  navigationIndex: NavigationIndex | undefined;
  validateLink: NavigationLink | undefined;
  showAddReimbursedAmountDialog: boolean = false;

  constructor(
    private billWsService: BillWsService,
    private documentWsService: DocumentWsService,
    private commentWsService: CommentWsService,
    private activatedRoute: ActivatedRoute,
    private layoutService: LayoutService,
    private router: Router,
    private validationService: ValidationService,
    private toastMessageService: ToastMessageService,
    private submissionService: SubmissionWsService,
  ) {
  }

  async ngOnInit() {
    try {
      combineLatest([
        this.activatedRoute.params,
        this.activatedRoute.queryParams
      ]).subscribe(async ([params, queryParams]) => {
        await this.layoutService.withPageLoading(async () => {
          const submissionViewActivated = queryParams['submissionView'] === 'true';
          this.bill = await this.billWsService.getBill(params['billId']);
          if (submissionViewActivated && this.bill.submissionId) {
            await this.updateSubmissionNavigation();
          }
          await this.loadBillDocuments();
          await this.loadBillComments();
        });
      });
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

  async updateSubmissionNavigation() {
    const submission = await this.submissionService.getSubmission(this.bill.submissionId!);
    const billsIds = submission.bills.map(bill => bill.id!);

    if (billsIds.length === 1) {
      return;
    }

    const currentBillIndex = billsIds.findIndex(value => value === this.bill.id);

    this.submissionNavigation = this.buildRouterLinks(billsIds, currentBillIndex);
    this.navigationIndex = {
      currentPage: currentBillIndex + 1,
      totalPages: billsIds.length
    }
    this.validateLink = { link: `/submissions/${submission.id}`, queryParams: {} }
  }

  private buildRouterLinks(billsIds: string[], currentBillIndex: number): LeftRightNavigation {
    const isFirst = currentBillIndex === 0;
    const isLast = currentBillIndex === billsIds.length - 1;

    const previousBillId = !isFirst ? billsIds[currentBillIndex - 1] : undefined;
    const nextBillId = !isLast ? billsIds[currentBillIndex + 1] : undefined;

    const buildLink = (id: string) => `/bill/${id}`;
    const queryParams = {submissionView: true};

    return {
      leftRouterLink: previousBillId ? {link: buildLink(previousBillId), queryParams: queryParams} : undefined,
      rightRouterLink: nextBillId ? {link: buildLink(nextBillId), queryParams: queryParams} : undefined,
    };
  }

  async loadBillDocuments() {
    this.documents = await this.billWsService.getBillDocuments(this.bill.id!);
  }

  async loadBillComments() {
    this.comments = await this.commentWsService.getAllBillComments(this.bill.id!);
  }

  async onUploadDocuments(event: any) {
    try {
      await this.layoutService.withPageLoading(async () => {
        if (!event.target.files && !event.target.files[0]) {
          return;
        }
        await this.uploadDocuments(event.target.files);
        await this.loadBillDocuments();
      });
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

  async uploadDocuments(files: FileList) {
    for (const file of Array.from(files)) {
      await this.billWsService.uploadBillDocument(this.bill.id!, file);
    }
  }

  async downloadBillDocument(document: BillDocument) {
    try {
      await this.documentWsService.downloadBillDocument(document.id!);
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

  async onDeleteBillDocument(document: BillDocument) {
    try {
      this.validationService.showConfirmationDialog(async () => {
        await this.documentWsService.deleteBillDocuments(document.id!);
        await this.loadBillDocuments();
      });
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

  async onMarkAsReimbursementInProgress() {
    this.showAddReimbursedAmountDialog = true;
  }

  async onValidateBillReimbursement(updatedBill: Bill) {
    try {
      const request: UpdateBillReimbursementRequest = {
        reimbursedAmount: updatedBill.reimbursedAmount,
        reimbursementDateTime: updatedBill.reimbursementDateTime
      }
      await this.billWsService.updateBillReimbursement(updatedBill.id!, request);
      this.bill = await this.billWsService.getBill(this.bill.id!);
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

  async onMarkAsReimbursed() {
    try {
      await this.billWsService.updateBillStatus(this.bill, BillStatus.REIMBURSED);
      this.bill = await this.billWsService.getBill(this.bill.id!);
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

  async onMarkAsPaid() {
    try {
      await this.billWsService.markBillAsPaid(this.bill);
      this.bill = await this.billWsService.getBill(this.bill.id!);
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

  async onDeleteBill() {
    this.validationService.showConfirmationDialog(async () => {
      await this.deleteBill();
    });
  }

  private async deleteBill() {
    try {
      await this.layoutService.withPageLoading(async () => {
        await this.billWsService.deleteBill(this.bill.id!);
      });
      await this.router.navigate([""])
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

  async onRejectBill() {
    try {
      await this.billWsService.markBillAsRejected(this.bill);
      this.bill = await this.billWsService.getBill(this.bill.id!);
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

  async onAddComment(comment: CommentWithTaggedUsers) {
    try {
      await this.commentWsService.createComment(this.bill.id!, comment);
      await this.loadBillComments();
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

  async onShowEditCommentDialog(comment: BillComment) {
    this.editComment = comment;
    this.editCommentDialogVisible = true;
  }

  async onEditComment(comment: BillComment) {
    try {
      this.editCommentDialogVisible = false;
      await this.commentWsService.updateComment(comment.id, comment.content);
      await this.loadBillComments();
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

  async onDeleteComment(commentId: string) {
    try {
      this.validationService.showConfirmationDialog(async () => {
        await this.commentWsService.deleteComment(commentId);
        await this.loadBillComments()
      });
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

  async downloadBillDocumentsMerged() {
    try {
      await this.documentWsService.getMergedBillsDocuments([this.bill.id!]);
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

  onEditDocumentDescription(document: BillDocument) {
    this.editedDocument = document;
    this.editDocumentDescriptionDialogVisible = true;
  }

  async onValidateNewDocumentDescription(description: string) {
    try {
      await this.documentWsService.updateDocument(this.editedDocument.id!, {description: description});
      await this.loadBillDocuments();
      this.editDocumentDescriptionDialogVisible = false;
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

  async onDrop(event: any) {
    this.layoutService.pageFocusing = false;
    await this.layoutService.withPageLoading(async () => {
      if (event.dataTransfer.items) {
        for (const item of [...event.dataTransfer.items]) {
          if (item.kind === "file") {
            const file = item.getAsFile();
            const fileExtension = file.name.split(".").pop();
            if (fileExtension != "pdf") {
              this.toastMessageService.displayMessage(`${file.name} couldn't be uploaded because it's not a PDF`, MessageType.Error, "File error");
              return;
            }
            await this.billWsService.uploadBillDocument(this.bill.id!, file);
            await this.loadBillDocuments();
          }
        }
      }
    });
  }

  onDragLeave() {
    this.dragCounter -= 1;
    if (this.dragCounter === 0) {
      this.layoutService.pageFocusing = false;
    }
  }

  onDragEnter() {
    this.dragCounter += 1;
    this.layoutService.pageFocusing = true;
  }
}
