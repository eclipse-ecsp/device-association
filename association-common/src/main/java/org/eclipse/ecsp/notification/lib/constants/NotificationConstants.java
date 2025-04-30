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

package org.eclipse.ecsp.notification.lib.constants;

/**
 * The {@code NotificationConstants} class contains constants related to notifications.
 * These constants include URLs, default values, and header names used in the notification system.
 */
public final class NotificationConstants {
    public static final String URL_SEPARATOR = "/";
    public static final String GET_USER_PROFILE_API = "/v1/users/profile";
    public static final String POST_NOTIFICATION_NON_REG_USER_API = "/v1/notifications/nonRegisteredUsers";
    public static final String DEFAULT_LOCALE = "en_US";
    public static final String DEFAULT_BRAND = "default";
    public static final String API_VERSION_V1 = "1.0";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";
    public static final String USERID = "userId";

    /**
     * Private constructor to prevent instantiation of the {@code NotificationConstants} class.
     */
    private NotificationConstants() {
    }
}
