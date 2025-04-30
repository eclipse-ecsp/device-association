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

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for DeviceActivationService.
 */
public class DeviceActivationServiceTest {

    @InjectMocks
    DeviceActivationService deviceActivationService;

    @Mock
    private RestTemplate restTemplate;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void activateDeviceTest_NullResponse() {
        ActivationRequest activationRequest = new ActivationRequest();
        activationRequest.setImei("IMEI1234");
        activationRequest.setSerialNumber("S1234");
        activationRequest.setBssid("B1234");
        activationRequest.setVin("TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0");
        activationRequest.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequest.setHwVersion("SW-101");
        activationRequest.setSwVersion("HW-101");
        activationRequest.setProductType("xyz");
        activationRequest.setDeviceType("dongle");
        Mockito.doReturn(null).when(restTemplate)
            .exchange(Mockito.any(), Mockito.any(), Mockito.any(), (Class) Mockito.any());
        assertThrows(RuntimeException.class, () -> deviceActivationService.activateDevice(activationRequest));
    }

    @Test
    public void activateDeviceTest_BadRequest() {
        ActivationRequest activationRequest = new ActivationRequest();
        activationRequest.setImei("IMEI1234");
        activationRequest.setSerialNumber("S1234");
        activationRequest.setBssid("B1234");
        activationRequest.setVin("TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0");
        activationRequest.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequest.setHwVersion("SW-101");
        activationRequest.setSwVersion("HW-101");
        activationRequest.setProductType("xyz");
        activationRequest.setDeviceType("dongle");
        ActivationResponse activationResponse = new ActivationResponse();
        activationResponse.setDeviceId("H1234");
        activationResponse.setDeviceAssociationCode("Assoc123");
        activationResponse.setPasscode("qwerty");
        ResponseEntity<ActivationResponse> response = new ResponseEntity<>(activationResponse, HttpStatus.BAD_REQUEST);
        Mockito.doReturn(response).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), (Class<ActivationResponse>) Mockito.any());
        assertThrows(RuntimeException.class, () -> deviceActivationService.activateDevice(activationRequest));
    }

    @Test
    public void activateDeviceTest_Ok() {
        ActivationRequest activationRequest = new ActivationRequest();
        activationRequest.setImei("IMEI1234");
        activationRequest.setSerialNumber("S1234");
        activationRequest.setBssid("B1234");
        activationRequest.setVin("TESTVIN_Make:FIRST_Model:500_Year:2012_Type:Car_0");
        activationRequest.setQualifier(
            "uW12717sf5LFPIkBK0z5bxGv5Tn72gCYHLMapw6PAMIS1FRNiiYP5X0p7b65HzmE+yMO+H3H+ZM6SSYakm"
                + "99c6y05dCcfdcWK/vYwiBr+bY=");
        activationRequest.setHwVersion("SW-101");
        activationRequest.setSwVersion("HW-101");
        activationRequest.setProductType("xyz");
        activationRequest.setDeviceType("dongle");
        ActivationResponse activationResponse = new ActivationResponse();
        activationResponse.setDeviceId("H1234");
        activationResponse.setDeviceAssociationCode("Assoc123");
        activationResponse.setPasscode("qwerty");
        ResponseEntity<ActivationResponse> response = new ResponseEntity<>(activationResponse, HttpStatus.OK);
        Mockito.doReturn(response).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), (Class) Mockito.any());
        deviceActivationService.activateDevice(activationRequest);
        assertEquals(response, restTemplate.exchange(Mockito.anyString(),
                Mockito.any(), Mockito.any(), (Class) Mockito.any()));
    }
}