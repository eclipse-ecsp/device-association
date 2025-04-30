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

package org.eclipse.ecsp.auth.lib.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents a session request.
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SessionRequest {
    @JsonProperty("userId")
    private String userId;

    @JsonProperty("deviceId")
    private String deviceid;

    @JsonProperty("updateRegions")
    private List<Region> updateRegions;

    @JsonProperty("maxVersion")
    private String maxversion;

    /**
     * Gets the user ID.
     *
     * @return The user ID.
     */
    @JsonProperty("userId")
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user ID.
     *
     * @param userId The user ID to set.
     */
    @JsonProperty("userId")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the device ID.
     *
     * @return The device ID.
     */
    @JsonProperty("deviceId")
    public String getDeviceid() {
        return deviceid;
    }

    /**
     * Sets the device ID.
     *
     * @param deviceid The device ID to set.
     */
    @JsonProperty("deviceId")
    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    /**
     * Gets the update regions.
     *
     * @return The update regions.
     */
    @JsonProperty("updateRegions")
    public List<Region> getUpdateRegions() {
        return updateRegions;
    }

    /**
     * Sets the update regions.
     *
     * @param updateRegions The update regions to set.
     */
    @JsonProperty("updateRegions")
    public void setUpdateRegions(List<Region> updateRegions) {
        this.updateRegions = updateRegions;
    }

    /**
     * Gets the maximum version.
     *
     * @return The maximum version.
     */
    @JsonProperty("maxVersion")
    public String getMaxversion() {
        return maxversion;
    }

    /**
     * Sets the maximum version.
     *
     * @param maxversion The maximum version to set.
     */
    @JsonProperty("maxVersion")
    public void setMaxversion(String maxversion) {
        this.maxversion = maxversion;
    }

    /**
     * Returns a string representation of the SessionRequest object.
     *
     * @return A string representation of the SessionRequest object.
     */
    @Override
    public String toString() {
        List<Region> regions = getUpdateRegions();
        String str = "";
        if (regions != null) {
            for (Region region : regions) {
                str = str.concat(region.getRegion());
            }
        }
        return getUserId() + " " + getDeviceid() + " " + getMaxversion() + " " + str;
    }
}
