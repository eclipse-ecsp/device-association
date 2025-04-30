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

import org.eclipse.ecsp.deviceassociation.lib.rest.support.SpringAuthTokenGenerator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for UserManagementClient.
 */
public class UserManagementClientTest {

    @Mock
    protected SpringAuthTokenGenerator springAuthTokenGenerator;
    @InjectMocks
    UserManagementClient userManagementClient;
    @Mock
    private RestTemplate restTemplate;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void getUserDetailTest() {

        Mockito.doReturn(new ResponseEntity<>("body", HttpStatus.ACCEPTED)).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(), Mockito.eq(String.class));
        assertNull(userManagementClient.getUserDetail("username", "fieldname"));
    }

    @Test
    public void getUserDetailTest_springAuth() {
        
        Mockito.doReturn(new ResponseEntity<>("body", HttpStatus.ACCEPTED)).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(), Mockito.eq(String.class));
        assertNull(userManagementClient.getUserDetail("username", "fieldname"));
    }

    @Test
    public void getUserDetailTest2_springAuth() {
        
        Mockito.doReturn(new ResponseEntity<>("body", HttpStatus.ACCEPTED)).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(), Mockito.eq(String.class));
        assertNull(userManagementClient.getUserDetail("username", "kk@gmail.com", "field1"));
    }

    @Test
    public void getUserDetailTest_throwHttpClientErrorException() {

        Mockito.doThrow(HttpClientErrorException.class).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(), Mockito.eq(String.class));
        assertNull(userManagementClient.getUserDetail("username", "fieldname"));
    }

    @Test
    public void getUserDetailTest2() {
        Mockito.doReturn(new ResponseEntity<>("body", HttpStatus.ACCEPTED)).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(), Mockito.eq(String.class));
        assertNull(userManagementClient.getUserDetail("username", "kk@gmail.com", "field1"));
    }

    @Test
    public void getUserDetailTest2_throwHttpClientErrorException() {
        Mockito.doThrow(HttpClientErrorException.class).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(), Mockito.eq(String.class));
        assertNull(userManagementClient.getUserDetail("username", "kk@gmail.com", "field1"));
    }

    @Test
    public void getUserDetailTest2_throwException() {

        Mockito.doThrow(RestClientException.class).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.any(HttpMethod.class), Mockito.any(), Mockito.eq(String.class));
        assertNull(userManagementClient.getUserDetail("username", "kk@gmail.com", "field1"));
    }
}
