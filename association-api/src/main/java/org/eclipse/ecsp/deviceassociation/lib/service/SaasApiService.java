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

import org.eclipse.ecsp.deviceassociation.dto.Models;
import org.eclipse.ecsp.deviceassociation.dto.ModelsInfo;
import org.eclipse.ecsp.deviceassociation.dto.WhiteListedModels;
import org.eclipse.ecsp.exception.shared.ApiTechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.SYSTEM_PARAMETERS_WHITELISTED_MODEL_API_FAILURE;
import static org.eclipse.ecsp.deviceassociation.lib.service.Constants.WHITELISTED_MODELS;

/**
 * This class represents the SaasApiService, which is responsible for interacting with the SaaS API.
 */
@Service
public class SaasApiService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SaasApiService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${saas_api_base_url}")
    private String baseUrl;

    @Value("${saas_api_base_url_version}")
    private String baseVersion;

    /**
     * Constructs the URL for system parameters based on the country name.
     *
     * @param countryName The name of the country.
     * @return The URL for system parameters.
     */
    private String getSystemParamsUrl(String countryName) {
        return baseUrl + Constants.URL_SEPARATOR + baseVersion + Constants.URL_SEPARATOR
            + Constants.SYS_PARAMS + Constants.QUESTION_MARK + Constants.SYS_PARAM_KEYS
            + Constants.EQUALS + countryName;
    }

    /**
     * Creates the HTTP headers for the API request.
     *
     * @return The HTTP headers.
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(Constants.ACCEPT, Constants.APPLICATION_JSON);
        return headers;
    }

    /**
     * Retrieves the static model details from the system parameter for the given country.
     *
     * @param countryName The name of the country.
     * @return The list of model information.
     */
    public List<ModelsInfo> getStaticModelDetailsFromSystemParameter(String countryName) {
        LOGGER.debug("## getStaticModelDetailsFromSystemParameter - START countryName: {}", countryName);
        Models models = getModelCodeNameAndDongleType(countryName);
        Map<String, List<ModelsInfo>> map = models.getModels();
        if (map != null) {
            return map.get(countryName);
        }
        LOGGER.debug("## getStaticModelDetailsFromSystemParameter - END");
        return Collections.emptyList();
    }

    /**
     * Retrieves the model code name and dongle type for the given country.
     *
     * @param countryName The name of the country.
     * @return The Models object containing the model information.
     */
    public Models getModelCodeNameAndDongleType(String countryName) {
        HttpEntity<String> entity = new HttpEntity<>(createHeaders());
        String systemParamsUrl = getSystemParamsUrl(countryName);
        LOGGER.debug("## systemParamsUrl: {}", systemParamsUrl);
        ResponseEntity<Models> response = restTemplate.exchange(systemParamsUrl, HttpMethod.GET, entity, Models.class);
        LOGGER.debug("system param api responseBody: {} ", response.getBody());
        return response.getBody();
    }

    /**
     * Retrieves the whitelisted models.
     *
     * @return The WhiteListedModels object containing the whitelisted models.
     * @throws ApiTechnicalException If there is an error while retrieving the whitelisted models.
     */
    public WhiteListedModels getWhiteListedModels() throws ApiTechnicalException {
        HttpEntity<String> entity = new HttpEntity<>(createHeaders());
        String systemParamsUrl = getSystemParamsUrlForSystemParamsKey(WHITELISTED_MODELS);
        LOGGER.info("## systemParamsUrl: {}", systemParamsUrl);
        try {
            ResponseEntity<WhiteListedModels> response =
                restTemplate.exchange(systemParamsUrl, HttpMethod.GET, entity, WhiteListedModels.class);
            LOGGER.debug("system param api responseBody: {} ", response.getBody());
            LOGGER.info("Successfully received the response from the systemParameters api");
            return response.getBody();
        } catch (Exception e) {
            throw new ApiTechnicalException(SYSTEM_PARAMETERS_WHITELISTED_MODEL_API_FAILURE.getCode(),
                SYSTEM_PARAMETERS_WHITELISTED_MODEL_API_FAILURE.getMessage(),
                SYSTEM_PARAMETERS_WHITELISTED_MODEL_API_FAILURE.getGeneralMessage(), e);
        }
    }

    /**
     * Constructs the URL for system parameters based on the system parameter keys.
     *
     * @param systemParamKeys The system parameter keys.
     * @return The URL for system parameters.
     */
    private String getSystemParamsUrlForSystemParamsKey(String systemParamKeys) {
        return baseUrl + Constants.URL_SEPARATOR + baseVersion + Constants.URL_SEPARATOR
            + Constants.SYS_PARAMS + Constants.QUESTION_MARK + Constants.SYS_PARAM_KEYS
            + Constants.EQUALS + systemParamKeys;
    }
}