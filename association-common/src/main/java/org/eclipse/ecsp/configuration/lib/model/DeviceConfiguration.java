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
 * Represents a device configuration.
 */
public class DeviceConfiguration {

    private Long id;
    private String harmanId;
    private Long configurationId;
    private Timestamp createdAt;
    private String createdBy;
    private Timestamp lastUpdatedAt;
    private String lastUpdatedBy;
    private ConfigurationStatus currentStatus;
    private Long requestId;

    /**
     * Default constructor for DeviceConfiguration.
     */
    public DeviceConfiguration() {

    }

    /**
     * Constructs a DeviceConfiguration object with the specified parameters.
     *
     * @param id                the ID of the device configuration
     * @param harmanId          the Harman ID of the device configuration
     * @param configurationId   the configuration ID of the device configuration
     * @param createdAt         the creation timestamp of the device configuration
     * @param createdBy         the creator of the device configuration
     * @param lastUpdatedAt     the last update timestamp of the device configuration
     * @param lastUpdatedBy     the last updater of the device configuration
     * @param currentStatus     the current status of the device configuration
     * @param requestId         the request ID of the device configuration
     */
    public DeviceConfiguration(Long id, String harmanId, Long configurationId, Timestamp createdAt, String createdBy,
                               Timestamp lastUpdatedAt, String lastUpdatedBy, ConfigurationStatus currentStatus,
                               Long requestId) {
        super();
        this.id = id;
        this.harmanId = harmanId;
        this.configurationId = configurationId;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.lastUpdatedAt = lastUpdatedAt;
        this.lastUpdatedBy = lastUpdatedBy;
        this.requestId = requestId;
    }

    /**
     * Gets the ID of the device configuration.
     *
     * @return the ID of the device configuration
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the device configuration.
     *
     * @param id the ID of the device configuration
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the Harman ID of the device configuration.
     *
     * @return the Harman ID of the device configuration
     */
    public String getHarmanId() {
        return harmanId;
    }

    /**
     * Sets the Harman ID of the device configuration.
     *
     * @param harmanId the Harman ID of the device configuration
     */
    public void setHarmanId(String harmanId) {
        this.harmanId = harmanId;
    }

    /**
     * Gets the configuration ID of the device configuration.
     *
     * @return the configuration ID of the device configuration
     */
    public Long getConfigurationId() {
        return configurationId;
    }

    /**
     * Sets the configuration ID of the device configuration.
     *
     * @param configurationId the configuration ID of the device configuration
     */
    public void setConfigurationId(Long configurationId) {
        this.configurationId = configurationId;
    }

    /**
     * Gets the creation timestamp of the device configuration.
     *
     * @return the creation timestamp of the device configuration
     */
    public Timestamp getCreatedAt() {
        if (createdAt != null) {
            return new Timestamp(createdAt.getTime());
        } else {
            return null;
        }
    }

    /**
     * Sets the creation timestamp of the device configuration.
     *
     * @param createdAt the creation timestamp of the device configuration
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
     * Gets the last update timestamp of the device configuration.
     *
     * @return the last update timestamp of the device configuration
     */
    public Timestamp getLastUpdatedAt() {
        if (lastUpdatedAt != null) {
            return new Timestamp(lastUpdatedAt.getTime());
        } else {
            return null;
        }
    }

    /**
     * Sets the last update timestamp of the device configuration.
     *
     * @param lastUpdatedAt the last update timestamp of the device configuration
     */
    @JsonSerialize(using = DateJsonSerializer.class)
    public void setLastUpdatedAt(Timestamp lastUpdatedAt) {
        if (lastUpdatedAt != null) {
            this.lastUpdatedAt = new Timestamp(lastUpdatedAt.getTime());
        } else {
            this.lastUpdatedAt = null;
        }
    }

    /**
     * Gets the creator of the device configuration.
     *
     * @return the creator of the device configuration
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the creator of the device configuration.
     *
     * @param createdBy the creator of the device configuration
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Gets the last updater of the device configuration.
     *
     * @return the last updater of the device configuration
     */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    /**
     * Sets the last updater of the device configuration.
     *
     * @param lastUpdatedBy the last updater of the device configuration
     */
    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    /**
     * Gets the current status of the device configuration.
     *
     * @return the current status of the device configuration
     */
    public ConfigurationStatus getCurrentStatus() {
        return currentStatus;
    }

    /**
     * Sets the current status of the device configuration.
     *
     * @param currentStatus the current status of the device configuration
     */
    public void setCurrentStatus(ConfigurationStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    /**
     * Gets the request ID of the device configuration.
     *
     * @return the request ID of the device configuration
     */
    public Long getRequestId() {
        return requestId;
    }

    /**
     * Sets the request ID of the device configuration.
     *
     * @param requestId the request ID of the device configuration
     */
    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    /**
     * Returns a hash code value for the object. This method is used by the hashing
     * algorithms, such as those used in hash tables.
     *
     * @return the hash code value for this object
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare
     * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise
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
        DeviceConfiguration other = (DeviceConfiguration) obj;
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
