
package com.putao.camera.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public class NetType {
    /**
     * 联通的3G为UMTS或HSDPA，移动和联通的2G为GPRS或EDGE，电信的2G为CDMA，电信的3G为EVDO
     */
    public static int getNetworkType(Context context) {
        ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectMgr.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            return info.getType();
        } else {
            return -1;
        }
    }

    public static boolean isStartWifi(Context context) {
        try {
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            return (wm != null && WifiManager.WIFI_STATE_ENABLED == wm.getWifiState());
        } catch (Exception e) {
        }
        return false;
    }
}
