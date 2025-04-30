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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Represents an activation request for a device.
 * This class contains various properties related to the device activation process.
 */
public class ActivationRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    private String vin;
    private String swVersion;
    private String hwVersion;
    private String serialNumber;
    private String imei;
    private String iccid;
    private String msisdn;
    private String imsi;
    private String qualifier;
    private String productType;
    private String deviceType;
    private String bssid;
    private String ssid;
    private String aad;

    /**
     * Constructs a new ActivationRequest object.
     */
    public ActivationRequest() {
    }

    /**
     * Constructs a new ActivationRequest object with the specified VIN, serial number, and qualifier.
     *
     * @param vin           the Vehicle Identification Number (VIN) of the device
     * @param serialNumber  the serial number of the device
     * @param qualifier     the qualifier for the activation request
     */
    public ActivationRequest(String vin, String serialNumber, String qualifier) {
        super();
        this.vin = vin;
        this.serialNumber = serialNumber;
        this.qualifier = qualifier;
    }

    /**
     * Get the VIN (Vehicle Identification Number).
     *
     * @return the VIN
     */
    public String getVin() {
        return vin;
    }

    /**
     * Set the VIN (Vehicle Identification Number).
     *
     * @param vin the VIN to set
     */
    public void setVin(String vin) {
        this.vin = vin;
    }

    /**
     * Get the software version.
     *
     * @return the software version
     */
    @JsonProperty(value = "SW-Version")
    public String getSwVersion() {
        return swVersion;
    }

    /**
     * Set the software version.
     *
     * @param swVersion the software version to set
     */
    @JsonProperty(value = "SW-Version")
    public void setSwVersion(String swVersion) {
        this.swVersion = swVersion;
    }

    /**
     * Get the hardware version.
     *
     * @return the hardware version
     */
    @JsonProperty(value = "HW-Version")
    public String getHwVersion() {
        return hwVersion;
    }

    /**
     * Set the hardware version.
     *
     * @param hwVersion the hardware version to set
     */
    @JsonProperty(value = "HW-Version")
    public void setHwVersion(String hwVersion) {
        this.hwVersion = hwVersion;
    }

    /**
     * Get the serial number of the device.
     *
     * @return the serial number
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Set the serial number of the device.
     *
     * @param serialNumber the serial number to set
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * Get the IMEI (International Mobile Equipment Identity) number.
     *
     * @return the IMEI number
     */
    @JsonProperty(value = "imei")
    public String getImei() {
        return imei;
    }

    /**
     * Set the IMEI (International Mobile Equipment Identity) number.
     *
     * @param imei the IMEI number to set
     */
    @JsonProperty(value = "imei")
    public void setImei(String imei) {
        this.imei = imei;
    }

    /**
     * Get the ICCID (Integrated Circuit Card Identifier) number.
     *
     * @return the ICCID number
     */
    @JsonProperty(value = "iccid")
    public String getIccid() {
        return iccid;
    }

    /**
     * Set the ICCID (Integrated Circuit Card Identifier) number.
     *
     * @param iccid the ICCID number to set
     */
    @JsonProperty(value = "iccid")
    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    /**
     * Get the MSISDN (Mobile Station International Subscriber Directory Number).
     *
     * @return the MSISDN
     */
    @JsonProperty(value = "msisdn")
    public String getMsisdn() {
        return msisdn;
    }

    /**
     * Set the MSISDN (Mobile Station International Subscriber Directory Number).
     *
     * @param msisdn the MSISDN to set
     */
    @JsonProperty(value = "msisdn")
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    /**
     * Get the IMSI (International Mobile Subscriber Identity) number.
     *
     * @return the IMSI number
     */
    @JsonProperty(value = "imsi")
    public String getImsi() {
        return imsi;
    }

    /**
     * Set the IMSI (International Mobile Subscriber Identity) number.
     *
     * @param imsi the IMSI number to set
     */
    @JsonProperty(value = "imsi")
    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    /**
     * Get the qualifier.
     *
     * @return the qualifier
     */
    public String getQualifier() {
        return qualifier;
    }

    /**
     * Set the qualifier.
     *
     * @param qualifier the qualifier to set
     */
    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    /**
     * Get the product type.
     *
     * @return the product type
     */
    public String getProductType() {
        return productType;
    }

    /**
     * Set the product type.
     *
     * @param deviceType the product type to set
     */
    public void setProductType(String deviceType) {
        this.productType = deviceType;
    }

    /**
     * Get the device type.
     *
     * @return the device type
     */
    public String getDeviceType() {
        return deviceType;
    }

    /**
     * Set the device type.
     *
     * @param deviceType the device type to set
     */
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    /**
     * Get the SSID (Service Set Identifier).
     *
     * @return the SSID
     */
    public String getSsid() {
        return ssid;
    }

    /**
     * Set the SSID (Service Set Identifier).
     *
     * @param ssid the SSID to set
     */
    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    /**
     * Get the AAD (Additional authenticated data).
     *
     * @return the AAD authentication method
     */
    @JsonProperty(value = "aad")
    public String getAad() {
        return aad;
    }

    /**
     * Set the AAD (Additional authenticated data).
     *
     * @param aad the AAD authentication method to set
     */
    @JsonProperty(value = "aad")
    public void setAad(String aad) {
        this.aad = aad;
    }

    /**
        * Returns a string representation of the ActivationRequest object.
        *
        * @return A string representation of the ActivationRequest object.
        */
    @Override
    public String toString() {
        return "ActivationRequestData [vin=" + vin + ", swVersion=" + swVersion + ", hwVersion=" + hwVersion 
            +            ", serialNumber="
            + serialNumber + ", imei=" + imei + ", iccid=" + iccid + ", msisdn=" + msisdn + ", imsi=" + imsi 
            +            ", qualifier="
            + qualifier + ", productType=" + productType + ", deviceType=" + deviceType + ", bssid=" + bssid 
            +            ", ssid=" + ssid + ", aad=" + aad + "]";
    }

    /**
     * Get the BSSID (Basic Service Set Identifier).
     *
     * @return the BSSID
     */
    @JsonProperty(value = "BSSID")
    public String getBssid() {
        return bssid;
    }

    /**
     * Set the BSSID (Basic Service Set Identifier).
     *
     * @param bssid the BSSID to set
     */
    @JsonProperty(value = "BSSID")
    public void setBssid(String bssid) {
        this.bssid = bssid;
    }
}
