import {Injectable} from '@angular/core';
import {HttpWsService} from '../http-ws.service';
import {Bill, BillStatus, UpdateBillReimbursementRequest} from './model';
import {BillDocument} from '../document/model';

@Injectable({
  providedIn: 'root'
})
export class BillWsService {

  constructor(
    private httpWsService: HttpWsService
  ) { }

  async getAllBills(): Promise<Bill[]> {
    return this.httpWsService.get<Bill[]>("/bills");
  }

  async getBill(billId: string): Promise<Bill> {
    return this.httpWsService.get<Bill>(`/bills/${billId}`);
  }

  async createBill(file: File): Promise<Bill> {
    const formData = new FormData();
    formData.append("file", file);
    return this.httpWsService.post("/bills", formData);
  }

  async uploadBillDocument(billId: string, document: File): Promise<void> {
    const formData = new FormData();
    formData.append("file", document);
    return this.httpWsService.post(`/bills/${billId}/documents`, formData);
  }

  async getBillDocuments(billId: string): Promise<BillDocument[]> {
    return this.httpWsService.get<BillDocument[]>(`/bills/${billId}/documents`);
  }
  async updateBill(billId: string, updatedBill: Bill): Promise<void> {
    return this.httpWsService.post(`/bills/${billId}`, updatedBill);
  }

  async updateBillStatus(bill: Bill, status: BillStatus) {
    return this.httpWsService.post(`/bills/${bill.id}/status`, {status: status});
  }

  async updateBillReimbursement(billId: string, request: UpdateBillReimbursementRequest): Promise<void> {
    return this.httpWsService.post(`/bills/${billId}/reimbursement`, request);
  }

  async markBillAsPaid(bill: Bill): Promise<void> {
    const requestBody: Bill = {
      serviceDateTime: bill.serviceDateTime,
      paidDateTime: new Date()
    }
    return this.httpWsService.post(`/bills/${bill.id}`, requestBody);
  }

  async markBillAsRejected(bill: Bill): Promise<void> {
    const requestBody: Bill = {
      serviceDateTime: bill.serviceDateTime,
      paidDateTime: bill.paidDateTime,
      status: BillStatus.REJECTED
    }
    return this.httpWsService.post(`/bills/${bill.id}`, requestBody);
  }

  async deleteBill(billId: string): Promise<void> {
    return this.httpWsService.delete(`/bills/${billId}`);
  }

  async getProvidersStartingWith(value: string) {
    return this.httpWsService.get<string[]>(`/bills/providers?value=${value}`)
  }
}
