package com.aylanetworks.aylasdk.util;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.aylanetworks.aylasdk.AylaNetworks;

import java.util.Locale;

/**
 * Android_Aura
 * <p/>
 * Copyright 2016 Ayla Networks, all rights reserved
 */
public class SystemInfoUtils {

    /**
     * Get Manufacturer name of the phone on which app is installed.
     * @return manufacturer of the phone
     */
    public static String getManufacturer(){
        return Build.MANUFACTURER;
    }

    /**
     * Get User visible Model name of the phone on which app is installed.
     * @return Model of the phone
     */
    public static String getModel(){
        return Build.MODEL;
    }

    /**
     * Get OS version of the phone.
     * @return OS Version of the phone.
     */
    public static String getOSVersion(){
        return Build.VERSION.RELEASE;
    }

    /**
     * Get Android SDK version.
     * @return SDK version.
     */
    public static int getSDKVersion(){
        return Build.VERSION.SDK_INT;
    }

    /**
     * Get display language in Location settings.
     * @return Dsiplay language
     */
    public static String getLanguage(){
        return Locale.getDefault().getDisplayLanguage();
    }

    /**
     * Get country name from Location settings
     * @return Display language
     */
    public static String getCountry(){
        return Locale.getDefault().getDisplayCountry();
    }

    /**
     * Get name of network operator.
     * @return Network operator name.
     */
    public static String getNetworkOperator(){
        Context appContext = AylaNetworks.sharedInstance().getContext();
        if(appContext == null){
            return null;
        }
        TelephonyManager telephonyManager = (TelephonyManager) appContext.
                getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getNetworkOperatorName();
    }
}
