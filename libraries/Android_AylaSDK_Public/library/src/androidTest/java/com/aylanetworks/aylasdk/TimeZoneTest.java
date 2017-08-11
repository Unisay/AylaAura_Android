package com.aylanetworks.aylasdk;

import android.test.InstrumentationTestCase;
import com.aylanetworks.aylasdk.auth.AylaAuthorization;
import com.aylanetworks.aylasdk.error.RequestFuture;
import com.aylanetworks.aylasdk.error.ServerError;

import org.joda.time.DateTimeZone;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Android_AylaSDK
 * <p>
 * Copyright 2016 Ayla Networks, all rights reserved
 */
public class TimeZoneTest extends InstrumentationTestCase{
    private boolean _isSignedIn;
    AylaDevice _device;
    String PacificTz = "America/Los_Angeles";
    String EasternTz = "America/New_York";
    String invalidTimeZoneId = "America/xxx";
    String southernHemisphereTz = "Australia/Sydney";
    String noDstTimeZone = "America/Phoenix";
    String europeTimeZone = "Europe/London";
    String chinaTimeZone = "Asia/Shanghai";
    String offsetWithMinutesTimeZone = "Pacific/Chatham";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _isSignedIn = TestConstants.signIn(getInstrumentation().getContext());
        assertTrue("Failed to sign-in", _isSignedIn);
        TestConstants.waitForDeviceManagerInitComplete();
        AylaDeviceManager deviceManager = AylaNetworks.sharedInstance()
                .getSessionManager(TestConstants.TEST_SESSION_NAME).getDeviceManager();
        assertNotNull(deviceManager);
        _device = deviceManager.deviceWithDSN(TestConstants.TEST_DEVICE_DSN);
        assertNotNull(_device);
    }

    public void testUpdateTimeZone(){
        AylaTimeZone timeZone = null;
        RequestFuture<AylaTimeZone> futureUpdate = RequestFuture.newFuture();
        _device.updateTimeZone(EasternTz, futureUpdate, futureUpdate);
        try {
            timeZone = futureUpdate.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Update timezone failed "+e.toString());
        } catch (ExecutionException e) {
            fail("Update timezone failed "+e.toString());
        } catch (TimeoutException e) {
            fail("Update timezone failed " + e.toString());
        }
        assertNotNull(timeZone);
        assertEquals("Updated timeZone identifier equals ", EasternTz, timeZone.tzId);
    }

    public void testUpdateTimeZoneFail(){
        AylaTimeZone timeZone = null;
        RequestFuture<AylaTimeZone> futureUpdate = RequestFuture.newFuture();
        _device.updateTimeZone(invalidTimeZoneId, futureUpdate, futureUpdate);
        try {
            timeZone = futureUpdate.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Update timezone failed "+e.toString());
        } catch (ExecutionException e) {
            try{
                ServerError error = (ServerError)e.getCause();
                assertEquals("Success. Error code 422 for invalid timezone ", 422,
                        error.getServerResponseCode());
            } catch (ClassCastException ex){
                fail("Create timezone test with invalid timezone identifier returned unexpected " +
                        "error code " + e);
            }
        } catch (TimeoutException e) {
            fail("Update timezone failed " + e.toString());
        }
        assertNull(timeZone);
    }

    public void testfetchTimeZone(){
        AylaTimeZone timeZone = null;
        RequestFuture<AylaTimeZone> futureFetch = RequestFuture.newFuture();
        _device.fetchTimeZone(futureFetch, futureFetch);
        try {
            timeZone = futureFetch.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Update timezone failed "+e.toString());
        } catch (ExecutionException e) {
            fail("Update timezone failed " + e.toString());
        } catch (TimeoutException e) {
            fail("Update timezone failed " + e.toString());
        }
        assertEquals("Updated timeZone identifier equals ", PacificTz, timeZone.tzId);
    }

    public void testUpdateUSTz(){
        AylaTimeZone timeZone = null;
        RequestFuture<AylaTimeZone> futureUpdate = RequestFuture.newFuture();
        _device.updateTimeZone(PacificTz, futureUpdate, futureUpdate);
        try {
            timeZone = futureUpdate.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            assertNotNull(timeZone);
            String dstChangeDate = timeZone.getDstNextChangeDate();
            TimeZone tz = TimeZone.getTimeZone(PacificTz);
            String nextDstComputed = null;
            if(tz.useDaylightTime()){
                Calendar cal = Calendar.getInstance(tz);
                DateTimeZone dateTimeZone = DateTimeZone.forID(PacificTz);
                cal.setTimeInMillis(dateTimeZone.nextTransition(cal.getTimeInMillis()));
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm Z");
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                nextDstComputed = dateFormat.format(cal.getTime());
            }
            assertEquals("DST change date from Ayla SDK same as computed value", dstChangeDate, nextDstComputed);
        } catch (InterruptedException e) {
            fail("Update timezone failed "+e.toString());
        } catch (ExecutionException e) {
            fail("Update timezone failed "+e.toString());
        } catch (TimeoutException e) {
            fail("Update timezone failed " + e.toString());
        }
    }

    public void testUpdateSouthernHemisphereTz(){
        AylaTimeZone timeZone = null;
        RequestFuture<AylaTimeZone> futureUpdate = RequestFuture.newFuture();
        _device.updateTimeZone(southernHemisphereTz, futureUpdate, futureUpdate);
        try {
            timeZone = futureUpdate.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            assertNotNull(timeZone);
            String dstChangeDate = timeZone.getDstNextChangeDate();
            TimeZone tz = TimeZone.getTimeZone(europeTimeZone);
            String nextDstComputed = null;
            if(tz.useDaylightTime()){
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(southernHemisphereTz));
                DateTimeZone dateTimeZone = DateTimeZone.forID(southernHemisphereTz);
                cal.setTimeInMillis(dateTimeZone.nextTransition(cal.getTimeInMillis()));
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm Z");
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                nextDstComputed = dateFormat.format(cal.getTime());
            }

            assertEquals("DST change date from Ayla SDK same as computed value", dstChangeDate,
                    nextDstComputed);
        } catch (InterruptedException e) {
            fail("Update timezone failed "+e.toString());
        } catch (ExecutionException e) {
            fail("Update timezone failed "+e.toString());
        } catch (TimeoutException e) {
            fail("Update timezone failed " + e.toString());
        }

    }

    public void testUpdateNoDSTTz(){
        AylaTimeZone timeZone = null;
        RequestFuture<AylaTimeZone> futureUpdate = RequestFuture.newFuture();
        _device.updateTimeZone(noDstTimeZone, futureUpdate, futureUpdate);
        try {
            timeZone = futureUpdate.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            assertNotNull(timeZone);
            String dstChangeDate = timeZone.getDstNextChangeDate();
            assertEquals("DST change date from Ayla SDK same as computed value", null,
                    dstChangeDate);
        } catch (InterruptedException e) {
            fail("Update timezone failed "+e.toString());
        } catch (ExecutionException e) {
            fail("Update timezone failed "+e.toString());
        } catch (TimeoutException e) {
            fail("Update timezone failed " + e.toString());
        }
    }

    public void testUpdateEuropeTz(){
        AylaTimeZone timeZone = null;
        RequestFuture<AylaTimeZone> futureUpdate = RequestFuture.newFuture();
        _device.updateTimeZone(europeTimeZone, futureUpdate, futureUpdate);
        try {
            timeZone = futureUpdate.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            assertNotNull(timeZone);
            String dstChangeDate = timeZone.getDstNextChangeDate();
            TimeZone tz = TimeZone.getTimeZone(europeTimeZone);
            String nextDstComputed = null;
            if(tz.useDaylightTime()){
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(europeTimeZone));
                DateTimeZone dateTimeZone = DateTimeZone.forID(europeTimeZone);
                cal.setTimeInMillis(dateTimeZone.nextTransition(cal.getTimeInMillis()));
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm Z");
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                nextDstComputed = dateFormat.format(cal.getTime());
            }

            assertEquals("DST change date from Ayla SDK same as computed value", dstChangeDate,
                    nextDstComputed);
        } catch (InterruptedException e) {
            fail("Update timezone failed "+e.toString());
        } catch (ExecutionException e) {
            fail("Update timezone failed "+e.toString());
        } catch (TimeoutException e) {
            fail("Update timezone failed " + e.toString());
        }

    }

    public void testUpdateChinaTz(){
        AylaTimeZone timeZone = null;
        RequestFuture<AylaTimeZone> futureUpdate = RequestFuture.newFuture();
        _device.updateTimeZone(chinaTimeZone, futureUpdate, futureUpdate);
        try {
            timeZone = futureUpdate.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            assertNotNull(timeZone);
            String dstChangeDate = timeZone.getDstNextChangeDate();
            TimeZone tz = TimeZone.getTimeZone(chinaTimeZone);
            String nextDstComputed = null;
            if( tz.useDaylightTime()){
                Calendar cal = Calendar.getInstance(tz);
                DateTimeZone dateTimeZone = DateTimeZone.forID(chinaTimeZone);
                cal.setTimeInMillis(dateTimeZone.nextTransition(cal.getTimeInMillis()));
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm Z");
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                nextDstComputed = dateFormat.format(cal.getTime());
            }

            assertEquals("DST change date from Ayla SDK same as computed value", dstChangeDate,
                    nextDstComputed);
        } catch (InterruptedException e) {
            fail("Update timezone failed "+e.toString());
        } catch (ExecutionException e) {
            fail("Update timezone failed "+e.toString());
        } catch (TimeoutException e) {
            fail("Update timezone failed " + e.toString());
        }

    }

    public void testUpdateOffsetWithMinutesTz(){
        AylaTimeZone timeZone = null;
        RequestFuture<AylaTimeZone> futureUpdate = RequestFuture.newFuture();
        _device.updateTimeZone(offsetWithMinutesTimeZone, futureUpdate, futureUpdate);
        try {
            timeZone = futureUpdate.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            assertNotNull(timeZone);
            String dstChangeDate = timeZone.getDstNextChangeDate();
            TimeZone tz = TimeZone.getTimeZone(offsetWithMinutesTimeZone);
            String nextDstComputed = null;
            if( tz.useDaylightTime()){
                Calendar cal = Calendar.getInstance(tz);
                DateTimeZone dateTimeZone = DateTimeZone.forID(offsetWithMinutesTimeZone);
                cal.setTimeInMillis(dateTimeZone.nextTransition(cal.getTimeInMillis()));
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm Z");
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                nextDstComputed = dateFormat.format(cal.getTime());
            }

            assertEquals("DST change date from Ayla SDK same as computed value", dstChangeDate,
                    nextDstComputed);
        } catch (InterruptedException e) {
            fail("Update timezone failed "+e.toString());
        } catch (ExecutionException e) {
            fail("Update timezone failed "+e.toString());
        } catch (TimeoutException e) {
            fail("Update timezone failed " + e.toString());
        }

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        RequestFuture<AylaTimeZone> futureUpdate = RequestFuture.newFuture();
        _device.updateTimeZone(PacificTz, futureUpdate, futureUpdate);
        futureUpdate.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
    }
}
