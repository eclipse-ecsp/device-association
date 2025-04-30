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
 * Represents a view of user roles.
 */
public class UserRolesView {

    long id;
    List<RoleDo> assigned;
    List<RoleDo> unassigned;

    /**
     * Default constructor.
     */
    public UserRolesView() {
        super();
    }

    /**
     * Constructs a new UserRolesView object with the specified parameters.
     *
     * @param id        the ID of the user
     * @param assigned  the list of assigned roles
     * @param unassigned  the list of unassigned roles
     */
    public UserRolesView(long id, List<RoleDo> assigned, List<RoleDo> unassigned) {
        super();
        this.id = id;
        this.assigned = assigned;
        this.unassigned = unassigned;
    }

    /**
     * Gets the ID of the user.
     *
     * @return The ID of the user.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the ID of the user.
     *
     * @param id The ID of the user.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the list of assigned roles.
     *
     * @return The list of assigned roles.
     */
    public List<RoleDo> getAssigned() {
        return assigned;
    }

    /**
     * Sets the list of assigned roles.
     *
     * @param assigned The list of assigned roles.
     */
    public void setAssigned(List<RoleDo> assigned) {
        this.assigned = assigned;
    }

    /**
     * Gets the list of unassigned roles.
     *
     * @return The list of unassigned roles.
     */
    public List<RoleDo> getUnassigned() {
        return unassigned;
    }

    /**
     * Sets the list of unassigned roles.
     *
     * @param unassigned The list of unassigned roles.
     */
    public void setUnassigned(List<RoleDo> unassigned) {
        this.unassigned = unassigned;
    }

    /**
     * Returns a string representation of the UserRolesView object.
     *
     * @return A string representation of the UserRolesView object.
     */
    @Override
    public String toString() {
        return "UserRolesView [id=" + id + ", assigned=" + assigned + ", unassigned=" + unassigned + "]";
    }
}
