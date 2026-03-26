import {Component, EventEmitter, Input, Output} from '@angular/core';
import {UpperCasePipe} from '@angular/common';
import {Router, RouterLink} from '@angular/router';
import {MenuItem} from './model';
import {MenuItem as PrimeMenuItem} from 'primeng/api';
import {Avatar} from 'primeng/avatar';
import {ToastMessageService} from '../../../../services/toast-message.service';
import {Badge} from 'primeng/badge';
import {AuthService} from '../../../../services/auth/auth.service';
import {Menu} from 'primeng/menu';
import {UserWsService} from '../../../../services/billkeeper-ws/user/user-ws.service';

@Component({
  selector: 'app-side-menu',
  imports: [
    UpperCasePipe,
    Avatar,
    RouterLink,
    Badge,
    Menu
  ],
  templateUrl: './side-menu.component.html',
  styleUrl: './side-menu.component.scss'
})
export class SideMenuComponent {

  @Input()
  open: boolean = true;

  @Output()
  openChange: EventEmitter<boolean> = new EventEmitter();

  items: MenuItem[] = [
    {
      label: 'Bill management',
      items: [
        {
          label: 'Bills',
          icon: 'pi pi-receipt',
          link: '/'
        },
        {
          label: 'Submissions',
          icon: 'pi pi-file-check',
          link: '/submissions'
        },
        {
          label: 'Stats',
          icon: 'pi pi-chart-bar',
          link: '/stats'
        }
      ]
    },
    {
      label: 'App',
      items: [
        {
          label: 'Settings',
          icon: 'pi pi-cog',
          link: '/settings'
        }
      ]
    }
  ];

  profileItems: PrimeMenuItem[] = [{
    label: 'Action',
    items: [
      {
        label: 'Profile',
        icon: 'pi pi-user',
        routerLink: ['/profile']
      },
      {
        label: 'Log out',
        icon: 'pi pi-sign-out',
        command: () => this.logout()
      }
    ]
  }];

  userFirstname: string | undefined;
  userProfilePicture: string = "assets/images/profile_placeholder.png";

  constructor(
    private userWsService: UserWsService,
    private authService: AuthService,
    private toastMessageService: ToastMessageService,
    private router: Router
  ) {
  }

  async ngOnInit() {
    try {
      const userProfile = await this.authService.getUserProfile();
      this.userFirstname = userProfile.firstName;
      this.userProfilePicture = await this.userWsService.getCurrentUserProfilePicture();
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

  isCurrentPage(route: string | undefined): boolean {
    return this.router.url === route;
  }

  logout() {
    this.authService.logout();
  }

}
