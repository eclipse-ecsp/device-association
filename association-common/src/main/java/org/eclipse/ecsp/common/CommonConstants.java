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
 * This class contains common constants used in the application.
 */
public final class CommonConstants {

    // String constants
    public static final String AUTH_TOKEN = "AUTH-Token";
    public static final String HARMAN_ID = "HarmanID";
    public static final String RANDOM_NUMBER = "RandomNumber";
    public static final String PASS_CODE = "PassCode";
    public static final String USER_ID = "UserID";
    public static final String FIRST_NAME = "FirstName";
    public static final String LAST_NAME = "LastName";
    public static final String EMAIL = "Email";
    public static final String ID = "ID";
    public static final String REGISTERED_SCOPE_ID = "registered_scope_id";

    // Logging
    public static final String UPDATE_ROLE_LOG = "updateRole:{}";
    public static final String SHOW_APPROVE_USER_LOG = "showApproveUser:{}";
    public static final String GET_UPDATE_PACKAGE_INFO =
        "Entering getUpdatePackageInfo(UpdatePackageFilterBean updatePackage)";

    // DAO SQL constants
    public static final String SELECT_USERID_GROUP_BY_USERID =
        "select \"UserID\" from \"UserOEM\" group by \"UserID\" having count(*)=1";
    public static final String SELECT_OEMID_WHERE_USERID = "select \"OEMID\" from \"UserOEM\" where \"UserID\"=?";
    public static final String SELECT_FROM_DEVICE_INFO_FACTORY_DATA_WHERE =
        "select * from public.\"DeviceInfoFactoryData\" where ";
    public static final String SELECT_ID_FROM_DEVICE_INFO_FACTORY_DATA_WHERE =
        "select \"ID\" from public.\"DeviceInfoFactoryData\" where ";
    public static final String SELECT_USER_ID_FROM_USER_OEM = "SELECT \"UserID\" FROM \"UserOEM\" WHERE \"OEMID\" IN (";
    public static final String SELECT_FROM_DEVICE_INFO_FACTORY_DATA = "select * from public.\"DeviceInfoFactoryData\" ";
    public static final String JOIN_CONDITION =
        " WHERE \"DeviceInfoFactoryData\".\"ID\"=\"device_association\".\"factory_data\"";
    public static final String SELECT_FROM_DEVICE_INFO_FACTORY_DATA_AND_ASSOCIATION =
        "select * from public.\"DeviceInfoFactoryData\" inner join public.\"device_association\" on " 
            +            "\"DeviceInfoFactoryData\".\"ID\"=\"device_association\".\"factory_data\" ";
    public static final String SELECT_FROM_DEVICE_INFO_FACTORY_DATA_FOR_IMEI =
        "select \"ID\", \"manufacturing_date\", \"model\",\"imei\",\"serial_number\",\"platform_version\",\"iccid\"," 
            +            "\"ssid\",\"bssid\",\"msisdn\",\"imsi\",\"record_date\",\"factory_created_date\","
            + "\"factory_admin\", \"state\",\"package_serial_number\" from public.\"DeviceInfoFactoryDataHistory\" ";
    public static final String VIN_DETAILS_JOIN_CONDITION =
        " left outer join public.\"vin_details\" on \"DeviceInfoFactoryData\".\"ID\"=\"vin_details\".\"reference_id\"";

    /**
     * Private constructor to prevent instantiation of the class.
     */
    private CommonConstants() {

    }
}
