package dev.kevindupas.capacitor.networkquality;

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
import android.telephony.TelephonyDisplayInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.getcapacitor.JSObject;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;

public class NetworkQuality {

    private static final String TAG = "NetworkQualityPlugin";

    private final Context context;
    private final TelephonyManager telephonyManager;

    public NetworkQuality(Context context) {
        this.context = context;
        this.telephonyManager = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    public JSObject getInfo() {
        JSObject ret = new JSObject();
        ret.put("signalStrengthLevel", this.getSignalStrengthLevel());
        ret.put("simOperatorName", this.telephonyManager.getSimOperatorName());
        ret.put("dataState", this.getDataState());

        String simOperator = this.telephonyManager.getSimOperator();
        if (simOperator != null && simOperator.length() >= 5) {
            ret.put("mcc", simOperator.substring(0, 3));
            ret.put("mnc", simOperator.substring(3));
        } else {
            ret.put("mcc", (Object) null);
            ret.put("mnc", (Object) null);
        }

        return ret;
    }

    public JSObject getRadioInfo() {
        JSObject ret = new JSObject();

        ret.put("signalStrengthLevel", this.getSignalStrengthLevel());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && this.checkPermission()) {
            this.appendRawSignalMetrics(ret);
        } else {
            putNullRadio(ret);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            boolean volte = this.context.getPackageManager().hasSystemFeature("android.hardware.telephony.ims");
            ret.put("isVoLteAvailable", volte);
            ret.put("isNrAvailable", this.getDataNetworkType(false).equals("5G"));
        } else {
            ret.put("isVoLteAvailable", (Object) null);
            ret.put("isNrAvailable", (Object) null);
        }

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

                if (cellInfo instanceof CellInfoNr) {
                    CellSignalStrengthNr nr = (CellSignalStrengthNr) ((CellInfoNr) cellInfo).getCellSignalStrength();
                    putOrNull(ret, "rsrp", nr.getSsRsrp());
                    putOrNull(ret, "rsrq", nr.getSsRsrq());
                    putOrNull(ret, "sinr", nr.getSsSinr());
                    ret.put("rssi", (Object) null);
                    ret.put("cqi", (Object) null);
                    return;
                }

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
            Log.e(TAG, "appendRawSignalMetrics error: " + e.getMessage());
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
        } else if (dataNetworkType == TelephonyManager.NETWORK_TYPE_NR) {
            return "5G";
        } else if (dataNetworkType == TelephonyManager.NETWORK_TYPE_LTE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && this.is5GNsaViaDisplayInfo()) {
                return "5G";
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && this.has5GNsaCell()) {
                return "5G";
            }
            return "LTE";
        }

        return "UNKNOWN";
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private boolean is5GNsaViaDisplayInfo() {
        try {
            final boolean[] result = {false};
            final Object lock = new Object();

            class DisplayInfoCallback extends android.telephony.TelephonyCallback
                    implements android.telephony.TelephonyCallback.DisplayInfoListener {
                @Override
                public void onDisplayInfoChanged(TelephonyDisplayInfo info) {
                    int override = info.getOverrideNetworkType();
                    result[0] = override == TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NR_NSA
                            || override == TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NR_NSA_MMWAVE
                            || override == TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NR_ADVANCED;
                    synchronized (lock) { lock.notifyAll(); }
                }
            }
            DisplayInfoCallback callback = new DisplayInfoCallback();

            this.telephonyManager.registerTelephonyCallback(context.getMainExecutor(), callback);
            synchronized (lock) { lock.wait(500); }
            this.telephonyManager.unregisterTelephonyCallback(callback);
            return result[0];
        } catch (Exception e) {
            Log.e(TAG, "is5GNsaViaDisplayInfo error: " + e.getMessage());
            return false;
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private boolean has5GNsaCell() {
        try {
            List<CellInfo> cellInfoList = this.telephonyManager.getAllCellInfo();
            if (cellInfoList == null) return false;
            for (CellInfo cellInfo : cellInfoList) {
                if (cellInfo instanceof CellInfoNr) return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "has5GNsaCell error: " + e.getMessage());
        }
        return false;
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
