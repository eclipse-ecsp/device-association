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
        SUCCESS, FAILURE
    }

    Msg message = Msg.SUCCESS;
    Integer failureReasonCode = null;
    String failureReason;
    Object data;

    public ResponsePayload() {
        this(null);
    }

    public ResponsePayload(Object data) {
        this.data = data;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReasonCode(int failureReasonCode) {
        this.failureReasonCode = failureReasonCode;
    }

    public Integer getFailureReasonCode() {
        return failureReasonCode;
    }

    public void setMessage(Msg message) {
        this.message = message;
    }

    public Msg getMessage() {
        return message;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}