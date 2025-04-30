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

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.sql.Timestamp;

/**
 * Represents factory data for a device.
 */
public class FactoryData {

    private String imei;
    private String imsi;
    private String ssid;
    private String iccid;
    private String msisdn;
    private long id;
    private String bssid;
    private String model;
    private Timestamp manufacturingDate;
    private String platformVersion;
    private Timestamp recordDate;

    private String serialNumber;
    private String state;
    private boolean isStolen;
    private boolean isFaulty;
    private String deviceType;

    /**
     * Checks if the device is marked as stolen.
     *
     * @return true if the device is stolen, false otherwise
     */
    public boolean isStolen() {
        return isStolen;
    }

    /**
     * Sets the stolen status of the device.
     *
     * @param isStolen true if the device is stolen, false otherwise
     */
    public void setStolen(boolean isStolen) {
        this.isStolen = isStolen;
    }

    /**
     * Checks if the device is marked as faulty.
     *
     * @return true if the device is faulty, false otherwise
     */
    public boolean isFaulty() {
        return isFaulty;
    }

    /**
     * Sets the faulty status of the device.
     *
     * @param isFaulty true if the device is faulty, false otherwise
     */
    public void setFaulty(boolean isFaulty) {
        this.isFaulty = isFaulty;
    }

    /**
     * Get the IMEI (International Mobile Equipment Identity) of the device.
     *
     * @return the IMEI of the device
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
     * Get the IMSI (International Mobile Subscriber Identity) of the device.
     *
     * @return the IMSI of the device
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
     * Get the SSID (Service Set Identifier) of the device.
     *
     * @return the SSID of the device
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
     * @return the ICCID of the device
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
     * Get the MSISDN (Mobile Station International Subscriber Directory Number) of the device.
     *
     * @return the MSISDN of the device
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
     * Get the ID of the device.
     *
     * @return the ID of the device
     */
    @JsonIgnore
    public long getId() {
        return id;
    }

    /**
     * Set the ID of the device.
     *
     * @param id the ID to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Get the BSSID (Basic Service Set Identifier) of the device.
     *
     * @return the BSSID of the device
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
     * Get the model of the device.
     *
     * @return the model of the device
     */
    public String getModel() {
        return model;
    }

    /**
     * Set the model of the device.
     *
     * @param model the model to set
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Get the manufacturing date of the device.
     *
     * @return the manufacturing date of the device
     */
    public Timestamp getManufacturingDate() {
        if (manufacturingDate != null) {
            return new Timestamp(manufacturingDate.getTime());
        } else {
            return null;
        }
    }

    /**
     * Set the manufacturing date of the device.
     *
     * @param manufacturingDate the manufacturing date to set
     */
    public void setManufacturingDate(Timestamp manufacturingDate) {
        if (manufacturingDate != null) {
            this.manufacturingDate = new Timestamp(manufacturingDate.getTime());
        } else {
            this.manufacturingDate = null;
        }
    }

    /**
     * Get the platform version of the device.
     *
     * @return the platform version of the device
     */
    public String getPlatformVersion() {
        return platformVersion;
    }

    /**
     * Set the platform version of the device.
     *
     * @param platformVersion the platform version to set
     */
    public void setPlatformVersion(String platformVersion) {
        this.platformVersion = platformVersion;
    }

    /**
     * Get the record date of the device.
     *
     * @return the record date of the device
     */
    public Timestamp getRecordDate() {
        if (recordDate != null) {
            return new Timestamp(recordDate.getTime());
        } else {
            return null;
        }
    }

    /**
     * Set the record date of the device.
     *
     * @param recordDate the record date to set
     */
    public void setRecordDate(Timestamp recordDate) {
        if (recordDate != null) {
            this.recordDate = new Timestamp(recordDate.getTime());
        } else {
            this.recordDate = null;
        }
    }

    /**
     * Get the serial number of the device.
     *
     * @return the serial number of the device
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
     * Get the state of the device.
     *
     * @return the state of the device
     */
    public String getState() {
        return state;
    }

    /**
     * Set the state of the device.
     *
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
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
}
