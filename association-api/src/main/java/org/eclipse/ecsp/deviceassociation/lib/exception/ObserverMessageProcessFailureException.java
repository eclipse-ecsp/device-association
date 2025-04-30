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

package org.eclipse.ecsp.deviceassociation.lib.exception;

/**
 * This exception is thrown when there is a failure in processing an observer message.
 */
public class ObserverMessageProcessFailureException extends Exception {

    private static final long serialVersionUID = 5520695522425790895L;

    /**
     * Constructs a new ObserverMessageProcessFailureException with no detail message.
     */
    public ObserverMessageProcessFailureException() {
        super();
    }

    /**
     * Constructs a new ObserverMessageProcessFailureException with the specified detail message, cause, suppression
     * enabled or disabled, and writable stack trace enabled or disabled.
     *
     * @param message            the detail message (which is saved for later retrieval by the getMessage() method).
     * @param cause              the cause (which is saved for later retrieval by the getCause() method).
     * @param enableSuppression  whether or not suppression is enabled or disabled.
     * @param writableStackTrace whether or not the stack trace should be writable.
     */
    public ObserverMessageProcessFailureException(String message, Throwable cause, boolean enableSuppression,
                                                  boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * Constructs a new ObserverMessageProcessFailureException with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     * @param cause   the cause (which is saved for later retrieval by the getCause() method).
     */
    public ObserverMessageProcessFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new ObserverMessageProcessFailureException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     */
    public ObserverMessageProcessFailureException(String message) {
        super(message);
    }

    /**
     * Constructs a new ObserverMessageProcessFailureException with the specified cause.
     *
     * @param cause the cause (which is saved for later retrieval by the getCause() method).
     */
    public ObserverMessageProcessFailureException(Throwable cause) {
        super(cause);
    }

}
