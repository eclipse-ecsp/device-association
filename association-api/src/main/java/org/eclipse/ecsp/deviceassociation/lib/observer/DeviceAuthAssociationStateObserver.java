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

package org.eclipse.ecsp.deviceassociation.lib.observer;

import jakarta.annotation.PostConstruct;
import org.eclipse.ecsp.auth.lib.rest.model.ActivationStateRequest;
import org.eclipse.ecsp.auth.lib.rest.model.DeactivationRequestData;
import org.eclipse.ecsp.common.config.EnvConfig;
import org.eclipse.ecsp.deviceassociation.lib.config.DeviceAssocationProperty;
import org.eclipse.ecsp.deviceassociation.lib.exception.ObserverMessageProcessFailureException;
import org.eclipse.ecsp.deviceassociation.lib.model.AssociationStatus;
import org.eclipse.ecsp.deviceassociation.lib.model.DeviceAssociation;
import org.eclipse.ecsp.services.clientlib.HcpRestClientLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

/**
 * This class is an implementation of the DeviceAssociationObserver interface and serves as an observer for device
 * authentication and association state changes.
 * It provides methods to handle state changes and perform deactivation of devices based on the association status.
 */
@Service
@DependsOn("deviceMessage")
public class DeviceAuthAssociationStateObserver implements DeviceAssociationObserver {
    private static final String HCP_USER = "HCP-User";

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceAuthAssociationStateObserver.class);
    private static final String USER_ID = "user-id";
    @Autowired
    private DeviceAssociationObservable observable;
    @Autowired
    private HcpRestClientLibrary hcpRestClientLibrary;
    @Autowired
    private EnvConfig<DeviceAssocationProperty> envConfig;

    /**
     * Initializes the observer by registering it with the observable.
     */
    @PostConstruct
    public void init() {
        observable.register(this);
    }

    /**
     * Called when the state of the device association changes.
     *
     * @param deviceAssociation the device association object
     * @throws ObserverMessageProcessFailureException if an error occurs while processing the message
     */
    @Override
    public void stateChanged(DeviceAssociation deviceAssociation) throws ObserverMessageProcessFailureException {
        LOGGER.debug("##stateChanged - START deviceAssociation: {} ", deviceAssociation);
        if (deviceAssociation == null) {
            return;
        }
        try {
            if (deviceAssociation.isDeviceAuthV2Deactivate()) {
                LOGGER.debug("## v2 version deactivate service");
                deactivateV2(deviceAssociation);
            } else if (deviceAssociation.getAssociationStatus().equals(AssociationStatus.DISASSOCIATED)
                && !deviceAssociation.isAuthsRequest()) {
                LOGGER.debug("v1 version deactivate service");
                deactivate(deviceAssociation);
            }
        } catch (Exception e) {
            LOGGER.error(
                    "exception occurred while trying to send notification to device auth. Request: {}",
                    deviceAssociation);
            throw new ObserverMessageProcessFailureException(e);
        }
    }

    /**
     * Deactivates the device association using the v1 version of the service.
     *
     * @param deviceAssociation the device association object
     * @throws ObserverMessageProcessFailureException if an error occurs while deactivating the device
     */
    private void deactivate(DeviceAssociation deviceAssociation) throws ObserverMessageProcessFailureException {
        String baseUrl = envConfig.getStringValue(DeviceAssocationProperty.SERVICE_AUTH_REST_URL_BASE);
        String deactivateUrl = envConfig.getStringValue(DeviceAssocationProperty.SERVICE_AUTH_REST_DEACTIVATE_DEVICE);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set(HCP_USER, deviceAssociation.getUserId());

        ResponseEntity<Object> responseEntity = hcpRestClientLibrary.doPost(baseUrl + deactivateUrl,
            httpHeaders,
            new ActivationStateRequest(deviceAssociation.getSerialNumber()), Object.class);
        if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            LOGGER.info("deactivation received 200 response {}", deviceAssociation);
        } else {
            throw new ObserverMessageProcessFailureException(
                String.valueOf(responseEntity.getStatusCode().value()));
        }
    }

    /**
     * Deactivates the device association using the v2 version of the service.
     *
     * @param deviceAssociation the device association object
     * @throws ObserverMessageProcessFailureException if an error occurs while deactivating the device
     */
    private void deactivateV2(DeviceAssociation deviceAssociation) throws ObserverMessageProcessFailureException {
        LOGGER.debug("## deactivateV2 - START deviceAssociation: {}", deviceAssociation);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set(USER_ID, deviceAssociation.getUserId());
        if (deviceAssociation.getFactoryId() <= 0) {
            throw new ObserverMessageProcessFailureException("Factory data ID cannot be null.");
        }
        String baseUrl = envConfig.getStringValue(DeviceAssocationProperty.SERVICE_AUTH_REST_URL_BASE);
        String deactivateUrl =
            envConfig.getStringValue(DeviceAssocationProperty.SERVICE_AUTH_REST_DEACTIVATE_V2_DEVICE);
        String deactivationUrl = baseUrl + deactivateUrl;
        LOGGER.debug("v2 De-activate url: {}", deactivationUrl);
        try {
            ResponseEntity<Object> responseEntity = hcpRestClientLibrary.doPost(deactivationUrl, httpHeaders,
                new DeactivationRequestData(String.valueOf(deviceAssociation.getFactoryId())), Object.class);
            if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                LOGGER.debug("## deactivation received 200 response: {}", deviceAssociation);
            } else {
                LOGGER.debug("## deactivation was failed");
                throw new ObserverMessageProcessFailureException(
                        String.valueOf(responseEntity.getStatusCode().value()));
            }
        } catch (HttpClientErrorException exp) {
            String response = exp.getResponseBodyAsString();
            throw new ObserverMessageProcessFailureException(response);
        } catch (Exception ex) {
            LOGGER.error("## Deactivation failed with exception");
            throw new ObserverMessageProcessFailureException(ex);
        }
        LOGGER.debug("## deactivateV2 - END");
    }
}
