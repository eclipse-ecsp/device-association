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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a request for association details.
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssociationDetailsRequest {
    private String imei;
    private String serialNumber;
    private String deviceId;

    /**
     * Default constructor for AssociationDetailsRequest.
     */
    public AssociationDetailsRequest() {

    }

    /**
     * Constructs a new AssociationDetailsRequest with the specified IMEI, serial number, and device ID.
     *
     * @param imei          the IMEI of the device
     * @param serialNumber  the serial number of the device
     * @param deviceId      the ID of the device
     */
    public AssociationDetailsRequest(String imei, String serialNumber, String deviceId) {
        this.imei = imei;
        this.serialNumber = serialNumber;
        this.deviceId = deviceId;
    }

    /**
     * Gets the IMEI number.
     *
     * @return the IMEI number
     */
    @JsonProperty(value = "imei")
    public String getImei() {
        return imei;
    }

    /**
     * Sets the IMEI number.
     *
     * @param imei the IMEI number to set
     */
    @JsonProperty(value = "imei")
    public void setImei(String imei) {
        this.imei = imei;
    }

    /**
     * Gets the serial number.
     *
     * @return the serial number
     */
    @JsonProperty(value = "serialnumber")
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the serial number.
     *
     * @param serialNumber the serial number to set
     */
    @JsonProperty(value = "serialnumber")
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * Gets the device ID.
     *
     * @return the device ID
     */
    @JsonProperty(value = "deviceid")
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Sets the device ID.
     *
     * @param deviceId the device ID to set
     */
    @JsonProperty(value = "deviceid")
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Returns a string representation of the AssociationDetailsRequest object.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "AssociationDetailsRequest [imei=" + imei + " ,serialNumber="
            + serialNumber + ",deviceId=" + deviceId + "]";
    }
}
