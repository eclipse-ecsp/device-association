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

    /**
     * The RestTemplate instance used for making RESTful web service calls.
     * This is an autowired dependency, which means it is automatically injected
     * by the Spring framework. It provides convenient methods for interacting
     * with RESTful APIs, such as sending HTTP requests and processing responses.
     */
    @Autowired
    protected RestTemplate restTemplate;

    /**
     * Executes an HTTP GET request to the specified service URL with the provided
     * request headers and maps the response to the specified class type.
     *
     * @param <T>           The type of the response body.
     * @param serviceUrl    The URL of the service to send the GET request to.
     * @param requestHeaders The HTTP headers to include in the request.
     * @param cls           The class type to which the response body should be mapped.
     * @return A {@link ResponseEntity} containing the response body of type {@code T}
     *         and HTTP status code. If the response is null, the method logs a debug
     *         message indicating that the response entity is null.
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
     * Sends an HTTP POST request to the specified service URL with the provided
     * request headers and body, and returns the response as a ResponseEntity of the
     * specified type.
     *
     * @param <T>           The type of the response body.
     * @param serviceUrl    The URL of the service to which the POST request is sent.
     * @param requestHeaders The HTTP headers to include in the request.
     * @param body          The body of the POST request.
     * @param cls           The class type of the response body.
     * @return A ResponseEntity containing the response body of the specified type.
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
     * Executes an HTTP PUT request to the specified service URL with the provided
     * request headers and body, and returns the response as a ResponseEntity of the
     * specified type.
     *
     * @param <T>           The type of the response body.
     * @param serviceUrl    The URL of the service to which the PUT request is sent.
     * @param requestHeaders The HTTP headers to include in the request.
     * @param body          The body of the PUT request.
     * @param cls           The class type of the response body.
     * @return A ResponseEntity containing the response from the server, with the
     *         body deserialized into the specified type.
     */
    public <T> ResponseEntity<T> doPut(String serviceUrl, HttpHeaders requestHeaders, Object body, Class<T> cls) {
        ResponseEntity<T> responseEntity = null;
        HttpEntity<?> httpEntity = new HttpEntity<>(body, requestHeaders);
        responseEntity = restTemplate.exchange(serviceUrl, HttpMethod.PUT, httpEntity, cls);
        log.info("Response Entity from doPUT operation {}", responseEntity);
        return responseEntity;
    }

}
