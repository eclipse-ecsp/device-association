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
import java.util.ArrayList;

/**
 * Represents the result of a device item.
 */
public class DeviceItemResult implements Serializable {

    static final long serialVersionUID = 1L;

    private ArrayList<DeviceItemStatus> deviceInfoStatusList = new ArrayList<>();
    private boolean exceptionOccured;

    /**
     * Gets the list of device information statuses.
     *
     * @return The list of device information statuses.
     */
    public ArrayList<DeviceItemStatus> getDeviceInfoStatusList() {
        return deviceInfoStatusList;
    }

    /**
     * Sets the list of device information statuses.
     *
     * @param deviceInfoStatusList The list of device information statuses.
     */
    public void setDeviceInfoStatusList(ArrayList<DeviceItemStatus> deviceInfoStatusList) {
        this.deviceInfoStatusList = deviceInfoStatusList;
    }

    /**
     * Checks if an exception occurred.
     *
     * @return True if an exception occurred, false otherwise.
     */
    public boolean isExceptionOccured() {
        return exceptionOccured;
    }

    /**
     * Sets whether an exception occurred.
     *
     * @param exceptionOccured True if an exception occurred, false otherwise.
     */
    public void setExceptionOccured(boolean exceptionOccured) {
        this.exceptionOccured = exceptionOccured;
    }
}