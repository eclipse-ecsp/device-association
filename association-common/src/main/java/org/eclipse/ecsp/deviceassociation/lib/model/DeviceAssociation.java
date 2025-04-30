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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.ecsp.deviceassociation.lib.util.DateTimeUtils;
import org.eclipse.ecsp.services.shared.deviceinfo.model.DeviceAttributes;

import java.sql.Timestamp;
import java.util.Map;

import static org.eclipse.ecsp.common.CommonConstants.ID;

/**
 * Represents a device association.
 * This class contains information about the association between a device and a user.
 */
@JsonInclude(Include.NON_NULL)
public class DeviceAssociation {

    /**
     * id | bigint | not null default.
     * nextval('device_association_id_seq'::regclass) | plain | | serial_number
     * | character varying | not null | extended | | user_id | character varying
     * | not null | extended | | harmanid | character varying | | extended | |
     * association_status | character varying | not null | extended | |
     * associated_on | timestamp with time zone | not null | plain | |
     * associated_by | character varying | not null | extended | |
     * disassociated_on | timestamp with time zone | | plain | |
     * disassociated_by | character varying | | extended | | modified_on |
     * timestamp with time zone | | plain | | modified_by | character varying |
     */

    private long id;
    private String serialNumber;
    @JsonIgnore
    private String userId;
    private String harmanId;
    private String vehicleId;
    private AssociationStatus associationStatus;
    private Timestamp associatedOn;
    @JsonIgnore
    private String associatedBy;
    private Timestamp disassociatedOn;
    @JsonIgnore
    private String disassociatedBy;
    @JsonIgnore
    private Timestamp modifiedOn;
    @JsonIgnore
    private String modifiedBy;
    private DeviceAttributes deviceAttributes;
    @JsonIgnore
    private long factoryId;
    // temporary to avoid making calls to AUTH.
    @JsonIgnore
    private boolean isAuthsRequest;
    private String associationType;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private long startTimeStamp;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private long endTimeStamp;

    private String imei;
    private String imsi;
    private String ssid;
    private String iccid;
    private String msisdn;
    private String bssid;
    @JsonIgnore
    private String model;
    @JsonIgnore
    private Timestamp manufacturingDate;
    @JsonIgnore
    private String platformVersion;
    @JsonIgnore
    private Timestamp recordDate;
    @JsonIgnore
    private boolean isDeviceAuthV2Deactivate;

    private String softwareVersion;
    private String vin;

    private String simTranStatus;
    private String deviceType;
    private String terminateFor;
    private Map<String, Object> metadata;

    /**
     * Returns the value of the terminateFor property.
     *
     * @return the value of the terminateFor property
     */
    public String getTerminateFor() {
        return terminateFor;
    }

    /**
     * Sets the termination reason for the device association.
     *
     * @param terminateFor the termination reason for the device association
     */
    public void setTerminateFor(String terminateFor) {
        this.terminateFor = terminateFor;
    }

    /**
     * Gets the SIM transaction status.
     *
     * @return the SIM transaction status
     */
    public String getSimTranStatus() {
        return simTranStatus;
    }

    /**
     * Sets the SIM transaction status for the device.
     *
     * @param simTranStatus the SIM transaction status to set
     */
    public void setSimTranStatus(String simTranStatus) {
        this.simTranStatus = simTranStatus;
    }

    /**
     * Returns the association ID.
     *
     * @return the association ID
     */
    @JsonProperty("associationId")
    public long getId() {
        return id;
    }

    /**
     * Sets the ID of the device association.
     *
     * @param id the ID to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
    * Gets the serial number of the device.
    *
    * @return The serial number of the device.
    */
    @JsonProperty("serialNumber")
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
     * Retrieves the user ID associated with the device association.
     *
     * @return The user ID.
     */
    @JsonIgnore
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user ID for the device association.
     *
     * @param userId the user ID to set
     */
    @JsonIgnore
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Retrieves the Harman ID of the device.
     *
     * @return The Harman ID of the device.
     */
    @JsonProperty("deviceId")
    public String getHarmanId() {
        return harmanId;
    }

    /**
     * Sets the Harman ID of the device.
     *
     * @param harmanId the Harman ID to set
     */
    @JsonProperty("deviceId")
    public void setHarmanId(String harmanId) {
        this.harmanId = harmanId;
    }

    /**
     * Returns the vehicle ID associated with this device association.
     *
     * @return the vehicle ID
     */
    public String getVehicleId() {
        return vehicleId;
    }

    /**
     * Sets the vehicle ID for the device association.
     *
     * @param vehicleId the vehicle ID to set
     */
    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    /**
     * Returns the association status of the device.
     *
     * @return The association status of the device.
     */
    @JsonProperty("associationStatus")
    public AssociationStatus getAssociationStatus() {
        return associationStatus;
    }

    /**
     * Sets the association status of the device.
     *
     * @param associationStatus the association status to be set
     */
    public void setAssociationStatus(AssociationStatus associationStatus) {
        this.associationStatus = associationStatus;
    }

    /**
    * Returns the timestamp when the device was associated.
    *
    * @return The timestamp when the device was associated, or null if it has not been associated yet.
    */
    @JsonIgnore
    public Timestamp getAssociatedOn() {
        if (associatedOn != null) {
            return new Timestamp(associatedOn.getTime());
        } else {
            return null;
        }
    }

    /**
     * Sets the timestamp when the device was associated.
     *
     * @param associatedOn the timestamp when the device was associated
     */
    public void setAssociatedOn(Timestamp associatedOn) {
        if (associatedOn != null) {
            this.associatedOn = new Timestamp(associatedOn.getTime());
        } else {
            this.associatedOn = null;
        }
    }

    /**
     * Returns the formatted associatedOn date as a string.
     *
     * @return The formatted associatedOn date.
     */
    @JsonProperty("associatedOn")
    public String getFormattedAssociatedOn() {
        return DateTimeUtils.getIsoDate(associatedOn);
    }

    /**
     * Returns the user who associated the device.
     *
     * @return the user who associated the device
     */
    @JsonIgnore
    public String getAssociatedBy() {
        return associatedBy;
    }

    /**
     * Sets the value of the associatedBy field.
     *
     * @param associatedBy the value to set for the associatedBy field
     */
    @JsonIgnore
    public void setAssociatedBy(String associatedBy) {
        this.associatedBy = associatedBy;
    }

    /**
     * Retrieves the user who performed the disassociation.
     *
     * @return The user who performed the disassociation.
     */
    @JsonIgnore
    public String getDisassociatedBy() {
        return disassociatedBy;
    }

    /**
     * Sets the user who initiated the disassociation of the device.
     *
     * @param disassociatedBy the user who initiated the disassociation
     */
    @JsonIgnore
    public void setDisassociatedBy(String disassociatedBy) {
        this.disassociatedBy = disassociatedBy;
    }

    /**
     * Retrieves the username of the user who last modified the device association.
     *
     * @return The username of the user who last modified the device association.
     */
    @JsonIgnore
    public String getModifiedBy() {
        return modifiedBy;
    }

    /**
     * Sets the value of the modifiedBy property.
     *
     * @param modifiedBy The new value for the modifiedBy property.
     */
    @JsonIgnore
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    /**
     * Returns the timestamp when the device was disassociated.
     *
     * @return The timestamp when the device was disassociated, or null if it has not been disassociated.
     */
    @JsonIgnore
    public Timestamp getDisassociatedOn() {
        if (disassociatedOn != null) {
            return new Timestamp(disassociatedOn.getTime());
        } else {
            return null;
        }
    }

    /**
     * Sets the timestamp when the device was disassociated.
     *
     * @param disassociatedOn the timestamp when the device was disassociated
     */
    public void setDisassociatedOn(Timestamp disassociatedOn) {
        if (disassociatedOn != null) {
            this.disassociatedOn = new Timestamp(disassociatedOn.getTime());
        } else {
            this.disassociatedOn = null;
        }
    }

    /**
     * Returns the display representation of the disassociatedOn date.
     *
     * @return The display representation of the disassociatedOn date.
     */
    @JsonProperty("disassociatedOn")
    public String getDisplayDisassociatedOn() {
        return DateTimeUtils.getIsoDate(disassociatedOn);
    }

    /**
     * Returns the modified timestamp of the device association.
     *
     * @return The modified timestamp as a {@link Timestamp} object, or null if the modifiedOn field is null.
     */
    @JsonIgnore
    public Timestamp getModifiedOn() {
        if (modifiedOn != null) {
            return new Timestamp(modifiedOn.getTime());
        } else {
            return null;
        }
    }

    /**
     * Sets the modifiedOn timestamp of the device association.
     *
     * @param modifiedOn The modifiedOn timestamp to set. If null, the modifiedOn timestamp will be set to null.
     */
    @JsonIgnore
    public void setModifiedOn(Timestamp modifiedOn) {
        if (modifiedOn != null) {
            this.modifiedOn = new Timestamp(modifiedOn.getTime());
        } else {
            this.modifiedOn = null;
        }
    }

    /**
     * Returns the device attributes associated with this device association.
     *
     * @return the device attributes
     */
    public DeviceAttributes getDeviceAttributes() {
        return deviceAttributes;
    }

    /**
     * Sets the device attributes for this DeviceAssociation.
     *
     * @param deviceAttributes the device attributes to be set
     */
    public void setDeviceAttributes(DeviceAttributes deviceAttributes) {
        this.deviceAttributes = deviceAttributes;
    }

    /**
     * Checks if the request is an authentication request.
     *
     * @return true if the request is an authentication request, false otherwise.
     */
    @JsonIgnore
    public boolean isAuthsRequest() {
        return isAuthsRequest;
    }

    /**
     * Sets whether the request is an authentication request.
     *
     * @param isAuthsRequest true if the request is an authentication request, false otherwise
     */
    @JsonIgnore
    public void setAuthsRequest(boolean isAuthsRequest) {
        this.isAuthsRequest = isAuthsRequest;
    }

    /**
     * Returns the factory ID of the device association.
     *
     * @return the factory ID
     */
    @JsonIgnore
    @JsonProperty(ID)
    public long getFactoryId() {
        return factoryId;
    }

    /**
     * Sets the factory ID of the device association.
     *
     * @param factoryId the factory ID to set
     */
    @JsonIgnore
    public void setFactoryId(long factoryId) {
        this.factoryId = factoryId;
    }

    /**
     * Gets the IMEI (International Mobile Equipment Identity) of the device.
     *
     * @return The IMEI of the device.
     */
    @JsonProperty("imei")
    public String getImei() {
        return imei;
    }

    /**
     * Sets the IMEI (International Mobile Equipment Identity) number of the device.
     *
     * @param imei the IMEI number to set
     */
    public void setImei(String imei) {
        this.imei = imei;
    }

    /**
     * Gets the IMSI (International Mobile Subscriber Identity) of the device.
     *
     * @return The IMSI of the device.
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
     * Returns the SSID (Service Set Identifier) of the device.
     *
     * @return the SSID of the device
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
     * @return The ICCID of the device.
     */
    @JsonProperty("iccid")
    public String getIccid() {
        return iccid;
    }

    /**
     * Sets the ICCID (Integrated Circuit Card Identifier) for the device.
     *
     * @param iccid the ICCID to set
     */
    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    /**
     * Gets the MSISDN (Mobile Station International Subscriber Directory Number) associated with the device.
     *
     * @return The MSISDN associated with the device.
     */
    @JsonProperty("msisdn")
    public String getMsisdn() {
        return msisdn;
    }

    /**
     * Sets the MSISDN (Mobile Station International Subscriber Directory Number) for the device association.
     *
     * @param msisdn the MSISDN to set
     */
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    /**
     * Returns a string representation of the DeviceAssociation object.
     *
     * @return A string representation of the DeviceAssociation object.
     */
    @Override
    public String toString() {
        return "DeviceAssociation [id=" + id + ", serialNumber=" + serialNumber + ", userID=" + userId + ", harmanId=" 
            +            harmanId
            + ", vehicleId=" + vehicleId + ", associationStatus=" + associationStatus + ", associatedOn=" 
            +            associatedOn + ", associatedBy=" + associatedBy
            + ", disassociatedOn=" + disassociatedOn + ", disassociatedBy=" + disassociatedBy + ", modifiedOn=" 
            +            modifiedOn
            + ", modifiedBy=" + modifiedBy + ", deviceAttributes=" + deviceAttributes + ", factory_id=" + factoryId
            + ", isAuthsRequest=" + isAuthsRequest + ", imei=" + imei + ", imsi=" + imsi + ", ssid=" + ssid 
            +            ", iccid=" + iccid
            + ", msisdn=" + msisdn + ", softwareVersion=" + softwareVersion + "]";
    }

    /**
     * Returns the BSSID (Basic Service Set Identifier) of the device.
     *
     * @return the BSSID of the device
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
    @JsonProperty("bssid")
    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    /**
     * Retrieves the model of the device.
     *
     * @return The model of the device.
     */
    @JsonIgnore
    public String getModel() {
        return model;
    }

    /**
     * Sets the model of the device.
     *
     * @param model the model of the device
     */
    @JsonIgnore
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Returns the platform version of the device.
     *
     * @return the platform version of the device
     */
    @JsonIgnore
    public String getPlatformVersion() {
        return platformVersion;
    }

    /**
     * Sets the platform version of the device.
     *
     * @param platformVersion the platform version to set
     */
    @JsonIgnore
    public void setPlatformVersion(String platformVersion) {
        this.platformVersion = platformVersion;
    }

    /**
     * Returns the manufacturing date of the device.
     *
     * @return The manufacturing date as a Timestamp object, or null if the manufacturing date is not set.
     */
    @JsonIgnore
    public Timestamp getManufacturingDate() {
        if (manufacturingDate != null) {
            return new Timestamp(manufacturingDate.getTime());
        } else {
            return null;
        }
    }

    /**
     * Sets the manufacturing date of the device.
     *
     * @param manufacturingDate the manufacturing date to be set
     */
    @JsonIgnore
    public void setManufacturingDate(Timestamp manufacturingDate) {
        if (manufacturingDate != null) {
            this.manufacturingDate = new Timestamp(manufacturingDate.getTime());
        } else {
            this.manufacturingDate = null;
        }
    }

    /**
     * Returns the record date of the device association.
     *
     * @return The record date as a Timestamp object, or null if the record date is null.
     */
    @JsonIgnore
    public Timestamp getRecordDate() {
        if (recordDate != null) {
            return new Timestamp(recordDate.getTime());
        } else {
            return null;
        }
    }

    /**
     * Sets the record date for the device association.
     *
     * @param recordDate the record date to be set
     */
    @JsonIgnore
    public void setRecordDate(Timestamp recordDate) {
        if (recordDate != null) {
            this.recordDate = new Timestamp(recordDate.getTime());
        } else {
            this.recordDate = null;
        }
    }

    /**
     * Checks if the device authentication V2 is deactivated.
     *
     * @return true if the device authentication V2 is deactivated, false otherwise.
     */
    @JsonIgnore
    public boolean isDeviceAuthV2Deactivate() {
        return isDeviceAuthV2Deactivate;
    }

    /**
     * Sets the flag indicating whether the device authentication V2 should be deactivated.
     *
     * @param isDeviceAuthV2Deactivate true if the device authentication V2 should be deactivated, false otherwise
     */
    @JsonIgnore
    public void setDeviceAuthV2Deactivate(boolean isDeviceAuthV2Deactivate) {
        this.isDeviceAuthV2Deactivate = isDeviceAuthV2Deactivate;
    }

    /**
     * Returns the software version of the device.
     *
     * @return the software version of the device
     */
    public String getSoftwareVersion() {
        return softwareVersion;
    }

    /**
     * Sets the software version of the device.
     *
     * @param softwareVersion the software version to set
     */
    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    /**
     * Returns the VIN (Vehicle Identification Number) of the device.
     *
     * @return the VIN of the device
     */
    public String getVin() {
        return vin;
    }

    /**
     * Sets the Vehicle Identification Number (VIN) for the device association.
     *
     * @param vin the VIN to set
     */
    public void setVin(String vin) {
        this.vin = vin;
    }

    /**
     * Returns the device type of the DeviceAssociation.
     *
     * @return the device type of the DeviceAssociation
     */
    public String getDeviceType() {
        return deviceType;
    }

    /**
     * Sets the device type for this DeviceAssociation.
     *
     * @param deviceType the device type to set
     */
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    /**
     * Returns the metadata associated with the device.
     *
     * @return a map containing the metadata of the device
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * Sets the metadata for the device association.
     *
     * @param metadata a map containing the metadata for the device association
     */
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    /**
     * Returns the association type of the device.
     *
     * @return the association type
     */
    public String getAssociationType() {
        return associationType;
    }

    /**
     * Sets the association type for the device.
     *
     * @param associationType the association type to set
     */
    public void setAssociationType(String associationType) {
        this.associationType = associationType;
    }

    /**
     * Returns the start timestamp of the device association.
     *
     * @return the start timestamp of the device association
     */
    public long getStartTimeStamp() {
        return startTimeStamp;
    }

    /**
     * Sets the start timestamp for the device association.
     *
     * @param startTimeStamp the start timestamp to set
     */
    public void setStartTimeStamp(long startTimeStamp) {
        this.startTimeStamp = startTimeStamp;
    }

    /**
     * Returns the end timestamp of the device association.
     *
     * @return the end timestamp of the device association
     */
    public long getEndTimeStamp() {
        return endTimeStamp;
    }

    /**
     * Sets the end timestamp for the device association.
     *
     * @param endTimeStamp the end timestamp to set
     */
    public void setEndTimeStamp(long endTimeStamp) {
        this.endTimeStamp = endTimeStamp;
    }

}