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

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * Represents a request to suspend a SIM card.
 */
public class SimSuspendRequest implements Serializable {

    @NotBlank(message = "imei is required and not allowed be empty")
    private String imei;

    /**
     * Retrieves the IMEI of the device associated with the SIM card.
     *
     * @return The IMEI of the device.
     */
    public String getImei() {
        return imei;
    }

    /**
     * Sets the IMEI of the device associated with the SIM card.
     *
     * @param imei The IMEI of the device.
     */
    public void setImei(String imei) {
        this.imei = imei;
    }
}