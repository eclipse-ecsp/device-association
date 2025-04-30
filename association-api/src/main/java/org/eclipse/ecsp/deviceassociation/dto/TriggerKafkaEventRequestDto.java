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
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Represents a request to trigger a Kafka event.
 * This class is used to encapsulate the necessary information for triggering a Kafka event,
 * including the device information, topic, event ID, and key.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TriggerKafkaEventRequestDto implements Serializable {

    private DeviceInfo deviceInfo;
    private String topic;
    private String eventId;
    private String key;

    /**
     * Constructs a new TriggerKafkaEventRequestDto with the specified parameters.
     *
     * @param deviceInfo The device information.
     * @param topic      The Kafka topic.
     * @param eventId    The event ID.
     * @param key        The event key.
     */
    public TriggerKafkaEventRequestDto(DeviceInfo deviceInfo, String topic, String eventId, String key) {
        super();
        this.deviceInfo = deviceInfo;
        this.topic = topic;
        this.eventId = eventId;
        this.key = key;
    }

    /**
     * Returns the device information.
     *
     * @return The device information.
     */
    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    /**
     * Sets the device information.
     *
     * @param deviceInfo The device information to set.
     */
    public void setDeviceInfo(DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    /**
     * Returns the Kafka topic.
     *
     * @return The Kafka topic.
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Sets the Kafka topic.
     *
     * @param topic The Kafka topic to set.
     */
    public void setTopic(String topic) {
        this.topic = topic;
    }

    /**
     * Returns the event ID.
     *
     * @return The event ID.
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Sets the event ID.
     *
     * @param eventId The event ID to set.
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Returns the event key.
     *
     * @return The event key.
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the event key.
     *
     * @param key The event key to set.
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Returns a string representation of the TriggerKafkaEventRequestDto object.
     *
     * @return A string representation of the object.
     */
    @Override
    public String toString() {
        return "TriggerKafkaEventRequestDto [deviceInfo=" + deviceInfo + ", topic=" + topic + ", eventId=" + eventId
            + ", key=" + key + "]";
    }

    /**
     * Custom serialization method to write the object to the output stream.
     *
     * @param stream The output stream to write the object to.
     * @throws IOException If an I/O error occurs while writing the object.
     */
    private void writeObject(ObjectOutputStream stream)
        throws IOException {
        stream.defaultWriteObject();
    }

    /**
     * Custom deserialization method to read the object from the input stream.
     *
     * @param stream The input stream to read the object from.
     * @throws IOException            If an I/O error occurs while reading the object.
     * @throws ClassNotFoundException If the class of the serialized object cannot be found.
     */
    private void readObject(ObjectInputStream stream)
        throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
    }
}
