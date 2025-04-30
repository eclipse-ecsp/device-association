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

/**
 * Validator to check if a given {@link Long} value is either null or not negative.
 * This validator is used in conjunction with the {@link NullOrNotEmptyLong} annotation.
 *
 * <p>Validation rules:
 * <ul>
 *   <li>If the value is {@code null}, it is considered valid.</li>
 *   <li>If the value is less than 0, it is considered invalid.</li>
 *   <li>Otherwise, the value is considered valid.</li>
 * </ul>
 *
 * <p>Example usage:
 * <pre>
 * {@code
 * @NullOrNotEmptyLong
 * private Long someValue;
 * }
 * </pre>
 *
 * <p>This class implements the {@link ConstraintValidator} interface to provide
 * custom validation logic for the {@link NullOrNotEmptyLong} annotation.
 */
public class NullOrNotEmptyLongValidator implements ConstraintValidator<NullOrNotEmptyLong, Long> {
    @Override
    public void initialize(NullOrNotEmptyLong parameters) {
    }

    /**
     * Validates a Long value to ensure it is either null or not negative.
     *
     * @param value the Long value to validate; can be null
     * @param constraintValidatorContext the context in which the constraint is evaluated
     * @return {@code true} if the value is null or non-negative; {@code false} if the value is negative
     */
    public boolean isValid(Long value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        } else if (value < 0) {
            return false;
        }
        return true;
    }
}
