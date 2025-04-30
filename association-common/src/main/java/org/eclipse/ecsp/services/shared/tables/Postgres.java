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

package org.eclipse.ecsp.services.shared.tables;

/**
 * The {@code Postgres} class represents a set of constants for the Postgres database tables used in the device
 * association module.
 * It provides the table names and column names as public static final strings.
 */
public final class Postgres {
    public static final String DEVICE_ASSOCIATION = "device_association";
    public static final String DEVICE = "\"Device\"";
    public static final String HCPINFO = "\"HCPInfo\"";
    public static final String DEVICEINFOFACTORYDATA = "\"DeviceInfoFactoryData\"";
    public static final String DEVICE_ACTIVATION_STATE = "device_activation_state";
    public static final String DEVICEINFO = "\"DeviceInfo\"";

    /**
     * Constructs a new instance of the Postgres class.
     * This constructor is private to prevent direct instantiation of the class.
     */
    private Postgres() {

    }
}
