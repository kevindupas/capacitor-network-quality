package dev.kevindupas.capacitor.telephony;

import android.Manifest;
import android.os.Build;
import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;

@CapacitorPlugin(
    name = "Telephony",
    permissions = {
        @Permission(alias = "phone_state", strings = { Manifest.permission.READ_BASIC_PHONE_STATE }),
        @Permission(alias = "location", strings = { Manifest.permission.ACCESS_FINE_LOCATION })
    }
)
public class TelephonyPlugin extends Plugin {

    private Telephony implementation;

    @Override
    public void load() {
        implementation = new Telephony(getContext());
    }

    @PluginMethod
    public void getInfo(PluginCall call) {
        final JSObject info = implementation.getInfo();
        call.resolve(info);
    }

    @PluginMethod
    public void getRadioInfo(PluginCall call) {
        final boolean locationGranted = getPermissionState("location") == PermissionState.GRANTED;

        if (!locationGranted) {
            requestPermissionForAliases(new String[]{"phone_state", "location"}, call, "radioInfoPermsCallback");
            return;
        }

        final JSObject info = implementation.getRadioInfo();
        call.resolve(info);
    }

    @PermissionCallback
    private void radioInfoPermsCallback(PluginCall call) {
        if (getPermissionState("location") == PermissionState.GRANTED) {
            final JSObject info = implementation.getRadioInfo();
            call.resolve(info);
        } else {
            call.reject("Location permission is required for radio info");
        }
    }

    @PluginMethod
    public void getNetworkType(PluginCall call) {
        Boolean withBasicPermission = call.getBoolean("withBasicPermission");

        final boolean permissionGranted = this.checkPermission(withBasicPermission);

        if (!permissionGranted) {
            requestPermissionForAlias("phone_state", call, "phoneStatePermsCallback");
            return;
        }

        String state = this.implementation.getDataNetworkType(withBasicPermission);

        JSObject ret = new JSObject();
        ret.put("type", state);
        call.resolve(ret);
    }

    @PermissionCallback
    private void phoneStatePermsCallback(PluginCall call) {
        if (getPermissionState("phone_state") == PermissionState.GRANTED) {
            getNetworkType(call);
        } else {
            call.reject("Permission is required");
        }
    }

    private boolean checkPermission(Boolean withBasicPermission) {
        // Location is required for getAllCellInfo() — always check it
        if (getPermissionState("location") == PermissionState.GRANTED) {
            return true;
        }
        if (Boolean.TRUE.equals(withBasicPermission)) {
            return getPermissionState("phone_state") == PermissionState.GRANTED;
        }
        return false;
    }
}
