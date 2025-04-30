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
 * Represents a login response object.
 */
@JsonInclude(Include.NON_NULL)
public class LoginResponse {
    private String token;
    private List<String> roles;
    private String userId;
    private String emailId;
    private String firstName;
    private String lastName;
    private String targetUrl;
    private Boolean isSuccess;
    private String reasonForFailure;
    private String tableauAuthTicket;

    /**
     * Default constructor for LoginResponse.
     * Sets the default value of isSuccess to false.
     */
    public LoginResponse() {
        this.isSuccess = false;
    }

    /**
     * Constructs a new LoginResponse object.
     *
     * @param isSuccess a boolean value indicating whether the login was successful or not
     */
    public LoginResponse(boolean isSuccess) {
        super();
        this.isSuccess = isSuccess;
    }

    /**
     * Returns the token associated with the login response.
     *
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the token for the login response.
     *
     * @param token the token to set
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Returns the roles associated with the login response.
     *
     * @return the roles
     */
    public List<String> getRoles() {
        return roles;
    }

    /**
     * Sets the roles for the login response.
     *
     * @param roles the roles to set
     */
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    /**
     * Returns the user ID associated with the login response.
     *
     * @return the user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user ID for the login response.
     *
     * @param userId the user ID to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Returns the email ID associated with the login response.
     *
     * @return the email ID
     */
    public String getEmailId() {
        return emailId;
    }

    /**
     * Sets the email ID for the login response.
     *
     * @param emailId the email ID to set
     */
    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    /**
     * Returns the first name associated with the login response.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name for the login response.
     *
     * @param firstName the first name to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns the last name associated with the login response.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name for the login response.
     *
     * @param lastName the last name to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns the target URL associated with the login response.
     *
     * @return the target URL
     */
    public String getTargetUrl() {
        return targetUrl;
    }

    /**
     * Sets the target URL for the login response.
     *
     * @param targetUrl the target URL to set
     */
    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    /**
     * Returns the success status of the login response.
     *
     * @return true if the login was successful, false otherwise
     */
    public Boolean getIsSuccess() {
        return isSuccess;
    }

    /**
     * Sets the success status for the login response.
     *
     * @param isSuccess the success status to set
     */
    public void setIsSuccess(Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    /**
     * Returns the reason for login failure associated with the login response.
     *
     * @return the reason for login failure
     */
    public String getReasonForFailure() {
        return reasonForFailure;
    }

    /**
     * Sets the reason for login failure for the login response.
     *
     * @param reasonForFailure the reason for login failure to set
     */
    public void setReasonForFailure(String reasonForFailure) {
        this.reasonForFailure = reasonForFailure;
    }

    /**
     * Returns the Tableau authentication ticket associated with the login response.
     *
     * @return the Tableau authentication ticket
     */
    public String getTableauAuthTicket() {
        return tableauAuthTicket;
    }

    /**
     * Sets the Tableau authentication ticket for the login response.
     *
     * @param tableauAuthTicket the Tableau authentication ticket to set
     */
    public void setTableauAuthTicket(String tableauAuthTicket) {
        this.tableauAuthTicket = tableauAuthTicket;
    }

    /**
     * Returns a string representation of the LoginResponse object.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "LoginResponse [token=" + token + ", roles=" + roles + ", userId=" + userId + ", emailId=" + emailId 
            +            ", firstName="
            + firstName + ", lastName=" + lastName + ", targetUrl=" + targetUrl + ", isSuccess=" + isSuccess 
            +            ", reasonForFailure="
            + reasonForFailure + ", tableauAuthTicket=" + tableauAuthTicket + "]";
    }
}
