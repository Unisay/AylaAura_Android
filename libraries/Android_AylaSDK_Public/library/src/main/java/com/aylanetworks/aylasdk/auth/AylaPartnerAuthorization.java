package com.aylanetworks.aylasdk.auth;
/**
 * AylaSDK
 * <p>
 * Copyright 2017 Ayla Networks, all rights reserved
 */

import com.android.volley.Request;
import com.android.volley.Response;
import com.aylanetworks.aylasdk.AylaAPIRequest;
import com.aylanetworks.aylasdk.AylaJsonRequest;
import com.aylanetworks.aylasdk.AylaNetworks;
import com.aylanetworks.aylasdk.error.ErrorListener;
import com.aylanetworks.aylasdk.error.JsonError;
import com.google.gson.annotations.Expose;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class AylaPartnerAuthorization {

    /** Partner Auth Token */
    @Expose
    private String accessToken;

    /** Partner Refresh Token */
    @Expose
    private String refreshToken;

    /** User role for current partner access */
    @Expose
    private String role;

    /** Duration in seconds of how long after its creation the current partner auth token is valid */
    @Expose
    private long expiresIn;

    /** Date at which the current partner auth token was created */
    @Expose
    private Date createdAt;

    private AylaPartnerAuthorization() {

    }

    /**
     * Log-in to partner cloud using short-lived partner token retrieved from Ayla IDP
     *
     * @param partnerToken Short-lived partner token to login to partner cloud
     * @param partnerAuthUrl                Partner cloud auth URL endpoint
     * @param partnerAppId                  App Id given to this app by partner cloud
     * @param partnerAppSecret              App secret given to this app by partner cloud
     * @param successListener Listener after successful auth to partner cloud. Passes in the created AylaPartnerAuthorization object
     * @param errorListener Listener if auth fails to partner cloud
     */
    public static AylaAPIRequest loginToPartnerCloud(String partnerToken,
                                                     String partnerAppId,
                                                     String partnerAppSecret,
                                                     String partnerAuthUrl,
                                                     Response.Listener<AylaPartnerAuthorization> successListener,
                                                     ErrorListener errorListener) {
        JSONObject params = new JSONObject();
        try {
            params.put("token", partnerToken);
            params.put("app_id", partnerAppId);
            params.put("app_secret", partnerAppSecret);
        } catch (JSONException e) {
            errorListener.onErrorResponse(new JsonError(null, "JSONException while trying to generate loginToPartnerCloud parameters.", e));
            return null;
        }
        AylaJsonRequest<AylaPartnerAuthorization> request = new AylaJsonRequest<AylaPartnerAuthorization>(
                Request.Method.POST,
                partnerAuthUrl,
                params.toString(),
                null,
                AylaPartnerAuthorization.class,
                null,
                successListener,
                errorListener);
        AylaNetworks.sharedInstance().getUserServiceRequestQueue().add(request);
        return  request;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getRole() {
        return role;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
