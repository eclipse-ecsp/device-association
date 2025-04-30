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
import org.springframework.stereotype.Service;

/**
 * This interface represents an observable object in the device association system.
 * It allows observers to register and receive notifications when a device association occurs.
 */
@Service
public interface DeviceAssociationObservable {

    /**
     * Notifies the observers about a device association.
     *
     * @param deviceAssociation The device association information.
     * @throws ObserverMessageProcessFailureException If there is a failure in processing the observer message.
     */
    public void notify(DeviceAssociation deviceAssociation) throws ObserverMessageProcessFailureException;

    /**
     * Registers an observer to receive notifications about device associations.
     *
     * @param observer The observer to be registered.
     */
    public void register(DeviceAssociationObserver observer);
}
