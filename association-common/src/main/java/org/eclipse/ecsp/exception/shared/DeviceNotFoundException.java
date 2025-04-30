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
 * Exception thrown when a device is not found.
 */
public class DeviceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 3425229443263434799L;

    /**
     * Constructs a new DeviceNotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public DeviceNotFoundException(String message) {
        super(message);
    }
}
