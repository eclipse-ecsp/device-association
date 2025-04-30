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
 * The AppRoleAdminHelperBean class represents a helper bean for managing app, role, and admin information.
 */
public class AppRoleAdminHelperBean {
    private long appId;
    private long roleId;
    private long adminId;
    private String appName;

    /**
     * Gets the ID of the app.
     *
     * @return The ID of the app.
     */
    public long getAppId() {
        return appId;
    }

    /**
     * Sets the ID of the app.
     *
     * @param appId The ID of the app.
     */
    public void setAppId(long appId) {
        this.appId = appId;
    }

    /**
     * Gets the ID of the role.
     *
     * @return The ID of the role.
     */
    public long getRoleId() {
        return roleId;
    }

    /**
     * Sets the ID of the role.
     *
     * @param roleId The ID of the role.
     */
    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    /**
     * Gets the ID of the admin.
     *
     * @return The ID of the admin.
     */
    public long getAdminId() {
        return adminId;
    }

    /**
     * Sets the ID of the admin.
     *
     * @param adminId The ID of the admin.
     */
    public void setAdminId(long adminId) {
        this.adminId = adminId;
    }

    /**
     * Gets the name of the app.
     *
     * @return The name of the app.
     */
    public String getAppName() {
        return appName;
    }

    /**
     * Sets the name of the app.
     *
     * @param appName The name of the app.
     */
    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * Returns a string representation of the AppRoleAdminHelperBean object.
     *
     * @return A string representation of the AppRoleAdminHelperBean object.
     */
    @Override
    public String toString() {
        return "AppRoleAdminHelperBean [appId=" + appId + ", roleId=" + roleId + ", adminId=" + adminId + ", appName=" 
            +            appName + "]";
    }
}
