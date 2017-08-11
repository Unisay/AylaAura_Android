package com.aylanetworks.aylasdk;

import android.test.InstrumentationTestCase;

import com.aylanetworks.aylasdk.auth.AylaAuthorization;
import com.aylanetworks.aylasdk.error.AylaError;
import com.aylanetworks.aylasdk.error.PreconditionError;
import com.aylanetworks.aylasdk.error.RequestFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Android_Aura
 * <p/>
 * Copyright 2016 Ayla Networks, all rights reserved
 */
public class AccountDeleteTest extends InstrumentationTestCase{

    private AylaTestAccountConfig _user;
    private AylaTestConfig _testConfig;
    AylaSystemSettings _testSystemSettings;
    AylaAuthorization _aylaAuth;
    AylaSessionManager _sessionManager;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _user = new AylaTestAccountConfig(TestConstants.TEST_USERNAME3, TestConstants.TEST_PASSWORD, null, "session1");
        _testConfig = new AylaTestConfig();
        _testConfig.setApiTimeOut(10000);
        _testSystemSettings = new AylaSystemSettings(TestConstants.US_DEVICE_DEV_SYSTEM_SETTINGS);
        _testConfig.setTestSystemSettings(_testSystemSettings);
        _aylaAuth = _testConfig.signIn(_user,
                getInstrumentation().getContext());
        assertNotNull(_aylaAuth);
        _sessionManager = AylaNetworks.sharedInstance()
                .getSessionManager(_user.getTestSessionName());
        assertNotNull(_sessionManager);

    }

    public void testAccountDeleteSuccess(){
        RequestFuture<AylaAPIRequest.EmptyResponse> requestFuture = RequestFuture.newFuture();
        _sessionManager.deleteAccount(requestFuture, requestFuture);
        try {
            requestFuture.get(_testConfig.getApiTimeOut(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in testAccountDeleteSuccess " + e);
        } catch (ExecutionException e) {
            fail("Error in testAccountDeleteSuccess " + e);
        } catch (TimeoutException e) {
            fail("Error in testAccountDeleteSuccess " + e);
        }
        assertTrue("Delete success",  requestFuture.isDone());
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
