import {Component, EventEmitter, inject, Input, Output} from '@angular/core';
import {Bill} from '../../../../services/billkeeper-ws/bill/model';
import {Button} from 'primeng/button';
import {Dialog} from 'primeng/dialog';
import {FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {InputMask} from 'primeng/inputmask';
import {CurrencyPipe} from '@angular/common';
import {TableModule} from 'primeng/table';
import {parseDateToDayMonthYear, parseDayMonthYearDate} from '../../../../services/utils';
import {InputNumber} from 'primeng/inputnumber';
import {ValueLoadingOrNsComponent} from '../../commun/value-loading-or-ns/value-loading-or-ns.component';
import {BillReimbursementRow} from './model';

@Component({
  selector: 'app-bill-reimbursement-dialog',
  imports: [
    Button,
    Dialog,
    ReactiveFormsModule,
    InputMask,
    CurrencyPipe,
    TableModule,
    InputNumber,
    ValueLoadingOrNsComponent
  ],
  templateUrl: './bill-reimbursement-dialog.component.html',
  styleUrl: './bill-reimbursement-dialog.component.scss',
})
export class BillReimbursementDialogComponent {

  form!: FormGroup;
  private _bills: Bill[] = [];

  private formBuilder = inject(FormBuilder);

  @Input()
  set bills(bills: Bill[]) {
    this._bills = bills;
    this.buildForm();
  }

  @Output()
  onValidateBillsReimbursement: EventEmitter<Bill[]> = new EventEmitter<Bill[]>();

  @Input()
  showDialog = false;

  @Output()
  showDialogChange: EventEmitter<boolean> = new EventEmitter<boolean>();

  private buildForm() {
    this.form = this.formBuilder.group({
      bills: this.formBuilder.array(this._bills.map(bill => this.createBillGroup(bill)))
    });
  }

  private createBillGroup(bill: Bill): FormGroup {
    return this.formBuilder.group({
      id: bill.id,
      name: bill.name,
      amount: bill.amount,
      currency: bill.currency,
      reimbursementDateTime: [bill.reimbursementDateTime ? parseDateToDayMonthYear(bill.reimbursementDateTime) : parseDateToDayMonthYear(new Date()), [Validators.required]],
      reimbursedAmount: [bill.reimbursedAmount, [Validators.required]]
    });
  }

  get billsArray(): FormArray {
    return this.form.get('bills') as FormArray;
  }

  onSubmit() {
    const updatedBills: Bill[] = this.form.value.bills.map((bill: BillReimbursementRow) => {
      return {
        ...bill,
        reimbursementDateTime: parseDayMonthYearDate(bill.reimbursementDateTime)
      };
    });
    this.onValidateBillsReimbursement.emit(updatedBills);
    this.showDialogChange.emit(false);
  }

}
