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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a device association event.
 */
public class DeviceAssociationEvent {

    public static final String VERSION = "1.0";
    /**
     * [ { "EventID":"VehicleAssociation | VehicleDisAssociation",.
     * "Version":"1.0", "Timestamp":1357000710057, · "Data": { "userId":
     * "schunchu", "PDID": "HU1234dsaf" }, "Timezone": 240 } ]
     */
    private String eventId;
    private String version;
    private long timestamp;
    private int timezone;
    private EventData data;

    /**
     * Constructs a new DeviceAssociationEvent object.
     *
     * @param eventId   The event ID.
     * @param version   The version.
     * @param timestamp The timestamp.
     * @param timezone  The timezone.
     * @param data      The event data.
     */
    public DeviceAssociationEvent(String eventId, String version, long timestamp, int timezone, EventData data) {
        super();
        this.eventId = eventId;
        this.version = version;
        this.timestamp = timestamp;
        this.timezone = timezone;
        this.data = data;
    }

    /**
     * Constructs a new empty DeviceAssociationEvent object.
     */
    public DeviceAssociationEvent() {
        super();
    }

    /**
     * Gets the event ID.
     *
     * @return The event ID.
     */
    @JsonProperty("EventID")
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
     * Gets the version.
     *
     * @return The version.
     */
    @JsonProperty("Version")
    public String getVersion() {
        return version;
    }

    /**
     * Sets the version.
     *
     * @param version The version to set.
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Gets the timestamp.
     *
     * @return The timestamp.
     */
    @JsonProperty("Timestamp")
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp.
     *
     * @param timestamp The timestamp to set.
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the timezone.
     *
     * @return The timezone.
     */
    @JsonProperty("Timezone")
    public int getTimezone() {
        return timezone;
    }

    /**
     * Sets the timezone.
     *
     * @param timezone The timezone to set.
     */
    public void setTimezone(int timezone) {
        this.timezone = timezone;
    }

    /**
     * Gets the event data.
     *
     * @return The event data.
     */
    @JsonProperty("Data")
    public EventData getData() {
        return data;
    }

    /**
     * Sets the event data.
     *
     * @param data The event data to set.
     */
    public void setData(EventData data) {
        this.data = data;
    }

    /**
     * Returns a string representation of the DeviceAssociationEvent object.
     *
     * @return A string representation of the DeviceAssociationEvent object.
     */
    @Override
    public String toString() {
        return "DeviceAssociationEvent [EventID=" + eventId + ", Version=" + version + ", Timestamp=" + timestamp
                + ", Timezone=" + timezone + ", data=" + data + "]";
    }

    /**
     * Represents the event data.
     */
    public static class EventData {

        private String userId;
        private String pdid;

        /**
         * Constructs a new EventData object.
         *
         * @param userId The user ID.
         * @param pdid   The PDID.
         */
        public EventData(String userId, String pdid) {
            super();
            this.userId = userId;
            this.pdid = pdid;
        }

        /**
         * Constructs a new empty EventData object.
         */
        public EventData() {
            super();
        }

        /**
         * Gets the user ID.
         *
         * @return The user ID.
         */
        public String getUserId() {
            return userId;
        }

        /**
         * Sets the user ID.
         *
         * @param userId The user ID to set.
         */
        public void setUserId(String userId) {
            this.userId = userId;
        }

        /**
         * Gets the PDID.
         *
         * @return The PDID.
         */
        @JsonProperty("PDID")
        public String getPdid() {
            return pdid;
        }

        /**
         * Sets the PDID.
         *
         * @param pdid The PDID to set.
         */
        public void setPdid(String pdid) {
            this.pdid = pdid;
        }

        /**
         * Returns a string representation of the EventData object.
         *
         * @return A string representation of the EventData object.
         */
        @Override
        public String toString() {
            return "EventData [userId=" + userId + ", PDID=" + pdid + "]";
        }

    }

}
