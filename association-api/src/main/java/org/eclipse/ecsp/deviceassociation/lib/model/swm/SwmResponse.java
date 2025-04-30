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

package org.eclipse.ecsp.deviceassociation.lib.model.swm;

import java.util.ArrayList;

/**
 * Represents a response from the SWM (Software Management) system.
 */
public class SwmResponse {
    private String id;
    private ArrayList<SwmActionResult> actionResult = new ArrayList<SwmActionResult>();

    /**
     * Gets the ID of the SWM response.
     *
     * @return The ID of the SWM response.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the SWM response.
     *
     * @param id The ID of the SWM response.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the list of SWM action results.
     *
     * @return The list of SWM action results.
     */
    public ArrayList<SwmActionResult> getActionResult() {
        return actionResult;
    }

    /**
     * Sets the list of SWM action results.
     *
     * @param actionResult The list of SWM action results.
     */
    public void setActionResult(ArrayList<SwmActionResult> actionResult) {
        this.actionResult = actionResult;
    }
}
