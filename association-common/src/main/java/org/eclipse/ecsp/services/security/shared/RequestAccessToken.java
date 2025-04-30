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
 * Represents a request for an access token.
 * This class is used to store information related to the request, such as the apps to request, user ID, email, and
 * OEM ID.
 */
public class RequestAccessToken implements Serializable {

    private static final long serialVersionUID = 2559167217001603966L;
    private String appsToRequestString;
    private String userId;
    private String email;
    private long oemId;

    /**
     * Default constructor.
     * Creates an empty RequestAccessToken object.
     */
    public RequestAccessToken() {
    }

    /**
     * Creates a RequestAccessToken object with the specified parameters.
     *
     * @param appsToRequestString The apps to request as a string.
     * @param userId The user ID associated with the request.
     * @param email The email associated with the request.
     * @param oemId The OEM ID associated with the request.
     */
    public RequestAccessToken(String appsToRequestString, String userId, String email, long oemId) {
        super();
        this.appsToRequestString = appsToRequestString;
        this.userId = userId;
        this.email = email;
        this.oemId = oemId;
    }

    /**
     * Gets the apps to request as a string.
     *
     * @return The apps to request as a string.
     */
    public String getAppsToRequestString() {
        return appsToRequestString;
    }

    /**
     * Sets the apps to request as a string.
     *
     * @param appsToRequestString The apps to request as a string.
     */
    public void setAppsToRequestString(String appsToRequestString) {
        this.appsToRequestString = appsToRequestString;
    }

    /**
     * Gets the user ID associated with the request.
     *
     * @return The user ID associated with the request.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user ID associated with the request.
     *
     * @param userId The user ID associated with the request.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the email associated with the request.
     *
     * @return The email associated with the request.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email associated with the request.
     *
     * @param email The email associated with the request.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the OEM ID associated with the request.
     *
     * @return The OEM ID associated with the request.
     */
    public long getOemId() {
        return oemId;
    }

    /**
     * Sets the OEM ID associated with the request.
     *
     * @param oemId The OEM ID associated with the request.
     */
    public void setOemId(long oemId) {
        this.oemId = oemId;
    }

    /**
     * Returns a string representation of the RequestAccessToken object.
     *
     * @return A string representation of the RequestAccessToken object.
     */
    @Override
    public String toString() {
        return "RequestAccessToken [appsToRequestString=" + appsToRequestString + ", userId=" + userId + ", email=" 
            +            email + ", oemId="
            + oemId + "]";
    }
}
