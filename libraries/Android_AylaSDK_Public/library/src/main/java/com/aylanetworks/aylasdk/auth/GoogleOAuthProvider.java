package com.aylanetworks.aylasdk.auth;

import android.webkit.WebView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.aylanetworks.aylasdk.AylaAPIRequest;
import com.aylanetworks.aylasdk.AylaJsonRequest;
import com.aylanetworks.aylasdk.AylaLog;
import com.aylanetworks.aylasdk.AylaLoginManager;
import com.aylanetworks.aylasdk.AylaNetworks;
import com.aylanetworks.aylasdk.AylaSystemSettings;
import com.aylanetworks.aylasdk.error.AylaError;
import com.aylanetworks.aylasdk.error.ErrorListener;
import com.aylanetworks.aylasdk.error.JsonError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * AylaSDK
 * <p>
 * Copyright 2017 Ayla Networks, all rights reserved
 *
 *
 * This class is used to authenticate login credentials via Google OAuth provider.
 * It passes authCode received from Google to Ayla User Service for login account validation.
 * The response received from Ayla User Service is converted to AylaAuthorization object
 */

public class GoogleOAuthProvider extends AylaOAuthProvider {
    private static final String LOG_TAG = "GoogleOAuthProvider";
    private final String authCode;
    private final String GOOGLE_AUTH = "google_provider";
    private final static String AUTH_URI_LOCAL = "http://" + LOCAL_HOST + ":9000/";
    private final WebView _webView;

    public GoogleOAuthProvider(String googleAuthCode,final WebView webView) {
        authCode = googleAuthCode;
        _webView = webView;
    }

    @Override
    public void authenticate(final AuthProviderListener listener) {
        AylaLoginManager loginManager = AylaNetworks.sharedInstance().getLoginManager();
        String url = loginManager.userServiceUrl("users/provider_auth.json");
        AylaSystemSettings settings = AylaNetworks.sharedInstance().getSystemSettings();
        
        // Construct a JSON object to contain the parameters.
        JSONObject userParam = new JSONObject();
        try {
            userParam.put("code", authCode);
            userParam.put("app_id", settings.appId);
            userParam.put("app_secret", settings.appSecret);
            userParam.put("provider", GOOGLE_AUTH);

        } catch (JSONException e) {
            listener.didFailAuthentication(new JsonError(null, "JSONException trying to create " +
                    "Post body for authentication to Service", e));
            return;
        }

        String postBodyString = userParam.toString();
        AylaLog.consoleLogDebug(LOG_TAG, "authenticate using Google JSON: "+postBodyString);

        AylaAPIRequest<AylaAuthorization> request = new
                AylaJsonRequest<>(
                Request.Method.POST,
                url,
                postBodyString,
                null,
                AylaAuthorization.class,
                null, // No session manager exists until we are logged in!
                new Response.Listener<AylaAuthorization>() {
                    @Override
                    public void onResponse(AylaAuthorization response) {
                        listener.didAuthenticate(response, false);
                    }
                },
                new ErrorListener() {
                    @Override
                    public void onErrorResponse(AylaError error) {
                        listener.didFailAuthentication(error);
                    }
                });

        loginManager.sendUserServiceRequest(request);
    }

    @Override
    public void authenticate(AuthProviderListener listener, String sessionName) {
        authenticate(listener);
    }
    public String getAuthType() {
        return GOOGLE_AUTH;
    }
    public String getAuthURL() {
        return AUTH_URI_LOCAL;
    }
    public WebView getWebView() {
        return _webView;
    }
}
