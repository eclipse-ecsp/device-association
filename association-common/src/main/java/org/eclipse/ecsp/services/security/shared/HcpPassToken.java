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

package org.eclipse.ecsp.services.security.shared;


import java.io.Serializable;
import java.util.List;

/**
 * Represents a HcpPassToken object used for security purposes.
 */
public class HcpPassToken implements Serializable {

    private static final long serialVersionUID = 1117320457123660437L;
    private String id;
    private long timestamp;
    private String key;
    private List<String> roles;

    /**
     * Default constructor.
     */
    public HcpPassToken() {

    }

    /**
     * Constructs a new HcpPassToken object with the specified id, timestamp, and key.
     *
     * @param id        the id of the HcpPassToken
     * @param timestamp the timestamp of the HcpPassToken
     * @param key       the key of the HcpPassToken
     */
    public HcpPassToken(String id, long timestamp, String key) {
        this.id = id;
        this.timestamp = timestamp;
        this.key = key;
    }

    /**
     * Constructs a new HcpPassToken with the specified parameters.
     *
     * @param id        the ID of the token
     * @param timestamp the timestamp of the token
     * @param key       the key of the token
     * @param roles     the roles associated with the token
     */
    public HcpPassToken(String id, long timestamp, String key, List<String> roles) {
        this.id = id;
        this.timestamp = timestamp;
        this.key = key;
        this.roles = roles;
    }

    /**
     * Gets the id of the token.
     *
     * @return the id of the token
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id of the token.
     *
     * @param id the id of the token
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the key of the token.
     *
     * @return the key of the token
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the key of the token.
     *
     * @param key the key of the token
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Gets the timestamp of the token.
     *
     * @return the timestamp of the token
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp of the token.
     *
     * @param timestamp the timestamp of the token
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the roles associated with the token.
     *
     * @return the roles associated with the token
     */
    public List<String> getRoles() {
        return roles;
    }

    /**
     * Sets the roles associated with the token.
     *
     * @param roles the roles associated with the token
     */
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    /**
     * Returns a string representation of the HcpPassToken object.
     *
     * @return a string representation of the HcpPassToken object
     */
    @Override
    public String toString() {
        return "HcpPassToken [id=" + id + ", timestamp=" + timestamp + ", key=" + key + ", roles=" + roles + "]";
    }

}
