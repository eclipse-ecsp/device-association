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

import java.io.Serializable;
import java.util.List;

/**
 * Represents a collection of vehicle IDs.
 */
public class SwmVehicleIds implements Serializable {
    private List<String> vehicleIds;

    /**
     * Retrieves the list of vehicle IDs.
     *
     * @return The list of vehicle IDs.
     */
    public List<String> getVehicleIds() {
        return vehicleIds;
    }

    /**
     * Sets the list of vehicle IDs.
     *
     * @param vehicleIds The list of vehicle IDs.
     */
    public void setVehicleIds(List<String> vehicleIds) {
        this.vehicleIds = vehicleIds;
    }
}
