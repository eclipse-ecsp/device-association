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

import org.eclipse.ecsp.common.config.EnvConfig;
import org.eclipse.ecsp.deviceassociation.dto.AssociationUpdateDto;
import org.eclipse.ecsp.deviceassociation.dto.AssociationUpdateRequest;
import org.eclipse.ecsp.deviceassociation.lib.config.DeviceAssocationProperty;
import org.eclipse.ecsp.deviceassociation.lib.dao.DeviceAssociationDao;
import org.eclipse.ecsp.deviceassociation.lib.exception.NoSuchEntityException;
import org.eclipse.ecsp.deviceassociation.lib.exception.ObserverMessageProcessFailureException;
import org.eclipse.ecsp.deviceassociation.lib.model.AssociationStatus;
import org.eclipse.ecsp.deviceassociation.lib.model.DeviceAssociation;
import org.eclipse.ecsp.deviceassociation.lib.model.DeviceAssociationHistory;
import org.eclipse.ecsp.deviceassociation.lib.model.SimDetails;
import org.eclipse.ecsp.deviceassociation.lib.model.wam.SimTransactionStatus;
import org.eclipse.ecsp.deviceassociation.lib.observer.DeviceAssociationObservable;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociateDeviceRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociateDeviceResponse;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociationDetailsResponse;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.CurrentDeviceDataPojo;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DelegateAssociationRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceStatusRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.FactoryData;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.ReplaceDeviceDataPojo;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.ReplaceFactoryDataRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.StateChangeRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.support.SpringAuthTokenGenerator;
import org.eclipse.ecsp.exception.shared.ApiNotificationException;
import org.eclipse.ecsp.exception.shared.ApiPreConditionFailedException;
import org.eclipse.ecsp.exception.shared.ApiResourceNotFoundException;
import org.eclipse.ecsp.exception.shared.ApiTechnicalException;
import org.eclipse.ecsp.exception.shared.ApiValidationFailedException;
import org.eclipse.ecsp.notification.lib.model.nc.UserProfile;
import org.eclipse.ecsp.notification.lib.rest.NotificationCenterClient;
import org.eclipse.ecsp.services.clientlib.HcpRestClientLibrary;
import org.eclipse.ecsp.services.device.dao.DeviceDao;
import org.eclipse.ecsp.services.device.model.Device;
import org.eclipse.ecsp.services.deviceactivation.dao.DeviceActivationStateDao;
import org.eclipse.ecsp.services.factorydata.dao.DeviceInfoFactoryDataDao;
import org.eclipse.ecsp.services.factorydata.domain.DeviceInfoFactoryData;
import org.eclipse.ecsp.services.shared.dao.HcpInfoDao;
import org.eclipse.ecsp.services.shared.db.HcpInfo;
import org.eclipse.ecsp.services.shared.deviceinfo.model.DeviceAttributes;
import org.eclipse.ecsp.services.shared.deviceinfo.model.DeviceInfo;
import org.eclipse.ecsp.springauth.client.rest.SpringAuthRestClient;
import org.eclipse.ecsp.userauth.lib.model.LoginResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import javax.naming.directory.InvalidAttributeValueException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for DeviceAssociationWithFactDataServiceV2.
 */
@RunWith(MockitoJUnitRunner.class)
public class DeviceAssociationWithFactDataServiceV2Test {

    public static final long ID = 1000L;
    public static final long ID_2 = 2000L;
    public static final int COUNT = 10;

    @InjectMocks
    DeviceAssociationWithFactDataServiceV2 deviceAssociationWithFactDataServiceV2;

    @Mock
    DeviceAssociationDao deviceAssociationDao;

    @Mock
    DeviceInfoFactoryDataDao deviceInfoFactoryDataDao;

    @Mock
    DeviceAssociationService deviceAssociationService;

    @Mock
    SimStateManager simStateManager;

    @Mock
    DeviceAssociationObservable observable;

    @Mock
    UserManagementClient userManagerService;

    @Mock
    EnvConfig<DeviceAssocationProperty> envConfig;

    @Mock
    HcpRestClientLibrary restClientLibrary;

    @Mock
    NotificationCenterClient ncClient;

    @Mock
    DeviceActivationStateDao deviceActivationStateDao;

    @Mock
    SpringAuthRestClient springAuthRestClient;

    @Mock
    HcpInfoDao hcpInfoDao;

    @Mock
    DeviceAssociationTypeService deviceAssociationTypeService;

    @Mock
    DeviceDao deviceDao;

    FactoryData factoryData1;
    DeviceStatusRequest deviceStatusRequest;
    CurrentDeviceDataPojo currentDeviceDataPojo;
    ReplaceDeviceDataPojo replaceDeviceDataPojo;
    HcpInfo hcpInfo;
    StateChangeRequest stateChangeRequest;
    @Mock
    private SpringAuthTokenGenerator springAuthTokenGenerator;

    /**
     * run before each test.
     */
    @Before
    public void beforeEach() {
        initMocks(this);

        stateChangeRequest = new StateChangeRequest();
        stateChangeRequest.setState("ACTIVE");
        stateChangeRequest.setImei("1234");
        stateChangeRequest.setDeviceId("deviceID");
        stateChangeRequest.setUserId("userID");

        factoryData1 = new FactoryData();
        factoryData1.setIccid("12345");
        factoryData1.setImei("12345");
        factoryData1.setSsid("12345");
        factoryData1.setMsisdn("12345");
        factoryData1.setImsi("12345");
        factoryData1.setBssid("12345");
        factoryData1.setManufacturingDate(new Timestamp(System.currentTimeMillis()));
        factoryData1.setModel("12345");
        factoryData1.setRecordDate(new Timestamp(System.currentTimeMillis()));
        factoryData1.setPlatformVersion("V1");
        factoryData1.setDeviceType("Dongle");
        factoryData1.setId(ID);

        deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setDeviceId("deviceId");
        deviceStatusRequest.setImei("1234");
        deviceStatusRequest.setSerialNumber("1234");
        deviceStatusRequest.setAssociationId(ID);
        deviceStatusRequest.setUserId("userID");
        deviceStatusRequest.setRequiredFor("WIPE");

        currentDeviceDataPojo = new CurrentDeviceDataPojo();
        currentDeviceDataPojo.setBssid("1234");
        currentDeviceDataPojo.setIccid("1234");
        currentDeviceDataPojo.setImsi("1234");
        currentDeviceDataPojo.setMsisdn("1234");
        currentDeviceDataPojo.setSerialNumber("1234");
        currentDeviceDataPojo.setImei("1234");

        replaceDeviceDataPojo = new ReplaceDeviceDataPojo();
        replaceDeviceDataPojo.setBssid("1234");
        replaceDeviceDataPojo.setIccid("1234");
        replaceDeviceDataPojo.setImsi("1234");
        replaceDeviceDataPojo.setMsisdn("1234");
        replaceDeviceDataPojo.setSerialNumber("1234");
        replaceDeviceDataPojo.setImei("1234");

        hcpInfo = new HcpInfo();
        hcpInfo.setHarmanId("Har123");
        hcpInfo.setFactoryId("12345");
    }

    @Test
    public void associateDeviceTest() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "forbidAssocAfterTerminate", false);

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setBssid("1234");
        associateDeviceRequest.setImei("12345");
        associateDeviceRequest.setSerialNumber("98765");

        FactoryData factoryData = new FactoryData();
        factoryData.setId(ID);
        factoryData.setFaulty(false);
        factoryData.setStolen(false);
        factoryData.setState("PROVISIONED");
        factoryData.setSerialNumber("12345");

        List<FactoryData> fetchFactoryData = new ArrayList<>();
        fetchFactoryData.add(factoryData);

        Mockito.doNothing().when(deviceAssociationDao).insert(Mockito.any());
        Mockito.doNothing().when(deviceAssociationDao).insertDeviceState(Mockito.any());
        Mockito.doNothing().when(deviceInfoFactoryDataDao)
            .changeDeviceState(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao).constructAndFetchFactoryData(Mockito.any());
        AssociateDeviceResponse associateDeviceResponse =
                deviceAssociationWithFactDataServiceV2.associateDevice(associateDeviceRequest);
        assertNotNull(associateDeviceResponse);
    }

    @Test
    public void associateDeviceTest_Provisioned_Alive() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "forbidAssocAfterTerminate", false);

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setBssid("1234");
        associateDeviceRequest.setImei("12345");
        associateDeviceRequest.setSerialNumber("98765");

        FactoryData factoryData = new FactoryData();
        factoryData.setId(ID);
        factoryData.setFaulty(false);
        factoryData.setStolen(false);
        factoryData.setState("PROVISIONED_ALIVE");
        factoryData.setSerialNumber("12345");

        List<FactoryData> fetchFactoryData = new ArrayList<>();
        fetchFactoryData.add(factoryData);

        Mockito.doNothing().when(deviceAssociationDao).insert(Mockito.any());
        Mockito.doNothing().when(deviceAssociationDao).insertDeviceState(Mockito.any());
        Mockito.doNothing().when(deviceInfoFactoryDataDao)
            .changeDeviceState(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao).constructAndFetchFactoryData(Mockito.any());
        AssociateDeviceResponse associateDeviceResponse =
                deviceAssociationWithFactDataServiceV2.associateDevice(associateDeviceRequest);
        assertNotNull(associateDeviceResponse);
    }

    @Test(expected = ApiValidationFailedException.class)
    public void associateDeviceTest_EmptyAssociateDeviceRequest() {

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setBssid("");
        associateDeviceRequest.setImei("");
        associateDeviceRequest.setSerialNumber("");

        deviceAssociationWithFactDataServiceV2.associateDevice(associateDeviceRequest);
    }

    //@Test(expected = ApiValidationFailedException.class)
    //public  void associateDeviceTest_NullAssociateDeviceRequest(){
    //
    //    AssociateDeviceRequest associateDeviceRequest = mock(AssociateDeviceRequest.class);
    //    Mockito.when(associateDeviceRequest.toString()).thenReturn(null);
    //
    //    deviceAssociationWithFactDataServiceV2.associateDevice(associateDeviceRequest);
    //}

    @Test(expected = ApiValidationFailedException.class)
    public void associateDeviceTest_Stolen_Or_Faulty() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "forbidAssocAfterTerminate", false);

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setBssid("1234");
        associateDeviceRequest.setImei("12345");
        associateDeviceRequest.setSerialNumber("98765");

        FactoryData factoryData = new FactoryData();
        factoryData.setId(ID);
        factoryData.setFaulty(false);
        factoryData.setStolen(false);
        factoryData.setState("STOLEN_OR_FAULTY");
        factoryData.setSerialNumber("12345");

        List<FactoryData> fetchFactoryData = new ArrayList<>();
        fetchFactoryData.add(factoryData);

        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao).constructAndFetchFactoryData(Mockito.any());
        deviceAssociationWithFactDataServiceV2.associateDevice(associateDeviceRequest);
    }

    @Test(expected = ApiValidationFailedException.class)
    public void associateDeviceTest_DefaultFetchState() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "forbidAssocAfterTerminate", false);

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setBssid("1234");
        associateDeviceRequest.setImei("12345");
        associateDeviceRequest.setSerialNumber("98765");

        FactoryData factoryData = new FactoryData();
        factoryData.setId(ID);
        factoryData.setFaulty(false);
        factoryData.setStolen(false);
        factoryData.setState("DEFAULT");
        factoryData.setSerialNumber("12345");

        List<FactoryData> fetchFactoryData = new ArrayList<>();
        fetchFactoryData.add(factoryData);

        List<AssociationDetailsResponse> associationDetailsResponseList = new ArrayList<>();
        AssociationDetailsResponse associationDetailsResponse = new AssociationDetailsResponse();
        associationDetailsResponse.setSerialNumber("98765");
        associationDetailsResponse.setUserId("user@ID");
        associationDetailsResponseList.add(associationDetailsResponse);

        Mockito.doReturn(associationDetailsResponseList).when(deviceAssociationDao)
            .fetchAssociationDetails(Mockito.any(), Mockito.anyBoolean());
        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao).constructAndFetchFactoryData(Mockito.any());
        deviceAssociationWithFactDataServiceV2.associateDevice(associateDeviceRequest);
    }

    @Test(expected = ApiValidationFailedException.class)
    public void associateDeviceTest_DefaultFetchState_UserId() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "forbidAssocAfterTerminate", false);

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setBssid("1234");
        associateDeviceRequest.setImei("12345");
        associateDeviceRequest.setSerialNumber("98765");

        FactoryData factoryData = new FactoryData();
        factoryData.setId(ID);
        factoryData.setFaulty(false);
        factoryData.setStolen(false);
        factoryData.setState("DEFAULT");
        factoryData.setSerialNumber("12345");

        List<FactoryData> fetchFactoryData = new ArrayList<>();
        fetchFactoryData.add(factoryData);

        List<AssociationDetailsResponse> associationDetailsResponseList = new ArrayList<>();
        AssociationDetailsResponse associationDetailsResponse = new AssociationDetailsResponse();
        associationDetailsResponse.setSerialNumber("98765");
        associationDetailsResponse.setUserId("userID");
        associationDetailsResponseList.add(associationDetailsResponse);

        Mockito.doReturn(associationDetailsResponseList).when(deviceAssociationDao)
            .fetchAssociationDetails(Mockito.any(), Mockito.anyBoolean());
        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao).constructAndFetchFactoryData(Mockito.any());
        deviceAssociationWithFactDataServiceV2.associateDevice(associateDeviceRequest);
    }

    @Test
    public void associateDeviceTest_FalseIsTerminated() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "forbidAssocAfterTerminate", true);

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setBssid("1234");
        associateDeviceRequest.setImei("12345");
        associateDeviceRequest.setSerialNumber("98765");

        FactoryData factoryData = new FactoryData();
        factoryData.setId(ID);
        factoryData.setFaulty(false);
        factoryData.setStolen(false);
        factoryData.setState("PROVISIONED");
        factoryData.setSerialNumber("12345");

        List<FactoryData> fetchFactoryData = new ArrayList<>();
        fetchFactoryData.add(factoryData);

        Mockito.doNothing().when(deviceAssociationDao).insert(Mockito.any());
        Mockito.doNothing().when(deviceAssociationDao).insertDeviceState(Mockito.any());
        Mockito.doNothing().when(deviceInfoFactoryDataDao)
            .changeDeviceState(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn(false).when(deviceAssociationDao).isDeviceTerminated(Mockito.anyLong());
        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao).constructAndFetchFactoryData(Mockito.any());
        AssociateDeviceResponse associateDeviceResponse =
                deviceAssociationWithFactDataServiceV2.associateDevice(associateDeviceRequest);
        assertNotNull(associateDeviceResponse);
    }

    @Test(expected = ApiPreConditionFailedException.class)
    public void associateDeviceTest_TrueIsTerminated() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "forbidAssocAfterTerminate", true);

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setBssid("1234");
        associateDeviceRequest.setImei("12345");
        associateDeviceRequest.setSerialNumber("98765");

        FactoryData factoryData = new FactoryData();
        factoryData.setId(ID);
        factoryData.setFaulty(false);
        factoryData.setStolen(false);
        factoryData.setState("PROVISIONED");
        factoryData.setSerialNumber("12345");

        List<FactoryData> fetchFactoryData = new ArrayList<>();
        fetchFactoryData.add(factoryData);

        Mockito.doReturn(true).when(deviceAssociationDao).isDeviceTerminated(Mockito.anyLong());
        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao).constructAndFetchFactoryData(Mockito.any());
        deviceAssociationWithFactDataServiceV2.associateDevice(associateDeviceRequest);
    }


    @Test(expected = ApiValidationFailedException.class)
    public void associateDeviceTest_TrueIsFaulty() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "forbidAssocAfterTerminate", true);

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setBssid("1234");
        associateDeviceRequest.setImei("12345");
        associateDeviceRequest.setSerialNumber("98765");

        FactoryData factoryData = new FactoryData();
        factoryData.setId(ID);
        factoryData.setFaulty(true);
        factoryData.setStolen(false);
        factoryData.setState("PROVISIONED");
        factoryData.setSerialNumber("12345");

        List<FactoryData> fetchFactoryData = new ArrayList<>();
        fetchFactoryData.add(factoryData);

        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao).constructAndFetchFactoryData(Mockito.any());
        deviceAssociationWithFactDataServiceV2.associateDevice(associateDeviceRequest);
    }

    @Test(expected = ApiValidationFailedException.class)
    public void associateDeviceTest_TrueIsStolen() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "forbidAssocAfterTerminate", true);

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setBssid("1234");
        associateDeviceRequest.setImei("12345");
        associateDeviceRequest.setSerialNumber("98765");

        FactoryData factoryData = new FactoryData();
        factoryData.setId(ID);
        factoryData.setFaulty(false);
        factoryData.setStolen(true);
        factoryData.setState("PROVISIONED");
        factoryData.setSerialNumber("12345");

        List<FactoryData> fetchFactoryData = new ArrayList<>();
        fetchFactoryData.add(factoryData);

        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao).constructAndFetchFactoryData(Mockito.any());
        deviceAssociationWithFactDataServiceV2.associateDevice(associateDeviceRequest);
    }

    @Test(expected = ApiValidationFailedException.class)
    public void associateDeviceTest_ApiValidationFailedException() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "forbidAssocAfterTerminate", true);

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setBssid("1234");
        associateDeviceRequest.setImei("12345");
        associateDeviceRequest.setSerialNumber("98765");

        FactoryData factoryData = new FactoryData();
        factoryData.setId(0L);
        factoryData.setFaulty(false);
        factoryData.setStolen(false);
        factoryData.setState("PROVISIONED");
        factoryData.setSerialNumber("12345");

        List<FactoryData> fetchFactoryData = new ArrayList<>();
        fetchFactoryData.add(factoryData);

        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao).constructAndFetchFactoryData(Mockito.any());
        deviceAssociationWithFactDataServiceV2.associateDevice(associateDeviceRequest);
    }

    @Test
    public void getAssociatedDevicesForUserTest_FalseVinAssocEnabled() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "vinAssocEnabled", false);
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "supportedDeviceItems",
            new String[]{"userVehicleAuthStatus"});

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setFactoryId(ID);
        deviceAssociation.setIccid(factoryData1.getIccid());
        deviceAssociation.setImei(factoryData1.getImei());
        deviceAssociation.setSsid(factoryData1.getSsid());
        deviceAssociation.setMsisdn(factoryData1.getMsisdn());
        deviceAssociation.setImsi(factoryData1.getImsi());
        deviceAssociation.setBssid(factoryData1.getBssid());
        deviceAssociation.setManufacturingDate(factoryData1.getManufacturingDate());
        deviceAssociation.setModel(factoryData1.getModel());
        deviceAssociation.setRecordDate(factoryData1.getRecordDate());
        deviceAssociation.setPlatformVersion(factoryData1.getPlatformVersion());
        deviceAssociation.setDeviceType(factoryData1.getDeviceType());
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);

        List<DeviceInfo> deviceItems = new ArrayList<>();
        DeviceInfo deviceInfo = new DeviceInfo("HarID", "name", "value");
        deviceItems.add(deviceInfo);

        List<FactoryData> data = new ArrayList<>();
        data.add(factoryData1);

        Mockito.doReturn(data).when(deviceAssociationDao).fetchFactoryData(Mockito.anyLong());
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(Mockito.anyString());
        List<DeviceAssociation> deviceAssociationList =
                deviceAssociationWithFactDataServiceV2.getAssociatedDevicesForUser("userID");
        assertNotNull(deviceAssociationList);
    }

    @Test
    public void getAssociatedDevicesForUserTest_AssociationStatus() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "vinAssocEnabled", false);
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "supportedDeviceItems",
            new String[]{"userVehicleAuthStatus"});

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setFactoryId(ID);
        deviceAssociation.setIccid(factoryData1.getIccid());
        deviceAssociation.setImei(factoryData1.getImei());
        deviceAssociation.setSsid(factoryData1.getSsid());
        deviceAssociation.setMsisdn(factoryData1.getMsisdn());
        deviceAssociation.setImsi(factoryData1.getImsi());
        deviceAssociation.setBssid(factoryData1.getBssid());
        deviceAssociation.setManufacturingDate(factoryData1.getManufacturingDate());
        deviceAssociation.setModel(factoryData1.getModel());
        deviceAssociation.setRecordDate(factoryData1.getRecordDate());
        deviceAssociation.setPlatformVersion(factoryData1.getPlatformVersion());
        deviceAssociation.setDeviceType(factoryData1.getDeviceType());
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATION_INITIATED);
        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);

        List<DeviceInfo> deviceItems = new ArrayList<>();
        DeviceInfo deviceInfo = new DeviceInfo("HarID", "name", "value");
        deviceItems.add(deviceInfo);

        List<FactoryData> data = new ArrayList<>();
        data.add(factoryData1);

        Mockito.doReturn(data).when(deviceAssociationDao).fetchFactoryData(Mockito.anyLong());
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(Mockito.anyString());
        List<DeviceAssociation> deviceAssociationList =
                deviceAssociationWithFactDataServiceV2.getAssociatedDevicesForUser("userID");
        assertNotNull(deviceAssociationList);
    }

    @Test
    public void getAssociatedDevicesForUserTest_TrueVinAssocEnabled_NullVin() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "vinAssocEnabled", false);
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "supportedDeviceItems",
            new String[]{"userVehicleAuthStatus"});

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setFactoryId(ID);
        deviceAssociation.setIccid(factoryData1.getIccid());
        deviceAssociation.setImei(factoryData1.getImei());
        deviceAssociation.setSsid(factoryData1.getSsid());
        deviceAssociation.setMsisdn(factoryData1.getMsisdn());
        deviceAssociation.setImsi(factoryData1.getImsi());
        deviceAssociation.setBssid(factoryData1.getBssid());
        deviceAssociation.setManufacturingDate(factoryData1.getManufacturingDate());
        deviceAssociation.setModel(factoryData1.getModel());
        deviceAssociation.setRecordDate(factoryData1.getRecordDate());
        deviceAssociation.setPlatformVersion(factoryData1.getPlatformVersion());
        deviceAssociation.setDeviceType(factoryData1.getDeviceType());
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);

        List<DeviceInfo> deviceItems = new ArrayList<>();
        DeviceInfo deviceInfo = new DeviceInfo("HarID", "name", "value");
        deviceItems.add(deviceInfo);

        List<FactoryData> data = new ArrayList<>();
        data.add(factoryData1);

        Mockito.doReturn(data).when(deviceAssociationDao).fetchFactoryData(Mockito.anyLong());
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(Mockito.anyString());
        List<DeviceAssociation> deviceAssociationList =
                deviceAssociationWithFactDataServiceV2.getAssociatedDevicesForUser("userID");
        assertNotNull(deviceAssociationList);
    }

    @Test
    public void getAssociatedDevicesForUserTest_TrueVinAssocEnabled_NullVin_AssociationStatus() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "vinAssocEnabled", false);
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "supportedDeviceItems",
            new String[]{"userVehicleAuthStatus"});

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setFactoryId(ID);
        deviceAssociation.setIccid(factoryData1.getIccid());
        deviceAssociation.setImei(factoryData1.getImei());
        deviceAssociation.setSsid(factoryData1.getSsid());
        deviceAssociation.setMsisdn(factoryData1.getMsisdn());
        deviceAssociation.setImsi(factoryData1.getImsi());
        deviceAssociation.setBssid(factoryData1.getBssid());
        deviceAssociation.setManufacturingDate(factoryData1.getManufacturingDate());
        deviceAssociation.setModel(factoryData1.getModel());
        deviceAssociation.setRecordDate(factoryData1.getRecordDate());
        deviceAssociation.setPlatformVersion(factoryData1.getPlatformVersion());
        deviceAssociation.setDeviceType(factoryData1.getDeviceType());
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATION_FAILED);
        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);

        List<DeviceInfo> deviceItems = new ArrayList<>();
        DeviceInfo deviceInfo = new DeviceInfo("HarID", "name", "value");
        deviceItems.add(deviceInfo);

        List<FactoryData> data = new ArrayList<>();
        data.add(factoryData1);

        Mockito.doReturn(data).when(deviceAssociationDao).fetchFactoryData(Mockito.anyLong());
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(Mockito.anyString());
        List<DeviceAssociation> deviceAssociationList =
                deviceAssociationWithFactDataServiceV2.getAssociatedDevicesForUser("userID");
        assertNotNull(deviceAssociationList);
    }

    @Test
    public void getAssociatedDevicesForUserTest_InProgressSameStatus() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "vinAssocEnabled", true);
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "supportedDeviceItems",
            new String[]{"userVehicleAuthStatus"});

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setFactoryId(ID);
        deviceAssociation.setIccid(factoryData1.getIccid());
        deviceAssociation.setImei(factoryData1.getImei());
        deviceAssociation.setSsid(factoryData1.getSsid());
        deviceAssociation.setMsisdn(factoryData1.getMsisdn());
        deviceAssociation.setImsi(factoryData1.getImsi());
        deviceAssociation.setBssid(factoryData1.getBssid());
        deviceAssociation.setManufacturingDate(factoryData1.getManufacturingDate());
        deviceAssociation.setModel(factoryData1.getModel());
        deviceAssociation.setRecordDate(factoryData1.getRecordDate());
        deviceAssociation.setPlatformVersion(factoryData1.getPlatformVersion());
        deviceAssociation.setDeviceType(factoryData1.getDeviceType());
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);

        List<DeviceInfo> deviceItems = new ArrayList<>();
        DeviceInfo deviceInfo = new DeviceInfo("HarID", "name", "value");
        deviceItems.add(deviceInfo);

        List<FactoryData> data = new ArrayList<>();
        data.add(factoryData1);

        SimDetails simDetails = new SimDetails();
        simDetails.setTranId("TransId");
        simDetails.setTranStatus(SimTransactionStatus.IN_PROGRESS.getSimTransactionStatus());

        SimTransactionStatusDto simTransactionStatus = new SimTransactionStatusDto();
        simTransactionStatus.setStatus(SimTransactionStatus.IN_PROGRESS.getSimTransactionStatus());

        Mockito.doReturn(simTransactionStatus).when(simStateManager)
            .pollTransactionStatus(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn("Th").when(deviceAssociationService).getRegion(Mockito.anyLong());
        Mockito.doReturn(simDetails).when(deviceAssociationDao).findLatestSimTransactionStatus(Mockito.anyLong());
        Mockito.doReturn("VinValue123445").when(deviceAssociationDao).getAssociatedVin(Mockito.any());
        Mockito.doReturn(data).when(deviceAssociationDao).fetchFactoryData(Mockito.anyLong());
        Mockito.doReturn(deviceItems).when(deviceAssociationDao).findDeviceInfoByName(Mockito.any(), Mockito.any());
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(Mockito.anyString());
        List<DeviceAssociation> deviceAssociationList =
                deviceAssociationWithFactDataServiceV2.getAssociatedDevicesForUser("userID");
        assertNotNull(deviceAssociationList);
    }

    @Test
    public void getAssociatedDevicesForUserTest_InProgressSameStatus_InitiatedAssociation() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "vinAssocEnabled", true);
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "supportedDeviceItems",
            new String[]{"userVehicleAuthStatus1"});

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setFactoryId(ID);
        deviceAssociation.setIccid(factoryData1.getIccid());
        deviceAssociation.setImei(factoryData1.getImei());
        deviceAssociation.setSsid(factoryData1.getSsid());
        deviceAssociation.setMsisdn(factoryData1.getMsisdn());
        deviceAssociation.setImsi(factoryData1.getImsi());
        deviceAssociation.setBssid(factoryData1.getBssid());
        deviceAssociation.setManufacturingDate(factoryData1.getManufacturingDate());
        deviceAssociation.setModel(factoryData1.getModel());
        deviceAssociation.setRecordDate(factoryData1.getRecordDate());
        deviceAssociation.setPlatformVersion(factoryData1.getPlatformVersion());
        deviceAssociation.setDeviceType(factoryData1.getDeviceType());
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATION_INITIATED);
        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);

        List<DeviceInfo> deviceItems = new ArrayList<>();
        DeviceInfo deviceInfo = new DeviceInfo("HarID", "name", "value");
        deviceItems.add(deviceInfo);

        List<FactoryData> data = new ArrayList<>();
        data.add(factoryData1);

        SimDetails simDetails = new SimDetails();
        simDetails.setTranId("TransId");
        simDetails.setTranStatus(SimTransactionStatus.IN_PROGRESS.getSimTransactionStatus());

        SimTransactionStatusDto simTransactionStatus = new SimTransactionStatusDto();
        simTransactionStatus.setStatus(SimTransactionStatus.IN_PROGRESS.getSimTransactionStatus());

        Mockito.doReturn(simTransactionStatus).when(simStateManager)
            .pollTransactionStatus(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn("Th").when(deviceAssociationService).getRegion(Mockito.anyLong());
        Mockito.doReturn(simDetails).when(deviceAssociationDao).findLatestSimTransactionStatus(Mockito.anyLong());
        Mockito.doReturn("VinValue123445").when(deviceAssociationDao).getAssociatedVin(Mockito.any());
        Mockito.doReturn(data).when(deviceAssociationDao).fetchFactoryData(Mockito.anyLong());
        Mockito.doReturn(deviceItems).when(deviceAssociationDao).findDeviceInfoByName(Mockito.any(), Mockito.any());
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(Mockito.anyString());
        List<DeviceAssociation> deviceAssociationList =
                deviceAssociationWithFactDataServiceV2.getAssociatedDevicesForUser("userID");
        assertNotNull(deviceAssociationList);
    }

    @Test
    public void getAssociatedDevicesForUserTest_InProgressDiffStatus() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "vinAssocEnabled", true);
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "supportedDeviceItems",
            new String[]{"userVehicleAuthStatus"});

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setFactoryId(ID);
        deviceAssociation.setIccid(factoryData1.getIccid());
        deviceAssociation.setImei(factoryData1.getImei());
        deviceAssociation.setSsid(factoryData1.getSsid());
        deviceAssociation.setMsisdn(factoryData1.getMsisdn());
        deviceAssociation.setImsi(factoryData1.getImsi());
        deviceAssociation.setBssid(factoryData1.getBssid());
        deviceAssociation.setManufacturingDate(factoryData1.getManufacturingDate());
        deviceAssociation.setModel(factoryData1.getModel());
        deviceAssociation.setRecordDate(factoryData1.getRecordDate());
        deviceAssociation.setPlatformVersion(factoryData1.getPlatformVersion());
        deviceAssociation.setDeviceType(factoryData1.getDeviceType());
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);

        List<DeviceInfo> deviceItems = new ArrayList<>();
        DeviceInfo deviceInfo = new DeviceInfo("HarID", "name", "value");
        deviceItems.add(deviceInfo);

        List<FactoryData> data = new ArrayList<>();
        data.add(factoryData1);

        SimDetails simDetails = new SimDetails();
        simDetails.setTranId("TransId");
        simDetails.setTranStatus(SimTransactionStatus.IN_PROGRESS.getSimTransactionStatus());

        SimTransactionStatusDto simTransactionStatus = new SimTransactionStatusDto();
        simTransactionStatus.setStatus(SimTransactionStatus.PENDING.getSimTransactionStatus());

        Mockito.doReturn(simTransactionStatus).when(simStateManager)
            .pollTransactionStatus(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn("Th").when(deviceAssociationService).getRegion(Mockito.anyLong());
        Mockito.doReturn(simDetails).when(deviceAssociationDao).findLatestSimTransactionStatus(Mockito.anyLong());
        Mockito.doReturn("VinValue123445").when(deviceAssociationDao).getAssociatedVin(Mockito.any());
        Mockito.doReturn(data).when(deviceAssociationDao).fetchFactoryData(Mockito.anyLong());
        Mockito.doReturn(deviceItems).when(deviceAssociationDao).findDeviceInfoByName(Mockito.any(), Mockito.any());
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(Mockito.anyString());
        List<DeviceAssociation> deviceAssociationList =
                deviceAssociationWithFactDataServiceV2.getAssociatedDevicesForUser("userID");
        assertNotNull(deviceAssociationList);
    }

    @Test
    public void getAssociatedDevicesForUserTest_PendingSameStatus() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "vinAssocEnabled", true);
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "supportedDeviceItems",
            new String[]{"userVehicleAuthStatus"});

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setFactoryId(ID);
        deviceAssociation.setIccid(factoryData1.getIccid());
        deviceAssociation.setImei(factoryData1.getImei());
        deviceAssociation.setSsid(factoryData1.getSsid());
        deviceAssociation.setMsisdn(factoryData1.getMsisdn());
        deviceAssociation.setImsi(factoryData1.getImsi());
        deviceAssociation.setBssid(factoryData1.getBssid());
        deviceAssociation.setManufacturingDate(factoryData1.getManufacturingDate());
        deviceAssociation.setModel(factoryData1.getModel());
        deviceAssociation.setRecordDate(factoryData1.getRecordDate());
        deviceAssociation.setPlatformVersion(factoryData1.getPlatformVersion());
        deviceAssociation.setDeviceType(factoryData1.getDeviceType());
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);

        List<DeviceInfo> deviceItems = new ArrayList<>();
        DeviceInfo deviceInfo = new DeviceInfo("HarID", "name", "value");
        deviceItems.add(deviceInfo);

        List<FactoryData> data = new ArrayList<>();
        data.add(factoryData1);

        SimDetails simDetails = new SimDetails();
        simDetails.setTranId("TransId");
        simDetails.setTranStatus(SimTransactionStatus.PENDING.getSimTransactionStatus());

        SimTransactionStatusDto simTransactionStatus = new SimTransactionStatusDto();
        simTransactionStatus.setStatus(SimTransactionStatus.PENDING.getSimTransactionStatus());

        Mockito.doReturn(simTransactionStatus).when(simStateManager)
            .pollTransactionStatus(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn("Th").when(deviceAssociationService).getRegion(Mockito.anyLong());
        Mockito.doReturn(simDetails).when(deviceAssociationDao).findLatestSimTransactionStatus(Mockito.anyLong());
        Mockito.doReturn("VinValue123445").when(deviceAssociationDao).getAssociatedVin(Mockito.any());
        Mockito.doReturn(data).when(deviceAssociationDao).fetchFactoryData(Mockito.anyLong());
        Mockito.doReturn(deviceItems).when(deviceAssociationDao).findDeviceInfoByName(Mockito.any(), Mockito.any());
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(Mockito.anyString());
        List<DeviceAssociation> deviceAssociationList =
                deviceAssociationWithFactDataServiceV2.getAssociatedDevicesForUser("userID");
        assertNotNull(deviceAssociationList);
    }

    @Test
    public void getAssociatedDevicesForUserTest_PendingDiffStatus() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "vinAssocEnabled", true);
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "supportedDeviceItems",
            new String[]{"userVehicleAuthStatus"});

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setFactoryId(ID);
        deviceAssociation.setIccid(factoryData1.getIccid());
        deviceAssociation.setImei(factoryData1.getImei());
        deviceAssociation.setSsid(factoryData1.getSsid());
        deviceAssociation.setMsisdn(factoryData1.getMsisdn());
        deviceAssociation.setImsi(factoryData1.getImsi());
        deviceAssociation.setBssid(factoryData1.getBssid());
        deviceAssociation.setManufacturingDate(factoryData1.getManufacturingDate());
        deviceAssociation.setModel(factoryData1.getModel());
        deviceAssociation.setRecordDate(factoryData1.getRecordDate());
        deviceAssociation.setPlatformVersion(factoryData1.getPlatformVersion());
        deviceAssociation.setDeviceType(factoryData1.getDeviceType());
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);

        List<DeviceInfo> deviceItems = new ArrayList<>();
        DeviceInfo deviceInfo = new DeviceInfo("HarID", "name", "value");
        deviceItems.add(deviceInfo);

        List<FactoryData> data = new ArrayList<>();
        data.add(factoryData1);

        SimDetails simDetails = new SimDetails();
        simDetails.setTranId("TransId");
        simDetails.setTranStatus(SimTransactionStatus.PENDING.getSimTransactionStatus());

        SimTransactionStatusDto simTransactionStatus = new SimTransactionStatusDto();
        simTransactionStatus.setStatus(SimTransactionStatus.IN_PROGRESS.getSimTransactionStatus());

        Mockito.doReturn(simTransactionStatus).when(simStateManager)
            .pollTransactionStatus(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn("Th").when(deviceAssociationService).getRegion(Mockito.anyLong());
        Mockito.doReturn(simDetails).when(deviceAssociationDao).findLatestSimTransactionStatus(Mockito.anyLong());
        Mockito.doReturn("VinValue123445").when(deviceAssociationDao).getAssociatedVin(Mockito.any());
        Mockito.doReturn(data).when(deviceAssociationDao).fetchFactoryData(Mockito.anyLong());
        Mockito.doReturn(deviceItems).when(deviceAssociationDao).findDeviceInfoByName(Mockito.any(), Mockito.any());
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(Mockito.anyString());
        List<DeviceAssociation> deviceAssociationList =
                deviceAssociationWithFactDataServiceV2.getAssociatedDevicesForUser("userID");
        assertNotNull(deviceAssociationList);
    }

    @Test
    public void getAssociatedDevicesForUserTest_FailedSameStatus() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "vinAssocEnabled", true);
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "supportedDeviceItems",
            new String[]{"userVehicleAuthStatus"});

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setFactoryId(ID);
        deviceAssociation.setIccid(factoryData1.getIccid());
        deviceAssociation.setImei(factoryData1.getImei());
        deviceAssociation.setSsid(factoryData1.getSsid());
        deviceAssociation.setMsisdn(factoryData1.getMsisdn());
        deviceAssociation.setImsi(factoryData1.getImsi());
        deviceAssociation.setBssid(factoryData1.getBssid());
        deviceAssociation.setManufacturingDate(factoryData1.getManufacturingDate());
        deviceAssociation.setModel(factoryData1.getModel());
        deviceAssociation.setRecordDate(factoryData1.getRecordDate());
        deviceAssociation.setPlatformVersion(factoryData1.getPlatformVersion());
        deviceAssociation.setDeviceType(factoryData1.getDeviceType());
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);

        List<DeviceInfo> deviceItems = new ArrayList<>();
        DeviceInfo deviceInfo = new DeviceInfo("HarID", "name", "value");
        deviceItems.add(deviceInfo);

        List<FactoryData> data = new ArrayList<>();
        data.add(factoryData1);

        SimDetails simDetails = new SimDetails();
        simDetails.setTranId("TransId");
        simDetails.setTranStatus(SimTransactionStatus.FAILED.getSimTransactionStatus());

        SimTransactionStatusDto simTransactionStatus = new SimTransactionStatusDto();
        simTransactionStatus.setStatus(SimTransactionStatus.FAILED.getSimTransactionStatus());

        Mockito.doReturn(simTransactionStatus).when(simStateManager)
            .pollTransactionStatus(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn("Th").when(deviceAssociationService).getRegion(Mockito.anyLong());
        Mockito.doReturn(simDetails).when(deviceAssociationDao).findLatestSimTransactionStatus(Mockito.anyLong());
        Mockito.doReturn("VinValue123445").when(deviceAssociationDao).getAssociatedVin(Mockito.any());
        Mockito.doReturn(data).when(deviceAssociationDao).fetchFactoryData(Mockito.anyLong());
        Mockito.doReturn(deviceItems).when(deviceAssociationDao).findDeviceInfoByName(Mockito.any(), Mockito.any());
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(Mockito.anyString());
        List<DeviceAssociation> deviceAssociationList =
                deviceAssociationWithFactDataServiceV2.getAssociatedDevicesForUser("userID");
        assertNotNull(deviceAssociationList);
    }

    @Test
    public void getAssociatedDevicesForUserTest_FailedDiffStatus() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "vinAssocEnabled", true);
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "supportedDeviceItems",
            new String[]{"userVehicleAuthStatus"});

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setFactoryId(ID);
        deviceAssociation.setIccid(factoryData1.getIccid());
        deviceAssociation.setImei(factoryData1.getImei());
        deviceAssociation.setSsid(factoryData1.getSsid());
        deviceAssociation.setMsisdn(factoryData1.getMsisdn());
        deviceAssociation.setImsi(factoryData1.getImsi());
        deviceAssociation.setBssid(factoryData1.getBssid());
        deviceAssociation.setManufacturingDate(factoryData1.getManufacturingDate());
        deviceAssociation.setModel(factoryData1.getModel());
        deviceAssociation.setRecordDate(factoryData1.getRecordDate());
        deviceAssociation.setPlatformVersion(factoryData1.getPlatformVersion());
        deviceAssociation.setDeviceType(factoryData1.getDeviceType());
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);

        List<DeviceInfo> deviceItems = new ArrayList<>();
        DeviceInfo deviceInfo = new DeviceInfo("HarID", "name", "value");
        deviceItems.add(deviceInfo);

        List<FactoryData> data = new ArrayList<>();
        data.add(factoryData1);

        SimDetails simDetails = new SimDetails();
        simDetails.setTranId("TransId");
        simDetails.setTranStatus(SimTransactionStatus.FAILED.getSimTransactionStatus());

        SimTransactionStatusDto simTransactionStatus = new SimTransactionStatusDto();
        simTransactionStatus.setStatus(SimTransactionStatus.PENDING.getSimTransactionStatus());

        Mockito.doReturn(simTransactionStatus).when(simStateManager)
            .pollTransactionStatus(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn("Th").when(deviceAssociationService).getRegion(Mockito.anyLong());
        Mockito.doReturn(simDetails).when(deviceAssociationDao).findLatestSimTransactionStatus(Mockito.anyLong());
        Mockito.doReturn("VinValue123445").when(deviceAssociationDao).getAssociatedVin(Mockito.any());
        Mockito.doReturn(data).when(deviceAssociationDao).fetchFactoryData(Mockito.anyLong());
        Mockito.doReturn(deviceItems).when(deviceAssociationDao).findDeviceInfoByName(Mockito.any(), Mockito.any());
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(Mockito.anyString());
        List<DeviceAssociation> deviceAssociationList =
                deviceAssociationWithFactDataServiceV2.getAssociatedDevicesForUser("userID");
        assertNotNull(deviceAssociationList);
    }

    @Test
    public void getAssociatedDevicesForUserTest_DiffStatus() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "vinAssocEnabled", true);
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "supportedDeviceItems",
            new String[]{"userVehicleAuthStatus"});

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setFactoryId(ID);
        deviceAssociation.setIccid(factoryData1.getIccid());
        deviceAssociation.setImei(factoryData1.getImei());
        deviceAssociation.setSsid(factoryData1.getSsid());
        deviceAssociation.setMsisdn(factoryData1.getMsisdn());
        deviceAssociation.setImsi(factoryData1.getImsi());
        deviceAssociation.setBssid(factoryData1.getBssid());
        deviceAssociation.setManufacturingDate(factoryData1.getManufacturingDate());
        deviceAssociation.setModel(factoryData1.getModel());
        deviceAssociation.setRecordDate(factoryData1.getRecordDate());
        deviceAssociation.setPlatformVersion(factoryData1.getPlatformVersion());
        deviceAssociation.setDeviceType(factoryData1.getDeviceType());
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);

        List<DeviceInfo> deviceItems = new ArrayList<>();
        DeviceInfo deviceInfo = new DeviceInfo("HarID", "name", "value");
        deviceItems.add(deviceInfo);

        List<FactoryData> data = new ArrayList<>();
        data.add(factoryData1);

        SimDetails simDetails = new SimDetails();
        simDetails.setTranId("TransId");
        simDetails.setTranStatus(SimTransactionStatus.CANCELED.getSimTransactionStatus());

        Mockito.doReturn(simDetails).when(deviceAssociationDao).findLatestSimTransactionStatus(Mockito.anyLong());
        Mockito.doReturn("VinValue123445").when(deviceAssociationDao).getAssociatedVin(Mockito.any());
        Mockito.doReturn(data).when(deviceAssociationDao).fetchFactoryData(Mockito.anyLong());
        Mockito.doReturn(deviceItems).when(deviceAssociationDao).findDeviceInfoByName(Mockito.any(), Mockito.any());
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(Mockito.anyString());
        List<DeviceAssociation> deviceAssociationList =
                deviceAssociationWithFactDataServiceV2.getAssociatedDevicesForUser("userID");
        assertNotNull(deviceAssociationList);
    }

    @Test
    public void getAssociatedDevicesForUserTest_Exception() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "vinAssocEnabled", true);
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "supportedDeviceItems",
            new String[]{"userVehicleAuthStatus"});

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setFactoryId(ID);
        deviceAssociation.setIccid(factoryData1.getIccid());
        deviceAssociation.setImei(factoryData1.getImei());
        deviceAssociation.setSsid(factoryData1.getSsid());
        deviceAssociation.setMsisdn(factoryData1.getMsisdn());
        deviceAssociation.setImsi(factoryData1.getImsi());
        deviceAssociation.setBssid(factoryData1.getBssid());
        deviceAssociation.setManufacturingDate(factoryData1.getManufacturingDate());
        deviceAssociation.setModel(factoryData1.getModel());
        deviceAssociation.setRecordDate(factoryData1.getRecordDate());
        deviceAssociation.setPlatformVersion(factoryData1.getPlatformVersion());
        deviceAssociation.setDeviceType(factoryData1.getDeviceType());
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);

        List<DeviceInfo> deviceItems = new ArrayList<>();
        DeviceInfo deviceInfo = new DeviceInfo("HarID", "name", "value");
        deviceItems.add(deviceInfo);

        List<FactoryData> data = new ArrayList<>();
        data.add(factoryData1);

        Mockito.doThrow(EmptyResultDataAccessException.class).when(deviceAssociationDao)
            .findLatestSimTransactionStatus(Mockito.anyLong());
        Mockito.doReturn("VinValue123445").when(deviceAssociationDao).getAssociatedVin(Mockito.any());
        Mockito.doReturn(data).when(deviceAssociationDao).fetchFactoryData(Mockito.anyLong());
        Mockito.doReturn(deviceItems).when(deviceAssociationDao).findDeviceInfoByName(Mockito.any(), Mockito.any());
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(Mockito.anyString());
        List<DeviceAssociation> deviceAssociationList =
                deviceAssociationWithFactDataServiceV2.getAssociatedDevicesForUser("userID");
        assertNotNull(deviceAssociationList);
    }

    @Test
    public void getAssociatedDevicesForUserTest_NullSimDetails() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "vinAssocEnabled", true);
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "supportedDeviceItems",
            new String[]{"userVehicleAuthStatus"});

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setFactoryId(ID);
        deviceAssociation.setIccid(factoryData1.getIccid());
        deviceAssociation.setImei(factoryData1.getImei());
        deviceAssociation.setSsid(factoryData1.getSsid());
        deviceAssociation.setMsisdn(factoryData1.getMsisdn());
        deviceAssociation.setImsi(factoryData1.getImsi());
        deviceAssociation.setBssid(factoryData1.getBssid());
        deviceAssociation.setManufacturingDate(factoryData1.getManufacturingDate());
        deviceAssociation.setModel(factoryData1.getModel());
        deviceAssociation.setRecordDate(factoryData1.getRecordDate());
        deviceAssociation.setPlatformVersion(factoryData1.getPlatformVersion());
        deviceAssociation.setDeviceType(factoryData1.getDeviceType());
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);

        List<DeviceInfo> deviceItems = new ArrayList<>();
        DeviceInfo deviceInfo = new DeviceInfo("HarID", "name", "value");
        deviceItems.add(deviceInfo);

        List<FactoryData> data = new ArrayList<>();
        data.add(factoryData1);

        Mockito.doReturn(null).when(deviceAssociationDao).findLatestSimTransactionStatus(Mockito.anyLong());
        Mockito.doReturn("VinValue123445").when(deviceAssociationDao).getAssociatedVin(Mockito.any());
        Mockito.doReturn(data).when(deviceAssociationDao).fetchFactoryData(Mockito.anyLong());
        Mockito.doReturn(deviceItems).when(deviceAssociationDao).findDeviceInfoByName(Mockito.any(), Mockito.any());
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(Mockito.anyString());
        List<DeviceAssociation> deviceAssociationList =
                deviceAssociationWithFactDataServiceV2.getAssociatedDevicesForUser("userID");
        assertNotNull(deviceAssociationList);
    }

    @Test
    public void getAssociatedDevicesForUserTest_AssociatedInitiatedAssociationStatus() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "vinAssocEnabled", false);
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "supportedDeviceItems",
            new String[]{"userVehicleAuthStatus"});

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setFactoryId(ID);
        deviceAssociation.setIccid(factoryData1.getIccid());
        deviceAssociation.setImei(factoryData1.getImei());
        deviceAssociation.setSsid(factoryData1.getSsid());
        deviceAssociation.setMsisdn(factoryData1.getMsisdn());
        deviceAssociation.setImsi(factoryData1.getImsi());
        deviceAssociation.setBssid(factoryData1.getBssid());
        deviceAssociation.setManufacturingDate(factoryData1.getManufacturingDate());
        deviceAssociation.setModel(factoryData1.getModel());
        deviceAssociation.setRecordDate(factoryData1.getRecordDate());
        deviceAssociation.setPlatformVersion(factoryData1.getPlatformVersion());
        deviceAssociation.setDeviceType(factoryData1.getDeviceType());
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATION_INITIATED);
        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);

        List<DeviceInfo> deviceItems = new ArrayList<>();
        DeviceInfo deviceInfo = new DeviceInfo("HarID", "name", "value");
        deviceItems.add(deviceInfo);

        List<FactoryData> data = new ArrayList<>();
        data.add(factoryData1);

        Mockito.doReturn(data).when(deviceAssociationDao).fetchFactoryData(Mockito.anyLong());
        Mockito.doReturn(deviceItems).when(deviceAssociationDao).findDeviceInfoByName(Mockito.any(), Mockito.any());
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(Mockito.anyString());
        List<DeviceAssociation> deviceAssociationList =
                deviceAssociationWithFactDataServiceV2.getAssociatedDevicesForUser("userID");
        assertNotNull(deviceAssociationList);
    }

    @Test
    public void getAssociatedDevicesForUserTest_AssociatedAssociationStatus_NullVin() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "vinAssocEnabled", false);
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "supportedDeviceItems",
            new String[]{"userVehicleAuthStatus"});

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setFactoryId(ID);
        deviceAssociation.setIccid(factoryData1.getIccid());
        deviceAssociation.setImei(factoryData1.getImei());
        deviceAssociation.setSsid(factoryData1.getSsid());
        deviceAssociation.setMsisdn(factoryData1.getMsisdn());
        deviceAssociation.setImsi(factoryData1.getImsi());
        deviceAssociation.setBssid(factoryData1.getBssid());
        deviceAssociation.setManufacturingDate(factoryData1.getManufacturingDate());
        deviceAssociation.setModel(factoryData1.getModel());
        deviceAssociation.setRecordDate(factoryData1.getRecordDate());
        deviceAssociation.setPlatformVersion(factoryData1.getPlatformVersion());
        deviceAssociation.setDeviceType(factoryData1.getDeviceType());
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);

        List<DeviceInfo> deviceItems = new ArrayList<>();
        DeviceInfo deviceInfo = new DeviceInfo("HarID", "name", "value");
        deviceItems.add(deviceInfo);

        List<FactoryData> data = new ArrayList<>();
        data.add(factoryData1);

        Mockito.doReturn(data).when(deviceAssociationDao).fetchFactoryData(Mockito.anyLong());
        Mockito.doReturn(deviceItems).when(deviceAssociationDao).findDeviceInfoByName(Mockito.any(), Mockito.any());
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(Mockito.anyString());
        List<DeviceAssociation> deviceAssociationList =
                deviceAssociationWithFactDataServiceV2.getAssociatedDevicesForUser("userID");
        assertNotNull(deviceAssociationList);
    }

    @Test
    public void getAssociatedDevicesForUserTest_AssociatedInitiatedAssociationStatus_NullVin() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "vinAssocEnabled", false);
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "supportedDeviceItems",
            new String[]{"userVehicleAuthStatus"});

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setFactoryId(ID);
        deviceAssociation.setIccid(factoryData1.getIccid());
        deviceAssociation.setImei(factoryData1.getImei());
        deviceAssociation.setSsid(factoryData1.getSsid());
        deviceAssociation.setMsisdn(factoryData1.getMsisdn());
        deviceAssociation.setImsi(factoryData1.getImsi());
        deviceAssociation.setBssid(factoryData1.getBssid());
        deviceAssociation.setManufacturingDate(factoryData1.getManufacturingDate());
        deviceAssociation.setModel(factoryData1.getModel());
        deviceAssociation.setRecordDate(factoryData1.getRecordDate());
        deviceAssociation.setPlatformVersion(factoryData1.getPlatformVersion());
        deviceAssociation.setDeviceType(factoryData1.getDeviceType());
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATION_INITIATED);
        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);

        List<DeviceInfo> deviceItems = new ArrayList<>();
        DeviceInfo deviceInfo = new DeviceInfo("HarID", "name", "value");
        deviceItems.add(deviceInfo);

        List<FactoryData> data = new ArrayList<>();
        data.add(factoryData1);

        Mockito.doReturn(data).when(deviceAssociationDao).fetchFactoryData(Mockito.anyLong());
        Mockito.doReturn(deviceItems).when(deviceAssociationDao).findDeviceInfoByName(Mockito.any(), Mockito.any());
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(Mockito.anyString());
        List<DeviceAssociation> deviceAssociationList =
                deviceAssociationWithFactDataServiceV2.getAssociatedDevicesForUser("userID");
        assertNotNull(deviceAssociationList);
    }

    @Test
    public void getAssociatedDevicesForUserTest_ZeroFactoryId() {

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setFactoryId(0L);
        deviceAssociation.setIccid(factoryData1.getIccid());
        deviceAssociation.setImei(factoryData1.getImei());
        deviceAssociation.setSsid(factoryData1.getSsid());
        deviceAssociation.setMsisdn(factoryData1.getMsisdn());
        deviceAssociation.setImsi(factoryData1.getImsi());
        deviceAssociation.setBssid(factoryData1.getBssid());
        deviceAssociation.setManufacturingDate(factoryData1.getManufacturingDate());
        deviceAssociation.setModel(factoryData1.getModel());
        deviceAssociation.setRecordDate(factoryData1.getRecordDate());
        deviceAssociation.setPlatformVersion(factoryData1.getPlatformVersion());
        deviceAssociation.setDeviceType(factoryData1.getDeviceType());
        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);

        List<DeviceInfo> deviceItems = new ArrayList<>();
        DeviceInfo deviceInfo = new DeviceInfo("HarID", "name", "value");
        deviceItems.add(deviceInfo);

        List<FactoryData> data = new ArrayList<>();
        data.add(factoryData1);

        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(Mockito.anyString());
        List<DeviceAssociation> deviceAssociationList =
                deviceAssociationWithFactDataServiceV2.getAssociatedDevicesForUser("userID");
        assertNotNull(deviceAssociationList);
    }

    @Test
    public void getAssociatedDevicesForUserTest_NullDeviceAssociations() {

        Mockito.doReturn(null).when(deviceAssociationDao).fetchAssociatedDevices(Mockito.anyString());
        List<DeviceAssociation> deviceAssociationList =
                deviceAssociationWithFactDataServiceV2.getAssociatedDevicesForUser("userID");
        assertNull(deviceAssociationList);
    }

    @Test
    public void getAssociationDetailsTest_NullDeviceAssociation() {

        Mockito.doReturn(null).when(deviceAssociationDao).find(Mockito.anyLong(), Mockito.anyString());
        DeviceAssociation deviceAssociation =
                deviceAssociationWithFactDataServiceV2.getAssociationDetails(ID, "userID");
        assertNull(deviceAssociation);
    }

    @Test
    public void getAssociationDetailsTest_NullId() {

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setHarmanId(null);

        Mockito.doReturn(deviceAssociation).when(deviceAssociationDao).find(Mockito.anyLong(), Mockito.anyString());
        DeviceAssociation actualDeviceAssociation =
                deviceAssociationWithFactDataServiceV2.getAssociationDetails(ID, "userID");
        assertEquals(deviceAssociation, actualDeviceAssociation);
    }

    @Test
    public void getAssociationDetailsTest_Exception() {

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setHarmanId("Har1234");

        DeviceAttributes deviceAttributes = new DeviceAttributes();
        ResponseEntity<DeviceAttributes> responseEntity = new ResponseEntity<>(deviceAttributes, HttpStatus.OK);

        Mockito.when(envConfig.getStringValue(Mockito.any())).thenReturn("baseUrl", "deviceInfoPath");
        Mockito.doReturn(deviceAssociation).when(deviceAssociationDao).find(Mockito.anyLong(), Mockito.anyString());
        DeviceAssociation actualDeviceAssociation =
                deviceAssociationWithFactDataServiceV2.getAssociationDetails(ID, "userID");
        assertEquals(deviceAssociation, actualDeviceAssociation);
    }

    //@Test(expected = ApiValidationFailedException.class)
    //public void terminateAssociationTest_NullDeviceStatusRequest(){
    //
    //    DeviceStatusRequest deviceStatusRequest = mock(DeviceStatusRequest.class);
    //    Mockito.when(deviceStatusRequest.toString()).thenReturn(null);
    //
    //    deviceAssociationWithFactDataServiceV2.terminateAssociation(deviceStatusRequest);
    //}

    @Test(expected = ApiValidationFailedException.class)
    public void terminateAssociationTest_EmptyDeviceStatusRequest() {

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setDeviceId("");
        deviceStatusRequest.setImei("");
        deviceStatusRequest.setSerialNumber("");
        deviceStatusRequest.setAssociationId(ID);
        deviceStatusRequest.setUserId("userID");

        deviceAssociationWithFactDataServiceV2.terminateAssociation(deviceStatusRequest);
    }

    @Test(expected = ApiResourceNotFoundException.class)
    public void terminateAssociationTest_NullDeviceAssociationList() {

        Mockito.doReturn(null).when(deviceAssociationDao)
            .constructAndFetchDeviceAssociationData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong(), Mockito.anyList());
        deviceAssociationWithFactDataServiceV2.terminateAssociation(deviceStatusRequest);
    }

    @Test(expected = ApiResourceNotFoundException.class)
    public void terminateAssociationTest_EmptyDeviceAssociationList() {

        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();

        Mockito.doReturn(deviceAssociationList).when(deviceAssociationDao)
            .constructAndFetchDeviceAssociationData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong(), Mockito.anyList());
        deviceAssociationWithFactDataServiceV2.terminateAssociation(deviceStatusRequest);
    }

    @Test(expected = ApiPreConditionFailedException.class)
    public void terminateAssociationTest_ManyDeviceAssociationList() {

        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        DeviceAssociation deviceAssociation1 = new DeviceAssociation();
        deviceAssociation.setId(ID_2);
        deviceAssociationList.add(deviceAssociation);
        deviceAssociationList.add(deviceAssociation1);

        Mockito.doReturn(deviceAssociationList).when(deviceAssociationDao)
            .constructAndFetchDeviceAssociationData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong(), Mockito.anyList());
        deviceAssociationWithFactDataServiceV2.terminateAssociation(deviceStatusRequest);
    }

    @Test
    public void terminateAssociationTest_ZeroUpdateCount() {
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "simSuspendCheck", false);
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "ncBaseUrl", "ncBaseUrlValue");

        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setHarmanId("HarmanID");
        deviceAssociationList.add(deviceAssociation);

        UserProfile userProfile = new UserProfile();

        Mockito.doReturn(0).when(deviceAssociationDao).updateForDisassociationById(Mockito.any());
        Mockito.doReturn(userProfile).when(ncClient).getUserProfile(Mockito.anyString(), Mockito.any());
        Mockito.doReturn(deviceAssociationList).when(deviceAssociationDao)
            .constructAndFetchDeviceAssociationData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong(), Mockito.anyList());
        int updatedCount = deviceAssociationWithFactDataServiceV2.terminateAssociation(deviceStatusRequest);
        assertEquals(0, updatedCount);
    }

    @Test(expected = ApiNotificationException.class)
    public void terminateAssociationTest_ApiNotificationException_SpringAuth()
        throws InvalidAttributeValueException, ObserverMessageProcessFailureException {
        
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "simSuspendCheck", false);
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "ncBaseUrl", "ncBaseUrlValue");

        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setHarmanId("HarmanID");
        deviceAssociationList.add(deviceAssociation);

        Device device = new Device();
        device.setHarmanId("HarmanId");
        device.setPasscode("Passcode");

        Mockito.doReturn(device).when(deviceDao).findByDeviceId(Mockito.anyString());
        Mockito.doThrow(ObserverMessageProcessFailureException.class).when(observable).notify(Mockito.any());
        Mockito.doReturn(1).when(deviceAssociationDao).updateForDisassociationById(Mockito.any());
        UserProfile userProfile = new UserProfile();
        Mockito.doReturn(userProfile).when(ncClient).getUserProfile(Mockito.anyString(), Mockito.any());
        Mockito.doReturn(deviceAssociationList).when(deviceAssociationDao)
            .constructAndFetchDeviceAssociationData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong(), Mockito.anyList());
        deviceAssociationWithFactDataServiceV2.terminateAssociation(deviceStatusRequest);
    }

    @Test
    public void terminateAssociationTest_NullActivateTranStatus() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "simSuspendCheck", true);
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "ncBaseUrl", "ncBaseUrlValue");

        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setHarmanId("HarmanID");
        deviceAssociationList.add(deviceAssociation);

        Mockito.doReturn(null).when(deviceAssociationDao).getActivateTranStatus(Mockito.anyLong());
        Mockito.doReturn(deviceAssociationList).when(deviceAssociationDao)
            .constructAndFetchDeviceAssociationData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong(), Mockito.anyList());
        int updatedCount = deviceAssociationWithFactDataServiceV2.terminateAssociation(deviceStatusRequest);
        assertEquals(0, updatedCount);
    }

    @Test(expected = ApiPreConditionFailedException.class)
    public void terminateAssociationTest_ApiPreConditionFailedException() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "simSuspendCheck", true);
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "ncBaseUrl", "ncBaseUrlValue");

        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setHarmanId("HarmanID");
        deviceAssociationList.add(deviceAssociation);

        Mockito.doReturn(SimTransactionStatus.IN_PROGRESS.getSimTransactionStatus()).when(deviceAssociationDao)
            .getActivateTranStatus(Mockito.anyLong());
        Mockito.doReturn(deviceAssociationList).when(deviceAssociationDao)
            .constructAndFetchDeviceAssociationData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong(), Mockito.anyList());
        deviceAssociationWithFactDataServiceV2.terminateAssociation(deviceStatusRequest);
    }

    @Test(expected = ApiPreConditionFailedException.class)
    public void terminateAssociationTest_IncorrectTerminateTranStatus() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "simSuspendCheck", true);
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "ncBaseUrl", "ncBaseUrlValue");

        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setHarmanId("HarmanID");
        deviceAssociationList.add(deviceAssociation);

        Mockito.doReturn(SimTransactionStatus.IN_PROGRESS.getSimTransactionStatus()).when(deviceAssociationDao)
            .getTerminateTranStatus(Mockito.anyLong());
        Mockito.doReturn(SimTransactionStatus.COMPLETED.getSimTransactionStatus()).when(deviceAssociationDao)
            .getActivateTranStatus(Mockito.anyLong());
        Mockito.doReturn(deviceAssociationList).when(deviceAssociationDao)
            .constructAndFetchDeviceAssociationData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong(), Mockito.anyList());
        deviceAssociationWithFactDataServiceV2.terminateAssociation(deviceStatusRequest);
    }

    @Test(expected = ApiPreConditionFailedException.class)
    public void restoreAssociationTest_ApiPreConditionFailedException() {

        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        DeviceAssociation deviceAssociation1 = new DeviceAssociation();
        deviceAssociation.setId(ID_2);
        deviceAssociationList.add(deviceAssociation);
        deviceAssociationList.add(deviceAssociation1);

        Mockito.doReturn(deviceAssociationList).when(deviceAssociationDao)
            .constructAndFetchDeviceAssociationData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong(), Mockito.anyList());
        deviceAssociationWithFactDataServiceV2.restoreAssociation(deviceStatusRequest);
    }

    @Test
    public void restoreAssociationTest() throws InvalidAttributeValueException {

        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociationList.add(deviceAssociation);

        Device device = new Device();
        device.setHarmanId("HarmanId");
        device.setPasscode("Passcode");

        Mockito.doReturn(1).when(deviceAssociationDao).updateDeviceAssociationStatusToRestore(Mockito.any());
        Mockito.doReturn(deviceAssociationList).when(deviceAssociationDao)
            .constructAndFetchDeviceAssociationData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong(), Mockito.anyList());
        int updatedCount = deviceAssociationWithFactDataServiceV2.restoreAssociation(deviceStatusRequest);
        assertEquals(1, updatedCount);
    }

    @Test()
    public void restoreAssociationTest_SpringAuth() throws InvalidAttributeValueException {
        
        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociationList.add(deviceAssociation);

        Device device = new Device();
        device.setHarmanId("HarmanId");
        device.setPasscode("Passcode");

        Mockito.doReturn(deviceAssociationList).when(deviceAssociationDao)
            .constructAndFetchDeviceAssociationData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong(), Mockito.anyList());
        Mockito.doReturn(1).when(deviceAssociationDao).updateDeviceAssociationStatusToRestore(Mockito.any());
        Mockito.doReturn(device).when(deviceDao).findByDeviceId(Mockito.any());
        Mockito.doReturn("dummytoken").when(springAuthTokenGenerator).fetchSpringAuthToken();
        int updatedCount = deviceAssociationWithFactDataServiceV2.restoreAssociation(deviceStatusRequest);
        assertEquals(1, updatedCount);
    }

    @Test(expected = ApiPreConditionFailedException.class)
    public void suspendDeviceTest_ApiPreConditionFailedException() throws Exception {

        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociationList.add(deviceAssociation);
        DeviceAssociation deviceAssociation1 = new DeviceAssociation();
        deviceAssociation.setId(ID_2);
        deviceAssociationList.add(deviceAssociation1);

        Mockito.doReturn(deviceAssociationList).when(deviceAssociationDao)
            .constructAndFetchDeviceAssociationData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong(), Mockito.anyList());
        deviceAssociationWithFactDataServiceV2.suspendDevice(deviceStatusRequest);
    }

    @Test(expected = ApiTechnicalException.class)
    public void suspendDeviceTest_ZeroUpdateCount() throws Exception {

        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociationList.add(deviceAssociation);

        Mockito.doReturn(0).when(deviceAssociationDao).updateDeviceAssociationStatusToSuspended(deviceAssociation);
        Mockito.doReturn(deviceAssociationList).when(deviceAssociationDao)
            .constructAndFetchDeviceAssociationData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong(), Mockito.anyList());
        deviceAssociationWithFactDataServiceV2.suspendDevice(deviceStatusRequest);
    }

    @Test
    public void suspendDeviceTest_SpringAuth() throws Exception {
        
        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociationList.add(deviceAssociation);

        Mockito.doReturn(1).when(deviceAssociationDao).updateDeviceAssociationStatusToSuspended(deviceAssociation);
        Mockito.doReturn(deviceAssociationList).when(deviceAssociationDao)
            .constructAndFetchDeviceAssociationData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong(), Mockito.anyList());
        AssociateDeviceResponse associateDeviceResponse =
                deviceAssociationWithFactDataServiceV2.suspendDevice(deviceStatusRequest);
        Assertions.assertNotNull(associateDeviceResponse);
    }

    @Test(expected = ApiValidationFailedException.class)
    public void replaceDeviceTest_NullReplaceFactoryDataRequest() throws Exception {

        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        deviceAssociationWithFactDataServiceV2.replaceDevice(replaceDeviceRequest, "userId");
    }

    @Test(expected = ApiValidationFailedException.class)
    public void replaceDeviceTest_ApiValidationFailedException() throws Exception {

        CurrentDeviceDataPojo currentDeviceDataPojo = new CurrentDeviceDataPojo();
        currentDeviceDataPojo.setBssid("1234");
        currentDeviceDataPojo.setIccid("1234");
        currentDeviceDataPojo.setImsi("1234");
        currentDeviceDataPojo.setMsisdn("1234");
        currentDeviceDataPojo.setSerialNumber("1234");
        currentDeviceDataPojo.setImei("1234");

        ReplaceDeviceDataPojo replaceDeviceDataPojo = new ReplaceDeviceDataPojo();
        replaceDeviceDataPojo.setBssid("1234");
        replaceDeviceDataPojo.setIccid("1234");
        replaceDeviceDataPojo.setImsi("1234");
        replaceDeviceDataPojo.setMsisdn("1234");
        replaceDeviceDataPojo.setSerialNumber("1234");
        replaceDeviceDataPojo.setImei("1234");

        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        replaceDeviceRequest.setReplaceWith(replaceDeviceDataPojo);
        replaceDeviceRequest.setCurrentValue(currentDeviceDataPojo);

        List<DeviceInfoFactoryData> deviceInfoFactoryDataList = new ArrayList<>();

        deviceAssociationWithFactDataServiceV2.replaceDevice(replaceDeviceRequest, "userId");
    }


    @Test(expected = ApiTechnicalException.class)
    public void replaceDeviceTest_WrongListReplaceValueDataSize() throws Exception {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "isCurrentDeviceToBeMovedToProvisioned",
            true);

        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        replaceDeviceRequest.setReplaceWith(replaceDeviceDataPojo);
        replaceDeviceRequest.setCurrentValue(currentDeviceDataPojo);

        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setId(ID);
        deviceInfoFactoryData.setFaulty(false);
        deviceInfoFactoryData.setStolen(false);
        List<DeviceInfoFactoryData> deviceInfoFactoryDataList = new ArrayList<>();
        deviceInfoFactoryDataList.add(deviceInfoFactoryData);

        List<DeviceInfoFactoryData> listReplaceValueData = new ArrayList<>();
        listReplaceValueData.add(deviceInfoFactoryData);
        listReplaceValueData.add(deviceInfoFactoryData);

        Mockito.doReturn(listReplaceValueData).when(deviceInfoFactoryDataDao)
            .constructAndFetchFactoryData(Mockito.any());
        Mockito.doReturn(true).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactData(Mockito.anyLong(), Mockito.anyString());
        deviceAssociationWithFactDataServiceV2.replaceDevice(replaceDeviceRequest, "userId");
    }

    @Test(expected = ApiPreConditionFailedException.class)
    public void replaceDeviceTest_ProvisionedState() throws Exception {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "isCurrentDeviceToBeMovedToProvisioned",
            true);

        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        replaceDeviceRequest.setReplaceWith(replaceDeviceDataPojo);
        replaceDeviceRequest.setCurrentValue(currentDeviceDataPojo);

        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setId(ID);
        deviceInfoFactoryData.setFaulty(false);
        deviceInfoFactoryData.setStolen(false);
        deviceInfoFactoryData.setState("PROVISIONED");
        List<DeviceInfoFactoryData> deviceInfoFactoryDataList = new ArrayList<>();
        deviceInfoFactoryDataList.add(deviceInfoFactoryData);

        List<DeviceInfoFactoryData> listReplaceValueData = new ArrayList<>();
        listReplaceValueData.add(deviceInfoFactoryData);

        Mockito.doReturn(listReplaceValueData).when(deviceInfoFactoryDataDao)
            .constructAndFetchFactoryData(Mockito.any());
        Mockito.doReturn(true).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactData(Mockito.anyLong(), Mockito.anyString());
        deviceAssociationWithFactDataServiceV2.replaceDevice(replaceDeviceRequest, "userId");
    }

    @Test(expected = ApiPreConditionFailedException.class)
    public void replaceDeviceTest_NullHcpInfo() throws Exception {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "isCurrentDeviceToBeMovedToProvisioned",
            true);

        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        replaceDeviceRequest.setReplaceWith(replaceDeviceDataPojo);
        replaceDeviceRequest.setCurrentValue(currentDeviceDataPojo);

        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setId(ID);
        deviceInfoFactoryData.setFaulty(false);
        deviceInfoFactoryData.setStolen(false);
        deviceInfoFactoryData.setState("PROVISIONED");
        List<DeviceInfoFactoryData> deviceInfoFactoryDataList = new ArrayList<>();
        deviceInfoFactoryDataList.add(deviceInfoFactoryData);

        List<DeviceInfoFactoryData> listReplaceValueData = new ArrayList<>();
        listReplaceValueData.add(deviceInfoFactoryData);

        Mockito.doReturn(null).when(hcpInfoDao).findActiveHcpInfo(Mockito.anyLong());
        Mockito.doReturn(listReplaceValueData).when(deviceInfoFactoryDataDao)
            .constructAndFetchFactoryData(Mockito.any());
        Mockito.doReturn(true).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactData(Mockito.anyLong(), Mockito.anyString());
        deviceAssociationWithFactDataServiceV2.replaceDevice(replaceDeviceRequest, "userId");
    }

    @Test(expected = ApiPreConditionFailedException.class)
    public void replaceDeviceTest_FalseIsCurrentDeviceToBeMovedToProvisioned() throws Exception {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "isCurrentDeviceToBeMovedToProvisioned",
            false);

        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        replaceDeviceRequest.setReplaceWith(replaceDeviceDataPojo);
        replaceDeviceRequest.setCurrentValue(currentDeviceDataPojo);

        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setId(ID);
        deviceInfoFactoryData.setFaulty(false);
        deviceInfoFactoryData.setStolen(false);
        deviceInfoFactoryData.setSerialNumber("12345");
        deviceInfoFactoryData.setState("PROVISIONED");
        List<DeviceInfoFactoryData> deviceInfoFactoryDataList = new ArrayList<>();
        deviceInfoFactoryDataList.add(deviceInfoFactoryData);

        List<DeviceInfoFactoryData> listReplaceValueData = new ArrayList<>();
        listReplaceValueData.add(deviceInfoFactoryData);

        Mockito.doReturn(listReplaceValueData).when(deviceInfoFactoryDataDao)
            .constructAndFetchFactoryData(Mockito.any());
        Mockito.doReturn(true).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactData(Mockito.anyLong(), Mockito.anyString());
        deviceAssociationWithFactDataServiceV2.replaceDevice(replaceDeviceRequest, "userId");
    }

    @Test(expected = ApiPreConditionFailedException.class)
    public void replaceDeviceTest_EmptyListReplaceValueData() throws Exception {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "isCurrentDeviceToBeMovedToProvisioned",
            true);

        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        replaceDeviceRequest.setReplaceWith(replaceDeviceDataPojo);
        replaceDeviceRequest.setCurrentValue(currentDeviceDataPojo);

        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setId(ID);
        deviceInfoFactoryData.setFaulty(false);
        deviceInfoFactoryData.setStolen(false);
        deviceInfoFactoryData.setSerialNumber("12345");
        deviceInfoFactoryData.setState("PROVISIONED");
        List<DeviceInfoFactoryData> deviceInfoFactoryDataList = new ArrayList<>();
        deviceInfoFactoryDataList.add(deviceInfoFactoryData);

        List<DeviceInfoFactoryData> listCurrentValueData = new ArrayList<>();
        listCurrentValueData.add(deviceInfoFactoryData);

        List<DeviceInfoFactoryData> listReplaceValueData = new ArrayList<>();

        Mockito.doReturn(true).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactData(Mockito.anyLong(), Mockito.anyString());
        Mockito.when(deviceInfoFactoryDataDao.constructAndFetchFactoryData(Mockito.any()))
            .thenReturn(listCurrentValueData, listReplaceValueData);
        deviceAssociationWithFactDataServiceV2.replaceDevice(replaceDeviceRequest, "userId");
    }

    @Test(expected = ApiPreConditionFailedException.class)
    public void replaceDeviceTest_WrongState() throws Exception {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "isCurrentDeviceToBeMovedToProvisioned",
            true);

        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        replaceDeviceRequest.setReplaceWith(replaceDeviceDataPojo);
        replaceDeviceRequest.setCurrentValue(currentDeviceDataPojo);

        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setId(ID);
        deviceInfoFactoryData.setFaulty(false);
        deviceInfoFactoryData.setStolen(false);
        deviceInfoFactoryData.setSerialNumber("12345");
        deviceInfoFactoryData.setState("ACTIVE");
        List<DeviceInfoFactoryData> deviceInfoFactoryDataList = new ArrayList<>();
        deviceInfoFactoryDataList.add(deviceInfoFactoryData);

        List<DeviceInfoFactoryData> listCurrentValueData = new ArrayList<>();
        listCurrentValueData.add(deviceInfoFactoryData);

        List<DeviceInfoFactoryData> listReplaceValueData = new ArrayList<>();
        listReplaceValueData.add(deviceInfoFactoryData);

        Mockito.doReturn(true).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactData(Mockito.anyLong(), Mockito.anyString());
        Mockito.when(deviceInfoFactoryDataDao.constructAndFetchFactoryData(Mockito.any()))
            .thenReturn(listCurrentValueData, listReplaceValueData);
        deviceAssociationWithFactDataServiceV2.replaceDevice(replaceDeviceRequest, "userId");
    }

    @Test(expected = ApiPreConditionFailedException.class)
    public void replaceDeviceTest_NullDeviceAssociation() throws Exception {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "isCurrentDeviceToBeMovedToProvisioned",
            true);

        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        replaceDeviceRequest.setReplaceWith(replaceDeviceDataPojo);
        replaceDeviceRequest.setCurrentValue(currentDeviceDataPojo);

        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setId(ID);
        deviceInfoFactoryData.setFaulty(false);
        deviceInfoFactoryData.setStolen(false);
        deviceInfoFactoryData.setSerialNumber("12345");
        deviceInfoFactoryData.setState("PROVISIONED");
        List<DeviceInfoFactoryData> deviceInfoFactoryDataList = new ArrayList<>();
        deviceInfoFactoryDataList.add(deviceInfoFactoryData);

        List<DeviceInfoFactoryData> listCurrentValueData = new ArrayList<>();
        listCurrentValueData.add(deviceInfoFactoryData);

        List<DeviceInfoFactoryData> listReplaceValueData = new ArrayList<>();
        listReplaceValueData.add(deviceInfoFactoryData);

        Mockito.doReturn(null).when(deviceAssociationDao).findValidAssociations(Mockito.anyString());
        deviceActivationStateDao.insert(Mockito.any());
        deviceActivationStateDao.disableActivationReadyByFacotryId(Mockito.anyLong());
        deviceDao.updateForReplaceDevice(Mockito.any());
        hcpInfoDao.updateForReplaceDevice(Mockito.any());
        Mockito.doReturn(hcpInfo).when(hcpInfoDao).findActiveHcpInfo(Mockito.anyLong());
        Mockito.doReturn(true).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactData(Mockito.anyLong(), Mockito.anyString());
        Mockito.when(deviceInfoFactoryDataDao.constructAndFetchFactoryData(Mockito.any()))
            .thenReturn(listCurrentValueData, listReplaceValueData);
        deviceAssociationWithFactDataServiceV2.replaceDevice(replaceDeviceRequest, "userId");
    }

    @Test
    public void replaceDeviceTest_ActiveStatus_SpringAuth() throws Exception {
        
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "isCurrentDeviceToBeMovedToProvisioned",
            true);

        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        replaceDeviceRequest.setReplaceWith(replaceDeviceDataPojo);
        replaceDeviceRequest.setCurrentValue(currentDeviceDataPojo);

        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setId(ID);
        deviceInfoFactoryData.setFaulty(true);
        deviceInfoFactoryData.setStolen(true);
        deviceInfoFactoryData.setSerialNumber("12345");
        deviceInfoFactoryData.setState("ACTIVE");
        List<DeviceInfoFactoryData> deviceInfoFactoryDataList = new ArrayList<>();
        deviceInfoFactoryDataList.add(deviceInfoFactoryData);

        DeviceInfoFactoryData deviceInfoFactoryData1 = new DeviceInfoFactoryData();
        deviceInfoFactoryData1.setId(ID);
        deviceInfoFactoryData1.setFaulty(true);
        deviceInfoFactoryData1.setStolen(true);
        deviceInfoFactoryData1.setSerialNumber("12345");
        deviceInfoFactoryData1.setState("PROVISIONED");
        List<DeviceInfoFactoryData> deviceInfoFactoryDataList1 = new ArrayList<>();
        deviceInfoFactoryDataList1.add(deviceInfoFactoryData);

        List<DeviceInfoFactoryData> listCurrentValueData = new ArrayList<>();
        listCurrentValueData.add(deviceInfoFactoryData);

        List<DeviceInfoFactoryData> listReplaceValueData = new ArrayList<>();
        listReplaceValueData.add(deviceInfoFactoryData1);

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setFactoryId(ID);
        deviceAssociation.setId(ID);
        deviceAssociation.setSerialNumber(factoryData1.getSerialNumber());
        deviceAssociation.setHarmanId("HAR123");
        deviceAssociation.setSsid(factoryData1.getSsid());

        deviceInfoFactoryDataDao.changeDeviceState(Mockito.anyLong(), Mockito.anyString(), Mockito.any());
        deviceInfoFactoryDataDao.changeDeviceState(Mockito.anyLong(), Mockito.anyString(), Mockito.any());
        deviceAssociationDao.updateForReplaceDevice(Mockito.anyLong(), Mockito.anyString(), Mockito.any(),
            Mockito.anyLong());
        Mockito.doReturn(deviceAssociation).when(deviceAssociationDao).findValidAssociations(Mockito.anyString());
        deviceActivationStateDao.insert(Mockito.any());
        deviceActivationStateDao.disableActivationReadyByFacotryId(Mockito.anyLong());
        deviceDao.updateForReplaceDevice(Mockito.any());
        hcpInfoDao.updateForReplaceDevice(Mockito.any());
        deviceActivationStateDao.disableRecord(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(hcpInfo).when(hcpInfoDao).findActiveHcpInfo(Mockito.anyLong());
        Mockito.doReturn(true).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactData(Mockito.anyLong(), Mockito.anyString());
        Mockito.when(deviceInfoFactoryDataDao.constructAndFetchFactoryData(Mockito.any()))
            .thenReturn(listCurrentValueData, listReplaceValueData);
        deviceAssociationWithFactDataServiceV2.replaceDevice(replaceDeviceRequest, "userId");
        Assertions.assertTrue(deviceAssociationDao.checkAssociatedDeviceWithFactData(Mockito.anyLong(),
                Mockito.anyString()));
    }

    @Test(expected = ApiValidationFailedException.class)
    public void replaceDeviceTest_NullCurrentValue() throws Exception {

        currentDeviceDataPojo = new CurrentDeviceDataPojo();
        currentDeviceDataPojo.setBssid(null);
        currentDeviceDataPojo.setIccid(null);
        currentDeviceDataPojo.setImsi(null);
        currentDeviceDataPojo.setMsisdn(null);
        currentDeviceDataPojo.setSerialNumber(null);
        currentDeviceDataPojo.setImei(null);

        replaceDeviceDataPojo = new ReplaceDeviceDataPojo();
        replaceDeviceDataPojo.setBssid(null);
        replaceDeviceDataPojo.setIccid(null);
        replaceDeviceDataPojo.setImsi(null);
        replaceDeviceDataPojo.setMsisdn(null);
        replaceDeviceDataPojo.setSerialNumber(null);
        replaceDeviceDataPojo.setImei(null);

        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        replaceDeviceRequest.setReplaceWith(replaceDeviceDataPojo);
        replaceDeviceRequest.setCurrentValue(currentDeviceDataPojo);

        deviceAssociationWithFactDataServiceV2.replaceDevice(replaceDeviceRequest, "userId");
    }

    @Test(expected = ApiValidationFailedException.class)
    public void replaceDeviceTest_NullReplaceValue() throws Exception {

        currentDeviceDataPojo = new CurrentDeviceDataPojo();
        currentDeviceDataPojo.setBssid("1234");
        currentDeviceDataPojo.setIccid("1234");
        currentDeviceDataPojo.setImsi("1234");
        currentDeviceDataPojo.setMsisdn("1234");
        currentDeviceDataPojo.setSerialNumber("1234");
        currentDeviceDataPojo.setImei("1234");

        replaceDeviceDataPojo = new ReplaceDeviceDataPojo();
        replaceDeviceDataPojo.setBssid(null);
        replaceDeviceDataPojo.setIccid(null);
        replaceDeviceDataPojo.setImsi(null);
        replaceDeviceDataPojo.setMsisdn(null);
        replaceDeviceDataPojo.setSerialNumber(null);
        replaceDeviceDataPojo.setImei(null);

        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        replaceDeviceRequest.setReplaceWith(replaceDeviceDataPojo);
        replaceDeviceRequest.setCurrentValue(currentDeviceDataPojo);

        deviceAssociationWithFactDataServiceV2.replaceDevice(replaceDeviceRequest, "userId");
    }

    @Test(expected = ApiValidationFailedException.class)
    public void stateChangeTest_EmptyState() throws Exception {

        StateChangeRequest stateChangeRequest = new StateChangeRequest();
        deviceAssociationWithFactDataServiceV2.stateChange(stateChangeRequest);
    }

    @Test(expected = ApiValidationFailedException.class)
    public void stateChangeTest_EmptyImei() throws Exception {

        StateChangeRequest stateChangeRequest = new StateChangeRequest();
        stateChangeRequest.setState("ACTIVE");
        deviceAssociationWithFactDataServiceV2.stateChange(stateChangeRequest);
    }

    @Test(expected = ApiResourceNotFoundException.class)
    public void stateChangeTest_ZeroId() throws Exception {

        DeviceInfoFactoryData factoryData = new DeviceInfoFactoryData();
        factoryData.setId(0L);

        Mockito.doReturn(factoryData).when(deviceInfoFactoryDataDao).findByFactoryImei(Mockito.anyString());
        deviceAssociationWithFactDataServiceV2.stateChange(stateChangeRequest);
    }

    @Test(expected = ApiResourceNotFoundException.class)
    public void stateChangeTest_EmptyFactoryId() throws Exception {

        stateChangeRequest.setImei("");

        DeviceInfoFactoryData factoryData = new DeviceInfoFactoryData();
        factoryData.setId(ID);

        hcpInfo = new HcpInfo();
        hcpInfo.setHarmanId("Har123");
        hcpInfo.setFactoryId("");

        Mockito.doReturn(hcpInfo).when(hcpInfoDao).findByDeviceId(Mockito.anyString());
        deviceAssociationWithFactDataServiceV2.stateChange(stateChangeRequest);
    }

    @Test(expected = ApiTechnicalException.class)
    public void stateChangeTest_EmptyId() throws Exception {

        stateChangeRequest.setImei("");

        DeviceInfoFactoryData factoryData = new DeviceInfoFactoryData();
        factoryData.setId(ID);

        hcpInfo = new HcpInfo();
        hcpInfo.setHarmanId("Har123");
        hcpInfo.setFactoryId("1111");

        Mockito.doReturn(hcpInfo).when(hcpInfoDao).findByDeviceId(Mockito.anyString());
        deviceAssociationWithFactDataServiceV2.stateChange(stateChangeRequest);
    }

    @Test
    public void stateChangeTest() throws Exception {

        DeviceInfoFactoryData factoryData = new DeviceInfoFactoryData();
        factoryData.setId(ID);

        Mockito.doReturn(new ResponseEntity<>(HttpStatus.OK)).when(restClientLibrary)
            .doPut(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.eq(Object.class));
        Mockito.when(envConfig.getStringValue(Mockito.any())).thenReturn("baseUrl", "deviceInfoPath");
        Mockito.doReturn(true).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactDataNotDisassociated(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(factoryData).when(deviceInfoFactoryDataDao).findByFactoryImei(Mockito.anyString());
        deviceAssociationWithFactDataServiceV2.stateChange(stateChangeRequest);
    }

    @Test(expected = NoSuchEntityException.class)
    public void stateChangeTest_Exception() throws Exception {

        DeviceInfoFactoryData factoryData = new DeviceInfoFactoryData();
        factoryData.setId(ID);

        Mockito.when(envConfig.getStringValue(Mockito.any())).thenReturn("baseUrl", "deviceInfoPath");
        Mockito.doReturn(true).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactDataNotDisassociated(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(factoryData).when(deviceInfoFactoryDataDao).findByFactoryImei(Mockito.anyString());
        deviceAssociationWithFactDataServiceV2.stateChange(stateChangeRequest);
    }


    @Test(expected = ApiTechnicalException.class)
    public void stateChangeTest_DiffUserId() throws Exception {

        DeviceInfoFactoryData factoryData = new DeviceInfoFactoryData();
        factoryData.setId(ID);

        String userId = "userID1";
        Mockito.when(envConfig.getStringValue(Mockito.any())).thenReturn(userId);
        Mockito.doReturn(false).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactDataNotDisassociated(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(factoryData).when(deviceInfoFactoryDataDao).findByFactoryImei(Mockito.anyString());
        deviceAssociationWithFactDataServiceV2.stateChange(stateChangeRequest);
    }

    @Test(expected = ApiTechnicalException.class)
    public void stateChangeTest_EmptyInternalUserCsv() throws Exception {

        DeviceInfoFactoryData factoryData = new DeviceInfoFactoryData();
        factoryData.setId(ID);

        Mockito.when(envConfig.getStringValue(Mockito.any())).thenReturn("");
        Mockito.doReturn(false).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactDataNotDisassociated(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(factoryData).when(deviceInfoFactoryDataDao).findByFactoryImei(Mockito.anyString());
        deviceAssociationWithFactDataServiceV2.stateChange(stateChangeRequest);
    }

    @Test(expected = ApiTechnicalException.class)
    public void stateChangeTest_NullInternalUserCsv() throws Exception {

        DeviceInfoFactoryData factoryData = new DeviceInfoFactoryData();
        factoryData.setId(ID);

        Mockito.when(envConfig.getStringValue(Mockito.any())).thenReturn(null);
        Mockito.doReturn(false).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactDataNotDisassociated(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(factoryData).when(deviceInfoFactoryDataDao).findByFactoryImei(Mockito.anyString());
        deviceAssociationWithFactDataServiceV2.stateChange(stateChangeRequest);
    }

    @Test
    public void stateChangeTest_() throws Exception {

        DeviceInfoFactoryData factoryData = new DeviceInfoFactoryData();
        factoryData.setId(ID);

        Mockito.doReturn(new ResponseEntity<>(HttpStatus.OK)).when(restClientLibrary)
            .doPut(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.eq(Object.class));
        Mockito.when(envConfig.getStringValue(Mockito.any())).thenReturn("userID", "baseUrl", "deviceInfoPath");
        Mockito.doReturn(false).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactDataNotDisassociated(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(factoryData).when(deviceInfoFactoryDataDao).findByFactoryImei(Mockito.anyString());
        deviceAssociationWithFactDataServiceV2.stateChange(stateChangeRequest);
    }

    @Test
    public void isInSameStateTest_NullFactoryData() {

        List<FactoryData> fetchFactoryData = new ArrayList<>();
        fetchFactoryData.add(factoryData1);

        Mockito.doReturn(null).when(deviceAssociationDao).constructAndFetchFactoryData(Mockito.any());
        deviceAssociationWithFactDataServiceV2.isInSameState(new AssociateDeviceRequest(), "ACTIVE");
        Assertions.assertFalse(deviceAssociationWithFactDataServiceV2.isInSameState(new AssociateDeviceRequest(),
                "ACTIVE"));
    }

    @Test
    public void isInSameStateTest_EmptyFactoryData() {

        List<FactoryData> fetchFactoryData = new ArrayList<>();

        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao).constructAndFetchFactoryData(Mockito.any());
        deviceAssociationWithFactDataServiceV2.isInSameState(new AssociateDeviceRequest(), "ACTIVE");
        Assertions.assertFalse(deviceAssociationWithFactDataServiceV2.isInSameState(new AssociateDeviceRequest(),
                "ACTIVE"));
    }

    @Test
    public void isInSameStateTest_DiffState() {

        factoryData1.setState("PROVISIONED");
        List<FactoryData> fetchFactoryData = new ArrayList<>();
        fetchFactoryData.add(factoryData1);

        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao).constructAndFetchFactoryData(Mockito.any());
        deviceAssociationWithFactDataServiceV2.isInSameState(new AssociateDeviceRequest(), "ACTIVE");
        Assertions.assertFalse(deviceAssociationWithFactDataServiceV2.isInSameState(new AssociateDeviceRequest(),
                "ACTIVE"));
    }

    @Test
    public void isInSameStateTest_SameState() {

        factoryData1.setState("ACTIVE");
        List<FactoryData> fetchFactoryData = new ArrayList<>();
        fetchFactoryData.add(factoryData1);

        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao).constructAndFetchFactoryData(Mockito.any());
        deviceAssociationWithFactDataServiceV2.isInSameState(new AssociateDeviceRequest(), "ACTIVE");
        Assertions.assertTrue(deviceAssociationWithFactDataServiceV2.isInSameState(new AssociateDeviceRequest(),
                "ACTIVE"));
    }

    @Test
    public void getAssociationHistoryTest() {

        String orderBy = "desc";
        String sortBy = "userid";
        deviceInfoFactoryDataDao.findIdByFactoryImei(Mockito.anyString());
        List<DeviceAssociationHistory> deviceAssociationHistoryList =
                deviceAssociationWithFactDataServiceV2.getAssociationHistory("1234", orderBy,
                        sortBy, 0, 0);
        Assertions.assertNotNull(deviceAssociationHistoryList);
    }

    @Test
    public void getAssociationHistoryTest_BlankSortBy() {

        String orderBy = "desc";
        String sortBy = "";
        List<DeviceAssociationHistory> deviceAssociationHistoryList =
                deviceAssociationWithFactDataServiceV2.getAssociationHistory("1234", orderBy, sortBy,
                        0, 0);
        Assertions.assertNotNull(deviceAssociationHistoryList);
    }

    @Test
    public void getAssociationHistoryTest_SortBy() {

        deviceInfoFactoryDataDao.findIdByFactoryImei(Mockito.anyString());
        List<DeviceAssociationHistory> deviceAssociationHistoryList =
                deviceAssociationWithFactDataServiceV2.getAssociationHistory("1234", "desc",
                        "associationstatus", 0, 0);
        Assertions.assertNotNull(deviceAssociationHistoryList);
    }

    @Test
    public void getAssociationHistoryTest_BlankOrderBy() {

        deviceInfoFactoryDataDao.findIdByFactoryImei(Mockito.anyString());
        List<DeviceAssociationHistory> deviceAssociationHistoryList =
                deviceAssociationWithFactDataServiceV2.getAssociationHistory("1234", "",
                        "associationstatus", 0, 0);
        Assertions.assertNotNull(deviceAssociationHistoryList);
    }

    @Test(expected = ApiResourceNotFoundException.class)
    public void getAssociationHistoryTest_IncorrectResultSizeDataAccessException() {

        Mockito.doThrow(IncorrectResultSizeDataAccessException.class).when(deviceInfoFactoryDataDao)
            .findIdByFactoryImei(Mockito.anyString());
        deviceAssociationWithFactDataServiceV2.getAssociationHistory("1234", "desc", "userid", 0, 0);
    }

    @Test
    public void getAssociationHistoryTotalCountTest() throws NoSuchEntityException {

        deviceInfoFactoryDataDao.findIdByFactoryImei(Mockito.anyString());
        Mockito.doReturn(1).when(deviceAssociationDao).findAssociationCountForFactoryId(Mockito.anyLong());
        int count = deviceAssociationWithFactDataServiceV2.getAssociationHistoryTotalCount("1234");
        Assertions.assertEquals(1, count);
    }

    @Test(expected = NoSuchEntityException.class)
    public void getAssociationHistoryTotalCountTest_Exception() throws NoSuchEntityException {

        Mockito.doThrow(IncorrectResultSizeDataAccessException.class).when(deviceInfoFactoryDataDao)
            .findIdByFactoryImei(Mockito.anyString());
        deviceAssociationWithFactDataServiceV2.getAssociationHistoryTotalCount("1234");
    }

    @Test(expected = ApiValidationFailedException.class)
    public void delegateAssociationTest_DefaultOwner() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "defaultAssociationType", "defaultOwner");

        DelegateAssociationRequest delegateAssociationRequest = new DelegateAssociationRequest();

        deviceAssociationWithFactDataServiceV2.delegateAssociation(delegateAssociationRequest, false);
    }

    @Test(expected = ApiValidationFailedException.class)
    public void delegateAssociationTest_DefaultOwner_DefaultAssociationType() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "defaultAssociationType", "defaultOwner1");

        DelegateAssociationRequest delegateAssociationRequest = new DelegateAssociationRequest();
        delegateAssociationRequest.setAssociationType("defaultOwner1");

        deviceAssociationWithFactDataServiceV2.delegateAssociation(delegateAssociationRequest, false);
    }

    @Test(expected = ApiValidationFailedException.class)
    public void delegateAssociationTest_DefaultOwner_IsAssocTypeExist() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "defaultAssociationType",
            "defaultOwner12");

        DelegateAssociationRequest delegateAssociationRequest = new DelegateAssociationRequest();
        delegateAssociationRequest.setAssociationType("defaultOwner1");

        Mockito.doReturn(false).when(deviceAssociationTypeService).isAssocTypeExist(Mockito.anyString());
        deviceAssociationWithFactDataServiceV2.delegateAssociation(delegateAssociationRequest, false);
    }

    @Test(expected = NullPointerException.class)
    public void delegateAssociationTest_NullDelegateAssociationRequest() {

        DelegateAssociationRequest delegateAssociationRequest = mock(DelegateAssociationRequest.class);
        Mockito.when(delegateAssociationRequest.toString()).thenReturn(null);

        deviceAssociationWithFactDataServiceV2.delegateAssociation(delegateAssociationRequest, false);
    }

    @Test(expected = ApiValidationFailedException.class)
    public void delegateAssociationTest_External() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "defaultAssociationType", "Owner");
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "userIdType", "external");

        DelegateAssociationRequest delegateAssociationRequest = new DelegateAssociationRequest();
        delegateAssociationRequest.setAssociationType("defaultOwner");
        delegateAssociationRequest.setDelegationUserId("");

        Mockito.doReturn(true).when(deviceAssociationTypeService).isAssocTypeExist(Mockito.anyString());
        deviceAssociationWithFactDataServiceV2.delegateAssociation(delegateAssociationRequest, false);
    }

    @Test(expected = ApiValidationFailedException.class)
    public void delegateAssociationTest_Internal_EmptyDelegateUserName() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "defaultAssociationType", "Owner");
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "userIdType", "internal");

        DelegateAssociationRequest delegateAssociationRequest = new DelegateAssociationRequest();
        delegateAssociationRequest.setAssociationType("defaultOwner");
        delegateAssociationRequest.setDelegationUserId("user123");
        delegateAssociationRequest.setEmail("user123@gamil.com");

        Mockito.doReturn("").when(userManagerService)
            .getUserDetail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn(true).when(deviceAssociationTypeService).isAssocTypeExist(Mockito.anyString());
        deviceAssociationWithFactDataServiceV2.delegateAssociation(delegateAssociationRequest, false);
    }

    @Test(expected = ApiValidationFailedException.class)
    public void delegateAssociationTest_Internal_EmptyEmail() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "defaultAssociationType", "Owner");
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "userIdType", "internal");

        DelegateAssociationRequest delegateAssociationRequest = new DelegateAssociationRequest();
        delegateAssociationRequest.setAssociationType("defaultOwner");
        delegateAssociationRequest.setDelegationUserId("");
        delegateAssociationRequest.setEmail("");

        Mockito.doReturn("delegateUserName").when(userManagerService)
            .getUserDetail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn(true).when(deviceAssociationTypeService).isAssocTypeExist(Mockito.anyString());
        deviceAssociationWithFactDataServiceV2.delegateAssociation(delegateAssociationRequest, false);
    }

    @Test(expected = ApiValidationFailedException.class)
    public void delegateAssociationTest_Internal1() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "defaultAssociationType", "Owner");
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "userIdType", "internal1");

        DelegateAssociationRequest delegateAssociationRequest = new DelegateAssociationRequest();
        delegateAssociationRequest.setAssociationType("defaultOwner");
        delegateAssociationRequest.setDelegationUserId("user123");
        delegateAssociationRequest.setEmail("user123@gamil.com");

        Mockito.doReturn(true).when(deviceAssociationTypeService).isAssocTypeExist(Mockito.anyString());
        deviceAssociationWithFactDataServiceV2.delegateAssociation(delegateAssociationRequest, false);
    }

    @Test(expected = ApiValidationFailedException.class)
    public void delegateAssociationTest_ZeroStartTime() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "defaultAssociationType", "Owner");
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "userIdType", "internal");

        DelegateAssociationRequest delegateAssociationRequest = new DelegateAssociationRequest();
        delegateAssociationRequest.setAssociationType("defaultOwner");
        delegateAssociationRequest.setDelegationUserId("user123");
        delegateAssociationRequest.setEmail("user123@gamil.com");
        delegateAssociationRequest.setEndTimestamp(System.currentTimeMillis());
        delegateAssociationRequest.setStartTimestamp(0L);

        Mockito.doReturn("delegateUserName").when(userManagerService)
            .getUserDetail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn(true).when(deviceAssociationTypeService).isAssocTypeExist(Mockito.anyString());
        deviceAssociationWithFactDataServiceV2.delegateAssociation(delegateAssociationRequest, false);
    }

    @Test(expected = ApiValidationFailedException.class)
    public void delegateAssociationTest_Time() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "defaultAssociationType", "Owner");
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "userIdType", "internal");

        DelegateAssociationRequest delegateAssociationRequest = new DelegateAssociationRequest();
        delegateAssociationRequest.setAssociationType("defaultOwner");
        delegateAssociationRequest.setDelegationUserId("user123");
        delegateAssociationRequest.setEmail("user123@gamil.com");
        delegateAssociationRequest.setEndTimestamp(System.currentTimeMillis() + System.currentTimeMillis());
        delegateAssociationRequest.setStartTimestamp(0L);

        Mockito.doReturn("delegateUserName").when(userManagerService)
            .getUserDetail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn(true).when(deviceAssociationTypeService).isAssocTypeExist(Mockito.anyString());
        deviceAssociationWithFactDataServiceV2.delegateAssociation(delegateAssociationRequest, false);
    }

    @Test(expected = ApiValidationFailedException.class)
    public void delegateAssociationTest_TrueIsAdmin() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "defaultAssociationType", "Owner");
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "userIdType", "internal");

        DelegateAssociationRequest delegateAssociationRequest = new DelegateAssociationRequest();
        delegateAssociationRequest.setAssociationType("defaultOwner");
        delegateAssociationRequest.setDelegationUserId("user123");
        delegateAssociationRequest.setEmail("user123@gamil.com");
        delegateAssociationRequest.setEndTimestamp(System.currentTimeMillis() + System.currentTimeMillis());
        delegateAssociationRequest.setStartTimestamp(0L);

        Mockito.doReturn(false).when(deviceAssociationDao).validUserAssociation(Mockito.any());
        Mockito.doReturn("delegateUserName").when(userManagerService)
            .getUserDetail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn(true).when(deviceAssociationTypeService).isAssocTypeExist(Mockito.anyString());
        deviceAssociationWithFactDataServiceV2.delegateAssociation(delegateAssociationRequest, true);
    }

    @Test
    public void delegateAssociationTest() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "defaultAssociationType", "Owner");
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "userIdType", "internal");

        DelegateAssociationRequest delegateAssociationRequest = new DelegateAssociationRequest();
        delegateAssociationRequest.setAssociationType("defaultOwner");
        delegateAssociationRequest.setDelegationUserId("user123");
        delegateAssociationRequest.setUserId("user123444");
        delegateAssociationRequest.setEmail("user123@gamil.com");
        delegateAssociationRequest.setEndTimestamp(System.currentTimeMillis() + System.currentTimeMillis());
        delegateAssociationRequest.setStartTimestamp(0L);
        delegateAssociationRequest.setDelegationUserId("other123");
        delegateAssociationRequest.setBssid("12345");
        delegateAssociationRequest.setImei("12345");
        delegateAssociationRequest.setSerialNumber("12345");
        delegateAssociationRequest.setIccid("12345");
        delegateAssociationRequest.setImsi("12345");
        delegateAssociationRequest.setMsisdn("12345");
        delegateAssociationRequest.setSsid("12345");

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setAssociatedBy(delegateAssociationRequest.getUserId());
        deviceAssociation.setAssociatedOn(new Timestamp(System.currentTimeMillis()));
        deviceAssociation.setModifiedOn(new Timestamp(System.currentTimeMillis()));
        deviceAssociation.setModifiedBy(delegateAssociationRequest.getUserId());
        deviceAssociation.setUserId(delegateAssociationRequest.getDelegationUserId());
        deviceAssociation.setStartTimeStamp(delegateAssociationRequest.getStartTimestamp());
        deviceAssociation.setEndTimeStamp(delegateAssociationRequest.getEndTimestamp());
        deviceAssociation.setAssociationType(delegateAssociationRequest.getAssociationType());

        Mockito.doNothing().when(deviceAssociationDao).insertM2M(deviceAssociation);
        Mockito.doReturn(deviceAssociation).when(deviceAssociationDao).findAssociation(Mockito.any());
        Mockito.doReturn(true).when(deviceAssociationDao).validUserAssociation(Mockito.any());
        Mockito.doReturn("delegateUserName").when(userManagerService)
            .getUserDetail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn(true).when(deviceAssociationTypeService).isAssocTypeExist(Mockito.anyString());
        AssociateDeviceResponse associateDeviceResponse =
                deviceAssociationWithFactDataServiceV2.delegateAssociation(delegateAssociationRequest, false);
        Assert.assertNotNull(associateDeviceResponse);
    }

    @Test(expected = ApiValidationFailedException.class)
    public void associateDeviceForSelfTest_EmptyAssociateDeviceRequest() {

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setBssid("");
        associateDeviceRequest.setImei("");
        associateDeviceRequest.setSerialNumber("");

        deviceAssociationWithFactDataServiceV2.associateDeviceForSelf(associateDeviceRequest, "adminUserId");
    }

    //@Test(expected = ApiValidationFailedException.class)
    //public void associateDeviceForSelfTest_NullDelegateAssociationRequest(){
    //
    //    AssociateDeviceRequest associateDeviceRequest = mock(AssociateDeviceRequest.class);
    //    Mockito.when(associateDeviceRequest.toString()).thenReturn(null);
    //
    //    deviceAssociationWithFactDataServiceV2.associateDeviceForSelf(associateDeviceRequest,"adminUserId");
    //}

    @Test(expected = ApiValidationFailedException.class)
    public void associateDeviceForSelfTest_EmptyFetchFactoryData() {

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setBssid("1234");
        associateDeviceRequest.setImei("2345");
        associateDeviceRequest.setSerialNumber("7368729");
        associateDeviceRequest.setUserId("user123");

        List<FactoryData> fetchFactoryData = new ArrayList<>();

        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao).constructAndFetchFactoryData(Mockito.any());
        deviceAssociationWithFactDataServiceV2.associateDeviceForSelf(associateDeviceRequest, "adminUserId");
    }

    @Test(expected = ApiValidationFailedException.class)
    public void associateDeviceForSelfTest_NullFetchFactoryData() {

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setBssid("1234");
        associateDeviceRequest.setImei("2345");
        associateDeviceRequest.setSerialNumber("7368729");
        associateDeviceRequest.setUserId("user123");

        List<FactoryData> fetchFactoryData = new ArrayList<>();
        fetchFactoryData.add(null);


        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao).constructAndFetchFactoryData(Mockito.any());
        deviceAssociationWithFactDataServiceV2.associateDeviceForSelf(associateDeviceRequest, "adminUserId");
    }

    @Test(expected = ApiValidationFailedException.class)
    public void associateDeviceForSelfTest_ZeroIdFetchFactoryData() {

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setBssid("1234");
        associateDeviceRequest.setImei("2345");
        associateDeviceRequest.setSerialNumber("7368729");
        associateDeviceRequest.setUserId("user123");

        List<FactoryData> fetchFactoryData = new ArrayList<>();
        FactoryData factoryData = new FactoryData();
        factoryData.setId(0L);
        fetchFactoryData.add(factoryData);


        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao).constructAndFetchFactoryData(Mockito.any());
        deviceAssociationWithFactDataServiceV2.associateDeviceForSelf(associateDeviceRequest, "adminUserId");
    }

    @Test(expected = ApiValidationFailedException.class)
    public void associateDeviceForSelfTest_Faulty() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "forbidAssocAfterTerminate", false);

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setBssid("1234");
        associateDeviceRequest.setImei("2345");
        associateDeviceRequest.setSerialNumber("7368729");
        associateDeviceRequest.setUserId("user123");

        List<FactoryData> fetchFactoryData = new ArrayList<>();
        factoryData1.setState("Active");
        factoryData1.setFaulty(true);
        fetchFactoryData.add(factoryData1);

        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao).constructAndFetchFactoryData(Mockito.any());
        deviceAssociationWithFactDataServiceV2.associateDeviceForSelf(associateDeviceRequest, "adminUserId");
    }

    @Test(expected = ApiValidationFailedException.class)
    public void associateDeviceForSelfTest_Stolen() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "forbidAssocAfterTerminate", false);

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setBssid("1234");
        associateDeviceRequest.setImei("2345");
        associateDeviceRequest.setSerialNumber("7368729");
        associateDeviceRequest.setUserId("user123");

        List<FactoryData> fetchFactoryData = new ArrayList<>();
        factoryData1.setState("Active");
        factoryData1.setStolen(true);
        fetchFactoryData.add(factoryData1);

        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao).constructAndFetchFactoryData(Mockito.any());
        deviceAssociationWithFactDataServiceV2.associateDeviceForSelf(associateDeviceRequest, "adminUserId");
    }

    @Test(expected = ApiValidationFailedException.class)
    public void associateDeviceForSelfTest_EmptyState() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "forbidAssocAfterTerminate", true);

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setBssid("1234");
        associateDeviceRequest.setImei("2345");
        associateDeviceRequest.setSerialNumber("7368729");
        associateDeviceRequest.setUserId("user123");

        List<FactoryData> fetchFactoryData = new ArrayList<>();
        factoryData1.setState("");
        factoryData1.setStolen(false);
        fetchFactoryData.add(factoryData1);

        List<AssociationDetailsResponse> associationDetailsResponseList = new ArrayList<>();
        AssociationDetailsResponse associationDetailsResponse = new AssociationDetailsResponse();
        associationDetailsResponse.setUserId("responseuser123");
        associationDetailsResponseList.add(associationDetailsResponse);


        Mockito.doReturn(associationDetailsResponseList).when(deviceAssociationDao)
            .fetchAssociationDetails(Mockito.any(), Mockito.anyBoolean());

        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao).constructAndFetchFactoryData(Mockito.any());
        deviceAssociationWithFactDataServiceV2.associateDeviceForSelf(associateDeviceRequest, "adminUserId");
    }

    @Test(expected = ApiPreConditionFailedException.class)
    public void associateDeviceForSelfTest_TrueIsTerminated() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "forbidAssocAfterTerminate", true);

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setBssid("1234");
        associateDeviceRequest.setImei("2345");
        associateDeviceRequest.setSerialNumber("7368729");
        associateDeviceRequest.setUserId("user123");

        List<FactoryData> fetchFactoryData = new ArrayList<>();
        factoryData1.setState("PROVISIONED");
        factoryData1.setStolen(false);
        fetchFactoryData.add(factoryData1);

        Mockito.doReturn(true).when(deviceAssociationDao).isDeviceTerminated(fetchFactoryData.get(0).getId());
        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao).constructAndFetchFactoryData(Mockito.any());
        deviceAssociationWithFactDataServiceV2.associateDeviceForSelf(associateDeviceRequest, "adminUserId");
    }

    @Test(expected = ApiValidationFailedException.class)
    public void associateDeviceForSelfTest_Provisioned_Alive_ApiValidationFailedException() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "forbidAssocAfterTerminate", true);
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "defaultAssociationType", "defaultOwner1");

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setBssid("1234");
        associateDeviceRequest.setImei("2345");
        associateDeviceRequest.setSerialNumber("7368729");
        associateDeviceRequest.setUserId("user123");

        List<FactoryData> fetchFactoryData = new ArrayList<>();
        factoryData1.setState("PROVISIONED_ALIVE");
        factoryData1.setStolen(false);
        fetchFactoryData.add(factoryData1);

        Mockito.doReturn(false).when(deviceAssociationTypeService).isAssocTypeExist(Mockito.any());
        Mockito.doReturn(false).when(deviceAssociationDao).isDeviceTerminated(Mockito.anyLong());
        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao).constructAndFetchFactoryData(Mockito.any());
        deviceAssociationWithFactDataServiceV2.associateDeviceForSelf(associateDeviceRequest, "adminUserId");
    }

    @Test
    public void associateDeviceForSelfTest_Provisioned_Alive() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "forbidAssocAfterTerminate", true);
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "defaultAssociationType", "defaultOwner");

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setBssid("1234");
        associateDeviceRequest.setImei("2345");
        associateDeviceRequest.setSerialNumber("7368729");
        associateDeviceRequest.setUserId("user123");

        List<FactoryData> fetchFactoryData = new ArrayList<>();
        factoryData1.setState("PROVISIONED_ALIVE");
        factoryData1.setStolen(false);
        fetchFactoryData.add(factoryData1);

        deviceAssociationDao.insert(Mockito.any());
        deviceAssociationDao.insertDeviceState(Mockito.any());
        deviceInfoFactoryDataDao.changeDeviceState(Mockito.anyLong(), Mockito.anyString(), Mockito.any());
        Mockito.doReturn(false).when(deviceAssociationDao).isDeviceTerminated(Mockito.anyLong());
        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao).constructAndFetchFactoryData(Mockito.any());
        AssociateDeviceResponse associateDeviceResponse =
                deviceAssociationWithFactDataServiceV2.associateDeviceForSelf(associateDeviceRequest,
                        "adminUserId");
        Assertions.assertNotNull(associateDeviceResponse);
    }

    @Test
    public void associateDeviceForSelfTest_EmptyAdminUserId() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "forbidAssocAfterTerminate", true);
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "defaultAssociationType", "defaultOwner1");

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setBssid("1234");
        associateDeviceRequest.setImei("2345");
        associateDeviceRequest.setSerialNumber("7368729");
        associateDeviceRequest.setUserId("user123");

        List<FactoryData> fetchFactoryData = new ArrayList<>();
        factoryData1.setState("PROVISIONED");
        factoryData1.setStolen(false);
        fetchFactoryData.add(factoryData1);

        deviceAssociationDao.insert(Mockito.any());
        deviceAssociationDao.insertDeviceState(Mockito.any());
        deviceInfoFactoryDataDao.changeDeviceState(Mockito.anyLong(), Mockito.anyString(), Mockito.any());
        Mockito.doReturn(true).when(deviceAssociationTypeService).isAssocTypeExist(Mockito.any());
        Mockito.doReturn(false).when(deviceAssociationDao).isDeviceTerminated(fetchFactoryData.get(0).getId());
        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao).constructAndFetchFactoryData(Mockito.any());
        AssociateDeviceResponse associateDeviceResponse =
                deviceAssociationWithFactDataServiceV2.associateDeviceForSelf(associateDeviceRequest, "");
        Assertions.assertNotNull(associateDeviceResponse);
    }

    @Test
    public void associateDeviceForSelfTest() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "forbidAssocAfterTerminate", true);
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "defaultAssociationType", "defaultOwner");

        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setBssid("1234");
        associateDeviceRequest.setImei("2345");
        associateDeviceRequest.setSerialNumber("7368729");
        associateDeviceRequest.setUserId("user123");

        List<FactoryData> fetchFactoryData = new ArrayList<>();
        factoryData1.setState("PROVISIONED");
        factoryData1.setStolen(false);
        fetchFactoryData.add(factoryData1);

        deviceAssociationDao.insert(Mockito.any());
        deviceAssociationDao.insertDeviceState(Mockito.any());
        deviceInfoFactoryDataDao.changeDeviceState(Mockito.anyLong(), Mockito.anyString(), Mockito.any());
        Mockito.doReturn(false).when(deviceAssociationDao).isDeviceTerminated(fetchFactoryData.get(0).getId());
        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao).constructAndFetchFactoryData(Mockito.any());
        AssociateDeviceResponse associateDeviceResponse =
                deviceAssociationWithFactDataServiceV2.associateDeviceForSelf(associateDeviceRequest, "adminUserId");
        Assertions.assertNotNull(associateDeviceResponse);
    }

    //@Test
    //public void terminateM2MassociationTest_NullRequest(){
    //
    //    DeviceStatusRequest deviceStatusRequest = mock(DeviceStatusRequest.class);
    //    Mockito.doReturn(null).when(deviceStatusRequest).toString();
    //    assertThrows(ApiValidationFailedException.class, () ->
    //    deviceAssociationWithFactDataServiceV2.terminateM2MAssociation(deviceStatusRequest,"userIdFromHeader",
    //    "adminUserId", true));
    //}

    @Test(expected = ApiPreConditionFailedException.class)
    public void terminateM2MassociationTest_MoreSize() {

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setDeviceId("deviceID");
        deviceStatusRequest.setImei("1234");
        deviceStatusRequest.setSerialNumber("567890");
        deviceStatusRequest.setAssociationId(ID);
        deviceStatusRequest.setUserId("userID");


        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociationList.add(deviceAssociation);
        deviceAssociationList.add(deviceAssociation);

        Mockito.doReturn(deviceAssociationList).when(deviceAssociationDao)
            .constructAndFetchDeviceAssociationData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong(), Mockito.anyList());
        deviceAssociationWithFactDataServiceV2.terminateM2Massociation(deviceStatusRequest, "userIdFromHeader",
            "adminUserId", true);
    }

    @Test(expected = ApiPreConditionFailedException.class)
    public void terminateM2MassociationTest_CompletedActivateStatus() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "defaultAssociationType", "Owner");
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "simSuspendCheck", true);

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setDeviceId("deviceID");
        deviceStatusRequest.setImei("1234");
        deviceStatusRequest.setSerialNumber("567890");
        deviceStatusRequest.setAssociationId(ID);
        deviceStatusRequest.setUserId("userID");

        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setAssociationType("Owner");
        deviceAssociationList.add(deviceAssociation);

        Mockito.doReturn("status").when(deviceAssociationDao).getActivateTranStatus(deviceAssociation.getId());
        Mockito.doReturn(deviceAssociationList).when(deviceAssociationDao)
            .constructAndFetchDeviceAssociationData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong(), Mockito.anyList());
        deviceAssociationWithFactDataServiceV2.terminateM2Massociation(deviceStatusRequest, "userIdFromHeader",
            "adminUserId", true);
    }

    @Test(expected = ApiPreConditionFailedException.class)
    public void terminateM2MassociationTest_CompletedTerminateStatus() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "defaultAssociationType", "Owner");
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "simSuspendCheck", true);

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setDeviceId("deviceID");
        deviceStatusRequest.setImei("1234");
        deviceStatusRequest.setSerialNumber("567890");
        deviceStatusRequest.setAssociationId(ID);
        deviceStatusRequest.setUserId("userID");

        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setAssociationType("Owner");
        deviceAssociationList.add(deviceAssociation);

        Mockito.doReturn("status").when(deviceAssociationDao).getTerminateTranStatus(deviceAssociation.getId());
        Mockito.doReturn("Completed").when(deviceAssociationDao).getActivateTranStatus(deviceAssociation.getId());
        Mockito.doReturn(deviceAssociationList).when(deviceAssociationDao)
            .constructAndFetchDeviceAssociationData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong(), Mockito.anyList());
        deviceAssociationWithFactDataServiceV2.terminateM2Massociation(deviceStatusRequest, "userIdFromHeader",
            "adminUserId", true);
    }

    @Test
    public void terminateM2MassociationTest_NullUserProfile()
        throws ObserverMessageProcessFailureException {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "defaultAssociationType", "Owner");
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "simSuspendCheck", true);

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setDeviceId("deviceID");
        deviceStatusRequest.setImei("1234");
        deviceStatusRequest.setSerialNumber("567890");
        deviceStatusRequest.setAssociationId(ID);
        deviceStatusRequest.setUserId("userID");
        deviceStatusRequest.setRequiredFor("wipe");

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setAssociationType("Owner");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATION_INITIATED);
        deviceAssociation.setHarmanId("HarmanId");
        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();
        deviceAssociationList.add(deviceAssociation);

        observable.notify(Mockito.any());
        springAuthRestClient.deleteRegisteredClient(Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn(null).when(ncClient).getUserProfile(Mockito.anyString(), Mockito.any());
        Mockito.doReturn("Completed").when(deviceAssociationDao).getTerminateTranStatus(deviceAssociation.getId());
        Mockito.doReturn("Completed").when(deviceAssociationDao).getActivateTranStatus(deviceAssociation.getId());
        Mockito.doReturn(deviceAssociationList).when(deviceAssociationDao)
            .constructAndFetchDeviceAssociationData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong(), Mockito.anyList());
        int count = deviceAssociationWithFactDataServiceV2.terminateM2Massociation(deviceStatusRequest,
                "userIdFromHeader", "adminUserId", true);
        Assertions.assertEquals(0, count);
    }

    @Test
    public void terminateM2MassociationTest_DiffAssociationStatus()
        throws ObserverMessageProcessFailureException {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "defaultAssociationType", "Owner");
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "simSuspendCheck", true);

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setDeviceId("deviceID");
        deviceStatusRequest.setImei("1234");
        deviceStatusRequest.setSerialNumber("567890");
        deviceStatusRequest.setAssociationId(ID);
        deviceStatusRequest.setUserId("userID");
        deviceStatusRequest.setRequiredFor("wipe");

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setAssociationType("Owner");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATION_INITIATED);
        deviceAssociation.setHarmanId("HarmanId");
        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();
        deviceAssociationList.add(deviceAssociation);

        ncClient.callNotifCenterNonRegisteredUserApi(Mockito.any(), Mockito.anyString(), Mockito.any());
        observable.notify(Mockito.any());
        springAuthRestClient.deleteRegisteredClient(Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn(new UserProfile()).when(ncClient).getUserProfile(Mockito.anyString(), Mockito.any());
        Mockito.doReturn("Completed").when(deviceAssociationDao).getTerminateTranStatus(deviceAssociation.getId());
        Mockito.doReturn("Completed").when(deviceAssociationDao).getActivateTranStatus(deviceAssociation.getId());
        Mockito.doReturn(deviceAssociationList).when(deviceAssociationDao)
            .constructAndFetchDeviceAssociationData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong(), Mockito.anyList());
        int count = deviceAssociationWithFactDataServiceV2.terminateM2Massociation(deviceStatusRequest,
                "userIdFromHeader", "adminUserId", true);
        Assertions.assertEquals(0, count);
    }

    @Test
    public void terminateM2MassociationTest_TrueSimSuspendCheck()
        throws ObserverMessageProcessFailureException {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "defaultAssociationType", "Owner");
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "simSuspendCheck", true);

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setDeviceId("deviceID");
        deviceStatusRequest.setImei("1234");
        deviceStatusRequest.setSerialNumber("567890");
        deviceStatusRequest.setAssociationId(ID);
        deviceStatusRequest.setUserId("userID");
        deviceStatusRequest.setRequiredFor("wipe");

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setAssociationType("Owner");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        deviceAssociation.setHarmanId("HarmanId");
        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();
        deviceAssociationList.add(deviceAssociation);

        ncClient.callNotifCenterNonRegisteredUserApi(Mockito.any(), Mockito.anyString(), Mockito.any());
        observable.notify(Mockito.any());
        springAuthRestClient.deleteRegisteredClient(Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn(new UserProfile()).when(ncClient).getUserProfile(Mockito.anyString(), Mockito.any());
        Mockito.doReturn("Completed").when(deviceAssociationDao).getTerminateTranStatus(deviceAssociation.getId());
        Mockito.doReturn("Completed").when(deviceAssociationDao).getActivateTranStatus(deviceAssociation.getId());
        Mockito.doReturn(deviceAssociationList).when(deviceAssociationDao)
            .constructAndFetchDeviceAssociationData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong(), Mockito.anyList());
        int count = deviceAssociationWithFactDataServiceV2.terminateM2Massociation(deviceStatusRequest,
                "userIdFromHeader", "adminUserId", true);
        Assertions.assertEquals(0, count);
    }

    @Test(expected = ApiNotificationException.class)
    public void terminateM2MassociationTest_ObserverMessageProcessFailureException()
        throws ObserverMessageProcessFailureException {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "defaultAssociationType", "Owner");
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "simSuspendCheck", true);

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setDeviceId("deviceID");
        deviceStatusRequest.setImei("1234");
        deviceStatusRequest.setSerialNumber("567890");
        deviceStatusRequest.setAssociationId(ID);
        deviceStatusRequest.setUserId("userID");
        deviceStatusRequest.setRequiredFor("wipe");

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setAssociationType("Owner");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATION_INITIATED);
        deviceAssociation.setHarmanId("HarmanId");
        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();
        deviceAssociationList.add(deviceAssociation);

        Mockito.doThrow(ObserverMessageProcessFailureException.class).when(observable).notify(Mockito.any());
        springAuthRestClient.deleteRegisteredClient(Mockito.anyString(), Mockito.anyString());
        deviceAssociationDao.updateForM2MdisassociationById(deviceAssociation);
        Mockito.doReturn(new UserProfile()).when(ncClient).getUserProfile(Mockito.anyString(), Mockito.any());
        Mockito.doReturn("Completed").when(deviceAssociationDao).getTerminateTranStatus(deviceAssociation.getId());
        Mockito.doReturn("Completed").when(deviceAssociationDao).getActivateTranStatus(deviceAssociation.getId());
        Mockito.doReturn(deviceAssociationList).when(deviceAssociationDao)
            .constructAndFetchDeviceAssociationData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong(), Mockito.anyList());
        deviceAssociationWithFactDataServiceV2.terminateM2Massociation(deviceStatusRequest, "userIdFromHeader",
            "adminUserId", true);
    }


    @Test
    public void terminateM2MassociationTest_FalseSimSuspendCheck()
        throws ObserverMessageProcessFailureException {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "defaultAssociationType", "Owner");
        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "simSuspendCheck", false);

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setDeviceId("deviceID");
        deviceStatusRequest.setImei("1234");
        deviceStatusRequest.setSerialNumber("567890");
        deviceStatusRequest.setAssociationId(ID);
        deviceStatusRequest.setUserId("userID");

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setAssociationType("Owner");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATION_INITIATED);
        deviceAssociation.setHarmanId("HarmanId");
        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();
        deviceAssociationList.add(deviceAssociation);

        ncClient.callNotifCenterNonRegisteredUserApi(Mockito.any(), Mockito.anyString(), Mockito.any());
        observable.notify(Mockito.any());
        springAuthRestClient.deleteRegisteredClient(Mockito.anyString(), Mockito.anyString());
        // Mockito.doReturn(COUNT).when(deviceAssociationDao).updateForM2MdisassociationById(deviceAssociation);
        Mockito.doReturn(new UserProfile()).when(ncClient).getUserProfile(Mockito.anyString(), Mockito.any());
        Mockito.doReturn(deviceAssociationList).when(deviceAssociationDao)
            .constructAndFetchDeviceAssociationData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong(), Mockito.anyList());
        int count = deviceAssociationWithFactDataServiceV2.terminateM2Massociation(deviceStatusRequest,
                "userIdFromHeader", "adminUserId", true);
        Assertions.assertEquals(0, count);
    }

    @Test
    public void terminateM2MassociationTest() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "defaultAssociationType", "Owner1");

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setDeviceId("deviceID");
        deviceStatusRequest.setImei("1234");
        deviceStatusRequest.setSerialNumber("567890");
        deviceStatusRequest.setAssociationId(ID);
        deviceStatusRequest.setUserId("userID");

        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setAssociationType("Owner");
        deviceAssociationList.add(deviceAssociation);

        Mockito.doReturn(COUNT).when(deviceAssociationDao).updateForM2MdisassociationById(deviceAssociation);
        Mockito.doReturn(deviceAssociationList).when(deviceAssociationDao)
            .constructAndFetchDeviceAssociationData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong(), Mockito.anyList());
        int count = deviceAssociationWithFactDataServiceV2.terminateM2Massociation(deviceStatusRequest,
                "userIdFromHeader", "adminUserId", true);
        Assertions.assertEquals(COUNT, count);
    }

    @Test(expected = ApiValidationFailedException.class)
    public void validatePerformTerminateTest_EmptyDeviceStatusRequest() {

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setDeviceId("");
        deviceStatusRequest.setImei("");
        deviceStatusRequest.setSerialNumber("");
        deviceStatusRequest.setAssociationId(ID);
        deviceStatusRequest.setUserId("userID");

        deviceAssociationWithFactDataServiceV2.validatePerformTerminate("userId", deviceStatusRequest, true);
    }

    @Test(expected = ApiValidationFailedException.class)
    public void validatePerformTerminateTest_IsOwner() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "defaultAssociationType", "DefaultOwner");

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setDeviceId("deviceID");
        deviceStatusRequest.setImei("1234");
        deviceStatusRequest.setSerialNumber("567890");
        deviceStatusRequest.setAssociationId(ID);
        deviceStatusRequest.setUserId("userID123");

        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setAssociationType("Owner");
        deviceAssociationList.add(deviceAssociation);

        deviceAssociationWithFactDataServiceV2.validatePerformTerminate("userId", deviceStatusRequest, false);
    }

    @Test(expected = ApiPreConditionFailedException.class)
    public void validatePerformTerminateTest_MoreDeviceAssociation() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "defaultAssociationType", "Owner");

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setDeviceId("deviceID");
        deviceStatusRequest.setImei("1234");
        deviceStatusRequest.setSerialNumber("567890");
        deviceStatusRequest.setAssociationId(ID);
        deviceStatusRequest.setUserId("userID");

        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setAssociationType("Owner");
        deviceAssociationList.add(deviceAssociation);
        deviceAssociationList.add(deviceAssociation);

        Mockito.when(
                deviceAssociationDao.constructAndFetchDeviceAssociationData(Mockito.anyString(), Mockito.anyString(),
                    Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyList()))
            .thenReturn(deviceAssociationList);
        deviceAssociationWithFactDataServiceV2.validatePerformTerminate("userId", deviceStatusRequest, false);
    }

    @Test
    public void validatePerformTerminateTest() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "defaultAssociationType", "Owner");

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setDeviceId("deviceID");
        deviceStatusRequest.setImei("1234");
        deviceStatusRequest.setSerialNumber("567890");
        deviceStatusRequest.setAssociationId(ID);
        deviceStatusRequest.setUserId("userID");

        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setAssociationType("Owner");
        deviceAssociationList.add(deviceAssociation);

        List<DeviceAssociation> deviceAssociationList1 = new ArrayList<>();
        DeviceAssociation deviceAssociation1 = new DeviceAssociation();
        deviceAssociation1.setId(ID);
        deviceAssociation1.setAssociationType("Owner");
        deviceAssociationList1.add(deviceAssociation);

        Mockito.when(
                deviceAssociationDao.constructAndFetchDeviceAssociationData(Mockito.anyString(), Mockito.anyString(),
                    Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyList()))
            .thenReturn(deviceAssociationList, deviceAssociationList1);
        deviceAssociationWithFactDataServiceV2.validatePerformTerminate("userId", deviceStatusRequest, false);
    }

    @Test
    public void validatePerformTerminateTest_EmptyAssociationType() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "defaultAssociationType", "Owner");

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setDeviceId("deviceID");
        deviceStatusRequest.setImei("1234");
        deviceStatusRequest.setSerialNumber("567890");
        deviceStatusRequest.setAssociationId(ID);
        deviceStatusRequest.setUserId("userID");

        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setAssociationType("Default");
        deviceAssociationList.add(deviceAssociation);

        Mockito.when(
                deviceAssociationDao.constructAndFetchDeviceAssociationData(Mockito.anyString(), Mockito.anyString(),
                    Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyList()))
            .thenReturn(deviceAssociationList);
        deviceAssociationWithFactDataServiceV2.validatePerformTerminate("userId", deviceStatusRequest, false);
    }

    @Test
    public void validatePerformTerminateTest_DiffAssociationType() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "defaultAssociationType", "Owner");

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setDeviceId("deviceID");
        deviceStatusRequest.setImei("1234");
        deviceStatusRequest.setSerialNumber("567890");
        deviceStatusRequest.setAssociationId(ID);
        deviceStatusRequest.setUserId("userID");

        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setAssociationType("Owner");
        deviceAssociationList.add(deviceAssociation);

        List<DeviceAssociation> deviceAssociationList1 = new ArrayList<>();
        DeviceAssociation deviceAssociation1 = new DeviceAssociation();
        deviceAssociation1.setId(ID);
        deviceAssociation1.setAssociationType("DefaultOwner");
        deviceAssociationList1.add(deviceAssociation);

        Mockito.when(
                deviceAssociationDao.constructAndFetchDeviceAssociationData(Mockito.anyString(), Mockito.anyString(),
                    Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyList()))
            .thenReturn(deviceAssociationList, deviceAssociationList1);
        deviceAssociationWithFactDataServiceV2.validatePerformTerminate("userId", deviceStatusRequest, false);
    }

    @Test
    public void validateAdminRequestTest() {

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setUserId("userID");

        deviceAssociationWithFactDataServiceV2.validateAdminRequest(deviceStatusRequest);
    }

    @Test
    public void validateAdminRequestTest_EmptyUserId() {

        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setUserId("");

        deviceAssociationWithFactDataServiceV2.validateAdminRequest(deviceStatusRequest);
    }

    @Test
    public void getAssociationTypeUsageCountTest_False() {

        Mockito.doReturn(1).when(deviceAssociationDao).getAssociationTypeUsageCount(Mockito.anyString());
        assertNotNull(deviceAssociationWithFactDataServiceV2.getAssociationTypeUsageCount("Owner", "userId"));
    }

    @Test(expected = ApiResourceNotFoundException.class)
    public void updateAssociationTest_NullDeviceAssociation() {

        AssociationUpdateRequest associationUpdateRequest = new AssociationUpdateRequest();
        associationUpdateRequest.setAssocType("vehicle Owner");
        associationUpdateRequest.setEndTime(System.currentTimeMillis());
        associationUpdateRequest.setStartTime(System.currentTimeMillis());

        AssociationUpdateDto associationUpdateDto = new AssociationUpdateDto();
        associationUpdateDto.setAssocId(ID);
        associationUpdateDto.setUserId("user677");
        associationUpdateDto.setAssociationUpdateRequest(associationUpdateRequest);

        Mockito.doReturn(null).when(deviceAssociationDao).fetchAssociationById(Mockito.anyLong());
        deviceAssociationWithFactDataServiceV2.updateAssociation(associationUpdateDto);
    }

    @Test(expected = ApiValidationFailedException.class)
    public void updateAssociationTest_WrongDeviceAssociationTime() {

        AssociationUpdateRequest associationUpdateRequest = new AssociationUpdateRequest();
        associationUpdateRequest.setAssocType("vehicle Owner");
        associationUpdateRequest.setEndTime(System.currentTimeMillis());
        associationUpdateRequest.setStartTime(System.currentTimeMillis());

        AssociationUpdateDto associationUpdateDto = new AssociationUpdateDto();
        associationUpdateDto.setAssocId(ID);
        associationUpdateDto.setUserId("user677");
        associationUpdateDto.setAssociationUpdateRequest(associationUpdateRequest);

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setAssociationType("Owner");
        deviceAssociation.setSerialNumber("12345");
        deviceAssociation.setStartTimeStamp(System.currentTimeMillis());
        deviceAssociation.setEndTimeStamp(System.currentTimeMillis());

        Mockito.doReturn(deviceAssociation).when(deviceAssociationDao).fetchAssociationById(Mockito.anyLong());
        deviceAssociationWithFactDataServiceV2.updateAssociation(associationUpdateDto);
    }

    @Test(expected = ApiValidationFailedException.class)
    public void updateAssociationTest_WrongAssociationRequestTime() {

        AssociationUpdateRequest associationUpdateRequest = new AssociationUpdateRequest();
        associationUpdateRequest.setAssocType("vehicle Owner");
        associationUpdateRequest.setEndTime(System.currentTimeMillis());
        associationUpdateRequest.setStartTime(System.currentTimeMillis());

        AssociationUpdateDto associationUpdateDto = new AssociationUpdateDto();
        associationUpdateDto.setAssocId(ID);
        associationUpdateDto.setUserId("user677");
        associationUpdateDto.setAssociationUpdateRequest(associationUpdateRequest);

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setAssociationType("Owner");
        deviceAssociation.setSerialNumber("12345");
        deviceAssociation.setStartTimeStamp(System.currentTimeMillis());
        deviceAssociation.setEndTimeStamp(System.currentTimeMillis() + System.currentTimeMillis());

        Mockito.doReturn(deviceAssociation).when(deviceAssociationDao).fetchAssociationById(Mockito.anyLong());
        deviceAssociationWithFactDataServiceV2.updateAssociation(associationUpdateDto);
    }

    @Test(expected = ApiValidationFailedException.class)
    public void updateAssociationTest_ZeroTime() {

        AssociationUpdateRequest associationUpdateRequest = new AssociationUpdateRequest();
        associationUpdateRequest.setAssocType("vehicle Owner");
        associationUpdateRequest.setEndTime(1L);
        associationUpdateRequest.setStartTime(0L);

        AssociationUpdateDto associationUpdateDto = new AssociationUpdateDto();
        associationUpdateDto.setAssocId(ID);
        associationUpdateDto.setUserId("user677");
        associationUpdateDto.setAssociationUpdateRequest(associationUpdateRequest);

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setAssociationType("Owner");
        deviceAssociation.setSerialNumber("12345");
        deviceAssociation.setStartTimeStamp(System.currentTimeMillis());
        deviceAssociation.setEndTimeStamp(System.currentTimeMillis());

        Mockito.doReturn(deviceAssociation).when(deviceAssociationDao).fetchAssociationById(Mockito.anyLong());
        deviceAssociationWithFactDataServiceV2.updateAssociation(associationUpdateDto);
    }

    @Test(expected = ApiPreConditionFailedException.class)
    public void updateAssociationTest_ApiPreConditionFailedException() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "defaultAssociationType", "vehicle Owner");

        AssociationUpdateRequest associationUpdateRequest = new AssociationUpdateRequest();
        associationUpdateRequest.setAssocType("vehicle Owner");
        associationUpdateRequest.setEndTime(System.currentTimeMillis() + System.currentTimeMillis());
        associationUpdateRequest.setStartTime(System.currentTimeMillis());

        AssociationUpdateDto associationUpdateDto = new AssociationUpdateDto();
        associationUpdateDto.setAssocId(ID);
        associationUpdateDto.setUserId("user677");
        associationUpdateDto.setAssociationUpdateRequest(associationUpdateRequest);

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setAssociationType("Owner");
        deviceAssociation.setSerialNumber("12345");
        deviceAssociation.setStartTimeStamp(System.currentTimeMillis());
        deviceAssociation.setEndTimeStamp(System.currentTimeMillis() + System.currentTimeMillis());

        Mockito.doReturn(deviceAssociation).when(deviceAssociationDao).fetchAssociationById(Mockito.anyLong());
        deviceAssociationWithFactDataServiceV2.updateAssociation(associationUpdateDto);
    }

    @Test(expected = ApiPreConditionFailedException.class)
    public void updateAssociationTest() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "defaultAssociationType", "vehicle Owner");

        AssociationUpdateRequest associationUpdateRequest = new AssociationUpdateRequest();
        associationUpdateRequest.setAssocType("vehicle Owner1");
        associationUpdateRequest.setEndTime(System.currentTimeMillis() + System.currentTimeMillis());
        associationUpdateRequest.setStartTime(System.currentTimeMillis());

        AssociationUpdateDto associationUpdateDto = new AssociationUpdateDto();
        associationUpdateDto.setAssocId(ID);
        associationUpdateDto.setUserId("user677");
        associationUpdateDto.setAssociationUpdateRequest(associationUpdateRequest);

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setAssociationType("Owner");
        deviceAssociation.setSerialNumber("12345");
        deviceAssociation.setStartTimeStamp(System.currentTimeMillis());
        deviceAssociation.setEndTimeStamp(System.currentTimeMillis() + System.currentTimeMillis());

        Mockito.doReturn(false).when(deviceAssociationDao)
            .validateUserIsOwnerOfDevice(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn(deviceAssociation).when(deviceAssociationDao).fetchAssociationById(Mockito.anyLong());
        deviceAssociationWithFactDataServiceV2.updateAssociation(associationUpdateDto);
    }

    @Test
    public void updateAssociationTest_UpdatedMap() {

        ReflectionTestUtils.setField(deviceAssociationWithFactDataServiceV2, "defaultAssociationType", "vehicle Owner");

        AssociationUpdateRequest associationUpdateRequest = new AssociationUpdateRequest();
        associationUpdateRequest.setAssocType("vehicle Owner1");
        associationUpdateRequest.setEndTime(System.currentTimeMillis() + System.currentTimeMillis());
        associationUpdateRequest.setStartTime(System.currentTimeMillis());

        AssociationUpdateDto associationUpdateDto = new AssociationUpdateDto();
        associationUpdateDto.setAssocId(ID);
        associationUpdateDto.setUserId("user677");
        associationUpdateDto.setAssociationUpdateRequest(associationUpdateRequest);

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setAssociationType("Owner");
        deviceAssociation.setSerialNumber("12345");
        deviceAssociation.setStartTimeStamp(System.currentTimeMillis());
        deviceAssociation.setEndTimeStamp(System.currentTimeMillis() + System.currentTimeMillis());

        deviceAssociationDao.update(Mockito.any(), Mockito.any());
        Mockito.doReturn(true).when(deviceAssociationDao)
            .validateUserIsOwnerOfDevice(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn(deviceAssociation).when(deviceAssociationDao).fetchAssociationById(Mockito.anyLong());
        deviceAssociationWithFactDataServiceV2.updateAssociation(associationUpdateDto);
    }

    @Test
    public void resetDeviceTest() {
        try {
            ResponseEntity<LoginResponse> responseEntity = Mockito.mock(ResponseEntity.class);
            LoginResponse response = Mockito.mock(LoginResponse.class);
            Mockito.doReturn(responseEntity).when(restClientLibrary)
                .doPost(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any());
            Mockito.when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
            Mockito.when(responseEntity.getBody()).thenReturn(response);
            Mockito.when(response.getToken()).thenReturn("token");
            ReflectionTestUtils.invokeMethod(deviceAssociationWithFactDataServiceV2, "resetDevice", "imei");
            Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
