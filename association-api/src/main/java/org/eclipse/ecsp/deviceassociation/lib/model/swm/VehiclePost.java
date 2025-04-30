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
 * Represents a vehicle post object.
 */
public class VehiclePost {

    private String chassisNumber;
    private String domainId;
    private String plant;
    private String productionWeek;
    private Map<String, String> specificAttributes;
    private String vehicleModelId;
    private String friendlyName;
    private String vin;

    /**
     * Gets the chassis number of the vehicle.
     *
     * @return The chassis number.
     */
    public String getChassisNumber() {
        return chassisNumber;
    }

    /**
     * Sets the chassis number of the vehicle.
     *
     * @param chassisNumber The chassis number to set.
     */
    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }

    /**
     * Gets the domain ID of the vehicle.
     *
     * @return The domain ID.
     */
    public String getDomainId() {
        return domainId;
    }

    /**
     * Sets the domain ID of the vehicle.
     *
     * @param domainId The domain ID to set.
     */
    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    /**
     * Gets the plant of the vehicle.
     *
     * @return The plant.
     */
    public String getPlant() {
        return plant;
    }

    /**
     * Sets the plant of the vehicle.
     *
     * @param plant The plant to set.
     */
    public void setPlant(String plant) {
        this.plant = plant;
    }

    /**
     * Gets the production week of the vehicle.
     *
     * @return The production week.
     */
    public String getProductionWeek() {
        return productionWeek;
    }

    /**
     * Sets the production week of the vehicle.
     *
     * @param productionWeek The production week to set.
     */
    public void setProductionWeek(String productionWeek) {
        this.productionWeek = productionWeek;
    }

    /**
     * Gets the specific attributes of the vehicle.
     *
     * @return The specific attributes.
     */
    public Map<String, String> getSpecificAttributes() {
        return specificAttributes;
    }

    /**
     * Sets the specific attributes of the vehicle.
     *
     * @param specificAttributes The specific attributes to set.
     */
    public void setSpecificAttributes(Map<String, String> specificAttributes) {
        this.specificAttributes = specificAttributes;
    }

    /**
     * Gets the vehicle model ID of the vehicle.
     *
     * @return The vehicle model ID.
     */
    public String getVehicleModelId() {
        return vehicleModelId;
    }

    /**
     * Sets the vehicle model ID of the vehicle.
     *
     * @param vehicleModelId The vehicle model ID to set.
     */
    public void setVehicleModelId(String vehicleModelId) {
        this.vehicleModelId = vehicleModelId;
    }

    /**
     * Gets the friendly name of the vehicle.
     *
     * @return The friendly name.
     */
    public String getFriendlyName() {
        return friendlyName;
    }

    /**
     * Sets the friendly name of the vehicle.
     *
     * @param friendlyName The friendly name to set.
     */
    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    /**
     * Gets the VIN (Vehicle Identification Number) of the vehicle.
     *
     * @return The VIN.
     */
    public String getVin() {
        return vin;
    }

    /**
     * Sets the VIN (Vehicle Identification Number) of the vehicle.
     *
     * @param vin The VIN to set.
     */
    public void setVin(String vin) {
        this.vin = vin;
    }

    /**
     * Returns a string representation of the VehiclePost object.
     *
     * @return A string representation of the object.
     */
    @Override
    public String toString() {
        return "VehiclePost{" 
            +            "chassisNumber='" + chassisNumber + '\'' 
            +            ", domainId='" + domainId + '\'' 
            +            ", plant='" + plant + '\'' 
            +            ", productionWeek='" + productionWeek + '\'' 
            +            ", specificAttributes=" + specificAttributes 
            +            ", vehicleModelId='" + vehicleModelId + '\'' 
            +            ", friendlyName='" + friendlyName + '\'' 
            +            ", vin='" + vin + '\'' 
            +            '}';
    }
}