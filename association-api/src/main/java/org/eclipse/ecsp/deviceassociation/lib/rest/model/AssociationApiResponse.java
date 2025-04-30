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

package org.eclipse.ecsp.deviceassociation.lib.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.eclipse.ecsp.common.HcpServicesBaseResponse;

import java.util.List;

/**
 * Represents the response from the Association API.
 *
 * @param <T> The type of data associated with the response.
 */
@JsonInclude(Include.NON_NULL)
public class AssociationApiResponse<T> extends HcpServicesBaseResponse {

    private static final long serialVersionUID = 1L;
    private List<T> data;

    /**
     * Default constructor for the AssociationApiResponse class.
     */
    public AssociationApiResponse() {
        super();
    }

    /**
     * Get the data associated with the AssociationApiResponse.
     *
     * @return The data associated with the AssociationApiResponse.
     */
    public List<T> getData() {
        return data;
    }

    /**
     * Set the data for the AssociationApiResponse.
     *
     * @param data The data to be set for the AssociationApiResponse.
     */
    public void setData(List<T> data) {
        this.data = data;
    }
}