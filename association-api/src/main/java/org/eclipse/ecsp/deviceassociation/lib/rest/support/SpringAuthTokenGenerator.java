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

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.apache.http.entity.ContentType;
import org.eclipse.ecsp.common.config.EnvConfig;
import org.eclipse.ecsp.deviceassociation.lib.config.DeviceAssocationProperty;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AccessTokenDetails;
import org.eclipse.ecsp.deviceassociation.lib.service.Constants;
import org.eclipse.ecsp.services.clientlib.HcpRestClientLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;

/**
 * This class is responsible for generating SpringAuth tokens for authentication.
 * It fetches the token using the provided client ID, client secret, and SpringAuth service URL.
 */
@Service
@Lazy
public class SpringAuthTokenGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringAuthTokenGenerator.class);
    @Autowired
    private HcpRestClientLibrary restClientLibrary;
    @Autowired
    private EnvConfig<DeviceAssocationProperty> envConfig;
    private String clientId;
    private String clientSecret;
    private String springAuthServiceUrl;

    /**
     * Fetches the SpringAuth token.
     *
     * @return The SpringAuth token.
     */
    public String fetchSpringAuthToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(Constants.HEADER_CONTENT_TYPE_KEY, ContentType.APPLICATION_FORM_URLENCODED.toString());
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        map.add(Constants.GRANT_TYPE_KEY, Constants.SPRING_AUTH_CLIENT_CREDENTIALS);
        map.add(Constants.SPRING_AUTH_CLIENT_ID, clientId);
        map.add(Constants.SPRING_AUTH_CLIENT_SECRET, clientSecret);
        map.add(Constants.SPRING_AUTH_SCOPE_KEY, Constants.SPRING_AUTH_SCOPE_VALUE);

        LOGGER.info("Fetching spring auth token for config: {}", map);
        LOGGER.debug("Fetching spring auth token for config: {}", map);
        LOGGER.debug(" Spring Auth URL: {}", springAuthServiceUrl);
        ResponseEntity<String> response = restClientLibrary.doPost(springAuthServiceUrl, headers, map,
            String.class);
        String token = null;
        if (response.getStatusCode().equals(HttpStatus.OK)) {

            String responseBody = response.getBody();
            ObjectMapper mapper = new ObjectMapper();

            try {
                AccessTokenDetails accessTokenDetails = mapper.readValue(responseBody, AccessTokenDetails.class);
                token = accessTokenDetails.getAccessToken();
                LOGGER.info("Fetched spring auth token successfully");
                LOGGER.debug("Fetched spring auth token successfully");
            } catch (IOException e) {
                LOGGER.error("Exception occurred while parsing spring auth token", e);
            }
        }

        return token;
    }

    /**
     * Loads secrets from vault.
     */
    @PostConstruct
    public void loadSecrets() {
        clientId = envConfig.getStringValue(DeviceAssocationProperty.SPRING_AUTH_CLIENT_ID).trim();
        clientSecret = envConfig.getStringValue(DeviceAssocationProperty.SPRING_AUTH_CLIENT_SECRET).trim();
        LOGGER.info("Fetched the client id and client secret details successfully");
        springAuthServiceUrl = envConfig.getStringValue(DeviceAssocationProperty.SPRING_AUTH_SERVICE_URL);
        LOGGER.info("Fetched the spring auth url successfully");
    }
}