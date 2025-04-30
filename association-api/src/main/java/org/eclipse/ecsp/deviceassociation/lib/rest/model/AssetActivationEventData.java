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
import org.eclipse.ecsp.entities.AbstractEventData;

/**
 * Represents the data for an asset activation event.
 * This class extends the AbstractEventData class and provides additional properties specific to asset activation.
 */
@EventMapping(id = Constants.ASSET_ACTIVATIONEVENT_ID, version = Version.V1_0)
public class AssetActivationEventData extends AbstractEventData {

    private static final long serialVersionUID = 1L;

    private String harmanId;
    private String serialNumber;
    private String userId;
    private String country;
    private String deviceType;

    /**
     * Gets the Harman ID.
     *
     * @return The Harman ID.
     */
    public String getHarmanId() {
        return harmanId;
    }

    /**
     * Sets the Harman ID.
     *
     * @param harmanId The Harman ID to set.
     */
    public void setHarmanId(String harmanId) {
        this.harmanId = harmanId;
    }

    /**
     * Gets the serial number.
     *
     * @return The serial number.
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Sets the serial number.
     *
     * @param serialNumber The serial number to set.
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * Gets the user ID.
     *
     * @return The user ID.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user ID.
     *
     * @param userId The user ID to set.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the country.
     *
     * @return The country.
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the country.
     *
     * @param country The country to set.
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Gets the device type.
     *
     * @return The device type.
     */
    public String getDeviceType() {
        return deviceType;
    }

    /**
     * Sets the device type.
     *
     * @param deviceType The device type to set.
     */
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    /**
     * Returns a string representation of the AssetActivationEventData object.
     *
     * @return A string representation of the object.
     */
    @Override
    public String toString() {
        return "AssetActivationEventData [harmanId=" + harmanId + ", serialNumber=" + serialNumber + ", userId="
            + userId + ", country=" + country + ", deviceType=" + deviceType + "]";
    }

}
