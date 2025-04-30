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

package org.eclipse.ecsp.deviceassociation.lib.mapper;

import org.eclipse.ecsp.services.shared.deviceinfo.model.DeviceInfo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class is responsible for mapping a row from a ResultSet to a DeviceInfo object.
 */
public class DeviceInfoRowMapper implements RowMapper<DeviceInfo> {

    /**
     * Maps a row from a ResultSet to a DeviceInfo object.
     *
     * @param resultSet the ResultSet containing the row data
     * @param arg1    the row number
     * @return the mapped DeviceInfo object
     * @throws SQLException if a database access error occurs
     */
    @Override
    public DeviceInfo mapRow(ResultSet resultSet, int arg1) throws SQLException {

        DeviceInfo bean = new DeviceInfo();

        bean.setHarmanId(resultSet.getString("HarmanID"));
        bean.setName(resultSet.getString("Name"));
        bean.setValue(resultSet.getString("Value"));

        return bean;
    }
}


