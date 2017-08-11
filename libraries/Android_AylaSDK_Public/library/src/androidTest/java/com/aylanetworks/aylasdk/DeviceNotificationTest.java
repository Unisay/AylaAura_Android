package com.aylanetworks.aylasdk;
/*
 * AylaSDK
 *
 * Copyright 2015 Ayla Networks, all rights reserved
 */

import android.test.InstrumentationTestCase;

import com.aylanetworks.aylasdk.error.RequestFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DeviceNotificationTest extends InstrumentationTestCase {
    private AylaDevice _device;
    private AylaDeviceNotification _deviceNotification;

    @Override
    protected void setUp() throws Exception {
        boolean isSignedIn = TestConstants.signIn(getInstrumentation().getContext());
        assertTrue(isSignedIn);
        TestConstants.waitForDeviceManagerInitComplete();
        AylaDeviceManager dm = AylaNetworks.sharedInstance()
                .getSessionManager(TestConstants.TEST_SESSION_NAME).getDeviceManager();
        _device = dm.deviceWithDSN(TestConstants.TEST_DEVICE_DSN);
        assertNotNull(_device);
    }

    public void testCreateDeviceNotification() {
        AylaDeviceNotification deviceNotification = new AylaDeviceNotification();
        deviceNotification.setNotificationType(AylaDeviceNotification.NotificationType.
                ConnectionLost);
        deviceNotification.setThreshold(3600);
        deviceNotification.setMessage("Connection dropped");

        RequestFuture<AylaDeviceNotification> future = RequestFuture.newFuture();
        _device.createNotification(deviceNotification, future, future);
        try {
            int API_TIMEOUT_MS = 20000;
            _deviceNotification = future.get(API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in DeviceNotification creation " + e);
        } catch (ExecutionException e) {
            fail("Error in DeviceNotification creation " + e);
        } catch (TimeoutException e) {
            fail("Error in DeviceNotification creation " + e);
        }
        assertNotNull(_deviceNotification);
    }

    public void testUpdateDeviceNotification() {
        //First create a notification
        AylaDeviceNotification deviceNotification = new AylaDeviceNotification();
        deviceNotification.setNotificationType(AylaDeviceNotification.NotificationType.
                ConnectionLost);
        deviceNotification.setThreshold(3600);
        deviceNotification.setMessage("Connection dropped");

        RequestFuture<AylaDeviceNotification> future = RequestFuture.newFuture();
        _device.createNotification(deviceNotification, future, future);
        try {
            int API_TIMEOUT_MS = 20000;
            _deviceNotification = future.get(API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in DeviceNotification creation " + e);
        } catch (ExecutionException e) {
            fail("Error in DeviceNotification creation " + e);
        } catch (TimeoutException e) {
            fail("Error in DeviceNotification creation " + e);
        }
        assertNotNull(_deviceNotification);

        //Now update the created Notification's ThreshHold
        Integer threshHold = 2000;
        _deviceNotification.setThreshold(threshHold);
        RequestFuture<AylaDeviceNotification> futureUpdate = RequestFuture.newFuture();
        _device.updateNotification(_deviceNotification, futureUpdate, futureUpdate);
        try {
            int API_TIMEOUT_MS = 20000;
            _deviceNotification = futureUpdate.get(API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in DeviceNotification update " + e);
        } catch (ExecutionException e) {
            fail("Error in DeviceNotification update " + e);
        } catch (TimeoutException e) {
            fail("Error in DeviceNotification update " + e);
        }
        assertNotNull(_deviceNotification);
        assertEquals(_deviceNotification.getThreshold(), threshHold);
    }

    //fetch all Notifications
    public void testFetchAllNotifications() {
        AylaDeviceNotification deviceNotification = new AylaDeviceNotification();
        deviceNotification.setNotificationType(AylaDeviceNotification.NotificationType.
                ConnectionLost);
        deviceNotification.setThreshold(3600);
        deviceNotification.setMessage("Connection dropped");

        RequestFuture<AylaDeviceNotification> future = RequestFuture.newFuture();
        _device.createNotification(deviceNotification, future, future);
        try {
            int API_TIMEOUT_MS = 20000;
            _deviceNotification = future.get(API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in DeviceNotification creation " + e);
        } catch (ExecutionException e) {
            fail("Error in DeviceNotification creation " + e);
        } catch (TimeoutException e) {
            fail("Error in DeviceNotification creation " + e);
        }
        assertNotNull(_deviceNotification);

        RequestFuture<AylaDeviceNotification[]> futureFetch = RequestFuture.newFuture();
        AylaDeviceNotification[] notifications = null;
        _device.fetchNotifications(futureFetch, futureFetch);
        try {
            int API_TIMEOUT_MS = 20000;
            notifications = futureFetch.get(API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in DeviceNotification fetch " + e);
        } catch (ExecutionException e) {
            fail("Error in DeviceNotification fetch " + e);
        } catch (TimeoutException e) {
            fail("Error in DeviceNotification fetch " + e);
        }
        assertNotNull(notifications);
        assert (notifications.length > 0);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if (_deviceNotification != null) {
            RequestFuture<AylaAPIRequest.EmptyResponse> futureDelete = RequestFuture.newFuture();
            _device.deleteNotification(_deviceNotification, futureDelete, futureDelete);
            try {
                futureDelete.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                fail("Error in DeviceNotification delete " + e);
            } catch (ExecutionException e) {
                fail("Error in DeviceNotification delete " + e);
            } catch (TimeoutException e) {
                fail("Error in DeviceNotification delete " + e);
            }
        }
    }
}
