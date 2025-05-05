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

package org.eclipse.ecsp.services.shared.constants;

/**
 * The {@code DatabaseConstants} class contains constants related to the database.
 */
public final class DatabaseConstants {
    /**
     * The timestamp format used for device information factory data.
     * This format follows the pattern "yyyy/MM/dd", where:
     * <ul>
     *   <li>yyyy - Represents the year in four digits.</li>
     *   <li>MM - Represents the month in two digits (01-12).</li>
     *   <li>dd - Represents the day of the month in two digits (01-31).</li>
     * </ul>
     */
    public static final String DEVICEINFOFACTORYDATA_TIMESTAMP_FORMAT = "yyyy/MM/dd";

    /**
     * Private constructor to prevent instantiation.
     */
    private DatabaseConstants() {

    }
}
