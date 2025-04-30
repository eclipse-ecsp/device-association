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
 * Custom annotation to validate that a field is either null or contains a non-empty Long value.
 * 
 * <p>This annotation can be applied to fields to enforce the validation logic defined
 * in the {@link NullOrNotEmptyLongValidator} class.</p>
 * 
 * <p>Usage example:</p>
 * <pre>
 * {@code
 * @NullOrNotEmptyLong
 * private Long someField;
 * }
 * </pre>
 * 
 * <p>Attributes:</p>
 * <ul>
 *   <li><b>message:</b> Custom error message to be used when validation fails. Defaults to
 *   "{jakarta.validation.constraints.NullOrNotEmptyLong.message}".</li>
 *   <li><b>groups:</b> Allows specification of validation groups. Defaults to an empty array.</li>
 *   <li><b>payload:</b> Can be used by clients to assign custom payload objects to a constraint. 
 *   Defaults to an empty array.</li>
 * </ul>
 * 
 * <p>Annotation metadata:</p>
 * <ul>
 *   <li><b>@Target:</b> Can be applied to fields.</li>
 *   <li><b>@Retention:</b> Retained at runtime.</li>
 *   <li><b>@Documented:</b> Included in the generated Javadoc.</li>
 *   <li><b>@Constraint:</b> Specifies the validator class {@link NullOrNotEmptyLongValidator}.</li>
 * </ul>
 *
 * @see NullOrNotEmptyLongValidator
 */
@Target({ElementType.FIELD})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = NullOrNotEmptyLongValidator.class)
public @interface NullOrNotEmptyLong {

    /**
     * Specifies the default validation message for the constraint.
     * The message can be customized by providing a different value
     * or using a resource bundle key for internationalization.
     *
     * @return the default validation message or a resource bundle key
     *         (e.g., "{jakarta.validation.constraints.NullOrNotEmptyLong.message}")
     */
    String message() default "{jakarta.validation.constraints.NullOrNotEmptyLong.message}";

    /**
     * Specifies the groups the constraint belongs to. This can be used to apply 
     * validation groups to different parts of the application. By default, it 
     * is an empty array, meaning the constraint applies to all validation groups.
     *
     * @return an array of classes representing the validation groups
     */
    Class<?>[] groups() default { };
    
    /**
     * Specifies an array of custom payload objects that can be used by clients
     * of the annotation to provide additional information or metadata.
     *
     * @return an array of classes extending {@link Payload}, which can be used
     *         to carry custom metadata or information for the annotation.
     */
    Class<? extends Payload>[] payload() default {};
}
