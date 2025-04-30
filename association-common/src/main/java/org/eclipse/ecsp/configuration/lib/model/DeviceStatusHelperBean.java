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

package org.eclipse.ecsp.configuration.lib.model;

/**
 * The DeviceStatusHelperBean class represents a helper bean for device status information.
 * It contains properties for the Harman ID, request ID, and configuration ID of a device.
 */
public class DeviceStatusHelperBean {

    private String harmanId;
    private Long requestId;

    //notification / configuration Id
    private Long id;

    /**
     * Gets the Harman ID of the device.
     *
     * @return The Harman ID of the device.
     */
    public String getHarmanId() {
        return harmanId;
    }

    /**
     * Sets the Harman ID of the device.
     *
     * @param harmanId The Harman ID of the device.
     */
    public void setHarmanId(String harmanId) {
        this.harmanId = harmanId;
    }

    /**
     * Gets the request ID associated with the device.
     *
     * @return The request ID associated with the device.
     */
    public Long getRequestId() {
        return requestId;
    }

    /**
     * Sets the request ID associated with the device.
     *
     * @param requestId The request ID associated with the device.
     */
    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    /**
     * Gets the ID of the device.
     *
     * @return The ID of the device.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the device.
     *
     * @param id The ID of the device.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns a string representation of the DeviceStatusHelperBean object.
     *
     * @return A string representation of the DeviceStatusHelperBean object.
     */
    @Override
    public String toString() {
        return "DeviceStatusHelperBean [harmanId=" + harmanId + ", requestId="
            + requestId + ", Id=" + id + "]";
    }

}
