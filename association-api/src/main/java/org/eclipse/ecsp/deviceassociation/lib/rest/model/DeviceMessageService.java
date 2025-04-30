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

package org.eclipse.ecsp.deviceassociation.lib.rest.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.eclipse.ecsp.common.config.EnvConfig;
import org.eclipse.ecsp.deviceassociation.lib.config.DeviceAssocationProperty;
import org.eclipse.ecsp.deviceassociation.lib.model.ClientConfigEventIds;
import org.eclipse.ecsp.deviceassociation.lib.service.Constants;
import org.eclipse.ecsp.exception.shared.ApiTechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Service class for handling device messages.
 */
@Service
public class DeviceMessageService {

    private static final String UNKNOWN_EXC_WHILE_PUBLISH_CONFIG =
            "Unknown exception while publishing config";
    /**
     * Logger Ref.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceMessageService.class);

    /**
     * device message Service URL.
     */
    private String baseUrl;

    /**
     * device message service API version.
     */
    private String baseVersion;

    @Autowired
    private EnvConfig<DeviceAssocationProperty> envConfig;

    /**
     * Rest Template Ref.
     */
    @Autowired
    private RestTemplate restTemplate;

    /**
     * Initializes the DeviceMessageService by setting the base URL and version.
     */
    @PostConstruct
    public void init() {
        baseUrl = envConfig.getStringValue(DeviceAssocationProperty.DEVICE_MQTT_MESSAGE_BASE_URL);
        baseVersion = envConfig.getStringValue(DeviceAssocationProperty.DEVICE_MQTT_MESSAGE_BASE_URL_VERSION);
    }

    /**
     * Publishes a message to a device.
     *
     * @param domain    the domain of the message
     * @param command   the command of the message
     * @param data      the data of the message
     * @param deviceId  the ID of the device
     * @param version   the version of the message
     * @throws Exception if an error occurs while publishing the message
     */
    @SuppressWarnings("unchecked")
    public void publishMessage(ClientConfigEventIds domain, String command, Object data, String deviceId,
                               String version) {

        try {
            ObjectMapper mapper = new ObjectMapper();

            Config message = new Config();
            message.setCommand(command);
            message.setData(data);
            message.setDomain(domain.getValue());
            message.setVersion(version);

            String requestJson = mapper.writeValueAsString(message);

            HttpEntity<String> entity = new HttpEntity<>(requestJson, createHeaders());

            LOGGER.info("Mqtt publish entity {} ", entity);
            ResponseEntity<String> response = restTemplate.exchange(getDeviceMessageServiceUrl(deviceId),
                HttpMethod.POST, entity, String.class);

            if (response.getStatusCode().equals(HttpStatus.OK)) {
                LOGGER.info("Mqtt publish is successful! ");
            } else {
                LOGGER.error("Mqtt publish failed with response code: {}. Response is {}: ",
                    response.getStatusCodeValue(),
                    response.getBody() != null ? response.getBody() : "No Response Body received");
                throw new ApiTechnicalException(UNKNOWN_EXC_WHILE_PUBLISH_CONFIG);
            }

        } catch (Exception e) {
            LOGGER.error("DeviceMessage service error message: {}", e.getMessage());
            throw new ApiTechnicalException(UNKNOWN_EXC_WHILE_PUBLISH_CONFIG);

        }
    }

    /**
     * Creates the HTTP headers for the request.
     *
     * @return the HttpHeaders object with the necessary headers
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
        return headers;
    }

    /**
     * Creates the device message service URL for the given device ID.
     *
     * @param deviceId the ID of the device
     * @return the device message service URL
     */
    private String getDeviceMessageServiceUrl(String deviceId) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(baseUrl).append(Constants.URL_SEPARATOR).append(baseVersion).append(Constants.URL_SEPARATOR)
            .append(Constants.DEVICES).append(Constants.URL_SEPARATOR).append(deviceId)
            .append(Constants.URL_SEPARATOR).append(Constants.CONFIG);
        return urlBuilder.toString();
    }
}

