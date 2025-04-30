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

import java.util.List;

/**
 * Represents a request for SWM vehicles.
 */
public class SwmVehiclesRequest extends SwmRequest {
    private static final long serialVersionUID = 1;
    private transient List<VehiclePost> vehiclePost;

    /**
     * Gets the list of vehicle posts.
     *
     * @return The list of vehicle posts.
     */
    public List<VehiclePost> getVehiclePost() {
        return vehiclePost;
    }

    /**
     * Sets the list of vehicle posts.
     *
     * @param vehiclePost The list of vehicle posts.
     */
    public void setVehiclePost(List<VehiclePost> vehiclePost) {
        this.vehiclePost = vehiclePost;
    }

    /**
     * Returns a string representation of the SwmVehiclesRequest object.
     *
     * @return A string representation of the SwmVehiclesRequest object.
     */
    @Override
    public String toString() {
        return "SwmVehiclesRequest{" 
            +            "vehiclePost=" + vehiclePost 
            +            '}';
    }
}
