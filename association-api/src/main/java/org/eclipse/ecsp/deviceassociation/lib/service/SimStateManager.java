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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.ecsp.exception.shared.SimStateChangeFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * The SimStateManager class is responsible for managing the state of a SIM card.
 * It provides methods to change the SIM state and retrieve the transaction status.
 */
@Service
public class SimStateManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimStateManager.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${wam_base_url}")
    private String baseUrl;

    @Value("${wam_base_url_version}")
    private String baseVersion;

    /**
     * Changes the state of a SIM card associated with the given IMSI and country code.
     *
     * @param imsi The IMSI (International Mobile Subscriber Identity) of the SIM card.
     * @param countryCode The country code of the SIM card.
     * @param state The new state to set for the SIM card.
     * @return The transaction ID of the state change request, or an empty string if the response is null.
     * @throws SimStateChangeFailureException If the state change request fails.
     */
    public String changeSimState(String imsi, String countryCode, SimState state) {
        WamRequest wamRequest = new WamRequest();
        wamRequest.setState(state.getSimState());
        LOGGER.info("## changeSimState - START imsi: {}", imsi);
        HttpEntity<WamRequest> entity = new HttpEntity<>(wamRequest, createHeaders());
        String wamUrl = buildWamStateChangeUri(imsi, countryCode);
        LOGGER.debug("## State change wam api called with state: {}, wamUrl: {}", wamRequest.getState(), wamUrl);
        try {
            ResponseEntity<WamResponse<SimResponseData<SimActivationState>>> response =
                restTemplate.exchange(wamUrl, HttpMethod.PUT,
                    entity, new ParameterizedTypeReference<WamResponse<SimResponseData<SimActivationState>>>() {
                    });
            LOGGER.info("## Wam response: {} ", response.getBody());
            WamResponse<SimResponseData<SimActivationState>> resp = response.getBody();
            // 2.33 Release - Sonar NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE code smell fix
            return resp != null ? resp.getData().getTransactionId() : StringUtils.EMPTY;
        } catch (HttpClientErrorException e) {
            throw new SimStateChangeFailureException(ApiMessageEnum.SIM_STATE_CHANGE_FAILED.getCode(),
                ApiMessageEnum.SIM_STATE_CHANGE_FAILED.getMessage(), e.getResponseBodyAsString(), e);
        }
    }

    /**
     * Retrieves the transaction status for a SIM card.
     *
     * @param countryId     The ID of the country.
     * @param imsi          The IMSI (International Mobile Subscriber Identity) of the SIM card.
     * @param transactionId The ID of the transaction.
     * @return The transaction status of the SIM card.
     * @throws SimStateChangeFailureException If there is a failure in retrieving the transaction status.
     */
    public SimTransactionStatusDto pollTransactionStatus(String countryId, String imsi, String transactionId) {
        try {
            final String uri = getWamTranStatusUrl(imsi, countryId, transactionId);
            ResponseEntity<WamResponse<SimTransactionStatusDto>> response = restTemplate
                .exchange(uri, HttpMethod.GET, null,
                    new ParameterizedTypeReference<WamResponse<SimTransactionStatusDto>>() {
                    });
            WamResponse<SimTransactionStatusDto> resp = response.getBody();
            return resp != null ? resp.getData() : new SimTransactionStatusDto();
        } catch (HttpClientErrorException e) {
            throw new SimStateChangeFailureException(ApiMessageEnum.SIM_GET_TRAN_STATE_FAILED.getCode(),
                ApiMessageEnum.SIM_GET_TRAN_STATE_FAILED.getMessage(), e.getResponseBodyAsString(), e);
        }
    }

    /**
     * Builds the URI for changing the state of a SIM in the WAM system.
     *
     * @param imsi The IMSI (International Mobile Subscriber Identity) of the SIM.
     * @param countryCode The country code of the SIM.
     * @return The URI for changing the state of the SIM in the WAM system.
     */
    private String buildWamStateChangeUri(String imsi, String countryCode) {
        return baseUrl + Constants.URL_SEPARATOR + baseVersion + Constants.URL_SEPARATOR + Constants.COUNTRIES 
            +            Constants.URL_SEPARATOR
            + countryCode + Constants.URL_SEPARATOR + Constants.SIMS + Constants.URL_SEPARATOR + imsi;
    }

    /**
     * Constructs the URL for retrieving the transaction status of a SIM card.
     *
     * @param imsi The IMSI (International Mobile Subscriber Identity) of the SIM card.
     * @param countryCode The country code of the SIM card.
     * @param transactionId The ID of the transaction.
     * @return The URL for retrieving the transaction status.
     */
    private String getWamTranStatusUrl(String imsi, String countryCode, String transactionId) {
        return baseUrl + Constants.URL_SEPARATOR + baseVersion + Constants.URL_SEPARATOR + Constants.COUNTRIES 
            +            Constants.URL_SEPARATOR
            + countryCode + Constants.URL_SEPARATOR + Constants.SIMS + Constants.URL_SEPARATOR + imsi 
            +            Constants.URL_SEPARATOR + Constants.TRANSACTIONS + Constants.URL_SEPARATOR + transactionId;
    }

    /**
     * Creates and returns the HttpHeaders object with the necessary headers for making a request.
     *
     * @return The HttpHeaders object with the required headers.
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(Constants.ACCEPT, Constants.APPLICATION_JSON);
        headers.add(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
        return headers;
    }

}