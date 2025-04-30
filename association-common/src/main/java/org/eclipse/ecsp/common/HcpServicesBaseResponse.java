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
    protected Integer httpStatusCode;
    protected String requestId;
    protected String code;
    protected String reason;
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
