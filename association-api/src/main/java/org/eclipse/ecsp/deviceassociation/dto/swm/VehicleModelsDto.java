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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

/**
 * Represents a DTO (Data Transfer Object) for vehicle models.
 */
public class VehicleModelsDto implements Serializable {
    private String totalCount;
    private String deprecationStatus;
    private String id;
    private String actionResult;
    private List<RepresentationObject> representationObjects;

    /**
     * Main method for testing purposes.
     *
     * @param args the command line arguments
     * @throws IOException if an I/O error occurs
     */
    public static void main(String[] args) throws IOException {
        String json =
            "{\"totalCount\":null,\"representationObjects\":[{\"id\":\"2959400019495939928387467878297999069\","
                + "\"deprecationStatus\":null,\"links\":{\"link\":[{\"uri\":\"//v1.0//vehicleModels/2959400019"
                + "495939928387467878297999069\",\"rel\":\"self\",\"type\":null},{\"uri\":\"//v1.0//vehicleModels/"
                + "logo/2959400019495939928387467878297999069\",\"rel\":\"self\",\"type\":null},{\"uri\":\"//v1.0//"
                + "vehicleModels/picture/2959400019495939928387467878297999069\",\"rel\":\"self\",\"type\":null}]},"
                + "\"actionResult\":[],\"additionalAttributes\":{\"map\":[]},\"modelCode\":\"aaa\",\"name\":"
                + "\"testmodel\"}],\"deprecationStatus\":null,\"id\":null,\"actionResult\":null}";

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        VehicleModelsDto vehicleModelsDto = mapper.readValue(json, VehicleModelsDto.class);
        String totalCount = vehicleModelsDto.getTotalCount();
    }

    /**
     * Gets the total count.
     *
     * @return the total count
     */
    public String getTotalCount() {
        return totalCount;
    }

    /**
     * Sets the total count.
     *
     * @param totalCount the total count to set
     */
    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * Gets the deprecation status.
     *
     * @return the deprecation status
     */
    public String getDeprecationStatus() {
        return deprecationStatus;
    }

    /**
     * Sets the deprecation status.
     *
     * @param deprecationStatus the deprecation status to set
     */
    public void setDeprecationStatus(String deprecationStatus) {
        this.deprecationStatus = deprecationStatus;
    }

    /**
     * Gets the ID.
     *
     * @return the ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID.
     *
     * @param id the ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the action result.
     *
     * @return the action result
     */
    public String getActionResult() {
        return actionResult;
    }

    /**
     * Sets the action result.
     *
     * @param actionResult the action result to set
     */
    public void setActionResult(String actionResult) {
        this.actionResult = actionResult;
    }

    /**
     * Gets the representation objects.
     *
     * @return the representation objects
     */
    public List<RepresentationObject> getRepresentationObjects() {
        return representationObjects;
    }

    /**
     * Sets the representation objects.
     *
     * @param representationObjects the representation objects to set
     */
    public void setRepresentationObjects(List<RepresentationObject> representationObjects) {
        this.representationObjects = representationObjects;
    }

    private void writeObject(ObjectOutputStream stream)
        throws IOException {
        stream.defaultWriteObject();
    }

    private void readObject(ObjectInputStream stream)
        throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
    }
}
