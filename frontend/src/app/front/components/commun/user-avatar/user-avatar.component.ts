import {Component, Input} from '@angular/core';
import {Avatar} from "primeng/avatar";
import {User} from '../../../../services/billkeeper-ws/user/model';
import {UserWsService} from '../../../../services/billkeeper-ws/user/user-ws.service';

@Component({
  selector: 'app-user-avatar',
  imports: [
    Avatar
  ],
  templateUrl: './user-avatar.component.html',
  styleUrl: './user-avatar.component.scss'
})
export class UserAvatarComponent {

  @Input()
  size: "normal" | "large" | "xlarge" | undefined = "normal"

  userProfilePicture: string = "";

  @Input()
  set user(user: User) {
    this.updateUserProfilePicture(user);
  }

  constructor(
    private userWsService: UserWsService
  ) {
  }

  async updateUserProfilePicture(user: User) {
    try {
      this.userProfilePicture = await this.userWsService.getProfilePicture(user);
    } catch (e) { }
  }

}
