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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import static org.eclipse.ecsp.common.CommonConstants.ID;

/**
 * Represents a response object containing user approval history.
 */
@JsonInclude(Include.NON_NULL)
public class UserApprovalHistoryResponse {
    private long id;
    private long requestId;
    private String userId;
    private String userName;
    private String role;
    private String approverId;
    private String token;
    private String status;
    private String justification;
    private String requestedOn;
    private int isLatest;

    /**
     * Retrieves the ID of the approval history.
     *
     * @return The ID of the approval history.
     */
    @JsonProperty(value = ID)
    public long getId() {
        return id;
    }

    /**
     * Sets the ID of the approval history.
     *
     * @param id The ID of the approval history.
     */
    @JsonProperty(value = ID)
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Retrieves the ID of the request.
     *
     * @return The ID of the request.
     */
    @JsonProperty(value = "requestId")
    public long getRequestId() {
        return requestId;
    }

    /**
     * Sets the ID of the request.
     *
     * @param requestId The ID of the request.
     */
    @JsonProperty(value = "requestId")
    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    /**
     * Retrieves the user ID.
     *
     * @return The user ID.
     */
    @JsonProperty(value = "userId")
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user ID.
     *
     * @param userId The user ID.
     */
    @JsonProperty(value = "userId")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Retrieves the user name.
     *
     * @return The user name.
     */
    @JsonProperty(value = "userName")
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the user name.
     *
     * @param userName The user name.
     */
    @JsonProperty(value = "userName")
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Retrieves the role.
     *
     * @return The role.
     */
    @JsonProperty(value = "role")
    public String getRole() {
        return role;
    }

    /**
     * Sets the role.
     *
     * @param role The role.
     */
    @JsonProperty(value = "role")
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Retrieves the approver ID.
     *
     * @return The approver ID.
     */
    @JsonProperty(value = "approverId")
    public String getApproverId() {
        return approverId;
    }

    /**
     * Sets the approver ID.
     *
     * @param approverId The approver ID.
     */
    @JsonProperty(value = "approverId")
    public void setApproverId(String approverId) {
        this.approverId = approverId;
    }

    /**
     * Retrieves the token.
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
     * @param token The token.
     */
    @JsonProperty(value = "token")
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Retrieves the status.
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
     * @param status The status.
     */
    @JsonProperty(value = "status")
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Retrieves the justification.
     *
     * @return The justification.
     */
    @JsonProperty(value = "justification")
    public String getJustification() {
        return justification;
    }

    /**
     * Sets the justification.
     *
     * @param justification The justification.
     */
    @JsonProperty(value = "justification")
    public void setJustification(String justification) {
        this.justification = justification;
    }

    /**
     * Retrieves the requested on date.
     *
     * @return The requested on date.
     */
    @JsonProperty(value = "requestedOn")
    public String getRequestedOn() {
        return requestedOn;
    }

    /**
     * Sets the requested on date.
     *
     * @param requestedOn The requested on date.
     */
    @JsonProperty(value = "requestedOn")
    public void setRequestedOn(String requestedOn) {
        this.requestedOn = requestedOn;
    }

    /**
     * Retrieves the latest flag.
     *
     * @return The latest flag.
     */
    @JsonProperty(value = "isLatest")
    public int getIsLatest() {
        return isLatest;
    }

    /**
     * Sets the latest flag.
     *
     * @param isLatest The latest flag.
     */
    @JsonProperty(value = "isLatest")
    public void setIsLatest(int isLatest) {
        this.isLatest = isLatest;
    }

    /**
     * Returns a string representation of the UserApprovalHistoryResponse object.
     *
     * @return A string representation of the UserApprovalHistoryResponse object.
     */
    @Override
    public String toString() {
        return "UserApprovalHistoryResponse [Id=" + id + ", requestId=" + requestId + ", userId=" + userId 
            +            ", userName=" + userName
            + ", role=" + role + ", approverId=" + approverId + ", token=" + token + ", status=" + status 
            +            ", justification="
            + justification + ", requestedOn=" + requestedOn + ", isLatest=" + isLatest + "]";
    }
}
