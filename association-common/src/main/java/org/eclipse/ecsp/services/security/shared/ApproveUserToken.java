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

package org.eclipse.ecsp.services.security.shared;

import java.io.Serializable;

/**
 * Represents a user token used for approving user requests.
 */
public class ApproveUserToken implements Serializable {

    private static final long serialVersionUID = 5691685816208962055L;
    private String appsRequestedString;
    private long toApproveUserId;
    private long adminId;
    private String firstName;
    private String lastName;
    private String email;
    private long oemId;
    private long reqId;

    /**
     * Default constructor.
     */
    public ApproveUserToken() {

    }

    /**
     * Constructs a new instance of the {@code ApproveUserToken} class with the specified parameters.
     *
     * @param appsRequestedString the string representing the requested apps
     * @param toApproveUserId the ID of the user to approve
     * @param oemId the ID of the OEM
     */
    public ApproveUserToken(String appsRequestedString, long toApproveUserId, long oemId) {
        super();
        this.toApproveUserId = toApproveUserId;
        this.oemId = oemId;
    }

    /**
     * Constructs a new instance of the {@code ApproveUserToken} class.
     *
     * @param appsRequestedString The string representation of the requested apps.
     * @param toApproveUserId The ID of the user to be approved.
     * @param adminId The ID of the admin performing the approval.
     * @param oemId The ID of the OEM associated with the user.
     */
    public ApproveUserToken(String appsRequestedString, long toApproveUserId, long adminId, long oemId) {
        super();
        this.appsRequestedString = appsRequestedString;
        this.toApproveUserId = toApproveUserId;
        this.adminId = adminId;
        this.oemId = oemId;
    }

    /**
     * Constructs a new instance of the {@code ApproveUserToken} class with the specified parameters.
     *
     * @param appsRequestedString The string representation of the requested apps.
     * @param toApproveUserId The ID of the user to be approved.
     * @param adminId The ID of the admin performing the approval.
     * @param oemId The ID of the OEM associated with the user.
     * @param reqId The ID of the request.
     */
    public ApproveUserToken(String appsRequestedString, long toApproveUserId, long adminId, long oemId, long reqId) {
        super();
        this.appsRequestedString = appsRequestedString;
        this.toApproveUserId = toApproveUserId;
        this.adminId = adminId;
        this.oemId = oemId;
        this.reqId = reqId;
    }

    /**
     * Get the OEM ID.
     *
     * @return The OEM ID.
     */
    public long getOemId() {
        return oemId;
    }

    /**
     * Set the OEM ID.
     *
     * @param oemId The OEM ID to set.
     */
    public void setOemId(long oemId) {
        this.oemId = oemId;
    }

    /**
     * Get the requested apps as a string.
     *
     * @return The requested apps as a string.
     */
    public String getAppsRequestedString() {
        return appsRequestedString;
    }

    /**
     * Set the requested apps as a string.
     *
     * @param appsRequestedString The requested apps as a string to set.
     */
    public void setAppsRequestedString(String appsRequestedString) {
        this.appsRequestedString = appsRequestedString;
    }

    /**
     * Get the ID of the user to approve.
     *
     * @return The ID of the user to approve.
     */
    public long getToApproveUserId() {
        return toApproveUserId;
    }

    /**
     * Set the ID of the user to approve.
     *
     * @param toApproveUserId The ID of the user to approve.
     */
    public void setToApproveUserId(long toApproveUserId) {
        this.toApproveUserId = toApproveUserId;
    }

    /**
     * Get the ID of the admin.
     *
     * @return The ID of the admin.
     */
    public long getAdminId() {
        return adminId;
    }

    /**
     * Set the ID of the admin.
     *
     * @param adminId The ID of the admin to set.
     */
    public void setAdminId(long adminId) {
        this.adminId = adminId;
    }

    /**
     * Get the first name.
     *
     * @return The first name.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Set the first name.
     *
     * @param firstName The first name to set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Get the last name.
     *
     * @return The last name.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Set the last name.
     *
     * @param lastName The last name to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Get the email.
     *
     * @return The email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the email.
     *
     * @param email The email to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get the request ID.
     *
     * @return The request ID.
     */
    public long getReqId() {
        return reqId;
    }

    /**
     * Set the request ID.
     *
     * @param reqId The request ID to set.
     */
    public void setReqId(long reqId) {
        this.reqId = reqId;
    }

    /**
     * Returns a string representation of the ApproveUserToken object.
     *
     * @return A string representation of the object.
     */
    @Override
    public String toString() {
        return "ApproveUserToken [appsRequestedString=" + appsRequestedString + ", toApproveUserId=" + toApproveUserId 
            +            ", adminId="
            + adminId + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", oemId=" 
            +            oemId + ", reqId="
            + reqId + "]";
    }
}
