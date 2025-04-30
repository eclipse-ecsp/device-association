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

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to validate that a list field is either null or not empty.
 * This constraint ensures that the annotated list is either not provided (null)
 * or contains at least one element.
 *
 * <p>Usage example:</p>
 * <pre>
 * &#64;NullOrNotEmptyList
 * private List items;
 * </pre>
 *
 * <p>Validation is performed by the {@code NullOrNotEmptyListValidator} class.</p>
 *
 * <p>Attributes:</p>
 * <ul>
 *   <li><b>message</b>: Custom error message to be used when validation fails.
 *       Default is "{jakarta.validation.constraints.NullOrNotEmptyList.message}".</li>
 *   <li><b>groups</b>: Groups for categorizing constraints.</li>
 *   <li><b>payload</b>: Payload for clients to specify additional metadata.</li>
 * </ul>
 *
 * <p>Annotation targets:</p>
 * <ul>
 *   <li>Fields</li>
 * </ul>
 *
 * <p>Retention policy:</p>
 * <ul>
 *   <li>Runtime</li>
 * </ul>
 *
 * <p>Required dependencies:</p>
 * <ul>
 *   <li>Jakarta Bean Validation API</li>
 * </ul>
 *
 * @see NullOrNotEmptyListValidator
 */
@Target({ElementType.FIELD})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = NullOrNotEmptyListValidator.class)
public @interface NullOrNotEmptyList {

    /**
     * Custom error message to be used when validation fails.
     * Default is "{jakarta.validation.constraints.NullOrNotEmptyList.message}".
     *
     * @return the error message
     */
    String message() default "{jakarta.validation.constraints.NullOrNotEmptyList.message}";

    /**
     * Groups for categorizing constraints.
     *
     * @return the groups
     */
    Class<?>[] groups() default { };

    /**
     * Payload for clients to specify additional metadata.
     *
     * @return the payload
     */
    Class<? extends Payload>[] payload() default {};
}
