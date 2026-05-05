import { Injectable } from '@angular/core';
import {User} from './billkeeper-ws/user/model';
import {UserWsService} from './billkeeper-ws/user/user-ws.service';
import {ToastMessageService} from './toast-message.service';
import {Subject} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserDataService {

  private _currentUser: User | null = null;
  private _currentUserprofilePicture: string | null = null;
  private _userRefreshed = new Subject<void>();
  userRefreshedObservable = this._userRefreshed.asObservable();


  constructor(
    private userWsService: UserWsService,
    private toastMessageService: ToastMessageService
  ) { }

  async init() {
    try {
      await this.refreshUserData();
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

  public async getCurrentUser(): Promise<User> {
    if (this._currentUser) {
      return this._currentUser;
    }
    this._currentUser = await this.userWsService.getCurrentUserProfile();
    return this._currentUser;
  }

  async getCurrentUserProfilePicture(): Promise<string> {
    if (this._currentUserprofilePicture) {
      return this._currentUserprofilePicture;
    }
    this._currentUserprofilePicture = await this.userWsService.getCurrentUserProfilePicture();
    return this._currentUserprofilePicture;
  }

  async refreshUserData() {
    this._currentUser = await this.userWsService.getCurrentUserProfile();
    this._currentUserprofilePicture = await this.userWsService.getCurrentUserProfilePicture();
    this._userRefreshed.next();
  }

  clearUserData() {
    this._currentUser = null;
    this._currentUserprofilePicture = null;
  }
}
