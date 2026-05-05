import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Button} from "primeng/button";
import {Dialog} from "primeng/dialog";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {InputText} from "primeng/inputtext";

@Component({
  selector: 'app-edit-email-dialog',
  imports: [
    Button,
    Dialog,
    FormsModule,
    InputText,
    ReactiveFormsModule
  ],
  templateUrl: './edit-email-dialog.component.html',
  styleUrl: './edit-email-dialog.component.scss'
})
export class EditEmailDialogComponent {

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

  form!: FormGroup;

  ngOnInit() {
    this.buildForm();
  }

  private buildForm() {
    this.form = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email])
    });
  }

  getValue() {
    return this.form.get('email');
  }

  onValidateButtonClick() {
    this.onValidate.emit(this.form.value.email);
  }

}
