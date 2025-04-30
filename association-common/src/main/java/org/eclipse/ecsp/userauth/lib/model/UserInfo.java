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

/**
 * Represents user information.
 */
public class UserInfo {
    private Long userIdLong;
    private String userName;
    private String authToken;

    /**
     * Gets the user name.
     *
     * @return The user name.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the user name.
     *
     * @param userName The user name to set.
     */
    public void setUserName(String userName) {
        this.userName = userName;
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
     * @param authToken The authentication token to set.
     */
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    /**
     * Gets the user ID as a long value.
     *
     * @return The user ID as a long value.
     */
    public long getUserIdLong() {
        return userIdLong;
    }

    /**
     * Sets the user ID as a long value.
     *
     * @param userIdLong The user ID to set as a long value.
     */
    public void setUserIdLong(Long userIdLong) {
        this.userIdLong = userIdLong;
    }

    /**
     * Returns a string representation of the UserInfo object.
     *
     * @return A string representation of the UserInfo object.
     */
    @Override
    public String toString() {
        return "UserInfo [userIdLong=" + userIdLong + ", userName=" + userName + ", authToken=" + authToken + "]";
    }
}
