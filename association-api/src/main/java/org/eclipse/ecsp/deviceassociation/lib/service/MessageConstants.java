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

    /**
     * Message indicating an invalid user was provided.
     */
    String INVALID_USER_PROVIDED = "Invalid User provided";

    /**
     * Message indicating that association data does not exist for the given input.
     */
    String ASSOCIATION_DATA_DOES_NOT_EXIST_FOR_GIVEN_INPUT = "Association data does not exist for given input.";

    /**
     * Message indicating that either BSSID, IMEI, or serial number is mandatory.
     */
    String BASIC_DATA_MANDATORY = "Either BSSID or IMEI or serial number is mandatory";

    /**
     * Message indicating that the user ID is mandatory.
     */
    String USER_ID_MANDATORY = "User ID is mandatory";

    /**
     * Message indicating an issue in fetching factory data from the database.
     */
    String ISSUE_IN_FETCHING_FACTORY_DATA = "Please provide valid data to fetch factory data from database.";

    /**
     * Message indicating that either IMEI, DeviceID, or SerialNumber is mandatory.
     */
    String EITHER_IMEI_OR_DEVICE_ID_OR_SERIAL_NUMBER_ARE_MANDATORY =
        "Either IMEI or DeviceID or SerialNumber are mandatory";

    /**
     * Message indicating an invalid state to associate a device.
     */
    String INVALID_STATE_TO_ASSOCIATE = "Device not in suspended status to restore back to Associate state";

    /**
     * Message indicating an invalid factory state for the device.
     */
    String INVALID_FACTORY_STATE = "This device is already assigned to a different email account, ";

    /**
     * Help message for contacting support.
     */
    String HELP_MSG = " . Please go to Help & Support and tap the Call Now button for Support.";

    /**
     * Message indicating invalid device replacement request data.
     */
    String INVALID_REPLACE_REQUEST_DATA = "Invalid Device replace request data.";

    /**
     * Message indicating no data found in inventory for the current value during replacement.
     */
    String INVALID_CURRENT_FACTORY_DATA_FOR_REPLACE = "No data is found in inventory for the passed current value";

    /**
     * Message indicating no data found in inventory for the requested inputs during deletion.
     */
    String INVALID_CURRENT_FACTORY_DATA_FOR_DELETE = "No data is found in inventory for the requested inputs";

    /**
     * Message indicating invalid device replacement current data state.
     */
    String INVALID_DEVICE_REPLACEMENT_CURRENT_DATA_STATE = "Must be in either Faulty/Stolen state";

    /**
     * Message indicating invalid device replacement replace data state.
     */
    String INVALID_DEVICE_REPLACEMENT_REPLACE_DATA_STATE = "Must be in Provisioned state";

    /**
     * Message indicating an invalid inactivated device for replacement.
     */
    String INVALID_INACTIVATED_DEVICE_FOR_REPLACEMENT =
        "Passed current factory data is not activated before. You can replace only an activated device";

    /**
     * Message indicating that either device ID or IMEI is mandatory for state change.
     */
    String BASIC_MANDATORY_STATE_CHANGE = "Either deviceid or imei is mandatory";

    /**
     * Message indicating no data was found.
     */
    String NO_DATA_FOUND = "No data found";

    /**
     * Message indicating that association history details could not be retrieved.
     */
    String ASSOCIATION_HISTORY_NOT_FOUND =
        "Failed to retrieve device based user association history details, Root Cause:Association details not found"
            + " in Database";

    /**
     * Message indicating that the device state is mandatory.
     */
    String STATE_MANDATORY = "Device state is mandatory";

    /**
     * Key for the user ID.
     */
    String USER_ID = "user-id";

    /**
     * Message indicating invalid state change criteria.
     */
    String INVALID_STATE_CHANGE_CRITERIA = "Please provide either deviceid or imei.";

    /**
     * Message indicating invalid device details for the user.
     */
    String INVALID_USER_DEVICE_DETAILS = "Invalid device details for the user.";

    /**
     * Message indicating a database integrity error.
     */
    String DATABASE_INTEGRITY_ERROR = "Database Integrity Error.";

    /**
     * Message indicating invalid association get details state.
     */
    String INVALID_ASSOCIATION_GETDETAILS_STATE =
        "Either requested device is not associated with any user or the details are not valid.";

    /**
     * Message indicating that either device ID, IMEI, or serial number is mandatory.
     */
    String BASIC_DEVICE_DATA_MANDATORY = "Either device id or IMEI or serial number is mandatory";

    /**
     * Message indicating that the device does not have an active subscription.
     */
    String STOLEN_OR_FAULTY_MSG =
        "This device does not have an active subscription. Please go to Help & Support and tap the Call Now button for"
            + " Contacting Support.";

    /**
     * Header message indicating an invalid factory state.
     */
    String INVALID_FACTORY_STATE_HEADER = " Device already assigned to account";

    /**
     * Message indicating registration failure.
     */
    String REGISTRATION_FAILURE_MSG = "Registration failure";

    /**
     * Message indicating association failure.
     */
    String TERMINATION_MSG = "Association failure";

    /**
     * Message indicating that the device is terminated and cannot be associated again.
     */
    String DEVICE_TERMINATED_MSG = "The device is terminated and not allowed to be associated again";

    /**
     * Message indicating that termination is required for a specific operation.
     */
    String TERMINATE_REQUIRED_FOR = "wipeData";
}