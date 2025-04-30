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

package org.eclipse.ecsp.services.factorydata.domain;

/**
 * Represents a report data transfer object.
 */
public class ReportDto {

    private String type;
    private String reportformat;
    private long since;
    private long until;

    /**
     * Gets the type of the report.
     *
     * @return The type of the report.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the report.
     *
     * @param type The type of the report.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the format of the report.
     *
     * @return The format of the report.
     */
    public String getReportformat() {
        return reportformat;
    }

    /**
     * Sets the format of the report.
     *
     * @param reportformat The format of the report.
     */
    public void setReportformat(String reportformat) {
        this.reportformat = reportformat;
    }

    /**
     * Gets the start time of the report.
     *
     * @return The start time of the report.
     */
    public long getSince() {
        return since;
    }

    /**
     * Sets the start time of the report.
     *
     * @param since The start time of the report.
     */
    public void setSince(long since) {
        this.since = since;
    }

    /**
     * Gets the end time of the report.
     *
     * @return The end time of the report.
     */
    public long getUntil() {
        return until;
    }

    /**
     * Sets the end time of the report.
     *
     * @param until The end time of the report.
     */
    public void setUntil(long until) {
        this.until = until;
    }

    /**
     * Returns a string representation of the ReportDto object.
     *
     * @return A string representation of the ReportDto object.
     */
    @Override
    public String toString() {
        return "DeviceStateReport [type=" + type + ", reportformat=" + reportformat + ", since=" + since + ", until=" 
            +            until + "]";
    }

}
