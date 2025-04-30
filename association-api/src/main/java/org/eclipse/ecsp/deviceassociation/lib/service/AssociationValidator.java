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

package org.eclipse.ecsp.deviceassociation.lib.service;

import org.eclipse.ecsp.exception.shared.ApiValidationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.DEVICE_INFO_SAVE_SIZE_VALIDATION_FAILED;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.DEVICE_INFO_SAVE_VALIDATION_FAILED;

/**
 * The AssociationValidator class provides methods for validating associations and device information.
 */
public class AssociationValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(AssociationValidator.class);

    /**
     * The `AssociationValidator` class provides utility methods for validating associations.
     */
    private AssociationValidator() {

    }

    /**
     * Validates if the given parameter is supported.
     *
     * @param param           The parameter to be validated.
     * @param supportedParams An array of supported parameters.
     * @return true if the parameter is supported, false otherwise.
     */
    public static boolean validateSupportedParam(String param, String[] supportedParams) {
        for (String type : supportedParams) {
            if (type.equals(param)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validates if the given parameters are supported.
     *
     * @param params           The list of parameters to be validated.
     * @param supportedParams  An array of supported parameters.
     * @throws ApiValidationFailedException if any of the parameters are not supported.
     */
    public static void validateSupportedParam(List<String> params, String[] supportedParams) {
        boolean match;
        for (String param : params) {
            match = false;
            for (String type : supportedParams) {
                if (type.equals(param)) {
                    match = true;
                    break;
                }
            }
            if (match == false) {
                LOGGER.error("Validation for allowed name is failed for param:{}", param);
                throw new ApiValidationFailedException(DEVICE_INFO_SAVE_VALIDATION_FAILED.getCode(),
                    DEVICE_INFO_SAVE_VALIDATION_FAILED.getMessage(),
                    DEVICE_INFO_SAVE_VALIDATION_FAILED.getGeneralMessage());
            }
        }
    }

    /**
     * Validates the size of the device info request.
     *
     * @param sizeOfInputRequest              The size of the input request.
     * @param supportedDeviceInfoRequestSize  The supported size of the device info request.
     * @throws ApiValidationFailedException   if the size of the input request is greater than the supported size.
     */
    public static void validateDeviceInfoRequestSize(int sizeOfInputRequest, int supportedDeviceInfoRequestSize) {
        if (sizeOfInputRequest > supportedDeviceInfoRequestSize) {
            LOGGER.error("## Request size is :{} which is greater than supported size supportedDeviceInfoSize :{}",
                sizeOfInputRequest, supportedDeviceInfoRequestSize);
            throw new ApiValidationFailedException(DEVICE_INFO_SAVE_SIZE_VALIDATION_FAILED.getCode(),
                DEVICE_INFO_SAVE_SIZE_VALIDATION_FAILED.getMessage(),
                DEVICE_INFO_SAVE_SIZE_VALIDATION_FAILED.getGeneralMessage());
        }
    }

}
