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

package org.eclipse.ecsp.deviceassociation.springmvc.rest.support;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * The DeviceAssociationHistoryValidator class provides validation methods for various request data related to device
 * association history.
 * It validates IMEI, page, size, sortby, and orderby values to ensure they meet the required criteria.
 */
public class DeviceAssociationHistoryValidator {

    private static final String INVALID_IMEI_VALUE =
        "Failed to retrieve device based user association history details, Root Cause: IMEI should be numeric";
    private static final String INVALID_PAGE_VALUE =
        "Failed to retrieve device based user association history details, Root Cause: Page must be numeric and "
            + "greater than 0";
    private static final String INVALID_SIZE_VALUE =
        "Failed to retrieve device based user association history details, Root Cause: size must be numeric and"
            + " greater than 0";
    private static final String INVALID_ORDERBY_VALUE =
        "Failed to retrieve device based user association history details, Root Cause: orderby must be asc or desc";
    private static final String INVALID_SORTBY_FIELD_RESPONSE =
        "Failed to retrieve device based user association history details, Root Cause: sortby field should contain one"
            + " of these allowed values (userid, associationstatus)";

    /**
     * Private constructor for the DeviceAssociationHistoryValidator class.
     * This class provides validation for device association history.
     */
    private DeviceAssociationHistoryValidator() {

    }

    /**
     * Checks if the given IMEI is valid.
     *
     * @param imei the IMEI to validate
     * @return true if the IMEI is valid, false otherwise
     */
    static boolean isValidImei(String imei) {
        String numberOnlyRegex = "[0-9]+";
        if (StringUtils.isNotEmpty(imei)) {

            if (!imei.matches(numberOnlyRegex)) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Validates the page and size values.
     *
     * @param arg the value to validate
     * @return true if the value is valid, false otherwise
     */
    static boolean isValidRequestData(String arg) {

        // If the input is null, then it is a valid scenario, so returning true.

        if (arg == null) {
            return true;
        } else if (!StringUtils.isNumeric(arg)) {
            return false;
        } else if (StringUtils.isNumeric(arg) && Integer.parseInt(arg) <= 0) {
            return false;
        }
        return true;
    }

    /**
     * Validates the IMEI request data.
     *
     * @param imei the IMEI to validate
     * @throws IllegalArgumentException if the IMEI is invalid
     */
    public static void validateImeiRequestData(String imei) {
        if (!isValidImei(imei)) {
            throw new IllegalArgumentException(INVALID_IMEI_VALUE);
        }
    }

    /**
     * Validates the page request data.
     *
     * @param page the page to validate
     * @throws IllegalArgumentException if the page is invalid
     */
    public static void validatePageRequestData(String page) {
        if (!isValidRequestData(page)) {
            throw new IllegalArgumentException(INVALID_PAGE_VALUE);
        }
    }

    /**
     * Validates the size request data.
     *
     * @param size the size to validate
     * @throws IllegalArgumentException if the size is invalid
     */
    public static void validateSizeRequestData(String size) {
        if (!isValidRequestData(size)) {
            throw new IllegalArgumentException(INVALID_SIZE_VALUE);
        }
    }

    /**
     * Validates the sortby and orderby request data.
     *
     * @param sortby  the sortby value to validate
     * @param orderby the orderby value to validate
     * @throws IllegalArgumentException if the sortby or orderby is invalid
     */
    public static void validateSortAndOrderRequestData(String sortby, String orderby) {
        if (StringUtils.isNotEmpty(sortby) && !AllowedSortBy.contains(sortby.toLowerCase())) {
            throw new IllegalArgumentException(INVALID_SORTBY_FIELD_RESPONSE);
        }
        if (StringUtils.isNotEmpty(orderby)
            && !(orderby.equalsIgnoreCase("asc") || orderby.equalsIgnoreCase("desc"))) {
            throw new IllegalArgumentException(INVALID_ORDERBY_VALUE);
        }
    }

    /**
     * Enum representing the allowed sort options for device association history.
     */
    private enum AllowedSortBy {
        USER_ID("userid"),
        CREATED_DATE("associationstatus");

        private static Set<String> values = new HashSet<>();

        static {
            for (AllowedSortBy allowed : AllowedSortBy.values()) {
                values.add(allowed.field);
            }
        }

        private String field;

        /**
         * Constructs an AllowedSortBy enum constant with the specified field value.
         *
         * @param field the field value associated with the enum constant
         */
        AllowedSortBy(String field) {
            this.field = field;
        }

        /**
         * Checks if the specified value is contained in the set of allowed sort options.
         *
         * @param value the value to check
         * @return true if the value is allowed, false otherwise
         */
        public static boolean contains(String value) {
            return values.contains(value);
        }
    }
}
