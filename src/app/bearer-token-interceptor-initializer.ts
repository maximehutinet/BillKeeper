import {INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG} from 'keycloak-angular';
import {ConfigurationService} from './services/configuration.service';

export const provideBearerTokenInterceptor = () => {
  const config = ConfigurationService.load();
  const urlRegex = escapeUrlToRegex(config.serverUrl);
  return {
    provide: INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG,
    useValue: [
      {
        urlPattern: urlRegex
      }
    ]
  }

  function escapeUrlToRegex(url: string): RegExp {
    const escaped = url.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
    return new RegExp(`^${escaped}/.*$`);
  }
}
