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

/**
 * Represents a login request.
 */
@JsonInclude(Include.NON_NULL)
public class LoginRequest {
    private String userId;
    private String password;
    private Boolean getProfile;
    private String targetUrl;

    /**
     * Default constructor.
     * Initializes the getProfile field to false.
     */
    public LoginRequest() {
        this.getProfile = false;
    }

    /**
     * Constructs a new LoginRequest object with the specified user ID and password.
     *
     * @param userId the user ID
     * @param password the password
     */
    public LoginRequest(String userId, String password) {
        super();
        this.userId = userId;
        this.password = password;
        this.getProfile = false;
    }

    /**
     * Constructs a new LoginRequest object with the specified user ID, password, and getProfile flag.
     *
     * @param userId     the user ID
     * @param password   the password
     * @param getProfile the flag indicating whether to get the user profile
     */
    public LoginRequest(String userId, String password, Boolean getProfile) {
        super();
        this.userId = userId;
        this.password = password;
        this.getProfile = getProfile;
    }

    /**
     * Gets the user ID.
     *
     * @return The user ID.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user ID.
     *
     * @param userId The user ID to set.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the password.
     *
     * @return The password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password The password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets whether to get the user's profile.
     *
     * @return Whether to get the user's profile.
     */
    public Boolean getGetProfile() {
        return getProfile;
    }

    /**
     * Sets whether to get the user's profile.
     *
     * @param getProfile Whether to get the user's profile.
     */
    public void setGetProfile(Boolean getProfile) {
        this.getProfile = getProfile;
    }

    /**
     * Gets the target URL.
     *
     * @return The target URL.
     */
    public String getTargetUrl() {
        return targetUrl;
    }

    /**
     * Sets the target URL.
     *
     * @param targetUrl The target URL to set.
     */
    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    /**
     * Returns a string representation of the LoginRequest object.
     *
     * @return A string representation of the LoginRequest object.
     */
    @Override
    public String toString() {
        return "LoginRequest [userId=" + userId + ", password=" + password + ", getProfile=" + getProfile
            + ", targetUrl=" + targetUrl
            + "]";
    }
}

