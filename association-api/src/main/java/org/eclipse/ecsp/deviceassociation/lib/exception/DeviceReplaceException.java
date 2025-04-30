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
 * This exception is thrown when there is an error while replacing a device.
 */
public class DeviceReplaceException extends RuntimeException {
    /**
     * Constructs a new DeviceReplaceException with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     * @param cause the cause (which is saved for later retrieval by the getCause() method).
     */
    public DeviceReplaceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new DeviceReplaceException with the specified cause.
     *
     * @param cause the cause (which is saved for later retrieval by the getCause() method).
     */
    public DeviceReplaceException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new DeviceReplaceException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     */
    public DeviceReplaceException(String message) {
        super(message);
    }
}