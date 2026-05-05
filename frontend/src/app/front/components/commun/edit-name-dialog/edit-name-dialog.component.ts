import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Dialog} from 'primeng/dialog';
import {InputText} from 'primeng/inputtext';
import {FormsModule} from '@angular/forms';
import {Button} from 'primeng/button';

@Component({
  selector: 'app-edit-name-dialog',
  imports: [
    Dialog,
    InputText,
    FormsModule,
    Button
  ],
  templateUrl: './edit-name-dialog.component.html',
  styleUrl: './edit-name-dialog.component.scss'
})
export class EditNameDialogComponent {

  @Input()
  header: string = "";

  @Input()
  value: string = "";

  @Output()
  onValidate: EventEmitter<string> = new EventEmitter<string>();

  @Input()
  showDialog = false;

  @Output()
  showDialogChange: EventEmitter<boolean> = new EventEmitter<boolean>();
}
