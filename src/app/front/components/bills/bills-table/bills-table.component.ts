import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Button} from "primeng/button";
import {Checkbox} from "primeng/checkbox";
import {CurrencyPipe, DatePipe} from "@angular/common";
import {TableModule} from "primeng/table";
import {Bill, BillStatus, ParsingJobStatus} from "../../../../services/billkeeper-ws/bill/model";
import {RouterLink} from '@angular/router';
import {BillStatusBadgeComponent} from '../bill-status-badge/bill-status-badge.component';
import {FormsModule} from '@angular/forms';
import {BillTableRow} from './model';
import {ValueLoadingOrNsComponent} from '../../commun/value-loading-or-ns/value-loading-or-ns.component';
import {MenuItem} from 'primeng/api';
import {Menu} from 'primeng/menu';

@Component({
  selector: 'app-bills-table',
  imports: [
    Button,
    Checkbox,
    DatePipe,
    TableModule,
    RouterLink,
    BillStatusBadgeComponent,
    FormsModule,
    CurrencyPipe,
    ValueLoadingOrNsComponent,
    Menu
  ],
  templateUrl: './bills-table.component.html',
  styleUrl: './bills-table.component.scss'
})
export class BillsTableComponent {

  protected readonly BillStatus = BillStatus;
  protected readonly ParsingJobStatus = ParsingJobStatus;

  tableRows: BillTableRow[] = [];
  allRowsSelected = false;

  @Input()
  set bills(bills: Bill[]) {
    this.buildTableRows(bills);
  }

  @Input()
  makeBillSelectable = false;

  @Input()
  selectedBills: Bill[] = [];

  @Output()
  selectedBillsChange: EventEmitter<Bill[]> = new EventEmitter();

  @Input()
  displayMarkAsPaidButton = false;

  @Output()
  onMarkBillAsPaid: EventEmitter<Bill> = new EventEmitter();

  @Input()
  displayMarkAsReimbursedButton = false;

  @Output()
  onMarkBillAsReimbursed: EventEmitter<Bill> = new EventEmitter();

  @Input()
  displayMarkAsReimbursementInProgressButton = false;

  @Output()
  onMarkBillAsReimbursementInProgress: EventEmitter<Bill> = new EventEmitter();

  @Input()
  displayDeleteButton = false;

  @Output()
  onDelete: EventEmitter<Bill> = new EventEmitter();

  @Input()
  submissionViewParamActive = false;

  private buildTableRows(bills: Bill[]) {
    this.tableRows = bills.map(bill => {
      return {
        checked: this.isBillSelected(bill),
        bill: bill,
        menuItems: [{
          label: 'Actions',
          items: this.getMenuItems(bill)
        }]
      }
    });
  }

  private getMenuItems(bill: Bill): MenuItem[] {
    let items: MenuItem[] = [
      {
        label: 'Edit',
        icon: 'pi pi-pencil',
        routerLink: '/bill/' + bill.id + '/edit'
      }
    ];
    if (this.displayMarkAsPaidButton && !bill.paidDateTime) {
      items.push({
        label: 'Mark as paid',
        icon: 'pi pi-dollar',
        command: () => this.onMarkBillAsPaid.emit(bill)
      });
    }
    if (this.displayMarkAsReimbursedButton && bill.status?.valueOf() === BillStatus.FILED.valueOf()) {
      items.push({
        label: 'Mark as reimbursement in progress',
        icon: 'pi pi-spinner-dotted',
        command: () => this.onMarkBillAsReimbursementInProgress.emit(bill)
      });
    }
    if (this.displayMarkAsReimbursedButton && bill.status?.valueOf() === BillStatus.REIMBURSEMENT_IN_PROGRESS.valueOf()) {
      items.push({
        label: 'Mark as reimbursed',
        icon: 'pi pi-check-circle',
        command: () => this.onMarkBillAsReimbursed.emit(bill)
      });
    }
    if (this.displayDeleteButton) {
      items.push({
        label: 'Delete',
        icon: 'pi pi-trash',
        command: () => this.onDelete.emit(bill),
        styleClass: 'menu-warning'
      });
    }
    return items;
  }

  private isBillSelected(bill: Bill): boolean {
    return this.selectedBills.filter(b => b.id === bill.id).length > 0;
  }

  public onCheckboxChange() {
    this.selectedBills = this.tableRows
      .filter(row => row.checked)
      .map(row => row.bill);
    this.allRowsSelected = this.selectedBills.length > 0;
    this.selectedBillsChange.emit(this.selectedBills);
  }

  public onAllRowsSelected() {
    this.changeTableRowsCheckedStatus(this.allRowsSelected);
    this.onCheckboxChange();
  }

  private changeTableRowsCheckedStatus(checkedStatus: boolean) {
    this.tableRows = this.tableRows.map(row => {
      return {
        checked: checkedStatus,
        bill: row.bill,
        menuItems: this.getMenuItems(row.bill)
      }
    });
  }
}
