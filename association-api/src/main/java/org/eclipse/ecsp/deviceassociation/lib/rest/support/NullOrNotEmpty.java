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
 * Custom annotation to validate that a field is either null or not empty.
 * This annotation can be applied to fields to ensure that they meet the
 * specified validation criteria.
 *
 * <p>Validation is performed using the {@link NullOrNotEmptyValidator} class.</p>
 *
 * <p>Usage example:</p>
 * <pre>
 * {@code
 * @NullOrNotEmpty
 * private String exampleField;
 * }
 * </pre>
 *
 * <p>Attributes:</p>
 * <ul>
 *   <li><b>message:</b> The default error message to be used when validation fails.
 *   Default value: "{jakarta.validation.constraints.NullOrNotEmpty.message}".</li>
 *   <li><b>groups:</b> Allows specification of validation groups. Default is an empty array.</li>
 *   <li><b>payload:</b> Can be used to carry metadata information. Default is an empty array.</li>
 * </ul>
 *
 * <p>Annotation metadata:</p>
 * <ul>
 *   <li><b>@Target:</b> Can be applied to fields.</li>
 *   <li><b>@Retention:</b> Retained at runtime.</li>
 *   <li><b>@Documented:</b> Included in the generated Javadoc.</li>
 *   <li><b>@Constraint:</b> Specifies the validator class {@link NullOrNotEmptyValidator}.</li>
 * </ul>
 */
@Target({ElementType.FIELD})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = NullOrNotEmptyValidator.class)
public @interface NullOrNotEmpty {

    /**
     * Specifies the default validation message to be used when the constraint is violated.
     * The message can be customized by providing a different value or using a message key
     * that resolves to a localized message in a resource bundle.
     *
     * @return the default validation message or message key
     */
    String message() default "{jakarta.validation.constraints.NullOrNotEmpty.message}";


    /**
     * Specifies the validation groups that this constraint belongs to.
     * Validation groups are used to control the order and grouping of validations.
     * By default, no groups are specified.
     *
     * @return an array of classes representing the validation groups
     */
    Class<?>[] groups() default { };

    /**
     * Specifies additional metadata information that can be carried with the constraint.
     * This can be used to provide additional context or information related to the validation.
     * By default, no payload is specified.
     *
     * @return an array of classes representing the payload information
     */
    Class<? extends Payload>[] payload() default {};
}
