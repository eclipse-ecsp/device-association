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
 * Exception thrown when a precondition for VIN association fails.
 */
public class VinAssociationPreConditionFailedException extends Exception {

    private static final long serialVersionUID = 1L;
   
    /**
     * Exception thrown when a precondition for VIN association is not met.
     * This exception is typically used to indicate that a specific condition
     * required for processing a VIN association has failed.
     *
     * @param message A detailed message describing the reason for the failure.
     */
    public VinAssociationPreConditionFailedException(String message) {
        super(message);
    }
}
