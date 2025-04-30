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

package org.eclipse.ecsp.deviceassociation.lib.model.usermanagement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.List;

/**
 * Represents a user request object.
 */
@JsonInclude(Include.NON_NULL)
public class UserRequest {

    List<String> userNames;
    List<String> emails;

    /**
     * Gets the list of user names.
     *
     * @return The list of user names.
     */
    public List<String> getUserNames() {
        return userNames;
    }

    /**
     * Sets the list of user names.
     *
     * @param userNames The list of user names.
     */
    public void setUserNames(List<String> userNames) {
        this.userNames = userNames;
    }

    /**
     * Gets the list of emails.
     *
     * @return The list of emails.
     */
    public List<String> getEmails() {
        return emails;
    }

    /**
     * Sets the list of emails.
     *
     * @param emails The list of emails.
     */
    public void setEmails(List<String> emails) {
        this.emails = emails;
    }
}
