import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class LayoutService {

  public pageLoading = false;
  public pageFocusing = false;

  constructor() { }

  async withPageLoading(fn: () => Promise<void>) {
    try {
      this.pageLoading = true;
      await Promise.all([
        fn(),
        new Promise(resolve => setTimeout(resolve, 400))
      ]);
      this.pageLoading = false;
    } catch (e) {
      this.pageLoading = false
      throw e;
    }
  }

}
