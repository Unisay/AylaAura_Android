package com.aylanetworks.aylasdk;

import junit.framework.TestSuite;

/**
 * Created by SAmin on 4/26/16.
 */
public class CoreTests extends TestSuite {

    public static TestSuite suite() {
        Class[] testclasses = {ApplicationTest.class,
                ApplicationTriggerTest.class, AylaDeviceNotificationAppTest.class,
                BatchDatapointTest.class, ContactTest.class, DatapointTest.class,
                DeviceDatumTest.class, DeviceManagerTest.class,
                DeviceNotificationTest.class, DevicePollingTest.class,
                LanModeTest.class, PropertyTest.class, PropertyTriggerTest.class,
                ScheduleActionTest.class, ScheduleTest.class,
                ServiceUrlsTest.class, SharesTest.class, SignInTest.class, TimeZoneTest.class,
                URLHelperTest.class, UserDatumTest.class, UserProfileTest.class};

        TestSuite suite = new TestSuite(testclasses);


        return suite;
    }
}


