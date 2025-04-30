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
 * Represents an application.
 */
public class App {
    private long appId;
    private String appName;
    private String roleName;

    /**
     * Gets the ID of the app.
     *
     * @return The app ID.
     */
    public long getAppId() {
        return appId;
    }

    /**
     * Sets the ID of the app.
     *
     * @param appId The app ID to set.
     */
    public void setAppId(long appId) {
        this.appId = appId;
    }

    /**
     * Gets the name of the app.
     *
     * @return The app name.
     */
    public String getAppName() {
        return appName;
    }

    /**
     * Sets the name of the app.
     *
     * @param appName The app name to set.
     */
    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * Gets the role name associated with the app.
     *
     * @return The role name.
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * Sets the role name associated with the app.
     *
     * @param roleName The role name to set.
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    /**
     * Generates the hash code for the app.
     *
     * @return The hash code value.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((appName == null) ? 0 : appName.hashCode());
        return result;
    }

    /**
     * Checks if the app is equal to another object.
     *
     * @param obj The object to compare.
     * @return True if the app is equal to the object, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        App other = (App) obj;
        if (appName == null) {
            if (other.appName != null) {
                return false;
            }
        } else if (!appName.equals(other.appName)) {
            return false;
        }
        return true;
    }

    /**
     * Returns a string representation of the app.
     *
     * @return The string representation of the app.
     */
    @Override
    public String toString() {
        return "App [appId=" + appId + ", appName=" + appName + ", roleName=" + roleName + "]";
    }

}
