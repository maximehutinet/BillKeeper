import {User} from '../user/model';

export interface Family {
  name: string;
  members: User[];
  owner: User;
}
