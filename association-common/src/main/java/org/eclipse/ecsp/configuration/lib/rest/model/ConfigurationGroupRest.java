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

package org.eclipse.ecsp.configuration.lib.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Represents a configuration group in the REST API.
 */
@JsonInclude(Include.NON_NULL)
public class ConfigurationGroupRest {

    private Long configurationId;
    private Long groupId;
    private Long requestId;

    /**
     * Retrieves the configuration ID.
     *
     * @return The configuration ID.
     */
    public Long getConfigurationId() {
        return configurationId;
    }

    /**
     * Sets the configuration ID.
     *
     * @param configurationId The configuration ID to set.
     */
    public void setConfigurationId(Long configurationId) {
        this.configurationId = configurationId;
    }

    /**
     * Retrieves the group ID.
     *
     * @return The group ID.
     */
    public Long getGroupId() {
        return groupId;
    }

    /**
     * Sets the group ID.
     *
     * @param groupId The group ID to set.
     */
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    /**
     * Retrieves the request ID.
     *
     * @return The request ID.
     */
    public Long getRequestId() {
        return requestId;
    }

    /**
     * Sets the request ID.
     *
     * @param requestId The request ID to set.
     */
    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    /**
     * Returns a string representation of the ConfigurationGroupRest object.
     *
     * @return A string representation of the object.
     */
    @Override
    public String toString() {
        return "ConfigurationGroupREST [configurationId=" + configurationId
            + ", groupId=" + groupId + ", requestId=" + requestId + "]";
    }

}
