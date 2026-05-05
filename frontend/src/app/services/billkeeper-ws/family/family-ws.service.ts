import { Injectable } from '@angular/core';
import {HttpWsService} from '../http-ws.service';
import {Family} from './model';

@Injectable({
  providedIn: 'root'
})
export class FamilyWsService {

  constructor(
    private httpWsService: HttpWsService
  ) { }

  public getCurrentUserFamily(): Promise<Family> {
    return this.httpWsService.get<Family>("/family/me");
  }

  public createFamily(name: string): Promise<void> {
    return this.httpWsService.post<void>("/family", {name: name});
  }

  public addMemberToFamily(email: string): Promise<void> {
    return this.httpWsService.post<void>("/family/members", {email: email});
  }

  public acceptFamilyInvitation(invitationId: string): Promise<void> {
    return this.httpWsService.post<void>(`/family/invitation/${invitationId}`, {});
  }
}
