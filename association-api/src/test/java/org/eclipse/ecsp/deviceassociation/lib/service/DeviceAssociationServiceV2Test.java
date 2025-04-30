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
import org.eclipse.ecsp.deviceassociation.lib.config.DeviceAssocationProperty;
import org.eclipse.ecsp.deviceassociation.lib.dao.DeviceAssociationDao;
import org.eclipse.ecsp.deviceassociation.lib.exception.DuplicateDeviceAssociationRequestException;
import org.eclipse.ecsp.deviceassociation.lib.model.AssociationStatus;
import org.eclipse.ecsp.deviceassociation.lib.model.DeviceAssociation;
import org.eclipse.ecsp.deviceassociation.lib.observer.DeviceAssociationObservable;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociateDeviceRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociateDeviceResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * DeviceAssociationServiceV2 class.
 */
public class DeviceAssociationServiceV2Test {

    @Mock
    protected EnvConfig<DeviceAssocationProperty> envConfig;
    @InjectMocks
    DeviceAssociationServiceV2 deviceAssociationServiceV2;
    @Mock
    DeviceAssociationObservable observable;
    @Mock
    DeviceAssociationDao deviceAssociationDao;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void associateDeviceTest_DuplicateDeviceAssociationRequestException() {
        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setUserId("User123");
        associateDeviceRequest.setSerialNumber("S1234");
        Mockito.doReturn(true).when(deviceAssociationDao)
            .isDeviceCurrentlyAssociatedToUser(Mockito.anyString(), Mockito.anyString());
        assertThrows(DuplicateDeviceAssociationRequestException.class,
            () -> deviceAssociationServiceV2.associateDevice(associateDeviceRequest));
    }

    @Test
    public void associateDeviceTest() throws Exception {
        AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
        associateDeviceRequest.setUserId("User123");
        associateDeviceRequest.setSerialNumber("S1234");

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setUserId("User1");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);

        Mockito.doReturn(false).when(deviceAssociationDao)
            .isDeviceCurrentlyAssociatedToUser(Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn(deviceAssociation).when(deviceAssociationDao).findValidAssociations(Mockito.anyString());
        Mockito.doNothing().when(observable).notify(deviceAssociation);
        AssociateDeviceResponse associateDeviceResponse =
                deviceAssociationServiceV2.associateDevice(associateDeviceRequest);
        assertNotNull(associateDeviceResponse);
    }

    @Test
    public void getAssociatedDevicesForUserTest() {
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setUserId("User1");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setImei("I1234");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        List<DeviceAssociation> deviceAssociations = new ArrayList<>();
        deviceAssociations.add(deviceAssociation);
        Mockito.doReturn(deviceAssociations).when(deviceAssociationDao).fetchAssociatedDevices(Mockito.anyString());
        String userId = "User123";
        List<DeviceAssociation> deviceAssociationList = deviceAssociationServiceV2.getAssociatedDevicesForUser(userId);
        assertNotNull(deviceAssociationList);
    }

    @Test
    public void getAssociationDetailsTest_NullDeviceAssociation() {
        long associationId = 1L;
        String userId = "User123";
        Mockito.doReturn(null).when(deviceAssociationDao).find(Mockito.anyLong(), Mockito.anyString());
        DeviceAssociation deviceAssociation = deviceAssociationServiceV2.getAssociationDetails(associationId, userId);
        Assertions.assertNull(deviceAssociation);
    }

    @Test
    public void getAssociationDetailsTest_NullHarmanId() {
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setUserId("User1");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setImei("I1234");
        deviceAssociation.setHarmanId(null);
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        Mockito.doReturn(deviceAssociation).when(deviceAssociationDao).find(Mockito.anyLong(), Mockito.anyString());
        long associationId = 1L;
        String userId = "User123";
        DeviceAssociation deviceAssociations = deviceAssociationServiceV2.getAssociationDetails(associationId, userId);
        Assertions.assertNull(deviceAssociations.getHarmanId());
    }

    @Test
    public void getAssociationDetailsTest() {
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setUserId("User1");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setImei("I1234");
        deviceAssociation.setHarmanId("H1234");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);

        Mockito.doReturn("baseURL").when(envConfig)
            .getStringValue(DeviceAssocationProperty.SERVICE_DEVICEINFO_REST_URL_BASE);
        Mockito.doReturn("deviceInfoPath").when(envConfig)
            .getStringValue(DeviceAssocationProperty.SERVICE_DEVICEINFO_REST_DEVICE_INFO);
        Mockito.doReturn(deviceAssociation).when(deviceAssociationDao).find(Mockito.anyLong(), Mockito.anyString());
        long associationId = 1L;
        String userId = "User123";
        DeviceAssociation deviceAssociations = deviceAssociationServiceV2.getAssociationDetails(associationId, userId);
        Assertions.assertNotNull(deviceAssociations);
    }
}