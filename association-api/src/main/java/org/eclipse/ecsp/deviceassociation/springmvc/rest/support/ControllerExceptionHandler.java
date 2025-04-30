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
import jakarta.validation.ConstraintViolationException;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.ErrorRest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.nio.file.AccessDeniedException;
import java.util.List;

/**
 * This class handles exceptions that occur within the controllers of the application.
 * It implements the HandlerExceptionResolver interface to provide a custom exception handling mechanism.
 */
public class ControllerExceptionHandler implements HandlerExceptionResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    private static final String EXCEPTION_LOG = "Internal Server Error (";
    private static final String CLOSE_BRACKET_LOG = ")";

    /**
     * Resolves the exception that occurred during request processing and returns a ModelAndView object
     * containing the appropriate error response.
     *
     * @param request  the HttpServletRequest object representing the current request
     * @param response the HttpServletResponse object representing the current response
     * @param handler  the handler object that was handling the request
     * @param ex       the exception that occurred during request processing
     * @return a ModelAndView object containing the error response
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
                                         Exception ex) {
        ModelAndView mv = new ModelAndView(new MappingJackson2JsonView());
        ErrorRest error = new ErrorRest();
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        StackTraceElement[] stackTrace = ex.getStackTrace();

        if (isSpringHttpConverterError(stackTrace)) {
            error.setMessage("Unable to parse request (" + ex + CLOSE_BRACKET_LOG);
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (ex instanceof AccessDeniedException) {
            error.setMessage("User is NOT AUTHORIZED to perform this operation");
            httpStatus = HttpStatus.UNAUTHORIZED;
        } else if (ex instanceof MissingServletRequestParameterException) {
            error.setMessage("Unknown Error. Please check input format.");
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (ex instanceof HttpRequestMethodNotSupportedException) {
            error.setMessage("Method not allowed exception encountered");
            httpStatus = HttpStatus.METHOD_NOT_ALLOWED;
        } else if (ex instanceof MethodArgumentNotValidException) {
            if (ex.getMessage() != null) {
                error.setMessage(getErrorsMap((MethodArgumentNotValidException) ex));
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                error.setMessage(EXCEPTION_LOG + ex + CLOSE_BRACKET_LOG);
            }
        } else if (ex instanceof ConstraintViolationException) {
            if (ex.getMessage() != null) {
                error.setMessage(ex.getMessage());
                httpStatus = HttpStatus.BAD_REQUEST;
            } else {
                error.setMessage(EXCEPTION_LOG + ex + CLOSE_BRACKET_LOG);
            }
        } else {
            error.setMessage(EXCEPTION_LOG + ex + CLOSE_BRACKET_LOG);
        }
        mv.addObject("error", error);
        response.setStatus(httpStatus.value());
        LOGGER.error("HttpStatus({}) {}", httpStatus, error.getMessage(), ex);
        return mv;
    }

    /**
     * Checks if the given stack trace contains any element from the Spring HTTP converter class.
     *
     * @param stackTrace the stack trace to be checked
     * @return true if the stack trace contains an element from the Spring HTTP converter class, false otherwise
     */
    private boolean isSpringHttpConverterError(StackTraceElement[] stackTrace) {
        for (StackTraceElement stackTraceElement : stackTrace) {
            if ("org.springframework.http.converter.AbstractHttpMessageConverter".equals(
                stackTraceElement.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private String getErrorsMap(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream().map(FieldError::getDefaultMessage).toList();
        return errors.get(0);
    }

}
