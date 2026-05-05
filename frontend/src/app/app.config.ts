import {ApplicationConfig, provideZoneChangeDetection} from '@angular/core';
import {provideRouter} from '@angular/router';

import {routes} from './app.routes';
import {providePrimeNG} from 'primeng/config';
import PrimeNgPreset from '../theme/primeng-preset'
import {provideHttpClient, withInterceptors} from '@angular/common/http';

import {ConfirmationService, MessageService} from 'primeng/api';
import {provideKeycloakAngular} from './keycloak-initializer';
import {includeBearerTokenInterceptor} from 'keycloak-angular';
import {provideBearerTokenInterceptor} from './bearer-token-interceptor-initializer';


export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({eventCoalescing: true}),
    provideRouter(routes),
    provideHttpClient(withInterceptors([includeBearerTokenInterceptor])),
    provideBearerTokenInterceptor(),
    provideKeycloakAngular(),
    providePrimeNG({
      theme: {
        preset: PrimeNgPreset
      }
    }),
    ConfirmationService,
    MessageService
  ]
};
