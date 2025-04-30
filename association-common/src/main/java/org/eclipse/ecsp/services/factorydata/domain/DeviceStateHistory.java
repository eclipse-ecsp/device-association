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

package org.eclipse.ecsp.services.factorydata.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.sql.Timestamp;

/**
 * Represents the history of device states.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceStateHistory {
    @JsonIgnore
    private Long id = 0L; // Sets 0L to be sure that is others parts of the code work as before
    private String state;
    private Timestamp stateTimestamp;
    private Timestamp manufacturingDate;
    private String model;
    private String imei;
    private String serialNumber;
    private String platformVersion;
    private String iccid;
    private String ssid;
    private String bssid;
    private String msisdn;
    private String imsi;
    private Timestamp recordDate;
    private String factoryAdmin;
    private Timestamp createdDate;
    private Boolean stolen;
    private Boolean faulty;
    private String packageSerialNumber;

    /**
     * Gets the ID of the device state history.
     *
     * @return The ID of the device state history.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the device state history.
     *
     * @param id The ID of the device state history.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the state of the device.
     *
     * @return The state of the device.
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the state of the device.
     *
     * @param state The state of the device.
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Gets the timestamp of when the device state was recorded.
     *
     * @return The timestamp of when the device state was recorded.
     */
    public Timestamp getStateTimestamp() {
        return stateTimestamp;
    }

    /**
     * Sets the timestamp of when the device state was recorded.
     *
     * @param stateTimestamp The timestamp of when the device state was recorded.
     */
    public void setStateTimestamp(Timestamp stateTimestamp) {
        this.stateTimestamp = stateTimestamp;
    }

    /**
     * Gets the manufacturing date of the device.
     *
     * @return The manufacturing date of the device.
     */
    public Timestamp getManufacturingDate() {
        return manufacturingDate;
    }

    /**
     * Sets the manufacturing date of the device.
     *
     * @param manufacturingDate The manufacturing date of the device.
     */
    public void setManufacturingDate(Timestamp manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
    }

    /**
     * Gets the model of the device.
     *
     * @return The model of the device.
     */
    public String getModel() {
        return model;
    }

    /**
     * Sets the model of the device.
     *
     * @param model The model of the device.
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Gets the IMEI number of the device.
     *
     * @return The IMEI number of the device.
     */
    public String getImei() {
        return imei;
    }

    /**
     * Sets the IMEI number of the device.
     *
     * @param imei The IMEI number of the device.
     */
    public void setImei(String imei) {
        this.imei = imei;
    }

    /**
     * Gets the serial number of the device.
     *
     * @return The serial number of the device.
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the serial number of the device.
     *
     * @param serialNumber The serial number of the device.
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * Gets the platform version of the device.
     *
     * @return The platform version of the device.
     */
    public String getPlatformVersion() {
        return platformVersion;
    }

    /**
     * Sets the platform version of the device.
     *
     * @param platformVersion The platform version of the device.
     */
    public void setPlatformVersion(String platformVersion) {
        this.platformVersion = platformVersion;
    }

    /**
     * Gets the ICCID of the device.
     *
     * @return The ICCID of the device.
     */
    public String getIccid() {
        return iccid;
    }

    /**
     * Sets the ICCID of the device.
     *
     * @param iccid The ICCID of the device.
     */
    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    /**
     * Gets the SSID of the device.
     *
     * @return The SSID of the device.
     */
    public String getSsid() {
        return ssid;
    }

    /**
     * Sets the SSID of the device.
     *
     * @param ssid The SSID of the device.
     */
    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    /**
     * Gets the BSSID of the device.
     *
     * @return The BSSID of the device.
     */
    public String getBssid() {
        return bssid;
    }

    /**
     * Sets the BSSID of the device.
     *
     * @param bssid The BSSID of the device.
     */
    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    /**
     * Gets the MSISDN of the device.
     *
     * @return The MSISDN of the device.
     */
    public String getMsisdn() {
        return msisdn;
    }

    /**
     * Sets the MSISDN of the device.
     *
     * @param msisdn The MSISDN of the device.
     */
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    /**
     * Gets the IMSI of the device.
     *
     * @return The IMSI of the device.
     */
    public String getImsi() {
        return imsi;
    }

    /**
     * Sets the IMSI of the device.
     *
     * @param imsi The IMSI of the device.
     */
    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    /**
     * Gets the record date of the device state.
     *
     * @return The record date of the device state.
     */
    public Timestamp getRecordDate() {
        return recordDate;
    }

    /**
     * Sets the record date of the device state.
     *
     * @param recordDate The record date of the device state.
     */
    public void setRecordDate(Timestamp recordDate) {
        this.recordDate = recordDate;
    }

    /**
     * Gets the factory admin of the device.
     *
     * @return The factory admin of the device.
     */
    public String getFactoryAdmin() {
        return factoryAdmin;
    }

    /**
     * Sets the factory admin of the device.
     *
     * @param factoryAdmin The factory admin of the device.
     */
    public void setFactoryAdmin(String factoryAdmin) {
        this.factoryAdmin = factoryAdmin;
    }

    /**
     * Gets the created date of the device state history.
     *
     * @return The created date of the device state history.
     */
    public Timestamp getCreatedDate() {
        return createdDate;
    }

    /**
     * Sets the created date of the device state history.
     *
     * @param createdDate The created date of the device state history.
     */
    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * Checks if the device is marked as stolen.
     *
     * @return True if the device is marked as stolen, false otherwise.
     */
    public Boolean getStolen() {
        return stolen;
    }

    /**
     * Sets the stolen status of the device.
     *
     * @param stolen True if the device is marked as stolen, false otherwise.
     */
    public void setStolen(Boolean stolen) {
        this.stolen = stolen;
    }

    /**
     * Checks if the device is marked as faulty.
     *
     * @return True if the device is marked as faulty, false otherwise.
     */
    public Boolean getFaulty() {
        return faulty;
    }

    /**
     * Sets the faulty status of the device.
     *
     * @param faulty True if the device is marked as faulty, false otherwise.
     */
    public void setFaulty(Boolean faulty) {
        this.faulty = faulty;
    }

    /**
     * Gets the package serial number of the device.
     *
     * @return The package serial number of the device.
     */
    public String getPackageSerialNumber() {
        return packageSerialNumber;
    }

    /**
     * Sets the package serial number of the device.
     *
     * @param packageSerialNumber The package serial number of the device.
     */
    public void setPackageSerialNumber(String packageSerialNumber) {
        this.packageSerialNumber = packageSerialNumber;
    }
}
