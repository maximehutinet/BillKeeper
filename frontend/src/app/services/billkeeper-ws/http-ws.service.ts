import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpParams} from "@angular/common/http";
import {lastValueFrom} from "rxjs";
import {ConfigurationService} from '../configuration.service';
import {AuthService} from '../auth/auth.service';

@Injectable({
  providedIn: 'root'
})
export class HttpWsService {

  constructor(
    private configurationService: ConfigurationService,
    private httpClient: HttpClient,
    private authService: AuthService
  ) { }

  async get<T>(path: string, params?: HttpParams | { [param: string]: string | string[] }): Promise<any> {
    try {
      const headers = await this.builderHeaders();
      return await lastValueFrom(this.httpClient.get<T>(this.url(path), {headers: headers, params: params}));
    } catch (error) {
      await this.handleError(error);
    }
  }

  async getBlob(path: string, params?: HttpParams | { [param: string]: string | string[] }): Promise<Blob> {
    try {
      const headers = await this.builderHeaders();
      return await lastValueFrom<Blob>(this.httpClient.get(this.url(path), {headers: headers, params, responseType: 'blob'}));
    } catch (error) {
      await this.handleError(error);
      return Promise.reject();
    }
  }

  async downloadBlob(path: string, filename: string): Promise<void> {
    const blob = await this.getBlob(path);
    const nav = (window.navigator as any);
    if (nav.msSaveOrOpenBlob) {
      nav.msSaveOrOpenBlob(blob, filename);
    } else {
      const url = URL.createObjectURL(blob);
      const link = document.createElement('a') as HTMLAnchorElement;
      link.href = url;
      link.download = filename;
      document.body.append(link);
      link.click();
      document.body.removeChild(link);
      URL.revokeObjectURL(url);
    }
  }

  async post<T>(path: string, body: any, params?: HttpParams | { [param: string]: string | string[] }): Promise<any> {
    try {
      const headers = await this.builderHeaders();
      return await lastValueFrom(this.httpClient.post<T>(this.url(path), body, {headers: headers, params: params}));
    } catch (error) {
      await this.handleError(error);
    }
  }

  async delete<T>(path: string, params?: HttpParams | { [param: string]: string | string[] }): Promise<any> {
    try {
      const headers = await this.builderHeaders();
      return await lastValueFrom(this.httpClient.delete<T>(this.url(path), {headers: headers, params: params}));
    } catch (error) {
      await this.handleError(error);
    }
  }

  private async builderHeaders(): Promise<HttpHeaders> {
    return new HttpHeaders();
  }

  private url(path: string) {
    return ConfigurationService.load().serverUrl + path;
  }

  private async handleError(error: any) {
    let errorHandled = false;
    if (error instanceof HttpErrorResponse) {
      if (error.status === 401) {
        this.authService.logout();
      }
    }
    if (!errorHandled) {
      throw error;
    }
  }
}
