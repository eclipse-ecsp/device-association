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

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * Represents an extended API response.
 *
 * @param <D> the type of the data in the response
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public final class ExtendedApiResponse<D> {
    private final String requestId;
    private final String code;
    private final String reason;
    private final String message;
    private final Meta meta;
    private final D data;
    private HttpStatus httpStatusCodeObj;
    private int httpStatusCode;
    private List<InterComponentError> errors;

    /**
     * Constructs a new ExtendedApiResponse object.
     *
     * @param builder the builder object used to construct the response
     */
    public ExtendedApiResponse(Builder<D> builder) {
        this.httpStatusCodeObj = builder.httpStatusCodeObj;
        this.httpStatusCode = builder.httpStatusCode;
        this.requestId = builder.requestId;
        this.code = builder.code;
        this.reason = builder.reason;
        this.message = builder.message;
        this.meta = builder.meta;
        this.data = builder.data;
        this.errors = builder.errors;
    }

    /**
     * Gets the HTTP status code associated with the response.
     *
     * @return the HTTP status code
     */
    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    /**
     * Sets the HTTP status code associated with the response.
     *
     * @param httpStatusCode the HTTP status code to set
     */
    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    /**
     * Gets the HTTP status code object associated with the response.
     *
     * @return the HTTP status code object
     */
    public HttpStatus getHttpStatusCodeObj() {
        return httpStatusCodeObj;
    }

    /**
     * Sets the HTTP status code object associated with the response.
     *
     * @param httpStatusCodeObj the HTTP status code object to set
     */
    public void setHttpStatusCodeObj(HttpStatus httpStatusCodeObj) {
        this.httpStatusCodeObj = httpStatusCodeObj;
    }

    /**
     * Gets the request ID associated with the response.
     *
     * @return the request ID
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Gets the code associated with the response.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Gets the reason associated with the response.
     *
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * Gets the message associated with the response.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the meta information associated with the response.
     *
     * @return the meta information
     */
    public Meta getMeta() {
        return meta;
    }

    /**
     * Gets the data in the response.
     *
     * @return the data
     */
    public D getData() {
        return data;
    }

    /**
     * Gets the list of inter-component errors in the response.
     *
     * @return the list of errors
     */
    public List<InterComponentError> getErrors() {
        return errors;
    }

    /**
     * Sets the list of inter-component errors in the response.
     *
     * @param errors the list of errors to set
     */
    public void setErrors(List<InterComponentError> errors) {
        this.errors = errors;
    }

    /**
     * Builder class for constructing ExtendedApiResponse objects.
     *
     * @param <D> the type of the data in the response
     */
    public static class Builder<D> {
        private final HttpStatus httpStatusCodeObj;
        private final int httpStatusCode;
        private final String requestId;
        private final String code;
        private final String reason;
        private final String message;
        private Meta meta;
        private D data;
        private List<InterComponentError> errors;

        /**
         * Constructs a new Builder object.
         *
         * @param requestId      the request ID
         * @param httpStatus     the HTTP status
         * @param code           the code
         * @param reason         the reason
         * @param message        the message
         */
        public Builder(String requestId, HttpStatus httpStatus, String code, String reason, String message) {
            this.httpStatusCodeObj = httpStatus;
            this.httpStatusCode = httpStatus.value();
            this.code = code;
            this.requestId = requestId;
            this.reason = reason;
            this.message = message;
        }

        /**
         * Sets the meta information for the response.
         *
         * @param meta the meta information to set
         * @return the builder object
         */
        public Builder<D> withMeta(Meta meta) {
            this.meta = meta;
            return this;
        }

        /**
         * Sets the data for the response.
         *
         * @param data the data to set
         * @return the builder object
         */
        public Builder<D> withData(D data) {
            this.data = data;
            return this;
        }

        /**
         * Sets the inter-component errors for the response.
         *
         * @param interComponentErrors the list of errors to set
         * @return the builder object
         */
        public Builder<D> withError(List<InterComponentError> interComponentErrors) {
            this.errors = interComponentErrors;
            return this;
        }

        /**
         * Builds an ExtendedApiResponse object.
         *
         * @return the ExtendedApiResponse object
         */
        public ExtendedApiResponse<D> build() {
            return new ExtendedApiResponse<>(this);
        }
    }

    /**
     * Represents the meta information in the response.
     */
    public static class Meta {
        private final int firstPage;
        private final int lastPage;
        private final int count;

        /**
         * Constructs a new Meta object.
         *
         * @param firstPage the first page
         * @param lastPage  the last page
         * @param count     the count
         */
        public Meta(int firstPage, int lastPage, int count) {
            this.firstPage = firstPage;
            this.lastPage = lastPage;
            this.count = count;
        }

        /**
         * Gets the first page.
         *
         * @return the first page
         */
        public int getFirstPage() {
            return firstPage;
        }

        /**
         * Gets the last page.
         *
         * @return the last page
         */
        public int getLastPage() {
            return lastPage;
        }

        /**
         * Gets the count.
         *
         * @return the count
         */
        public int getCount() {
            return count;
        }
    }

    /**
     * Represents an inter-component error.
     */
    public static class InterComponentError {
        private String code;
        private String reason;
        private String message;

        /**
         * Gets the code of the error.
         *
         * @return the code
         */
        public String getCode() {
            return code;
        }

        /**
         * Sets the code of the error.
         *
         * @param code the code to set
         */
        public void setCode(String code) {
            this.code = code;
        }

        /**
         * Gets the reason of the error.
         *
         * @return the reason
         */
        public String getReason() {
            return reason;
        }

        /**
         * Sets the reason of the error.
         *
         * @param reason the reason to set
         */
        public void setReason(String reason) {
            this.reason = reason;
        }

        /**
         * Gets the message of the error.
         *
         * @return the message
         */
        public String getMessage() {
            return message;
        }

        /**
         * Sets the message of the error.
         *
         * @param message the message to set
         */
        public void setMessage(String message) {
            this.message = message;
        }
    }
}
