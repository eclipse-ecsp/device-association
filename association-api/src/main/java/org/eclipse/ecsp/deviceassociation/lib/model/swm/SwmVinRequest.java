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

/**
 * Represents a VIN (Vehicle Identification Number) request for the SWM (Software Management) system.
 * This class extends the SwmRequest class and provides methods to get and set the VIN.
 */
public class SwmVinRequest extends SwmRequest {
    private String vin;

    /**
     * Gets the VIN.
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
     * Returns a string representation of the SwmVinRequest object.
     *
     * @return A string representation of the SwmVinRequest object.
     */
    @Override
    public String toString() {
        return "SwmVinRequest{" 
            +            "vin='" + vin + '\'' 
            +            '}';
    }
}
