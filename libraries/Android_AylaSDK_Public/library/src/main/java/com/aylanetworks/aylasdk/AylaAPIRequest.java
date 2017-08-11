package com.aylanetworks.aylasdk;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.aylanetworks.aylasdk.error.AylaError;
import com.aylanetworks.aylasdk.error.ErrorListener;
import com.aylanetworks.aylasdk.lan.AylaHttpServer;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/*
 * AylaSDK
 *
 * Copyright 2015 Ayla Networks, all rights reserved
 */

/**
 * The AylaAPIRequest class is used to encapsulate a network request. Based on Volley, this class
 * is configured to use the current user's authentication credentials for each request, if the
 * credentials are present. The credentials are retrieved from the AylaUser object held by
 * the {@link AylaSessionManager}, available via the {@link AylaSessionManager#getAuthHeaderValue()}
 * method.
 *
 * @param <T> Type of object to be serialized and returned to the caller in the case of a successful
 *           fetch operation
 */
public class AylaAPIRequest<T> extends Request<T> {
    private final static String LOG_TAG = "AylaAPIReq";
    protected final Response.Listener<T> _successListener;
    protected Map<String, String> _additionalHeaders;
    protected Class<T> _clazz;
    protected Map<String, String> _responseHeaders;
    protected boolean _logResponse;
    protected WeakReference<AylaSessionManager> _sessionManagerRef;
    protected AylaAPIRequest _chainedRequest;
    protected ErrorListener _errorListener;
    protected NetworkResponse _networkResponse;
    protected long _networkResponseTimestamp = -1;

    public AylaAPIRequest(int method, String url, Map<String, String> headers, Class<T> clazz,
                          AylaSessionManager sessionManager,
                          Response.Listener<T> successListener,
                          final ErrorListener errorListener) {
        super(method, url, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Convert the VolleyError to an AylaError
                if (errorListener != null) {
                    errorListener.onErrorResponse(AylaError.fromVolleyError(error));
                }
            }
        });

        if (errorListener == null || successListener == null) {
            AylaLog.e(LOG_TAG, "Null listener found in API request");
        }

        _errorListener = errorListener;
        _sessionManagerRef = new WeakReference<>(sessionManager);
        _successListener = successListener;
        _clazz = clazz;
        _additionalHeaders = headers;

        // Set the default timeout for requests based on the system settings
        AylaSystemSettings settings = AylaNetworks.sharedInstance().getSystemSettings();
        setRetryPolicy(new DefaultRetryPolicy(settings.defaultNetworkTimeoutMs,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        setShouldCache(false);      // Ayla requests should not be cached under any circumstances
    }

    /**
     * Sets a request as part of a chain of requests. This method should be called if a public
     * API method makes more than one API request to fulfill the original request. The requests
     * are chained in this manner to allow the entire operation to be canceled by canceling the
     * original request.
     *
     * @param request Sub-request that should be canceled if this request is canceled.
     */
    public void setChainedRequest(AylaAPIRequest request) {
        _chainedRequest = request;
    }

    /**
     * Causes the response to this request to be output to the log. The log message will be
     * a "D" (Debug) message with the tag set to "AylaAPI" by default. Derived classes may override
     * the {@link #getLogTag()} method to use a different tag if desired.
     */
    public void logResponse() {
        _logResponse = true;
    }

    public Response.Listener<T> getSuccessListener() {
        return _successListener;
    }

    /**
     * Returns the log tag for this request. Derived classes may override this method to return
     * a different tag than the default "AylaAPI" tag. Responses will only be logged if the
     * logResponse() method is called before submitting the request.
     *
     * @return The log tag for diagnostic messages created by this object
     */
    protected String getLogTag() {
        return "AylaAPI";
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        _networkResponse = response;

        // Save our response headers
        _responseHeaders = response.headers;

        // Deserialize the JSON data into an object
        String json;
        try {
            json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            if ( _logResponse ) {
                String responseString = new String(response.data);
                AylaLog.consoleLogDebug(getLogTag(), "Request: " + this.toString() +
                        " response code: " + response.statusCode +
                        " response body: " + responseString);
            }
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }

        try {
            return Response.success(
                    getGson().fromJson(json, _clazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(new AylaError(AylaError.ErrorType.JsonError,
                    json, e)));
        } catch (JsonParseException e) {
            return Response.error(new ParseError(new AylaError(AylaError.ErrorType.JsonError,
                    json, e)));
        }
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError error) {
        if ( _logResponse ) {
            AylaLog.consoleLogError(getLogTag(), error.toString() + " for " + getUrl());
        }
        return error;
    }

    @Override
    protected void deliverResponse(T response) {
        if (_successListener != null) {
            _successListener.onResponse(response);
        } else {
            AylaLog.e(LOG_TAG, "No success listener for request: " + getUrl());
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();

        // Add the required headers
        headers.put("Accept", AylaHttpServer.MIME_JSON);
        headers.put("Connection", "Keep-Alive");

        if (getUrl().startsWith("https")) {
            // If we have a session, we have an auth token
            String authorization = "none";
            AylaSessionManager sm = _sessionManagerRef.get();
            if (sm != null) {
                String authToken = sm.getAuthHeaderValue();
                if ( authToken != null ) {
                    authorization = authToken;
                }
            }
            headers.put("Authorization", authorization);
        }

        // Add / update the headers with the supplied map, if present
        if (_additionalHeaders != null) {
            headers.putAll(_additionalHeaders);
        }
        return headers;
    }

    /**
     * Override the cancel method to cancel any chained request we may have in addition to
     * canceling ourselves
     */
    @Override
    public void cancel() {
        if ( _chainedRequest != null ) {
            _chainedRequest.cancel();
        }
        super.cancel();
    }

    /**
     * Returns the network round trip time the request took in milliseconds.
     * If no network response has been received, -1 will be returned.
     *
     * @return The network round trip time the request took in milliseconds,
     * or -1 if no response has been received
     */
    public long getNetworkTimeMs() {
        if (_networkResponse != null) {
            return _networkResponse.networkTimeMs;
        }

        return -1;
    }

    /**
     * Returns the time when the response was received from the network (before any local processing)
     * in milliseconds since Linux Epoch time (recorded with System.currentTimeMillis()).
     * If no network response has been received, -1 will be returned.
     *
     * @return The time when the response was received from the network (before any local processing),
     * or -1 if no response has been received
     */
    public long getNetworkResponseTimestamp() {
        return _networkResponseTimestamp;
    }

    /**
     * Returns the NetworkResponse object received for the request, containing useful
     * information such as the network round trip time, status code, and headers data.
     * If no network response has been received, null will be returned
     *
     * @returns The NetworkResponse object received for the request, or null if no response has
     * been received
     */
    public NetworkResponse getNetworkResponse() {
        return _networkResponse;
    }

    /**
     * Returns the Gson object used to parse JSON responses and turn them into objects.
     * Override this method if you need a different Gson parser to parse the results of your
     * request.
     * <p>
     * The default Gson parser can differentiate between the various AylaDevice types such as
     * AylaDeviceNode or AylaDeviceGateway, and should be used to create AylaDevice objects
     * from JSON.
     *
     * @return The Gson object used by this object to parse JSON responses
     */
    protected Gson getGson() {
        return AylaNetworks.sharedInstance().getGson();
    }

    /**
     * API calls that do not expect data back are delivered this EmptyResponse object as a result
     */
    public static class EmptyResponse {}
}
