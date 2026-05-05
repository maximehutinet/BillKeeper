import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Bill, BillStatus} from '../../../../services/billkeeper-ws/bill/model';
import {Beneficiary} from '../../../../services/billkeeper-ws/beneficiary/model';
import {BeneficiarySelectOption, StatusSelectOption} from './model';
import {MultiSelect} from 'primeng/multiselect';
import {FormsModule} from '@angular/forms';
import {billStatusToString} from '../../../../services/utils';
import {Observable, Subscription} from 'rxjs';
import {Button} from 'primeng/button';

@Component({
  selector: 'app-bills-filter',
  imports: [
    MultiSelect,
    FormsModule,
    Button
  ],
  templateUrl: './bills-filter.component.html',
  styleUrl: './bills-filter.component.scss'
})
export class BillsFilterComponent {

  beneficiaryOptions: BeneficiarySelectOption[] = [];
  selectedBeneficiaries: BeneficiarySelectOption[] = [];

  statusOptions: StatusSelectOption[] = [];
  selectedStatus: StatusSelectOption[] = [];

  private _bills: Bill[] = [];
  private resetFiltersSubscription!: Subscription;
  private initialStatusFilters: BillStatus[] = [BillStatus.TO_PAY, BillStatus.TO_FILE];

  @Input()
  set bills(value: Bill[]) {
    this._bills = value;
    this.updateSelectOptions();
    this.initSelectedFilters();
  }

  @Output()
  billsChange: EventEmitter<Bill[]> = new EventEmitter();

  @Input()
  resetFiltersObservable: Observable<void> = new Observable();

  ngOnInit() {
    this.resetFiltersSubscription = this.resetFiltersObservable.subscribe(() => this.resetFilters());
  }

  ngOnDestroy() {
    this.resetFiltersSubscription.unsubscribe();
  }

  private updateSelectOptions() {
    this._bills.forEach(bill => {
      if (bill.beneficiary && !this.beneficiaryOptionsContainsBeneficiary(this.beneficiaryOptions, bill.beneficiary)) {
        this.beneficiaryOptions.push({
          name: bill.beneficiary!.firstname,
          beneficiary: bill.beneficiary!
        });
      }
      if (bill.status && !this.statusOptionsContainsStatus(this.statusOptions, bill.status)) {
        this.statusOptions.push({
          name: billStatusToString(bill.status),
          status: {value: bill.status}
        });
      }
    });
  }

  private beneficiaryOptionsContainsBeneficiary(options: BeneficiarySelectOption[], beneficiary: Beneficiary): boolean {
    return options
      .filter(option => option.beneficiary.id === beneficiary.id)
      .length > 0;
  }

  private statusOptionsContainsStatus(options: StatusSelectOption[], status: BillStatus): boolean {
    return options
      .filter(option => option.status.value === status)
      .length > 0;
  }

  private billHasSelectedBeneficiary(bill: Bill): boolean {
    if (this.selectedBeneficiaries.length === 0) {
      return true;
    }
    if (bill.beneficiary) {
      return this.beneficiaryOptionsContainsBeneficiary(this.selectedBeneficiaries, bill.beneficiary);
    }
    return false;
  }

  private billHasSelectedStatus(bill: Bill): boolean {
    if (this.selectedStatus.length === 0) {
      return true;
    }
    if (bill.status) {
      return this.statusOptionsContainsStatus(this.selectedStatus, bill.status);
    }
    return false;
  }

  private initSelectedFilters() {
    this.initialStatusFilters.forEach(status => {
      if (this.statusOptionsContainsStatus(this.statusOptions, status)) {
        this.selectedStatus.push({
          name: billStatusToString(status),
          status: {value: status}
        });
        this.onSelectChange();
      }
    });
  }

  onSelectChange() {
    const filteredBills = this._bills
      .filter(bill => this.billHasSelectedBeneficiary(bill))
      .filter(bill => this.billHasSelectedStatus(bill));
    this.billsChange.emit(filteredBills);
  }

  resetFilters() {
    this.selectedStatus = [];
    this.selectedBeneficiaries = [];
    this.billsChange.emit(this._bills);
  }

}
