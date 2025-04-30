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

/**
 * Represents information about a model.
 */
public class ModelsInfo {
    private String modelCode;
    private String modelName;
    private String dongleType;

    /**
     * Gets the model code.
     *
     * @return The model code.
     */
    public String getModelCode() {
        return modelCode;
    }

    /**
     * Sets the model code.
     *
     * @param modelCode The model code to set.
     */
    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    /**
     * Gets the model name.
     *
     * @return The model name.
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * Sets the model name.
     *
     * @param modelName The model name to set.
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * Gets the dongle type.
     *
     * @return The dongle type.
     */
    public String getDongleType() {
        return dongleType;
    }

    /**
     * Sets the dongle type.
     *
     * @param dongleType The dongle type to set.
     */
    public void setDongleType(String dongleType) {
        this.dongleType = dongleType;
    }

    /**
     * Returns a string representation of the ModelsInfo object.
     *
     * @return A string representation of the object.
     */
    @Override
    public String toString() {
        return "ModelsInfo{" 
            +            "modelCode='" + modelCode + '\'' 
            +            ", modelName='" + modelName + '\'' 
            +            ", dongleType='" + dongleType + '\'' 
            +            '}';
    }
}
