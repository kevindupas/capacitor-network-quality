import Foundation
import Capacitor

@objc(NetworkQualityPlugin)
public class NetworkQualityPlugin: CAPPlugin {
    @objc func getInfo(_ call: CAPPluginCall) {
        call.reject("Not available on iOS")
    }

    @objc func getRadioInfo(_ call: CAPPluginCall) {
        call.reject("Not available on iOS")
    }

    @objc func getNetworkType(_ call: CAPPluginCall) {
        call.reject("Not available on iOS")
    }
}
