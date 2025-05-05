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

package org.eclipse.ecsp.deviceassociation.lib.model.wam;

/**
 * Represents the possible actions that can be performed on a SIM user.
 */
public enum SimUserAction {
    /**
     * The SIM user action is to activate a new user.
     */
    ACTIVATE("Activate"),
    /**
     * The SIM user action is to terminate an existing user.
     */
    TERMINATE("Terminate");

    private String simUserAction;

    /**
     * Constructs a new SimUserAction with the specified action.
     *
     * @param simUserAction the action associated with the SimUserAction
     */
    SimUserAction(String simUserAction) {
        this.simUserAction = simUserAction;
    }

    /**
     * Returns the action associated with the SimUserAction.
     *
     * @return the action associated with the SimUserAction
     */
    public String getSimUserAction() {
        return simUserAction;
    }
}
