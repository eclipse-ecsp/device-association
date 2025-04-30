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

package org.eclipse.ecsp.deviceassociation.lib.model.wam;

/**
 * Represents the status of a SIM transaction.
 */
public enum SimTransactionStatus {
    COMPLETED("Completed"),
    IN_PROGRESS("In Progress"),
    PENDING("Pending"),
    CANCELED("Canceled"),
    REJECTED("Rejected"),
    FAILED("Failed");

    private String simTransactionStatus;

    /**
     * Constructs a new SimTransactionStatus with the specified status.
     *
     * @param simTransactionStatus the status of the SIM transaction
     */
    SimTransactionStatus(String simTransactionStatus) {
        this.simTransactionStatus = simTransactionStatus;
    }

    /**
     * Returns the status of the SIM transaction.
     *
     * @return the status of the SIM transaction
     */
    public String getSimTransactionStatus() {
        return simTransactionStatus;
    }
}
