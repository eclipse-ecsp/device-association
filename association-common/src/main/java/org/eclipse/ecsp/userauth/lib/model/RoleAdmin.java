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
 * The RoleAdmin class represents the association between an admin and a role.
 */
public class RoleAdmin {
    private long adminId;
    private long roleId;

    /**
     * Returns a string representation of the RoleAdmin object.
     *
     * @return a string representation of the RoleAdmin object.
     */
    @Override
    public String toString() {
        return "RoleAdmin [adminId=" + adminId + ", roleId=" + roleId + "]";
    }

    /**
     * Returns the admin ID associated with the RoleAdmin object.
     *
     * @return the admin ID associated with the RoleAdmin object.
     */
    public long getAdminId() {
        return adminId;
    }

    /**
     * Sets the admin ID for the RoleAdmin object.
     *
     * @param adminId the admin ID to be set.
     */
    public void setAdminId(long adminId) {
        this.adminId = adminId;
    }

    /**
     * Returns the role ID associated with the RoleAdmin object.
     *
     * @return the role ID associated with the RoleAdmin object.
     */
    public long getRoleId() {
        return roleId;
    }

    /**
     * Sets the role ID for the RoleAdmin object.
     *
     * @param roleId the role ID to be set.
     */
    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }
}
