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

import java.io.Serializable;

/**
 * The base response class for HCP services.
 */
public abstract class HcpServicesBaseResponse implements Serializable {
    /**
     * The HTTP status code associated with the response.
     * This field typically indicates the result of the HTTP request,
     * such as 200 for success, 404 for not found, etc.
     */
    protected Integer httpStatusCode;

    /**
     * The unique identifier for the request. This is used to track and correlate
     * the request throughout the system.
     */
    protected String requestId;
    /**
     * The response code indicating the status or result of the operation.
     * This field is typically used to convey success, failure, or specific
     * error codes in the response.
     */
    protected String code;
    /**
     * The reason for the response, providing additional context or explanation
     * for the status or result of the operation.
     */
    protected String reason;
    /**
     * A message providing further details about the response.
     * This field can be used to convey additional information or error messages
     * related to the operation.
     */
    protected String message;

    /**
     * Gets the HTTP status code of the response.
     *
     * @return The HTTP status code.
     */
    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    /**
     * Sets the HTTP status code of the response.
     *
     * @param httpStatusCode The HTTP status code to set.
     */
    public void setHttpStatusCode(Integer httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    /**
     * Gets the request ID associated with the response.
     *
     * @return The request ID.
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Sets the request ID associated with the response.
     *
     * @param requestId The request ID to set.
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * Gets the code associated with the response.
     *
     * @return The code.
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code associated with the response.
     *
     * @param code The code to set.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the reason associated with the response.
     *
     * @return The reason.
     */
    public String getReason() {
        return reason;
    }

    /**
     * Sets the reason associated with the response.
     *
     * @param reason The reason to set.
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * Gets the message associated with the response.
     *
     * @return The message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message associated with the response.
     *
     * @param message The message to set.
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
