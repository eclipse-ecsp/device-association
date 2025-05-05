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
 * Represents the state of a SIM card.
 */
public enum SimState {
    /**
     * The SIM card is in an active state.
     */
    ACTIVE("ACTIVE"),
    /**
     * The SIM card is in an SUSPEND state.
     */
    SUSPEND("SUSPEND");

    private String simState;

    /**
     * Constructs a new SimState with the specified state.
     *
     * @param simState the state of the SIM card
     */
    SimState(String simState) {
        this.simState = simState;
    }

    /**
     * Returns the state of the SIM card.
     *
     * @return the state of the SIM card
     */
    public String getSimState() {
        return simState;
    }
}
