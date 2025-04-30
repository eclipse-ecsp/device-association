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

import org.eclipse.ecsp.common.ExtendedApiResponse;
import org.eclipse.ecsp.deviceassociation.dto.AssociationUpdateRequest;
import org.eclipse.ecsp.deviceassociation.lib.service.DeviceAssociationTypeService;
import org.eclipse.ecsp.deviceassociation.lib.service.DeviceAssociationWithFactDataServiceV2;
import org.eclipse.ecsp.exception.shared.ApiPreConditionFailedException;
import org.eclipse.ecsp.exception.shared.ApiResourceNotFoundException;
import org.eclipse.ecsp.exception.shared.ApiValidationFailedException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.ResponseEntity;

import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.ASSOCIATION_UPDATED_SUCCESSFULLY;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.ASSOCIATION_UPDATE_BASIC_DATA_MANDATORY;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.ASSOCIATION_UPDATE_FAILED;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.ASSOC_TYPE_VALIDATION_FAILURE;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.INVALID_USER_ID_ERR_MSG;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.PRECONDITION_FAILED;

/**
 * M2MassociationControllerTest class.
 */
public class M2MassociationControllerTest {
    private static final int STATUS_CODE_400 = 400;
    private static final int STATUS_CODE_200 = 200;
    private static final int STATUS_CODE_500 = 500;
    private static final int ACTUAL_SIZE = 2;
    private static final long END_TIME = 2L;
    @InjectMocks
    M2MassociationController m2mAssociationController;

    @Mock
    DeviceAssociationWithFactDataServiceV2 deviceAssocFactoryService;

    @Mock
    DeviceAssociationTypeService deviceAssociationTypeService;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void updateAssociationTest_emptyUserId() {
        String requestId = "123";
        long associationId = 1L;
        AssociationUpdateRequest associationUpdateRequest = new AssociationUpdateRequest();
        associationUpdateRequest.setAssocType("driver");
        associationUpdateRequest.setStartTime(1L);
        associationUpdateRequest.setEndTime(END_TIME);
        ResponseEntity<?> responseEntity =
            m2mAssociationController.updateAssociation(requestId, null, associationId, associationUpdateRequest);
        ExtendedApiResponse<Object> extendedApiResponse = (ExtendedApiResponse) responseEntity.getBody();
        assertNotNull(extendedApiResponse);
        assertEquals(STATUS_CODE_400, responseEntity.getStatusCodeValue());
        assertEquals(INVALID_USER_ID_ERR_MSG.getCode(), extendedApiResponse.getCode());
    }

    @Test
    public void updateAssociationTest_emptyRequestFields() {
        String requestId = "123";
        String userId = "User123";
        long associationId = 1L;
        AssociationUpdateRequest associationUpdateRequest = new AssociationUpdateRequest();
        ResponseEntity<?> responseEntity =
            m2mAssociationController.updateAssociation(requestId, userId, associationId, associationUpdateRequest);
        ExtendedApiResponse<Object> extendedApiResponse = (ExtendedApiResponse) responseEntity.getBody();
        assertNotNull(extendedApiResponse);
        assertEquals(STATUS_CODE_400, responseEntity.getStatusCodeValue());
        assertEquals(ASSOCIATION_UPDATE_BASIC_DATA_MANDATORY.getCode(), extendedApiResponse.getCode());
    }

    @Test
    public void updateAssociationTest_emptyAssociationType() {
        AssociationUpdateRequest associationUpdateRequest = new AssociationUpdateRequest();
        associationUpdateRequest.setAssocType("");
        associationUpdateRequest.setStartTime(1L);
        associationUpdateRequest.setEndTime(1L);
        Mockito.doReturn(false).when(deviceAssociationTypeService)
            .isAssocTypeExist(associationUpdateRequest.getAssocType());
        String requestId = "123";
        String userId = "User123";
        long associationId = 1L;
        ResponseEntity<?> responseEntity =
            m2mAssociationController.updateAssociation(requestId, userId, associationId, associationUpdateRequest);
        ExtendedApiResponse<Object> extendedApiResponse = (ExtendedApiResponse) responseEntity.getBody();
        assertNotNull(extendedApiResponse);
        assertEquals(STATUS_CODE_400, responseEntity.getStatusCodeValue());
        assertEquals(ASSOC_TYPE_VALIDATION_FAILURE.getCode(), extendedApiResponse.getCode());
    }

    @Test
    public void updateAssociationTest_startTime0() {
        AssociationUpdateRequest associationUpdateRequest = new AssociationUpdateRequest();
        associationUpdateRequest.setAssocType("driver");
        associationUpdateRequest.setStartTime(0L);
        associationUpdateRequest.setEndTime(END_TIME);
        String requestId = "Req123";
        String userId = "User123";
        long associationId = 1L;
        ResponseEntity<?> responseEntity = m2mAssociationController.updateAssociation(requestId, userId,
                associationId, associationUpdateRequest);
        ExtendedApiResponse<Object> extendedApiResponse = (ExtendedApiResponse) responseEntity.getBody();
        assertNotNull(extendedApiResponse);
    }

    @Test
    public void updateAssociationTest_endTime0() {
        AssociationUpdateRequest associationUpdateRequest = new AssociationUpdateRequest();
        associationUpdateRequest.setAssocType("driver");
        associationUpdateRequest.setStartTime(1L);
        associationUpdateRequest.setEndTime(0L);
        String requestId = "Req123";
        String userId = "User123";
        long associationId = 1L;
        ResponseEntity<?> responseEntity = m2mAssociationController.updateAssociation(requestId, userId,
                associationId, associationUpdateRequest);
        ExtendedApiResponse<Object> extendedApiResponse = (ExtendedApiResponse) responseEntity.getBody();
        assertNotNull(extendedApiResponse);
    }

    @Test
    public void updateAssociationTest_ApiPreConditionFailedException() {
        AssociationUpdateRequest associationUpdateRequest = new AssociationUpdateRequest();
        associationUpdateRequest.setAssocType("driver");
        associationUpdateRequest.setStartTime(1L);
        associationUpdateRequest.setEndTime(END_TIME);
        Mockito.doThrow(new ApiPreConditionFailedException("Error has occurred")).when(deviceAssocFactoryService)
            .updateAssociation(Mockito.any());
        String requestId = "123";
        String userId = "User123";
        long associationId = 1L;
        ResponseEntity<?> responseEntity =
            m2mAssociationController.updateAssociation(requestId, userId, associationId, associationUpdateRequest);
        ExtendedApiResponse<Object> extendedApiResponse = (ExtendedApiResponse) responseEntity.getBody();
        assertNotNull(extendedApiResponse);
        assertEquals(PRECONDITION_FAILED.value(), responseEntity.getStatusCodeValue());
    }

    @Test
    public void updateAssociationTest_ApiResourceNotFoundException() {
        AssociationUpdateRequest associationUpdateRequest = new AssociationUpdateRequest();
        associationUpdateRequest.setAssocType("driver");
        associationUpdateRequest.setStartTime(1L);
        associationUpdateRequest.setEndTime(END_TIME);
        Mockito.doThrow(new ApiResourceNotFoundException("Error has occurred")).when(deviceAssocFactoryService)
            .updateAssociation(Mockito.any());
        String requestId = "123";
        String userId = "User123";
        long associationId = 1L;
        ResponseEntity<?> responseEntity =
            m2mAssociationController.updateAssociation(requestId, userId, associationId, associationUpdateRequest);
        ExtendedApiResponse<Object> extendedApiResponse = (ExtendedApiResponse) responseEntity.getBody();
        assertNotNull(extendedApiResponse);
        assertEquals(NOT_FOUND.value(), responseEntity.getStatusCodeValue());
    }

    @Test
    public void updateAssociationTest_ApiValidationFailedException() {
        AssociationUpdateRequest associationUpdateRequest = new AssociationUpdateRequest();
        associationUpdateRequest.setAssocType("driver");
        associationUpdateRequest.setStartTime(1L);
        associationUpdateRequest.setEndTime(END_TIME);
        Mockito.doThrow(new ApiValidationFailedException("Error has occurred")).when(deviceAssocFactoryService)
            .updateAssociation(Mockito.any());
        String requestId = "123";
        String userId = "User123";
        long associationId = 1L;
        ResponseEntity<?> responseEntity =
            m2mAssociationController.updateAssociation(requestId, userId, associationId, associationUpdateRequest);
        ExtendedApiResponse<Object> extendedApiResponse = (ExtendedApiResponse) responseEntity.getBody();
        assertNotNull(extendedApiResponse);
        assertEquals(BAD_REQUEST.value(), responseEntity.getStatusCodeValue());
    }

    @Test
    public void updateAssociationTest_Exception() {
        AssociationUpdateRequest associationUpdateRequest = new AssociationUpdateRequest();
        associationUpdateRequest.setAssocType("driver");
        associationUpdateRequest.setStartTime(1L);
        associationUpdateRequest.setEndTime(END_TIME);
        Mockito.doThrow(new EmptyResultDataAccessException(1)).when(deviceAssocFactoryService)
            .updateAssociation(Mockito.any());
        String requestId = "123";
        String userId = "User123";
        long associationId = 1L;
        ResponseEntity<?> responseEntity =
            m2mAssociationController.updateAssociation(requestId, userId, associationId, associationUpdateRequest);
        ExtendedApiResponse<Object> extendedApiResponse = (ExtendedApiResponse) responseEntity.getBody();
        assertNotNull(extendedApiResponse);
        assertEquals(STATUS_CODE_500, responseEntity.getStatusCodeValue());
        assertEquals(ASSOCIATION_UPDATE_FAILED.getCode(), extendedApiResponse.getCode());
    }

    @Test
    public void updateAssociationTest() {
        String requestId = "123";
        String userId = "User123";
        long associationId = 1L;
        AssociationUpdateRequest associationUpdateRequest = new AssociationUpdateRequest();
        associationUpdateRequest.setAssocType("driver");
        associationUpdateRequest.setStartTime(1L);
        associationUpdateRequest.setEndTime(END_TIME);
        ResponseEntity<?> responseEntity =
            m2mAssociationController.updateAssociation(requestId, userId, associationId, associationUpdateRequest);
        ExtendedApiResponse<Object> extendedApiResponse = (ExtendedApiResponse) responseEntity.getBody();
        assertNotNull(extendedApiResponse);
        assertEquals(STATUS_CODE_200, responseEntity.getStatusCodeValue());
        assertEquals(ASSOCIATION_UPDATED_SUCCESSFULLY.getCode(), extendedApiResponse.getCode());
    }

    @Test
    public void getAssociationTypeCountTest() {
        String requestId = "123";
        String associationType = "driver";
        Mockito.doReturn(1).when(deviceAssocFactoryService).getAssociationTypeUsageCount(associationType, requestId);
        ResponseEntity<?> responseEntity = m2mAssociationController.getAssociationTypeCount(requestId, associationType);
        assertEquals(STATUS_CODE_200, responseEntity.getStatusCodeValue());
    }

    @Test
    public void getAssociationTypeCountTest_NullRequestId() {
        String associationType = "driver";
        ResponseEntity<?> responseEntity = m2mAssociationController.getAssociationTypeCount(null, associationType);
        assertEquals(OK.value(), responseEntity.getStatusCodeValue());
    }

    @Test
    public void getAssociationTypeCountTest_NullAssociationType() {
        String requestId = "123";
        ResponseEntity<?> responseEntity = m2mAssociationController.getAssociationTypeCount(requestId, null);
        assertEquals(OK.value(), responseEntity.getStatusCodeValue());
    }

    @Test
    public void getAssociationTypeCountTest_NullInputs() {
        ResponseEntity<?> responseEntity = m2mAssociationController.getAssociationTypeCount(null, null);
        assertEquals(OK.value(), responseEntity.getStatusCodeValue());
    }

    @Test
    public void getAssociationTypeCountTest_Exception() {
        String requestId = "123";
        String associationType = "driver";
        Mockito.doThrow(new IncorrectResultSizeDataAccessException(1, ACTUAL_SIZE)).when(deviceAssocFactoryService)
            .getAssociationTypeUsageCount(associationType, requestId);
        ResponseEntity<?> responseEntity = m2mAssociationController.getAssociationTypeCount(requestId, associationType);
        assertEquals(INTERNAL_SERVER_ERROR.value(), responseEntity.getStatusCodeValue());
    }
}