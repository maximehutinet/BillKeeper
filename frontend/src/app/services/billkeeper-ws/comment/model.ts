import {User} from '../user/model';

export interface BillComment {
  id: string;
  dateTime: Date;
  content: string;
  user?: User;
}
