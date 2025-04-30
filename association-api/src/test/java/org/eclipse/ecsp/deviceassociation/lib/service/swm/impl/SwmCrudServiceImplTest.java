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

package org.eclipse.ecsp.deviceassociation.lib.service.swm.impl;

import org.eclipse.ecsp.common.config.EnvConfig;
import org.eclipse.ecsp.deviceassociation.lib.config.DeviceAssocationProperty;
import org.eclipse.ecsp.deviceassociation.lib.model.swm.SwmUpdateVehicleRequest;
import org.eclipse.ecsp.deviceassociation.lib.model.swm.SwmVehiclesRequest;
import org.eclipse.ecsp.deviceassociation.lib.model.swm.SwmVinRequest;
import org.eclipse.ecsp.deviceassociation.lib.model.swm.VehiclePost;
import org.eclipse.ecsp.exception.shared.ApiTechnicalException;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for SwmCrudServiceImpl.
 */
public class SwmCrudServiceImplTest {

    @InjectMocks
    SwmCrudServiceImpl swmCrudServiceImpl;

    @Mock
    RestTemplate restTemplate;

    @Mock
    EnvConfig<DeviceAssocationProperty> envConfig;


    SwmVehiclesRequest swmRequest;

    /**
     * run before each test.
     */
    @Before
    public void beforeEach() {
        initMocks(this);
        VehiclePost vehicleData = new VehiclePost();
        vehicleData.setChassisNumber("132394293457");
        vehicleData.setDomainId("VNLKS912U");
        vehicleData.setPlant("PLANT");
        vehicleData.setProductionWeek("2019/01/01");
        vehicleData.setVehicleModelId("MX127777");
        vehicleData.setVin("JN1TAAT32A0XXXXYY");
        vehicleData.setFriendlyName("JhonCena");
        Map<String, String> specificAttributes = new HashMap<>();
        specificAttributes.put("vehicleModelYear", "2019/01/01");
        vehicleData.setSpecificAttributes(specificAttributes);
        List<VehiclePost> vehiclePostList = new ArrayList<>();
        vehiclePostList.add(vehicleData);
        swmRequest = new SwmVehiclesRequest();
        swmRequest.setVehiclePost(vehiclePostList);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void createVehicleTest() {
        ReflectionTestUtils.setField(swmCrudServiceImpl, "swmUserName", "user123");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "swmPassword", "password");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "swmDomain", "domain1");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "loginUrl", "https://loginUrl/");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "vehiclesUrl", "https://vehiclesUrl/");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "createVehicleUrl", "https://createVehicleUrl/");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "vehicleModelsUrl", "https://vehicleModelsUrl/");

        String response = "{\"sessionId\": \"12345\"}";

        ResponseEntity<String> sessionIdResp = ResponseEntity.ok().body(response);

        String jsonString = "{\"id\":\"46\",\"actionResult\":[{\"code\":0,\"priority\":0.0}]} ";

        ResponseEntity<String> createVechicleResp = ResponseEntity.ok().body(jsonString);
        Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.eq(String.class)))
            .thenReturn(sessionIdResp, createVechicleResp);

        boolean vehicleStatus = swmCrudServiceImpl.createVehicle(swmRequest);
        Assertions.assertTrue(vehicleStatus);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = ApiTechnicalException.class)
    public void createVehicleTestWithBadRequest() {

        ReflectionTestUtils.setField(swmCrudServiceImpl, "swmUserName", "user123");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "swmPassword", "password");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "swmDomain", "domain1");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "loginUrl", "https://loginUrl/");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "vehiclesUrl", "https://vehiclesUrl/");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "createVehicleUrl", "https://createVehicleUrl/");

        String response = "{\"sessionId\": \"12345\"}";

        ResponseEntity<String> sessionIdResp = new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

        String jsonString = "{\"id\":\"46\",\"actionResult\":[{\"code\":0,\"priority\":0.0}]} ";

        ResponseEntity<String> createVechicleResp = ResponseEntity.ok().body(jsonString);
        Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.eq(String.class)))
            .thenReturn(sessionIdResp, createVechicleResp);

        boolean vehicleStatus = swmCrudServiceImpl.createVehicle(swmRequest);
        Assertions.assertTrue(vehicleStatus);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void createVehicleTestWithVehicleModelId() {

        ReflectionTestUtils.setField(swmCrudServiceImpl, "swmUserName", "user123");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "swmPassword", "password");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "swmDomain", "domain1");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "loginUrl", "https://loginUrl/");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "vehicleModelsUrl", "https://vehicleModelsUrl/");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "createVehicleUrl", "https://createVehicleUrl/");

        String vehicleModelsDtoString = "{\"representationObjects\":[{\"modelCode\":\"MX127777\"}]}";
        ResponseEntity<String> vehicleModelsDtoResp = ResponseEntity.ok().body(vehicleModelsDtoString);

        String response = "{\"sessionId\": \"12345\"}";

        ResponseEntity<String> sessionIdResp = ResponseEntity.ok().body(response);

        String jsonString = "{\"id\":\"46\",\"actionResult\":[{\"code\":0,\"priority\":0.0}]} ";

        ResponseEntity<String> createVechicleResp = ResponseEntity.ok().body(jsonString);
        Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.eq(String.class)))
            .thenReturn(sessionIdResp, vehicleModelsDtoResp, createVechicleResp);

        boolean vehicleStatus = swmCrudServiceImpl.createVehicle(swmRequest);
        Assertions.assertTrue(vehicleStatus);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void createVehicleTestWithInvalidCode() {

        ReflectionTestUtils.setField(swmCrudServiceImpl, "swmUserName", "user123");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "swmPassword", "password");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "swmDomain", "domain1");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "loginUrl", "https://loginUrl/");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "vehiclesUrl", "https://vehiclesUrl/");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "createVehicleUrl", "https://createVehicleUrl/");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "vehicleModelsUrl", "https://vehicleModelsUrl/");

        String response = "{\"sessionId\": \"12345\"}";

        ResponseEntity<String> sessionIdResp = ResponseEntity.ok().body(response);

        String jsonString =
            "{\"id\":\"46\",\"actionResult\":[{\"code\":1,\"priority\":0.0,\"reasonMessage\":{\"parameters\":[],"
                + "\"localizedMessage\":\"Vehicle already exists\"}}]}";

        ResponseEntity<String> createVechicleResp = ResponseEntity.ok().body(jsonString);
        Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.eq(String.class)))
            .thenReturn(sessionIdResp, createVechicleResp);

        boolean vehicleStatus = swmCrudServiceImpl.createVehicle(swmRequest);
        Assertions.assertTrue(vehicleStatus);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void createVehicleTestWithInvalidCode1() {

        ReflectionTestUtils.setField(swmCrudServiceImpl, "swmUserName", "user123");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "swmPassword", "password");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "swmDomain", "domain1");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "loginUrl", "https://loginUrl/");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "vehiclesUrl", "https://vehiclesUrl/");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "createVehicleUrl", "https://createVehicleUrl/");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "vehicleModelsUrl", "https://vehicleModelsUrl/");

        String response = "{\"sessionId\": \"12345\"}";

        ResponseEntity<String> sessionIdResp = ResponseEntity.ok().body(response);

        String jsonString =
            "{\"id\":\"46\",\"actionResult\":[{\"code\":1,\"priority\":0.0,\"resultMessage\":{\"parameters\":[],"
                + "\"localizedMessage\":\"Vehicle already exists\"}}]}";

        ResponseEntity<String> createVechicleResp = ResponseEntity.ok().body(jsonString);
        Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.eq(String.class)))
            .thenReturn(sessionIdResp, createVechicleResp);

        boolean vehicleStatus = swmCrudServiceImpl.createVehicle(swmRequest);
        Assertions.assertTrue(vehicleStatus);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = ApiTechnicalException.class)
    public void createVehicleTestWithParseException() {

        ReflectionTestUtils.setField(swmCrudServiceImpl, "loginUrl", "https://loginUrl/");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "createVehicleUrl", "https://createVehicleUrl/");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "vehicleModelsUrl", "https://vehicleModelsUrl/");

        String response = "{\"sessionId\": \"12345\"}";

        ResponseEntity<String> sessionIdResp = ResponseEntity.ok().body(response);

        String jsonString =
            "{\"id\":\"46\",\"actionResult\":[{\"code\":1,\"priority\":0.0,\"resultMessage\":{\"parameters\":"
                + "[]Vehicle already exists\"}}]}";

        ResponseEntity<String> createVechicleResp = ResponseEntity.ok().body(jsonString);
        Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.eq(String.class)))
            .thenReturn(sessionIdResp, createVechicleResp);

        boolean vehicleStatus = swmCrudServiceImpl.createVehicle(swmRequest);
        Assertions.assertTrue(vehicleStatus);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void updateVehicleTest() {

        ReflectionTestUtils.setField(swmCrudServiceImpl, "swmUserName", "user123");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "swmPassword", "password");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "swmDomain", "domain1");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "loginUrl", "https://loginUrl/");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "vehiclesUrl", "https://vehiclesUrl/");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "updateVehicleUrl", "https://updateVehicleUrl/");

        SwmUpdateVehicleRequest swmUpdateVehicleRequest = new SwmUpdateVehicleRequest();
        swmUpdateVehicleRequest.setChassisNumber("chassis123");
        swmUpdateVehicleRequest.setProductionWeek("2019/01/01");
        swmUpdateVehicleRequest.setPlant("PLANT123");
        swmUpdateVehicleRequest.setVin("VIN:12345677888888766555423322safGGh");
        Map<String, String> specificAttributesMap = new HashMap<>();
        specificAttributesMap.put("vehicleModelYear", "2019/01/01");
        swmUpdateVehicleRequest.setSpecificAttributes(specificAttributesMap);

        String response = "{\"representationObjects\":[{\"id\":\"12\"}]}";
        ResponseEntity<String> responseEntity = ResponseEntity.ok().body(response);

        String response2 = "{\"sessionId\": \"12345\"}";
        ResponseEntity<String> responseEntity2 = ResponseEntity.ok().body(response2);

        Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.eq(String.class)))
            .thenReturn(responseEntity2, responseEntity, responseEntity2);
        boolean updateStatus = swmCrudServiceImpl.updateVehicle(swmUpdateVehicleRequest);
        Assertions.assertTrue(updateStatus);
    }

    @Test(expected = ApiTechnicalException.class)
    public void updateVehicleTest_throwException() {

        ReflectionTestUtils.setField(swmCrudServiceImpl, "loginUrl", "https://loginUrl/");

        SwmUpdateVehicleRequest swmUpdateVehicleRequest = new SwmUpdateVehicleRequest();
        swmUpdateVehicleRequest.setChassisNumber("chassis123");
        swmUpdateVehicleRequest.setProductionWeek("2019/01/01");
        swmUpdateVehicleRequest.setPlant("PLANT123");
        swmUpdateVehicleRequest.setVin("VIN:12345677888888766555423322safGGh");
        Map<String, String> specificAttributesMap = new HashMap<>();
        specificAttributesMap.put("vehicleModelYear", "2019/01/01");
        swmUpdateVehicleRequest.setSpecificAttributes(specificAttributesMap);

        String response2 = "{\"sessionId\": }";
        ResponseEntity<String> responseEntity2 = ResponseEntity.ok().body(response2);

        Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.eq(String.class)))
            .thenReturn(responseEntity2);
        swmCrudServiceImpl.updateVehicle(swmUpdateVehicleRequest);
    }


    @SuppressWarnings("unchecked")
    @Test(expected = ApiTechnicalException.class)
    public void createVehicleTestWithApiTechnicalException() {

        ReflectionTestUtils.setField(swmCrudServiceImpl, "createVehicleUrl", "https://createVehicleUrl/");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "loginUrl", "https://loginUrl/");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "vehicleModelsUrl", "https://vehicleModelsUrl/");

        String response = "{\"sessionId\": \"12345\"}";

        ResponseEntity<String> sessionIdResp = ResponseEntity.ok().body(response);

        String jsonString =
            "{\"id\":\"46\",\"actionResult\":[{\"code\":1,\"priority\":0.0,\"resultMessage\":{\"parameters\":[],"
                + "\"localizedMessage\":\" \"}}]}";

        ResponseEntity<String> createVechicleResp = ResponseEntity.ok().body(jsonString);
        Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.eq(String.class)))
            .thenReturn(sessionIdResp, createVechicleResp);

        swmCrudServiceImpl.createVehicle(swmRequest);

    }

    @SuppressWarnings("unchecked")
    @Test
    public void deleteVehicle() {

        ReflectionTestUtils.setField(swmCrudServiceImpl, "swmUserName", "user123");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "swmPassword", "password");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "swmDomain", "domain1");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "loginUrl", "https://loginUrl/");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "vehiclesUrl", "https://vehiclesUrl/");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "deleteVehicleUrl", "https://deleteVehicleUrl/");

        SwmVinRequest request = new SwmVinRequest();
        request.setVin("JN1TAAT32A0XXXXYY");
        String response = "{\"sessionId\": \"12345\"}";
        ResponseEntity<String> sessionIdResp = ResponseEntity.ok().body(response);

        String deleteAndUpdateResp = "{\"representationObjects\":[{\"id\":\"12\"}]}";
        ResponseEntity<String> deleteAndUpdateEntityResp = ResponseEntity.ok().body(deleteAndUpdateResp);
        ResponseEntity<String> deleteResponse = ResponseEntity.ok().body("success");

        Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.eq(String.class)))
            .thenReturn(sessionIdResp, deleteAndUpdateEntityResp, sessionIdResp, deleteResponse);
        boolean deleteStatus = swmCrudServiceImpl.deleteVehicle(request);
        Assertions.assertTrue(deleteStatus);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void deleteVehicleWithNullId() {

        ReflectionTestUtils.setField(swmCrudServiceImpl, "swmUserName", "user123");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "swmPassword", "password");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "swmDomain", "domain1");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "loginUrl", "https://loginUrl/");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "vehiclesUrl", "https://vehiclesUrl/");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "deleteVehicleUrl", "https://deleteVehicleUrl/");

        SwmVinRequest request = new SwmVinRequest();
        request.setVin("JN1TAAT32A0XXXXYY");
        String response = "{\"sessionId\": \"12345\"}";
        ResponseEntity<String> sessionIdResp = ResponseEntity.ok().body(response);

        String deleteAndUpdateResp = "{\"representationObjects\":[{\"ID\":\"12\"}]}";
        ResponseEntity<String> deleteAndUpdateEntityResp = ResponseEntity.ok().body(deleteAndUpdateResp);
        ResponseEntity<String> deleteResponse = ResponseEntity.ok().body("success");

        Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.eq(String.class)))
            .thenReturn(sessionIdResp, deleteAndUpdateEntityResp, sessionIdResp, deleteResponse);
        boolean deleteStatus = swmCrudServiceImpl.deleteVehicle(request);
        Assertions.assertFalse(deleteStatus);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = ApiTechnicalException.class)
    public void deleteVehicleWithException() {

        SwmVinRequest request = new SwmVinRequest();
        request.setVin("JN1TAAT32A0XXXXYY");
        String response = "{\"sessionId\": \"12345\"}";
        ResponseEntity<String> sessionIdResp = ResponseEntity.ok().body(response);

        String deleteAndUpdateResp = "{\"representationObjects\":[{\"ID\":\"12\"]}";
        ResponseEntity<String> deleteAndUpdateEntityResp = ResponseEntity.ok().body(deleteAndUpdateResp);
        ResponseEntity<String> deleteResponse = ResponseEntity.ok().body("success");

        Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.eq(String.class)))
            .thenReturn(sessionIdResp, deleteAndUpdateEntityResp, sessionIdResp, deleteResponse);
        swmCrudServiceImpl.deleteVehicle(request);
    }

    @Test(expected = ApiTechnicalException.class)
    public void deleteVehicleThrowException() {

        ReflectionTestUtils.setField(swmCrudServiceImpl, "swmUserName", "user123");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "swmPassword", "password");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "swmDomain", "domain1");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "loginUrl", "https://loginUrl/");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "vehiclesUrl", "https://vehiclesUrl/");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "deleteVehicleUrl", "https://deleteVehicleUrl/");

        SwmVinRequest request = new SwmVinRequest();
        request.setVin("JN1TAAT32A0XXXXYY");

        Mockito.doThrow(RestClientException.class).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.eq(String.class));
        swmCrudServiceImpl.deleteVehicle(request);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void deleteVehicleWithBadResponse() {

        ReflectionTestUtils.setField(swmCrudServiceImpl, "swmUserName", "user123");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "swmPassword", "password");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "swmDomain", "domain1");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "loginUrl", "https://loginUrl/");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "vehiclesUrl", "https://vehiclesUrl/");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "deleteVehicleUrl", "https://deleteVehicleUrl/");

        SwmVinRequest request = new SwmVinRequest();
        request.setVin("JN1TAAT32A0XXXXYY");
        String response = "{\"sessionId\": \"12345\"}";
        ResponseEntity<String> sessionIdResp = ResponseEntity.ok().body(response);

        String deleteAndUpdateResp = "{\"representationObjects\":[{\"id\":\"12\"}]}";
        ResponseEntity<String> deleteAndUpdateEntityResp = ResponseEntity.ok().body(deleteAndUpdateResp);
        ResponseEntity<String> deleteResponse = ResponseEntity.badRequest().body("FAILED");


        Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.eq(String.class)))
            .thenReturn(sessionIdResp, deleteAndUpdateEntityResp, sessionIdResp, deleteResponse);
        boolean deleteStatus = swmCrudServiceImpl.deleteVehicle(request);
        Assertions.assertFalse(deleteStatus);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void deleteVehicleWithBadRequest() {

        ReflectionTestUtils.setField(swmCrudServiceImpl, "swmUserName", "user123");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "swmPassword", "password");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "swmDomain", "domain1");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "loginUrl", "https://loginUrl/");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "vehiclesUrl", "https://vehiclesUrl/");
        ReflectionTestUtils.setField(swmCrudServiceImpl, "deleteVehicleUrl", "https://deleteVehicleUrl/");

        SwmVinRequest request = new SwmVinRequest();
        request.setVin("JN1TAAT32A0XXXXYY");
        String response = "{\"sessionId\": \"12345\"}";
        ResponseEntity<String> sessionIdResp = ResponseEntity.ok().body(response);

        String deleteAndUpdateResp = "{\"representationObjects\":[{\"id\":\"12\"}]}";
        ResponseEntity<String> deleteAndUpdateEntityResp =
            new ResponseEntity<>(deleteAndUpdateResp, HttpStatus.BAD_REQUEST);
        ResponseEntity<String> deleteResponse = ResponseEntity.badRequest().body("FAILED");


        Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.eq(String.class)))
            .thenReturn(sessionIdResp, deleteAndUpdateEntityResp, sessionIdResp, deleteResponse);
        boolean deleteStatus = swmCrudServiceImpl.deleteVehicle(request);
        Assertions.assertFalse(deleteStatus);
    }

    @Test
    public void populateDataTestFalse() throws Exception {

        Mockito.when(envConfig.getStringValue(DeviceAssocationProperty.SWM_PASSWORD))
            .thenReturn("swmPassword", "baseUrl", "loginApi", "updateApi", "deleteApi", "vehicleModelsApi",
                "vehiclesApi", "vehicleModelId", "swmUserName", "swmDomain");
        Mockito.doReturn(false).when(envConfig).getBooleanValue(DeviceAssocationProperty.SECRET_VAULT_ENABLE_FLG);
        swmCrudServiceImpl.populateData();
        Assertions.assertFalse(envConfig.getBooleanValue(DeviceAssocationProperty.SECRET_VAULT_ENABLE_FLG));
    }
}

