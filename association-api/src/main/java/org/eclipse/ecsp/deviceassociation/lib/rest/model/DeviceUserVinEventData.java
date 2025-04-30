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

import org.eclipse.ecsp.annotations.EventMapping;
import org.eclipse.ecsp.deviceassociation.lib.service.Constants;
import org.eclipse.ecsp.domain.Version;

/**
 * Represents the event data for a device user VIN.
 */
@EventMapping(id = Constants.VIN_EVENT_ID, version = Version.V1_0)
public class DeviceUserVinEventData extends DeviceVinEventData {

    private static final long serialVersionUID = 1L;
    private String userId;

    /**
     * Gets the user ID associated with the device.
     *
     * @return The user ID.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user ID associated with the device.
     *
     * @param userId The user ID to set.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Returns a string representation of the DeviceUserVinEventData object.
     *
     * @return A string representation of the object.
     */
    @Override
    public String toString() {
        return "DeviceUserVinEventData [userId=" + userId + ", value=" + value + ", dummy=" + dummy + ", type=" + type
            + "]";
    }

}
