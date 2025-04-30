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
 * Represents a token used for password recovery.
 */
public class ForgotPasswordToken implements Serializable {

    private static final long serialVersionUID = 6731493839607228123L;
    private String id;
    private long timestamp;
    private String key;
    private long changePasswordSerial;
    private List<String> roles;

    /**
     * Constructs a new instance of the {@code ForgotPasswordToken} class.
     */
    public ForgotPasswordToken() {

    }

    /**
     * Constructs a new instance of the {@code ForgotPasswordToken} class with the specified parameters.
     *
     * @param id The ID of the token.
     * @param timestamp The timestamp when the token was created.
     * @param key The key associated with the token.
     * @param changePasswordSerial The serial number for changing the password.
     */
    public ForgotPasswordToken(String id, long timestamp, String key, long changePasswordSerial) {
        this.id = id;
        this.timestamp = timestamp;
        this.key = key;
        this.changePasswordSerial = changePasswordSerial;
    }

    /**
     * Constructs a new instance of the {@code ForgotPasswordToken} class with the specified parameters.
     *
     * @param id        the ID of the token
     * @param timestamp the timestamp of when the token was created
     * @param key       the key associated with the token
     * @param roles     the list of roles associated with the token
     */
    public ForgotPasswordToken(String id, long timestamp, String key, List<String> roles) {

        this.id = id;
        this.timestamp = timestamp;
        this.key = key;
        this.roles = roles;
    }

    /**
     * Gets the ID of the token.
     *
     * @return The ID of the token.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the token.
     *
     * @param id The ID of the token.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the key associated with the token.
     *
     * @return The key associated with the token.
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the key associated with the token.
     *
     * @param key The key associated with the token.
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Gets the timestamp when the token was created.
     *
     * @return The timestamp when the token was created.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp when the token was created.
     *
     * @param timestamp The timestamp when the token was created.
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the list of roles associated with the token.
     *
     * @return The list of roles associated with the token.
     */
    public List<String> getRoles() {
        return roles;
    }

    /**
     * Sets the list of roles associated with the token.
     *
     * @param roles The list of roles associated with the token.
     */
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    /**
     * Gets the serial number for changing the password.
     *
     * @return The serial number for changing the password.
     */
    public long getChangePasswordSerial() {
        return changePasswordSerial;
    }

    /**
     * Sets the serial number for changing the password.
     *
     * @param changePasswordSerial The serial number for changing the password.
     */
    public void setChangePasswordSerial(long changePasswordSerial) {
        this.changePasswordSerial = changePasswordSerial;
    }

    /**
     * Returns a string representation of the {@code ForgotPasswordToken} object.
     *
     * @return A string representation of the {@code ForgotPasswordToken} object.
     */
    @Override
    public String toString() {
        return "ForgotPasswordToken [id=" + id + ", timestamp=" + timestamp + ", key=" + key + ", changePasswordSerial="
            + changePasswordSerial + ", roles=" + roles + "]";
    }

}
