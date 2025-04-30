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

package org.eclipse.ecsp.deviceassociation.dto.swm;

import java.util.List;

/**
 * Represents additional attributes for a device association.
 */
public class AdditionalAttributes {
    private List<Object> map;

    /**
     * Gets the additional attributes map.
     *
     * @return The additional attributes map.
     */
    public List<Object> getMap() {
        return map;
    }

    /**
     * Sets the additional attributes map.
     *
     * @param map The additional attributes map to set.
     */
    public void setMap(List<Object> map) {
        this.map = map;
    }
}
