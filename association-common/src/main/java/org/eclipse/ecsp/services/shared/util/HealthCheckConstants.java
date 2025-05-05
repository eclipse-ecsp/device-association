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

package org.eclipse.ecsp.services.shared.util;

/**
 * The {@code HealthCheckConstants} class contains constants related to health checks.
 */
public final class HealthCheckConstants {
    /**
     * Constant representing the status "OK".
     * This is typically used to indicate that a health check or operation
     * has completed successfully without any issues.
     */
    public static final String OK = "OK";

    /**
     * Constant representing the success code for a successful database connection.
     * A value of 1 indicates that the database connection was established successfully.
     */
    public static final int DB_CONN_SUCCESS_CODE = 1;

    /**
     * A constant representing the health check value.
     * This is used to denote the health status in the application.
     */
    public static final String HEALTH_VAL = "health";

    /**
     * Private constructor to prevent instantiation of the {@code HealthCheckConstants} class.
     */
    private HealthCheckConstants() {

    }
}
