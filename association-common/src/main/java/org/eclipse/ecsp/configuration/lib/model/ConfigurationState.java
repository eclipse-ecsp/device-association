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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.sql.Timestamp;

/**
 * Represents the state of a configuration.
 */
public class ConfigurationState {

    private Long id;
    private long deviceConfigurationId;
    private ConfigurationStatus status;
    private Timestamp createdAt;

    /**
     * Default constructor for ConfigurationState.
     */
    public ConfigurationState() {

    }

    /**
     * Constructs a ConfigurationState object with the specified parameters.
     *
     * @param id                     the ID of the configuration state
     * @param deviceConfigurationId  the ID of the device configuration
     * @param status                 the status of the configuration
     * @param createdAt              the timestamp when the configuration was created
     */
    public ConfigurationState(Long id, Long deviceConfigurationId, ConfigurationStatus status, Timestamp createdAt) {
        super();
        this.id = id;
        this.deviceConfigurationId = deviceConfigurationId;
        this.status = status;
        this.createdAt = createdAt;
    }

    /**
     * Gets the ID of the configuration state.
     *
     * @return the ID of the configuration state
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the configuration state.
     *
     * @param id the ID of the configuration state
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the ID of the device configuration.
     *
     * @return the ID of the device configuration
     */
    public long getDeviceConfigurationId() {
        return deviceConfigurationId;
    }

    /**
     * Sets the ID of the device configuration.
     *
     * @param deviceConfigurationId the ID of the device configuration
     */
    public void setDeviceConfigurationId(long deviceConfigurationId) {
        this.deviceConfigurationId = deviceConfigurationId;
    }

    /**
     * Gets the status of the configuration.
     *
     * @return the status of the configuration
     */
    public ConfigurationStatus getStatus() {
        return status;
    }

    /**
     * Sets the status of the configuration.
     *
     * @param status the status of the configuration
     */
    public void setStatus(ConfigurationStatus status) {
        this.status = status;
    }

    /**
     * Gets the timestamp when the configuration was created.
     *
     * @return the timestamp when the configuration was created
     */
    public Timestamp getCreatedAt() {
        if (createdAt != null) {
            return new Timestamp(createdAt.getTime());
        } else {
            return null;
        }
    }

    /**
     * Sets the timestamp when the configuration was created.
     *
     * @param createdAt the timestamp when the configuration was created
     */
    @JsonSerialize(using = DateJsonSerializer.class)
    public void setCreatedAt(Timestamp createdAt) {
        if (createdAt != null) {
            this.createdAt = new Timestamp(createdAt.getTime());
        } else {
            this.createdAt = null;
        }
    }

    /**
     * Generates the hash code for the ConfigurationState object.
     *
     * @return the hash code for the ConfigurationState object
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    /**
     * Checks if the ConfigurationState object is equal to another object.
     *
     * @param obj the object to compare with
     * @return true if the ConfigurationState object is equal to the other object, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ConfigurationState other = (ConfigurationState) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

}
