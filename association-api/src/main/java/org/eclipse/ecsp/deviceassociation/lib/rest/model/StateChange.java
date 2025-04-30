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

package org.eclipse.ecsp.deviceassociation.lib.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.eclipse.ecsp.services.factorydata.domain.DeviceState;

/**
 * Represents a state change of a device.
 * This class contains information about the factory ID and the device state.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StateChange {

    /**
     * The factory id associated with the state change.
     */
    private Long factoryId;

    /**
     * The state of the device.
     */
    private DeviceState state;

    /**
     * Gets the factory id associated with the state change.
     *
     * @return The factory id.
     */
    public Long getFactoryId() {
        return factoryId;
    }

    /**
     * Sets the factory id associated with the state change.
     *
     * @param factoryId The factory id to set.
     */
    public void setFactoryId(Long factoryId) {
        this.factoryId = factoryId;
    }

    /**
     * Gets the state of the device.
     *
     * @return The device state.
     */
    public DeviceState getState() {
        return state;
    }

    /**
     * Sets the state of the device.
     *
     * @param state The device state to set.
     */
    public void setState(DeviceState state) {
        this.state = state;
    }

    /**
     * Generates a hash code for the state change.
     *
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((factoryId == null) ? 0 : factoryId.hashCode());
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        return result;
    }

    /**
     * Checks if the state change is equal to another object.
     *
     * @param obj The object to compare.
     * @return True if the state change is equal to the object, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        StateChange other = (StateChange) obj;
        if (factoryId == null) {
            if (other.factoryId != null) {
                return false;
            }
        } else if (!factoryId.equals(other.factoryId)) {
            return false;
        }
        if (state != other.state) {
            return false;
        }
        return true;
    }

    /**
     * Returns a string representation of the state change.
     *
     * @return The string representation.
     */
    @Override
    public String toString() {
        return "StateChange [factoryId=" + factoryId + ", state=" + state + "]";
    }

}
