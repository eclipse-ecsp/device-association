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

package org.eclipse.ecsp.userauth.lib.model;

/**
 * Represents a response containing a string message.
 */
public class StringResponse {
    private String message;

    /**
     * Constructs a new StringResponse object with an empty message.
     */
    public StringResponse() {
    }

    /**
     * Constructs a new StringResponse object with the specified message.
     *
     * @param message the string message
     */
    public StringResponse(String message) {
        super();
        this.message = message;
    }

    /**
     * Gets the message of the StringResponse.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message of the StringResponse.
     *
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Returns a string representation of the StringResponse object.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "StringResponse [message=" + message + "]";
    }
}
