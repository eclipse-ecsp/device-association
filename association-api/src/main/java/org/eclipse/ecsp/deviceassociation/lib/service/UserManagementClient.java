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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.ecsp.deviceassociation.lib.model.usermanagement.UserRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.support.SpringAuthTokenGenerator;
import org.eclipse.ecsp.notification.lib.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

/**
 * This class represents a client for user management operations.
 * It provides methods to fetch user details based on different criteria.
 */
@Component
public class UserManagementClient {

    public static final String BEARER_KEY = "Bearer ";
    public static final String USER_DETAILS_ERROR_MESSAGE = "### call to getUserDetails api failed:: {}";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(UserManagementClient.class);

    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Autowired
    @Lazy
    protected SpringAuthTokenGenerator springAuthTokenGenerator;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${users_base_url}")
    private String baseUrl;
    @Value("${users_base_url_spring_auth}")
    private String baseUrlSpringAuth;
    @Value("${user_filter_url}")
    private String userFilterUrl;

    /**
     * Fetches user detail based on the provided userName and fieldName.
     *
     * @param userName the userName of the user
     * @param fieldName the fieldName to fetch from the user detail
     * @return the user detail for the specified fieldName, or an empty string if not found
     */
    public String getUserDetail(String userName, String fieldName) {
        try {
            UserRequest userRequest = new UserRequest();
            userRequest.setUserNames(Arrays.asList(userName));
            // Story 562005, 565671 - WSO2 to Spring Auth changes
            String filterUrl;
            filterUrl = buildUserFilterUriSpringAuth();
            HttpEntity<UserRequest> entity = new HttpEntity<>(userRequest, createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(filterUrl, HttpMethod.POST,
                entity, String.class);
            JsonNode root = MAPPER.readTree(response.getBody());
            JsonNode node = root.findValue(fieldName);
            return node != null ? node.asText() : "";
        } catch (HttpClientErrorException e) {
            LOGGER.error(USER_DETAILS_ERROR_MESSAGE, e);
        } catch (Exception e) {
            LOGGER.error(USER_DETAILS_ERROR_MESSAGE, e.getMessage());
        }
        return null;
    }

    /**
     * Fetches user detail based on the provided userName, email, and fieldName.
     *
     * @param userName the userName of the user
     * @param email the email of the user
     * @param fieldName the fieldName to fetch from the user detail
     * @return the user detail for the specified fieldName, or an empty string if not found
     */
    public String getUserDetail(String userName, String email, String fieldName) {
        try {
            UserRequest userRequest = new UserRequest();
            if (StringUtils.isNotEmpty(userName)) {
                userRequest.setUserNames(Arrays.asList(userName));
            }
            if (StringUtils.isNotEmpty(email)) {
                userRequest.setEmails(Arrays.asList(email));
            }
            String filterUrl;
            filterUrl = buildUserFilterUriSpringAuth();
            HttpEntity<UserRequest> entity = new HttpEntity<>(userRequest, createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(filterUrl, HttpMethod.POST,
                entity, String.class);
            JsonNode root = MAPPER.readTree(response.getBody());
            JsonNode node = root.findValue(fieldName);
            return node != null ? node.asText() : "";
        } catch (HttpClientErrorException e) {
            LOGGER.error(USER_DETAILS_ERROR_MESSAGE, e);
        } catch (Exception e) {
            LOGGER.error(USER_DETAILS_ERROR_MESSAGE, e.getMessage());
        }
        return null;
    }

    /**
     * Builds the URI for the user filter with Spring Authentication.
     *
     * @return The URI for the user filter with Spring Authentication.
     */
    private String buildUserFilterUriSpringAuth() {
        return baseUrlSpringAuth + Constants.URL_SEPARATOR + userFilterUrl + Constants.QUERY_PARAM_SEPARATOR 
            +            Constants.DEFAULT_SORT_ORDER;
    }

    /**
     * Creates and returns the HttpHeaders object with the necessary headers for making API requests.
     *
     * @return The HttpHeaders object with the required headers.
     */
    private HttpHeaders createHeaders() {
        String token;
        HttpHeaders headers = new HttpHeaders();
        headers.add(Constants.ACCEPT, Constants.APPLICATION_JSON);
        headers.add(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
        token = springAuthTokenGenerator.fetchSpringAuthToken();
        headers.set(Constants.HEADER_NAME_AUTHORIZATION, BEARER_KEY + token);
        return headers;
    }
}
