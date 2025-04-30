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

import org.eclipse.ecsp.deviceassociation.lib.exception.ObserverMessageProcessFailureException;
import org.eclipse.ecsp.deviceassociation.lib.model.DeviceAssociation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the default implementation of the DeviceAssociationObservable interface.
 * It is responsible for notifying registered observers when a device association event occurs.
 */
@Service
public class DefaultDeviceAssociationObservable implements DeviceAssociationObservable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDeviceAssociationObservable.class);
    private List<DeviceAssociationObserver> observers = new ArrayList<>();

    /**
     * Notifies all registered observers about the changes in device association.
     *
     * @param deviceAssociation The updated device association information.
     * @throws ObserverMessageProcessFailureException If there is a failure in processing the observer message.
     */
    @Override
    public void notify(DeviceAssociation deviceAssociation) throws ObserverMessageProcessFailureException {
        for (DeviceAssociationObserver observer : observers) {
            observer.stateChanged(deviceAssociation);
        }
    }

    /**
     * Registers an observer to receive notifications about changes in device association.
     *
     * @param observer The observer to be registered.
     */
    @Override
    public void register(DeviceAssociationObserver observer) {
        LOGGER.info("observer registration request {}", observer);
        if (observer != null) {
            observers.add(observer);
        }
    }
}
