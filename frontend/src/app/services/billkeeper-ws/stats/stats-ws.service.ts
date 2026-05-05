import { Injectable } from '@angular/core';
import {HttpWsService} from '../http-ws.service';
import {Stats} from './model';

@Injectable({
  providedIn: 'root'
})
export class StatsWsService {

  constructor(
    private httpWsService: HttpWsService
  ) { }

  getBillsStats(): Promise<Stats> {
    return this.httpWsService.get<Stats>("/stats/bills");
  }
}
