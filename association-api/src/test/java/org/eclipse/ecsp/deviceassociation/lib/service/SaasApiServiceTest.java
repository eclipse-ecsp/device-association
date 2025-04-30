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

import org.eclipse.ecsp.deviceassociation.dto.Models;
import org.eclipse.ecsp.deviceassociation.dto.ModelsInfo;
import org.eclipse.ecsp.deviceassociation.dto.WhiteListedModel;
import org.eclipse.ecsp.deviceassociation.dto.WhiteListedModels;
import org.eclipse.ecsp.exception.shared.ApiTechnicalException;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for SaasApiService.
 */
public class SaasApiServiceTest {

    @InjectMocks
    SaasApiService saasApiService;

    @Mock
    private RestTemplate restTemplate;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void getStaticModelDetailsFromSystemParameterTest() {
        ModelsInfo modelsInfo = new ModelsInfo();
        modelsInfo.setModelName("XTRAIL");
        modelsInfo.setModelCode("T32");
        modelsInfo.setDongleType("dongle");
        List<ModelsInfo> modelsInfoList = new ArrayList<>();
        modelsInfoList.add(modelsInfo);
        Map<String, List<ModelsInfo>> modelsmap = new HashMap<>();
        modelsmap.put("WhiteListedModels", modelsInfoList);
        Models models = new Models();
        models.setModels(modelsmap);
        ResponseEntity<Models> response = new ResponseEntity<>(models, HttpStatus.OK);
        Mockito.doReturn(response).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), (Class) Mockito.any());
        String countryName = "Thailand";
        List<ModelsInfo> modelsInfos = saasApiService.getStaticModelDetailsFromSystemParameter(countryName);
        Assertions.assertNull(modelsInfos);
    }

    @Test
    public void getStaticModelDetailsFromSystemParameterTest_NullMap() {
        ModelsInfo modelsInfo = new ModelsInfo();
        modelsInfo.setModelName("XTRAIL");
        modelsInfo.setModelCode("T32");
        modelsInfo.setDongleType("dongle");
        Models models = new Models();
        ResponseEntity<Models> response = new ResponseEntity<>(models, HttpStatus.OK);
        Mockito.doReturn(response).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), (Class) Mockito.any());
        String countryName = "Thailand";
        List<ModelsInfo> modelsInfos = saasApiService.getStaticModelDetailsFromSystemParameter(countryName);
        Assertions.assertNotNull(modelsInfos);
    }

    @Test
    public void getWhiteListedModelsTest() {
        WhiteListedModel whiteListedModel = new WhiteListedModel();
        whiteListedModel.setCountryCode("TH");
        whiteListedModel.setModelCode("T32");
        whiteListedModel.setModelName("XTRAIL");
        List<WhiteListedModel> whiteListedModelList = new ArrayList<>();
        whiteListedModelList.add(whiteListedModel);
        WhiteListedModels whiteListedModels = new WhiteListedModels();
        whiteListedModels.setWhiteListedModels(whiteListedModelList);
        ResponseEntity<WhiteListedModels> response = new ResponseEntity<>(whiteListedModels, HttpStatus.OK);
        Mockito.doReturn(response).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), (Class) Mockito.any());
        saasApiService.getWhiteListedModels();
        Assertions.assertNotNull(response);
    }

    @Test
    public void getWhiteListedModelsTest_ApiTechnicalException() {
        Mockito.doThrow(ApiTechnicalException.class).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), (Class) Mockito.any());
        assertThrows(ApiTechnicalException.class, () -> saasApiService.getWhiteListedModels());
    }
}