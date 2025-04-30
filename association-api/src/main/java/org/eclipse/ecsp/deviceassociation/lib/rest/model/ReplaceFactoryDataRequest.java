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

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import org.eclipse.ecsp.deviceassociation.lib.rest.support.NullOrNotEmpty;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

/**
 * Represents a request to replace factory data for a device.
 */
@JsonInclude(NON_EMPTY)
public class ReplaceFactoryDataRequest {

    @NullOrNotEmpty(message = "serialNumber is not allowed to be empty")
    String serialNumber;
    @Valid
    CurrentDeviceDataPojo currentValue;
    @Valid
    ReplaceDeviceDataPojo replaceWith;

    /**
     * Constructs a new ReplaceFactoryDataRequest object.
     */
    public ReplaceFactoryDataRequest() {

    }

    /**
     * Gets the current value of the device data.
     *
     * @return The current value of the device data.
     */
    public CurrentDeviceDataPojo getCurrentValue() {
        return currentValue;
    }

    /**
     * Sets the current value of the device data.
     *
     * @param currentValue The current value of the device data.
     */
    public void setCurrentValue(CurrentDeviceDataPojo currentValue) {
        this.currentValue = currentValue;
    }

    /**
     * Gets the device data to replace with.
     *
     * @return The device data to replace with.
     */
    public ReplaceDeviceDataPojo getReplaceWith() {
        return replaceWith;
    }

    /**
     * Sets the device data to replace with.
     *
     * @param replaceWith The device data to replace with.
     */
    public void setReplaceWith(ReplaceDeviceDataPojo replaceWith) {
        this.replaceWith = replaceWith;
    }

    /**
     * Gets the serial number of the device.
     *
     * @return The serial number of the device.
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the serial number of the device.
     *
     * @param serialNumber The serial number of the device.
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * Returns a string representation of the ReplaceFactoryDataRequest object.
     *
     * @return A string representation of the ReplaceFactoryDataRequest object.
     */
    @Override
    public String toString() {
        return "ReplaceFactoryDataRequest [currentValue=" + currentValue + ", replaceWith=" + replaceWith + "]";
    }

}
