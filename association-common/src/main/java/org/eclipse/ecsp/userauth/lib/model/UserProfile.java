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

import java.util.List;

/**
 * Represents a user profile containing information about the user and their approved and not approved apps.
 */
public class UserProfile {
    private String userId;
    private List<App> appsApproved;
    private List<App> appsNotApproved;

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
     * Gets the list of approved apps.
     *
     * @return The list of approved apps.
     */
    public List<App> getAppsApproved() {
        return appsApproved;
    }

    /**
     * Sets the list of approved apps.
     *
     * @param appsApproved The list of approved apps to set.
     */
    public void setAppsApproved(List<App> appsApproved) {
        this.appsApproved = appsApproved;
    }

    /**
     * Gets the list of not approved apps.
     *
     * @return The list of not approved apps.
     */
    public List<App> getAppsNotApproved() {
        return appsNotApproved;
    }

    /**
     * Sets the list of not approved apps.
     *
     * @param appsNotApproved The list of not approved apps to set.
     */
    public void setAppsNotApproved(List<App> appsNotApproved) {
        this.appsNotApproved = appsNotApproved;
    }

    /**
     * Returns a string representation of the UserProfile object.
     *
     * @return A string representation of the UserProfile object.
     */
    @Override
    public String toString() {
        return "UserProfile [userId=" + userId + ", appsApproved=" + appsApproved + ", appsNotApproved=" 
            +            appsNotApproved + "]";
    }
}
