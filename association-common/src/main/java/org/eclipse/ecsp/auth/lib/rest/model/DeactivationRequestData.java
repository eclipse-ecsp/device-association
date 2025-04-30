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

/**
 * Represents the data for a deactivation request.
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeactivationRequestData {

    private String factoryId;

    /**
     * Default constructor for DeactivationRequestData.
     */
    public DeactivationRequestData() {

    }

    /**
     * Constructor for DeactivationRequestData with factoryId.
     *
     * @param factoryId the factoryId to set
     */
    public DeactivationRequestData(String factoryId) {
        this.factoryId = factoryId;
    }

    /**
     * Get the factoryId.
     *
     * @return the factoryId
     */
    public String getFactoryId() {
        return factoryId;
    }

    /**
     * Set the factoryId.
     *
     * @param factoryId the factoryId to set
     */
    public void setFactoryId(String factoryId) {
        this.factoryId = factoryId;
    }

}
