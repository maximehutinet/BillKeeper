import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Button} from "primeng/button";
import {Dialog} from "primeng/dialog";
import {FloatLabel} from "primeng/floatlabel";
import {FormsModule} from "@angular/forms";
import {Textarea} from "primeng/textarea";
import {BillComment} from '../../../../services/billkeeper-ws/comment/model';

@Component({
  selector: 'app-edit-comment-dialog',
    imports: [
        Button,
        Dialog,
        FloatLabel,
        FormsModule,
        Textarea
    ],
  templateUrl: './edit-comment-dialog.component.html',
  styleUrl: './edit-comment-dialog.component.scss'
})
export class EditCommentDialogComponent {

  @Input()
  comment: BillComment = {
    id: "",
    content: "",
    dateTime: new Date()
  }

  @Output()
  onValidateCommentEdit: EventEmitter<BillComment> = new EventEmitter<BillComment>();

  @Input()
  showDialog = false;

  @Output()
  showDialogChange: EventEmitter<boolean> = new EventEmitter<boolean>();
}
