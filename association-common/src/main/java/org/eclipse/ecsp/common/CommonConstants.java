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

package org.eclipse.ecsp.common;

/**
 * CommonConstants is a utility class that contains various constants used across the application.
 * It includes string constants, logging messages, and SQL query strings.
 * This class is not meant to be instantiated.
 */
public final class CommonConstants {

    /**
     * The authentication token header key.
     */
    public static final String AUTH_TOKEN = "AUTH-Token";

    /**
     * The Harman ID key.
     */
    public static final String HARMAN_ID = "HarmanID";

    /**
     * The random number key.
     */
    public static final String RANDOM_NUMBER = "RandomNumber";

    /**
     * The passcode key.
     */
    public static final String PASS_CODE = "PassCode";

    /**
     * The user ID key.
     */
    public static final String USER_ID = "UserID";

    /**
     * The first name key.
     */
    public static final String FIRST_NAME = "FirstName";

    /**
     * The last name key.
     */
    public static final String LAST_NAME = "LastName";

    /**
     * The email key.
     */
    public static final String EMAIL = "Email";

    /**
     * The ID key.
     */
    public static final String ID = "ID";

    /**
     * The registered scope ID key.
     */
    public static final String REGISTERED_SCOPE_ID = "registered_scope_id";

    /**
     * Log message for updating a role.
     */
    public static final String UPDATE_ROLE_LOG = "updateRole:{}";

    /**
     * Log message for showing an approve user action.
     */
    public static final String SHOW_APPROVE_USER_LOG = "showApproveUser:{}";

    /**
     * Log message for entering the getUpdatePackageInfo method.
     */
    public static final String GET_UPDATE_PACKAGE_INFO =
        "Entering getUpdatePackageInfo(UpdatePackageFilterBean updatePackage)";

    // DAO SQL constants

    /**
     * SQL query to select UserID grouped by UserID with a count of 1.
     */
    public static final String SELECT_USERID_GROUP_BY_USERID =
        "select \"UserID\" from \"UserOEM\" group by \"UserID\" having count(*)=1";

    /**
     * SQL query to select OEMID where UserID matches a given value.
     */
    public static final String SELECT_OEMID_WHERE_USERID = "select \"OEMID\" from \"UserOEM\" where \"UserID\"=?";

    /**
     * SQL query to select all records from DeviceInfoFactoryData with a condition.
     */
    public static final String SELECT_FROM_DEVICE_INFO_FACTORY_DATA_WHERE =
        "select * from public.\"DeviceInfoFactoryData\" where ";

    /**
     * SQL query to select the ID from DeviceInfoFactoryData with a condition.
     */
    public static final String SELECT_ID_FROM_DEVICE_INFO_FACTORY_DATA_WHERE =
        "select \"ID\" from public.\"DeviceInfoFactoryData\" where ";

    /**
     * SQL query to select UserID from UserOEM where OEMID is in a given list.
     */
    public static final String SELECT_USER_ID_FROM_USER_OEM = "SELECT \"UserID\" FROM \"UserOEM\" WHERE \"OEMID\" IN (";

    /**
     * SQL query to select all records from DeviceInfoFactoryData.
     */
    public static final String SELECT_FROM_DEVICE_INFO_FACTORY_DATA = "select * from public.\"DeviceInfoFactoryData\" ";

    /**
     * SQL join condition for DeviceInfoFactoryData and device_association.
     */
    public static final String JOIN_CONDITION =
        " WHERE \"DeviceInfoFactoryData\".\"ID\"=\"device_association\".\"factory_data\"";

    /**
     * SQL query to select all records from DeviceInfoFactoryData and device_association with a join.
     */
    public static final String SELECT_FROM_DEVICE_INFO_FACTORY_DATA_AND_ASSOCIATION =
        "select * from public.\"DeviceInfoFactoryData\" inner join public.\"device_association\" on " 
            + "\"DeviceInfoFactoryData\".\"ID\"=\"device_association\".\"factory_data\" ";

    /**
     * SQL query to select specific fields from DeviceInfoFactoryDataHistory for IMEI details.
     */
    public static final String SELECT_FROM_DEVICE_INFO_FACTORY_DATA_FOR_IMEI =
        "select \"ID\", \"manufacturing_date\", \"model\",\"imei\",\"serial_number\",\"platform_version\",\"iccid\"," 
            + "\"ssid\",\"bssid\",\"msisdn\",\"imsi\",\"record_date\",\"factory_created_date\","
            + "\"factory_admin\", \"state\",\"package_serial_number\" from public.\"DeviceInfoFactoryDataHistory\" ";

    /**
     * SQL join condition for DeviceInfoFactoryData and vin_details.
     */
    public static final String VIN_DETAILS_JOIN_CONDITION =
        " left outer join public.\"vin_details\" on \"DeviceInfoFactoryData\".\"ID\"=\"vin_details\".\"reference_id\"";

    /**
     * Private constructor to prevent instantiation of the class.
     */
    private CommonConstants() {
        // Prevent instantiation
    }
}
