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

import org.eclipse.ecsp.auth.lib.dao.DeviceInfoSharedDao;
import org.eclipse.ecsp.common.config.EnvConfig;
import org.eclipse.ecsp.deviceassociation.dto.TriggerKafkaEventRequestDto;
import org.eclipse.ecsp.deviceassociation.lib.config.DeviceAssocationProperty;
import org.eclipse.ecsp.deviceassociation.lib.dao.DeviceAssociationDao;
import org.eclipse.ecsp.deviceassociation.lib.exception.DuplicateDeviceAssociationRequestException;
import org.eclipse.ecsp.deviceassociation.lib.exception.WipeDataFailureException;
import org.eclipse.ecsp.deviceassociation.lib.model.AssociationStatus;
import org.eclipse.ecsp.deviceassociation.lib.model.DeviceAssociation;
import org.eclipse.ecsp.deviceassociation.lib.observer.DeviceAssociationObservable;
import org.eclipse.ecsp.deviceassociation.lib.observer.KafkaDeviceNotificationObserver;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociateDeviceRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociateDeviceResponse;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceItem;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceItemDto;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceItemResult;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceItems;
import org.eclipse.ecsp.exception.shared.ApiPreConditionFailedException;
import org.eclipse.ecsp.exception.shared.ApiValidationFailedException;
import org.eclipse.ecsp.services.clientlib.HcpRestClientLibrary;
import org.eclipse.ecsp.services.shared.deviceinfo.model.DeviceAttributes;
import org.eclipse.ecsp.services.shared.deviceinfo.model.DeviceInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for DeviceAssociationService.
 */
public class DeviceAssociationServiceTest {
    private static long ASSOCIATION_ID = 1234L;
    private static int UPDATED_COUNT = 10;

    @InjectMocks
    DeviceAssociationService deviceAssociationService;

    @Mock
    DeviceAssociationWithFactDataServiceV2 deviceAssocFactoryServiceV2;

    @Mock
    DeviceAssociationDao deviceAssociationDao;

    @Mock
    DeviceAssociationObservable observable;

    @Mock
    EnvConfig<DeviceAssocationProperty> envConfig;

    @Mock
    HcpRestClientLibrary restClientLibrary;

    @Mock
    DeviceActivationService deviceActivationService;

    @Mock
    DeviceInfoSharedDao deviceInfoDao;

    @Mock
    KafkaDeviceNotificationObserver kafkaDeviceNotifier;


    @Before
    public void beforeEach() {
        initMocks(this);
    }


    @Test(expected = DuplicateDeviceAssociationRequestException.class)
    public void associateDeviceTest_Duplicate() throws Exception {

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setUserId("userId");
        associateDeviceRequest.setSerialNumber("12345");

        Mockito.doReturn(true).when(deviceAssociationDao)
            .isDeviceCurrentlyAssociatedToUser(Mockito.anyString(), Mockito.anyString());
        deviceAssociationService.associateDevice(associateDeviceRequest);
    }

    @Test
    public void associateDeviceTest_NullDeviceAssociation() throws Exception {

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setUserId("userId");
        associateDeviceRequest.setSerialNumber("12345");

        Mockito.doReturn(null).when(deviceAssociationDao).findValidAssociations(Mockito.anyString());
        Mockito.doReturn(false).when(deviceAssociationDao)
            .isDeviceCurrentlyAssociatedToUser(Mockito.anyString(), Mockito.anyString());
        deviceAssociationService.associateDevice(associateDeviceRequest);
        Assertions.assertNull(deviceAssociationDao.findValidAssociations(Mockito.anyString()));
    }

    @Test
    public void associateDeviceTest_Disassociated() throws Exception {

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setUserId("userId");
        associateDeviceRequest.setSerialNumber("12345");

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setUserId("userId");
        deviceAssociation.setAssociationStatus(AssociationStatus.DISASSOCIATED);

        deviceAssociationDao.insert(Mockito.any());
        deviceAssociationDao.insertDeviceState(Mockito.any());
        Mockito.doReturn(deviceAssociation).when(deviceAssociationDao).findValidAssociations(Mockito.anyString());
        Mockito.doReturn(false).when(deviceAssociationDao)
            .isDeviceCurrentlyAssociatedToUser(Mockito.anyString(), Mockito.anyString());
        AssociateDeviceResponse associateDeviceResponse =
                deviceAssociationService.associateDevice(associateDeviceRequest);
        Assertions.assertNotNull(associateDeviceResponse);
    }

    @Test
    public void associateDeviceTest() throws Exception {

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setUserId("userId");
        associateDeviceRequest.setSerialNumber("12345");

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setUserId("userId");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);

        deviceAssociationDao.insert(Mockito.any());
        deviceAssociationDao.insertDeviceState(Mockito.any());
        observable.notify(deviceAssociation);
        Mockito.doReturn(UPDATED_COUNT).when(deviceAssociationDao).updateDeviceAssociation(Mockito.any());
        Mockito.doReturn(deviceAssociation).when(deviceAssociationDao).findValidAssociations(Mockito.anyString());
        Mockito.doReturn(false).when(deviceAssociationDao)
            .isDeviceCurrentlyAssociatedToUser(Mockito.anyString(), Mockito.anyString());
        AssociateDeviceResponse associateDeviceResponse =
                deviceAssociationService.associateDevice(associateDeviceRequest);
        Assertions.assertNotNull(associateDeviceResponse);
    }

    @Test
    public void getAssociatedDevicesForUserTest() {

        List<DeviceAssociation> deviceAssociations = new ArrayList<>();

        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(Mockito.anyString());
        deviceAssociationService.getAssociatedDevicesForUser("userId");
        Assertions.assertEquals(deviceAssociations, deviceAssociationDao.fetchAssociatedDevices(Mockito.anyString()));
    }

    @Test
    public void getAssociationDetailsTest_NullDeviceAssociation() {

        Mockito.doReturn(null).when(deviceAssociationDao).find(Mockito.anyLong(), Mockito.anyString());
        DeviceAssociation deviceAssociation = deviceAssociationService.getAssociationDetails(ASSOCIATION_ID, "userId");
        Assertions.assertNull(deviceAssociation);
    }

    @Test
    public void getAssociationDetailsTest_NullHarmanId() {
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setHarmanId(null);

        Mockito.doReturn(deviceAssociation).when(deviceAssociationDao).find(Mockito.anyLong(), Mockito.anyString());
        DeviceAssociation deviceAssociations = deviceAssociationService.getAssociationDetails(ASSOCIATION_ID, "userId");
        Assertions.assertNull(deviceAssociations.getHarmanId());
    }

    @Test
    public void getAssociationDetailsTest_ResponseNotOk() {
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setHarmanId("harmanId");

        ResponseEntity<DeviceAttributes> responseEntity = new ResponseEntity<>(HttpStatus.ACCEPTED);

        Mockito.doReturn(responseEntity).when(restClientLibrary)
            .doGet(Mockito.anyString(), Mockito.any(), Mockito.eq(DeviceAttributes.class));

        Mockito.when(envConfig.getStringValue(Mockito.any())).thenReturn("baseURL", "deviceInfoPath");
        Mockito.doReturn(deviceAssociation).when(deviceAssociationDao).find(Mockito.anyLong(), Mockito.anyString());
        DeviceAssociation deviceAssociations = deviceAssociationService.getAssociationDetails(ASSOCIATION_ID, "userId");
        Assertions.assertNotNull(deviceAssociations);
    }

    @Test
    public void getAssociationDetailsTest_Exception() {
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setHarmanId("harmanId");

        ResponseEntity<DeviceAttributes> responseEntity = new ResponseEntity<>(HttpStatus.OK);

        Mockito.doReturn(responseEntity).when(restClientLibrary)
            .doGet(Mockito.anyString(), Mockito.any(), Mockito.eq(DeviceAttributes.class));
        Mockito.when(envConfig.getStringValue(Mockito.any())).thenReturn("baseURL", "deviceInfoPath");
        Mockito.doReturn(deviceAssociation).when(deviceAssociationDao).find(Mockito.anyLong(), Mockito.anyString());
        DeviceAssociation deviceAssociations = deviceAssociationService.getAssociationDetails(ASSOCIATION_ID, "userId");
        Assertions.assertNotNull(deviceAssociations);
    }

    @Test
    public void getImsiTest() {
        String imei = "12345";
        String imsi = "3456";
        Mockito.doReturn(imsi).when(deviceAssociationDao).getImsi(imei);
        deviceAssociationService.getImsi(imei);
        Assertions.assertEquals(imsi, deviceAssociationService.getImsi(imei));
    }

    @Test
    public void getRegionTest() {

        String countryCode = "TH";
        Mockito.doReturn(countryCode).when(deviceAssociationDao).getCountryCode(ASSOCIATION_ID);
        deviceAssociationService.getRegion(ASSOCIATION_ID);
        Assertions.assertEquals(countryCode, deviceAssociationService.getRegion(ASSOCIATION_ID));
    }

    @Test
    public void getActiveTranStatusTest() {

        String transStatus = "Active";
        Mockito.doReturn(transStatus).when(deviceAssociationDao).getActivateTranStatus(ASSOCIATION_ID);
        deviceAssociationService.getActiveTranStatus(ASSOCIATION_ID);
        Assertions.assertEquals(transStatus, deviceAssociationService.getActiveTranStatus(ASSOCIATION_ID));
    }

    @Test(expected = ApiPreConditionFailedException.class)
    public void wipeDevicesTest_NullUserId() throws Exception {

        List<String> serialNumbers = new ArrayList<>();
        serialNumbers.add("12345");

        deviceAssociationService.wipeDevices(null, serialNumbers);
    }

    @Test(expected = ApiPreConditionFailedException.class)
    public void wipeDevicesTest_DiffUserId() throws Exception {

        List<String> serialNumbers = new ArrayList<>();
        serialNumbers.add("12345");

        deviceAssociationService.wipeDevices("user\r\nId", serialNumbers);
    }

    @Test(expected = ApiPreConditionFailedException.class)
    public void wipeDevicesTest_ZeroDeviceAssociations() throws Exception {

        List<String> serialNumbers = new ArrayList<>();
        serialNumbers.add("12345");

        List<DeviceAssociation> deviceAssociations = new ArrayList<>();

        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(Mockito.anyString());
        deviceAssociationService.wipeDevices("userId", serialNumbers);
    }

    @Test(expected = ApiPreConditionFailedException.class)
    public void wipeDevicesTest_NullDeviceAssociations() throws Exception {

        List<String> serialNumbers = new ArrayList<>();
        serialNumbers.add("12345");

        Mockito.doReturn(null).when(deviceAssociationDao).fetchAssociatedDevices(Mockito.anyString());
        deviceAssociationService.wipeDevices("userId", serialNumbers);
    }

    @Test(expected = ApiPreConditionFailedException.class)
    public void wipeDevicesTest_InvalidStatus() throws Exception {

        List<String> serialNumbers = new ArrayList<>();
        serialNumbers.add("12345");

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setHarmanId("harmanId");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATION_INITIATED);

        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);

        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(Mockito.anyString());
        deviceAssociationService.wipeDevices("userId", serialNumbers);
    }

    @Test(expected = ApiPreConditionFailedException.class)
    public void wipeDevicesTest_EmptySerialNumbers() throws Exception {

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setHarmanId("harmanId");
        deviceAssociation.setSerialNumber("10000");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);

        List<DeviceAssociation> deviceAssociations = new ArrayList<>();

        List<String> serialNumbers = new ArrayList<>();
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(Mockito.anyString());
        deviceAssociationService.wipeDevices("userId", serialNumbers);
    }

    @Test(expected = ApiPreConditionFailedException.class)
    public void wipeDevicesTest_EmptyUniqueAssociations() throws Exception {

        List<String> serialNumbers = new ArrayList<>();
        serialNumbers.add("12345");

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setHarmanId("harmanId");
        deviceAssociation.setSerialNumber("1000");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);

        List<DeviceAssociation> deviceAssociations = new ArrayList<>();


        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(Mockito.anyString());
        deviceAssociationService.wipeDevices("userId", serialNumbers);
    }

    @Test
    public void wipeDevicesTest_NullType() throws Exception {

        ReflectionTestUtils.setField(deviceAssociationService, "defaultAssociationType", "defaultOwner");

        List<String> serialNumbers = new ArrayList<>();
        serialNumbers.add("12345");

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setHarmanId("harmanId");
        deviceAssociation.setSerialNumber("12345");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        deviceAssociation.setAssociationType(null);

        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);

        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setName("SW-Version");
        deviceInfo.setValue("value");
        List<DeviceInfo> deviceInfoList = new ArrayList<>();
        deviceInfoList.add(deviceInfo);

        ActivationResponse activationResponse = new ActivationResponse();
        activationResponse.setDeviceId("deviceId");

        Mockito.doReturn(UPDATED_COUNT).when(deviceAssociationDao)
            .updateUserIdWithDummyValue(Mockito.anyList(), Mockito.anyList());
        Mockito.doReturn(UPDATED_COUNT).when(deviceAssociationDao)
            .updateActivationStateWithDummy(Mockito.anyList(), Mockito.anyList());
        Mockito.doReturn(activationResponse).when(deviceActivationService).activateDevice(Mockito.any());
        Mockito.doReturn(deviceInfoList).when(deviceAssociationDao).findDeviceInfo(Mockito.anyString());

        AssociateDeviceResponse associateDeviceResponse =
            new AssociateDeviceResponse(ASSOCIATION_ID, AssociationStatus.ASSOCIATION_INITIATED);

        Mockito.doReturn(associateDeviceResponse).when(deviceAssocFactoryServiceV2)
            .associateDeviceForSelf(Mockito.any(), Mockito.eq(null));
        Mockito.doReturn(UPDATED_COUNT).when(deviceAssocFactoryServiceV2)
            .terminateM2Massociation(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean());
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(Mockito.anyString());
        List<String> devices = deviceAssociationService.wipeDevices("userId", serialNumbers);
        Assertions.assertNotNull(devices);
    }

    @Test(expected = ApiPreConditionFailedException.class)
    public void wipeDevicesTest_updatetoDummyValues() throws Exception {

        ReflectionTestUtils.setField(deviceAssociationService, "defaultAssociationType", "defaultOwner");

        List<String> serialNumbers = new ArrayList<>();
        serialNumbers.add("12345");

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setHarmanId("harmanId");
        deviceAssociation.setSerialNumber("12345");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        deviceAssociation.setAssociationType(null);

        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);

        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setName("SW-Version");
        deviceInfo.setValue("value");
        List<DeviceInfo> deviceInfoList = new ArrayList<>();
        deviceInfoList.add(deviceInfo);

        ActivationResponse activationResponse = new ActivationResponse();
        activationResponse.setDeviceId("deviceId");

        Mockito.doThrow(ApiPreConditionFailedException.class).when(deviceAssociationDao)
            .updateActivationStateWithDummy(Mockito.anyList(), Mockito.anyList());
        Mockito.doReturn(activationResponse).when(deviceActivationService).activateDevice(Mockito.any());
        Mockito.doReturn(deviceInfoList).when(deviceAssociationDao).findDeviceInfo(Mockito.anyString());

        AssociateDeviceResponse associateDeviceResponse =
            new AssociateDeviceResponse(ASSOCIATION_ID, AssociationStatus.ASSOCIATION_INITIATED);
        Mockito.doReturn(associateDeviceResponse).when(deviceAssocFactoryServiceV2)
            .associateDeviceForSelf(Mockito.any(), Mockito.eq(null));
        Mockito.doReturn(UPDATED_COUNT).when(deviceAssocFactoryServiceV2)
            .terminateM2Massociation(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean());
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(Mockito.anyString());
        deviceAssociationService.wipeDevices("userId", serialNumbers);
    }

    @Test
    public void wipeDevicesTest_HwVersion() throws Exception {

        ReflectionTestUtils.setField(deviceAssociationService, "defaultAssociationType", "defaultOwner");

        List<String> serialNumbers = new ArrayList<>();
        serialNumbers.add("12345");

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setHarmanId("harmanId");
        deviceAssociation.setSerialNumber("12345");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        deviceAssociation.setAssociationType(null);

        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);

        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setName("HW-Version");
        deviceInfo.setValue("value");
        List<DeviceInfo> deviceInfoList = new ArrayList<>();
        deviceInfoList.add(deviceInfo);

        ActivationResponse activationResponse = new ActivationResponse();
        activationResponse.setDeviceId("deviceId");

        AssociateDeviceResponse associateDeviceResponse =
            new AssociateDeviceResponse(ASSOCIATION_ID, AssociationStatus.ASSOCIATION_INITIATED);

        Mockito.doReturn(activationResponse).when(deviceActivationService).activateDevice(Mockito.any());
        Mockito.doReturn(deviceInfoList).when(deviceAssociationDao).findDeviceInfo(Mockito.anyString());
        Mockito.doReturn(associateDeviceResponse).when(deviceAssocFactoryServiceV2)
            .associateDeviceForSelf(Mockito.any(), Mockito.eq(null));
        Mockito.doReturn(UPDATED_COUNT).when(deviceAssocFactoryServiceV2)
            .terminateM2Massociation(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean());
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(Mockito.anyString());
        List<String> devices = deviceAssociationService.wipeDevices("userId", serialNumbers);
        Assertions.assertNotNull(devices);
    }

    @Test(expected = WipeDataFailureException.class)
    public void wipeDevicesTest_ActivationResponse() throws Exception {

        ReflectionTestUtils.setField(deviceAssociationService, "defaultAssociationType", "defaultOwner");

        List<String> serialNumbers = new ArrayList<>();
        serialNumbers.add("12345");

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setHarmanId("harmanId");
        deviceAssociation.setSerialNumber("12345");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        deviceAssociation.setAssociationType(null);

        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);

        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setName("HW-Version");
        deviceInfo.setValue("value");
        List<DeviceInfo> deviceInfoList = new ArrayList<>();
        deviceInfoList.add(deviceInfo);

        ActivationResponse activationResponse = new ActivationResponse();
        activationResponse.setDeviceId("deviceId");

        Mockito.doThrow(WipeDataFailureException.class).when(deviceActivationService).activateDevice(Mockito.any());
        Mockito.doReturn(deviceInfoList).when(deviceAssociationDao).findDeviceInfo(Mockito.anyString());

        AssociateDeviceResponse associateDeviceResponse =
            new AssociateDeviceResponse(ASSOCIATION_ID, AssociationStatus.ASSOCIATION_INITIATED);

        Mockito.doReturn(associateDeviceResponse).when(deviceAssocFactoryServiceV2)
            .associateDeviceForSelf(Mockito.any(), Mockito.eq(null));
        Mockito.doReturn(UPDATED_COUNT).when(deviceAssocFactoryServiceV2)
            .terminateM2Massociation(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean());
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(Mockito.anyString());
        deviceAssociationService.wipeDevices("userId", serialNumbers);
    }

    @Test(expected = WipeDataFailureException.class)
    public void wipeDevicesTest_ActivationResponse_snoLengthTest() throws Exception {

        ReflectionTestUtils.setField(deviceAssociationService, "defaultAssociationType", "defaultOwner");

        List<String> serialNumbers = new ArrayList<>();
        serialNumbers.add("1234");

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setHarmanId("harmanId");
        deviceAssociation.setSerialNumber("1234");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        deviceAssociation.setAssociationType(null);

        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setName("HW-Version");
        deviceInfo.setValue("value");
        List<DeviceInfo> deviceInfoList = new ArrayList<>();
        deviceInfoList.add(deviceInfo);

        ActivationResponse activationResponse = new ActivationResponse();
        activationResponse.setDeviceId("deviceId");

        Mockito.doThrow(WipeDataFailureException.class).when(deviceActivationService).activateDevice(Mockito.any());
        Mockito.doReturn(deviceInfoList).when(deviceAssociationDao).findDeviceInfo(Mockito.anyString());

        AssociateDeviceResponse associateDeviceResponse =
            new AssociateDeviceResponse(ASSOCIATION_ID, AssociationStatus.ASSOCIATION_INITIATED);
        Mockito.doReturn(associateDeviceResponse).when(deviceAssocFactoryServiceV2)
            .associateDeviceForSelf(Mockito.any(), Mockito.eq(null));
        Mockito.doReturn(UPDATED_COUNT).when(deviceAssocFactoryServiceV2)
            .terminateM2Massociation(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean());
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(Mockito.anyString());
        deviceAssociationService.wipeDevices("userId", serialNumbers);
    }

    @Test(expected = WipeDataFailureException.class)
    public void wipeDevicesTest_Null_WipeDataFailureException() throws Exception {

        ReflectionTestUtils.setField(deviceAssociationService, "defaultAssociationType", "defaultOwner");

        List<String> serialNumbers = new ArrayList<>();
        serialNumbers.add("12345");

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setHarmanId("harmanId");
        deviceAssociation.setSerialNumber("12345");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);

        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);
        deviceAssociation.setAssociationType(null);


        Mockito.doThrow(WipeDataFailureException.class).when(deviceAssocFactoryServiceV2)
            .terminateM2Massociation(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean());
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(Mockito.anyString());
        deviceAssociationService.wipeDevices("userId", serialNumbers);
    }

    @Test(expected = WipeDataFailureException.class)
    public void wipeDevicesTest_WipeDataFailureException() throws Exception {

        List<String> serialNumbers = new ArrayList<>();
        serialNumbers.add("12345");

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setHarmanId("harmanId");
        deviceAssociation.setSerialNumber("12345");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);

        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);
        deviceAssociation.setAssociationType(null);


        Mockito.doThrow(WipeDataFailureException.class).when(deviceAssociationDao)
            .fetchAssociatedDevices(Mockito.anyString());
        deviceAssociationService.wipeDevices("userId", serialNumbers);
    }

    @Test(expected = ApiPreConditionFailedException.class)
    public void wipeDevicesTest_ApiPreConditionFailedException() throws Exception {

        ReflectionTestUtils.setField(deviceAssociationService, "defaultAssociationType", "Owner");

        List<String> serialNumbers = new ArrayList<>();
        serialNumbers.add("12345");
        serialNumbers.add("123459");

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setHarmanId("harmanId");
        deviceAssociation.setSerialNumber("12345");
        deviceAssociation.setAssociationType("defaultOwner");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);

        DeviceAssociation deviceAssociation2 = new DeviceAssociation();
        deviceAssociation2.setHarmanId("harmanId2");
        deviceAssociation2.setSerialNumber("12345");
        deviceAssociation2.setAssociationType("Owner");
        deviceAssociation2.setAssociationStatus(AssociationStatus.ASSOCIATED);

        DeviceAssociation deviceAssociation1 = new DeviceAssociation();
        deviceAssociation1.setHarmanId("harmanId1");
        deviceAssociation1.setSerialNumber("123451");
        deviceAssociation.setAssociationType("defaultOwner");
        deviceAssociation1.setAssociationStatus(AssociationStatus.ASSOCIATED);

        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);
        deviceAssociations.add(deviceAssociation1);
        deviceAssociations.add(deviceAssociation2);


        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(Mockito.anyString());
        deviceAssociationService.wipeDevices("userId", serialNumbers);
    }

    @Test(expected = WipeDataFailureException.class)
    public void wipeDevicesTest_associateDevice() throws Exception {

        ReflectionTestUtils.setField(deviceAssociationService, "defaultAssociationType", "defaultOwner");

        List<String> serialNumbers = new ArrayList<>();
        serialNumbers.add("12345");

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setHarmanId("harmanId");
        deviceAssociation.setSerialNumber("12345");
        deviceAssociation.setUserId("userId@123");
        deviceAssociation.setAssociationType("defaultOwner");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);

        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);

        Mockito.doThrow(WipeDataFailureException.class).when(deviceAssocFactoryServiceV2)
            .associateDeviceForSelf(Mockito.any(), Mockito.eq(null));
        Mockito.doReturn(UPDATED_COUNT).when(deviceAssocFactoryServiceV2)
            .terminateM2Massociation(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean());
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).getAllM2Massociations(Mockito.anyString());
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(Mockito.anyString());
        deviceAssociationService.wipeDevices("userId@123", serialNumbers);
    }

    @Test(expected = WipeDataFailureException.class)
    public void wipeDevicesTest_associateDeviceMultipleM2M() throws Exception {

        ReflectionTestUtils.setField(deviceAssociationService, "defaultAssociationType", "defaultOwner");

        List<String> serialNumbers = new ArrayList<>();
        serialNumbers.add("12345");

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setHarmanId("harmanId");
        deviceAssociation.setSerialNumber("12345");
        deviceAssociation.setUserId("userId@123");
        deviceAssociation.setAssociationType("defaultOwner");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);

        DeviceAssociation deviceAssociation2 = new DeviceAssociation();
        deviceAssociation2.setHarmanId("harmanId2");
        deviceAssociation2.setSerialNumber("12345");
        deviceAssociation2.setAssociationType("Owner");
        deviceAssociation2.setUserId("userId@123");
        deviceAssociation2.setAssociationStatus(AssociationStatus.ASSOCIATED);

        DeviceAssociation deviceAssociation1 = new DeviceAssociation();
        deviceAssociation1.setHarmanId("harmanId1");
        deviceAssociation1.setSerialNumber("123451");
        deviceAssociation.setAssociationType("defaultOwner");
        deviceAssociation1.setUserId("userId@123");
        deviceAssociation1.setAssociationStatus(AssociationStatus.ASSOCIATED);

        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);
        deviceAssociations.add(deviceAssociation1);
        deviceAssociations.add(deviceAssociation2);

        Mockito.doThrow(WipeDataFailureException.class).when(deviceAssocFactoryServiceV2)
            .associateDeviceForSelf(Mockito.any(), Mockito.eq(null));
        Mockito.doReturn(UPDATED_COUNT).when(deviceAssocFactoryServiceV2)
            .terminateM2Massociation(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean());
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).getAllM2Massociations(Mockito.anyString());
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(Mockito.anyString());
        deviceAssociationService.wipeDevices("userId@123", serialNumbers);
    }

    @Test(expected = WipeDataFailureException.class)
    public void wipeDevicesTest_terminateAllSecondaryDevice() throws Exception {

        ReflectionTestUtils.setField(deviceAssociationService, "defaultAssociationType", "defaultOwner");

        List<String> serialNumbers = new ArrayList<>();
        serialNumbers.add("12345");

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setHarmanId("harmanId");
        deviceAssociation.setSerialNumber("12345");
        deviceAssociation.setUserId("userId@123");
        deviceAssociation.setAssociationType("Owner");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);

        DeviceAssociation deviceAssociation2 = new DeviceAssociation();
        deviceAssociation2.setHarmanId("harmanId2");
        deviceAssociation2.setSerialNumber("12345");
        deviceAssociation2.setAssociationType("rider");
        deviceAssociation2.setUserId("userId@123");
        deviceAssociation2.setAssociationStatus(AssociationStatus.ASSOCIATED);

        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);
        deviceAssociations.add(deviceAssociation2);

        Mockito.doThrow(WipeDataFailureException.class).when(deviceAssocFactoryServiceV2)
            .associateDeviceForSelf(Mockito.any(), Mockito.eq(null));
        Mockito.doReturn(UPDATED_COUNT).when(deviceAssocFactoryServiceV2)
            .terminateM2Massociation(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean());
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).getAllM2Massociations(Mockito.anyString());
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(Mockito.anyString());
        deviceAssociationService.wipeDevices("userId@123", serialNumbers);
    }

    @Test(expected = WipeDataFailureException.class)
    public void wipeDevicesTest_terminateAllSecondaryDeviceFailure() throws Exception {

        ReflectionTestUtils.setField(deviceAssociationService, "defaultAssociationType", "defaultOwner");

        List<String> serialNumbers = new ArrayList<>();
        serialNumbers.add("12345");

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setHarmanId("harmanId");
        deviceAssociation.setSerialNumber("12345");
        deviceAssociation.setUserId("userId@123");
        deviceAssociation.setAssociationType("Owner");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);

        DeviceAssociation deviceAssociation2 = new DeviceAssociation();
        deviceAssociation2.setHarmanId("harmanId2");
        deviceAssociation2.setSerialNumber("12345");
        deviceAssociation2.setAssociationType("rider");
        deviceAssociation2.setUserId("userId@123");
        deviceAssociation2.setAssociationStatus(AssociationStatus.ASSOCIATED);

        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);
        deviceAssociations.add(deviceAssociation2);

        Mockito.doThrow(WipeDataFailureException.class).when(deviceAssocFactoryServiceV2)
            .associateDeviceForSelf(Mockito.any(), Mockito.eq(null));
        Mockito.doReturn(UPDATED_COUNT).when(deviceAssocFactoryServiceV2)
            .terminateM2Massociation(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean());
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).getAllM2Massociations(Mockito.anyString());
        Mockito.doThrow(ApiPreConditionFailedException.class).when(deviceAssocFactoryServiceV2)
            .terminateM2Massociation(Mockito.any(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean());
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(Mockito.anyString());
        deviceAssociationService.wipeDevices("userId@123", serialNumbers);
    }

    @Test
    public void saveDeviceItemTest_ApiValidationFailedException() {

        ReflectionTestUtils.setField(deviceAssociationService, "supportedDeviceInfoRequestSize", "10");

        DeviceItem deviceItem = new DeviceItem();
        deviceItem.setValue("value");
        deviceItem.setName("name");
        ArrayList<DeviceItem> deviceItemList = new ArrayList<>();
        deviceItemList.add(deviceItem);

        ArrayList<DeviceItems> deviceItemsList = new ArrayList<>();
        DeviceItems deviceItems = new DeviceItems();
        deviceItems.setDeviceId("deviceId");
        deviceItems.setItems(deviceItemList);
        deviceItemsList.add(deviceItems);

        DeviceItemDto deviceItemDto = new DeviceItemDto();
        deviceItemDto.setData(deviceItemsList);

        Mockito.doReturn(false).when(deviceAssociationDao).associationByDeviceExists(Mockito.anyString());
        DeviceItemResult deviceItemResult = deviceAssociationService.saveDeviceItem(deviceItemDto);
        Assertions.assertNotNull(deviceItemResult);
    }

    @Test(expected = ApiValidationFailedException.class)
    public void saveDeviceItemTest_InvalidSupportedDeviceInfoRequestSize() {

        ReflectionTestUtils.setField(deviceAssociationService, "supportedDeviceInfoRequestSize", "0");

        DeviceItem deviceItem = new DeviceItem();
        deviceItem.setValue("value");
        deviceItem.setName("name");
        ArrayList<DeviceItem> deviceItemList = new ArrayList<>();
        deviceItemList.add(deviceItem);

        ArrayList<DeviceItems> deviceItemsList = new ArrayList<>();
        DeviceItems deviceItems = new DeviceItems();
        deviceItems.setDeviceId("deviceId");
        deviceItems.setItems(deviceItemList);
        deviceItemsList.add(deviceItems);

        DeviceItemDto deviceItemDto = new DeviceItemDto();
        deviceItemDto.setData(deviceItemsList);

        Mockito.doReturn(false).when(deviceAssociationDao).associationByDeviceExists(Mockito.anyString());
        deviceAssociationService.saveDeviceItem(deviceItemDto);
    }

    @Test
    public void saveDeviceItemTest_InvalidDeviceItems() {

        ReflectionTestUtils.setField(deviceAssociationService, "supportedDeviceInfoRequestSize", "10");

        DeviceItem deviceItem = new DeviceItem();
        deviceItem.setValue("value");
        deviceItem.setName("name");
        ArrayList<DeviceItem> deviceItemList = new ArrayList<>();
        deviceItemList.add(deviceItem);

        ArrayList<DeviceItems> deviceItemsList = new ArrayList<>();
        DeviceItems deviceItems = new DeviceItems();
        deviceItems.setDeviceId("deviceId");
        deviceItems.setItems(deviceItemList);
        deviceItemsList.add(deviceItems);

        DeviceItemDto deviceItemDto = new DeviceItemDto();
        deviceItemDto.setData(deviceItemsList);

        deviceInfoDao.updateDeviceInfo(Mockito.any(), Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn(true).when(deviceAssociationDao).associationByDeviceExists(Mockito.anyString());
        DeviceItemResult deviceItemResult = deviceAssociationService.saveDeviceItem(deviceItemDto);
        Assertions.assertNotNull(deviceItemResult);
    }

    @Test
    public void saveDeviceItemTest_InvalidDeviceInfoParams() {

        ReflectionTestUtils.setField(deviceAssociationService, "supportedDeviceInfoRequestSize", "10");
        ReflectionTestUtils.setField(deviceAssociationService, "deviceInfoParams", new String[]{"name2", "name1"});

        DeviceItem deviceItem = new DeviceItem();
        deviceItem.setValue("value");
        deviceItem.setName("name");
        ArrayList<DeviceItem> deviceItemList = new ArrayList<>();
        deviceItemList.add(deviceItem);

        ArrayList<DeviceItems> deviceItemsList = new ArrayList<>();
        DeviceItems deviceItems = new DeviceItems();
        deviceItems.setDeviceId("deviceId");
        deviceItems.setItems(deviceItemList);
        deviceItemsList.add(deviceItems);

        DeviceItemDto deviceItemDto = new DeviceItemDto();
        deviceItemDto.setData(deviceItemsList);

        deviceInfoDao.updateDeviceInfo(Mockito.any(), Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn(true).when(deviceAssociationDao).associationByDeviceExists(Mockito.anyString());
        DeviceItemResult deviceItemResult = deviceAssociationService.saveDeviceItem(deviceItemDto);
        Assertions.assertNotNull(deviceItemResult);
    }

    @Test
    public void saveDeviceItemTest() {

        ReflectionTestUtils.setField(deviceAssociationService, "supportedDeviceInfoRequestSize", "10");
        ReflectionTestUtils.setField(deviceAssociationService, "deviceInfoParams", new String[]{"name", "name1"});

        DeviceItem deviceItem = new DeviceItem();
        deviceItem.setValue("value");
        deviceItem.setName("name");
        ArrayList<DeviceItem> deviceItemList = new ArrayList<>();
        deviceItemList.add(deviceItem);

        ArrayList<DeviceItems> deviceItemsList = new ArrayList<>();
        DeviceItems deviceItems = new DeviceItems();
        deviceItems.setDeviceId("deviceId");
        deviceItems.setItems(deviceItemList);
        deviceItemsList.add(deviceItems);

        DeviceItemDto deviceItemDto = new DeviceItemDto();
        deviceItemDto.setData(deviceItemsList);

        deviceInfoDao.updateDeviceInfo(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        deviceInfoDao.updateDeviceInfo(Mockito.any(), Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn(true).when(deviceAssociationDao).associationByDeviceExists(Mockito.anyString());
        DeviceItemResult deviceItemResult = deviceAssociationService.saveDeviceItem(deviceItemDto);
        Assertions.assertNotNull(deviceItemResult);
    }

    @Test
    public void triggerKafkaEventTest() {

        TriggerKafkaEventRequestDto triggerKafkaEventRequestDto =
            new TriggerKafkaEventRequestDto(new org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceInfo(), "", "",
                "");

        kafkaDeviceNotifier.sendEventToKafka(Mockito.any());
        deviceAssociationService.triggerKafkaEvent(triggerKafkaEventRequestDto);
        Assertions.assertNotNull(triggerKafkaEventRequestDto);
    }


}
