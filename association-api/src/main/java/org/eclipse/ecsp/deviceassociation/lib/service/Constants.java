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

package org.eclipse.ecsp.deviceassociation.lib.service;

/**
 * The Constants class contains constant values used in the application.
 */
public class Constants {

    /**
     * Private constructor to prevent instantiation of the Constants class.
     */
    private Constants() {

    }

    /**
     * URL Separator Constant.
     */
    public static final String URL_SEPARATOR = "/";

    /**
     * APPLICATION_JSON Constant.
     */
    public static final String APPLICATION_JSON = "application/json";

    /**
     * CONTENT TYPE constant.
     */
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String ACCEPT = "accept";

    public static final String CLIENT_ID = "clientId";
    public static final String DEVICES = "devices";
    public static final String CONFIG = "config";
    public static final String VIN_EVENT_ID = "VIN";
    public static final String ASSET_ACTIVATIONEVENT_ID = "AssetActivation";
    public static final String FIRMWARE_VERSION_EVENT_ID = "FirmwareVersion";
    public static final String PLATFORM_GENERATED_VIN = "HCP";
    public static final String TYPE_UNAVAILABLE = "UNAVAILABLE";
    public static final String VINS = "vins";
    public static final String DECODE = "decode";
    public static final String QUESTION_MARK = "?";
    public static final String TYPE = "type";
    public static final String EQUALS = "=";
    public static final String AND = "&";
    public static final String TRIM = "modelType";
    public static final String SYS_PARAMS = "systemparameters";
    public static final String SYS_PARAM_KEYS = "systemparamkeys";
    public static final String MODELS = "models";
    public static final String COUNTRIES = "countries";
    public static final String SIMS = "sims";
    public static final String TRANSACTIONS = "transactions";
    public static final String HEADER_CONTENT_TYPE_KEY = "Content-Type";
    public static final String GRANT_TYPE_KEY = "grant_type";
    public static final String PASSWORD_TYPE = "password";
    public static final String USERNAME = "username";
    public static final String SCOPE_KEY = "scope";
    public static final String HEADER_NAME_AUTHORIZATION = "Authorization";
    public static final String SCOPE_VALUE =
        "ManageUserRolesAndPermissions SelfManage ManageUsers ManageUserSubscriptionsAndBilling IgniteSystem";
    public static final String SPRING_AUTH_CLIENT_CREDENTIALS = "client_credentials";
    public static final String SPRING_AUTH_CLIENT_ID = "client_id";
    public static final String SPRING_AUTH_CLIENT_SECRET = "client_secret";
    public static final String SPRING_AUTH_SCOPE_VALUE = "SelfManage OAuth2ClientMgmt IgniteSystem ManageUsers";
    public static final String SPRING_AUTH_SCOPE_KEY = "scopes";

    // Device Association Fields
    public static final String SERIAL_NUMBER = "serialNumber";
    public static final String MANUFACTURING_DATE = "manufacturingDate";
    public static final String RECORD_DATE = "recordDate";
    public static final String MODEL = "model";
    public static final String PLATFORM_VERSION = "platformVersion";
    public static final String MSISDN = "msisdn";
    public static final String IMEI = "imei";
    public static final String ICCID = "iccid";
    public static final String SSID = "ssid";
    public static final String BSSID = "bssid";
    public static final String IMSI = "imsi";
    public static final String PACKAGE_SERIAL_NUMBER = "packageSerialNumber";
    public static final String VIN = "vin";

    // SWM constants
    public static final String USER_NAME = "userName";
    public static final String SESSIONID = "sessionId";
    public static final String SWM_DOMAIN = "domain";
    public static final String SWM_DOMAIN_ID_VAL = "40835552926145409927638592560729411453";
    public static final String SWM_VEHICLE_MODEL_YEAR = "vehicleModelYear";
    public static final String SUCCESS = "Success";
    public static final String ACTION_NEW = "NEW";
    public static final String SWM_VEHICLE_ALREADY_EXIST = "Vehicle already exists";

    public static final String WHITELISTED_MODELS = "whiteListedModels";

    public static final String ASSOC_TYPE = "associationTypes";
    public static final String ASSOC_TYPE_KEYS = "associationType";
    public static final String ACTIVE_STATUS = "activeStatus";

    public static final String ASSOCIATED = "ASSOCIATED";
    public static final String ACTIVE = "ACTIVE";

}
