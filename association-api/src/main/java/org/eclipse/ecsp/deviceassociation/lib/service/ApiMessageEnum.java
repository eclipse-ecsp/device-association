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

import java.text.MessageFormat;

/**
 * This enum represents the API message codes and their corresponding descriptions for the device association service.
 * Each enum constant consists of a code, a message, and a description.
 * The code is a unique identifier for the message.
 * The message provides a brief summary of the message.
 * The description provides additional details about the message.
 */
public enum ApiMessageEnum {
    /**
     * General error indicating an internal server error.
     */
    GENERAL_ERROR("assoc-777", "Internal server error", "Not successful. Something went wrong. Please contact admin."),
    /**
     * Validation error indicating that either BSSID, IMEI, or serial number is mandatory.
     */
    BASIC_DATA_MANDATORY("assoc-001", "Validation failed", "Either BSSID or IMEI or serial number is mandatory."),
    /**
     * Validation error indicating invalid data provided to fetch factory data from the database.
     */
    FETCHING_FACTORY_DATA_ERROR("assoc-002", "Validation failed",
        "Please provide valid data to fetch factory data from database."),
    /**
     * Validation error indicating the device is already assigned to a different account.
     */
    INVALID_FACTORY_STATE("assoc-003", "Validation failed",
        "Device already assigned to account. This device is already assigned to a different email account. Please go"
            + " to Help & Support and tap the Call Now button for Support."),
    /**
     * Validation error indicating association data does not exist for the given input.
     */
    ASSO_DATA_NOT_FOUND("assoc-004", "Validation failed", "Association data does not exist for given input."),
    /**
     * Validation error indicating the UserId from the header is either null or empty.
     */
    INVALID_USER_ID_ERR_MSG("assoc-005", "Validation failed", "UserId from header either null or empty."),
    /**
     * Success message indicating the association with the user was completed successfully.
     */
    ASSOCIATION_SUCCESS("assoc-006", "Success", "Association with user done successfully."),
    /**
     * Precondition failed error indicating the device is terminated and cannot be associated again.
     */
    DEVICE_TERMINATED_MSG("assoc-007", "PreCondition failed",
        "The device is terminated and not allowed to be associated again"),
    /**
     * Precondition failed error indicating the device does not have an active subscription.
     */
    STOLEN_OR_FAULTY_MSG("assoc-008", "PreCondition failed",
        "This device does not have an active subscription. Please go to Help & Support and tap the Call Now button for"
            + " Contacting Support."),
    /**
     * Validation error indicating the User ID is mandatory.
     */
    USER_ID_MANDATORY("assoc-009", "Validation failed", "User ID is mandatory."),
    /**
     * Validation error indicating no valid association was found.
     */
    NO_VALID_ASSOERR("assoc-010", "Validation failed", "No valid association found."),
    /**
     * Success message indicating the user disassociation with the device was completed successfully.
     */
    TERMINATE_ASSO_SUCCESS("assoc-011", "Success", "User dis-association with device completed successfully."),
    /**
     * Precondition failed error indicating a database integrity error with association data.
     */
    ASSO_INTEGRITY_ERROR("assoc-012", "PreCondition failed",
        "Association data - Database Integrity Error. There is more than one record."),
    /**
     * Internal server error indicating a failure to send a notification to the user.
     */
    ASSO_NOTIF_ERROR("assoc-013", "Internal server error", "Failed to send notification to user."),
    /**
     * Precondition failed error indicating no association was found for the device.
     */
    ASSO_NOT_FOUND("assoc-014", "PreCondition failed", "No association found for the device."),
    /**
     * Precondition failed error indicating the VIN is already associated with this device.
     */
    VIN_ALREADY_ASSO("assoc-015", "PreCondition failed", "Vin already associated for this device."),
    /**
     * Precondition failed error indicating the VIN is already associated with another device.
     */
    VIN_ALREADY_ASSO_WITH_OTHER_DEVICE("assoc-016", "PreCondition failed",
        "This vin is already associated to some other device."),
    /**
     * Precondition failed error indicating VIN association is not enabled.
     */
    VIN_ASSO_NOT_ENABLED("assoc-017", "PreCondition failed", "vin association is not enabled. Please contact admin."),
    /**
     * Success message indicating the VIN association was completed successfully.
     */
    VIN_ASSO_SUCCESS("assoc-018", "Success", "Vin association completed successfully."),
    /**
     * Precondition failed error indicating a mismatch in the factory dongle type.
     */
    DONGLE_TYPE_MISMATCHED("assoc-019", "PreCondition failed",
        "Factory dongle type not matched with pre-defined dongle type"),
    /**
     * Precondition failed error indicating SIM activation failed, and the device was disassociated.
     */
    SIM_ACTIVATION_FAILED("assoc-020", "PreCondition failed", "Sim activation failed, device disassociated."),
    /**
     * Precondition failed error indicating no association was found or the association state is invalid.
     */
    INVALID_ASSO("assoc-021", "PreCondition failed", "No association found or invalid association state."),
    /**
     * Precondition failed error indicating the VIN is not associated with this device.
     */
    VIN_NOT_ASSO("assoc-022", "PreCondition failed", "Vin is not associated for this device."),
    /**
     * Success message indicating user association details were retrieved successfully.
     */
    FIND_ASSO("assoc-023", "Success", "User association details retrieved successfully."),
    /**
     * Success message indicating the VIN was replaced successfully.
     */
    REPLACE_VIN_SUCCESS("assoc-024", "Success", "Vin is is replaced successfully."),
    /**
     * Not found error indicating association details were not found.
     */
    ASSO_DETAILS_NOT_FOUND("assoc-025", "Not found", "Association details not found."),
    /**
     * Not supported error indicating the API version is not supported.
     */
    NOT_SUPPORTED_VERSION("assoc-026", "Not supported", "This version of api is not supported."),
    /**
     * Success message indicating the device state was changed successfully.
     */
    DEVICE_STATE_CHANGE("assoc-027", "Success", "Device state changed successfully."),
    /**
     * Validation error indicating either device ID, IMEI, or serial number is mandatory.
     */
    BASIC_DEVICE_DATA_MANDATORY("assoc-028", "Validation failed",
        "Either device id or IMEI or serial number is mandatory."),
    /**
     * Validation error indicating the requested device is not associated with any user or the details are invalid.
     */
    INVALID_ASSOCIATION_GETDETAILS_STATE("assoc-029", "Validation",
        "Either requested device is not associated with any user or the details are not valid."),
    /**
     * Resource not found error indicating device association details were not found for the requested inputs.
     */
    ASSOCIATION_DETAILS_NOT_FOUND("assoc-030", "Resource not found",
        "Device association details not found for the requested inputs"),
    /**
     * Success message indicating the device restore was completed successfully.
     */
    DEVICE_RESTORE("assoc-031", "Success", "Device restore completed successfully"),
    /**
     * Resource not found error indicating no valid association was found.
     */
    DEVICE_RESTORE_FAILED("assoc-032", "Resource not found", "No valid association found"),
    /**
     * Precondition failed error indicating a database integrity error with association data.
     */
    DEVICE_ASSO_INTEGRITY_ERR("assoc-033", "PreCondition failed",
        "Association data - Database Integrity Error. There is more than one record."),
    /**
     * Validation error indicating either IMEI, DeviceID, or SerialNumber is mandatory.
     */
    EITHER_IMEI_OR_DEVICE_ID_OR_SERIAL_NUMBER_ARE_MANDATORY("assoc-034", "Validation failed",
        "Either IMEI or DeviceID or SerialNumber are mandatory."),
    /**
     * Success message indicating the device suspend was completed successfully.
     */
    DEVICE_SUSPEND_SUCCESS("assoc-035", "Success", "Device suspend completed successfully."),
    /**
     * Success message indicating the update of the status to SUSPENDED was unsuccessful.
     */
    SUSPEND_FAILED("assoc-036", "Success",
        "Update of status to SUSPENDED in device_association table was un-successful."),
    /**
     * Internal server error indicating a failure to de-register the device with Spring Auth.
     */
    DE_REGISTER_WITH_SPRINGAUTH_FAILED("assoc-037", "Internal server error", "Failed to de-register device with "
        + "Spring Auth"),
    /**
     * Internal server error indicating a failure to register the device with Spring Auth.
     */
    REGISTER_WITH_SPRINGAUTH_FAILED("assoc-038", "Internal server error", "Failed to register device with Spring Auth"),
    /**
     * Success message indicating the device was replaced successfully.
     */
    REPLACE_DEVICE_SUCCESS("assoc-039", "Success", "Device replaced successfully."),
    /**
     * Validation error indicating invalid device replace request data.
     */
    INVALID_REPLACE_REQUEST_DATA("assoc-040", "Validation failed", "Invalid Device replace request data."),
    /**
     * Resource not found error indicating no data was found in the inventory for the passed current value.
     */
    INVALID_CURRENT_FACTORY_DATA_FOR_REPLACE("assoc-041", "Resource not found",
        "No data is found in inventory for the passed current value."),
    /**
     * Validation error indicating the current device must be in either Faulty or Stolen state.
     */
    INVALID_DEVICE_REPLACEMENT_CURRENT_DATA_STATE("assoc-042", "Validation failed",
        "Current device must be in either Faulty/Stolen state."),
    /**
     * Internal server error indicating a database integrity error during factory data replacement.
     */
    DATABASE_INTEGRITY_ERROR("assoc-043", "Internal server error", "Replace factory data - Database Integrity Error."),
    /**
     * Precondition failed error indicating the current device must be in the Provisioned state.
     */
    INVALID_DEVICE_REPLACEMENT_REPLACE_DATA_STATE("assoc-044", "PreCondition failed",
        "Current device must be in Provisioned state."),
    /**
     * Precondition failed error indicating the passed current factory data was not activated before.
     */
    INVALID_INACTIVATED_DEVICE_FOR_REPLACEMENT("assoc-045", "PreCondition failed",
        "Passed current factory data is not activated before. You can replace only an activated device."),
    /**
     * Resource not found error indicating association data does not exist for the given input.
     */
    ASSOCIATION_DATA_DOES_NOT_EXIST_FOR_GIVEN_INPUT("assoc-046", "Resource not found",
        "Association data does not exist for given input."),
    /**
     * Validation error indicating the device state is mandatory.
     */
    STATE_MANDATORY("assoc-047", "Validation failed", "Device state is mandatory."),
    /**
     * Validation error indicating either device ID or IMEI is mandatory.
     */
    BASIC_MANDATORY_STATE_CHANGE("assoc-048", "Validation failed", "Either device id or imei is mandatory."),
    /**
     * Resource not found error indicating no data was found.
     */
    FACTORY_DATA_NO_DATA_FOUND("assoc-049", "Resource not found", "No data found."),
    /**
     * Resource not found error indicating no whitelisted users were found.
     */
    USER_NOT_WHITELISTED("assoc-050", "Resource not found", "No white listed users found."),
    /**
     * Success message indicating the association history was retrieved successfully.
     */
    ASSO_HISTORY_MSG("assoc-051", "Success", "Association history retrieved successfully."),
    /**
     * Resource not found error indicating the association history details were not found in the database.
     */
    ASSOCIATION_HISTORY_NOT_FOUND("assoc-052", "Resource not found",
        "Failed to retrieve device based user association history details, Root Cause:Association details"
            + " not found in Database."),
    /**
     * Validation error indicating the IMEI should be numeric.
     */
    INVALID_IMEI_VALUE("assoc-053", "Validation failed",
        "Failed to retrieve device based user association history details, Root Cause: IMEI should be numeric."),
    /**
     * Validation error indicating the page value must be numeric and greater than 0.
     */
    INVALID_PAGE_VALUE("assoc-054", "Validation failed",
        "Failed to retrieve device based user association history details, Root Cause: Page must be numeric"
            + " and greater than 0"),
    /**
     * Validation error indicating the size value must be numeric and greater than 0.
     */
    INVALID_SIZE_VALUE("assoc-055", "Validation failed",
        "Failed to retrieve device based user association history details, Root Cause: size must be numeric"
            + " and greater than 0"),
    /**
     * Validation error indicating the orderby value must be "asc" or "desc".
     */
    INVALID_ORDERBY_VALUE("assoc-056", "Validation failed",
        "Failed to retrieve device based user association history details, Root Cause: orderby must be asc or desc"),
    /**
     * Resource not found error indicating the sortby field should contain one of the allowed values.
     */
    INVALID_SORTBY_FIELD_RESPONSE("assoc-057", "Resource not found",
        "Failed to retrieve device based user association history details, Root Cause: sortby field should contain"
            + " one of these allowed values (userid, associationstatus)"),
    /**
     * Internal server error indicating the device disassociation failed during SIM activation.
     */
    DIS_ASSOC_FAILED_DURING_SIM_ACTIVATION("assoc-058", "Internal server error",
        "Device dis-associate failed while activating SIM."),
    /**
     * Success message indicating the VIN replacement was completed successfully.
     */
    VIN_REPLACE_SUCCESS("assoc-059", "Success", "Vin replace completed successfully"),
    /**
     * Success message indicating the SIM suspend was initiated successfully.
     */
    SIM_SUSPEND_INITIATION_SUCCESS("assoc-060", "Success", "Sim suspend initiated successfully."),
    /**
     * Precondition failed error indicating SIM activation is mandatory before SIM suspend.
     */
    SIM_SUSPEND_FAILED("assoc-061", "PreCondition failed", "Sim activation is mandatory before sim suspend."),
    /**
     * Precondition failed error indicating SIM suspend is mandatory before device termination.
     */
    SIM_SUSPEND_CONDITION_FAILED("assoc-062", "PreCondition failed",
        "Sim suspend is mandatory before device termination."),
    /**
     * Internal server error indicating the SIM state change failed.
     */
    SIM_STATE_CHANGE_FAILED("assoc-063", "Internal server error", "Sim state change failed."),
    /**
     * Internal server error indicating a failure to retrieve the SIM transaction status.
     */
    SIM_GET_TRAN_STATE_FAILED("assoc-064", "Internal server error", "Failed to retrieve sim transaction status."),
    /**
     * Validation error indicating a failure to retrieve region details from user management.
     */
    USER_DETAILS_NOT_FOUND("assoc-065", "Validation failed",
        "Failed to retrieve region details from user management. "),
    /**
     * Internal server error indicating the SWM session ID is null.
     */
    SWM_SESSION_ID_NULL("assoc-066", "Internal server error", "SWM session id is null"),
    /**
     * Internal server error indicating the SWM vehicle creation failed due to a possible session ID expiration.
     */
    SWM_VEHICLE_CREATION_FAILED("assoc-067", "Internal server error",
        "SWM vehicle creation failed. Possible cause: SessionId expired"),
    /**
     * Internal server error indicating a failure to parse the SWM vehicle creation response JSON.
     */
    SWM_VEHICLE_CREATION_RESPONSE_JSON_PARSE_FAILED("assoc-068", "Internal server error",
        "Unable to parse swm vehicle creation response json."),
    /**
     * Internal server error indicating the SWM vehicle creation failed due to an internal error in SWM.
     */
    SWM_VEHICLE_CREATION_INTERNAL_ERROR("assoc-069", "Internal server error",
        "SWM vehicle creation failed due to SWM internal error."),
    /**
     * Success message indicating the device was deleted successfully.
     */
    DEVICE_DELETE_SUCCESS("assoc-070", "Success", "Device deleted successfully."),
    /**
     * Internal server error indicating a failure to parse the SWM vehicle delete response JSON.
     */
    SWM_VEHICLE_DELETE_RESPONSE_JSON_PARSE_FAILED("assoc-071", "Internal server error",
        "Unable to parse swm vehicle delete response json."),
    /**
     * Internal server error indicating the SWM vehicle deletion failed due to a possible session ID expiration.
     */
    SWM_VEHICLE_DELETE_FAILED("assoc-072", "Internal server error",
        "SWM vehicle deletion failed. Possible cause: SessionId expired"),
    /**
     * Internal server error indicating the SWM vehicle update failed due to a possible session ID expiration.
     */
    SWM_VEHICLE_UPDATE_FAILED("assoc-073", "Internal server error",
        "SWM vehicle update failed. Possible cause: SessionId expired"),
    /**
     * Precondition failed error indicating SIM activation should be completed before performing device termination.
     */
    SIM_ACTIVATION_PENDING("assoc-074", "PreCondition failed",
        "Sim activation should be completed before performing device terminate"),
    /**
     * Success message indicating the wipe data operation was completed successfully.
     */
    WIPE_DATA_SUCCESS("assoc-075", "Success", "Wipe data completed successfully"),
    /**
     * Validation error indicating no association was found for the user.
     */
    WIPE_DATA_NO_ASSOC_FOUND("assoc-076", "Validation failed", "No association found for the user"),
    /**
     * Validation error indicating no association was found for some serial numbers.
     */
    WIPE_DATA_NO_ASSOC_FOUND_FOR_SOME_DEVICE("assoc-077", "Validation failed",
        "No association found for some serial number(s)"),
    /**
     * Validation error indicating no device is in the ASSOCIATED state.
     */
    WIPE_DATA_NO_ASSOC_STATE_FOUND("assoc-078", "Validation failed", "no device is in ASSOCIATED state"),
    /**
     * Internal server error indicating a failure during the wipe data operation due to association issues.
     */
    WIPE_DATA_ASSOCIATION_FAILURE("assoc-079", "Internal server error",
        "Wipe data failed during association. Please contact admin"),
    /**
     * Internal server error indicating a failure during the wipe data operation due to termination issues.
     */
    WIPE_DATA_TERMINATION_FAILURE("assoc-080", "Internal server error",
        "Wipe data failed during termination. Please contact admin"),
    /**
     * Internal server error indicating a failure during the wipe data operation due to activation issues.
     */
    WIPE_DATA_ACTIVATION_FAILURE("assoc-081", "Internal server error",
        "Wipe data failed during activation. Please contact admin"),
    /**
     * Precondition failed error indicating the model is not present in the whitelisted models.
     */
    MODEL_NOT_FOUND_IN_WHITELISTED_MODELS_EXCEPTION("assoc-082", "PreCondition failed",
        "Model not present in whitelisted models"),
    /**
     * Internal server error indicating a failure in the VIN Decode API.
     */
    VIN_DECODE_API_FAILURE("assoc-083", "Internal server error", "Vin Decode failed"),
    /**
     * Internal server error indicating the system parameters service is down.
     */
    SYSTEM_PARAMETERS_WHITELISTED_MODEL_API_FAILURE("assoc-084", "Internal server error",
        "System parameters service down"),
    /**
     * Validation error indicating the whitelisted models are empty.
     */
    WHITELISTED_MODELS_IS_EMPTY("assoc-085", "Validation failed", "WhiteListed models are empty"),
    /**
     * Success message indicating all device items were saved successfully.
     */
    DEVICE_INFO_SAVE_SUCCESS("assoc-090", "Success", "All devices item saved successfully"),
    /**
     * Success message indicating some device items were saved successfully.
     */
    DEVICE_INFO_SAVE_PARTIAL_SUCCESS("assoc-091", "Success", "Device item partially saved successfully"),
    /**
     * Internal server error indicating a failure to save all device items.
     */
    DEVICE_INFO_SAVE_FAILED("assoc-092", "Internal server error", "Failed to save all devices item"),
    /**
     * Validation error indicating invalid input for saving device information.
     */
    DEVICE_INFO_SAVE_VALIDATION_FAILED("assoc-093", "Validation failed", "Invalid Input"),
    /**
     * Validation error indicating the input size exceeds the allowed size.
     */
    DEVICE_INFO_SAVE_SIZE_VALIDATION_FAILED("assoc-094", "Validation failed",
        "Input size is greater than allowed size"),
    /**
     * Validation error indicating the association type is not allowed.
     */
    DELIGATION_ASSOCIATION_TYPE_VALIDATION_FAILED("assoc-095", "Validation failed", "Association Type is not allowed"),
    /**
     * Validation error indicating the end time cannot be before the start time.
     */
    START_END_TIME_VALIDATION_FAILED("assoc-096", "Validation failed", "End time cannot be before start time"),
    /**
     * Validation error indicating the provided user is already associated with the device.
     */
    ASSOCIATION_ALREADY_EXISTS("assoc-097", "Validation failed", "Provided User is already associated to the device"),
    /**
     * Validation error indicating the user is not valid to perform delegation or activation is still pending.
     */
    OWNER_VALIDATION_FAILED("assoc-098", "Validation failed",
        "Not an valid User to perform delegation or Activation is still pending"),
    /**
     * Validation error indicating the user details provided are invalid.
     */
    INVALID_USER_DETAILS("assoc-099", "Validation failed", "User details provided are Invalid"),
    /**
     * Validation error indicating both start time and end time must be provided.
     */
    START_END_TIME_REQUIRED_VALIDATION_FAILED("assoc-100", "Validation failed",
        "Please provide both start time and end time"),
    /**
     * Validation error indicating the device is not yet activated or associated with the owner.
     */
    OWNER_ASSO_NOT_FOUND("assoc-101", "Validation failed",
        "Device is not yet activated or not yet associated to owner"),
    /**
     * Validation error indicating the association type validation failed.
     */
    ASSOC_TYPE_VALIDATION_FAILURE("assoc-102", "Validation failed", "Association type validation failed"),
    /**
     * Validation error indicating the user is not allowed to disassociate another user 
     * unless they are an admin or vehicle owner.
     */
    DISASSOCIATION_SUBORDINATE_VALIDATION_FAILED("assoc-103", "Validation failed",
        "User is not allowed to disassociate another user, unless admin/VehicleOwner"),
    /**
     * Success message indicating the termination of the device was completed successfully.
     */
    TERMINATION_SUCCESS("assoc-104", "Success", "Termination of the device completed successfully."),
    /**
     * Success message indicating the validation for performing termination or disassociation was successful.
     */
    VALIDATE_PERFORM_TERMINATION_SUCCESS("assoc-105", "Success",
        "Validation for performing termination or disassociation is successful"),
    /**
     * Precondition failed error indicating a database integrity error with M2M association data.
     */
    M2M_ASSOC_INTEGRITY_ERROR("assoc-106", "PreCondition failed", "Association data - Database Integrity Error."),
    /**
     * Validation error indicating the admin cannot send the user ID through the body.
     */
    M2M_ADMIN_REQUEST_INTEGRITY_ERROR("assoc-107", "Validation failed", "Admin can not send user-Id through body."),
    // We are using assoc codes from 110 to 117 in Api Gateway to return association component messages. Please refrain
    // yourself from using this assoc codes.
    /**
     * Precondition failed error indicating the new association type cannot be updated to owner.
     */
    NEW_ASSOCIATION_TYPE_CANNOT_BE_UPDATED_TO_OWNER("assoc-00108", "PreCondition failed",
        "New Association-Type cannot be owner"),
    /**
     * Precondition failed error indicating the user is not the owner of the device.
     */
    USER_NOT_OWNER_OF_DEVICE("assoc-00109", "PreCondition failed", "User is not owner of the device"),
    /**
     * Success message indicating the association was updated successfully.
     */
    ASSOCIATION_UPDATED_SUCCESSFULLY("assoc-00118", "Update Successful", "Association updated successfully"),
    /**
     * Internal server error indicating a failure to update the association.
     */
    ASSOCIATION_UPDATE_FAILED("assoc-00119", "Internal server error", "Failed to update Association"),
    /**
     * Validation error indicating either association type, start time, or end time must be provided.
     */
    ASSOCIATION_UPDATE_BASIC_DATA_MANDATORY("assoc-00120", "Validation failed",
        "Either Association-Type, StartTime or EndTime must be provided"),
    /**
     * Validation error indicating the user is not valid to perform termination.
     */
    OWNER_TERMINATION_VALIDATION_FAILED("assoc-00121", "Validation failed", "Not a valid User to perform termination"),
    /**
     * Validation error indicating dummy values cannot be updated.
     */
    UPDATE_DUMMY_VALIDATION_FAILED("assoc-00122", "Validation failed",
        "Cannot update to dummy values, please contact admin"),
    /**
     * Validation error indicating the user ID type configured is invalid.
     */
    USER_ID_TYPE_INVALID("assoc-00123", "Validation failed",
        "User Id type configured is invalid, please contact admin"),
    /**
     * Success message indicating the termination of the device was successful but failed to delete the vehicle profile.
     */
    VEHICLE_PROFILE_TERMINATION_FAILED("assoc-00124", "Success", "Termination of device was success but failed to "
        + "delete vehicle profile");

    private String code;
    private String message;
    private String generalMessage;

    /**
     * Constructor for ApiMessageEnum.
     *
     * @param code           The error code.
     * @param message        The error message.
     * @param generalMessage The general error message.
     */
    ApiMessageEnum(String code, String message, String generalMessage) {
        this.code = code;
        this.message = message;
        this.generalMessage = generalMessage;
    }

    /**
     * Get the error code.
     *
     * @return The error code.
     */
    public String getCode() {
        return code;
    }

    /**
     * Get the error message.
     *
     * @return The error message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get the formatted error message with the provided values.
     *
     * @param message The error message template.
     * @param value   The values to be formatted into the message.
     * @return The formatted error message.
     */
    public String getMessage(String message, Object[] value) {
        return new MessageFormat(message).format(value);
    }

    /**
     * Get the general error message.
     *
     * @return The general error message.
     */
    public String getGeneralMessage() {
        return generalMessage;
    }
}
