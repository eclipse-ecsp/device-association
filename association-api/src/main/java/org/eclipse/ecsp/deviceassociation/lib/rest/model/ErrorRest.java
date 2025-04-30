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

package org.eclipse.ecsp.deviceassociation.lib.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Represents an error response in the REST API.
 */
@JsonInclude(Include.NON_NULL)
public class ErrorRest {
    private String message;
    private String reference;

    /**
     * Gets the error message.
     *
     * @return The error message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the error message.
     *
     * @param message The error message.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the reference associated with the error.
     *
     * @return The error reference.
     */
    public String getReference() {
        return reference;
    }

    /**
     * Sets the reference associated with the error.
     *
     * @param reference The error reference.
     */
    public void setReference(String reference) {
        this.reference = reference;
    }
}
