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
 * Represents an application role.
 */
public class AppRole {
    private long appId;
    private Long roleId;

    /**
     * Gets the ID of the application.
     *
     * @return The ID of the application.
     */
    public long getAppId() {
        return appId;
    }

    /**
     * Sets the ID of the application.
     *
     * @param appId The ID of the application.
     */
    public void setAppId(long appId) {
        this.appId = appId;
    }

    /**
     * Gets the ID of the role.
     *
     * @return The ID of the role.
     */
    public Long getRoleId() {
        return roleId;
    }

    /**
     * Sets the ID of the role.
     *
     * @param roleId The ID of the role.
     */
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    /**
     * Returns a string representation of the AppRole object.
     *
     * @return A string representation of the AppRole object.
     */
    @Override
    public String toString() {
        return "AppRole [appId=" + appId + ", roleId=" + roleId + "]";
    }
}
