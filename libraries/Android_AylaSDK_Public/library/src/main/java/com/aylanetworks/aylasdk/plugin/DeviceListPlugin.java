package com.aylanetworks.aylasdk.plugin;

/*
 * Aura_Android
 *
 * Copyright 2016 Ayla Networks, all rights reserved
 */


import com.aylanetworks.aylasdk.AylaDevice;

import java.util.Map;

/**
 * The DeviceListPlugin provides a means to modify the list of AylaDevice objects by
 * AylaDeviceManager before they are used to update the running device list.
 *
 * Implementers of this plugin class must return an array of AylaDevice-derived objects from the
 * {@link #updateDeviceMap(Map)} method. This list will be used by AylaDeviceManager.
 *
 * It is important to note that the updateDeviceMap method will be called multiple times, and
 * should return the same set of objects each time. Returning a new set of objects will result in
 * problems with existing listener interfaces and live device objects owned by AylaDeviceManager.
 *
 * Changes to existing device objects should be handled via calls to
 * {@link AylaDevice#updateFrom(AylaDevice, AylaDevice.DataSource)} to perform updates rather
 * than creating new objects.
 */
public interface DeviceListPlugin {
    /**
     * Called by the AylaDeviceManager after merging the list of devices from the Ayla cloud
     * service. The provided map is the same map used by AylaDeviceManager for device management.
     * Implementers may update or modify the master device map in this method.
     */
    void updateDeviceMap(Map<String, AylaDevice> deviceHashMap);
}
