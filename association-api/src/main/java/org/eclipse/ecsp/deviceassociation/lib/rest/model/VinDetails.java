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

import jakarta.validation.constraints.NotBlank;
import org.eclipse.ecsp.deviceassociation.lib.rest.support.NullOrNotEmpty;
import java.io.Serializable;

/**
 * Represents the details of a VIN (Vehicle Identification Number).
 */
public class VinDetails implements Serializable {

    @NotBlank(message = "vin is required and not allowed to be empty")
    private String vin;
    @NotBlank(message = "imei is required and not allowed to be empty")
    private String imei;
    @NullOrNotEmpty(message = "region is not allowed to be empty")
    private String region;

    /**
     * Retrieves the VIN.
     *
     * @return The VIN.
     */
    public String getVin() {
        return vin;
    }

    /**
     * Sets the VIN.
     *
     * @param vin The VIN to set.
     */
    public void setVin(String vin) {
        this.vin = vin;
    }

    /**
     * Retrieves the IMEI number.
     *
     * @return The IMEI number.
     */
    public String getImei() {
        return imei;
    }

    /**
     * Sets the IMEI number.
     *
     * @param imei The IMEI number to set.
     */
    public void setImei(String imei) {
        this.imei = imei;
    }

    /**
     * Retrieves the region associated with the VIN.
     *
     * @return The region associated with the VIN.
     */
    public String getRegion() {
        return region;
    }

    /**
     * Sets the region associated with the VIN.
     *
     * @param region The region to set.
     */
    public void setRegion(String region) {
        this.region = region;
    }
}