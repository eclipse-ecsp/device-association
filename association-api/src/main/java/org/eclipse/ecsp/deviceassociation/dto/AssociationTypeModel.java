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

package org.eclipse.ecsp.deviceassociation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Represents a model for association types.
 * This class contains a list of data objects, each containing a list of association types.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssociationTypeModel {

    private List<Data> data;

    /**
     * Get the data object for association types.
     *
     * @return The data object for association types.
     */
    public List<Data> getData() {
        return data;
    }

    /**
     * Set the data object for association types.
     *
     * @param data The data object for association types.
     */
    public void setData(List<Data> data) {
        this.data = data;
    }

    /**
     * Represents the data object for association types.
     * This class contains a list of association types.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {
        private List<AssocType> assocTypeList;

        /**
         * Get the list of association types.
         *
         * @return The list of association types.
         */
        public List<AssocType> getAssocTypeList() {
            return assocTypeList;
        }

        /**
         * Set the list of association types.
         *
         * @param assocTypeList The list of association types.
         */
        public void setAssocTypeList(List<AssocType> assocTypeList) {
            this.assocTypeList = assocTypeList;
        }
    }

    /**
     * Represents the association type model.
     * This class is used to store the association type information.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AssocType {
        private String associationType;

        /**
         * Gets the association type.
         *
         * @return The association type.
         */
        public String getAssociationType() {
            return associationType;
        }

        /**
         * Sets the association type.
         *
         * @param associationType The association type to set.
         */
        public void setAssociationType(String associationType) {
            this.associationType = associationType;
        }
    }
}