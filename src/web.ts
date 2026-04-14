import { WebPlugin } from '@capacitor/core';

import type {
  TelephonyPlugin,
  TelephonyInfo,
  TelephonyNetworkType,
  TelephonyRadioInfo,
  PermissionStatus,
} from './definitions';
import { TelephonySignalStrengthLevel, TelephonyIpVersion } from './definitions';

export class TelephonyWeb extends WebPlugin implements TelephonyPlugin {
  async checkPermissions(): Promise<PermissionStatus> {
    return { phone_state: 'denied', location: 'denied' };
  }

  async requestPermissions(): Promise<PermissionStatus> {
    return { phone_state: 'denied', location: 'denied' };
  }

  async getInfo(): Promise<TelephonyInfo> {
    throw this.unimplemented('Not implemented on web.');
  }

  async getRadioInfo(): Promise<TelephonyRadioInfo> {
    return {
      signalStrengthLevel: TelephonySignalStrengthLevel.UNKNOWN,
      rsrp: null,
      rsrq: null,
      sinr: null,
      rssi: null,
      cqi: null,
      isVoLteAvailable: null,
      isNrAvailable: null,
      ipVersion: TelephonyIpVersion.UNKNOWN,
    };
  }

  async getNetworkType(): Promise<{ type: TelephonyNetworkType }> {
    throw this.unimplemented('Not implemented on web.');
  }
}
