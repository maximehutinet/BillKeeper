import { Injectable } from '@angular/core';
import {MessageService} from 'primeng/api';

@Injectable({
  providedIn: 'root'
})
export class ToastMessageService {

  constructor(
    private messageService: MessageService
  ) { }

  displayMessage(message: string, type: MessageType, title: string = '') {
    this.messageService.add({severity: type.toString(), summary: title, detail: message});
  }

  displayError(error: any) {
    this.displayMessage(`An error occurred : ${error.message}`, MessageType.Error, "Error");
  }

}

export enum MessageType {
  Success = "success",
  Info = "info",
  Warn = "warn",
  Error = "error"
}
