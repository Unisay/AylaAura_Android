
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

public class ScheduleTest extends InstrumentationTestCase {
    private AylaDevice _device;

    @Override
    protected void setUp() throws Exception {
        TestConstants.signIn(getInstrumentation().getContext());
        TestConstants.waitForDeviceManagerInitComplete();
        AylaDeviceManager dm = AylaNetworks.sharedInstance()
                .getSessionManager(TestConstants.TEST_SESSION_NAME).getDeviceManager();

        _device = dm.deviceWithDSN(TestConstants.TEST_DEVICE_DSN);
        assertNotNull(_device);
    }

    //fetch all Schedules
    public void testFetchSchedules() {
        AylaSchedule[] aylaSchedules = null;
        RequestFuture<AylaSchedule[]> futureFetch = RequestFuture.newFuture();
        _device.fetchSchedules(futureFetch, futureFetch);
        try {
            int API_TIMEOUT_MS = 20000;
            aylaSchedules = futureFetch.get(API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in Schedule Fetch " + e);
        } catch (ExecutionException e) {
            fail("Error in Schedule Fetch " + e);
        } catch (TimeoutException e) {
            fail("Error in Schedule Fetch " + e);
        }
        assertNotNull(aylaSchedules);
        assert (aylaSchedules.length > 0);
    }

    public void testDisableAndEnableSchedule() {
        AylaSchedule[] aylaSchedules = null;
        RequestFuture<AylaSchedule[]> futureFetch = RequestFuture.newFuture();
        _device.fetchSchedules(futureFetch, futureFetch);
        try {
            int API_TIMEOUT_MS = 20000;
            aylaSchedules = futureFetch.get(API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in Schedule Fetch " + e);
        } catch (ExecutionException e) {
            fail("Error in Schedule Fetch " + e);
        } catch (TimeoutException e) {
            fail("Error in Schedule Fetch " + e);
        }
        assertNotNull(aylaSchedules);
        if (aylaSchedules.length > 0) {
            AylaSchedule aylaSchedule = null;
            RequestFuture<AylaSchedule> futureUpdate = RequestFuture.newFuture();
            //Disable first schedule
            _device.disableSchedule(aylaSchedules[0], futureUpdate, futureUpdate);
            try {
                int API_TIMEOUT_MS = 30000;
                aylaSchedule = futureUpdate.get(API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                fail("Error in Schedule Update " + e);
            } catch (ExecutionException e) {
                fail("Error in Schedule Update " + e);
            } catch (TimeoutException e) {
                fail("Error in Schedule Update " + e);
            }
            assertNotNull(aylaSchedule);
            assertEquals(aylaSchedule.isActive(), false);

            RequestFuture<AylaSchedule> futureUpdateEnable = RequestFuture.newFuture();
            //Enable first schedule
            _device.enableSchedule(aylaSchedules[0], futureUpdateEnable, futureUpdateEnable);
            try {
                int API_TIMEOUT_MS = 30000;
                aylaSchedule = futureUpdateEnable.get(API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                fail("Error in Schedule Update " + e);
            } catch (ExecutionException e) {
                fail("Error in Schedule Update " + e);
            } catch (TimeoutException e) {
                fail("Error in Schedule Update " + e);
            }
            assertNotNull(aylaSchedule);
            assertEquals(aylaSchedule.isActive(), true);
        }
    }
    public void testCreateSchedule(){
        //Create one new schedule and then delete it
        AylaSchedule aylaSchedule = new AylaSchedule();
        //Add some random number less than 10000 to name. For some reason if you create the same
        //schedule name repeatedly and delete them the server fails
        Random random = new Random();
        int randomNumber = random.nextInt(10000);
        aylaSchedule.setName("schedule_" + randomNumber);
        aylaSchedule.setDisplayName("test schedule");
        aylaSchedule.setDirection("input");
        aylaSchedule.setStartTimeEachDay("20:30:00");

        AylaSchedule createdSchedule =null;
        RequestFuture<AylaSchedule> future = RequestFuture.newFuture();
        _device.createSchedule(aylaSchedule, future, future);
        try {
            int API_TIMEOUT_MS = 20000;
            createdSchedule = future.get(API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in Schedule creation " + e);
        } catch (ExecutionException e) {
            fail("Error in Schedule creation " + e);
        } catch (TimeoutException e) {
            fail("Error in Schedule creation " + e);
        }
        assertNotNull(createdSchedule);

        //Now delete this schedule
        RequestFuture<AylaAPIRequest.EmptyResponse> futureDelete = RequestFuture.newFuture();
        _device.deleteSchedule(createdSchedule, futureDelete, futureDelete);
        try {
            futureDelete.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in Schedule delete " + e);
        } catch (ExecutionException e) {
            fail("Error in Schedule delete " + e);
        } catch (TimeoutException e) {
            fail("Error in Schedule delete " + e);
        }
    }
}


