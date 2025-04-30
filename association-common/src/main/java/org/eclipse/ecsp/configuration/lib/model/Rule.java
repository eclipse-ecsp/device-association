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
 * Represents a rule in the system.
 */
public class Rule {

    private long id;
    private String name;
    private String ruleDefinition;
    private long priority;
    private long configurationId;
    private boolean enable;
    private Timestamp createdOn;
    private long createdBy;
    private Timestamp modifiedOn;
    private String modifiedBy;

    /**
     * Get the ID of the rule.
     *
     * @return the ID of the rule
     */
    public long getId() {
        return id;
    }

    /**
     * Set the ID of the rule.
     *
     * @param id the ID of the rule to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Get the name of the rule.
     *
     * @return the name of the rule
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the rule.
     *
     * @param name the name of the rule to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the definition of the rule.
     *
     * @return the definition of the rule
     */
    public String getRuleDefinition() {
        return ruleDefinition;
    }

    /**
     * Set the definition of the rule.
     *
     * @param ruleDefinition the definition of the rule to set
     */
    public void setRuleDefinition(String ruleDefinition) {
        this.ruleDefinition = ruleDefinition;
    }

    /**
     * Get the priority of the rule.
     *
     * @return the priority of the rule
     */
    public long getPriority() {
        return priority;
    }

    /**
     * Set the priority of the rule.
     *
     * @param priority the priority of the rule to set
     */
    public void setPriority(long priority) {
        this.priority = priority;
    }

    /**
     * Get the configuration ID associated with the rule.
     *
     * @return the configuration ID associated with the rule
     */
    public long getConfigurationId() {
        return configurationId;
    }

    /**
     * Set the configuration ID associated with the rule.
     *
     * @param configurationId the configuration ID to set
     */
    public void setConfigurationId(long configurationId) {
        this.configurationId = configurationId;
    }

    /**
     * Check if the rule is enabled.
     *
     * @return true if the rule is enabled, false otherwise
     */
    public boolean isEnable() {
        return enable;
    }

    /**
     * Set the enable status of the rule.
     *
     * @param enable true to enable the rule, false to disable it
     */
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    /**
     * Get the creation timestamp of the rule.
     *
     * @return the creation timestamp of the rule
     */
    public Timestamp getCreatedOn() {
        if (createdOn != null) {
            return new Timestamp(createdOn.getTime());
        } else {
            return null;
        }
    }

    /**
     * Set the creation timestamp of the rule.
     *
     * @param createdOn the creation timestamp to set
     */
    public void setCreatedOn(Timestamp createdOn) {
        if (createdOn != null) {
            this.createdOn = new Timestamp(createdOn.getTime());
        } else {
            this.createdOn = null;
        }
    }

    /**
     * Get the ID of the user who created the rule.
     *
     * @return the ID of the user who created the rule
     */
    public long getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the ID of the user who created the rule.
     *
     * @param createdBy the ID of the user to set
     */
    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get the modification timestamp of the rule.
     *
     * @return the modification timestamp of the rule
     */
    public Timestamp getModifiedOn() {
        if (modifiedOn != null) {
            return new Timestamp(modifiedOn.getTime());
        } else {
            return null;
        }
    }

    /**
     * Set the modification timestamp of the rule.
     *
     * @param modifiedOn the modification timestamp to set
     */
    public void setModifiedOn(Timestamp modifiedOn) {
        if (modifiedOn != null) {
            this.modifiedOn = new Timestamp(modifiedOn.getTime());
        } else {
            this.modifiedOn = null;
        }
    }

    /**
     * Get the user who last modified the rule.
     *
     * @return the user who last modified the rule
     */
    public String getModifiedBy() {
        return modifiedBy;
    }

    /**
     * Set the user who last modified the rule.
     *
     * @param modifiedBy the user who last modified the rule to set
     */
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
}
