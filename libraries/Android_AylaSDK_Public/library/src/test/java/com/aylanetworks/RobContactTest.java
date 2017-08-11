package com.aylanetworks.aylasdk;
/*
 * AylaSDK
 *
 * Copyright 2015 RobCacheTest.java Networks, all rights reserved
 */


import com.aylanetworks.aylasdk.auth.AylaAuthorization;
import com.aylanetworks.aylasdk.error.RequestFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public class RobContactTest {
    private AylaContact _aylaContact;
    private AylaSessionManager _sessionManager;

    @Before
    public void setUp() throws Exception {
        AylaAuthorization aylaAuthorization = RobTestConstants.signIn(
                RuntimeEnvironment.application.getApplicationContext());
        assertNotNull("Failed to sign-in", aylaAuthorization);
        _sessionManager = AylaNetworks.sharedInstance()
                .getSessionManager(RobTestConstants.TEST_SESSION_NAME);
        assertNotNull(_sessionManager);
    }

    @Test
    public void testCreateContact() {
        AylaContact contact = new AylaContact();
        contact.setFirstname("RobCacheTest.java");
        contact.setLastname("Networks");
        contact.setDisplayName("RobCacheTest.java rocks");
        contact.setEmail("support@aylanetworks.com");
        contact.setCountry("USA");

        RequestFuture<AylaContact> future = RequestFuture.newFuture();
        _sessionManager.createContact(contact, future, future);
        try {
            _aylaContact = future.get(RobTestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in contact creation " + e);
        } catch (ExecutionException e) {
            fail("Error in contact creation " + e);
        } catch (TimeoutException e) {
            fail("Error in contact creation " + e);
        }
        assertNotNull(_aylaContact);
    }

    @Test
    public void testUpdateContact() {
        AylaContact contact = new AylaContact();
        contact.setFirstname("Ayla");
        contact.setLastname("Networks");
        contact.setDisplayName("Ayla rocks");
        contact.setEmail("support@aylanetworks.com");
        contact.setCountry("USA");

        RequestFuture<AylaContact> future = RequestFuture.newFuture();
        _sessionManager.createContact(contact, future, future);
        try {
            _aylaContact = future.get(RobTestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in contact creation " + e);
        } catch (ExecutionException e) {
            fail("Error in contact creation " + e);
        } catch (TimeoutException e) {
            fail("Error in contact creation " + e);
        }
        assertNotNull(_aylaContact);

        RequestFuture<AylaContact> update = RequestFuture.newFuture();
        String updatedCountry = "Germany";
        _aylaContact.setCountry(updatedCountry);
        _sessionManager.updateContact(contact, update, update);
        try {
            _aylaContact = future.get(RobTestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in update contact " + e);
        } catch (ExecutionException e) {
            fail("Error in update contact " + e);
        } catch (TimeoutException e) {
            fail("Error in update contact " + e);
        }
        assertNotNull(_aylaContact);
        assertEquals(_aylaContact.getCountry(), updatedCountry);
    }

    @Test
    public void testFetchAllContacts() {
        AylaContact contact = new AylaContact();
        contact.setFirstname("Ayla");
        contact.setLastname("Networks");
        contact.setDisplayName("Ayla rocks");
        contact.setEmail("support@aylanetworks.com");
        contact.setCountry("USA");

        RequestFuture<AylaContact> future = RequestFuture.newFuture();
        _sessionManager.createContact(contact, future, future);
        try {
            _aylaContact = future.get(RobTestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in contact creation " + e);
        } catch (ExecutionException e) {
            fail("Error in contact creation " + e);
        } catch (TimeoutException e) {
            fail("Error in contact creation " + e);
        }
        assertNotNull(_aylaContact);

        AylaContact[] aylaContacts = null;
        RequestFuture<AylaContact[]> fetch = RequestFuture.newFuture();
        _sessionManager.fetchContacts(fetch, fetch);
        try {
            aylaContacts = fetch.get(RobTestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in fetch contacts " + e);
        } catch (ExecutionException e) {
            fail("Error in fetch contacts " + e);
        } catch (TimeoutException e) {
            fail("Error in fetch contacts " + e);
        }
        assertNotNull(aylaContacts);
        assert (aylaContacts.length>0);
    }


    @After
    public void tearDown() throws Exception {
        if (_aylaContact != null) {
            RequestFuture<AylaAPIRequest.EmptyResponse> futureDelete = RequestFuture.newFuture();
            _sessionManager.deleteContact(_aylaContact, futureDelete, futureDelete);
            try {
                futureDelete.get(RobTestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                fail("Error in delete contact " + e);
            } catch (ExecutionException e) {
                fail("Error in delete contact " + e);
            } catch (TimeoutException e) {
                fail("Error in delete contact " + e);
            }
        }
    }
}