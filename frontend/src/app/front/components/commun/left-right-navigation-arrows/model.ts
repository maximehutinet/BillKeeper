import {Params} from '@angular/router';

export interface NavigationLink {
  link: string;
  queryParams: Params;
}

export interface LeftRightNavigation {
  leftRouterLink: NavigationLink | undefined;
  rightRouterLink: NavigationLink | undefined;
}

export interface NavigationIndex {
  currentPage: number;
  totalPages: number;
}
