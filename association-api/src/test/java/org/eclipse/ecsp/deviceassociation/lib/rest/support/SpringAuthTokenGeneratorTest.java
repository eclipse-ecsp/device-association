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

package org.eclipse.ecsp.deviceassociation.lib.rest.support;

import org.apache.http.entity.ContentType;
import org.eclipse.ecsp.common.config.EnvConfig;
import org.eclipse.ecsp.deviceassociation.lib.config.DeviceAssocationProperty;
import org.eclipse.ecsp.deviceassociation.lib.service.Constants;
import org.eclipse.ecsp.services.clientlib.HcpRestClientLibrary;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for SpringAuthTokenGenerator.
 */
public class SpringAuthTokenGeneratorTest {

    public static final ResponseEntity<String> RESPONSE_ENTITY2 = null;
    private static final String URI = "/token";
    private static final String RESPONSE1 = "{\n" 
            +        "  \"access_token\": \"dummyToken\",\n" 
            +        "  \"scope\": \"OAuth2ClientMgmt SelfManage\",\n" 
            +        "  \"token_type\": \"Bearer\",\n" 
            +        "  \"expires_in\": 3599\n" 
            +        "}";
    public static final ResponseEntity<String> RESPONSE_ENTITY1 = new ResponseEntity<>(RESPONSE1, HttpStatus.OK);
    public static final ResponseEntity<String> RESPONSE_ENTITY3 =
        new ResponseEntity<>(RESPONSE1, HttpStatus.INTERNAL_SERVER_ERROR);
    @InjectMocks
    private SpringAuthTokenGenerator springAuthTokenGenerator;
    @Mock
    private HcpRestClientLibrary restClientLibrary;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private EnvConfig<DeviceAssocationProperty> envConfig;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void fetchSpringAuthTokenTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", ContentType.APPLICATION_FORM_URLENCODED.toString());
        ReflectionTestUtils.setField(springAuthTokenGenerator, "springAuthServiceUrl", "/token");
        ReflectionTestUtils.setField(springAuthTokenGenerator, "clientId", "dummyClient");
        ReflectionTestUtils.setField(springAuthTokenGenerator, "clientSecret", "dummySecret");
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(Constants.GRANT_TYPE_KEY, Constants.SPRING_AUTH_CLIENT_CREDENTIALS);
        body.add(Constants.SPRING_AUTH_SCOPE_KEY, Constants.SPRING_AUTH_SCOPE_VALUE);
        body.add(Constants.SPRING_AUTH_CLIENT_ID, "dummyClient");
        body.add(Constants.SPRING_AUTH_CLIENT_SECRET, "dummySecret");
        Mockito.doReturn(RESPONSE_ENTITY1).when(restTemplate)
            .exchange(URI, HttpMethod.POST, new HttpEntity<>(body, null), String.class);
        Mockito.when(restClientLibrary.doPost(URI, headers, body, String.class)).thenReturn(RESPONSE_ENTITY1);
        String token = springAuthTokenGenerator.fetchSpringAuthToken();
        assertEquals("dummyToken", token);
    }

    @Test
    public void loadSecretsTest2() {
        Mockito.when(envConfig.getBooleanValue(DeviceAssocationProperty.SECRET_VAULT_ENABLE_FLG)).thenReturn(false);
        Mockito.when(envConfig.getStringValue(DeviceAssocationProperty.SPRING_AUTH_CLIENT_ID))
            .thenReturn("dummyClient");
        Mockito.when(envConfig.getStringValue(DeviceAssocationProperty.SPRING_AUTH_CLIENT_SECRET))
            .thenReturn("dummySecret");
        Mockito.when(envConfig.getStringValue(DeviceAssocationProperty.SPRING_AUTH_SERVICE_URL)).thenReturn("/token");
        springAuthTokenGenerator.loadSecrets();
        assertFalse(envConfig.getBooleanValue(DeviceAssocationProperty.SECRET_VAULT_ENABLE_FLG));
    }

    @Test
    public void fetchSpringAuthTokenTestNotNullResponse() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", ContentType.APPLICATION_FORM_URLENCODED.toString());
        ReflectionTestUtils.setField(springAuthTokenGenerator, "springAuthServiceUrl", "/token");
        ReflectionTestUtils.setField(springAuthTokenGenerator, "clientId", "dummyClient");
        ReflectionTestUtils.setField(springAuthTokenGenerator, "clientSecret", "dummySecret");
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(Constants.GRANT_TYPE_KEY, Constants.SPRING_AUTH_CLIENT_CREDENTIALS);
        body.add(Constants.SPRING_AUTH_SCOPE_KEY, Constants.SPRING_AUTH_SCOPE_VALUE);
        body.add(Constants.SPRING_AUTH_CLIENT_ID, "dummyClient");
        body.add(Constants.SPRING_AUTH_CLIENT_SECRET, "dummySecret");
        Mockito.doReturn(RESPONSE_ENTITY1).when(restTemplate)
            .exchange(URI, HttpMethod.POST, new HttpEntity<>(body, null), String.class);
        Mockito.when(restClientLibrary.doPost(URI, headers, body, String.class)).thenReturn(RESPONSE_ENTITY1);
        String token = springAuthTokenGenerator.fetchSpringAuthToken();
        assertNotNull(RESPONSE_ENTITY1);
    }

    @Test
    public void fetchSpringAuthTokenTestNullResponse() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", ContentType.APPLICATION_FORM_URLENCODED.toString());
        ReflectionTestUtils.setField(springAuthTokenGenerator, "springAuthServiceUrl", "/token");
        ReflectionTestUtils.setField(springAuthTokenGenerator, "clientId", "dummyClient");
        ReflectionTestUtils.setField(springAuthTokenGenerator, "clientSecret", "dummySecret");
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(Constants.GRANT_TYPE_KEY, Constants.SPRING_AUTH_CLIENT_CREDENTIALS);
        body.add(Constants.SPRING_AUTH_SCOPE_KEY, Constants.SPRING_AUTH_SCOPE_VALUE);
        body.add(Constants.SPRING_AUTH_CLIENT_ID, "dummyClient");
        body.add(Constants.SPRING_AUTH_CLIENT_SECRET, "dummySecret");
        Mockito.doReturn(RESPONSE_ENTITY2).when(restTemplate)
            .exchange(URI, HttpMethod.POST, new HttpEntity<>(body, null), String.class);
        Mockito.when(restClientLibrary.doPost(URI, headers, body, String.class)).thenReturn(RESPONSE_ENTITY1);
        String token = springAuthTokenGenerator.fetchSpringAuthToken();
        assertNull(RESPONSE_ENTITY2);
    }

    @Test
    public void fetchSpringAuthTokenTestWithInternalError() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", ContentType.APPLICATION_FORM_URLENCODED.toString());
        ReflectionTestUtils.setField(springAuthTokenGenerator, "springAuthServiceUrl", "/token");
        ReflectionTestUtils.setField(springAuthTokenGenerator, "clientId", "dummyClient");
        ReflectionTestUtils.setField(springAuthTokenGenerator, "clientSecret", "dummySecret");
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(Constants.GRANT_TYPE_KEY, Constants.SPRING_AUTH_CLIENT_CREDENTIALS);
        body.add(Constants.SPRING_AUTH_SCOPE_KEY, Constants.SPRING_AUTH_SCOPE_VALUE);
        body.add(Constants.SPRING_AUTH_CLIENT_ID, "dummyClient");
        body.add(Constants.SPRING_AUTH_CLIENT_SECRET, "dummySecret");
        Mockito.doReturn(RESPONSE_ENTITY3).when(restTemplate)
            .exchange(URI, HttpMethod.POST, new HttpEntity<>(body, null), String.class);
        Mockito.when(restClientLibrary.doPost(URI, headers, body, String.class)).thenReturn(RESPONSE_ENTITY1);
        String token = springAuthTokenGenerator.fetchSpringAuthToken();
        assertNotNull(RESPONSE_ENTITY3);
    }
}