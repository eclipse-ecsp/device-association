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

import org.eclipse.ecsp.deviceassociation.dto.AssociationTypeModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for DeviceAssociationTypeService.
 */
public class DeviceAssociationTypeServiceTest {

    @InjectMocks
    DeviceAssociationTypeService deviceAssociationTypeService;

    @Mock
    private RestTemplate restTemplate;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void isAssocTypeExistTest_NullResponse() {
        String associationTypeName = "driver";
        Mockito.doReturn(null).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), (Class<AssociationTypeModel>) Mockito.any());
        deviceAssociationTypeService.isAssocTypeExist(associationTypeName);
        Assertions.assertFalse(deviceAssociationTypeService.isAssocTypeExist(associationTypeName));
    }

    @Test
    public void isAssocTypeExistTest_ValidResponse() {
        AssociationTypeModel.AssocType assocType = new AssociationTypeModel.AssocType();
        assocType.setAssociationType("driver");
        List<AssociationTypeModel.AssocType> assocTypeList = new ArrayList<>();
        assocTypeList.add(assocType);
        AssociationTypeModel.Data data = new AssociationTypeModel.Data();
        data.setAssocTypeList(assocTypeList);
        List<AssociationTypeModel.Data> dataList = new ArrayList<>();
        dataList.add(data);
        AssociationTypeModel associationTypeModel = new AssociationTypeModel();
        associationTypeModel.setData(dataList);
        ResponseEntity<AssociationTypeModel> response = new ResponseEntity<>(associationTypeModel, HttpStatus.OK);
        Mockito.doReturn(response).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), (Class<AssociationTypeModel>) Mockito.any());
        String associationTypeName = "driver";
        deviceAssociationTypeService.isAssocTypeExist(associationTypeName);
        Assertions.assertTrue(deviceAssociationTypeService.isAssocTypeExist(associationTypeName));
    }

    @Test
    public void isAssocTypeExistTest_InvalidResponse() {
        AssociationTypeModel.Data data = new AssociationTypeModel.Data();
        data.setAssocTypeList(null);
        List<AssociationTypeModel.Data> dataList = new ArrayList<>();
        dataList.add(data);
        AssociationTypeModel associationTypeModel = new AssociationTypeModel();
        associationTypeModel.setData(dataList);
        ResponseEntity<AssociationTypeModel> response =
            new ResponseEntity<>(associationTypeModel, HttpStatus.BAD_REQUEST);
        Mockito.doReturn(response).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), (Class<AssociationTypeModel>) Mockito.any());
        String associationTypeName = "driver";
        deviceAssociationTypeService.isAssocTypeExist(associationTypeName);
        Assertions.assertFalse(deviceAssociationTypeService.isAssocTypeExist(associationTypeName));
    }
}