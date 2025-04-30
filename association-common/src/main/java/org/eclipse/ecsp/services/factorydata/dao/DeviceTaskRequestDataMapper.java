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

package org.eclipse.ecsp.services.factorydata.dao;

import org.eclipse.ecsp.services.factorydata.domain.HcpTaskState;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class maps the result set of a database query to a HcpTaskState object.
 */
public class DeviceTaskRequestDataMapper implements RowMapper<HcpTaskState> {
    private static final DeviceTaskRequestDataMapper DEVICE_TASK_REQUEST_DATA_MAPPER =
        new DeviceTaskRequestDataMapper();

    /**
     * Returns the singleton instance of DeviceTaskRequestDataMapper.
     *
     * @return The singleton instance of DeviceTaskRequestDataMapper.
     */
    public static DeviceTaskRequestDataMapper getDeviceSatetHistoryDataMapper() {
        return DEVICE_TASK_REQUEST_DATA_MAPPER;
    }

    /**
     * Maps a row from the result set to a HcpTaskState object.
     *
     * @param resultSet The result set containing the data to be mapped.
     * @param rowNum    The current row number.
     * @return The mapped HcpTaskState object.
     * @throws SQLException If an error occurs while mapping the data.
     */
    @Override
    public HcpTaskState mapRow(ResultSet resultSet, int rowNum) throws SQLException {

        HcpTaskState taskRequest = new HcpTaskState();
        taskRequest.setTaskId(resultSet.getLong("task_id"));
        taskRequest.setResult(resultSet.getString("result"));
        taskRequest.setStatus(resultSet.getString("task_status"));
        taskRequest.setStartTime(resultSet.getTimestamp("start_time"));
        taskRequest.setEndTime(resultSet.getTimestamp("end_time"));
        taskRequest.setTaskType(resultSet.getString("task_type"));

        return taskRequest;

    }

}
