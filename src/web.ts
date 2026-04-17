import { WebPlugin } from '@capacitor/core';

import type {
  NetworkQualityPlugin,
  NetworkInfo,
  RadioInfo,
  PermissionStatus,
} from './definitions';
import { SignalStrengthLevel, IpVersion, NetworkType } from './definitions';

export class NetworkQualityWeb extends WebPlugin implements NetworkQualityPlugin {
  async checkPermissions(): Promise<PermissionStatus> {
    return { phone_state: 'denied', location: 'denied' };
  }

  async requestPermissions(): Promise<PermissionStatus> {
    return { phone_state: 'denied', location: 'denied' };
  }

  async getInfo(): Promise<NetworkInfo> {
    throw this.unimplemented('Not implemented on web.');
  }

  async getRadioInfo(): Promise<RadioInfo> {
    return {
      signalStrengthLevel: SignalStrengthLevel.UNKNOWN,
      rsrp: null,
      rsrq: null,
      sinr: null,
      rssi: null,
      cqi: null,
      isVoLteAvailable: null,
      isNrAvailable: null,
      ipVersion: IpVersion.UNKNOWN,
    };
  }

  async getNetworkType(): Promise<{ type: NetworkType }> {
    throw this.unimplemented('Not implemented on web.');
  }
}
