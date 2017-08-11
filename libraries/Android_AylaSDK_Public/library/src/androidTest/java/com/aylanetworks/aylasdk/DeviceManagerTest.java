package com.aylanetworks.aylasdk;
/*
 * AylaSDK
 *
 * Copyright 2015 Ayla Networks, all rights reserved
 */

import android.test.InstrumentationTestCase;

import java.util.List;

public class DeviceManagerTest
        extends InstrumentationTestCase {

    public void testInitComplete() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Thread interrupted");
        }

        TestConstants.signIn(getInstrumentation().getContext());
        TestConstants.waitForDeviceManagerInitComplete();

        AylaDeviceManager deviceManager = AylaNetworks.sharedInstance()
                .getSessionManager(TestConstants.TEST_SESSION_NAME).getDeviceManager();

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
