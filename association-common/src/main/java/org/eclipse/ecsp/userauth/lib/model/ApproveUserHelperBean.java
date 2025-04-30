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

import java.util.List;

/**
 * Represents a helper bean for approving user requests.
 * This bean contains information about the admin, user to be approved, roles to be added, and other related details.
 */
@JsonInclude(Include.NON_NULL)
public class ApproveUserHelperBean {
    private long adminId;
    private long reqId;
    private long toApproveUserId;
    private List<Long> rolesToAdd;
    private List<String> approverRoles;
    private String tableauUserId;
    private long tableauUserIdLong;
    private String authToken;

    /**
     * Gets the admin ID.
     *
     * @return The admin ID.
     */
    public long getAdminId() {
        return adminId;
    }

    /**
     * Sets the admin ID.
     *
     * @param adminId The admin ID to set.
     */
    public void setAdminId(long adminId) {
        this.adminId = adminId;
    }

    /**
     * Gets the user ID to be approved.
     *
     * @return The user ID to be approved.
     */
    public long getToApproveUserId() {
        return toApproveUserId;
    }

    /**
     * Sets the user ID to be approved.
     *
     * @param toApproveUserId The user ID to be approved.
     */
    public void setToApproveUserId(long toApproveUserId) {
        this.toApproveUserId = toApproveUserId;
    }

    /**
     * Gets the roles to add for the user.
     *
     * @return The roles to add for the user.
     */
    public List<Long> getRolesToAdd() {
        return rolesToAdd;
    }

    /**
     * Sets the roles to add for the user.
     *
     * @param rolesToAdd The roles to add for the user.
     */
    public void setRolesToAdd(List<Long> rolesToAdd) {
        this.rolesToAdd = rolesToAdd;
    }

    /**
     * Gets the approver roles.
     *
     * @return The approver roles.
     */
    public List<String> getApproverRoles() {
        return approverRoles;
    }

    /**
     * Sets the approver roles.
     *
     * @param approverRoles The approver roles.
     */
    public void setApproverRoles(List<String> approverRoles) {
        this.approverRoles = approverRoles;
    }

    /**
     * Gets the Tableau user ID.
     *
     * @return The Tableau user ID.
     */
    public String getTableauUserId() {
        return tableauUserId;
    }

    /**
     * Sets the Tableau user ID.
     *
     * @param tableauUserId The Tableau user ID.
     */
    public void setTableauUserId(String tableauUserId) {
        this.tableauUserId = tableauUserId;
    }

    /**
     * Gets the Tableau user ID as a long value.
     *
     * @return The Tableau user ID as a long value.
     */
    public long getTableauUserIdLong() {
        return tableauUserIdLong;
    }

    /**
     * Sets the Tableau user ID as a long value.
     *
     * @param tableauUserIdLong The Tableau user ID as a long value.
     */
    public void setTableauUserIdLong(long tableauUserIdLong) {
        this.tableauUserIdLong = tableauUserIdLong;
    }

    /**
     * Gets the authentication token.
     *
     * @return The authentication token.
     */
    public String getAuthToken() {
        return authToken;
    }

    /**
     * Sets the authentication token.
     *
     * @param authToken The authentication token.
     */
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    /**
     * Gets the request ID.
     *
     * @return The request ID.
     */
    public long getReqId() {
        return reqId;
    }

    /**
     * Sets the request ID.
     *
     * @param reqId The request ID.
     */
    public void setReqId(long reqId) {
        this.reqId = reqId;
    }

    /**
     * Returns a string representation of the ApproveUserHelperBean object.
     *
     * @return A string representation of the object.
     */
    @Override
    public String toString() {
        return "ApproveUserHelperBean [adminId=" + adminId + ", reqId=" + reqId + ", toApproveUserId=" 
            +            toApproveUserId + ", rolesToAdd="
            + rolesToAdd + ", approverRoles=" + approverRoles + ", tableauUserId=" + tableauUserId 
            +            ", tableauUserIdLong="
            + tableauUserIdLong + ", authToken=" + authToken + "]";
    }

}
