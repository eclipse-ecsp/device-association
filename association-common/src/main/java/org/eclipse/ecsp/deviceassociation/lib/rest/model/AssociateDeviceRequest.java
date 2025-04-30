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

/**
 * Represents a request to associate a device.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect
public class AssociateDeviceRequest {
    private String userId;
    private String serialNumber;
    @JsonProperty("imei")
    private String imei;
    @JsonProperty("imsi")
    private String imsi;
    @JsonProperty("ssid")
    private String ssid;
    @JsonProperty("iccid")
    private String iccid;
    @JsonProperty("msisdn")
    private String msisdn;
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
     * Gets the user ID associated with the device.
     *
     * @return the user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user ID associated with the device.
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
     * Gets the IMEI (International Mobile Equipment Identity) of the device.
     *
     * @return the IMEI
     */
    @JsonProperty("imei")
    public String getImei() {
        return imei;
    }

    /**
     * Sets the IMEI (International Mobile Equipment Identity) of the device.
     *
     * @param imei the IMEI to set
     */
    public void setImei(String imei) {
        this.imei = imei;
    }

    /**
     * Gets the IMSI (International Mobile Subscriber Identity) of the device.
     *
     * @return the IMSI
     */
    @JsonProperty("imsi")
    public String getImsi() {
        return imsi;
    }

    /**
     * Sets the IMSI (International Mobile Subscriber Identity) of the device.
     *
     * @param imsi the IMSI to set
     */
    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    /**
     * Gets the SSID (Service Set Identifier) of the device.
     *
     * @return the SSID
     */
    @JsonProperty("ssid")
    public String getSsid() {
        return ssid;
    }

    /**
     * Sets the SSID (Service Set Identifier) of the device.
     *
     * @param ssid the SSID to set
     */
    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    /**
     * Gets the ICCID (Integrated Circuit Card Identifier) of the device.
     *
     * @return the ICCID
     */
    @JsonProperty("iccid")
    public String getIccid() {
        return iccid;
    }

    /**
     * Sets the ICCID (Integrated Circuit Card Identifier) of the device.
     *
     * @param iccid the ICCID to set
     */
    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    /**
     * Gets the MSISDN (Mobile Station International Subscriber Directory Number) of the device.
     *
     * @return the MSISDN
     */
    @JsonProperty("msisdn")
    public String getMsisdn() {
        return msisdn;
    }

    /**
     * Sets the MSISDN (Mobile Station International Subscriber Directory Number) of the device.
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
     * Gets the BSSID (Basic Service Set Identifier) of the device.
     *
     * @return the BSSID
     */
    @JsonProperty("bssid")
    public String getBssid() {
        return bssid;
    }

    /**
     * Sets the BSSID (Basic Service Set Identifier) of the device.
     *
     * @param bssid the BSSID to set
     */
    public void setBssid(String bssid) {
        this.bssid = bssid;
    }
}
