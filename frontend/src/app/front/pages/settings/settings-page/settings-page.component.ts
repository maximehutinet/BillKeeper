import {Component} from '@angular/core';
import {MainLayoutComponent} from "../../../layouts/main-layout/main-layout.component";
import {SettingsWsService} from '../../../../services/billkeeper-ws/settings/settings-ws.service';
import {MessageType, ToastMessageService} from '../../../../services/toast-message.service';
import {Settings} from '../../../../services/billkeeper-ws/settings/model';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {FloatLabel} from 'primeng/floatlabel';
import {TopBarComponent} from '../../../components/layout/top-bar/top-bar.component';
import {Button} from 'primeng/button';
import {InputNumberModule} from 'primeng/inputnumber';
import {Fieldset} from "primeng/fieldset";
import {Beneficiary} from '../../../../services/billkeeper-ws/beneficiary/model';
import {BeneficiaryWsService} from '../../../../services/billkeeper-ws/beneficiary/beneficiary-ws.service';
import {EditNameDialogComponent} from '../../../components/commun/edit-name-dialog/edit-name-dialog.component';
import {ValidationService} from '../../../../services/validation.service';
import {LayoutService} from '../../../../services/layout.service';

@Component({
  selector: 'app-settings-page',
  imports: [
    MainLayoutComponent,
    FormsModule,
    FloatLabel,
    ReactiveFormsModule,
    TopBarComponent,
    Button,
    InputNumberModule,
    Fieldset,
    EditNameDialogComponent,
  ],
  templateUrl: './settings-page.component.html',
  styleUrl: './settings-page.component.scss'
})
export class SettingsPageComponent {

  settings?: Settings;
  beneficiaries: Beneficiary[] = [];
  form!: FormGroup;
  editBeneficiaryMode = false;
  editBeneficiaryNameDialogVisible = false;
  editedBeneficiary: Beneficiary = {
    id: "",
    firstname: ""
  };

  constructor(
    private settingsWsService: SettingsWsService,
    private validationService: ValidationService,
    private beneficiaryWSService: BeneficiaryWsService,
    private toastMessageService: ToastMessageService,
    private layoutService: LayoutService
  ) {
  }

  async ngOnInit() {
    try {
      await this.layoutService.withPageLoading(async () => {
        this.settings = await this.settingsWsService.getSettings();
        this.buildForm();
        this.beneficiaries = await this.beneficiaryWSService.getAllBeneficiaries();
      });
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

  private buildForm() {
    this.form = new FormGroup({
        euroToUsdExchangeRate: new FormControl(this.settings?.euroToUsdExchangeRate ?? 1),
        chfToUsdExchangeRate: new FormControl(this.settings?.chfToUsdExchangeRate ?? 1),
    })
  }

  async onSave() {
    try {
      const request: Settings = {
        euroToUsdExchangeRate: this.form.value.euroToUsdExchangeRate,
        chfToUsdExchangeRate: this.form.value.chfToUsdExchangeRate
      }
      await this.settingsWsService.createUpdateSettings(request);
      await this.updateBeneficiaries();
      this.settings = await this.settingsWsService.getSettings();
      this.beneficiaries = await this.beneficiaryWSService.getAllBeneficiaries();
      this.toastMessageService.displayMessage("Modifications saved", MessageType.Success, "Settings")
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

  onEditBeneficiaryName(beneficiary: Beneficiary) {
    this.editBeneficiaryMode = true;
    this.editedBeneficiary = beneficiary;
    this.editBeneficiaryNameDialogVisible = true;
  }

  onValidateBeneficiaryName(name: string) {
    this.editedBeneficiary.firstname = name;
    this.editBeneficiaryNameDialogVisible = false;
    if (this.editBeneficiaryMode) {
      this.beneficiaries = this.beneficiaries
        .map(beneficiary => {
          if (beneficiary.id === this.editedBeneficiary?.id) {
            return this.editedBeneficiary;
          } else {
            return beneficiary;
          }
        })
      this.editBeneficiaryMode = false;
    } else {
      this.beneficiaries.push(this.editedBeneficiary);
    }
    this.editedBeneficiary = {
      id: "",
      firstname: ""
    }
  }

  private async updateBeneficiaries() {
    const currentBeneficiaries = await this.beneficiaryWSService.getAllBeneficiaries();
    const beneficiariesToUpdate = this.getBeneficiariesToUpdate(currentBeneficiaries);
    const beneficiariesToCreate = this.beneficiaries
      .filter(beneficiary => beneficiary.id.length === 0);
    const beneficiariesToDelete = this.getBeneficiariesToDelete(currentBeneficiaries);

    beneficiariesToUpdate.forEach(beneficiary => {
      this.beneficiaryWSService.updateBeneficiary(beneficiary);
    });
    beneficiariesToCreate.forEach(beneficiary => {
      this.beneficiaryWSService.createBeneficiary(beneficiary.firstname);
    });
    beneficiariesToDelete.forEach(beneficiary => {
      this.beneficiaryWSService.deleteBeneficiary(beneficiary);
    });
  }

  private getBeneficiariesToUpdate(currentBeneficiaries: Beneficiary[]) {
    let beneficiariesToUpdate: Beneficiary[] = [];
    this.beneficiaries.forEach(beneficiary => {
      const currentBeneficiary = currentBeneficiaries.find(b => b.id === beneficiary.id);
      if (currentBeneficiary && currentBeneficiary.firstname !== beneficiary.firstname) {
        beneficiariesToUpdate.push(beneficiary);
      }
    });
    return beneficiariesToUpdate;
  }

  private getBeneficiariesToDelete(currentBeneficiaries: Beneficiary[]) {
    let beneficiariesToDelete: Beneficiary[] = [];
    currentBeneficiaries.forEach(currentBeneficiary => {
      if (!this.beneficiaries.find(b => b.id === currentBeneficiary.id)) {
        beneficiariesToDelete.push(currentBeneficiary);
      }
    });
    return beneficiariesToDelete;
  }

  onAddBeneficiary() {
    this.editedBeneficiary.firstname = "";
    this.editBeneficiaryMode = false;
    this.editBeneficiaryNameDialogVisible = true;
  }

  onDeleteBeneficiary(beneficiary: Beneficiary) {
    this.validationService.showConfirmationDialog(async () => {
      this.beneficiaries = this.beneficiaries.filter(b => b.id !== beneficiary.id);
    });
  }
}
