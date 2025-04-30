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
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Represents the user details in the association API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IsUserDetails {

    private List<Resource> resources;

    /**
     * Get the list of resources.
     *
     * @return the resources
     */
    @JsonProperty("Resources")
    public List<Resource> getResources() {
        return resources;
    }

    /**
     * Set the list of resources.
     *
     * @param resources the resources to set
     */
    @JsonProperty("Resources")
    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    /**
     * Returns a string representation of the IsUserDetails object.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "ISUserDetails [Resources=" + resources + "]";
    }

    /**
     * Represents a resource in the IsUserDetails object.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Resource {
        private List<String> emails;

        /**
         * Get the list of emails.
         *
         * @return the emails
         */
        public List<String> getEmails() {
            return emails;
        }

        /**
         * Set the list of emails.
         *
         * @param emails the emails to set
         */
        public void setEmails(List<String> emails) {
            this.emails = emails;
        }

        /**
         * Returns a string representation of the Resource object.
         *
         * @return a string representation of the object
         */
        @Override
        public String toString() {
            return "Resource [emails=" + emails + "]";
        }

    }

}
