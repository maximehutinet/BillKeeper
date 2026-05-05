import { Injectable } from '@angular/core';
import {HttpWsService} from '../http-ws.service';
import {Beneficiary} from './model';

@Injectable({
  providedIn: 'root'
})
export class BeneficiaryWsService {

  constructor(
    private httpWsService: HttpWsService
  ) { }

  async getAllBeneficiaries(): Promise<Beneficiary[]> {
    return this.httpWsService.get<Beneficiary[]>("/beneficiaries")
  }

  async createBeneficiary(firstname: string): Promise<void> {
    return this.httpWsService.post<void>("/beneficiaries", {firstname: firstname});
  }

  async updateBeneficiary(beneficiary: Beneficiary): Promise<void> {
    return this.httpWsService.post<void>(`/beneficiaries/${beneficiary.id}`, {firstname: beneficiary.firstname});
  }

  async deleteBeneficiary(beneficiary: Beneficiary): Promise<void> {
    return this.httpWsService.delete<void>(`/beneficiaries/${beneficiary.id}`);
  }
}
