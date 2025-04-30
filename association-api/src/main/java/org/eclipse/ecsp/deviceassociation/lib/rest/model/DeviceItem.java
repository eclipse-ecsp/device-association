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

import org.eclipse.ecsp.deviceassociation.lib.rest.support.NullOrNotEmpty;

import java.io.Serializable;

/**
 * Represents a device item.
 */
public class DeviceItem implements Serializable {

    private static final long serialVersionUID = 1L;
    @NullOrNotEmpty(message = "name is required and not allowed to be empty")
    private String name;
    @NullOrNotEmpty(message = "value is required and not allowed to be empty")
    private Object value;

    /**
     * Gets the name of the device item.
     *
     * @return The name of the device item.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the device item.
     *
     * @param name The name of the device item.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the value of the device item.
     *
     * @return The value of the device item.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Sets the value of the device item.
     *
     * @param value The value of the device item.
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * Returns a string representation of the device item.
     *
     * @return A string representation of the device item.
     */
    @Override
    public String toString() {
        return "DeviceInfo [name=" + name + ", value=" + value + "]";
    }
}