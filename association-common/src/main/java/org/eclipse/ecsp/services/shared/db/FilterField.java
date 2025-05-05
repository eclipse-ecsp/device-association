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

package org.eclipse.ecsp.services.shared.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * FilterField annotation is used to mark fields that may participate in the building of the where clause.
 * Only fields that are assigned a value and marked with FilterField annotation will be used to build the where clause
 * Current implementation works only with primitive types  (int/Integer, long/Long, float/Float, double/Double,
 * boolean/Boolean),
 * Strings, dates and Lists or arrays of these types.
 * FilterField annotation has following parameters:
 * dbname - defines the field name to be used. If dbname is not set, the field name will be used
 * novalue - defines a "no value" value. By default, if the field value is null it will not be used in the where clause
 * in some cases (for instance primitive numbers) we may want to define an alternative value
 * range - this parameter is used to specify that the field is a min or max value of the range
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FilterField {
    /**
     * A constant representing an empty string.
     * This can be used as a placeholder or default value
     * for fields that are expected to be null or empty.
     */
    public static final String NULL = "";

    /**
     * Specifies the name of the database field associated with this filter field.
     *
     * @return the name of the database field, or {@code NULL} if not specified.
     */
    String dbname() default NULL;

    /**
     * Specifies the default value for the field when no value is provided.
     *
     * @return The default value as a string. Defaults to "NULL".
     */
    String novalue() default NULL;

    /**
     * Specifies the range constraint for the field.
     * The default value is {@link Range#NONE}, indicating no range constraint.
     *
     * @return the range constraint for the field
     */
    Range range() default Range.NONE;

    /**
     * Enum representing the range types for a filter field.
     * <ul>
     *   <li>{@code MIN} - Represents the minimum range.</li>
     *   <li>{@code MAX} - Represents the maximum range.</li>
     *   <li>{@code NONE} - Represents no range.</li>
     * </ul>
     */
    public enum Range {
        /**
         * Represents the minimum range.
         */
        MIN, 
        /**
         * Represents the maximum range.
         */
        MAX, 
        /**
         * Represents no range.
         */
        NONE
    }
}
