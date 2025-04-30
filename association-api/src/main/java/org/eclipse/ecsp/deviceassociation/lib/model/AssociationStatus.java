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

package org.eclipse.ecsp.deviceassociation.lib.model;

/**
 * Represents the status of an association between a device and a vehicle.
 */
public enum AssociationStatus {

    ASSOCIATED("VehicleAssociation"),
    ASSOCIATION_INITIATED("ASSOCIATION_INITIATED"),
    DISASSOCIATED("VehicleDisAssociation"),
    ASSOCIATION_FAILED("ASSOCIATION_FAILED"),
    SUSPENDED("SUSPENDED");
    private String notificationEventName;

    /**
     * Constructs an AssociationStatus enum constant with the specified notification event name.
     *
     * @param notificationEventName the notification event name associated with the status
     */
    private AssociationStatus(String notificationEventName) {
        this.notificationEventName = notificationEventName;
    }

    /**
     * Returns the notification event name associated with the status.
     *
     * @return the notification event name
     */
    public String getNotificationEventName() {
        return notificationEventName;
    }

}
