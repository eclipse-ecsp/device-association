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

package org.eclipse.ecsp.deviceassociation.lib.rest.model;

import java.sql.Timestamp;

/**
 * Represents a user in the system.
 */
public class UserDo {

    private String userId;
    private String email;
    private Timestamp updatedOn;

    /**
     * Default constructor for UserDo.
     */
    public UserDo() {
        super();
    }

    /**
     * Constructs a UserDo object with the specified userId, email, and updatedOn.
     *
     * @param userId    the user ID
     * @param email     the email address
     * @param updatedOn the timestamp of the last update
     */
    public UserDo(String userId, String email, Timestamp updatedOn) {
        super();
        this.userId = userId;
        this.email = email;
        this.updatedOn = updatedOn;
    }

    /**
     * Gets the user ID.
     *
     * @return the user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user ID.
     *
     * @param userId the user ID to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the email address.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address.
     *
     * @param email the email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the timestamp of the last update.
     *
     * @return the timestamp of the last update
     */
    public Timestamp getUpdatedOn() {
        if (updatedOn != null) {
            return new Timestamp(updatedOn.getTime());
        } else {
            return null;
        }
    }

    /**
     * Sets the timestamp of the last update.
     *
     * @param updatedOn the timestamp of the last update to set
     */
    public void setUpdatedOn(Timestamp updatedOn) {
        if (updatedOn != null) {
            this.updatedOn = new Timestamp(updatedOn.getTime());
        } else {
            this.updatedOn = null;
        }
    }
}
