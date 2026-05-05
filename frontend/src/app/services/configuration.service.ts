import {Injectable} from '@angular/core';
import {Configuration} from "./model/configuration";

@Injectable({
  providedIn: 'root'
})
export class ConfigurationService {
  private static _config: Configuration | null = null;

  static load(): Configuration {
    if (this._config) {
      return this._config;
    }

    const xhr = new XMLHttpRequest();
    xhr.open('GET', '/assets/configuration.json', false);
    xhr.send(null);

    if (xhr.status === 200) {
      this._config = JSON.parse(xhr.responseText) as Configuration;
    } else {
      throw new Error('Failed to load configuration');
    }

    return this._config!;
  }
}
