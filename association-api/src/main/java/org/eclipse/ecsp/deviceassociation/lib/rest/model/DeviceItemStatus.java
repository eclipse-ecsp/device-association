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

import java.io.Serializable;

/**
 * Represents the status of a device item.
 */
public class DeviceItemStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    private String deviceId;
    private String status;

    /**
     * Constructor for creating a new DeviceItemStatus object.
     *
     * @param deviceId The ID of the device.
     * @param status The status of the device.
     */
    public DeviceItemStatus(String deviceId, String status) {
        super();
        this.deviceId = deviceId;
        this.status = status;
    }

    /**
     * Gets the ID of the device.
     *
     * @return The device ID.
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Sets the ID of the device.
     *
     * @param deviceId The device ID to set.
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Gets the status of the device.
     *
     * @return The device status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the device.
     *
     * @param status The device status to set.
     */
    public void setStatus(String status) {
        this.status = status;
    }

}