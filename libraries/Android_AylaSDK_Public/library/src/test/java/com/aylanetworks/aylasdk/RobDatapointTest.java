package com.aylanetworks.aylasdk;
/*
 * AylaSDK
 *
 * Copyright 2015 Ayla Networks, all rights reserved
 */

import android.test.InstrumentationTestCase;

import com.aylanetworks.aylasdk.error.RequestFuture;
import com.aylanetworks.aylasdk.util.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public class RobDatapointTest {
    private AylaProperty<Integer> _property;
    private AylaProperty<String> _stringProperty;
    private AylaProperty<Float> _decimalProperty;
    private AylaProperty<Integer> _intProperty;

    @Before
    public void setUp() {
        RobTestConstants.signIn(RuntimeEnvironment.application.getApplicationContext());
        RobTestConstants.waitForDeviceManagerInitComplete();
        AylaDeviceManager deviceManager = AylaNetworks.sharedInstance()
                .getSessionManager(RobTestConstants.TEST_SESSION_NAME)
                .getDeviceManager();
        AylaDevice device = deviceManager.deviceWithDSN(RobTestConstants.TEST_DEVICE_DSN);
        assertNotNull(device);

        RequestFuture<AylaProperty[]> futureProperty = RequestFuture.newFuture();
        device.fetchPropertiesCloud(null, futureProperty, futureProperty);
        try {
            AylaProperty[] properties = futureProperty.get();
            assertNotNull(properties);
        } catch (InterruptedException e) {
            fail("Failed to fetch all properties");
        } catch (ExecutionException e) {
            fail("Failed to fetch all properties");
        }

        _property = device.getProperty(RobTestConstants.TEST_DEVICE_PROPERTY);
        assertNotNull(_property);

        _stringProperty = device.getProperty(RobTestConstants.TEST_DEVICE_PROPERTY_STRING);
        assertNotNull(_stringProperty);

        _decimalProperty = device.getProperty(RobTestConstants.TEST_DEVICE_PROPERTY_DECIMAL);
        assertNotNull(_decimalProperty);

        _intProperty = device.getProperty(RobTestConstants.TEST_DEVICE_PROPERTY_INT);
        assertNotNull(_intProperty);

    }

    @Test
    public void testFetchDatapoints() {
        RequestFuture<AylaDatapoint[]> future = RequestFuture.newFuture();
        _property.fetchDatapoints(0, null, null, future, future);

        AylaDatapoint[] datapoints = null;
        try {
            datapoints = future.get();
        } catch (InterruptedException e) {
            fail("Interrupted Exception");
        } catch (ExecutionException e) {
            fail("Execution Exception");
        }

        assertNotNull(datapoints);
    }

    @Test
    public void testFetchDatapointsDateRange() {
        RequestFuture<AylaDatapoint[]> future = RequestFuture.newFuture();
        Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(2015, Calendar.DECEMBER, 20, 0, 0, 0);
        Date fromDate = cal.getTime();

        cal.set(2015, Calendar.DECEMBER, 22, 23, 59, 59);
        Date toDate = cal.getTime();

        _property.fetchDatapoints(0, fromDate, toDate, future, future);

        AylaDatapoint[] datapoints = null;
        try {
            datapoints = future.get();
        } catch (InterruptedException e) {
            fail("Interrupted Exception");
        } catch (ExecutionException e) {
            fail("Execution Exception");
        }

        assertNotNull(datapoints);
        for (AylaDatapoint dp : datapoints) {
            assertTrue(dp.getCreatedAt().after(fromDate) &&
                    dp.getCreatedAt().before(toDate));
        }
    }

    @Test
    public void testFetchDatapointsStartOnly() {
        RequestFuture<AylaDatapoint[]> future = RequestFuture.newFuture();
        Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(2015, Calendar.DECEMBER, 29, 0, 0, 0);
        Date fromDate = cal.getTime();

        _property.fetchDatapoints(0, fromDate, null, future, future);

        AylaDatapoint[] datapoints = null;
        try {
            datapoints = future.get();
        } catch (InterruptedException e) {
            fail("Interrupted Exception");
        } catch (ExecutionException e) {
            fail("Execution Exception");
        }

        assertNotNull(datapoints);
        for (AylaDatapoint dp : datapoints) {
            assertTrue(dp.getCreatedAt().after(fromDate));
        }
    }

    @Test
    public void testFetchDatapointsEndOnly() {
        RequestFuture<AylaDatapoint[]> future = RequestFuture.newFuture();
        Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(2015, Calendar.NOVEMBER, 29, 0, 0, 0);
        Date toDate = cal.getTime();

        _property.fetchDatapoints(0, null, toDate, future, future);

        AylaDatapoint[] datapoints = null;
        try {
            datapoints = future.get();
        } catch (InterruptedException e) {
            fail("Interrupted Exception");
        } catch (ExecutionException e) {
            fail("Execution Exception");
        }

        assertNotNull(datapoints);
        for (AylaDatapoint dp : datapoints) {
            assertTrue(dp.getCreatedAt().before(toDate));
        }
    }

    @Test
    public void testCreateDatapoint() {
        RequestFuture<AylaDatapoint<Integer>> future = RequestFuture.newFuture();

        Date beforeSending = new Date();
        Date ackAt = null;

        _property.createDatapoint(1, null, future, future);

        AylaDatapoint datapoint = null;
        try {
            datapoint = future.get();
        } catch (InterruptedException e) {
            fail(e.getMessage());
        } catch (ExecutionException e) {
            fail(e.getMessage());
        }

        assertNotNull(datapoint);
        if (_property.isAckEnabled()) {
            assertNotNull(datapoint.getAckedAt());
            ackAt = DateUtils.fromJsonString(datapoint.getAckedAt());
            assertNotNull(ackAt);
        }
        assertEquals(1, datapoint.getValue());
        Date afterSending = new Date();
        if (ackAt != null) {
            assertTrue(ackAt.after(beforeSending));
            assertTrue(ackAt.before(afterSending));
        }
    }

    @Test
    public void testCreateDatapointString() {
        RequestFuture<AylaDatapoint<String>> future = RequestFuture.newFuture();

        Date beforeSending = new Date();
        Date ackAt = null;
        String newVal = String.valueOf(new Random(1).nextInt());
        _stringProperty.createDatapoint(newVal, null, future, future);

        AylaDatapoint datapoint = null;
        try {
            datapoint = future.get();
        } catch (InterruptedException e) {
            fail(e.getMessage());
        } catch (ExecutionException e) {
            fail(e.getMessage());
        }

        assertNotNull(datapoint);
        if (_stringProperty.isAckEnabled()) {
            assertNotNull(datapoint.getAckedAt());
            ackAt = DateUtils.fromJsonString(datapoint.getAckedAt());
            assertNotNull(ackAt);
        }
        assertEquals(newVal, datapoint.getValue());
        Date afterSending = new Date();
        if (ackAt != null) {
            assertTrue(ackAt.after(beforeSending));
            assertTrue(ackAt.before(afterSending));
        }
    }

    @Test
    public void testCreateDatapointDecimal() {
        RequestFuture<AylaDatapoint<Float>> future = RequestFuture.newFuture();

        Date beforeSending = new Date();
        Date ackAt = null;
        Float newVal = _decimalProperty.getValue() + 1;
        _decimalProperty.createDatapoint(newVal, null, future, future);

        AylaDatapoint datapoint = null;
        try {
            datapoint = future.get();
        } catch (InterruptedException e) {
            fail(e.getMessage());
        } catch (ExecutionException e) {
            fail(e.getMessage());
        }

        assertNotNull(datapoint);
        if (_decimalProperty.isAckEnabled()) {
            assertNotNull(datapoint.getAckedAt());
            ackAt = DateUtils.fromJsonString(datapoint.getAckedAt());
            assertNotNull(ackAt);
        }
        assertEquals(newVal, datapoint.getValue());
        Date afterSending = new Date();
        if (ackAt != null) {
            assertTrue(ackAt.after(beforeSending));
            assertTrue(ackAt.before(afterSending));
        }
    }

    @Test
    public void testCreateDatapointInt() {
        RequestFuture<AylaDatapoint<Integer>> future = RequestFuture.newFuture();

        Date beforeSending = new Date();
        Date ackAt = null;
        int newValue =  _intProperty.getValue() + 1;
        _intProperty.createDatapoint(newValue, null, future, future);

        AylaDatapoint datapoint = null;
        try {
            datapoint = future.get();
        } catch (InterruptedException e) {
            fail(e.getMessage());
        } catch (ExecutionException e) {
            fail(e.getMessage());
        }

        assertNotNull(datapoint);
        if (_intProperty.isAckEnabled()) {
            assertNotNull(datapoint.getAckedAt());
            ackAt = DateUtils.fromJsonString(datapoint.getAckedAt());
            assertNotNull(ackAt);
        }
        assertEquals(newValue, datapoint.getValue());
        Date afterSending = new Date();
        if (ackAt != null) {
            assertTrue(ackAt.after(beforeSending));
            assertTrue(ackAt.before(afterSending));
        }
    }
}
