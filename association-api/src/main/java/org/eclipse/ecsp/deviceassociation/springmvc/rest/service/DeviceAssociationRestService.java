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

package org.eclipse.ecsp.deviceassociation.springmvc.rest.service;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.ecsp.common.ErrorUtils;
import org.eclipse.ecsp.common.HcpServicesFailureResponse;
import org.eclipse.ecsp.common.RecordStats;
import org.eclipse.ecsp.deviceassociation.lib.enums.DeviceAttributeEnums;
import org.eclipse.ecsp.deviceassociation.lib.exception.DeviceReplaceException;
import org.eclipse.ecsp.deviceassociation.lib.exception.InvalidPinException;
import org.eclipse.ecsp.deviceassociation.lib.exception.InvalidUserAssociation;
import org.eclipse.ecsp.deviceassociation.lib.exception.NoSuchEntityException;
import org.eclipse.ecsp.deviceassociation.lib.exception.ObserverMessageProcessFailureException;
import org.eclipse.ecsp.deviceassociation.lib.model.DeviceAssociation;
import org.eclipse.ecsp.deviceassociation.lib.model.DeviceAssociationHistory;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociateDeviceRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociateDeviceResponse;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociationDetailsRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociationDetailsResponse;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceAssociationHistorySuccessResponse;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceAssosiationDetails;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceInfo;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceState;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceStatusRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.ReplaceFactoryDataRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.StateChangeRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.User;
import org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum;
import org.eclipse.ecsp.deviceassociation.lib.service.DeviceAssociationService;
import org.eclipse.ecsp.deviceassociation.lib.service.DeviceAssociationWithFactDataService;
import org.eclipse.ecsp.deviceassociation.lib.service.DeviceAssosiationDetailsService;
import org.eclipse.ecsp.deviceassociation.lib.service.MessageConstants;
import org.eclipse.ecsp.deviceassociation.lib.service.UserBelongingValidator;
import org.eclipse.ecsp.deviceassociation.lib.service.VehicleProfileService;
import org.eclipse.ecsp.deviceassociation.springmvc.rest.support.DeviceAssociationHistoryValidator;
import org.eclipse.ecsp.deviceassociation.springmvc.rest.support.RestResponse;
import org.eclipse.ecsp.security.Security;
import org.eclipse.ecsp.services.shared.rest.support.SimpleResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.eclipse.ecsp.deviceassociation.lib.service.MessageConstants.INVALID_USER_DEVICE_DETAILS;
import static org.eclipse.ecsp.deviceassociation.lib.service.MessageConstants.USER_ID_MANDATORY;
import static org.springframework.http.HttpStatus.OK;

/**
 * This class represents the REST service for device association.
 * It provides APIs for associating devices with users and fetching associated devices.
 */
@Validated
@RestController
public class DeviceAssociationRestService {

    /**
     * A constant representing a successful operation or response.
     */
    public static final String SUCCESS = "Success";

    /**
     * Constant message indicating that the termination of a device was successful,
     * but the deletion of the associated vehicle profile failed.
     */
    public static final String VP_DELETE_FAILED = "Termination of device was success but failed to delete vehicle "
        + "profile";
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceAssociationRestService.class);
    private static final String USER_ID = "user-id";
    private static final String PROVISIONED = "PROVISIONED";
    private static final String CONNECTION_REFUSED = "Connection refused";
    private static final String GENERAL_ERROR = "Not successful. Something went wrong. Please contact admin";
    /**
     * A regular expression pattern that matches carriage return (`\r`) and newline (`\n`) characters.
     * This can be used to identify or remove line breaks in strings.
     */
    public static final String CARRIAGE_AND_NEWLINE_REGEX = "[\r\n]";
    /**
     * A constant representing the response code used in the Device Association REST service.
     * This code is typically used to identify specific responses or errors in the service.
     */
    public static final String RESPONSE_CODE = "HCP-001";
    /**
     * Error message indicating that the retrieval of device-based user association 
     * history details has failed.
     */
    public static final String USER_ASSOC_RETRIEVAL_FAILED =
            "Device based user association history details retrieval failed";
    private static final int SIZE = 20;
    private static final int STATUS_CODE_200 = 200;
    private static final int STATUS_CODE_400 = 400;
    private static final int STATUS_CODE_404 = 404;
    private static final int STATUS_CODE_500 = 500;
    private static final int CAPACITY = 100;

    /**
     * Indicates whether the replacement of IVI (In-Vehicle Infotainment) devices is enabled.
     * This value is configured through the application properties using the key 
     * {@code replace_ivi_device_enabled}. If not explicitly set, it defaults to {@code false}.
     */
    @Value("${replace_ivi_device_enabled:false}")
    public boolean replaceIviDeviceEnabled;
    @Autowired
    private DeviceAssociationService deviceAssociationService;
    @Autowired
    private DeviceAssociationWithFactDataService deviceAssocFactoryService;
    @Autowired
    private DeviceAssosiationDetailsService deviceAssosiationDetailsService;
    @Autowired
    private UserBelongingValidator<Map<String, Object>> userBelongingValidator;
    @Autowired
    private VehicleProfileService vehicleProfileService;

    private static <T> String replaceNewLineAndCarriage(T data) {
        return data != null ? data.toString().replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "") : null;
    }

    /**
     * Associates a device with a user.
     *
     * @param request The request object containing the necessary information for device association.
     * @return A ResponseEntity containing a SimpleResponseMessage indicating the result of the device association.
     */
    @PostMapping(value = "v1/user/devices/associate/", produces = "application/json")
    @Operation(summary = "POST v1/user/devices/associate/", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR,
            scopes = { "AssociateMyselfToVehicle,UserDeviceAssocn,SelfManage" })
    public ResponseEntity<SimpleResponseMessage> associateDevice(@RequestBody AssociateDeviceRequest request) {
        String associateDeviceRequestData =
                request.toString().replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "");
        LOGGER.info("Invoking associateDevice for request: {}", associateDeviceRequestData);
        return RestResponse.createSimpleErrorResponse(HttpStatus.BAD_REQUEST, "V1 version is not supported");
    }

    /**
     * Retrieves the associated devices of the user.
     *
     * @param httpServletRequest the HTTP servlet request
     * @return a ResponseEntity containing the associated devices of the user
     */
    @GetMapping(value = "v1/user/associations/", produces = "application/json")
    @Operation(summary = "GET v1/user/associations/", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR,
            scopes = { "AssociateMyselfToVehicle,UserDeviceAssocn,SelfManage" })
    public ResponseEntity<Object> associatedDevicesOfUser(HttpServletRequest httpServletRequest) {
        LOGGER.info("associatedDevicesOfUser");
        String userId = httpServletRequest.getHeader(USER_ID);
        if (isNotValidUserId(userId)) {
            return RestResponse.createSimpleErrorResponses(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR);
        }
        return RestResponse.ok(deviceAssociationService.getAssociatedDevicesForUser(userId));
    }

    /**
     * Associates a device with factory data.
     *
     * @param device   The AssociateDeviceRequest object containing the device information.
     * @param request  The HttpServletRequest object.
     * @return         The ResponseEntity containing the response data.
     */
    @PostMapping(value = "v2/user/devices/associate/", produces = "application/json")
    @Operation(summary = "POST v2/user/devices/associate/", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR,
            scopes = { "AssociateMyselfToVehicle,UserDeviceAssocn,SelfManage" })
    public ResponseEntity<Object> associateDeviceWithFactoryData(@Valid @RequestBody AssociateDeviceRequest device,
                                                            HttpServletRequest request) {
        String associateDeviceRequestData = device.toString().replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "");
        LOGGER.info("associateDeviceWithFactoryData - START AssociateDeviceRequest: {}", associateDeviceRequestData);
        String userId = request.getHeader(USER_ID);
        if (isNotValidUserId(userId)) {
            return RestResponse.createSimpleErrorResponses(HttpStatus.INTERNAL_SERVER_ERROR,
                    "UserId from header either null or empty");
        }
        device.setUserId(userId);
        AssociateDeviceResponse associateDeviceResponse;
        try {
            associateDeviceResponse = deviceAssocFactoryService.associateDevice(device);
        } catch (InvalidPinException e) {
            LOGGER.error("Pin validation failed: device - {}, error - {}", device, e);
            return RestResponse.createSimpleErrorResponses(HttpStatus.BAD_REQUEST,
                "Pin validation failed for this device");
        } catch (NoSuchEntityException e) {
            if (e.getMessage() != null) {
                LOGGER.error("NoSuchEntityException :{}", e.getMessage());
                return RestResponse.createSimpleErrorResponses(HttpStatus.BAD_REQUEST, e.getMessage());
            } else {
                return RestResponse.createSimpleErrorResponses(HttpStatus.BAD_REQUEST, e.getSimpleResponseMessage());
            }
        } catch (Exception e) {
            Map<Object, Object> details = new LinkedHashMap<>();
            details.put("imei", device.getImei());
            details.put("serialNumber", device.getSerialNumber());
            details.put(ErrorUtils.ERROR_CODE_KEY, ApiMessageEnum.GENERAL_ERROR.getCode());
            LOGGER.error("{}",
                ErrorUtils.buildError("## Error has occurred while performing device association.", e,
                    details));
            return RestResponse.createSimpleErrorResponses(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR);
        }
        return RestResponse.ok(associateDeviceResponse);
    }

    /**
     * Retrieves the associated devices of a user along with factory data.
     *
     * @param httpServletRequest The HTTP servlet request.
     * @return A ResponseEntity containing the associated devices of the user with factory data.
     */
    @GetMapping(value = "v2/user/associations/", produces = "application/json")
    @Operation(summary = "GET v2/user/associations/", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR, scopes = { "SelfManage,Dongle,DEFAULT" })
    public ResponseEntity<Object> associatedDevicesOfUserWithFactoryData(HttpServletRequest httpServletRequest) {
        LOGGER.info("associatedDevicesOfUserWithFactoryData - START");
        String userId = httpServletRequest.getHeader(USER_ID);
        if (isNotValidUserId(userId)) {
            return RestResponse.createSimpleErrorResponses(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR);
        }
        List<DeviceAssociation> deviceAssociations = deviceAssocFactoryService.getAssociatedDevicesForUser(userId);
        return RestResponse.ok(deviceAssociations);
    }

    /**
     * Retrieves the device association details for a given user.
     *
     * @param userId the ID of the user
     * @return a ResponseEntity containing the device association details
     */
    @GetMapping(value = "v1/associationdetails", produces = "application/json")
    @Operation(summary = "GET v1/associationdetails", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR, scopes = { "IgniteSystem" })
    public ResponseEntity<Object> getDeviceAssociationDetails(@RequestParam(name = "userId", required = false,
                                                    defaultValue = "") String userId) {
        LOGGER.info("Get device association details for user - {}.",
            (userId == null) ? null : userId.replaceAll(CARRIAGE_AND_NEWLINE_REGEX, ""));

        List<DeviceAssosiationDetails> deviceAssociationDetails;

        try {
            DeviceAssociation deviceAssociation = new DeviceAssociation();
            deviceAssociation.setUserId(userId);
            deviceAssociationDetails = deviceAssosiationDetailsService.getDeviceAssosiationDetails(
                deviceAssociation.getUserId());
        } catch (NoSuchEntityException ex) {
            // error handler should be implemented later.
            return RestResponse.createSimpleErrorResponses(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
        return new ResponseEntity<>(deviceAssociationDetails, OK);
    }

    /**
     * Retrieves the details of a specific association based on the given association ID.
     *
     * @param associationId       The ID of the association to retrieve details for.
     * @param httpServletRequest The HTTP servlet request object.
     * @return A ResponseEntity containing the association details if found, or an error response if not found.
     */
    @GetMapping(value = "v1/user/associations/{associationID}", produces = "application/json")
    @Operation(summary = "GET v1/user/associations/{associationID}", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR,
            scopes = { "AssociateMyselfToVehicle,UserDeviceAssocn,SelfManage" })
    public ResponseEntity<Object> getAssociationDetails(@PathVariable("associationID") long associationId,
                                                   HttpServletRequest httpServletRequest) {
        LOGGER.info("getAssociationDetails - START associationID: {}", associationId);
        String userId = httpServletRequest.getHeader(USER_ID);
        if (isNotValidUserId(userId)) {
            return RestResponse.createSimpleErrorResponses(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR);
        }
        DeviceAssociation deviceAssociation = deviceAssociationService.getAssociationDetails(associationId, userId);
        return (deviceAssociation == null) ? RestResponse.notFound(HttpStatus.NOT_FOUND, "No record found") :
            RestResponse.ok(deviceAssociation);
    }

    /**
     * Retrieves the association details for a user based on the provided parameters.
     *
     * @param httpServletRequest   The HttpServletRequest object.
     * @param imeiParam            The IMEI parameter (optional).
     * @param serialNumberParam    The serial number parameter (optional).
     * @param deviceIdParam        The device ID parameter (optional).
     * @return                     The ResponseEntity containing the association details response.
     */
    @GetMapping(value = "/v1/users/association/details", produces = "application/json")
    @Operation(summary = "GET /v1/users/association/details", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR,
            scopes = { "AssociateMyselfToVehicle,UserDeviceAssocn,SelfManage" })
    public ResponseEntity<Object> getAssociationDetails(HttpServletRequest httpServletRequest,
           @RequestParam(value = "imei", required = false, defaultValue = "") String imeiParam,
           @RequestParam(value = "serialnumber", required = false, defaultValue = "") String serialNumberParam,
           @RequestParam(value = "deviceid", required = false, defaultValue = "") String deviceIdParam) {
        LOGGER.info("Invoking getAssociationDetails");
        AssociationDetailsResponse associationDetailsResponse;
        String imei = getRequestParameterValues(httpServletRequest, "imei", imeiParam);
        String serialNumber = getRequestParameterValues(httpServletRequest, "serialnumber", serialNumberParam);
        String deviceId = getRequestParameterValues(httpServletRequest, "deviceid", deviceIdParam);
        String userId = httpServletRequest.getHeader(USER_ID);
        if (StringUtils.isEmpty(imei) && StringUtils.isEmpty(serialNumber) && StringUtils.isEmpty(deviceId)) {
            return RestResponse.createSimpleErrorResponses(HttpStatus.BAD_REQUEST,
                MessageConstants.BASIC_DEVICE_DATA_MANDATORY);
        }
        AssociationDetailsRequest associationDetailsRequest = new AssociationDetailsRequest();
        try {
            /*
             * Below condition is to check weather inputs(eg: imei, serialNumber)
             * belongs to user or not.
             */
            if (StringUtils.isNotEmpty(userId)
                && !userBelongingValidator.validateUserBelonging(
                generateQueryConditionMap(imei, serialNumber, deviceId, userId))) {
                return RestResponse.createSimpleErrorResponses(HttpStatus.UNAUTHORIZED,
                    MessageConstants.INVALID_USER_DEVICE_DETAILS);
            }
            String requestImei = replaceNewLineAndCarriage(imei);
            String requestSerialNumber = replaceNewLineAndCarriage(serialNumber);
            String requestDeviceId = replaceNewLineAndCarriage(deviceId);
            LOGGER.debug("From header IMEI: {}, serialNumber: {}, deviceId: {}", requestImei, requestSerialNumber,
                requestDeviceId);
            associationDetailsRequest.setDeviceId(deviceId);
            associationDetailsRequest.setImei(imei);
            associationDetailsRequest.setSerialNumber(serialNumber);
            LOGGER.debug("Get pojo: {}", associationDetailsRequest);
            associationDetailsResponse = deviceAssociationService.getAssociationDetails(associationDetailsRequest);
            if (null == associationDetailsResponse) {
                AssociateDeviceRequest assoDeviceRequest = new AssociateDeviceRequest();
                assoDeviceRequest.setImei(associationDetailsRequest.getImei());
                assoDeviceRequest.setSerialNumber(associationDetailsRequest.getSerialNumber());
                if (((!StringUtils.isEmpty(imei)) || (!StringUtils.isEmpty(serialNumber)))
                        && deviceAssocFactoryService.isInSameState(assoDeviceRequest, PROVISIONED)) {
                    return RestResponse.createSimpleErrorResponses(HttpStatus.BAD_REQUEST,
                            MessageConstants.INVALID_ASSOCIATION_GETDETAILS_STATE);
                }
                String associationDetailsRequestData =
                        associationDetailsRequest.toString().replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "");
                LOGGER.error(
                    "Device association details not found for the requested inputs :{}, URI: {}, RequestObject: {}",
                    associationDetailsRequestData,
                    (httpServletRequest.getRequestURI() == null) ? null :
                        httpServletRequest.getRequestURI().replaceAll(CARRIAGE_AND_NEWLINE_REGEX, ""),
                    associationDetailsRequestData);
                return RestResponse.createSimpleErrorResponses(HttpStatus.NOT_FOUND,
                    "Device association details not found for the requested inputs");
            }
        } catch (Exception e) {
            Map<Object, Object> details = new LinkedHashMap<>();
            details.put("associationDetailsRequest", associationDetailsRequest);
            details.put(ErrorUtils.ERROR_CODE_KEY, ApiMessageEnum.GENERAL_ERROR.getCode());
            LOGGER.error("{}",
                ErrorUtils.buildError("## Exception occurred in /v1/users/association/details/", e,
                    details));
            return RestResponse.createSimpleErrorResponses(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return RestResponse.ok(associationDetailsResponse);
    }

    /**
     * Disassociates a user association with the given association ID.
     *
     * @param associationId       The ID of the association to be disassociated.
     * @param httpServletRequest The HTTP servlet request object.
     * @return A ResponseEntity representing the result of the disassociation operation.
     */
    @DeleteMapping(value = "v1/user/associations/{associationID}/disassociate", produces = "application/json")
    @Hidden
    public ResponseEntity<Object> disassociate(@PathVariable("associationID") long associationId,
                                          HttpServletRequest httpServletRequest) {
        return new ResponseEntity<>("Not supported from 2.14 version onwards", HttpStatus.OK);
    }

    /**
     * Retrieves the user details of a vehicle based on the provided Harman ID.
     *
     * @param harmanId The Harman ID of the vehicle.
     * @return A ResponseEntity containing the user details of the vehicle in JSON format.
     */
    @GetMapping(value = "v1/users", produces = "application/json")
    @Hidden
    public ResponseEntity<Object> getUserDetailsOfVehicle(@RequestParam("harmanId") String harmanId) {
        LOGGER.info("getUserDetailsOfVehicle harmanId: {}", (harmanId == null) ? null :
            harmanId.replaceAll(CARRIAGE_AND_NEWLINE_REGEX, ""));
        if (StringUtils.isBlank(harmanId)) {
            return RestResponse.createSimpleErrorResponses(HttpStatus.BAD_REQUEST, "Invalid harmanId");
        }
        List<User> users = deviceAssociationService.getUserDetailsOfVehicle(harmanId);
        return RestResponse.ok(users);
    }

    /**
     * Checks if a device is already associated with a user.
     *
     * @param serialNumber The serial number of the device.
     * @param httpServletRequest The HTTP servlet request.
     * @return A ResponseEntity containing the result of the association check.
     */
    @GetMapping(value = "v1/devices/{serialNumber}/associated", produces = "application/json")
    @Operation(summary = "GET v1/devices/{serialNumber}/associated", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR,
            scopes = { "AssociateMyselfToVehicle,UserDeviceAssocn,SelfManage" })
    public ResponseEntity<Object> isDeviceAlreadyAssociated(@PathVariable("serialNumber") String serialNumber,
                                                       HttpServletRequest httpServletRequest) {
        String userId = httpServletRequest.getHeader(USER_ID);
        LOGGER.info("isDeviceAlreadyAssociated - START serialNumber: {}",
            (serialNumber == null) ? null : serialNumber.replaceAll(CARRIAGE_AND_NEWLINE_REGEX, ""));
        try {
            if (StringUtils.isNotEmpty(userId)
                && !userBelongingValidator.validateUserBelonging(
                generateQueryConditionMap(null, serialNumber, null, userId))) {
                return RestResponse.createSimpleErrorResponses(HttpStatus.UNAUTHORIZED,
                    MessageConstants.INVALID_USER_DEVICE_DETAILS);
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred in v1/devices/{}/associated/ . Exception:{}",
                (serialNumber == null) ? null : serialNumber.replaceAll(CARRIAGE_AND_NEWLINE_REGEX, ""), e);
            return RestResponse.createSimpleErrorResponses(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return RestResponse.ok(deviceAssociationService.isDeviceAlreadyAssociated(serialNumber));
    }

    /**
     * Handles the device state changed event.
     *
     * @param deviceState The updated device state.
     * @param httpServletRequest The HTTP servlet request.
     * @return A ResponseEntity containing the response for the device state changed event.
     */
    @PostMapping(value = "v1/devices/stateChanged/", produces = "application/json")
    @Hidden
    public ResponseEntity<SimpleResponseMessage> deviceStateChanged(@RequestBody DeviceState deviceState,
                                                HttpServletRequest httpServletRequest) {
        String deviceStateData =
                deviceState.toString().replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "");
        LOGGER.debug("## TRIGGERED deviceStateChanged - START deviceState: {}", deviceStateData);
        String userId = httpServletRequest.getHeader(USER_ID);
        if (isNotValidUserId(userId)) {
            return RestResponse.createSimpleErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR);
        }
        try {
            deviceAssociationService.deviceStateChanged(deviceState, userId);
        } catch (Exception e) {
            LOGGER.error("Exception occurred in v1/devices/stateChanged/ {}. Exception:{}",
                deviceState.toString().replaceAll(CARRIAGE_AND_NEWLINE_REGEX, ""), e);
            return RestResponse.createSimpleErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return RestResponse.ok(new SimpleResponseMessage(SUCCESS));
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
     * Restores the association for a user and a device.
     *
     * @param request  The device status request.
     * @param userId   The user ID.
     * @return         The response entity.
     */
    @PostMapping(value = "v1/user/associations/restore", produces = "application/json")
    @Operation(summary = "POST v1/user/associations/restore", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR,
            scopes = { "AssociateMyselfToVehicle,UserDeviceAssocn,SelfManage" })
    public ResponseEntity<SimpleResponseMessage> restoreAssociation(@Valid @RequestBody DeviceStatusRequest request,
                                                @RequestHeader(USER_ID) String userId) {
        String deviceStateRequestData =
                request.toString().replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "");
        LOGGER.info("restoreDeviceRequest: {}", deviceStateRequestData);
        if (isNotValidUserId(userId)) {
            return RestResponse.createSimpleErrorResponse(HttpStatus.BAD_REQUEST, USER_ID_MANDATORY);
        }
        request.setUserId(userId);
        try {
            int updateCount = deviceAssocFactoryService.restoreAssociation(request);
            return (updateCount > 0) ? RestResponse.ok(new SimpleResponseMessage(SUCCESS))
                : RestResponse.createSimpleErrorResponse(HttpStatus.BAD_REQUEST, "No valid association found");
        } catch (InvalidUserAssociation e) {
            String errorMessage = buildErrorMessage("Exception while restoring device", request, e.getMessage());
            LOGGER.error("{}", errorMessage);
            return RestResponse.createSimpleErrorResponse(HttpStatus.UNAUTHORIZED, INVALID_USER_DEVICE_DETAILS);
        } catch (NoSuchEntityException ex) {
            String errorMessage =
                buildErrorMessage("NoSuchEntityException while restoring device", request, ex.getMessage());
            LOGGER.error("{}", errorMessage);
            return RestResponse.createSimpleErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (Exception e) {
            LOGGER.error("Exception while restoring device ", e);
            return RestResponse.createSimpleErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR);
        }
    }

    /**
     * Terminates the association between a user and a device.
     *
     * @param request  The device status request.
     * @param userId   The user ID.
     * @return         The response entity.
     */
    @PostMapping(value = "v1/user/associations/terminate", produces = "application/json")
    @Operation(summary = "POST v1/user/associations/terminate", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR,
            scopes = { "AssociateMyselfToVehicle,UserDeviceAssocn,SelfManage,IgniteSystem" })
    public ResponseEntity<SimpleResponseMessage> terminateAssociation(@Valid @RequestBody DeviceStatusRequest request,
                                                  @RequestHeader(USER_ID) String userId) {
        String deviceStateRequestData =
                request.toString().replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "");
        LOGGER.info("Invoking terminateDevice for request: {}, userId: {}", deviceStateRequestData,
                (userId == null) ? null : userId.replaceAll(CARRIAGE_AND_NEWLINE_REGEX, ""));
        if (isNotValidUserId(userId)) {
            return RestResponse.createSimpleErrorResponse(HttpStatus.BAD_REQUEST, "User ID is mandatory");
        }
        request.setUserId(userId);
        try {
            String deleteVehicleProfileUrl = vehicleProfileService.fetchVehicleProfileUrl(request);
            int updateCount = deviceAssocFactoryService.terminateAssociation(request);
            if (updateCount > 0) {
                boolean isVpDeleted = vehicleProfileService.deleteVehicleProfile(deleteVehicleProfileUrl);
                if (!isVpDeleted) {
                    return RestResponse.ok(new SimpleResponseMessage(VP_DELETE_FAILED));
                }
                return RestResponse.ok(new SimpleResponseMessage(SUCCESS));
            } else {
                return RestResponse.createSimpleErrorResponse(HttpStatus.BAD_REQUEST, "No valid association found");
            }
        } catch (NoSuchEntityException ex) {
            String errorMessage =
                buildErrorMessage("NoSuchEntityException while terminating device", request, ex.getMessage());
            LOGGER.error("{}", errorMessage);
            return RestResponse.createSimpleErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (InvalidUserAssociation e) {
            String errorMessage =
                buildErrorMessage("InvalidUserAssociation Exception while terminating device", request, e.getMessage());
            LOGGER.error("{}", errorMessage);
            return RestResponse.createSimpleErrorResponse(HttpStatus.UNAUTHORIZED,
                "Association data does not exist for given input.");
        } catch (ObserverMessageProcessFailureException ex) {
            String errorMessage = buildErrorMessage("Exception while terminating device", request, ex.getMessage());
            LOGGER.error("{}", errorMessage);
            if (ex.getMessage().contains(CONNECTION_REFUSED)) {
                return RestResponse.createSimpleErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR);
            }
            return RestResponse.createSimpleErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR);
        } catch (Exception e) {
            LOGGER.error("Exception while terminating device", e);
            return RestResponse.createSimpleErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR);
        }
    }

    /**
     * Suspends a device association for a user.
     *
     * @param request  The device status request containing the necessary information.
     * @param userId   The user ID obtained from the request header.
     * @return         A ResponseEntity representing the HTTP response.
     */
    @PostMapping(value = "/v1/user/associations/suspend", produces = "application/json")
    @Operation(summary = "POST /v1/user/associations/suspend", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR,
            scopes = { "AssociateMyselfToVehicle,UserDeviceAssocn,SelfManage" })
    public ResponseEntity<SimpleResponseMessage> suspendDevice(@Valid @RequestBody DeviceStatusRequest request,
                                           @RequestHeader(USER_ID) String userId) {
        if (request == null) {
            return RestResponse.createSimpleErrorResponse(HttpStatus.BAD_REQUEST,
                MessageConstants.EITHER_IMEI_OR_DEVICE_ID_OR_SERIAL_NUMBER_ARE_MANDATORY);
        }
        String deviceStateRequestData =
                request.toString().replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "");
        LOGGER.info("suspend device request: {}", deviceStateRequestData);
        if (isNotValidUserId(userId)) {
            return RestResponse.createSimpleErrorResponse(HttpStatus.BAD_REQUEST, USER_ID_MANDATORY);
        }
        request.setUserId(userId);

        try {
            deviceAssocFactoryService.suspendDevice(request);
        } catch (NoSuchEntityException ex) {
            String errorMessage =
                buildErrorMessage("NoSuchEntityException while suspending device", request, ex.getMessage());
            LOGGER.error("{}", errorMessage);
            return RestResponse.createSimpleErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (InvalidUserAssociation e) {
            String errorMessage =
                buildErrorMessage("InvalidUserAssociation Exception while suspending device", request, e.getMessage());
            LOGGER.error("{}", errorMessage);
            return RestResponse.createSimpleErrorResponse(HttpStatus.UNAUTHORIZED, INVALID_USER_DEVICE_DETAILS);
        } catch (Exception e) {
            LOGGER.error("Exception while suspending device", e);
            return RestResponse.createSimpleErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR);
        }
        return RestResponse.ok(new SimpleResponseMessage(SUCCESS));
    }

    /**
     * Replaces a device for a user.
     *
     * @param replaceDeviceRequest The request object containing the data for replacing the device.
     * @param userId The ID of the user.
     * @return A ResponseEntity representing the response of the device replacement operation.
     */
    @PostMapping(value = "/v1/user/device/replace", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "POST /v1/user/device/replace", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR,
            scopes = { "AssociateMyselfToVehicle,UserDeviceAssocn,SelfManage,IgniteSystem" })
    public ResponseEntity<SimpleResponseMessage> replaceDevice(
                                           @Valid @RequestBody ReplaceFactoryDataRequest replaceDeviceRequest,
                                           @RequestHeader(USER_ID) String userId) {
        String replaceDeviceRequestData =
                replaceDeviceRequest.toString().replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "");
        LOGGER.info("Device replace request raised for request: {}", replaceDeviceRequestData);

        if (isNotValidUserId(userId)) {
            return RestResponse.createSimpleErrorResponse(HttpStatus.BAD_REQUEST, USER_ID_MANDATORY);
        }
        try {
            if (replaceIviDeviceEnabled) {
                LOGGER.info("Replace IVI Device Enabled");
                deviceAssocFactoryService.replaceIviDevice(replaceDeviceRequest, userId);
            } else {
                deviceAssocFactoryService.replaceDevice(replaceDeviceRequest, userId);
            }
        } catch (DeviceReplaceException ex) {
            Map<Object, Object> details = new LinkedHashMap<>();
            details.put(ErrorUtils.ERROR_CODE_KEY, "500");
            LOGGER.error("{}",
                ErrorUtils.buildError("## Error has occurred while replacing device", ex,
                    details));
            return RestResponse.createSimpleErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (Exception e) {
            Map<Object, Object> details = new LinkedHashMap<>();
            details.put(ErrorUtils.ERROR_CODE_KEY, "500");
            LOGGER.error("{}",
                ErrorUtils.buildError("## Error has occurred while replacing device", e,
                    details));
            return RestResponse.createSimpleErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR);
        }
        return RestResponse.ok(new SimpleResponseMessage(SUCCESS));
    }

    /**
     * Changes the state of a device for a user.
     *
     * @param stateChangeRequest The request object containing the state change details.
     * @param userId The ID of the user.
     * @return A ResponseEntity representing the response of the state change operation.
     */
    @PutMapping(value = "/v1/user/device/state", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "PUT /v1/user/device/state", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR,
            scopes = { "AssociateMyselfToVehicle,UserDeviceAssocn,SelfManage" })
    public ResponseEntity<SimpleResponseMessage> changeState(@Valid @RequestBody StateChangeRequest stateChangeRequest,
                                         @RequestHeader(USER_ID) String userId) {
        String stateChangeRequestData =
                stateChangeRequest.toString().replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "");
        LOGGER.info("## Invoking changeState for request: {}, userId: {}", stateChangeRequestData,
                (userId == null) ? null : userId.replaceAll(CARRIAGE_AND_NEWLINE_REGEX, ""));
        try {
            if (isNotValidUserId(userId)) {
                return RestResponse.createSimpleErrorResponse(HttpStatus.BAD_REQUEST, USER_ID_MANDATORY);
            }
            stateChangeRequest.setUserId(userId);
            deviceAssocFactoryService.stateChange(stateChangeRequest);
        } catch (NoSuchEntityException ex) {
            if (stateChangeRequest.getImei() != null) {
                LOGGER.error("No such entity exception while changing device state, device with imei: {}, error - {}",
                    stateChangeRequest.getImei(),
                    ex.getMessage());
            } else {
                LOGGER.error("No such entity exception while changing device state, error - {}", ex.getMessage());
            }
            return RestResponse.createSimpleErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (InvalidUserAssociation ex) {
            if (stateChangeRequest.getImei() != null) {
                LOGGER.error(
                    "Invalid User association exception while changing device state, device with imei: {}, error - {}",
                    stateChangeRequest.getImei(), ex.getMessage());
            } else {
                LOGGER.error("Invalid User association exception while changing device state, error - {}",
                    ex.getMessage());
            }
            return RestResponse.createSimpleErrorResponse(HttpStatus.UNAUTHORIZED, INVALID_USER_DEVICE_DETAILS);
        } catch (Exception ex) {
            if (stateChangeRequest.getImei() != null) {
                LOGGER.error("Exception while changing device state, device with imei: {}, error - {}",
                    stateChangeRequest.getImei(),
                    ex);
            } else {
                LOGGER.error("Exception while changing device state, error: ", ex);
            }
            return RestResponse.createSimpleErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Not successful :" + ex.getMessage());
        }
        return RestResponse.ok(new SimpleResponseMessage(SUCCESS));

    }

    /**
     * Retrieves the association history details for a device with the specified IMEI.
     *
     * @param imei   The IMEI of the device.
     * @param page   The page number for pagination (optional).
     * @param size   The number of records per page (optional).
     * @param sortby The field to sort the records by (optional).
     * @param orderby The order in which to sort the records (optional).
     * @return A ResponseEntity containing the association history details.
     */
    @GetMapping(value = "v1/devices/{imei}/association/history", produces = "application/json")
    @Operation(summary = "GET v1/devices/{imei}/association/history", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @SecurityRequirement(name = Security.Fields.JWT_AUTH_VALIDATOR, scopes = { "IgniteSystem,userOrThirdPartyToken" })
    public ResponseEntity<Object> getAssociationHistory(@PathVariable("imei") String imei,
                               @RequestParam(value = "page", required = false, defaultValue = "1") String page,
                               @RequestParam(value = "size", required = false, defaultValue = "20") String size,
                               @RequestParam(value = "sortby", required = false, defaultValue = "") String sortby,
                               @RequestParam(value = "orderby", required = false, defaultValue = "") String orderby) {
        String requestImei = imei.replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "");
        String requestPage = page.replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "");
        String requestSize = size.replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "");
        String requestSortBy = sortby.replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "");
        String requestOrderBy = orderby.replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "");
        LOGGER.info("Invoking getAssociationHistory for imei: {}, page: {}, size: {}, sortby: {},"
                        + " orderby: {}", requestImei, requestPage, requestSize, requestSortBy,
                requestOrderBy);
        int pageVal = 1;
        int sizeVal = SIZE;
        HcpServicesFailureResponse hcpServicesFailureResponse;
        try {
            DeviceAssociationHistoryValidator.validateImeiRequestData(imei);
            DeviceAssociationHistoryValidator.validatePageRequestData(page);
            DeviceAssociationHistoryValidator.validateSizeRequestData(size);
            DeviceAssociationHistoryValidator.validateSortAndOrderRequestData(sortby, orderby);

            pageVal = Integer.parseInt(page);
            sizeVal = Integer.parseInt(size);

            LOGGER.debug(
                "v1/devices/{imei}/association/history got called with params imei:{},size:{},page:{},sortby:{},"
                    + "orderby:{}", requestImei, sizeVal, pageVal, requestSortBy, requestOrderBy);

            DeviceInfo deviceInfo = new DeviceInfo();
            deviceInfo.setImei(imei);

            List<DeviceAssociationHistory> associationHistoryList =
                deviceAssocFactoryService.getAssociationHistory(deviceInfo.getImei(), orderby, sortby,
                    pageVal, sizeVal);
            DeviceAssociationHistorySuccessResponse<DeviceAssociationHistory> deviceAssociationHistory =
                new DeviceAssociationHistorySuccessResponse<>();
            deviceAssociationHistory.setData(associationHistoryList);
            RecordStats<Object> recordStats = new RecordStats<>();
            recordStats.setPage(pageVal);
            recordStats.setSize(sizeVal);
            int total = deviceAssocFactoryService.getAssociationHistoryTotalCount(deviceInfo.getImei());
            recordStats.setTotal(total);
            deviceAssociationHistory.setRecordStats(recordStats);
            deviceAssociationHistory.setRequestId("req1");
            deviceAssociationHistory.setCode(RESPONSE_CODE);
            deviceAssociationHistory.setReason("Device based user association history details retrieval");
            deviceAssociationHistory.setHttpStatusCode(STATUS_CODE_200);
            deviceAssociationHistory.setMessage("Device based user association history details retrieved successfully");
            return RestResponse.ok(deviceAssociationHistory);
        } catch (NoSuchEntityException ex) {
            LOGGER.error("Error has occurred as association history was not found in database:{} ", ex.getMessage());
            hcpServicesFailureResponse = setErrorCodeAndMessage(STATUS_CODE_404, ex.getMessage(), "req1",
                    RESPONSE_CODE, USER_ASSOC_RETRIEVAL_FAILED);
            return RestResponse.createFailureResponse(hcpServicesFailureResponse);
        } catch (IllegalArgumentException ex) {
            LOGGER.error("Error has occurred due to validation failure :{} ", ex.getMessage());
            hcpServicesFailureResponse = setErrorCodeAndMessage(STATUS_CODE_400, ex.getMessage(), "req1",
                    RESPONSE_CODE, USER_ASSOC_RETRIEVAL_FAILED);
            return RestResponse.createFailureResponse(hcpServicesFailureResponse);
        } catch (Exception ex) {
            LOGGER.error("Error has occurred while retrieving association history details  Error: ", ex);
            hcpServicesFailureResponse = setErrorCodeAndMessage(STATUS_CODE_500, ex.getMessage(), "req1",
                    RESPONSE_CODE, USER_ASSOC_RETRIEVAL_FAILED);
            return RestResponse.createFailureResponse(hcpServicesFailureResponse);
        }
    }

    /**
     * Replaces the VIN (Vehicle Identification Number) for a device.
     *
     * @param deviceId The ID of the device.
     * @param vin The new VIN to replace the existing VIN.
     * @return A ResponseEntity representing the result of the operation.
     */
    @PutMapping(value = "v1/device/{deviceId}/vin/{vin}/replace", produces = "application/json")
    @ResponseBody
    @Hidden
    public ResponseEntity<Object> replaceVinForDevice(@PathVariable("deviceId") String deviceId,
                                                 @PathVariable("vin") String vin) {

        try {
            LOGGER.info("Start - Replace vin for Device: {}, VIN: {}",
                (deviceId == null) ? null : deviceId.replaceAll(CARRIAGE_AND_NEWLINE_REGEX, ""),
                (vin == null) ? null : vin.replaceAll(CARRIAGE_AND_NEWLINE_REGEX, ""));
            /* Extract association_id for device */
            // Part-1 Association regular flow happened or not
            boolean associationExists = deviceAssociationService.associationByDeviceExists(deviceId);
            if (!associationExists) {
                throw new NoSuchEntityException("No association found or invalid association state");
            }
            long assocId = deviceAssociationService.getAssociationIdByDeviceId(deviceId);
            LOGGER.info("Association ID for Device: {} is : {}",
                (deviceId == null) ? null : deviceId.replaceAll(CARRIAGE_AND_NEWLINE_REGEX, ""), assocId);
            /* Check if any vin is associated for that association_id */
            boolean vinAssociated = deviceAssociationService.getVinAssociation(assocId);
            if (!vinAssociated) {
                throw new NoSuchEntityException("vin is not associated for this device");
            }

            /* Check if the vin is already in use */
            boolean vinExists = deviceAssociationService.vinAlreadyAssociated(vin);
            if (vinExists) {
                throw new NoSuchEntityException("This vin is already associated to some other device");
            }
            deviceAssociationService.replaceVin(assocId, vin);
            LOGGER.info("Successfully replaced vin for Device: {} with vin: {}",
                (deviceId == null) ? null : deviceId.replaceAll(CARRIAGE_AND_NEWLINE_REGEX, ""),
                (vin == null) ? null : vin.replaceAll(CARRIAGE_AND_NEWLINE_REGEX, ""));
        } catch (NoSuchEntityException e) {
            return RestResponse.createSimpleErrorResponses(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception ex) {
            LOGGER.error("Error has occurred while replacing vin for device.", ex);
            HcpServicesFailureResponse hcpServicesFailureResponse = setErrorCodeAndMessage(STATUS_CODE_500,
                    ex.getMessage(), "req1", RESPONSE_CODE, "Replace Vin request failed");
            return RestResponse.createFailureResponse(hcpServicesFailureResponse);
        }
        return RestResponse.ok(SUCCESS);

    }

    /**
     * Builds an error message with the provided user error message, device status request, and exception message.
     *
     * @param userErrorMessage The user error message to include in the error message.
     * @param request The device status request object containing the IMEI, device ID, and serial number.
     * @param exceptionMsg The exception message to include in the error message.
     * @return The built error message as a string.
     */
    private String buildErrorMessage(String userErrorMessage, DeviceStatusRequest request, String exceptionMsg) {
        StringBuilder errorLogSb = new StringBuilder(CAPACITY);
        errorLogSb.append(userErrorMessage);
        if (request.getImei() != null) {
            errorLogSb.append(", imei: ").append(request.getImei());
        }
        if (request.getDeviceId() != null) {
            errorLogSb.append(", deviceId: ").append(request.getDeviceId());
        }
        if (request.getSerialNumber() != null) {
            errorLogSb.append(", serialNumber: ").append(request.getSerialNumber());
        }
        errorLogSb.append(", error: ").append(exceptionMsg);
        return errorLogSb.toString();
    }

    /**
     * Generates a query condition map based on the provided parameters.
     *
     * @param imei          the IMEI number of the device
     * @param serialNumber  the serial number of the device
     * @param deviceId     the ID of the device
     * @param userId       the ID of the user
     * @return a map containing the query conditions
     */
    private Map<String, Object> generateQueryConditionMap(String imei, String serialNumber, String deviceId,
                                                          String userId) {
        Map<String, Object> queryConditionMap = new LinkedHashMap<>();
        if (StringUtils.isNotEmpty(imei)) {
            queryConditionMap.put(DeviceAttributeEnums.IMEI.getString(), imei);
        }
        if (StringUtils.isNotEmpty(serialNumber)) {
            queryConditionMap.put(DeviceAttributeEnums.SERIAL_NUMBER.getString(), serialNumber);
        }
        if (StringUtils.isNotEmpty(deviceId)) {
            queryConditionMap.put(DeviceAttributeEnums.DEVICE_ID.getString(), deviceId);
        }
        if (StringUtils.isNotEmpty(userId)) {
            queryConditionMap.put(DeviceAttributeEnums.USER_ID.getString(), userId);
        }

        return queryConditionMap;
    }

    /**
     * Retrieves the value of a request parameter from the given HttpServletRequest object.
     * If the parameter is blank or null, the method returns the provided default value.
     *
     * @param httpServletRequest The HttpServletRequest object from which to retrieve the parameter value.
     * @param param              The name of the parameter to retrieve.
     * @param requestParam       The default value to return if the parameter is blank or null.
     * @return The value of the request parameter, or the default value if the parameter is blank or null.
     */
    private String getRequestParameterValues(HttpServletRequest httpServletRequest, String param,
                                             String requestParam) {
        String parameter;
        if (StringUtils.isNotBlank(httpServletRequest.getParameter(param))) {
            parameter = httpServletRequest.getParameter(param);
        } else {
            parameter = requestParam;
        }
        return parameter;
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
}
