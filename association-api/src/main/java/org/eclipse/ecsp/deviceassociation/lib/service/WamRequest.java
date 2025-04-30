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

package org.eclipse.ecsp.deviceassociation.lib.service;

/**
 * Represents a WamRequest object.
 */
class WamRequest {
    private String state;

    /**
     * Gets the state of the WamRequest.
     *
     * @return the state of the WamRequest
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the state of the WamRequest.
     *
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
    }
}
