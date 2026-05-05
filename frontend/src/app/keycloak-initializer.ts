import {provideKeycloak} from 'keycloak-angular';
import {ConfigurationService} from './services/configuration.service';

export const provideKeycloakAngular = () => {
  const keycloakConfig = ConfigurationService.load().keycloakConfiguration;
  return provideKeycloak({
    config: {
      url: keycloakConfig!.url,
      realm: keycloakConfig!.realm,
      clientId: keycloakConfig!.clientId
    },
    initOptions: {
      onLoad: 'login-required',
      enableLogging: false
    }
  });
}
