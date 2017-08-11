package com.aylanetworks.aylasdk;
/*
 * AylaSDK
 *
 * Copyright 2015 Ayla Networks, all rights reserved
 */

import android.test.InstrumentationTestCase;

import com.aylanetworks.aylasdk.auth.AylaAuthorization;
import com.aylanetworks.aylasdk.error.RequestFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ContactTest extends InstrumentationTestCase {
    private AylaContact _aylaContact;
    private AylaSessionManager _sessionManager;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        boolean isSignedIn = TestConstants.signIn(
                getInstrumentation().getContext());
        assertTrue("Failed to sign-in", isSignedIn);
        _sessionManager = AylaNetworks.sharedInstance()
                .getSessionManager(TestConstants.TEST_SESSION_NAME);
        assertNotNull(_sessionManager);
    }

    public void testCreateContact() {
        AylaContact contact = new AylaContact();
        contact.setFirstname("Ayla");
        contact.setLastname("Networks");
        contact.setDisplayName("Ayla rocks");
        contact.setEmail("support@aylanetworks.com");
        contact.setCountry("USA");

        RequestFuture<AylaContact> future = RequestFuture.newFuture();
        _sessionManager.createContact(contact, future, future);
        try {
            _aylaContact = future.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("Error in contact creation " + e);
        } catch (ExecutionException e) {
            fail("Error in contact creation " + e);
        } catch (TimeoutException e) {
            fail("Error in contact creation " + e);
        }
        assertNotNull(_aylaContact);
    }

    public void testUpdateContact() {
        AylaContact contact = new AylaContact();
        contact.setFirstname("Ayla2");
        contact.setLastname("Networks2");
        contact.setDisplayName("Ayla rocks2");
        contact.setEmail("test1@aylanetworks.com");
        contact.setCountry("USA");

        RequestFuture<AylaContact> future = RequestFuture.newFuture();
        _sessionManager.createContact(contact, future, future);
        try {
            _aylaContact = future.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
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
            _aylaContact = future.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
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

    //fetch all
    public void testFetchAllContacts() {
        AylaContact contact = new AylaContact();
        contact.setFirstname("Ayla3");
        contact.setLastname("Networks3");
        contact.setDisplayName("Ayla rocks3");
        contact.setEmail("test2@aylanetworks.com");
        contact.setCountry("USA");

        RequestFuture<AylaContact> future = RequestFuture.newFuture();
        _sessionManager.createContact(contact, future, future);
        try {
            _aylaContact = future.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
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
            aylaContacts = fetch.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
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

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if (_aylaContact != null) {
            RequestFuture<AylaAPIRequest.EmptyResponse> futureDelete = RequestFuture.newFuture();
            _sessionManager.deleteContact(_aylaContact, futureDelete, futureDelete);
            try {
                futureDelete.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
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