package com.aylanetworks.aylasdk;
/*
 * AylaSDK
 *
 * Copyright 2015 Ayla Networks, all rights reserved
 */

import com.aylanetworks.aylasdk.error.RequestFuture;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.After;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import static org.junit.Assert.*;
import org.junit.Test;
import org.robolectric.shadows.ShadowEnvironment;

import android.os.Environment;

@RunWith(RobolectricTestRunner.class)
public class RobApplicationTriggerTest {
    private AylaProperty _property;
    private AylaPropertyTrigger _propertyTrigger;
    private AylaPropertyTriggerApp _triggerApp;
    private final String _testEMailAddress = RobTestConstants.TEST_USERNAME;
    private final String _testPhoneNumber = RobTestConstants.TEST_PHONE_NUMBER;

    @Before
    public void setUp() throws Exception {
        RobTestConstants.signIn(RuntimeEnvironment.application.getApplicationContext());
        RobTestConstants.waitForDeviceManagerInitComplete();
        AylaDeviceManager dm = AylaNetworks.sharedInstance()
                .getSessionManager(RobTestConstants.TEST_SESSION_NAME).getDeviceManager();

        AylaDevice device = dm.deviceWithDSN(RobTestConstants.TEST_DEVICE_DSN);
        assertNotNull(device);
        _property = device.getProperty(RobTestConstants.TEST_DEVICE_PROPERTY);
        assertNotNull(_property);

        AylaLog.initAylaLog("test", AylaLog.LogLevel.Debug, AylaLog.LogLevel.Debug);

        //Create a Property Trigger First
        Random random = new Random();
        Integer anInt = random.nextInt();
        String propertyNickname = "TriggerName_" + anInt.toString();
        AylaPropertyTrigger aylaPropertyTrigger = new AylaPropertyTrigger();
        aylaPropertyTrigger.setPropertyNickname(propertyNickname);
        aylaPropertyTrigger.setTriggerType(AylaPropertyTrigger.TriggerType.Always.stringValue());

        //System.out.println("external storage before setting:" + ShadowEnvironment.getExternalStorageDirectory());
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);
        //ShadowEnvironment.setExternalStorageEmulated(new File("/tmp"), false);
        System.out.println("external storage path:" + ShadowEnvironment.getExternalStorageDirectory());
        RequestFuture<AylaPropertyTrigger> future = RequestFuture.newFuture();
        _property.createTrigger(aylaPropertyTrigger, future, future);
        try {
            int API_TIMEOUT_MS = 20000; //20000;
            _propertyTrigger = future.get(API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in propertyTrigger creation " + e);
        } catch (ExecutionException e) {
            fail("Error in propertyTrigger creation " + e);
        } catch (TimeoutException e) {
            fail("Error in propertyTrigger creation " + e);
        }
        assertNotNull(_propertyTrigger);
    }

    @Test
    public void testCreateEmailTriggerApp() {
        AylaPropertyTriggerApp triggerApp = new AylaPropertyTriggerApp();
        triggerApp.setEmailAddress(_testEMailAddress);
        AylaEmailTemplate template = new AylaEmailTemplate();
        template.setEmailBodyHtml("this is body");
        triggerApp.configureAsEmail(null, "test", null, template);

        RequestFuture<AylaPropertyTriggerApp> future = RequestFuture.newFuture();
        _propertyTrigger.createApp(triggerApp, future, future);
        try {
            int API_TIMEOUT_MS = 2000; //20000;
            _triggerApp = future.get(API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in AppTrigger creation " + e);
        } catch (ExecutionException e) {
            fail("Error in AppTrigger creation " + e);
        } catch (TimeoutException e) {
            fail("Error in AppTrigger creation " + e);
        }
        assertNotNull(_triggerApp);
    }

    @Test
    public void testUpdateEmailTriggerApp() {
        //First create a trigger
        AylaPropertyTriggerApp triggerApp = new AylaPropertyTriggerApp();
        triggerApp.setEmailAddress(_testEMailAddress);
        AylaEmailTemplate template = new AylaEmailTemplate();
        template.setEmailBodyHtml("this is body");
        triggerApp.configureAsEmail(null, "test", null, template);

        RequestFuture<AylaPropertyTriggerApp> future = RequestFuture.newFuture();
        _propertyTrigger.createApp(triggerApp, future, future);
        try {
            int API_TIMEOUT_MS = 20000;
            _triggerApp = future.get(API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in App Creation on Trigger " + e);
        } catch (ExecutionException e) {
            fail("Error in App Creation on Trigger " + e);
        } catch (TimeoutException e) {
            fail("Error in App Creation on Trigger " + e);
        }
        assertNotNull(_triggerApp);

        //Now update the trigger
        RequestFuture<AylaPropertyTriggerApp> updatedApp = RequestFuture.newFuture();
        String updatedBodyHTML = "this is updated body";
        template.setEmailBodyHtml(updatedBodyHTML);
        _triggerApp.setEmailTemplate(template);
        _propertyTrigger.updateApp(_triggerApp, updatedApp, updatedApp);
        try {
            int API_TIMEOUT_MS = 20000;
            _triggerApp = future.get(API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in App Update on Trigger " + e);
        } catch (ExecutionException e) {
            fail("Error in App Update on Trigger " + e);
        } catch (TimeoutException e) {
            fail("Error in App Update on Trigger " + e);
        }
        assertNotNull(_triggerApp);
        assertEquals(updatedBodyHTML, _triggerApp.getEmailTemplate().getEmailBodyHtml());
    }

    @Test
    public void testCreateSMSTriggerApp() {
        AylaPropertyTriggerApp triggerApp = new AylaPropertyTriggerApp();
        AylaContact contact = new AylaContact();
        contact.setPhoneCountryCode("1");
        contact.setPhoneNumber(_testPhoneNumber);
        triggerApp.configureAsSMS(contact, "SMS trigger Message");

        RequestFuture<AylaPropertyTriggerApp> future = RequestFuture.newFuture();
        _propertyTrigger.createApp(triggerApp, future, future);
        try {
            int API_TIMEOUT_MS = 90000;
            _triggerApp = future.get(API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in App Creation on Trigger " + e);
        } catch (ExecutionException e) {
            fail("Error in App Creation on Trigger " + e);
        } catch (TimeoutException e) {
            fail("Error in App Creation on Trigger " + e);
        }
        assertNotNull(_triggerApp);
    }

    @Test
    public void testUpdateSMSTriggerApp() {
        //First create a trigger
        AylaPropertyTriggerApp triggerApp = new AylaPropertyTriggerApp();
        AylaContact contact = new AylaContact();
        contact.setPhoneCountryCode("1");
        contact.setPhoneNumber(_testPhoneNumber);
        triggerApp.configureAsSMS(contact, "SMS trigger Message");

        RequestFuture<AylaPropertyTriggerApp> future = RequestFuture.newFuture();
        _propertyTrigger.createApp(triggerApp, future, future);
        try {
            int API_TIMEOUT_MS = 20000;
            _triggerApp = future.get(API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in App Creation on Trigger " + e);
        } catch (ExecutionException e) {
            fail("Error in App Creation on Trigger " + e);
        } catch (TimeoutException e) {
            fail("Error in App Creation on Trigger " + e);
        }
        assertNotNull(_triggerApp);

        //Now update the trigger
        RequestFuture<AylaPropertyTriggerApp> updatedApp = RequestFuture.newFuture();
        AylaContact contactUpdated = new AylaContact();
        contactUpdated.setPhoneCountryCode("1");
        contactUpdated.setPhoneNumber(_testPhoneNumber);
        String message = "Updated SMS Message";
        _triggerApp.configureAsSMS(contactUpdated, message);
        _propertyTrigger.updateApp(_triggerApp, updatedApp, updatedApp);
        try {
            int API_TIMEOUT_MS = 20000;
            _triggerApp = future.get(API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in App Update on Trigger " + e);
        } catch (ExecutionException e) {
            fail("Error in App Update on Trigger " + e);
        } catch (TimeoutException e) {
            fail("Error in App Update on Trigger " + e);
        }
        assertNotNull(_triggerApp);
        assertEquals(_triggerApp.getMessage(), message);
    }

    //fetch all Triggers
    @Test
    public void testFetchAllAppTriggers() {
        AylaPropertyTriggerApp triggerApp = new AylaPropertyTriggerApp();
        triggerApp.setEmailAddress(_testEMailAddress);
        AylaEmailTemplate template = new AylaEmailTemplate();
        template.setEmailBodyHtml("this is body");
        triggerApp.configureAsEmail(null, "test", null, template);

        RequestFuture<AylaPropertyTriggerApp> future = RequestFuture.newFuture();
        _propertyTrigger.createApp(triggerApp, future, future);
        try {
            int API_TIMEOUT_MS = 20000;
            _triggerApp = future.get(API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in AppTrigger creation " + e);
        } catch (ExecutionException e) {
            fail("Error in AppTrigger creation " + e);
        } catch (TimeoutException e) {
            fail("Error in AppTrigger creation " + e);
        }
        assertNotNull(_triggerApp);

        RequestFuture<AylaPropertyTriggerApp[]> futureFetch = RequestFuture.newFuture();
        _propertyTrigger.fetchApps(futureFetch, futureFetch);
        AylaPropertyTriggerApp[] triggers = null;
        try {
            triggers = futureFetch.get(RobTestConstants.API_TIMEOUT_MS, TimeUnit
                    .MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in Fetch AppTrigger " + e);
        } catch (ExecutionException e) {
            fail("Error in Fetch AppTrigger " + e);
        } catch (TimeoutException e) {
            fail("Error in Fetch AppTrigger " + e);
        }
        assertNotNull(triggers);
    }

    @After
    public void tearDown() throws Exception {
        if (_triggerApp != null) {
            RequestFuture<AylaAPIRequest.EmptyResponse> futureDelete = RequestFuture.newFuture();
            _propertyTrigger.deleteApp(_triggerApp, futureDelete, futureDelete);
            try {
                futureDelete.get(RobTestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                fail("Error in propertyTrigger delete " + e);
            } catch (ExecutionException e) {
                fail("Error in propertyTrigger delete " + e);
            } catch (TimeoutException e) {
                fail("Error in propertyTrigger delete " + e);
            }
        }
        if (_propertyTrigger != null) {
            RequestFuture<AylaAPIRequest.EmptyResponse> futureDelete = RequestFuture.newFuture();
            _property.deleteTrigger(_propertyTrigger, futureDelete, futureDelete);
            try {
                futureDelete.get(RobTestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                fail("Error in propertyTrigger delete " + e);
            } catch (ExecutionException e) {
                fail("Error in propertyTrigger delete " + e);
            } catch (TimeoutException e) {
                fail("Error in propertyTrigger delete " + e);
            }
        }
    }
}

