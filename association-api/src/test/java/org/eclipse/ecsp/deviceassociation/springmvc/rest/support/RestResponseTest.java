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

import org.eclipse.ecsp.services.shared.rest.support.SimpleResponseMessage;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test class for RestResponse.
 */
public class RestResponseTest {
    private static final int STATUS_CODE_400 = 400;
    private static final int STATUS_CODE_404 = 404;
    private static final int STATUS_CODE_412 = 412;
    private static final int STATUS_CODE_4 = 4;

    @InjectMocks
    private RestResponse restResponse;

    @Test
    public void createdTestWithUrl() {
        String resource = "resource";
        String url = "url";
        assertNotNull(restResponse.created(resource, url));
    }

    @Test
    public void createdTestWithResource() {
        String resource = "resource";
        assertNotNull(restResponse.created(resource));
    }

    @Test
    public void okTest() {
        String resource = "resource";
        assertNotNull(restResponse.ok(resource));
    }

    @Test
    public void badRequestTest() {
        String resource = "resource";
        assertNotNull(restResponse.badRequest(resource));
    }

    @Test
    public void internalServerErrorTest() {
        Object resource = "resource";
        assertNotNull(restResponse.internalServerError(resource));
    }

    @Test
    public void notFoundTest() {
        String message = "message";
        assertNotNull(restResponse.notFound(message));
    }

    @Test
    public void methodNotAllowedTest() {
        assertNotNull(restResponse.methodNotAllowed());
    }

    @Test
    public void createSimpleErrorResponseTest() {
        assertNotNull(restResponse.createSimpleErrorResponse(HttpStatus.MULTI_STATUS, "message"));
    }

    @Test
    public void createSimpleResponseMessageTest() {
        assertNotNull(restResponse.createSimpleResponseMessage(HttpStatus.MULTI_STATUS, "message"));
    }

    @Test
    public void createSimpleErrorResponseTest2() {

        SimpleResponseMessage response = new SimpleResponseMessage("header");
        assertNotNull(restResponse.createSimpleErrorResponse(HttpStatus.ACCEPTED, response));
    }

    @Test
    public void internalServerErrorTest2() {
        assertNotNull(restResponse.internalServerError("message"));
    }

    @Test
    public void noContentTest() {
        String message = "message";
        assertNotNull(restResponse.noContent(message));
    }

    @Test
    public void preconditionFailedTest() {
        String string = "message";
        assertNotNull(restResponse.preconditionFailed(string));
    }

    @Test
    public void createFailureResponseTest_400() {

        assertNotNull(restResponse.createFailureResponse(STATUS_CODE_400, "message", "requestId", "code", "reason"));
    }

    @Test
    public void createFailureResponseTest_404() {

        assertNotNull(restResponse.createFailureResponse(STATUS_CODE_404, "message", "requestId", "code", "reason"));
    }

    @Test
    public void createFailureResponseTest_412() {

        assertNotNull(restResponse.createFailureResponse(STATUS_CODE_412, "message", "requestId", "code", "reason"));
    }

    @Test
    public void createFailureResponseTest() {

        assertNotNull(restResponse.createFailureResponse(STATUS_CODE_4, "message", "requestId", "code", "reason"));
    }
}
