import {KeycloakServerConfig} from 'keycloak-js';

export interface Configuration {
  serverUrl: string;
  keycloakConfiguration?: KeycloakServerConfig;
}
