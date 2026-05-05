import {Component} from '@angular/core';
import {Button} from "primeng/button";
import {Location} from '@angular/common';
import {TopBarComponent} from '../top-bar/top-bar.component';

@Component({
  selector: 'app-top-bar-with-back-button',
  imports: [
    Button,
    TopBarComponent
  ],
  templateUrl: './top-bar-with-back-button.component.html',
  styleUrl: './top-bar-with-back-button.component.scss'
})
export class TopBarWithBackButtonComponent {

    constructor(
      public location: Location,
    ) {
    }
}
