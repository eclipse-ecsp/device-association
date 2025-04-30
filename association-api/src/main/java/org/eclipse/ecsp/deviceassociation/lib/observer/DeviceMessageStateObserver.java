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
import org.eclipse.ecsp.common.config.EnvConfig;
import org.eclipse.ecsp.deviceassociation.lib.config.DeviceAssocationProperty;
import org.eclipse.ecsp.deviceassociation.lib.exception.ObserverMessageProcessFailureException;
import org.eclipse.ecsp.deviceassociation.lib.model.AssociationStatus;
import org.eclipse.ecsp.deviceassociation.lib.model.ClientConfigEventIds;
import org.eclipse.ecsp.deviceassociation.lib.model.DeviceAssociation;
import org.eclipse.ecsp.deviceassociation.lib.model.MqttCommand;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceMessageService;
import org.eclipse.ecsp.deviceassociation.lib.service.EventMetadataConstants;
import org.eclipse.ecsp.deviceassociation.lib.service.MessageConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

/**
 * The DeviceMessageStateObserver class is responsible for observing the state changes of device associations
 * and sending messages to the device message service based on the association status.
 */
@Service("deviceMessage")
@DependsOn("kafkaDeviceNotif")
public class DeviceMessageStateObserver implements DeviceAssociationObserver {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceMessageStateObserver.class);

    @Autowired
    private DeviceAssociationObservable observable;

    @Autowired
    private DeviceMessageService deviceMessageService;

    @Autowired
    private EnvConfig<DeviceAssocationProperty> envConfig;

    private boolean deviceMessageEnabled;

    /**
     * Initializes the DeviceMessageStateObserver.
     * This method is annotated with @PostConstruct, indicating that it should be called after the bean has been
     * constructed and all dependencies have been injected.
     * It retrieves the value of the DEVICE_MESSAGE_ENABLED property from the environment configuration and registers
     * the observer if the value is true.
     */
    @PostConstruct
    public void init() {
        deviceMessageEnabled = envConfig.getBooleanValue(DeviceAssocationProperty.DEVICE_MESSAGE_ENABLED);
        if (deviceMessageEnabled) {
            observable.register(this);
        }
    }

    /**
     * This method is called when the state of a device association changes.
     * If device message is enabled, it publishes a message to the device-message service
     * based on the association status and terminate for value of the device association.
     *
     * @param deviceAssociation The device association object representing the changed state.
     * @throws ObserverMessageProcessFailureException If an error occurs while processing the message.
     */
    @Override
    public void stateChanged(DeviceAssociation deviceAssociation) throws ObserverMessageProcessFailureException {
        if (deviceMessageEnabled) {

            if (deviceAssociation == null) {
                return;
            }

            LOGGER.info("stateChanged - : {}", deviceAssociation);
            try {
                if (deviceAssociation.getAssociationStatus().equals(AssociationStatus.DISASSOCIATED)) {

                    ClientConfigEventIds eventName;
                    if (MessageConstants.TERMINATE_REQUIRED_FOR.equals(deviceAssociation.getTerminateFor())) {
                        eventName = ClientConfigEventIds.WIPEDATA;
                    } else {
                        eventName = ClientConfigEventIds.DISASSOCIATION;
                    }
                    LOGGER.debug("Publishing {} event to device-message", eventName.getValue());
                    deviceMessageService.publishMessage(eventName, MqttCommand.PUT.value(), null,
                        deviceAssociation.getHarmanId(), EventMetadataConstants.VERSION_1_0);

                }

            } catch (Exception e) {
                LOGGER.error(
                    "exception occured while trying to send the message to notificaiton service about disassociation");
                throw new ObserverMessageProcessFailureException(e.getMessage());
            }
        }
    }

}
