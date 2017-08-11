package com.aylanetworks.aylasdk.localdevice.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.aylanetworks.aylasdk.AylaAPIRequest;
import com.aylanetworks.aylasdk.AylaDevice;
import com.aylanetworks.aylasdk.AylaDeviceManager;
import com.aylanetworks.aylasdk.AylaJsonRequest;
import com.aylanetworks.aylasdk.AylaLog;
import com.aylanetworks.aylasdk.AylaSessionManager;
import com.aylanetworks.aylasdk.change.Change;
import com.aylanetworks.aylasdk.error.AylaError;
import com.aylanetworks.aylasdk.error.ErrorListener;
import com.aylanetworks.aylasdk.error.PreconditionError;
import com.aylanetworks.aylasdk.plugin.DeviceListPlugin;
import com.aylanetworks.aylasdk.setup.AylaRegistration;
import com.aylanetworks.aylasdk.setup.AylaRegistrationCandidate;
import com.aylanetworks.aylasdk.util.EmptyListener;
import com.aylanetworks.aylasdk.localdevice.AylaLocalDevice;
import com.aylanetworks.aylasdk.localdevice.AylaLocalDeviceManager;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


/*
 * Ayla SDK
 *
 * Copyright 2017 Ayla Networks, all rights reserved
 */


/**
 * The AylaBLEDeviceManager implements the AylaLocalDeviceManager and DeviceListPlugin interfaces
 * to provide local device support for BLE devices.
 */
@SuppressWarnings("WeakerAccess")
public class AylaBLEDeviceManager implements AylaLocalDeviceManager, DeviceListPlugin {
    private static final String LOG_TAG = "AylaBLEMgr";

    private BluetoothAdapter _bluetoothAdapter;

    private UUID[] _scanServices;

    /**
     * AylaBLEDeviceManager constructor.
     * @param context Context for the application
     * @param scanServices Array of service UUIDs the manager should use when scanning for local
     *                     devices. Each supported device must export a UUID from this array in
     *                     order to be discovered and automatically connect.
     */
    public AylaBLEDeviceManager(Context context, UUID[] scanServices) {
        BluetoothManager bm = (BluetoothManager)context.getSystemService(Context.BLUETOOTH_SERVICE);
        _scanServices = scanServices;
        _bluetoothAdapter = bm.getAdapter();
    }

    public interface ScanFilter {
        boolean filter(ScanRecordHelper scanRecord);
    }

    /**
     * Filter for service scans that filters based on discvered service UUIDs
     */
    public static class ServiceScanFilter implements ScanFilter {
        private UUID[] _scanServices;
        public ServiceScanFilter(UUID[] scanServices) {
            _scanServices = scanServices;
        }
        public boolean filter(ScanRecordHelper scanRecord) {
            for (UUID uuid : _scanServices) {
                if (scanRecord.containsService(uuid)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Finds local BLE devices which advertise a service ID specified in the array of serviceUUIDs
     * used to initialize the AylaBLEDeviceManager
     *
     * @param timeoutInMs Timeout for discovery in ms
     * @param successListener Listener to receive the results of the scan
     * @param errorListener Listener to receive an error should one occur
     * @return an AylaAPIRequest, which may be used to cancel the request
     */
    public AylaAPIRequest findLocalDevices(int timeoutInMs,
                                           final Response.Listener<AylaLocalDevice[]> successListener,
                                           ErrorListener errorListener) {
        ScanFilter filter = new ServiceScanFilter(_scanServices);
        return findLocalDevices(filter, timeoutInMs, successListener, errorListener);
    }

    /**
     * The AylaBLEDeviceManager's implementation of this uses a {@link ScanFilter} object as the
     * hint when doing a scan.
     *
     * @param hint Search-specific hint used to filter the set of returned devices. The format of
     *             this object is not defined, but may be used by subclasses to filter based on
     *             Bluetooth address ranges, GATT service IDs, etc.
     *
     * @param timeoutInMs Maximum time the search should take in ms
     * @param successListener Listener to be notified with the results
     * @param errorListener Listener to receive an error should one occur.
     *
     * @return a cancelable AylaAPIRequest
     */
    @Override
    public AylaAPIRequest findLocalDevices(final Object hint,
                                           final int timeoutInMs,
                                           final Response.Listener<AylaLocalDevice[]> successListener,
                                           ErrorListener errorListener) {
        final ScanFilter scanFilter = (ScanFilter)hint;

        if (_bluetoothAdapter == null) {
            errorListener.onErrorResponse(new PreconditionError("Bluetooth adapter not available"));
            return null;
        }

        if (!_bluetoothAdapter.isEnabled()) {
            errorListener.onErrorResponse(new PreconditionError("Bluetooth is not enabled"));
            return null;
        }

        final Set<AylaLocalDevice> discoveredDevices = new HashSet<>();
        final BluetoothAdapter.LeScanCallback callback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord) {
                ScanRecordHelper srh = ScanRecordHelper.parseFromBytes(scanRecord);
                boolean shouldAdd = true;
                if (srh != null) {
                    // Make sure if we have a filter that the service ID matches one of them
                    if (scanFilter != null) {
                        shouldAdd = scanFilter.filter(srh);
                    }
                }
                if (shouldAdd) {
                    AylaBLEDevice foundDevice = createLocalDevice(bluetoothDevice, rssi, scanRecord);
                    discoveredDevices.add(foundDevice);
                }
            }
        };

        final Handler timeoutHandler = new Handler(Looper.getMainLooper());
        timeoutHandler.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        //noinspection deprecation
                        _bluetoothAdapter.stopLeScan(callback);
                        AylaLocalDevice[] devices = discoveredDevices.toArray(new
                                AylaLocalDevice[discoveredDevices.size()]);
                        successListener.onResponse(devices);
                    }
                }, timeoutInMs);

        // Start the BLE scan
        //noinspection deprecation
        _bluetoothAdapter.startLeScan(callback);

        // Create an AylaAPIRequest to handle cancellation
        return new AylaAPIRequest<AylaLocalDevice[]>(
                Request.Method.GET,
                "LESCAN",
                null,
                AylaLocalDevice[].class,
                null, successListener, errorListener) {
            @Override
            public void cancel() {
                timeoutHandler.removeCallbacksAndMessages(null);
                //noinspection deprecation
                _bluetoothAdapter.stopLeScan(callback);
            }
        };
    }

    @Override
    public AylaAPIRequest registerLocalDevice(final AylaSessionManager sessionManager,
                                              final AylaLocalDevice localDevice,
                                              final String oem,
                                              final Response.Listener<AylaLocalDevice> successListener,
                                              final ErrorListener errorListener) {
        if (sessionManager == null) {
            errorListener.onErrorResponse(new PreconditionError("Valid session required"));
            return null;
        }

        if (!localDevice.isConnectedLocal()) {
            // We need to connect to the device first
            AylaLog.i(LOG_TAG, "Connecting to registration candidate " + localDevice);
            localDevice.connectLocal(new Response.Listener<AylaAPIRequest.EmptyResponse>() {
                @Override
                public void onResponse(AylaAPIRequest.EmptyResponse response) {
                    // We should be connected now, try calling ourselves again
                    registerLocalDevice(sessionManager, localDevice, oem, successListener,
                            errorListener);
                }
            }, errorListener);
        }

        final AylaDeviceManager dm = sessionManager.getDeviceManager();
        if (dm == null) {
            errorListener.onErrorResponse(new PreconditionError("No device manager available"));
            return null;
        }

        String dsn = localDevice.getDsn();
        if (dsn != null && dm.deviceWithDSN(dsn) != null) {
            errorListener.onErrorResponse(new PreconditionError("Device is already registered"));
            return null;
        }

        final AylaAPIRequest<AylaLocalDevice> returnedRequest = new AylaAPIRequest<>(
                Request.Method.GET, null, null, AylaLocalDevice.class, sessionManager,
                successListener, errorListener);

        // Make sure the device stays connected or call the error listener
        final AylaDevice.DeviceChangeListener dcListener = new AylaDevice.DeviceChangeListener() {
            @Override
            public void deviceChanged(AylaDevice device, Change change) {
                if (!localDevice.isConnectedLocal()) {
                    // We can't continue
                    returnedRequest.cancel();
                    errorListener.onErrorResponse(new AylaError(AylaError.ErrorType.AylaError,
                            "Device disconnected from Bluetooth during setup, unable to register"));
                }
            }

            @Override
            public void deviceError(AylaDevice device, AylaError error) {
                returnedRequest.cancel();
                errorListener.onErrorResponse(error);
            }

            @Override
            public void deviceLanStateChanged(AylaDevice device, boolean lanModeEnabled) {

            }
        };
        localDevice.addListener(dcListener);

        final EmptyListener<AylaAPIRequest.EmptyResponse> emptyListener = new EmptyListener<>();
        final ErrorListener cleanupErrorListener = new ErrorListener() {
            @Override
            public void onErrorResponse(AylaError error) {
                AylaLog.e(LOG_TAG, "Error during local device registration: " + error.getMessage());
                localDevice.removeListener(dcListener);
                localDevice.disconnectLocal(emptyListener, emptyListener);
                errorListener.onErrorResponse(error);
            }
        };

        // Request a registration candidate from the service using the information we have from
        // this device
        String url = dm.deviceServiceUrl("apiv1/devices/discover.json");
        String jsonBody = localDevice.getCandidateJson(oem);
        AylaJsonRequest<AylaDevice.Wrapper> request = new AylaJsonRequest<>(
                Request.Method.POST, url, jsonBody, null, AylaBLEDevice.Wrapper.class,
                sessionManager,
                new Response.Listener<AylaDevice.Wrapper>() {
                    @Override
                    public void onResponse(AylaDevice.Wrapper response) {
                        if (returnedRequest.isCanceled()) {
                            localDevice.removeListener(dcListener);
                            localDevice.disconnectLocal(emptyListener, emptyListener);
                            AylaLog.i(LOG_TAG, "Local candidate: request canceled");
                            return;
                        }

                        AylaLog.d(LOG_TAG, "Response to discover.json: " + response);
                        localDevice.updateFrom(response.device, AylaDevice.DataSource.CLOUD);

                        // Register the device
                        AylaRegistration registration = sessionManager.getDeviceManager()
                                .getAylaRegistration();
                        AylaRegistrationCandidate candidate = new AylaRegistrationCandidate(localDevice);
                        candidate.setRegistrationType(AylaDevice.RegistrationType.DSN);
                        candidate.setHardwareAddress(localDevice.getHardwareAddress());
                        registration.registerCandidate(candidate, new Response.Listener<AylaDevice>() {
                            @Override
                            public void onResponse(AylaDevice response) {
                                AylaLog.i(LOG_TAG, "Registration success: " + response);
                                localDevice.removeListener(dcListener);
                                localDevice.updateFrom(response, AylaDevice.DataSource.CLOUD);

                                AylaBLEDevice bleDevice = (AylaBLEDevice) localDevice;

                                // Save the mapping between the unique hardware ID and the BD_ADDR
                                bleDevice.mapBluetoothAddress(bleDevice.getBluetoothAddress());
                                dm.addDevice(localDevice);
                                successListener.onResponse(localDevice);
                            }
                        }, cleanupErrorListener);

                    }
                }, cleanupErrorListener);

        dm.sendDeviceServiceRequest(request);
        returnedRequest.setChainedRequest(request);

        return returnedRequest;
    }

    @Override
    public Class<? extends AylaDevice> getDeviceClass(String model, String oemModel, String
            uniqueId) {
        if (TextUtils.equals(model, AylaBLEDevice.DEFAULT_MODEL) &&
                TextUtils.equals(oemModel, AylaBLEDevice.DEFAULT_OEM_MODEL)) {
            return AylaBLEDevice.class;
        }

        // Not a device we are familiar with
        return null;
    }

    /**
     * Method to create an AylaBLEDevice from a BluetoothDevice, rssi and service record. This
     * implementation creates a new AylaBLEDevice and returns it.
     *
     * @param bluetoothDevice BluetoothDevice found in a scan
     * @param rssi RSSI
     * @param scanRecord Scan record of the device
     * @return the AylaBLEDevice for this record
     */
    protected AylaBLEDevice createLocalDevice(BluetoothDevice bluetoothDevice, int rssi, byte[]
            scanRecord) {
       return new AylaBLEDevice(bluetoothDevice, rssi, scanRecord);
    }

    // AylaPlugin methods
    @Override
    public String pluginName() {
        return "Ayla BLE Device Manager";
    }

    @Override
    public void initialize(String pluginId, AylaSessionManager sessionManager) {
        AylaLog.d(LOG_TAG, "AylaBLEDeviceManager: Initialize in role of: " + pluginId);
    }

    @Override
    public void onPause(String pluginId, AylaSessionManager sessionManager) {
        AylaLog.d(LOG_TAG, "AylaBLEDeviceManager: onPause");
        // When operating in the context of the local device manager, disconnect our devices
        if (TextUtils.equals(pluginId, AylaLocalDeviceManager.PLUGIN_ID_LOCAL_DEVICE)) {
            // Disconnect local connections
            AylaDeviceManager dm = null;
            if (sessionManager != null) {
                dm = sessionManager.getDeviceManager();
            }

            if (dm == null) {
                AylaLog.d(LOG_TAG, "onPause: No device manager available");
                return;
            }

            // Go through the device list and disconnect any local devices
            List<AylaDevice> devices = dm.getDevices();
            for (AylaDevice d : devices) {
                if (d instanceof AylaBLEDevice) {
                    AylaBLEDevice bleDevice = (AylaBLEDevice) d;
                    EmptyListener<AylaAPIRequest.EmptyResponse> emptyListener = new EmptyListener<>();
                    AylaLog.d(LOG_TAG, "Disconnecting BLE device " + bleDevice);
                    bleDevice.disconnectLocal(emptyListener, emptyListener);
                }
            }
        }
    }

    @Override
    public void onResume(String pluginId, AylaSessionManager sessionManager) {
        AylaLog.d(LOG_TAG, "AylaBLEDeviceManager: onResume");
        // When operating in the context of the local device manager, re-connect our devices
        if (TextUtils.equals(pluginId, AylaLocalDeviceManager.PLUGIN_ID_LOCAL_DEVICE)) {
            // Scan for our local devices and connect to them
            AylaDeviceManager dm = null;
            if (sessionManager != null) {
                dm = sessionManager.getDeviceManager();
            }

            if (dm == null) {
                AylaLog.d(LOG_TAG, "onResume: No device manager available");
                return;
            }

            // Go through the device list and connect any local devices
            List<AylaDevice> devices = dm.getDevices();
            for (AylaDevice d : devices) {
                if (d instanceof AylaBLEDevice) {
                    AylaBLEDevice bleDevice = (AylaBLEDevice) d;
                    EmptyListener<AylaAPIRequest.EmptyResponse> emptyListener = new EmptyListener<>();
                    AylaLog.d(LOG_TAG, "Attempting connection to BLE device " + bleDevice);
                    bleDevice.connectLocal(emptyListener, emptyListener);
                }
            }
        }
    }

    @Override
    public void shutDown(String pluginId, AylaSessionManager sessionManager) {
        AylaLog.d(LOG_TAG, "AylaBLEDeviceManager: shutDown");
        onPause(pluginId, sessionManager);
    }


    @Override
    public void updateDeviceMap(Map<String, AylaDevice> deviceMap) {
        for (AylaDevice device : deviceMap.values()) {
            if (device instanceof AylaBLEDevice) {
                AylaBLEDevice bleDevice = (AylaBLEDevice)device;
                bleDevice.initializeBluetooth();
            }
        }
    }

    public BluetoothDevice getRemoteDevice(String hardware_address) {
        return _bluetoothAdapter.getRemoteDevice(hardware_address);
    }
}
