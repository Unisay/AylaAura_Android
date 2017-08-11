package com.aylanetworks.aylasdk.util;

import com.aylanetworks.aylasdk.AylaNetworks;
import com.aylanetworks.aylasdk.AylaSystemSettings;
import com.aylanetworks.aylasdk.AylaSystemSettings.ServiceLocation;
import com.aylanetworks.aylasdk.AylaSystemSettings.ServiceType;

import java.util.HashMap;
import java.util.Map;

/*
 * AylaSDK
 *
 * Copyright 2015 Ayla Networks, all rights reserved
 */

/**
 * This class contains the various URLs the SDK uses to connect to the cloud services. The URLs
 * vary depending on the specific cloud service (e.g. Device, User), the service type
 * (e.g. Staging, Development) and service location (e.g. USA, China).
 *
 * Simply call {@link #getBaseServiceURL} to retrieve
 * the base URL for the desired service of the desired type in the desired location.
 */
public class ServiceUrls {

    /**
     * Service types. Different APIs are called against different servers. To get the URL for
     * one of these CloudService types, call {@link #getBaseServiceURL} and pass in one of the
     * CloudService types.
     */
    public enum CloudService {
        Device,
        User,
        Datastream,
        mdssSubscription,
        Log
    }

    /**
     * Returns the base URL for the specified CloudService. The URL returned may be directly
     * appended to in order to build a complete request URL.
     *
     * @param cloudService CloudService to get the URL for
     * @param serviceType Type of service: Field | Staging | Demo | Development
     * @param serviceLocation USA | China | Europe
     *
     * @return a String with the base URL of the requested CloudService.
     */
    public static String getBaseServiceURL(CloudService cloudService,
                             ServiceType serviceType,
                             ServiceLocation serviceLocation) {
        Map<ServiceLocation, Map<ServiceType, String> > urlMap;
        switch (cloudService) {
            case Device:
                urlMap = __deviceServiceUrlMap;
                break;

            case User:
                urlMap = __userServiceUrlMap;
                break;

            case Datastream:
                urlMap = __datastreamServiceUrlMap;
                break;

            case mdssSubscription:
                urlMap = __mdssSubscriptionServiceUrlMap;
                break;

            case Log:
                urlMap = __loggingServiceUrlMap;
                break;

            default:
                return null;
        }

        return urlMap.get(serviceLocation).get(serviceType);
    }

    // Maps of locations to maps of service types to URL strings.
    //
    // e.g. to get the US services of the Field service:
    //
    //     String url = __deviceServiceUrlMap.get(USA).get(Field);
    //
    // Note that each of the URLs MUST end with a trailing slash (/).

    private static Map<ServiceLocation, Map<ServiceType, String> > __userServiceUrlMap;
    private static Map<ServiceLocation, Map<ServiceType, String> > __loggingServiceUrlMap;
    private static Map<ServiceLocation, Map<ServiceType, String> > __deviceServiceUrlMap;
    private static Map<ServiceLocation, Map<ServiceType, String> > __datastreamServiceUrlMap;
    private static Map<ServiceLocation, Map<ServiceType, String> > __mdssSubscriptionServiceUrlMap;

    static {
        // Initialize our maps
        __userServiceUrlMap = new HashMap<>();
        __loggingServiceUrlMap = new HashMap<>();
        __deviceServiceUrlMap = new HashMap<>();
        __datastreamServiceUrlMap = new HashMap<>();
        __mdssSubscriptionServiceUrlMap = new HashMap<>();
        //
        // USA URLs
        //

        // US User Service URLs
        Map<ServiceType, String> serviceTypeUrlMap = new HashMap<>();
        serviceTypeUrlMap.put(ServiceType.Field, "https://user-field.aylanetworks.com/");
        serviceTypeUrlMap.put(ServiceType.Development, "https://user-dev.aylanetworks.com/");
        serviceTypeUrlMap.put(ServiceType.Staging, "https://staging-user.ayladev.com/");
        serviceTypeUrlMap.put(ServiceType.Demo, "https://staging-user.ayladev.com/");
        __userServiceUrlMap.put(ServiceLocation.USA, serviceTypeUrlMap);

        // US Logging Service URLs
        serviceTypeUrlMap = new HashMap<>();
        serviceTypeUrlMap.put(ServiceType.Field, "https://log.aylanetworks.com/");
        serviceTypeUrlMap.put(ServiceType.Development, "https://log.aylanetworks.com/");
        serviceTypeUrlMap.put(ServiceType.Staging, "https://staging-log.ayladev.com/");
        serviceTypeUrlMap.put(ServiceType.Demo, "https://staging-log.ayladev.com/");
        __loggingServiceUrlMap.put(ServiceLocation.USA, serviceTypeUrlMap);

        // US Device Service URLs
        serviceTypeUrlMap = new HashMap<>();
        serviceTypeUrlMap.put(ServiceType.Field, "https://ads-field.aylanetworks.com/");
        serviceTypeUrlMap.put(ServiceType.Development, "https://ads-dev.aylanetworks.com/");
        serviceTypeUrlMap.put(ServiceType.Staging, "https://staging-ads.ayladev.com/");
        serviceTypeUrlMap.put(ServiceType.Demo, "https://staging-ads.ayladev.com/");
        __deviceServiceUrlMap.put(ServiceLocation.USA, serviceTypeUrlMap);

        //US Datastream Service URLs
        serviceTypeUrlMap = new HashMap<>();
        serviceTypeUrlMap.put(ServiceType.Field, "https://mstream-field.aylanetworks.com/");
        serviceTypeUrlMap.put(ServiceType.Development, "https://mstream-dev.aylanetworks.com/");
        serviceTypeUrlMap.put(ServiceType.Staging, "https://staging-mstream.ayladev.com/");
        serviceTypeUrlMap.put(ServiceType.Demo, "https://staging-mdss.ayladev.com/");
        __datastreamServiceUrlMap.put(ServiceLocation.USA, serviceTypeUrlMap);

        //US Mobile DSS REST service URLs
        serviceTypeUrlMap = new HashMap<>();
        serviceTypeUrlMap.put(ServiceType.Field, "https://mdss-field.aylanetworks.com/");
        serviceTypeUrlMap.put(ServiceType.Development, "https://mdss-dev.aylanetworks.com/");
        serviceTypeUrlMap.put(ServiceType.Staging, "https://staging-mdss.ayladev.com/");
        serviceTypeUrlMap.put(ServiceType.Demo, "https://staging-dstr.ayladev.com/");
        __mdssSubscriptionServiceUrlMap.put(ServiceLocation.USA, serviceTypeUrlMap);


        //
        // China URLs
        //

        // China User Service URLs
        serviceTypeUrlMap = new HashMap<>();
        serviceTypeUrlMap.put(ServiceType.Field, "https://user-field.ayla.com.cn/");
        serviceTypeUrlMap.put(ServiceType.Development, "https://user-dev.ayla.com.cn/");
        serviceTypeUrlMap.put(ServiceType.Staging, "https://staging-user.ayladev.com.cn/");
        serviceTypeUrlMap.put(ServiceType.Demo, "https://staging-user.ayladev.com.cn/");
        __userServiceUrlMap.put(ServiceLocation.China, serviceTypeUrlMap);

        // China Logging Service URLs
        serviceTypeUrlMap = new HashMap<>();
        serviceTypeUrlMap.put(ServiceType.Field, "https://log.ayla.com.cn/");
        serviceTypeUrlMap.put(ServiceType.Development, "https://log.ayla.com.cn/");
        serviceTypeUrlMap.put(ServiceType.Staging, "https://staging-log.ayladev.com.cn/");
        serviceTypeUrlMap.put(ServiceType.Demo, "https://staging-log.ayladev.com.cn/");
        __loggingServiceUrlMap.put(ServiceLocation.China, serviceTypeUrlMap);

        // China Device Service URLs
        serviceTypeUrlMap = new HashMap<>();
        serviceTypeUrlMap.put(ServiceType.Field, "https://ads-field.ayla.com.cn/");
        serviceTypeUrlMap.put(ServiceType.Development, "https://ads-dev.ayla.com.cn/");
        serviceTypeUrlMap.put(ServiceType.Staging, "https://staging-ads.ayladev.com.cn/");
        serviceTypeUrlMap.put(ServiceType.Demo, "https://staging-ads.ayladev.com.cn/");
        __deviceServiceUrlMap.put(ServiceLocation.China, serviceTypeUrlMap);

        //China Datastream Service URLs
        serviceTypeUrlMap = new HashMap<>();
        serviceTypeUrlMap.put(ServiceType.Field, "https://mstream-field.ayla.com.cn/");
        serviceTypeUrlMap.put(ServiceType.Development, "https://mstream-dev.ayla.com.cn/");
        serviceTypeUrlMap.put(ServiceType.Staging, "https://staging-mstream.ayladev.com/");
        serviceTypeUrlMap.put(ServiceType.Demo, "https://stream.ayla.com.cn/");
        __datastreamServiceUrlMap.put(ServiceLocation.China, serviceTypeUrlMap);

        //China Mobile DSS REST service URLs
        serviceTypeUrlMap = new HashMap<>();
        serviceTypeUrlMap.put(ServiceType.Field, "https://mdss-field.ayla.com.cn/");
        serviceTypeUrlMap.put(ServiceType.Development, "https://mdss-dev.ayla.com.cn/");
        serviceTypeUrlMap.put(ServiceType.Staging, "https://staging-mdss.ayladev.com/");
        serviceTypeUrlMap.put(ServiceType.Demo, "https://stream.ayla.com.cn/");
        __mdssSubscriptionServiceUrlMap.put(ServiceLocation.China, serviceTypeUrlMap);

        //
        // Europe URLs
        //

        // Europe User Service URLs
        serviceTypeUrlMap = new HashMap<>();
        serviceTypeUrlMap.put(ServiceType.Field, "https://user-field-eu.aylanetworks.com/");
        serviceTypeUrlMap.put(ServiceType.Development, "https://user-dev.aylanetworks.com/");
        serviceTypeUrlMap.put(ServiceType.Staging, "https://staging-user.ayladev.com/");
        serviceTypeUrlMap.put(ServiceType.Demo, "https://staging-user.ayladev.com/");
        __userServiceUrlMap.put(ServiceLocation.Europe, serviceTypeUrlMap);

        // Europe Logging Service URLs
        serviceTypeUrlMap = new HashMap<>();
        serviceTypeUrlMap.put(ServiceType.Field, "https://log-eu.aylanetworks.com/");
        serviceTypeUrlMap.put(ServiceType.Development, "https://log-eu.aylanetworks.com/");
        serviceTypeUrlMap.put(ServiceType.Staging, "https://staging-log-eu.ayladev.com/");
        serviceTypeUrlMap.put(ServiceType.Demo, "https://staging-log-eu.ayladev.com/");
        __loggingServiceUrlMap.put(ServiceLocation.Europe, serviceTypeUrlMap);

        // Europe Device Service URLs
        serviceTypeUrlMap = new HashMap<>();
        serviceTypeUrlMap.put(ServiceType.Field, "https://ads-eu.aylanetworks.com/");
        serviceTypeUrlMap.put(ServiceType.Development, "https://ads-dev.aylanetworks.com/");
        serviceTypeUrlMap.put(ServiceType.Staging, "https://staging-ads.ayladev.com/");
        serviceTypeUrlMap.put(ServiceType.Demo, "https://staging-ads.ayladev.com/");
        __deviceServiceUrlMap.put(ServiceLocation.Europe, serviceTypeUrlMap);

        //Europe Datastream Service URLs
        serviceTypeUrlMap = new HashMap<>();
        serviceTypeUrlMap.put(ServiceType.Field, "https://mstream-field-eu.aylanetworks.com/");
        serviceTypeUrlMap.put(ServiceType.Development, "https://mstream-dev.aylanetworks.com/");
        serviceTypeUrlMap.put(ServiceType.Staging, "https://staging-mstream.ayladev.com/");
        serviceTypeUrlMap.put(ServiceType.Demo, "https://staging-mdss.ayladev.com/");
        __datastreamServiceUrlMap.put(ServiceLocation.Europe, serviceTypeUrlMap);

        //Europe Mobile DSS REST service URLs
        serviceTypeUrlMap = new HashMap<>();
        serviceTypeUrlMap.put(ServiceType.Field, "https://mdss-field-eu.aylanetworks.com/");
        serviceTypeUrlMap.put(ServiceType.Development, "https://mdss-dev.aylanetworks.com/");
        serviceTypeUrlMap.put(ServiceType.Staging, "https://staging-mdss.ayladev.com/");
        serviceTypeUrlMap.put(ServiceType.Demo, "https://staging-dstr.ayladev.com/");
        __mdssSubscriptionServiceUrlMap.put(ServiceLocation.Europe, serviceTypeUrlMap);
    }
}
