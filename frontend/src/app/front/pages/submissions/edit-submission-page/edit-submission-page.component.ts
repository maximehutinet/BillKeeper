import {Component} from '@angular/core';
import {Button} from "primeng/button";
import {FloatLabel} from "primeng/floatlabel";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {InputText} from "primeng/inputtext";
import {MainLayoutComponent} from "../../../layouts/main-layout/main-layout.component";
import {Location} from "@angular/common";
import {
  TopBarWithBackButtonComponent
} from "../../../components/layout/top-bar-with-back-button/top-bar-with-back-button.component";
import {SubmissionWsService} from '../../../../services/billkeeper-ws/submission/submission-ws.service';
import {
  CreateUpdateInsuranceSubmissionRequest,
  InsuranceSubmissionWithBills
} from '../../../../services/billkeeper-ws/submission/model';
import {ToastMessageService} from '../../../../services/toast-message.service';
import {LayoutService} from '../../../../services/layout.service';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-edit-submission-page',
  imports: [
    Button,
    FloatLabel,
    FormsModule,
    InputText,
    MainLayoutComponent,
    ReactiveFormsModule,
    TopBarWithBackButtonComponent
  ],
  templateUrl: './edit-submission-page.component.html',
  styleUrl: './edit-submission-page.component.scss'
})
export class EditSubmissionPageComponent {

  submission: InsuranceSubmissionWithBills = {
    bills: []
  }
  form!: FormGroup;

  constructor(
    private submissionWsService: SubmissionWsService,
    private activatedRoute: ActivatedRoute,
    private toastMessageService: ToastMessageService,
    private layoutService: LayoutService,
    private location: Location
  ) {
  }

  async ngOnInit() {
    try {
      await this.layoutService.withPageLoading(async () => {
        const submissionId = await this.activatedRoute.snapshot.params['submissionId'];
        this.submission = await this.submissionWsService.getSubmission(submissionId);
        this.buildForm();
      });
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

  private buildForm() {
    this.form = new FormGroup({
      name: new FormControl(this.submission.name),
      eClaimId: new FormControl(this.submission.eClaimId)
    });
  }

  async onSaveSubmission() {
    try {
      const request: CreateUpdateInsuranceSubmissionRequest = {
        name: this.form.value.name,
        eClaimId: this.form.value.eClaimId
      }
      await this.submissionWsService.updateSubmission(this.submission.id!, request);
      this.location.back();
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }
}
