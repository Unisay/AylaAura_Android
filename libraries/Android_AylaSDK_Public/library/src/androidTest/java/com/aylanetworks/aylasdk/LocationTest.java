package com.aylanetworks.aylasdk;

import android.test.InstrumentationTestCase;
import com.aylanetworks.aylasdk.auth.AylaAuthorization;
import com.aylanetworks.aylasdk.error.RequestFuture;
import com.aylanetworks.aylasdk.error.ServerError;

import org.joda.time.DateTimeZone;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
/**
 * Android_AylaSDK
 * <p>
 * Copyright 2016 Ayla Networks, all rights reserved
 */

public class LocationTest extends InstrumentationTestCase{

    private boolean _isSignedIn;
    AylaDevice _device;
    AylaDeviceManager _deviceManager;
    String _testDeviceLat ;
    String _testDeviceLong;
    String _testLat1 = "39.09";
    String _testLong1 = "-121.5673";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _isSignedIn = TestConstants.signIn(getInstrumentation().getContext());
        assertTrue("Failed to sign-in", _isSignedIn);
        TestConstants.waitForDeviceManagerInitComplete();
        _deviceManager = AylaNetworks.sharedInstance()
                .getSessionManager(TestConstants.TEST_SESSION_NAME).getDeviceManager();
        assertNotNull(_deviceManager);
        _device = _deviceManager.deviceWithDSN(TestConstants.TEST_DEVICE_DSN);
        _testDeviceLat = _device.getLat();
        _testDeviceLong = _device.getLng();
        assertNotNull(_device);
    }

    //Update with valid data
    public void testUpdateLocation(){
        RequestFuture<AylaDevice> future = RequestFuture.newFuture();
        _device.updateLocation(_testLat1, _testLong1, AylaDevice.LocationProvider.User, future,
                future);

        try {
            future.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        } catch (ExecutionException e) {
            e.printStackTrace();
            fail(e.getMessage());
        } catch (TimeoutException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    //Update with invalid location
    public void testUpdateLocationInvalidLocation(){
        RequestFuture<AylaDevice> future = RequestFuture.newFuture();
        _device.updateLocation("3745376237", "28346782347", AylaDevice.LocationProvider.User,
                future, future);

        try {
            future.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        } catch (ExecutionException e) {
            e.printStackTrace();
            try{
                ServerError error = (ServerError) e.getCause();
                assertTrue(error.getServerResponseCode() == 422);
            } catch (ClassCastException ex){
                fail("testUpdateLocationInvalidDevice failed with unexpected " +
                        "error code " + e);
            }
        } catch (TimeoutException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        RequestFuture<AylaDevice> future = RequestFuture.newFuture();
        _device.updateLocation(_testDeviceLat, _testDeviceLong, AylaDevice.LocationProvider.User, future,
                future);

        try {
            future.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail(e.getMessage());
        } catch (ExecutionException e) {
            e.printStackTrace();
            fail(e.getMessage());
        } catch (TimeoutException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}