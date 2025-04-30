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
import java.util.Objects;

/**
 * Validator to check if a given object is either null or not an empty string.
 * This validator is used in conjunction with the {@link NullOrNotEmpty} annotation.
 * 
 * <p>Validation logic:</p>
 * <ul>
 *   <li>If the value is {@code null}, it is considered valid.</li>
 *   <li>If the value is a non-null object and its string representation is empty, it is considered invalid.</li>
 *   <li>Otherwise, the value is considered valid.</li>
 * </ul>
 * 
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * @NullOrNotEmpty
 * private String fieldName;
 * }
 * </pre>
 * 
 * <p>Note: This validator does not perform deep validation for complex objects. 
 * It only checks the string representation of the object.</p>
 *
 * @author Akshay
 * @see NullOrNotEmpty
 * @see ConstraintValidator
 */
public class NullOrNotEmptyValidator implements ConstraintValidator<NullOrNotEmpty, Object> {

    @Override
    public void initialize(NullOrNotEmpty parameters) {
    }

    /**
     * Validates whether the given object is either null or not an empty string.
     *
     * @param value the object to validate; can be null
     * @param constraintValidatorContext the context in which the constraint is evaluated
     * @return {@code true} if the object is null or not an empty string; {@code false} otherwise
     */
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        if (Objects.isNull(value)) {
            return true;
        } else if (value.toString().isEmpty()) {
            return false;
        }
        return true;
    }
}
