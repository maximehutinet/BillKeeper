import {Component} from '@angular/core';
import {MainLayoutComponent} from '../../../layouts/main-layout/main-layout.component';
import {LayoutService} from '../../../../services/layout.service';
import {ActivatedRoute, Router} from '@angular/router';
import {SuccessDialogComponent} from '../../../components/commun/success-dialog/success-dialog.component';
import {FamilyWsService} from '../../../../services/billkeeper-ws/family/family-ws.service';
import {ToastMessageService} from '../../../../services/toast-message.service';

@Component({
  selector: 'app-accept-family-invitation-page',
  imports: [
    MainLayoutComponent,
    SuccessDialogComponent
  ],
  templateUrl: './accept-family-invitation-page.component.html',
  styleUrl: './accept-family-invitation-page.component.scss'
})
export class AcceptFamilyInvitationPageComponent {

  showSuccessDialog = false;
  successDialogMessage = "";

  constructor(
    private activatedRoute: ActivatedRoute,
    private layoutService: LayoutService,
    private router: Router,
    private familyWsService: FamilyWsService,
    private toastMessageService: ToastMessageService
  ) {
  }

  async ngOnInit() {
    try {
      this.layoutService.pageFocusing = true;
      const invitationId = await this.activatedRoute.snapshot.params['invitationId'];
      await this.familyWsService.acceptFamilyInvitation(invitationId);
      const family = await this.familyWsService.getCurrentUserFamily();
      this.successDialogMessage = `You have successfully joined ${family.name}`;
      this.showSuccessDialog = true;

    } catch (e) {
      this.toastMessageService.displayError(e);
      this.layoutService.pageFocusing = false;
      await this.router.navigate(['/']);
    }
  }

  async onValidate() {
    this.layoutService.pageFocusing = false;
    await this.router.navigate(['/']);
  }
}
