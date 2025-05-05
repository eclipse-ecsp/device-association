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
 * A utility class that holds constant values used throughout the application.
 */
public final class Constants {

    /**
     * The URL separator used in constructing URLs.
     */
    public static final String URL_SEPARATOR = "/";

    /**
     * The HTTP header name for specifying the content type of a request or response.
     */
    public static final String CONTENT_TYPE = "Content-Type";

    /**
     * The MIME type for JSON content.
     */
    public static final String APPLICATION_JSON = "application/json";

    /**
     * The HTTP header name for specifying the accepted response format.
     */
    public static final String ACCEPT = "accept";

    /**
     * The HTTP header name for authorization credentials.
     */
    public static final String HEADER_NAME_AUTHORIZATION = "Authorization";

    /**
     * The separator used to denote the start of query parameters in a URL.
     */
    public static final String QUERY_PARAM_SEPARATOR = "?";

    /**
     * The default sort order used in queries, set to descending order.
     */
    public static final String DEFAULT_SORT_ORDER = "sortOrder=DESC";

    /**
     * Private constructor to prevent instantiation of the Constants class.
     */
    private Constants() {
    }
}
