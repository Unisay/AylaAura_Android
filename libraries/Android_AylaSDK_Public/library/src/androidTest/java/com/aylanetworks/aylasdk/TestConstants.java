package com.aylanetworks.aylasdk;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.text.TextUtils;

import com.aylanetworks.aylasdk.auth.AylaAuthorization;
import com.aylanetworks.aylasdk.auth.UsernameAuthProvider;
import com.aylanetworks.aylasdk.change.Change;
import com.aylanetworks.aylasdk.change.ListChange;
import com.aylanetworks.aylasdk.error.AylaError;
import com.aylanetworks.aylasdk.error.RequestFuture;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.aylanetworks.aylasdk.AylaDSManager.AylaDSSubscriptionType.AylaDSSubscriptionTypeDatapoint;
/*
 * AylaSDK
 *
 * Copyright 2015 Ayla Networks, all rights reserved
 */

public class TestConstants {

    // System settings for US, Dynamic Service Type, Development appSecret
    public static final AylaSystemSettings US_DEVICE_DEV_SYSTEM_SETTINGS;
    // Name for our session
    public static final String TEST_SESSION_NAME = "test-default-session";
    public static final String TEST_DSS_SESSION_NAME = "test-dss-session";

    // Login credentials
    public static final String TEST_USERNAME =  "aminoneplus@gmail.com";
    public static final String TEST_USERNAME_NEW   = "";
    public static final String TEST_PASSWORD = "amin1234";
    public static final String TEST_BAD_PASSWORD = "thisisnotmypassword";
    public static final String TEST_PHONE_NUMBER = "9126607535";

    // Test device 1 constants
    public static final String TEST_DEVICE_DSN = "AC000W000021725";
    public static final String TEST_DEVICE_SSID = "Ayla-4439c47ff7e4";
    public static final String TEST_DEVICE_PROPERTY = "Blue_LED";
    public static final String TEST_DEVICE_PROPERTY_STREAM_UP= "stream_up";

    public static final String TEST_DEVICE_PROPERTY_STRING= "cmd";
    public static final String TEST_DEVICE_PROPERTY_DECIMAL= "decimal_in";
    public static final String TEST_DEVICE_PROPERTY_INT= "input";
    // Test device 2 constants
    public static final String TEST_USERNAME2 = "aylatestfest@gmail.com";
    public static final String TEST_USERNAME3 = "networksayla@gmail.com";
    public static final String TEST_DEVICE2_DSN = "AC000W000069399";
    public static final String TEST_DEVICE2_SSID = "Ayla-4439c47ff5d6";


    //Registration constants. Make sure you change them to valid DSN's that exists in Ayla Cloud
    public static final String TEST_REGISTER_SAME_LAN_DSN = "AC000W000021725";
    public static final String TEST_REGISTER_BUTTON_PUSH = "AC000W000281726";
    public static final String TEST_REGISTER_AP_MODE = "AC000W0000APTEST";
    public static final String TEST_REGISTER_DSN = "AC000W0000DSNTEST";

    // How long to wait for an API call
    public static int API_TIMEOUT_MS = 10000;

    /**
     * Helper method for test cases to sign in and optionally wait for the device manager
     * initialization to complete.
     *
     * @param context Context for AylaSystemSettings
     * @return The AylaAuthorization object resulting from sign-in, or null if a problem occurred
     */
    public static boolean signIn(Context context) {

        if(AylaNetworks.sharedInstance() != null){
            AylaSessionManager sessionManager = AylaNetworks.sharedInstance().getSessionManager
                    (TEST_SESSION_NAME);
            if( sessionManager != null){
                return true;
            }
        }

        // Initialize the library
        AylaSystemSettings systemSettings =
                new AylaSystemSettings(TestConstants.US_DEVICE_DEV_SYSTEM_SETTINGS);

        // We need to set our context
        systemSettings.context = context;

        InstrumentationTestCase.assertNotNull(systemSettings.context);
        AylaNetworks.initialize(systemSettings);

        AylaNetworks cm = AylaNetworks.sharedInstance();
        InstrumentationTestCase.assertNotNull(cm);

        // Use a RequestFuture to make our call to sign in synchronous
        RequestFuture<AylaAuthorization> future = RequestFuture.newFuture();

        AylaLoginManager loginManager = AylaNetworks.sharedInstance().getLoginManager();

        UsernameAuthProvider authProvider = new UsernameAuthProvider(TestConstants.TEST_USERNAME,
                TestConstants.TEST_PASSWORD);

        loginManager.signIn(authProvider, TEST_SESSION_NAME, future, future);

        AylaAuthorization auth = null;

        try {
            auth = future.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            InstrumentationTestCase.fail("Interrupted exception in sign-in: " + e.toString());
        } catch (ExecutionException e) {
            InstrumentationTestCase.fail("Execution exception in sign-in: " + e.toString());
        } catch (TimeoutException e) {
            InstrumentationTestCase.fail("Sign-in request timed out: " + e.toString());
        }

        // Make sure we have a user and an access token and a refresh token.
        InstrumentationTestCase.assertNotNull("No authorization returned", auth);
        InstrumentationTestCase.assertNotNull("Authorization has no access token", auth.getAccessToken());
        InstrumentationTestCase.assertNotNull("Authorization has no refresh token", auth.getRefreshToken());

        return auth != null;
    }

    public static boolean waitForDeviceManagerInitComplete() {
        // Make sure a DeviceManager and SessionManager exist
        AylaSessionManager sessionManager = AylaNetworks.sharedInstance()
                .getSessionManager(TEST_SESSION_NAME);
        InstrumentationTestCase.assertNotNull("No session manager was created after login", sessionManager);

        AylaDeviceManager deviceManager = sessionManager.getDeviceManager();
        InstrumentationTestCase.assertNotNull("No device manager was created after login", deviceManager);

        if(deviceManager.getState() == AylaDeviceManager.DeviceManagerState.Ready){
            return true;
        }

        InitCompleteWaiter listener = new InitCompleteWaiter();
        deviceManager.addListener(listener);

        // Wait for the init complete message to come in
        //noinspection ThrowableResultOfMethodCallIgnored
        return listener.waitForInitComplete();
    }

    public static boolean waitForTestDeviceLANMode(int timeout) {
        AylaDeviceManager dm = AylaNetworks.sharedInstance().getSessionManager(TEST_SESSION_NAME)
                .getDeviceManager();
        AylaDevice testDevice = dm.deviceWithDSN(TEST_DEVICE_DSN);
        if(testDevice.isLanModeActive()){
            return true;
        }
        return new LanModeWaiter(testDevice).waitForLanMode(timeout);
    }

    static {
        // System settings for US, Dynamic Service Type, Development appSecret
        US_DEVICE_DEV_SYSTEM_SETTINGS = new AylaSystemSettings();
        US_DEVICE_DEV_SYSTEM_SETTINGS.appId = "AgileLinkProd-id";
        US_DEVICE_DEV_SYSTEM_SETTINGS.appSecret = "AgileLinkProd-8249425";
        US_DEVICE_DEV_SYSTEM_SETTINGS.serviceLocation = AylaSystemSettings.ServiceLocation.USA;
        US_DEVICE_DEV_SYSTEM_SETTINGS.serviceType = AylaSystemSettings.ServiceType.Development;
        US_DEVICE_DEV_SYSTEM_SETTINGS.allowDSS = false;
        US_DEVICE_DEV_SYSTEM_SETTINGS.dssSubscriptionTypes = new
                String[]{AylaDSSubscriptionTypeDatapoint.stringValue()};
        US_DEVICE_DEV_SYSTEM_SETTINGS.deviceDetailProvider = new AylaSystemSettings.DeviceDetailProvider() {
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

                if (TextUtils.equals(device.getModel(), "GenericNode")) {
                    return new String[]{"01:0006_S:00", "01:0006_S:01", "01:0006_S:0000"};
                }
                return null;
            }
        };
    }

    /**
     * Waits for the DeviceManagerInitComplete notification
     */
    public static class InitCompleteWaiter implements AylaDeviceManager.DeviceManagerListener {
        private Exception _error;
        private final Object _initCompleteSemaphore = new Object();

        public boolean waitForInitComplete() {
            try {
                //noinspection SynchronizationOnLocalVariableOrMethodParameter
                synchronized (_initCompleteSemaphore) {
                    // Give this a longer time than normal - it could take a while
                    _initCompleteSemaphore.wait(TestConstants.API_TIMEOUT_MS * 3);
                }
            } catch (InterruptedException e) {
                InstrumentationTestCase.fail("Failed to get deviceListChanged call from DeviceManager");
                return false;
            }
            return _error == null;
        }

        @Override
        public void deviceManagerInitComplete(Map<String, AylaError> failedDevices) {
            if (failedDevices.size() > 0) {
                InstrumentationTestCase.fail("deviceManagerInitComplete called with errors: "
                        + failedDevices
                        .toString());
                _error = failedDevices.values().iterator().next();
            }
            synchronized (_initCompleteSemaphore) {
                _initCompleteSemaphore.notify();
            }
        }

        @Override
        public void deviceManagerInitFailure(AylaError error,
                                             AylaDeviceManager.DeviceManagerState
                                                     failedState) {
            InstrumentationTestCase.fail("deviceManagerInitFailure called. Error: " + error +
                    ", failedState: " + failedState);
            _error = error;
            synchronized (_initCompleteSemaphore) {
                _initCompleteSemaphore.notify();
            }
        }

        @Override
        public void deviceListChanged(ListChange change) {
        }

        @Override
        public void deviceManagerError(AylaError error) {
            InstrumentationTestCase.fail("Device manager encountered an error: " + error);
            _error = error;
        }

        @Override
        public void deviceManagerStateChanged(AylaDeviceManager.DeviceManagerState oldState,
                                              AylaDeviceManager.DeviceManagerState newState) {
            AylaLog.d("DeviceManagerTest", "State changed: " + oldState.toString() + " --> " +
                    newState.toString());
        }
    }

    public static class LanModeWaiter implements AylaDevice.DeviceChangeListener {
        private AylaDevice _device;
        final Object semaphore = new Object();

        public LanModeWaiter(AylaDevice device) {
            _device = device;
            _device.addListener(this);
        }

        public boolean waitForLanMode(int timeout) {
            boolean ret = false;
            synchronized (semaphore) {
                try {
                    semaphore.wait(timeout);
                    ret = true;
                } catch (InterruptedException e) {
                    ret = false;
                }
            }

            _device.removeListener(this);
            return ret;
        }

        @Override
        public void deviceChanged(AylaDevice device, Change change) {

        }

        @Override
        public void deviceError(AylaDevice device, AylaError error) {

        }

        @Override
        public void deviceLanStateChanged(AylaDevice device, boolean lanModeEnabled) {
            if ( device.getDsn().equals(TEST_DEVICE_DSN) && lanModeEnabled ) {
                synchronized (semaphore) {
                    semaphore.notify();
                }
            }
        }
    }
}
