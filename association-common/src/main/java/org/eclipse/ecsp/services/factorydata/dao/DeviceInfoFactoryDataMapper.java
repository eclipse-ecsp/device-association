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

import org.eclipse.ecsp.services.factorydata.domain.DeviceInfoFactoryData;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.eclipse.ecsp.common.CommonConstants.ID;

/**
 * This class maps the result set of a database query to a DeviceInfoFactoryData object.
 */
public class DeviceInfoFactoryDataMapper implements RowMapper<DeviceInfoFactoryData> {

    private static final DeviceInfoFactoryDataMapper DEVICE_INFO_FACTORY_DATA_MAPPER =
        new DeviceInfoFactoryDataMapper();

    /**
     * Returns the singleton instance of DeviceInfoFactoryDataMapper.
     *
     * @return The singleton instance of DeviceInfoFactoryDataMapper.
     */
    public static DeviceInfoFactoryDataMapper getDeviceInfoFactoryDataMapper() {
        return DEVICE_INFO_FACTORY_DATA_MAPPER;
    }

    /**
     * Maps a row of the result set to a DeviceInfoFactoryData object.
     *
     * @param resultSet The result set containing the data to be mapped.
     * @param rowNum    The current row number.
     * @return The mapped DeviceInfoFactoryData object.
     * @throws SQLException If an error occurs while mapping the data.
     */
    @Override
    public DeviceInfoFactoryData mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        DeviceInfoFactoryData factoryData = new DeviceInfoFactoryData();
        factoryData.setId(resultSet.getLong(ID));
        factoryData.setManufacturingDate(resultSet.getTimestamp("manufacturing_date"));
        factoryData.setModel(resultSet.getString("model"));
        factoryData.setBssid(resultSet.getString("bssid"));
        factoryData.setImei(resultSet.getString("imei"));
        factoryData.setSerialNumber(resultSet.getString("serial_number"));
        factoryData.setPlatformVersion(resultSet.getString("platform_version"));
        factoryData.setIccid(resultSet.getString("iccid"));
        factoryData.setSsid(resultSet.getString("ssid"));
        factoryData.setMsisdn(resultSet.getString("msisdn"));
        factoryData.setImsi(resultSet.getString("imsi"));
        factoryData.setRecordDate(resultSet.getTimestamp("record_date"));
        factoryData.setCreatedDate(resultSet.getTimestamp("created_date"));
        factoryData.setFactoryAdmin(resultSet.getString("factory_admin"));
        factoryData.setState(resultSet.getString("state"));
        factoryData.setStolen(resultSet.getBoolean("isStolen"));
        factoryData.setFaulty(resultSet.getBoolean("isFaulty"));
        factoryData.setPackageSerialNumber(resultSet.getString("package_serial_number"));
        factoryData.setDeviceType(resultSet.getString("device_type"));
        return factoryData;
    }
}
