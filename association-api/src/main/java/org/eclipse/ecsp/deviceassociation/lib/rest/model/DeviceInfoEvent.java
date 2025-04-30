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

import org.eclipse.ecsp.entities.AbstractEventData;

/**
 * Represents a device information event.
 */
public class DeviceInfoEvent extends AbstractEventData {

    private static final long serialVersionUID = 1L;

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
     * Returns the serial version UID.
     *
     * @return the serial version UID
     */
    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    /**
     * Returns the Harman ID.
     *
     * @return the Harman ID
     */
    public String getHarmanId() {
        return harmanId;
    }

    /**
     * Sets the Harman ID.
     *
     * @param harmanId the Harman ID to set
     */
    public void setHarmanId(String harmanId) {
        this.harmanId = harmanId;
    }

    /**
     * Returns the serial number.
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
     * Returns the software version.
     *
     * @return the software version
     */
    public String getSoftwareVersion() {
        return softwareVersion;
    }

    /**
     * Sets the software version.
     *
     * @param softwareVersion the software version to set
     */
    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    /**
     * Returns the device type.
     *
     * @return the device type
     */
    public String getDeviceType() {
        return deviceType;
    }

    /**
     * Sets the device type.
     *
     * @param deviceType the device type to set
     */
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    /**
     * Returns the VIN.
     *
     * @return the VIN
     */
    public String getVin() {
        return vin;
    }

    /**
     * Sets the VIN.
     *
     * @param vin the VIN to set
     */
    public void setVin(String vin) {
        this.vin = vin;
    }

    /**
     * Returns the IMEI.
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
     * Returns the SSID.
     *
     * @return the SSID
     */
    public String getSsid() {
        return ssid;
    }

    /**
     * Sets the SSID.
     *
     * @param ssid the SSID to set
     */
    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    /**
     * Returns the ICCID.
     *
     * @return the ICCID
     */
    public String getIccid() {
        return iccid;
    }

    /**
     * Sets the ICCID.
     *
     * @param iccid the ICCID to set
     */
    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    /**
     * Returns the BSSID.
     *
     * @return the BSSID
     */
    public String getBssid() {
        return bssid;
    }

    /**
     * Sets the BSSID.
     *
     * @param bssid the BSSID to set
     */
    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    /**
     * Returns the MSISDN.
     *
     * @return the MSISDN
     */
    public String getMsisdn() {
        return msisdn;
    }

    /**
     * Sets the MSISDN.
     *
     * @param msisdn the MSISDN to set
     */
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    /**
     * Returns the IMSI.
     *
     * @return the IMSI
     */
    public String getImsi() {
        return imsi;
    }

    /**
     * Sets the IMSI.
     *
     * @param imsi the IMSI to set
     */
    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    /**
     * Returns the product type.
     *
     * @return the product type
     */
    public String getProductType() {
        return productType;
    }

    /**
     * Sets the product type.
     *
     * @param productType the product type to set
     */
    public void setProductType(String productType) {
        this.productType = productType;
    }

    /**
     * Returns the hardware version.
     *
     * @return the hardware version
     */
    public String getHardwareVersion() {
        return hardwareVersion;
    }

    /**
     * Sets the hardware version.
     *
     * @param hardwareVersion the hardware version to set
     */
    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    /**
     * Returns a string representation of the DeviceInfoEvent object.
     *
     * @return a string representation of the DeviceInfoEvent object
     */
    @Override
    public String toString() {
        return "DeviceInfoEvent [harmanId=" + harmanId + ", serialNumber=" + serialNumber + ", softwareVersion="
            + softwareVersion + ", hardwareVersion=" + hardwareVersion + ", deviceType=" + deviceType + ", vin="
            + vin + ", imei=" + imei + ", ssid=" + ssid + ", iccid=" + iccid + ", bssid=" + bssid + ", msisdn="
            + msisdn + ", imsi=" + imsi + ", productType=" + productType + "]";
    }

}
