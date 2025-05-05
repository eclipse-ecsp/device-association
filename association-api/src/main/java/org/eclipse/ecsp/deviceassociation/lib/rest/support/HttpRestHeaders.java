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

package org.eclipse.ecsp.deviceassociation.lib.rest.support;

/**
 * This interface defines the HTTP headers used in RESTful API requests and responses.
 */
public interface HttpRestHeaders {
    /**
     * The HTTP header field name for specifying the location of the resource 
     * that corresponds to the response content. This header is typically used 
     * to indicate the URI of a newly created resource or the location of a 
     * resource that is relevant to the response.
     */
    String CONTENT_LOCATION = "Content-Location";
}
