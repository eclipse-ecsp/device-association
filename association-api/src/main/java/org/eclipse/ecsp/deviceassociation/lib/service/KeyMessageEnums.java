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
    /**
     * Represents an error when PIN validation fails for a device.
     */
    PIN_VALIDATION_ERROR("pin_valid_err", "Pin validation failed for this device"),

    /**
     * Represents an error when an association already exists for a device and user.
     */
    ASSO_ALREADY_EXISTS("asso_already_exists_err", "Association already exist for this device and user"),

    /**
     * Represents a general error when something goes wrong and the operation is not successful.
     */
    GENERAL_ERROR("general_error", "Not successful. Something went wrong. Please contact admin"),

    /**
     * Indicates that either BSSID, IMEI, or serial number is mandatory for the operation.
     */
    BASIC_DATA_MANDATORY("basic_data_mandatory", "Either BSSID or IMEI or serial number is mandatory????"),

    /**
     * Represents an error when there is an issue in fetching factory data from the database.
     */
    FETCHING_FACTORY_DATA_ERROR("issue_in_fetching_factory_data",
        "Please provide valid data to fetch factory data from database."),

    /**
     * Indicates that the device is already assigned to a different email account.
     */
    INVALID_FACTORY_STATE("invalid_factory_state",
        " Device already assigned to account. This device is already assigned to a different email account, {0}"
            + " . Please go to Help & Support and tap the Call Now button for Support."),

    /**
     * Indicates that the user ID is mandatory for the operation.
     */
    USER_ID_MANDATORY("user_id_mandatory", "User ID is mandatory"),

    /**
     * Represents an error when no valid association is found.
     */
    NO_VALID_ASSOERR("no_valid_asso", "No valid association found"),

    /**
     * Indicates that association data does not exist for the given input.
     */
    ASSO_DATA_NOT_FOUND("asso_data_missing", "Association data does not exist for given input."),

    /**
     * Represents an error when invalid device details are provided for the user.
     */
    INVALID_USER("invalid_user", "Invalid device details for the user."),

    /**
     * Indicates that the UserId from the header is either null or empty.
     */
    INVALID_USER_ID_ERR_MSG("invalid_user_id_err_msg", "UserId from header either null or empty"),

    /**
     * Represents an error when no association is found for the device.
     */
    ASSO_NOT_FOUND("asso_not_found", "No association found for the device"),

    /**
     * Indicates that the VIN is already associated with this device.
     */
    VIN_ALREADY_ASSO("vin_already_asso", "Vin already associated for this device"),

    /**
     * Indicates that the VIN is already associated with another device.
     */
    VIN_ALREADY_ASSO_WITH_OTHER_DEVICE("vin_already_asso_with_other_device",
        "This vin is already associated to some other device"),

    /**
     * Represents an error when VIN association is not enabled.
     */
    VIN_ASSO_NOT_ENABLED("vin_asso_not_enabled", "vin association is not enabled. Please contact admin"),

    /**
     * Indicates a successful operation.
     */
    SUCCESS("success", "Success"),

    /**
     * Represents an error when no association is found or the association state is invalid.
     */
    INVALID_ASSO("invalid_asso", "No association found or invalid association state"),

    /**
     * Indicates that the VIN is not associated with this device.
     */
    VIN_NOT_ASSO("vin_not_asso", "vin is not associated for this device"),

    /**
     * Represents an error when the factory dongle type does not match the predefined dongle type.
     */
    DONGLE_TYPE_MISMATCHED("dongle_type_mismatch", "Factory dongle type not matched with pre-defined dongle type"),

    /**
     * Represents an error when SIM activation fails and the device is disassociated.
     */
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