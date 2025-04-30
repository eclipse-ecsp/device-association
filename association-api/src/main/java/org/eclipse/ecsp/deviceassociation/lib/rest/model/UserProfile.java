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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents a user profile.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfile {

    /**
     * "firstName": "Bob", "lastName": "Argo", "email": "asd@gmail.com",.
     * "country": "USA", "userId": "bob_argo", "age": 22, "city": "Chicago",
     * "langauge": "English", "gender": "Male", "mobile": "+33567845798"
     */

    private String email;

    /**
     * Retrieves the email of the user.
     *
     * @return the email of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the user.
     *
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns a string representation of the UserProfile object.
     *
     * @return a string representation of the UserProfile object
     */
    @Override
    public String toString() {
        return "UserProfile [email=" + email + "]";
    }
}
