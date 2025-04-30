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

package org.eclipse.ecsp.deviceassociation.lib.observer;

import org.eclipse.ecsp.common.config.EnvConfig;
import org.eclipse.ecsp.deviceassociation.lib.config.DeviceAssocationProperty;
import org.eclipse.ecsp.deviceassociation.lib.exception.ObserverMessageProcessFailureException;
import org.eclipse.ecsp.deviceassociation.lib.model.AssociationStatus;
import org.eclipse.ecsp.deviceassociation.lib.model.DeviceAssociation;
import org.eclipse.ecsp.services.clientlib.HcpRestClientLibrary;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for DeviceAuthAssociationStateObserver.
 */
public class DeviceAuthAssociationStateObserverTest {

    private static final long FACTORY_ID = 2L;

    @InjectMocks
    DeviceAuthAssociationStateObserver deviceAuthAssociationStateObserver;

    @Mock
    EnvConfig envConfig;

    @Mock
    HcpRestClientLibrary hcpRestClientLibrary;

    @Mock
    DeviceAssociationObservable observable;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void stateChangedTest_NullDeviceAssociation() throws ObserverMessageProcessFailureException {
        Mockito.doNothing().when(observable).register(Mockito.any());
        deviceAuthAssociationStateObserver.init();
        deviceAuthAssociationStateObserver.stateChanged(null);
        assertNotNull(observable);
    }

    @Test
    public void stateChangedTest_NullFactoryId() {
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(1L);
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setHarmanId("H1234");
        deviceAssociation.setVehicleId("V1234");
        deviceAssociation.setDeviceAuthV2Deactivate(true);
        assertThrows(ObserverMessageProcessFailureException.class,
            () -> deviceAuthAssociationStateObserver.stateChanged(deviceAssociation));
    }

    @Test
    public void stateChangedTest_deactivateV2Success() throws ObserverMessageProcessFailureException {
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(1L);
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setHarmanId("H1234");
        deviceAssociation.setVehicleId("V1234");
        deviceAssociation.setFactoryId(FACTORY_ID);
        deviceAssociation.setDeviceAuthV2Deactivate(true);

        String baseUrl = "http://docker_host:8080/hcp-auth-webapp/";
        String deactivateUrl = "/v2/device/deactivate";

        ResponseEntity<Object> responseEntity = new ResponseEntity<>(HttpStatus.OK);

        Mockito.doReturn(baseUrl).when(envConfig).getStringValue(DeviceAssocationProperty.SERVICE_AUTH_REST_URL_BASE);
        Mockito.doReturn(deactivateUrl).when(envConfig)
            .getStringValue(DeviceAssocationProperty.SERVICE_AUTH_REST_DEACTIVATE_V2_DEVICE);
        Mockito.doReturn(responseEntity).when(hcpRestClientLibrary)
            .doPost(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any());
        deviceAuthAssociationStateObserver.stateChanged(deviceAssociation);
        assertEquals(responseEntity, hcpRestClientLibrary.doPost(Mockito.anyString(),
                Mockito.any(), Mockito.any(), Mockito.any()));
    }

    @Test
    public void stateChangedTest_deactivateV2Failed() {
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(1L);
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setHarmanId("H1234");
        deviceAssociation.setVehicleId("V1234");
        deviceAssociation.setFactoryId(FACTORY_ID);
        deviceAssociation.setDeviceAuthV2Deactivate(true);

        String baseUrl = "http://docker_host:8080/hcp-auth-webapp/";
        String deactivateUrl = "/v2/device/deactivate";

        ResponseEntity<Object> responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Mockito.doReturn(baseUrl).when(envConfig).getStringValue(DeviceAssocationProperty.SERVICE_AUTH_REST_URL_BASE);
        Mockito.doReturn(deactivateUrl).when(envConfig)
            .getStringValue(DeviceAssocationProperty.SERVICE_AUTH_REST_DEACTIVATE_V2_DEVICE);
        Mockito.doReturn(responseEntity).when(hcpRestClientLibrary)
            .doPost(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any());
        assertThrows(ObserverMessageProcessFailureException.class,
            () -> deviceAuthAssociationStateObserver.stateChanged(deviceAssociation));
    }

    @Test
    public void stateChangedTest_HttpClientErrorException() {
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(1L);
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setHarmanId("H1234");
        deviceAssociation.setVehicleId("V1234");
        deviceAssociation.setFactoryId(FACTORY_ID);
        deviceAssociation.setDeviceAuthV2Deactivate(true);

        String baseUrl = "http://docker_host:8080/hcp-auth-webapp/";
        String deactivateUrl = "/v2/device/deactivate";

        Mockito.doReturn(baseUrl).when(envConfig).getStringValue(DeviceAssocationProperty.SERVICE_AUTH_REST_URL_BASE);
        Mockito.doReturn(deactivateUrl).when(envConfig)
            .getStringValue(DeviceAssocationProperty.SERVICE_AUTH_REST_DEACTIVATE_V2_DEVICE);
        Mockito.doThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR)).when(hcpRestClientLibrary)
            .doPost(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any());
        assertThrows(ObserverMessageProcessFailureException.class,
            () -> deviceAuthAssociationStateObserver.stateChanged(deviceAssociation));
    }

    @Test
    public void stateChangedTest_AuthsRequestIsTrue() throws ObserverMessageProcessFailureException {
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(1L);
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setHarmanId("H1234");
        deviceAssociation.setVehicleId("V1234");
        deviceAssociation.setFactoryId(FACTORY_ID);
        deviceAssociation.setDeviceAuthV2Deactivate(false);
        deviceAssociation.setAssociationStatus(AssociationStatus.DISASSOCIATED);
        deviceAssociation.setAuthsRequest(true);
        deviceAuthAssociationStateObserver.stateChanged(deviceAssociation);
        assertEquals(AssociationStatus.DISASSOCIATED, deviceAssociation.getAssociationStatus());
        assertTrue(deviceAssociation.isAuthsRequest());
    }

    @Test
    public void stateChangedTest_Associated() throws ObserverMessageProcessFailureException {
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(1L);
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setHarmanId("H1234");
        deviceAssociation.setVehicleId("V1234");
        deviceAssociation.setFactoryId(FACTORY_ID);
        deviceAssociation.setDeviceAuthV2Deactivate(false);
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        deviceAssociation.setAuthsRequest(false);
        deviceAuthAssociationStateObserver.stateChanged(deviceAssociation);
        assertEquals(AssociationStatus.ASSOCIATED, deviceAssociation.getAssociationStatus());
    }

    @Test
    public void stateChangedTest_deactivatev1Success() throws ObserverMessageProcessFailureException {
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(1L);
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setHarmanId("H1234");
        deviceAssociation.setVehicleId("V1234");
        deviceAssociation.setFactoryId(FACTORY_ID);
        deviceAssociation.setDeviceAuthV2Deactivate(false);
        deviceAssociation.setAssociationStatus(AssociationStatus.DISASSOCIATED);
        deviceAssociation.setAuthsRequest(false);

        String baseUrl = "http://docker_host:8080/hcp-auth-webapp/";
        String deactivateUrl = "/device/deactivate";

        ResponseEntity<Object> responseEntity = new ResponseEntity<>(HttpStatus.OK);

        Mockito.doReturn(baseUrl).when(envConfig).getStringValue(DeviceAssocationProperty.SERVICE_AUTH_REST_URL_BASE);
        Mockito.doReturn(deactivateUrl).when(envConfig)
            .getStringValue(DeviceAssocationProperty.SERVICE_AUTH_REST_DEACTIVATE_DEVICE);
        Mockito.doReturn(responseEntity).when(hcpRestClientLibrary)
            .doPost(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any());
        deviceAuthAssociationStateObserver.stateChanged(deviceAssociation);
        assertEquals(responseEntity, hcpRestClientLibrary.doPost(Mockito.anyString(), Mockito.any(),
                Mockito.any(), Mockito.any()));
    }

    @Test
    public void stateChangedTest_deactivatev1Failed() {
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(1L);
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setHarmanId("H1234");
        deviceAssociation.setVehicleId("V1234");
        deviceAssociation.setFactoryId(FACTORY_ID);
        deviceAssociation.setDeviceAuthV2Deactivate(false);
        deviceAssociation.setAssociationStatus(AssociationStatus.DISASSOCIATED);
        deviceAssociation.setAuthsRequest(false);

        String baseUrl = "http://docker_host:8080/hcp-auth-webapp/";
        String deactivateUrl = "/device/deactivate";

        ResponseEntity<Object> responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Mockito.doReturn(baseUrl).when(envConfig).getStringValue(DeviceAssocationProperty.SERVICE_AUTH_REST_URL_BASE);
        Mockito.doReturn(deactivateUrl).when(envConfig)
            .getStringValue(DeviceAssocationProperty.SERVICE_AUTH_REST_DEACTIVATE_DEVICE);
        Mockito.doReturn(responseEntity).when(hcpRestClientLibrary)
            .doPost(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any());
        assertThrows(ObserverMessageProcessFailureException.class,
            () -> deviceAuthAssociationStateObserver.stateChanged(deviceAssociation));
    }
}