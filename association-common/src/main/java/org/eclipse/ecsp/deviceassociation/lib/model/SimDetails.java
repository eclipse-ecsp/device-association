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

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Represents the details of a SIM card.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimDetails {
    private long id;
    private String tranId;
    private String tranStatus;

    /**
     * Gets the ID of the SIM details.
     *
     * @return the ID of the SIM details
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the ID of the SIM details.
     *
     * @param id the ID of the SIM details
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the transaction ID of the SIM details.
     *
     * @return the transaction ID of the SIM details
     */
    public String getTranId() {
        return tranId;
    }

    /**
     * Sets the transaction ID of the SIM details.
     *
     * @param tranId the transaction ID of the SIM details
     */
    public void setTranId(String tranId) {
        this.tranId = tranId;
    }

    /**
     * Gets the transaction status of the SIM details.
     *
     * @return the transaction status of the SIM details
     */
    public String getTranStatus() {
        return tranStatus;
    }

    /**
     * Sets the transaction status of the SIM details.
     *
     * @param tranStatus the transaction status of the SIM details
     */
    public void setTranStatus(String tranStatus) {
        this.tranStatus = tranStatus;
    }
}
