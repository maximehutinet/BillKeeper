import {Component, EventEmitter, Input, Output} from '@angular/core';
import {BillDocument} from '../../../../services/billkeeper-ws/document/model';
import {Button} from 'primeng/button';
import {Tab, TabList, TabPanel, TabPanels, Tabs} from 'primeng/tabs';
import {DocumentPdfViewerComponent} from '../document-pdf-viewer/document-pdf-viewer.component';

@Component({
  selector: 'app-documents-viewer',
  imports: [
    Button,
    Tab,
    TabList,
    TabPanel,
    TabPanels,
    Tabs,
    DocumentPdfViewerComponent
  ],
  templateUrl: './documents-viewer.component.html',
  styleUrl: './documents-viewer.component.scss'
})
export class DocumentsViewerComponent {

  _documents: BillDocument[] = [];

  @Input()
  set documents(documents: BillDocument[]) {
    this._documents = documents;
    if (this._documents.length > 0) {
      this.activeDocumentId = this._documents[0].id;
    }
  };

  get documents() {
    return this._documents;
  }

  @Input()
  editMode = false;

  @Output()
  onEditDocumentDescription: EventEmitter<BillDocument> = new EventEmitter();

  @Output()
  onDownloadDocument: EventEmitter<BillDocument> = new EventEmitter();

  @Output()
  onDeleteDocument: EventEmitter<BillDocument> = new EventEmitter();

  activeDocumentId: string | undefined;

  constructor() { }

}
