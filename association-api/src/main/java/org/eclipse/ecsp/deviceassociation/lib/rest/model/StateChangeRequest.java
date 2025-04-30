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

import jakarta.validation.constraints.NotBlank;
import org.eclipse.ecsp.deviceassociation.lib.rest.support.NullOrNotEmpty;

/**
 * Represents a state change request for a device association.
 */
public class StateChangeRequest {

    @NotBlank(message = "state is required and not allowed be empty")
    private String state;
    @NullOrNotEmpty(message = "imei is not allowed be empty")
    private String imei;
    @NullOrNotEmpty(message = "deviceId is not allowed be empty")
    private String deviceId;
    private String userId;

    /**
     * Get the state of the device association.
     *
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * Set the state of the device association.
     *
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Get the IMEI (International Mobile Equipment Identity) of the device.
     *
     * @return the IMEI
     */
    public String getImei() {
        return imei;
    }

    /**
     * Set the IMEI (International Mobile Equipment Identity) of the device.
     *
     * @param imei the IMEI to set
     */
    public void setImei(String imei) {
        this.imei = imei;
    }

    /**
     * Get the device ID.
     *
     * @return the device ID
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Set the device ID.
     *
     * @param deviceId the device ID to set
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Get the user ID.
     *
     * @return the user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Set the user ID.
     *
     * @param userId the user ID to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
