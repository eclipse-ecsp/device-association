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

import org.eclipse.ecsp.deviceassociation.lib.rest.support.NullOrNotEmpty;
import org.eclipse.ecsp.deviceassociation.lib.rest.support.NullOrNotEmptyLong;

/**
 * Represents a request for device status.
 */
public class DeviceStatusRequest {

    @NullOrNotEmptyLong(message = "associationId is not allowed be empty")
    private Long associationId;
    @NullOrNotEmpty(message = "deviceId is not allowed be empty")
    private String deviceId;
    @NullOrNotEmpty(message = "serialNumber is not allowed be empty")
    private String serialNumber;
    @NullOrNotEmpty(message = "imei is not allowed be empty")
    private String imei;
    @NullOrNotEmpty(message = "userId is not allowed be empty")
    private String userId;
    private String requiredFor;

    /**
     * Retrieves the device ID.
     *
     * @return the device ID
     */
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
     * Retrieves the serial number.
     *
     * @return the serial number
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the serial number.
     *
     * @param serialNumber the serial number to set
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * Retrieves the IMEI.
     *
     * @return the IMEI
     */
    public String getImei() {
        return imei;
    }

    /**
     * Sets the IMEI.
     *
     * @param imei the IMEI to set
     */
    public void setImei(String imei) {
        this.imei = imei;
    }

    /**
     * Retrieves the user ID.
     *
     * @return the user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user ID.
     *
     * @param userId the user ID to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Retrieves the required for field.
     *
     * @return the required for field
     */
    public String getRequiredFor() {
        return requiredFor;
    }

    /**
     * Sets the required for field.
     *
     * @param requiredFor the required for field to set
     */
    public void setRequiredFor(String requiredFor) {
        this.requiredFor = requiredFor;
    }

    /**
     * Retrieves the association ID.
     *
     * @return the association ID
     */
    public Long getAssociationId() {
        return associationId;
    }

    /**
     * Sets the association ID.
     *
     * @param associationId the association ID to set
     */
    public void setAssociationId(Long associationId) {
        this.associationId = associationId;
    }

    /**
     * Returns a string representation of the DeviceStatusRequest object.
     *
     * @return a string representation of the DeviceStatusRequest object
     */
    @Override
    public String toString() {
        return "DeviceStatusRequest{" 
            +            "associationId=" + associationId 
            +            ", deviceId='" + deviceId + '\'' 
            +            ", serialNumber='" + serialNumber + '\'' 
            +            ", imei='" + imei + '\'' 
            +            ", userId='" + userId + '\'' 
            +            ", requiredFor='" + requiredFor + '\'' 
            +            '}';
    }
}
