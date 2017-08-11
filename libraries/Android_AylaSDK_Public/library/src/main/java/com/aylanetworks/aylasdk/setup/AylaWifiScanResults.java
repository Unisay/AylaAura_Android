package com.aylanetworks.aylasdk.setup;
/*
 * Android_AylaSDK
 *
 * Copyright 2015 Ayla Networks, all rights reserved
 */

import android.text.TextUtils;

import com.google.gson.annotations.Expose;

/**
 * Returned via a call to {@link AylaSetup#fetchDeviceAccessPoints}, this class represents the
 * set of discovered WiFi access points discovered by the mobile device.
 */
public class AylaWifiScanResults {
    @Expose
    public long mtime;
    @Expose
    public Result[] results;

    public static class Result {
        @Expose
        public String ssid;
        @Expose
        public String type;
        @Expose
        public int chan;
        @Expose
        public int signal;
        @Expose
        public int bars;
        @Expose
        public String security;
        @Expose
        public String bssid;

        public boolean isSecurityOpen() {
            return TextUtils.equals(security, "None");
        }
    }

    public static class Wrapper {
        @Expose
        public AylaWifiScanResults wifi_scan;
    }
}
