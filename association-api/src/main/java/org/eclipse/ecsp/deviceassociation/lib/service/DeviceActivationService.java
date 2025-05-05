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

import org.eclipse.ecsp.exception.shared.ApiTechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Service class for device activation.
 */
@Component
public class DeviceActivationService {

    /**
     * A constant representing the HTTP header name "Content-Type".
     * This is typically used to specify the media type of the resource
     * being sent or received in an HTTP request or response.
     */
    public static final String CONTENT_TYPE = "Content-Type";
    /**
     * A constant representing the media type "application/json".
     * This is typically used to indicate that the content being sent or received
     * is in JSON format.
     */
    public static final String APPLICATION_JSON = "application/json";
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceActivationService.class);

    @Value("${deviceauth_activateurl}")
    private String activateUrl;

    @Value("${deviceauth_baseurl}")
    private String activateBaseUrl;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Get the complete activation URL.
     *
     * @return the complete activation URL
     */
    private String getActivateUrl() {
        return activateBaseUrl + activateUrl;
    }

    /**
     * Activate a device.
     *
     * @param activationRequest the activation request
     * @return the activation response
     */
    public ActivationResponse activateDevice(ActivationRequest activationRequest) {
        try {
            LOGGER.debug("inside DeviceActivationService.activateDevice");
            HttpEntity<ActivationRequest> entity =
                new HttpEntity<>(activationRequest, createHeaders());

            LOGGER.debug("complete_activateURL :{}", getActivateUrl());
            // Story 546176, 550679, 554403 - Enabled AAD Authentication method (MAC)
            LOGGER.debug(
                "sending activation call with serial number:{}, qualifier:{}, vin:{},SWversion{}:,HWversion:{},"
                    + "product type:{}, deviceType:{}, aad:{}",
                activationRequest.getSerialNumber(), activationRequest.getQualifier(), activationRequest.getVin(),
                activationRequest.getSwVersion(), activationRequest.getHwVersion(), activationRequest.getProductType(),
                activationRequest.getDeviceType(), activationRequest.getAad());

            ResponseEntity<ActivationResponse> response = restTemplate.exchange(getActivateUrl(), HttpMethod.POST,
                entity, ActivationResponse.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                LOGGER.error(
                    "activate device endpoint did not respond with success code instead with response status code :{}"
                        + " response body :{} ", response.getStatusCode(), response.getBody());
                // throw Exception
                throw new ApiTechnicalException("activate device endpoint did not respond with success code");
            }

            return response.getBody();
        } catch (Exception e) {
            // Error in activation
            throw new ApiTechnicalException(e.getMessage());
        }
    }

    /**
     * Create the HTTP headers for the request.
     *
     * @return the HTTP headers
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(CONTENT_TYPE, APPLICATION_JSON);
        return headers;
    }
}
