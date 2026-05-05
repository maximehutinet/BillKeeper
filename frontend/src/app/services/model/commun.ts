import {BillStatus, Currency} from '../billkeeper-ws/bill/model';
import {SubmissionStatus} from '../billkeeper-ws/submission/model';

export interface EnumDropdownOption {
  value: BillStatus | Currency | SubmissionStatus | undefined
}

export class LocalStorageKeys {
  static readonly MENU_STATE = 'last_menu_state';
}
