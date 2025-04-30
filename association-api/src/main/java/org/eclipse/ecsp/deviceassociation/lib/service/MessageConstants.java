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
 * This interface contains constant messages used in the device association service.
 */
public interface MessageConstants {

    String INVALID_USER_PROVIDED = "Invalid User provided";
    String ASSOCIATION_DATA_DOES_NOT_EXIST_FOR_GIVEN_INPUT = "Association data does not exist for given input.";
    String BASIC_DATA_MANDATORY = "Either BSSID or IMEI or serial number is mandatory";
    String USER_ID_MANDATORY = "User ID is mandatory";
    String ISSUE_IN_FETCHING_FACTORY_DATA = "Please provide valid data to fetch factory data from database.";
    String EITHER_IMEI_OR_DEVICE_ID_OR_SERIAL_NUMBER_ARE_MANDATORY =
        "Either IMEI or DeviceID or SerialNumber are mandatory";
    String INVALID_STATE_TO_ASSOCIATE = "Device not in suspended status to restore back to Associate state";
    String INVALID_FACTORY_STATE = "This device is already assigned to a different email account, ";
    String HELP_MSG = " . Please go to Help & Support and tap the Call Now button for Support.";
    String INVALID_REPLACE_REQUEST_DATA = "Invalid Device replace request data.";
    String INVALID_CURRENT_FACTORY_DATA_FOR_REPLACE = "No data is found in inventory for the passed current value";
    String INVALID_CURRENT_FACTORY_DATA_FOR_DELETE = "No data is found in inventory for the requested inputs";
    String INVALID_DEVICE_REPLACEMENT_CURRENT_DATA_STATE = "Must be in either Faulty/Stolen state";
    String INVALID_DEVICE_REPLACEMENT_REPLACE_DATA_STATE = "Must be in Provisioned state";
    String INVALID_INACTIVATED_DEVICE_FOR_REPLACEMENT =
        "Passed current factory data is not activated before. You can replace only an activated device";
    String BASIC_MANDATORY_STATE_CHANGE = "Either deviceid or imei is mandatory";
    String NO_DATA_FOUND = "No data found";
    String ASSOCIATION_HISTORY_NOT_FOUND =
        "Failed to retrieve device based user association history details, Root Cause:Association details not found"
            + " in Database";
    String STATE_MANDATORY = "Device state is mandatory";
    String USER_ID = "user-id";
    String INVALID_STATE_CHANGE_CRITERIA = "Please provide either deviceid or imei.";
    String INVALID_USER_DEVICE_DETAILS = "Invalid device details for the user.";
    String DATABASE_INTEGRITY_ERROR = "Database Integrity Error.";
    String INVALID_ASSOCIATION_GETDETAILS_STATE =
        "Either requested device is not associated with any user or the details are not valid.";
    String BASIC_DEVICE_DATA_MANDATORY = "Either device id or IMEI or serial number is mandatory";
    String STOLEN_OR_FAULTY_MSG =
        "This device does not have an active subscription. Please go to Help & Support and tap the Call Now button for"
            + " Contacting Support.";
    String INVALID_FACTORY_STATE_HEADER = " Device already assigned to account";
    String REGISTRATION_FAILURE_MSG = "Registration failure";
    String TERMINATION_MSG = "Association failure";
    String DEVICE_TERMINATED_MSG = "The device is terminated and not allowed to be associated again";
    String TERMINATE_REQUIRED_FOR = "wipeData";
}