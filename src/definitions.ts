import type { PermissionState } from "@capacitor/core";

export interface PermissionStatus {
  phone_state: PermissionState;
  location: PermissionState;
}

export interface NetworkQualityPlugin {
  /**
   * Check current permission status for phone state and location.
   */
  checkPermissions(): Promise<PermissionStatus>;

  /**
   * Request phone state and location permissions from the user.
   */
  requestPermissions(): Promise<PermissionStatus>;

  /**
   * Returns basic network info: signal level, operator name, data state, MCC/MNC.
   * Available on Android only.
   */
  getInfo(): Promise<NetworkInfo>;

  /**
   * Returns extended radio information: raw signal metrics (RSRP, RSRQ, SINR, RSSI, CQI),
   * VoLTE/VoNR availability, and IP version.
   * Requires READ_PHONE_STATE + ACCESS_FINE_LOCATION permissions on Android.
   * Not available on iOS (Apple platform restriction).
   */
  getRadioInfo(): Promise<RadioInfo>;

  /**
   * Returns the current data network type (2G, 3G, LTE, 5G).
   * Available on Android only.
   */
  getNetworkType(options?: {
    withBasicPermission?: boolean;
  }): Promise<{ type: NetworkType }>;
}

export enum SignalStrengthLevel {
  UNKNOWN = "UNKNOWN",
  NONE = "NONE",
  POOR = "POOR",
  MODERATE = "MODERATE",
  GOOD = "GOOD",
  GREAT = "GREAT",
}

export enum NetworkType {
  UNKNOWN = "UNKNOWN",
  TWO_G = "2G",
  THREE_G = "3G",
  LTE = "LTE",
  FIVE_G = "5G",
}

export enum DataState {
  UNKNOWN = "UNKNOWN",
  DISCONNECTED = "DISCONNECTED",
  CONNECTING = "CONNECTING",
  CONNECTED = "CONNECTED",
  SUSPENDED = "SUSPENDED",
  DISCONNECTING = "DISCONNECTING",
  HANDOVER_IN_PROGRESS = "HANDOVER_IN_PROGRESS",
}

export enum IpVersion {
  UNKNOWN = "unknown",
  IPV4 = "IPv4",
  IPV6 = "IPv6",
  DUAL = "dual",
}

export interface NetworkInfo {
  dataState: DataState;
  signalStrengthLevel: SignalStrengthLevel;
  simOperatorName: string;
  /**
   * Mobile Country Code (3 digits, e.g. "639" for Kenya).
   * null if SIM absent or operator string unavailable.
   */
  mcc: string | null;
  /**
   * Mobile Network Code (2–3 digits, e.g. "02" for Safaricom Kenya).
   * null if SIM absent or operator string unavailable.
   */
  mnc: string | null;
}

export interface RadioInfo {
  signalStrengthLevel: SignalStrengthLevel;
  /**
   * Reference Signal Received Power in dBm (LTE/NR).
   * Typical range: -44 (excellent) to -140 (no signal). null if unavailable.
   */
  rsrp: number | null;
  /**
   * Reference Signal Received Quality in dB (LTE/NR).
   * Typical range: -3 (excellent) to -20 (poor). null if unavailable.
   */
  rsrq: number | null;
  /**
   * Signal-to-Interference-plus-Noise Ratio in dB (LTE/NR).
   * Typical range: +30 (excellent) to -20 (poor). null if unavailable.
   */
  sinr: number | null;
  /**
   * Received Signal Strength Indicator in dBm (WCDMA/3G or LTE).
   * Typical range: -50 (excellent) to -100 (poor). null if unavailable.
   */
  rssi: number | null;
  /**
   * Channel Quality Indicator (LTE only, 0–15). null if unavailable.
   */
  cqi: number | null;
  /**
   * Whether VoLTE is supported by device and network. Android 12+ only.
   */
  isVoLteAvailable: boolean | null;
  /**
   * Whether VoNR / 5G NR is available. Android 12+ only.
   */
  isNrAvailable: boolean | null;
  /**
   * Detected IP version: "IPv4", "IPv6", "dual", or "unknown".
   */
  ipVersion: IpVersion;
}
