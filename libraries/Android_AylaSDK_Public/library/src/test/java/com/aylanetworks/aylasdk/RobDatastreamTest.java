package com.aylanetworks.aylasdk;

import android.text.TextUtils;

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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public class RobDatastreamTest {
    private RobTestAccountConfig _user;
    private RobTestConfig _testConfig;
    AylaSystemSettings _testSystemSettings;
    private AylaAuthorization _aylaAuthorization;
    private AylaSessionManager _sessionManager;
    private AylaDSSubscription _aylaSubscription;
    private AylaDSManager _dssManager;
    private AylaDeviceManager _deviceManager;

    //Have two sets of tests. _testSystemSettings.allowDSS = true and false
    //To test subscription CRUD. Set allowDSS to false.

    @Before
    public void setUp() throws Exception {
        _testConfig = new RobTestConfig();
        _testConfig.setApiTimeOut(10000);
        _testSystemSettings = new AylaSystemSettings();
        _testSystemSettings.appId = "AgileLinkProd-id";
        _testSystemSettings.appSecret = "AgileLinkProd-8249425";
       /* _testSystemSettings.appId = "aMCA-id";
        _testSystemSettings.appSecret = "aMCA-9097620";*/
        _testSystemSettings.serviceLocation = AylaSystemSettings.ServiceLocation.USA;
        _testSystemSettings.serviceType = AylaSystemSettings.ServiceType.Development;
        _testSystemSettings.deviceDetailProvider = new AylaSystemSettings.DeviceDetailProvider() {
            @Override
            public String[] getManagedPropertyNames(AylaDevice device) {
                if (device == null) {
                    return null;
                }

                if (TextUtils.equals(device.getOemModel(), "ledevb")) {
                    return new String[]{"Blue_button", "Blue_LED", "Green_LED"};
                }

                if (TextUtils.equals(device.getModel(), "AY001MRT1") &&
                        TextUtils.equals(device.getOemModel(), "generic")) {
                    return new String[]{"cmd", "join_enable", "join_status", "log", "network_up"};
                }

                if (false && TextUtils.equals(device.getModel(), "GenericNode")) {
                    return new String[]{"Off_Cmd", "On_Cmd", "OnOff_Status"};
                }
                return null;
            }
        };
        _testSystemSettings.allowDSS = true;
        _testConfig.setTestSystemSettings(_testSystemSettings);
        
        //Define user account
        //Define owner and recipient accounts
        _user = new RobTestAccountConfig(RobTestConstants.TEST_USERNAME, RobTestConstants.TEST_PASSWORD, RobTestConstants.TEST_DEVICE_DSN, "session1");

        _aylaAuthorization = _testConfig.signIn(_user,
                RuntimeEnvironment.application.getApplicationContext());

        assertNotNull("Failed to sign-in", _aylaAuthorization);
        _sessionManager = AylaNetworks.sharedInstance()
                .getSessionManager(_user.getTestSessionName());
        assertNotNull("Failed to get session manager", _sessionManager);

        _testConfig.waitForDeviceManagerInitComplete(_user.getTestSessionName());

    }

    @Test
    public void testCreateSubscription(){
        _deviceManager = _sessionManager.getDeviceManager();
        _dssManager = _sessionManager.getDSManager();
        RequestFuture<AylaDSSubscription> requestFutureCreate = RequestFuture.newFuture();
        _dssManager.createSubscription("testName", "description1", _deviceManager.getDevices(),
                 requestFutureCreate, requestFutureCreate);

        try {
            _aylaSubscription = requestFutureCreate.get(_testConfig.getApiTimeOut(), TimeUnit
                    .MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Failed to create subscription "+e);
        } catch (ExecutionException e) {
            e.printStackTrace();
            fail("Failed to create subscription " + e);
        } catch (TimeoutException e) {
            e.printStackTrace();
            fail("Failed to create subscription "+e);
        }
        assertNotNull("Ayla subscription created ", _aylaSubscription);
    }

    @Test
    public void testFetchSubscriptionWithId(){
        _deviceManager = _sessionManager.getDeviceManager();
        _dssManager = _sessionManager.getDSManager();
        AylaDSSubscription subscription = null;
        RequestFuture<AylaDSSubscription> requestFutureFetch = RequestFuture.newFuture();
        if(_aylaSubscription != null){
            if(_aylaSubscription.getId() != null){
                _dssManager.fetchSubscription(_aylaSubscription.getId(),
                        requestFutureFetch,
                        requestFutureFetch);
                try {
                    subscription = requestFutureFetch.get(_testConfig.getApiTimeOut(), TimeUnit
                            .MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    fail("Exception in fetchSubscription with id "+e);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    fail("Exception in fetchSubscription with id " + e);
                } catch (TimeoutException e) {
                    e.printStackTrace();
                    fail("Exception in fetchSubscription with id " + e);
                }
                assertNotNull(subscription);
                assertEquals("Fetched subscription is same as excpected subscription" ,
                        _aylaSubscription.getId(), subscription.getId());

            }
        }
    }

    @Test
    public void testUpdateSubscription(){
        _deviceManager = _sessionManager.getDeviceManager();
        _dssManager = _sessionManager.getDSManager();
        AylaDSSubscription updatedSubscription = null;
        if(_aylaSubscription != null){
            if(_aylaSubscription.getId() != null){
                RequestFuture<AylaDSSubscription> requestFutureCreate = RequestFuture.newFuture();
                _dssManager.updateSubscription(_aylaSubscription,
                        _deviceManager.getDevices(),
                         requestFutureCreate, requestFutureCreate);

                try {
                    updatedSubscription = requestFutureCreate.get(_testConfig.getApiTimeOut(), TimeUnit
                            .MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    fail("Failed to create subscription "+e);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    fail("Failed to create subscription " + e);
                } catch (TimeoutException e) {
                    e.printStackTrace();
                    fail("Failed to create subscription "+e);
                }
                assertNotNull("Ayla subscription created ", updatedSubscription);
                assertEquals("Fetched subscription is same as excpected subscription",
                        _aylaSubscription.getId(), updatedSubscription.getId());
            }
        }

    }

    @After
    public void tearDown() throws Exception {
        RequestFuture<AylaAPIRequest.EmptyResponse> requestFutureDelete = RequestFuture.newFuture();
        if(_aylaSubscription != null){
            _dssManager.deleteSubscription(_aylaSubscription.getId(), requestFutureDelete,
                    requestFutureDelete);

            try {
                requestFutureDelete.get(_testConfig.getApiTimeOut(), TimeUnit
                        .MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                fail("Failed to delete subscription "+e);
            } catch (ExecutionException e) {
                e.printStackTrace();
                fail("Failed to delete subscription " + e);
            } catch (TimeoutException e) {
                e.printStackTrace();
                fail("Failed to delete subscription "+e);
            }
        }

    }
}
