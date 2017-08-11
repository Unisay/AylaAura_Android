package com.aylanetworks.aylasdk.auth;
/*
 * AylaSDK
 *
 * Copyright 2015 Ayla Networks, all rights reserved
 */

import com.android.volley.Request;
import com.android.volley.Response;
import com.aylanetworks.aylasdk.AylaAPIRequest;
import com.aylanetworks.aylasdk.AylaJsonRequest;
import com.aylanetworks.aylasdk.AylaLoginManager;
import com.aylanetworks.aylasdk.AylaNetworks;
import com.aylanetworks.aylasdk.AylaSessionManager;
import com.aylanetworks.aylasdk.AylaSystemSettings;
import com.aylanetworks.aylasdk.AylaUser;
import com.aylanetworks.aylasdk.error.AuthError;
import com.aylanetworks.aylasdk.error.AylaError;
import com.aylanetworks.aylasdk.error.ErrorListener;
import com.aylanetworks.aylasdk.error.InvalidArgumentError;
import com.aylanetworks.aylasdk.error.JsonError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * AylaAuthProvider that provides authorization to the Ayla network using a
 * username and password.
 */
public class UsernameAuthProvider extends BaseAuthProvider {
    private String _username;
    private String _password;

    /**
     * Constructor
     *
     * @param username Username to sign-in, generally an email address
     * @param password Password to sign-in
     */
    public UsernameAuthProvider(String username, String password) {
        _username = username;
        _password = password;
    }

    @Override
    public void authenticate(AuthProviderListener listener, String sessionName) {
        authenticate(listener);

    }

    @Override
    public void authenticate(final AuthProviderListener listener) {
        AylaLoginManager loginManager = AylaNetworks.sharedInstance().getLoginManager();
        String url = loginManager.userServiceUrl("users/sign_in.json");

        // Login expects JSON data in this format:
        //
        // { "user": {
        //      "email":"user@aylanetworks.com",
        //      "password":"password",
        //      "application":{
        //          "app_id":"my_app_id",
        //          "app_secret":"my_app_secret"
        //      }
        //    }
        // }

        AylaSystemSettings settings = AylaNetworks.sharedInstance().getSystemSettings();

        // Construct a JSON object to contain the parameters.
        JSONObject user = new JSONObject();
        JSONObject userParam = new JSONObject();
        try {
            userParam.put("email", _username);
            userParam.put("password", _password);
            JSONObject application = new JSONObject();
            application.put("app_id", settings.appId);
            application.put("app_secret", settings.appSecret);
            userParam.put("application", application);
            user.put("user", userParam);
        } catch (JSONException e) {
            listener.didFailAuthentication(new AuthError("JSONException in signIn()", e));
            return;
        }

        String bodyText = user.toString();

        // Create our request object with some overrides to handle the POST body and
        // updating the CoreManager when we succeed
        AylaAPIRequest<AylaAuthorization> request = new AylaJsonRequest<AylaAuthorization>(
                Request.Method.POST,
                url,
                bodyText,
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


}

