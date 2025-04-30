/*
 *  *******************************************************************************
 *  Copyright (c) 2023-24 Harman International
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  SPDX-License-Identifier: Apache-2.0
 *  *******************************************************************************
 */

package org.eclipse.ecsp.deviceassociation.lib.rest.model;

import org.eclipse.ecsp.annotations.EventMapping;
import org.eclipse.ecsp.deviceassociation.lib.service.Constants;
import org.eclipse.ecsp.domain.Version;
import org.eclipse.ecsp.entities.AbstractEventData;

/**
 * Represents an event data object for device VIN events.
 * Inherits from the AbstractEventData class.
 */
@EventMapping(id = Constants.VIN_EVENT_ID, version = Version.V1_0)
public class DeviceVinEventData extends AbstractEventData {

    private static final long serialVersionUID = 1L;
    protected String value;
    protected boolean dummy;
    protected String type;
    protected String modelName;
    protected String deviceType;

    /**
     * Constructs a new DeviceVinEventData object.
     */
    public DeviceVinEventData() {
        super();
    }

    /**
     * Gets the value of the VIN.
     *
     * @return The value of the VIN.
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the VIN.
     *
     * @param value The value of the VIN.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Checks if the event data is dummy.
     *
     * @return True if the event data is dummy, false otherwise.
     */
    public boolean isDummy() {
        return dummy;
    }

    /**
     * Sets whether the event data is dummy.
     *
     * @param dummy True if the event data is dummy, false otherwise.
     */
    public void setDummy(boolean dummy) {
        this.dummy = dummy;
    }

    /**
     * Gets the type of the event data.
     *
     * @return The type of the event data.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the event data.
     *
     * @param type The type of the event data.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the model name associated with the event data.
     *
     * @return The model name associated with the event data.
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * Sets the model name associated with the event data.
     *
     * @param modelName The model name associated with the event data.
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * Gets the device type associated with the event data.
     *
     * @return The device type associated with the event data.
     */
    public String getDeviceType() {
        return deviceType;
    }

    /**
     * Sets the device type associated with the event data.
     *
     * @param deviceType The device type associated with the event data.
     */
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    /**
     * Returns a string representation of the DeviceVinEventData object.
     *
     * @return A string representation of the DeviceVinEventData object.
     */
    @Override
    public String toString() {
        return "DeviceVinEventData{" 
            +            "value='" + value + '\'' 
            +            ", dummy=" + dummy 
            +            ", type='" + type + '\'' 
            +            ", modelName='" + modelName + '\'' 
            +            '}';
    }
}
