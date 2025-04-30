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

import org.eclipse.ecsp.services.shared.rest.support.SimpleResponseMessage;

/**
 * This exception is thrown when a requested entity does not exist.
 */
public class NoSuchEntityException extends Exception {

    private static final long serialVersionUID = 2904135699401091912L;
    private SimpleResponseMessage simpleResponseMessage;

    /**
     * Constructs a new NoSuchEntityException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public NoSuchEntityException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new NoSuchEntityException with the specified cause.
     *
     * @param cause the cause
     */
    public NoSuchEntityException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new NoSuchEntityException with the specified detail message.
     *
     * @param message the detail message
     */
    public NoSuchEntityException(String message) {
        super(message);
    }

    /**
     * Constructs a new NoSuchEntityException with the specified SimpleResponseMessage.
     *
     * @param simpleResponseMessage the SimpleResponseMessage
     */
    public NoSuchEntityException(SimpleResponseMessage simpleResponseMessage) {
        this.simpleResponseMessage = simpleResponseMessage;
    }

    /**
     * Returns the SimpleResponseMessage associated with this exception.
     *
     * @return the SimpleResponseMessage
     */
    public SimpleResponseMessage getSimpleResponseMessage() {
        return simpleResponseMessage;
    }

    /**
     * Sets the SimpleResponseMessage for this exception.
     *
     * @param simpleResponseMessage the SimpleResponseMessage
     */
    public void setSimpleResponseMessage(SimpleResponseMessage simpleResponseMessage) {
        this.simpleResponseMessage = simpleResponseMessage;
    }
}

