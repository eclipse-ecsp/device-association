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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static org.eclipse.ecsp.common.CommonConstants.AUTH_TOKEN;

/**
 * The HcpRestClientLibrary class provides methods for making RESTful API calls.
 */
@Component
@Slf4j
public class HcpRestClientLibrary {

    @Autowired
    protected RestTemplate restTemplate;

    /**
     * Sends a GET request to the specified service URL with the provided request headers.
     *
     * @param serviceUrl     The URL of the service.
     * @param requestHeaders The headers to be included in the request.
     * @param cls            The class type of the response entity.
     * @return The response entity.
     */
    public <T> ResponseEntity<T> doGet(String serviceUrl, HttpHeaders requestHeaders, Class<T> cls) {
        log.debug("doGet:{}<------", serviceUrl);
        ResponseEntity<T> responseEntity = null;
        HttpEntity<?> httpEntity = new HttpEntity<>(requestHeaders);

        responseEntity = restTemplate.exchange(serviceUrl, HttpMethod.GET, httpEntity, cls);

        if (null != responseEntity) {
            log.debug("Response Entity : {}", responseEntity);
        } else {
            log.debug("Response Entity is null");
        }
        return responseEntity;
    }

    /**
     * Sends a POST request to the specified service URL with the provided request headers and body.
     *
     * @param serviceUrl     The URL of the service.
     * @param requestHeaders The headers to be included in the request.
     * @param body           The body of the request.
     * @param cls            The class type of the response entity.
     * @return The response entity.
     */
    public <T> ResponseEntity<T> doPost(String serviceUrl, HttpHeaders requestHeaders, Object body, Class<T> cls) {
        ResponseEntity<T> responseEntity = null;
        HttpEntity<?> httpEntity = new HttpEntity<>(body, requestHeaders);
        responseEntity = restTemplate.exchange(serviceUrl, HttpMethod.POST, httpEntity, cls);
        log.debug("Response Entity {}", responseEntity);
        return responseEntity;
    }

    /**
     * Generates the authentication headers with the provided auth token.
     *
     * @param authToken The authentication token.
     * @return The request headers with the authentication token.
     */
    public HttpHeaders getAuthHeaders(String authToken) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add(AUTH_TOKEN, authToken);
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        return requestHeaders;
    }

    /**
     * Sends a PUT request to the specified service URL with the provided request headers and body.
     *
     * @param serviceUrl     The URL of the service.
     * @param requestHeaders The headers to be included in the request.
     * @param body           The body of the request.
     * @param cls            The class type of the response entity.
     * @return The response entity.
     */
    public <T> ResponseEntity<T> doPut(String serviceUrl, HttpHeaders requestHeaders, Object body, Class<T> cls) {
        ResponseEntity<T> responseEntity = null;
        HttpEntity<?> httpEntity = new HttpEntity<>(body, requestHeaders);
        responseEntity = restTemplate.exchange(serviceUrl, HttpMethod.PUT, httpEntity, cls);
        log.info("Response Entity from doPUT operation {}", responseEntity);
        return responseEntity;
    }

}
