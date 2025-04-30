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

import org.eclipse.ecsp.deviceassociation.lib.model.AssociationStatus;

/**
 * Represents a response object for device association.
 */
public class AssociateDeviceResponse {

    private long associationId;
    private AssociationStatus associationStatus;

    /**
     * Constructs a new AssociateDeviceResponse object with the specified association ID and status.
     *
     * @param associationId The ID of the association.
     * @param associationStatus The status of the association.
     */
    public AssociateDeviceResponse(long associationId, AssociationStatus associationStatus) {
        super();
        this.associationId = associationId;
        this.associationStatus = associationStatus;
    }

    /**
     * Constructs a new AssociateDeviceResponse object with the specified association status.
     *
     * @param associationStatus The association status of the device.
     */
    public AssociateDeviceResponse(AssociationStatus associationStatus) {
        super();
        this.associationStatus = associationStatus;
    }

    /**
     * Retrieves the association Id.
     *
     * @return the association Id
     */
    public long getAssociationId() {
        return associationId;
    }

    /**
     * Sets the association Id.
     *
     * @param associationId  the association Id to set
     */
    public void setAssociationId(long associationId) {
        this.associationId = associationId;
    }

    /**
     * Retrieves the association status.
     *
     * @return the association status
     */
    public AssociationStatus getAssociationStatus() {
        return associationStatus;
    }

    /**
     * Sets the association status.
     *
     * @param associationStatus  the association status to set
     */
    public void setAssociationStatus(AssociationStatus associationStatus) {
        this.associationStatus = associationStatus;
    }

}
