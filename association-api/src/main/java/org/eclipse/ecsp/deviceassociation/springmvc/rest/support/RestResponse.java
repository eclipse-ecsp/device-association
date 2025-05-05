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

import org.eclipse.ecsp.common.HcpServicesFailureResponse;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.ErrorResponseRest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.ErrorRest;
import org.eclipse.ecsp.deviceassociation.lib.rest.support.HttpRestHeaders;
import org.eclipse.ecsp.services.shared.rest.support.SimpleResponseMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * This class provides utility methods for creating different types of REST responses.
 */
public class RestResponse {
    private static final int STATUS_CODE_400 = 400;
    private static final int STATUS_CODE_404 = 404;
    private static final int STATUS_CODE_412 = 412;

    /**
     * Constructs a new RestResponse object.
     */
    private RestResponse() {

    }

    /**
     * Creates a response with HTTP status code 201 (Created).
     *
     * @param resource the resource to be included in the response body
     * @param <T>      the type of the resource
     * @return a ResponseEntity with HTTP status code 201 and the provided resource
     */
    public static <T> ResponseEntity<T> created(T resource) {
        return new ResponseEntity<>(resource, HttpStatus.CREATED);
    }

    /**
     * Creates a response with HTTP status code 201 (Created) and sets the Content-Location header.
     *
     * @param resource     the resource to be included in the response body
     * @param resourceUrl  the URL of the created resource
     * @param <T>          the type of the resource
     * @return a ResponseEntity with HTTP status code 201, the provided resource, and the Content-Location header
     */
    public static <T> ResponseEntity<T> created(T resource, String resourceUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpRestHeaders.CONTENT_LOCATION, resourceUrl);
        return new ResponseEntity<>(resource, headers, HttpStatus.CREATED);
    }

    /**
     * Creates a response with HTTP status code 200 (OK).
     *
     * @param resource the resource to be included in the response body
     * @param <T>      the type of the resource
     * @return a ResponseEntity with HTTP status code 200 and the provided resource
     */
    public static <T> ResponseEntity<T> ok(T resource) {
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    /**
     * Creates a response with HTTP status code 400 (Bad Request).
     *
     * @param resource the resource to be included in the response body
     * @param <T>      the type of the resource
     * @return a ResponseEntity with HTTP status code 400 and the provided resource
     */
    public static <T> ResponseEntity<T> badRequest(T resource) {
        return new ResponseEntity<>(resource, HttpStatus.BAD_REQUEST);
    }

    /**
     * Creates a response with HTTP status code 500 (Internal Server Error).
     *
     * @param resource the resource to be included in the response body
     * @param <T>      the type of the resource
     * @return a ResponseEntity with HTTP status code 500 and the provided resource
     */
    public static <T> ResponseEntity<T> internalServerError(T resource) {
        return new ResponseEntity<>(resource, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Creates a response with HTTP status code 500 (Internal Server Error) and an ErrorResponseRest body.
     *
     * @param message the error message
     * @return a ResponseEntity with HTTP status code 500 and an ErrorResponseRest body containing the error message
     */
    public static ResponseEntity<ErrorResponseRest> internalServerError(String message) {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, message, null);
    }

    /**
     * Creates a response with HTTP status code 412 (Precondition Failed) and an ErrorResponseRest body.
     *
     * @param message the error message
     * @return a ResponseEntity with HTTP status code 412 and an ErrorResponseRest body containing the error message
     */
    public static ResponseEntity<ErrorResponseRest> preconditionFailed(String message) {
        return createErrorResponse(HttpStatus.PRECONDITION_FAILED, message, null);
    }

    /**
     * Creates a response with HTTP status code 204 (No Content) and an ErrorResponseRest body.
     *
     * @param message the error message
     * @return a ResponseEntity with HTTP status code 204 and an ErrorResponseRest body containing the error message
     */
    public static ResponseEntity<ErrorResponseRest> noContent(String message) {
        return createErrorResponse(HttpStatus.NO_CONTENT, message, null);
    }

    /**
     * Creates a response with the provided HttpStatus and a SimpleResponseMessage body.
     *
     * @param status   the HttpStatus
     * @param response the SimpleResponseMessage
     * @return a ResponseEntity with the provided HttpStatus and SimpleResponseMessage body
     */
    public static ResponseEntity<SimpleResponseMessage> createSimpleErrorResponse(HttpStatus status,
                                                                                  SimpleResponseMessage response) {
        return new ResponseEntity<>(response, status);
    }

    /**
     * Creates a response with the provided HttpStatus and a SimpleResponseMessage body containing the error message.
     *
     * @param status  the HttpStatus
     * @param message the error message
     * @return a ResponseEntity with the provided HttpStatus and SimpleResponseMessage body containing the error message
     */
    public static ResponseEntity<SimpleResponseMessage> createSimpleErrorResponse(HttpStatus status, String message) {
        return new ResponseEntity<>(new SimpleResponseMessage(message), status);
    }

    /**
     * Creates a response with the provided HttpStatus and a SimpleResponseMessage body containing the error message.
     *
     * @param status  the HttpStatus
     * @param message the error message
     * @return a ResponseEntity with the provided HttpStatus and SimpleResponseMessage body containing the error message
     */
    public static ResponseEntity<Object> createSimpleErrorResponses(HttpStatus status, String message) {
        return new ResponseEntity<>(new SimpleResponseMessage(message), status);
    }

    /**
     * Creates a response with the provided HttpStatus and a SimpleResponseMessage body containing the error message.
     *
     * @param status  the HttpStatus
     * @param response the SimpleResponseMessage
     * @return a ResponseEntity with the provided HttpStatus and SimpleResponseMessage body containing the error message
     */
    public static ResponseEntity<Object> createSimpleErrorResponses(HttpStatus status,
                                                                    SimpleResponseMessage response) {
        return new ResponseEntity<>(response, status);
    }

    /**
     * Creates a ResponseEntity object containing an ErrorResponseRest object with the specified status, message, and
     * reference.
     *
     * @param status    the HTTP status of the response
     * @param message   the error message
     * @param reference the reference to the error
     * @return a ResponseEntity object containing the ErrorResponseRest object
     */
    private static ResponseEntity<ErrorResponseRest> createErrorResponse(HttpStatus status, String message,
                                                                         String reference) {
        ErrorRest error = new ErrorRest();
        error.setMessage(message);
        error.setReference(reference);
        ErrorResponseRest errorResponse = new ErrorResponseRest();
        errorResponse.setError(error);
        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Creates a response with HTTP status code 404 (Not Found) and an ErrorResponseRest body.
     *
     * @param message the error message
     * @return a ResponseEntity with HTTP status code 404 and an ErrorResponseRest body containing the error message
     */
    public static ResponseEntity<ErrorResponseRest> notFound(String message) {
        return createErrorResponse(HttpStatus.NOT_FOUND, message, null);
    }

    /**
     * Creates a ResponseEntity representing a "Not Found" (404) error response.
     *
     * @param <T>     The type of the response body.
     * @param status  The HTTP status to be returned, typically HttpStatus.NOT_FOUND.
     * @param message The error message to include in the response body.
     * @return A ResponseEntity containing the specified HTTP status and error message.
     */
    public static <T> ResponseEntity<T> notFound(HttpStatus status, String message) {
        return (ResponseEntity<T>) createErrorResponse(status, message, null);
    }

    /**
     * Creates a response with HTTP status code 405 (Method Not Allowed) and an ErrorResponseRest body.
     *
     * @return a ResponseEntity with HTTP status code 405 and an ErrorResponseRest body containing the error message
     */
    public static ResponseEntity<ErrorResponseRest> methodNotAllowed() {
        return createErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, "HTTP method is not allowed for this resource", null);
    }

    /**
     * Creates a failure response with the provided resource.
     *
     * @param resource the resource to be included in the response body
     * @param <T>      the type of the resource
     * @return a ResponseEntity with the provided HTTP status code and a HcpServicesFailureResponse body
     */
    public static <T> ResponseEntity<T> createFailureResponse(T resource) {
        HcpServicesFailureResponse hcpServicesFailureResponse = (HcpServicesFailureResponse) resource;
        return (ResponseEntity<T>) createFailureResponse(hcpServicesFailureResponse.getHttpStatusCode(),
                hcpServicesFailureResponse.getMessage(), hcpServicesFailureResponse.getRequestId(),
                hcpServicesFailureResponse.getCode(), hcpServicesFailureResponse.getReason());
    }

    /**
     * Creates a failure response with the provided HTTP status code, error message, request ID, code, and reason.
     *
     * @param httpSatatusCode the HTTP status code
     * @param message        the error message
     * @param requestId      the request ID
     * @param code           the error code
     * @param reason         the reason for the failure
     * @return a ResponseEntity with the provided HTTP status code and a HcpServicesFailureResponse body
     */
    public static ResponseEntity<HcpServicesFailureResponse> createFailureResponse(int httpSatatusCode, String message,
                                                                                   String requestId, String code,
                                                                                   String reason) {
        HcpServicesFailureResponse failureResponse = new HcpServicesFailureResponse();
        failureResponse.setCode(code);
        failureResponse.setReason(reason);
        failureResponse.setHttpStatusCode(httpSatatusCode);
        failureResponse.setMessage(message);
        failureResponse.setRequestId(requestId);
        if (httpSatatusCode == STATUS_CODE_400) {
            return new ResponseEntity<>(failureResponse, HttpStatus.BAD_REQUEST);
        } else if (httpSatatusCode == STATUS_CODE_404) {
            return new ResponseEntity<>(failureResponse, HttpStatus.NOT_FOUND);
        } else if (httpSatatusCode == STATUS_CODE_412) {
            return new ResponseEntity<>(failureResponse, HttpStatus.PRECONDITION_FAILED);
        } else {
            return new ResponseEntity<>(failureResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Creates a response with the provided HttpStatus and a SimpleResponseMessage body containing the message.
     *
     * @param status  the HttpStatus
     * @param message the message
     * @return a ResponseEntity with the provided HttpStatus and SimpleResponseMessage body containing the message
     */
    public static ResponseEntity<SimpleResponseMessage> createSimpleResponseMessage(HttpStatus status, String message) {
        return new ResponseEntity<>(new SimpleResponseMessage(message), status);
    }

}