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

import org.eclipse.ecsp.services.factorydata.domain.DeviceStateHistory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class is responsible for mapping the result set of a database query to a DeviceStateHistory object.
 */
public class DeviceStateHistoryDataMapper implements RowMapper<DeviceStateHistory> {

    private static final DeviceStateHistoryDataMapper DEVICE_STATE_HISTORY_DATA_MAPPER =
        new DeviceStateHistoryDataMapper();
    private static final String STATE = "state";

    /**
     * Returns the singleton instance of DeviceStateHistoryDataMapper.
     *
     * @return The singleton instance of DeviceStateHistoryDataMapper.
     */
    public static DeviceStateHistoryDataMapper getDeviceSatetHistoryDataMapper() {
        return DEVICE_STATE_HISTORY_DATA_MAPPER;
    }

    /**
     * Maps a row from the result set to a DeviceStateHistory object.
     *
     * @param resultSet The result set containing the data to be mapped.
     * @param rowNum    The row number of the result set.
     * @return The mapped DeviceStateHistory object.
     * @throws SQLException If an error occurs while accessing the result set.
     */
    @Override
    public DeviceStateHistory mapRow(ResultSet resultSet, int rowNum) throws SQLException {

        DeviceStateHistory stateHistory = new DeviceStateHistory();
        stateHistory.setState(resultSet.getString(STATE));
        stateHistory.setStateTimestamp(resultSet.getTimestamp("created_timestamp"));
        stateHistory.setManufacturingDate(resultSet.getTimestamp("manufacturing_date"));
        stateHistory.setBssid(resultSet.getString("bssid"));
        stateHistory.setImei(resultSet.getString("imei"));
        stateHistory.setSerialNumber(resultSet.getString("serial_number"));
        stateHistory.setPlatformVersion(resultSet.getString("platform_version"));
        stateHistory.setIccid(resultSet.getString("iccid"));
        stateHistory.setSsid(resultSet.getString("ssid"));
        stateHistory.setMsisdn(resultSet.getString("msisdn"));
        stateHistory.setImsi(resultSet.getString("imsi"));
        stateHistory.setRecordDate(resultSet.getTimestamp("record_date"));
        stateHistory.setCreatedDate(resultSet.getTimestamp("factory_created_date"));
        stateHistory.setFactoryAdmin(resultSet.getString("factory_admin"));
        stateHistory.setFaulty(resultSet.getString(STATE).equalsIgnoreCase("FAULTY") ? Boolean.TRUE : Boolean.FALSE);
        stateHistory.setStolen(resultSet.getString(STATE).equalsIgnoreCase("STOLEN") ? Boolean.TRUE : Boolean.FALSE);
        stateHistory.setPackageSerialNumber(resultSet.getString("package_serial_number"));
        stateHistory.setModel(resultSet.getString("model"));

        return stateHistory;

    }

}
