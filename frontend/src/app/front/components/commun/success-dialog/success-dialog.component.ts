import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Dialog} from "primeng/dialog";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {Button} from 'primeng/button';

@Component({
  selector: 'app-success-dialog',
  imports: [
    Dialog,
    FormsModule,
    Button,
    ReactiveFormsModule
  ],
  templateUrl: './success-dialog.component.html',
  styleUrl: './success-dialog.component.scss'
})
export class SuccessDialogComponent {

  @Input()
  header: string = "";

  @Input()
  text: string = "";

  @Input()
  showDialog = false;

  @Output()
  showDialogChange: EventEmitter<boolean> = new EventEmitter<boolean>();

}
