import {Component, EventEmitter, Input, Output} from '@angular/core';
import {User} from '../../../../services/billkeeper-ws/user/model';
import {UserAvatarComponent} from '../user-avatar/user-avatar.component';

@Component({
  selector: 'app-suggested-users-menu',
  imports: [
    UserAvatarComponent
  ],
  templateUrl: './suggested-users-menu.component.html',
  styleUrl: './suggested-users-menu.component.scss'
})
export class SuggestedUsersMenuComponent {

  @Input()
  users: User[] = []

  @Output()
  onClickUser: EventEmitter<User> = new EventEmitter();

}
