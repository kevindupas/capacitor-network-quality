#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

CAP_PLUGIN(NetworkQualityPlugin, "NetworkQuality",
           CAP_PLUGIN_METHOD(getInfo, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(getRadioInfo, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(getNetworkType, CAPPluginReturnPromise);
)
