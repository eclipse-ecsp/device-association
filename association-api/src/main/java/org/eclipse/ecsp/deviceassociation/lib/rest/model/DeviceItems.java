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

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a collection of device items.
 */
public class DeviceItems implements Serializable {

    static final long serialVersionUID = 1L;

    @NotBlank(message = "deviceId is required and not allowed to be empty")
    private String deviceId;
    private ArrayList<@Valid DeviceItem> items;

    /**
     * Gets the device ID.
     *
     * @return The device ID.
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Sets the device ID.
     *
     * @param deviceId The device ID to set.
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Gets the list of device items.
     *
     * @return The list of device items.
     */
    public ArrayList<DeviceItem> getItems() {
        return items;
    }

    /**
     * Sets the list of device items.
     *
     * @param items The list of device items to set.
     */
    public void setItems(ArrayList<DeviceItem> items) {
        this.items = items;
    }

    /**
     * Returns a string representation of the DeviceItems object.
     *
     * @return A string representation of the DeviceItems object.
     */
    @Override
    public String toString() {
        return "DeviceInfoList [deviceId=" + deviceId + ", items=" + items + "]";
    }
}