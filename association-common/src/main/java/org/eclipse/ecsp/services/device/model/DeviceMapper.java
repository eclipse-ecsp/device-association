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

package org.eclipse.ecsp.services.device.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.eclipse.ecsp.common.CommonConstants.HARMAN_ID;
import static org.eclipse.ecsp.common.CommonConstants.ID;
import static org.eclipse.ecsp.common.CommonConstants.PASS_CODE;
import static org.eclipse.ecsp.common.CommonConstants.RANDOM_NUMBER;
import static org.eclipse.ecsp.common.CommonConstants.REGISTERED_SCOPE_ID;

/**
 * The DeviceMapper class is responsible for mapping the result set of a database query to a Device object.
 */
public class DeviceMapper implements RowMapper<Device> {
    
    /**
     * Maps a row from the ResultSet to a Device object.
     *
     * @param resultSet the ResultSet containing the data for the row
     * @param rowNum the row number
     * @return the mapped Device object
     * @throws SQLException if an error occurs while accessing the ResultSet
     */
    @Override
    public Device mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Device device = new Device();
        device.setHarmanId(resultSet.getString(HARMAN_ID));
        device.setPasscode(resultSet.getString(PASS_CODE));
        device.setRandomNumber(resultSet.getLong(RANDOM_NUMBER));
        device.setRegisteredScopeId(resultSet.getString(REGISTERED_SCOPE_ID));
        device.setId(resultSet.getLong(ID));
        return device;
    }

}
