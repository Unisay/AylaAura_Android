package com.aylanetworks.aylasdk;
/*
 * AylaSDK
 *
 * Copyright 2015 Ayla Networks, all rights reserved
 */

import android.test.InstrumentationTestCase;

import com.aylanetworks.aylasdk.error.RequestFuture;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public class RobPropertyTriggerTest {
    private AylaProperty<Integer> _property;
    private AylaPropertyTrigger _propertyTrigger;

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
    }

    @Test
    public void testCreatePropertyTrigger() {
        Random random = new Random();
        Integer anInt = random.nextInt();
        String propertyNickname = "TriggerName_" + anInt.toString();

        AylaPropertyTrigger aylaPropertyTrigger = new AylaPropertyTrigger();
        aylaPropertyTrigger.setPropertyNickname(propertyNickname);
        aylaPropertyTrigger.setTriggerType(AylaPropertyTrigger.TriggerType.Always.stringValue());

        RequestFuture<AylaPropertyTrigger> future = RequestFuture.newFuture();
        _property.createTrigger(aylaPropertyTrigger, future, future);
        try {
            int API_TIMEOUT_MS = 20000;
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
    public void testUpdatePropertyTrigger() {
        Random random = new Random();
        Integer anInt = random.nextInt();
        String propertyNickname = "TriggerName_" + anInt.toString();

        AylaPropertyTrigger aylaPropertyTrigger = new AylaPropertyTrigger();
        aylaPropertyTrigger.setPropertyNickname(propertyNickname);
        aylaPropertyTrigger.setTriggerType(AylaPropertyTrigger.TriggerType.Always.stringValue());

        RequestFuture<AylaPropertyTrigger> future = RequestFuture.newFuture();
        _property.createTrigger(aylaPropertyTrigger, future, future);
        try {
            int API_TIMEOUT_MS = 20000;
            _propertyTrigger = future.get(API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in propertyTrigger creation " + e);
        } catch (ExecutionException e) {
            fail("Error in propertyTrigger creation " + e);
        } catch (TimeoutException e) {
            fail("Error in propertyTrigger creation " + e);
        }
        assertNotNull(_propertyTrigger);

        _propertyTrigger.setTriggerType("compare_absolute");
        _propertyTrigger.setCompareType(">=");
        _propertyTrigger.setValue("1");

        anInt = random.nextInt();
        String updatedTriggerName = "TriggerName_" + anInt.toString();
        _propertyTrigger.setPropertyNickname(updatedTriggerName);
        RequestFuture<AylaPropertyTrigger> futureUpdate = RequestFuture.newFuture();
        _property.updateTrigger(_propertyTrigger, futureUpdate, futureUpdate);

        try {
            int API_TIMEOUT_MS = 20000;
            _propertyTrigger = futureUpdate.get(API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in propertyTrigger update " + e);
        } catch (ExecutionException e) {
            fail("Error in propertyTrigger update " + e);
        } catch (TimeoutException e) {
            fail("Error in propertyTrigger update " + e);
        }
        assertNotNull(_propertyTrigger);
        assertEquals(_propertyTrigger.getPropertyNickname(), updatedTriggerName);
    }

    //fetch all Triggers
    @Test
    public void testFetchAllTriggers() {
        //Create one trigger first to make sure there is at least one trigger for fetch
        Random random = new Random();
        Integer anInt = random.nextInt();
        String propertyNickname = "TriggerName_" + anInt.toString();

        AylaPropertyTrigger aylaPropertyTrigger = new AylaPropertyTrigger();
        aylaPropertyTrigger.setPropertyNickname(propertyNickname);
        aylaPropertyTrigger.setTriggerType(AylaPropertyTrigger.TriggerType.Always.stringValue());

        RequestFuture<AylaPropertyTrigger> future = RequestFuture.newFuture();
        _property.createTrigger(aylaPropertyTrigger, future, future);
        try {
            int API_TIMEOUT_MS = 20000;
            _propertyTrigger = future.get(API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in propertyTrigger creation " + e);
        } catch (ExecutionException e) {
            fail("Error in propertyTrigger creation " + e);
        } catch (TimeoutException e) {
            fail("Error in propertyTrigger creation " + e);
        }
        assertNotNull(_propertyTrigger);

        RequestFuture<AylaPropertyTrigger[]> futureFetch = RequestFuture.newFuture();
        _property.fetchTriggers(futureFetch, futureFetch);
        AylaPropertyTrigger[] triggers = null;
        try {
            triggers = futureFetch.get(RobTestConstants.API_TIMEOUT_MS, TimeUnit
                    .MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in Fetch Triggers " + e);
        } catch (ExecutionException e) {
            fail("Error in Fetch Triggers " + e);
        } catch (TimeoutException e) {
            fail("Error in Fetch Triggers " + e);
        }
        assertNotNull(triggers);
    }

    @After
    public void tearDown() throws Exception {
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

