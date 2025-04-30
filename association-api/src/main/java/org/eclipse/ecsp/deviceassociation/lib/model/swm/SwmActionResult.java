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

/**
 * Represents the result of a SWM action.
 */
public class SwmActionResult {

    private int code;
    private float priority;
    private SwmErrorMessage resultMessage;
    private SwmErrorMessage reasonMessage;

    /**
     * Gets the code associated with the action result.
     *
     * @return The code associated with the action result.
     */
    public int getCode() {
        return code;
    }

    /**
     * Sets the code associated with the action result.
     *
     * @param code The code to set.
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * Gets the priority of the action result.
     *
     * @return The priority of the action result.
     */
    public float getPriority() {
        return priority;
    }

    /**
     * Sets the priority of the action result.
     *
     * @param priority The priority to set.
     */
    public void setPriority(float priority) {
        this.priority = priority;
    }

    /**
     * Gets the result message associated with the action result.
     *
     * @return The result message associated with the action result.
     */
    public SwmErrorMessage getResultMessage() {
        return resultMessage;
    }

    /**
     * Sets the result message associated with the action result.
     *
     * @param resultMessage The result message to set.
     */
    public void setResultMessage(SwmErrorMessage resultMessage) {
        this.resultMessage = resultMessage;
    }

    /**
     * Gets the reason message associated with the action result.
     *
     * @return The reason message associated with the action result.
     */
    public SwmErrorMessage getReasonMessage() {
        return reasonMessage;
    }

    /**
     * Sets the reason message associated with the action result.
     *
     * @param reasonMessage The reason message to set.
     */
    public void setReasonMessage(SwmErrorMessage reasonMessage) {
        this.reasonMessage = reasonMessage;
    }

}
