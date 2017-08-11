package com.aylanetworks.aylasdk.setup;
/*
 * AylaSDK
 *
 * Copyright 2015 Ayla Networks, all rights reserved
 */

import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.aylanetworks.aylasdk.AylaAPIRequest;
import com.aylanetworks.aylasdk.AylaDevice;
import com.aylanetworks.aylasdk.AylaDevice.RegistrationType;
import com.aylanetworks.aylasdk.AylaDeviceManager;
import com.aylanetworks.aylasdk.AylaJsonRequest;
import com.aylanetworks.aylasdk.AylaLog;
import com.aylanetworks.aylasdk.AylaSessionManager;
import com.aylanetworks.aylasdk.error.AylaError;
import com.aylanetworks.aylasdk.error.ErrorListener;
import com.aylanetworks.aylasdk.error.InvalidArgumentError;
import com.aylanetworks.aylasdk.error.JsonError;
import com.aylanetworks.aylasdk.util.EmptyListener;
import com.aylanetworks.aylasdk.util.URLHelper;
import com.google.gson.annotations.Expose;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Class used to Register/UnRegister an Ayla Device with the Cloud Service. A device must be
 * registered to the user before it can be accessed by the Device Service methods. The main
 * Registration methods are SameLan,ButtonPush,AP-Mode, Display and DSN.
 *
 * Each device type generally supports one method of registration, which depends on the hardware
 * implementation of the device. Depending on the registration type, a Registration Candidate may
 * need to be fetched from the service to provide information about the device to be registered,
 * such as its DSN, oemModel, etc. This may be done via a call to {@link #registerCandidate}.
 *
 * When enough information has been gathered to register the device, {@link #registerDevice} may
 * be called to associate the device with the user's account.
 */
public class AylaRegistration {
    public static final String AYLA_REGISTRATION_TARGET_DSN = "dsn";
    public static final String AYLA_REGISTRATION_TYPE = "regtype";
    private static final String LOG_TAG = "AylaRegistration";
    @Expose
    private String regtoken;
    @Expose
    private String lanIpAddress;

    private WeakReference<AylaDeviceManager> _deviceManagerRef;


    public AylaRegistration(AylaDeviceManager deviceManager) {
        _deviceManagerRef = new WeakReference<>(deviceManager);
    }

    /**
     * Register the device. Based on the Registration type of the Device we need  dsn/Setup
     * Token/Registration Token. Location information will be sent to the service if latitude and
     * longitude values are set on the AylaRegistrationCandidate object passed to this method.
     * For SameLan the dsn and Registration Token are needed for Registration.
     * For ButtonPush only dsn is needed for Registration.
     * For AP-Mode both dsn and Setup Token are needed.
     * For Display only Registration Token is needed for Registration.
     * For DSN only dsn is needed for Registration.
     * This method does not return any data, but the successListener will be delivered an
     * AylaDevice object in the case of success. As always, the errorListener will be called
     * if an error occurred.
     *
     * @param candidate       This is the AylaRegistrationCandidate that needs to be Registered
     * @param successListener Listener to receive on successful Registration of the device
     * @param errorListener   Listener to receive an AylaError should one occur
     * @return the AylaAPIRequest object used to Register new Ayla Device
     */
    public AylaAPIRequest registerCandidate(AylaRegistrationCandidate candidate,
                                            final Response.Listener<AylaDevice> successListener,
                                            final ErrorListener errorListener) {
        if (candidate == null) {
            if (errorListener != null) {
                errorListener.onErrorResponse(new InvalidArgumentError("Device Object is " +
                        "required"));
            }
            return null;
        }

        String deviceDSN = candidate.getDsn();
        String errResponse =null;

        switch(candidate.getRegistrationType()) {
            case SameLan:
                String lanIp = candidate.getLan_ip();
                if (!TextUtils.isEmpty(deviceDSN)&&!TextUtils.isEmpty(lanIp)) {
                    return getModuleRegistrationToken(candidate, successListener, errorListener);
                } else{
                    errResponse = "Both Candidate DSN and Lan IP are required";
                }
                break;
            case ButtonPush:
                if (!TextUtils.isEmpty(deviceDSN)) {
                    return registerDevice(candidate,successListener, errorListener,null);
                } else{
                    errResponse = "Candidate DSN is required";
                }
                break;
            case APMode:
                if(TextUtils.isEmpty(deviceDSN)){
                    errResponse = "Candidate DSN is required";
                    break;
                }
                if (candidate.getSetupToken() != null) {
                    return registerDevice(candidate,successListener, errorListener,null);
                } else {
                    errResponse = "Setup token is required";
                }
                break;
            case Display:
                if (!TextUtils.isEmpty(candidate.getRegistrationToken())) {
                    candidate.setDsn(null);
                    return registerDevice(candidate,successListener, errorListener,null);
                } else {
                    errResponse = "Registration token is required";
                }
                break;
            case DSN:
            case Node:
                if (!TextUtils.isEmpty(deviceDSN)) {
                    return registerDevice(candidate,successListener, errorListener,null);
                } else {
                    errResponse = "Candidate DSN is required";
                }
                break;
            case None:
                errResponse = "Registration type is required";
                break;
            default:
                errResponse = "Invalid Registration type";
                break;
        }
        if (errorListener != null && errResponse != null) {
            errorListener.onErrorResponse(new InvalidArgumentError(errResponse));
        }
        return null;
    }

    /**
     * This method gets a Registration Candidate from the server.
     * @param dsn dsn of the device
     * @param registrationType Registration type
     * @param successListener Listener to receive on successful Registration of the device
     * @param errorListener   Listener to receive an AylaError should one occur
     * @return the AylaAPIRequest object used to Register new Ayla Device
     */
    public AylaAPIRequest fetchCandidate(final String dsn,
                                                final RegistrationType registrationType,
                                                final Response.Listener<AylaRegistrationCandidate>
                                                        successListener,
                                                final ErrorListener errorListener) {
        AylaDeviceManager deviceManager= _deviceManagerRef.get();
        if (deviceManager == null) {
            if (errorListener != null) {
                errorListener.onErrorResponse(new AylaError(AylaError.ErrorType.AylaError,
                        "No device manager is available"));
            }
            return null;
        }

        AylaSessionManager sessionManager = deviceManager.getSessionManager();
        if (sessionManager == null) {
            if (errorListener != null) {
                errorListener.onErrorResponse(new AylaError(AylaError.ErrorType.AylaError,
                        "No session is active"));
            }
            return null;
        }

        if (registrationType == RegistrationType.Node) {
            if (errorListener != null) {
                errorListener.onErrorResponse(new InvalidArgumentError("Use AylaDeviceGateway" +
                        ".fetchRegistrationCandidates for Node candidates"));
            }
            return null;
        }

        // String url = "http://ads-dev.aylanetworks.com/apiv1/devices/register.json";
        String url = deviceManager.deviceServiceUrl("apiv1/devices/register.json");

        String typeValue = registrationType.stringValue();
        Map<String, String> params = new HashMap<>();

        if (dsn != null && registrationType != RegistrationType.Display) {
            params.put(AYLA_REGISTRATION_TARGET_DSN, dsn);
        }
        if (typeValue != null) {
            params.put(AYLA_REGISTRATION_TYPE, typeValue);
        }

        url = URLHelper.appendParameters(url, params);

        AylaAPIRequest request = new AylaAPIRequest<>(
                    Request.Method.GET,
                    url,
                    null,
                    AylaRegistrationCandidate.Wrapper.class,
                    sessionManager,
                    new Response.Listener<AylaRegistrationCandidate.Wrapper>() {
                        @Override
                        public void onResponse(AylaRegistrationCandidate.Wrapper regDevice) {
                            AylaRegistrationCandidate registrationCandidate = regDevice.device;
                            //The returned registrationCandidate does not have the RegistrationType
                            registrationCandidate.setRegistrationType(registrationType);
                            successListener.onResponse(registrationCandidate);
                        }
                    },
                    errorListener);

        deviceManager.sendDeviceServiceRequest(request);
        return request;
    }

    /**
     * This method is invoked to get a Registration Token from the device.When the token is
     * received successfully this method then calls register Device.
     *
     * @param device          This is the AylaRegistrationCandidate that needs to be Registered
     * @param successListener Listener to receive on successful Registration of the device
     * @param errorListener   Listener to receive an AylaError should one occur
     * @return the AylaAPIRequest object
     */
    private AylaAPIRequest
    getModuleRegistrationToken(final AylaRegistrationCandidate device,
                               final Response.Listener<AylaDevice> successListener,
                               final ErrorListener errorListener) {
        String lanIpServiceBaseURL = String.format("http://%s/", device.getLan_ip());
        // http://192.168.0.1/regtoken.json
        String url = String.format("%s%s", lanIpServiceBaseURL, "regtoken.json");
        AylaLog.i(LOG_TAG, "LAN IP Address " + device.getLan_ip());

        AylaDeviceManager deviceManager = _deviceManagerRef.get();
        if (deviceManager == null) {
            if (errorListener != null) {
                errorListener.onErrorResponse(new AylaError(AylaError.ErrorType.AylaError,
                        "No device manager is available"));
            }
            return null;
        }

        AylaSessionManager sessionManager = deviceManager.getSessionManager();
        if (sessionManager == null) {
            if (errorListener != null) {
                errorListener.onErrorResponse(new AylaError(AylaError.ErrorType.AylaError,
                        "No session is active"));
            }
            return null;
        }

        AylaAPIRequest request = new AylaAPIRequest<AylaRegistration>(
                Request.Method.GET,
                url,
                null,
                AylaRegistration.class,
                sessionManager,
                new EmptyListener<AylaRegistration>(), errorListener) {

            @Override
            protected void deliverResponse(AylaRegistration response) {
                device.setRegistrationToken(response.regtoken);
                registerDevice(device, successListener, errorListener, this);
            }
        };

        deviceManager.sendDeviceServiceRequest(request);
        return request;
    }

    /**
     * This method is invoked to make a Registration call to the Server.This is private helper
     * method needed for Registration.
     *
     * @param device          This is the AylaRegistrationCandidate that needs to be Registered
     * @param successListener Listener to receive on successful Registration of the device
     * @param errorListener   Listener to receive an AylaError should one occur
     * @param originalRequest Original AylaAPIRequest
     * @return the AylaAPIRequest object
     */
    protected AylaAPIRequest registerDevice(final AylaRegistrationCandidate device,
                                            final Response.Listener<AylaDevice> successListener,
                                            final ErrorListener errorListener,
                                            final AylaAPIRequest originalRequest) {
        final AylaDeviceManager deviceManager = _deviceManagerRef.get();
        if (deviceManager == null) {
            if (errorListener != null) {
                errorListener.onErrorResponse(new AylaError(AylaError.ErrorType.AylaError,
                        "No device manager is available"));
            }
            return null;
        }

        AylaSessionManager sessionManager = deviceManager.getSessionManager();
        if (sessionManager == null) {
            if (errorListener != null) {
                errorListener.onErrorResponse(new AylaError(AylaError.ErrorType.AylaError,
                        "No session is active"));
            }
            return null;
        }

        String dsn = device.getDsn();
        String regToken = device.getRegistrationToken();
        String setupToken = device.getSetupToken();

        JSONObject regDeviceJson = new JSONObject();
        try {
            JSONObject jsonObject = new JSONObject();
            if (dsn != null) {
                jsonObject.put("dsn", dsn);
            }
            if (setupToken != null) {
                jsonObject.put("setup_token", setupToken);

            }
            if (regToken != null) {
                jsonObject.put("regtoken", regToken);
            }
            regDeviceJson.put("device", jsonObject);

        } catch (JSONException jsonException) {
            if (errorListener != null) {
                errorListener.onErrorResponse(new JsonError("Json error for Registration",
                        jsonException.getMessage(), jsonException));
            }
            return null;
        }

        String url = deviceManager.deviceServiceUrl("apiv1/devices.json");
        Map<String, String> params = new HashMap<>(2);
        String lat = device.getLatitude();
        String lng = device.getLongitude();
        if(lat != null && lng != null){
            params.put("lat", lat );
            params.put("lng", lng);
            url = URLHelper.appendParameters(url, params);
        }

        AylaAPIRequest<AylaDevice.Wrapper> request = new
                AylaJsonRequest<AylaDevice.Wrapper>(
                            Request.Method.POST,
                            url,
                            regDeviceJson.toString(),
                            null,
                            AylaDevice.Wrapper.class,
                            sessionManager,
                            new Response.Listener<AylaDevice.Wrapper>() {
                                @Override
                                public void onResponse(AylaDevice.Wrapper registeredDevice) {
                                    AylaDevice aylaDevice = registeredDevice.device;
                                    // Make sure we get the most up-to-date version of the device
                                    deviceManager.addDevice(aylaDevice);
                                    successListener.onResponse(aylaDevice);
                                }
                        }, errorListener) {
                };
        // This is a compound request- we need to keep the chain going so canceling the original
        // request will cancel this new request.
        if (originalRequest != null) {
            if (originalRequest.isCanceled()) {
                request.cancel();
            } else {
                originalRequest.setChainedRequest(request);
                deviceManager.sendDeviceServiceRequest(request);
            }
        } else {
            deviceManager.sendDeviceServiceRequest(request);
        }
        return request;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");

        result.append(this.getClass().getName()).append(" Object {").append(NEW_LINE);
        result.append(" regToken: ").append(regtoken).append(NEW_LINE);
        result.append(" lanIpAddress: ").append(lanIpAddress).append(NEW_LINE);
        result.append("}");
        return result.toString();
    }
}
