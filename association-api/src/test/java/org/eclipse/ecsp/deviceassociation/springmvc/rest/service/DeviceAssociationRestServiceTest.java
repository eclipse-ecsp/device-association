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

import jakarta.servlet.http.HttpServletRequest;
import org.eclipse.ecsp.deviceassociation.lib.exception.DeviceReplaceException;
import org.eclipse.ecsp.deviceassociation.lib.exception.InvalidPinException;
import org.eclipse.ecsp.deviceassociation.lib.exception.InvalidUserAssociation;
import org.eclipse.ecsp.deviceassociation.lib.exception.NoSuchEntityException;
import org.eclipse.ecsp.deviceassociation.lib.exception.ObserverMessageProcessFailureException;
import org.eclipse.ecsp.deviceassociation.lib.model.AssociationStatus;
import org.eclipse.ecsp.deviceassociation.lib.model.DeviceAssociation;
import org.eclipse.ecsp.deviceassociation.lib.model.DeviceAssociationHistory;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociateDeviceRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociateDeviceResponse;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociationDetailsResponse;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.CurrentDeviceDataPojo;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceAssosiationDetails;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceDetail;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceState;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceStatusRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.ReplaceDeviceDataPojo;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.ReplaceFactoryDataRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.StateChangeRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.User;
import org.eclipse.ecsp.deviceassociation.lib.service.DeviceAssociationService;
import org.eclipse.ecsp.deviceassociation.lib.service.DeviceAssociationWithFactDataService;
import org.eclipse.ecsp.deviceassociation.lib.service.DeviceAssosiationDetailsService;
import org.eclipse.ecsp.deviceassociation.lib.service.UserBelongingValidator;
import org.eclipse.ecsp.deviceassociation.lib.service.VehicleProfileService;
import org.eclipse.ecsp.services.shared.rest.support.SimpleResponseMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for DeviceAssociationRestService.
 */
public class DeviceAssociationRestServiceTest {

    private static final String USER_ID = "user-id";
    private static final int STATUS_CODE_400 = 400;
    private static final int STATUS_CODE_404 = 404;
    private static final int STATUS_CODE_500 = 500;
    private static final String RESPONSE_VP = "{\"message\":\"SUCCESS\",\"data\":true}";
    public static final ResponseEntity<String> RESPONSE_ENTITY_VP = new ResponseEntity<>(RESPONSE_VP, HttpStatus.OK);

    @InjectMocks
    DeviceAssociationRestService deviceAssociationRestService;
    @Mock
    HttpServletRequest httpServletRequest;
    @Mock
    DeviceStatusRequest deviceStatusRequest;
    @Mock
    ReplaceFactoryDataRequest replaceDeviceRequest;
    @Mock
    StateChangeRequest stateChangeRequest;
    @Mock
    DeviceAssociationService deviceAssociationService;
    @Mock
    DeviceAssociationWithFactDataService deviceAssocFactoryService;
    @Mock
    DeviceAssosiationDetailsService deviceAssosiationDetailsService;
    @Mock
    UserBelongingValidator<Map<String, Object>> userBelongingValidator;
    @Mock
    VehicleProfileService vehicleProfileService;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    //@Test
    //public void associateDeviceTest_NullCheck() {
    //    AssociateDeviceRequest associateDeviceRequest = mock(AssociateDeviceRequest.class);
    //    Mockito.doReturn(null).when(associateDeviceRequest).toString();
    //    ResponseEntity<SimpleResponseMessage> responseEntity =
    //    deviceAssociationRESTService.associateDevice(associateDeviceRequest);
    //    assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    //}

    @Test
    public void associateDeviceTest() {
        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        ResponseEntity<SimpleResponseMessage> responseEntity =
            deviceAssociationRestService.associateDevice(associateDeviceRequest);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void associatedDevicesOfUserTest_NullUserId() {
        Mockito.doReturn(null).when(httpServletRequest).getHeader(USER_ID);
        ResponseEntity<?> responseEntity = deviceAssociationRestService.associatedDevicesOfUser(httpServletRequest);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void associatedDevicesOfUserTest_ValidUserId() {
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(1L);
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setHarmanId("H1234");
        deviceAssociation.setVehicleId("V1234");
        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);
        String userId = "User123";
        Mockito.doReturn(userId).when(httpServletRequest).getHeader(USER_ID);
        Mockito.doReturn(deviceAssociations).when(deviceAssociationService).getAssociatedDevicesForUser(userId);
        ResponseEntity<?> responseEntity = deviceAssociationRestService.associatedDevicesOfUser(httpServletRequest);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void associateDeviceWithFactoryDataTest_NullUserId() {
        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setSerialNumber("S1234");
        associateDeviceRequest.setImei("I1234");
        associateDeviceRequest.setBssid("B1234");
        associateDeviceRequest.setUserId("User-123");
        associateDeviceRequest.setFactoryId(1L);
        associateDeviceRequest.setIccid("ICC1234");
        associateDeviceRequest.setImsi("IMSI1234");
        associateDeviceRequest.setMsisdn("MS1234");
        associateDeviceRequest.setSsid("SS1234");
        Mockito.doReturn(null).when(httpServletRequest).getHeader(USER_ID);
        ResponseEntity<?> responseEntity =
            deviceAssociationRestService.associateDeviceWithFactoryData(associateDeviceRequest, httpServletRequest);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void associateDeviceWithFactoryDataTest_ValidRequest() throws Exception {
        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setSerialNumber("S1234");
        associateDeviceRequest.setImei("I1234");
        associateDeviceRequest.setBssid("B1234");
        associateDeviceRequest.setUserId("User-123");
        associateDeviceRequest.setFactoryId(1L);
        associateDeviceRequest.setIccid("ICC1234");
        associateDeviceRequest.setImsi("IMSI1234");
        associateDeviceRequest.setMsisdn("MS1234");
        associateDeviceRequest.setSsid("SS1234");

        AssociateDeviceResponse associateDeviceResponse =
            new AssociateDeviceResponse(1L, AssociationStatus.ASSOCIATION_INITIATED);
        String userId = "User123";
        Mockito.doReturn(userId).when(httpServletRequest).getHeader(USER_ID);
        Mockito.doReturn(associateDeviceResponse).when(deviceAssocFactoryService)
            .associateDevice(associateDeviceRequest);
        ResponseEntity<?> responseEntity =
            deviceAssociationRestService.associateDeviceWithFactoryData(associateDeviceRequest, httpServletRequest);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void associateDeviceWithFactoryDataTest_InvalidPinException() throws Exception {
        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setSerialNumber("S1234");
        associateDeviceRequest.setImei("I1234");
        associateDeviceRequest.setBssid("B1234");
        associateDeviceRequest.setUserId("User-123");
        associateDeviceRequest.setFactoryId(1L);
        associateDeviceRequest.setIccid("ICC1234");
        associateDeviceRequest.setImsi("IMSI1234");
        associateDeviceRequest.setMsisdn("MS1234");
        associateDeviceRequest.setSsid("SS1234");
        String userId = "User123";
        Mockito.doReturn(userId).when(httpServletRequest).getHeader(USER_ID);
        Mockito.doThrow(new InvalidPinException("Pin validation failed")).when(deviceAssocFactoryService)
            .associateDevice(associateDeviceRequest);
        ResponseEntity<?> responseEntity =
            deviceAssociationRestService.associateDeviceWithFactoryData(associateDeviceRequest, httpServletRequest);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void associateDeviceWithFactoryDataTest_DuplicateDeviceAssociationRequestException() throws Exception {
        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setSerialNumber("S1234");
        associateDeviceRequest.setImei("I1234");
        associateDeviceRequest.setBssid("B1234");
        associateDeviceRequest.setUserId("User-123");
        associateDeviceRequest.setFactoryId(1L);
        associateDeviceRequest.setIccid("ICC1234");
        associateDeviceRequest.setImsi("IMSI1234");
        associateDeviceRequest.setMsisdn("MS1234");
        associateDeviceRequest.setSsid("SS1234");
        String userId = "User123";
        Mockito.doReturn(userId).when(httpServletRequest).getHeader(USER_ID);
        Mockito.doThrow(new NoSuchEntityException("test")).when(deviceAssocFactoryService)
            .associateDevice(associateDeviceRequest);
        ResponseEntity<?> responseEntity =
            deviceAssociationRestService.associateDeviceWithFactoryData(associateDeviceRequest, httpServletRequest);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void associateDeviceWithFactoryDataTest_NoSuchEntityExceptionWithMessage() throws Exception {
        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setUserId("User-123");
        associateDeviceRequest.setFactoryId(1L);
        associateDeviceRequest.setIccid("ICC1234");
        associateDeviceRequest.setImsi("IMSI1234");
        associateDeviceRequest.setMsisdn("MS1234");
        associateDeviceRequest.setSsid("SS1234");
        String userId = "User123";
        Mockito.doReturn(userId).when(httpServletRequest).getHeader(USER_ID);
        Mockito.doThrow(new NoSuchEntityException("Either BSSID or IMEI or serial number is mandatory????"))
            .when(deviceAssocFactoryService).associateDevice(associateDeviceRequest);
        ResponseEntity<?> responseEntity =
            deviceAssociationRestService.associateDeviceWithFactoryData(associateDeviceRequest, httpServletRequest);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void associateDeviceWithFactoryDataTest_NoSuchEntityExceptionWithSimpleResponseMessage() throws Exception {
        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setUserId("User-123");
        associateDeviceRequest.setFactoryId(1L);
        associateDeviceRequest.setIccid("ICC1234");
        associateDeviceRequest.setImsi("IMSI1234");
        associateDeviceRequest.setMsisdn("MS1234");
        associateDeviceRequest.setSsid("SS1234");
        SimpleResponseMessage simpleResponseMessage =
            new SimpleResponseMessage("Either BSSID or IMEI or serial number is mandatory????");
        String userId = "User123";
        Mockito.doReturn(userId).when(httpServletRequest).getHeader(USER_ID);
        Mockito.doThrow(new NoSuchEntityException(simpleResponseMessage)).when(deviceAssocFactoryService)
            .associateDevice(associateDeviceRequest);
        ResponseEntity<?> responseEntity =
            deviceAssociationRestService.associateDeviceWithFactoryData(associateDeviceRequest, httpServletRequest);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void associateDeviceWithFactoryDataTest_Exception() throws Exception {
        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setUserId("User-123");
        associateDeviceRequest.setFactoryId(1L);
        associateDeviceRequest.setIccid("ICC1234");
        associateDeviceRequest.setImsi("IMSI1234");
        associateDeviceRequest.setMsisdn("MS1234");
        associateDeviceRequest.setSsid("SS1234");
        String userId = "User123";
        Mockito.doReturn("es-US").when(httpServletRequest).getHeader("accept-language");
        Mockito.doReturn(userId).when(httpServletRequest).getHeader(USER_ID);
        Mockito.doThrow(new NoSuchEntityException("Error has occurred while performing device association"))
            .when(deviceAssocFactoryService).associateDevice(associateDeviceRequest);
        ResponseEntity<?> responseEntity =
            deviceAssociationRestService.associateDeviceWithFactoryData(associateDeviceRequest, httpServletRequest);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void associatedDevicesOfUserWithFactoryDataTest_NullUserId() {
        Mockito.doReturn(null).when(httpServletRequest).getHeader(USER_ID);
        ResponseEntity<?> responseEntity =
            deviceAssociationRestService.associatedDevicesOfUserWithFactoryData(httpServletRequest);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void associatedDevicesOfUserWithFactoryDataTest_ValidUserId() {
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(1L);
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setHarmanId("H1234");
        deviceAssociation.setVehicleId("V1234");
        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);
        String userId = "User123";
        Mockito.doReturn(userId).when(httpServletRequest).getHeader(USER_ID);
        Mockito.doReturn(deviceAssociations).when(deviceAssocFactoryService).getAssociatedDevicesForUser(userId);
        ResponseEntity<?> responseEntity =
            deviceAssociationRestService.associatedDevicesOfUserWithFactoryData(httpServletRequest);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void getDeviceAssociationDetailsTest() throws Exception {
        DeviceAssosiationDetails deviceAssosiationDetails = new DeviceAssosiationDetails();
        deviceAssosiationDetails.setAssociationId(1L);
        deviceAssosiationDetails.setSerialNumber("S1234");
        deviceAssosiationDetails.setAssociationStatus("ASSOCIATED");
        deviceAssosiationDetails.setAssociatedOn("2020-06-01 07:37:49.444+00");
        deviceAssosiationDetails.setDisassociatedOn("2020-06-01 09:02:07.175+00");
        deviceAssosiationDetails.setDeviceId("H1234");
        deviceAssosiationDetails.setVehicleId("V1234");
        deviceAssosiationDetails.setImei("I1234");
        List<DeviceAssosiationDetails> deviceAssociationDetailsList = new ArrayList<>();
        deviceAssociationDetailsList.add(deviceAssosiationDetails);
        String userId = "User123";
        Mockito.doReturn(deviceAssociationDetailsList).when(deviceAssosiationDetailsService)
            .getDeviceAssosiationDetails(userId);
        ResponseEntity<?> responseEntity = deviceAssociationRestService.getDeviceAssociationDetails(userId);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void getDeviceAssociationDetailsTest_NoSuchEntityException() throws Exception {
        Mockito.doThrow(new NoSuchEntityException("UserID mandatory")).when(deviceAssosiationDetailsService)
            .getDeviceAssosiationDetails(null);
        ResponseEntity<?> responseEntity = deviceAssociationRestService.getDeviceAssociationDetails(null);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void getAssociationDetailsTest_NullUserId() {
        long associationId = 1L;
        ResponseEntity<?> responseEntity =
            deviceAssociationRestService.getAssociationDetails(associationId, httpServletRequest);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void getAssociationDetailsTest_NullDeviceAssociation() {
        String userId = "User123";
        long associationId = 1L;
        Mockito.doReturn(userId).when(httpServletRequest).getHeader(USER_ID);
        Mockito.doReturn(null).when(deviceAssociationService).getAssociationDetails(associationId, userId);
        ResponseEntity<?> responseEntity =
            deviceAssociationRestService.getAssociationDetails(associationId, httpServletRequest);
        assertEquals(HttpStatus.NOT_FOUND.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void getAssociationDetailsTest_ValidDeviceAssociation() {
        String userId = "User123";
        Mockito.doReturn(userId).when(httpServletRequest).getHeader(USER_ID);
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setUserId(userId);
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATION_INITIATED);
        long associationId = 1L;
        Mockito.doReturn(deviceAssociation).when(deviceAssociationService).getAssociationDetails(associationId, userId);
        ResponseEntity<?> responseEntity =
            deviceAssociationRestService.getAssociationDetails(associationId, httpServletRequest);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void disassociateTest() {
        ResponseEntity<?> responseEntity = deviceAssociationRestService.disassociate(1L, httpServletRequest);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
        assertEquals("Not supported from 2.14 version onwards", responseEntity.getBody());
    }

    @Test
    public void getUserDetailsOfVehicleTest_NullHarmanId() {
        ResponseEntity<?> responseEntity = deviceAssociationRestService.getUserDetailsOfVehicle(null);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void getUserDetailsOfVehicleTest_ValidHarmanId() {
        User user = new User();
        user.setUserId("User123");
        user.setEmail("abc@xyz.com");
        user.setFirstName("ABC");
        user.setLastName("XYZ");
        List<User> users = new ArrayList<>();
        users.add(user);
        String harmanId = "H1234";
        Mockito.doReturn(users).when(deviceAssociationService).getUserDetailsOfVehicle(harmanId);
        ResponseEntity<?> responseEntity = deviceAssociationRestService.getUserDetailsOfVehicle(harmanId);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void isDeviceAlreadyAssociatedTest_InvalidDeviceDetails() throws Exception {
        String userId = "User123";
        String serialNumber = "S1234";
        Mockito.doReturn(userId).when(httpServletRequest).getHeader(USER_ID);
        Mockito.doReturn(false).when(userBelongingValidator).validateUserBelonging(Mockito.any());
        ResponseEntity<?> responseEntity =
            deviceAssociationRestService.isDeviceAlreadyAssociated(serialNumber, httpServletRequest);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void isDeviceAlreadyAssociatedTest_ValidDeviceDetailsEmptyUserId() throws Exception {
        String userId = "";
        String serialNumber = "S1234";
        Mockito.doReturn(userId).when(httpServletRequest).getHeader(USER_ID);
        Mockito.doReturn(true).when(userBelongingValidator).validateUserBelonging(Mockito.any());
        ResponseEntity<?> responseEntity =
            deviceAssociationRestService.isDeviceAlreadyAssociated(serialNumber, httpServletRequest);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void isDeviceAlreadyAssociatedTest_ValidDeviceDetails() throws Exception {
        String userId = "User123";
        String serialNumber = "S1234";
        Mockito.doReturn(userId).when(httpServletRequest).getHeader(USER_ID);
        Mockito.doReturn(true).when(userBelongingValidator).validateUserBelonging(Mockito.any());
        ResponseEntity<?> responseEntity =
            deviceAssociationRestService.isDeviceAlreadyAssociated(serialNumber, httpServletRequest);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void deviceStateChangedTest_InvalidUserId() {
        DeviceState deviceState = new DeviceState();
        deviceState.setDeviceType("dongle");
        deviceState.setState(DeviceState.State.ACTIVATED);
        deviceState.setHarmanId("HID123");
        deviceState.setSerialNumber("S123");
        ResponseEntity<?> responseEntity =
            deviceAssociationRestService.deviceStateChanged(deviceState, httpServletRequest);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseEntity.getStatusCode().value());
    }

    //@Test
    //public void deviceStateChangedTest_ExceptionNullDeviceState() throws Exception {
    //    String userId = "User123";
    //    DeviceState deviceState = mock(DeviceState.class);
    //    Mockito.doReturn(null).when(deviceState).toString();
    //    Mockito.doReturn(userId).when(httpServletRequest).getHeader(USER_ID);
    //    Mockito.doThrow(new NoSuchEntityException("Database Integrity Error")).
    //    when(deviceAssociationService).deviceStateChanged(deviceState, userId);
    //    ResponseEntity<?> responseEntity =
    //    deviceAssociationRESTService.deviceStateChanged(deviceState, httpServletRequest);
    //    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseEntity.getStatusCode().value());
    //}

    @Test
    public void deviceStateChangedTest_Exception() throws Exception {
        DeviceState deviceState = new DeviceState();
        deviceState.setDeviceType("dongle");
        deviceState.setState(DeviceState.State.ACTIVATED);
        deviceState.setHarmanId("HID123");
        deviceState.setSerialNumber("S123");
        String userId = "User123";
        Mockito.doReturn(userId).when(httpServletRequest).getHeader(USER_ID);
        Mockito.doThrow(new NoSuchEntityException("Database Integrity Error")).when(deviceAssociationService)
            .deviceStateChanged(deviceState, userId);
        ResponseEntity<?> responseEntity =
            deviceAssociationRestService.deviceStateChanged(deviceState, httpServletRequest);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void deviceStateChangedTest_ValidDetails() throws Exception {
        DeviceState deviceState = new DeviceState();
        deviceState.setDeviceType("dongle");
        deviceState.setState(DeviceState.State.ACTIVATED);
        deviceState.setHarmanId("HID123");
        deviceState.setSerialNumber("S123");
        String userId = "User123";
        Mockito.doReturn(userId).when(httpServletRequest).getHeader(USER_ID);
        Mockito.doNothing().when(deviceAssociationService).deviceStateChanged(deviceState, userId);
        ResponseEntity<?> responseEntity =
            deviceAssociationRestService.deviceStateChanged(deviceState, httpServletRequest);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void getAssociationDetailsTest2_NullDeviceData() {
        ResponseEntity<?> responseEntity = deviceAssociationRestService.getAssociationDetails(httpServletRequest,
            null, null, null);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void getAssociationDetailsTest2_InvalidUserBelonging() throws Exception {
        String userId = "User123";
        String imei = "1234";
        String serialNumber = "S1234";
        String deviceId = "HID1234";
        Mockito.doReturn(imei).when(httpServletRequest).getParameter("imei");
        Mockito.doReturn(serialNumber).when(httpServletRequest).getParameter("serialnumber");
        Mockito.doReturn(deviceId).when(httpServletRequest).getParameter("deviceid");
        Mockito.doReturn(false).when(userBelongingValidator).validateUserBelonging(Mockito.any());
        Mockito.doReturn(userId).when(httpServletRequest).getHeader(USER_ID);
        ResponseEntity<?> responseEntity = deviceAssociationRestService.getAssociationDetails(httpServletRequest,
            null, null, null);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void getAssociationDetailsTest2_SameState() throws Exception {
        String userId = "User123";
        String serialNumber = "S1234";
        String deviceId = "HID1234";
        Mockito.doReturn(serialNumber).when(httpServletRequest).getParameter("serialnumber");
        Mockito.doReturn(deviceId).when(httpServletRequest).getParameter("deviceid");
        Mockito.doReturn(true).when(userBelongingValidator).validateUserBelonging(Mockito.any());
        Mockito.doReturn(userId).when(httpServletRequest).getHeader(USER_ID);
        Mockito.doReturn(true).when(deviceAssocFactoryService).isInSameState(Mockito.any(), Mockito.any());
        ResponseEntity<?> responseEntity = deviceAssociationRestService.getAssociationDetails(httpServletRequest,
            null, null, null);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void getAssociationDetailsTest2_AssociationDetailsNotFound() throws Exception {
        String userId = "User123";
        String imei = "1234";
        String serialNumber = "S1234";
        Mockito.doReturn(imei).when(httpServletRequest).getParameter("imei");
        Mockito.doReturn(serialNumber).when(httpServletRequest).getParameter("serialnumber");
        Mockito.doReturn(true).when(userBelongingValidator).validateUserBelonging(Mockito.any());
        Mockito.doReturn(userId).when(httpServletRequest).getHeader(USER_ID);
        Mockito.doReturn(false).when(deviceAssocFactoryService).isInSameState(Mockito.any(), Mockito.any());
        ResponseEntity<?> responseEntity = deviceAssociationRestService.getAssociationDetails(httpServletRequest,
            null, null, null);
        assertEquals(HttpStatus.NOT_FOUND.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void getAssociationDetailsTest2_RuntimeException() throws Exception {
        String userId = "User123";
        String imei = "I1234";
        String serialNumber = "S1234";
        Mockito.doReturn(imei).when(httpServletRequest).getParameter("imei");
        Mockito.doReturn(serialNumber).when(httpServletRequest).getParameter("serialnumber");
        Mockito.doReturn(true).when(userBelongingValidator).validateUserBelonging(Mockito.any());
        Mockito.doReturn(userId).when(httpServletRequest).getHeader(USER_ID);
        Mockito.doThrow(new RuntimeException()).when(deviceAssocFactoryService)
            .isInSameState(Mockito.any(), Mockito.any());
        ResponseEntity<?> responseEntity = deviceAssociationRestService.getAssociationDetails(httpServletRequest,
            null, null, null);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void getAssociationDetailsTest2_ValidDeviceData() throws Exception {
        String userId = "User123";
        DeviceDetail deviceDetail = new DeviceDetail();
        deviceDetail.setImei("1234");
        deviceDetail.setHarmanId("H1234");
        AssociationDetailsResponse associationDetailsResponse = new AssociationDetailsResponse();
        associationDetailsResponse.setUserId(userId);
        associationDetailsResponse.setId(1L);
        String serialNumber = "S1234";
        associationDetailsResponse.setSerialNumber(serialNumber);
        associationDetailsResponse.setAssociationStatus("ASSOCIATED");
        associationDetailsResponse.setDeviceDetail(deviceDetail);
        Mockito.doReturn(serialNumber).when(httpServletRequest).getParameter("serialnumber");
        Mockito.doReturn(true).when(userBelongingValidator).validateUserBelonging(Mockito.any());
        Mockito.doReturn(userId).when(httpServletRequest).getHeader(USER_ID);
        Mockito.doReturn(associationDetailsResponse).when(deviceAssociationService)
            .getAssociationDetails(Mockito.any());
        ResponseEntity<?> responseEntity = deviceAssociationRestService.getAssociationDetails(httpServletRequest,
            null, null, null);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void getAssociationDetailsTest_ValidDeviceData() throws Exception {
        String userId = "User123";
        DeviceDetail deviceDetail = new DeviceDetail();
        deviceDetail.setImei("1234");
        deviceDetail.setHarmanId("H1234");
        AssociationDetailsResponse associationDetailsResponse = new AssociationDetailsResponse();
        associationDetailsResponse.setUserId(userId);
        associationDetailsResponse.setId(1L);
        associationDetailsResponse.setSerialNumber("S1234");
        associationDetailsResponse.setAssociationStatus("ASSOCIATED");
        associationDetailsResponse.setDeviceDetail(deviceDetail);
        Mockito.doReturn(true).when(userBelongingValidator).validateUserBelonging(Mockito.any());
        Mockito.doReturn(userId).when(httpServletRequest).getHeader(USER_ID);
        Mockito.doReturn(associationDetailsResponse).when(deviceAssociationService)
            .getAssociationDetails(Mockito.any());
        ResponseEntity<?> responseEntity = deviceAssociationRestService.getAssociationDetails(httpServletRequest,
            "1234", null, null);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void restoreAssociationTest_BlankUserId() {
        String userId = " ";
        deviceStatusRequest.setDeviceId("HID1234");
        deviceStatusRequest.setAssociationId(1L);
        deviceStatusRequest.setImei("I1234");
        deviceStatusRequest.setUserId(userId);
        deviceStatusRequest.setSerialNumber("S1234");
        ResponseEntity<?> responseEntity = deviceAssociationRestService.restoreAssociation(deviceStatusRequest, userId);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void restoreAssociationTest_UpdateCount1() throws Exception {
        String userId = "User123";
        deviceStatusRequest.setDeviceId("HID1234");
        deviceStatusRequest.setAssociationId(1L);
        deviceStatusRequest.setImei("I1234");
        deviceStatusRequest.setUserId(userId);
        deviceStatusRequest.setSerialNumber("S1234");
        Mockito.doReturn(1).when(deviceAssocFactoryService).restoreAssociation(deviceStatusRequest);
        ResponseEntity<?> responseEntity = deviceAssociationRestService.restoreAssociation(deviceStatusRequest, userId);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void restoreAssociationTest_UpdateCount0() throws Exception {
        String userId = "User123";
        deviceStatusRequest.setDeviceId("HID1234");
        deviceStatusRequest.setAssociationId(1L);
        deviceStatusRequest.setImei("I1234");
        deviceStatusRequest.setUserId(userId);
        deviceStatusRequest.setSerialNumber("S1234");
        Mockito.doReturn(0).when(deviceAssocFactoryService).restoreAssociation(deviceStatusRequest);
        ResponseEntity<?> responseEntity = deviceAssociationRestService.restoreAssociation(deviceStatusRequest, userId);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void restoreAssociationTest_InvalidUserAssociationException() throws Exception {
        String userId = "User123";
        deviceStatusRequest.setDeviceId("HID1234");
        deviceStatusRequest.setAssociationId(1L);
        deviceStatusRequest.setImei("I1234");
        deviceStatusRequest.setUserId(userId);
        deviceStatusRequest.setSerialNumber("S1234");
        Mockito.doThrow(new InvalidUserAssociation()).when(deviceAssocFactoryService)
            .restoreAssociation(deviceStatusRequest);
        ResponseEntity<?> responseEntity = deviceAssociationRestService.restoreAssociation(deviceStatusRequest, userId);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void restoreAssociationTest_NoSuchEntityException() throws Exception {
        String userId = "User123";
        deviceStatusRequest.setDeviceId("HID1234");
        deviceStatusRequest.setAssociationId(1L);
        deviceStatusRequest.setImei("I1234");
        deviceStatusRequest.setUserId(userId);
        deviceStatusRequest.setSerialNumber("S1234");
        Mockito.doThrow(new NoSuchEntityException("Association data does not exist for given input"))
            .when(deviceAssocFactoryService).restoreAssociation(deviceStatusRequest);
        ResponseEntity<?> responseEntity = deviceAssociationRestService.restoreAssociation(deviceStatusRequest, userId);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void restoreAssociationTest_Exception() throws Exception {
        String userId = "User123";
        deviceStatusRequest.setDeviceId("HID1234");
        deviceStatusRequest.setAssociationId(1L);
        deviceStatusRequest.setImei("I1234");
        deviceStatusRequest.setUserId(userId);
        deviceStatusRequest.setSerialNumber("S1234");
        Mockito.doThrow(new NoSuchEntityException("test")).when(
                deviceAssocFactoryService).restoreAssociation(deviceStatusRequest);
        ResponseEntity<?> responseEntity = deviceAssociationRestService.restoreAssociation(deviceStatusRequest, userId);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void terminateAssociationTest_BlankUserId() {
        String userId = " ";
        deviceStatusRequest.setDeviceId("HID1234");
        deviceStatusRequest.setAssociationId(1L);
        deviceStatusRequest.setImei("I1234");
        deviceStatusRequest.setUserId(userId);
        deviceStatusRequest.setSerialNumber("S1234");
        ResponseEntity<?> responseEntity =
            deviceAssociationRestService.terminateAssociation(deviceStatusRequest, userId);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void terminateAssociationTest_UpdateCount1() throws Exception {
        String userId = "User123";
        deviceStatusRequest.setDeviceId("HID1234");
        deviceStatusRequest.setAssociationId(1L);
        deviceStatusRequest.setImei("I1234");
        deviceStatusRequest.setUserId(userId);
        deviceStatusRequest.setSerialNumber("S1234");
        Mockito.doReturn(1).when(deviceAssocFactoryService).terminateAssociation(deviceStatusRequest);
        Mockito.doReturn(RESPONSE_ENTITY_VP).when(vehicleProfileService).vehicleProfileTerminate(Mockito.any());
        ResponseEntity<?> responseEntity =
            deviceAssociationRestService.terminateAssociation(deviceStatusRequest, userId);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void terminateAssociationTest_UpdateCount0() throws Exception {
        String userId = "User123";
        deviceStatusRequest.setDeviceId("HID1234");
        deviceStatusRequest.setAssociationId(1L);
        deviceStatusRequest.setImei("I1234");
        deviceStatusRequest.setUserId(userId);
        deviceStatusRequest.setSerialNumber("S1234");
        Mockito.doReturn(0).when(deviceAssocFactoryService).terminateAssociation(deviceStatusRequest);
        ResponseEntity<?> responseEntity =
            deviceAssociationRestService.terminateAssociation(deviceStatusRequest, userId);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void terminateAssociationTest_InvalidUserAssociationException() throws Exception {
        String userId = "User123";
        deviceStatusRequest.setDeviceId("HID1234");
        deviceStatusRequest.setAssociationId(1L);
        deviceStatusRequest.setImei("I1234");
        deviceStatusRequest.setUserId(userId);
        deviceStatusRequest.setSerialNumber("S1234");
        Mockito.doThrow(new InvalidUserAssociation()).when(deviceAssocFactoryService)
            .terminateAssociation(deviceStatusRequest);
        ResponseEntity<?> responseEntity =
            deviceAssociationRestService.terminateAssociation(deviceStatusRequest, userId);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void terminateAssociationTest_NoSuchEntityException() throws Exception {
        String userId = "User123";
        deviceStatusRequest.setDeviceId("HID1234");
        deviceStatusRequest.setAssociationId(1L);
        deviceStatusRequest.setImei("I1234");
        deviceStatusRequest.setUserId(userId);
        deviceStatusRequest.setSerialNumber("S1234");
        Mockito.doThrow(new NoSuchEntityException("Association data does not exist for given input"))
            .when(deviceAssocFactoryService).terminateAssociation(deviceStatusRequest);
        ResponseEntity<?> responseEntity =
            deviceAssociationRestService.terminateAssociation(deviceStatusRequest, userId);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void terminateAssociationTest_ObserverMessageProcessFailureException() throws Exception {
        String userId = "User123";
        deviceStatusRequest.setDeviceId("HID1234");
        deviceStatusRequest.setAssociationId(1L);
        deviceStatusRequest.setImei("I1234");
        deviceStatusRequest.setUserId(userId);
        deviceStatusRequest.setSerialNumber("S1234");
        Mockito.doThrow(new ObserverMessageProcessFailureException("Exception")).when(deviceAssocFactoryService)
            .terminateAssociation(deviceStatusRequest);
        ResponseEntity<?> responseEntity =
            deviceAssociationRestService.terminateAssociation(deviceStatusRequest, userId);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void terminateAssociationTest_ObserverMessageProcessFailureExceptionWithConnectionRefusedMessage()
        throws Exception {
        String userId = "User123";
        deviceStatusRequest.setDeviceId("HID1234");
        deviceStatusRequest.setAssociationId(1L);
        deviceStatusRequest.setImei("I1234");
        deviceStatusRequest.setUserId(userId);
        deviceStatusRequest.setSerialNumber("S1234");
        Mockito.doThrow(new ObserverMessageProcessFailureException("Connection refused"))
            .when(deviceAssocFactoryService).terminateAssociation(deviceStatusRequest);
        ResponseEntity<?> responseEntity =
            deviceAssociationRestService.terminateAssociation(deviceStatusRequest, userId);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void terminateAssociationTest_Exception() throws Exception {
        String userId = "User123";
        deviceStatusRequest.setDeviceId("HID1234");
        deviceStatusRequest.setAssociationId(1L);
        deviceStatusRequest.setImei("I1234");
        deviceStatusRequest.setUserId(userId);
        deviceStatusRequest.setSerialNumber("S1234");
        Mockito.doThrow(new NoSuchEntityException("test")).when(
                deviceAssocFactoryService).terminateAssociation(deviceStatusRequest);
        ResponseEntity<?> responseEntity =
            deviceAssociationRestService.terminateAssociation(deviceStatusRequest, userId);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void suspendDeviceTest_NullRequest() {
        String userId = "User123";
        ResponseEntity<?> responseEntity = deviceAssociationRestService.suspendDevice(null, userId);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void suspendDeviceTest_InvalidUserId() {
        String userId = " ";
        deviceStatusRequest.setDeviceId("HID1234");
        deviceStatusRequest.setAssociationId(1L);
        deviceStatusRequest.setImei("I1234");
        deviceStatusRequest.setUserId(userId);
        deviceStatusRequest.setSerialNumber("S1234");
        ResponseEntity<?> responseEntity = deviceAssociationRestService.suspendDevice(deviceStatusRequest, userId);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void suspendDeviceTest_InvalidUserAssociationException() throws Exception {
        String userId = "User123";
        deviceStatusRequest.setDeviceId("HID1234");
        deviceStatusRequest.setAssociationId(1L);
        deviceStatusRequest.setImei("I1234");
        deviceStatusRequest.setUserId(userId);
        deviceStatusRequest.setSerialNumber("S1234");
        Mockito.doThrow(new InvalidUserAssociation()).when(deviceAssocFactoryService)
            .suspendDevice(deviceStatusRequest);
        ResponseEntity<?> responseEntity = deviceAssociationRestService.suspendDevice(deviceStatusRequest, userId);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void suspendDeviceTest_NoSuchEntityException() throws Exception {
        String userId = "User123";
        deviceStatusRequest.setDeviceId("HID1234");
        deviceStatusRequest.setAssociationId(1L);
        deviceStatusRequest.setImei("I1234");
        deviceStatusRequest.setUserId(userId);
        deviceStatusRequest.setSerialNumber("S1234");
        Mockito.doThrow(new NoSuchEntityException("Association data does not exist for given input"))
            .when(deviceAssocFactoryService).suspendDevice(deviceStatusRequest);
        ResponseEntity<?> responseEntity = deviceAssociationRestService.suspendDevice(deviceStatusRequest, userId);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void suspendDeviceTest_Exception() throws Exception {
        String userId = "User123";
        deviceStatusRequest.setDeviceId("HID1234");
        deviceStatusRequest.setAssociationId(1L);
        deviceStatusRequest.setImei("I1234");
        deviceStatusRequest.setUserId(userId);
        deviceStatusRequest.setSerialNumber("S1234");
        Mockito.doThrow(
                new NoSuchEntityException("test")).when(deviceAssocFactoryService).suspendDevice(deviceStatusRequest);
        ResponseEntity<?> responseEntity = deviceAssociationRestService.suspendDevice(deviceStatusRequest, userId);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void suspendDeviceTest_ValidData() throws Exception {
        String userId = "User123";
        deviceStatusRequest.setDeviceId("HID1234");
        deviceStatusRequest.setAssociationId(1L);
        deviceStatusRequest.setImei("I1234");
        deviceStatusRequest.setUserId(userId);
        deviceStatusRequest.setSerialNumber("S1234");
        AssociateDeviceResponse associateDeviceResponse = new AssociateDeviceResponse(1L, AssociationStatus.ASSOCIATED);
        Mockito.doReturn(associateDeviceResponse).when(deviceAssocFactoryService).suspendDevice(deviceStatusRequest);
        ResponseEntity<?> responseEntity = deviceAssociationRestService.suspendDevice(deviceStatusRequest, userId);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void replaceDeviceTest_BlankUserId() {
        CurrentDeviceDataPojo currentValue = new CurrentDeviceDataPojo();
        currentValue.setSerialNumber("S1234");
        currentValue.setBssid("B1234");
        currentValue.setIccid("ICC1234");
        currentValue.setImei("I1234");
        currentValue.setImsi("IMSI1234");
        ReplaceDeviceDataPojo replaceWith = new ReplaceDeviceDataPojo();
        replaceWith.setImei("I-1234");
        replaceWith.setBssid("B-1234");
        replaceWith.setIccid("ICC-1234");
        replaceWith.setImsi("IMSI-1234");
        replaceWith.setSerialNumber("S-1234");
        replaceDeviceRequest.setSerialNumber("S1234");
        replaceDeviceRequest.setCurrentValue(currentValue);
        replaceDeviceRequest.setReplaceWith(replaceWith);
        String userId = " ";
        ResponseEntity<?> responseEntity = deviceAssociationRestService.replaceDevice(replaceDeviceRequest, userId);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void replaceDeviceTest_ValidRequest() {
        CurrentDeviceDataPojo currentValue = new CurrentDeviceDataPojo();
        currentValue.setSerialNumber("S1234");
        currentValue.setBssid("B1234");
        currentValue.setIccid("ICC1234");
        currentValue.setImei("I1234");
        currentValue.setImsi("IMSI1234");
        ReplaceDeviceDataPojo replaceWith = new ReplaceDeviceDataPojo();
        replaceWith.setImei("I-1234");
        replaceWith.setBssid("B-1234");
        replaceWith.setIccid("ICC-1234");
        replaceWith.setImsi("IMSI-1234");
        replaceWith.setSerialNumber("S-1234");
        replaceDeviceRequest.setSerialNumber("S1234");
        replaceDeviceRequest.setCurrentValue(currentValue);
        replaceDeviceRequest.setReplaceWith(replaceWith);
        String userId = "User123";
        ResponseEntity<?> responseEntity = deviceAssociationRestService.replaceDevice(replaceDeviceRequest, userId);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void replaceDeviceTest_replaceIviDeviceEnabledTrue() {
        ReflectionTestUtils.setField(deviceAssociationRestService, "replaceIviDeviceEnabled", true);
        CurrentDeviceDataPojo currentValue = new CurrentDeviceDataPojo();
        currentValue.setSerialNumber("S1234");
        currentValue.setBssid("B1234");
        currentValue.setIccid("ICC1234");
        currentValue.setImei("I1234");
        currentValue.setImsi("IMSI1234");
        ReplaceDeviceDataPojo replaceWith = new ReplaceDeviceDataPojo();
        replaceWith.setImei("I-1234");
        replaceWith.setBssid("B-1234");
        replaceWith.setIccid("ICC-1234");
        replaceWith.setImsi("IMSI-1234");
        replaceWith.setSerialNumber("S-1234");
        replaceDeviceRequest.setSerialNumber("S1234");
        replaceDeviceRequest.setCurrentValue(currentValue);
        replaceDeviceRequest.setReplaceWith(replaceWith);
        String userId = "User123";
        ResponseEntity<?> responseEntity = deviceAssociationRestService.replaceDevice(replaceDeviceRequest, userId);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void replaceDeviceTest_DeviceReplaceException() {
        CurrentDeviceDataPojo currentValue = new CurrentDeviceDataPojo();
        currentValue.setSerialNumber("S1234");
        currentValue.setBssid("B1234");
        currentValue.setIccid("ICC1234");
        currentValue.setImei("I1234");
        currentValue.setImsi("IMSI1234");
        ReplaceDeviceDataPojo replaceWith = new ReplaceDeviceDataPojo();
        replaceWith.setImei("I-1234");
        replaceWith.setBssid("B-1234");
        replaceWith.setIccid("ICC-1234");
        replaceWith.setImsi("IMSI-1234");
        replaceWith.setSerialNumber("S-1234");
        replaceDeviceRequest.setSerialNumber("S1234");
        replaceDeviceRequest.setCurrentValue(currentValue);
        replaceDeviceRequest.setReplaceWith(replaceWith);
        String userId = "User123";
        Mockito.doThrow(new DeviceReplaceException("Database integrity error")).when(deviceAssocFactoryService)
            .replaceDevice(replaceDeviceRequest, userId);
        ResponseEntity<?> responseEntity = deviceAssociationRestService.replaceDevice(replaceDeviceRequest, userId);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void replaceDeviceTest_Exception() {
        CurrentDeviceDataPojo currentValue = new CurrentDeviceDataPojo();
        currentValue.setSerialNumber("S1234");
        currentValue.setBssid("B1234");
        currentValue.setIccid("ICC1234");
        currentValue.setImei("I1234");
        currentValue.setImsi("IMSI1234");
        ReplaceDeviceDataPojo replaceWith = new ReplaceDeviceDataPojo();
        replaceWith.setImei("I-1234");
        replaceWith.setBssid("B-1234");
        replaceWith.setIccid("ICC-1234");
        replaceWith.setImsi("IMSI-1234");
        replaceWith.setSerialNumber("S-1234");
        replaceDeviceRequest.setSerialNumber("S1234");
        replaceDeviceRequest.setCurrentValue(currentValue);
        replaceDeviceRequest.setReplaceWith(replaceWith);
        String userId = "User123";
        Mockito.doThrow(new InvalidUserAssociation()).when(deviceAssocFactoryService)
            .replaceDevice(replaceDeviceRequest, userId);
        ResponseEntity<?> responseEntity = deviceAssociationRestService.replaceDevice(replaceDeviceRequest, userId);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void changeStateTest_NullUserId() {
        String userId = null;
        StateChangeRequest request = new StateChangeRequest();
        request.setState("PROVISIONED");
        request.setDeviceId("H1234");
        request.setImei("I1234");
        request.setUserId("User123");
        ResponseEntity<?> responseEntity = deviceAssociationRestService.changeState(request, userId);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void changeStateTest_NoSuchEntityExceptionWithImei() throws Exception {
        StateChangeRequest request = new StateChangeRequest();
        request.setState("PROVISIONED");
        request.setDeviceId("H1234");
        request.setImei("I1234");
        request.setUserId("User123");
        Mockito.doThrow(new NoSuchEntityException("Exception from device info query service"))
            .when(deviceAssocFactoryService).stateChange(request);
        String userId = "User123";
        ResponseEntity<?> responseEntity = deviceAssociationRestService.changeState(request, userId);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void changeStateTest_NoSuchEntityExceptionWithoutImei() throws Exception {
        StateChangeRequest request = new StateChangeRequest();
        request.setState("PROVISIONED");
        request.setDeviceId("H1234");
        request.setUserId("User123");
        Mockito.doThrow(new NoSuchEntityException("Exception from device info query service"))
            .when(deviceAssocFactoryService).stateChange(request);
        String userId = "User123";
        ResponseEntity<?> responseEntity = deviceAssociationRestService.changeState(request, userId);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void changeStateTest_InvalidUserAssociationExceptionWithImei() throws Exception {
        StateChangeRequest request = new StateChangeRequest();
        request.setState("PROVISIONED");
        request.setDeviceId("H1234");
        request.setImei("I1234");
        request.setUserId("User123");
        Mockito.doThrow(new InvalidUserAssociation()).when(deviceAssocFactoryService).stateChange(request);
        String userId = "User123";
        ResponseEntity<?> responseEntity = deviceAssociationRestService.changeState(request, userId);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void changeStateTest_InvalidUserAssociationExceptionWithoutImei() throws Exception {
        StateChangeRequest request = new StateChangeRequest();
        request.setState("PROVISIONED");
        request.setDeviceId("H1234");
        request.setUserId("User123");
        Mockito.doThrow(new InvalidUserAssociation()).when(deviceAssocFactoryService).stateChange(request);
        String userId = "User123";
        ResponseEntity<?> responseEntity = deviceAssociationRestService.changeState(request, userId);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void changeStateTest_ExceptionWithImei() throws Exception {
        StateChangeRequest request = new StateChangeRequest();
        request.setState("PROVISIONED");
        request.setDeviceId("H1234");
        request.setImei("I1234");
        request.setUserId("User123");
        Mockito.doThrow(new NoSuchEntityException("test")).when(deviceAssocFactoryService).stateChange(request);
        String userId = "User123";
        ResponseEntity<?> responseEntity = deviceAssociationRestService.changeState(request, userId);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void changeStateTest_ExceptionWithoutImei() throws Exception {
        StateChangeRequest request = new StateChangeRequest();
        request.setState("PROVISIONED");
        request.setDeviceId("H1234");
        request.setUserId("User123");
        Mockito.doThrow(new NoSuchEntityException("test")).when(deviceAssocFactoryService).stateChange(request);
        String userId = "User123";
        ResponseEntity<?> responseEntity = deviceAssociationRestService.changeState(request, userId);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void changeStateTest_ValidRequest() {
        String userId = "User123";
        StateChangeRequest request = new StateChangeRequest();
        request.setState("PROVISIONED");
        request.setDeviceId("H1234");
        request.setImei("I1234");
        request.setUserId("User123");
        ResponseEntity<?> responseEntity = deviceAssociationRestService.changeState(request, userId);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void getAssociationHistoryTest() throws Exception {
        Timestamp associatedOn = new Timestamp(System.currentTimeMillis());
        Timestamp modifiedOn = new Timestamp(System.currentTimeMillis());
        DeviceAssociationHistory deviceAssociationHistory = new DeviceAssociationHistory();
        deviceAssociationHistory.setAssociationStatus(AssociationStatus.ASSOCIATED);
        deviceAssociationHistory.setUserId("User123");
        deviceAssociationHistory.setHarmanId("H1234");
        deviceAssociationHistory.setAssociatedBy("User123");
        deviceAssociationHistory.setAssociatedOn(associatedOn);
        deviceAssociationHistory.setModifiedOn(modifiedOn);
        deviceAssociationHistory.setModifiedBy("User2131");
        List<DeviceAssociationHistory> associationHistoryList = new ArrayList<>();
        associationHistoryList.add(deviceAssociationHistory);
        Mockito.doReturn(associationHistoryList).when(deviceAssocFactoryService)
            .getAssociationHistory(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(),
                Mockito.anyInt());
        String imei = "1234";
        Mockito.doReturn(1).when(deviceAssocFactoryService).getAssociationHistoryTotalCount(imei);
        String page = "5";
        String size = "20";
        String sortby = "userid";
        String orderby = "asc";
        ResponseEntity<?> responseEntity =
            deviceAssociationRestService.getAssociationHistory(imei, page, size, sortby, orderby);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void getAssociationHistoryTest_NoSuchEntityException() throws Exception {
        String imei = "1234";
        String page = "5";
        String size = "20";
        String sortby = "userid";
        String orderby = "asc";
        Mockito.doThrow(new NoSuchEntityException("Failed to retrieve device based user association history details"))
            .when(deviceAssocFactoryService)
            .getAssociationHistory(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(),
                Mockito.anyInt());
        ResponseEntity<?> responseEntity =
            deviceAssociationRestService.getAssociationHistory(imei, page, size, sortby, orderby);
        assertEquals(STATUS_CODE_404, responseEntity.getStatusCode().value());
    }

    @Test
    public void getAssociationHistoryTest_IllegalArgumentException() {
        String imei = "I1234";
        String page = "5";
        String size = "20";
        String sortby = "userid";
        String orderby = "asc";
        ResponseEntity<?> responseEntity =
            deviceAssociationRestService.getAssociationHistory(imei, page, size, sortby, orderby);
        assertEquals(STATUS_CODE_400, responseEntity.getStatusCode().value());
    }

    @Test
    public void getAssociationHistoryTest_Exception() throws Exception {
        String imei = "1234";
        String page = "5";
        String size = "20";
        String sortby = "userid";
        String orderby = "asc";
        Mockito.doThrow(new EmptyResultDataAccessException(1)).when(deviceAssocFactoryService)
            .getAssociationHistory(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(),
                Mockito.anyInt());
        ResponseEntity<?> responseEntity =
            deviceAssociationRestService.getAssociationHistory(imei, page, size, sortby, orderby);
        assertEquals(STATUS_CODE_500, responseEntity.getStatusCode().value());
    }


    @Test
    public void replaceVinForDeviceTest_NullInputs() {
        ResponseEntity<?> responseEntity = deviceAssociationRestService.replaceVinForDevice(null, null);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void replaceVinForDeviceTest_NoAssociationFound() {
        String deviceId = "H1234";
        String vin = "V1234";
        Mockito.doReturn(false).when(deviceAssociationService).associationByDeviceExists(deviceId);
        ResponseEntity<?> responseEntity = deviceAssociationRestService.replaceVinForDevice(deviceId, vin);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void replaceVinForDeviceTest_VinNotAssociated() {
        String deviceId = "H1234";
        String vin = "V1234";
        Mockito.doReturn(true).when(deviceAssociationService).associationByDeviceExists(deviceId);
        Mockito.doReturn(1L).when(deviceAssociationService).getAssociationIdByDeviceId(deviceId);
        Mockito.doReturn(false).when(deviceAssociationService).getVinAssociation(1L);
        ResponseEntity<?> responseEntity = deviceAssociationRestService.replaceVinForDevice(deviceId, vin);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void replaceVinForDeviceTest_VinAlreadyAssociated() {
        String deviceId = "H1234";
        String vin = "V1234";
        Mockito.doReturn(true).when(deviceAssociationService).associationByDeviceExists(deviceId);
        Mockito.doReturn(1L).when(deviceAssociationService).getAssociationIdByDeviceId(deviceId);
        Mockito.doReturn(true).when(deviceAssociationService).getVinAssociation(1L);
        Mockito.doReturn(true).when(deviceAssociationService).vinAlreadyAssociated(vin);
        ResponseEntity<?> responseEntity = deviceAssociationRestService.replaceVinForDevice(deviceId, vin);
        assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void replaceVinForDeviceTest_ValidData() {
        String deviceId = "H1234";
        String vin = "V1234";
        Mockito.doReturn(true).when(deviceAssociationService).associationByDeviceExists(deviceId);
        Mockito.doReturn(1L).when(deviceAssociationService).getAssociationIdByDeviceId(deviceId);
        Mockito.doReturn(true).when(deviceAssociationService).getVinAssociation(1L);
        Mockito.doReturn(false).when(deviceAssociationService).vinAlreadyAssociated(vin);
        ResponseEntity<?> responseEntity = deviceAssociationRestService.replaceVinForDevice(deviceId, vin);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void replaceVinForDeviceTest_Exception() {
        String deviceId = "H1234";
        String vin = "V1234";
        Mockito.doReturn(true).when(deviceAssociationService).associationByDeviceExists(deviceId);
        Mockito.doThrow(new EmptyResultDataAccessException(1)).when(deviceAssociationService)
            .getAssociationIdByDeviceId(deviceId);
        ResponseEntity<?> responseEntity = deviceAssociationRestService.replaceVinForDevice(deviceId, vin);
        assertEquals(STATUS_CODE_500, responseEntity.getStatusCode().value());
    }
}