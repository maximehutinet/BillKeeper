import {Beneficiary} from '../../../../services/billkeeper-ws/beneficiary/model';
import {EnumDropdownOption} from '../../../../services/model/commun';

export interface BeneficiarySelectOption {
  name: string;
  beneficiary: Beneficiary;
}

export interface StatusSelectOption {
  name: string;
  status: EnumDropdownOption;
}
