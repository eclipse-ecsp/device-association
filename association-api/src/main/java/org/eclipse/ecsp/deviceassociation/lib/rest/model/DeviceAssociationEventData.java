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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.ecsp.entities.AbstractEventData;

/**
 * Represents the event data for device association.
 */
public class DeviceAssociationEventData extends AbstractEventData {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String pdid;

    /**
     * Constructs a new instance of the {@code DeviceAssociationEventData} class.
     *
     * @param userId The user ID associated with the event.
     * @param pdid   The PDID (Product Device ID) associated with the event.
     */
    public DeviceAssociationEventData(String userId, String pdid) {
        super();
        this.userId = userId;
        this.pdid = pdid;
    }

    /**
     * Gets the user ID.
     *
     * @return the user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user ID.
     *
     * @param userId the user ID to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the PDID.
     *
     * @return the PDID
     */
    @JsonProperty("PDID")
    public String getPdid() {
        return pdid;
    }

    /**
     * Sets the PDID.
     *
     * @param pdid the PDID to set
     */
    public void setPdid(String pdid) {
        this.pdid = pdid;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "EventData [userId=" + userId + ", PDID=" + pdid + "]";
    }

}
