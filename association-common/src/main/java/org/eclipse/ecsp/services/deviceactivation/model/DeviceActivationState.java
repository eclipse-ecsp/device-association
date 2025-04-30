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

package org.eclipse.ecsp.services.deviceactivation.model;

import java.sql.Timestamp;

/**
 * Represents the state of device activation.
 */
public class DeviceActivationState {

    /**
     * id bigserial primary key, serial_number character varying not null,.
     * activation_initiated_on timestamp with time zone not null,
     * activation_initiated_by character varying not null,
     * deactivation_initiated_on timestamp with time zone,
     * deactivation_initiated_by character varying
     */
    private long id;
    private String serialNumber;
    private long factoryDataId;
    private Timestamp activationInitiatedOn;
    private String activationInitiatedBy;
    private Timestamp deactivationInitiatedOn;
    private String deactivationInitiatedBy;
    private boolean activationReady;

    /**
     * Retrieves the ID of the device activation state.
     *
     * @return the ID of the device activation state
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the ID of the device activation state.
     *
     * @param id the ID to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Retrieves the serial number of the device.
     *
     * @return the serial number of the device
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
     * Retrieves the ID of the factory data associated with the device.
     *
     * @return the ID of the factory data
     */
    public long getFactoryDataId() {
        return factoryDataId;
    }

    /**
     * Sets the ID of the factory data associated with the device.
     *
     * @param factoryDataId the ID of the factory data to set
     */
    public void setFactoryDataId(long factoryDataId) {
        this.factoryDataId = factoryDataId;
    }

    /**
     * Retrieves the timestamp when the activation was initiated.
     *
     * @return the timestamp when the activation was initiated
     */
    public Timestamp getActivationInitiatedOn() {
        return activationInitiatedOn;
    }

    /**
     * Sets the timestamp when the activation was initiated.
     *
     * @param activationInitiatedOn the timestamp when the activation was initiated to set
     */
    public void setActivationInitiatedOn(Timestamp activationInitiatedOn) {
        this.activationInitiatedOn = activationInitiatedOn;
    }

    /**
     * Retrieves the user who initiated the activation.
     *
     * @return the user who initiated the activation
     */
    public String getActivationInitiatedBy() {
        return activationInitiatedBy;
    }

    /**
     * Sets the user who initiated the activation.
     *
     * @param activationInitiatedBy the user who initiated the activation to set
     */
    public void setActivationInitiatedBy(String activationInitiatedBy) {
        this.activationInitiatedBy = activationInitiatedBy;
    }

    /**
     * Retrieves the timestamp when the deactivation was initiated.
     *
     * @return the timestamp when the deactivation was initiated
     */
    public Timestamp getDeactivationInitiatedOn() {
        return deactivationInitiatedOn;
    }

    /**
     * Sets the timestamp when the deactivation was initiated.
     *
     * @param deactivationInitiatedOn the timestamp when the deactivation was initiated to set
     */
    public void setDeactivationInitiatedOn(Timestamp deactivationInitiatedOn) {
        this.deactivationInitiatedOn = deactivationInitiatedOn;
    }

    /**
     * Retrieves the user who initiated the deactivation.
     *
     * @return the user who initiated the deactivation
     */
    public String getDeactivationInitiatedBy() {
        return deactivationInitiatedBy;
    }

    /**
     * Sets the user who initiated the deactivation.
     *
     * @param deactivationInitiatedBy the user who initiated the deactivation to set
     */
    public void setDeactivationInitiatedBy(String deactivationInitiatedBy) {
        this.deactivationInitiatedBy = deactivationInitiatedBy;
    }

    /**
     * Checks if the device is ready for activation.
     *
     * @return true if the device is ready for activation, false otherwise
     */
    public boolean isActivationReady() {
        return activationReady;
    }

    /**
     * Sets whether the device is ready for activation.
     *
     * @param activationReady true if the device is ready for activation, false otherwise
     */
    public void setActivationReady(boolean activationReady) {
        this.activationReady = activationReady;
    }

    /**
     * Returns a string representation of the DeviceActivationState object.
     *
     * @return a string representation of the DeviceActivationState object
     */
    @Override
    public String toString() {
        return "DeviceActivationState [id=" + id + ", serialNumber=" + serialNumber + ", factoryDataId=" + factoryDataId
            + " activationInitiatedOn="
            + activationInitiatedOn + ", activationInitiatedBy=" + activationInitiatedBy + ", deactivationInitiatedOn="
            + deactivationInitiatedOn + ", deactivationInitiatedBy=" + deactivationInitiatedBy + ", activationReady=" 
            +            activationReady
            + "]";
    }

}
