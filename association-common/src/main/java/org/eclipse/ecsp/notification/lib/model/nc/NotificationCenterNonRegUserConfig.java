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

package org.eclipse.ecsp.notification.lib.model.nc;

import java.util.List;

/**
 * Represents the configuration for a non-registered user in the notification center.
 */
public class NotificationCenterNonRegUserConfig {
    private String notificationId;
    private String version;
    private List<Recipient> recipients;

    /**
     * Default constructor.
     */
    public NotificationCenterNonRegUserConfig() {
        super();
    }

    /**
     * Constructs a new NotificationCenterNonRegUserConfig object with the specified notification ID, version, and
     * recipients.
     *
     * @param notificationId The ID of the notification.
     * @param version The version of the notification.
     * @param recipients The list of recipients for the notification.
     */
    public NotificationCenterNonRegUserConfig(String notificationId, String version, List<Recipient> recipients) {
        super();
        this.notificationId = notificationId;
        this.version = version;
        this.recipients = recipients;
    }

    /**
     * Returns a string representation of the NotificationCenterNonRegUserConfig object.
     *
     * @return The string representation of the object.
     */
    @Override
    public String toString() {
        return "NotificationCenterConfig [notificationId=" + notificationId + ", version=" + version + ", recipients="
            + recipients + "]";
    }

    /**
     * Gets the notification ID.
     *
     * @return The notification ID.
     */
    public String getNotificationId() {
        return notificationId;
    }

    /**
     * Sets the notification ID.
     *
     * @param notificationId The notification ID to set.
     */
    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    /**
     * Gets the version.
     *
     * @return The version.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the version.
     *
     * @param version The version to set.
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Gets the list of recipients.
     *
     * @return The list of recipients.
     */
    public List<Recipient> getRecipients() {
        return recipients;
    }

    /**
     * Sets the list of recipients.
     *
     * @param recipients The list of recipients to set.
     */
    public void setRecipients(List<Recipient> recipients) {
        this.recipients = recipients;
    }
}
