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

package org.eclipse.ecsp.deviceassociation.dto.swm;

import java.util.List;

/**
 * Represents a representation object.
 */
public class RepresentationObject {
    private String id;
    private String deprecationStatus;
    private Links links;
    private List<Object> actionResult;
    private AdditionalAttributes additionalAttributes;
    private String modelCode;
    private String modelName;

    /**
     * Gets the ID of the representation object.
     *
     * @return The ID of the representation object.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the representation object.
     *
     * @param id The ID of the representation object.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the deprecation status of the representation object.
     *
     * @return The deprecation status of the representation object.
     */
    public String getDeprecationStatus() {
        return deprecationStatus;
    }

    /**
     * Sets the deprecation status of the representation object.
     *
     * @param deprecationStatus The deprecation status of the representation object.
     */
    public void setDeprecationStatus(String deprecationStatus) {
        this.deprecationStatus = deprecationStatus;
    }

    /**
     * Gets the links associated with the representation object.
     *
     * @return The links associated with the representation object.
     */
    public Links getLinks() {
        return links;
    }

    /**
     * Sets the links associated with the representation object.
     *
     * @param links The links associated with the representation object.
     */
    public void setLinks(Links links) {
        this.links = links;
    }

    /**
     * Gets the action result of the representation object.
     *
     * @return The action result of the representation object.
     */
    public List<Object> getActionResult() {
        return actionResult;
    }

    /**
     * Sets the action result of the representation object.
     *
     * @param actionResult The action result of the representation object.
     */
    public void setActionResult(List<Object> actionResult) {
        this.actionResult = actionResult;
    }

    /**
     * Gets the additional attributes of the representation object.
     *
     * @return The additional attributes of the representation object.
     */
    public AdditionalAttributes getAdditionalAttributes() {
        return additionalAttributes;
    }

    /**
     * Sets the additional attributes of the representation object.
     *
     * @param additionalAttributes The additional attributes of the representation object.
     */
    public void setAdditionalAttributes(AdditionalAttributes additionalAttributes) {
        this.additionalAttributes = additionalAttributes;
    }

    /**
     * Gets the model code of the representation object.
     *
     * @return The model code of the representation object.
     */
    public String getModelCode() {
        return modelCode;
    }

    /**
     * Sets the model code of the representation object.
     *
     * @param modelCode The model code of the representation object.
     */
    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    /**
     * Gets the model name of the representation object.
     *
     * @return The model name of the representation object.
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * Sets the model name of the representation object.
     *
     * @param modelName The model name of the representation object.
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
}
