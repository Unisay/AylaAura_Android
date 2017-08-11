package com.aylanetworks.aylasdk;

import android.test.InstrumentationTestCase;

import com.aylanetworks.aylasdk.auth.AylaAuthorization;
import com.aylanetworks.aylasdk.error.RequestFuture;
import com.aylanetworks.aylasdk.error.ServerError;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Android_AylaSDK
 * <p>
 * Copyright 2016 Ayla Networks, all rights reserved
 */
public class DeviceDatumTest extends InstrumentationTestCase {

    AylaDevice _device;
    private String testKey1 = "testkey1";
    private String invalidKey = "InvalidKey";
    private String testKeys[] = {testKey1};
    private String testValue = "test-value";
    private String testUpdateValue = "test-value-update";
    private int datumCount = 1; //replace with number of user datum in your test account
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        boolean isSignedIn = TestConstants.signIn(
                getInstrumentation().getContext());
        assertTrue("Failed to sign-in", isSignedIn);
        TestConstants.waitForDeviceManagerInitComplete();
        AylaDeviceManager deviceManager = AylaNetworks.sharedInstance()
                .getSessionManager(TestConstants.TEST_SESSION_NAME)
                .getDeviceManager();
        assertNotNull(deviceManager);
        _device = deviceManager.deviceWithDSN(TestConstants.TEST_DEVICE_DSN);
        assertNotNull(_device);
        AylaDatum datum = null;
        RequestFuture<AylaDatum> future = RequestFuture.newFuture();
        _device.createDatum(testKey1, testValue, future, future);
        try {
            datum = future.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in device datum creation "+e);
        } catch (ExecutionException e) {
            fail("Error in device datum creation " + e);
        } catch (TimeoutException e) {
            fail("Error in device datum creation " + e);
        }
        assertNotNull(datum);
    }

    public void testUpdateDatum(){
        AylaDatum updatedDatum = null;
        RequestFuture<AylaDatum> futureUpdate = RequestFuture.newFuture();
        _device.updateDatum(testKey1, testUpdateValue, futureUpdate, futureUpdate);
        try {
            updatedDatum = futureUpdate.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in device datum update " + e);
        } catch (ExecutionException e) {
            fail("Error in device datum update " + e);
        } catch (TimeoutException e) {
            fail("Error in device datum update "+e);
        }
        assertEquals("Returned datum key equals set datum key ", testKey1, updatedDatum.getKey());
        assertEquals("Returned datum value equals updated datum value ", testUpdateValue,
                updatedDatum.getValue());
    }

    //Update invalid datum
    public void testUpdateDatumFail(){
        AylaDatum updatedDatum = null;
        RequestFuture<AylaDatum> futureUpdate = RequestFuture.newFuture();
        _device.updateDatum(invalidKey, testUpdateValue, futureUpdate, futureUpdate);
        try {
            updatedDatum = futureUpdate.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in device datum update " + e);
        } catch (ExecutionException e) {
            try{
                ServerError error = (ServerError)e.getCause();
                assertEquals("Success. Server returned 404for bad key ", 404, error
                        .getServerResponseCode());
            } catch (ClassCastException ex){
                fail("Datum fetch with bad key returned unexpected error code " + e);
            }
        } catch (TimeoutException e) {
            fail("Error in device datum update "+e);
        }
        assertNull(updatedDatum);
    }

    public void testDeleteDatumFail(){
        RequestFuture<AylaAPIRequest.EmptyResponse> futureDelete = RequestFuture.newFuture();
        _device.deleteDatum(invalidKey, futureDelete, futureDelete);
        try {
            futureDelete.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in device datum delete " + e);
        } catch (ExecutionException e) {
            try{
                ServerError error = (ServerError)e.getCause();
                assertEquals("Success. Server returned 404for bad key ", 404,
                        error.getServerResponseCode());
            } catch (ClassCastException ex){
                fail("Datum fetch with bad key returned unexpected error code " + e);
            }
        } catch (TimeoutException e) {
            fail("Error in device datum delete "+e);
        }
    }

    public void testFetchDatumWithKey(){
        AylaDatum fetchedDatum = null;
        //fetch datum with key
        RequestFuture<AylaDatum> futureFetchdatum = RequestFuture.newFuture();
        _device.fetchAylaDatum(testKey1, futureFetchdatum, futureFetchdatum);
        try {
            fetchedDatum = futureFetchdatum.get(TestConstants.API_TIMEOUT_MS,
                    TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in device datum fetchWithKey " + e);
        } catch (ExecutionException e) {
            fail("Error in device datum fetchWithKey " + e);
        } catch (TimeoutException e) {
            fail("Error in device datum fetchWithKey " + e);
        }
        assertEquals("Returned datum key equals set datum key ", testKey1, fetchedDatum.getKey());
        assertEquals("Returned datum value equals set datum value ", testValue,
                fetchedDatum.getValue());

    }

    public void testFetchDatumWithKeyFail(){
        AylaDatum fetchedDatum = null;
        //fetch datum with key
        RequestFuture<AylaDatum> futureFetchdatum = RequestFuture.newFuture();
        _device.fetchAylaDatum(invalidKey, futureFetchdatum, futureFetchdatum);
        try {
            fetchedDatum = futureFetchdatum.get(TestConstants.API_TIMEOUT_MS,
                    TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in device datum fetchWithKey " + e);
        } catch (ExecutionException e) {
            try{
                ServerError error = (ServerError)e.getCause();
                assertEquals("Success. Server returned 404for bad key ", 404,
                        error.getServerResponseCode());
            } catch (ClassCastException ex){
                fail("Datum fetch with bad key returned unexpected error code " + e);
            }
        } catch (TimeoutException e) {
            fail("Error in device datum fetchWithKey " + e);
        }
        assertNull(fetchedDatum);
    }

    //fetch filtered datum array for multiple keys
    public void testFetchDatumList(){
        AylaDatum[] datumList = new AylaDatum[0];
        RequestFuture<AylaDatum[]> futureFetch = RequestFuture.newFuture();
        _device.fetchAylaDatums(testKeys, futureFetch, futureFetch);
        try {
            datumList = futureFetch.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in device datum creation "+e);
        } catch (ExecutionException e) {
            fail("Error in device datum creation " + e);
        } catch (TimeoutException e) {
            fail("Error in device datum creation " + e);
        }
        assertEquals("Fetched number of datum objects matches number of keys sent", testKeys
                .length, datumList.length);

    }

    //fetch filtered datum array for multiple keys. Should return 0 items
    public void testFetchDatumWithPattern0(){
        AylaDatum[] datumList = new AylaDatum[0];
        RequestFuture<AylaDatum[]> futureFetch = RequestFuture.newFuture();
        _device.fetchAylaDatums("%abcd%", futureFetch, futureFetch);
        try {
            datumList = futureFetch.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in device datum creation "+e);
        } catch (ExecutionException e) {
            fail("Error in device datum creation " + e);
        } catch (TimeoutException e) {
            fail("Error in device datum creation " + e);
        }
        assertNotNull(datumList);
        assertEquals(datumList.length, 0);

    }

    //fetch filtered datum array for multiple keys
    public void testFetchDatumWithPattern1(){
        AylaDatum[] datumList = new AylaDatum[0];
        RequestFuture<AylaDatum[]> futureFetch = RequestFuture.newFuture();
        _device.fetchAylaDatums("%est%", futureFetch, futureFetch);
        try {
            datumList = futureFetch.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in device datum creation "+e);
        } catch (ExecutionException e) {
            fail("Error in device datum creation " + e);
        } catch (TimeoutException e) {
            fail("Error in device datum creation " + e);
        }
        assertNotNull(datumList);
        assertTrue(datumList[0].getKey().contains("est"));

    }
    //fetch filtered datum array for multiple keys
    public void testFetchDatumWithPattern2(){
        AylaDatum[] datumList = new AylaDatum[0];
        RequestFuture<AylaDatum[]> futureFetch = RequestFuture.newFuture();
        _device.fetchAylaDatums("%1", futureFetch, futureFetch);
        try {
            datumList = futureFetch.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in device datum creation "+e);
        } catch (ExecutionException e) {
            fail("Error in device datum creation " + e);
        } catch (TimeoutException e) {
            fail("Error in device datum creation " + e);
        }
        assertNotNull(datumList);
        assertTrue(datumList[0].getKey().endsWith("1"));

    }
    //fetch all datum
    public void testFetchAllDatum() {
        AylaDatum[] datumList = new AylaDatum[0];
        RequestFuture<AylaDatum[]> futureFetch = RequestFuture.newFuture();
        _device.fetchAylaDatums(futureFetch, futureFetch);
        try {
            datumList = futureFetch.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in device datum creation " + e);
        } catch (ExecutionException e) {
            fail("Error in device datum creation " + e);
        } catch (TimeoutException e) {
            fail("Error in device datum creation " + e);
        }
        assertEquals("Fetched number of datum objects matches total number of datum ", datumCount,
                datumList.length);

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        RequestFuture futureDelete = RequestFuture.newFuture();
        _device.deleteDatum(testKey1, futureDelete, futureDelete);
        futureDelete.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
    }
}
