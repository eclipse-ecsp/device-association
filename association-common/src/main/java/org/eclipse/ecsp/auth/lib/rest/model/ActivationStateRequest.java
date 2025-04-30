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

package org.eclipse.ecsp.auth.lib.rest.model;

/**
 * Represents a request to retrieve the activation state of a device.
 */
public class ActivationStateRequest {

    private String serialNumber;

    /**
     * Constructs an empty ActivationStateRequest object.
     */
    public ActivationStateRequest() {
        super();
    }

    /**
     * Constructs an ActivationStateRequest object with the specified serial number.
     *
     * @param serialNumber the serial number of the device
     */
    public ActivationStateRequest(String serialNumber) {
        super();
        this.serialNumber = serialNumber;
    }

    /**
     * Retrieves the serial number of the device.
     *
     * @return the serial number
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the serial number of the device.
     *
     * @param serialNumber the serial number to set
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * Returns a string representation of the ActivationStateRequest object.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "ReadyToActivateRequest [serialNumber=" + serialNumber + "]";
    }

}
