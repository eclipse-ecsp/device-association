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
 * Represents a user in the Tableau system.
 */
public class TableauUser {
    private long id;
    private String userName;

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
     * Gets the username of the user.
     *
     * @return The username of the user.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the username of the user.
     *
     * @param userName The username of the user.
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Returns a string representation of the TableauUser object.
     *
     * @return A string representation of the TableauUser object.
     */
    @Override
    public String toString() {
        return "TableauUser [id=" + id + ", userName=" + userName + "]";
    }
}
