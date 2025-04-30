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

package org.eclipse.ecsp.exception.shared;

/**
 * The {@code TimeoutException} class represents an exception that is thrown when a timeout occurs.
 */
public class TimeoutException extends Exception {

    private String status;

    /**
     * Constructs a {@code TimeoutException} with no specified detail message.
     */
    public TimeoutException() {
    }

    /**
     * Constructs a {@code TimeoutException} with the specified detail message.
     *
     * @param message the detail message
     */
    public TimeoutException(String message) {
        super(message);
    }

    /**
     * Constructs a {@code TimeoutException} with the specified detail message and status.
     *
     * @param message the detail message
     * @param status the status associated with the exception
     */
    public TimeoutException(String message, String status) {
        super(message);
        this.status = status;
    }

    /**
     * Returns the status associated with the exception.
     *
     * @return the status associated with the exception
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status associated with the exception.
     *
     * @param status the status to be set
     */
    public void setStatus(String status) {
        this.status = status;
    }
}