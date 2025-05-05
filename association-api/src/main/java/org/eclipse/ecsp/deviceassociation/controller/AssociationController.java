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

package org.eclipse.ecsp.deviceassociation.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.ecsp.common.ApiResponse;
import org.eclipse.ecsp.common.ErrorUtils;
import org.eclipse.ecsp.common.ExtendedApiResponse;
import org.eclipse.ecsp.common.HcpServicesFailureResponse;
import org.eclipse.ecsp.deviceassociation.dto.M2Mterminate;
import org.eclipse.ecsp.deviceassociation.dto.TriggerKafkaEventRequestDto;
import org.eclipse.ecsp.deviceassociation.lib.exception.WipeDataFailureException;
import org.eclipse.ecsp.deviceassociation.lib.model.DeviceAssociation;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociateDeviceRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociateDeviceResponse;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociationApiResponse;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DelegateAssociationRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceInfo;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceItemDto;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceItemResult;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceItemStatus;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceStatusRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.SimSuspendRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.VinDetails;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.WipeDeviceRequest;
import org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum;
import org.eclipse.ecsp.deviceassociation.lib.service.DeviceAssociationService;
import org.eclipse.ecsp.deviceassociation.lib.service.DeviceAssociationWithFactDataServiceV2;
import org.eclipse.ecsp.deviceassociation.lib.service.UserManagementClient;
import org.eclipse.ecsp.deviceassociation.lib.service.VehicleProfileService;
import org.eclipse.ecsp.deviceassociation.lib.service.VinAssociationService;
import org.eclipse.ecsp.deviceassociation.springmvc.rest.support.RestResponse;
import org.eclipse.ecsp.deviceassociation.webutil.WebUtils;
import org.eclipse.ecsp.exception.shared.ApiNotificationException;
import org.eclipse.ecsp.exception.shared.ApiPreConditionFailedException;
import org.eclipse.ecsp.exception.shared.ApiResourceNotFoundException;
import org.eclipse.ecsp.exception.shared.ApiTechnicalException;
import org.eclipse.ecsp.exception.shared.ApiValidationFailedException;
import org.eclipse.ecsp.exception.shared.SimStateChangeFailureException;
import org.eclipse.ecsp.security.Security;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.ASSOCIATION_SUCCESS;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.BASIC_DATA_MANDATORY;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.DEVICE_INFO_SAVE_FAILED;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.DEVICE_INFO_SAVE_PARTIAL_SUCCESS;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.DEVICE_INFO_SAVE_SUCCESS;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.DEVICE_INFO_SAVE_VALIDATION_FAILED;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.FIND_ASSO;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.GENERAL_ERROR;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.INVALID_USER_ID_ERR_MSG;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.M2M_ADMIN_REQUEST_INTEGRITY_ERROR;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.NO_VALID_ASSOERR;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.SIM_SUSPEND_INITIATION_SUCCESS;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.TERMINATE_ASSO_SUCCESS;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.TERMINATION_SUCCESS;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.USER_DETAILS_NOT_FOUND;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.VALIDATE_PERFORM_TERMINATION_SUCCESS;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.VEHICLE_PROFILE_TERMINATION_FAILED;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.VIN_ALREADY_ASSO_WITH_OTHER_DEVICE;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.VIN_ASSO_SUCCESS;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.VIN_NOT_ASSO;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.VIN_REPLACE_SUCCESS;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.WIPE_DATA_SUCCESS;
import static org.eclipse.ecsp.deviceassociation.webutil.WebUtils.getResponseEntity;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.PRECONDITION_FAILED;

/**
 * This class is responsible for handling device association operations.
 * It provides methods to associate and disassociate devices with a user.
 */
@RestController
public class AssociationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AssociationController.class);
    private static final String USER_ID = "user-id";
    private static final String COUNTRY = "country";
    private static final String CORRELATION_ID = "correlationId";
    private static final String SUCCESS = "SUCCESS";

    private static final int BAD_REQUEST_STATUS_CODE = 400;
    private static final int PRECONDITION_FAILED_STATUS_CODE = 412;
    private static final int GENERAL_ERROR_STATUS_CODE = 500;
    private static final int SUCCESS_STATUS_CODE = 200;

    private static final String ADMIN_USER_ID = "admin-user-id";
    private static final String NOT_ADMIN = "notAdmin";
    private static final String USERID_FROM_REQUEST_HEADER = "## userId: {} from request header ";
    private static final String DEVICE_ASSOCIATION_ERROR_MESSAGE =
            "## Error has occurred while performing device association. User: {}, ErrMsg: {}";
    private static final String DEVICE_ASSOC_ERROR_MESSAGE =
            "## Error has occurred while performing device association.";
    private static final String SERIAL_NUMBER = "serialNumber";
    private static final String CARRIAGE_AND_NEWLINE_REGEX = "[\r\n]";
    private static final String VIN_ASSOCIATION_ERROR_MSG =
            "## Error has occurred while performing vin association with user: {}, imei: {}, ErrMsg: {}";
    private static final String USERID = "userId";
    private static final String TERMINATION_WITH_USER_ERROR =
            "## Error has occurred while performing terminate with user: {}, ErrMsg: {}";
    private static final String DEVICE_TERMINATION_WITH_USER_ERROR =
            "## Error has occurred while performing device terminate with user: {}, ErrMsg: {}";
    private static final String DEVICE_ID = "deviceId";
    private static final String DIS_ASSOCIATION_WITH_DEVICE_ERROR =
            "## Error has occurred while performing user dis-association (terminate) with device";
    private static final String DEVICE_ASSOCIATION_ERROR =
            "## Error has occurred while performing device association. imei: {},serialNumber: {},bssid: {} ErrMsg: {}";
    private static final String VALIDATING_TERMINATION_ERROR =
            "## Error has occurred while validating whether to perform termination or disassociation."
                    + " User: {}, ErrMsg: {}";

    private final DeviceAssociationWithFactDataServiceV2 deviceAssocFactoryService;

    private final VehicleProfileService vehicleProfileService;
    private final VinAssociationService vinAssociationService;
    private final DeviceAssociationService deviceAssociationService;
    @Value("${vin_association_enabled:false}")
    private boolean vinAssocEnabled;
    @Value("${code_value_vin_decode_check:false}")
    private boolean codeValueVinDecodeCheck;
    @Autowired
    private UserManagementClient userManagerService;

    /**
     * Constructs an instance of AssociationController with the specified services.
     *
     * @param deviceAssocFactoryService the service for handling device associations with fact data (version 2)
     * @param vinAssociationService the service for managing VIN (Vehicle Identification Number) associations
     * @param deviceAssociationService the service for managing device associations
     * @param vehicleProfileService the service for handling vehicle profile operations
     */
    @Autowired
    public AssociationController(
        DeviceAssociationWithFactDataServiceV2 deviceAssocFactoryService, VinAssociationService vinAssociationService,
        DeviceAssociationService deviceAssociationService, VehicleProfileService vehicleProfileService) {
        this.deviceAssocFactoryService = deviceAssocFactoryService;
        this.vinAssociationService = vinAssociationService;
        this.deviceAssociationService = deviceAssociationService;
        this.vehicleProfileService = vehicleProfileService;
    }

    /**
     * Replaces newline and carriage return characters in the given data with an empty string.
     *
     * @param data the data to be processed
     * @param <T> the type of the data
     * @return the processed data with newline and carriage return characters removed, or null if the input data is null
     */
    private static <T> String replaceNewLineAndCarriage(T data) {
        return data != null ? data.toString().replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "") : null;
    }

    /**
     * Associates a device with a user.
     *
     * @param associateDeviceRequest The request object containing the device information.
     * @param request                The HTTP servlet request.
     * @return The response entity containing the API response.
     */
    @PostMapping(value = "v3/user/devices/associate/", produces = "application/json")
    @Operation(summary = "POST v3/user/devices/associate/", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR,
            scopes = { "AssociateMyselfToVehicle,UserDeviceAssocn,SelfManage" })
    public ResponseEntity<ApiResponse<Object>> associateDevice(
        @Valid @RequestBody AssociateDeviceRequest associateDeviceRequest,
        HttpServletRequest request) {
        // 2.33 Release - Sonar RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE codesmell fix
        String associateDeviceRequestData =
                associateDeviceRequest.toString().replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "");
        LOGGER.info("## associateDeviceWithFactoryData Controller - START request: {}", associateDeviceRequestData);
        String userId = request.getHeader(USER_ID);
        ApiResponse<Object> apiResponse;
        String bssid = associateDeviceRequest.getBssid();
        final String serialNumber = associateDeviceRequest.getSerialNumber();
        String imei = associateDeviceRequest.getImei();
        if (isNotValidUserId(userId)) {
            apiResponse =
                new ApiResponse.Builder<>(INVALID_USER_ID_ERR_MSG.getCode(), INVALID_USER_ID_ERR_MSG.getMessage(),
                    BAD_REQUEST)
                    .build();
        } else if (StringUtils.isEmpty(bssid) && StringUtils.isEmpty(imei) && StringUtils.isEmpty(serialNumber)) {
            apiResponse = new ApiResponse.Builder<>(BASIC_DATA_MANDATORY.getCode(), BASIC_DATA_MANDATORY.getMessage(),
                BAD_REQUEST)
                .build();
        } else {
            LOGGER.debug(USERID_FROM_REQUEST_HEADER,
                (userId == null) ? null : userId.replaceAll(CARRIAGE_AND_NEWLINE_REGEX, ""));
            associateDeviceRequest.setUserId(userId);
            try {
                //Perform association
                AssociateDeviceResponse associateDeviceResponse =
                    deviceAssocFactoryService.associateDevice(associateDeviceRequest);
                apiResponse = new ApiResponse.Builder<>(ASSOCIATION_SUCCESS.getCode(), ASSOCIATION_SUCCESS.getMessage(),
                    HttpStatus.OK)
                    .withData(associateDeviceResponse).build();
            } catch (ApiValidationFailedException e) {
                LOGGER.error(DEVICE_ASSOCIATION_ERROR_MESSAGE, userId, e.getErrorMessage());
                apiResponse = new ApiResponse.Builder<>(e.getCode(), e.getMessage(), BAD_REQUEST).build();
            } catch (ApiResourceNotFoundException e) {
                LOGGER.error(DEVICE_ASSOCIATION_ERROR_MESSAGE, userId, e.getErrorMessage());
                apiResponse = new ApiResponse.Builder<>(e.getCode(), e.getMessage(), HttpStatus.NOT_FOUND).build();
            } catch (ApiPreConditionFailedException e) {
                LOGGER.error("## Error has occurred while performing association. User: {}, ErrMsg: {}", userId,
                    e.generalMessage());
                apiResponse =
                    new ApiResponse.Builder<>(e.getCode(), e.getMessage(), HttpStatus.PRECONDITION_FAILED).build();
            } catch (Exception e) {
                Map<Object, Object> details = new LinkedHashMap<>();
                details.put("imei", associateDeviceRequest.getImei());
                details.put(SERIAL_NUMBER, serialNumber);
                details.put(ErrorUtils.ERROR_CODE_KEY, GENERAL_ERROR.getCode());
                LOGGER.error("{}", ErrorUtils.buildError(DEVICE_ASSOC_ERROR_MESSAGE, e, details));
                apiResponse = new ApiResponse.Builder<>(GENERAL_ERROR.getCode(), GENERAL_ERROR.getMessage(),
                    INTERNAL_SERVER_ERROR).build();
            }
        }
        LOGGER.info("## associateDeviceWithFactoryData Controller - END");
        return WebUtils.getResponseEntity(apiResponse);
    }

    /**
     * Associates a VIN (Vehicle Identification Number) with a user's device.
     *
     * @param vinRequest The VIN details.
     * @param userId The user ID.
     * @return The response entity containing the API response.
     */
    @PutMapping(value = "v1/user/device/vin", produces = "application/json")
    @Operation(summary = "PUT v1/user/device/vin", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR,
            scopes = { "AssociateMyselfToVehicle,UserDeviceAssocn,SelfManage" })
    public ResponseEntity<ApiResponse<Object>> associateVin(@Valid @RequestBody VinDetails vinRequest,
                                                            @RequestHeader(USER_ID) String userId) {
        String vin = vinRequest.getVin();
        String imei = vinRequest.getImei();
        LOGGER.info("## associateVin Controller - START vin: {}, imei: {}", vinRequest.getVin(), vinRequest.getImei());
        LOGGER.debug("## vinAssocEnabled: {}", vinAssocEnabled);
        ApiResponse<Object> apiResponse;
        try {
            if (!vinAssocEnabled) {
                apiResponse = new ApiResponse.Builder<>(ApiMessageEnum.VIN_ASSO_NOT_ENABLED.getCode(),
                    ApiMessageEnum.VIN_ASSO_NOT_ENABLED.getMessage(), HttpStatus.BAD_REQUEST).build();
            } else {
                apiResponse = vinAssocEnabled(userId, vin, imei, vinRequest);
            }
        } catch (ApiValidationFailedException e) {
            LOGGER.error(VIN_ASSOCIATION_ERROR_MSG, replaceNewLineAndCarriage(userId),
                    replaceNewLineAndCarriage(imei), replaceNewLineAndCarriage(e.getMessage()));
            apiResponse = new ApiResponse.Builder<>(e.getCode(), e.getMessage(), BAD_REQUEST).build();
        } catch (ApiPreConditionFailedException e) {
            LOGGER.error(VIN_ASSOCIATION_ERROR_MSG, replaceNewLineAndCarriage(userId),
                    replaceNewLineAndCarriage(imei), replaceNewLineAndCarriage(e.generalMessage()));
            apiResponse =
                new ApiResponse.Builder<>(e.getCode(), e.getMessage(), HttpStatus.PRECONDITION_FAILED).build();
        } catch (SimStateChangeFailureException e) {
            Map<Object, Object> details = new LinkedHashMap<>();
            details.put("vin", vinRequest.getVin());
            details.put("vinRequest", vinRequest.getImei());
            details.put(USERID, userId);
            details.put(ErrorUtils.ERROR_CODE_KEY, GENERAL_ERROR.getCode());
            LOGGER.error("{}",
                ErrorUtils.buildError("## error has occurred while performing sim activation using imsi",
                        e, details));
            apiResponse =
                new ApiResponse.Builder<>(e.getCode(), e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (ApiTechnicalException e) {
            // 2.33 Release - Sonar CRLF_INJECTION_LOGS vulnerability fix
            LOGGER.error(VIN_ASSOCIATION_ERROR_MSG, replaceNewLineAndCarriage(userId),
                    replaceNewLineAndCarriage(imei), replaceNewLineAndCarriage(e.getErrorMessage()));
            apiResponse = new ApiResponse.Builder<>(e.getCode(), e.getMessage(), INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            Map<Object, Object> details = new LinkedHashMap<>();
            details.put("vin", vinRequest.getVin());
            details.put("vinRequest", vinRequest.getImei());
            details.put(USERID, userId);
            details.put(ErrorUtils.ERROR_CODE_KEY, GENERAL_ERROR.getCode());
            LOGGER.error("{}",
                ErrorUtils.buildError("## Error has occurred while performing vin association", e, details));
            apiResponse = new ApiResponse.Builder<>(ApiMessageEnum.GENERAL_ERROR.getCode(),
                ApiMessageEnum.GENERAL_ERROR.getMessage(), INTERNAL_SERVER_ERROR).build();
        }
        LOGGER.debug("## associateVin Controller - END");
        return WebUtils.getResponseEntity(apiResponse);
    }

    /**
     * Checks if VIN association is enabled for the given user and performs VIN association.
     *
     * @param userId      The ID of the user.
     * @param vin         The VIN (Vehicle Identification Number) to associate.
     * @param imei        The IMEI (International Mobile Equipment Identity) of the device.
     * @param vinRequest  The VIN details for association.
     * @return An instance of ApiResponse representing the result of the VIN association.
     */
    private ApiResponse<Object> vinAssocEnabled(String userId, String vin, String imei, VinDetails vinRequest) {
        Long deviceAssociationId = vinAssociationService.checkVinAssociationPreconditions(userId, vin, imei);
        LOGGER.debug("## codeValueVinDecodeCheck: {}", codeValueVinDecodeCheck);
        if (codeValueVinDecodeCheck) {
            // get model for the device from DeviceInfoFactoryData
            String factoryDataModel = deviceAssocFactoryService.getModelByImei(imei);
            vinAssociationService.decodingPreConditionCheck(vin, factoryDataModel, userId, deviceAssociationId);
        }
        vinRequest.setRegion(getValue(userId, COUNTRY));
        // Perform vin association
        vinAssociationService.vinAssociate(userId, vinRequest, deviceAssociationId);
        return new ApiResponse.Builder<>(VIN_ASSO_SUCCESS.getCode(), VIN_ASSO_SUCCESS.getMessage(),
            HttpStatus.OK).build();
    }

    /**
     * Retrieves the value of a specific field for a given user.
     *
     * @param userId    the ID of the user
     * @param fieldName the name of the field to retrieve the value from
     * @return the value of the specified field for the user
     * @throws ApiValidationFailedException if the user details are not found or the field value is blank
     */
    private String getValue(String userId, String fieldName) {
        String region = userManagerService.getUserDetail(userId, fieldName);
        if (StringUtils.isBlank(region)) {
            throw new ApiValidationFailedException(USER_DETAILS_NOT_FOUND.getCode(),
                USER_DETAILS_NOT_FOUND.getMessage());
        }
        return region;
    }

    /**
     * Retrieves the associations for a user.
     *
     * @param httpServletRequest the HTTP servlet request
     * @return the response entity containing the API response
     */
    @GetMapping(value = "v3/user/associations/", produces = "application/json")
    @Operation(summary = "GET v3/user/associations/", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR,
            scopes = { "AssociateMyselfToVehicle,UserDeviceAssocn,SelfManage,IgniteSystem" })
    public ResponseEntity<ApiResponse<Object>> findAssociation(HttpServletRequest httpServletRequest) {
        ApiResponse<Object> apiResponse;
        String userId = httpServletRequest.getHeader(USER_ID);
        LOGGER.info("## findAssociation - START userId: {}", (userId == null) ? null :
            userId.replaceAll(CARRIAGE_AND_NEWLINE_REGEX, ""));
        try {
            if (isNotValidUserId(userId)) {
                LOGGER.error("## User id is either null of empty from  header");
                apiResponse = new ApiResponse.Builder<>(ApiMessageEnum.INVALID_USER_ID_ERR_MSG.getCode(),
                    ApiMessageEnum.INVALID_USER_ID_ERR_MSG.getMessage(), BAD_REQUEST).build();
            } else {
                LOGGER.debug("## UserId: {} from header", (userId == null) ? null :
                    userId.replaceAll(CARRIAGE_AND_NEWLINE_REGEX, ""));
                List<DeviceAssociation> deviceAssociations =
                    deviceAssocFactoryService.getAssociatedDevicesForUser(userId);
                apiResponse =
                    new ApiResponse.Builder<>(ApiMessageEnum.FIND_ASSO.getCode(), FIND_ASSO.getMessage(), HttpStatus.OK)
                        .withData(deviceAssociations).build();
            }
        } catch (Exception e) {
            Map<Object, Object> details = new LinkedHashMap<>();
            details.put(USERID, userId);
            details.put(ErrorUtils.ERROR_CODE_KEY, GENERAL_ERROR.getCode());
            LOGGER.error("{}",
                ErrorUtils.buildError("## Error has occurred while finding user association details", e, details));
            apiResponse =
                new ApiResponse.Builder<>(GENERAL_ERROR.getCode(), GENERAL_ERROR.getMessage(), INTERNAL_SERVER_ERROR)
                    .build();
        }
        LOGGER.debug("## associatedDevicesOfUserWithFactoryData Controller - END");
        return WebUtils.getResponseEntity(apiResponse);
    }

    /**
     * Terminates the association between a user and a device.
     *
     * @param deviceStatusRequest The request object containing device status information.
     * @param userId              The ID of the user.
     * @return A ResponseEntity containing the API response.
     */
    @PostMapping(value = "v2/user/associations/terminate", produces = "application/json")
    @Operation(summary = "POST v2/user/associations/terminate", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR,
            scopes = { "AssociateMyselfToVehicle,UserDeviceAssocn,SelfManage,IgniteSystem" })
    public ResponseEntity<ApiResponse<Object>> terminateAssociation(
        @Valid @RequestBody DeviceStatusRequest deviceStatusRequest,
        @RequestHeader(USER_ID) String userId) {
        // 2.33 Release - Sonar RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE codesmell fix
        String deviceStatusRequestData =
                deviceStatusRequest.toString().replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "");
        LOGGER.info("## terminateAssociation Controller - START request: {}", deviceStatusRequestData);
        ApiResponse<Object> apiResponse;
        if (isNotValidUserId(userId)) {
            apiResponse = new ApiResponse.Builder<>(ApiMessageEnum.USER_ID_MANDATORY.getCode(),
                ApiMessageEnum.USER_ID_MANDATORY.getMessage(), BAD_REQUEST).build();
        } else {
            String requestUserId = replaceNewLineAndCarriage(userId);
            LOGGER.info(USERID_FROM_REQUEST_HEADER, requestUserId);
            deviceStatusRequest.setUserId(userId);
            try {
                String deleteVehicleProfileUrl = vehicleProfileService.fetchVehicleProfileUrl(deviceStatusRequest);
                int updateCount = deviceAssocFactoryService.terminateAssociation(deviceStatusRequest);
                apiResponse = v2Terminate(updateCount, deleteVehicleProfileUrl);
            } catch (ApiResourceNotFoundException e) {
                LOGGER.error(TERMINATION_WITH_USER_ERROR, userId, e.getErrorMessage());
                apiResponse = new ApiResponse.Builder<>(e.getCode(), e.getMessage(), HttpStatus.NOT_FOUND).build();
            } catch (ApiPreConditionFailedException e) {
                LOGGER.error(TERMINATION_WITH_USER_ERROR, userId, e.generalMessage());
                apiResponse =
                    new ApiResponse.Builder<>(e.getCode(), e.getMessage(), HttpStatus.PRECONDITION_FAILED).build();
            } catch (ApiNotificationException e) {
                LOGGER.error(TERMINATION_WITH_USER_ERROR, userId, e.getErrorMessage());
                apiResponse = new ApiResponse.Builder<>(e.getCode(), e.getMessage(), INTERNAL_SERVER_ERROR).build();
            } catch (ApiTechnicalException e) {
                LOGGER.error(DEVICE_TERMINATION_WITH_USER_ERROR, userId, e.getErrorMessage());
                apiResponse = new ApiResponse.Builder<>(e.getCode(), e.getMessage(), INTERNAL_SERVER_ERROR).build();
            } catch (Exception e) {
                Map<Object, Object> details = new LinkedHashMap<>();
                details.put("imei", deviceStatusRequest.getImei());
                details.put(SERIAL_NUMBER, deviceStatusRequest.getSerialNumber());
                details.put(DEVICE_ID, deviceStatusRequest.getDeviceId());
                details.put(ErrorUtils.ERROR_CODE_KEY, GENERAL_ERROR.getCode());
                LOGGER.error("{}", ErrorUtils.buildError(DIS_ASSOCIATION_WITH_DEVICE_ERROR, e, details));
                apiResponse = new ApiResponse.Builder<>(GENERAL_ERROR.getCode(), GENERAL_ERROR.getMessage(),
                    INTERNAL_SERVER_ERROR).build();
            }
        }
        LOGGER.info("## terminateAssociation Controller - END");
        return WebUtils.getResponseEntity(apiResponse);
    }

    /**
     * Terminates the association based on the provided update count and delete vehicle profile URL.
     *
     * @param updateCount           The number of updates made to the association.
     * @param deleteVehicleProfileUrl The URL to delete the vehicle profile.
     * @return                      An ApiResponse object representing the result of the termination.
     */
    public ApiResponse<Object> v2Terminate(int updateCount, String deleteVehicleProfileUrl) {
        LOGGER.info("Inside v2Terminate method");
        ApiResponse<Object> apiResponse;
        if (updateCount > 0) {
            boolean isVpDeleted = vehicleProfileService.deleteVehicleProfile(deleteVehicleProfileUrl);
            if (isVpDeleted) {
                apiResponse = new ApiResponse.Builder<>(TERMINATE_ASSO_SUCCESS.getCode(),
                    TERMINATE_ASSO_SUCCESS.getMessage(), OK).build();
            } else {
                apiResponse = new ApiResponse.Builder<>(VEHICLE_PROFILE_TERMINATION_FAILED.getCode(),
                    VEHICLE_PROFILE_TERMINATION_FAILED.getMessage(), OK).build();
            }
        } else {
            apiResponse = new ApiResponse.Builder<>(NO_VALID_ASSOERR.getCode(), NO_VALID_ASSOERR.getMessage(),
                BAD_REQUEST).build();
        }
        LOGGER.info("Exiting v2Terminate method");
        return apiResponse;
    }

    /**
     * Replaces the VIN (Vehicle Identification Number) for a device.
     *
     * @param deviceId The ID of the device.
     * @param vin The new VIN to replace the existing one.
     * @return A ResponseEntity containing the API response.
     */
    @PutMapping(value = "v2/device/{deviceId}/vin/{vin}/replace", produces = "application/json")
    @Hidden
    public ResponseEntity<ApiResponse<Object>> replaceVinForDevice(@PathVariable("deviceId") String deviceId,
                                                 @PathVariable("vin") String vin) {
        ApiResponse<Object> apiResponse;
        try {
            String requestDeviceId = replaceNewLineAndCarriage(deviceId);
            String requestVinId = replaceNewLineAndCarriage(vin);
            LOGGER.info("Start - Replace vin for Device: {}, VIN: {}", requestDeviceId, requestVinId);
            /* Extract association_id for device */
            // Part-1 Association regular flow happened or not
            boolean associationExists = deviceAssociationService.associationByDeviceExists(deviceId);
            if (!associationExists) {
                apiResponse = new ApiResponse.Builder<>(NO_VALID_ASSOERR.getCode(), NO_VALID_ASSOERR.getMessage(),
                    BAD_REQUEST).build();
            } else {
                long assocId = deviceAssociationService.getAssociationIdByDeviceId(deviceId);
                LOGGER.info("Association ID for Device: {} is : {}", requestDeviceId, assocId);
                /* Check if any vin is associated for that association_id */
                boolean vinAssociated = deviceAssociationService.getVinAssociation(assocId);
                if (!vinAssociated) {
                    apiResponse = new ApiResponse.Builder<>(VIN_NOT_ASSO.getCode(), VIN_NOT_ASSO.getMessage(),
                        PRECONDITION_FAILED).build();
                } else {
                    /* Check if the vin is already in use */
                    boolean vinExists = deviceAssociationService.vinAlreadyAssociated(vin);
                    if (vinExists) {
                        apiResponse = new ApiResponse.Builder<>(VIN_ALREADY_ASSO_WITH_OTHER_DEVICE.getCode(),
                            VIN_ALREADY_ASSO_WITH_OTHER_DEVICE.getMessage(), PRECONDITION_FAILED).build();
                    } else {
                        deviceAssociationService.replaceVin(assocId, vin);
                        LOGGER.info("Successfully replaced vin for Device: {} with vin: {}", requestDeviceId,
                            requestVinId);
                        apiResponse =
                            new ApiResponse.Builder<>(VIN_REPLACE_SUCCESS.getCode(), VIN_REPLACE_SUCCESS.getMessage(),
                                OK).build();
                    }
                }
            }
        } catch (Exception e) {
            Map<Object, Object> details = new LinkedHashMap<>();
            details.put(DEVICE_ID, deviceId);
            details.put("vin", vin);
            details.put(ErrorUtils.ERROR_CODE_KEY, GENERAL_ERROR.getCode());
            LOGGER.error("{}",
                ErrorUtils.buildError("## Error has occurred while performing replace vin for device", e,
                    details));
            apiResponse = new ApiResponse.Builder<>(GENERAL_ERROR.getCode(), GENERAL_ERROR.getMessage(),
                INTERNAL_SERVER_ERROR).build();
        }
        return WebUtils.getResponseEntity(apiResponse);
    }

    /**
     * Suspends the SIM card associated with the given IMEI number.
     *
     * @param simSuspendRequest The request object containing the IMEI number.
     * @param userId            The user ID associated with the request.
     * @return A ResponseEntity containing the API response.
     */
    @PostMapping(value = "v1/sim/suspend", produces = "application/json")
    @Operation(summary = "POST v1/sim/suspend", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR,
            scopes = { "AssociateMyselfToVehicle,UserDeviceAssocn,SelfManage,IgniteSystem" })
    public ResponseEntity<ApiResponse<Object>> suspendSim(@Valid @RequestBody SimSuspendRequest simSuspendRequest,
                                                          @RequestHeader(USER_ID) String userId) {
        String imei = simSuspendRequest.getImei();
        LOGGER.info("## suspendSim Controller - START imei: {}", imei);
        LOGGER.debug("## vinAssocEnabled: {}", vinAssocEnabled);
        ApiResponse<Object> apiResponse;
        try {
            if (!vinAssocEnabled) {
                apiResponse = new ApiResponse.Builder<>(ApiMessageEnum.VIN_ASSO_NOT_ENABLED.getCode(),
                    ApiMessageEnum.VIN_ASSO_NOT_ENABLED.getMessage(), HttpStatus.BAD_REQUEST).build();
            } else {
                Long deviceAssociationId = vinAssociationService.checkSimSuspendPreconditions(userId, imei);
                vinAssociationService.simSuspend(userId, simSuspendRequest, deviceAssociationId);
                apiResponse = new ApiResponse.Builder<>(SIM_SUSPEND_INITIATION_SUCCESS.getCode(),
                    SIM_SUSPEND_INITIATION_SUCCESS.getMessage(), ACCEPTED).build();
            }
        } catch (ApiPreConditionFailedException e) {
            LOGGER.error("## Error has occurred while performing sim Suspend with user: {}, imei: {}, ErrMsg: {}",
                (userId == null) ? null : userId.replaceAll(CARRIAGE_AND_NEWLINE_REGEX, ""),
                (imei == null) ? null : imei.replaceAll(CARRIAGE_AND_NEWLINE_REGEX, ""),
                (e.generalMessage() == null) ? null : e.generalMessage().replaceAll(CARRIAGE_AND_NEWLINE_REGEX, ""));
            apiResponse =
                new ApiResponse.Builder<>(e.getCode(), e.getMessage(), HttpStatus.PRECONDITION_FAILED).build();
        } catch (SimStateChangeFailureException e) {
            Map<Object, Object> details = new LinkedHashMap<>();
            details.put("imei", simSuspendRequest.getImei());
            details.put(USERID, userId);
            details.put(ErrorUtils.ERROR_CODE_KEY, GENERAL_ERROR.getCode());
            LOGGER.error("{}",
                ErrorUtils.buildError("## error has occurred while performing sim state change using imsi", e,
                    details));
            apiResponse =
                new ApiResponse.Builder<>(e.getCode(), e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            Map<Object, Object> details = new LinkedHashMap<>();
            details.put("simSuspendRequest", simSuspendRequest.getImei());
            details.put(USERID, userId);
            details.put(ErrorUtils.ERROR_CODE_KEY, GENERAL_ERROR.getCode());
            LOGGER.error("{}",
                ErrorUtils.buildError("## Error has occurred while performing vin association", e,
                    details));
            apiResponse = new ApiResponse.Builder<>(ApiMessageEnum.GENERAL_ERROR.getCode(),
                ApiMessageEnum.GENERAL_ERROR.getMessage(), INTERNAL_SERVER_ERROR).build();
        }
        LOGGER.debug("## Suspend Sim Controller - END");
        return WebUtils.getResponseEntity(apiResponse);
    }

    /**
     * Wipes the devices associated with a user.
     *
     * @param wipeDeviceRequest The request object containing the serial numbers of the devices to be wiped.
     * @param userId            The ID of the user.
     * @return A ResponseEntity containing the API response.
     */
    @PostMapping(value = "v1/user/associations/wipe", produces = "application/json")
    @Operation(summary = "POST v1/user/associations/wipe", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR,
            scopes = { "AssociateMyselfToVehicle,UserDeviceAssocn,SelfManage" })
    public ResponseEntity<ApiResponse<Object>> wipeDevices(
        @RequestBody(required = false) WipeDeviceRequest wipeDeviceRequest,
        @RequestHeader(value = USER_ID, required = true) String userId) {
        String requestUserId = replaceNewLineAndCarriage(userId);
        boolean serialNumbersPresent = wipeDeviceRequest != null && wipeDeviceRequest.getSerialNumber() != null;
        String serialNumbers =
            serialNumbersPresent ? Arrays.toString(wipeDeviceRequest.getSerialNumber().toArray()) : null;
        LOGGER.info("## wipeDevices Controller - START user-id: {}, serial number(s): {}", userId, serialNumbers);
        ApiResponse<Object> apiResponse;
        try {
            List<String> newDeviceIds = deviceAssociationService.wipeDevices(userId,
                wipeDeviceRequest != null ? wipeDeviceRequest.getSerialNumber() : null);

            apiResponse =
                new ApiResponse.Builder<>(ApiMessageEnum.WIPE_DATA_SUCCESS.getCode(), WIPE_DATA_SUCCESS.getMessage(),
                    HttpStatus.OK)
                    .withData(newDeviceIds).build();

        } catch (ApiPreConditionFailedException e) {
            // 2.33 Release - Sonar CRLF_INJECTION_LOGS vulnerability fix
            LOGGER.error(
                "## Error has occurred while performing wipe data with user: {}, serial numbers: {}, ErrMsg: {}",
                requestUserId, (serialNumbersPresent ? Arrays.toString(
                        wipeDeviceRequest.getSerialNumber().toArray()).replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "")
                            : null), replaceNewLineAndCarriage(e.generalMessage()));
            apiResponse = new ApiResponse.Builder<>(e.getCode(), e.getMessage(), HttpStatus.BAD_REQUEST).build();
        } catch (WipeDataFailureException e) {
            LOGGER.error(
                "## Error has occurred while performing wipe data with user: {}, serial numbers: {}, ErrMsg: {}",
                requestUserId, (serialNumbersPresent ? Arrays.toString(
                        wipeDeviceRequest.getSerialNumber().toArray()).replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "")
                            : null), replaceNewLineAndCarriage(e.getMessage()));
            apiResponse = new ApiResponse.Builder<>(e.getCode(),
                e.getMessage(), INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            Map<Object, Object> details = new LinkedHashMap<>();
            details.put("serial numbers",
                    serialNumbersPresent ? Arrays.toString(wipeDeviceRequest.getSerialNumber().toArray()) : null);
            details.put(USERID, userId);
            details.put(ErrorUtils.ERROR_CODE_KEY, GENERAL_ERROR.getCode());
            LOGGER.error("{}",
                ErrorUtils.buildError("## Error has occurred while performing wipe device", e,
                   details));
            apiResponse = new ApiResponse.Builder<>(ApiMessageEnum.GENERAL_ERROR.getCode(),
                ApiMessageEnum.GENERAL_ERROR.getMessage(), INTERNAL_SERVER_ERROR).build();
        }
        LOGGER.debug("## wipe devices Controller - END");
        return WebUtils.getResponseEntity(apiResponse);
    }

    /**
     * Saves a device item.
     *
     * @param deviceItemDto The device item DTO.
     * @param requestId The request ID.
     * @return The response entity containing the extended API response.
     */
    @PutMapping(value = "v1/devices/item", produces = "application/json")
    @Operation(summary = "PUT v1/devices/item", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR, scopes = { "IgniteSystem" })
    public ResponseEntity<ExtendedApiResponse<Object>> saveDeviceItem(@Valid @RequestBody DeviceItemDto deviceItemDto,
                                                                      @RequestHeader(value = CORRELATION_ID,
                                                                          required = false) String requestId) {
        LOGGER.info("## saveDeviceItem - START requestId: {} ",
            (requestId == null) ? null : requestId.replaceAll(CARRIAGE_AND_NEWLINE_REGEX, ""));
        ExtendedApiResponse<Object> extendedApiResponse;
        try {
            DeviceItemResult deviceItemResult = deviceAssociationService.saveDeviceItem(deviceItemDto);
            extendedApiResponse = buildDeviceItemResponse(deviceItemDto.getData().size(), requestId,
                deviceItemResult.getDeviceInfoStatusList(), deviceItemResult.isExceptionOccured());
        } catch (ApiValidationFailedException e) {
            LOGGER.error("## Error has occurred while saving DeviceInfo ErrMsg:{}", e.getMessage());
            extendedApiResponse = new ExtendedApiResponse.Builder<>(requestId, BAD_REQUEST, e.getCode(), e.getMessage(),
                e.getErrorMessage()).build();
        } catch (Exception e) {
            Map<Object, Object> details = new LinkedHashMap<>();
            details.put("requestBody", deviceItemDto.toString());
            LOGGER.error("{}", ErrorUtils.buildError("## Error has occurred while saving DeviceInfo", e,
                details));
            extendedApiResponse =
                new ExtendedApiResponse.Builder<>(requestId, INTERNAL_SERVER_ERROR, DEVICE_INFO_SAVE_FAILED.getCode(),
                    DEVICE_INFO_SAVE_FAILED.getMessage(), DEVICE_INFO_SAVE_FAILED.getGeneralMessage()).build();
        }
        return getResponseEntity(extendedApiResponse);
    }

    /**
     * Builds an ExtendedApiResponse object based on the provided parameters.
     *
     * @param totalItems           The total number of device items.
     * @param requestId            The ID of the request.
     * @param deviceItemStatusList The list of device item statuses.
     * @param exceptionOccurred    Indicates whether an exception occurred during the process.
     * @return The ExtendedApiResponse object.
     */
    private ExtendedApiResponse<Object> buildDeviceItemResponse(int totalItems, String requestId,
                                                                List<DeviceItemStatus> deviceItemStatusList,
                                                                boolean exceptionOccurred) {
        ExtendedApiResponse<Object> extendedApiResponse;
        int successItems = (int) deviceItemStatusList.stream().filter(d -> SUCCESS.equals(d.getStatus())).count();
        if (totalItems == successItems) {
            extendedApiResponse = new ExtendedApiResponse.Builder<>(requestId, OK, DEVICE_INFO_SAVE_SUCCESS.getCode(),
                DEVICE_INFO_SAVE_SUCCESS.getMessage(), DEVICE_INFO_SAVE_SUCCESS.getGeneralMessage()).build();
        } else if (successItems < totalItems && successItems > 0) {
            extendedApiResponse =
                new ExtendedApiResponse.Builder<>(requestId, OK, DEVICE_INFO_SAVE_PARTIAL_SUCCESS.getCode(),
                    DEVICE_INFO_SAVE_PARTIAL_SUCCESS.getMessage(),
                    DEVICE_INFO_SAVE_PARTIAL_SUCCESS.getGeneralMessage()).withData(deviceItemStatusList).build();
        } else {
            if (exceptionOccurred) {
                extendedApiResponse = new ExtendedApiResponse.Builder<>(requestId, INTERNAL_SERVER_ERROR,
                    DEVICE_INFO_SAVE_FAILED.getCode(),
                    DEVICE_INFO_SAVE_FAILED.getMessage(), DEVICE_INFO_SAVE_FAILED.getGeneralMessage()).build();
            } else {
                extendedApiResponse = new ExtendedApiResponse.Builder<>(requestId, BAD_REQUEST,
                    DEVICE_INFO_SAVE_VALIDATION_FAILED.getCode(),
                    DEVICE_INFO_SAVE_VALIDATION_FAILED.getMessage(),
                    DEVICE_INFO_SAVE_VALIDATION_FAILED.getGeneralMessage()).build();
            }
        }
        return extendedApiResponse;
    }

    /**
     * Checks if the given user ID is not valid.
     *
     * @param userId the user ID to check
     * @return true if the user ID is not valid, false otherwise
     */
    private boolean isNotValidUserId(String userId) {
        return userId == null || StringUtils.isBlank(userId);
    }

    /**
     * Sets the response object with the provided HTTP status code, API message enum,
     * and association API response.
     *
     * @param httpStatusCode The HTTP status code to set in the response object.
     * @param apiMessageEnum The API message enum to set the code, reason, and message in the response object.
     * @param response The association API response object to set the HTTP status code, code, reason, and message.
     */
    private void setResponseObject(Integer httpStatusCode, ApiMessageEnum apiMessageEnum,
                                   AssociationApiResponse<AssociateDeviceResponse> response) {
        response.setHttpStatusCode(httpStatusCode);
        response.setCode(apiMessageEnum.getCode());
        response.setReason(apiMessageEnum.getGeneralMessage());
        response.setMessage(apiMessageEnum.getMessage());
    }

    /**
     * Validates the termination of a device association.
     *
     * @param userId              the ID of the user performing the termination
     * @param deviceStatusRequest the device status request containing information about the device
     * @param isAdmin             a flag indicating whether the user is an admin or not
     * @return true if the termination is valid, false otherwise
     */
    public boolean validateTermination(String userId, DeviceStatusRequest deviceStatusRequest,
                                       boolean isAdmin) {
        LOGGER.info("##Validate Perform Terminate - START");
        M2Mterminate m2mTerminate = deviceAssocFactoryService.validatePerformTerminate(userId, deviceStatusRequest,
            isAdmin);
        boolean performTerminate = m2mTerminate.isPerformTerminate();
        LOGGER.debug("performTerminate: {}", performTerminate);
        LOGGER.info("##Validate Perform Terminate - END");
        return performTerminate;
    }

    /**
     * Delegates the association request to the appropriate service for processing.
     *
     * @param delegateAssociationRequest The request object containing the association details.
     * @param request                   The HttpServletRequest object.
     * @param requestId                 The correlation ID for the request.
     * @return                          The ResponseEntity containing the response data.
     */
    @PostMapping(value = "/v1/associations/delegation", produces = "application/json")
    @Operation(summary = "POST /v1/associations/delegation", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR, scopes = { "SelfManage" })
    public ResponseEntity<Object> delegateAssociation(
            @Valid @RequestBody DelegateAssociationRequest delegateAssociationRequest, HttpServletRequest request,
            @RequestHeader(value = CORRELATION_ID, required = true) String requestId) {
        String delegateAssociationRequestData =
                delegateAssociationRequest.toString().replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "");
        LOGGER.info("## delegateAssociation Controller - START request: {}", delegateAssociationRequestData);
        String userId = request.getHeader(USER_ID);
        String bssid = delegateAssociationRequest.getBssid();
        String serialNumber = delegateAssociationRequest.getSerialNumber();
        String imei = delegateAssociationRequest.getImei();
        HcpServicesFailureResponse hcpServicesFailureResponse;

        if (isNotValidUserId(userId)) {

            hcpServicesFailureResponse = setErrorCodeAndMessage(BAD_REQUEST_STATUS_CODE,
                    INVALID_USER_ID_ERR_MSG.getMessage(), requestId,
                    INVALID_USER_ID_ERR_MSG.getCode(), INVALID_USER_ID_ERR_MSG.getGeneralMessage());
            return RestResponse.createFailureResponse(hcpServicesFailureResponse);
        } else if (StringUtils.isEmpty(bssid) && StringUtils.isEmpty(imei) && StringUtils.isEmpty(serialNumber)) {

            hcpServicesFailureResponse = setErrorCodeAndMessage(BAD_REQUEST_STATUS_CODE,
                    BASIC_DATA_MANDATORY.getMessage(), requestId,
                    BASIC_DATA_MANDATORY.getCode(), BASIC_DATA_MANDATORY.getGeneralMessage());
            return RestResponse.createFailureResponse(hcpServicesFailureResponse);
        } else {

            LOGGER.info(USERID_FROM_REQUEST_HEADER,
                (userId == null) ? null : userId.replaceAll(CARRIAGE_AND_NEWLINE_REGEX, ""));
            delegateAssociationRequest.setUserId(userId);
            try {
                //Perform delegation
                AssociateDeviceResponse responseData = deviceAssocFactoryService
                    .delegateAssociation(delegateAssociationRequest, false);
                AssociationApiResponse<AssociateDeviceResponse> response = new AssociationApiResponse();
                setResponseObject(SUCCESS_STATUS_CODE, ASSOCIATION_SUCCESS, response);
                response.setRequestId(requestId);
                response.setData((List) Collections.singletonList(responseData));
                LOGGER.info("## delegateAssociation Controller - END");
                return RestResponse.ok(response);

            } catch (ApiValidationFailedException e) {
                LOGGER.error(DEVICE_ASSOCIATION_ERROR_MESSAGE, userId, e.getErrorMessage());
                hcpServicesFailureResponse = setErrorCodeAndMessage(BAD_REQUEST_STATUS_CODE, e.getMessage(),
                        requestId, e.getCode(), e.getErrorMessage());
                return RestResponse.createFailureResponse(hcpServicesFailureResponse);
            } catch (ApiResourceNotFoundException e) {
                LOGGER.error(DEVICE_ASSOCIATION_ERROR_MESSAGE, userId, e.getErrorMessage());
                hcpServicesFailureResponse = setErrorCodeAndMessage(BAD_REQUEST_STATUS_CODE, e.getMessage(),
                        requestId, e.getCode(), e.getErrorMessage());
                return RestResponse.createFailureResponse(hcpServicesFailureResponse);
            } catch (ApiPreConditionFailedException e) {
                LOGGER.error(DEVICE_ASSOCIATION_ERROR_MESSAGE, userId, e.generalMessage());
                hcpServicesFailureResponse = setErrorCodeAndMessage(PRECONDITION_FAILED_STATUS_CODE, e.getMessage(),
                        requestId, e.getCode(), e.generalMessage());
                return RestResponse.createFailureResponse(hcpServicesFailureResponse);
            } catch (Exception e) {
                Map<Object, Object> details = new LinkedHashMap<>();
                details.put("imei", delegateAssociationRequest.getImei());
                details.put(SERIAL_NUMBER, serialNumber);
                details.put(ErrorUtils.ERROR_CODE_KEY, GENERAL_ERROR.getCode());
                LOGGER.error("{}", ErrorUtils.buildError(DEVICE_ASSOC_ERROR_MESSAGE, e, details));
                hcpServicesFailureResponse = setErrorCodeAndMessage(GENERAL_ERROR_STATUS_CODE,
                        GENERAL_ERROR.getMessage(), requestId, GENERAL_ERROR.getCode(),
                        GENERAL_ERROR.getGeneralMessage());
                return RestResponse.createFailureResponse(hcpServicesFailureResponse);
            }
        }
    }

    /**
     * Associates a device for self.
     *
     * @param associateDeviceRequest The request object containing the device information.
     * @param userId                The user ID.
     * @param requestId             The request ID.
     * @return                      The response entity.
     */
    @PostMapping(value = "v1/associations/self", produces = "application/json")
    @Operation(summary = "POST v1/associations/self", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR,
            scopes = { "AssociateMyselfToVehicle,UserDeviceAssocn,SelfManage" })
    public ResponseEntity<Object> associateDeviceForSelf(
            @Valid @RequestBody AssociateDeviceRequest associateDeviceRequest,
            @RequestHeader(value = USER_ID, required = true) String userId,
            @RequestHeader(value = CORRELATION_ID, required = true) String requestId) {
        String associateDeviceRequestData =
                associateDeviceRequest.toString().replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "");
        LOGGER.info("## associateDeviceForSelf Controller - START request: {}", associateDeviceRequestData);
        String bssid = associateDeviceRequest.getBssid();
        final String serialNumber = associateDeviceRequest.getSerialNumber();
        String imei = associateDeviceRequest.getImei();
        HcpServicesFailureResponse hcpServicesFailureResponse;
        if (isNotValidUserId(userId)) {
            hcpServicesFailureResponse = setErrorCodeAndMessage(BAD_REQUEST_STATUS_CODE,
                    INVALID_USER_ID_ERR_MSG.getMessage(), requestId, INVALID_USER_ID_ERR_MSG.getCode(),
                    INVALID_USER_ID_ERR_MSG.getGeneralMessage());
            return RestResponse.createFailureResponse(hcpServicesFailureResponse);
        } else if (StringUtils.isEmpty(bssid) && StringUtils.isEmpty(imei) && StringUtils.isEmpty(serialNumber)) {
            hcpServicesFailureResponse = setErrorCodeAndMessage(BAD_REQUEST_STATUS_CODE,
                    BASIC_DATA_MANDATORY.getMessage(), requestId, BASIC_DATA_MANDATORY.getCode(),
                    BASIC_DATA_MANDATORY.getGeneralMessage());
            return RestResponse.createFailureResponse(hcpServicesFailureResponse);
        } else {
            LOGGER.debug(USERID_FROM_REQUEST_HEADER,
                (userId == null) ? null : userId.replaceAll(CARRIAGE_AND_NEWLINE_REGEX, ""));
            associateDeviceRequest.setUserId(userId);
            try {
                //Perform association
                AssociateDeviceResponse associateDeviceResponse =
                    deviceAssocFactoryService.associateDeviceForSelf(associateDeviceRequest, null);
                AssociationApiResponse<AssociateDeviceResponse> response = new AssociationApiResponse();
                setResponseObject(SUCCESS_STATUS_CODE, ASSOCIATION_SUCCESS, response);
                response.setRequestId(requestId);
                response.setData(Collections.singletonList(associateDeviceResponse));
                LOGGER.info("## associateDeviceWithFactoryData Controller - END");
                return RestResponse.ok(response);
            } catch (ApiValidationFailedException e) {
                LOGGER.error(DEVICE_ASSOCIATION_ERROR_MESSAGE, userId, e.getErrorMessage());
                hcpServicesFailureResponse = setErrorCodeAndMessage(BAD_REQUEST.value(), e.getMessage(),
                        requestId, e.getCode(), e.getErrorMessage());
                return RestResponse.createFailureResponse(hcpServicesFailureResponse);
            } catch (ApiResourceNotFoundException e) {
                LOGGER.error(DEVICE_ASSOCIATION_ERROR_MESSAGE, userId, e.getErrorMessage());
                hcpServicesFailureResponse = setErrorCodeAndMessage(NOT_FOUND.value(), e.getMessage(),
                        requestId, e.getCode(), e.getErrorMessage());
                return RestResponse.createFailureResponse(hcpServicesFailureResponse);
            } catch (ApiPreConditionFailedException e) {
                hcpServicesFailureResponse = setErrorCodeAndMessage(PRECONDITION_FAILED.value(), e.getMessage(),
                        requestId, e.getCode(), e.generalMessage());
                return RestResponse.createFailureResponse(hcpServicesFailureResponse);
            } catch (Exception e) {
                Map<Object, Object> details = new LinkedHashMap<>();
                details.put("imei", associateDeviceRequest.getImei());
                details.put(SERIAL_NUMBER, serialNumber);
                details.put(ErrorUtils.ERROR_CODE_KEY, GENERAL_ERROR.getCode());
                LOGGER.error("{}", ErrorUtils.buildError(DEVICE_ASSOC_ERROR_MESSAGE, e, details));
                hcpServicesFailureResponse = setErrorCodeAndMessage(INTERNAL_SERVER_ERROR.value(), e.getMessage(),
                        requestId, GENERAL_ERROR.getCode(), GENERAL_ERROR.getGeneralMessage());
                return RestResponse.createFailureResponse(hcpServicesFailureResponse);
            }
        }
    }

    /**
     * Delegates the association request by an admin.
     *
     * @param delegateAssociationRequest The request object containing association details.
     * @param request                   The HttpServletRequest object.
     * @param requestId                 The correlation ID header value.
     * @return                          The ResponseEntity containing the response data.
     */
    @PostMapping(value = "/v1/associations", produces = "application/json")
    @Operation(summary = "POST /v1/associations", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR, scopes = { "IgniteSystem" })
    public ResponseEntity<Object> delegateAssociationByAdmin(
        @Valid @RequestBody DelegateAssociationRequest delegateAssociationRequest,
        HttpServletRequest request, @RequestHeader(value = CORRELATION_ID, required = true) String requestId) {
        String delegateAssociationRequestData =
                delegateAssociationRequest.toString().replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "");
        LOGGER.info("## delegateAssociationByAdmin Controller - START request: {}", delegateAssociationRequestData);
        String userId = request.getHeader(USER_ID);
        String bssid = delegateAssociationRequest.getBssid();
        String serialNumber = delegateAssociationRequest.getSerialNumber();
        String imei = delegateAssociationRequest.getImei();
        HcpServicesFailureResponse hcpServicesFailureResponse;
        if (isNotValidUserId(userId)) {

            hcpServicesFailureResponse = setErrorCodeAndMessage(BAD_REQUEST_STATUS_CODE,
                    INVALID_USER_ID_ERR_MSG.getMessage(), requestId, INVALID_USER_ID_ERR_MSG.getCode(),
                    INVALID_USER_ID_ERR_MSG.getGeneralMessage());
            return RestResponse.createFailureResponse(hcpServicesFailureResponse);

        } else if (StringUtils.isEmpty(bssid) && StringUtils.isEmpty(imei) && StringUtils.isEmpty(serialNumber)) {

            hcpServicesFailureResponse = setErrorCodeAndMessage(BAD_REQUEST_STATUS_CODE,
                    BASIC_DATA_MANDATORY.getMessage(), requestId, BASIC_DATA_MANDATORY.getCode(),
                    BASIC_DATA_MANDATORY.getGeneralMessage());
            return RestResponse.createFailureResponse(hcpServicesFailureResponse);
        } else {
            LOGGER.info(USERID_FROM_REQUEST_HEADER,
                (userId == null) ? null : userId.replaceAll(CARRIAGE_AND_NEWLINE_REGEX, ""));
            delegateAssociationRequest.setUserId(userId);
            try {
                //Perform delegation
                AssociateDeviceResponse responseData = deviceAssocFactoryService
                    .delegateAssociationByAdmin(delegateAssociationRequest);
                AssociationApiResponse<AssociateDeviceResponse> response = new AssociationApiResponse();
                setResponseObject(SUCCESS_STATUS_CODE, ASSOCIATION_SUCCESS, response);
                response.setData(Collections.singletonList(responseData));
                response.setRequestId(requestId);
                LOGGER.info("## delegateAssociationByAdmin Controller - END");
                return RestResponse.ok(response);

            } catch (ApiValidationFailedException e) {
                LOGGER.error(DEVICE_ASSOCIATION_ERROR, imei, serialNumber, bssid, e.getErrorMessage());
                hcpServicesFailureResponse = setErrorCodeAndMessage(BAD_REQUEST_STATUS_CODE, e.getMessage(),
                        requestId, e.getCode(), e.getErrorMessage());
                return RestResponse.createFailureResponse(hcpServicesFailureResponse);
            } catch (ApiResourceNotFoundException e) {
                LOGGER.error(DEVICE_ASSOCIATION_ERROR, imei, serialNumber, bssid, e.getErrorMessage());
                hcpServicesFailureResponse = setErrorCodeAndMessage(BAD_REQUEST_STATUS_CODE, e.getMessage(),
                        requestId, e.getCode(), e.getErrorMessage());
                return RestResponse.createFailureResponse(hcpServicesFailureResponse);
            } catch (ApiPreConditionFailedException e) {
                LOGGER.error(DEVICE_ASSOCIATION_ERROR, imei, serialNumber, bssid, e.generalMessage());
                hcpServicesFailureResponse = setErrorCodeAndMessage(PRECONDITION_FAILED_STATUS_CODE, e.getMessage(),
                        requestId, e.getCode(), e.generalMessage());
                return RestResponse.createFailureResponse(hcpServicesFailureResponse);
            } catch (Exception e) {
                Map<Object, Object> details = new LinkedHashMap<>();
                details.put("imei", delegateAssociationRequest.getImei());
                details.put(SERIAL_NUMBER, serialNumber);
                details.put(ErrorUtils.ERROR_CODE_KEY, GENERAL_ERROR.getCode());
                LOGGER.error("{}", ErrorUtils.buildError(DEVICE_ASSOC_ERROR_MESSAGE, e, details));
                hcpServicesFailureResponse = setErrorCodeAndMessage(GENERAL_ERROR_STATUS_CODE,
                        GENERAL_ERROR.getMessage(), requestId, GENERAL_ERROR.getCode(),
                        GENERAL_ERROR.getGeneralMessage());
                return RestResponse.createFailureResponse(hcpServicesFailureResponse);
            }
        }
    }

    /**
     * Terminates the M2M self association for a device.
     *
     * @param deviceStatusRequest The request object containing device status information.
     * @param userId              The user ID obtained from the request header.
     * @param requestId           The correlation ID obtained from the request header.
     * @return A ResponseEntity representing the termination response.
     */
    @PostMapping(value = "/v1/associations/self/terminate", produces = "application/json")
    @Operation(summary = "POST /v1/associations/self/terminate", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR, scopes = { "SelfManage" })
    public ResponseEntity<Object> terminateM2MselfAssociation(
            @Valid @RequestBody DeviceStatusRequest deviceStatusRequest,
            @RequestHeader(USER_ID) String userId, @RequestHeader(value = CORRELATION_ID) String requestId) {
        // 2.33 Release - Sonar RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE codesmell fix
        String deviceStatusRequestData = deviceStatusRequest.toString().replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "");
        LOGGER.info("## terminateM2MSelfAssociation Controller - START request: {}", deviceStatusRequestData);
        String requestUserId = replaceNewLineAndCarriage(userId);
        AssociationApiResponse<AssociateDeviceResponse> response = new AssociationApiResponse<>();
        HcpServicesFailureResponse hcpServicesFailureResponse;
        if (isNotValidUserId(userId)) {
            hcpServicesFailureResponse = setErrorCodeAndMessage(BAD_REQUEST_STATUS_CODE,
                    INVALID_USER_ID_ERR_MSG.getMessage(), requestId, INVALID_USER_ID_ERR_MSG.getCode(),
                    INVALID_USER_ID_ERR_MSG.getGeneralMessage());
            return RestResponse.createFailureResponse(hcpServicesFailureResponse);
        } else {
            LOGGER.info(USERID_FROM_REQUEST_HEADER, requestUserId);
            deviceStatusRequest.setUserId(userId);
            try {
                LOGGER.info("##Validate Perform Terminate");
                boolean performTerminate = validateTermination(requestUserId, deviceStatusRequest, false);
                String deleteVehicleProfileUrl = vehicleProfileService.fetchVehicleProfileUrl(deviceStatusRequest);
                int updateCount =
                    deviceAssocFactoryService.terminateM2Massociation(deviceStatusRequest, userId, NOT_ADMIN, false);
                return selfM2mTerminate(updateCount, performTerminate, deleteVehicleProfileUrl, response, requestId);
            } catch (ApiValidationFailedException e) {
                LOGGER.error(TERMINATION_WITH_USER_ERROR, userId, e.getErrorMessage());
                hcpServicesFailureResponse = setErrorCodeAndMessage(BAD_REQUEST_STATUS_CODE, e.getMessage(), requestId,
                    e.getCode(), e.getErrorMessage());
                return RestResponse.createFailureResponse(hcpServicesFailureResponse);
            } catch (ApiResourceNotFoundException e) {
                LOGGER.error(TERMINATION_WITH_USER_ERROR, userId, e.getErrorMessage());
                hcpServicesFailureResponse = setErrorCodeAndMessage(BAD_REQUEST_STATUS_CODE, e.getMessage(),
                        requestId, e.getCode(), e.getErrorMessage());
                return RestResponse.createFailureResponse(hcpServicesFailureResponse);
            } catch (ApiPreConditionFailedException e) {
                LOGGER.error(TERMINATION_WITH_USER_ERROR, userId, e.generalMessage());
                hcpServicesFailureResponse = setErrorCodeAndMessage(PRECONDITION_FAILED_STATUS_CODE, e.getMessage(),
                        requestId, e.getCode(), e.generalMessage());
                return RestResponse.createFailureResponse(hcpServicesFailureResponse);
            } catch (ApiNotificationException e) {
                LOGGER.error(TERMINATION_WITH_USER_ERROR, userId, e.getErrorMessage());
                hcpServicesFailureResponse = setErrorCodeAndMessage(GENERAL_ERROR_STATUS_CODE, e.getMessage(),
                        requestId, e.getCode(), e.getErrorMessage());
                return RestResponse.createFailureResponse(hcpServicesFailureResponse);
            } catch (ApiTechnicalException e) {
                LOGGER.error(DEVICE_TERMINATION_WITH_USER_ERROR, userId, e.getErrorMessage());
                hcpServicesFailureResponse = setErrorCodeAndMessage(GENERAL_ERROR_STATUS_CODE, e.getMessage(),
                        requestId, e.getCode(), e.getErrorMessage());
                return RestResponse.createFailureResponse(hcpServicesFailureResponse);
            } catch (Exception e) {
                hcpServicesFailureResponse = generalException(deviceStatusRequest, e, requestId);
                return RestResponse.createFailureResponse(hcpServicesFailureResponse);
            }
        }
    }

    /**
     * Performs self-termination based on the provided parameters.
     *
     * @param updateCount            The number of updates performed.
     * @param performTerminate       A flag indicating whether to perform termination.
     * @param deleteVehicleProfileUrl The URL for deleting the vehicle profile.
     * @param response               The response object.
     * @param requestId              The ID of the request.
     * @return The ResponseEntity containing the result of the self-termination.
     */
    public ResponseEntity<Object> selfM2mTerminate(int updateCount, boolean performTerminate,
                                                   String deleteVehicleProfileUrl,
                                                   AssociationApiResponse<AssociateDeviceResponse> response,
                                                   String requestId) {
        LOGGER.info("Inside selfM2mTerminate method");
        HcpServicesFailureResponse hcpServicesFailureResponse;
        if (updateCount > 0) {
            LOGGER.info("Exiting selfM2mTerminate method");
            return performTerminateCheck(performTerminate, deleteVehicleProfileUrl, response, requestId);
        } else {
            hcpServicesFailureResponse = setErrorCodeAndMessage(BAD_REQUEST_STATUS_CODE,
                NO_VALID_ASSOERR.getMessage(), requestId, NO_VALID_ASSOERR.getCode(),
                NO_VALID_ASSOERR.getGeneralMessage());
            LOGGER.info("Exiting selfM2mTerminate method");
            return RestResponse.createFailureResponse(hcpServicesFailureResponse);
        }
    }

    /**
     * Performs a termination check based on the given parameters.
     *
     * @param performTerminate       a boolean indicating whether to perform the termination check
     * @param deleteVehicleProfileUrl the URL for deleting the vehicle profile
     * @param response               the response object to be updated
     * @param requestId              the ID of the request
     * @return a ResponseEntity containing the updated response object
     */
    public ResponseEntity<Object> performTerminateCheck(boolean performTerminate, String deleteVehicleProfileUrl,
                                                        AssociationApiResponse<AssociateDeviceResponse> response,
                                                        String requestId) {
        LOGGER.info("Inside performTerminateCheck method");
        if (performTerminate) {
            boolean isVpDeleted = vehicleProfileService.deleteVehicleProfile(deleteVehicleProfileUrl);
            if (!isVpDeleted) {
                setResponseObject(SUCCESS_STATUS_CODE, VEHICLE_PROFILE_TERMINATION_FAILED, response);
                response.setRequestId(requestId);
                LOGGER.info("## M2M SELF Termination Controller - END");
                return RestResponse.ok(response);
            }
        }
        setResponseObject(SUCCESS_STATUS_CODE, TERMINATION_SUCCESS, response);
        response.setRequestId(requestId);
        LOGGER.info("## M2M SELF Termination Controller - END");
        return RestResponse.ok(response);
    }

    
    /**
     * Handles general exceptions and constructs a failure response for the HCP services.
     *
     * @param deviceStatusRequest The request object containing device status details such as
     *                            IMEI, serial number, and device ID.
     * @param e                   The exception that occurred.
     * @param requestId           The unique identifier for the request.
     * @return A {@code HcpServicesFailureResponse} object containing the error code, message,
     *         and other relevant details.
     */
    public HcpServicesFailureResponse generalException(DeviceStatusRequest deviceStatusRequest,
                                                       Exception e, String requestId) {
        String error = ErrorUtils.buildError(DIS_ASSOCIATION_WITH_DEVICE_ERROR, e, getErrorMap(
            deviceStatusRequest.getImei(), deviceStatusRequest.getSerialNumber(),
            deviceStatusRequest.getDeviceId(), GENERAL_ERROR.getCode()));
        LOGGER.error("{}", error);
        return setErrorCodeAndMessage(GENERAL_ERROR_STATUS_CODE, GENERAL_ERROR.getMessage(), requestId,
            GENERAL_ERROR.getCode(), GENERAL_ERROR.getGeneralMessage());
    }

    /**
     * Terminates the M2M association for a device.
     *
     * @param deviceStatusRequest The request object containing device status information.
     * @param userId              The user ID obtained from the request header.
     * @param adminUserId         The admin user ID obtained from the request header (optional).
     * @param isAdmin             A boolean indicating whether the user is an admin.
     * @param requestId           The correlation ID obtained from the request header.
     * @return A ResponseEntity representing the result of the termination operation.
     */
    @PostMapping(value = "/v1/associations/terminate", produces = "application/json")
    @Operation(summary = "POST /v1/associations/terminate", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR, scopes = { "SelfManage,IgniteSystem" })
    public ResponseEntity<Object> terminateM2Massociation(@Valid @RequestBody DeviceStatusRequest deviceStatusRequest,
                                                     @RequestHeader(USER_ID) String userId,
                                                     @RequestHeader(name = ADMIN_USER_ID, required = false,
                                                         defaultValue = NOT_ADMIN) String adminUserId,
                                                     @RequestHeader(name = "isAdmin",
                                                             defaultValue = "false") boolean isAdmin,
                                                     @RequestHeader(value = CORRELATION_ID) String requestId) {
        String deviceStatusRequestData =
                deviceStatusRequest.toString().replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "");
        LOGGER.info("## terminateM2MSelfAssociation Controller - START request: {}", deviceStatusRequestData);
        String requestAdminUserId = replaceNewLineAndCarriage(adminUserId);
        String requestUserId = replaceNewLineAndCarriage(userId);
        LOGGER.info("## Request Headers adminUserId {} isAdmin {}", requestAdminUserId, isAdmin);
        HcpServicesFailureResponse hcpServicesFailureResponse;
        if (validateAdminRequests(isAdmin, deviceStatusRequest)) {
            hcpServicesFailureResponse = setErrorCodeAndMessage(BAD_REQUEST_STATUS_CODE,
                    M2M_ADMIN_REQUEST_INTEGRITY_ERROR.getMessage(),
                    requestId, M2M_ADMIN_REQUEST_INTEGRITY_ERROR.getCode(),
                    M2M_ADMIN_REQUEST_INTEGRITY_ERROR.getGeneralMessage());
            return RestResponse.createFailureResponse(hcpServicesFailureResponse);
        }

        if (isNotValidUserId(userId)) {
            hcpServicesFailureResponse = setErrorCodeAndMessage(BAD_REQUEST_STATUS_CODE,
                    INVALID_USER_ID_ERR_MSG.getMessage(), requestId, INVALID_USER_ID_ERR_MSG.getCode(),
                    INVALID_USER_ID_ERR_MSG.getGeneralMessage());
            return RestResponse.createFailureResponse(hcpServicesFailureResponse);
        } else {
            LOGGER.info(USERID_FROM_REQUEST_HEADER, requestUserId);

            try {
                LOGGER.info("## Validate Perform Terminate");
                boolean performTerminate = validateTermination(requestUserId, deviceStatusRequest, isAdmin);
                String deleteVehicleProfileUrl = vehicleProfileService.fetchVehicleProfileUrl(deviceStatusRequest);
                return m2mTerminate(performTerminate, isAdmin, userId, adminUserId, deviceStatusRequest,
                    deleteVehicleProfileUrl, requestId);
            } catch (ApiValidationFailedException e) {
                LOGGER.error(TERMINATION_WITH_USER_ERROR, userId, e.getErrorMessage());
                hcpServicesFailureResponse = setErrorCodeAndMessage(BAD_REQUEST_STATUS_CODE, e.getMessage(), requestId,
                    e.getCode(), e.getErrorMessage());
                return RestResponse.createFailureResponse(hcpServicesFailureResponse);
            } catch (ApiResourceNotFoundException e) {
                LOGGER.error(TERMINATION_WITH_USER_ERROR, userId, e.getErrorMessage());
                hcpServicesFailureResponse = setErrorCodeAndMessage(BAD_REQUEST_STATUS_CODE, e.getMessage(),
                        requestId, e.getCode(), e.getErrorMessage());
                return RestResponse.createFailureResponse(hcpServicesFailureResponse);
            } catch (ApiPreConditionFailedException e) {
                LOGGER.error(TERMINATION_WITH_USER_ERROR, userId, e.generalMessage());
                hcpServicesFailureResponse = setErrorCodeAndMessage(PRECONDITION_FAILED_STATUS_CODE, e.getMessage(),
                        requestId, e.getCode(), e.generalMessage());
                return RestResponse.createFailureResponse(hcpServicesFailureResponse);
            } catch (ApiNotificationException e) {
                LOGGER.error(TERMINATION_WITH_USER_ERROR, userId, e.getErrorMessage());
                hcpServicesFailureResponse = setErrorCodeAndMessage(GENERAL_ERROR_STATUS_CODE, e.getMessage(),
                        requestId, e.getCode(), e.getErrorMessage());
                return RestResponse.createFailureResponse(hcpServicesFailureResponse);
            } catch (ApiTechnicalException e) {
                LOGGER.error(DEVICE_TERMINATION_WITH_USER_ERROR, userId, e.getErrorMessage());
                hcpServicesFailureResponse = setErrorCodeAndMessage(GENERAL_ERROR_STATUS_CODE, e.getMessage(),
                        requestId, e.getCode(), e.getErrorMessage());
                return RestResponse.createFailureResponse(hcpServicesFailureResponse);
            } catch (Exception e) {
                hcpServicesFailureResponse = generalError(deviceStatusRequest, requestId, e);
                return RestResponse.createFailureResponse(hcpServicesFailureResponse);
            }
        }
    }

    /**
     * Terminates the M2M association for a device.
     *
     * @param performTerminate       Flag indicating whether to perform the termination.
     * @param isAdmin                Flag indicating whether the user is an admin.
     * @param userId                 The ID of the user.
     * @param adminUserId            The ID of the admin user.
     * @param deviceStatusRequest    The device status request.
     * @param deleteVehicleProfileUrl The URL for deleting the vehicle profile.
     * @param requestId              The ID of the request.
     * @return The response entity object.
     */
    public ResponseEntity<Object> m2mTerminate(boolean performTerminate, boolean isAdmin, String userId,
                                               String adminUserId, DeviceStatusRequest deviceStatusRequest,
                                               String deleteVehicleProfileUrl, String requestId) {
        LOGGER.info("Inside m2mTerminate method");
        AssociationApiResponse<AssociateDeviceResponse> response = new AssociationApiResponse<>();
        ResponseEntity<Object> res;
        res = m2mTermination(isAdmin, userId, deviceStatusRequest, adminUserId, response, requestId);
        if (performTerminate) {
            boolean isVpDeleted = vehicleProfileService.deleteVehicleProfile(deleteVehicleProfileUrl);
            if (!isVpDeleted) {
                setResponseObject(SUCCESS_STATUS_CODE, VEHICLE_PROFILE_TERMINATION_FAILED, response);
                response.setRequestId(requestId);
                LOGGER.info("## M2M Termination Controller - END");
                return RestResponse.ok(response);
            }
        }
        LOGGER.info("## M2M Termination Controller - END");
        return res;
    }

    /**
     * Handles general errors that occur during device association.
     *
     * @param deviceStatusRequest The device status request object.
     * @param requestId The unique identifier for the request.
     * @param e The exception that occurred.
     * @return The HcpServicesFailureResponse object containing the error details.
     */
    public HcpServicesFailureResponse generalError(DeviceStatusRequest deviceStatusRequest, String requestId,
                                                   Exception e) {
        Map<Object, Object> details = new LinkedHashMap<>();
        details.put("imei", deviceStatusRequest.getImei());
        details.put(SERIAL_NUMBER, deviceStatusRequest.getSerialNumber());
        details.put(DEVICE_ID, deviceStatusRequest.getDeviceId());
        details.put(ErrorUtils.ERROR_CODE_KEY, GENERAL_ERROR.getCode());
        String error = ErrorUtils.buildError(DIS_ASSOCIATION_WITH_DEVICE_ERROR, e, details);
        LOGGER.error("{}", error);
        return setErrorCodeAndMessage(GENERAL_ERROR_STATUS_CODE, GENERAL_ERROR.getMessage(), requestId,
            GENERAL_ERROR.getCode(), GENERAL_ERROR.getGeneralMessage());
    }

    /**
     * Performs M2M termination for device association.
     *
     * @param isAdmin              a boolean indicating whether the user is an admin or not
     * @param userId               the ID of the user
     * @param deviceStatusRequest  the device status request object
     * @param adminUserId          the ID of the admin user
     * @param response             the association API response object
     * @param requestId            the ID of the request
     * @return                     a ResponseEntity object representing the result of the termination
     */
    private ResponseEntity<Object> m2mTermination(boolean isAdmin, String userId,
                              DeviceStatusRequest deviceStatusRequest, String adminUserId,
                              AssociationApiResponse<AssociateDeviceResponse> response, String requestId) {
        LOGGER.info("Inside m2mTermination method");
        int updateCount = 0;
        updateCount = performTerminateAssociation(isAdmin, userId, deviceStatusRequest, adminUserId);
        if (updateCount <= 0) {
            HcpServicesFailureResponse hcpServicesFailureResponse = setErrorCodeAndMessage(
                    BAD_REQUEST_STATUS_CODE, NO_VALID_ASSOERR.getMessage(),
                    requestId, NO_VALID_ASSOERR.getCode(), NO_VALID_ASSOERR.getGeneralMessage());
            return RestResponse.createFailureResponse(hcpServicesFailureResponse);
        }
        setResponseObject(SUCCESS_STATUS_CODE, TERMINATION_SUCCESS, response);
        response.setRequestId(requestId);
        LOGGER.info("Exiting m2mTermination method");
        return RestResponse.ok(response);
    }

    /**
     * Validates admin requests based on the isAdmin flag and the device status request.
     *
     * @param isAdmin              a boolean indicating whether the request is from an admin or not
     * @param deviceStatusRequest  the device status request to be validated
     * @return                     true if the request is valid, false otherwise
     */
    private boolean validateAdminRequests(boolean isAdmin, DeviceStatusRequest deviceStatusRequest) {
        return (isAdmin && !deviceAssocFactoryService.validateAdminRequest(deviceStatusRequest));
    }

    /**
     * Performs the termination of association for a device.
     *
     * @param isAdmin              a boolean indicating whether the user is an admin or not
     * @param userId               the ID of the user
     * @param deviceStatusRequest  the device status request object
     * @param adminUserId          the ID of the admin user
     * @return the number of updates made during the termination process
     */
    private int performTerminateAssociation(boolean isAdmin, String userId, DeviceStatusRequest deviceStatusRequest,
                                            String adminUserId) {
        int updateCount;
        if (isAdmin) {
            deviceStatusRequest.setUserId(userId);
            updateCount =
                deviceAssocFactoryService.terminateM2Massociation(deviceStatusRequest, userId, adminUserId, true);
        } else {
            updateCount =
                deviceAssocFactoryService.terminateM2Massociation(deviceStatusRequest, userId, adminUserId, false);
        }
        return updateCount;
    }

    /**
     * Validates whether to perform termination or disassociation for a device.
     *
     * @param deviceStatusRequest The request object containing device status information.
     * @param userId              The user ID associated with the request.
     * @param isAdmin             A flag indicating whether the user is an admin.
     * @param requestId           The ID of the request.
     * @return A ResponseEntity containing the validation result.
     */
    @PostMapping("/validatePerformTerminate")
    @Hidden
    public ResponseEntity<Object> validatePerformTerminate(@RequestBody DeviceStatusRequest deviceStatusRequest,
                                                      @RequestHeader(USER_ID) String userId,
                                                      @RequestHeader(name = "isAdmin", required = false)
                                                          boolean isAdmin,
                                                      @RequestHeader(value = CORRELATION_ID) String requestId) {
        AssociationApiResponse<M2Mterminate> response = new AssociationApiResponse<>();
        HcpServicesFailureResponse hcpServicesFailureResponse;
        try {
            LOGGER.info("##Validate Perform Terminate Controller Started");
            M2Mterminate m2mTerminate =
                deviceAssocFactoryService.validatePerformTerminate(userId, deviceStatusRequest, isAdmin);
            response.setHttpStatusCode(SUCCESS_STATUS_CODE);
            response.setCode(VALIDATE_PERFORM_TERMINATION_SUCCESS.getCode());
            response.setReason(VALIDATE_PERFORM_TERMINATION_SUCCESS.getGeneralMessage());
            response.setMessage(VALIDATE_PERFORM_TERMINATION_SUCCESS.getMessage());
            response.setData(Collections.singletonList(m2mTerminate));
            return RestResponse.ok(response);
        } catch (ApiValidationFailedException e) {
            LOGGER.error(VALIDATING_TERMINATION_ERROR, userId, e.getErrorMessage());
            hcpServicesFailureResponse = setErrorCodeAndMessage(BAD_REQUEST_STATUS_CODE, e.getMessage(), requestId,
                    e.getCode(), e.getErrorMessage());
            return RestResponse.createFailureResponse(hcpServicesFailureResponse);
        } catch (ApiResourceNotFoundException e) {
            LOGGER.error(VALIDATING_TERMINATION_ERROR, userId, e.getErrorMessage());
            hcpServicesFailureResponse = setErrorCodeAndMessage(BAD_REQUEST_STATUS_CODE, e.getMessage(), requestId,
                    e.getCode(), e.getErrorMessage());
            return RestResponse.createFailureResponse(hcpServicesFailureResponse);
        } catch (ApiPreConditionFailedException e) {
            hcpServicesFailureResponse = setErrorCodeAndMessage(PRECONDITION_FAILED_STATUS_CODE, e.getMessage(),
                    requestId, e.getCode(), e.generalMessage());
            return RestResponse.createFailureResponse(hcpServicesFailureResponse);
        } catch (Exception e) {
            Map<Object, Object> details = new LinkedHashMap<>();
            details.put(USERID, userId);
            details.put(ErrorUtils.ERROR_CODE_KEY, GENERAL_ERROR.getCode());
            LOGGER.error("{}",
                    ErrorUtils.buildError("## Error has occurred while validating whether to"
                            + " perform termination or disassociation.", e, details));
            hcpServicesFailureResponse = setErrorCodeAndMessage(GENERAL_ERROR_STATUS_CODE, GENERAL_ERROR.getMessage(),
                    requestId, GENERAL_ERROR.getCode(), GENERAL_ERROR.getGeneralMessage());
            return RestResponse.createFailureResponse(hcpServicesFailureResponse);
        }
    }

    /**
     * Triggers a Kafka event with the specified device information, event ID, topic name, and key.
     *
     * @param deviceInfo The device information.
     * @param eventId The event ID.
     * @param topicName The topic name.
     * @param key The key.
     * @return A ResponseEntity representing the HTTP response.
     */
    @PostMapping("/triggerKafkaEvent")
    @Hidden
    public ResponseEntity<Object> triggerKafkaEvent(@RequestBody DeviceInfo deviceInfo,
                                               @RequestParam("eventId") String eventId,
                                               @RequestParam("topicName") String topicName,
                                               @RequestParam("key") String key) {
        AssociationApiResponse<M2Mterminate> response = new AssociationApiResponse<>();
        // 2.33 Release - Sonar CRLF_INJECTION_LOGS vulnerability fix
        LOGGER.info("##triggerKafkaEvent Controller Started :{},eventId:{} topic:{} ,key:{}",
            deviceInfo != null ? deviceInfo.toString().replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "") : null,
            eventId != null ? eventId.replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "") : null,
            topicName != null ? topicName.replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "") : null,
            key != null ? key.replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "") : null);
        deviceAssociationService
            .triggerKafkaEvent(new TriggerKafkaEventRequestDto(deviceInfo, topicName, eventId, key));

        return RestResponse.ok(response);

    }

    private HcpServicesFailureResponse setErrorCodeAndMessage(int httpSatatusCode, String message,
                                                              String requestId, String code,
                                                              String reason) {
        HcpServicesFailureResponse hcpServicesFailureResponse = new HcpServicesFailureResponse();
        hcpServicesFailureResponse.setHttpStatusCode(httpSatatusCode);
        hcpServicesFailureResponse.setMessage(message);
        hcpServicesFailureResponse.setRequestId(requestId);
        hcpServicesFailureResponse.setCode(code);
        hcpServicesFailureResponse.setReason(reason);
        return hcpServicesFailureResponse;
    }

    private static Map<Object, Object> getErrorMap(String imei, String serialNumber, String deviceId,
                                                   String errorCode) {
        Map<Object, Object> errorDetails = new LinkedHashMap<>();
        errorDetails.put("imei", imei);
        errorDetails.put(SERIAL_NUMBER, serialNumber);
        errorDetails.put(DEVICE_ID, deviceId);
        errorDetails.put(ErrorUtils.ERROR_CODE_KEY, errorCode);
        return errorDetails;
    }
}
