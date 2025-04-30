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

/**
 * Represents the state of a device.
 */
public class DeviceState {

    private String harmanId;
    private String serialNumber;
    private State state;
    private String softwareVersion;
    private String deviceType;
    private boolean reactivationFlag;

    /**
     * Default constructor.
     */
    public DeviceState() {
        super();
    }

    /**
     * Constructs a new DeviceState object with the specified parameters.
     *
     * @param harmanId The Harman ID of the device.
     * @param serialNumber The serial number of the device.
     * @param state The state of the device.
     */
    public DeviceState(String harmanId, String serialNumber, State state) {
        super();
        this.harmanId = harmanId;
        this.serialNumber = serialNumber;
        this.state = state;
    }

    /**
     * Retrieves the Harman ID of the device.
     *
     * @return the Harman ID
     */
    public String getHarmanId() {
        return harmanId;
    }

    /**
     * Sets the Harman ID of the device.
     *
     * @param harmanId the Harman ID to set
     */
    public void setHarmanId(String harmanId) {
        this.harmanId = harmanId;
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
     * Retrieves the state of the device.
     *
     * @return the state
     */
    public State getState() {
        return state;
    }

    /**
     * Sets the state of the device.
     *
     * @param state the state to set
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * Retrieves the software version of the device.
     *
     * @return the software version
     */
    public String getSoftwareVersion() {
        return softwareVersion;
    }

    /**
     * Sets the software version of the device.
     *
     * @param softwareVersion the software version to set
     */
    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    /**
     * Retrieves the device type.
     *
     * @return the device type
     */
    public String getDeviceType() {
        return deviceType;
    }

    /**
     * Sets the device type.
     *
     * @param deviceType the device type to set
     */
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    /**
     * Checks if the reactivation flag is set.
     *
     * @return true if the reactivation flag is set, false otherwise
     */
    public boolean isReactivationFlag() {
        return reactivationFlag;
    }

    /**
     * Sets the reactivation flag.
     *
     * @param reactivationFlag the reactivation flag to set
     */
    public void setReactivationFlag(boolean reactivationFlag) {
        this.reactivationFlag = reactivationFlag;
    }

    /**
     * Returns a string representation of the DeviceState object.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "DeviceState [harmanId=" + harmanId + ", serialNumber=" + serialNumber + ", state=" + state 
            + ", deviceType=" + deviceType + ", reactivationFlag=" + reactivationFlag + "]";
    }

    /**
     * Represents the state of a device.
     */
    public enum State {
        ACTIVATED,
        DEACTIVATED;
    }
}
