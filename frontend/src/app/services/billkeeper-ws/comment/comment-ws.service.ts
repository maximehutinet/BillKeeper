import { Injectable } from '@angular/core';
import {HttpWsService} from '../http-ws.service';
import {BillComment} from './model';
import {CommentWithTaggedUsers} from '../../../front/components/comments/add-comment-textarea/model';

@Injectable({
  providedIn: 'root'
})
export class CommentWsService {

  constructor(
    private httpWsService: HttpWsService
  ) { }

  async getAllBillComments(billId: string): Promise<BillComment[]> {
    return this.httpWsService.get<BillComment[]>(`/comments?billId=${billId}`);
  }

  async createComment(billId: string, comment: CommentWithTaggedUsers): Promise<void> {
    return this.httpWsService.post<void>(`/comments?billId=${billId}`, comment);
  }

  async updateComment(commentId: string, content: string): Promise<void> {
    return this.httpWsService.post<void>(`/comments/${commentId}`, {content: content});
  }

  async deleteComment(commentId: string): Promise<void> {
    return this.httpWsService.delete<void>(`/comments/${commentId}`);
  }
}
