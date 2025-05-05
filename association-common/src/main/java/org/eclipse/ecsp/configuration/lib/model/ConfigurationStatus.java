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

package org.eclipse.ecsp.configuration.lib.model;

/**
 * Represents the status of a configuration.
 * Each status is associated with a specific message.
 */
public enum ConfigurationStatus {

    /**
     * Indicates that the configuration is new and has not been processed yet.
     */
    NEW("New"),

    /**
     * Indicates that the configuration has been sent for processing.
     */
    SENT("Sent"),

    /**
     * Indicates that the configuration has been successfully delivered.
     */
    DELIVERED("Delivered"),

    /**
     * Indicates that the configuration is invalid or contains errors.
     */
    INVALID("Invalid"),

    /**
     * Indicates that the configuration has been deleted.
     */
    DELETED("Deleted"),

    /**
     * Represents all possible statuses of the configuration.
     */
    ALL("All");

    private String message;

    /**
     * Constructs a ConfigurationStatus object with the specified message.
     *
     * @param message the message associated with the configuration status
     */
    private ConfigurationStatus(String message) {
        this.message = message;
    }

    /**
     * Returns the message associated with the configuration status.
     *
     * @return the message associated with the configuration status
     */
    public String getMessage() {
        return message;
    }
}

