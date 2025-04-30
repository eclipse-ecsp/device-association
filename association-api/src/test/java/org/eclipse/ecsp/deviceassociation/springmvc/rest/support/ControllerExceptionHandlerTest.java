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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import java.nio.file.AccessDeniedException;
import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for ControllerExceptionHandler.
 */
public class ControllerExceptionHandlerTest {

    @InjectMocks
    ControllerExceptionHandler controllerExceptionHandler;

    @Mock
    HttpServletRequest httpServletRequest;

    @Mock
    HttpServletResponse httpServletResponse;

    @Mock
    Object handler;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void resolveExceptionTest_BadRequest() {
        StackTraceElement stackTraceElement =
            new StackTraceElement("org.springframework.http.converter.AbstractHttpMessageConverter", "method", "file",
                1);
        StackTraceElement[] stackTraceElements = {stackTraceElement};
        Exception ex = mock(Exception.class);
        Mockito.when(ex.getStackTrace()).thenReturn(stackTraceElements);
        Assertions.assertNotNull(controllerExceptionHandler.resolveException(httpServletRequest,
                httpServletResponse, handler, ex));
    }

    @Test
    public void resolveExceptionTest_AccessDeniedException() {
        Exception ex = new AccessDeniedException("1");
        Assertions.assertNotNull(controllerExceptionHandler.resolveException(httpServletRequest,
                httpServletResponse, handler, ex));
    }

    @Test
    public void resolveExceptionTest_MissingServletRequestParameterException() {
        Exception ex = new MissingServletRequestParameterException("1", "2");
        Assertions.assertNotNull(controllerExceptionHandler.resolveException(httpServletRequest,
                httpServletResponse, handler, ex));
    }

    @Test
    public void resolveExceptionTest_HttpRequestMethodNotSupportedException() {
        Exception ex = new HttpRequestMethodNotSupportedException("getDetails", Arrays.asList("Not supported"));
        Assertions.assertNotNull(controllerExceptionHandler.resolveException(httpServletRequest,
                httpServletResponse, handler, ex));
    }

    @Test
    public void resolveExceptionTest_IllegalAccessException() {
        Exception ex = new IllegalAccessException("Illegal access");
        Assertions.assertNotNull(controllerExceptionHandler.resolveException(httpServletRequest,
                httpServletResponse, handler, ex));
    }
}