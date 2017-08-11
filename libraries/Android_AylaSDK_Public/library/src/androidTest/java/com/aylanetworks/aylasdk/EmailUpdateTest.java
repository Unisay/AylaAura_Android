package com.aylanetworks.aylasdk;

import android.test.InstrumentationTestCase;

import com.aylanetworks.aylasdk.auth.AylaAuthorization;
import com.aylanetworks.aylasdk.error.RequestFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Android_AylaSDK
 * <p>
 * Copyright 2016 Ayla Networks, all rights reserved
 */
public class EmailUpdateTest extends InstrumentationTestCase {

    private boolean isSignedIn;

    @Override
    public void setUp() {
        isSignedIn = TestConstants.signIn(getInstrumentation().getContext());
        assertTrue("Failed to sign-in", isSignedIn);
    }

    public void testUpdateUserEmailAddress() {
        AylaSessionManager sessionManager = AylaNetworks.sharedInstance()
                .getSessionManager(TestConstants.TEST_SESSION_NAME);
        assertNotNull(sessionManager);

        RequestFuture<AylaAPIRequest.EmptyResponse> future = RequestFuture.newFuture();
        sessionManager.updateUserEmailAddress(TestConstants.TEST_USERNAME_NEW, future, future);
        try {
            future.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("updateUserEmailAddress interrupted: " + e);
        } catch (ExecutionException e) {
            fail("updateUserEmailAddress execution exception: " + e);
        } catch (TimeoutException e) {
            fail("updateUserEmailAddress timeout: " + e);
        }
        assertTrue("Update Email success", future.isDone());
    }
}
