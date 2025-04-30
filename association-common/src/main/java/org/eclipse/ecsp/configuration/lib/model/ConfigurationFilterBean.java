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

import org.eclipse.ecsp.services.shared.db.FilterField;
import org.eclipse.ecsp.services.shared.db.FilterField.Range;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Represents a configuration filter bean used for filtering configurations.
 */
public class ConfigurationFilterBean {

    @FilterField(dbname = "\"Configuration\".\"ID\"")
    private ArrayList<Long> configurationIdList;

    @FilterField(dbname = "\"Configuration\".\"FileName\"")
    private String fileName;

    @FilterField(dbname = "\"Configuration\".\"CreatedAt\"", range = Range.MIN)
    private Timestamp createdAtFrom;

    @FilterField(dbname = "\"Configuration\".\"CreatedAt\"", range = Range.MAX)
    private Timestamp createdAtTo;

    @FilterField(dbname = "\"Configuration\".\"Deleted\"")
    private Boolean deleted;

    @FilterField(dbname = "\"Configuration\".\"CreatedBy\"")
    private String createdBy;

    /**
     * Get the list of configuration IDs.
     *
     * @return The list of configuration IDs.
     */
    public ArrayList<Long> getConfigurationIdList() {
        return configurationIdList;
    }

    /**
     * Set the list of configuration IDs.
     *
     * @param configurationIdList The list of configuration IDs.
     */
    public void setConfigurationIdList(ArrayList<Long> configurationIdList) {
        this.configurationIdList = configurationIdList;
    }

    /**
     * Get the created at timestamp from.
     *
     * @return The created at timestamp from.
     */
    public Timestamp getCreatedAtFrom() {
        if (createdAtFrom != null) {
            return new Timestamp(createdAtFrom.getTime());
        } else {
            return null;
        }
    }

    /**
     * Set the created at timestamp from.
     *
     * @param createdAtFrom The created at timestamp from.
     */
    public void setCreatedAtFrom(Timestamp createdAtFrom) {
        if (createdAtFrom != null) {
            this.createdAtFrom = new Timestamp(createdAtFrom.getTime());
        } else {
            this.createdAtFrom = null;
        }
    }

    /**
     * Get the created at timestamp to.
     *
     * @return The created at timestamp to.
     */
    public Timestamp getCreatedAtTo() {
        if (createdAtTo != null) {
            return new Timestamp(createdAtTo.getTime());
        } else {
            return null;
        }
    }

    /**
     * Set the created at timestamp to.
     *
     * @param createdAtTo The created at timestamp to.
     */
    public void setCreatedAtTo(Timestamp createdAtTo) {
        if (createdAtTo != null) {
            this.createdAtTo = new Timestamp(createdAtTo.getTime());
        } else {
            this.createdAtTo = null;
        }
    }

    /**
     * Get the deleted flag.
     *
     * @return The deleted flag.
     */
    public Boolean getDeleted() {
        return deleted;
    }

    /**
     * Set the deleted flag.
     *
     * @param deleted The deleted flag.
     */
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * Get the file name.
     *
     * @return The file name.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Set the file name.
     *
     * @param fileName The file name.
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Get the created by.
     *
     * @return The created by.
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Set the created by.
     *
     * @param createdBy The created by.
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Returns a string representation of the ConfigurationFilterBean object.
     *
     * @return A string representation of the ConfigurationFilterBean object.
     */
    @Override
    public String toString() {
        return "ConfigurationFilterBean [configurationIdList="
            + configurationIdList + ", fileName=" + fileName
            + ", createdAtFrom=" + createdAtFrom + ", createdAtTo="
            + createdAtTo + ", deleted=" + deleted + ", createdBy="
            + createdBy + "]";
    }

}
