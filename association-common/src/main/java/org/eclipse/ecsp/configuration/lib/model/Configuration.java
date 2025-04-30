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
import java.util.Arrays;

/**
 * Represents a configuration object.
 */
public class Configuration {

    private Long id;
    private String configurationName;
    private String fileName;
    private byte[] fileData;
    private Timestamp createdAt;
    private String createdBy;
    private Timestamp lastUpdatedAt;
    private String lastUpdatedBy;
    private Boolean deleted;
    private String url;
    private String s3Url;

    /**
     * Default constructor for Configuration.
     */
    public Configuration() {

    }

    /**
     * Constructs a Configuration object with the specified parameters.
     *
     * @param id                 the ID of the configuration
     * @param fileName           the name of the file
     * @param fileData           the data of the file
     * @param createdAt          the timestamp when the configuration was created
     * @param lastUpdatedAt      the timestamp when the configuration was last updated
     * @param deleted            indicates if the configuration is deleted
     * @param configurationName  the name of the configuration
     */
    public Configuration(Long id, String fileName, byte[] fileData, Timestamp createdAt,
                         Timestamp lastUpdatedAt, Boolean deleted, String configurationName) {
        super();
        this.id = id;
        this.fileName = fileName;
        this.fileData = fileData;
        this.createdAt = createdAt;
        this.lastUpdatedAt = lastUpdatedAt;
        this.deleted = deleted;
        this.configurationName = configurationName;
    }

    /**
     * Gets the ID of the configuration.
     *
     * @return the ID of the configuration
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the configuration.
     *
     * @param id the ID of the configuration
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the name of the file.
     *
     * @return the name of the file
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the name of the file.
     *
     * @param fileName the name of the file
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Gets the data of the file.
     *
     * @return the data of the file
     */
    public byte[] getFileData() {
        return fileData;
    }

    /**
     * Sets the data of the file.
     *
     * @param fileData the data of the file
     */
    public void setFileData(byte[] fileData) {
        if (fileData == null) {
            this.fileData = new byte[0];
        } else {
            this.fileData = Arrays.copyOf(fileData, fileData.length);
        }
    }

    /**
     * Gets the timestamp when the configuration was created.
     *
     * @return the timestamp when the configuration was created
     */
    public Timestamp getCreatedAt() {
        if (createdAt != null) {
            return new Timestamp(createdAt.getTime());
        } else {
            return null;
        }
    }

    /**
     * Sets the timestamp when the configuration was created.
     *
     * @param createdAt the timestamp when the configuration was created
     */
    public void setCreatedAt(Timestamp createdAt) {
        if (createdAt != null) {
            this.createdAt = new Timestamp(createdAt.getTime());
        } else {
            this.createdAt = null;
        }
    }

    /**
     * Gets the timestamp when the configuration was last updated.
     *
     * @return the timestamp when the configuration was last updated
     */
    public Timestamp getLastUpdatedAt() {
        if (lastUpdatedAt != null) {
            return new Timestamp(lastUpdatedAt.getTime());
        } else {
            return null;
        }
    }

    /**
     * Sets the timestamp when the configuration was last updated.
     *
     * @param lastUpdatedAt the timestamp when the configuration was last updated
     */
    public void setLastUpdatedAt(Timestamp lastUpdatedAt) {
        if (lastUpdatedAt != null) {
            this.lastUpdatedAt = new Timestamp(lastUpdatedAt.getTime());
        } else {
            this.lastUpdatedAt = null;
        }
    }

    /**
     * Gets the deleted status of the configuration.
     *
     * @return true if the configuration is deleted, false otherwise
     */
    public Boolean getDeleted() {
        return deleted;
    }

    /**
     * Sets the deleted status of the configuration.
     *
     * @param deleted true if the configuration is deleted, false otherwise
     */
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * Gets the creator of the configuration.
     *
     * @return the creator of the configuration
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the creator of the configuration.
     *
     * @param createdBy the creator of the configuration
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Gets the last updater of the configuration.
     *
     * @return the last updater of the configuration
     */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    /**
     * Sets the last updater of the configuration.
     *
     * @param lastUpdatedBy the last updater of the configuration
     */
    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    /**
     * Gets the name of the configuration.
     *
     * @return the name of the configuration
     */
    public String getConfigurationName() {
        return configurationName;
    }

    /**
     * Sets the name of the configuration.
     *
     * @param configurationName the name of the configuration
     */
    public void setConfigurationName(String configurationName) {
        this.configurationName = configurationName;
    }

    /**
     * Gets the URL of the configuration.
     *
     * @return the URL of the configuration
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the URL of the configuration.
     *
     * @param url the URL of the configuration
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets the S3 URL of the configuration.
     *
     * @return the S3 URL of the configuration
     */
    public String getS3Url() {
        return s3Url;
    }

    /**
     * Sets the S3 URL of the configuration.
     *
     * @param s3Url the S3 URL of the configuration
     */
    public void setS3Url(String s3Url) {
        this.s3Url = s3Url;
    }

    /**
     * Generates a string representation of the Configuration object.
     *
     * @return a string representation of the Configuration object
     */
    @Override
    public String toString() {
        return "Configuration [id=" + id + ", configurationName=" + configurationName + ", fileName=" + fileName
            + ", fileData="
            + Arrays.toString(fileData) + ", createdAt=" + createdAt + ", createdBy=" + createdBy + ", lastUpdatedAt="
            + lastUpdatedAt
            + ", lastUpdatedBy=" + lastUpdatedBy + ", deleted=" + deleted + ", url=" + url + ", s3Url=" + s3Url + "]";
    }

    /**
     * Generates a hash code for the Configuration object.
     *
     * @return the hash code for the Configuration object
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    /**
     * Checks if the Configuration object is equal to another object.
     *
     * @param obj the object to compare with
     * @return true if the Configuration object is equal to the other object, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Configuration other = (Configuration) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

}
