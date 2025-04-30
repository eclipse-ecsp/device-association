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

/**
 * The {@code DeviceAssociationDetailsValidator} class provides utility methods 
 * to validate device-related input fields such as IMEI, serial number, and device ID.
 * It ensures that the input data conforms to specific formats and throws an 
 * {@link IllegalArgumentException} if the validation fails.
 * 
 * <p>Validation rules:
 * <ul>
 *   <li>IMEI: Must be numeric.</li>
 *   <li>Serial Number: Must be alphanumeric.</li>
 *   <li>Device ID: Must be alphanumeric.</li>
 * </ul>
 * 
 * <p>This class is designed to be used as a utility class and cannot be instantiated.
 * All methods are static.
 * 
 * <p>Example usage:
 * <pre>
 * {@code
 * DeviceAssociationDetailsValidator.validateImeiRequestData("123456789012345");
 * DeviceAssociationDetailsValidator.validateSerialNumberRequestData("SN12345");
 * DeviceAssociationDetailsValidator.validateDeviceIdRequestData("Device123");
 * }
 * </pre>
 *
 * @author Akshay
 * @version 1.0
 */
public class DeviceAssociationDetailsValidator {

    private static final String INVALID_IMEI_VALUE = "IMEI should be numeric";

    private static final String INVALID_SERIAL_NUMBER_VALUE = "Serial number should be alphanumeric";

    private static final String INVALID_DEVICE_ID_VALUE = "Device Id should be alphanumeric";

    private DeviceAssociationDetailsValidator() {
    }

    /**
     * Validates whether the given IMEI (International Mobile Equipment Identity) is valid.
     *
     * <p>
     * An IMEI is considered valid if it is not blank and contains only numeric characters.
     * </p>
     *
     * @param imei the IMEI string to validate
     * @return {@code true} if the IMEI is valid or blank, {@code false} if it contains non-numeric characters
     */
    static boolean isValidImei(String imei) {
        String numberOnlyRegex = "[0-9]+";
        if (StringUtils.isNotBlank(imei)) {
            if (!imei.matches(numberOnlyRegex)) {
                return false;
            }
            return true;
        }
        return true;
    }

    /**
     * Validates if the given input string contains only alphanumeric characters.
     *
     * @param inputField the input string to validate
     * @return {@code true} if the input string is alphanumeric or blank; 
     *         {@code false} if the input string contains non-alphanumeric characters
     */
    static boolean isValidAlphaNumberic(String inputField) {
        String alphaNumbericOnlyRegex = "[0-9a-zA-Z]+";
        if (StringUtils.isNotBlank(inputField)) {
            if (!inputField.matches(alphaNumbericOnlyRegex)) {
                return false;
            }
            return true;
        }
        return true;
    }

    /**
     * Validates the IMEI request data.
     *
     * @param imei the IMEI string to validate
     * @throws IllegalArgumentException if the IMEI is invalid
     */
    public static void validateImeiRequestData(String imei) {
        if (!isValidImei(imei)) {
            throw new IllegalArgumentException(INVALID_IMEI_VALUE);
        }
    }

    /**
     * Validates the serial number request data.
     *
     * @param serialNumber the serial number string to validate
     * @throws IllegalArgumentException if the serial number is invalid
     */
    public static void validateSerialNumberRequestData(String serialNumber) {
        if (!isValidAlphaNumberic(serialNumber)) {
            throw new IllegalArgumentException(INVALID_SERIAL_NUMBER_VALUE);
        }
    }

    /**
     * Validates the device ID request data.
     *
     * @param deviceId the device ID string to validate
     * @throws IllegalArgumentException if the device ID is invalid
     */
    public static void validateDeviceIdRequestData(String deviceId) {
        if (!isValidAlphaNumberic(deviceId)) {
            throw new IllegalArgumentException(INVALID_DEVICE_ID_VALUE);
        }
    }
}
