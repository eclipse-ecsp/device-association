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

package org.eclipse.ecsp.common;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Map;

/**
 * Utility class for handling errors.
 */
public final class ErrorUtils {
    /**
     * The maximum capacity value used in the application.
     * This constant is set to 150 and can be used wherever a predefined capacity limit is required.
     */
    public static final int CAPACITY = 150;
    /**
     * A constant key used to represent the error code in a map or context.
     * This key is typically used to store or retrieve error codes associated
     * with specific operations or exceptions.
     */
    public static final String ERROR_CODE_KEY = "errorCode";

    /**
     * Private constructor to prevent instantiation of the class.
     */
    private ErrorUtils() {
    }

    /**
     * Builds an error string based on the provided parameters.
     *
     * @param generalErrorMessage The general error message.
     * @param e                   The exception that occurred.
     * @param requestInput        The request input containing additional information.
     * @return The error string.
     */
    public static String buildError(String generalErrorMessage, Exception e, Map<Object, Object> requestInput) {
        StringBuilder errorSb = new StringBuilder(CAPACITY);
        errorSb.append("\n").append(generalErrorMessage).append("\n");
        errorSb.append("Request input:").append("\n");
        Object errorCode = requestInput.remove(ERROR_CODE_KEY);
        for (Map.Entry<Object, Object> m : requestInput.entrySet()) {
            Object key = m.getKey();
            Object value = m.getValue();
            errorSb.append("  ").append(key).append(": ").append(value).append("\n");
        }
        errorSb.append("Error Code: ").append(errorCode).append("\n");
        errorSb.append("Error Message: ").append(e.getMessage()).append("\n");
        errorSb.append("RootCause Message: ").append(ExceptionUtils.getRootCauseMessage(e)).append("\n");
        errorSb.append("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
            .append("\n");
        errorSb.append("Error StackTrace:").append("\n");
        errorSb.append("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
            .append("\n");
        errorSb.append(ExceptionUtils.getStackTrace(e)).append("\n");
        return errorSb.toString();
    }
}
