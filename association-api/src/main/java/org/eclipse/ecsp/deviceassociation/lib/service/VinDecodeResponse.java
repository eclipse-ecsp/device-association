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

import java.io.Serializable;

/**
 * Represents a response object for VIN decoding.
 */
public class VinDecodeResponse implements Serializable {
    private String country;
    private String modelCode;
    private String modelName;
    private String manufacture;

    /**
     * Gets the country of the vehicle.
     *
     * @return The country of the vehicle.
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the country of the vehicle.
     *
     * @param country The country of the vehicle.
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Gets the model code of the vehicle.
     *
     * @return The model code of the vehicle.
     */
    public String getModelCode() {
        return modelCode;
    }

    /**
     * Sets the model code of the vehicle.
     *
     * @param modelCode The model code of the vehicle.
     */
    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    /**
     * Gets the model name of the vehicle.
     *
     * @return The model name of the vehicle.
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * Sets the model name of the vehicle.
     *
     * @param modelName The model name of the vehicle.
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * Gets the manufacturer of the vehicle.
     *
     * @return The manufacturer of the vehicle.
     */
    public String getManufacture() {
        return manufacture;
    }

    /**
     * Sets the manufacturer of the vehicle.
     *
     * @param manufacture The manufacturer of the vehicle.
     */
    public void setManufacture(String manufacture) {
        this.manufacture = manufacture;
    }

    /**
     * Returns a string representation of the VinDecodeResponse object.
     *
     * @return A string representation of the VinDecodeResponse object.
     */
    @Override
    public String toString() {
        return "VinDecodeResponse{"
            + "country='" + country + '\''
            + ", modelCode='" + modelCode + '\''
            + ", modelName='" + modelName + '\''
            + ", manufacture='" + manufacture + '\''
            + '}';
    }
}
