import {Component} from '@angular/core';
import {ConfirmDialog} from "primeng/confirmdialog";

@Component({
  selector: 'app-validation-dialog',
    imports: [
        ConfirmDialog
    ],
  templateUrl: './validation-dialog.component.html',
  styleUrl: './validation-dialog.component.scss'
})
export class ValidationDialogComponent {

  constructor(
  ) {
  }

}
