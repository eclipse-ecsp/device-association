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

package org.eclipse.ecsp.services.shared.db.model;

/**
 * Represents a status count object.
 */
public class StatusCount {

    private String status;
    private Long count;

    /**
     * Constructs a new StatusCount object with the specified status and count.
     *
     * @param status the status of the count
     * @param count the count value
     */
    public StatusCount(String status, Long count) {
        super();
        this.status = status;
        this.count = count;
    }

    /**
     * Default constructor for StatusCount.
     */
    public StatusCount() {
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the count.
     *
     * @return the count
     */
    public Long getCount() {
        return count;
    }

    /**
     * Sets the count.
     *
     * @param count the count to set
     */
    public void setCount(Long count) {
        this.count = count;
    }

    /**
     * Returns a string representation of the StatusCount object.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "StatusCount [status=" + status + ", count=" + count + "]";
    }

}
