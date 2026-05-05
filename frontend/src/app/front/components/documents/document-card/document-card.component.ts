import {Component, EventEmitter, Input, Output} from '@angular/core';
import {BillDocument} from '../../../../services/billkeeper-ws/document/model';
import {DatePipe} from '@angular/common';
import {Button} from 'primeng/button';

@Component({
  selector: 'app-document-card',
  imports: [
    DatePipe,
    Button,
  ],
  templateUrl: './document-card.component.html',
  styleUrl: './document-card.component.scss'
})
export class DocumentCardComponent {

  @Input()
  document: BillDocument = { }

  @Output()
  onSeeDocumentClick: EventEmitter<void> = new EventEmitter();

  @Output()
  onLinkDocumentClick: EventEmitter<void> = new EventEmitter();

  @Output()
  onDeleteDocumentClick: EventEmitter<void> = new EventEmitter();
}
