package com.example.lixiaoqing.myaccessibility.tools;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Base64;

import java.util.UUID;

import static android.content.Context.TELEPHONY_SERVICE;

public class DeviceTool {

    @SuppressWarnings("unused")
    public static boolean checkEnable(Context paramContext) {

        NetworkInfo localNetworkInfo = ((ConnectivityManager) paramContext
                .getSystemService("connectivity")).getActiveNetworkInfo();
        return (localNetworkInfo != null) && (localNetworkInfo.isAvailable());
    }


    private static String int2ip(int ipInt) {
        return String.valueOf(ipInt & 0xFF) + "." +
                ((ipInt >> 8) & 0xFF) + "." +
                ((ipInt >> 16) & 0xFF) + "." +
                ((ipInt >> 24) & 0xFF);
    }


    public static String getLocalIpAddress(Context context) {
        try {

            WifiManager wifiManager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int i = wifiInfo.getIpAddress();
            return int2ip(i);
        } catch (Exception ex) {
            return "error";
        }
    }

    public static String getAndroidID(Context context){

        return android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
    }

    public static String getIMEI(Context context){
        try {

            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
            return telephonyManager.getDeviceId();
        } catch (Exception ignored){
            return "null";
        }
    }

    public static String getAndroidVersion(){
        return "Android:" + Build.VERSION.RELEASE;
    }
    public static String getModel(){
        return android.os.Build.MANUFACTURER + ":" + Build.MODEL;
    }

    // get device id
    @SuppressWarnings("unused")
    public String getUniquePseudoID() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD){
            return "";
        }

        String serial;

        StringBuilder stringBuilder = new StringBuilder("35");
        stringBuilder.append(Build.BOARD.length() % 10);
        stringBuilder.append(Build.BRAND.length() % 10);
//        try{
//            stringBuilder.append(Build.SUPPORTED_ABIS[0].length() % 10);
//        } catch (Exception ignored){}
        stringBuilder.append(Build.DEVICE.length() % 10);
        stringBuilder.append(Build.DISPLAY.length() % 10);
        stringBuilder.append(Build.HOST.length() % 10);
        stringBuilder.append(Build.ID.length() % 10);
        stringBuilder.append(Build.MANUFACTURER.length() % 10);
        stringBuilder.append(Build.MODEL.length() % 10);
        stringBuilder.append(Build.PRODUCT.length() % 10);
        stringBuilder.append(Build.TAGS.length() % 10);
        stringBuilder.append(Build.TYPE.length() % 10);
        stringBuilder.append(Build.USER.length() % 10);


        try {
            serial = android.os.Build.class.getField("SERIAL").get(null).toString();
        } catch (Exception ignored) {
            serial = "serial";
        }

        String id = new UUID(stringBuilder.toString().hashCode(), serial.hashCode()).toString();

        return ToBase64StringForUrl(id.getBytes());

    }

    public static String ToBase64StringForUrl(byte[] bytes)
    {
        return new String (Base64.encode(bytes, Base64.URL_SAFE));
    }
}
