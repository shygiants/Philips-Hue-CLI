package io.github.shygiants.philips_hue.hue;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.*;
import io.github.shygiants.philips_hue.console.Shell;
import io.github.shygiants.philips_hue.util.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.List;

/**
 * @auther Sanghoon Yoon (iDBLab, shygiants@gmail.com)
 * @date 2016. 11. 25.
 * @see
 */
public final class Controller implements PHSDKListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);

    private final PHHueSDK phHueSDK;

    public Controller(final String ap_ip, final int ap_port) {
        phHueSDK = PHHueSDK.getInstance();
        PHAccessPoint accessPoint = new PHAccessPoint();
        accessPoint.setIpAddress(ap_ip + ":" + ap_port);
        accessPoint.setUsername("LightAgent");

        phHueSDK.getNotificationManager().registerSDKListener(this);

        phHueSDK.connect(accessPoint);
    }

    public void onCacheUpdated(List cacheNotificationsList, PHBridge bridge) {
        // Here you receive notifications that the BridgeResource Cache was updated. Use the PHMessageType to check
        // which cache was updated, e.g.
        if (cacheNotificationsList.contains(PHMessageType.LIGHTS_CACHE_UPDATED)) {
            LOGGER.debug("Lights Cache Updated ");
        }
    }

    public void onAuthenticationRequired(PHAccessPoint phAccessPoint) {
        phHueSDK.startPushlinkAuthentication(phAccessPoint);
        // Arriving here indicates that Pushlinking is required (to prove the User has physical access to the bridge).
        // Typically here you will display a pushlink image (with a timer) indicating to to the user they need to push
        // the button on their bridge within 30 seconds.
    }

    public void onConnectionResumed(PHBridge phBridge) {

    }

    public void onConnectionLost(PHAccessPoint phAccessPoint) {
        // Here you would handle the loss of connection to your bridge.
        LOGGER.error("Connection Lost");
    }

    public void onAccessPointsFound(List<PHAccessPoint> list) {
        // Handle your bridge search results here.  Typically if multiple results are returned you will want to display
        // them in a list and let the user select their bridge.   If one is found you may opt to connect automatically
        // to that bridge.
    }

    public void onBridgeConnected(PHBridge phBridge, String s) {
        // Here it is recommended to set your connected bridge in your sdk object (as above) and start the heartbeat.
        // At this point you are connected to a bridge so you should pass control to your main program/activity.
        // The username is generated randomly by the bridge.
        // Also it is recommended you store the connected IP Address/ Username in your app here.
        // This will allow easy automatic connection on subsequent use.

        phHueSDK.setSelectedBridge(phBridge);
        phHueSDK.enableHeartbeat(phBridge, PHHueSDK.HB_INTERVAL);
        Shell.getInstance().start(this);
    }

    public void onError(int i, String s) {
        // Here you can handle events such as Bridge Not Responding, Authentication Failed and Bridge Not Found
        LOGGER.error(s);
    }

    public void onParsingErrors(List parsingErrorsList) {
        // Any JSON parsing errors are returned here.  Typically your program should never return these.
        LOGGER.error("{}", parsingErrorsList);
    }

    public List<PHLight> getAllLights() {
        PHBridge phBridge = phHueSDK.getSelectedBridge();
        if (phBridge == null) throw new IllegalStateException();

        return phBridge.getResourceCache().getAllLights();
    }

    public void turnOnOff(String id, boolean onOff) {
        PHBridge phBridge = phHueSDK.getSelectedBridge();
        PHLight light = phBridge.getResourceCache().getLights().get(id);
        if (light == null) {
            System.out.println(String.format(Resources.strings("consoleNoSuchLight"), id));
            return;
        }

        PHLightState lightState = new PHLightState();
        lightState.setOn(onOff);

        phBridge.updateLightState(light, lightState);
        System.out.println(String.format(Resources.strings("consoleLight" + (onOff? "On": "Off")), id));
    }

    public void color(String id) {
        PHBridge phBridge = phHueSDK.getSelectedBridge();
        PHLight light = phBridge.getResourceCache().getLights().get(id);
        if (light == null) {
            System.out.println(String.format(Resources.strings("consoleNoSuchLight"), id));
            return;
        }

        Color color = Color.WHITE;
        float[] xy = PHUtilities.calculateXYFromRGB(
                color.getRed(), color.getGreen(), color.getBlue(), light.getModelNumber());
        PHLightState lightState = new PHLightState();
        lightState.setX(xy[0]);
        lightState.setY(xy[1]);

        phBridge.updateLightState(light, lightState);
        System.out.println(String.format(Resources.strings("consoleLightColor"), id));
    }
}
