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
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a Device Item Data Transfer Object.
 * This class is used to transfer device item data between different layers of the application.
 */
public class DeviceItemDto implements Serializable {

    private static final long serialVersionUID = 1L;
    private ArrayList<@Valid DeviceItems> data;

    /**
     * Gets the list of device items.
     *
     * @return The list of device items.
     */
    public ArrayList<DeviceItems> getData() {
        return data;
    }

    /**
     * Sets the list of device items.
     *
     * @param data The list of device items.
     */
    public void setData(ArrayList<DeviceItems> data) {
        this.data = data;
    }

    /**
     * Returns a string representation of the DeviceItemDto object.
     *
     * @return A string representation of the DeviceItemDto object.
     */
    @Override
    public String toString() {
        return "DeviceInfoRequest [data=" + data + "]";
    }
}