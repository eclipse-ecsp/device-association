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

import org.eclipse.ecsp.common.HcpServicesBaseResponse;
import org.eclipse.ecsp.common.RecordStats;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * Represents a success response for device association history in version 2 of the API.
 * This class extends the {@link HcpServicesBaseResponse} class.
 *
 * @param <T> the type of factory data
 */
public class DeviceAssociationHistorySuccessResponseV2<T> extends HcpServicesBaseResponse {

    private static final long serialVersionUID = 1L;
    private RecordStats<Object> recordStats;
    private List<T> factoryData;

    /**
     * Constructs a new DeviceAssociationHistorySuccessResponseV2 object.
     */
    public DeviceAssociationHistorySuccessResponseV2() {
        super();
    }

    /**
     * Gets the factory data.
     *
     * @return the factory data
     */
    public List<T> getFactoryData() {
        return factoryData;
    }

    /**
     * Sets the factory data.
     *
     * @param factoryData the factory data to set
     */
    public void setFactoryData(List<T> factoryData) {
        this.factoryData = factoryData;
    }

    /**
     * Gets the record statistics.
     *
     * @return the record statistics
     */
    public RecordStats<Object> getRecordStats() {
        return recordStats;
    }

    /**
     * Sets the record statistics.
     *
     * @param recordStats the record statistics to set
     */
    public void setRecordStats(RecordStats<Object> recordStats) {
        this.recordStats = recordStats;
    }

    /**
     * Custom serialization method to write the object to the output stream.
     *
     * @param stream the output stream to write to
     * @throws IOException if an I/O error occurs
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }

    /**
     * Custom deserialization method to read the object from the input stream.
     *
     * @param stream the input stream to read from
     * @throws IOException            if an I/O error occurs
     * @throws ClassNotFoundException if the class of a serialized object cannot be found
     */
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
    }

}