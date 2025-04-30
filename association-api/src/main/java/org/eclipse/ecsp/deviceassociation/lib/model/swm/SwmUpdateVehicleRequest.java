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

package org.eclipse.ecsp.deviceassociation.lib.model.swm;

import java.util.Map;

/**
 * Represents a request to update a vehicle in the SWM system.
 */
public class SwmUpdateVehicleRequest extends SwmRequest {
    /**
     * Mandatory. The chassis number of the vehicle.
     */
    private String chassisNumber;
    private String displayName;
    private String domainId;
    /**
     * Mandatory. The ID of the vehicle.
     */
    private String id;
    private String msisdn;
    private String plant;
    /**
     * Mandatory. The production week of the vehicle.
     */
    private String productionWeek;
    private Map<String, String> specificAttributes;
    private String supplementaryId;
    private String vehicleStatus;
    private String vin;

    /**
     * Retrieves the chassis number of the vehicle.
     *
     * @return The chassis number of the vehicle.
     */
    public String getChassisNumber() {
        return chassisNumber;
    }

    /**
     * Sets the chassis number of the vehicle.
     *
     * @param chassisNumber The chassis number of the vehicle.
     */
    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }

    /**
     * Retrieves the display name of the vehicle.
     *
     * @return The display name of the vehicle.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the display name of the vehicle.
     *
     * @param displayName The display name of the vehicle.
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Retrieves the domain ID of the vehicle.
     *
     * @return The domain ID of the vehicle.
     */
    public String getDomainId() {
        return domainId;
    }

    /**
     * Sets the domain ID of the vehicle.
     *
     * @param domainId The domain ID of the vehicle.
     */
    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    /**
     * Retrieves the ID of the vehicle.
     *
     * @return The ID of the vehicle.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the vehicle.
     *
     * @param id The ID of the vehicle.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Retrieves the MSISDN of the vehicle.
     *
     * @return The MSISDN of the vehicle.
     */
    public String getMsisdn() {
        return msisdn;
    }

    /**
     * Sets the MSISDN of the vehicle.
     *
     * @param msisdn The MSISDN of the vehicle.
     */
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    /**
     * Retrieves the plant of the vehicle.
     *
     * @return The plant of the vehicle.
     */
    public String getPlant() {
        return plant;
    }

    /**
     * Sets the plant of the vehicle.
     *
     * @param plant The plant of the vehicle.
     */
    public void setPlant(String plant) {
        this.plant = plant;
    }

    /**
     * Retrieves the production week of the vehicle.
     *
     * @return The production week of the vehicle.
     */
    public String getProductionWeek() {
        return productionWeek;
    }

    /**
     * Sets the production week of the vehicle.
     *
     * @param productionWeek The production week of the vehicle.
     */
    public void setProductionWeek(String productionWeek) {
        this.productionWeek = productionWeek;
    }

    /**
     * Retrieves the specific attributes of the vehicle.
     *
     * @return The specific attributes of the vehicle.
     */
    public Map<String, String> getSpecificAttributes() {
        return specificAttributes;
    }

    /**
     * Sets the specific attributes of the vehicle.
     *
     * @param specificAttributes The specific attributes of the vehicle.
     */
    public void setSpecificAttributes(Map<String, String> specificAttributes) {
        this.specificAttributes = specificAttributes;
    }

    /**
     * Retrieves the supplementary ID of the vehicle.
     *
     * @return The supplementary ID of the vehicle.
     */
    public String getSupplementaryId() {
        return supplementaryId;
    }

    /**
     * Sets the supplementary ID of the vehicle.
     *
     * @param supplementaryId The supplementary ID of the vehicle.
     */
    public void setSupplementaryId(String supplementaryId) {
        this.supplementaryId = supplementaryId;
    }

    /**
     * Retrieves the status of the vehicle.
     *
     * @return The status of the vehicle.
     */
    public String getVehicleStatus() {
        return vehicleStatus;
    }

    /**
     * Sets the status of the vehicle.
     *
     * @param vehicleStatus The status of the vehicle.
     */
    public void setVehicleStatus(String vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    /**
     * Retrieves the VIN of the vehicle.
     *
     * @return The VIN of the vehicle.
     */
    public String getVin() {
        return vin;
    }

    /**
     * Sets the VIN of the vehicle.
     *
     * @param vin The VIN of the vehicle.
     */
    public void setVin(String vin) {
        this.vin = vin;
    }

    /**
     * Returns a string representation of the SwmUpdateVehicleRequest object.
     *
     * @return A string representation of the SwmUpdateVehicleRequest object.
     */
    @Override
    public String toString() {
        return "SwmUpdateVehicleRequest{" 
            +            "chassisNumber='" + chassisNumber + '\'' 
            +            ", displayName='" + displayName + '\'' 
            +            ", domainId='" + domainId + '\'' 
            +            ", id='" + id + '\'' 
            +            ", msisdn='" + msisdn + '\'' 
            +            ", plant='" + plant + '\'' 
            +            ", productionWeek='" + productionWeek + '\'' 
            +            ", specificAttributes=" + specificAttributes 
            +            ", supplementaryId='" + supplementaryId + '\'' 
            +            ", vehicleStatus='" + vehicleStatus + '\'' 
            +            ", vin='" + vin + '\'' 
            +            '}';
    }
}
