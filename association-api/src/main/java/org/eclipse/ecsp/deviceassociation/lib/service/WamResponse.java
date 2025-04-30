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

package org.eclipse.ecsp.deviceassociation.lib.service;

/**
 * Represents a response from the WAM (Wireless Association Module) service.
 *
 * @param <T> the type of data contained in the response
 */
class WamResponse<T> {
    private String code;
    private String message;
    private T data;

    /**
     * Gets the code associated with the response.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code for the response.
     *
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the message associated with the response.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message for the response.
     *
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the data contained in the response.
     *
     * @return the data
     */
    public T getData() {
        return data;
    }

    /**
     * Sets the data for the response.
     *
     * @param data the data to set
     */
    public void setData(T data) {
        this.data = data;
    }
}
