import {Component, EventEmitter, Input, Output} from '@angular/core';
import {InsuranceSubmissionWithBills, SubmissionStatus} from '../../../../services/billkeeper-ws/submission/model';
import {StatusSelectOption} from '../../bills/bills-filter/model';
import {submissionStatusToString} from '../../../../services/utils';
import {MultiSelect} from 'primeng/multiselect';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-submissions-filter',
  imports: [
    MultiSelect,
    FormsModule
  ],
  templateUrl: './submissions-filter.component.html',
  styleUrl: './submissions-filter.component.scss'
})
export class SubmissionsFilterComponent {

  private _submissions: InsuranceSubmissionWithBills[] = [];

  statusOptions: StatusSelectOption[] = [];
  selectedStatus: StatusSelectOption[] = [];

  @Input()
  set submissions(value: InsuranceSubmissionWithBills[]) {
    this._submissions = value;
    this.updateSelectOptions();
    this.initSelectedFilters();
  }

  @Output()
  submissionsChange: EventEmitter<InsuranceSubmissionWithBills[]> = new EventEmitter();

  private initSelectedFilters() {
    if (this.statusOptionsContainsStatus(this.statusOptions, SubmissionStatus.OPEN)) {
      this.selectedStatus.push({
        name: submissionStatusToString(SubmissionStatus.OPEN),
        status: {value: SubmissionStatus.OPEN}
      });
      this.onSelectChange();
    }
  }

  private updateSelectOptions() {
    this._submissions.forEach(submission => {
      if (submission.status && !this.statusOptionsContainsStatus(this.statusOptions, submission.status)) {
        this.statusOptions.push({
          name: submissionStatusToString(submission.status),
          status: {value: submission.status}
        });
      }
    });
  }

  private statusOptionsContainsStatus(options: StatusSelectOption[], status: SubmissionStatus): boolean {
    return options
      .filter(option => option.status.value === status)
      .length > 0;
  }

  private submissionHasSelectedStatus(submission: InsuranceSubmissionWithBills): boolean {
    if (this.selectedStatus.length === 0) {
      return true;
    }
    if (submission.status) {
      return this.statusOptionsContainsStatus(this.selectedStatus, submission.status);
    }
    return false;
  }

  onSelectChange() {
    const filteredSubmissions = this._submissions
      .filter(submission => this.submissionHasSelectedStatus(submission));
    this.submissionsChange.emit(filteredSubmissions);
  }

}
