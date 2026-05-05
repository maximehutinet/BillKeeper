import {Component, Input} from '@angular/core';
import {CurrencyPipe} from '@angular/common';

@Component({
  selector: 'app-stats-card',
  imports: [
    CurrencyPipe
  ],
  templateUrl: './stats-card.component.html',
  styleUrl: './stats-card.component.scss'
})
export class StatsCardComponent {

  @Input()
  text: string | undefined;

  @Input()
  value: number | undefined;

  @Input()
  isCurrency = false;
}
