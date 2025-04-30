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
 * Represents a REST response containing a code, message, and data.
 *
 * @param <T> the type of the data in the response
 */
public class RestResponse<T> {

    private String code;
    private String message;
    private T data;

    /**
     * Gets the code of the response.
     *
     * @return the code of the response
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code of the response.
     *
     * @param code the code of the response
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the message of the response.
     *
     * @return the message of the response
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message of the response.
     *
     * @param message the message of the response
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the data of the response.
     *
     * @return the data of the response
     */
    public T getData() {
        return data;
    }

    /**
     * Sets the data of the response.
     *
     * @param data the data of the response
     */
    public void setData(T data) {
        this.data = data;
    }

    /**
     * Returns a string representation of the RestResponse object.
     *
     * @return a string representation of the RestResponse object
     */
    @Override
    public String toString() {
        return "RestResponse [code=" + code + ", message=" + message + ", data=" + data + "]";
    }
}
