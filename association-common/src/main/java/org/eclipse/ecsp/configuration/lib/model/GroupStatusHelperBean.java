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

package org.eclipse.ecsp.configuration.lib.model;

/**
 * The GroupStatusHelperBean class represents a helper bean for managing group status information.
 */
public class GroupStatusHelperBean {

    private String status;
    private Long requestId;

    /**
     * Gets the status of the group.
     *
     * @return The status of the group.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the group.
     *
     * @param status The status of the group.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the request ID associated with the group.
     *
     * @return The request ID associated with the group.
     */
    public Long getRequestId() {
        return requestId;
    }

    /**
     * Sets the request ID associated with the group.
     *
     * @param requestId The request ID associated with the group.
     */
    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    /**
     * Returns a string representation of the GroupStatusHelperBean object.
     *
     * @return A string representation of the GroupStatusHelperBean object.
     */
    @Override
    public String toString() {
        return "GroupStatusHelperBean [status=" + status + ", requestId="
            + requestId + "]";
    }
}
