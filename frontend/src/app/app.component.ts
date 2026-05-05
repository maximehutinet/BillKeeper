import {Component} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {AuthService} from './services/auth/auth.service';
import {UserDataService} from './services/user-data.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {

  constructor(
    private userDataService: UserDataService,
    private authService: AuthService
  ) {
  }
  async ngOnInit(): Promise<void> {
    this.preventDefaultBrowserDragAndDropBehavior();
    if (this.authService.isLoggedIn()) {
      await this.userDataService.init();
    }
  }

  private preventDefaultBrowserDragAndDropBehavior() {
    window.addEventListener("dragover", e => {
      e && e.preventDefault();
    }, false);
    window.addEventListener("drop", e => {
      e && e.preventDefault();
    }, false);
  }
}
