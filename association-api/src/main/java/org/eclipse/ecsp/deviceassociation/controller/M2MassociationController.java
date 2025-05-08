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
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.ecsp.common.ExtendedApiResponse;
import org.eclipse.ecsp.deviceassociation.dto.AssociationUpdateDto;
import org.eclipse.ecsp.deviceassociation.dto.AssociationUpdateRequest;
import org.eclipse.ecsp.deviceassociation.lib.service.DeviceAssociationTypeService;
import org.eclipse.ecsp.deviceassociation.lib.service.DeviceAssociationWithFactDataServiceV2;
import org.eclipse.ecsp.exception.shared.ApiPreConditionFailedException;
import org.eclipse.ecsp.exception.shared.ApiResourceNotFoundException;
import org.eclipse.ecsp.exception.shared.ApiValidationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.ASSOCIATION_UPDATED_SUCCESSFULLY;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.ASSOCIATION_UPDATE_BASIC_DATA_MANDATORY;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.ASSOCIATION_UPDATE_FAILED;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.ASSOC_TYPE_VALIDATION_FAILURE;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.INVALID_USER_ID_ERR_MSG;
import static org.eclipse.ecsp.deviceassociation.webutil.WebUtils.getResponseEntity;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.PRECONDITION_FAILED;

/**
 * This class is a controller for managing M2M associations.
 * It provides endpoints for updating associations and getting association type counts.
 */
@SuppressWarnings("JavaDoc")
@RestController
@Slf4j
public class M2MassociationController {
    private static final String USER_ID = "user-id";
    private static final String CORRELATION_ID = "correlationId";
    private static final int RESPONSE_ENTITY_BODY = -1;
    private static final String UPDATING_ASSOCIATION_BY_USER_ERROR =
            "## Error has occurred while updating association by user: {}, ErrMsg: {}";
    private final DeviceAssociationWithFactDataServiceV2 deviceAssocFactoryService;

    private final DeviceAssociationTypeService deviceAssociationTypeService;

    /**
     * Constructs a new instance of the M2MassociationController class.
     *
     * @param deviceAssocFactoryService The service for device association with fact data.
     * @param deviceAssociationTypeService The service for device association types.
     */
    @Autowired
    public M2MassociationController(
        DeviceAssociationWithFactDataServiceV2 deviceAssocFactoryService,
        DeviceAssociationTypeService deviceAssociationTypeService) {
        this.deviceAssocFactoryService = deviceAssocFactoryService;
        this.deviceAssociationTypeService = deviceAssociationTypeService;
    }

    /**
     * Updates an association based on the provided association ID and request body.
     *
     * @param requestId               The correlation ID for the request.
     * @param userId                  The ID of the user making the request.
     * @param associationId           The ID of the association to be updated.
     * @param associationUpdateRequest The request body containing the updated association data.
     * @return A ResponseEntity containing the response data.
     */
    @PatchMapping(value = "/v1/self/associations/{associationId}", produces = "application/json",
        consumes = "application/json")
    @Operation(summary = "PATCH /v1/self/associations/{associationId}", responses = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Success", responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))})
    @SecurityRequirement(name = "JwtAuthValidator",
            scopes = { "AssociateMyselfToVehicle,UserDeviceAssocn,SelfManage" })
    public ResponseEntity<ExtendedApiResponse<Object>> updateAssociation(
        @RequestHeader(value = CORRELATION_ID) String requestId,
        @RequestHeader(value = USER_ID) String userId,
        @PathVariable(value = "associationId") long associationId,
        @Valid @RequestBody AssociationUpdateRequest associationUpdateRequest) {

        log.info("## updateAssociation Controller - START requestId: {} requestObject {}", requestId,
            associationUpdateRequest.toString());
        String associationType = associationUpdateRequest.getAssocType();
        ExtendedApiResponse<Object> extendedApiResponse;
        // Validate Input
        if (StringUtils.isEmpty(userId)) {
            return getResponseEntity(
                new ExtendedApiResponse.Builder<>(requestId, BAD_REQUEST, INVALID_USER_ID_ERR_MSG.getCode(),
                    INVALID_USER_ID_ERR_MSG.getMessage(), INVALID_USER_ID_ERR_MSG.getGeneralMessage()).build());
        }
        if (ObjectUtils.isEmpty(associationUpdateRequest) || (StringUtils.isEmpty(associationType)
            && associationUpdateRequest.getStartTime() == 0L && associationUpdateRequest.getEndTime() == 0L)) {
            return getResponseEntity(new ExtendedApiResponse.Builder<>(requestId, BAD_REQUEST,
                ASSOCIATION_UPDATE_BASIC_DATA_MANDATORY.getCode(),
                ASSOCIATION_UPDATE_BASIC_DATA_MANDATORY.getMessage(),
                ASSOCIATION_UPDATE_BASIC_DATA_MANDATORY.getGeneralMessage()).build());
        }
        if (StringUtils.isEmpty(associationType) && !deviceAssociationTypeService.isAssocTypeExist(associationType)) {
            return getResponseEntity(
                new ExtendedApiResponse.Builder<>(requestId, BAD_REQUEST, ASSOC_TYPE_VALIDATION_FAILURE.getCode(),
                    ASSOC_TYPE_VALIDATION_FAILURE.getMessage(),
                    ASSOC_TYPE_VALIDATION_FAILURE.getGeneralMessage()).build());
        }

        AssociationUpdateDto associationUpdateDto = AssociationUpdateDto.builder()
            .associationUpdateRequest(associationUpdateRequest)
            .assocId(associationId)
            .requestId(requestId)
            .userId(userId)
            .build();

        try {
            //update Association
            deviceAssocFactoryService.updateAssociation(associationUpdateDto);
            extendedApiResponse =
                new ExtendedApiResponse.Builder<>(requestId, OK, ASSOCIATION_UPDATED_SUCCESSFULLY.getCode(),
                    ASSOCIATION_UPDATED_SUCCESSFULLY.getMessage(),
                    ASSOCIATION_UPDATED_SUCCESSFULLY.getGeneralMessage()).build();
        } catch (ApiPreConditionFailedException e) {
            log.error(UPDATING_ASSOCIATION_BY_USER_ERROR, userId, e.generalMessage());
            extendedApiResponse =
                new ExtendedApiResponse.Builder<>(requestId, PRECONDITION_FAILED, e.getCode(), e.getMessage(),
                    e.generalMessage()).build();
        } catch (ApiResourceNotFoundException e) {
            log.error(UPDATING_ASSOCIATION_BY_USER_ERROR, userId, e.getErrorMessage());
            extendedApiResponse = new ExtendedApiResponse.Builder<>(requestId, NOT_FOUND, e.getCode(), e.getMessage(),
                e.getErrorMessage()).build();
        } catch (ApiValidationFailedException e) {
            log.error(UPDATING_ASSOCIATION_BY_USER_ERROR, userId, e.getErrorMessage());
            extendedApiResponse = new ExtendedApiResponse.Builder<>(requestId, BAD_REQUEST, e.getCode(), e.getMessage(),
                e.getErrorMessage()).build();
        } catch (Exception e) {
            log.error(UPDATING_ASSOCIATION_BY_USER_ERROR, userId, e.getMessage());
            extendedApiResponse =
                new ExtendedApiResponse.Builder<>(requestId, INTERNAL_SERVER_ERROR, ASSOCIATION_UPDATE_FAILED.getCode(),
                    ASSOCIATION_UPDATE_FAILED.getMessage(), ASSOCIATION_UPDATE_FAILED.getGeneralMessage()).build();
        }

        return getResponseEntity(extendedApiResponse);
    }

    /**
     * Retrieves the count of association types for a given association type.
     *
     * @param requestId The correlation ID for the request.
     * @param associationType The association type to retrieve the count for.
     * @return A ResponseEntity containing the count of association types.
     */
    @GetMapping(value = "/v1/associations/types/{associationType}", produces = "application/json")
    @Hidden
    public ResponseEntity<Integer> getAssociationTypeCount(
        @RequestHeader(value = CORRELATION_ID) String requestId,
        @PathVariable(value = "associationType") String associationType) {
        log.info("## getAssociationTypeCount Controller - START requestId: {} , associationType: {}",
            (requestId == null) ? null : requestId.replaceAll("[\r\n]", ""),
            (associationType == null) ? null : associationType.replaceAll("[\r\n]", ""));
        Integer count;
        try {
            count = deviceAssocFactoryService.getAssociationTypeUsageCount(associationType, requestId);
            log.info("count {}", count);
        } catch (Exception e) {
            log.error("## Error has occurred while fetching association-type {} usage count, ErrMsg: {}",
                associationType, e.getStackTrace());
            return new ResponseEntity<>(RESPONSE_ENTITY_BODY, INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(count, OK);
    }

}
