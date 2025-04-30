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

import org.eclipse.ecsp.deviceassociation.dto.AssociationTypeModel;
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

import java.util.UUID;

/**
 * This class provides services related to device association types.
 */
@Service
public class DeviceAssociationTypeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceAssociationTypeService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${association_type_base_url}")
    private String baseUrl;

    @Value("${association_type_base_url_version}")
    private String baseVersion;

    /**
     * Constructs the association type URL based on the given association type name.
     *
     * @param associationTypeName The name of the association type.
     * @return The constructed association type URL.
     */
    private String getAssocTypeUrl(String associationTypeName) {
        return baseUrl + Constants.URL_SEPARATOR + baseVersion + Constants.URL_SEPARATOR
            + Constants.ASSOC_TYPE + Constants.QUESTION_MARK + Constants.ASSOC_TYPE_KEYS
            + Constants.EQUALS + associationTypeName + Constants.AND + Constants.ACTIVE_STATUS + Constants.EQUALS 
            +            "true";
    }

    /**
     * Creates the HTTP headers for the API request.
     *
     * @return The HTTP headers.
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(Constants.ACCEPT, Constants.APPLICATION_JSON);
        headers.add("RequestId", UUID.randomUUID().toString());
        return headers;
    }

    /**
     * Checks if the given association type exists.
     *
     * @param associationTypeName The name of the association type to check.
     * @return true if the association type exists, false otherwise.
     */
    public boolean isAssocTypeExist(String associationTypeName) {
        HttpEntity<String> entity = new HttpEntity<>(createHeaders());
        String assocTypeUrl = getAssocTypeUrl(associationTypeName);
        LOGGER.debug("## assocTypeUrl: {}", assocTypeUrl);
        try {
            ResponseEntity<AssociationTypeModel> response = restTemplate
                .exchange(assocTypeUrl, HttpMethod.GET, entity, AssociationTypeModel.class);
            AssociationTypeModel body = response.getBody();
            if (body != null && body.getData() != null && body.getData().get(0) != null
                && body.getData().get(0).getAssocTypeList() != null) {
                LOGGER.debug("assoc type api responseBody: {} ", body.getData().get(0));
                boolean isValid = !body.getData().get(0).getAssocTypeList().isEmpty();
                LOGGER.debug("Association type validation passed: {}", isValid);
                return isValid;
            }
            LOGGER.error("Association type validation failed");
            return false;
        } catch (Exception e) {
            //Association type is not reachable, validation failure
            LOGGER.error("Association type is not reachable,validation failure");
            LOGGER.error("assoc type api exception: {} ", e.getMessage());
            return false;
        }
    }
}