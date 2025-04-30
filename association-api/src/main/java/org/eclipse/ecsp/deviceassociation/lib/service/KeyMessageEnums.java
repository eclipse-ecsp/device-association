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
 * This enum represents the key-value pairs for different error messages used in the application.
 * Each enum constant represents a specific error message along with its corresponding key.
 */
public enum KeyMessageEnums {
    PIN_VALIDATION_ERROR("pin_valid_err", "Pin validation failed for this device"),
    ASSO_ALREADY_EXISTS("asso_already_exists_err", "Association already exist for this device and user"),
    GENERAL_ERROR("general_error", "Not successful. Something went wrong. Please contact admin"),
    BASIC_DATA_MANDATORY("basic_data_mandatory", "Either BSSID or IMEI or serial number is mandatory????"),
    FETCHING_FACTORY_DATA_ERROR("issue_in_fetching_factory_data",
        "Please provide valid data to fetch factory data from database."),
    INVALID_FACTORY_STATE("invalid_factory_state",
        " Device already assigned to account. This device is already assigned to a different email account, {0}"
            + " . Please go to Help & Support and tap the Call Now button for Support."),
    USER_ID_MANDATORY("user_id_mandatory", "User ID is mandatory"),
    NO_VALID_ASSOERR("no_valid_asso", "No valid association found"),
    ASSO_DATA_NOT_FOUND("asso_data_missing", "Association data does not exist for given input."),
    INVALID_USER("invalid_user", "Invalid device details for the user."),
    INVALID_USER_ID_ERR_MSG("invalid_user_id_err_msg", "UserId from header either null or empty"),
    ASSO_NOT_FOUND("asso_not_found", "No association found for the device"),
    VIN_ALREADY_ASSO("vin_already_asso", "Vin already associated for this device"),
    VIN_ALREADY_ASSO_WITH_OTHER_DEVICE("vin_already_asso_with_other_device",
        "This vin is already associated to some other device"),
    VIN_ASSO_NOT_ENABLED("vin_asso_not_enabled", "vin association is not enabled. Please contact admin"),
    SUCCESS("success", "Success"),
    INVALID_ASSO("invalid_asso", "No association found or invalid association state"),
    VIN_NOT_ASSO("vin_not_asso", "vin is not associated for this device"),
    DONGLE_TYPE_MISMATCHED("dongle_type_mismatch", "Factory dongle type not matched with pre-defined dongle type"),
    SIM_ACTIVATION_FAILED("sim_activation_failed", "Sim activation Failed. Device Disassociated"),
    ;

    private String key;
    private String defaultMessage;

    /**
     * Constructs a KeyMessageEnums object with the specified key and default message.
     *
     * @param key     the key associated with the message
     * @param message the default message
     */
    private KeyMessageEnums(String key, String message) {
        this.key = key;
        this.defaultMessage = message;
    }

    /**
     * Returns the key associated with the message.
     *
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the default message.
     *
     * @return the default message
     */
    public String getDefaultMessage() {
        return defaultMessage;
    }
}