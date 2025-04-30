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
 * Represents a role in the system.
 */
public class RoleDo {
    public static final int VALUE_32 = 32;
    private long roleId;
    private String roleName;
    private String desc;
    private String createdAt;
    private String modifiedAt;

    /**
     * Default constructor.
     */
    public RoleDo() {
        super();
    }

    /**
     * Constructs a new RoleDo object with the specified parameters.
     *
     * @param roleId     the ID of the role
     * @param roleName   the name of the role
     * @param desc       the description of the role
     * @param createdAt  the creation timestamp of the role
     * @param modifiedAt the modification timestamp of the role
     */
    public RoleDo(long roleId, String roleName, String desc, String createdAt, String modifiedAt) {
        super();
        this.roleId = roleId;
        this.roleName = roleName;
        this.desc = desc;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    /**
     * Constructs a new RoleDo object with the specified role ID and role name.
     *
     * @param roleId   the ID of the role
     * @param roleName the name of the role
     */
    public RoleDo(long roleId, String roleName) {
        super();
        this.roleId = roleId;
        this.roleName = roleName;
    }

    /**
     * Constructs a new RoleDo object with the specified roleId.
     *
     * @param roleId the ID of the role
     */
    public RoleDo(long roleId) {
        super();
        this.roleId = roleId;
    }

    /**
     * Get the ID of the role.
     *
     * @return The ID of the role.
     */
    public long getRoleId() {
        return roleId;
    }

    /**
     * Set the ID of the role.
     *
     * @param roleId The ID of the role.
     */
    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    /**
     * Get the name of the role.
     *
     * @return The name of the role.
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * Set the name of the role.
     *
     * @param roleName The name of the role.
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    /**
     * Get the description of the role.
     *
     * @return The description of the role.
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Set the description of the role.
     *
     * @param desc The description of the role.
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * Get the creation timestamp of the role.
     *
     * @return The creation timestamp of the role.
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * Set the creation timestamp of the role.
     *
     * @param createdAt The creation timestamp of the role.
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Get the modification timestamp of the role.
     *
     * @return The modification timestamp of the role.
     */
    public String getModifiedAt() {
        return modifiedAt;
    }

    /**
     * Set the modification timestamp of the role.
     *
     * @param modifiedAt The modification timestamp of the role.
     */
    public void setModifiedAt(String modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    /**
     * Generates a hash code for the role.
     *
     * @return The hash code for the role.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (roleId ^ (roleId >>> VALUE_32));
        result = prime * result + ((roleName == null) ? 0 : roleName.hashCode());
        return result;
    }

    /**
     * Checks if the role is equal to another object.
     *
     * @param obj The object to compare with.
     * @return True if the role is equal to the other object, false otherwise.
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
        RoleDo other = (RoleDo) obj;
        if (roleId != other.roleId) {
            return false;
        }
        if (roleName == null) {
            if (other.roleName != null) {
                return false;
            }
        } else if (!roleName.equals(other.roleName)) {
            return false;
        }
        return true;
    }

    /**
     * Returns a string representation of the role.
     *
     * @return The string representation of the role.
     */
    @Override
    public String toString() {
        return "RoleDO [roleID=" + roleId + ", roleName=" + roleName + ", desc=" + desc + ", createdAt=" + createdAt 
            +            ", modifiedAt="
            + modifiedAt + "]";
    }
}