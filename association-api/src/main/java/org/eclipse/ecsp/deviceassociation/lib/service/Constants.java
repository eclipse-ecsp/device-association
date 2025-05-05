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
 * A utility class that holds constant values used throughout the application.
 * This class is not meant to be instantiated.
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
     * MIME type for JSON content.
     */
    public static final String APPLICATION_JSON = "application/json";

    /**
     * HTTP header key for Content-Type.
     */
    public static final String CONTENT_TYPE = "Content-Type";

    /**
     * HTTP header key for Accept.
     */
    public static final String ACCEPT = "accept";

    /**
     * Key for client ID.
     */
    public static final String CLIENT_ID = "clientId";

    /**
     * Key for devices.
     */
    public static final String DEVICES = "devices";

    /**
     * Key for configuration.
     */
    public static final String CONFIG = "config";

    /**
     * Event ID for Vehicle Identification Number (VIN).
     */
    public static final String VIN_EVENT_ID = "VIN";

    /**
     * Event ID for Asset Activation.
     */
    public static final String ASSET_ACTIVATIONEVENT_ID = "AssetActivation";

    /**
     * Event ID for Firmware Version.
     */
    public static final String FIRMWARE_VERSION_EVENT_ID = "FirmwareVersion";

    /**
     * Identifier for platform-generated VIN.
     */
    public static final String PLATFORM_GENERATED_VIN = "HCP";

    /**
     * Constant for unavailable type.
     */
    public static final String TYPE_UNAVAILABLE = "UNAVAILABLE";

    /**
     * Key for VINs.
     */
    public static final String VINS = "vins";

    /**
     * Key for decode.
     */
    public static final String DECODE = "decode";

    /**
     * Constant for a question mark.
     */
    public static final String QUESTION_MARK = "?";

    /**
     * Key for type.
     */
    public static final String TYPE = "type";

    /**
     * Constant for equals sign.
     */
    public static final String EQUALS = "=";

    /**
     * Constant for ampersand (&).
     */
    public static final String AND = "&";

    /**
     * Key for model type.
     */
    public static final String TRIM = "modelType";

    /**
     * Key for system parameters.
     */
    public static final String SYS_PARAMS = "systemparameters";

    /**
     * Key for system parameter keys.
     */
    public static final String SYS_PARAM_KEYS = "systemparamkeys";

    /**
     * Key for models.
     */
    public static final String MODELS = "models";

    /**
     * Key for countries.
     */
    public static final String COUNTRIES = "countries";

    /**
     * Key for SIM cards.
     */
    public static final String SIMS = "sims";

    /**
     * Key for transactions.
     */
    public static final String TRANSACTIONS = "transactions";

    /**
     * HTTP header key for Content-Type.
     */
    public static final String HEADER_CONTENT_TYPE_KEY = "Content-Type";

    /**
     * Key for grant type in authentication.
     */
    public static final String GRANT_TYPE_KEY = "grant_type";

    /**
     * Value for password grant type.
     */
    public static final String PASSWORD_TYPE = "password";

    /**
     * Key for username.
     */
    public static final String USERNAME = "username";

    /**
     * Key for scope in authentication.
     */
    public static final String SCOPE_KEY = "scope";

    /**
     * HTTP header key for Authorization.
     */
    public static final String HEADER_NAME_AUTHORIZATION = "Authorization";

    /**
     * Scope value for authentication.
     */
    public static final String SCOPE_VALUE =
        "ManageUserRolesAndPermissions SelfManage ManageUsers ManageUserSubscriptionsAndBilling IgniteSystem";

    /**
     * Spring authentication grant type for client credentials.
     */
    public static final String SPRING_AUTH_CLIENT_CREDENTIALS = "client_credentials";

    /**
     * Spring authentication key for client ID.
     */
    public static final String SPRING_AUTH_CLIENT_ID = "client_id";

    /**
     * Spring authentication key for client secret.
     */
    public static final String SPRING_AUTH_CLIENT_SECRET = "client_secret";

    /**
     * Spring authentication scope value.
     */
    public static final String SPRING_AUTH_SCOPE_VALUE = "SelfManage OAuth2ClientMgmt IgniteSystem ManageUsers";

    /**
     * Spring authentication key for scopes.
     */
    public static final String SPRING_AUTH_SCOPE_KEY = "scopes";

    /**
     * Key for serial number.
     */
    public static final String SERIAL_NUMBER = "serialNumber";

    /**
     * Key for manufacturing date.
     */
    public static final String MANUFACTURING_DATE = "manufacturingDate";

    /**
     * Key for record date.
     */
    public static final String RECORD_DATE = "recordDate";

    /**
     * Key for model.
     */
    public static final String MODEL = "model";

    /**
     * Key for platform version.
     */
    public static final String PLATFORM_VERSION = "platformVersion";

    /**
     * Key for MSISDN (Mobile Station International Subscriber Directory Number).
     */
    public static final String MSISDN = "msisdn";

    /**
     * Key for IMEI (International Mobile Equipment Identity).
     */
    public static final String IMEI = "imei";

    /**
     * Key for ICCID (Integrated Circuit Card Identifier).
     */
    public static final String ICCID = "iccid";

    /**
     * Key for SSID (Service Set Identifier).
     */
    public static final String SSID = "ssid";

    /**
     * Key for BSSID (Basic Service Set Identifier).
     */
    public static final String BSSID = "bssid";

    /**
     * Key for IMSI (International Mobile Subscriber Identity).
     */
    public static final String IMSI = "imsi";

    /**
     * Key for package serial number.
     */
    public static final String PACKAGE_SERIAL_NUMBER = "packageSerialNumber";

    /**
     * Key for VIN (Vehicle Identification Number).
     */
    public static final String VIN = "vin";

    /**
     * Key for user name.
     */
    public static final String USER_NAME = "userName";

    /**
     * Key for session ID.
     */
    public static final String SESSIONID = "sessionId";

    /**
     * Key for SWM domain.
     */
    public static final String SWM_DOMAIN = "domain";

    /**
     * Value for SWM domain ID.
     */
    public static final String SWM_DOMAIN_ID_VAL = "40835552926145409927638592560729411453";

    /**
     * Key for SWM vehicle model year.
     */
    public static final String SWM_VEHICLE_MODEL_YEAR = "vehicleModelYear";

    /**
     * Constant for success status.
     */
    public static final String SUCCESS = "Success";

    /**
     * Constant for new action.
     */
    public static final String ACTION_NEW = "NEW";

    /**
     * Message indicating that the vehicle already exists.
     */
    public static final String SWM_VEHICLE_ALREADY_EXIST = "Vehicle already exists";

    /**
     * Key for whitelisted models.
     */
    public static final String WHITELISTED_MODELS = "whiteListedModels";

    /**
     * Key for association types.
     */
    public static final String ASSOC_TYPE = "associationTypes";

    /**
     * Key for association type.
     */
    public static final String ASSOC_TYPE_KEYS = "associationType";

    /**
     * Key for active status.
     */
    public static final String ACTIVE_STATUS = "activeStatus";

    /**
     * Constant for associated status.
     */
    public static final String ASSOCIATED = "ASSOCIATED";

    /**
     * Constant for active status.
     */
    public static final String ACTIVE = "ACTIVE";
}
