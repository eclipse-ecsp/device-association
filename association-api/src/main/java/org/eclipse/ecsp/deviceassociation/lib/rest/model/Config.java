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

/**
 * Represents a configuration object.
 */
class Config {

    private Object data;

    private String domain;

    private String version;

    private String command;

    /**
     * Gets the data associated with the configuration.
     *
     * @return The data associated with the configuration.
     */
    public Object getData() {
        return data;
    }

    /**
     * Sets the data associated with the configuration.
     *
     * @param data The data to be associated with the configuration.
     */
    public void setData(Object data) {
        this.data = data;
    }

    /**
     * Gets the domain of the configuration.
     *
     * @return The domain of the configuration.
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Sets the domain of the configuration.
     *
     * @param domain The domain to be set for the configuration.
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * Gets the version of the configuration.
     *
     * @return The version of the configuration.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the version of the configuration.
     *
     * @param version The version to be set for the configuration.
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Gets the command of the configuration.
     *
     * @return The command of the configuration.
     */
    public String getCommand() {
        return command;
    }

    /**
     * Sets the command of the configuration.
     *
     * @param command The command to be set for the configuration.
     */
    public void setCommand(String command) {
        this.command = command;
    }

}
