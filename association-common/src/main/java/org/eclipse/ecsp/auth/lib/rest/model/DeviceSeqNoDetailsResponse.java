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

import java.util.List;

/**
 * Represents a response containing device sequence number and Harman ID mapping details.
 */
@JsonAutoDetect
public class DeviceSeqNoDetailsResponse {

    @JsonProperty("deviceSeqNoAndHarmanIdList")
    private List<SeqNoHidMapInfo> seqNoHidMapInfo;

    /**
     * Retrieves the list of sequence number and Harman ID mapping information.
     *
     * @return The list of sequence number and Harman ID mapping information.
     */
    @JsonProperty("deviceSeqNoAndHarmanIdList")
    public List<SeqNoHidMapInfo> getSeqNoHidMapInfo() {
        return seqNoHidMapInfo;
    }

    /**
     * Sets the list of sequence number and Harman ID mapping information.
     *
     * @param seqNoHidMapInfo The list of sequence number and Harman ID mapping information.
     */
    @JsonProperty("deviceSeqNoAndHarmanIdList")
    public void setSeqNoHidMapInfo(List<SeqNoHidMapInfo> seqNoHidMapInfo) {
        this.seqNoHidMapInfo = seqNoHidMapInfo;
    }

}
