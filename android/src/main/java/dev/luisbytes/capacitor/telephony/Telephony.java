package dev.luisbytes.capacitor.telephony;

import static android.telephony.TelephonyManager.DATA_CONNECTED;
import static android.telephony.TelephonyManager.DATA_CONNECTING;
import static android.telephony.TelephonyManager.DATA_DISCONNECTED;
import static android.telephony.TelephonyManager.DATA_DISCONNECTING;
import static android.telephony.TelephonyManager.DATA_HANDOVER_IN_PROGRESS;
import static android.telephony.TelephonyManager.DATA_SUSPENDED;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoNr;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthNr;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.getcapacitor.JSObject;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;

public class Telephony {

    private final Context context;
    private final TelephonyManager telephonyManager;

    public Telephony(Context context) {
        this.context = context;
        this.telephonyManager = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    public JSObject getInfo() {
        JSObject ret = new JSObject();
        ret.put("signalStrengthLevel", this.getSignalStrengthLevel());
        ret.put("simOperatorName", this.telephonyManager.getSimOperatorName());
        ret.put("dataState", this.getDataState());
        return ret;
    }

    public JSObject getRadioInfo() {
        JSObject ret = new JSObject();

        // Qualitative signal level
        ret.put("signalStrengthLevel", this.getSignalStrengthLevel());

        // Raw radio values — requires READ_PHONE_STATE + API 29+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && this.checkPermission()) {
            this.appendRawSignalMetrics(ret);
        } else {
            ret.put("rsrp", (Object) null);
            ret.put("rsrq", (Object) null);
            ret.put("sinr", (Object) null);
            ret.put("rssi", (Object) null);
            ret.put("cqi", (Object) null);
        }

        // VoLTE / VoNR — Android 12+ (API 31+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ret.put("isVoLteAvailable", this.telephonyManager.isVoLteAvailable());
            ret.put("isNrAvailable", this.getDataNetworkType(false).equals("5G"));
        } else {
            ret.put("isVoLteAvailable", (Object) null);
            ret.put("isNrAvailable", (Object) null);
        }

        // IPv4 / IPv6
        ret.put("ipVersion", this.detectIpVersion());

        return ret;
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void appendRawSignalMetrics(JSObject ret) {
        try {
            List<CellInfo> cellInfoList = this.telephonyManager.getAllCellInfo();
            if (cellInfoList == null || cellInfoList.isEmpty()) {
                putNullRadio(ret);
                return;
            }

            for (CellInfo cellInfo : cellInfoList) {
                if (!cellInfo.isRegistered()) continue;

                // LTE
                if (cellInfo instanceof CellInfoLte) {
                    CellSignalStrengthLte lte = ((CellInfoLte) cellInfo).getCellSignalStrength();
                    putOrNull(ret, "rsrp", lte.getRsrp());
                    putOrNull(ret, "rsrq", lte.getRsrq());
                    putOrNull(ret, "rssi", lte.getRssi());
                    putOrNull(ret, "cqi", lte.getCqi());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        putOrNull(ret, "sinr", lte.getRssnr());
                    } else {
                        ret.put("sinr", (Object) null);
                    }
                    return;
                }

                // 5G NR
                if (cellInfo instanceof CellInfoNr) {
                    CellSignalStrengthNr nr = (CellSignalStrengthNr) ((CellInfoNr) cellInfo).getCellSignalStrength();
                    putOrNull(ret, "rsrp", nr.getSsRsrp());
                    putOrNull(ret, "rsrq", nr.getSsRsrq());
                    putOrNull(ret, "sinr", nr.getSsSinr());
                    ret.put("rssi", (Object) null);
                    ret.put("cqi", (Object) null);
                    return;
                }

                // WCDMA (3G)
                if (cellInfo instanceof CellInfoWcdma) {
                    putOrNull(ret, "rssi", ((CellInfoWcdma) cellInfo).getCellSignalStrength().getDbm());
                    ret.put("rsrp", (Object) null);
                    ret.put("rsrq", (Object) null);
                    ret.put("sinr", (Object) null);
                    ret.put("cqi", (Object) null);
                    return;
                }
            }

            putNullRadio(ret);
        } catch (Exception e) {
            putNullRadio(ret);
        }
    }

    private void putOrNull(JSObject obj, String key, int value) {
        if (value != Integer.MAX_VALUE) {
            obj.put(key, value);
        } else {
            obj.put(key, (Object) null);
        }
    }

    private void putNullRadio(JSObject ret) {
        ret.put("rsrp", (Object) null);
        ret.put("rsrq", (Object) null);
        ret.put("sinr", (Object) null);
        ret.put("rssi", (Object) null);
        ret.put("cqi", (Object) null);
    }

    private String detectIpVersion() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            boolean hasIpv4 = false;
            boolean hasIpv6 = false;

            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (!iface.isUp() || iface.isLoopback()) continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof Inet6Address) hasIpv6 = true;
                    else if (addr instanceof Inet4Address) hasIpv4 = true;
                }
            }

            if (hasIpv4 && hasIpv6) return "dual";
            if (hasIpv6) return "IPv6";
            if (hasIpv4) return "IPv4";
            return "unknown";
        } catch (Exception e) {
            return "unknown";
        }
    }

    public String getDataNetworkType(@Nullable Boolean withBasicPermission) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) return "UNKNOWN";

        final boolean permissionGranted = this.checkPermission();
        if (!permissionGranted) return "UNKNOWN";

        if (Boolean.TRUE.equals(withBasicPermission) && Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            return "UNKNOWN";
        }

        @SuppressLint("MissingPermission") final int dataNetworkType = this.telephonyManager.getDataNetworkType();

        if (
                dataNetworkType == TelephonyManager.NETWORK_TYPE_GPRS ||
                        dataNetworkType == TelephonyManager.NETWORK_TYPE_EDGE ||
                        dataNetworkType == TelephonyManager.NETWORK_TYPE_CDMA ||
                        dataNetworkType == TelephonyManager.NETWORK_TYPE_1xRTT ||
                        dataNetworkType == TelephonyManager.NETWORK_TYPE_IDEN
        ) {
            return "2G";
        } else if (
                dataNetworkType == TelephonyManager.NETWORK_TYPE_UMTS ||
                        dataNetworkType == TelephonyManager.NETWORK_TYPE_EVDO_0 ||
                        dataNetworkType == TelephonyManager.NETWORK_TYPE_EVDO_A ||
                        dataNetworkType == TelephonyManager.NETWORK_TYPE_HSDPA ||
                        dataNetworkType == TelephonyManager.NETWORK_TYPE_HSUPA ||
                        dataNetworkType == TelephonyManager.NETWORK_TYPE_HSPA ||
                        dataNetworkType == TelephonyManager.NETWORK_TYPE_EVDO_B ||
                        dataNetworkType == TelephonyManager.NETWORK_TYPE_EHRPD ||
                        dataNetworkType == TelephonyManager.NETWORK_TYPE_HSPAP
        ) {
            return "3G";
        } else if (dataNetworkType == TelephonyManager.NETWORK_TYPE_LTE) {
            return "LTE";
        } else if (dataNetworkType == TelephonyManager.NETWORK_TYPE_NR) {
            return "5G";
        }

        return "UNKNOWN";
    }

    private String getSignalStrengthLevel() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) return "UNKNOWN";

        SignalStrength signalStrength = this.telephonyManager.getSignalStrength();
        if (signalStrength == null) return "UNKNOWN";

        int level = signalStrength.getLevel();
        if (level == 0) return "NONE";
        if (level == 1) return "POOR";
        if (level == 2) return "MODERATE";
        if (level == 3) return "GOOD";
        if (level == 4) return "GREAT";

        return "UNKNOWN";
    }

    private String getDataState() {
        int dataState = this.telephonyManager.getDataState();
        if (dataState == DATA_DISCONNECTED) return "DISCONNECTED";
        if (dataState == DATA_CONNECTING) return "CONNECTING";
        if (dataState == DATA_CONNECTED) return "CONNECTED";
        if (dataState == DATA_SUSPENDED) return "SUSPENDED";
        if (dataState == DATA_DISCONNECTING) return "DISCONNECTING";
        if (dataState == DATA_HANDOVER_IN_PROGRESS) return "HANDOVER_IN_PROGRESS";
        return "UNKNOWN";
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private Boolean checkPermission() {
        int permissionState;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            permissionState = this.context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
            return permissionState == PackageManager.PERMISSION_GRANTED;
        }
        permissionState = this.context.checkSelfPermission(Manifest.permission.READ_BASIC_PHONE_STATE);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }
}
