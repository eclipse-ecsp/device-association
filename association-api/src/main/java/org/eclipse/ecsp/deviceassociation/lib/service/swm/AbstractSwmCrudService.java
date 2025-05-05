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

package org.eclipse.ecsp.deviceassociation.lib.service.swm;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.ecsp.common.config.EnvConfig;
import org.eclipse.ecsp.deviceassociation.lib.config.DeviceAssocationProperty;
import org.eclipse.ecsp.deviceassociation.lib.model.swm.SwmRequest;
import org.eclipse.ecsp.deviceassociation.lib.model.swm.SwmVinRequest;
import org.eclipse.ecsp.deviceassociation.lib.service.Constants;
import org.eclipse.ecsp.exception.shared.ApiTechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.SWM_SESSION_ID_NULL;

/**
 * This abstract class provides a base implementation for the SWM CRUD service.
 * It contains common methods and properties used by the SWM CRUD service classes.
 *
 * @param <I> the type of the identifier used in the CRUD operations
 */
public abstract class AbstractSwmCrudService<I> implements IswmCrudService<I> {
    /**
     * The constant ID represents the key for the vehicle ID in the response.
     */
    public static final String ID = "id";
    /**
     * The constant REPRESENTATION_OBJECTS represents the key for the representation objects in the response.
     */
    public static final String REPRESENTATION_OBJECTS = "representationObjects";

    /**
     * Logger instance for logging messages related to the AbstractSwmCrudService class.
     * This logger is used to log information, warnings, and errors for debugging and
     * monitoring purposes.
     */
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractSwmCrudService.class);
    /**
     * The URL for the SWM login API.
     */
    protected String loginUrl;
    /**
     * The URL for creating a vehicle in the SWM system.
     */
    protected String createVehicleUrl;
    /**
     * The URL for updating a vehicle in the SWM system.
     */
    protected String updateVehicleUrl;
    /**
     * The URL for deleting a vehicle in the SWM system.
     */
    protected String deleteVehicleUrl;
    /**
     * The URL for retrieving vehicle models from the SWM system.
     */
    protected String vehicleModelsUrl;
    /**
     * The URL for retrieving vehicles from the SWM system.
     */
    protected String vehiclesUrl;
    
    /**
     * The username used for SWM (Software Management) operations.
     * This field is protected to allow access within the class and its subclasses.
     */
    protected String swmUserName;
    /**
     * The password used for SWM (Software Management) operations.
     * This field is protected to allow access within the class and its subclasses.
     */
    protected String swmPassword;
    /**
     * The domain associated with the SWM (Software Management) service.
     * This field is used to specify the domain context for operations
     * performed by the service.
     */
    protected String swmDomain;
    /**
     * The identifier for the vehicle model associated with this service.
     * This field is used to reference a specific vehicle model in the system.
     */
    protected String vehicleModelId;
    /**
     * The environment configuration for device association properties.
     * This is an autowired dependency that provides access to the configuration
     * settings specific to device association.
     *
     * @see EnvConfig
     * @see DeviceAssocationProperty
     */
    @Autowired
    protected EnvConfig<DeviceAssocationProperty> envConfig;
    /**
     * The {@link RestTemplate} instance used for making RESTful web service calls.
     * This is a Spring-managed bean that is automatically injected using the 
     * {@code @Autowired} annotation.
     */
    @Autowired
    protected RestTemplate restTemplate;

    /**
     * Finds the session ID for the device association service.
     * If a session ID is already available and not stale, it is returned.
     * Otherwise, a new session ID is generated and stored in the session ID map.
     *
     * @return The session ID for the device association service.
     */
    protected String findSessionId() {
        return generateSwmSessionId();
    }

    /**
     * Generates a SWM session ID by making a login API call to the SWM server.
     *
     * @return The generated SWM session ID.
     * @throws ApiTechnicalException If there is an error while getting the session ID.
     */
    private String generateSwmSessionId() {
        HttpEntity<String> entity = new HttpEntity<>(
            "&userName=" + swmUserName + "&password=" + swmPassword + "&domain=" + swmDomain + "&");
        String sessionId;
        try {
            LOGGER.debug("## Hitting swm login api: {} ", loginUrl);
            ResponseEntity<String> response = restTemplate.exchange(loginUrl, HttpMethod.POST, entity, String.class);
            LOGGER.debug("## Response code: {}, from: {}", response.getStatusCode(), loginUrl);
            if (response.getStatusCode() == HttpStatus.OK) {
                LOGGER.debug("## Got success from Login API");
                HashMap<String, Object> responseMap = new ObjectMapper().readValue(response.getBody(), HashMap.class);
                sessionId = responseMap.get("sessionId").toString();
                LOGGER.debug("## Session id from SWM: {}", sessionId);
            } else {
                LOGGER.error("## Error while getting session id , http status code :{} ",
                    response.getStatusCode().value());
                throw new ApiTechnicalException(SWM_SESSION_ID_NULL.getCode(), SWM_SESSION_ID_NULL.getMessage(),
                    SWM_SESSION_ID_NULL.getGeneralMessage());
            }
        } catch (RuntimeException e) { // 2.33 Release - Sonar REC_CATCH_EXCEPTION code smell fix
            LOGGER.error("SWM session id is null. Error message: {} ", e.getMessage());
            throw new ApiTechnicalException(SWM_SESSION_ID_NULL.getCode(), SWM_SESSION_ID_NULL.getMessage(),
                SWM_SESSION_ID_NULL.getGeneralMessage());
        } catch (Exception e) {
            throw new ApiTechnicalException(SWM_SESSION_ID_NULL.getCode(), SWM_SESSION_ID_NULL.getMessage(),
                SWM_SESSION_ID_NULL.getGeneralMessage());
        }
        return sessionId;
    }

    /**
     * Calls the SWM Vehicle Model API to retrieve vehicle models.
     *
     * @param headers the HttpHeaders containing any necessary request headers
     * @return a ResponseEntity containing the response from the API
     */
    protected ResponseEntity<String> callSwmVehicleModelApi(HttpHeaders headers) {
        return restTemplate.exchange(vehicleModelsUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);
    }

    /**
     * Finds the ID to delete and update the vehicle based on the given SwmRequest.
     *
     * @param swmRequest The SwmRequest object containing the necessary information.
     * @return The ID of the vehicle to delete and update, or null if not found.
     * @throws IOException If an I/O error occurs while making the request.
     */
    protected String findIdToDeleteAndUpdateVehicle(SwmRequest swmRequest) throws IOException {
        LOGGER.debug("## findIdToDeleteAndUpdateVehicle - START swmRequest: {}", swmRequest);
        SwmVinRequest swmVinRequest = (SwmVinRequest) swmRequest;
        HttpEntity<SwmVinRequest> entity = new HttpEntity<>(swmVinRequest, createHeaders(findSessionId()));
        ResponseEntity<String> response = restTemplate.exchange(vehiclesUrl, HttpMethod.POST, entity, String.class);
        HttpStatusCode statusCode = response.getStatusCode();
        LOGGER.debug("## Response code: {}, from: {}", statusCode, createVehicleUrl);
        String id;
        if (statusCode.value() == HttpStatus.OK.value()) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            String res = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            HashMap<String, Object> responseMap = objectMapper.readValue(res, HashMap.class);
            List<Object> representationObjects = (List) responseMap.get(REPRESENTATION_OBJECTS);
            Map<String, Object> map = (Map) representationObjects.get(0);
            id = (String) map.get(ID);
        } else {
            LOGGER.warn("## Unable to find id from vehiclesUrl: {} from SWM, response StatusCode: {}", vehiclesUrl,
                statusCode.value());
            id = null;
        }
        LOGGER.debug("## findIdToDeleteAndUpdateVehicle - END id: {}", id);
        return id;
    }

    /**
     * Creates and returns HttpHeaders object with the specified session ID.
     *
     * @param sessionId the session ID to be added to the headers
     * @return the HttpHeaders object with the specified session ID
     */
    protected HttpHeaders createHeaders(String sessionId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(Constants.HEADER_CONTENT_TYPE_KEY, Constants.APPLICATION_JSON);
        headers.add(Constants.SESSIONID, sessionId);
        return headers;
    }
}
