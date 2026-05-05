import {Component} from '@angular/core';
import {UserWsService} from '../../../../services/billkeeper-ws/user/user-ws.service';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MainLayoutComponent} from '../../../layouts/main-layout/main-layout.component';
import {TopBarComponent} from '../../../components/layout/top-bar/top-bar.component';
import {Fieldset} from 'primeng/fieldset';
import {User} from '../../../../services/billkeeper-ws/user/model';
import {ToastMessageService} from '../../../../services/toast-message.service';
import {Button} from 'primeng/button';
import {LayoutService} from '../../../../services/layout.service';
import {Avatar} from 'primeng/avatar';
import {Family} from '../../../../services/billkeeper-ws/family/model';
import {FamilyWsService} from '../../../../services/billkeeper-ws/family/family-ws.service';
import {EditNameDialogComponent} from '../../../components/commun/edit-name-dialog/edit-name-dialog.component';
import {UserAvatarComponent} from '../../../components/commun/user-avatar/user-avatar.component';
import {EditEmailDialogComponent} from '../../../components/commun/edit-email-dialog/edit-email-dialog.component';
import {SuccessDialogComponent} from '../../../components/commun/success-dialog/success-dialog.component';
import {UserDataService} from '../../../../services/user-data.service';

@Component({
  selector: 'app-user-profile-page',
  imports: [
    FormsModule,
    MainLayoutComponent,
    ReactiveFormsModule,
    TopBarComponent,
    Fieldset,
    Button,
    Avatar,
    EditNameDialogComponent,
    UserAvatarComponent,
    EditEmailDialogComponent,
    SuccessDialogComponent
  ],
  templateUrl: './user-profile-page.component.html',
  styleUrl: './user-profile-page.component.scss'
})
export class UserProfilePageComponent {

  currentUser: User | undefined;
  profilePicture: string = "";
  family?: Family;
  showFamilyNameDialog = false;
  showAddMemberDialog = false;
  showAddMemberSuccessDialog = false;
  addMemberSuccessDialogText = "";

  constructor(
    private userWsService: UserWsService,
    private userDataService: UserDataService,
    private familyWsService: FamilyWsService,
    private toastMessageService: ToastMessageService,
    private layoutService: LayoutService
  ) {
  }

  async ngOnInit() {
    try {
      await this.layoutService.withPageLoading(async () => {
        this.currentUser = await this.userDataService.getCurrentUser();
        this.profilePicture = await this.userDataService.getCurrentUserProfilePicture();
        this.family = await this.familyWsService.getCurrentUserFamily();
        this.userDataService.userRefreshedObservable.subscribe(async () => {
          this.profilePicture = await this.userDataService.getCurrentUserProfilePicture();
        });
      });
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

  async onUploadProfilePicture(event: any) {
    await this.layoutService.withPageLoading(async () => {
      if (!event.target.files && !event.target.files[0]) {
        return;
      }
      await this.uploadProfilePicture(event.target.files[0]);
      await this.userDataService.refreshUserData();
    });
  }

  private async uploadProfilePicture(file: File) {
    try {
      await this.userWsService.uploadCurrentUserProfilePicture(file);
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

  async onValidateFamilyName(name: string) {
    try {
      this.showFamilyNameDialog = false;
      await this.familyWsService.createFamily(name);
      this.family = await this.familyWsService.getCurrentUserFamily();
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

  async onValidateAddMemberEmail(email: string) {
    try {
      this.showAddMemberDialog = false;
      await this.familyWsService.addMemberToFamily(email);
      this.addMemberSuccessDialogText = `An invitation email was sent to ${email}, the member will be visible as soon as they accept the invitation`;
      this.showAddMemberSuccessDialog = true;
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }
}
