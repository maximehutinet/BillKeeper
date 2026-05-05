export interface BillDocument {
  id?: string;
  dateTime?: Date;
  url?: string;
  description?: string;
}

export interface UpdateDocumentRequest {
  description?: string;
  billId?: string;
}
