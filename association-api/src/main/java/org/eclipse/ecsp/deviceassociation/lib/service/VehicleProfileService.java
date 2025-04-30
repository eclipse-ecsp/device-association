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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.ecsp.common.ResponsePayload;
import org.eclipse.ecsp.common.config.EnvConfig;
import org.eclipse.ecsp.deviceassociation.lib.config.DeviceAssocationProperty;
import org.eclipse.ecsp.deviceassociation.lib.dao.DeviceAssociationDao;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociationDetailsRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociationDetailsResponse;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceStatusRequest;
import org.eclipse.ecsp.deviceassociation.lib.util.Pair;
import org.eclipse.ecsp.exception.shared.ApiTechnicalException;
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
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.VIN_DECODE_API_FAILURE;

/**
 * This class represents a service for decoding vehicle profiles.
 * It provides methods for decoding VINs and retrieving VIN decode responses.
 */
@Service
public class VehicleProfileService {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(VehicleProfileService.class);

    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Autowired
    protected DeviceAssociationDao deviceAssociationDao;
    @Autowired
    protected EnvConfig<DeviceAssocationProperty> envConfig;
    @Autowired
    protected DeviceAssociationService deviceAssociationService;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${vehicle_profile_base_url}")
    private String baseUrl;
    @Value("${vehicle_profile_base_url_version}")
    private String baseVersion;

    /**
     * Decodes the given VIN (Vehicle Identification Number) and retrieves the model code and model name associated
     * with it.
     *
     * @param vin The VIN to be decoded.
     * @return A Pair object containing the model code and model name retrieved from the VIN decoding process.
     *         Returns null if the decoding process fails.
     */
    public Pair<String, String> decodeVin(String vin) {
        LOGGER.debug("## decodeVin - START");
        HttpEntity<String> entity = new HttpEntity<>(createHeaders());
        String modelCode;
        String modelName;
        String url = getDecodeVinUrl(vin);
        LOGGER.debug("## decode vin endpoint: {} ", url);
        try {
            ResponseEntity<RestResponse<String>> response = restTemplate.exchange(url, HttpMethod.POST, entity,
                new ParameterizedTypeReference<RestResponse<String>>() {
                });
            RestResponse<String> body = response.getBody();
            String decodedValue = Objects.requireNonNull(body != null ? body : new RestResponse<String>()).getData();
            VinDecodeResponse vinDecodeResponse = MAPPER.readValue(decodedValue, VinDecodeResponse.class);
            modelCode = vinDecodeResponse.getModelCode();
            modelName = vinDecodeResponse.getModelName();
            LOGGER.debug("## ModelCode: {}, ModelName: {} after vin decoding", modelCode, modelName);
            return new Pair<>(modelCode, modelName);
        } catch (Exception e) {
            //Note: Don't propagate exception back to caller to avoid inconsistency in database tables
            LOGGER.error(
                "YOU CAN IGNORE THIS EXCEPTION as we bypass decoding error - Vin decoding failed, cause: {},"
                    + " Error Trace: "
                    + ExceptionUtils.getRootCauseMessage(e), e);
        }
        LOGGER.debug("## decodeVin - END");
        return null;
    }

    /**
     * Decodes the given VIN (Vehicle Identification Number) based on the specified decode type.
     *
     * @param vin        The VIN to be decoded.
     * @param decodeType The type of decoding to be performed on the VIN.
     * @return The decoded response containing information about the vehicle.
     * @throws ApiTechnicalException If an error occurs while decoding the VIN.
     */
    public VinDecodeResponse decodeVinByType(String vin, String decodeType) {
        LOGGER.info("## decodeVin - START");
        HttpEntity<String> entity = new HttpEntity<>(createHeaders());
        String url = getDecodeVinUrl(vin, decodeType);
        LOGGER.info("## decode vin endpoint: {} ", url);
        VinDecodeResponse vinDecodeResponse;

        ResponseEntity<RestResponse<String>> response;
        try {
            response = restTemplate.exchange(url, HttpMethod.POST, entity,
                new ParameterizedTypeReference<RestResponse<String>>() {
                });
            RestResponse<String> body = response.getBody();
            String decodedValue = Objects.requireNonNull(body != null ? body : new RestResponse<String>()).getData();
            vinDecodeResponse = MAPPER.readValue(decodedValue, VinDecodeResponse.class);
        } catch (Exception e) {
            throw new ApiTechnicalException(VIN_DECODE_API_FAILURE.getCode(),
                VIN_DECODE_API_FAILURE.getMessage(),
                VIN_DECODE_API_FAILURE.getGeneralMessage(), e);
        }
        LOGGER.debug("#VinDecode Response {}", vinDecodeResponse);
        return vinDecodeResponse;
    }

    /**
     * Returns the URL for decoding a VIN (Vehicle Identification Number).
     *
     * @param vin The VIN to be decoded.
     * @return The URL for decoding the VIN.
     */
    private String getDecodeVinUrl(String vin) {
        return baseUrl + Constants.URL_SEPARATOR + baseVersion + Constants.URL_SEPARATOR
            + Constants.VINS + Constants.URL_SEPARATOR + vin
            + Constants.URL_SEPARATOR + Constants.DECODE
            + Constants.QUESTION_MARK + Constants.TYPE + Constants.EQUALS + "CODE_VALUE";
    }

    /**
     * Returns the URL for decoding a VIN (Vehicle Identification Number) based on the provided VIN and type.
     *
     * @param vin  the VIN to be decoded
     * @param type the type of decoding to be performed
     * @return the URL for decoding the VIN
     */
    private String getDecodeVinUrl(String vin, String type) {
        return baseUrl + Constants.URL_SEPARATOR + baseVersion + Constants.URL_SEPARATOR
            + Constants.VINS + Constants.URL_SEPARATOR + vin
            + Constants.URL_SEPARATOR + Constants.DECODE
            + Constants.QUESTION_MARK + Constants.TYPE + Constants.EQUALS + type;
    }

    /**
     * Deletes a vehicle profile using the provided deleteVehicleProfileUrl.
     *
     * @param deleteVehicleProfileUrl The URL for deleting the vehicle profile.
     */
    public boolean deleteVehicleProfile(String deleteVehicleProfileUrl) {
        boolean isDeleted = false;
        LOGGER.debug("## Calling Vehicle Profile DELETE API");
        ResponseEntity<ResponsePayload> responseVp = vehicleProfileTerminate(deleteVehicleProfileUrl);
        Boolean data = responseVp.getBody() != null ? ((Boolean) responseVp.getBody().getData()) : null;
        if (Boolean.TRUE.equals(data)) {
            isDeleted = true;
            LOGGER.info("Successfully deleted the vehicle profile with response code: {}. Response message: {} , "
                    + "response data: {}", responseVp.getStatusCodeValue(),
                responseVp.getBody().getMessage(), responseVp.getBody().getData());
        } else {
            LOGGER.error("Vehicle profile deletion failed with response code: {}. Response message: {} , "
                    + "response data: {}", responseVp.getStatusCodeValue(),
                responseVp.getBody().getMessage(), responseVp.getBody().getData());
        }
        LOGGER.debug("## End of call to vehicle profile delete API");
        return isDeleted;
    }

    /**
     * Fetches the URL for deleting the vehicle profile based on the provided device status request.
     *
     * @param deviceStatusRequest The device status request containing the device ID, serial number, and IMEI.
     * @return The URL for deleting the vehicle profile.
     */
    public String fetchVehicleProfileUrl(DeviceStatusRequest deviceStatusRequest) {
        String deviceId = deviceStatusRequest.getDeviceId();
        String serialNumber = deviceStatusRequest.getSerialNumber();
        String imei = deviceStatusRequest.getImei();
        String deleteVehicleProfileUrl;
        LOGGER.debug("Fetch Delete vehicle profile URL");
        if (deviceId != null && StringUtils.isNotEmpty(deviceId)) {
            deleteVehicleProfileUrl = getVehicleProfileUrl(deviceId);
        } else {
            AssociationDetailsRequest associationDetailsRequest = new AssociationDetailsRequest();
            associationDetailsRequest.setImei(imei);
            associationDetailsRequest.setSerialNumber(serialNumber);
            associationDetailsRequest.setDeviceId(deviceId);
            AssociationDetailsResponse associationDetailsResponse =
                deviceAssociationService.getAssociationDetails(associationDetailsRequest);
            deviceId = associationDetailsResponse.getDeviceDetail().getHarmanId();
            deleteVehicleProfileUrl = getVehicleProfileUrl(deviceId);
        }
        return deleteVehicleProfileUrl;
    }

    /**
     * Returns the URL for deleting the vehicle profile associated with the given device ID.
     *
     * @param deviceId The ID of the device.
     * @return The URL for deleting the vehicle profile.
     */
    private String getVehicleProfileUrl(String deviceId) {
        String vpBaseUrl = envConfig.getStringValue(DeviceAssocationProperty.VEHICLE_PROFILE_BASE_URL);
        String vpBaseUrlVersion = envConfig.getStringValue(DeviceAssocationProperty
            .VEHICLE_PROFILE_TERMINATE_BASE_URL_VERSION);
        String vpTerminate = envConfig.getStringValue(DeviceAssocationProperty.VEHICLE_PROFILE_TERMINATE);
        String vpDeleteUrl = vpBaseUrl + Constants.URL_SEPARATOR + vpBaseUrlVersion + vpTerminate
            + Constants.QUESTION_MARK + Constants.CLIENT_ID + Constants.EQUALS + deviceId;
        return vpDeleteUrl;
    }

    /**
     * Terminates a vehicle profile by sending a DELETE request to the specified URL.
     *
     * @param deleteVehicleProfileUrl The URL to send the DELETE request to.
     * @return The response entity containing the result of the DELETE request.
     */
    public ResponseEntity<ResponsePayload> vehicleProfileTerminate(String deleteVehicleProfileUrl) {
        LOGGER.debug("Vehicle Profile DELETE API URL: {} ", deleteVehicleProfileUrl);
        HttpEntity<String> entity = new HttpEntity<>(createHeaders());
        ResponseEntity<ResponsePayload> response = restTemplate.exchange(deleteVehicleProfileUrl, HttpMethod.DELETE,
            entity, ResponsePayload.class);
        return response;
    }

    /**
     * Creates and returns the HttpHeaders object with the necessary headers for the request.
     *
     * @return The HttpHeaders object with the necessary headers.
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
        headers.add(Constants.ACCEPT, Constants.APPLICATION_JSON);
        return headers;
    }

}