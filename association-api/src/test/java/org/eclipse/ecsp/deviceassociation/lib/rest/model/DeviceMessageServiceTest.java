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

package org.eclipse.ecsp.deviceassociation.lib.rest.model;


import org.eclipse.ecsp.common.config.EnvConfig;
import org.eclipse.ecsp.deviceassociation.lib.model.ClientConfigEventIds;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * DeviceMessageServiceTest class.
 */
public class DeviceMessageServiceTest {

    @InjectMocks
    DeviceMessageService deviceMessageService;

    @Mock
    RestTemplate restTemplate;

    @Mock
    EnvConfig envConfig;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void initTest() {

        deviceMessageService.init();
        assertNotNull(deviceMessageService);
    }

    @Test
    public void publishMessageTest_NullResponse() {
        ClientConfigEventIds domain = ClientConfigEventIds.DISASSOCIATION;
        String command = "put";
        Object data = null;
        String deviceId = "H1234";
        String version = "1.0";
        Mockito.doReturn(null).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.eq(String.class));
        assertThrows(Exception.class,
            () -> deviceMessageService.publishMessage(domain, command, data, deviceId, version));
    }

    @Test
    public void publishMessageTest_NullStatusCode() {
        ClientConfigEventIds domain = ClientConfigEventIds.DISASSOCIATION;
        String command = "put";
        Object data = null;
        String deviceId = "H1234";
        String version = "1.0";
        ResponseEntity<String> response = mock(ResponseEntity.class);
        Mockito.doReturn(response).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.eq(String.class));
        Mockito.doReturn(null).when(response).getStatusCode();
        assertThrows(Exception.class,
            () -> deviceMessageService.publishMessage(domain, command, data, deviceId, version));
    }

    @Test
    public void publishMessageTest_OkStatusCode() throws Exception {
        ClientConfigEventIds domain = ClientConfigEventIds.DISASSOCIATION;
        String command = "put";
        Object data = null;
        String deviceId = "H1234";
        String version = "1.0";
        ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.OK);
        Mockito.doReturn(response).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.eq(String.class));
        deviceMessageService.publishMessage(domain, command, data, deviceId, version);
        assertNull(restTemplate.exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.eq(String.class)));
    }

    @Test
    public void publishMessageTest_BadRequest() {
        ClientConfigEventIds domain = ClientConfigEventIds.DISASSOCIATION;
        String command = "put";
        Object data = null;
        String deviceId = "H1234";
        String version = "1.0";
        ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Mockito.doReturn(response).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.eq(String.class));
        assertThrows(Exception.class,
            () -> deviceMessageService.publishMessage(domain, command, data, deviceId, version));
    }
}