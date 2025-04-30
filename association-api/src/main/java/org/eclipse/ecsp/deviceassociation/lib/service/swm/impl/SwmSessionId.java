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

package org.eclipse.ecsp.deviceassociation.lib.service.swm.impl;

/**
 * Represents a session ID used in the SWM (Software Management) service.
 */
public class SwmSessionId {
    private String sessionId;
    private long creationTime;

    /**
     * Default constructor for SwmSessionId.
     */
    public SwmSessionId() {
        super();
    }

    /**
     * Constructor for SwmSessionId with specified session ID and creation time.
     *
     * @param sessionId     the session ID
     * @param creationTime  the creation time of the session
     */
    public SwmSessionId(String sessionId, long creationTime) {
        this.sessionId = sessionId;
        this.creationTime = creationTime;
    }

    /**
     * Retrieves the session ID.
     *
     * @return the session ID
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Sets the session ID.
     *
     * @param sessionId  the session ID to set
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Retrieves the creation time of the session.
     *
     * @return the creation time
     */
    public long getCreationTime() {
        return creationTime;
    }

    /**
     * Sets the creation time of the session.
     *
     * @param creationTime  the creation time to set
     */
    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }
}
