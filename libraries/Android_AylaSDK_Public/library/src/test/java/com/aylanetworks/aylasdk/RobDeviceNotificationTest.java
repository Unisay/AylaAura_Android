package com.aylanetworks.aylasdk;
/*
 * AylaSDK
 *
 * Copyright 2015 Ayla Networks, all rights reserved
 */

import com.aylanetworks.aylasdk.error.RequestFuture;

import org.junit.After;
import org.junit.Before;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class RobDeviceNotificationTest {
    private AylaDevice _device;
    private AylaDeviceNotification _deviceNotification;

    @Before
    public void setUp() throws Exception {
        RobTestConstants.signIn(RuntimeEnvironment.application.getApplicationContext());
        RobTestConstants.waitForDeviceManagerInitComplete();
        AylaDeviceManager dm = AylaNetworks.sharedInstance()
                .getSessionManager(RobTestConstants.TEST_SESSION_NAME).getDeviceManager();
        _device = dm.deviceWithDSN(RobTestConstants.TEST_DEVICE_DSN);
        assertNotNull(_device);
    }

    @Test
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

    @Test
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
        Integer threshHold = 4000;
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
    @Test
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

    @After
    public void tearDown() throws Exception {
        if (_deviceNotification != null) {
            RequestFuture<AylaAPIRequest.EmptyResponse> futureDelete = RequestFuture.newFuture();
            _device.deleteNotification(_deviceNotification, futureDelete, futureDelete);
            try {
                futureDelete.get(RobTestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
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
