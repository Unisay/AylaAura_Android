package com.aylanetworks.aylasdk.change;
/*
 * AylaSDK
 *
 * Copyright 2015 Ayla Networks, all rights reserved
 */

import java.util.Set;

/**
 * A PropertyChange is used to notify listeners of the details of a Device's property changing.
 * Changes can include the property's value, timestamp, or any other field.
 *
 * The PropertyChange object inherits from FieldChange, and adds the getPropertyName method to
 * indicate which property changed.
 */
public class PropertyChange extends FieldChange {
    private String _propertyName;

    /**
     * Constructs a PropertyChange with the given property name and field change. If the
     * fieldChange parameter is null, the property itself was added or removed rather than a
     * field within it changing.
     *
     * @param propertyName Name of the property that changed
     * @param changedFieldNames Set of names of fields that changed within this object.
     *                          Properties generally will only change their value and timestamps.
     */
    public PropertyChange(String propertyName, Set<String> changedFieldNames) {
        super(ChangeType.Property, changedFieldNames);

        _propertyName = propertyName;
    }

    /**
     * Constructs a PropertyChange indicating that the value field has changed
     * @param propertyName Name of the property whose value changed
     */
    public PropertyChange(String propertyName) {
        super(ChangeType.Property, "value");
        _propertyName = propertyName;
    }

    public String getPropertyName() {
        return _propertyName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(256);
        sb.append("PropertyChange: [");
        sb.append(_propertyName);
        sb.append("] ");
        sb.append(super.toString());
        return sb.toString();
    }
}
