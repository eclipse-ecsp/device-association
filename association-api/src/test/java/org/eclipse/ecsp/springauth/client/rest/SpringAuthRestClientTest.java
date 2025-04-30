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

package org.eclipse.ecsp.springauth.client.rest;

import org.eclipse.ecsp.springauth.client.exception.SpringAuthClientException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import javax.naming.directory.InvalidAttributeValueException;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * The `SpringAuthRestClientTest` class is a Java test class that contains unit tests for the `SpringAuthRestClient`
 * class.
 * It uses the JUnit testing framework and Mockito for mocking dependencies.
 */
public class SpringAuthRestClientTest {

    public static final String TOKEN = "token ";
    private static final String RESPONSE1 = "{\n"
        + "  \"code\": \"sp-000\",\n"
        + "  \"message\": \"Client deleted successfully!!\",\n"
        + "  \"httpStatus\": \"OK\"\n"
        + "}";
    public static final ResponseEntity<String> RESPONSE_ENTITY1 = new ResponseEntity<>(RESPONSE1, HttpStatus.OK);
    public static final ResponseEntity<String> RESPONSE_ENTITY2 = new ResponseEntity<>(RESPONSE1, HttpStatus.CREATED);

    @InjectMocks
    private SpringAuthRestClient springAuthRestClient;

    @Mock
    private RestTemplate restTemplate;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void deleteRegisteredClientTest() {
        ReflectionTestUtils.setField(springAuthRestClient, "springAuthBaseUrl",
            "http://uidam-user-management.sw-platform:8080/v1/oauth2/client");
        ReflectionTestUtils.setField(springAuthRestClient, "restTemplate", restTemplate);

        Mockito.doReturn(RESPONSE_ENTITY1).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.eq(HttpMethod.DELETE), Mockito.any(HttpEntity.class),
                Mockito.eq(String.class));

        assertNull(springAuthRestClient.deleteRegisteredClient(TOKEN, "DOGQENC8JRZ480"));
    }

    @Test
    public void deleteRegisteredClientTest_nullResponse() {
        ReflectionTestUtils.setField(springAuthRestClient, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(springAuthRestClient, "springAuthBaseUrl", "http://uidam-user-management"
            + ".sw-platform:8080/v1/oauth2/client");

        Mockito.doReturn(null).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.eq(HttpMethod.DELETE), Mockito.any(HttpEntity.class),
                Mockito.eq(String.class));
        assertThrows(SpringAuthClientException.class, () ->
            springAuthRestClient.deleteRegisteredClient(TOKEN, "DOGQENC8JRZ480"));
    }

    @Test
    public void deleteRegisteredClientTest_noContent() {
        ReflectionTestUtils.setField(springAuthRestClient, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(springAuthRestClient, "springAuthBaseUrl",
            "http://uidam-user-management.sw-platform:8080/v1/oauth2/client");

        Mockito.doReturn(new ResponseEntity<>("body", HttpStatus.NO_CONTENT)).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.eq(HttpMethod.DELETE), Mockito.any(HttpEntity.class),
                Mockito.eq(String.class));
        assertNull(springAuthRestClient.deleteRegisteredClient(TOKEN, "DOGQENC8JRZ480"));
    }

    @Test
    public void createRegisteredClientTest() {
        ReflectionTestUtils.setField(springAuthRestClient, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(springAuthRestClient, "springAuthBaseUrl",
            "http://uidam-user-management.sw-platform:8080/v1/oauth2/client");

        Mockito.doReturn(RESPONSE_ENTITY2).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.any(HttpEntity.class),
                Mockito.eq(String.class));
        springAuthRestClient.createRegisteredClient(TOKEN, "DOYXXMUMIBO501",
            "PFeEgAkntRW5p2C3UWFtWutwyKP6bEZuKa15U1VBnzzrQBQWB7LCKpbKinMpPSBG1718252728166",
            "dongle");
        assertEquals("token ", TOKEN);
    }

    @Test
    public void createRegisteredClientTest_nullResponse() {
        ReflectionTestUtils.setField(springAuthRestClient, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(springAuthRestClient, "springAuthBaseUrl",
            "http://uidam-user-management.sw-platform:8080/v1/oauth2/client");

        Mockito.doReturn(null).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.any(HttpEntity.class),
                Mockito.eq(String.class));
        springAuthRestClient.createRegisteredClient(TOKEN, "DOYXXMUMIBO501",
            "PFeEgAkntRW5p2C3UWFtWutwyKP6bEZuKa15U1VBnzzrQBQWB7LCKpbKinMpPSBG1718252728166", "dongle");
        assertEquals("token ", TOKEN);
    }

    @Test
    public void createRegisteredClientTest_noContent() {
        ReflectionTestUtils.setField(springAuthRestClient, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(springAuthRestClient, "springAuthBaseUrl",
            "http://uidam-user-management.sw-platform:8080/v1/oauth2/client");

        Mockito.doReturn(new ResponseEntity<>("body", HttpStatus.NO_CONTENT)).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.any(HttpEntity.class),
                Mockito.eq(String.class));
        assertThrows(SpringAuthClientException.class, () ->
            springAuthRestClient.createRegisteredClient(TOKEN, "DOYXXMUMIBO501",
                "PFeEgAkntRW5p2C3UWFtWutwyKP6bEZuKa15U1VBnzzrQBQWB7LCKpbKinMpPSBG1718252728166", "dongle"));
    }

    @Test
    public void updateRegisteredClientTest() throws InvalidAttributeValueException {
        ReflectionTestUtils.setField(springAuthRestClient, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(springAuthRestClient, "springAuthBaseUrl",
            "http://uidam-user-management.sw-platform:8080/v1/oauth2/client");

        Mockito.doReturn(new ResponseEntity<>("body", HttpStatus.OK)).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.eq(HttpMethod.PUT), Mockito.any(HttpEntity.class),
                Mockito.eq(String.class));

        springAuthRestClient.updateRegisteredClient(TOKEN, "DOYXXMUMIBO501",
            "PFeEgAkntRW5p2C3UWFtWutwyKP6bEZuKa15U1VBnzzrQBQWB7LCKpbKinMpPSBG1718252728166", "dongle", "status");
        assertEquals("token ", TOKEN);
    }

    @Test
    public void updateRegisteredClientTest_nullResponse() throws InvalidAttributeValueException {
        ReflectionTestUtils.setField(springAuthRestClient, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(springAuthRestClient, "springAuthBaseUrl",
            "http://uidam-user-management.sw-platform:8080/v1/oauth2/client");

        Mockito.doReturn(null).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.eq(HttpMethod.PUT), Mockito.any(HttpEntity.class),
                Mockito.eq(String.class));

        springAuthRestClient.updateRegisteredClient(TOKEN, "DOYXXMUMIBO501",
            "PFeEgAkntRW5p2C3UWFtWutwyKP6bEZuKa15U1VBnzzrQBQWB7LCKpbKinMpPSBG1718252728166", "dongle", "status");
        assertEquals("token ", TOKEN);
    }

    @Test
    public void updateRegisteredClientTest_noContent() throws InvalidAttributeValueException {
        ReflectionTestUtils.setField(springAuthRestClient, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(springAuthRestClient, "springAuthBaseUrl",
            "http://uidam-user-management.sw-platform:8080/v1/oauth2/client");

        Mockito.doReturn(new ResponseEntity<>("body", HttpStatus.NO_CONTENT)).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.eq(HttpMethod.PUT), Mockito.any(HttpEntity.class),
                Mockito.eq(String.class));

        assertThrows(SpringAuthClientException.class, () ->
            springAuthRestClient.updateRegisteredClient(TOKEN, "DOYXXMUMIBO501",
                "PFeEgAkntRW5p2C3UWFtWutwyKP6bEZuKa15U1VBnzzrQBQWB7LCKpbKinMpPSBG1718252728166",
                "dongle", "status"));
    }


    @Test
    public void getRegisteredClientTest() throws InvalidAttributeValueException {
        ReflectionTestUtils.setField(springAuthRestClient, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(springAuthRestClient, "springAuthBaseUrl",
            "http://uidam-user-management.sw-platform:8080/v1/oauth2/client");

        Mockito.doReturn(new ResponseEntity<>("body", HttpStatus.OK)).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(HttpEntity.class),
                Mockito.eq(String.class));

        springAuthRestClient.getRegisteredClient(TOKEN, "DOGQENC8JRZ480");
        assertEquals("token ", TOKEN);
    }

    @Test
    public void getRegisteredClientTest_noContent() {
        ReflectionTestUtils.setField(springAuthRestClient, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(springAuthRestClient, "springAuthBaseUrl",
            "http://uidam-user-management.sw-platform:8080/v1/oauth2/client");

        Mockito.doReturn(new ResponseEntity<>("body", HttpStatus.NO_CONTENT)).when(restTemplate)
            .exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(HttpEntity.class),
                Mockito.eq(String.class));

        assertThrows(SpringAuthClientException.class, () ->
            springAuthRestClient.getRegisteredClient(TOKEN, "DOGQENC8JRZ480"));
    }
}