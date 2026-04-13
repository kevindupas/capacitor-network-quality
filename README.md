# @kevindupas/capacitor-telephony-infos

Extended TelephonyManager for Android — RSRP, RSRQ, SINR, RSSI, VoLTE, VoNR, IP version.

> **iOS note:** Raw radio indicators (RSRP, RSRQ, SINR, RSSI), network generation, and SIM operator are not accessible on iOS due to Apple platform restrictions. This plugin is Android-only for radio data.

## Install

```bash
npm install @kevindupas/capacitor-telephony-infos
npx cap sync
```

## Android setup

To use this plugin you need to add the following permissions to the `AndroidManifest.xml` before or after the `application` tag.

```xml
<uses-permission android:name="android.permission.READ_BASIC_PHONE_STATE" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" android:maxSdkVersion="32" />
```

## API

<docgen-index>

* [`getInfo()`](#getinfo)
* [`getRadioInfo()`](#getradioinfo)
* [`getNetworkType(...)`](#getnetworktype)
* [Interfaces](#interfaces)
* [Enums](#enums)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### getInfo()

```typescript
getInfo() => Promise<TelephonyInfo>
```

Returns basic telephony info: signal level, operator name, data state.
Available on Android only.

**Returns:** <code>Promise&lt;<a href="#telephonyinfo">TelephonyInfo</a>&gt;</code>

--------------------


### getRadioInfo()

```typescript
getRadioInfo() => Promise<TelephonyRadioInfo>
```

Returns extended radio information: raw signal metrics (RSRP, RSRQ, SINR, RSSI),
VoLTE/VoNR availability, and IP version.
Requires READ_PHONE_STATE permission on Android.
Not available on iOS (Apple platform restriction).

**Returns:** <code>Promise&lt;<a href="#telephonyradioinfo">TelephonyRadioInfo</a>&gt;</code>

--------------------


### getNetworkType(...)

```typescript
getNetworkType(options?: { withBasicPermission?: boolean | undefined; } | undefined) => Promise<{ type: TelephonyNetworkType; }>
```

Returns the current data network type (2G, 3G, LTE, 5G).
Available on Android only.

| Param         | Type                                            |
| ------------- | ----------------------------------------------- |
| **`options`** | <code>{ withBasicPermission?: boolean; }</code> |

**Returns:** <code>Promise&lt;{ type: <a href="#telephonynetworktype">TelephonyNetworkType</a>; }&gt;</code>

--------------------


### Interfaces


#### TelephonyInfo

| Prop                      | Type                                                                                  |
| ------------------------- | ------------------------------------------------------------------------------------- |
| **`dataState`**           | <code><a href="#telephonydatastate">TelephonyDataState</a></code>                     |
| **`signalStrengthLevel`** | <code><a href="#telephonysignalstrengthlevel">TelephonySignalStrengthLevel</a></code> |
| **`simOperatorName`**     | <code>string</code>                                                                   |


#### TelephonyRadioInfo

| Prop                      | Type                                                                                  | Description                                                                                                                                     |
| ------------------------- | ------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------- |
| **`signalStrengthLevel`** | <code><a href="#telephonysignalstrengthlevel">TelephonySignalStrengthLevel</a></code> | Qualitative signal level (NONE / POOR / MODERATE / GOOD / GREAT).                                                                               |
| **`rsrp`**                | <code>number \| null</code>                                                           | Reference Signal Received Power in dBm (LTE/NR). Typical range: -44 (excellent) to -140 (no signal). null if unavailable or unsupported.        |
| **`rsrq`**                | <code>number \| null</code>                                                           | Reference Signal Received Quality in dB (LTE/NR). Typical range: -3 (excellent) to -20 (poor). null if unavailable or unsupported.              |
| **`sinr`**                | <code>number \| null</code>                                                           | Signal-to-Interference-plus-Noise Ratio in dB (LTE/NR). Typical range: +30 (excellent) to -20 (poor). null if unavailable or unsupported.       |
| **`rssi`**                | <code>number \| null</code>                                                           | Received Signal Strength Indicator in dBm (WCDMA/3G or LTE). Typical range: -50 (excellent) to -100 (poor). null if unavailable or unsupported. |
| **`cqi`**                 | <code>number \| null</code>                                                           | Channel Quality Indicator (LTE only, 0–15). null if unavailable or unsupported.                                                                 |
| **`isVoLteAvailable`**    | <code>boolean \| null</code>                                                          | Whether VoLTE (Voice over LTE) is supported by the device and network. Android 12+ only. null on older versions and iOS.                        |
| **`isNrAvailable`**       | <code>boolean \| null</code>                                                          | Whether VoNR / 5G NR is supported by the device and network. Android 12+ only. null on older versions and iOS.                                  |
| **`ipVersion`**           | <code><a href="#telephonyipversion">TelephonyIpVersion</a></code>                     | Detected IP version: "IPv4", "IPv6", "dual", or "unknown".                                                                                      |


### Enums


#### TelephonyDataState

| Members                    | Value                               |
| -------------------------- | ----------------------------------- |
| **`UNKNOWN`**              | <code>"UNKNOWN"</code>              |
| **`DISCONNECTED`**         | <code>"DISCONNECTED"</code>         |
| **`CONNECTING`**           | <code>"CONNECTING"</code>           |
| **`CONNECTED`**            | <code>"CONNECTED"</code>            |
| **`SUSPENDED`**            | <code>"SUSPENDED"</code>            |
| **`DISCONNECTING`**        | <code>"DISCONNECTING"</code>        |
| **`HANDOVER_IN_PROGRESS`** | <code>"HANDOVER_IN_PROGRESS"</code> |


#### TelephonySignalStrengthLevel

| Members        | Value                   |
| -------------- | ----------------------- |
| **`UNKNOWN`**  | <code>"UNKNOWN"</code>  |
| **`NONE`**     | <code>"NONE"</code>     |
| **`POOR`**     | <code>"POOR"</code>     |
| **`MODERATE`** | <code>"MODERATE"</code> |
| **`GOOD`**     | <code>"GOOD"</code>     |
| **`GREAT`**    | <code>"GREAT"</code>    |


#### TelephonyIpVersion

| Members       | Value                  |
| ------------- | ---------------------- |
| **`UNKNOWN`** | <code>"unknown"</code> |
| **`IPV4`**    | <code>"IPv4"</code>    |
| **`IPV6`**    | <code>"IPv6"</code>    |
| **`DUAL`**    | <code>"dual"</code>    |


#### TelephonyNetworkType

| Members       | Value                  |
| ------------- | ---------------------- |
| **`UNKNOWN`** | <code>"UNKNOWN"</code> |
| **`TWO_G`**   | <code>"2G"</code>      |
| **`THREE_G`** | <code>"3G"</code>      |
| **`LTE`**     | <code>"LTE"</code>     |
| **`FIVE_G`**  | <code>"5G"</code>      |

</docgen-api>
