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

package org.eclipse.ecsp.services.device.model;

import java.sql.Timestamp;

/**
 * Represents a device.
 */
public class Device {
    private long id;
    private String harmanId;
    private Timestamp activationDate;
    private String passcode;
    private long randomNumber;
    private String registeredScopeId;

    /**
     * Default constructor.
     */
    public Device() {
    }

    /**
     * Constructs a new Device object with the specified parameters.
     *
     * @param harmanId The Harman ID of the device.
     * @param activationDate The activation date of the device.
     * @param passcode The passcode of the device.
     * @param randomNumber The random number associated with the device.
     */
    public Device(String harmanId, Timestamp activationDate, String passcode, long randomNumber) {
        super();
        this.harmanId = harmanId;
        this.activationDate = activationDate;
        this.passcode = passcode;
        this.randomNumber = randomNumber;
    }

    /**
     * Gets the ID of the device.
     *
     * @return the ID of the device
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the ID of the device.
     *
     * @param id the ID of the device
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the Harman ID of the device.
     *
     * @return the Harman ID of the device
     */
    public String getHarmanId() {
        return harmanId;
    }

    /**
     * Sets the Harman ID of the device.
     *
     * @param harmanId the Harman ID of the device
     */
    public void setHarmanId(String harmanId) {
        this.harmanId = harmanId;
    }

    /**
     * Gets the activation date of the device.
     *
     * @return the activation date of the device
     */
    public Timestamp getActivationDate() {
        return activationDate;
    }

    /**
     * Sets the activation date of the device.
     *
     * @param activationDate the activation date of the device
     */
    public void setActivationDate(Timestamp activationDate) {
        this.activationDate = activationDate;
    }

    /**
     * Gets the passcode of the device.
     *
     * @return the passcode of the device
     */
    public String getPasscode() {
        return passcode;
    }

    /**
     * Sets the passcode of the device.
     *
     * @param passcode the passcode of the device
     */
    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    /**
     * Gets the random number associated with the device.
     *
     * @return the random number associated with the device
     */
    public long getRandomNumber() {
        return randomNumber;
    }

    /**
     * Sets the random number associated with the device.
     *
     * @param randomNumber the random number associated with the device
     */
    public void setRandomNumber(long randomNumber) {
        this.randomNumber = randomNumber;
    }

    /**
     * Gets the registered scope ID of the device.
     *
     * @return the registered scope ID of the device
     */
    public String getRegisteredScopeId() {
        return registeredScopeId;
    }

    /**
     * Sets the registered scope ID of the device.
     *
     * @param registeredScopeId the registered scope ID of the device
     */
    public void setRegisteredScopeId(String registeredScopeId) {
        this.registeredScopeId = registeredScopeId;
    }

    /**
     * Returns a string representation of the device.
     *
     * @return a string representation of the device
     */
    @Override
    public String toString() {
        return "Device{"
            + "harmanId='" + harmanId + '\''
            + ", activationDate=" + activationDate
            + ", passcode='" + passcode + '\''
            + ", randomNumber=" + randomNumber
            + ", registered_scope_id='" + registeredScopeId + '\''
            + '}';
    }
}
