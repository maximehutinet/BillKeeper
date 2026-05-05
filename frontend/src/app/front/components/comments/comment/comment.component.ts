import {Component, EventEmitter, Input, Output} from '@angular/core';
import {BillComment} from '../../../../services/billkeeper-ws/comment/model';
import {DatePipe} from '@angular/common';
import {Button} from 'primeng/button';
import {UserAvatarComponent} from '../../commun/user-avatar/user-avatar.component';
import {UserDataService} from '../../../../services/user-data.service';
import {User} from '../../../../services/billkeeper-ws/user/model';
import {ToastMessageService} from '../../../../services/toast-message.service';

@Component({
  selector: 'app-comment',
  imports: [
    DatePipe,
    Button,
    UserAvatarComponent
  ],
  templateUrl: './comment.component.html',
  styleUrl: './comment.component.scss'
})
export class CommentComponent {

  @Input()
  comment: BillComment = {
    id: '',
    dateTime: new Date(),
    content: ""
  }

  currentUser: User | null = null;

  @Output()
  onEditEvent: EventEmitter<void> = new EventEmitter();

  @Output()
  onDeleteEvent: EventEmitter<void> = new EventEmitter();

  constructor(
    public userDataService: UserDataService,
    public toastMessageService: ToastMessageService
  ) {
  }

  async ngOnInit() {
    try {
      this.currentUser = await this.userDataService.getCurrentUser();
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

}
