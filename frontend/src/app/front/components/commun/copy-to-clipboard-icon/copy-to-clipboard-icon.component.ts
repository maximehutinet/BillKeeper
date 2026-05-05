import {Component, Input} from '@angular/core';
import {Tooltip} from "primeng/tooltip";
import {MessageType, ToastMessageService} from '../../../../services/toast-message.service';
import {Clipboard} from '@angular/cdk/clipboard';

@Component({
  selector: 'app-copy-to-clipboard-icon',
    imports: [
        Tooltip
    ],
  templateUrl: './copy-to-clipboard-icon.component.html',
  styleUrl: './copy-to-clipboard-icon.component.scss'
})
export class CopyToClipboardIconComponent {

  @Input()
  value: string = "";

  constructor(
    private clipboard: Clipboard,
    private toastMessageService: ToastMessageService
  ) {
  }

  onCopyToClipboard() {
    if (this.clipboard.copy(this.value)) {
      this.toastMessageService.displayMessage("Value copied!", MessageType.Success, "Clipboard");
    }
  }
}
