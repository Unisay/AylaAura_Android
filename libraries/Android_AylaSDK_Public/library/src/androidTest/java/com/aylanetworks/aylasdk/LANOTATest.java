package com.aylanetworks.aylasdk;/*
 * AylaSDK
 *
 * Copyright 2015 Ayla Networks, all rights reserved
 */

import android.os.Environment;
import android.test.InstrumentationTestCase;

import com.aylanetworks.aylasdk.error.RequestFuture;
import com.aylanetworks.aylasdk.ota.AylaLanOTADevice;
import com.aylanetworks.aylasdk.ota.AylaOTAImageInfo;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class LANOTATest extends InstrumentationTestCase {
    private final static String LOG_TAG = "LANOTATest";
    private static final String OTA_DEVICE_DSN = "AC000W000101534";
    private static final String OTA_DEVICE_SSID= "Ayla-00aefa040b57";

    @Override
    public void setUp() {
        TestConstants.signIn(getInstrumentation().getContext());
        TestConstants.waitForDeviceManagerInitComplete();
    }
    public void testLanMode() {
        AylaDeviceManager deviceManager = AylaNetworks.sharedInstance()
                .getSessionManager(TestConstants.TEST_SESSION_NAME).getDeviceManager();
        RequestFuture<AylaOTAImageInfo> future = RequestFuture.newFuture();
        AylaLanOTADevice aylaLanOTA = new AylaLanOTADevice(deviceManager,OTA_DEVICE_DSN,
                OTA_DEVICE_SSID);
        aylaLanOTA.fetchOTAImageInfo(future,future);
        AylaOTAImageInfo imageInfo=null;
        AylaLog.d(LOG_TAG, "Calling fetchOTAImageInfo");
        try {
            int API_TIMEOUT_MS = 20000;
            imageInfo=future.get(API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        } catch (ExecutionException e) {
            fail(e.getMessage());
        } catch (TimeoutException e) {
            fail(e.getMessage());
        }
        StringBuilder path = new StringBuilder(Environment.getExternalStorageDirectory().toString());
        path.append("/Download/");
        path.append(OTA_DEVICE_DSN);
        path.append(".image");

        AylaLog.d(LOG_TAG, "Download Image to path " +path);

        RequestFuture<AylaAPIRequest.EmptyResponse> futureFetchFile = RequestFuture.newFuture();
        aylaLanOTA.fetchOTAImageFile(imageInfo,path.toString(),null,futureFetchFile,futureFetchFile);
        AylaLog.d(LOG_TAG, "Calling fetchOTAImageFile");
        try {
            int API_TIMEOUT_MS = 30000;
            futureFetchFile.get(API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        } catch (ExecutionException e) {
            fail(e.getMessage());
        } catch (TimeoutException e) {
            fail(e.getMessage());
        }

        AylaLog.d(LOG_TAG, "Calling pushOTAImageToDevice");
        RequestFuture<AylaAPIRequest.EmptyResponse> futurePushImage = RequestFuture.newFuture();
        aylaLanOTA.pushOTAImageToDevice(futurePushImage,futurePushImage);
        try {
            int API_TIMEOUT_MS = 90000;
            futurePushImage.get(API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        } catch (ExecutionException e) {
            fail(e.getMessage());
        } catch (TimeoutException e) {
            fail(e.getMessage());
        }
    }
}