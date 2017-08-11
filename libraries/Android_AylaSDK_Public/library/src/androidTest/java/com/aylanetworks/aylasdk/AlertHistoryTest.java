package com.aylanetworks.aylasdk;

import android.test.InstrumentationTestCase;

import com.aylanetworks.aylasdk.AylaAlertHistory.AlertFilter;
import com.aylanetworks.aylasdk.error.AuthError;
import com.aylanetworks.aylasdk.error.PreconditionError;
import com.aylanetworks.aylasdk.error.RequestFuture;
import com.aylanetworks.aylasdk.error.ServerError;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.aylanetworks.aylasdk.AylaAlertHistory.AlertHistoryFilters.AlertType;
import static com.aylanetworks.aylasdk.AylaAlertHistory.AlertHistoryFilters.PropertyName;
import static com.aylanetworks.aylasdk.AylaAlertHistory.FilterOperators.Not;
import static com.aylanetworks.aylasdk.AylaAlertHistory.FilterOperators.NotIn;

/**
 * AylaSDK
 * <p/>
 * Copyright 2016 Ayla Networks, all rights reserved
 */
public class AlertHistoryTest extends InstrumentationTestCase{
    AylaDevice _device;
    AylaDeviceManager _deviceManager;
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        boolean isSignedIn = TestConstants.signIn(getInstrumentation().getContext());
        assertTrue(isSignedIn);
        TestConstants.waitForDeviceManagerInitComplete();
        _deviceManager = AylaNetworks.sharedInstance().getSessionManager(TestConstants
                .TEST_SESSION_NAME).getDeviceManager();
        assertNotNull(_deviceManager);
        _device = _deviceManager.deviceWithDSN(TestConstants.TEST_DEVICE_DSN);
        assertNotNull(_device);
    }

    //Test with null DSN
    public void testWithNullDSN(){
        RequestFuture<AylaAlertHistory[]> requestFuture = RequestFuture.newFuture();
        AylaAlertHistory[] alertHistory;
        _deviceManager.fetchAlertHistory(null, requestFuture,
                requestFuture, false, 10, 1, null, null);
        try {
            alertHistory = requestFuture.get(30000, TimeUnit.MILLISECONDS);
            assertNull(alertHistory);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Exception "+e.getMessage());
        } catch (ExecutionException e) {
            try{
                PreconditionError error = (PreconditionError) e.getCause();
                assertEquals(PreconditionError.class, error.getClass());
            } catch (ClassCastException ex){
                fail("testWithNullDSN failed with invalid unexpected " +
                        "error code " + e);
            }
        } catch (TimeoutException e) {
            e.printStackTrace();
            fail("Exception "+e.getMessage());
        }
    }


    //Test with DSN not owned by the user. Expects 403 error code.
    public void testWithForbiddenDSN(){
        RequestFuture<AylaAlertHistory[]> requestFuture = RequestFuture.newFuture();
        AylaAlertHistory[] alertHistory;
        _deviceManager.fetchAlertHistory(TestConstants.TEST_DEVICE2_DSN, requestFuture,
                requestFuture, false, 10, 1, null, null);
        try {
            alertHistory = requestFuture.get(30000, TimeUnit.MILLISECONDS);
            assertNull(alertHistory);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Exception "+e.getMessage());
        } catch (ExecutionException e) {
            try{
                AuthError error = (AuthError) e.getCause();
               assertTrue(error.getMessage().contains("403"));
            } catch (ClassCastException ex){
                fail("testWithForbiddenDSN failed with invalid unexpected " +
                        "error code " + e);
            }
        } catch (TimeoutException e) {
            e.printStackTrace();
            fail("Exception "+e.getMessage());
        }
    }
    //Test invalid DSN. Expects 404 error code.
    public void testWithInvalidDSN(){
        RequestFuture<AylaAlertHistory[]> requestFuture = RequestFuture.newFuture();
        AylaAlertHistory[] alertHistory;
        _deviceManager.fetchAlertHistory("AC0000", requestFuture,
                requestFuture, false, 10, 1, null, null);
        try {
            alertHistory = requestFuture.get(30000, TimeUnit.MILLISECONDS);
            assertNull(alertHistory);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Exception "+e.getMessage());
        } catch (ExecutionException e) {
            try{
                ServerError error = (ServerError) e.getCause();
                assertTrue(error.getMessage().contains("404"));
            } catch (ClassCastException ex){
                fail("testWithForbiddenDSN failed with invalid unexpected " +
                        "error code " + e);
            }
        } catch (TimeoutException e) {
            e.printStackTrace();
            fail("Exception "+e.getMessage());
        }
    }

    //Test with no filter
    public void testWithNoFilters(){
        RequestFuture<AylaAlertHistory[]> requestFuture = RequestFuture.newFuture();
        AylaAlertHistory[] alertHistory;
        _deviceManager.fetchAlertHistory(TestConstants.TEST_DEVICE_DSN, requestFuture,
                requestFuture, false, 10, 1, null, null);
        try {
            alertHistory = requestFuture.get(30000, TimeUnit.MILLISECONDS);
            assertNotNull(alertHistory);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Exception "+e.getMessage());
        } catch (ExecutionException e) {
            e.printStackTrace();
            fail("Exception "+e.getMessage());
        } catch (TimeoutException e) {
            e.printStackTrace();
            fail("Exception "+e.getMessage());
        }
    }

    //Test with pagination
    public void testPaginated(){
        RequestFuture<AylaAlertHistory[]> requestFuture = RequestFuture.newFuture();
        AylaAlertHistory[] alertHistory;
        _deviceManager.fetchAlertHistory(TestConstants.TEST_DEVICE_DSN, requestFuture,
                requestFuture, true, 5, 1, null, null);
        try {
            alertHistory = requestFuture.get(30000, TimeUnit.MILLISECONDS);
            assertNotNull(alertHistory);
            assertTrue(alertHistory.length <= 5);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Exception "+e.getMessage());
        } catch (ExecutionException e) {
            e.printStackTrace();
            fail("Exception "+e.getMessage());
        } catch (TimeoutException e) {
            e.printStackTrace();
            fail("Exception "+e.getMessage());
        }
    }

    //Test with one filter, one operand
    public void testWithFilter(){
        RequestFuture<AylaAlertHistory[]> requestFuture = RequestFuture.newFuture();
        AylaAlertHistory[] alertHistory;
        AlertFilter filter = new AlertFilter();
        filter.add(PropertyName, Not, "Blue_LED");
        _deviceManager.fetchAlertHistory(TestConstants.TEST_DEVICE_DSN, requestFuture,
                requestFuture, false, 10, 1, filter, null);
        try {
            alertHistory = requestFuture.get(30000, TimeUnit.MILLISECONDS);
            assertNotNull(alertHistory);
            for(AylaAlertHistory alert: alertHistory){
                assertFalse(alert.getPropertyName().equals("Blue_LED"));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Exception "+e.getMessage());
        } catch (ExecutionException e) {
            e.printStackTrace();
            fail("Exception "+e.getMessage());
        } catch (TimeoutException e) {
            e.printStackTrace();
            fail("Exception "+e.getMessage());
        }
    }


    //Test with one filter, mulitple operands
    public void testWithSingleFilterMultipleOperands(){
        RequestFuture<AylaAlertHistory[]> requestFuture = RequestFuture.newFuture();
        AylaAlertHistory[] alertHistory;
        AlertFilter filter = new AlertFilter();
        filter.add(PropertyName, NotIn,  "Blue_LED,Blue_Button");
        _deviceManager.fetchAlertHistory(TestConstants.TEST_DEVICE_DSN, requestFuture,
                requestFuture, false, 10, 1, filter, null);
        try {
            alertHistory = requestFuture.get(30000, TimeUnit.MILLISECONDS);
            assertNotNull(alertHistory);
            for(AylaAlertHistory alert: alertHistory){
                assertFalse(alert.getPropertyName().equals("Blue_LED"));
                assertFalse(alert.getPropertyName().equals("Blue_button"));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Exception "+e.getMessage());
        } catch (ExecutionException e) {
            e.printStackTrace();
            fail("Exception "+e.getMessage());
        } catch (TimeoutException e) {
            e.printStackTrace();
            fail("Exception "+e.getMessage());
        }
    }

    //Test with multiple filters
    public void testWithMultipleFilters(){
        RequestFuture<AylaAlertHistory[]> requestFuture = RequestFuture.newFuture();
        AylaAlertHistory[] alertHistory;
        AlertFilter filter = new AlertFilter();
        filter.add(PropertyName, Not,  "Blue_LED");
        filter.add(AlertType, Not, "email");
        _deviceManager.fetchAlertHistory(TestConstants.TEST_DEVICE_DSN, requestFuture,
                requestFuture, false, 10, 1, filter, null);
        try {
            alertHistory = requestFuture.get(30000, TimeUnit.MILLISECONDS);
            assertNotNull(alertHistory);
            for(AylaAlertHistory alert: alertHistory){
                assertFalse(alert.getPropertyName().equals("Blue_LED"));
                assertFalse(alert.getAlertType().equals("email"));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Exception "+e.getMessage());
        } catch (ExecutionException e) {
            e.printStackTrace();
            fail("Exception "+e.getMessage());
        } catch (TimeoutException e) {
            e.printStackTrace();
            fail("Exception "+e.getMessage());
        }
    }

    //Test with results sorted oldest to most recent
    public void testWithSortDesc(){
        RequestFuture<AylaAlertHistory[]> requestFuture = RequestFuture.newFuture();
        AylaAlertHistory[] alertHistory;

        Map<String, String> map = new HashMap<>(2);
        map.put("order_by", "sent_at");
        map.put("order", "desc");
        _deviceManager.fetchAlertHistory(TestConstants.TEST_DEVICE_DSN, requestFuture,
                requestFuture, false, 10, 1, null, map);
        try {
            alertHistory = requestFuture.get(30000, TimeUnit.MILLISECONDS);
            assertNotNull(alertHistory);
            boolean success = checkDateSortingOrder(alertHistory, false);
            assertTrue(success);

        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Exception "+e.getMessage());
        } catch (ExecutionException e) {
            e.printStackTrace();
            fail("Exception "+e.getMessage());
        } catch (TimeoutException e) {
            e.printStackTrace();
            fail("Exception "+e.getMessage());
        }
    }

    //Test with results sorted most recent to oldest
    public void testWithSortAsc(){
        RequestFuture<AylaAlertHistory[]> requestFuture = RequestFuture.newFuture();
        AylaAlertHistory[] alertHistory;

        Map<String, String> map = new HashMap<>(2);
        map.put("order_by", "sent_at");
        map.put("order", "asc");
        _deviceManager.fetchAlertHistory(TestConstants.TEST_DEVICE_DSN, requestFuture,
                requestFuture, false, 10, 1, null, map);
        try {
            alertHistory = requestFuture.get(30000, TimeUnit.MILLISECONDS);
            assertNotNull(alertHistory);
            boolean success = checkDateSortingOrder(alertHistory, true);
            assertTrue(success);

        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Exception "+e.getMessage());
        } catch (ExecutionException e) {
            e.printStackTrace();
            fail("Exception "+e.getMessage());
        } catch (TimeoutException e) {
            e.printStackTrace();
            fail("Exception "+e.getMessage());
        }
    }

    private boolean checkDateSortingOrder(AylaAlertHistory[] alertList, boolean isAscending){
        boolean success = true;
        Date date1;
        Date date2;
        for(int i=1; i< alertList.length; i++){
            date1 = alertList[i-1].getSentAtDate();
            date2 = alertList[i].getSentAtDate();
            if(date1.equals(date2)){
                continue;
            }
            success = date1.after(date2) ;
            if(isAscending){
                //check for ascending order
                if(success){
                    return false;
                }
            } else{
                // check for descneding order
                if(!success){
                    return false;
                }
            }

        }
        return true;
    }
}
