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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.eclipse.ecsp.common.ApiResponse;
import org.eclipse.ecsp.common.ExtendedApiResponse;
import org.eclipse.ecsp.deviceassociation.dto.M2Mterminate;
import org.eclipse.ecsp.deviceassociation.lib.exception.WipeDataFailureException;
import org.eclipse.ecsp.deviceassociation.lib.model.AssociationStatus;
import org.eclipse.ecsp.deviceassociation.lib.model.DeviceAssociation;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociateDeviceRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociateDeviceResponse;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DelegateAssociationRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceInfo;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceItem;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceItemDto;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceItemResult;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceItemStatus;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceItems;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceStatusRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.SimSuspendRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.VinDetails;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.WipeDeviceRequest;
import org.eclipse.ecsp.deviceassociation.lib.service.DeviceAssociationService;
import org.eclipse.ecsp.deviceassociation.lib.service.DeviceAssociationWithFactDataServiceV2;
import org.eclipse.ecsp.deviceassociation.lib.service.SimStateManager;
import org.eclipse.ecsp.deviceassociation.lib.service.UserManagementClient;
import org.eclipse.ecsp.deviceassociation.lib.service.VehicleProfileService;
import org.eclipse.ecsp.deviceassociation.lib.service.VinAssociationService;
import org.eclipse.ecsp.exception.shared.ApiNotificationException;
import org.eclipse.ecsp.exception.shared.ApiPreConditionFailedException;
import org.eclipse.ecsp.exception.shared.ApiResourceNotFoundException;
import org.eclipse.ecsp.exception.shared.ApiTechnicalException;
import org.eclipse.ecsp.exception.shared.ApiValidationFailedException;
import org.eclipse.ecsp.exception.shared.SimStateChangeFailureException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.BadPaddingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.FIND_ASSO;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.GENERAL_ERROR;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.INVALID_USER_ID_ERR_MSG;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.VIN_ASSO_NOT_ENABLED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;


/**
 * AssociationControllerTest class.
 */
public class AssociationControllerTest {

    private static final long ASSOCIATION_ID = 4444L;
    private static String USER_ID = "HAR123";
    private static String requestUserId = null;
    private static final String RESPONSE_VP = "{\"message\":\"SUCCESS\",\"data\":true}";
    public static final ResponseEntity<String> RESPONSE_ENTITY_VP = new ResponseEntity<>(RESPONSE_VP, HttpStatus.OK);

    @InjectMocks
    AssociationController associationController;

    @Mock
    RestTemplate restTemplate;

    @Mock
    ObjectMapper mapper;

    @Mock
    DeviceAssociationWithFactDataServiceV2 deviceAssocFactoryService;

    @Mock
    VinAssociationService vinAssociationService;

    @Mock
    DeviceAssociationService deviceAssociationService;

    @Mock
    SimStateManager simStateManager;
    @Mock
    UserManagementClient userManagerService;
    @Mock
    VehicleProfileService vehicleProfileService;

    public AssociationControllerTest() {
    }

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    //@Test
    //public  void associateDeviceTest_NullAssociateDeviceRequest(){
    //
    //    AssociateDeviceRequest associateDeviceRequest = mock(AssociateDeviceRequest.class);
    //    Mockito.when(associateDeviceRequest.toString()).thenReturn(null);
    //
    //    HttpServletRequest mockHttpServletRequest = mock( HttpServletRequest.class );
    //    Mockito.when( mockHttpServletRequest.getHeader(Mockito.anyString())).thenReturn( "userID" );
    //
    //    associationController.associateDevice(associateDeviceRequest,mockHttpServletRequest);
    //}

    @Test
    public void associateDeviceTest() {

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setBssid("Bssid");
        associateDeviceRequest.setImei("12345");
        associateDeviceRequest.setSerialNumber("98765");

        HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
        Mockito.when(mockHttpServletRequest.getHeader(Mockito.anyString())).thenReturn("use\rrID\nid");

        ResponseEntity<ApiResponse<Object>> apiResponse =
                associationController.associateDevice(associateDeviceRequest, mockHttpServletRequest);
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
    }

    @Test
    public void associateDeviceTest_NullUserId() {

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setBssid("Bssid");
        associateDeviceRequest.setImei("12345");
        associateDeviceRequest.setSerialNumber("98765");

        HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
        Mockito.when(mockHttpServletRequest.getHeader(Mockito.anyString())).thenReturn(null);

        ResponseEntity<ApiResponse<Object>> apiResponse =
                associationController.associateDevice(associateDeviceRequest, mockHttpServletRequest);
        assertEquals(HttpStatus.BAD_REQUEST, apiResponse.getStatusCode());
    }

    @Test
    public void associateDeviceTest_EmptyAssociateDeviceRequest() {

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setBssid("");
        associateDeviceRequest.setImei("");
        associateDeviceRequest.setSerialNumber("");

        HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
        Mockito.when(mockHttpServletRequest.getHeader(Mockito.anyString())).thenReturn("userID");

        ResponseEntity<ApiResponse<Object>> apiResponse =
                associationController.associateDevice(associateDeviceRequest, mockHttpServletRequest);
        assertEquals(HttpStatus.BAD_REQUEST, apiResponse.getStatusCode());
    }

    @Test
    public void associateDeviceTest_EmptySerialNumber() {

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setBssid("Bssid");
        associateDeviceRequest.setImei("12345");
        associateDeviceRequest.setSerialNumber("");

        HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
        Mockito.when(mockHttpServletRequest.getHeader(Mockito.anyString())).thenReturn("userID");

        ResponseEntity<ApiResponse<Object>> apiResponse =
                associationController.associateDevice(associateDeviceRequest, mockHttpServletRequest);
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
    }

    @Test
    public void associateDeviceTest_EmptyImei() {

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setBssid("Bssid");
        associateDeviceRequest.setImei("");
        associateDeviceRequest.setSerialNumber("98765");

        HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
        Mockito.when(mockHttpServletRequest.getHeader(Mockito.anyString())).thenReturn("userID");

        ResponseEntity<ApiResponse<Object>> apiResponse =
                associationController.associateDevice(associateDeviceRequest, mockHttpServletRequest);
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
    }

    @Test
    public void associateDeviceTest_EmptyBssid() {

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setBssid("");
        associateDeviceRequest.setImei("12345");
        associateDeviceRequest.setSerialNumber("98765");

        HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
        Mockito.when(mockHttpServletRequest.getHeader(Mockito.anyString())).thenReturn("userID");

        ResponseEntity<ApiResponse<Object>> apiResponse =
                associationController.associateDevice(associateDeviceRequest, mockHttpServletRequest);
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
    }

    @Test
    public void associateDeviceTest_AssociateDeviceResponse() {

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setBssid("");
        associateDeviceRequest.setImei("12345");
        associateDeviceRequest.setSerialNumber("98765");

        HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
        Mockito.when(mockHttpServletRequest.getHeader(Mockito.anyString())).thenReturn("userID");

        AssociateDeviceResponse associateDeviceResponse =
            new AssociateDeviceResponse(ASSOCIATION_ID, AssociationStatus.ASSOCIATED);

        Mockito.doReturn(associateDeviceResponse).when(deviceAssocFactoryService).associateDevice(Mockito.any());
        ResponseEntity<ApiResponse<Object>> apiResponse =
                associationController.associateDevice(associateDeviceRequest, mockHttpServletRequest);
        assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
    }

    @Test
    public void associateDeviceTest_ApiValidationFailedException() {

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setBssid("");
        associateDeviceRequest.setImei("12345");
        associateDeviceRequest.setSerialNumber("98765");

        HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
        Mockito.when(mockHttpServletRequest.getHeader(Mockito.anyString())).thenReturn("userID");

        Mockito.doThrow(ApiValidationFailedException.class).when(deviceAssocFactoryService)
            .associateDevice(Mockito.any());
        ResponseEntity<ApiResponse<Object>> apiResponse =
                associationController.associateDevice(associateDeviceRequest, mockHttpServletRequest);
        assertEquals(HttpStatus.BAD_REQUEST, apiResponse.getStatusCode());
    }

    @Test
    public void associateDeviceTest_ApiResourceNotFoundException() {

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setBssid("");
        associateDeviceRequest.setImei("12345");
        associateDeviceRequest.setSerialNumber("98765");

        HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
        Mockito.when(mockHttpServletRequest.getHeader(Mockito.anyString())).thenReturn("userID");

        Mockito.doThrow(ApiResourceNotFoundException.class).when(deviceAssocFactoryService)
            .associateDevice(Mockito.any());
        ResponseEntity<ApiResponse<Object>> apiResponse =
                associationController.associateDevice(associateDeviceRequest, mockHttpServletRequest);
        assertEquals(HttpStatus.NOT_FOUND, apiResponse.getStatusCode());
    }

    @Test
    public void associateDeviceTest_ApiPreConditionFailedException() {

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setBssid("");
        associateDeviceRequest.setImei("12345");
        associateDeviceRequest.setSerialNumber("98765");

        HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
        Mockito.when(mockHttpServletRequest.getHeader(Mockito.anyString())).thenReturn("userID");

        Mockito.doThrow(ApiPreConditionFailedException.class).when(deviceAssocFactoryService)
            .associateDevice(Mockito.any());
        ResponseEntity<ApiResponse<Object>> apiResponse =
                associationController.associateDevice(associateDeviceRequest, mockHttpServletRequest);
        assertEquals(HttpStatus.PRECONDITION_FAILED, apiResponse.getStatusCode());
    }

    @Test
    public void associateDeviceTest_Exception() {

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setBssid("");
        associateDeviceRequest.setImei("12345");
        associateDeviceRequest.setSerialNumber("98765");

        HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
        Mockito.when(mockHttpServletRequest.getHeader(Mockito.anyString())).thenReturn("userID");

        Mockito.doThrow(RuntimeException.class).when(deviceAssocFactoryService).associateDevice(Mockito.any());
        ResponseEntity<ApiResponse<Object>> apiResponse =
                associationController.associateDevice(associateDeviceRequest, mockHttpServletRequest);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, apiResponse.getStatusCode());
    }

    @Test
    public void associateVinTest() {

        ReflectionTestUtils.setField(associationController, "vinAssocEnabled", false);

        VinDetails vinRequest = new VinDetails();
        vinRequest.setVin("VinRequest");
        vinRequest.setImei("1234");
        vinRequest.setRegion("TH");

        ResponseEntity<ApiResponse<Object>> apiResponse =
                associationController.associateVin(vinRequest, "HAR123");
        assertEquals(VIN_ASSO_NOT_ENABLED.getCode(), apiResponse.getBody().getCode());
    }

    @Test
    public void associateVinTest_ElseVinAssocEnabled() {

        ReflectionTestUtils.setField(associationController, "vinAssocEnabled", true);
        ReflectionTestUtils.setField(associationController, "codeValueVinDecodeCheck", true);

        VinDetails vinRequest = new VinDetails();
        vinRequest.setVin("VinRequest");
        vinRequest.setImei("1234");
        vinRequest.setRegion("TH");

        Mockito.doNothing().when(vinAssociationService)
            .vinAssociate(Mockito.anyString(), Mockito.any(), Mockito.anyLong());
        Mockito.doReturn("factoryDataModel").when(deviceAssocFactoryService).getModelByImei(Mockito.anyString());
        Mockito.doNothing().when(vinAssociationService)
            .decodingPreConditionCheck(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any());
        Mockito.doReturn(1L).when(vinAssociationService)
            .checkVinAssociationPreconditions(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        associationController.associateVin(vinRequest, "HAR123");
    }

    @Test
    public void associateVinTest_getValue() throws JsonProcessingException {

        ReflectionTestUtils.setField(associationController, "vinAssocEnabled", true);
        ReflectionTestUtils.setField(associationController, "userManagerService", new UserManagementClient());
        ReflectionTestUtils.setField(associationController, "codeValueVinDecodeCheck", false);

        VinDetails vinRequest = new VinDetails();
        vinRequest.setVin("VinRequest");
        vinRequest.setImei("1234");
        vinRequest.setRegion("TH");

        Mockito.doReturn(null).when(mapper).readTree(Mockito.anyString());
        Mockito.doReturn(null).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.eq(String.class));
        Mockito.doReturn("region").when(userManagerService).getUserDetail(Mockito.anyString(), Mockito.anyString());

        Mockito.doReturn(1L).when(vinAssociationService)
            .checkVinAssociationPreconditions(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        associationController.associateVin(vinRequest, "HAR123");
    }

    @Test
    public void associateVinTest_BlankGetValue() {

        ReflectionTestUtils.setField(associationController, "vinAssocEnabled", true);
        ReflectionTestUtils.setField(associationController, "codeValueVinDecodeCheck", false);
        ReflectionTestUtils.setField(associationController, "userManagerService", new UserManagementClient());

        VinDetails vinRequest = new VinDetails();
        vinRequest.setVin("VinRequest");
        vinRequest.setImei("1234");
        vinRequest.setRegion("TH");

        Mockito.doReturn("").when(userManagerService).getUserDetail(Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn(1L).when(vinAssociationService)
            .checkVinAssociationPreconditions(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        associationController.associateVin(vinRequest, "HAR123");
    }

    @Test
    public void associateVinTest_FalseCodeValueVinDecodeCheck() {

        ReflectionTestUtils.setField(associationController, "vinAssocEnabled", true);
        ReflectionTestUtils.setField(associationController, "userManagerService", new UserManagementClient());
        ReflectionTestUtils.setField(associationController, "codeValueVinDecodeCheck", false);
        ReflectionTestUtils.setField(vinAssociationService, "wamEnabled", false);

        VinDetails vinRequest = new VinDetails();
        vinRequest.setVin("VinRequest");
        vinRequest.setImei("1234");
        vinRequest.setRegion("TH");


        Mockito.doReturn("Output").when(deviceAssociationService).getImsi(Mockito.anyString());
        Mockito.doReturn("TransactionID").when(simStateManager)
            .changeSimState(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doReturn("RegionValue").when(userManagerService)
            .getUserDetail(Mockito.anyString(), Mockito.anyString());
        Mockito.doNothing().when(vinAssociationService)
            .vinAssociate(Mockito.anyString(), Mockito.any(), Mockito.anyLong());
        Mockito.doReturn(1L).when(vinAssociationService)
            .checkVinAssociationPreconditions(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        associationController.associateVin(vinRequest, "HAR123");
    }

    @Test
    public void associateVinTest_ApiValidationFailedException() {

        ReflectionTestUtils.setField(associationController, "vinAssocEnabled", true);
        String imei = "1234";
        VinDetails vinRequest = new VinDetails();
        vinRequest.setVin("VinRequest");
        vinRequest.setImei(imei);
        vinRequest.setRegion("TH");

        Mockito.doThrow(ApiValidationFailedException.class).when(vinAssociationService)
            .checkVinAssociationPreconditions(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        associationController.associateVin(vinRequest, USER_ID);
    }

    @Test
    public void associateVinTest_ApiValidationFailedException_NullUserId() {

        ReflectionTestUtils.setField(associationController, "vinAssocEnabled", true);

        String imei = "5678";
        VinDetails vinRequest = new VinDetails();
        vinRequest.setVin("VinRequest");
        vinRequest.setImei(imei);
        vinRequest.setRegion("TH");

        Mockito.doThrow(ApiValidationFailedException.class).when(vinAssociationService)
            .checkVinAssociationPreconditions(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        associationController.associateVin(vinRequest, requestUserId);
    }

    @Test
    public void associateVinTest_ApiValidationFailedException_NullUserIdImei() {

        ReflectionTestUtils.setField(associationController, "vinAssocEnabled", true);

        String imei = null;
        VinDetails vinRequest = new VinDetails();
        vinRequest.setVin("VinRequest");
        vinRequest.setImei(imei);
        vinRequest.setRegion("TH");

        Mockito.doThrow(ApiValidationFailedException.class).when(vinAssociationService)
            .checkVinAssociationPreconditions(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        associationController.associateVin(vinRequest, null);
    }

    @Test
    public void associateVinTest_ApiValidationFailedException_NullImei() {

        ReflectionTestUtils.setField(associationController, "vinAssocEnabled", true);

        String imei = null;
        VinDetails vinRequest = new VinDetails();
        vinRequest.setVin("VinRequest");
        vinRequest.setImei(imei);
        vinRequest.setRegion("TH");

        Mockito.doThrow(ApiValidationFailedException.class).when(vinAssociationService)
            .checkVinAssociationPreconditions(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        associationController.associateVin(vinRequest, "UserId");
    }

    @Test
    public void associateVinTest_ApiPreConditionFailedException() {

        ReflectionTestUtils.setField(associationController, "vinAssocEnabled", true);

        VinDetails vinRequest = new VinDetails();
        vinRequest.setVin("VinRequest");
        vinRequest.setImei("1234");
        vinRequest.setRegion("TH");

        Mockito.doThrow(ApiPreConditionFailedException.class).when(vinAssociationService)
            .checkVinAssociationPreconditions(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        ResponseEntity<ApiResponse<Object>> apiResponse =
                associationController.associateVin(vinRequest, "HAR123");
        assertEquals(HttpStatus.PRECONDITION_FAILED, apiResponse.getStatusCode());
    }

    @Test
    public void associateVinTest_ApiPreConditionFailedException_NullUserId() {

        ReflectionTestUtils.setField(associationController, "vinAssocEnabled", true);

        VinDetails vinRequest = new VinDetails();
        vinRequest.setVin("VinRequest");
        vinRequest.setImei("1234");
        vinRequest.setRegion("TH");

        Mockito.doThrow(ApiPreConditionFailedException.class).when(vinAssociationService)
            .checkVinAssociationPreconditions(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        ResponseEntity<ApiResponse<Object>> apiResponse =
                associationController.associateVin(vinRequest, null);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, apiResponse.getStatusCode());
    }

    @Test
    public void associateVinTest_SimStateChangeFailureException() {

        ReflectionTestUtils.setField(associationController, "vinAssocEnabled", true);

        VinDetails vinRequest = new VinDetails();
        vinRequest.setVin("VinRequest");
        vinRequest.setImei("1234");
        vinRequest.setRegion("TH");

        Mockito.doThrow(SimStateChangeFailureException.class).when(vinAssociationService)
            .checkVinAssociationPreconditions(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        ResponseEntity<ApiResponse<Object>> apiResponse =
                associationController.associateVin(vinRequest, "HAR123");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, apiResponse.getStatusCode());
    }

    @Test
    public void associateVinTest_ApiTechnicalException() {

        ReflectionTestUtils.setField(associationController, "vinAssocEnabled", true);

        VinDetails vinRequest = new VinDetails();
        vinRequest.setVin("VinRequest");
        vinRequest.setImei("1234");
        vinRequest.setRegion("TH");

        Mockito.doThrow(ApiTechnicalException.class).when(vinAssociationService)
            .checkVinAssociationPreconditions(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        ResponseEntity<ApiResponse<Object>> apiResponse =
                associationController.associateVin(vinRequest, "HAR123");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, apiResponse.getStatusCode());
    }

    @Test
    public void associateVinTest_Exception() {

        ReflectionTestUtils.setField(associationController, "vinAssocEnabled", true);

        VinDetails vinRequest = new VinDetails();
        vinRequest.setVin("VinRequest");
        vinRequest.setImei("1234");
        vinRequest.setRegion("TH");

        Mockito.doThrow(RestClientException.class).when(vinAssociationService)
            .checkVinAssociationPreconditions(Mockito.any(), Mockito.any(), Mockito.any());
        associationController.associateVin(vinRequest, "HAR123");
    }

    @Test
    public void findAssociationTest_NullUserId() {
        HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
        Mockito.when(mockHttpServletRequest.getHeader(Mockito.anyString())).thenReturn(null);

        ResponseEntity<ApiResponse<Object>> response =
                associationController.findAssociation(mockHttpServletRequest);
        assertEquals(INVALID_USER_ID_ERR_MSG.getCode(), response.getBody().getCode());
    }

    @Test
    public void findAssociationTest_BlankUserId() {
        HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
        Mockito.when(mockHttpServletRequest.getHeader(Mockito.anyString())).thenReturn("");

        ResponseEntity<ApiResponse<Object>> response =
                associationController.findAssociation(mockHttpServletRequest);
        assertEquals(INVALID_USER_ID_ERR_MSG.getCode(), response.getBody().getCode());
    }

    @Test
    public void findAssociationTest() {
        HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
        Mockito.when(mockHttpServletRequest.getHeader(Mockito.anyString())).thenReturn("user\r\nID");

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setUserId("UserId");
        deviceAssociation.setHarmanId("HarmanID");

        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);

        Mockito.doReturn(deviceAssociations).when(deviceAssocFactoryService)
            .getAssociatedDevicesForUser(Mockito.anyString());
        ResponseEntity<ApiResponse<Object>> response =
                associationController.findAssociation(mockHttpServletRequest);
        assertEquals(FIND_ASSO.getCode(), response.getBody().getCode());
    }

    @Test
    public void findAssociationTest_Exception() {

        HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
        Mockito.when(mockHttpServletRequest.getHeader(Mockito.anyString())).thenReturn("userID");
        Mockito.doThrow(RestClientException.class).when(deviceAssocFactoryService)
            .getAssociatedDevicesForUser(Mockito.anyString());
        ResponseEntity<ApiResponse<Object>> response =
                associationController.findAssociation(mockHttpServletRequest);
        assertEquals(GENERAL_ERROR.getCode(), response.getBody().getCode());
    }

    //@Test
    //public void terminateAssociationTest_NullDeviceStatusRequest(){
    //
    //    DeviceStatusRequest  deviceStatusRequest  = mock(DeviceStatusRequest .class);
    //    Mockito.when(deviceStatusRequest.toString()).thenReturn(null);
    //
    //    associationController.terminateAssociation(deviceStatusRequest, "HAR123");
    //}

    @Test
    public void terminateAssociationTest_BlankUserId() {

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setAssociationId(ASSOCIATION_ID);
        deviceStatusRequest.setImei("1234");
        deviceStatusRequest.setRequiredFor("wipeData");
        deviceStatusRequest.setSerialNumber("9123456");
        deviceStatusRequest.setUserId("userId");

        ResponseEntity<ApiResponse<Object>> response =
                associationController.terminateAssociation(deviceStatusRequest, "");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void terminateAssociationTest_EmptyUserId() {

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setAssociationId(ASSOCIATION_ID);
        deviceStatusRequest.setImei("1234");
        deviceStatusRequest.setRequiredFor("wipeData");
        deviceStatusRequest.setSerialNumber("9123456");
        deviceStatusRequest.setUserId("userId");

        String userId = "";
        ResponseEntity<ApiResponse<Object>> response =
                associationController.terminateAssociation(deviceStatusRequest, userId);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void terminateAssociationTest_NullUserId() {

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setAssociationId(ASSOCIATION_ID);
        deviceStatusRequest.setImei("1234");
        deviceStatusRequest.setRequiredFor("wipeData");
        deviceStatusRequest.setSerialNumber("9123456");
        deviceStatusRequest.setUserId("userId");

        ResponseEntity<ApiResponse<Object>> response =
                associationController.terminateAssociation(deviceStatusRequest, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void terminateAssociationTest_ZeroUpdateCount() {

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setAssociationId(ASSOCIATION_ID);
        deviceStatusRequest.setImei("1234");
        deviceStatusRequest.setRequiredFor("wipeData");
        deviceStatusRequest.setSerialNumber("9123456");
        deviceStatusRequest.setUserId("userId");

        Mockito.doReturn(0).when(deviceAssocFactoryService).terminateAssociation(Mockito.any());
        ResponseEntity<ApiResponse<Object>> response =
                associationController.terminateAssociation(deviceStatusRequest, "userId");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void terminateAssociationTest() {

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setAssociationId(ASSOCIATION_ID);
        deviceStatusRequest.setImei("1234");
        deviceStatusRequest.setRequiredFor("wipeData");
        deviceStatusRequest.setSerialNumber("9123456");
        deviceStatusRequest.setUserId("userId");

        Mockito.doReturn(1).when(deviceAssocFactoryService).terminateAssociation(Mockito.any());
        Mockito.doReturn(RESPONSE_ENTITY_VP).when(vehicleProfileService).vehicleProfileTerminate(Mockito.any());
        ResponseEntity<ApiResponse<Object>> response =
                associationController.terminateAssociation(deviceStatusRequest, "userId");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void terminateAssociationTest_ApiResourceNotFoundException() {

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setAssociationId(ASSOCIATION_ID);
        deviceStatusRequest.setImei("1234");
        deviceStatusRequest.setRequiredFor("wipeData");
        deviceStatusRequest.setSerialNumber("9123456");
        deviceStatusRequest.setUserId("userId");

        Mockito.doThrow(ApiResourceNotFoundException.class).when(deviceAssocFactoryService)
            .terminateAssociation(Mockito.any());
        ResponseEntity<ApiResponse<Object>> response =
                associationController.terminateAssociation(deviceStatusRequest, "userId");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void terminateAssociationTest_ApiPreConditionFailedException() {

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setAssociationId(ASSOCIATION_ID);
        deviceStatusRequest.setImei("1234");
        deviceStatusRequest.setRequiredFor("wipeData");
        deviceStatusRequest.setSerialNumber("9123456");
        deviceStatusRequest.setUserId("userId");

        Mockito.doThrow(ApiPreConditionFailedException.class).when(deviceAssocFactoryService)
            .terminateAssociation(Mockito.any());
        ResponseEntity<ApiResponse<Object>> response =
                associationController.terminateAssociation(deviceStatusRequest, "userId");
        assertEquals(HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
    }

    @Test
    public void terminateAssociationTest_ApiNotificationException() {

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setAssociationId(ASSOCIATION_ID);
        deviceStatusRequest.setImei("1234");
        deviceStatusRequest.setRequiredFor("wipeData");
        deviceStatusRequest.setSerialNumber("9123456");
        deviceStatusRequest.setUserId("userId");

        Mockito.doThrow(ApiNotificationException.class).when(deviceAssocFactoryService)
            .terminateAssociation(Mockito.any());
        ResponseEntity<ApiResponse<Object>> response =
                associationController.terminateAssociation(deviceStatusRequest, "userId");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void terminateAssociationTest_ApiTechnicalException() {

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setAssociationId(ASSOCIATION_ID);
        deviceStatusRequest.setImei("1234");
        deviceStatusRequest.setRequiredFor("wipeData");
        deviceStatusRequest.setSerialNumber("9123456");
        deviceStatusRequest.setUserId("userId");

        Mockito.doThrow(ApiTechnicalException.class).when(deviceAssocFactoryService)
            .terminateAssociation(Mockito.any());
        ResponseEntity<ApiResponse<Object>> response =
                associationController.terminateAssociation(deviceStatusRequest, "userId");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void terminateAssociationTest_Exception() {

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setAssociationId(ASSOCIATION_ID);
        deviceStatusRequest.setImei("1234");
        deviceStatusRequest.setRequiredFor("wipeData");
        deviceStatusRequest.setSerialNumber("9123456");
        deviceStatusRequest.setUserId("userId");

        Mockito.doThrow(RestClientException.class).when(deviceAssocFactoryService).terminateAssociation(Mockito.any());
        ResponseEntity<ApiResponse<Object>> response =
                associationController.terminateAssociation(deviceStatusRequest, "userId");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void replaceVinForDeviceTest_NullDeviceId() {

        ResponseEntity<?> response =
                associationController.replaceVinForDevice(null, "VinRequest");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void replaceVinForDeviceTest_DeviceId() {

        ResponseEntity<?> response =
                associationController.replaceVinForDevice("device\n\rID", "VinRequest");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void replaceVinForDeviceTest_FalseAssociationExists() {

        Mockito.doReturn(false).when(deviceAssociationService).associationByDeviceExists(Mockito.anyString());
        ResponseEntity<?> response =
                associationController.replaceVinForDevice("deviceID", "VinRequest");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void replaceVinForDeviceTest_FalseVinAssociated() {

        Mockito.doReturn(false).when(deviceAssociationService).getVinAssociation(Mockito.anyLong());
        Mockito.doReturn(ASSOCIATION_ID).when(deviceAssociationService).getAssociationIdByDeviceId(Mockito.anyString());
        Mockito.doReturn(true).when(deviceAssociationService).associationByDeviceExists(Mockito.anyString());
        ResponseEntity<?> response =
                associationController.replaceVinForDevice("deviceID", "VinRequest");
        assertEquals(HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
    }

    @Test
    public void replaceVinForDeviceTest_TrueVinExists() {

        Mockito.doReturn(true).when(deviceAssociationService).vinAlreadyAssociated(Mockito.anyString());
        Mockito.doReturn(true).when(deviceAssociationService).getVinAssociation(Mockito.anyLong());
        Mockito.doReturn(ASSOCIATION_ID).when(deviceAssociationService).getAssociationIdByDeviceId(Mockito.anyString());
        Mockito.doReturn(true).when(deviceAssociationService).associationByDeviceExists(Mockito.anyString());
        ResponseEntity<?> response =
                associationController.replaceVinForDevice("device\r\nID", "VinRequest");
        assertEquals(HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
    }

    @Test
    public void replaceVinForDeviceTest_FalseVinExists_NullDeviceId() {

        Mockito.doReturn(false).when(deviceAssociationService).vinAlreadyAssociated(Mockito.anyString());
        Mockito.doReturn(true).when(deviceAssociationService).getVinAssociation(Mockito.anyLong());
        Mockito.doReturn(ASSOCIATION_ID).when(deviceAssociationService).getAssociationIdByDeviceId(Mockito.anyString());
        Mockito.doReturn(true).when(deviceAssociationService).associationByDeviceExists(Mockito.anyString());
        ResponseEntity<?> response =
                associationController.replaceVinForDevice("null", "VinRequest");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void replaceVinForDeviceTest_FalseVinExists_DeviceId() {

        Mockito.doReturn(false).when(deviceAssociationService).vinAlreadyAssociated(Mockito.anyString());
        Mockito.doReturn(true).when(deviceAssociationService).getVinAssociation(Mockito.anyLong());
        Mockito.doReturn(ASSOCIATION_ID).when(deviceAssociationService).getAssociationIdByDeviceId(Mockito.anyString());
        Mockito.doReturn(true).when(deviceAssociationService).associationByDeviceExists(Mockito.anyString());
        ResponseEntity<?> response = associationController.replaceVinForDevice("device\r\nID", "VinRequest");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void replaceVinForDeviceTest() {

        Mockito.doNothing().when(deviceAssociationService).replaceVin(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(false).when(deviceAssociationService).vinAlreadyAssociated(Mockito.anyString());
        Mockito.doReturn(true).when(deviceAssociationService).getVinAssociation(Mockito.anyLong());
        Mockito.doReturn(ASSOCIATION_ID).when(deviceAssociationService).getAssociationIdByDeviceId(Mockito.anyString());
        Mockito.doReturn(false).when(deviceAssociationService).associationByDeviceExists(Mockito.anyString());
        ResponseEntity<?> response = associationController.replaceVinForDevice("deviceID", "VinRequest");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void replaceVinForDeviceTest_Exception() {

        Mockito.doThrow(RestClientException.class).when(deviceAssociationService)
            .associationByDeviceExists(Mockito.anyString());
        ResponseEntity<?> response = associationController.replaceVinForDevice("deviceID", "VinRequest");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void suspendSimTest_FalseVinAssocEnabled() {

        ReflectionTestUtils.setField(associationController, "vinAssocEnabled", false);

        SimSuspendRequest simSuspendRequest = new SimSuspendRequest();
        simSuspendRequest.setImei("1234");

        ResponseEntity<ApiResponse<Object>> response = associationController.suspendSim(simSuspendRequest, "userID");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void suspendSimTest() {

        ReflectionTestUtils.setField(associationController, "vinAssocEnabled", true);

        SimSuspendRequest simSuspendRequest = new SimSuspendRequest();
        simSuspendRequest.setImei("1234");

        Mockito.doReturn(ASSOCIATION_ID).when(vinAssociationService)
            .checkSimSuspendPreconditions(Mockito.anyString(), Mockito.anyString());
        Mockito.doNothing().when(vinAssociationService)
            .simSuspend(Mockito.anyString(), Mockito.any(), Mockito.anyLong());

        ResponseEntity<ApiResponse<Object>> response = associationController.suspendSim(simSuspendRequest, "userID");
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    public void suspendSimTest_ApiPreConditionFailedException() {

        ReflectionTestUtils.setField(associationController, "vinAssocEnabled", true);

        SimSuspendRequest simSuspendRequest = new SimSuspendRequest();
        simSuspendRequest.setImei("1234");

        Mockito.doThrow(ApiPreConditionFailedException.class).when(vinAssociationService)
            .checkSimSuspendPreconditions(Mockito.anyString(), Mockito.anyString());
        ResponseEntity<ApiResponse<Object>> response = associationController.suspendSim(simSuspendRequest, "userID");
        assertEquals(HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
    }

    @Test
    public void suspendSimTest_SimStateChangeFailureException() {

        ReflectionTestUtils.setField(associationController, "vinAssocEnabled", true);

        SimSuspendRequest simSuspendRequest = new SimSuspendRequest();
        simSuspendRequest.setImei("1234");

        Mockito.doThrow(SimStateChangeFailureException.class).when(vinAssociationService)
            .checkSimSuspendPreconditions(Mockito.anyString(), Mockito.anyString());
        ResponseEntity<ApiResponse<Object>> response = associationController.suspendSim(simSuspendRequest, "userID");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void suspendSimTest_Exception() {

        ReflectionTestUtils.setField(associationController, "vinAssocEnabled", true);

        SimSuspendRequest simSuspendRequest = new SimSuspendRequest();
        simSuspendRequest.setImei("1234");

        Mockito.doThrow(RestClientException.class).when(vinAssociationService)
            .checkSimSuspendPreconditions(Mockito.anyString(), Mockito.anyString());
        ResponseEntity<ApiResponse<Object>> response = associationController.suspendSim(simSuspendRequest, "userID");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void wipeDevicesTest_NullWipeDeviceRequest() {

        ResponseEntity<ApiResponse<Object>> response = associationController.wipeDevices(null, "userID");
        assertNotNull(response);
    }

    @Test
    public void wipeDevicesTest_NullDeviceIds() throws Exception {

        WipeDeviceRequest wipeDeviceRequest = new WipeDeviceRequest();
        wipeDeviceRequest.setSerialNumber(Arrays.asList("12345", "2344"));


        Mockito.doReturn(null).when(deviceAssociationService).wipeDevices(Mockito.anyString(), Mockito.any());
        ResponseEntity<ApiResponse<Object>> response = associationController.wipeDevices(wipeDeviceRequest, "userID");
        assertNotNull(response);
    }

    @Test
    public void wipeDevicesTest() throws Exception {

        WipeDeviceRequest wipeDeviceRequest = new WipeDeviceRequest();
        wipeDeviceRequest.setSerialNumber(Arrays.asList("12345", "3445"));

        Mockito.doReturn(Arrays.asList("12345", "6826")).when(deviceAssociationService)
            .wipeDevices(Mockito.anyString(), Mockito.any());
        ResponseEntity<ApiResponse<Object>> response = associationController.wipeDevices(wipeDeviceRequest, "userID");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void wipeDevicesTest_ApiPreConditionFailedException() throws Exception {

        WipeDeviceRequest wipeDeviceRequest = new WipeDeviceRequest();
        wipeDeviceRequest.setSerialNumber(Arrays.asList("12345", "2345"));

        Mockito.doThrow(ApiPreConditionFailedException.class).when(deviceAssociationService)
            .wipeDevices(Mockito.anyString(), Mockito.any());
        ResponseEntity<ApiResponse<Object>> response = associationController.wipeDevices(wipeDeviceRequest, "userID");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void wipeDevicesTest_ApiPreConditionFailedException_NullUserId() throws Exception {

        WipeDeviceRequest wipeDeviceRequest = new WipeDeviceRequest();
        wipeDeviceRequest.setSerialNumber(Arrays.asList("12345", "668461"));

        Mockito.doThrow(ApiPreConditionFailedException.class).when(deviceAssociationService)
            .wipeDevices(Mockito.anyString(), Mockito.any());
        ResponseEntity<ApiResponse<Object>> response = associationController.wipeDevices(wipeDeviceRequest, null);
        assertNotNull(response);
    }

    @Test
    public void wipeDevicesTest_ApiPreConditionFailedException_TrueSerialNumbersPresent() throws Exception {

        WipeDeviceRequest wipeDeviceRequest = new WipeDeviceRequest();
        wipeDeviceRequest.setSerialNumber(Arrays.asList("12345", "98989"));

        Mockito.doThrow(ApiPreConditionFailedException.class).when(deviceAssociationService)
            .wipeDevices(Mockito.anyString(), Mockito.any());
        ResponseEntity<ApiResponse<Object>> response = associationController.wipeDevices(wipeDeviceRequest, "userID");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void wipeDevicesTest_ApiPreConditionFailedException_FalseSerialNumbersPresent() throws Exception {

        WipeDeviceRequest wipeDeviceRequest = new WipeDeviceRequest();

        Mockito.doThrow(ApiPreConditionFailedException.class).when(deviceAssociationService)
            .wipeDevices(Mockito.anyString(), Mockito.any());
        ResponseEntity<ApiResponse<Object>> response = associationController.wipeDevices(wipeDeviceRequest, "userID");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void wipeDevicesTest_WipeDataFailureException() throws Exception {

        WipeDeviceRequest wipeDeviceRequest = new WipeDeviceRequest();
        wipeDeviceRequest.setSerialNumber(Arrays.asList("12345", "7879"));

        Mockito.doThrow(WipeDataFailureException.class).when(deviceAssociationService)
            .wipeDevices(Mockito.anyString(), Mockito.any());
        ResponseEntity<ApiResponse<Object>> response = associationController.wipeDevices(wipeDeviceRequest, "userID");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void wipeDevicesTest_WipeDataFailureException_NullUserId() throws Exception {

        WipeDeviceRequest wipeDeviceRequest = new WipeDeviceRequest();
        wipeDeviceRequest.setSerialNumber(Arrays.asList("12345", "66342"));

        Mockito.doThrow(WipeDataFailureException.class).when(deviceAssociationService)
            .wipeDevices(Mockito.anyString(), Mockito.any());
        ResponseEntity<ApiResponse<Object>> response = associationController.wipeDevices(wipeDeviceRequest, null);
        assertNotNull(response);
    }

    @Test
    public void wipeDevicesTest_WipeDataFailureException_TrueSerialNumbersPresent() throws Exception {

        WipeDeviceRequest wipeDeviceRequest = new WipeDeviceRequest();
        wipeDeviceRequest.setSerialNumber(Arrays.asList("12345", "98989"));

        Mockito.doThrow(WipeDataFailureException.class).when(deviceAssociationService)
            .wipeDevices(Mockito.anyString(), Mockito.any());
        ResponseEntity<ApiResponse<Object>> response = associationController.wipeDevices(wipeDeviceRequest, "userID");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void wipeDevicesTest_WipeDataFailureException_FalseSerialNumbersPresent() throws Exception {

        WipeDeviceRequest wipeDeviceRequest = new WipeDeviceRequest();

        Mockito.doThrow(WipeDataFailureException.class).when(deviceAssociationService)
            .wipeDevices(Mockito.anyString(), Mockito.any());
        ResponseEntity<ApiResponse<Object>> response = associationController.wipeDevices(wipeDeviceRequest, "userID");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void wipeDevicesTest_Exception() throws Exception {

        WipeDeviceRequest wipeDeviceRequest = new WipeDeviceRequest();
        wipeDeviceRequest.setSerialNumber(Arrays.asList("12345", "234585"));

        Mockito.doThrow(BadPaddingException.class).when(deviceAssociationService).wipeDevices(Mockito.anyString(),
                Mockito.any());
        associationController.wipeDevices(wipeDeviceRequest, "userID");
        ResponseEntity<ApiResponse<Object>> response = associationController.wipeDevices(wipeDeviceRequest, "userID");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void wipeDevicesTest_Exception_TrueSerialNumbersPresent() throws Exception {

        WipeDeviceRequest wipeDeviceRequest = new WipeDeviceRequest();
        wipeDeviceRequest.setSerialNumber(Arrays.asList("12345", "98989"));

        Mockito.doThrow(BadPaddingException.class).when(deviceAssociationService).wipeDevices(Mockito.anyString(),
                Mockito.any());
        ResponseEntity<ApiResponse<Object>> response = associationController.wipeDevices(wipeDeviceRequest, "userID");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void wipeDevicesTest_Exception_FalseSerialNumbersPresent() throws Exception {

        WipeDeviceRequest wipeDeviceRequest = new WipeDeviceRequest();

        Mockito.doThrow(BadPaddingException.class).when(deviceAssociationService).wipeDevices(Mockito.anyString(),
                Mockito.any());
        ResponseEntity<ApiResponse<Object>> response = associationController.wipeDevices(wipeDeviceRequest, "userID");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void saveDeviceItemTest_NullRequestId() {

        ResponseEntity<ExtendedApiResponse<Object>> response =
                associationController.saveDeviceItem(new DeviceItemDto(), null);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void saveDeviceItemTest() {
        DeviceItem deviceItem = new DeviceItem();
        deviceItem.setName("Name");
        deviceItem.setValue("Value");
        ArrayList<DeviceItem> deviceItemList = new ArrayList<>();
        deviceItemList.add(deviceItem);

        DeviceItems deviceItems = new DeviceItems();
        deviceItems.setDeviceId("deviceId");
        deviceItems.setItems(deviceItemList);
        ArrayList<DeviceItems> deviceItemsList = new ArrayList<>();
        deviceItemsList.add(deviceItems);

        DeviceItemDto deviceItemDto = new DeviceItemDto();
        deviceItemDto.setData(deviceItemsList);

        DeviceItemResult deviceItemResult = new DeviceItemResult();

        Mockito.doReturn(deviceItemResult).when(deviceAssociationService).saveDeviceItem(deviceItemDto);
        ResponseEntity<ExtendedApiResponse<Object>> response =
                associationController.saveDeviceItem(new DeviceItemDto(), "userID");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void saveDeviceItemTest_ApiValidationFailedException() {
        DeviceItem deviceItem = new DeviceItem();
        deviceItem.setName("Name");
        deviceItem.setValue("Value");
        ArrayList<DeviceItem> deviceItemList = new ArrayList<>();
        deviceItemList.add(deviceItem);

        DeviceItems deviceItems = new DeviceItems();
        deviceItems.setDeviceId("deviceId");
        deviceItems.setItems(deviceItemList);
        ArrayList<DeviceItems> data = new ArrayList<>();
        data.add(deviceItems);

        DeviceItemDto deviceItemDto = new DeviceItemDto();
        deviceItemDto.setData(data);

        Mockito.doThrow(ApiValidationFailedException.class).when(deviceAssociationService)
            .saveDeviceItem(deviceItemDto);
        ResponseEntity<ExtendedApiResponse<Object>> response =
                associationController.saveDeviceItem(deviceItemDto, "userID");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void saveDeviceItemTest_Exception() {
        DeviceItem deviceItem = new DeviceItem();
        deviceItem.setName("Name");
        deviceItem.setValue("Value");
        ArrayList<DeviceItem> deviceItemList = new ArrayList<>();
        deviceItemList.add(deviceItem);

        DeviceItems deviceItems = new DeviceItems();
        deviceItems.setDeviceId("deviceId");
        deviceItems.setItems(deviceItemList);
        ArrayList<DeviceItems> data = new ArrayList<>();
        data.add(deviceItems);

        DeviceItemDto deviceItemDto = new DeviceItemDto();
        deviceItemDto.setData(data);

        Mockito.doThrow(RestClientException.class).when(deviceAssociationService).saveDeviceItem(deviceItemDto);
        ResponseEntity<ExtendedApiResponse<Object>> response =
                associationController.saveDeviceItem(deviceItemDto, "userID");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void saveDeviceItemTest_buildDeviceItemResponse() {
        DeviceItem deviceItem = new DeviceItem();
        deviceItem.setName("Name");
        deviceItem.setValue("Value");
        ArrayList<DeviceItem> deviceItemList = new ArrayList<>();
        deviceItemList.add(deviceItem);

        DeviceItems deviceItems = new DeviceItems();
        deviceItems.setDeviceId("deviceId");
        deviceItems.setItems(deviceItemList);
        ArrayList<DeviceItems> data = new ArrayList<>();
        data.add(deviceItems);

        DeviceItemDto deviceItemDto = new DeviceItemDto();
        deviceItemDto.setData(data);

        Mockito.doThrow(RestClientException.class).when(deviceAssociationService).saveDeviceItem(deviceItemDto);
        ResponseEntity<ExtendedApiResponse<Object>> response =
                associationController.saveDeviceItem(deviceItemDto, "userID");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void saveDeviceItemTest_BuildDeviceItemResponse() {
        DeviceItem deviceItem = new DeviceItem();
        deviceItem.setName("Name");
        deviceItem.setValue("Value");
        ArrayList<DeviceItem> deviceItemList = new ArrayList<>();
        deviceItemList.add(deviceItem);

        DeviceItems deviceItems = new DeviceItems();
        deviceItems.setDeviceId("deviceId");
        deviceItems.setItems(deviceItemList);
        ArrayList<DeviceItems> data = new ArrayList<>();
        data.add(deviceItems);

        DeviceItemDto deviceItemDto = new DeviceItemDto();
        deviceItemDto.setData(data);

        DeviceItemResult deviceItemResult = new DeviceItemResult();
        ArrayList<DeviceItemStatus> deviceInfoStatusList = new ArrayList<>();
        DeviceItemStatus deviceItemStatus = new DeviceItemStatus("1", "SUCCESS");
        deviceInfoStatusList.add(deviceItemStatus);

        deviceItemResult.setExceptionOccured(true);
        deviceItemResult.setDeviceInfoStatusList(deviceInfoStatusList);

        Mockito.doReturn(deviceItemResult).when(deviceAssociationService).saveDeviceItem(deviceItemDto);
        ResponseEntity<ExtendedApiResponse<Object>> response =
                associationController.saveDeviceItem(deviceItemDto, "userID");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void saveDeviceItemTest_BuildDeviceItemResponse2() {
        DeviceItem deviceItem = new DeviceItem();
        deviceItem.setName("Name");
        deviceItem.setValue("Value");
        ArrayList<DeviceItem> deviceItemList = new ArrayList<>();
        deviceItemList.add(deviceItem);

        DeviceItems deviceItems = new DeviceItems();
        deviceItems.setDeviceId("deviceId");
        deviceItems.setItems(deviceItemList);
        DeviceItems deviceItems1 = new DeviceItems();
        deviceItems1.setDeviceId("deviceId");
        deviceItems1.setItems(deviceItemList);
        ArrayList<DeviceItems> data = new ArrayList<>();
        data.add(deviceItems);
        data.add(deviceItems1);

        DeviceItemDto deviceItemDto = new DeviceItemDto();
        deviceItemDto.setData(data);

        DeviceItemResult deviceItemResult = new DeviceItemResult();
        ArrayList<DeviceItemStatus> deviceInfoStatusList = new ArrayList<>();
        DeviceItemStatus deviceItemStatus = new DeviceItemStatus("1", "SUCCESS");
        deviceInfoStatusList.add(deviceItemStatus);

        deviceItemResult.setExceptionOccured(true);
        deviceItemResult.setDeviceInfoStatusList(deviceInfoStatusList);

        Mockito.doReturn(deviceItemResult).when(deviceAssociationService).saveDeviceItem(deviceItemDto);
        ResponseEntity<ExtendedApiResponse<Object>> response =
                associationController.saveDeviceItem(deviceItemDto, "userID");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void saveDeviceItemTest_TrueExceptionOccurred() {
        DeviceItem deviceItem = new DeviceItem();
        deviceItem.setName("Name");
        deviceItem.setValue("Value");
        ArrayList<DeviceItem> deviceItemList = new ArrayList<>();
        deviceItemList.add(deviceItem);

        DeviceItems deviceItems = new DeviceItems();
        deviceItems.setDeviceId("deviceId");
        deviceItems.setItems(deviceItemList);
        ArrayList<DeviceItems> data = new ArrayList<>();
        data.add(deviceItems);

        DeviceItemDto deviceItemDto = new DeviceItemDto();
        deviceItemDto.setData(data);

        DeviceItemResult deviceItemResult = new DeviceItemResult();
        ArrayList<DeviceItemStatus> deviceInfoStatusList = new ArrayList<>();
        DeviceItemStatus deviceItemStatus = new DeviceItemStatus("1", "SUCCESS");
        DeviceItemStatus deviceItemStatus1 = new DeviceItemStatus("2", "SUCCESS");
        deviceInfoStatusList.add(deviceItemStatus);
        deviceInfoStatusList.add(deviceItemStatus1);

        deviceItemResult.setExceptionOccured(true);
        deviceItemResult.setDeviceInfoStatusList(deviceInfoStatusList);

        Mockito.doReturn(deviceItemResult).when(deviceAssociationService).saveDeviceItem(deviceItemDto);
        ResponseEntity<ExtendedApiResponse<Object>> response =
                associationController.saveDeviceItem(deviceItemDto, "userID");
        assertNotNull(response);
    }

    @Test
    public void saveDeviceItemTest_FalseExceptionOccurred() {
        DeviceItem deviceItem = new DeviceItem();
        deviceItem.setName("Name");
        deviceItem.setValue("Value");
        ArrayList<DeviceItem> deviceItemList = new ArrayList<>();
        deviceItemList.add(deviceItem);

        DeviceItems deviceItems = new DeviceItems();
        deviceItems.setDeviceId("deviceId");
        deviceItems.setItems(deviceItemList);
        ArrayList<DeviceItems> data = new ArrayList<>();
        data.add(deviceItems);

        DeviceItemDto deviceItemDto = new DeviceItemDto();
        deviceItemDto.setData(data);

        DeviceItemResult deviceItemResult = new DeviceItemResult();
        ArrayList<DeviceItemStatus> deviceInfoStatusList = new ArrayList<>();
        DeviceItemStatus deviceItemStatus = new DeviceItemStatus("1", "SUCCESS");
        DeviceItemStatus deviceItemStatus1 = new DeviceItemStatus("2", "SUCCESS");
        deviceInfoStatusList.add(deviceItemStatus);
        deviceInfoStatusList.add(deviceItemStatus1);

        deviceItemResult.setExceptionOccured(false);
        deviceItemResult.setDeviceInfoStatusList(deviceInfoStatusList);

        Mockito.doReturn(deviceItemResult).when(deviceAssociationService).saveDeviceItem(deviceItemDto);
        ResponseEntity<ExtendedApiResponse<Object>> response =
                associationController.saveDeviceItem(deviceItemDto, "userID");
        assertNotNull(response);
    }

    @Test
    public void delegateAssociationTest() {

        DelegateAssociationRequest delegateAssociationRequest = new DelegateAssociationRequest();
        delegateAssociationRequest.setSerialNumber("123455");
        delegateAssociationRequest.setImei("9898");
        delegateAssociationRequest.setBssid("575867");

        HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
        Mockito.when(mockHttpServletRequest.getHeader(Mockito.anyString())).thenReturn("userID");

        AssociateDeviceResponse responseData =
            new AssociateDeviceResponse(ASSOCIATION_ID, AssociationStatus.ASSOCIATION_INITIATED);

        Mockito.doReturn(responseData).when(deviceAssocFactoryService)
            .delegateAssociation(Mockito.any(), Mockito.anyBoolean());
        ResponseEntity<?> response = associationController.delegateAssociation(delegateAssociationRequest,
                mockHttpServletRequest, "requestID");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void delegateAssociationTest_ApiValidationFailedException() {

        DelegateAssociationRequest delegateAssociationRequest = new DelegateAssociationRequest();
        delegateAssociationRequest.setSerialNumber("123455");
        delegateAssociationRequest.setImei("9898");
        delegateAssociationRequest.setBssid("575867");

        HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
        Mockito.when(mockHttpServletRequest.getHeader(Mockito.anyString())).thenReturn("userID");

        Mockito.doThrow(ApiValidationFailedException.class).when(deviceAssocFactoryService)
            .delegateAssociation(Mockito.any(), Mockito.anyBoolean());
        ResponseEntity<?> response = associationController.delegateAssociation(delegateAssociationRequest,
                mockHttpServletRequest, "requestID");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void delegateAssociationTest_ApiResourceNotFoundException() {

        DelegateAssociationRequest delegateAssociationRequest = new DelegateAssociationRequest();
        delegateAssociationRequest.setSerialNumber("123455");
        delegateAssociationRequest.setImei("9898");
        delegateAssociationRequest.setBssid("575867");

        HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
        Mockito.when(mockHttpServletRequest.getHeader(Mockito.anyString())).thenReturn("userID");

        Mockito.doThrow(ApiResourceNotFoundException.class).when(deviceAssocFactoryService)
            .delegateAssociation(Mockito.any(), Mockito.anyBoolean());
        ResponseEntity<?> response = associationController.delegateAssociation(delegateAssociationRequest,
                mockHttpServletRequest, "requestID");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void delegateAssociationTest_ApiPreConditionFailedException() {

        DelegateAssociationRequest delegateAssociationRequest = new DelegateAssociationRequest();
        delegateAssociationRequest.setSerialNumber("123455");
        delegateAssociationRequest.setImei("9898");
        delegateAssociationRequest.setBssid("575867");

        HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
        Mockito.when(mockHttpServletRequest.getHeader(Mockito.anyString())).thenReturn("userID");

        Mockito.doThrow(ApiPreConditionFailedException.class).when(deviceAssocFactoryService)
            .delegateAssociation(Mockito.any(), Mockito.anyBoolean());
        ResponseEntity<?> response = associationController.delegateAssociation(delegateAssociationRequest,
                mockHttpServletRequest, "requestID");
        assertEquals(HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
    }

    @Test
    public void delegateAssociationTest_Exception() {

        DelegateAssociationRequest delegateAssociationRequest = new DelegateAssociationRequest();
        delegateAssociationRequest.setSerialNumber("123455");
        delegateAssociationRequest.setImei("9898");
        delegateAssociationRequest.setBssid("575867");

        HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
        Mockito.when(mockHttpServletRequest.getHeader(Mockito.anyString())).thenReturn("userID");

        Mockito.doThrow(RestClientException.class).when(deviceAssocFactoryService)
            .delegateAssociation(Mockito.any(), Mockito.anyBoolean());
        ResponseEntity<?> response = associationController.delegateAssociation(delegateAssociationRequest,
                mockHttpServletRequest, "requestID");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void delegateAssociationTest_EmptyDelegateAssociationRequest() {

        DelegateAssociationRequest delegateAssociationRequest = new DelegateAssociationRequest();
        delegateAssociationRequest.setSerialNumber("");
        delegateAssociationRequest.setImei("");
        delegateAssociationRequest.setBssid("");

        HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
        Mockito.when(mockHttpServletRequest.getHeader(Mockito.anyString())).thenReturn("userID");

        ResponseEntity<?> response = associationController.delegateAssociation(delegateAssociationRequest,
                mockHttpServletRequest, "requestID");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void delegateAssociationTest_NullUserId() {

        DelegateAssociationRequest delegateAssociationRequest = new DelegateAssociationRequest();

        HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
        Mockito.when(mockHttpServletRequest.getHeader(Mockito.anyString())).thenReturn(null);

        ResponseEntity<?> response = associationController.delegateAssociation(delegateAssociationRequest,
                mockHttpServletRequest, "requestID");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void delegateAssociationTest_EmptyUserId() {

        DelegateAssociationRequest delegateAssociationRequest = new DelegateAssociationRequest();

        HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
        Mockito.when(mockHttpServletRequest.getHeader(Mockito.anyString())).thenReturn("");

        ResponseEntity<?> response = associationController.delegateAssociation(delegateAssociationRequest,
                mockHttpServletRequest, "requestID");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    //@Test
    //public void delegateAssociationTest_NullDelegateAssociationRequest(){
    //
    //    DelegateAssociationRequest delegateAssociationRequest = mock(DelegateAssociationRequest.class);
    //    Mockito.when(delegateAssociationRequest.toString()).thenReturn(null);
    //
    //    HttpServletRequest mockHttpServletRequest = mock( HttpServletRequest.class );
    //    Mockito.when( mockHttpServletRequest.getHeader(Mockito.anyString())).thenReturn("");
    //
    //    associationController.delegateAssociation(delegateAssociationRequest, mockHttpServletRequest,"requestID");
    //}

    //@Test
    //public void associateDeviceForSelfTest_NullAssociateDeviceRequest(){
    //
    //
    //    AssociateDeviceRequest associateDeviceRequest = mock(AssociateDeviceRequest.class);
    //    Mockito.when(associateDeviceRequest.toString()).thenReturn(null);
    //    associationController.associateDeviceForSelf(associateDeviceRequest,"userId","requestId");
    //}

    @Test
    public void associateDeviceForSelfTest_EmptyAssociateDeviceRequest() {

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setSerialNumber("");
        associateDeviceRequest.setImei("");
        associateDeviceRequest.setBssid("");

        ResponseEntity<?> response = associationController.associateDeviceForSelf(associateDeviceRequest,
                "userId", "requestId");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void associateDeviceForSelfTest_NullUserId() {

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setSerialNumber("123455");
        associateDeviceRequest.setImei("9898");
        associateDeviceRequest.setBssid("575867");

        ResponseEntity<?> response = associationController.associateDeviceForSelf(associateDeviceRequest,
                null, "requestId");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void associateDeviceForSelfTest_EmptyUserId() {

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setSerialNumber("123455");
        associateDeviceRequest.setImei("9898");
        associateDeviceRequest.setBssid("575867");

        ResponseEntity<?> response = associationController.associateDeviceForSelf(associateDeviceRequest,
                "", "requestId");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void associateDeviceForSelfTest() {

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setSerialNumber("123455");
        associateDeviceRequest.setImei("9898");
        associateDeviceRequest.setBssid("575867");

        AssociateDeviceResponse associateDeviceResponse =
            new AssociateDeviceResponse(ASSOCIATION_ID, AssociationStatus.ASSOCIATION_INITIATED);

        Mockito.doReturn(associateDeviceResponse).when(deviceAssocFactoryService)
            .associateDeviceForSelf(Mockito.any(), Mockito.anyString());
        ResponseEntity<?> response = associationController.associateDeviceForSelf(associateDeviceRequest,
                "user\r\nId", "requestId");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void associateDeviceForSelfTest_Exception() {

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setSerialNumber("123455");
        associateDeviceRequest.setImei("9898");
        associateDeviceRequest.setBssid("575867");

        Mockito.doThrow(RuntimeException.class).when(deviceAssocFactoryService)
            .associateDeviceForSelf(Mockito.any(), Mockito.any());
        ResponseEntity<?> response = associationController.associateDeviceForSelf(associateDeviceRequest,
                "userId", "requestId");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void associateDeviceForSelfTest_ApiPreConditionFailedException() {

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setSerialNumber("123455");
        associateDeviceRequest.setImei("9898");
        associateDeviceRequest.setBssid("575867");

        Mockito.doThrow(ApiPreConditionFailedException.class).when(deviceAssocFactoryService)
            .associateDeviceForSelf(Mockito.any(), Mockito.any());
        ResponseEntity<?> response = associationController.associateDeviceForSelf(associateDeviceRequest,
                "userId", "requestId");
        assertEquals(HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
    }

    @Test
    public void associateDeviceForSelfTest_ApiResourceNotFoundException() {

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setSerialNumber("123455");
        associateDeviceRequest.setImei("9898");
        associateDeviceRequest.setBssid("575867");

        Mockito.doThrow(ApiResourceNotFoundException.class).when(deviceAssocFactoryService)
            .associateDeviceForSelf(Mockito.any(), Mockito.any());
        ResponseEntity<?> response = associationController.associateDeviceForSelf(associateDeviceRequest,
                "userId", "requestId");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void associateDeviceForSelfTest_ApiValidationFailedException() {

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setSerialNumber("123455");
        associateDeviceRequest.setImei("9898");
        associateDeviceRequest.setBssid("575867");

        Mockito.doThrow(ApiValidationFailedException.class).when(deviceAssocFactoryService)
                .associateDeviceForSelf(Mockito.any(), Mockito.any());
        ResponseEntity<?> response = associationController.associateDeviceForSelf(associateDeviceRequest,
                "userId", "requestId");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void delegateAssociationByAdminTest_EmptyDelegateAssociationRequest() {

        DelegateAssociationRequest delegateAssociationRequest = new DelegateAssociationRequest();
        delegateAssociationRequest.setSerialNumber("");
        delegateAssociationRequest.setImei("");
        delegateAssociationRequest.setBssid("");

        HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
        Mockito.when(mockHttpServletRequest.getHeader(Mockito.anyString())).thenReturn("userID");

        ResponseEntity<?> response = associationController.delegateAssociationByAdmin(delegateAssociationRequest,
                mockHttpServletRequest, "requestID");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void delegateAssociationByAdminTest_NullUserId() {

        DelegateAssociationRequest delegateAssociationRequest = new DelegateAssociationRequest();

        HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
        Mockito.when(mockHttpServletRequest.getHeader(Mockito.anyString())).thenReturn(null);

        ResponseEntity<?> response = associationController.delegateAssociationByAdmin(delegateAssociationRequest,
                mockHttpServletRequest, "requestID");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void delegateAssociationByAdminTest_EmptyUserId() {

        DelegateAssociationRequest delegateAssociationRequest = new DelegateAssociationRequest();

        HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
        Mockito.when(mockHttpServletRequest.getHeader(Mockito.anyString())).thenReturn("userId");

        ResponseEntity<?> response = associationController.delegateAssociationByAdmin(delegateAssociationRequest,
                mockHttpServletRequest, "requestID");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    //@Test
    //public void delegateAssociationByAdminTest_NullDelegateAssociationRequest(){
    //
    //    DelegateAssociationRequest delegateAssociationRequest = mock(DelegateAssociationRequest.class);
    //    Mockito.when(delegateAssociationRequest.toString()).thenReturn(null);
    //
    //    HttpServletRequest mockHttpServletRequest = mock( HttpServletRequest.class );
    //    Mockito.when( mockHttpServletRequest.getHeader(Mockito.anyString())).thenReturn("userId");
    //
    //    associationController.delegateAssociationByAdmin(delegateAssociationRequest, mockHttpServletRequest,
    //    "requestID");
    //}

    @Test
    public void delegateAssociationByAdminTest() {

        DelegateAssociationRequest delegateAssociationRequest = new DelegateAssociationRequest();
        delegateAssociationRequest.setSerialNumber("12344");
        delegateAssociationRequest.setImei("58768");
        delegateAssociationRequest.setBssid("67897");

        HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
        Mockito.when(mockHttpServletRequest.getHeader(Mockito.anyString())).thenReturn("userId");

        AssociateDeviceResponse associateDeviceResponse =
            new AssociateDeviceResponse(ASSOCIATION_ID, AssociationStatus.ASSOCIATION_INITIATED);

        Mockito.doReturn(associateDeviceResponse).when(deviceAssocFactoryService)
            .delegateAssociationByAdmin(Mockito.any());
        ResponseEntity<?> response = associationController.delegateAssociationByAdmin(delegateAssociationRequest,
                mockHttpServletRequest, "requestID");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void delegateAssociationByAdminTest_ApiValidationFailedException() {

        DelegateAssociationRequest delegateAssociationRequest = new DelegateAssociationRequest();
        delegateAssociationRequest.setSerialNumber("12344");
        delegateAssociationRequest.setImei("58768");
        delegateAssociationRequest.setBssid("67897");

        HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
        Mockito.when(mockHttpServletRequest.getHeader(Mockito.anyString())).thenReturn("userId");

        Mockito.doThrow(ApiValidationFailedException.class).when(deviceAssocFactoryService)
            .delegateAssociationByAdmin(Mockito.any());
        ResponseEntity<?> response = associationController.delegateAssociationByAdmin(delegateAssociationRequest,
                mockHttpServletRequest, "requestID");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void delegateAssociationByAdminTest_ApiResourceNotFoundException() {

        DelegateAssociationRequest delegateAssociationRequest = new DelegateAssociationRequest();
        delegateAssociationRequest.setSerialNumber("12344");
        delegateAssociationRequest.setImei("58768");
        delegateAssociationRequest.setBssid("67897");

        HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
        Mockito.when(mockHttpServletRequest.getHeader(Mockito.anyString())).thenReturn("userId");

        Mockito.doThrow(ApiResourceNotFoundException.class).when(deviceAssocFactoryService)
            .delegateAssociationByAdmin(Mockito.any());
        ResponseEntity<?> response = associationController.delegateAssociationByAdmin(delegateAssociationRequest,
                mockHttpServletRequest, "requestID");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void delegateAssociationByAdminTest_ApiPreConditionFailedException() {

        DelegateAssociationRequest delegateAssociationRequest = new DelegateAssociationRequest();
        delegateAssociationRequest.setSerialNumber("12344");
        delegateAssociationRequest.setImei("58768");
        delegateAssociationRequest.setBssid("67897");

        HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
        Mockito.when(mockHttpServletRequest.getHeader(Mockito.anyString())).thenReturn("userId");

        Mockito.doThrow(ApiPreConditionFailedException.class).when(deviceAssocFactoryService)
            .delegateAssociationByAdmin(Mockito.any());
        ResponseEntity<?> response = associationController.delegateAssociationByAdmin(delegateAssociationRequest,
                mockHttpServletRequest, "requestID");
        assertEquals(HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
    }

    @Test
    public void delegateAssociationByAdminTest_Exception() {

        DelegateAssociationRequest delegateAssociationRequest = new DelegateAssociationRequest();
        delegateAssociationRequest.setSerialNumber("12344");
        delegateAssociationRequest.setImei("58768");
        delegateAssociationRequest.setBssid("67897");

        HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
        Mockito.when(mockHttpServletRequest.getHeader(Mockito.anyString())).thenReturn("userId");

        Mockito.doThrow(RuntimeException.class).when(deviceAssocFactoryService)
            .delegateAssociationByAdmin(Mockito.any());
        ResponseEntity<?> response = associationController.delegateAssociationByAdmin(delegateAssociationRequest,
                mockHttpServletRequest, "requestID");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void terminateM2MselfAssociationTest_NullUserId() {

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        ResponseEntity<?> response = associationController.terminateM2MselfAssociation(deviceStatusRequest,
                null, "requestID");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void terminateM2MselfAssociationTest_EmptyUserId() {

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        ResponseEntity<?> response = associationController.terminateM2MselfAssociation(deviceStatusRequest,
                "", "requestID");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    //@Test
    //public void terminateM2MselfAssociationTest_NullDeviceStatusRequest(){
    //
    //    DeviceStatusRequest deviceStatusRequest = mock(DeviceStatusRequest.class);
    //    Mockito.when(deviceStatusRequest.toString()).thenReturn(null);
    //
    //    associationController.terminateM2MselfAssociation(deviceStatusRequest, "userID","requestID");
    //}

    @Test
    public void terminateM2MselfAssociationTest() {

        M2Mterminate m2Mterminate = new M2Mterminate();
        m2Mterminate.setPerformTerminate(true);
        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();

        Mockito.doReturn(m2Mterminate).when(deviceAssocFactoryService).validatePerformTerminate(Mockito.anyString(),
            Mockito.any(), Mockito.anyBoolean());
        Mockito.doReturn(1).when(deviceAssocFactoryService)
            .terminateM2Massociation(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean());
        Mockito.doReturn(RESPONSE_ENTITY_VP).when(vehicleProfileService).vehicleProfileTerminate(Mockito.any());
        ResponseEntity<?> response = associationController.terminateM2MselfAssociation(deviceStatusRequest,
                "userID", "requestID");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void terminateM2MselfAssociationTest_ZeroUpdateCount() {

        M2Mterminate m2Mterminate = new M2Mterminate();
        m2Mterminate.setPerformTerminate(false);
        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();

        Mockito.doReturn(m2Mterminate).when(deviceAssocFactoryService).validatePerformTerminate(Mockito.anyString(),
            Mockito.any(), Mockito.anyBoolean());
        Mockito.doReturn(0).when(deviceAssocFactoryService)
            .terminateM2Massociation(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean());
        ResponseEntity<?> response = associationController.terminateM2MselfAssociation(deviceStatusRequest,
                "userID", "requestID");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void terminateM2MselfAssociationTest_ApiResourceNotFoundException() {

        M2Mterminate m2Mterminate = new M2Mterminate();
        m2Mterminate.setPerformTerminate(false);
        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();

        Mockito.doReturn(m2Mterminate).when(deviceAssocFactoryService).validatePerformTerminate(Mockito.anyString(),
            Mockito.any(), Mockito.anyBoolean());
        Mockito.doThrow(ApiResourceNotFoundException.class).when(deviceAssocFactoryService)
            .terminateM2Massociation(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean());
        ResponseEntity<?> response = associationController.terminateM2MselfAssociation(deviceStatusRequest,
                "userID", "requestID");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void terminateM2MselfAssociationTest_ApiPreConditionFailedException() {

        M2Mterminate m2Mterminate = new M2Mterminate();
        m2Mterminate.setPerformTerminate(false);
        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();

        Mockito.doReturn(m2Mterminate).when(deviceAssocFactoryService).validatePerformTerminate(Mockito.anyString(),
            Mockito.any(), Mockito.anyBoolean());
        Mockito.doThrow(ApiPreConditionFailedException.class).when(deviceAssocFactoryService)
            .terminateM2Massociation(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean());
        ResponseEntity<?> response = associationController.terminateM2MselfAssociation(deviceStatusRequest,
                "userID", "requestID");
        assertEquals(HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
    }

    @Test
    public void terminateM2MselfAssociationTest_ApiNotificationException() {

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();

        Mockito.doThrow(ApiNotificationException.class).when(deviceAssocFactoryService)
            .terminateM2Massociation(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean());
        ResponseEntity<?> response = associationController.terminateM2MselfAssociation(deviceStatusRequest,
                "userID", "requestID");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void terminateM2MselfAssociationTest_ApiTechnicalException() {

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();

        Mockito.doThrow(ApiTechnicalException.class).when(deviceAssocFactoryService)
            .terminateM2Massociation(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean());
        ResponseEntity<?> response = associationController.terminateM2MselfAssociation(deviceStatusRequest,
                "userID", "requestID");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void terminateM2MselfAssociationTest_Exception() {

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();

        Mockito.doThrow(RestClientException.class).when(deviceAssocFactoryService)
            .terminateM2Massociation(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean());
        ResponseEntity<?> response = associationController.terminateM2MselfAssociation(deviceStatusRequest,
                "userID", "requestID");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void terminateM2MassociationTest_NullUserId() {

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        ResponseEntity<?> response = associationController.terminateM2Massociation(deviceStatusRequest,
                null, "adminUserId", false, "requestID");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void terminateM2MassociationTest_EmptyUserId() {

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        ResponseEntity<?> response = associationController.terminateM2Massociation(deviceStatusRequest,
                "", "adminUserId", false, "requestID");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void terminateM2MassociationTest_EmptyAdminUserId() {

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        ResponseEntity<?> response = associationController.terminateM2Massociation(deviceStatusRequest,
                "userID", "", true, "requestID");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void terminateM2MassociationTest_NullAdminUserId() {

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        ResponseEntity<?> response = associationController.terminateM2Massociation(deviceStatusRequest,
                "userID", null, true, "requestID");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    //@Test
    //public void terminateM2MassociationTest_NullDeviceStatusRequest(){
    //
    //    DeviceStatusRequest deviceStatusRequest = mock(DeviceStatusRequest.class);
    //    Mockito.when(deviceStatusRequest.toString()).thenReturn(null);
    //
    //    associationController.terminateM2Massociation(deviceStatusRequest, "userID","adminUserId",true,"requestID");
    //}

    @Test
    public void terminateM2MassociationTest_TrueIsAdmin() {

        M2Mterminate m2Mterminate = new M2Mterminate();
        m2Mterminate.setPerformTerminate(false);
        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();

        Mockito.doReturn(m2Mterminate).when(deviceAssocFactoryService).validatePerformTerminate(Mockito.anyString(),
            Mockito.any(), Mockito.anyBoolean());
        Mockito.doReturn(true).when(deviceAssocFactoryService).validateAdminRequest(Mockito.any());
        Mockito.doReturn(1).when(deviceAssocFactoryService)
            .terminateM2Massociation(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean());
        ResponseEntity<?> response = associationController.terminateM2Massociation(deviceStatusRequest,
                "userID", "adminUserId", true, "requestID");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void terminateM2MassociationTest_TrueIsAdmin_ZeroUpdateCount() {

        M2Mterminate m2Mterminate = new M2Mterminate();
        m2Mterminate.setPerformTerminate(false);
        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();

        Mockito.doReturn(m2Mterminate).when(deviceAssocFactoryService).validatePerformTerminate(Mockito.anyString(),
            Mockito.any(), Mockito.anyBoolean());
        Mockito.doReturn(true).when(deviceAssocFactoryService).validateAdminRequest(Mockito.any());
        Mockito.doReturn(0).when(deviceAssocFactoryService)
            .terminateM2Massociation(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean());
        ResponseEntity<?> response = associationController.terminateM2Massociation(deviceStatusRequest,
                "userID", "adminUserId", true, "requestID");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void terminateM2MassociationTest_FalseIsAdmin() {

        M2Mterminate m2Mterminate = new M2Mterminate();
        m2Mterminate.setPerformTerminate(false);
        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();

        Mockito.doReturn(m2Mterminate).when(deviceAssocFactoryService).validatePerformTerminate(Mockito.anyString(),
            Mockito.any(), Mockito.anyBoolean());
        Mockito.doReturn(1).when(deviceAssocFactoryService)
            .terminateM2Massociation(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean());
        ResponseEntity<?> response = associationController.terminateM2Massociation(deviceStatusRequest,
                "userID", "adminUserId", false, "requestID");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void terminateM2MassociationTest_FalseIsAdmin_ZeroUpdateCount() {

        M2Mterminate m2Mterminate = new M2Mterminate();
        m2Mterminate.setPerformTerminate(false);
        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();

        Mockito.doReturn(m2Mterminate).when(deviceAssocFactoryService).validatePerformTerminate(Mockito.anyString(),
            Mockito.any(), Mockito.anyBoolean());
        Mockito.doReturn(0).when(deviceAssocFactoryService)
            .terminateM2Massociation(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean());
        ResponseEntity<?> response = associationController.terminateM2Massociation(deviceStatusRequest,
                "userID", "adminUserId", false, "requestID");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void terminateM2MassociationTest_ApiResourceNotFoundException() {

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();

        Mockito.doThrow(ApiResourceNotFoundException.class).when(deviceAssocFactoryService)
                .terminateM2Massociation(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean());
        ResponseEntity<?> response = associationController.terminateM2Massociation(deviceStatusRequest,
                "userID", "adminUserId", true, "requestID");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void terminateM2MassociationTest_ApiPreConditionFailedException() {

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();

        Mockito.doThrow(ApiPreConditionFailedException.class).when(deviceAssocFactoryService)
            .terminateM2Massociation(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean());
        ResponseEntity<?> response = associationController.terminateM2Massociation(deviceStatusRequest,
                "userID", "adminUserId", true, "requestID");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void terminateM2MassociationTest_ApiNotificationException() {

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();

        Mockito.doThrow(ApiNotificationException.class).when(deviceAssocFactoryService)
            .terminateM2Massociation(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean());
        ResponseEntity<?> response = associationController.terminateM2Massociation(deviceStatusRequest,
                "userID", "adminUserId", true, "requestID");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void terminateM2MassociationTest_ApiTechnicalException() {

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();

        Mockito.doThrow(ApiTechnicalException.class).when(deviceAssocFactoryService)
            .terminateM2Massociation(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean());
        ResponseEntity<?> response = associationController.terminateM2Massociation(deviceStatusRequest,
                "userID", "adminUserId", true, "requestID");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void terminateM2MassociationTest_Exception() {

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();

        Mockito.doThrow(RestClientException.class).when(deviceAssocFactoryService)
            .terminateM2Massociation(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean());
        ResponseEntity<?> response = associationController.terminateM2Massociation(deviceStatusRequest,
                "userID", "adminUserId", true, "requestID");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void terminateM2MassociationTest_ApiResourceNotFoundException_FalseIsAdmin() {

        M2Mterminate m2Mterminate = new M2Mterminate();
        m2Mterminate.setPerformTerminate(false);
        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();

        Mockito.doReturn(m2Mterminate).when(deviceAssocFactoryService).validatePerformTerminate(Mockito.anyString(),
            Mockito.any(), Mockito.anyBoolean());
        Mockito.doThrow(ApiResourceNotFoundException.class).when(deviceAssocFactoryService)
            .terminateM2Massociation(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean());
        ResponseEntity<?> response = associationController.terminateM2Massociation(deviceStatusRequest,
                "userID", "adminUserId", false, "requestID");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void terminateM2MassociationTest_ApiPreConditionFailedException_FalseIsAdmin() {

        M2Mterminate m2Mterminate = new M2Mterminate();
        m2Mterminate.setPerformTerminate(false);
        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();

        Mockito.doReturn(m2Mterminate).when(deviceAssocFactoryService).validatePerformTerminate(Mockito.anyString(),
            Mockito.any(), Mockito.anyBoolean());
        Mockito.doThrow(ApiPreConditionFailedException.class).when(deviceAssocFactoryService)
            .terminateM2Massociation(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean());
        ResponseEntity<?> response = associationController.terminateM2Massociation(deviceStatusRequest,
                "userID", "adminUserId", false, "requestID");
        assertEquals(HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
    }

    @Test
    public void terminateM2MassociationTest_ApiNotificationException_FalseIsAdmin() {

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();

        Mockito.doThrow(ApiNotificationException.class).when(deviceAssocFactoryService)
            .terminateM2Massociation(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean());
        ResponseEntity<?> response = associationController.terminateM2Massociation(deviceStatusRequest,
                "userID", "adminUserId", false, "requestID");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void terminateM2MassociationTest_ApiTechnicalException_FalseIsAdmin() {

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();

        Mockito.doThrow(ApiTechnicalException.class).when(deviceAssocFactoryService)
            .terminateM2Massociation(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean());
        ResponseEntity<?> response = associationController.terminateM2Massociation(deviceStatusRequest,
                "userID", "adminUserId", false, "requestID");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void terminateM2MassociationTest_Exception_FalseIsAdmin() {

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();

        Mockito.doThrow(RestClientException.class).when(deviceAssocFactoryService)
            .terminateM2Massociation(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean());
        ResponseEntity<?> response = associationController.terminateM2Massociation(deviceStatusRequest,
                "userID", "adminUserId", false, "requestID");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void validatePerformTerminateTest() {

        M2Mterminate m2Mterminate = new M2Mterminate();
        m2Mterminate.setPerformTerminate(false);
        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();

        Mockito.doReturn(m2Mterminate).when(deviceAssocFactoryService).validatePerformTerminate(Mockito.anyString(),
            Mockito.any(), Mockito.anyBoolean());
        associationController.validatePerformTerminate(deviceStatusRequest, "userID", false, "requestID");
    }

    @Test
    public void validatePerformTerminateTest_ApiValidationFailedException() {

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();

        Mockito.doThrow(ApiValidationFailedException.class).when(deviceAssocFactoryService)
            .validatePerformTerminate(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean());
        associationController.validatePerformTerminate(deviceStatusRequest, "userID", false, "requestID");
    }

    @Test
    public void validatePerformTerminateTest_ApiResourceNotFoundException() {

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        Mockito.doThrow(ApiResourceNotFoundException.class).when(deviceAssocFactoryService)
            .validatePerformTerminate(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean());
        associationController.validatePerformTerminate(deviceStatusRequest, "userID", false, "requestID");
    }

    @Test
    public void validatePerformTerminateTest_ApiPreConditionFailedException() {

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        Mockito.doThrow(ApiPreConditionFailedException.class).when(deviceAssocFactoryService)
            .validatePerformTerminate(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean());
        associationController.validatePerformTerminate(deviceStatusRequest, "userID", false, "requestID");
    }

    @Test
    public void validatePerformTerminateTest_Exception() {

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        Mockito.doThrow(RestClientException.class).when(deviceAssocFactoryService)
            .validatePerformTerminate(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean());
        associationController.validatePerformTerminate(deviceStatusRequest, "userID", false, "requestID");
    }

    @Test
    public void triggerKafkaEventTest() {

        DeviceInfo deviceInfo = new DeviceInfo();

        Mockito.doNothing().when(deviceAssociationService).triggerKafkaEvent(Mockito.any());
        ResponseEntity<?> response = associationController.triggerKafkaEvent(deviceInfo, "eventId", "topicName", "key");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


}

