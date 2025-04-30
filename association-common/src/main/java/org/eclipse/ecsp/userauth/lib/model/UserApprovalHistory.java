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

import org.eclipse.ecsp.services.shared.db.FilterField;

import java.sql.Timestamp;

/**
 * Represents the approval history of a user.
 * This class contains fields and methods to manage the user approval history.
 */
public class UserApprovalHistory {
    @FilterField(dbname = "\"UserApprovalHistory\".\"ID\"")
    private long id;
    @FilterField(dbname = "\"UserApprovalHistory\".\"RequestID\"")
    private long reqId;
    @FilterField(dbname = "\"UserApprovalHistory\".\"UserID\"")
    private long userId;
    @FilterField(dbname = "\"UserApprovalHistory\".\"RoleID\"")
    private long roleId;
    @FilterField(dbname = "\"UserApprovalHistory\".\"Justification\"")
    private String justification;
    @FilterField(dbname = "\"UserApprovalHistory\".\"ApproverID\"")
    private long approverId;
    @FilterField(dbname = "\"UserApprovalHistory\".\"ApproverName\"")
    private String approverName;
    @FilterField(dbname = "\"UserApprovalHistory\".\"Status\"")
    private String status;
    @FilterField(dbname = "\"UserApprovalHistory\".\"RequestedOn\"")
    private Timestamp requestedOn;
    @FilterField(dbname = "\"UserApprovalHistory\".\"IsLatest\"")
    private int isLatest;
    @FilterField(dbname = "\"User\".\"UserID\"")
    private String user;
    @FilterField(dbname = "\"Role\".\"RoleName\"")
    private String role;
    @FilterField(dbname = "\"User\".\"UserID\"")
    private String approver;
    @FilterField(dbname = "\"User\".\"FirstName\"")
    private String userName;

    /**
     * Retrieves the ID of the approval history.
     *
     * @return The ID of the approval history.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the ID of the approval history.
     *
     * @param id The ID of the approval history.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Retrieves the request ID associated with the approval history.
     *
     * @return The request ID associated with the approval history.
     */
    public long getReqId() {
        return reqId;
    }

    /**
     * Sets the request ID associated with the approval history.
     *
     * @param reqId The request ID associated with the approval history.
     */
    public void setReqId(long reqId) {
        this.reqId = reqId;
    }

    /**
     * Retrieves the user ID associated with the approval history.
     *
     * @return The user ID associated with the approval history.
     */
    public long getUserId() {
        return userId;
    }

    /**
     * Sets the user ID associated with the approval history.
     *
     * @param userId The user ID associated with the approval history.
     */
    public void setUserId(long userId) {
        this.userId = userId;
    }

    /**
     * Retrieves the role ID associated with the approval history.
     *
     * @return The role ID associated with the approval history.
     */
    public long getRoleId() {
        return roleId;
    }

    /**
     * Sets the role ID associated with the approval history.
     *
     * @param roleId The role ID associated with the approval history.
     */
    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    /**
     * Retrieves the justification for the approval.
     *
     * @return The justification for the approval.
     */
    public String getJustification() {
        return justification;
    }

    /**
     * Sets the justification for the approval.
     *
     * @param justification The justification for the approval.
     */
    public void setJustification(String justification) {
        this.justification = justification;
    }

    /**
     * Retrieves the ID of the approver.
     *
     * @return The ID of the approver.
     */
    public long getApproverId() {
        return approverId;
    }

    /**
     * Sets the ID of the approver.
     *
     * @param approverId The ID of the approver.
     */
    public void setApproverId(long approverId) {
        this.approverId = approverId;
    }

    /**
     * Retrieves the status of the approval.
     *
     * @return The status of the approval.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the approval.
     *
     * @param status The status of the approval.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Retrieves the timestamp when the approval was requested.
     *
     * @return The timestamp when the approval was requested.
     */
    public Timestamp getRequestedOn() {
        return requestedOn;
    }

    /**
     * Sets the timestamp when the approval was requested.
     *
     * @param requestedOn The timestamp when the approval was requested.
     */
    public void setRequestedOn(Timestamp requestedOn) {
        this.requestedOn = requestedOn;
    }

    /**
     * Retrieves whether the approval is the latest one.
     *
     * @return 1 if the approval is the latest, 0 otherwise.
     */
    public int getIsLatest() {
        return isLatest;
    }

    /**
     * Sets whether the approval is the latest one.
     *
     * @param isLatest 1 if the approval is the latest, 0 otherwise.
     */
    public void setIsLatest(int isLatest) {
        this.isLatest = isLatest;
    }

    /**
     * Retrieves the user associated with the approval.
     *
     * @return The user associated with the approval.
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets the user associated with the approval.
     *
     * @param user The user associated with the approval.
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Retrieves the role associated with the approval.
     *
     * @return The role associated with the approval.
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the role associated with the approval.
     *
     * @param role The role associated with the approval.
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Retrieves the approver associated with the approval.
     *
     * @return The approver associated with the approval.
     */
    public String getApprover() {
        return approver;
    }

    /**
     * Sets the approver associated with the approval.
     *
     * @param approver The approver associated with the approval.
     */
    public void setApprover(String approver) {
        this.approver = approver;
    }

    /**
     * Retrieves the name of the user associated with the approval.
     *
     * @return The name of the user associated with the approval.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the name of the user associated with the approval.
     *
     * @param userName The name of the user associated with the approval.
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Retrieves the name of the approver.
     *
     * @return The name of the approver.
     */
    public String getApproverName() {
        return approverName;
    }

    /**
     * Sets the name of the approver.
     *
     * @param approverName The name of the approver.
     */
    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }

    /**
     * Returns a string representation of the UserApprovalHistory object.
     *
     * @return A string representation of the UserApprovalHistory object.
     */
    @Override
    public String toString() {
        return "UserApprovalHistory [id=" + id + ", reqId=" + reqId + ", userId=" + userId + ", roleId=" + roleId 
            +            ", justification="
            + justification + ", approverId=" + approverId + ", approverName=" + approverName + ", status=" + status 
            +            ", requestedOn="
            + requestedOn + ", isLatest=" + isLatest + ", user=" + user + ", role=" + role + ", approver=" + approver 
            +            ", userName="
            + userName + "]";
    }

}
