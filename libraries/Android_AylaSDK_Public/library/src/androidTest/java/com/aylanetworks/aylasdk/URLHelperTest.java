package com.aylanetworks.aylasdk;
/*
 * AylaSDK
 *
 * Copyright 2015 Ayla Networks, all rights reserved
 */

import android.test.InstrumentationTestCase;

import com.aylanetworks.aylasdk.util.DateUtils;
import com.aylanetworks.aylasdk.util.URLHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class URLHelperTest extends InstrumentationTestCase {

    public void testParameterizeArray() {
        String[] arrayItems = new String[]{"Item One", "Item Two", "Item Three"};
        String calculated = URLHelper.parameterizeArray("MyArray", arrayItems);
        String expected = "?MyArray[]=Item+One&MyArray[]=Item+Two&MyArray[]=Item+Three";
        assertEquals(expected, calculated);
    }

    public void testAppendParameters() {
        String baseUrl = "https://www.google.com/";
        Map<String, String> params = new HashMap<>();
        params.put("username", "bking");
        params.put("password", "mypassword");
        params.put("some_other_argument", "This has some spaces in it and an & ampersand");

        String calculated = URLHelper.appendParameters(baseUrl, params);
        String expected = "https://www.google" +
                ".com/?some_other_argument=This+has+some+spaces+in+it+and+an+%26+ampersand&" +
                "username=bking&password=mypassword";
        assertEquals(expected, calculated);
    }

    public void testApiGmtDateFormat() {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        cal.set(2015, Calendar.DECEMBER, 28, 14, 15, 16);
        Date testDate = cal.getTime();

        String calculated = DateUtils.getISO8601DateFormat().format(testDate);
        String expected = "2015-12-28T14:15:16Z";
        assertEquals(expected, calculated);
    }
}
