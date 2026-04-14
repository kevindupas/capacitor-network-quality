import type { PermissionState } from "@capacitor/core";

export interface PermissionStatus {
  phone_state: PermissionState;
  location: PermissionState;
}

export interface TelephonyPlugin {
  /**
   * Check current permission status for phone state.
   */
  checkPermissions(): Promise<PermissionStatus>;

  /**
   * Request phone state permissions from the user.
   */
  requestPermissions(): Promise<PermissionStatus>;

  /**
   * Returns basic telephony info: signal level, operator name, data state.
   * Available on Android only.
   */
  getInfo(): Promise<TelephonyInfo>;

  /**
   * Returns extended radio information: raw signal metrics (RSRP, RSRQ, SINR, RSSI),
   * VoLTE/VoNR availability, and IP version.
   * Requires READ_PHONE_STATE permission on Android.
   * Not available on iOS (Apple platform restriction).
   */
  getRadioInfo(): Promise<TelephonyRadioInfo>;

  /**
   * Returns the current data network type (2G, 3G, LTE, 5G).
   * Available on Android only.
   */
  getNetworkType(options?: {
    withBasicPermission?: boolean;
  }): Promise<{ type: TelephonyNetworkType }>;
}

export enum TelephonySignalStrengthLevel {
  UNKNOWN = "UNKNOWN",
  NONE = "NONE",
  POOR = "POOR",
  MODERATE = "MODERATE",
  GOOD = "GOOD",
  GREAT = "GREAT",
}

export enum TelephonyNetworkType {
  UNKNOWN = "UNKNOWN",
  TWO_G = "2G",
  THREE_G = "3G",
  LTE = "LTE",
  FIVE_G = "5G",
}

export enum TelephonyDataState {
  UNKNOWN = "UNKNOWN",
  DISCONNECTED = "DISCONNECTED",
  CONNECTING = "CONNECTING",
  CONNECTED = "CONNECTED",
  SUSPENDED = "SUSPENDED",
  DISCONNECTING = "DISCONNECTING",
  HANDOVER_IN_PROGRESS = "HANDOVER_IN_PROGRESS",
}

export enum TelephonyIpVersion {
  UNKNOWN = "unknown",
  IPV4 = "IPv4",
  IPV6 = "IPv6",
  DUAL = "dual",
}

export interface TelephonyInfo {
  dataState: TelephonyDataState;
  signalStrengthLevel: TelephonySignalStrengthLevel;
  simOperatorName: string;
}

export interface TelephonyRadioInfo {
  /**
   * Qualitative signal level (NONE / POOR / MODERATE / GOOD / GREAT).
   */
  signalStrengthLevel: TelephonySignalStrengthLevel;

  /**
   * Reference Signal Received Power in dBm (LTE/NR).
   * Typical range: -44 (excellent) to -140 (no signal).
   * null if unavailable or unsupported.
   */
  rsrp: number | null;

  /**
   * Reference Signal Received Quality in dB (LTE/NR).
   * Typical range: -3 (excellent) to -20 (poor).
   * null if unavailable or unsupported.
   */
  rsrq: number | null;

  /**
   * Signal-to-Interference-plus-Noise Ratio in dB (LTE/NR).
   * Typical range: +30 (excellent) to -20 (poor).
   * null if unavailable or unsupported.
   */
  sinr: number | null;

  /**
   * Received Signal Strength Indicator in dBm (WCDMA/3G or LTE).
   * Typical range: -50 (excellent) to -100 (poor).
   * null if unavailable or unsupported.
   */
  rssi: number | null;

  /**
   * Channel Quality Indicator (LTE only, 0–15).
   * null if unavailable or unsupported.
   */
  cqi: number | null;

  /**
   * Whether VoLTE (Voice over LTE) is supported by the device and network.
   * Android 12+ only. null on older versions and iOS.
   */
  isVoLteAvailable: boolean | null;

  /**
   * Whether VoNR / 5G NR is supported by the device and network.
   * Android 12+ only. null on older versions and iOS.
   */
  isNrAvailable: boolean | null;

  /**
   * Detected IP version: "IPv4", "IPv6", "dual", or "unknown".
   */
  ipVersion: TelephonyIpVersion;
}
