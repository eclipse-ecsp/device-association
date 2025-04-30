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
 * Represents a response object for approving a user.
 */
public class ApproveUserResponse {
    private List<App> appsApprovedNow;
    private List<App> appsApprovedAlready;

    /**
     * Gets the list of apps that are approved now.
     *
     * @return The list of apps approved now.
     */
    public List<App> getAppsApprovedNow() {
        return appsApprovedNow;
    }

    /**
     * Sets the list of apps that are approved now.
     *
     * @param appsApprovedNow The list of apps approved now.
     */
    public void setAppsApprovedNow(List<App> appsApprovedNow) {
        this.appsApprovedNow = appsApprovedNow;
    }

    /**
     * Gets the list of apps that are already approved.
     *
     * @return The list of apps already approved.
     */
    public List<App> getAppsApprovedAlready() {
        return appsApprovedAlready;
    }

    /**
     * Sets the list of apps that are already approved.
     *
     * @param appsApprovedAlready The list of apps already approved.
     */
    public void setAppsApprovedAlready(List<App> appsApprovedAlready) {
        this.appsApprovedAlready = appsApprovedAlready;
    }

    /**
     * Returns a string representation of the ApproveUserResponse object.
     *
     * @return A string representation of the object.
     */
    @Override
    public String toString() {
        return "ApproveUserResponse [appsApprovedNow=" + appsApprovedNow + ", appsApprovedAlready=" 
            +            appsApprovedAlready + "]";
    }

}
