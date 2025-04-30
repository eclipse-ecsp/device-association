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

import com.fasterxml.jackson.annotation.JsonInclude;

import java.sql.Timestamp;

/**
 * Represents the state of an HCP task.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HcpTaskState {

    private long taskId;
    private String taskType;
    private String status;
    private Timestamp startTime;
    private Timestamp endTime;
    private String taskParams;
    private String result;

    /**
     * Returns the string representation of the HcpTaskState object.
     *
     * @return the string representation of the HcpTaskState object
     */
    @Override
    public String toString() {
        return "DeviceTaskRequest [taskId=" + taskId + ", taskType=" + taskType + ", Status=" + status
            +            ", startTime=" + startTime
            + ", endTime=" + endTime + ", taskParams=" + taskParams + ", result=" + result + "]";
    }

    /**
     * Returns the start time of the task.
     *
     * @return the start time of the task
     */
    public Timestamp getStartTime() {
        if (startTime != null) {
            return new Timestamp(startTime.getTime());
        } else {
            return null;
        }
    }

    /**
     * Sets the start time of the task.
     *
     * @param startTime the start time of the task
     */
    public void setStartTime(Timestamp startTime) {
        if (startTime != null) {
            this.startTime = new Timestamp(startTime.getTime());
        } else {
            this.startTime = null;
        }
    }

    /**
     * Returns the end time of the task.
     *
     * @return the end time of the task
     */
    public Timestamp getEndTime() {
        if (endTime != null) {
            return new Timestamp(endTime.getTime());
        } else {
            return null;
        }
    }

    /**
     * Sets the end time of the task.
     *
     * @param endTime the end time of the task
     */
    public void setEndTime(Timestamp endTime) {
        if (endTime != null) {
            this.endTime = new Timestamp(endTime.getTime());
        } else {
            this.endTime = null;
        }
    }

    /**
     * Returns the result of the task.
     *
     * @return the result of the task
     */
    public String getResult() {
        return result;
    }

    /**
     * Sets the result of the task.
     *
     * @param result the result of the task
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * Returns the task ID.
     *
     * @return the task ID
     */
    public long getTaskId() {
        return taskId;
    }

    /**
     * Sets the task ID.
     *
     * @param taskId the task ID
     */
    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    /**
     * Returns the task type.
     *
     * @return the task type
     */
    public String getTaskType() {
        return taskType;
    }

    /**
     * Sets the task type.
     *
     * @param taskType the task type
     */
    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    /**
     * Returns the status of the task.
     *
     * @return the status of the task
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the task.
     *
     * @param status the status of the task
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns the task parameters.
     *
     * @return the task parameters
     */
    public String getTaskParams() {
        return taskParams;
    }

    /**
     * Sets the task parameters.
     *
     * @param taskParams the task parameters
     */
    public void setTaskParams(String taskParams) {
        this.taskParams = taskParams;
    }

}
