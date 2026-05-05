import {Component, Input} from '@angular/core';
import {BillDocument} from '../../../../services/billkeeper-ws/document/model';
import {AuthService} from '../../../../services/auth/auth.service';
import {NgxExtendedPdfViewerModule} from 'ngx-extended-pdf-viewer';

@Component({
  selector: 'app-document-pdf-viewer',
  imports: [
    NgxExtendedPdfViewerModule
  ],
  templateUrl: './document-pdf-viewer.component.html',
  styleUrl: './document-pdf-viewer.component.scss'
})
export class DocumentPdfViewerComponent {

  url: string | undefined;
  token: string | undefined;
  _document: BillDocument = {};

  @Input()
  set document(document: BillDocument) {
    this._document = document;
    this.setSource();
  }

  constructor(private authService: AuthService) { }

  async setSource() {
    this.url = this._document.url;
    const token = await this.authService.getToken();
    this.token = token ? 'Bearer ' + token : undefined;
  }

}
