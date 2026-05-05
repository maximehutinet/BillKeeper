import {Injectable} from '@angular/core';
import {HttpWsService} from '../http-ws.service';
import {UpdateDocumentRequest} from './model';

@Injectable({
  providedIn: 'root'
})
export class DocumentWsService {

  constructor(
    private httpWsService: HttpWsService
  ) { }

  public getMergedBillsDocuments(billIds: string[]) {
    return this.httpWsService.downloadBlob(`/documents?billIds=${billIds.join(",")}`, "MergedFile.pdf")
  }

  public downloadBillDocument(documentId: string) {
    return this.httpWsService.downloadBlob(`/documents/${documentId}`, `${documentId}.pdf`);
  }

  async deleteBillDocuments(documentId: string): Promise<void> {
    return this.httpWsService.delete(`/documents/${documentId}`);
  }

  async updateDocument(documentId: string, request: UpdateDocumentRequest): Promise<void> {
    return this.httpWsService.post(`/documents/${documentId}`, request);
  }
}
