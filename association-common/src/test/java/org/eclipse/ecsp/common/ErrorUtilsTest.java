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

import org.junit.Test;

import java.util.LinkedHashMap;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test class for ErrorUtils.
 *
 * @author aagrahari
 */
public class ErrorUtilsTest {
    @Test
    public void buildError() {
        String errorMessage =
            ErrorUtils.buildError("general error", new Exception(), new LinkedHashMap<Object, Object>() {{
                    put("imei", "12312312312");
                    put(ErrorUtils.ERROR_CODE_KEY, "assoc-777");
                }
            });

        assertNotNull(errorMessage);
        assertTrue(errorMessage.contains("Error Code"));
        assertTrue(errorMessage.contains("Error Message"));
        assertTrue(errorMessage.contains("RootCause Message"));
        assertTrue(errorMessage.contains("Error StackTrace"));
    }
}
