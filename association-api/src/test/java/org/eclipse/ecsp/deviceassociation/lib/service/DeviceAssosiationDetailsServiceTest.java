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

import org.eclipse.ecsp.deviceassociation.lib.dao.DeviceAssociationDao;
import org.eclipse.ecsp.deviceassociation.lib.exception.NoSuchEntityException;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociationDetailsResponse;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceAssosiationDetails;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceDetail;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for DeviceAssosiationDetailsService.
 */
public class DeviceAssosiationDetailsServiceTest {

    private static final long ID = 99L;

    @InjectMocks
    DeviceAssosiationDetailsService deviceAssosiationDetailsService;

    @Mock
    DeviceAssociationDao deviceAssociationDao;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void getDeviceAssosiationDetails_NullUserId() {
        assertThrows(NoSuchEntityException.class,
            () -> deviceAssosiationDetailsService.getDeviceAssosiationDetails(null));
    }

    @Test
    public void getDeviceAssosiationDetails_NullDetailsList() throws NoSuchEntityException {
        String userId = "User123";
        Mockito.doReturn(null).when(deviceAssociationDao).fetchAssociationDetails(Mockito.any(), Mockito.anyBoolean());
        List<DeviceAssosiationDetails> deviceAssosiationDetails =
                deviceAssosiationDetailsService.getDeviceAssosiationDetails(userId);
        Assertions.assertNotNull(deviceAssosiationDetails);
    }

    @Test
    public void getDeviceAssosiationDetails_EmptyDetailsList() throws NoSuchEntityException {
        String userId = "User123";
        List<AssociationDetailsResponse> detailsList = new ArrayList<>();
        detailsList.add(null);
        Mockito.doReturn(detailsList).when(deviceAssociationDao)
            .fetchAssociationDetails(Mockito.any(), Mockito.anyBoolean());
        List<DeviceAssosiationDetails> deviceAssosiationDetails =
                deviceAssosiationDetailsService.getDeviceAssosiationDetails(userId);
        Assertions.assertNotNull(deviceAssosiationDetails);
    }

    @Test
    public void getDeviceAssosiationDetails_NullDeviceDetail() throws NoSuchEntityException {
        AssociationDetailsResponse associationDetailsResponse = new AssociationDetailsResponse();
        associationDetailsResponse.setId(ID);
        associationDetailsResponse.setUserId("User123");
        associationDetailsResponse.setSerialNumber("S1234");
        List<AssociationDetailsResponse> detailsList = new ArrayList<>();
        detailsList.add(associationDetailsResponse);
        Mockito.doReturn(detailsList).when(deviceAssociationDao)
            .fetchAssociationDetails(Mockito.any(), Mockito.anyBoolean());
        String userId = "User123";
        List<DeviceAssosiationDetails> deviceAssosiationDetails =
                deviceAssosiationDetailsService.getDeviceAssosiationDetails(userId);
        Assertions.assertNotNull(deviceAssosiationDetails);
    }

    @Test
    public void getDeviceAssosiationDetails_ValidData() throws NoSuchEntityException {

        DeviceDetail deviceDetail = new DeviceDetail();
        deviceDetail.setImei("I1234");
        deviceDetail.setHarmanId("H1234");

        AssociationDetailsResponse associationDetailsResponse = new AssociationDetailsResponse();
        associationDetailsResponse.setId(ID);
        associationDetailsResponse.setUserId("User123");
        associationDetailsResponse.setSerialNumber("S1234");
        associationDetailsResponse.setDeviceDetail(deviceDetail);
        List<AssociationDetailsResponse> detailsList = new ArrayList<>();
        detailsList.add(associationDetailsResponse);
        Mockito.doReturn(detailsList).when(deviceAssociationDao)
            .fetchAssociationDetails(Mockito.any(), Mockito.anyBoolean());
        String userId = "User123";
        List<DeviceAssosiationDetails> deviceAssosiationDetails =
                deviceAssosiationDetailsService.getDeviceAssosiationDetails(userId);
        Assertions.assertNotNull(deviceAssosiationDetails);
    }
}