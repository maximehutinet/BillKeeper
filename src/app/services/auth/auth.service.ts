import {inject, Injectable} from '@angular/core';
import Keycloak from 'keycloak-js';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly keycloak = inject(Keycloak);

  constructor() { }

  isLoggedIn() {
    return this.keycloak.authenticated;
  }

  async logout() {
    await this.keycloak.logout();
  }

  async getUserProfile() {
    return this.keycloak.loadUserProfile();
  }

  async getToken() {
    return this.keycloak.token;
  }
}
