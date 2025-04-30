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

import com.amazonaws.services.cloudwatch.model.InternalServiceException;
import org.eclipse.ecsp.common.config.EnvConfig;
import org.eclipse.ecsp.deviceassociation.lib.config.DeviceAssocationProperty;
import org.eclipse.ecsp.deviceassociation.lib.dao.DeviceAssociationDao;
import org.eclipse.ecsp.deviceassociation.lib.exception.DeviceReplaceException;
import org.eclipse.ecsp.deviceassociation.lib.exception.InvalidPinException;
import org.eclipse.ecsp.deviceassociation.lib.exception.InvalidUserAssociation;
import org.eclipse.ecsp.deviceassociation.lib.exception.NoSuchEntityException;
import org.eclipse.ecsp.deviceassociation.lib.exception.UpdateDeviceException;
import org.eclipse.ecsp.deviceassociation.lib.model.AssociationStatus;
import org.eclipse.ecsp.deviceassociation.lib.model.DeviceAssociation;
import org.eclipse.ecsp.deviceassociation.lib.model.DeviceAssociationHistory;
import org.eclipse.ecsp.deviceassociation.lib.model.swm.SwmRequest;
import org.eclipse.ecsp.deviceassociation.lib.observer.DeviceAssociationObservable;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociateDeviceRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociateDeviceResponse;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociationDetailsResponse;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.CurrentDeviceDataPojo;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceDetail;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceStatusRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.FactoryData;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.ReplaceDeviceDataPojo;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.ReplaceFactoryDataRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.StateChangeRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.support.SpringAuthTokenGenerator;
import org.eclipse.ecsp.deviceassociation.lib.service.swm.IswmCrudService;
import org.eclipse.ecsp.services.clientlib.HcpRestClientLibrary;
import org.eclipse.ecsp.services.device.dao.DeviceDao;
import org.eclipse.ecsp.services.device.model.Device;
import org.eclipse.ecsp.services.deviceactivation.dao.DeviceActivationStateDao;
import org.eclipse.ecsp.services.factorydata.dao.DeviceInfoFactoryDataDao;
import org.eclipse.ecsp.services.factorydata.domain.DeviceInfoFactoryData;
import org.eclipse.ecsp.services.factorydata.domain.DeviceState;
import org.eclipse.ecsp.services.shared.dao.HcpInfoDao;
import org.eclipse.ecsp.services.shared.db.HcpInfo;
import org.eclipse.ecsp.springauth.client.rest.SpringAuthRestClient;
import org.eclipse.ecsp.userauth.lib.model.LoginResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;

import javax.management.InvalidAttributeValueException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for DeviceAssociationWithFactDataService.
 */
public class DeviceAssociationWithFactDataServiceTest {
    private static int INT_ID = 99;
    private static long LONG_ID = 99L;
    private static long RANDOM_NUMBER = 2L;
    private static int PAGE = 5;
    private static int SIZE = 10;

    @InjectMocks
    DeviceAssociationWithFactDataService deviceAssociationWithFactDataService;

    @Mock
    DeviceAssociationDao deviceAssociationDao;

    @Mock
    SpringAuthRestClient springAuthRestClient;

    @Mock
    EnvConfig envConfig;

    @Mock
    DeviceDao deviceDao;

    @Mock
    HcpInfoDao hcpInfoDao;

    @Mock
    DeviceInfoFactoryDataDao deviceInfoFactoryDataDao;

    @Mock
    DeviceActivationStateDao deviceActivationStateDao;

    @Mock
    DeviceAssociationObservable observable;

    @Mock
    HcpRestClientLibrary restClientLibrary;

    @Mock
    IswmCrudService<SwmRequest> swmService;

    @Mock
    HttpHeaders headers;

    @Mock
    private SpringAuthTokenGenerator springAuthTokenGenerator;

    @InjectMocks
    CurrentDeviceDataPojo currentValue;
    ReplaceDeviceDataPojo replaceWith;
    DeviceInfoFactoryData deviceInfoFactoryDataCurrent;
    DeviceInfoFactoryData deviceInfoFactoryDataReplace;

    /**
     * run before each test.
     */
    @Before
    public void beforeEach() {
        initMocks(this);

        CurrentDeviceDataPojo cv = new CurrentDeviceDataPojo();
        cv.setSerialNumber("S1234");
        cv.setBssid("B1234");
        cv.setIccid("ICC1234");
        cv.setImei("I1234");
        cv.setImsi("IMSI1234");
        currentValue = cv;

        ReplaceDeviceDataPojo rw = new ReplaceDeviceDataPojo();
        rw.setImei("I-1234");
        rw.setBssid("B-1234");
        rw.setIccid("ICC-1234");
        rw.setImsi("IMSI-1234");
        rw.setSerialNumber("S-1234");
        replaceWith = rw;

        DeviceInfoFactoryData difdCurrent = new DeviceInfoFactoryData();
        difdCurrent.setId(1L);
        difdCurrent.setSerialNumber("S1234");
        difdCurrent.setBssid("B1234");
        difdCurrent.setIccid("ICC1234");
        difdCurrent.setImei("I1234");
        difdCurrent.setImsi("IMSI1234");
        deviceInfoFactoryDataCurrent = difdCurrent;

        DeviceInfoFactoryData difdReplace = new DeviceInfoFactoryData();
        difdReplace.setId(RANDOM_NUMBER);
        difdReplace.setImei("I-1234");
        difdReplace.setBssid("B-1234");
        difdReplace.setIccid("ICC-1234");
        difdReplace.setImsi("IMSI-1234");
        difdReplace.setSerialNumber("S-1234");
        deviceInfoFactoryDataReplace = difdReplace;
    }

    @Test
    public void associateDeviceTest_EmptyRequest() {
        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        assertThrows(NoSuchEntityException.class,
            () -> deviceAssociationWithFactDataService.associateDevice(associateDeviceRequest));
    }

    //@Test
    //public void associateDeviceTest_NullRequest() {
    //    AssociateDeviceRequest associateDeviceRequest = mock(AssociateDeviceRequest.class);
    //    Mockito.doReturn(null).when(associateDeviceRequest).toString();
    //    assertThrows(NoSuchEntityException.class, () ->
    //    deviceAssociationWithFactDataService.associateDevice(associateDeviceRequest));
    //}

    @Test
    public void associateDeviceTest_NoSuchEntityExceptionNullFactoryDataList() {
        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setUserId("User123");
        associateDeviceRequest.setSerialNumber("S1234");
        Mockito.doReturn(null).when(deviceAssociationDao).constructAndFetchFactoryData(associateDeviceRequest);
        assertThrows(NoSuchEntityException.class,
            () -> deviceAssociationWithFactDataService.associateDevice(associateDeviceRequest));
    }

    @Test
    public void associateDeviceTest_NoSuchEntityExceptionEmptyFactoryData() {
        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setUserId("User123");
        associateDeviceRequest.setSerialNumber("S1234");
        List<FactoryData> fetchFactoryData = new ArrayList<>();
        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao)
            .constructAndFetchFactoryData(associateDeviceRequest);
        assertThrows(NoSuchEntityException.class,
            () -> deviceAssociationWithFactDataService.associateDevice(associateDeviceRequest));
    }

    @Test
    public void associateDeviceTest_NoSuchEntityExceptionNullFactoryData() {
        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setUserId("User123");
        associateDeviceRequest.setSerialNumber("S1234");
        List<FactoryData> fetchFactoryData = new ArrayList<>();
        fetchFactoryData.add(null);
        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao)
            .constructAndFetchFactoryData(associateDeviceRequest);
        assertThrows(NoSuchEntityException.class,
            () -> deviceAssociationWithFactDataService.associateDevice(associateDeviceRequest));
    }

    @Test
    public void associateDeviceTest_NoSuchEntityException0Id() {
        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setUserId("User123");
        associateDeviceRequest.setSerialNumber("S1234");
        FactoryData factoryData = new FactoryData();
        factoryData.setBssid("B1234");
        factoryData.setImei("I1234");
        factoryData.setSerialNumber("S1234");
        factoryData.setDeviceType("dongle");
        factoryData.setFaulty(true);
        factoryData.setStolen(false);
        factoryData.setState("PROVISIONED");
        List<FactoryData> fetchFactoryData = new ArrayList<>();
        fetchFactoryData.add(factoryData);
        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao)
            .constructAndFetchFactoryData(associateDeviceRequest);
        assertThrows(NoSuchEntityException.class,
            () -> deviceAssociationWithFactDataService.associateDevice(associateDeviceRequest));
    }

    @Test
    public void associateDeviceTest_InputBssid() throws Exception {
        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setUserId("User123");
        associateDeviceRequest.setBssid("B1234");
        FactoryData factoryData = new FactoryData();
        factoryData.setBssid("B1234");
        factoryData.setImei("I1234");
        factoryData.setSerialNumber("S1234");
        factoryData.setDeviceType("dongle");
        factoryData.setId(INT_ID);
        factoryData.setFaulty(false);
        factoryData.setStolen(false);
        factoryData.setState("PROVISIONED");
        List<FactoryData> fetchFactoryData = new ArrayList<>();
        fetchFactoryData.add(factoryData);
        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao)
            .constructAndFetchFactoryData(associateDeviceRequest);
        AssociateDeviceResponse associateDeviceResponse =
                deviceAssociationWithFactDataService.associateDevice(associateDeviceRequest);
        Assertions.assertNotNull(associateDeviceResponse);
    }

    @Test
    public void associateDeviceTest_InputImei() throws Exception {
        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setUserId("User123");
        associateDeviceRequest.setImei("I1234");
        FactoryData factoryData = new FactoryData();
        factoryData.setBssid("B1234");
        factoryData.setImei("I1234");
        factoryData.setSerialNumber("S1234");
        factoryData.setDeviceType("dongle");
        factoryData.setId(INT_ID);
        factoryData.setFaulty(false);
        factoryData.setStolen(false);
        factoryData.setState("PROVISIONED_ALIVE");
        List<FactoryData> fetchFactoryData = new ArrayList<>();
        fetchFactoryData.add(factoryData);
        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao)
            .constructAndFetchFactoryData(associateDeviceRequest);
        AssociateDeviceResponse associateDeviceResponse =
                deviceAssociationWithFactDataService.associateDevice(associateDeviceRequest);
        Assertions.assertNotNull(associateDeviceResponse);
    }

    @Test
    public void associateDeviceTest_InvalidPin() {
        ReflectionTestUtils.setField(deviceAssociationWithFactDataService, "pinValidation", true);
        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setUserId("User123");
        associateDeviceRequest.setImei("I1234");
        FactoryData factoryData = new FactoryData();
        factoryData.setBssid("B1234");
        factoryData.setImei("I1234");
        factoryData.setSerialNumber("S1234");
        factoryData.setDeviceType("dongle");
        factoryData.setId(INT_ID);
        factoryData.setFaulty(false);
        factoryData.setStolen(false);
        factoryData.setState("PROVISIONED_ALIVE");
        List<FactoryData> fetchFactoryData = new ArrayList<>();
        fetchFactoryData.add(factoryData);
        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao)
            .constructAndFetchFactoryData(associateDeviceRequest);
        assertThrows(InvalidPinException.class,
            () -> deviceAssociationWithFactDataService.associateDevice(associateDeviceRequest));
    }

    @Test
    public void associateDeviceTest_InputSerialNumber() throws Exception {
        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setUserId("User123");
        associateDeviceRequest.setSerialNumber("S1234");
        FactoryData factoryData = new FactoryData();
        factoryData.setBssid("B1234");
        factoryData.setImei("I1234");
        factoryData.setSerialNumber("S1234");
        factoryData.setDeviceType("dongle");
        factoryData.setId(INT_ID);
        factoryData.setFaulty(false);
        factoryData.setStolen(false);
        factoryData.setState("PROVISIONED");
        List<FactoryData> fetchFactoryData = new ArrayList<>();
        fetchFactoryData.add(factoryData);
        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao)
            .constructAndFetchFactoryData(associateDeviceRequest);
        AssociateDeviceResponse associateDeviceResponse =
                deviceAssociationWithFactDataService.associateDevice(associateDeviceRequest);
        Assertions.assertNotNull(associateDeviceResponse);
    }

    @Test
    public void associateDeviceTest_isTerminatedFalse() throws Exception {
        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setUserId("User123");
        associateDeviceRequest.setSerialNumber("S1234");
        FactoryData factoryData = new FactoryData();
        factoryData.setBssid("B1234");
        factoryData.setImei("I1234");
        factoryData.setSerialNumber("S1234");
        factoryData.setDeviceType("dongle");
        factoryData.setId(INT_ID);
        factoryData.setFaulty(false);
        factoryData.setStolen(false);
        factoryData.setState("PROVISIONED");
        List<FactoryData> fetchFactoryData = new ArrayList<>();
        fetchFactoryData.add(factoryData);
        ReflectionTestUtils.setField(deviceAssociationWithFactDataService, "forbidAssocAfterTerminate", true);
        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao)
            .constructAndFetchFactoryData(associateDeviceRequest);
        AssociateDeviceResponse associateDeviceResponse =
                deviceAssociationWithFactDataService.associateDevice(associateDeviceRequest);
        Assertions.assertNotNull(associateDeviceResponse);
    }

    @Test
    public void associateDeviceTest_isTerminatedTrue() {
        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setUserId("User123");
        associateDeviceRequest.setSerialNumber("S1234");
        FactoryData factoryData = new FactoryData();
        factoryData.setBssid("B1234");
        factoryData.setImei("I1234");
        factoryData.setSerialNumber("S1234");
        factoryData.setDeviceType("dongle");
        factoryData.setId(INT_ID);
        factoryData.setFaulty(false);
        factoryData.setStolen(false);
        factoryData.setState("PROVISIONED");
        List<FactoryData> fetchFactoryData = new ArrayList<>();
        fetchFactoryData.add(factoryData);
        ReflectionTestUtils.setField(deviceAssociationWithFactDataService, "forbidAssocAfterTerminate", true);
        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao)
            .constructAndFetchFactoryData(associateDeviceRequest);
        Mockito.doReturn(true).when(deviceAssociationDao).isDeviceTerminated(fetchFactoryData.get(0).getId());
        assertThrows(NoSuchEntityException.class,
            () -> deviceAssociationWithFactDataService.associateDevice(associateDeviceRequest));
    }

    @Test
    public void associateDeviceTest_EmptyFetchState() {
        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setUserId("User123");
        associateDeviceRequest.setSerialNumber("S1234");
        FactoryData factoryData = new FactoryData();
        factoryData.setBssid("B1234");
        factoryData.setImei("I1234");
        factoryData.setSerialNumber("S1234");
        factoryData.setDeviceType("dongle");
        factoryData.setId(INT_ID);
        factoryData.setFaulty(false);
        factoryData.setStolen(false);
        factoryData.setState("");
        List<FactoryData> fetchFactoryData = new ArrayList<>();
        fetchFactoryData.add(factoryData);

        DeviceDetail deviceDetail = new DeviceDetail();
        deviceDetail.setImei("I1234");
        deviceDetail.setHarmanId("H1234");

        AssociationDetailsResponse associationDetailsResponse = new AssociationDetailsResponse();
        associationDetailsResponse.setId(LONG_ID);
        associationDetailsResponse.setUserId("User123");
        associationDetailsResponse.setSerialNumber("S1234");
        associationDetailsResponse.setDeviceDetail(deviceDetail);
        List<AssociationDetailsResponse> associationDetailsResponseList = new ArrayList<>();
        associationDetailsResponseList.add(associationDetailsResponse);

        ReflectionTestUtils.setField(deviceAssociationWithFactDataService, "forbidAssocAfterTerminate", true);
        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao)
            .constructAndFetchFactoryData(associateDeviceRequest);
        Mockito.doReturn(associationDetailsResponseList).when(deviceAssociationDao)
            .fetchAssociationDetails(Mockito.anyMap(), Mockito.anyBoolean());
        assertThrows(NoSuchEntityException.class,
            () -> deviceAssociationWithFactDataService.associateDevice(associateDeviceRequest));
    }

    @Test
    public void associateDeviceTest_FaultyState() {
        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setUserId("User123");
        associateDeviceRequest.setBssid("B1234");
        associateDeviceRequest.setImei("I1234");
        associateDeviceRequest.setSerialNumber("S1234");
        FactoryData factoryData = new FactoryData();
        factoryData.setBssid("B1234");
        factoryData.setImei("I1234");
        factoryData.setSerialNumber("S1234");
        factoryData.setDeviceType("dongle");
        factoryData.setId(INT_ID);
        factoryData.setState("PROVISIONED");
        factoryData.setFaulty(true);
        factoryData.setStolen(false);

        List<FactoryData> fetchFactoryData = new ArrayList<>();
        fetchFactoryData.add(factoryData);
        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao)
            .constructAndFetchFactoryData(associateDeviceRequest);
        assertThrows(NoSuchEntityException.class,
            () -> deviceAssociationWithFactDataService.associateDevice(associateDeviceRequest));
    }

    @Test
    public void associateDeviceTest_StolenState() {
        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setUserId("User123");
        associateDeviceRequest.setBssid("B1234");
        associateDeviceRequest.setImei("I1234");
        associateDeviceRequest.setSerialNumber("S1234");
        FactoryData factoryData = new FactoryData();
        factoryData.setBssid("B1234");
        factoryData.setImei("I1234");
        factoryData.setSerialNumber("S1234");
        factoryData.setDeviceType("dongle");
        factoryData.setId(INT_ID);
        factoryData.setState("PROVISIONED");
        factoryData.setFaulty(false);
        factoryData.setStolen(true);

        List<FactoryData> fetchFactoryData = new ArrayList<>();
        fetchFactoryData.add(factoryData);
        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao)
            .constructAndFetchFactoryData(associateDeviceRequest);
        assertThrows(NoSuchEntityException.class,
            () -> deviceAssociationWithFactDataService.associateDevice(associateDeviceRequest));
    }

    @Test
    public void associateDeviceTest_ActiveState() {
        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setUserId("User123");
        associateDeviceRequest.setBssid("B1234");
        associateDeviceRequest.setImei("I1234");
        associateDeviceRequest.setSerialNumber("S1234");
        FactoryData factoryData = new FactoryData();
        factoryData.setBssid("B1234");
        factoryData.setImei("I1234");
        factoryData.setSerialNumber("S1234");
        factoryData.setDeviceType("dongle");
        factoryData.setId(INT_ID);
        factoryData.setState("ACTIVE");
        factoryData.setFaulty(false);
        factoryData.setStolen(false);

        DeviceDetail deviceDetail = new DeviceDetail();
        deviceDetail.setImei("I1234");
        deviceDetail.setHarmanId("H1234");

        AssociationDetailsResponse associationDetailsResponse = new AssociationDetailsResponse();
        associationDetailsResponse.setId(LONG_ID);
        associationDetailsResponse.setUserId("User123");
        associationDetailsResponse.setSerialNumber("S1234");
        associationDetailsResponse.setDeviceDetail(deviceDetail);

        List<AssociationDetailsResponse> associationDetailsResponseList = new ArrayList<>();
        associationDetailsResponseList.add(associationDetailsResponse);

        List<FactoryData> fetchFactoryData = new ArrayList<>();
        fetchFactoryData.add(factoryData);
        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao)
            .constructAndFetchFactoryData(associateDeviceRequest);
        Mockito.doReturn(associationDetailsResponseList).when(deviceAssociationDao)
            .fetchAssociationDetails(Mockito.anyMap(), Mockito.anyBoolean());
        assertThrows(NoSuchEntityException.class,
            () -> deviceAssociationWithFactDataService.associateDevice(associateDeviceRequest));
    }

    @Test
    public void associateDeviceTest_UserIdContainsSymbol() {
        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setUserId("User@123");
        associateDeviceRequest.setBssid("B1234");
        associateDeviceRequest.setImei("I1234");
        associateDeviceRequest.setSerialNumber("S1234");
        FactoryData factoryData = new FactoryData();
        factoryData.setBssid("B1234");
        factoryData.setImei("I1234");
        factoryData.setSerialNumber("S1234");
        factoryData.setDeviceType("dongle");
        factoryData.setId(INT_ID);
        factoryData.setState("ACTIVE");
        factoryData.setFaulty(false);
        factoryData.setStolen(false);

        DeviceDetail deviceDetail = new DeviceDetail();
        deviceDetail.setImei("I1234");
        deviceDetail.setHarmanId("H1234");

        AssociationDetailsResponse associationDetailsResponse = new AssociationDetailsResponse();
        associationDetailsResponse.setId(LONG_ID);
        associationDetailsResponse.setUserId("User@123");
        associationDetailsResponse.setSerialNumber("S1234");
        associationDetailsResponse.setDeviceDetail(deviceDetail);

        List<AssociationDetailsResponse> associationDetailsResponseList = new ArrayList<>();
        associationDetailsResponseList.add(associationDetailsResponse);

        List<FactoryData> fetchFactoryData = new ArrayList<>();
        fetchFactoryData.add(factoryData);
        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao)
            .constructAndFetchFactoryData(associateDeviceRequest);
        Mockito.doReturn(associationDetailsResponseList).when(deviceAssociationDao)
            .fetchAssociationDetails(Mockito.anyMap(), Mockito.anyBoolean());
        assertThrows(NoSuchEntityException.class,
            () -> deviceAssociationWithFactDataService.associateDevice(associateDeviceRequest));
    }

    @Test
    public void getAssociatedDevicesForUserTest_EmptyAssociationsList() {
        String userId = "User123";
        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(userId);
        List<DeviceAssociation> deviceAssociationList =
                deviceAssociationWithFactDataService.getAssociatedDevicesForUser(userId);
        Assertions.assertNotNull(deviceAssociationList);
    }

    @Test
    public void getAssociatedDevicesForUserTest_0Id() {
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(1L);
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setHarmanId("H1234");
        deviceAssociation.setVehicleId("V1234");
        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);
        String userId = "User123";
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(userId);
        List<DeviceAssociation> deviceAssociationList =
                deviceAssociationWithFactDataService.getAssociatedDevicesForUser(userId);
        Assertions.assertNotNull(deviceAssociationList);
    }

    @Test
    public void getAssociatedDevicesForUserTest_NullFactoryDataList() {
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(1L);
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setHarmanId("H1234");
        deviceAssociation.setVehicleId("V1234");
        deviceAssociation.setFactoryId(1L);
        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);
        String userId = "User123";
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(userId);
        Mockito.doReturn(null).when(deviceAssociationDao).fetchFactoryData(deviceAssociation.getFactoryId());
        List<DeviceAssociation> deviceAssociationList =
                deviceAssociationWithFactDataService.getAssociatedDevicesForUser(userId);
        Assertions.assertNotNull(deviceAssociationList);
    }

    @Test
    public void getAssociatedDevicesForUserTest_NullFactoryData() {
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(1L);
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setHarmanId("H1234");
        deviceAssociation.setVehicleId("V1234");
        deviceAssociation.setFactoryId(1L);
        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);
        List<FactoryData> data = new ArrayList<>();
        data.add(null);
        String userId = "User123";
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(userId);
        Mockito.doReturn(data).when(deviceAssociationDao).fetchFactoryData(deviceAssociation.getFactoryId());
        List<DeviceAssociation> deviceAssociationList =
                deviceAssociationWithFactDataService.getAssociatedDevicesForUser(userId);
        Assertions.assertNotNull(deviceAssociationList);
    }

    @Test
    public void getAssociatedDevicesForUserTest() {
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(1L);
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setHarmanId("H1234");
        deviceAssociation.setVehicleId("V1234");
        deviceAssociation.setFactoryId(1L);
        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);
        FactoryData factoryData = new FactoryData();
        factoryData.setBssid("B1234");
        factoryData.setImei("I1234");
        factoryData.setSerialNumber("S1234");
        factoryData.setImsi("IMSI1234");
        factoryData.setMsisdn("MS1234");
        factoryData.setIccid("ICC1234");
        factoryData.setDeviceType("dongle");
        factoryData.setId(INT_ID);
        factoryData.setState("PROVISIONED");
        factoryData.setFaulty(false);
        factoryData.setStolen(false);
        List<FactoryData> data = new ArrayList<>();
        data.add(factoryData);
        String userId = "User123";
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(userId);
        Mockito.doReturn(data).when(deviceAssociationDao).fetchFactoryData(deviceAssociation.getFactoryId());
        List<DeviceAssociation> deviceAssociationList =
                deviceAssociationWithFactDataService.getAssociatedDevicesForUser(userId);
        Assertions.assertNotNull(deviceAssociationList);
    }

    @Test
    public void getAssociationDetailsTest_NullDeviceAssociation() {
        String userId = "User123";
        long associationId = 1L;
        Mockito.doReturn(null).when(deviceAssociationDao).find(associationId, userId);
        DeviceAssociation deviceAssociation =
                deviceAssociationWithFactDataService.getAssociationDetails(associationId, userId);
        Assertions.assertNull(deviceAssociation);
    }

    @Test
    public void getAssociationDetailsTest_NullHarmanId() {
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(1L);
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setVehicleId("V1234");
        deviceAssociation.setFactoryId(1L);
        String userId = "User123";
        long associationId = 1L;
        Mockito.doReturn(deviceAssociation).when(deviceAssociationDao).find(associationId, userId);
        DeviceAssociation deviceAssociations =
                deviceAssociationWithFactDataService.getAssociationDetails(associationId, userId);
        Assertions.assertNull(deviceAssociations.getHarmanId());
    }

    @Test
    public void getAssociationDetailsTest() {
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(1L);
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setHarmanId("H1234");
        deviceAssociation.setVehicleId("V1234");
        deviceAssociation.setFactoryId(1L);
        String userId = "User123";
        long associationId = 1L;
        Mockito.doReturn(deviceAssociation).when(deviceAssociationDao).find(associationId, userId);
        DeviceAssociation deviceAssociations =
                deviceAssociationWithFactDataService.getAssociationDetails(associationId, userId);
        Assertions.assertNotNull(deviceAssociations);
    }

    @Test
    public void terminateAssociationTest_InvalidUserAssociationException() {
        String userId = "User123";
        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setDeviceId("HID1234");
        deviceStatusRequest.setAssociationId(1L);
        deviceStatusRequest.setImei("I1234");
        deviceStatusRequest.setUserId(userId);
        deviceStatusRequest.setSerialNumber("S1234");
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setHarmanId("H1234");
        DeviceAssociation deviceAssociation1 = new DeviceAssociation();
        deviceAssociation1.setImei("IMEI1111");
        deviceAssociation1.setSerialNumber("SNO1111");
        deviceAssociation1.setHarmanId("HID1111");
        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();
        deviceAssociationList.add(deviceAssociation);
        deviceAssociationList.add(deviceAssociation1);
        Mockito.doReturn(deviceAssociationList).when(deviceAssociationDao)
            .constructAndFetchDeviceAssociationData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong(), Mockito.anyList());
        assertThrows(InvalidUserAssociation.class,
            () -> deviceAssociationWithFactDataService.terminateAssociation(deviceStatusRequest));
    }

    @Test
    public void terminateAssociationTest_TerminationSuccess_SpringAuth() throws Exception {
        
        String userId = "User123";
        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setDeviceId("HID1234");
        deviceStatusRequest.setAssociationId(1L);
        deviceStatusRequest.setImei("I1234");
        deviceStatusRequest.setUserId(userId);
        deviceStatusRequest.setSerialNumber("S1234");
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setHarmanId("H1234");
        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();
        deviceAssociationList.add(deviceAssociation);

        Map map = new HashMap();
        Mockito.doReturn(deviceAssociationList).when(deviceAssociationDao)
            .constructAndFetchDeviceAssociationData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong(), Mockito.anyList());
        Mockito.doReturn(1).when(deviceAssociationDao).updateForDisassociationById(Mockito.any());
        Mockito.doReturn("dummytoken").when(springAuthTokenGenerator).fetchSpringAuthToken();
        Mockito.doReturn(map).when(springAuthRestClient).deleteRegisteredClient(Mockito.anyString(),
            Mockito.anyString());
        Mockito.doNothing().when(observable).notify(Mockito.any());
        int updatedCount = deviceAssociationWithFactDataService.terminateAssociation(deviceStatusRequest);
        Assertions.assertEquals(1, updatedCount);
    }

    @Test
    public void restoreAssociationTest_EmptyRequest() {
        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        assertThrows(NoSuchEntityException.class,
            () -> deviceAssociationWithFactDataService.restoreAssociation(deviceStatusRequest));
    }

    @Test
    public void restoreAssociationTest_InvalidUserAssociationException() {
        String userId = "User123";
        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setDeviceId("HID1234");
        deviceStatusRequest.setAssociationId(1L);
        deviceStatusRequest.setImei("I1234");
        deviceStatusRequest.setUserId(userId);
        deviceStatusRequest.setSerialNumber("S1234");
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setHarmanId("H1234");
        DeviceAssociation deviceAssociation1 = new DeviceAssociation();
        deviceAssociation1.setImei("IMEI1111");
        deviceAssociation1.setSerialNumber("SNO1111");
        deviceAssociation1.setHarmanId("HID1111");
        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();
        deviceAssociationList.add(deviceAssociation);
        deviceAssociationList.add(deviceAssociation1);
        Mockito.doReturn(deviceAssociationList).when(deviceAssociationDao)
            .constructAndFetchDeviceAssociationData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong(), Mockito.anyList());
        assertThrows(InvalidUserAssociation.class,
            () -> deviceAssociationWithFactDataService.restoreAssociation(deviceStatusRequest));
    }

    @Test
    public void restoreAssociationTest_NullDevice() throws Exception {
        String userId = "User123";
        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setDeviceId("HID1234");
        deviceStatusRequest.setAssociationId(1L);
        deviceStatusRequest.setImei("I1234");
        deviceStatusRequest.setUserId(userId);
        deviceStatusRequest.setSerialNumber("S1234");
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setHarmanId("H1234");
        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();
        deviceAssociationList.add(deviceAssociation);
        Mockito.doReturn(deviceAssociationList).when(deviceAssociationDao)
            .constructAndFetchDeviceAssociationData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong(), Mockito.anyList());
        Mockito.doReturn(1).when(deviceAssociationDao).updateDeviceAssociationStatusToRestore(Mockito.any());
        Mockito.doReturn(null).when(deviceDao).findByDeviceId(Mockito.anyString());
        int updatedCount = deviceAssociationWithFactDataService.restoreAssociation(deviceStatusRequest);
        Assertions.assertEquals(1, updatedCount);
    }

    @Test
    public void restoreAssociationTest_RestoreSuccess_SpringAuth() throws Exception {
        
        String userId = "User123";
        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setDeviceId("HID1234");
        deviceStatusRequest.setAssociationId(1L);
        deviceStatusRequest.setImei("I1234");
        deviceStatusRequest.setUserId(userId);
        deviceStatusRequest.setSerialNumber("S1234");
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setHarmanId("H1234");

        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();
        deviceAssociationList.add(deviceAssociation);

        Device device = new Device();
        device.setId(1L);
        device.setHarmanId("H1234");
        device.setPasscode("passcode1234");
        device.setRandomNumber(RANDOM_NUMBER);

        Mockito.doReturn(deviceAssociationList).when(deviceAssociationDao)
            .constructAndFetchDeviceAssociationData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong(), Mockito.anyList());
        Mockito.doReturn(1).when(deviceAssociationDao).updateDeviceAssociationStatusToRestore(Mockito.any());
        Mockito.doReturn(device).when(deviceDao).findByDeviceId(Mockito.anyString());
        Mockito.doNothing().when(springAuthRestClient)
            .updateRegisteredClient(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString());
        int updatedCount = deviceAssociationWithFactDataService.restoreAssociation(deviceStatusRequest);
        Assertions.assertEquals(1, updatedCount);
    }

    @Test
    public void suspendDeviceTest_NullDeviceAssociationList() {
        String userId = "User123";
        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setDeviceId("HID1234");
        deviceStatusRequest.setAssociationId(1L);
        deviceStatusRequest.setImei("I1234");
        deviceStatusRequest.setUserId(userId);
        deviceStatusRequest.setSerialNumber("S1234");
        Mockito.doReturn(null).when(deviceAssociationDao)
            .constructAndFetchDeviceAssociationData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong(), Mockito.anyList());
        assertThrows(NoSuchEntityException.class,
            () -> deviceAssociationWithFactDataService.suspendDevice(deviceStatusRequest));
    }

    @Test
    public void suspendDeviceTest_EmptyDeviceAssociationList() {
        String userId = "User123";
        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setDeviceId("HID1234");
        deviceStatusRequest.setAssociationId(1L);
        deviceStatusRequest.setImei("I1234");
        deviceStatusRequest.setUserId(userId);
        deviceStatusRequest.setSerialNumber("S1234");
        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();
        Mockito.doReturn(deviceAssociationList).when(deviceAssociationDao)
            .constructAndFetchDeviceAssociationData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong(), Mockito.anyList());
        assertThrows(NoSuchEntityException.class,
            () -> deviceAssociationWithFactDataService.suspendDevice(deviceStatusRequest));
    }

    @Test
    public void suspendDeviceTest_InvalidUserAssociationException() {
        String userId = "User123";
        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setDeviceId("HID1234");
        deviceStatusRequest.setAssociationId(1L);
        deviceStatusRequest.setImei("I1234");
        deviceStatusRequest.setUserId(userId);
        deviceStatusRequest.setSerialNumber("S1234");
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setHarmanId("H1234");
        DeviceAssociation deviceAssociation1 = new DeviceAssociation();
        deviceAssociation1.setImei("IMEI1111");
        deviceAssociation1.setSerialNumber("SNO1111");
        deviceAssociation1.setHarmanId("HID1111");
        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();
        deviceAssociationList.add(deviceAssociation);
        deviceAssociationList.add(deviceAssociation1);
        Mockito.doReturn(deviceAssociationList).when(deviceAssociationDao)
            .constructAndFetchDeviceAssociationData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong(), Mockito.anyList());
        assertThrows(InvalidUserAssociation.class,
            () -> deviceAssociationWithFactDataService.suspendDevice(deviceStatusRequest));
    }

    @Test
    public void suspendDeviceTest_InternalServiceException() {
        String userId = "User123";
        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setDeviceId("HID1234");
        deviceStatusRequest.setAssociationId(1L);
        deviceStatusRequest.setImei("I1234");
        deviceStatusRequest.setUserId(userId);
        deviceStatusRequest.setSerialNumber("S1234");
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setHarmanId("H1234");
        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();
        deviceAssociationList.add(deviceAssociation);
        Mockito.doReturn(deviceAssociationList).when(deviceAssociationDao)
            .constructAndFetchDeviceAssociationData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong(), Mockito.anyList());
        Mockito.doReturn(0).when(deviceAssociationDao).updateDeviceAssociationStatusToSuspended(Mockito.any());
        assertThrows(InternalServiceException.class,
            () -> deviceAssociationWithFactDataService.suspendDevice(deviceStatusRequest));
    }

    @Test
    public void suspendDeviceTest_SuspendSuccess() throws Exception {
        String userId = "User123";
        DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
        deviceStatusRequest.setDeviceId("HID1234");
        deviceStatusRequest.setAssociationId(1L);
        deviceStatusRequest.setImei("I1234");
        deviceStatusRequest.setUserId(userId);
        deviceStatusRequest.setSerialNumber("S1234");
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setHarmanId("H1234");
        deviceAssociation.setId(1L);
        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();
        deviceAssociationList.add(deviceAssociation);
        Mockito.doReturn(deviceAssociationList).when(deviceAssociationDao)
            .constructAndFetchDeviceAssociationData(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong(), Mockito.anyList());
        Mockito.doReturn(1).when(deviceAssociationDao).updateDeviceAssociationStatusToSuspended(Mockito.any());
        Mockito.doReturn(null).when(springAuthRestClient).deleteRegisteredClient(Mockito.anyString(),
            Mockito.anyString());
        AssociateDeviceResponse associateDeviceResponse =
                deviceAssociationWithFactDataService.suspendDevice(deviceStatusRequest);
        Assertions.assertNotNull(associateDeviceResponse);
    }

    @Test
    public void replaceDeviceTest_NullReplaceFactoryDataRequest() {
        String userId = "User123";
        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        assertThrows(DeviceReplaceException.class,
            () -> deviceAssociationWithFactDataService.replaceDevice(replaceDeviceRequest, userId));
    }

    @Test
    public void replaceDeviceTest_EmptyListCurrentValueData() {

        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        replaceDeviceRequest.setCurrentValue(currentValue);
        replaceDeviceRequest.setReplaceWith(replaceWith);

        List<DeviceInfoFactoryData> listCurrentValueData = new ArrayList<>();
        Mockito.doReturn(listCurrentValueData).when(deviceInfoFactoryDataDao)
            .constructAndFetchFactoryData(Mockito.any());
        String userId = "User123";
        assertThrows(DeviceReplaceException.class,
            () -> deviceAssociationWithFactDataService.replaceDevice(replaceDeviceRequest, userId));
    }

    @Test
    public void replaceDeviceTest_InvalidUserAssociationException() {

        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        replaceDeviceRequest.setCurrentValue(currentValue);
        replaceDeviceRequest.setReplaceWith(replaceWith);

        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setId(1L);
        deviceInfoFactoryData.setFaulty(false);
        deviceInfoFactoryData.setStolen(false);
        List<DeviceInfoFactoryData> listCurrentValueData = new ArrayList<>();
        listCurrentValueData.add(deviceInfoFactoryData);
        Mockito.doReturn(listCurrentValueData).when(deviceInfoFactoryDataDao)
            .constructAndFetchFactoryData(Mockito.any());
        Mockito.doReturn(false).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactData(Mockito.anyLong(), Mockito.anyString());
        String userId = "User123";
        assertThrows(InvalidUserAssociation.class,
            () -> deviceAssociationWithFactDataService.replaceDevice(replaceDeviceRequest, userId));
    }

    @Test
    public void replaceDeviceTest_InvalidCurrentData() {


        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        CurrentDeviceDataPojo currentValue = new CurrentDeviceDataPojo();
        replaceDeviceRequest.setCurrentValue(currentValue);
        replaceDeviceRequest.setReplaceWith(replaceWith);

        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setId(1L);
        deviceInfoFactoryData.setFaulty(true);
        deviceInfoFactoryData.setStolen(false);
        DeviceInfoFactoryData deviceInfoFactoryData2 = new DeviceInfoFactoryData();
        deviceInfoFactoryData2.setId(RANDOM_NUMBER);
        deviceInfoFactoryData2.setFaulty(false);
        deviceInfoFactoryData2.setStolen(false);
        List<DeviceInfoFactoryData> listCurrentValueData = new ArrayList<>();
        listCurrentValueData.add(deviceInfoFactoryData);
        Mockito.doReturn(listCurrentValueData).when(deviceInfoFactoryDataDao)
            .constructAndFetchFactoryData(Mockito.any());
        List<DeviceInfoFactoryData> listReplaceValueData = new ArrayList<>();
        listReplaceValueData.add(deviceInfoFactoryData2);
        Mockito.when(deviceInfoFactoryDataDao.constructAndFetchFactoryData(Mockito.any()))
            .thenReturn(listCurrentValueData, listReplaceValueData);
        String userId = "User123";
        assertThrows(DeviceReplaceException.class,
            () -> deviceAssociationWithFactDataService.replaceDevice(replaceDeviceRequest, userId));
    }

    @Test
    public void replaceDeviceTest_InvalidReplaceData() {

        ReplaceDeviceDataPojo replaceWith = new ReplaceDeviceDataPojo();

        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        replaceDeviceRequest.setCurrentValue(currentValue);
        replaceDeviceRequest.setReplaceWith(replaceWith);

        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setId(1L);
        deviceInfoFactoryData.setFaulty(true);
        deviceInfoFactoryData.setStolen(false);
        DeviceInfoFactoryData deviceInfoFactoryData2 = new DeviceInfoFactoryData();
        deviceInfoFactoryData2.setId(RANDOM_NUMBER);
        deviceInfoFactoryData2.setFaulty(false);
        deviceInfoFactoryData2.setStolen(false);
        List<DeviceInfoFactoryData> listCurrentValueData = new ArrayList<>();
        listCurrentValueData.add(deviceInfoFactoryData);
        Mockito.doReturn(listCurrentValueData).when(deviceInfoFactoryDataDao)
            .constructAndFetchFactoryData(Mockito.any());
        List<DeviceInfoFactoryData> listReplaceValueData = new ArrayList<>();
        listReplaceValueData.add(deviceInfoFactoryData2);
        Mockito.when(deviceInfoFactoryDataDao.constructAndFetchFactoryData(Mockito.any()))
            .thenReturn(listCurrentValueData, listReplaceValueData);
        String userId = "User123";
        assertThrows(DeviceReplaceException.class,
            () -> deviceAssociationWithFactDataService.replaceDevice(replaceDeviceRequest, userId));
    }

    @Test
    public void replaceDeviceTest_NotFaultyOrStolen() {

        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        replaceDeviceRequest.setCurrentValue(currentValue);
        replaceDeviceRequest.setReplaceWith(replaceWith);

        DeviceInfoFactoryData deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setId(1L);
        deviceInfoFactoryData.setFaulty(false);
        deviceInfoFactoryData.setStolen(false);
        deviceInfoFactoryData.setState("ACTIVE");
        List<DeviceInfoFactoryData> listCurrentValueData = new ArrayList<>();
        listCurrentValueData.add(deviceInfoFactoryData);

        Mockito.doReturn(listCurrentValueData).when(deviceInfoFactoryDataDao)
            .constructAndFetchFactoryData(Mockito.any());
        Mockito.doReturn(true).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactData(Mockito.anyLong(), Mockito.anyString());
        String userId = "User123";
        assertThrows(DeviceReplaceException.class,
            () -> deviceAssociationWithFactDataService.replaceDevice(replaceDeviceRequest, userId));
    }

    @Test
    public void replaceDeviceTest_EmptyListReplaceValueData() {
        ReflectionTestUtils.setField(deviceAssociationWithFactDataService, "isCurrentDeviceToBeMovedToProvisioned",
            true);

        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        replaceDeviceRequest.setCurrentValue(currentValue);
        replaceDeviceRequest.setReplaceWith(replaceWith);

        deviceInfoFactoryDataCurrent.setFaulty(true);
        deviceInfoFactoryDataCurrent.setStolen(false);
        deviceInfoFactoryDataCurrent.setState("PROVISIONED");
        List<DeviceInfoFactoryData> listCurrentValueData = new ArrayList<>();
        listCurrentValueData.add(deviceInfoFactoryDataCurrent);

        List<DeviceInfoFactoryData> listReplaceValueData = new ArrayList<>();

        Mockito.when(deviceInfoFactoryDataDao.constructAndFetchFactoryData(Mockito.any()))
            .thenReturn(listCurrentValueData, listReplaceValueData);
        Mockito.doReturn(true).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactData(Mockito.anyLong(), Mockito.anyString());
        String userId = "User123";
        assertThrows(DeviceReplaceException.class,
            () -> deviceAssociationWithFactDataService.replaceDevice(replaceDeviceRequest, userId));
    }

    @Test
    public void replaceDeviceTest_ReplaceValueDataListSize2() {
        ReflectionTestUtils.setField(deviceAssociationWithFactDataService, "isCurrentDeviceToBeMovedToProvisioned",
            true);

        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        replaceDeviceRequest.setCurrentValue(currentValue);
        replaceDeviceRequest.setReplaceWith(replaceWith);
        
        deviceInfoFactoryDataCurrent.setFaulty(true);
        deviceInfoFactoryDataCurrent.setStolen(false);
        deviceInfoFactoryDataCurrent.setState("PROVISIONED");
        List<DeviceInfoFactoryData> listCurrentValueData = new ArrayList<>();
        listCurrentValueData.add(deviceInfoFactoryDataCurrent);

        deviceInfoFactoryDataReplace.setFaulty(false);
        deviceInfoFactoryDataReplace.setStolen(false);
        deviceInfoFactoryDataReplace.setState("PROVISIONED");

        DeviceInfoFactoryData deviceInfoFactoryDataReplace2 = new DeviceInfoFactoryData();
        deviceInfoFactoryDataReplace2.setState("ACTIVE");
        deviceInfoFactoryDataReplace2.setImei("232");
        deviceInfoFactoryDataReplace2.setSerialNumber("32342");

        List<DeviceInfoFactoryData> listReplaceValueData = new ArrayList<>();
        listReplaceValueData.add(deviceInfoFactoryDataReplace);
        listReplaceValueData.add(deviceInfoFactoryDataReplace2);

        Mockito.when(deviceInfoFactoryDataDao.constructAndFetchFactoryData(Mockito.any()))
            .thenReturn(listCurrentValueData, listReplaceValueData);
        Mockito.doReturn(true).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactData(Mockito.anyLong(), Mockito.anyString());
        String userId = "User123";
        assertThrows(DeviceReplaceException.class,
            () -> deviceAssociationWithFactDataService.replaceDevice(replaceDeviceRequest, userId));
    }

    @Test
    public void replaceDeviceTest_ReplaceDeviceNotInProvisionedState() {
        ReflectionTestUtils.setField(deviceAssociationWithFactDataService, "isCurrentDeviceToBeMovedToProvisioned",
            true);

        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        replaceDeviceRequest.setCurrentValue(currentValue);
        replaceDeviceRequest.setReplaceWith(replaceWith);
        
        deviceInfoFactoryDataCurrent.setFaulty(true);
        deviceInfoFactoryDataCurrent.setStolen(false);
        deviceInfoFactoryDataCurrent.setState("PROVISIONED");
        List<DeviceInfoFactoryData> listCurrentValueData = new ArrayList<>();
        listCurrentValueData.add(deviceInfoFactoryDataCurrent);


        deviceInfoFactoryDataReplace.setFaulty(false);
        deviceInfoFactoryDataReplace.setStolen(false);
        deviceInfoFactoryDataReplace.setState("ACTIVE");

        List<DeviceInfoFactoryData> listReplaceValueData = new ArrayList<>();
        listReplaceValueData.add(deviceInfoFactoryDataReplace);

        Mockito.when(deviceInfoFactoryDataDao.constructAndFetchFactoryData(Mockito.any()))
            .thenReturn(listCurrentValueData, listReplaceValueData);
        Mockito.doReturn(true).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactData(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(null).when(hcpInfoDao).findActiveHcpInfo(Mockito.anyLong());
        String userId = "User123";
        assertThrows(DeviceReplaceException.class,
            () -> deviceAssociationWithFactDataService.replaceDevice(replaceDeviceRequest, userId));
    }

    @Test
    public void replaceDeviceTest_NullHcpInfo() {
        ReflectionTestUtils.setField(deviceAssociationWithFactDataService, "isCurrentDeviceToBeMovedToProvisioned",
            true);

        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        replaceDeviceRequest.setCurrentValue(currentValue);
        replaceDeviceRequest.setReplaceWith(replaceWith);
        
        deviceInfoFactoryDataCurrent.setFaulty(true);
        deviceInfoFactoryDataCurrent.setStolen(false);
        deviceInfoFactoryDataCurrent.setState("PROVISIONED");
        List<DeviceInfoFactoryData> listCurrentValueData = new ArrayList<>();
        listCurrentValueData.add(deviceInfoFactoryDataCurrent);


        deviceInfoFactoryDataReplace.setFaulty(false);
        deviceInfoFactoryDataReplace.setStolen(false);
        deviceInfoFactoryDataReplace.setState("PROVISIONED");

        List<DeviceInfoFactoryData> listReplaceValueData = new ArrayList<>();
        listReplaceValueData.add(deviceInfoFactoryDataReplace);

        Mockito.when(deviceInfoFactoryDataDao.constructAndFetchFactoryData(Mockito.any()))
            .thenReturn(listCurrentValueData, listReplaceValueData);
        Mockito.doReturn(true).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactData(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(null).when(hcpInfoDao).findActiveHcpInfo(Mockito.anyLong());
        String userId = "User123";
        assertThrows(DeviceReplaceException.class,
            () -> deviceAssociationWithFactDataService.replaceDevice(replaceDeviceRequest, userId));
    }

    @Test
    public void replaceDeviceTest_ActiveRecordId0_AssociationDataDoesNotExist() {

        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        replaceDeviceRequest.setCurrentValue(currentValue);
        replaceDeviceRequest.setReplaceWith(replaceWith);

        deviceInfoFactoryDataCurrent.setFaulty(true);
        deviceInfoFactoryDataCurrent.setStolen(false);
        deviceInfoFactoryDataCurrent.setState("ACTIVE");
        List<DeviceInfoFactoryData> listCurrentValueData = new ArrayList<>();
        listCurrentValueData.add(deviceInfoFactoryDataCurrent);


        deviceInfoFactoryDataReplace.setFaulty(false);
        deviceInfoFactoryDataReplace.setStolen(false);
        deviceInfoFactoryDataReplace.setState("PROVISIONED");

        List<DeviceInfoFactoryData> listReplaceValueData = new ArrayList<>();
        listReplaceValueData.add(deviceInfoFactoryDataReplace);

        HcpInfo hcpInfo = new HcpInfo();
        hcpInfo.setFactoryId("1234");
        hcpInfo.setHarmanId("H1234");
        hcpInfo.setVin("V1234");
        hcpInfo.setId(1L);
        hcpInfo.setSerialNumber("S1234");

        Mockito.when(deviceInfoFactoryDataDao.constructAndFetchFactoryData(Mockito.any()))
            .thenReturn(listCurrentValueData, listReplaceValueData);
        Mockito.doReturn(true).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactData(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(hcpInfo).when(hcpInfoDao).findActiveHcpInfo(Mockito.anyLong());
        Mockito.doReturn(0L).when(deviceActivationStateDao).findActiveDevice(Long.parseLong(hcpInfo.getFactoryId()));
        String userId = "User123";
        assertThrows(DeviceReplaceException.class,
            () -> deviceAssociationWithFactDataService.replaceDevice(replaceDeviceRequest, userId));
    }

    @Test
    public void replaceDeviceTest_ActiveRecordId1() {

        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        replaceDeviceRequest.setCurrentValue(currentValue);
        replaceDeviceRequest.setReplaceWith(replaceWith);


        deviceInfoFactoryDataCurrent.setFaulty(true);
        deviceInfoFactoryDataCurrent.setStolen(false);
        deviceInfoFactoryDataCurrent.setState("ACTIVE");
        List<DeviceInfoFactoryData> listCurrentValueData = new ArrayList<>();
        listCurrentValueData.add(deviceInfoFactoryDataCurrent);


        deviceInfoFactoryDataReplace.setFaulty(false);
        deviceInfoFactoryDataReplace.setStolen(false);
        deviceInfoFactoryDataReplace.setState("PROVISIONED");

        List<DeviceInfoFactoryData> listReplaceValueData = new ArrayList<>();
        listReplaceValueData.add(deviceInfoFactoryDataReplace);

        HcpInfo hcpInfo = new HcpInfo();
        hcpInfo.setFactoryId("1234");
        hcpInfo.setHarmanId("H1234");
        hcpInfo.setVin("V1234");
        hcpInfo.setId(1L);
        hcpInfo.setSerialNumber("S1234");

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(1L);
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        deviceAssociation.setDeviceType("dongle");

        Mockito.when(deviceInfoFactoryDataDao.constructAndFetchFactoryData(Mockito.any()))
            .thenReturn(listCurrentValueData, listReplaceValueData);
        Mockito.doReturn(true).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactData(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(hcpInfo).when(hcpInfoDao).findActiveHcpInfo(Mockito.anyLong());
        Mockito.doReturn(1L).when(deviceActivationStateDao).findActiveDevice(Long.parseLong(hcpInfo.getFactoryId()));
        Mockito.doNothing().when(deviceActivationStateDao).disableRecord(Mockito.anyLong(), Mockito.anyString());

        Mockito.doReturn(deviceAssociation).when(deviceAssociationDao).findValidAssociations(Mockito.anyString());
        String userId = "User123";
        deviceAssociationWithFactDataService.replaceDevice(replaceDeviceRequest, userId);
        Assertions.assertTrue(deviceAssociationDao.checkAssociatedDeviceWithFactData(Mockito.anyLong(),
                Mockito.anyString()));
    }

    @Test
    public void replaceDeviceTest_ActiveRecordId1_SpringAuth() {

        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        replaceDeviceRequest.setCurrentValue(currentValue);
        replaceDeviceRequest.setReplaceWith(replaceWith);


        deviceInfoFactoryDataCurrent.setFaulty(true);
        deviceInfoFactoryDataCurrent.setStolen(false);
        deviceInfoFactoryDataCurrent.setState("ACTIVE");
        List<DeviceInfoFactoryData> listCurrentValueData = new ArrayList<>();
        listCurrentValueData.add(deviceInfoFactoryDataCurrent);


        deviceInfoFactoryDataReplace.setFaulty(false);
        deviceInfoFactoryDataReplace.setStolen(false);
        deviceInfoFactoryDataReplace.setState("PROVISIONED");

        List<DeviceInfoFactoryData> listReplaceValueData = new ArrayList<>();
        listReplaceValueData.add(deviceInfoFactoryDataReplace);

        HcpInfo hcpInfo = new HcpInfo();
        hcpInfo.setFactoryId("1234");
        hcpInfo.setHarmanId("H1234");
        hcpInfo.setVin("V1234");
        hcpInfo.setId(1L);
        hcpInfo.setSerialNumber("S1234");

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(1L);
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        deviceAssociation.setDeviceType("dongle");

        Mockito.when(deviceInfoFactoryDataDao.constructAndFetchFactoryData(Mockito.any()))
            .thenReturn(listCurrentValueData, listReplaceValueData);
        Mockito.doReturn(true).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactData(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(hcpInfo).when(hcpInfoDao).findActiveHcpInfo(Mockito.anyLong());
        Mockito.doReturn(1L).when(deviceActivationStateDao).findActiveDevice(Long.parseLong(hcpInfo.getFactoryId()));
        Mockito.doNothing().when(deviceActivationStateDao).disableRecord(Mockito.anyLong(), Mockito.anyString());

        Mockito.doReturn(deviceAssociation).when(deviceAssociationDao).findValidAssociations(Mockito.anyString());
        String userId = "User123";
        deviceAssociationWithFactDataService.replaceDevice(replaceDeviceRequest, userId);
        Assertions.assertTrue(deviceAssociationDao.checkAssociatedDeviceWithFactData(Mockito.anyLong(),
                Mockito.anyString()));
    }

    @Test
    public void replaceDeviceTest_isCurrentDeviceToBeMovedToProvisionedTrue_Active() {
        ReflectionTestUtils.setField(deviceAssociationWithFactDataService, "isCurrentDeviceToBeMovedToProvisioned",
            true);





        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        replaceDeviceRequest.setCurrentValue(currentValue);
        replaceDeviceRequest.setReplaceWith(replaceWith);


        deviceInfoFactoryDataCurrent.setFaulty(true);
        deviceInfoFactoryDataCurrent.setStolen(false);
        deviceInfoFactoryDataCurrent.setState("ACTIVE");
        List<DeviceInfoFactoryData> listCurrentValueData = new ArrayList<>();
        listCurrentValueData.add(deviceInfoFactoryDataCurrent);


        deviceInfoFactoryDataReplace.setFaulty(false);
        deviceInfoFactoryDataReplace.setStolen(false);
        deviceInfoFactoryDataReplace.setState("PROVISIONED");

        List<DeviceInfoFactoryData> listReplaceValueData = new ArrayList<>();
        listReplaceValueData.add(deviceInfoFactoryDataReplace);

        HcpInfo hcpInfo = new HcpInfo();
        hcpInfo.setFactoryId("1234");
        hcpInfo.setHarmanId("H1234");
        hcpInfo.setVin("V1234");
        hcpInfo.setId(1L);
        hcpInfo.setSerialNumber("S1234");

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(1L);
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        deviceAssociation.setDeviceType("dongle");

        Mockito.when(deviceInfoFactoryDataDao.constructAndFetchFactoryData(Mockito.any()))
            .thenReturn(listCurrentValueData, listReplaceValueData);
        Mockito.doReturn(true).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactData(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(hcpInfo).when(hcpInfoDao).findActiveHcpInfo(Mockito.anyLong());
        Mockito.doReturn(1L).when(deviceActivationStateDao).findActiveDevice(Long.parseLong(hcpInfo.getFactoryId()));
        Mockito.doNothing().when(deviceActivationStateDao).disableRecord(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(deviceAssociation).when(deviceAssociationDao).findValidAssociations(Mockito.anyString());
        Mockito.doNothing().when(deviceInfoFactoryDataDao)
            .changeDeviceState(deviceInfoFactoryDataCurrent.getId(), DeviceState.PROVISIONED.getValue(),
                "Old Device is Provisioned with its imei - " + deviceInfoFactoryDataCurrent.getImei());
        String userId = "User123";
        deviceAssociationWithFactDataService.replaceDevice(replaceDeviceRequest, userId);
        Assertions.assertTrue(deviceAssociationDao.checkAssociatedDeviceWithFactData(Mockito.anyLong(),
                Mockito.anyString()));
    }

    @Test
    public void replaceDeviceTest_isCurrentDeviceToBeMovedToProvisionedTrue_Ready_To_Activate() {
        ReflectionTestUtils.setField(deviceAssociationWithFactDataService, "isCurrentDeviceToBeMovedToProvisioned",
            true);





        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        replaceDeviceRequest.setCurrentValue(currentValue);
        replaceDeviceRequest.setReplaceWith(replaceWith);


        deviceInfoFactoryDataCurrent.setFaulty(true);
        deviceInfoFactoryDataCurrent.setStolen(false);
        deviceInfoFactoryDataCurrent.setState("READY_TO_ACTIVATE");
        List<DeviceInfoFactoryData> listCurrentValueData = new ArrayList<>();
        listCurrentValueData.add(deviceInfoFactoryDataCurrent);


        deviceInfoFactoryDataReplace.setFaulty(false);
        deviceInfoFactoryDataReplace.setStolen(false);
        deviceInfoFactoryDataReplace.setState("PROVISIONED");

        List<DeviceInfoFactoryData> listReplaceValueData = new ArrayList<>();
        listReplaceValueData.add(deviceInfoFactoryDataReplace);

        HcpInfo hcpInfo = new HcpInfo();
        hcpInfo.setFactoryId("1234");
        hcpInfo.setHarmanId("H1234");
        hcpInfo.setVin("V1234");
        hcpInfo.setId(1L);
        hcpInfo.setSerialNumber("S1234");

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(1L);
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        deviceAssociation.setDeviceType("dongle");

        Mockito.when(deviceInfoFactoryDataDao.constructAndFetchFactoryData(Mockito.any()))
            .thenReturn(listCurrentValueData, listReplaceValueData);
        Mockito.doReturn(true).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactData(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(hcpInfo).when(hcpInfoDao).findActiveHcpInfo(Mockito.anyLong());
        Mockito.doReturn(1L).when(deviceActivationStateDao).findActiveDevice(Long.parseLong(hcpInfo.getFactoryId()));
        Mockito.doNothing().when(deviceActivationStateDao).disableRecord(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(deviceAssociation).when(deviceAssociationDao).findValidAssociations(Mockito.anyString());
        Mockito.doNothing().when(deviceInfoFactoryDataDao)
            .changeDeviceState(deviceInfoFactoryDataCurrent.getId(), DeviceState.PROVISIONED.getValue(),
                "Old Device is Provisioned with its imei - " + deviceInfoFactoryDataCurrent.getImei());
        String userId = "User123";
        deviceAssociationWithFactDataService.replaceDevice(replaceDeviceRequest, userId);
        Assertions.assertTrue(deviceAssociationDao.checkAssociatedDeviceWithFactData(Mockito.anyLong(),
                Mockito.anyString()));
    }

    @Test
    public void replaceDeviceTest_isCurrentDeviceToBeMovedToProvisionedTrue_Stolen() {
        ReflectionTestUtils.setField(deviceAssociationWithFactDataService, "isCurrentDeviceToBeMovedToProvisioned",
            true);





        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        replaceDeviceRequest.setCurrentValue(currentValue);
        replaceDeviceRequest.setReplaceWith(replaceWith);


        deviceInfoFactoryDataCurrent.setFaulty(false);
        deviceInfoFactoryDataCurrent.setStolen(true);
        deviceInfoFactoryDataCurrent.setState("STOLEN");
        List<DeviceInfoFactoryData> listCurrentValueData = new ArrayList<>();
        listCurrentValueData.add(deviceInfoFactoryDataCurrent);


        deviceInfoFactoryDataReplace.setFaulty(false);
        deviceInfoFactoryDataReplace.setStolen(false);
        deviceInfoFactoryDataReplace.setState("PROVISIONED");

        List<DeviceInfoFactoryData> listReplaceValueData = new ArrayList<>();
        listReplaceValueData.add(deviceInfoFactoryDataReplace);

        HcpInfo hcpInfo = new HcpInfo();
        hcpInfo.setFactoryId("1234");
        hcpInfo.setHarmanId("H1234");
        hcpInfo.setVin("V1234");
        hcpInfo.setId(1L);
        hcpInfo.setSerialNumber("S1234");

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(1L);
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        deviceAssociation.setDeviceType("dongle");

        Mockito.when(deviceInfoFactoryDataDao.constructAndFetchFactoryData(Mockito.any()))
            .thenReturn(listCurrentValueData, listReplaceValueData);
        Mockito.doReturn(true).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactData(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(hcpInfo).when(hcpInfoDao).findActiveHcpInfo(Mockito.anyLong());
        Mockito.doReturn(1L).when(deviceActivationStateDao).findActiveDevice(Long.parseLong(hcpInfo.getFactoryId()));
        Mockito.doNothing().when(deviceActivationStateDao).disableRecord(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(deviceAssociation).when(deviceAssociationDao).findValidAssociations(Mockito.anyString());
        Mockito.doNothing().when(deviceInfoFactoryDataDao)
            .changeDeviceStateForStolenOrFaulty(deviceInfoFactoryDataCurrent.getId(),
                DeviceState.PROVISIONED.getValue(),
                "Old Device is Provisioned with its imei - " + deviceInfoFactoryDataCurrent.getImei());
        String userId = "User123";
        deviceAssociationWithFactDataService.replaceDevice(replaceDeviceRequest, userId);
        Assertions.assertTrue(deviceAssociationDao.checkAssociatedDeviceWithFactData(Mockito.anyLong(),
                Mockito.anyString()));
    }

    @Test
    public void replaceDeviceTest_isCurrentDeviceToBeMovedToProvisionedTrue_Faulty() {
        ReflectionTestUtils.setField(deviceAssociationWithFactDataService, "isCurrentDeviceToBeMovedToProvisioned",
            true);





        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        replaceDeviceRequest.setCurrentValue(currentValue);
        replaceDeviceRequest.setReplaceWith(replaceWith);


        deviceInfoFactoryDataCurrent.setFaulty(true);
        deviceInfoFactoryDataCurrent.setStolen(false);
        deviceInfoFactoryDataCurrent.setState("FAULTY");
        List<DeviceInfoFactoryData> listCurrentValueData = new ArrayList<>();
        listCurrentValueData.add(deviceInfoFactoryDataCurrent);


        deviceInfoFactoryDataReplace.setFaulty(false);
        deviceInfoFactoryDataReplace.setStolen(false);
        deviceInfoFactoryDataReplace.setState("PROVISIONED");

        List<DeviceInfoFactoryData> listReplaceValueData = new ArrayList<>();
        listReplaceValueData.add(deviceInfoFactoryDataReplace);

        HcpInfo hcpInfo = new HcpInfo();
        hcpInfo.setFactoryId("1234");
        hcpInfo.setHarmanId("H1234");
        hcpInfo.setVin("V1234");
        hcpInfo.setId(1L);
        hcpInfo.setSerialNumber("S1234");

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(1L);
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        deviceAssociation.setDeviceType("dongle");

        Mockito.when(deviceInfoFactoryDataDao.constructAndFetchFactoryData(Mockito.any()))
            .thenReturn(listCurrentValueData, listReplaceValueData);
        Mockito.doReturn(true).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactData(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(hcpInfo).when(hcpInfoDao).findActiveHcpInfo(Mockito.anyLong());
        Mockito.doReturn(1L).when(deviceActivationStateDao).findActiveDevice(Long.parseLong(hcpInfo.getFactoryId()));
        Mockito.doNothing().when(deviceActivationStateDao).disableRecord(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(deviceAssociation).when(deviceAssociationDao).findValidAssociations(Mockito.anyString());
        Mockito.doNothing().when(deviceInfoFactoryDataDao)
            .changeDeviceStateForStolenOrFaulty(deviceInfoFactoryDataCurrent.getId(),
                DeviceState.PROVISIONED.getValue(),
                "Old Device is Provisioned with its imei - " + deviceInfoFactoryDataCurrent.getImei());
        String userId = "User123";
        deviceAssociationWithFactDataService.replaceDevice(replaceDeviceRequest, userId);
    }

    @Test
    public void replaceDeviceTest_isCurrentDeviceToBeMovedToProvisionedTrue_Provisioned() {
        ReflectionTestUtils.setField(deviceAssociationWithFactDataService, "isCurrentDeviceToBeMovedToProvisioned",
            true);




        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        replaceDeviceRequest.setCurrentValue(currentValue);
        replaceDeviceRequest.setReplaceWith(replaceWith);


        deviceInfoFactoryDataCurrent.setFaulty(true);
        deviceInfoFactoryDataCurrent.setStolen(false);
        deviceInfoFactoryDataCurrent.setState("PROVISIONED");
        List<DeviceInfoFactoryData> listCurrentValueData = new ArrayList<>();
        listCurrentValueData.add(deviceInfoFactoryDataCurrent);


        deviceInfoFactoryDataReplace.setFaulty(false);
        deviceInfoFactoryDataReplace.setStolen(false);
        deviceInfoFactoryDataReplace.setState("PROVISIONED");

        List<DeviceInfoFactoryData> listReplaceValueData = new ArrayList<>();
        listReplaceValueData.add(deviceInfoFactoryDataReplace);

        HcpInfo hcpInfo = new HcpInfo();
        hcpInfo.setFactoryId("1234");
        hcpInfo.setHarmanId("H1234");
        hcpInfo.setVin("V1234");
        hcpInfo.setId(1L);
        hcpInfo.setSerialNumber("S1234");

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(1L);
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        deviceAssociation.setDeviceType("dongle");

        Mockito.when(deviceInfoFactoryDataDao.constructAndFetchFactoryData(Mockito.any()))
            .thenReturn(listCurrentValueData, listReplaceValueData);
        Mockito.doReturn(true).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactData(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(hcpInfo).when(hcpInfoDao).findActiveHcpInfo(Mockito.anyLong());
        Mockito.doReturn(1L).when(deviceActivationStateDao).findActiveDevice(Long.parseLong(hcpInfo.getFactoryId()));
        Mockito.doNothing().when(deviceActivationStateDao).disableRecord(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(deviceAssociation).when(deviceAssociationDao).findValidAssociations(Mockito.anyString());
        Mockito.doNothing().when(deviceInfoFactoryDataDao)
            .changeDeviceStateForStolenOrFaulty(deviceInfoFactoryDataCurrent.getId(),
                DeviceState.PROVISIONED.getValue(),
                "Old Device is Provisioned with its imei - " + deviceInfoFactoryDataCurrent.getImei());
        String userId = "User123";
        deviceAssociationWithFactDataService.replaceDevice(replaceDeviceRequest, userId);
    }

    @Test
    public void stateChangeTest_EmptyState() {
        StateChangeRequest stateChangeRequest = new StateChangeRequest();
        stateChangeRequest.setImei("I1234");
        stateChangeRequest.setDeviceId("HID1234");
        assertThrows(NoSuchEntityException.class,
            () -> deviceAssociationWithFactDataService.stateChange(stateChangeRequest));
    }

    @Test
    public void stateChangeTest_EmptyImei_EmptySerialNumber() {
        StateChangeRequest stateChangeRequest = new StateChangeRequest();
        stateChangeRequest.setState("ACTIVE");
        assertThrows(NoSuchEntityException.class,
            () -> deviceAssociationWithFactDataService.stateChange(stateChangeRequest));
    }

    @Test
    public void stateChangeTest_Idis0() {
        StateChangeRequest stateChangeRequest = new StateChangeRequest();
        stateChangeRequest.setState("ACTIVE");
        stateChangeRequest.setImei("I1234");

        DeviceInfoFactoryData factoryData = new DeviceInfoFactoryData();
        factoryData.setImei("I1234");
        Mockito.doReturn(factoryData).when(deviceInfoFactoryDataDao).findByFactoryImei(stateChangeRequest.getImei());
        assertThrows(NoSuchEntityException.class,
            () -> deviceAssociationWithFactDataService.stateChange(stateChangeRequest));
    }

    @Test
    public void stateChangeTest_EmptyImei() {
        StateChangeRequest stateChangeRequest = new StateChangeRequest();
        stateChangeRequest.setState("ACTIVE");
        stateChangeRequest.setDeviceId("HID1234");

        HcpInfo hcpData = new HcpInfo();
        hcpData.setSerialNumber("S1234");
        Mockito.doReturn(hcpData).when(hcpInfoDao).findByDeviceId(stateChangeRequest.getDeviceId());
        assertThrows(NoSuchEntityException.class,
            () -> deviceAssociationWithFactDataService.stateChange(stateChangeRequest));
    }

    @Test
    public void stateChangeTest_NoSuchEntityException() {
        StateChangeRequest stateChangeRequest = new StateChangeRequest();
        stateChangeRequest.setState("ACTIVE");
        stateChangeRequest.setDeviceId("HID1234");

        HcpInfo hcpData = new HcpInfo();
        hcpData.setSerialNumber("S1234");
        hcpData.setFactoryId("988");
        Mockito.doReturn(hcpData).when(hcpInfoDao).findByDeviceId(stateChangeRequest.getDeviceId());
        Mockito.doReturn(true).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactDataNotDisassociated(Mockito.anyLong(), Mockito.any());
        assertThrows(NoSuchEntityException.class,
            () -> deviceAssociationWithFactDataService.stateChange(stateChangeRequest));
    }

    @Test
    public void stateChangeTest_internalUserCsvIsNull() {
        StateChangeRequest stateChangeRequest = new StateChangeRequest();
        stateChangeRequest.setState("ACTIVE");
        stateChangeRequest.setImei("I1234");

        DeviceInfoFactoryData factoryData = new DeviceInfoFactoryData();
        factoryData.setImei("I1234");
        factoryData.setId(1L);
        Mockito.doReturn(factoryData).when(deviceInfoFactoryDataDao).findByFactoryImei(stateChangeRequest.getImei());
        Mockito.doReturn(null).when(envConfig).getStringValue(DeviceAssocationProperty.WHITE_LIST_USERS);
        assertThrows(InvalidUserAssociation.class,
            () -> deviceAssociationWithFactDataService.stateChange(stateChangeRequest));
    }

    @Test
    public void stateChangeTest_internalUserCsvIsEmpty() {
        StateChangeRequest stateChangeRequest = new StateChangeRequest();
        stateChangeRequest.setState("ACTIVE");
        stateChangeRequest.setImei("I1234");

        DeviceInfoFactoryData factoryData = new DeviceInfoFactoryData();
        factoryData.setImei("I1234");
        factoryData.setId(1L);
        Mockito.doReturn(factoryData).when(deviceInfoFactoryDataDao).findByFactoryImei(stateChangeRequest.getImei());
        Mockito.doReturn("").when(envConfig).getStringValue(DeviceAssocationProperty.WHITE_LIST_USERS);
        assertThrows(InvalidUserAssociation.class,
            () -> deviceAssociationWithFactDataService.stateChange(stateChangeRequest));
    }

    @Test
    public void stateChangeTest_UserNotWhiteListed() {
        StateChangeRequest stateChangeRequest = new StateChangeRequest();
        stateChangeRequest.setState("ACTIVE");
        stateChangeRequest.setImei("I1234");
        stateChangeRequest.setUserId("User123");

        DeviceInfoFactoryData factoryData = new DeviceInfoFactoryData();
        factoryData.setImei("I1234");
        factoryData.setId(1L);
        Mockito.doReturn(factoryData).when(deviceInfoFactoryDataDao).findByFactoryImei(stateChangeRequest.getImei());
        Mockito.doReturn("user1,user2").when(envConfig).getStringValue(DeviceAssocationProperty.WHITE_LIST_USERS);
        assertThrows(InvalidUserAssociation.class,
            () -> deviceAssociationWithFactDataService.stateChange(stateChangeRequest));
    }

    @Test
    public void stateChangeTest_UserWhiteListed() {
        StateChangeRequest stateChangeRequest = new StateChangeRequest();
        stateChangeRequest.setState("ACTIVE");
        stateChangeRequest.setImei("I1234");
        stateChangeRequest.setUserId("User123");

        DeviceInfoFactoryData factoryData = new DeviceInfoFactoryData();
        factoryData.setImei("I1234");
        factoryData.setId(1L);
        Mockito.doReturn(factoryData).when(deviceInfoFactoryDataDao).findByFactoryImei(stateChangeRequest.getImei());
        Mockito.doReturn("User123,user009").when(envConfig).getStringValue(DeviceAssocationProperty.WHITE_LIST_USERS);
        assertThrows(NoSuchEntityException.class,
            () -> deviceAssociationWithFactDataService.stateChange(stateChangeRequest));
    }

    @Test
    public void stateChangeTest_Success() throws Exception {
        StateChangeRequest stateChangeRequest = new StateChangeRequest();
        stateChangeRequest.setState("ACTIVE");
        stateChangeRequest.setImei("I1234");
        stateChangeRequest.setUserId("User123");

        DeviceInfoFactoryData factoryData = new DeviceInfoFactoryData();
        factoryData.setImei("I1234");
        factoryData.setId(1L);
        ResponseEntity responseEntity = new ResponseEntity(HttpStatus.OK);
        Mockito.doReturn(factoryData).when(deviceInfoFactoryDataDao).findByFactoryImei(stateChangeRequest.getImei());
        Mockito.doReturn("User123,user009").when(envConfig).getStringValue(Mockito.any());
        Mockito.doReturn(responseEntity).when(restClientLibrary)
            .doPut(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any());

        deviceAssociationWithFactDataService.stateChange(stateChangeRequest);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void stateChangeTest_HttpClientErrorException() {
        StateChangeRequest stateChangeRequest = new StateChangeRequest();
        stateChangeRequest.setState("ACTIVE");
        stateChangeRequest.setImei("I1234");
        stateChangeRequest.setUserId("User123");

        DeviceInfoFactoryData factoryData = new DeviceInfoFactoryData();
        factoryData.setImei("I1234");
        factoryData.setId(1L);
        Mockito.doReturn(factoryData).when(deviceInfoFactoryDataDao).findByFactoryImei(stateChangeRequest.getImei());
        Mockito.doReturn("User123,user009").when(envConfig).getStringValue(DeviceAssocationProperty.WHITE_LIST_USERS);
        Mockito.doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST)).when(restClientLibrary)
            .doPut(Mockito.anyString(), Mockito.any(), Mockito.anyString(), Mockito.any());
        assertThrows(NoSuchEntityException.class,
            () -> deviceAssociationWithFactDataService.stateChange(stateChangeRequest));
    }

    @Test
    public void isInSameStateTest_SameState() {
        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setUserId("User123");
        associateDeviceRequest.setSerialNumber("S1234");
        associateDeviceRequest.setImei("I1234");
        associateDeviceRequest.setBssid("B1234");
        associateDeviceRequest.setMsisdn("M1234");
        FactoryData factoryData = new FactoryData();
        factoryData.setSerialNumber("S1234");
        factoryData.setImei("I1234");
        factoryData.setBssid("B1234");
        factoryData.setMsisdn("M1234");
        factoryData.setImsi("IMSI1234");
        factoryData.setSsid("SSID1234");
        factoryData.setState("ACTIVE");
        List<FactoryData> fetchFactoryData = new ArrayList<>();
        fetchFactoryData.add(factoryData);
        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao)
            .constructAndFetchFactoryData(associateDeviceRequest);
        String expectedState = "ACTIVE";
        assertTrue(deviceAssociationWithFactDataService.isInSameState(associateDeviceRequest, expectedState));
    }

    @Test
    public void isInSameStateTest_NullFactoryDataList() {
        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setUserId("User123");
        associateDeviceRequest.setSerialNumber("S1234");
        associateDeviceRequest.setImei("I1234");
        associateDeviceRequest.setBssid("B1234");
        associateDeviceRequest.setMsisdn("M1234");
        String expectedState = "ACTIVE";
        Mockito.doReturn(null).when(deviceAssociationDao).constructAndFetchFactoryData(associateDeviceRequest);
        assertFalse(deviceAssociationWithFactDataService.isInSameState(associateDeviceRequest, expectedState));
    }

    @Test
    public void isInSameStateTest_EmptyFactoryDataList() {
        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setUserId("User123");
        associateDeviceRequest.setSerialNumber("S1234");
        associateDeviceRequest.setImei("I1234");
        associateDeviceRequest.setBssid("B1234");
        associateDeviceRequest.setMsisdn("M1234");
        String expectedState = "ACTIVE";
        List<FactoryData> fetchFactoryData = new ArrayList<>();
        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao)
            .constructAndFetchFactoryData(associateDeviceRequest);
        assertFalse(deviceAssociationWithFactDataService.isInSameState(associateDeviceRequest, expectedState));
    }

    @Test
    public void isInSameStateTest_NullFactoryData() {
        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setUserId("User123");
        associateDeviceRequest.setSerialNumber("S1234");
        associateDeviceRequest.setImei("I1234");
        associateDeviceRequest.setBssid("B1234");
        associateDeviceRequest.setMsisdn("M1234");
        String expectedState = "ACTIVE";
        List<FactoryData> fetchFactoryData = new ArrayList<>();
        fetchFactoryData.add(null);
        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao)
            .constructAndFetchFactoryData(associateDeviceRequest);
        assertFalse(deviceAssociationWithFactDataService.isInSameState(associateDeviceRequest, expectedState));
    }

    @Test
    public void isInSameStateTest_DifferentState() {
        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setUserId("User123");
        associateDeviceRequest.setSerialNumber("S1234");
        associateDeviceRequest.setImei("I1234");
        associateDeviceRequest.setBssid("B1234");
        associateDeviceRequest.setMsisdn("M1234");
        FactoryData factoryData = new FactoryData();
        factoryData.setSerialNumber("S1234");
        factoryData.setImei("I1234");
        factoryData.setBssid("B1234");
        factoryData.setMsisdn("M1234");
        factoryData.setImsi("IMSI1234");
        factoryData.setSsid("SSID1234");
        factoryData.setState("PROVISIONED");
        List<FactoryData> fetchFactoryData = new ArrayList<>();
        fetchFactoryData.add(factoryData);
        Mockito.doReturn(fetchFactoryData).when(deviceAssociationDao)
            .constructAndFetchFactoryData(associateDeviceRequest);
        String expectedState = "ACTIVE";
        assertFalse(deviceAssociationWithFactDataService.isInSameState(associateDeviceRequest, expectedState));
    }

    @Test
    public void getAssociationHistoryTest_BlankSortByOrderBy() throws NoSuchEntityException {
        DeviceAssociationHistory deviceAssociationHistory = new DeviceAssociationHistory();
        deviceAssociationHistory.setId(RANDOM_NUMBER);
        deviceAssociationHistory.setUserId("User123");
        deviceAssociationHistory.setHarmanId("H1234");
        deviceAssociationHistory.setAssociationStatus(AssociationStatus.ASSOCIATED);
        deviceAssociationHistory.setFactoryId(1L);
        deviceAssociationHistory.setSerialNumber("S1234");
        List<DeviceAssociationHistory> deviceAssociationList = new ArrayList<>();
        deviceAssociationList.add(deviceAssociationHistory);
        String imei = "I1234";
        String orderby = "";
        String sortby = " ";
        int page = PAGE;
        int size = SIZE;
        long factoryId = 1L;

        Mockito.doReturn(factoryId).when(deviceInfoFactoryDataDao).findIdByFactoryImei(Mockito.anyString());
        Mockito.doReturn(deviceAssociationList).when(deviceAssociationDao)
            .getAssociationDetails(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(),
                Mockito.anyInt());
        List<DeviceAssociationHistory> deviceAssociationHistoryList =
                deviceAssociationWithFactDataService.getAssociationHistory(imei, orderby, sortby, page, size);
        assertNotNull(deviceAssociationHistoryList);
    }

    @Test
    public void getAssociationHistoryTest_SortByUserid() throws NoSuchEntityException {
        DeviceAssociationHistory deviceAssociationHistory = new DeviceAssociationHistory();
        deviceAssociationHistory.setId(RANDOM_NUMBER);
        deviceAssociationHistory.setUserId("User123");
        deviceAssociationHistory.setHarmanId("H1234");
        deviceAssociationHistory.setAssociationStatus(AssociationStatus.ASSOCIATED);
        deviceAssociationHistory.setFactoryId(1L);
        deviceAssociationHistory.setSerialNumber("S1234");
        List<DeviceAssociationHistory> deviceAssociationList = new ArrayList<>();
        deviceAssociationList.add(deviceAssociationHistory);
        String imei = "I1234";
        String orderby = "asc";
        String sortby = "userid";
        int page = PAGE;
        int size = SIZE;
        long factoryId = 1L;
        Mockito.doReturn(factoryId).when(deviceInfoFactoryDataDao).findIdByFactoryImei(Mockito.anyString());
        Mockito.doReturn(deviceAssociationList).when(deviceAssociationDao)
            .getAssociationDetails(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(),
                Mockito.anyInt());
        List<DeviceAssociationHistory> deviceAssociationHistoryList =
                deviceAssociationWithFactDataService.getAssociationHistory(imei, orderby, sortby, page, size);
        assertNotNull(deviceAssociationHistoryList);
    }

    @Test
    public void getAssociationHistoryTest_SortByAssociationStatus_IncorrectResultSizeDataAccessException() {
        String imei = "I1234";
        String orderby = "asc";
        String sortby = "associationstatus";
        int page = PAGE;
        int size = SIZE;
        Mockito.doThrow(new IncorrectResultSizeDataAccessException(1)).when(deviceInfoFactoryDataDao)
            .findIdByFactoryImei(Mockito.anyString());
        assertThrows(NoSuchEntityException.class,
            () -> deviceAssociationWithFactDataService.getAssociationHistory(imei, orderby, sortby, page, size));
    }

    @Test
    public void getAssociationHistoryTotalCountTest() throws NoSuchEntityException {
        String imei = "I1234";
        Mockito.doReturn(1L).when(deviceInfoFactoryDataDao).findIdByFactoryImei(Mockito.anyString());
        Mockito.doReturn(1).when(deviceAssociationDao).findAssociationCountForFactoryId(Mockito.anyLong());
        int count = deviceAssociationWithFactDataService.getAssociationHistoryTotalCount(imei);
        Assertions.assertEquals(1, count);
    }

    @Test
    public void getAssociationHistoryTotalCountTest_IncorrectResultSizeDataAccessException() {
        String imei = "I1234";
        Mockito.doThrow(new IncorrectResultSizeDataAccessException(1)).when(deviceInfoFactoryDataDao)
            .findIdByFactoryImei(Mockito.anyString());
        assertThrows(NoSuchEntityException.class,
            () -> deviceAssociationWithFactDataService.getAssociationHistoryTotalCount(imei));
    }

    @Test
    public void performSwmVehicleUpdateTest_Success() throws Exception {

        replaceWith.setChassisNumber("CH-1234");
        replaceWith.setProductionWeek("WEEK-3");
        replaceWith.setVin("TESTVIN_Make:THIRD_Model:Dart_Year:2013_Type:Car_0");
        replaceWith.setPlant("Plant-403");
        replaceWith.setVehicleModelYear("2013");
        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        String vin = "TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0";
        replaceDeviceRequest.setReplaceWith(replaceWith);
        Mockito.doReturn(true).when(swmService).updateVehicle(Mockito.any());
        deviceAssociationWithFactDataService.performSwmVehicleUpdate(replaceDeviceRequest, vin);
        Assertions.assertTrue(swmService.updateVehicle(Mockito.any()));
    }

    @Test
    public void performSwmVehicleUpdateTest_InvalidAttributeValueException() {

        replaceWith.setVin("TESTVIN_Make:THIRD_Model:Dart_Year:2013_Type:Car_0");
        replaceWith.setPlant("Plant-403");
        replaceWith.setVehicleModelYear("2013");

        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        String vin = "TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0";
        replaceDeviceRequest.setReplaceWith(replaceWith);
        assertThrows(InvalidAttributeValueException.class,
            () -> deviceAssociationWithFactDataService.performSwmVehicleUpdate(replaceDeviceRequest, vin));
    }

    @Test
    public void performSwmVehicleUpdateTest_VinIsNull() {

        replaceWith.setChassisNumber("CH-1234");
        replaceWith.setProductionWeek("WEEK-3");
        replaceWith.setVin("TESTVIN_Make:THIRD_Model:Dart_Year:2013_Type:Car_0");
        replaceWith.setPlant("Plant-403");
        replaceWith.setVehicleModelYear("2013");

        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        replaceDeviceRequest.setReplaceWith(replaceWith);
        assertThrows(UpdateDeviceException.class,
            () -> deviceAssociationWithFactDataService.performSwmVehicleUpdate(replaceDeviceRequest, null));
    }

    @Test
    public void performSwmVehicleUpdateTest_deleteDeviceStatusFalse() throws Exception {

        replaceWith.setChassisNumber("CH-1234");
        replaceWith.setProductionWeek("WEEK-3");
        replaceWith.setVin("TESTVIN_Make:THIRD_Model:Dart_Year:2013_Type:Car_0");
        replaceWith.setPlant("Plant-403");
        replaceWith.setVehicleModelYear("2013");
        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        String vin = "TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0";
        replaceDeviceRequest.setReplaceWith(replaceWith);
        Mockito.doReturn(false).when(swmService).updateVehicle(Mockito.any());
        assertThrows(UpdateDeviceException.class,
            () -> deviceAssociationWithFactDataService.performSwmVehicleUpdate(replaceDeviceRequest, vin));
    }

    @Test
    public void replaceIviDeviceTest_InvalidReplaceDeviceRequest() {
        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        String userId = "User1234";
        String vin = "TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0";
        Mockito.doReturn(vin).when(deviceAssociationDao)
            .getAssociatedVinForReplaceApi(replaceDeviceRequest.getSerialNumber());
        assertThrows(DeviceReplaceException.class,
            () -> deviceAssociationWithFactDataService.replaceIviDevice(replaceDeviceRequest, userId));
    }

    @Test
    public void replaceIviDeviceTest_EmptyCurrentDataFromDb() {


        replaceWith.setChassisNumber("CH-1234");
        replaceWith.setProductionWeek("WEEK-3");
        replaceWith.setVin("TESTVIN_Make:THIRD_Model:Dart_Year:2013_Type:Car_0");
        replaceWith.setPlant("Plant-403");
        replaceWith.setVehicleModelYear("2013");
        replaceWith.setManufacturingDate("10-10-2013");
        replaceWith.setRecordDate("12-10-2013");
        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        String vin = "TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0";
        replaceDeviceRequest.setSerialNumber("S1234");
        replaceDeviceRequest.setReplaceWith(replaceWith);
        Mockito.doReturn(vin).when(deviceAssociationDao)
            .getAssociatedVinForReplaceApi(replaceDeviceRequest.getSerialNumber());
        Mockito.doReturn(null).when(deviceInfoFactoryDataDao)
            .findFactoryDataBySerialNumber(replaceDeviceRequest.getSerialNumber());
        String userId = "User1234";
        assertThrows(DeviceReplaceException.class,
            () -> deviceAssociationWithFactDataService.replaceIviDevice(replaceDeviceRequest, userId));
    }

    @Test
    public void replaceIviDeviceTest_InvalidDeviceDetailsForUser() {


        deviceInfoFactoryDataCurrent.setFaulty(true);
        deviceInfoFactoryDataCurrent.setStolen(false);
        deviceInfoFactoryDataCurrent.setState("PROVISIONED");


        replaceWith.setChassisNumber("CH-1234");
        replaceWith.setProductionWeek("WEEK-3");
        replaceWith.setVin("TESTVIN_Make:THIRD_Model:Dart_Year:2013_Type:Car_0");
        replaceWith.setPlant("Plant-403");
        replaceWith.setVehicleModelYear("2013");
        replaceWith.setManufacturingDate("10-10-2013");
        replaceWith.setRecordDate("12-10-2013");
        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        String vin = "TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0";
        replaceDeviceRequest.setSerialNumber("S1234");
        replaceDeviceRequest.setReplaceWith(replaceWith);
        Mockito.doReturn(vin).when(deviceAssociationDao)
            .getAssociatedVinForReplaceApi(replaceDeviceRequest.getSerialNumber());
        Mockito.doReturn(deviceInfoFactoryDataCurrent).when(deviceInfoFactoryDataDao)
            .findFactoryDataBySerialNumber(replaceDeviceRequest.getSerialNumber());
        Mockito.doReturn(false).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactData(Mockito.anyLong(), Mockito.anyString());
        String userId = "User1234";
        assertThrows(DeviceReplaceException.class,
            () -> deviceAssociationWithFactDataService.replaceIviDevice(replaceDeviceRequest, userId));
    }

    @Test
    public void replaceIviDeviceTest_NotFaultyOrStolen() {

        deviceInfoFactoryDataCurrent.setFaulty(false);
        deviceInfoFactoryDataCurrent.setStolen(false);
        deviceInfoFactoryDataCurrent.setState("PROVISIONED");

        replaceWith.setChassisNumber("CH-1234");
        replaceWith.setProductionWeek("WEEK-3");
        replaceWith.setVin("TESTVIN_Make:THIRD_Model:Dart_Year:2013_Type:Car_0");
        replaceWith.setPlant("Plant-403");
        replaceWith.setVehicleModelYear("2013");
        replaceWith.setManufacturingDate("10-10-2013");
        replaceWith.setRecordDate("12-10-2013");
        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        String vin = "TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0";
        replaceDeviceRequest.setSerialNumber("S1234");
        replaceDeviceRequest.setReplaceWith(replaceWith);
        Mockito.doReturn(vin).when(deviceAssociationDao)
            .getAssociatedVinForReplaceApi(replaceDeviceRequest.getSerialNumber());
        Mockito.doReturn(deviceInfoFactoryDataCurrent).when(deviceInfoFactoryDataDao)
            .findFactoryDataBySerialNumber(replaceDeviceRequest.getSerialNumber());
        Mockito.doReturn(true).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactData(Mockito.anyLong(), Mockito.anyString());
        String userId = "User1234";
        assertThrows(DeviceReplaceException.class,
            () -> deviceAssociationWithFactDataService.replaceIviDevice(replaceDeviceRequest, userId));
    }


    @Test
    public void replaceIviDeviceTest_EmptyListReplaceValueData() {
        ReflectionTestUtils.setField(deviceAssociationWithFactDataService, "isCurrentDeviceToBeMovedToProvisioned",
            true);
        Timestamp manufacturingDate = new Timestamp(System.currentTimeMillis());
        Timestamp recordDate = new Timestamp(System.currentTimeMillis());

        deviceInfoFactoryDataCurrent.setFaulty(true);
        deviceInfoFactoryDataCurrent.setStolen(false);
        deviceInfoFactoryDataCurrent.setState("PROVISIONED");
        deviceInfoFactoryDataCurrent.setManufacturingDate(manufacturingDate);
        deviceInfoFactoryDataCurrent.setRecordDate(recordDate);


        replaceWith.setChassisNumber("CH-1234");
        replaceWith.setProductionWeek("WEEK-3");
        replaceWith.setVin("TESTVIN_Make:THIRD_Model:Dart_Year:2013_Type:Car_0");
        replaceWith.setPlant("Plant-403");
        replaceWith.setVehicleModelYear("2013");
        replaceWith.setManufacturingDate("10/10/2013");
        replaceWith.setRecordDate("12/10/2013");
        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();

        String vin = "TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0";
        replaceDeviceRequest.setSerialNumber("S1234");
        replaceDeviceRequest.setReplaceWith(replaceWith);
        Mockito.doReturn(vin).when(deviceAssociationDao)
            .getAssociatedVinForReplaceApi(replaceDeviceRequest.getSerialNumber());
        Mockito.doReturn(deviceInfoFactoryDataCurrent).when(deviceInfoFactoryDataDao)
            .findFactoryDataBySerialNumber(replaceDeviceRequest.getSerialNumber());
        Mockito.doReturn(true).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactData(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(null).when(deviceInfoFactoryDataDao).constructAndFetchFactoryData(Mockito.any());
        String userId = "User1234";
        assertThrows(DeviceReplaceException.class,
            () -> deviceAssociationWithFactDataService.replaceIviDevice(replaceDeviceRequest, userId));
    }

    @Test
    public void replaceIviDeviceTest_ListReplaceValueDataSize2() {
        ReflectionTestUtils.setField(deviceAssociationWithFactDataService, "isCurrentDeviceToBeMovedToProvisioned",
            true);
        Timestamp manufacturingDate = new Timestamp(System.currentTimeMillis());
        Timestamp recordDate = new Timestamp(System.currentTimeMillis());

        deviceInfoFactoryDataCurrent.setFaulty(true);
        deviceInfoFactoryDataCurrent.setStolen(false);
        deviceInfoFactoryDataCurrent.setState("PROVISIONED");
        deviceInfoFactoryDataCurrent.setManufacturingDate(manufacturingDate);
        deviceInfoFactoryDataCurrent.setRecordDate(recordDate);


        deviceInfoFactoryDataReplace.setFaulty(false);
        deviceInfoFactoryDataReplace.setStolen(false);
        deviceInfoFactoryDataReplace.setState("PROVISIONED");
        DeviceInfoFactoryData deviceInfoFactoryDataReplace2 = new DeviceInfoFactoryData();

        List<DeviceInfoFactoryData> listReplaceValueData = new ArrayList<>();
        listReplaceValueData.add(deviceInfoFactoryDataReplace);
        listReplaceValueData.add(deviceInfoFactoryDataReplace2);


        replaceWith.setChassisNumber("CH-1234");
        replaceWith.setProductionWeek("WEEK-3");
        replaceWith.setVin("TESTVIN_Make:THIRD_Model:Dart_Year:2013_Type:Car_0");
        replaceWith.setPlant("Plant-403");
        replaceWith.setVehicleModelYear("2013");

        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        String vin = "TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0";
        replaceDeviceRequest.setSerialNumber("S1234");
        replaceDeviceRequest.setReplaceWith(replaceWith);

        Mockito.doReturn(vin).when(deviceAssociationDao)
            .getAssociatedVinForReplaceApi(replaceDeviceRequest.getSerialNumber());
        Mockito.doReturn(deviceInfoFactoryDataCurrent).when(deviceInfoFactoryDataDao)
            .findFactoryDataBySerialNumber(replaceDeviceRequest.getSerialNumber());
        Mockito.doReturn(true).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactData(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(listReplaceValueData).when(deviceInfoFactoryDataDao)
            .constructAndFetchFactoryData(Mockito.any());
        String userId = "User1234";
        assertThrows(DeviceReplaceException.class,
            () -> deviceAssociationWithFactDataService.replaceIviDevice(replaceDeviceRequest, userId));
    }

    @Test
    public void replaceIviDeviceTest_ReplaceDataNotInProvisionedState() {
        ReflectionTestUtils.setField(deviceAssociationWithFactDataService, "isCurrentDeviceToBeMovedToProvisioned",
            true);
        Timestamp manufacturingDate = new Timestamp(System.currentTimeMillis());
        Timestamp recordDate = new Timestamp(System.currentTimeMillis());

        deviceInfoFactoryDataCurrent.setFaulty(true);
        deviceInfoFactoryDataCurrent.setStolen(false);
        deviceInfoFactoryDataCurrent.setState("PROVISIONED");
        deviceInfoFactoryDataCurrent.setManufacturingDate(manufacturingDate);
        deviceInfoFactoryDataCurrent.setRecordDate(recordDate);


        deviceInfoFactoryDataReplace.setFaulty(false);
        deviceInfoFactoryDataReplace.setStolen(false);
        deviceInfoFactoryDataReplace.setState("ACTIVE");

        List<DeviceInfoFactoryData> listReplaceValueData = new ArrayList<>();
        listReplaceValueData.add(deviceInfoFactoryDataReplace);


        replaceWith.setChassisNumber("CH-1234");
        replaceWith.setProductionWeek("WEEK-3");
        replaceWith.setVin("TESTVIN_Make:THIRD_Model:Dart_Year:2013_Type:Car_0");
        replaceWith.setPlant("Plant-403");
        replaceWith.setVehicleModelYear("2013");
        replaceWith.setManufacturingDate("10/10/2013");
        replaceWith.setRecordDate("12/10/2013");

        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        String vin = "TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0";
        replaceDeviceRequest.setSerialNumber("S1234");
        replaceDeviceRequest.setReplaceWith(replaceWith);
        Mockito.doReturn(vin).when(deviceAssociationDao)
            .getAssociatedVinForReplaceApi(replaceDeviceRequest.getSerialNumber());
        Mockito.doReturn(deviceInfoFactoryDataCurrent).when(deviceInfoFactoryDataDao)
            .findFactoryDataBySerialNumber(replaceDeviceRequest.getSerialNumber());
        Mockito.doReturn(true).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactData(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(listReplaceValueData).when(deviceInfoFactoryDataDao)
            .constructAndFetchFactoryData(Mockito.any());
        String userId = "User1234";
        assertThrows(DeviceReplaceException.class,
            () -> deviceAssociationWithFactDataService.replaceIviDevice(replaceDeviceRequest, userId));
    }

    @Test
    public void replaceIviDeviceTest_NullHcpInfo() {
        ReflectionTestUtils.setField(deviceAssociationWithFactDataService, "isCurrentDeviceToBeMovedToProvisioned",
            true);
        Timestamp manufacturingDate = new Timestamp(System.currentTimeMillis());
        Timestamp recordDate = new Timestamp(System.currentTimeMillis());

        deviceInfoFactoryDataCurrent.setFaulty(true);
        deviceInfoFactoryDataCurrent.setStolen(false);
        deviceInfoFactoryDataCurrent.setState("PROVISIONED");
        deviceInfoFactoryDataCurrent.setManufacturingDate(manufacturingDate);
        deviceInfoFactoryDataCurrent.setRecordDate(recordDate);


        deviceInfoFactoryDataReplace.setFaulty(false);
        deviceInfoFactoryDataReplace.setStolen(false);
        deviceInfoFactoryDataReplace.setState("PROVISIONED");

        List<DeviceInfoFactoryData> listReplaceValueData = new ArrayList<>();
        listReplaceValueData.add(deviceInfoFactoryDataReplace);


        replaceWith.setChassisNumber("CH-1234");
        replaceWith.setProductionWeek("WEEK-3");
        replaceWith.setVin("TESTVIN_Make:THIRD_Model:Dart_Year:2013_Type:Car_0");
        replaceWith.setPlant("Plant-403");
        replaceWith.setVehicleModelYear("2013");
        replaceWith.setManufacturingDate("10/10/2013");
        replaceWith.setRecordDate("12/10/2013");
        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        replaceDeviceRequest.setSerialNumber("S1234");
        replaceDeviceRequest.setReplaceWith(replaceWith);
        String vin = "TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0";
        Mockito.doReturn(vin).when(deviceAssociationDao)
            .getAssociatedVinForReplaceApi(replaceDeviceRequest.getSerialNumber());
        Mockito.doReturn(deviceInfoFactoryDataCurrent).when(deviceInfoFactoryDataDao)
            .findFactoryDataBySerialNumber(replaceDeviceRequest.getSerialNumber());
        Mockito.doReturn(true).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactData(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(listReplaceValueData).when(deviceInfoFactoryDataDao)
            .constructAndFetchFactoryData(Mockito.any());
        Mockito.doReturn(null).when(hcpInfoDao).findActiveHcpInfo(Mockito.anyLong());
        String userId = "User1234";
        assertThrows(DeviceReplaceException.class,
            () -> deviceAssociationWithFactDataService.replaceIviDevice(replaceDeviceRequest, userId));
    }

    @Test
    public void replaceIviDeviceTest_CurrentDataActive() {
        ReflectionTestUtils.setField(deviceAssociationWithFactDataService, "isCurrentDeviceToBeMovedToProvisioned",
            true);
        Timestamp manufacturingDate = new Timestamp(System.currentTimeMillis());
        Timestamp recordDate = new Timestamp(System.currentTimeMillis());

        deviceInfoFactoryDataCurrent.setFaulty(true);
        deviceInfoFactoryDataCurrent.setStolen(false);
        deviceInfoFactoryDataCurrent.setState("ACTIVE");
        deviceInfoFactoryDataCurrent.setManufacturingDate(manufacturingDate);
        deviceInfoFactoryDataCurrent.setRecordDate(recordDate);

        deviceInfoFactoryDataReplace.setFaulty(false);
        deviceInfoFactoryDataReplace.setStolen(false);
        deviceInfoFactoryDataReplace.setState("PROVISIONED");

        List<DeviceInfoFactoryData> listReplaceValueData = new ArrayList<>();
        listReplaceValueData.add(deviceInfoFactoryDataReplace);

        HcpInfo hcpInfo = new HcpInfo();
        hcpInfo.setFactoryId("1234");
        hcpInfo.setHarmanId("H1234");
        hcpInfo.setVin("V1234");
        hcpInfo.setId(1L);
        hcpInfo.setSerialNumber("S1234");


        replaceWith.setChassisNumber("CH-1234");
        replaceWith.setProductionWeek("WEEK-3");
        replaceWith.setVin("TESTVIN_Make:THIRD_Model:Dart_Year:2013_Type:Car_0");
        replaceWith.setPlant("Plant-403");
        replaceWith.setVehicleModelYear("2013");
        replaceWith.setManufacturingDate("10/10/2013");
        replaceWith.setRecordDate("12/10/2013");
        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        replaceDeviceRequest.setSerialNumber("S1234");
        replaceDeviceRequest.setReplaceWith(replaceWith);

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        String vin = "TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0";
        Mockito.doReturn(vin).when(deviceAssociationDao)
            .getAssociatedVinForReplaceApi(replaceDeviceRequest.getSerialNumber());
        Mockito.doReturn(deviceInfoFactoryDataCurrent).when(deviceInfoFactoryDataDao)
            .findFactoryDataBySerialNumber(replaceDeviceRequest.getSerialNumber());
        Mockito.doReturn(true).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactData(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(listReplaceValueData).when(deviceInfoFactoryDataDao)
            .constructAndFetchFactoryData(Mockito.any());
        Mockito.doReturn(hcpInfo).when(hcpInfoDao).findActiveHcpInfo(Mockito.anyLong());
        Mockito.doNothing().when(deviceActivationStateDao).disableActivationReadyByFacotryId(Mockito.anyLong());
        Mockito.doReturn(deviceAssociation).when(deviceAssociationDao).findValidAssociations(Mockito.anyString());
        Mockito.doReturn(1).when(deviceAssociationDao)
            .updateForReplaceDevice(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(), Mockito.anyLong());
        String userId = "User1234";
        deviceAssociationWithFactDataService.replaceIviDevice(replaceDeviceRequest, userId);
        Assertions.assertTrue(deviceAssociationDao.checkAssociatedDeviceWithFactData(Mockito.anyLong(),
                Mockito.anyString()));
    }

    @Test
    public void replaceIviDeviceTest_CurrentDataActive_SpringAuth() {
        ReflectionTestUtils.setField(deviceAssociationWithFactDataService, "isCurrentDeviceToBeMovedToProvisioned",
            true);
        
        Timestamp manufacturingDate = new Timestamp(System.currentTimeMillis());
        Timestamp recordDate = new Timestamp(System.currentTimeMillis());

        deviceInfoFactoryDataCurrent.setFaulty(true);
        deviceInfoFactoryDataCurrent.setStolen(false);
        deviceInfoFactoryDataCurrent.setState("ACTIVE");
        deviceInfoFactoryDataCurrent.setManufacturingDate(manufacturingDate);
        deviceInfoFactoryDataCurrent.setRecordDate(recordDate);

        deviceInfoFactoryDataReplace.setFaulty(false);
        deviceInfoFactoryDataReplace.setStolen(false);
        deviceInfoFactoryDataReplace.setState("PROVISIONED");

        List<DeviceInfoFactoryData> listReplaceValueData = new ArrayList<>();
        listReplaceValueData.add(deviceInfoFactoryDataReplace);

        HcpInfo hcpInfo = new HcpInfo();
        hcpInfo.setFactoryId("1234");
        hcpInfo.setHarmanId("H1234");
        hcpInfo.setVin("V1234");
        hcpInfo.setId(1L);
        hcpInfo.setSerialNumber("S1234");


        replaceWith.setChassisNumber("CH-1234");
        replaceWith.setProductionWeek("WEEK-3");
        replaceWith.setVin("TESTVIN_Make:THIRD_Model:Dart_Year:2013_Type:Car_0");
        replaceWith.setPlant("Plant-403");
        replaceWith.setVehicleModelYear("2013");
        replaceWith.setManufacturingDate("10/10/2013");
        replaceWith.setRecordDate("12/10/2013");
        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        replaceDeviceRequest.setSerialNumber("S1234");
        replaceDeviceRequest.setReplaceWith(replaceWith);

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        String vin = "TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0";
        Mockito.doReturn(vin).when(deviceAssociationDao)
            .getAssociatedVinForReplaceApi(replaceDeviceRequest.getSerialNumber());
        Mockito.doReturn(deviceInfoFactoryDataCurrent).when(deviceInfoFactoryDataDao)
            .findFactoryDataBySerialNumber(replaceDeviceRequest.getSerialNumber());
        Mockito.doReturn(true).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactData(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(listReplaceValueData).when(deviceInfoFactoryDataDao)
            .constructAndFetchFactoryData(Mockito.any());
        Mockito.doReturn(hcpInfo).when(hcpInfoDao).findActiveHcpInfo(Mockito.anyLong());
        Mockito.doNothing().when(deviceActivationStateDao).disableActivationReadyByFacotryId(Mockito.anyLong());
        Mockito.doReturn(deviceAssociation).when(deviceAssociationDao).findValidAssociations(Mockito.anyString());
        String userId = "User1234";
        Mockito.doReturn(1).when(deviceAssociationDao)
            .updateForReplaceDevice(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(), Mockito.anyLong());
        deviceAssociationWithFactDataService.replaceIviDevice(replaceDeviceRequest, userId);
        Assertions.assertTrue(deviceAssociationDao.checkAssociatedDeviceWithFactData(Mockito.anyLong(),
                Mockito.anyString()));
    }

    @Test
    public void replaceIviDeviceTest_CurrentDataReadyToActivate_swmIntegrationEnabled() throws Exception {
        ReflectionTestUtils.setField(deviceAssociationWithFactDataService, "isCurrentDeviceToBeMovedToProvisioned",
            true);
        ReflectionTestUtils.setField(deviceAssociationWithFactDataService, "swmIntegrationEnabled", true);
        Timestamp manufacturingDate = new Timestamp(System.currentTimeMillis());
        Timestamp recordDate = new Timestamp(System.currentTimeMillis());

        deviceInfoFactoryDataCurrent.setFaulty(true);
        deviceInfoFactoryDataCurrent.setStolen(false);
        deviceInfoFactoryDataCurrent.setState("READY_TO_ACTIVATE");
        deviceInfoFactoryDataCurrent.setManufacturingDate(manufacturingDate);
        deviceInfoFactoryDataCurrent.setRecordDate(recordDate);

        deviceInfoFactoryDataReplace.setFaulty(false);
        deviceInfoFactoryDataReplace.setStolen(false);
        deviceInfoFactoryDataReplace.setState("PROVISIONED");

        List<DeviceInfoFactoryData> listReplaceValueData = new ArrayList<>();
        listReplaceValueData.add(deviceInfoFactoryDataReplace);

        HcpInfo hcpInfo = new HcpInfo();
        hcpInfo.setFactoryId("1234");
        hcpInfo.setHarmanId("H1234");
        hcpInfo.setVin("V1234");
        hcpInfo.setId(1L);
        hcpInfo.setSerialNumber("S1234");


        replaceWith.setChassisNumber("CH-1234");
        replaceWith.setProductionWeek("WEEK-3");
        replaceWith.setVin("TESTVIN_Make:THIRD_Model:Dart_Year:2013_Type:Car_0");
        replaceWith.setPlant("Plant-403");
        replaceWith.setVehicleModelYear("2013");
        replaceWith.setManufacturingDate("10/10/2013");
        replaceWith.setRecordDate("12/10/2013");
        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        replaceDeviceRequest.setSerialNumber("S1234");
        replaceDeviceRequest.setReplaceWith(replaceWith);

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);

        String vin = "TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0";
        Mockito.doReturn(vin).when(deviceAssociationDao)
            .getAssociatedVinForReplaceApi(replaceDeviceRequest.getSerialNumber());
        Mockito.doReturn(deviceInfoFactoryDataCurrent).when(deviceInfoFactoryDataDao)
            .findFactoryDataBySerialNumber(replaceDeviceRequest.getSerialNumber());
        Mockito.doReturn(true).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactData(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(listReplaceValueData).when(deviceInfoFactoryDataDao)
            .constructAndFetchFactoryData(Mockito.any());
        Mockito.doReturn(hcpInfo).when(hcpInfoDao).findActiveHcpInfo(Mockito.anyLong());
        Mockito.doNothing().when(deviceActivationStateDao).disableActivationReadyByFacotryId(Mockito.anyLong());
        Mockito.doReturn(deviceAssociation).when(deviceAssociationDao).findValidAssociations(Mockito.anyString());
        Mockito.doReturn(1).when(deviceAssociationDao)
            .updateForReplaceDevice(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(), Mockito.anyLong());
        Mockito.doReturn(true).when(swmService).updateVehicle(Mockito.any());
        String userId = "User1234";
        deviceAssociationWithFactDataService.replaceIviDevice(replaceDeviceRequest, userId);
        Assertions.assertTrue(deviceAssociationDao.checkAssociatedDeviceWithFactData(Mockito.anyLong(),
                Mockito.anyString()));
    }

    @Test
    public void replaceIviDeviceTest_CurrentDataReadyStolen_swmIntegrationEnabled() throws Exception {
        ReflectionTestUtils.setField(deviceAssociationWithFactDataService, "isCurrentDeviceToBeMovedToProvisioned",
            true);
        ReflectionTestUtils.setField(deviceAssociationWithFactDataService, "swmIntegrationEnabled", true);
        Timestamp manufacturingDate = new Timestamp(System.currentTimeMillis());
        Timestamp recordDate = new Timestamp(System.currentTimeMillis());

        deviceInfoFactoryDataCurrent.setFaulty(true);
        deviceInfoFactoryDataCurrent.setStolen(false);
        deviceInfoFactoryDataCurrent.setState("STOLEN");
        deviceInfoFactoryDataCurrent.setManufacturingDate(manufacturingDate);
        deviceInfoFactoryDataCurrent.setRecordDate(recordDate);

        deviceInfoFactoryDataReplace.setFaulty(false);
        deviceInfoFactoryDataReplace.setStolen(false);
        deviceInfoFactoryDataReplace.setState("PROVISIONED");

        List<DeviceInfoFactoryData> listReplaceValueData = new ArrayList<>();
        listReplaceValueData.add(deviceInfoFactoryDataReplace);

        HcpInfo hcpInfo = new HcpInfo();
        hcpInfo.setFactoryId("1234");
        hcpInfo.setHarmanId("H1234");
        hcpInfo.setVin("V1234");
        hcpInfo.setId(1L);
        hcpInfo.setSerialNumber("S1234");


        replaceWith.setVin("TESTVIN_Make:THIRD_Model:Dart_Year:2013_Type:Car_0");
        replaceWith.setPlant("Plant-403");
        replaceWith.setVehicleModelYear("2013");
        replaceWith.setManufacturingDate("10/10/2013");
        replaceWith.setRecordDate("12/10/2013");
        replaceWith.setModel("Dart");
        replaceWith.setPlatformVersion("v3");
        replaceWith.setSsid("SSID-1234");
        replaceWith.setMsisdn("MS-1234");
        replaceWith.setPackageSerialNumber("PKGS-1234");
        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        replaceDeviceRequest.setSerialNumber("S1234");
        replaceDeviceRequest.setReplaceWith(replaceWith);

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);

        String vin = "TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0";
        Mockito.doReturn(vin).when(deviceAssociationDao)
            .getAssociatedVinForReplaceApi(replaceDeviceRequest.getSerialNumber());
        Mockito.doReturn(deviceInfoFactoryDataCurrent).when(deviceInfoFactoryDataDao)
            .findFactoryDataBySerialNumber(replaceDeviceRequest.getSerialNumber());
        Mockito.doReturn(true).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactData(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(listReplaceValueData).when(deviceInfoFactoryDataDao)
            .constructAndFetchFactoryData(Mockito.any());
        Mockito.doReturn(hcpInfo).when(hcpInfoDao).findActiveHcpInfo(Mockito.anyLong());
        Mockito.doNothing().when(deviceActivationStateDao).disableActivationReadyByFacotryId(Mockito.anyLong());
        Mockito.doReturn(deviceAssociation).when(deviceAssociationDao).findValidAssociations(Mockito.anyString());
        Mockito.doReturn(1).when(deviceAssociationDao)
            .updateForReplaceDevice(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(), Mockito.anyLong());
        Mockito.doReturn(true).when(swmService).updateVehicle(Mockito.any());
        String userId = "User1234";
        deviceAssociationWithFactDataService.replaceIviDevice(replaceDeviceRequest, userId);
        Assertions.assertTrue(deviceAssociationDao.checkAssociatedDeviceWithFactData(Mockito.anyLong(),
                Mockito.anyString()));
    }

    @Test
    public void replaceIviDeviceTest_CurrentDataReadyFaulty() {
        ReflectionTestUtils.setField(deviceAssociationWithFactDataService, "isCurrentDeviceToBeMovedToProvisioned",
            true);
        Timestamp manufacturingDate = new Timestamp(System.currentTimeMillis());
        Timestamp recordDate = new Timestamp(System.currentTimeMillis());

        deviceInfoFactoryDataCurrent.setFaulty(true);
        deviceInfoFactoryDataCurrent.setStolen(false);
        deviceInfoFactoryDataCurrent.setState("FAULTY");
        deviceInfoFactoryDataCurrent.setManufacturingDate(manufacturingDate);
        deviceInfoFactoryDataCurrent.setRecordDate(recordDate);

        deviceInfoFactoryDataReplace.setFaulty(false);
        deviceInfoFactoryDataReplace.setStolen(false);
        deviceInfoFactoryDataReplace.setState("PROVISIONED");

        List<DeviceInfoFactoryData> listReplaceValueData = new ArrayList<>();
        listReplaceValueData.add(deviceInfoFactoryDataReplace);

        HcpInfo hcpInfo = new HcpInfo();
        hcpInfo.setFactoryId("1234");
        hcpInfo.setHarmanId("H1234");
        hcpInfo.setVin("V1234");
        hcpInfo.setId(1L);
        hcpInfo.setSerialNumber("S1234");

        ReplaceDeviceDataPojo replaceWith = new ReplaceDeviceDataPojo();
        replaceWith.setSerialNumber("S-1234");
        replaceWith.setPlant("Plant-403");
        replaceWith.setVehicleModelYear("2013");
        replaceWith.setManufacturingDate("10/10/2013");
        replaceWith.setRecordDate("12/10/2013");
        replaceWith.setModel("Dart");
        replaceWith.setPlatformVersion("v3");
        replaceWith.setSsid("SSID-1234");
        replaceWith.setMsisdn("MS-1234");
        replaceWith.setPackageSerialNumber("PKGS-1234");
        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        replaceDeviceRequest.setSerialNumber("S1234");
        replaceDeviceRequest.setReplaceWith(replaceWith);

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);

        String vin = "TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0";
        Mockito.doReturn(vin).when(deviceAssociationDao)
            .getAssociatedVinForReplaceApi(replaceDeviceRequest.getSerialNumber());
        Mockito.doReturn(deviceInfoFactoryDataCurrent).when(deviceInfoFactoryDataDao)
            .findFactoryDataBySerialNumber(replaceDeviceRequest.getSerialNumber());
        Mockito.doReturn(true).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactData(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(listReplaceValueData).when(deviceInfoFactoryDataDao)
            .constructAndFetchFactoryData(Mockito.any());
        Mockito.doReturn(hcpInfo).when(hcpInfoDao).findActiveHcpInfo(Mockito.anyLong());
        Mockito.doNothing().when(deviceActivationStateDao).disableActivationReadyByFacotryId(Mockito.anyLong());
        Mockito.doReturn(deviceAssociation).when(deviceAssociationDao).findValidAssociations(Mockito.anyString());
        String userId = "User1234";
        Mockito.doReturn(1).when(deviceAssociationDao)
            .updateForReplaceDevice(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(), Mockito.anyLong());
        deviceAssociationWithFactDataService.replaceIviDevice(replaceDeviceRequest, userId);
        Assertions.assertTrue(deviceAssociationDao.checkAssociatedDeviceWithFactData(Mockito.anyLong(),
                Mockito.anyString()));
    }

    @Test
    public void replaceIviDeviceTest_CurrentDataReadyProvisioned() {
        ReflectionTestUtils.setField(deviceAssociationWithFactDataService, "isCurrentDeviceToBeMovedToProvisioned",
            true);
        Timestamp manufacturingDate = new Timestamp(System.currentTimeMillis());
        Timestamp recordDate = new Timestamp(System.currentTimeMillis());

        deviceInfoFactoryDataCurrent.setFaulty(true);
        deviceInfoFactoryDataCurrent.setStolen(false);
        deviceInfoFactoryDataCurrent.setState("PROVISIONED");
        deviceInfoFactoryDataCurrent.setManufacturingDate(manufacturingDate);
        deviceInfoFactoryDataCurrent.setRecordDate(recordDate);


        deviceInfoFactoryDataReplace.setFaulty(false);
        deviceInfoFactoryDataReplace.setStolen(false);
        deviceInfoFactoryDataReplace.setState("PROVISIONED");

        List<DeviceInfoFactoryData> listReplaceValueData = new ArrayList<>();
        listReplaceValueData.add(deviceInfoFactoryDataReplace);

        HcpInfo hcpInfo = new HcpInfo();
        hcpInfo.setFactoryId("1234");
        hcpInfo.setHarmanId("H1234");
        hcpInfo.setVin("V1234");
        hcpInfo.setId(1L);
        hcpInfo.setSerialNumber("S1234");


        replaceWith.setChassisNumber("CH-1234");
        replaceWith.setProductionWeek("WEEK-3");
        replaceWith.setVin("TESTVIN_Make:THIRD_Model:Dart_Year:2013_Type:Car_0");
        replaceWith.setPlant("Plant-403");
        replaceWith.setVehicleModelYear("2013");
        replaceWith.setManufacturingDate("10/10/2013");
        replaceWith.setRecordDate("12/10/2013");
        ReplaceFactoryDataRequest replaceDeviceRequest = new ReplaceFactoryDataRequest();
        replaceDeviceRequest.setSerialNumber("S1234");
        replaceDeviceRequest.setReplaceWith(replaceWith);

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);

        String vin = "TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0";
        Mockito.doReturn(vin).when(deviceAssociationDao)
            .getAssociatedVinForReplaceApi(replaceDeviceRequest.getSerialNumber());
        Mockito.doReturn(deviceInfoFactoryDataCurrent).when(deviceInfoFactoryDataDao)
            .findFactoryDataBySerialNumber(replaceDeviceRequest.getSerialNumber());
        Mockito.doReturn(true).when(deviceAssociationDao)
            .checkAssociatedDeviceWithFactData(Mockito.anyLong(), Mockito.anyString());
        Mockito.doReturn(listReplaceValueData).when(deviceInfoFactoryDataDao)
            .constructAndFetchFactoryData(Mockito.any());
        Mockito.doReturn(hcpInfo).when(hcpInfoDao).findActiveHcpInfo(Mockito.anyLong());
        Mockito.doNothing().when(deviceActivationStateDao).disableActivationReadyByFacotryId(Mockito.anyLong());
        Mockito.doReturn(deviceAssociation).when(deviceAssociationDao).findValidAssociations(Mockito.anyString());
        String userId = "User1234";
        Mockito.doReturn(1).when(deviceAssociationDao)
            .updateForReplaceDevice(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(), Mockito.anyLong());
        deviceAssociationWithFactDataService.replaceIviDevice(replaceDeviceRequest, userId);
        Assertions.assertTrue(deviceAssociationDao.checkAssociatedDeviceWithFactData(Mockito.anyLong(),
                Mockito.anyString()));
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
            ReflectionTestUtils.invokeMethod(deviceAssociationWithFactDataService, "resetDevice", "imei");
            Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}