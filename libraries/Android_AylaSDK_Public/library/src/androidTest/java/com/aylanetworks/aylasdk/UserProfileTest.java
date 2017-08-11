package com.aylanetworks.aylasdk;/*
 * {PROJECT_NAME}
 *
 * Copyright 2015 Ayla Networks, all rights reserved
 */

import android.test.InstrumentationTestCase;

import com.aylanetworks.aylasdk.auth.AylaAuthorization;
import com.aylanetworks.aylasdk.error.RequestFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class UserProfileTest extends InstrumentationTestCase{
    private boolean _isSignedIn;

    @Override
    public void setUp() {
        _isSignedIn = TestConstants.signIn(getInstrumentation().getContext());
        assertTrue("Failed to sign-in", _isSignedIn);
    }

    public void testFetchUserProfile() {
        RequestFuture<AylaUser> future = RequestFuture.newFuture();
        AylaNetworks.sharedInstance().getSessionManager(TestConstants.TEST_SESSION_NAME)
                .fetchUserProfile(future, future);
        AylaUser user = null;
        try {
            user = future.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("fetchUserProfile interrupted: " + e);
        } catch (ExecutionException e) {
            fail("fetchUserProfile execution exception: " + e);
        } catch (TimeoutException e) {
            fail("fetchUserProfile timed out: " + e);
        }

        assertNotNull("No user profile was fetched", user);
        assertNotNull("No email address in user profile", user.getEmail());
    }

    public void testUpdateUserProfile() {
        AylaSessionManager sessionManager = AylaNetworks.sharedInstance()
                .getSessionManager(TestConstants.TEST_SESSION_NAME);

        // First fetch the user profile
        RequestFuture<AylaUser> future = RequestFuture.newFuture();
        sessionManager.fetchUserProfile(future, future);
        AylaUser user = null;
        try {
            user = future.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("fetchUserProfile interrupted: " + e);
        } catch (ExecutionException e) {
            fail("fetchUserProfile execution exception: " + e);
        } catch (TimeoutException e) {
            fail("fetchUserProfile timed out: " + e);
        }

        AylaLog.d("testUpdateUserProfile", "User first name: " + user.getFirstname());

        // Save the existing first name
        String originalFirstName = user.getFirstname();
        String testUpdateFirstName = originalFirstName + "-Updated";

        user.setFirstname(testUpdateFirstName);
        sessionManager.updateUserProfile(user, future, future);
        try {
            user = future.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("updateUserProfile interrupted: " + e);
        } catch (ExecutionException e) {
            fail("updateUserProfile execution exception: " + e);
        } catch (TimeoutException e) {
            fail("updateUserProfile timed out: " + e);
        }

        // Verify that the returned user has the correct first name
        assertEquals("Returned user did not reflect update", user.getFirstname(),
                testUpdateFirstName);
        AylaLog.d("testUpdateUserProfile", "New user first name: " + user.getFirstname());

        // Fetch the user again to make sure the server really did update
        sessionManager.fetchUserProfile(future, future);
        try {
            user = future.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("fetchUserProfile interrupted: " + e);
        } catch (ExecutionException e) {
            fail("fetchUserProfile execution exception: " + e);
        } catch (TimeoutException e) {
            fail("fetchUserProfile timed out: " + e);
        }

        assertEquals("Subsequent fetch of user profile did not reflect update", user.getFirstname(),
                testUpdateFirstName);
        AylaLog.d("testUpdateUserProfile", "Second fetch first name: " + user.getFirstname());

        // Put the user back to what it was originally
        user.setFirstname(originalFirstName);
        sessionManager.updateUserProfile(user, future, future);
        try {
            user = future.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("fetchUserProfile interrupted: " + e);
        } catch (ExecutionException e) {
            fail("fetchUserProfile execution exception: " + e);
        } catch (TimeoutException e) {
            fail("fetchUserProfile timed out: " + e);
        }
        AylaLog.d("testUpdateUserProfile", "Final update first name: " + user.getFirstname());

        assertEquals("Failed to put original username back", user.getFirstname(), originalFirstName);
    }
}
