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

package org.eclipse.ecsp.services.clientlib;

import org.eclipse.ecsp.common.CommonConstants;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for HcpRestClientLibrary.
 */
public class HcpRestClientLibraryTest {

    private static final int STATUS_CODE_200 = 200;

    @InjectMocks
    private HcpRestClientLibrary hcpRestClientLibrary;

    @Mock
    private RestTemplate restTemplate;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void doGet() {

        ResponseEntity<String> response = new ResponseEntity<>("{\"id\":\"1\"}", HttpStatus.OK);

        Mockito.doReturn(response).when(restTemplate)
            .exchange("/v1/devices", HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);
        ResponseEntity<String> stringResponseEntity =
            hcpRestClientLibrary.doGet("/v1/devices", new HttpHeaders(), String.class);

        assertEquals(STATUS_CODE_200, stringResponseEntity.getStatusCodeValue());
        assertNotNull(stringResponseEntity.getBody());
        assertEquals("{\"id\":\"1\"}", stringResponseEntity.getBody());
    }

    @Test
    public void doGetNullResponse() {

        ResponseEntity<String> response = new ResponseEntity<>("{\"id\":\"1\"}", HttpStatus.OK);

        Mockito.doReturn(response).when(restTemplate)
            .exchange("/v1/devices", HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);
        ResponseEntity<String> stringResponseEntity = hcpRestClientLibrary.doGet(null, new HttpHeaders(), String.class);
        assertNull(stringResponseEntity);
    }

    @Test
    public void doPost() {

        ResponseEntity<String> response = new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        Mockito.doReturn(response).when(restTemplate)
            .exchange("/v4/device/activate", HttpMethod.POST, new HttpEntity<>(body, null), String.class);
        ResponseEntity<String> stringResponseEntity =
            hcpRestClientLibrary.doPost("/v4/device/activate", new HttpHeaders(), body, String.class);

        assertEquals(STATUS_CODE_200, stringResponseEntity.getStatusCodeValue());
        assertNotNull(stringResponseEntity.getBody());
        assertEquals("SUCCESS", stringResponseEntity.getBody());
    }

    @Test
    public void doPut() {

        ResponseEntity<String> response = new ResponseEntity<>("{\"id\":\"1\"}", HttpStatus.OK);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(CommonConstants.AUTH_TOKEN, "password");

        Mockito.doReturn(response).when(restTemplate)
            .exchange("/v1/devices", HttpMethod.PUT, new HttpEntity<>(body, new HttpHeaders()), String.class);
        ResponseEntity<String> stringResponseEntity =
            hcpRestClientLibrary.doPut("/v1/devices", new HttpHeaders(), body, String.class);

        assertEquals(STATUS_CODE_200, stringResponseEntity.getStatusCodeValue());
        assertNotNull(stringResponseEntity.getBody());
        assertEquals("{\"id\":\"1\"}", stringResponseEntity.getBody());
    }

    @Test
    public void getAuthHeaders() {
        assertNotNull(hcpRestClientLibrary.getAuthHeaders("token"));
    }
}

