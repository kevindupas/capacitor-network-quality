# @kevindupas/capacitor-network-quality

Android TelephonyManager plugin for Capacitor — signal metrics (RSRP, RSRQ, SINR, RSSI, CQI), network type (2G/3G/LTE/5G), VoLTE, 5G NSA detection, IP version, MCC/MNC.

![Capacitor 8](https://img.shields.io/badge/Capacitor-8.x-blue?logo=capacitor)
![Android](https://img.shields.io/badge/Platform-Android-green?logo=android)
![npm](https://img.shields.io/npm/v/@kevindupas/capacitor-network-quality)

> **iOS note:** Raw radio indicators (RSRP, RSRQ, SINR, RSSI), network generation, and SIM operator are not accessible on iOS due to Apple platform restrictions. This plugin is Android-only for radio data.

## Install

```bash
npm install @kevindupas/capacitor-network-quality
npx cap sync
```

## Android setup

Add to `AndroidManifest.xml` (before or after the `application` tag):

```xml
<uses-permission android:name="android.permission.READ_BASIC_PHONE_STATE" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

> `READ_PHONE_STATE` is required for `TelephonyDisplayInfo` (5G NSA detection). `ACCESS_FINE_LOCATION` is required for `getAllCellInfo()` (raw signal metrics). Both are runtime permissions — the plugin requests them automatically.

## Usage

```typescript
import { NetworkQuality } from '@kevindupas/capacitor-network-quality';

// Basic info — no permission required
const info = await NetworkQuality.getInfo();
// { signalStrengthLevel: 'GOOD', simOperatorName: 'Safaricom', dataState: 'CONNECTED', mcc: '639', mnc: '02' }

// Raw signal metrics — requires READ_PHONE_STATE + ACCESS_FINE_LOCATION
const radio = await NetworkQuality.getRadioInfo();
// { rsrp: -85, rsrq: -10, sinr: 15, rssi: -75, cqi: 12, isVoLteAvailable: true, isNrAvailable: false, ipVersion: 'dual' }

// Network type — requires READ_PHONE_STATE
const { type } = await NetworkQuality.getNetworkType();
// 'LTE' | '5G' | '3G' | '2G' | 'UNKNOWN'
```

## API

<docgen-index>

* [`checkPermissions()`](#checkpermissions)
* [`requestPermissions()`](#requestpermissions)
* [`getInfo()`](#getinfo)
* [`getRadioInfo()`](#getradioinfo)
* [`getNetworkType(...)`](#getnetworktype)
* [Interfaces](#interfaces)
* [Type Aliases](#type-aliases)
* [Enums](#enums)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### checkPermissions()

```typescript
checkPermissions() => Promise<PermissionStatus>
```

Check current permission status for phone state and location.

**Returns:** <code>Promise&lt;<a href="#permissionstatus">PermissionStatus</a>&gt;</code>

--------------------


### requestPermissions()

```typescript
requestPermissions() => Promise<PermissionStatus>
```

Request phone state and location permissions from the user.

**Returns:** <code>Promise&lt;<a href="#permissionstatus">PermissionStatus</a>&gt;</code>

--------------------


### getInfo()

```typescript
getInfo() => Promise<NetworkInfo>
```

Returns basic network info: signal level, operator name, data state, MCC/MNC.
Available on Android only.

**Returns:** <code>Promise&lt;<a href="#networkinfo">NetworkInfo</a>&gt;</code>

--------------------


### getRadioInfo()

```typescript
getRadioInfo() => Promise<RadioInfo>
```

Returns extended radio information: raw signal metrics (RSRP, RSRQ, SINR, RSSI, CQI),
VoLTE/VoNR availability, and IP version.
Requires READ_PHONE_STATE + ACCESS_FINE_LOCATION permissions on Android.
Not available on iOS (Apple platform restriction).

**Returns:** <code>Promise&lt;<a href="#radioinfo">RadioInfo</a>&gt;</code>

--------------------


### getNetworkType(...)

```typescript
getNetworkType(options?: { withBasicPermission?: boolean | undefined; } | undefined) => Promise<{ type: NetworkType; }>
```

Returns the current data network type (2G, 3G, LTE, 5G).
Available on Android only.

| Param         | Type                                            |
| ------------- | ----------------------------------------------- |
| **`options`** | <code>{ withBasicPermission?: boolean; }</code> |

**Returns:** <code>Promise&lt;{ type: <a href="#networktype">NetworkType</a>; }&gt;</code>

--------------------


### Interfaces


#### PermissionStatus

| Prop              | Type                                                        |
| ----------------- | ----------------------------------------------------------- |
| **`phone_state`** | <code><a href="#permissionstate">PermissionState</a></code> |
| **`location`**    | <code><a href="#permissionstate">PermissionState</a></code> |


#### NetworkInfo

| Prop                      | Type                                                                | Description                                                                                                         |
| ------------------------- | ------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------- |
| **`dataState`**           | <code><a href="#datastate">DataState</a></code>                     |                                                                                                                     |
| **`signalStrengthLevel`** | <code><a href="#signalstrengthlevel">SignalStrengthLevel</a></code> |                                                                                                                     |
| **`simOperatorName`**     | <code>string</code>                                                 |                                                                                                                     |
| **`mcc`**                 | <code>string \| null</code>                                         | Mobile Country Code (3 digits, e.g. "639" for Kenya). null if SIM absent or operator string unavailable.            |
| **`mnc`**                 | <code>string \| null</code>                                         | Mobile Network Code (2–3 digits, e.g. "02" for Safaricom Kenya). null if SIM absent or operator string unavailable. |


#### RadioInfo

| Prop                      | Type                                                                | Description                                                                                                                      |
| ------------------------- | ------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------- |
| **`signalStrengthLevel`** | <code><a href="#signalstrengthlevel">SignalStrengthLevel</a></code> |                                                                                                                                  |
| **`rsrp`**                | <code>number \| null</code>                                         | Reference Signal Received Power in dBm (LTE/NR). Typical range: -44 (excellent) to -140 (no signal). null if unavailable.        |
| **`rsrq`**                | <code>number \| null</code>                                         | Reference Signal Received Quality in dB (LTE/NR). Typical range: -3 (excellent) to -20 (poor). null if unavailable.              |
| **`sinr`**                | <code>number \| null</code>                                         | Signal-to-Interference-plus-Noise Ratio in dB (LTE/NR). Typical range: +30 (excellent) to -20 (poor). null if unavailable.       |
| **`rssi`**                | <code>number \| null</code>                                         | Received Signal Strength Indicator in dBm (WCDMA/3G or LTE). Typical range: -50 (excellent) to -100 (poor). null if unavailable. |
| **`cqi`**                 | <code>number \| null</code>                                         | Channel Quality Indicator (LTE only, 0–15). null if unavailable.                                                                 |
| **`isVoLteAvailable`**    | <code>boolean \| null</code>                                        | Whether VoLTE is supported by device and network. Android 12+ only.                                                              |
| **`isNrAvailable`**       | <code>boolean \| null</code>                                        | Whether VoNR / 5G NR is available. Android 12+ only.                                                                             |
| **`ipVersion`**           | <code><a href="#ipversion">IpVersion</a></code>                     | Detected IP version: "IPv4", "IPv6", "dual", or "unknown".                                                                       |


### Type Aliases


#### PermissionState

<code>'prompt' | 'prompt-with-rationale' | 'granted' | 'denied'</code>


### Enums


#### DataState

| Members                    | Value                               |
| -------------------------- | ----------------------------------- |
| **`UNKNOWN`**              | <code>"UNKNOWN"</code>              |
| **`DISCONNECTED`**         | <code>"DISCONNECTED"</code>         |
| **`CONNECTING`**           | <code>"CONNECTING"</code>           |
| **`CONNECTED`**            | <code>"CONNECTED"</code>            |
| **`SUSPENDED`**            | <code>"SUSPENDED"</code>            |
| **`DISCONNECTING`**        | <code>"DISCONNECTING"</code>        |
| **`HANDOVER_IN_PROGRESS`** | <code>"HANDOVER_IN_PROGRESS"</code> |


#### SignalStrengthLevel

| Members        | Value                   |
| -------------- | ----------------------- |
| **`UNKNOWN`**  | <code>"UNKNOWN"</code>  |
| **`NONE`**     | <code>"NONE"</code>     |
| **`POOR`**     | <code>"POOR"</code>     |
| **`MODERATE`** | <code>"MODERATE"</code> |
| **`GOOD`**     | <code>"GOOD"</code>     |
| **`GREAT`**    | <code>"GREAT"</code>    |


#### IpVersion

| Members       | Value                  |
| ------------- | ---------------------- |
| **`UNKNOWN`** | <code>"unknown"</code> |
| **`IPV4`**    | <code>"IPv4"</code>    |
| **`IPV6`**    | <code>"IPv6"</code>    |
| **`DUAL`**    | <code>"dual"</code>    |


#### NetworkType

| Members       | Value                  |
| ------------- | ---------------------- |
| **`UNKNOWN`** | <code>"UNKNOWN"</code> |
| **`TWO_G`**   | <code>"2G"</code>      |
| **`THREE_G`** | <code>"3G"</code>      |
| **`LTE`**     | <code>"LTE"</code>     |
| **`FIVE_G`**  | <code>"5G"</code>      |

</docgen-api>
