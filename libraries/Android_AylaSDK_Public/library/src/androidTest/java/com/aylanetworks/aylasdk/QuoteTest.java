package com.aylanetworks.aylasdk;
/*
 * Android_AylaSDK
 *
 * Copyright 2015 Ayla Networks, all rights reserved
 */

import android.test.InstrumentationTestCase;

import com.aylanetworks.aylasdk.util.ObjectUtils;

public class QuoteTest extends InstrumentationTestCase {
    public void testQuote() {
        String s = "This is a string. It has a \"quoted string\" in the middle.";
        String quoted = ObjectUtils.quote(s);
        assertEquals("\"This is a string. It has a \"quoted string\" in the middle.\"",
                quoted);

        String unquoted = ObjectUtils.unquote(quoted);
        assertEquals(s, unquoted);
    }

    public void testQuoteNull() {
        String s = ObjectUtils.quote(null);
        assertNotNull(s);
        assertEquals(s, "\"\"");
    }

    public void testUnquoteNull() {
        String s = ObjectUtils.unquote(null);
        assertNull(s);
    }
}
