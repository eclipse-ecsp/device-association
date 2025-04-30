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

package org.eclipse.ecsp.deviceassociation.lib.service;

/**
 * Represents the response data for a SIM operation.
 *
 * @param <T> the type of the state object
 */
class SimResponseData<T> {
    private String transactionId;
    private T state;

    /**
     * Gets the transaction ID associated with the response.
     *
     * @return the transaction ID
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Sets the transaction ID associated with the response.
     *
     * @param transactionId the transaction ID to set
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * Gets the state object associated with the response.
     *
     * @return the state object
     */
    public T getState() {
        return state;
    }

    /**
     * Sets the state object associated with the response.
     *
     * @param state the state object to set
     */
    public void setState(T state) {
        this.state = state;
    }
}

