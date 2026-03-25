import {Component, EventEmitter, Input, Output} from '@angular/core';
import {UpperCasePipe} from '@angular/common';
import {Router, RouterLink} from '@angular/router';
import {MenuItem} from './model';
import {MenuItem as PrimeMenuItem} from 'primeng/api';
import {Avatar} from 'primeng/avatar';
import {DocumentWsService} from '../../../../services/billkeeper-ws/document/document-ws.service';
import {ToastMessageService} from '../../../../services/toast-message.service';
import {Badge} from 'primeng/badge';
import {SideMenuService} from '../../../../services/side-menu.service';
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
          label: 'Documents',
          icon: 'pi pi-file',
          link: '/documents'
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
    private documentWsService: DocumentWsService,
    private sideMenuService: SideMenuService,
    private userWsService: UserWsService,
    private authService: AuthService,
    private toastMessageService: ToastMessageService,
    private router: Router
  ) {
  }

  async ngOnInit() {
    try {
      this.sideMenuService.updateSideMenu.subscribe(async () => await this.updateSideMenu());
      await this.updateSideMenu();
      const userProfile = await this.authService.getUserProfile();
      this.userFirstname = userProfile.firstName;
      this.userProfilePicture = await this.userWsService.getCurrentUserProfilePicture();
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

  private async updateSideMenu() {
    try {
      const documents = await this.documentWsService.getAllOrphansDocuments();
      this.updateItemsWithDocumentBadge(documents.length);
    } catch (e) {
      this.toastMessageService.displayError(e);
    }
  }

  private updateItemsWithDocumentBadge(documentCount: number) {
    this.items = this.items.map(item => {
      item.items?.map(child => {
        if (child.link === '/documents') {
          child.badgeValue = documentCount;
        }
        return child;
      });
      return item;
    });
  }

  isCurrentPage(route: string | undefined): boolean {
    return this.router.url === route;
  }

  logout() {
    this.authService.logout();
  }

}
