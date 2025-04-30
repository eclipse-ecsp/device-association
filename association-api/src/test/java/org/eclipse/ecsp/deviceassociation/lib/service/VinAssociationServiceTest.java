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

import org.eclipse.ecsp.deviceassociation.dto.ModelsInfo;
import org.eclipse.ecsp.deviceassociation.dto.WhiteListedModel;
import org.eclipse.ecsp.deviceassociation.dto.WhiteListedModels;
import org.eclipse.ecsp.deviceassociation.lib.exception.ObserverMessageProcessFailureException;
import org.eclipse.ecsp.deviceassociation.lib.model.wam.SimTransactionStatus;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.SimSuspendRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.VinDetails;
import org.eclipse.ecsp.deviceassociation.lib.util.Pair;
import org.eclipse.ecsp.exception.shared.ApiPreConditionFailedException;
import org.eclipse.ecsp.exception.shared.ApiTechnicalException;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for VinAssociationService.
 */
public class VinAssociationServiceTest {

    @InjectMocks
    VinAssociationService vinAssociationService;
    @Mock
    SaasApiService saasApiService;
    @Mock
    DeviceAssociationService deviceAssociationService;
    @Mock
    SimStateManager simStateManager;
    @Mock
    private VehicleProfileService vehicleProfileService;
    @Mock
    private UserManagementClient userManagementClient;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void checkVinAssociationPreconditionsTest_NullDeviceAssociationId() {
        ReflectionTestUtils.setField(vinAssociationService, "decodeType", "CODE_VALUE");
        VinDecodeResponse vinDecodeResponse = new VinDecodeResponse();
        vinDecodeResponse.setCountry("TH");
        vinDecodeResponse.setModelName("FIRST");
        vinDecodeResponse.setModelCode("500");
        WhiteListedModel whiteListedModel = new WhiteListedModel();
        whiteListedModel.setModelCode("500");
        whiteListedModel.setModelName("FIRST");
        whiteListedModel.setCountryCode("TH");
        List<WhiteListedModel> whiteListedModelsList = new ArrayList<>();
        whiteListedModelsList.add(whiteListedModel);
        WhiteListedModels whiteListedModels = new WhiteListedModels();
        whiteListedModels.setWhiteListedModels(whiteListedModelsList);
        Mockito.doReturn(vinDecodeResponse).when(vehicleProfileService)
            .decodeVinByType(Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn("TH").when(userManagementClient).getUserDetail(Mockito.any(), Mockito.anyString());
        Mockito.doReturn(whiteListedModels).when(saasApiService).getWhiteListedModels();
        Mockito.doReturn(null).when(deviceAssociationService).associationExists(Mockito.any(), Mockito.any());
        String userId = "User123";
        String vin = "TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0";
        String imei = "IMEI123";
        assertThrows(ApiPreConditionFailedException.class,
            () -> vinAssociationService.checkVinAssociationPreconditions(userId, vin, imei));
    }

    @Test
    public void checkVinAssociationPreconditionsTest_VinAlreadyAssociated() {
        ReflectionTestUtils.setField(vinAssociationService, "decodeType", "CODE_VALUE");
        VinDecodeResponse vinDecodeResponse = new VinDecodeResponse();
        vinDecodeResponse.setCountry("TH");
        vinDecodeResponse.setModelName("FIRST");
        vinDecodeResponse.setModelCode("500");
        WhiteListedModel whiteListedModel = new WhiteListedModel();
        whiteListedModel.setModelCode("500");
        whiteListedModel.setModelName("FIRST");
        whiteListedModel.setCountryCode("TH");
        List<WhiteListedModel> whiteListedModelsList = new ArrayList<>();
        whiteListedModelsList.add(whiteListedModel);
        WhiteListedModels whiteListedModels = new WhiteListedModels();
        whiteListedModels.setWhiteListedModels(whiteListedModelsList);
        Mockito.doReturn(vinDecodeResponse).when(vehicleProfileService)
            .decodeVinByType(Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn("TH").when(userManagementClient).getUserDetail(Mockito.any(), Mockito.anyString());
        Mockito.doReturn(whiteListedModels).when(saasApiService).getWhiteListedModels();
        Mockito.doReturn(1L).when(deviceAssociationService).associationExists(Mockito.any(), Mockito.any());
        Mockito.doReturn(true).when(deviceAssociationService).getVinAssociation(Mockito.anyLong());
        String userId = "User123";
        String vin = "TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0";
        String imei = "IMEI123";
        assertThrows(ApiPreConditionFailedException.class,
            () -> vinAssociationService.checkVinAssociationPreconditions(userId, vin, imei));
    }

    @Test
    public void checkVinAssociationPreconditionsTest_VinExists() {
        ReflectionTestUtils.setField(vinAssociationService, "decodeType", "CODE_VALUE");
        VinDecodeResponse vinDecodeResponse = new VinDecodeResponse();
        vinDecodeResponse.setCountry("TH");
        vinDecodeResponse.setModelName("FIRST");
        vinDecodeResponse.setModelCode("500");
        WhiteListedModel whiteListedModel = new WhiteListedModel();
        whiteListedModel.setModelCode("500");
        whiteListedModel.setModelName("FIRST");
        whiteListedModel.setCountryCode("TH");
        List<WhiteListedModel> whiteListedModelsList = new ArrayList<>();
        whiteListedModelsList.add(whiteListedModel);
        WhiteListedModels whiteListedModels = new WhiteListedModels();
        whiteListedModels.setWhiteListedModels(whiteListedModelsList);
        Mockito.doReturn(vinDecodeResponse).when(vehicleProfileService)
            .decodeVinByType(Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn("TH").when(userManagementClient).getUserDetail(Mockito.any(), Mockito.anyString());
        Mockito.doReturn(whiteListedModels).when(saasApiService).getWhiteListedModels();
        Mockito.doReturn(1L).when(deviceAssociationService).associationExists(Mockito.any(), Mockito.any());
        Mockito.doReturn(false).when(deviceAssociationService).getVinAssociation(Mockito.anyLong());
        Mockito.doReturn(true).when(deviceAssociationService).vinAlreadyAssociated(Mockito.any());
        String userId = "User123";
        String vin = "TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0";
        String imei = "IMEI123";
        assertThrows(ApiPreConditionFailedException.class,
            () -> vinAssociationService.checkVinAssociationPreconditions(userId, vin, imei));
    }

    @Test
    public void checkVinAssociationPreconditionsTest_EmptyWhiteListedModels() {
        VinDecodeResponse vinDecodeResponse = new VinDecodeResponse();
        vinDecodeResponse.setCountry("TH");
        vinDecodeResponse.setModelName("FIRST");
        vinDecodeResponse.setModelCode("500");
        WhiteListedModels whiteListedModels = new WhiteListedModels();
        Mockito.doReturn(vinDecodeResponse).when(vehicleProfileService)
            .decodeVinByType(Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn("TH").when(userManagementClient).getUserDetail(Mockito.any(), Mockito.anyString());
        Mockito.doReturn(whiteListedModels).when(saasApiService).getWhiteListedModels();
        String userId = "User123";
        String vin = "TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0";
        String imei = "IMEI123";
        assertThrows(ApiTechnicalException.class,
            () -> vinAssociationService.checkVinAssociationPreconditions(userId, vin, imei));
    }

    @Test
    public void checkVinAssociationPreconditionsTest_ModelNotFound() {
        ReflectionTestUtils.setField(vinAssociationService, "decodeType", "CODE_VALUE");
        VinDecodeResponse vinDecodeResponse = new VinDecodeResponse();
        vinDecodeResponse.setCountry("IN");
        vinDecodeResponse.setModelName("FIRST");
        vinDecodeResponse.setModelCode("500");
        WhiteListedModel whiteListedModel = new WhiteListedModel();
        whiteListedModel.setModelCode("500");
        whiteListedModel.setModelName("FIRST");
        whiteListedModel.setCountryCode("TH");
        List<WhiteListedModel> whiteListedModelsList = new ArrayList<>();
        whiteListedModelsList.add(whiteListedModel);
        WhiteListedModels whiteListedModels = new WhiteListedModels();
        whiteListedModels.setWhiteListedModels(whiteListedModelsList);
        Mockito.doReturn(vinDecodeResponse).when(vehicleProfileService)
            .decodeVinByType(Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn("IN").when(userManagementClient).getUserDetail(Mockito.any(), Mockito.anyString());
        Mockito.doReturn(whiteListedModels).when(saasApiService).getWhiteListedModels();
        Mockito.doReturn(1L).when(deviceAssociationService).associationExists(Mockito.any(), Mockito.any());
        Mockito.doReturn(false).when(deviceAssociationService).getVinAssociation(Mockito.anyLong());
        Mockito.doReturn(false).when(deviceAssociationService).vinAlreadyAssociated(Mockito.any());
        String userId = "User123";
        String vin = "TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0";
        String imei = "IMEI123";
        assertThrows(ApiPreConditionFailedException.class,
            () -> vinAssociationService.checkVinAssociationPreconditions(userId, vin, imei));
    }

    @Test
    public void checkVinAssociationPreconditionsTest() {
        ReflectionTestUtils.setField(vinAssociationService, "decodeType", "CODE_VALUE");
        VinDecodeResponse vinDecodeResponse = new VinDecodeResponse();
        vinDecodeResponse.setCountry("TH");
        vinDecodeResponse.setModelName("FIRST");
        vinDecodeResponse.setModelCode("500");
        WhiteListedModel whiteListedModel = new WhiteListedModel();
        whiteListedModel.setModelCode("500");
        whiteListedModel.setModelName("FIRST");
        whiteListedModel.setCountryCode("TH");
        List<WhiteListedModel> whiteListedModelsList = new ArrayList<>();
        whiteListedModelsList.add(whiteListedModel);
        WhiteListedModels whiteListedModels = new WhiteListedModels();
        whiteListedModels.setWhiteListedModels(whiteListedModelsList);
        Mockito.doReturn(vinDecodeResponse).when(vehicleProfileService)
            .decodeVinByType(Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn("TH").when(userManagementClient).getUserDetail(Mockito.any(), Mockito.anyString());
        Mockito.doReturn(whiteListedModels).when(saasApiService).getWhiteListedModels();
        Mockito.doReturn(1L).when(deviceAssociationService).associationExists(Mockito.any(), Mockito.any());
        Mockito.doReturn(false).when(deviceAssociationService).getVinAssociation(Mockito.anyLong());
        Mockito.doReturn(false).when(deviceAssociationService).vinAlreadyAssociated(Mockito.any());
        String userId = "User123";
        String vin = "TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0";
        String imei = "IMEI123";
        Long associationId = vinAssociationService.checkVinAssociationPreconditions(userId, vin, imei);
        Assertions.assertEquals(1L, associationId);
    }

    @Test
    public void checkSimSuspendPreconditionsTest_NullDeviceAssociationId() {
        String userId = "User123";
        String imei = "IMEI123";
        Mockito.doReturn(null).when(deviceAssociationService).associationExists(Mockito.any(), Mockito.any());
        assertThrows(ApiPreConditionFailedException.class,
            () -> vinAssociationService.checkSimSuspendPreconditions(userId, imei));
    }

    @Test
    public void checkSimSuspendPreconditionsTest_In_Progress() {
        String userId = "User123";
        String imei = "IMEI123";
        Long deviceAssociationId = 1L;
        Mockito.doReturn(deviceAssociationId).when(deviceAssociationService)
            .associationExists(Mockito.any(), Mockito.any());
        Mockito.doReturn(SimTransactionStatus.IN_PROGRESS.getSimTransactionStatus()).when(deviceAssociationService)
            .getActiveTranStatus(deviceAssociationId);
        assertThrows(ApiPreConditionFailedException.class,
            () -> vinAssociationService.checkSimSuspendPreconditions(userId, imei));
    }

    @Test
    public void checkSimSuspendPreconditionsTest_Completed() {
        String userId = "User123";
        String imei = "IMEI123";
        Long deviceAssociationId = 1L;
        Mockito.doReturn(deviceAssociationId).when(deviceAssociationService)
            .associationExists(Mockito.any(), Mockito.any());
        Mockito.doReturn(SimTransactionStatus.COMPLETED.getSimTransactionStatus()).when(deviceAssociationService)
            .getActiveTranStatus(deviceAssociationId);
        Long actualDeviceAssociationId = vinAssociationService.checkSimSuspendPreconditions(userId, imei);
        Assertions.assertEquals(deviceAssociationId, actualDeviceAssociationId);
    }

    @Test
    public void decodingPreConditionCheckTest_NullDecodeVinPair() {
        String vin = "TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0";
        String factoryDataModel = "Model123";
        String userId = "User123";
        Long assocId = 1L;
        Mockito.doReturn(null).when(vehicleProfileService).decodeVin(Mockito.any());
        vinAssociationService.decodingPreConditionCheck(vin, factoryDataModel, userId, assocId);
        Assertions.assertNull(vehicleProfileService.decodeVin(Mockito.any()));
    }

    @Test
    public void decodingPreConditionCheckTest_ApiTechnicalException() throws ObserverMessageProcessFailureException {
        String vin = "TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0";
        String factoryDataModel = "Model123";
        String userId = "User123";
        Long assocId = 1L;
        String modelCode = "500";
        String modelName = "FIRST";
        Pair<String, String> decodeVinPair = new Pair<>(modelCode, modelName);
        Mockito.doReturn(decodeVinPair).when(vehicleProfileService).decodeVin(Mockito.any());
        Mockito.doThrow(ObserverMessageProcessFailureException.class).when(deviceAssociationService)
            .disassociate(assocId, userId);
        assertThrows(ApiTechnicalException.class,
            () -> vinAssociationService.decodingPreConditionCheck(vin, factoryDataModel, userId, assocId));
    }

    @Test
    public void decodingPreConditionCheckTest_NullModelDetailsList() throws ObserverMessageProcessFailureException {
        String userId = "User123";
        Long assocId = 1L;
        String modelCode = "500";
        String modelName = "FIRST";
        Pair<String, String> decodeVinPair = new Pair<>(modelCode, modelName);
        Mockito.doReturn(decodeVinPair).when(vehicleProfileService).decodeVin(Mockito.any());
        Mockito.doReturn(null).when(saasApiService).getStaticModelDetailsFromSystemParameter(Mockito.anyString());
        Mockito.doReturn(1).when(deviceAssociationService).disassociate(assocId, userId);
        String vin = "TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0";
        String factoryDataModel = "HSA-15TN-SB";
        vinAssociationService.decodingPreConditionCheck(vin, factoryDataModel, userId, assocId);
        Assertions.assertEquals(decodeVinPair, vehicleProfileService.decodeVin(Mockito.any()));
    }

    @Test
    public void decodingPreConditionCheckTest_DifferentModelsInfo() throws ObserverMessageProcessFailureException {
        ModelsInfo modelsInfo = new ModelsInfo();
        modelsInfo.setModelCode("500");
        modelsInfo.setModelName("HSA-15TN-SB");
        modelsInfo.setDongleType("Dongle-A");
        List<ModelsInfo> modelDetailsList = new ArrayList<>();
        modelDetailsList.add(modelsInfo);
        String modelCode = "500";
        String modelName = "FIRST";
        Pair<String, String> decodeVinPair = new Pair<>(modelCode, modelName);
        Mockito.doReturn(decodeVinPair).when(vehicleProfileService).decodeVin(Mockito.any());
        Mockito.doReturn(modelDetailsList).when(saasApiService)
            .getStaticModelDetailsFromSystemParameter(Mockito.anyString());
        String vin = "TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0";
        String factoryDataModel = "HSA-15TN-SB";
        String userId = "User123";
        Long assocId = 1L;
        Mockito.doReturn(1).when(deviceAssociationService).disassociate(assocId, userId);
        vinAssociationService.decodingPreConditionCheck(vin, factoryDataModel, userId, assocId);
        Assertions.assertEquals(decodeVinPair, vehicleProfileService.decodeVin(Mockito.any()));
    }

    @Test
    public void decodingPreConditionCheckTest_SameModelsInfo() throws ObserverMessageProcessFailureException {
        ModelsInfo modelsInfo = new ModelsInfo();
        modelsInfo.setModelCode("500");
        modelsInfo.setModelName("FIRST");
        modelsInfo.setDongleType("Dongle-B");
        List<ModelsInfo> modelDetailsList = new ArrayList<>();
        modelDetailsList.add(modelsInfo);
        String modelCode = "500";
        String modelName = "FIRST";
        Pair<String, String> decodeVinPair = new Pair<>(modelCode, modelName);
        Mockito.doReturn(decodeVinPair).when(vehicleProfileService).decodeVin(Mockito.any());
        Mockito.doReturn(modelDetailsList).when(saasApiService)
            .getStaticModelDetailsFromSystemParameter(Mockito.anyString());
        String vin = "TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0";
        String factoryDataModel = "HSA-15TN-SA";
        String userId = "User123";
        Long assocId = 1L;
        Mockito.doReturn(1).when(deviceAssociationService).disassociate(assocId, userId);
        vinAssociationService.decodingPreConditionCheck(vin, factoryDataModel, userId, assocId);
        Assertions.assertEquals(decodeVinPair, vehicleProfileService.decodeVin(Mockito.any()));
    }

    @Test
    public void vinAssociateTest() {
        VinDetails request = new VinDetails();
        request.setImei("IMEI123");
        request.setVin("TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0");
        request.setRegion("TH");
        Long deviceAssociationId = 1L;
        String userId = "User123";
        vinAssociationService.vinAssociate(userId, request, deviceAssociationId);
        Assertions.assertNotNull(request);
    }

    @Test
    public void vinAssociateTest_NullData() {
        VinDetails request = new VinDetails();
        request.setImei(null);
        request.setVin(null);
        request.setRegion("TH");
        Long deviceAssociationId = 1L;
        vinAssociationService.vinAssociate(null, request, deviceAssociationId);
        Assertions.assertNotNull(request);
    }

    @Test
    public void vinAssociateTest_wamenabledTrue() {
        ReflectionTestUtils.setField(vinAssociationService, "wamEnabled", true);
        VinDetails request = new VinDetails();
        request.setImei("IMEI123");
        request.setVin("TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0");
        request.setRegion("TH");
        Long deviceAssociationId = 1L;
        Mockito.doReturn("IMSI123").when(deviceAssociationService).getImsi(Mockito.anyString());
        Mockito.doReturn("TransactId:012").when(simStateManager)
            .changeSimState(Mockito.anyString(), Mockito.anyString(), Mockito.any());
        String userId = "User123";
        vinAssociationService.vinAssociate(userId, request, deviceAssociationId);
        Assertions.assertEquals("IMSI123", deviceAssociationService.getImsi(Mockito.anyString()));
    }

    @Test
    public void vinAssociateTest_SimActivationFailed() {
        ReflectionTestUtils.setField(vinAssociationService, "wamEnabled", true);
        VinDetails request = new VinDetails();
        request.setImei("IMEI123");
        request.setVin("TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0");
        request.setRegion("TH");
        Long deviceAssociationId = 1L;
        Mockito.doReturn(null).when(simStateManager)
            .changeSimState(Mockito.anyString(), Mockito.anyString(), Mockito.any());
        String userId = "User123";
        assertThrows(ApiPreConditionFailedException.class,
            () -> vinAssociationService.vinAssociate(userId, request, deviceAssociationId));
    }

    @Test
    public void vinAssociateTest_EmptyTransactionId() {
        ReflectionTestUtils.setField(vinAssociationService, "wamEnabled", true);
        VinDetails request = new VinDetails();
        request.setImei("IMEI123");
        request.setVin("TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0");
        request.setRegion("TH");
        Long deviceAssociationId = 1L;
        Mockito.doReturn("").when(simStateManager)
            .changeSimState(Mockito.anyString(), Mockito.anyString(), Mockito.any());
        String userId = "User123";
        assertThrows(ApiPreConditionFailedException.class,
            () -> vinAssociationService.vinAssociate(userId, request, deviceAssociationId));
    }

    @Test
    public void vinAssociateTest_SimActivationFailed_DisassociateSuccessful()
        throws ObserverMessageProcessFailureException {
        ReflectionTestUtils.setField(vinAssociationService, "wamEnabled", true);
        VinDetails request = new VinDetails();
        request.setImei("IMEI123");
        request.setVin("TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0");
        request.setRegion("TH");
        Long deviceAssociationId = 1L;
        Mockito.doReturn(null).when(simStateManager)
            .changeSimState(Mockito.anyString(), Mockito.anyString(), Mockito.any());
        Mockito.doReturn(1).when(deviceAssociationService).disassociate(Mockito.anyLong(), Mockito.anyString());
        String userId = "User123";
        assertThrows(ApiPreConditionFailedException.class,
            () -> vinAssociationService.vinAssociate(userId, request, deviceAssociationId));
    }

    @Test
    public void vinAssociateTest_DisassociateFailed() throws ObserverMessageProcessFailureException {
        ReflectionTestUtils.setField(vinAssociationService, "wamEnabled", true);
        VinDetails request = new VinDetails();
        request.setImei("IMEI123");
        request.setVin("TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0");
        request.setRegion("TH");
        Long deviceAssociationId = 1L;
        Mockito.doReturn(null).when(simStateManager)
            .changeSimState(Mockito.anyString(), Mockito.anyString(), Mockito.any());
        Mockito.doThrow(ObserverMessageProcessFailureException.class).when(deviceAssociationService)
            .disassociate(Mockito.anyLong(), Mockito.anyString());
        String userId = "User123";
        assertThrows(ApiPreConditionFailedException.class,
            () -> vinAssociationService.vinAssociate(userId, request, deviceAssociationId));
    }

    @Test
    public void simSuspendTest_NullUserId_NullTransactionId() {
        SimSuspendRequest request = new SimSuspendRequest();
        request.setImei("IMEI123");
        Long deviceAssociationId = 1L;
        Mockito.doReturn(null).when(deviceAssociationService).getImsi(Mockito.any());
        Mockito.doReturn(null).when(deviceAssociationService).getRegion(Mockito.any());
        assertThrows(ApiTechnicalException.class,
            () -> vinAssociationService.simSuspend(null, request, deviceAssociationId));
    }

    @Test
    public void simSuspendTest_ValidInput() {
        SimSuspendRequest request = new SimSuspendRequest();
        request.setImei("IMEI123");
        Mockito.doReturn("IMSI123").when(deviceAssociationService).getImsi(Mockito.any());
        Mockito.doReturn("TH").when(deviceAssociationService).getRegion(Mockito.any());
        Mockito.doReturn("TransactId123").when(simStateManager)
            .changeSimState(Mockito.anyString(), Mockito.anyString(), Mockito.any());
        String userId = "User123";
        Long deviceAssociationId = 1L;
        vinAssociationService.simSuspend(userId, request, deviceAssociationId);
        Assertions.assertEquals("IMSI123", deviceAssociationService.getImsi(Mockito.anyString()));
    }
}