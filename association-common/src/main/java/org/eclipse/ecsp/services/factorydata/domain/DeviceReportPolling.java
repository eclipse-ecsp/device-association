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

package org.eclipse.ecsp.services.factorydata.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Represents the device report polling information.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceReportPolling {

    String href;
    String type;

    /**
     * Gets the type of the device report polling.
     *
     * @return The type of the device report polling.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the device report polling.
     *
     * @param type The type of the device report polling.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the href of the device report polling.
     *
     * @return The href of the device report polling.
     */
    public String getHref() {
        return href;
    }

    /**
     * Sets the href of the device report polling.
     *
     * @param href The href of the device report polling.
     */
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * Returns a string representation of the DeviceReportPolling object.
     *
     * @return A string representation of the DeviceReportPolling object.
     */
    @Override
    public String toString() {
        return "DeviceReportPolling [href=" + href + ", type=" + type + "]";
    }
}
