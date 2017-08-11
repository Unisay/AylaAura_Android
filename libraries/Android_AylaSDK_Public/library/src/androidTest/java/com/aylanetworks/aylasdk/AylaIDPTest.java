package com.aylanetworks.aylasdk;

import android.test.InstrumentationTestCase;

import com.aylanetworks.aylasdk.auth.AylaIDPAuthProvider;
import com.aylanetworks.aylasdk.auth.AylaPartnerAuthorization;
import com.aylanetworks.aylasdk.error.RequestFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * AylaSDKTest
 *
 * Copyright 2017 Ayla Networks, all rights reserved
 */

public class AylaIDPTest extends InstrumentationTestCase {

    private static final String PARTNER_ID = "12dcef78";
    private static final String PARTNER_APP_ID = "mobile-partner-app-id";
    private static final String PARTNER_APP_SECRET = "uUeLyGoHWqwOiF3WDuMHQM-3MnA";
    private static final String PARTNER_AUTH_URL = "https://partner-cloud-mob-prod.ayladev.com/api/v1/token_sign_in";
    private static final String PARTNER_REFRESH_URL = "";

    private List<AylaIDPAuthProvider.IDPPartnerToken> partnerTokens;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TestConstants.signIn(getInstrumentation().getContext());
    }

    public void testGetPartnerTokensFromAylaIDPService() {
        AylaSessionManager sessionManager = AylaNetworks.sharedInstance().getSessionManager(TestConstants.TEST_SESSION_NAME);
        AylaIDPAuthProvider authProvider = new AylaIDPAuthProvider(sessionManager);

        List<String> partnerIds = new ArrayList<>();
        partnerIds.add(PARTNER_ID);
        RequestFuture<List<AylaIDPAuthProvider.IDPPartnerToken>> future = RequestFuture.newFuture();
        authProvider.getPartnerTokensForPartnerIds(partnerIds, future, future);

        try {
            partnerTokens = future.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("GetPartnerTokensFromAylaIDPService interrupted: " + e);
        } catch (ExecutionException e) {
            fail("GetPartnerTokensFromAylaIDPService execution exception: " + e);
        } catch (TimeoutException e) {
            fail("GetPartnerTokensFromAylaIDPService timeout: " + e);
        }
        assertNotNull("No partner token was fetched", partnerTokens);
        assertEquals(partnerIds.size(), partnerTokens.size());

        RequestFuture<AylaPartnerAuthorization> authFuture = RequestFuture.newFuture();
        AylaPartnerAuthorization.loginToPartnerCloud(partnerTokens.get(0).getPartnerToken(), PARTNER_APP_ID, PARTNER_APP_SECRET, PARTNER_AUTH_URL, authFuture, authFuture);
        AylaPartnerAuthorization authorization = null;
        try {
            authorization = authFuture.get(TestConstants.API_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("loginToPartnerCloud interrupted: " + e);
        } catch (ExecutionException e) {
            fail("loginToPartnerCloud execution exception: " + e);
        } catch (TimeoutException e) {
            fail("loginToPartnerCloud timeout: " + e);
        }
        assertNotNull("No partner authorization returned", authorization);
        assertNotNull("No partner authorization access token returned", authorization.getAccessToken());
        assertNotNull("No partner authorization refresh token returned", authorization.getRefreshToken());
    }
}
