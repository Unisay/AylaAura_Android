package com.aylanetworks.aylasdk;
/*
 * AylaSDK
 *
 * Copyright 2015 Ayla Networks, all rights reserved
 */

import android.test.InstrumentationTestCase;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public class RobDeviceManagerTest {

    @Test
    public void testInitComplete() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Thread interrupted");
        }

        RobTestConstants.signIn(RuntimeEnvironment.application.getApplicationContext());
        RobTestConstants.waitForDeviceManagerInitComplete();

        AylaDeviceManager deviceManager = AylaNetworks.sharedInstance()
                .getSessionManager(RobTestConstants.TEST_SESSION_NAME).getDeviceManager();

        // We should have at least one device at this point
        List<AylaDevice> deviceList = deviceManager.getDevices();
        assertNotNull(deviceList);
        assertTrue(deviceList.size() > 0);

        // The devices should all have properties
        for (AylaDevice device : deviceList) {
            assertTrue("Device " + device.getDsn() + " has no properties",
                    device.getProperties().size() > 0);
        }
    }
}
