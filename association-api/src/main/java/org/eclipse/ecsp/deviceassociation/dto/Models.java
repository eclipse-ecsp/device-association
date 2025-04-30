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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Represents a collection of models.
 */
public class Models implements Serializable {
    private Map<String, List<ModelsInfo>> models;

    /**
     * Gets the models.
     *
     * @return The models.
     */
    public Map<String, List<ModelsInfo>> getModels() {
        return models;
    }

    /**
     * Sets the models.
     *
     * @param models The models to set.
     */
    public void setModels(Map<String, List<ModelsInfo>> models) {
        this.models = models;
    }

    /**
     * Returns a string representation of the Models object.
     *
     * @return A string representation of the Models object.
     */
    @Override
    public String toString() {
        return "Models [models=" + models + "]";
    }

    /**
     * Custom serialization writeObject method to resolve SE_BAD_FIELD Sonar code smell issue.
     *
     * @param stream The ObjectOutputStream to write the object to.
     * @throws IOException If an I/O error occurs while writing the object.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }

    /**
     * Custom serialization readObject method to resolve SE_BAD_FIELD Sonar code smell issue.
     *
     * @param stream The ObjectInputStream to read the object from.
     * @throws IOException            If an I/O error occurs while reading the object.
     * @throws ClassNotFoundException If the class of a serialized object cannot be found.
     */
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
    }
}
