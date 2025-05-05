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

package org.eclipse.ecsp.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * ResponsePayload.
 */
@JsonInclude(value = Include.NON_NULL)
public class ResponsePayload {
    
    /**
     * MSG.
     */
    public enum Msg {
        /**
         * SUCCESS.
         */
        SUCCESS, 
        /**
         * FAILURE.
         */
        FAILURE
    }

    /**
     * Represents a response payload with a default message.
     * The default message is set to {@link Msg#SUCCESS}.
     */
    Msg message = Msg.SUCCESS;
    /**
     * Represents a failure reason code.
     * It is set to null by default.
     */
    Integer failureReasonCode = null;
    /**
     * Represents a failure reason message.
     * It is set to null by default.
     */
    String failureReason;

    /**
     * Represents the data associated with the response payload.
     * This can hold any type of object depending on the context of the response.
     */
    Object data;

    /**
     * Default constructor for the ResponsePayload class.
     * Initializes a new instance of the ResponsePayload with a null value.
     */
    public ResponsePayload() {
        this(null);
    }

    /**
     * Constructs a new ResponsePayload with the specified data.
     *
     * @param data the data to be encapsulated in the response payload
     */
    public ResponsePayload(Object data) {
        this.data = data;
    }

    /**
     * Sets the failure reason for the response payload.
     *
     * @param failureReason the reason for the failure to be set
     */
    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    /**
     * Retrieves the reason for the failure.
     *
     * @return A string representing the failure reason.
     */
    public String getFailureReason() {
        return failureReason;
    }

    /**
     * Sets the failure reason code for the response payload.
     *
     * @param failureReasonCode the integer code representing the reason for failure
     */
    public void setFailureReasonCode(int failureReasonCode) {
        this.failureReasonCode = failureReasonCode;
    }

    /**
     * Retrieves the failure reason code associated with the response payload.
     *
     * @return the failure reason code as an {@code Integer}, or {@code null} if no failure reason is set.
     */
    public Integer getFailureReasonCode() {
        return failureReasonCode;
    }

    /**
     * Sets the message for the response payload.
     *
     * @param message the message to set, represented as a {@link Msg} object
     */
    public void setMessage(Msg message) {
        this.message = message;
    }

    /**
     * Retrieves the message associated with this response payload.
     *
     * @return the {@link Msg} object representing the message.
     */
    public Msg getMessage() {
        return message;
    }

    /**
     * Sets the data for the response payload.
     *
     * @param data the data to be set, represented as an Object
     */
    public void setData(Object data) {
        this.data = data;
    }

    /**
     * Retrieves the data associated with this response payload.
     *
     * @return an Object representing the data.
     */
    public Object getData() {
        return data;
    }
}