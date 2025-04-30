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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.List;

/**
 * This class represents a helper bean for requesting an account.
 * It contains information about the user, app access requested, justification, and OEM ID.
 */
@JsonInclude(Include.NON_NULL)
public class RequestAccountHelperBean {
    private User user;
    private List<Long> appAccessRequested;
    private String justification;
    private long oemId;

    /**
     * Get the OEM ID.
     *
     * @return The OEM ID.
     */
    public long getOemId() {
        return oemId;
    }

    /**
     * Set the OEM ID.
     *
     * @param oemId The OEM ID to set.
     */
    public void setOemId(long oemId) {
        this.oemId = oemId;
    }

    /**
     * Get the user.
     *
     * @return The user.
     */
    public User getUser() {
        return user;
    }

    /**
     * Set the user.
     *
     * @param user The user to set.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Get the list of app access requested.
     *
     * @return The list of app access requested.
     */
    public List<Long> getAppAccessRequested() {
        return appAccessRequested;
    }

    /**
     * Set the list of app access requested.
     *
     * @param appAccessRequested The list of app access requested to set.
     */
    public void setAppAccessRequested(List<Long> appAccessRequested) {
        this.appAccessRequested = appAccessRequested;
    }

    /**
     * Get the justification.
     *
     * @return The justification.
     */
    public String getJustification() {
        return justification;
    }

    /**
     * Set the justification.
     *
     * @param justification The justification to set.
     */
    public void setJustification(String justification) {
        this.justification = justification;
    }

    /**
     * Returns a string representation of the RequestAccountHelperBean object.
     *
     * @return A string representation of the RequestAccountHelperBean object.
     */
    @Override
    public String toString() {
        return "RequestAccountHelperBean [user=" + user + ", appAccessRequested=" + appAccessRequested 
            +            ", justification=" + justification
            + ", oemId=" + oemId + "]";
    }
}
