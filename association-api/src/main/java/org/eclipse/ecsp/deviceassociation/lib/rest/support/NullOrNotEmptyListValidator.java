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

package org.eclipse.ecsp.deviceassociation.lib.rest.support;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;

/**
 * Validator to check if a list is either null or not empty.
 * This validator is used in conjunction with the {@link NullOrNotEmptyList} annotation.
 *
 * <p>Validation logic:
 * <ul>
 *   <li>If the list is null, it is considered valid.</li>
 *   <li>If the list is empty, it is considered invalid.</li>
 *   <li>If the list contains elements, it is considered valid.</li>
 * </ul>
 *
 * <p>Example usage:
 * <pre>
 * {@code
 * @NullOrNotEmptyList
 * private List<String> myList;
 * }
 * </pre>
 *
 * @author Akshay
 * @see NullOrNotEmptyList
 * @see jakarta.validation.ConstraintValidator
 */
public class NullOrNotEmptyListValidator implements ConstraintValidator<NullOrNotEmptyList, List<String>> {

    /**
     * Initializes the validator with the specified parameters.
     * This method is typically used to perform any setup or configuration
     * required before the validation logic is executed.
     *
     * @param parameters the annotation instance containing the parameters
     *                    for the validator. Can be used to access any
     *                    attributes defined in the annotation.
     */
    public void initialize(NullOrNotEmpty parameters) {
    }

    /**
     * Validates that a given list of strings is either null or not empty.
     *
     * @param value The list of strings to validate. Can be null.
     * @param constraintValidatorContext The context in which the constraint is evaluated.
     * @return {@code true} if the list is null or not empty, {@code false} if the list is empty.
     */
    public boolean isValid(List<String> value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        } else if (value.isEmpty()) {
            return false;
        }
        return true;
    }
}
