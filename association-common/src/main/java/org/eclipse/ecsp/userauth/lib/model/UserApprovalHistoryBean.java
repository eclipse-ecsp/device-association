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

package org.eclipse.ecsp.userauth.lib.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

/**
 * Represents a user approval history bean.
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserApprovalHistoryBean {
    private long requestId;
    private long userId;
    private long roleId;
    private long approverId;
    private String token;
    private String status;
    private Timestamp requestedOn;
    private int isLatest;

    /**
     * Gets the request ID.
     *
     * @return The request ID.
     */
    @JsonProperty(value = "requestId")
    public long getRequestId() {
        return requestId;
    }

    /**
     * Sets the request ID.
     *
     * @param requestId The request ID to set.
     */
    @JsonProperty(value = "requestId")
    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    /**
     * Gets the isLatest flag.
     *
     * @return The isLatest flag.
     */
    @JsonProperty(value = "isLatest")
    public int getIsLatest() {
        return isLatest;
    }

    /**
     * Sets the isLatest flag.
     *
     * @param isLatest The isLatest flag to set.
     */
    @JsonProperty(value = "isLatest")
    public void setIsLatest(int isLatest) {
        this.isLatest = isLatest;
    }

    /**
     * Gets the user ID.
     *
     * @return The user ID.
     */
    @JsonProperty(value = "userId")
    public long getUserId() {
        return userId;
    }

    /**
     * Sets the user ID.
     *
     * @param userId The user ID to set.
     */
    @JsonProperty(value = "userId")
    public void setUserId(long userId) {
        this.userId = userId;
    }

    /**
     * Gets the role ID.
     *
     * @return The role ID.
     */
    @JsonProperty(value = "roleId")
    public long getRoleId() {
        return roleId;
    }

    /**
     * Sets the role ID.
     *
     * @param roleId The role ID to set.
     */
    @JsonProperty(value = "roleId")
    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    /**
     * Gets the approver ID.
     *
     * @return The approver ID.
     */
    @JsonProperty(value = "approverId")
    public long getApproverId() {
        return approverId;
    }

    /**
     * Sets the approver ID.
     *
     * @param approverId The approver ID to set.
     */
    @JsonProperty(value = "approverId")
    public void setApproverId(long approverId) {
        this.approverId = approverId;
    }

    /**
     * Gets the token.
     *
     * @return The token.
     */
    @JsonProperty(value = "token")
    public String getToken() {
        return token;
    }

    /**
     * Sets the token.
     *
     * @param token The token to set.
     */
    @JsonProperty(value = "token")
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Gets the status.
     *
     * @return The status.
     */
    @JsonProperty(value = "status")
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status The status to set.
     */
    @JsonProperty(value = "status")
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the requested on timestamp.
     *
     * @return The requested on timestamp.
     */
    @JsonProperty(value = "RequestedOn")
    public Timestamp getRequestedOn() {
        return requestedOn;
    }

    /**
     * Sets the requested on timestamp.
     *
     * @param dateRequested The requested on timestamp to set.
     */
    @JsonProperty(value = "RequestedOn")
    public void setRequestedOn(Timestamp dateRequested) {
        this.requestedOn = dateRequested;
    }

    /**
     * Returns a string representation of the UserApprovalHistoryBean object.
     *
     * @return A string representation of the UserApprovalHistoryBean object.
     */
    @Override
    public String toString() {
        return "UserApprovalHistoryBean [requestId=" + requestId + ", userId=" + userId + ", roleId=" + roleId
            + ", approverId=" + approverId + ", token=" + token + ", status=" + status + ", requestedOn="
            + requestedOn + ", isLatest=" + isLatest + "]";
    }
}
