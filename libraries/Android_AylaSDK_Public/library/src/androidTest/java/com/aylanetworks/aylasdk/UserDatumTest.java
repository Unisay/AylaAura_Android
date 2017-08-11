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
public class UserDatumTest extends InstrumentationTestCase{
    private boolean _isSignedIn;
    private AylaSessionManager _sessionManager;
    private String testKey1 = "test-key-1";
    private String invalidKey = "%abcd";
    private String testKeys[] = {testKey1};
    private String testValue = "test-value";
    private String testUpdateValue = "test-value-update";
    private int datumCount = 18; //replace with number of user datum in your test account

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _isSignedIn = TestConstants.signIn(getInstrumentation().getContext());
        assertTrue("Failed to sign-in", _isSignedIn);
        _sessionManager = AylaNetworks.sharedInstance()
                .getSessionManager(TestConstants.TEST_SESSION_NAME);
        assertNotNull("Failed to get session manager", _sessionManager);
        AylaDatum datum = null;
        RequestFuture<AylaDatum> future = RequestFuture.newFuture();
        _sessionManager.createDatum(testKey1, testValue, future, future);
        try {
            datum = future.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in datum creation " + e);
        } catch (ExecutionException e) {
            fail("Error in datum creation " + e);
        } catch (TimeoutException e) {
            fail("Error in datum creation " + e);
        }
        assertNotNull(datum);
    }

    //fetch datum with key
    public void testFetchDatumWithKey(){
        AylaDatum datum = null;
        RequestFuture<AylaDatum> futureDatumWithKey = RequestFuture.newFuture();
        _sessionManager.fetchAylaDatum(testKey1, futureDatumWithKey, futureDatumWithKey);
        try {
            datum = futureDatumWithKey.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in datum fetch " + e);
        } catch (ExecutionException e) {
            fail("Error in datum fetch " + e);
        } catch (TimeoutException e) {
            fail("Error in datum fetch " + e);
        }
        assertEquals("Fetched datum value equals set datum value ", datum.getValue(), testValue);
    }

    //fetch datum with invalid key
    public void testFetchDatumFail(){
        AylaDatum datum = null;
        RequestFuture<AylaDatum> futureDatumWithKey = RequestFuture.newFuture();
        _sessionManager.fetchAylaDatum(invalidKey, futureDatumWithKey, futureDatumWithKey);
        try {
            datum = futureDatumWithKey.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in datum fetch " + e);
        } catch (ExecutionException e) {
            try{
                ServerError error = (ServerError)e.getCause();
                assertEquals("Success. Server returned 404 for invalid key ", 404,
                        error.getServerResponseCode());
            } catch (ClassCastException ex){
                fail("Datum fetch with bad key returned unexpected error code " + e);
            }

        } catch (TimeoutException e) {
            fail("Error in datum fetch " + e);
        }
        assertNull(datum);
    }

    //fetch datum with invalid key
    public void testDeleteFail(){
        AylaDatum datum = null;
        RequestFuture<AylaAPIRequest.EmptyResponse> futureDatumWithKey = RequestFuture.newFuture();
        _sessionManager.deleteDatum(invalidKey, futureDatumWithKey, futureDatumWithKey);
        try {
            futureDatumWithKey.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in datum fetch " + e);
        } catch (ExecutionException e) {
            try{
                ServerError error = (ServerError)e.getCause();
                assertEquals("Success. Server returned 404 for delete with invalid key ", 404,
                        error.getServerResponseCode());
            } catch (ClassCastException ex){
                fail("Datum delete with bad key returned unexpected error code " + e);
            }
        } catch (TimeoutException e) {
            fail("Error in datum fetch " + e);
        }
    }

    //fetch filtered datum array for multiple keys
    public void testFetchDatumList(){
        AylaDatum datumList[] = null;
        RequestFuture<AylaDatum[]> futureFetchList = RequestFuture.newFuture();
        _sessionManager.fetchAylaDatums(testKeys, futureFetchList, futureFetchList);
        try {
            datumList = futureFetchList.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in datum fetch " + e);
        } catch (ExecutionException e) {
            fail("Error in datum fetch " + e);
        } catch (TimeoutException e) {
            fail("Error in datum fetch " + e);
        }
        assertEquals("Fetched number of datum objects matches number of keys sent",
                testKeys.length, datumList.length);

    }

    //fetch filtered datum array for key pattern. No keys with this pattern exist
    public void testFetchDatumWithNoWildcard(){
        AylaDatum datumList[] = null;
        RequestFuture<AylaDatum[]> futureFetchList = RequestFuture.newFuture();
        _sessionManager.fetchAylaDatums("abcd", futureFetchList, futureFetchList);
        try {
            datumList = futureFetchList.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in datum fetch " + e);
        } catch (ExecutionException e) {
            fail("Error in datum fetch " + e);
        } catch (TimeoutException e) {
            fail("Error in datum fetch " + e);
        }
        assertNotNull(datumList);
        assertEquals(datumList.length, 0);
    }

    //fetch filtered datum array for key pattern. No keys with this pattern exist
    public void testFetchDatumWithPattern0(){
        AylaDatum datumList[] = null;
        RequestFuture<AylaDatum[]> futureFetchList = RequestFuture.newFuture();
        _sessionManager.fetchAylaDatums("%abcd%", futureFetchList, futureFetchList);
        try {
            datumList = futureFetchList.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in datum fetch " + e);
        } catch (ExecutionException e) {
            fail("Error in datum fetch " + e);
        } catch (TimeoutException e) {
            fail("Error in datum fetch " + e);
        }
        assertNotNull(datumList);
        assertEquals(datumList.length, 0);
    }


    //fetch filtered datum array for key pattern.
    public void testFetchDatumWithPattern1(){
        AylaDatum datumList[] = null;
        RequestFuture<AylaDatum[]> futureFetchList = RequestFuture.newFuture();
        _sessionManager.fetchAylaDatums("%est%", futureFetchList, futureFetchList);
        try {
            datumList = futureFetchList.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in datum fetch " + e);
        } catch (ExecutionException e) {
            fail("Error in datum fetch " + e);
        } catch (TimeoutException e) {
            fail("Error in datum fetch " + e);
        }
        assertNotNull(datumList);
        assertTrue(datumList[0].getKey().contains("est"));
    }

    //fetch filtered datum array for key pattern. Should return keys
    public void testFetchDatumWithPattern2(){
        AylaDatum datumList[] = null;
        RequestFuture<AylaDatum[]> futureFetchList = RequestFuture.newFuture();
        _sessionManager.fetchAylaDatums("%1", futureFetchList, futureFetchList);
        try {
            datumList = futureFetchList.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in datum fetch " + e);
        } catch (ExecutionException e) {
            fail("Error in datum fetch " + e);
        } catch (TimeoutException e) {
            fail("Error in datum fetch " + e);
        }
        assertNotNull(datumList);
        assertTrue(datumList[0].getKey().endsWith("1"));
    }

    //fetch all datum
    public void testFetchAllDatum(){
        AylaDatum datumList[] = null;
        RequestFuture<AylaDatum[]> futureFetchList = RequestFuture.newFuture();
        _sessionManager.fetchAylaDatums(futureFetchList, futureFetchList);
        try {
            datumList = futureFetchList.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in datum fetch " + e);
        } catch (ExecutionException e) {
            fail("Error in datum fetch " + e);
        } catch (TimeoutException e) {
            fail("Error in datum fetch " + e);
        }
        assertNotNull(datumList);
    }

    //test update datum
    public void testUpdateDatum(){
        AylaDatum updatedDatum = null;
        RequestFuture<AylaDatum> futureUpdate = RequestFuture.newFuture();
        _sessionManager.updateDatum(testKey1, testUpdateValue, futureUpdate, futureUpdate);
        try {
            updatedDatum = futureUpdate.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in datum update " + e);
        } catch (ExecutionException e) {
            fail("Error in datum update " + e);
        } catch (TimeoutException e) {
            fail("Error in datum update " + e);
        }
        assertEquals("Updated datum value matches changed value ", testUpdateValue,
                updatedDatum.getValue());
    }

    //test update datum. Update for invalid datum. Fail with 404
    public void testUpdateDatumFail(){
        AylaDatum updatedDatum = null;
        RequestFuture<AylaDatum> futureUpdate = RequestFuture.newFuture();
        _sessionManager.updateDatum(invalidKey, testUpdateValue, futureUpdate, futureUpdate);
        try {
            updatedDatum = futureUpdate.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in datum update " + e);
        } catch (ExecutionException e) {
            try{
                ServerError error = (ServerError)e.getCause();
                assertEquals("Success. Server returned 404for bad key ", 404,
                        error.getServerResponseCode());
            } catch (ClassCastException ex){
                fail("Datum fetch with bad key returned unexpected error code " + e);
            }
        } catch (TimeoutException e) {
            fail("Error in datum update " + e);
        }
        assertNull(updatedDatum);
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        RequestFuture futureDelete = RequestFuture.newFuture();
        _sessionManager.deleteDatum(testKey1, futureDelete, futureDelete);
        futureDelete.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
    }
}


