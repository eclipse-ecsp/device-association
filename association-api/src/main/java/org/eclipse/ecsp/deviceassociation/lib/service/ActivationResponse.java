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

package org.eclipse.ecsp.deviceassociation.lib.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the response received after activating a device.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_EMPTY)
public class ActivationResponse {
    private String deviceId;
    private String passcode;
    private String deviceAssociationCode;
    @JsonIgnore
    private boolean isProvisionedAlive;

    /**
     * Retrieves the device ID.
     *
     * @return the device ID
     */
    @JsonProperty("deviceId")
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Sets the device ID.
     *
     * @param deviceId the device ID to set
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Retrieves the device association code.
     *
     * @return the device association code
     */
    public String getDeviceAssociationCode() {
        return deviceAssociationCode;
    }

    /**
     * Sets the device association code.
     *
     * @param deviceAssociationCode the device association code to set
     */
    public void setDeviceAssociationCode(String deviceAssociationCode) {
        this.deviceAssociationCode = deviceAssociationCode;
    }

    /**
     * Retrieves the passcode.
     *
     * @return the passcode
     */
    public String getPasscode() {
        return passcode;
    }

    /**
     * Sets the passcode.
     *
     * @param passcode the passcode to set
     */
    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    /**
     * Returns a string representation of the ActivationResponse object.
     *
     * @return a string representation of the object
     */
    public String toString() {
        return this.deviceId + ":" + this.passcode;
    }

    /**
     * Checks if the device is provisioned and alive.
     *
     * @return true if the device is provisioned and alive, false otherwise
     */
    @JsonIgnore
    public boolean isProvisionedAlive() {
        return isProvisionedAlive;
    }

    /**
     * Sets the provisioned alive status of the device.
     *
     * @param isProvisionedAlive the provisioned alive status to set
     */
    @JsonIgnore
    public void setProvisionedAlive(boolean isProvisionedAlive) {
        this.isProvisionedAlive = isProvisionedAlive;
    }
}
