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

public class PropertyTriggerTest extends InstrumentationTestCase {
    private AylaProperty<Integer> _property;
    private AylaPropertyTrigger _propertyTrigger;

    @Override
    protected void setUp() throws Exception {
        TestConstants.signIn(getInstrumentation().getContext());
        TestConstants.waitForDeviceManagerInitComplete();
        AylaDeviceManager dm = AylaNetworks.sharedInstance()
                .getSessionManager(TestConstants.TEST_SESSION_NAME).getDeviceManager();

        AylaDevice device = dm.deviceWithDSN(TestConstants.TEST_DEVICE_DSN);
        assertNotNull(device);
        _property = device.getProperty(TestConstants.TEST_DEVICE_PROPERTY);
        assertNotNull(_property);
    }

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
            triggers = futureFetch.get(TestConstants.API_TIMEOUT_MS, TimeUnit
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

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if (_propertyTrigger != null) {
            RequestFuture<AylaAPIRequest.EmptyResponse> futureDelete = RequestFuture.newFuture();
            _property.deleteTrigger(_propertyTrigger, futureDelete, futureDelete);
            try {
                futureDelete.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
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

