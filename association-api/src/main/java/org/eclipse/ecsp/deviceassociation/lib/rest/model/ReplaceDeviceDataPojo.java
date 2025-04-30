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

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import org.eclipse.ecsp.deviceassociation.lib.rest.support.NullOrNotEmpty;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

/**
 * Represents the data for replacing a device.
 */
@JsonInclude(NON_EMPTY)
public class ReplaceDeviceDataPojo {

    @NullOrNotEmpty(message = "manufacturingDate is not allowed be empty")
    private String manufacturingDate;
    @NullOrNotEmpty(message = "model is not allowed be empty")
    private String model;
    @NullOrNotEmpty(message = "imei is not allowed be empty")
    private String imei;
    @NotBlank(message = "serialNumber is required not allowed be empty")
    private String serialNumber;
    @NullOrNotEmpty(message = "platformVersion is not allowed be empty")
    private String platformVersion;
    @NullOrNotEmpty(message = "iccid is not allowed be empty")
    private String iccid;
    @NullOrNotEmpty(message = "ssid is not allowed be empty")
    private String ssid;
    @NullOrNotEmpty(message = "bssid is not allowed be empty")
    private String bssid;
    @NullOrNotEmpty(message = "msisdn is not allowed be empty")
    private String msisdn;
    @NullOrNotEmpty(message = "imsi is not allowed be empty")
    private String imsi;
    @NullOrNotEmpty(message = "recordDate is not allowed be empty")
    private String recordDate;
    @NullOrNotEmpty(message = "packageSerialNumber is not allowed be empty")
    private String packageSerialNumber;

    private String vin;
    @NullOrNotEmpty(message = "chassisNumber is not allowed be empty")
    private String chassisNumber;
    private String state;
    @NullOrNotEmpty(message = "plant is not allowed be empty")
    private String plant;
    @NullOrNotEmpty(message = "productionWeek is not allowed be empty")
    private String productionWeek;
    @NullOrNotEmpty(message = "vehicleModelYear is not allowed be empty")
    private String vehicleModelYear;
    @NullOrNotEmpty(message = "friendlyName is not allowed be empty")
    private String friendlyName;

    /**
     * Constructs a new ReplaceDeviceDataPojo object.
     */
    public ReplaceDeviceDataPojo() {

    }

    /**
     * Get the IMEI (International Mobile Equipment Identity) of the device.
     *
     * @return The IMEI of the device.
     */
    public String getImei() {
        return imei;
    }

    /**
     * Set the IMEI (International Mobile Equipment Identity) of the device.
     *
     * @param imei The IMEI of the device.
     */
    public void setImei(String imei) {
        this.imei = imei;
    }

    /**
     * Get the serial number of the device.
     *
     * @return The serial number of the device.
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Set the serial number of the device.
     *
     * @param serialNumber The serial number of the device.
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * Get the ICCID (Integrated Circuit Card Identifier) of the device.
     *
     * @return The ICCID of the device.
     */
    public String getIccid() {
        return iccid;
    }

    /**
     * Set the ICCID (Integrated Circuit Card Identifier) of the device.
     *
     * @param iccid The ICCID of the device.
     */
    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    /**
     * Get the BSSID (Basic Service Set Identifier) of the device.
     *
     * @return The BSSID of the device.
     */
    public String getBssid() {
        return bssid;
    }

    /**
     * Set the BSSID (Basic Service Set Identifier) of the device.
     *
     * @param bssid The BSSID of the device.
     */
    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    /**
     * Get the MSISDN (Mobile Station International Subscriber Directory Number) of the device.
     *
     * @return The MSISDN of the device.
     */
    public String getMsisdn() {
        return msisdn;
    }

    /**
     * Set the MSISDN (Mobile Station International Subscriber Directory Number) of the device.
     *
     * @param msisdn The MSISDN of the device.
     */
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    /**
     * Get the IMSI (International Mobile Subscriber Identity) of the device.
     *
     * @return The IMSI of the device.
     */
    public String getImsi() {
        return imsi;
    }

    /**
     * Set the IMSI (International Mobile Subscriber Identity) of the device.
     *
     * @param imsi The IMSI of the device.
     */
    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    /**
     * Get the manufacturing date of the device.
     *
     * @return The manufacturing date of the device.
     */
    public String getManufacturingDate() {
        return manufacturingDate;
    }

    /**
     * Set the manufacturing date of the device.
     *
     * @param manufacturingDate The manufacturing date of the device.
     */
    public void setManufacturingDate(String manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
    }

    /**
     * Get the model of the device.
     *
     * @return The model of the device.
     */
    public String getModel() {
        return model;
    }

    /**
     * Set the model of the device.
     *
     * @param model The model of the device.
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Get the platform version of the device.
     *
     * @return The platform version of the device.
     */
    public String getPlatformVersion() {
        return platformVersion;
    }

    /**
     * Set the platform version of the device.
     *
     * @param platformVersion The platform version of the device.
     */
    public void setPlatformVersion(String platformVersion) {
        this.platformVersion = platformVersion;
    }

    /**
     * Get the SSID (Service Set Identifier) of the device.
     *
     * @return The SSID of the device.
     */
    public String getSsid() {
        return ssid;
    }

    /**
     * Set the SSID (Service Set Identifier) of the device.
     *
     * @param ssid The SSID of the device.
     */
    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    /**
     * Get the record date of the device.
     *
     * @return The record date of the device.
     */
    public String getRecordDate() {
        return recordDate;
    }

    /**
     * Set the record date of the device.
     *
     * @param recordDate The record date of the device.
     */
    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    /**
     * Get the package serial number of the device.
     *
     * @return The package serial number of the device.
     */
    public String getPackageSerialNumber() {
        return packageSerialNumber;
    }

    /**
     * Set the package serial number of the device.
     *
     * @param packageSerialNumber The package serial number of the device.
     */
    public void setPackageSerialNumber(String packageSerialNumber) {
        this.packageSerialNumber = packageSerialNumber;
    }

    /**
     * Get the VIN (Vehicle Identification Number) of the device.
     *
     * @return The VIN of the device.
     */
    public String getVin() {
        return vin;
    }

    /**
     * Set the VIN (Vehicle Identification Number) of the device.
     *
     * @param vin The VIN of the device.
     */
    public void setVin(String vin) {
        this.vin = vin;
    }

    /**
     * Get the chassis number of the device.
     *
     * @return The chassis number of the device.
     */
    public String getChassisNumber() {
        return chassisNumber;
    }

    /**
     * Set the chassis number of the device.
     *
     * @param chassisNumber The chassis number of the device.
     */
    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }

    /**
     * Get the state of the device.
     *
     * @return The state of the device.
     */
    public String getState() {
        return state;
    }

    /**
     * Set the state of the device.
     *
     * @param state The state of the device.
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Get the plant of the device.
     *
     * @return The plant of the device.
     */
    public String getPlant() {
        return plant;
    }

    /**
     * Set the plant of the device.
     *
     * @param plant The plant of the device.
     */
    public void setPlant(String plant) {
        this.plant = plant;
    }

    /**
     * Get the production week of the device.
     *
     * @return The production week of the device.
     */
    public String getProductionWeek() {
        return productionWeek;
    }

    /**
     * Set the production week of the device.
     *
     * @param productionWeek The production week of the device.
     */
    public void setProductionWeek(String productionWeek) {
        this.productionWeek = productionWeek;
    }

    /**
     * Get the vehicle model year of the device.
     *
     * @return The vehicle model year of the device.
     */
    public String getVehicleModelYear() {
        return vehicleModelYear;
    }

    /**
     * Set the vehicle model year of the device.
     *
     * @param vehicleModelYear The vehicle model year of the device.
     */
    public void setVehicleModelYear(String vehicleModelYear) {
        this.vehicleModelYear = vehicleModelYear;
    }

    /**
     * Get the friendly name of the device.
     *
     * @return The friendly name of the device.
     */
    public String getFriendlyName() {
        return friendlyName;
    }

    /**
     * Set the friendly name of the device.
     *
     * @param friendlyName The friendly name of the device.
     */
    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    @Override
    public String toString() {
        return "ReplaceDeviceDataPojo{" 
            + "manufacturingDate='" + manufacturingDate + '\'' 
            + ", model='" + model + '\'' 
            + ", imei='" + imei + '\'' 
            + ", serialNumber='" + serialNumber + '\'' 
            + ", platformVersion='" + platformVersion + '\'' 
            + ", iccid='" + iccid + '\'' 
            + ", ssid='" + ssid + '\'' 
            + ", bssid='" + bssid + '\'' 
            + ", msisdn='" + msisdn + '\'' 
            + ", imsi='" + imsi + '\'' 
            + ", recordDate='" + recordDate + '\'' 
            + ", packageSerialNumber='" + packageSerialNumber + '\'' 
            + ", vin='" + vin + '\'' 
            + ", chassisNumber='" + chassisNumber + '\'' 
            + ", plant='" + plant + '\'' 
            + ", productionWeek='" + productionWeek + '\'' 
            + ", vehicleModelYear='" + vehicleModelYear + '\'' 
            + ", friendlyName='" + friendlyName + '\'' 
            + '}';
    }
}
