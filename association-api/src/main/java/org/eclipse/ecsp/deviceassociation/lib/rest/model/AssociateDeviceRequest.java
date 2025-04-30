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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.ecsp.deviceassociation.lib.rest.support.NullOrNotEmpty;

/**
 * Represents a request to associate a device.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect
public class AssociateDeviceRequest {
    private String userId;
    @NullOrNotEmpty(message = "serialNumber is not allowed be empty")
    private String serialNumber;
    @NullOrNotEmpty(message = "imei is not allowed be empty")
    @JsonProperty("imei")
    private String imei;
    @NullOrNotEmpty(message = "imsi is not allowed be empty")
    @JsonProperty("imsi")
    private String imsi;
    @NullOrNotEmpty(message = "ssid is not allowed be empty")
    @JsonProperty("ssid")
    private String ssid;
    @NullOrNotEmpty(message = "iccid is not allowed be empty")
    @JsonProperty("iccid")
    private String iccid;
    @NullOrNotEmpty(message = "msisdn is not allowed be empty")
    @JsonProperty("msisdn")
    private String msisdn;
    @NullOrNotEmpty(message = "bssid is not allowed be empty")
    @JsonProperty("bssid")
    private String bssid;

    private long factoryId;

    /**
     * Gets the serial number of the device.
     *
     * @return the serial number
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the serial number of the device.
     *
     * @param serialNumber the serial number to set
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * Gets the user ID.
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
     * Returns a string representation of the AssociateDeviceRequest object.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "AssociateDeviceRequest [userID=" + userId + ", serialNumber=" + serialNumber + ", imei=" + imei 
            +            ", imsi=" + imsi
            + ", ssid=" + ssid + ", iccid=" + iccid + ", msisdn=" + msisdn + ", bssid=" + bssid + ", factoryID=" 
            +            factoryId + "]";
    }

    /**
     * Gets the IMEI number of the device.
     *
     * @return the IMEI number
     */
    @JsonProperty("imei")
    public String getImei() {
        return imei;
    }

    /**
     * Sets the IMEI number of the device.
     *
     * @param imei the IMEI number to set
     */
    public void setImei(String imei) {
        this.imei = imei;
    }

    /**
     * Gets the IMSI number of the device.
     *
     * @return the IMSI number
     */
    @JsonProperty("imsi")
    public String getImsi() {
        return imsi;
    }

    /**
     * Sets the IMSI number of the device.
     *
     * @param imsi the IMSI number to set
     */
    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    /**
     * Gets the SSID of the device.
     *
     * @return the SSID
     */
    @JsonProperty("ssid")
    public String getSsid() {
        return ssid;
    }

    /**
     * Sets the SSID of the device.
     *
     * @param ssid the SSID to set
     */
    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    /**
     * Gets the ICCID of the device.
     *
     * @return the ICCID
     */
    @JsonProperty("iccid")
    public String getIccid() {
        return iccid;
    }

    /**
     * Sets the ICCID of the device.
     *
     * @param iccid the ICCID to set
     */
    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    /**
     * Gets the MSISDN of the device.
     *
     * @return the MSISDN
     */
    @JsonProperty("msisdn")
    public String getMsisdn() {
        return msisdn;
    }

    /**
     * Sets the MSISDN of the device.
     *
     * @param msisdn the MSISDN to set
     */
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    /**
     * Gets the factory ID of the device.
     *
     * @return the factory ID
     */
    @JsonIgnore
    public long getFactoryId() {
        return factoryId;
    }

    /**
     * Sets the factory ID of the device.
     *
     * @param factoryId the factory ID to set
     */
    public void setFactoryId(long factoryId) {
        this.factoryId = factoryId;
    }

    /**
     * Gets the BSSID of the device.
     *
     * @return the BSSID
     */
    @JsonProperty("bssid")
    public String getBssid() {
        return bssid;
    }

    /**
     * Sets the BSSID of the device.
     *
     * @param bssid the BSSID to set
     */
    public void setBssid(String bssid) {
        this.bssid = bssid;
    }
}
