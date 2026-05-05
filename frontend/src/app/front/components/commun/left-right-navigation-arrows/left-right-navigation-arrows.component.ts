import {Component, Input} from '@angular/core';
import {RouterLink} from '@angular/router';
import {LeftRightNavigation, NavigationIndex, NavigationLink} from './model';

@Component({
  selector: 'app-left-right-navigation-arrows',
  imports: [
    RouterLink
  ],
  templateUrl: './left-right-navigation-arrows.component.html',
  styleUrl: './left-right-navigation-arrows.component.scss'
})
export class LeftRightNavigationArrowsComponent {

  @Input()
  routerLinks: LeftRightNavigation | undefined;

  @Input()
  navigationIndex: NavigationIndex | undefined;

  @Input()
  validateLink: NavigationLink | undefined;

}
