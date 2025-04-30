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
 * Represents a successful response for device association history.
 * This class extends the {@link HcpServicesBaseResponse} class.
 *
 * @param <T> the type of data in the response
 */
public class DeviceAssociationHistorySuccessResponse<T> extends HcpServicesBaseResponse {

    private static final long serialVersionUID = 1L;
    private RecordStats<Object> recordStats;
    private List<T> data;

    /**
     * Constructs a new DeviceAssociationHistorySuccessResponse object.
     */
    public DeviceAssociationHistorySuccessResponse() {
        super();
    }

    /**
     * Gets the data in the response.
     *
     * @return the data in the response
     */
    public List<T> getData() {
        return data;
    }

    /**
     * Sets the data in the response.
     *
     * @param data the data to be set in the response
     */
    public void setData(List<T> data) {
        this.data = data;
    }

    /**
     * Gets the record statistics in the response.
     *
     * @return the record statistics in the response
     */
    public RecordStats<Object> getRecordStats() {
        return recordStats;
    }

    /**
     * Sets the record statistics in the response.
     *
     * @param recordStats the record statistics to be set in the response
     */
    public void setRecordStats(RecordStats<Object> recordStats) {
        this.recordStats = recordStats;
    }

    /**
     * Custom serialization method to write the object to a stream.
     *
     * @param stream the output stream to write the object to
     * @throws IOException if an I/O error occurs while writing the object
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }

    /**
     * Custom deserialization method to read the object from a stream.
     *
     * @param stream the input stream to read the object from
     * @throws IOException            if an I/O error occurs while reading the object
     * @throws ClassNotFoundException if the class of the serialized object cannot be found
     */
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
    }

}