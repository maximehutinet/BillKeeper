import {Injectable} from '@angular/core';
import {HttpWsService} from '../http-ws.service';
import {User} from './model';

@Injectable({
  providedIn: 'root'
})
export class UserWsService {

  constructor(
    private httpWsService: HttpWsService
  ) { }

  private PLACEHOLDER_PROFILE_PICTURE = "assets/images/profile_placeholder.webp";

  async getCurrentUserProfile(): Promise<User> {
    return this.httpWsService.get<User>('/users/me');
  }

  async uploadCurrentUserProfilePicture(file: File): Promise<void> {
    const formData = new FormData();
    formData.append("file", file);
    return this.httpWsService.post("/users/me/picture", formData);
  }

  async getProfilePicture(user: User): Promise<string> {
    const blob = await this.httpWsService.getBlob(`/users/${user.id}/picture`);
    return blob ? URL.createObjectURL(blob) : this.PLACEHOLDER_PROFILE_PICTURE;
  }

  async getCurrentUserProfilePicture(): Promise<string> {
    const blob = await this.httpWsService.getBlob("/users/me/picture");
    return blob ? URL.createObjectURL(blob) : this.PLACEHOLDER_PROFILE_PICTURE;
  }

  async getUsersStartingWith(value: string) {
    return this.httpWsService.get<User[]>(`/users/suggestions?value=${value}`)
  }
}
