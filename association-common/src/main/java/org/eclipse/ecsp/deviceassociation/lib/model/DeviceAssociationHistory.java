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

package org.eclipse.ecsp.deviceassociation.lib.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.sql.Timestamp;

/**
 * Represents the history of device associations.
 */
@JsonInclude(Include.NON_NULL)
public class DeviceAssociationHistory {
    @JsonIgnore
    private long id;
    private String serialNumber;
    private String userId;
    private String harmanId;
    private AssociationStatus associationStatus;
    private Timestamp associatedOn;
    private String associatedBy;
    private Timestamp disassociatedOn;
    private String disassociatedBy;
    private Timestamp modifiedOn;
    private String modifiedBy;
    @JsonIgnore
    private long factoryId;

    /**
     * Get the ID of the device association history.
     *
     * @return the ID
     */
    public long getId() {
        return id;
    }

    /**
     * Set the ID of the device association history.
     *
     * @param id the ID to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Get the serial number of the associated device.
     *
     * @return the serial number
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Set the serial number of the associated device.
     *
     * @param serialNumber the serial number to set
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * Get the user ID associated with the device.
     *
     * @return the user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Set the user ID associated with the device.
     *
     * @param userId the user ID to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Get the Harman ID associated with the device.
     *
     * @return the Harman ID
     */
    public String getHarmanId() {
        return harmanId;
    }

    /**
     * Set the Harman ID associated with the device.
     *
     * @param harmanId the Harman ID to set
     */
    public void setHarmanId(String harmanId) {
        this.harmanId = harmanId;
    }

    /**
     * Get the association status of the device.
     *
     * @return the association status
     */
    public AssociationStatus getAssociationStatus() {
        return associationStatus;
    }

    /**
     * Set the association status of the device.
     *
     * @param associationStatus the association status to set
     */
    public void setAssociationStatus(AssociationStatus associationStatus) {
        this.associationStatus = associationStatus;
    }

    /**
     * Get the timestamp when the device was associated.
     *
     * @return the associatedOn timestamp
     */
    public Timestamp getAssociatedOn() {
        if (associatedOn != null) {
            return new Timestamp(associatedOn.getTime());
        } else {
            return null;
        }
    }

    /**
     * Set the timestamp when the device was associated.
     *
     * @param associatedOn the associatedOn timestamp to set
     */
    public void setAssociatedOn(Timestamp associatedOn) {
        if (associatedOn != null) {
            this.associatedOn = new Timestamp(associatedOn.getTime());
        } else {
            this.associatedOn = null;
        }
    }

    /**
     * Get the user who associated the device.
     *
     * @return the associatedBy user
     */
    public String getAssociatedBy() {
        return associatedBy;
    }

    /**
     * Set the user who associated the device.
     *
     * @param associatedBy the associatedBy user to set
     */
    public void setAssociatedBy(String associatedBy) {
        this.associatedBy = associatedBy;
    }

    /**
     * Get the timestamp when the device was disassociated.
     *
     * @return the disassociatedOn timestamp
     */
    public Timestamp getDisassociatedOn() {
        if (disassociatedOn != null) {
            return new Timestamp(disassociatedOn.getTime());
        } else {
            return null;
        }
    }

    /**
     * Set the timestamp when the device was disassociated.
     *
     * @param disassociatedOn the disassociatedOn timestamp to set
     */
    public void setDisassociatedOn(Timestamp disassociatedOn) {
        if (disassociatedOn != null) {
            this.disassociatedOn = new Timestamp(disassociatedOn.getTime());
        } else {
            this.disassociatedOn = null;
        }
    }

    /**
     * Get the user who disassociated the device.
     *
     * @return the disassociatedBy user
     */
    public String getDisassociatedBy() {
        return disassociatedBy;
    }

    /**
     * Set the user who disassociated the device.
     *
     * @param disassociatedBy the disassociatedBy user to set
     */
    public void setDisassociatedBy(String disassociatedBy) {
        this.disassociatedBy = disassociatedBy;
    }

    /**
     * Get the timestamp when the device was last modified.
     *
     * @return the modifiedOn timestamp
     */
    public Timestamp getModifiedOn() {
        if (modifiedOn != null) {
            return new Timestamp(modifiedOn.getTime());
        } else {
            return null;
        }
    }

    /**
     * Set the timestamp when the device was last modified.
     *
     * @param modifiedOn the modifiedOn timestamp to set
     */
    public void setModifiedOn(Timestamp modifiedOn) {
        if (modifiedOn != null) {
            this.modifiedOn = new Timestamp(modifiedOn.getTime());
        } else {
            this.modifiedOn = null;
        }
    }

    /**
     * Get the user who last modified the device.
     *
     * @return the modifiedBy user
     */
    public String getModifiedBy() {
        return modifiedBy;
    }

    /**
     * Set the user who last modified the device.
     *
     * @param modifiedBy the modifiedBy user to set
     */
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    /**
     * Get the factory ID associated with the device.
     *
     * @return the factory ID
     */
    public long getFactoryId() {
        return factoryId;
    }

    /**
     * Set the factory ID associated with the device.
     *
     * @param factoryId the factory ID to set
     */
    public void setFactoryId(long factoryId) {
        this.factoryId = factoryId;
    }
}