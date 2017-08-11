package com.aylanetworks.aylasdk;

import junit.framework.TestSuite;

/**
 * Created by SAmin on 4/26/16.
 */
public class TestIterator extends TestSuite {

    public static TestSuite suite() {

        Class[] iterationClass = new Class[10];

        for (int i = 0; i < 10; i++) {
            iterationClass[i] = ApplicationTest.class;
        }

        TestSuite suite = new TestSuite(iterationClass);
        return suite;
    }
}
