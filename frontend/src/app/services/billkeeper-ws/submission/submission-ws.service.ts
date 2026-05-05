import { Injectable } from '@angular/core';
import {HttpWsService} from '../http-ws.service';
import {CreateUpdateInsuranceSubmissionRequest, InsuranceSubmissionWithBills} from './model';

@Injectable({
  providedIn: 'root'
})
export class SubmissionWsService {

  constructor(
    private httpWsService: HttpWsService
  ) { }

  async getAllSubmissions(): Promise<InsuranceSubmissionWithBills[]> {
    return this.httpWsService.get<InsuranceSubmissionWithBills[]>("/submissions");
  }

  async getSubmission(submissionId: string): Promise<InsuranceSubmissionWithBills> {
    return this.httpWsService.get<InsuranceSubmissionWithBills>(`/submissions/${submissionId}`);
  }

  async createSubmission(submission: CreateUpdateInsuranceSubmissionRequest): Promise<void> {
    return this.httpWsService.post(`/submissions`, submission);
  }

  async updateSubmission(submissionId: string, submission: CreateUpdateInsuranceSubmissionRequest): Promise<void> {
    return this.httpWsService.post(`/submissions/${submissionId}`, submission);
  }

  async deleteSubmission(submissionId: string): Promise<void> {
    return this.httpWsService.delete(`/submissions/${submissionId}`);
  }
}
