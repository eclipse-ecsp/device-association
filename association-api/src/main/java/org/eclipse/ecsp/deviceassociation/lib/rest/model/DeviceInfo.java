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

/**
 * Represents device information.
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceInfo {

    private String harmanId;
    private String serialNumber;
    private String softwareVersion;
    private String hardwareVersion;
    private String deviceType;
    private String vin;
    private String imei;
    private String ssid;
    private String iccid;
    private String bssid;
    private String msisdn;
    private String imsi;
    private String productType;

    /**
     * Default constructor.
     */
    public DeviceInfo() {
        super();
    }

    /**
     * Constructor with parameters.
     *
     * @param harmanId         the Harman ID of the device
     * @param serialNumber     the serial number of the device
     * @param softwareVersion  the software version of the device
     * @param hardwareVersion  the hardware version of the device
     * @param deviceType       the type of the device
     * @param vin              the VIN (Vehicle Identification Number) of the device
     * @param imei             the IMEI (International Mobile Equipment Identity) of the device
     * @param ssid             the SSID (Service Set Identifier) of the device
     * @param iccid            the ICCID (Integrated Circuit Card Identifier) of the device
     * @param bssid            the BSSID (Basic Service Set Identifier) of the device
     * @param msisdn           the MSISDN (Mobile Station International Subscriber Directory Number) of the device
     * @param imsi             the IMSI (International Mobile Subscriber Identity) of the device
     * @param productType      the type of the product
     */
    public DeviceInfo(String harmanId, String serialNumber, String softwareVersion, String hardwareVersion,
                      String deviceType, String vin, String imei, String ssid, String iccid, String bssid,
                      String msisdn, String imsi, String productType) {
        super();
        this.harmanId = harmanId;
        this.serialNumber = serialNumber;
        this.softwareVersion = softwareVersion;
        this.hardwareVersion = hardwareVersion;
        this.deviceType = deviceType;
        this.vin = vin;
        this.imei = imei;
        this.ssid = ssid;
        this.iccid = iccid;
        this.bssid = bssid;
        this.msisdn = msisdn;
        this.imsi = imsi;
        this.productType = productType;
    }

    /**
     * Get the Harman ID of the device.
     *
     * @return the Harman ID
     */
    public String getHarmanId() {
        return harmanId;
    }

    /**
     * Set the Harman ID of the device.
     *
     * @param harmanId the Harman ID to set
     */
    public void setHarmanId(String harmanId) {
        this.harmanId = harmanId;
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
     * Get the software version of the device.
     *
     * @return the software version
     */
    public String getSoftwareVersion() {
        return softwareVersion;
    }

    /**
     * Set the software version of the device.
     *
     * @param softwareVersion the software version to set
     */
    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    /**
     * Get the hardware version of the device.
     *
     * @return the hardware version
     */
    public String getHardwareVersion() {
        return hardwareVersion;
    }

    /**
     * Set the hardware version of the device.
     *
     * @param hardwareVersion the hardware version to set
     */
    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    /**
     * Get the type of the device.
     *
     * @return the device type
     */
    public String getDeviceType() {
        return deviceType;
    }

    /**
     * Set the type of the device.
     *
     * @param deviceType the device type to set
     */
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    /**
     * Get the VIN (Vehicle Identification Number) of the device.
     *
     * @return the VIN
     */
    public String getVin() {
        return vin;
    }

    /**
     * Set the VIN (Vehicle Identification Number) of the device.
     *
     * @param vin the VIN to set
     */
    public void setVin(String vin) {
        this.vin = vin;
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
     * Get the SSID (Service Set Identifier) of the device.
     *
     * @return the SSID
     */
    public String getSsid() {
        return ssid;
    }

    /**
     * Set the SSID (Service Set Identifier) of the device.
     *
     * @param ssid the SSID to set
     */
    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    /**
     * Get the ICCID (Integrated Circuit Card Identifier) of the device.
     *
     * @return the ICCID
     */
    public String getIccid() {
        return iccid;
    }

    /**
     * Set the ICCID (Integrated Circuit Card Identifier) of the device.
     *
     * @param iccid the ICCID to set
     */
    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    /**
     * Get the BSSID (Basic Service Set Identifier) of the device.
     *
     * @return the BSSID
     */
    public String getBssid() {
        return bssid;
    }

    /**
     * Set the BSSID (Basic Service Set Identifier) of the device.
     *
     * @param bssid the BSSID to set
     */
    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    /**
     * Get the MSISDN (Mobile Station International Subscriber Directory Number) of the device.
     *
     * @return the MSISDN
     */
    public String getMsisdn() {
        return msisdn;
    }

    /**
     * Set the MSISDN (Mobile Station International Subscriber Directory Number) of the device.
     *
     * @param msisdn the MSISDN to set
     */
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    /**
     * Get the IMSI (International Mobile Subscriber Identity) of the device.
     *
     * @return the IMSI
     */
    public String getImsi() {
        return imsi;
    }

    /**
     * Set the IMSI (International Mobile Subscriber Identity) of the device.
     *
     * @param imsi the IMSI to set
     */
    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    /**
     * Get the type of the product.
     *
     * @return the product type
     */
    public String getProductType() {
        return productType;
    }

    /**
     * Set the type of the product.
     *
     * @param productType the product type to set
     */
    public void setProductType(String productType) {
        this.productType = productType;
    }

    /**
     * Returns a string representation of the DeviceInfo object.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "DeviceInfo [harmanId=" + harmanId + ", serialNumber=" + serialNumber + ", softwareVersion="
            + softwareVersion + ", hardwareVersion=" + hardwareVersion + ", deviceType=" + deviceType + ", vin="
            + vin + ", imei=" + imei + ", ssid=" + ssid + ", iccid=" + iccid + ", bssid=" + bssid + ", msisdn="
            + msisdn + ", imsi=" + imsi + ", productType=" + productType + "]";
    }

}
