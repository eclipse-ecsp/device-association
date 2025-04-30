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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.ecsp.common.config.EnvConfig;
import org.eclipse.ecsp.deviceassociation.lib.config.DeviceAssocationProperty;
import org.eclipse.ecsp.services.clientlib.HcpRestClientLibrary;
import org.eclipse.ecsp.userauth.lib.model.LoginRequest;
import org.eclipse.ecsp.userauth.lib.model.LoginResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * Utility class for generating HTTP headers with authentication tokens.
 */
public class HttpPlatformUtil {

    private static final String TOKEN_HEADER_NAME = "AUTH-Token";
    private static final String USER_ID = "";

    /**
     * Private constructor to prevent instantiation.
     */
    private HttpPlatformUtil() {

    }

    /**
     * Generate authentication headers.
     *
     * @param envConfig              The environment configuration.
     * @param hcpRestClientLibrary   The HCP REST client library.
     * @return                       The generated HTTP headers.
     */
    public static HttpHeaders generateAuthHeader(EnvConfig<DeviceAssocationProperty> envConfig,
                                                 HcpRestClientLibrary hcpRestClientLibrary) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set(TOKEN_HEADER_NAME, generateUserAuthToken(envConfig, hcpRestClientLibrary));
        return httpHeaders;
    }

    /**
     * Generates a user authentication token using the provided environment configuration and HcpRestClientLibrary.
     *
     * @param envConfig The environment configuration containing the necessary properties.
     * @param hcpRestClientLibrary The HcpRestClientLibrary used to make the HTTP request.
     * @return The generated user authentication token.
     */
    private static String generateUserAuthToken(EnvConfig<DeviceAssocationProperty> envConfig,
                                                HcpRestClientLibrary hcpRestClientLibrary) {
        String token = StringUtils.EMPTY;
        String baseUrl = envConfig.getStringValue(DeviceAssocationProperty.SERVICE_USER_AUTH_REST_URL_BASE);
        String loginUrl = envConfig.getStringValue(DeviceAssocationProperty.SERVICE_USER_AUTH_REST_LOGIN);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<LoginResponse> response = hcpRestClientLibrary.doPost(baseUrl + loginUrl, httpHeaders,
            new LoginRequest(envConfig.getStringValue(DeviceAssocationProperty.SERVICE_USER_AUTH_USERNAME),
                    envConfig.getStringValue(DeviceAssocationProperty.SERVICE_USER_AUTH_PASSWORD)),
            LoginResponse.class);
        if (response != null && response.getStatusCode().equals(HttpStatus.OK)) {
            LoginResponse body = response.getBody();
            token = body != null ? body.getToken() : StringUtils.EMPTY;
        }
        return token;
    }

    /**
     * Generate authentication headers (version 2).
     *
     * @param envConfig              The environment configuration.
     * @param hcpRestClientLibrary   The HCP REST client library.
     * @return                       The generated HTTP headers.
     */
    public static HttpHeaders generateAuthHeaderV2(EnvConfig<DeviceAssocationProperty> envConfig,
                                                   HcpRestClientLibrary hcpRestClientLibrary) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set(USER_ID, generateUserAuthToken(envConfig, hcpRestClientLibrary));
        return httpHeaders;
    }

}
