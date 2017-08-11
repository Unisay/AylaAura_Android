package com.aylanetworks.aylasdk;

import android.test.InstrumentationTestCase;

import com.aylanetworks.aylasdk.util.ServiceUrls;

/*
 * AylaSDK
 *
 * Copyright 2015 Ayla Networks, all rights reserved
 */

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public class RobServiceUrlsTest {
    private AylaSystemSettings _systemSettings;

    @Before
    public void setUp() throws Exception {
        // Create
        _systemSettings = new AylaSystemSettings(RobTestConstants.US_DEVICE_DEV_SYSTEM_SETTINGS);
        _systemSettings.context = RuntimeEnvironment.application.getApplicationContext();
        AylaNetworks.initialize(_systemSettings);
    }

    @Test
    public void testLogDevelopChinaServiceUrl() {
        String expected = "https://log.ayla.com.cn/";
        String actual = ServiceUrls.getBaseServiceURL(ServiceUrls.CloudService.Log,
                AylaSystemSettings.ServiceType.Development,
                AylaSystemSettings.ServiceLocation.China);
        assertEquals("China Development Log service is not valid", expected, actual);
    }

    @Test
    public void testLogDevelopUsServiceUrl() {
        String expected = "https://log.aylanetworks.com/";
        String actual = ServiceUrls.getBaseServiceURL(ServiceUrls.CloudService.Log,
                AylaSystemSettings.ServiceType.Development,
                AylaSystemSettings.ServiceLocation.USA);
        assertEquals("US Development Log service is not valid", expected, actual);
    }

    @Test
    public void testUserFieldUsServiceUrl() {
        String expected = "https://user.aylanetworks.com/";
        String actual = ServiceUrls.getBaseServiceURL(ServiceUrls.CloudService.User,
                AylaSystemSettings.ServiceType.Field,
                AylaSystemSettings.ServiceLocation.USA);
        assertEquals("User Field US service is not valid", expected, actual);
    }

    @Test
    public void testUserFieldChinaServiceUrl() {
        String expected = "https://user.ayla.com.cn/";
        String actual = ServiceUrls.getBaseServiceURL(ServiceUrls.CloudService.User,
                AylaSystemSettings.ServiceType.Field,
                AylaSystemSettings.ServiceLocation.China);
        assertEquals("User Field China service is not valid", expected, actual);
    }

    @Test
    public void testUserFieldEuropeServiceUrl() {
        String expected = "https://user-eu.aylanetworks.com/";
        String actual = ServiceUrls.getBaseServiceURL(ServiceUrls.CloudService.User,
                AylaSystemSettings.ServiceType.Field,
                AylaSystemSettings.ServiceLocation.Europe);
        assertEquals("User Field Europe service is not valid", expected, actual);
    }
}

