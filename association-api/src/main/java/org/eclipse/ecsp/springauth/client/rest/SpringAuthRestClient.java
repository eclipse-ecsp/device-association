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

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.ecsp.springauth.client.exception.SpringAuthClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.naming.directory.InvalidAttributeValueException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a REST client for interacting with the Spring Auth server.
 * It provides methods for creating, updating, and deleting registered clients in Spring Auth.
 */
@Slf4j
public class SpringAuthRestClient {

    /**
     * Auth Header Key Constant.
     */
    public static final String AUTH_HEADER_KEY = "Authorization";
    /**
     * CONTENT TYPE constant.
     */
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";
    public static final String BEARER_KEY = "Bearer ";
    public static final String CLIENT_ID = "clientId";
    public static final String CLIENT_SECRET = "clientSecret";
    public static final String CLIENT_NAME = "clientName";
    public static final String CLIENT_CREDENTIALS = "client_credentials";
    public static final String AUTHORIZATION_CODE = "authorization_code";
    public static final String AUTHORIZATION_GRANT_TYPES = "authorizationGrantTypes";
    public static final String REDIRECT_URIS = "redirectUris";
    public static final String REDIRECT_URL = "http://localhost:9000/login";
    public static final String SCOPES = "scopes";
    public static final String STATUS = "status";
    public static final String EXCEPTION_MESSAGE = " was not successful";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Constructs a new SpringAuthRestClient with the specified base URL.
     *
     * @param springAuthBaseUrl the base URL of the Spring Auth service
     */
    public SpringAuthRestClient(String springAuthBaseUrl) {
        super();
        this.springAuthBaseUrl = springAuthBaseUrl;
        log.debug("springAuthBaseUrl :{}", springAuthBaseUrl);
    }

    /**
     * Constructs a new SpringAuthRestClient object.
     */
    public SpringAuthRestClient() {
        super();
    }

    private String springAuthBaseUrl;

    /**
     * Rest Template Ref.
     */
    @Autowired
    private RestTemplate restTemplate;

    /**
     * Deletes a registered client from the Spring Auth server.
     *
     * @param token    The authentication token.
     * @param clientId The ID of the client to be deleted.
     * @return A map containing the response from the Spring Auth server.
     * @throws SpringAuthClientException If there is an error while deleting the client.
     */
    public Map<String, Object> deleteRegisteredClient(String token, String clientId) {
        log.info("## springAuthRestClient.deleteRegisteredClient() - START clientId: {}", clientId);
        log.debug("## springAuthRestClient.deleteRegisteredClient() - START clientId: {}", clientId);
        try {
            HttpEntity<String> entity = new HttpEntity<>(createSpringAuthHeaders(token));

            String completeDeleteApiUrl = springAuthBaseUrl + "/" + clientId;
            log.debug("completeDeleteApiUrl :{}", completeDeleteApiUrl);
            ResponseEntity<String> response = restTemplate.exchange(completeDeleteApiUrl, HttpMethod.DELETE,
                    entity, String.class);
            //response is 200 if successful
            if (null != response && response.getStatusCode() != HttpStatus.OK) {
                log.info(
                        "delete device from Spring Auth server dint respond with success code instead with response"
                            + " status code :{} response body :{} ", response.getStatusCode(), response.getBody());
            }
            log.info(
                    "delete device from Spring Auth server responded with success code - response status code :{} "
                        + "response body :{} ", response.getStatusCode(), response.getBody());
        } catch (Exception e) {
            log.error("Error in delete device from Spring Auth: {}", e);
            throw new SpringAuthClientException("Response from Spring Auth for deleteDevice was not successful", e);
        }
        Map<String, Object> responseMap = null;
        log.info("## springAuthRestClient.deleteRegisteredClient() - END");
        log.debug("## springAuthRestClient.deleteRegisteredClient() - END");
        return responseMap;
    }

    /**
     * Creates HttpHeaders for Spring Auth with the provided token.
     *
     * @param token The authentication token.
     * @return The HttpHeaders object containing the Spring Auth headers.
     */
    private HttpHeaders createSpringAuthHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTH_HEADER_KEY, BEARER_KEY + token);
        headers.add(CONTENT_TYPE, APPLICATION_JSON);
        return headers;
    }

    /**
     * Creates a registered client with the specified token, clientId, clientSecret, and deviceType.
     *
     * @param token        The token used for authentication.
     * @param clientId     The ID of the client.
     * @param clientSecret The secret key of the client.
     * @param deviceType   The type of the device.
     * @throws SpringAuthClientException If there is an error while creating the client.
     */
    public void createRegisteredClient(String token, String clientId, String clientSecret, String deviceType) {
        log.info("## springAuthRestClient.createRegisteredClient() - START clientId: {} and deviceType: {}",
            clientId, deviceType);
        log.debug("## springAuthRestClient.createRegisteredClient() - START clientId: {} and deviceType: {}",
            clientId, deviceType);
        try {
            Map<String, Object> payloadMap;
            payloadMap = createPayload(clientId, clientSecret, deviceType);
            String postData = OBJECT_MAPPER.writeValueAsString(payloadMap);
            HttpEntity<String> entity = new HttpEntity<>(postData, createSpringAuthHeaders(token));
            log.debug("## Complete URL for create client :{} Post data for create client :{}", springAuthBaseUrl,
                entity);
            ResponseEntity<String> response = restTemplate.exchange(springAuthBaseUrl, HttpMethod.POST, entity,
                String.class);
            if (null != response && !response.getStatusCode().equals(HttpStatus.CREATED)) {
                throw new SpringAuthClientException("Response from spring auth for create application for the device "
                    + clientId + EXCEPTION_MESSAGE);
            }
        } catch (Exception e) {
            throw new SpringAuthClientException("Error while creating the client " + e.getMessage(), e);
        }
        log.info("## springAuthRestClient.createRegisteredClient() - END");
        log.debug("## springAuthRestClient.createRegisteredClient() - END");
    }

    /**
     * Retrieves the registered client information from the Spring Auth service.
     *
     * @param token    The authentication token.
     * @param clientId The ID of the client to retrieve.
     * @throws SpringAuthClientException If there is an error retrieving the client information.
     */
    public void getRegisteredClient(String token, String clientId) {
        log.info("## springAuthRestClient.getRegisteredClient() - START clientId: {}", clientId);
        log.debug("## springAuthRestClient.getRegisteredClient() - START clientId: {}", clientId);
        String getRegisteredClientUrl = springAuthBaseUrl + "/" + clientId;
        HttpEntity<String> entity = new HttpEntity<>(createSpringAuthHeaders(token));
        log.debug("Complete URL for get RegisteredClient :{}", getRegisteredClientUrl);
        ResponseEntity<String> response = restTemplate.exchange(getRegisteredClientUrl, HttpMethod.GET, entity,
            String.class);
        if (null != response && !response.getStatusCode().equals(HttpStatus.OK)) {
            throw new SpringAuthClientException("Response from Spring Auth for getRegisteredClient for the device "
                + clientId + EXCEPTION_MESSAGE);
        }
        log.debug("getRegisteredClient response status :{} response body :{}", response.getStatusCode(),
            response.getBody());
        log.info("## springAuthRestClient.getRegisteredClient() - END");
        log.debug("## springAuthRestClient.getRegisteredClient() - END");
    }

    /**
     * Creates a payload map for authentication.
     *
     * @param clientId     the client ID
     * @param clientSecret the client secret
     * @param deviceType   the device type
     * @return a map containing the payload for authentication
     */
    private static Map<String, Object> createPayload(String clientId, String clientSecret, String deviceType) {
        Map<String, Object> map = new HashMap<>();
        List<String> scopes = new ArrayList<>();
        scopes.add(deviceType);
        map.put(CLIENT_ID, clientId);
        map.put(CLIENT_SECRET, clientSecret);
        map.put(CLIENT_NAME, clientId);
        List<String> authorizationGrantTypes = new ArrayList<>();
        authorizationGrantTypes.add(CLIENT_CREDENTIALS);
        authorizationGrantTypes.add(AUTHORIZATION_CODE);
        map.put(AUTHORIZATION_GRANT_TYPES, authorizationGrantTypes);
        List<String> redirectUris = new ArrayList<>();
        redirectUris.add(REDIRECT_URL);
        map.put(REDIRECT_URIS, redirectUris);
        map.put(SCOPES, scopes);
        return map;
    }

    /**
     * Updates the registered client with the specified token, clientId, clientSecret, deviceType, and status.
     *
     * @param token         The token used for authentication.
     * @param clientId      The ID of the client to update.
     * @param clientSecret  The secret key of the client.
     * @param deviceType    The type of the device.
     * @param status        The status of the client.
     * @throws InvalidAttributeValueException If the clientId is blank.
     * @throws SpringAuthClientException      If there is an exception while updating the application details.
     */
    public void updateRegisteredClient(String token, String clientId, String clientSecret, String deviceType,
                                       String status) throws InvalidAttributeValueException {
        log.info("## springAuthRestClient.updateRegisteredClient() - START clientId: {} and deviceType: {}",
            clientId, deviceType);
        log.debug("## springAuthRestClient.updateRegisteredClient() - START clientId: {} and deviceType: {}",
            clientId, deviceType);
        if (StringUtils.isBlank(clientId)) {
            throw new InvalidAttributeValueException("ClientId is required");
        }
        try {
            Map<String, Object> payloadMap;
            payloadMap = updatePayload(clientId, clientSecret, deviceType, status);
            String putData = OBJECT_MAPPER.writeValueAsString(payloadMap);
            HttpEntity<String> entity = new HttpEntity<>(putData, createSpringAuthHeaders(token));

            String completeUpdateApiUrl = springAuthBaseUrl + "/" + clientId;
            log.debug("completeUpdateApiUrl :{}", completeUpdateApiUrl);
            log.debug("## Complete URL for update application :{} Put data for update application :{}",
                completeUpdateApiUrl, entity);
            ResponseEntity<String> response = restTemplate.exchange(completeUpdateApiUrl, HttpMethod.PUT, entity,
                String.class);
            if (null != response && !response.getStatusCode().equals(HttpStatus.OK)) {
                throw new SpringAuthClientException("Response from Spring Auth server for updating application for"
                    + " the device " + clientId + EXCEPTION_MESSAGE);
            }
        } catch (Exception e) {
            throw new SpringAuthClientException("Exception while updating application details" + e.getMessage(), e);
        }
        log.info("## springAuthRestClient.updateRegisteredClient() - END");
        log.debug("## springAuthRestClient.updateRegisteredClient() - END");
    }

    /**
     * Constructs a payload map for updating client information.
     *
     * @param clientId      the client ID
     * @param clientSecret  the client secret
     * @param deviceType    the device type
     * @param status        the status
     * @return a map containing the payload for updating client information
     */
    private static Map<String, Object> updatePayload(String clientId, String clientSecret, String deviceType,
                                                     String status) {
        Map<String, Object> map = new HashMap<>();
        map.put(CLIENT_SECRET, clientSecret);
        map.put(CLIENT_NAME, clientId);
        List<String> authorizationGrantTypes = new ArrayList<>();
        authorizationGrantTypes.add(CLIENT_CREDENTIALS);
        authorizationGrantTypes.add(AUTHORIZATION_CODE);
        map.put(AUTHORIZATION_GRANT_TYPES, authorizationGrantTypes);
        List<String> redirectUris = new ArrayList<>();
        redirectUris.add(REDIRECT_URL);
        map.put(REDIRECT_URIS, redirectUris);
        map.put(STATUS, status);
        if (StringUtils.isNotBlank(deviceType)) {
            List<String> scopes = new ArrayList<>();
            scopes.add(deviceType);
            map.put(SCOPES, scopes);
        }
        return map;
    }

}
