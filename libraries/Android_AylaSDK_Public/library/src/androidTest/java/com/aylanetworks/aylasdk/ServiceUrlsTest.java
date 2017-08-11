package com.aylanetworks.aylasdk;

import android.test.InstrumentationTestCase;
import com.aylanetworks.aylasdk.util.ServiceUrls;

/*
 * AylaSDK
 *
 * Copyright 2015 Ayla Networks, all rights reserved
 */

public class ServiceUrlsTest extends InstrumentationTestCase {
    private AylaSystemSettings _systemSettings;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        // Create
        _systemSettings = new AylaSystemSettings(TestConstants.US_DEVICE_DEV_SYSTEM_SETTINGS);
        _systemSettings.context = getInstrumentation().getContext();
        AylaNetworks.initialize(_systemSettings);
    }

    public void testLogDevelopChinaServiceUrl() {
        String expected = "https://log.ayla.com.cn/";
        String actual = ServiceUrls.getBaseServiceURL(ServiceUrls.CloudService.Log,
                AylaSystemSettings.ServiceType.Development,
                AylaSystemSettings.ServiceLocation.China);
        assertEquals("China Development Log service is not valid", expected, actual);
    }

    public void testLogDevelopUsServiceUrl() {
        String expected = "https://log.aylanetworks.com/";
        String actual = ServiceUrls.getBaseServiceURL(ServiceUrls.CloudService.Log,
                AylaSystemSettings.ServiceType.Development,
                AylaSystemSettings.ServiceLocation.USA);
        assertEquals("US Development Log service is not valid", expected, actual);
    }

    public void testUserFieldUsServiceUrl() {
        String expected = "https://user-field.aylanetworks.com/";
        String actual = ServiceUrls.getBaseServiceURL(ServiceUrls.CloudService.User,
                AylaSystemSettings.ServiceType.Field,
                AylaSystemSettings.ServiceLocation.USA);
        assertEquals("User Field US service is not valid", expected, actual);
    }

    public void testUserFieldChinaServiceUrl() {
        String expected = "https://user-field.ayla.com.cn/";
        String actual = ServiceUrls.getBaseServiceURL(ServiceUrls.CloudService.User,
                AylaSystemSettings.ServiceType.Field,
                AylaSystemSettings.ServiceLocation.China);
        assertEquals("User Field China service is not valid", expected, actual);
    }

    public void testUserFieldEuropeServiceUrl() {
        String expected = "https://user-field-eu.aylanetworks.com/";
        String actual = ServiceUrls.getBaseServiceURL(ServiceUrls.CloudService.User,
                AylaSystemSettings.ServiceType.Field,
                AylaSystemSettings.ServiceLocation.Europe);
        assertEquals("User Field Europe service is not valid", expected, actual);
    }
}

