package com.aylanetworks.aylasdk;

import junit.framework.TestSuite;

/**
 * Created by SAmin on 5/18/16.
 */
public class SanityTests extends TestSuite {

    public static TestSuite suite() {
        Class[] testclasses = { LanModeTest.class, PropertyTest.class, SignInTest.class};

        TestSuite suite = new TestSuite(testclasses);


        return suite;
    }
}
