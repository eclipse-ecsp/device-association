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

package org.eclipse.ecsp.configuration.lib.model;

import java.sql.Timestamp;

/**
 * Represents a configuration update log.
 */
public class ConfigurationUpdateLog {

    private long id;
    private String harmanId;
    private long configurationId;
    private String ruleIdentified;
    private boolean status;
    private long createdBy;
    private Timestamp modifiedOn;
    private long modifiedBy;

    /**
     * Get the ID of the configuration update log.
     *
     * @return the ID
     */
    public long getId() {
        return id;
    }

    /**
     * Set the ID of the configuration update log.
     *
     * @param id the ID to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Get the Harman ID associated with the configuration update log.
     *
     * @return the Harman ID
     */
    public String getHarmanId() {
        return harmanId;
    }

    /**
     * Set the Harman ID associated with the configuration update log.
     *
     * @param harmanId the Harman ID to set
     */
    public void setHarmanId(String harmanId) {
        this.harmanId = harmanId;
    }

    /**
     * Get the configuration ID associated with the configuration update log.
     *
     * @return the configuration ID
     */
    public long getConfigurationId() {
        return configurationId;
    }

    /**
     * Set the configuration ID associated with the configuration update log.
     *
     * @param configurationId the configuration ID to set
     */
    public void setConfigurationId(long configurationId) {
        this.configurationId = configurationId;
    }

    /**
     * Get the rule identified for the configuration update log.
     *
     * @return the rule identified
     */
    public String getRuleIdentified() {
        return ruleIdentified;
    }

    /**
     * Set the rule identified for the configuration update log.
     *
     * @param ruleIdentified the rule identified to set
     */
    public void setRuleIdentified(String ruleIdentified) {
        this.ruleIdentified = ruleIdentified;
    }

    /**
     * Get the status of the configuration update log.
     *
     * @return the status
     */
    public boolean isStatus() {
        return status;
    }

    /**
     * Set the status of the configuration update log.
     *
     * @param status the status to set
     */
    public void setStatus(boolean status) {
        this.status = status;
    }

    /**
     * Get the ID of the user who created the configuration update log.
     *
     * @return the ID of the user who created the log
     */
    public long getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the ID of the user who created the configuration update log.
     *
     * @param createdBy the ID of the user who created the log to set
     */
    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the modified timestamp of the configuration update log.
     *
     * @return the modified timestamp
     */
    public Timestamp getModifiedOn() {
        if (modifiedOn != null) {
            return new Timestamp(modifiedOn.getTime());
        } else {
            return null;
        }
    }

    /**
     * Set the modified timestamp of the configuration update log.
     *
     * @param modifiedOn the modified timestamp to set
     */
    public void setModifiedOn(Timestamp modifiedOn) {
        if (modifiedOn != null) {
            this.modifiedOn = new Timestamp(modifiedOn.getTime());
        } else {
            this.modifiedOn = null;
        }
    }

    /**
     * Get the ID of the user who modified the configuration update log.
     *
     * @return the ID of the user who modified the log
     */
    public long getModifiedBy() {
        return modifiedBy;
    }

    /**
     * Set the ID of the user who modified the configuration update log.
     *
     * @param modifiedBy the ID of the user who modified the log to set
     */
    public void setModifiedBy(long modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
}
