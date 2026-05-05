import { Injectable } from '@angular/core';
import {HttpWsService} from '../http-ws.service';
import {Settings} from './model';

@Injectable({
  providedIn: 'root'
})
export class SettingsWsService {

  constructor(
    private httpWsService: HttpWsService
  ) { }

  async getSettings(): Promise<Settings>{
    return this.httpWsService.get<Settings>("/settings");
  }

  async createUpdateSettings(request: Settings): Promise<void>{
    return this.httpWsService.post<void>("/settings", request);
  }
}
