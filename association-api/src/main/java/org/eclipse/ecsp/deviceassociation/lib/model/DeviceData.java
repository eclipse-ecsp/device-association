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

package org.eclipse.ecsp.deviceassociation.lib.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.io.Serializable;

/**
 * Represents device data.
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceData implements Serializable {

    private String factoryAdmin;
    private String manufacturingDate;
    private String model;
    private String imei;
    private String serialNumber;
    private String platformVersion;
    private String iccid;
    private String ssid;
    private String bssid;
    private String msisdn;
    private String imsi;
    private String recordDate;
    private String packageSerialNumber;

    private String chassisNumber;
    private String plant;
    private String productionWeek;
    private String vehicleModelYear;
    private String vin;
    private String friendlyName;

    /**
     * Gets the manufacturing date of the device.
     *
     * @return The manufacturing date.
     */
    public String getManufacturingDate() {
        return this.manufacturingDate;
    }

    /**
     * Sets the manufacturing date of the device.
     *
     * @param manufacturingDate The manufacturing date to set.
     */
    public void setManufacturingDate(String manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
    }

    /**
     * Gets the model of the device.
     *
     * @return The model.
     */
    public String getModel() {
        return model;
    }

    /**
     * Sets the model of the device.
     *
     * @param model The model to set.
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Gets the IMEI number of the device.
     *
     * @return The IMEI number.
     */
    public String getImei() {
        return imei;
    }

    /**
     * Sets the IMEI number of the device.
     *
     * @param imei The IMEI number to set.
     */
    public void setImei(String imei) {
        this.imei = imei;
    }

    /**
     * Gets the serial number of the device.
     *
     * @return The serial number.
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the serial number of the device.
     *
     * @param serialNumber The serial number to set.
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * Gets the platform version of the device.
     *
     * @return The platform version.
     */
    public String getPlatformVersion() {
        return platformVersion;
    }

    /**
     * Sets the platform version of the device.
     *
     * @param platformVersion The platform version to set.
     */
    public void setPlatformVersion(String platformVersion) {
        this.platformVersion = platformVersion;
    }

    /**
     * Gets the ICCID of the device.
     *
     * @return The ICCID.
     */
    public String getIccid() {
        return iccid;
    }

    /**
     * Sets the ICCID of the device.
     *
     * @param iccid The ICCID to set.
     */
    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    /**
     * Gets the SSID of the device.
     *
     * @return The SSID.
     */
    public String getSsid() {
        return ssid;
    }

    /**
     * Sets the SSID of the device.
     *
     * @param ssid The SSID to set.
     */
    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    /**
     * Gets the BSSID of the device.
     *
     * @return The BSSID.
     */
    public String getBssid() {
        return bssid;
    }

    /**
     * Sets the BSSID of the device.
     *
     * @param bssid The BSSID to set.
     */
    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    /**
     * Gets the MSISDN of the device.
     *
     * @return The MSISDN.
     */
    public String getMsisdn() {
        return msisdn;
    }

    /**
     * Sets the MSISDN of the device.
     *
     * @param msisdn The MSISDN to set.
     */
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    /**
     * Gets the IMSI of the device.
     *
     * @return The IMSI.
     */
    public String getImsi() {
        return imsi;
    }

    /**
     * Sets the IMSI of the device.
     *
     * @param imsi The IMSI to set.
     */
    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    /**
     * Gets the record date of the device.
     *
     * @return The record date.
     */
    public String getRecordDate() {
        return recordDate;
    }

    /**
     * Sets the record date of the device.
     *
     * @param recordDate The record date to set.
     */
    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    /**
     * Gets the factory admin of the device.
     *
     * @return The factory admin.
     */
    public String getFactoryAdmin() {
        return factoryAdmin;
    }

    /**
     * Sets the factory admin of the device.
     *
     * @param factoryAdmin The factory admin to set.
     */
    public void setFactoryAdmin(String factoryAdmin) {
        this.factoryAdmin = factoryAdmin;
    }

    /**
     * Gets the package serial number of the device.
     *
     * @return The package serial number.
     */
    public String getPackageSerialNumber() {
        return packageSerialNumber;
    }

    /**
     * Sets the package serial number of the device.
     *
     * @param packageSerialNumber The package serial number to set.
     */
    public void setPackageSerialNumber(String packageSerialNumber) {
        this.packageSerialNumber = packageSerialNumber;
    }

    /**
     * Gets the VIN of the device.
     *
     * @return The VIN.
     */
    public String getVin() {
        return vin;
    }

    /**
     * Sets the VIN of the device.
     *
     * @param vin The VIN to set.
     */
    public void setVin(String vin) {
        this.vin = vin;
    }

    /**
     * Gets the chassis number of the device.
     *
     * @return The chassis number.
     */
    public String getChassisNumber() {
        return chassisNumber;
    }

    /**
     * Sets the chassis number of the device.
     *
     * @param chassisNumber The chassis number to set.
     */
    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }

    /**
     * Gets the plant of the device.
     *
     * @return The plant.
     */
    public String getPlant() {
        return plant;
    }

    /**
     * Sets the plant of the device.
     *
     * @param plant The plant to set.
     */
    public void setPlant(String plant) {
        this.plant = plant;
    }

    /**
     * Gets the production week of the device.
     *
     * @return The production week.
     */
    public String getProductionWeek() {
        return productionWeek;
    }

    /**
     * Sets the production week of the device.
     *
     * @param productionWeek The production week to set.
     */
    public void setProductionWeek(String productionWeek) {
        this.productionWeek = productionWeek;
    }

    /**
     * Gets the vehicle model year of the device.
     *
     * @return The vehicle model year.
     */
    public String getVehicleModelYear() {
        return vehicleModelYear;
    }

    /**
     * Sets the vehicle model year of the device.
     *
     * @param vehicleModelYear The vehicle model year to set.
     */
    public void setVehicleModelYear(String vehicleModelYear) {
        this.vehicleModelYear = vehicleModelYear;
    }

    /**
     * Gets the friendly name of the device.
     *
     * @return The friendly name.
     */
    public String getFriendlyName() {
        return friendlyName;
    }

    /**
     * Sets the friendly name of the device.
     *
     * @param friendlyName The friendly name to set.
     */
    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    /**
     * Returns a string representation of the DeviceData object.
     *
     * @return The string representation.
     */
    @Override
    public String toString() {
        return "DeviceData{" 
            +            "factoryAdmin='" + factoryAdmin + '\'' 
            +            ", manufacturingDate='" + manufacturingDate + '\'' 
            +            ", model='" + model + '\'' 
            +            ", imei='" + imei + '\'' 
            +            ", serialNumber='" + serialNumber + '\'' 
            +            ", platformVersion='" + platformVersion + '\'' 
            +            ", iccid='" + iccid + '\'' 
            +            ", ssid='" + ssid + '\'' 
            +            ", bssid='" + bssid + '\'' 
            +            ", msisdn='" + msisdn + '\'' 
            +            ", imsi='" + imsi + '\'' 
            +            ", recordDate='" + recordDate + '\'' 
            +            ", packageSerialNumber='" + packageSerialNumber + '\'' 
            +            ", vin='" + vin + '\'' 
            +            ", chassisNumber='" + chassisNumber + '\'' 
            +            ", plant='" + plant + '\'' 
            +            ", productionWeek='" + productionWeek + '\'' 
            +            ", vehicleModelYear='" + vehicleModelYear + '\'' 
            +            ", friendlyName='" + friendlyName + '\'' 
            +            '}';
    }
}
