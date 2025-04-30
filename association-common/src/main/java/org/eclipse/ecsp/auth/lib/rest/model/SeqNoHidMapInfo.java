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

package org.eclipse.ecsp.auth.lib.rest.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents information about a sequence number to HID mapping.
 */
@JsonAutoDetect
public class SeqNoHidMapInfo {
    @JsonProperty("id")
    private long id;

    @JsonProperty("harmanId")
    private String harmanId;

    /**
     * Gets the ID of the sequence number to HID mapping.
     *
     * @return The ID of the mapping.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the ID of the sequence number to HID mapping.
     *
     * @param id The ID of the mapping.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the Harman ID associated with the sequence number.
     *
     * @return The Harman ID.
     */
    public String getHarmanId() {
        return harmanId;
    }

    /**
     * Sets the Harman ID associated with the sequence number.
     *
     * @param harmanId The Harman ID.
     */
    public void setHarmanId(String harmanId) {
        this.harmanId = harmanId;
    }
}
