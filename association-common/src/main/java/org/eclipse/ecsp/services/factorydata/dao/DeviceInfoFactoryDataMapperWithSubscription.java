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

import org.eclipse.ecsp.services.factorydata.domain.DeviceInfoFactoryDataWithSubscription;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.UUID;

import static org.eclipse.ecsp.common.CommonConstants.ID;

/**
 * This class is responsible for mapping the result set of a database query to a DeviceInfoFactoryDataWithSubscription
 * object.
 */
public class DeviceInfoFactoryDataMapperWithSubscription implements RowMapper<DeviceInfoFactoryDataWithSubscription> {
    private static final DeviceInfoFactoryDataMapperWithSubscription DEVICE_INFO_FACTORY_DATA_MAPPER_WITH_SUBSCRIPTION =
        new DeviceInfoFactoryDataMapperWithSubscription();

    /**
     * Returns the singleton instance of DeviceInfoFactoryDataMapperWithSubscription.
     *
     * @return The singleton instance of DeviceInfoFactoryDataMapperWithSubscription.
     */
    public static DeviceInfoFactoryDataMapperWithSubscription getDeviceInfoFactoryDataWithSubscriptionMapper() {
        return DEVICE_INFO_FACTORY_DATA_MAPPER_WITH_SUBSCRIPTION;
    }

    /**
     * Maps a row from the result set to a DeviceInfoFactoryDataWithSubscription object.
     *
     * @param resultSet The result set containing the data to be mapped.
     * @param rowNum    The current row number.
     * @return The mapped DeviceInfoFactoryDataWithSubscription object.
     * @throws SQLException If an error occurs while accessing the result set.
     */
    @Override
    public DeviceInfoFactoryDataWithSubscription mapRow(ResultSet resultSet, int rowNum) throws SQLException {

        ResultSetMetaData rsmd = resultSet.getMetaData();
        Boolean hasAssociationTableInfo = false;
        Boolean hasVinDetailsTableInfo = false;
        int columns = rsmd.getColumnCount();

        for (int x = 1; x <= columns; x++) {
            if ("harman_id".equals(rsmd.getColumnName(x))) {
                hasAssociationTableInfo = true;
            }
            if ("vin".equals(rsmd.getColumnName(x))) {
                hasVinDetailsTableInfo = true;
            }
        }
        DeviceInfoFactoryDataWithSubscription factoryData = new DeviceInfoFactoryDataWithSubscription();
        factoryData.setId(resultSet.getLong(ID));
        factoryData.setManufacturingDate(resultSet.getTimestamp("manufacturing_date"));
        factoryData.setModel(resultSet.getString("model"));
        String bssid = resultSet.getString("bssid");
        String imei = resultSet.getString("imei");
        String iccid = resultSet.getString("iccid");
        String ssid = resultSet.getString("ssid");
        String msisdn = resultSet.getString("msisdn");
        String imsi = resultSet.getString("imsi");
        String platformVersion = resultSet.getString("platform_version");


        factoryData.setBssid(isUuid(bssid));
        factoryData.setImei(isUuid(imei));
        factoryData.setSerialNumber(resultSet.getString("serial_number"));
        factoryData.setPlatformVersion(isUuid(platformVersion));
        factoryData.setIccid(isUuid(iccid));
        factoryData.setSsid(isUuid(ssid));
        factoryData.setMsisdn(isUuid(msisdn));
        factoryData.setImsi(isUuid(imsi));
        factoryData.setRecordDate(resultSet.getTimestamp("record_date"));
        factoryData.setCreatedDate(resultSet.getTimestamp("created_date"));
        factoryData.setFactoryAdmin(resultSet.getString("factory_admin"));
        factoryData.setState(resultSet.getString("state"));
        factoryData.setStolen(resultSet.getBoolean("isStolen"));
        factoryData.setFaulty(resultSet.getBoolean("isFaulty"));
        factoryData.setPackageSerialNumber(resultSet.getString("package_serial_number"));

        if (Boolean.TRUE.equals(hasAssociationTableInfo)) {
            factoryData.setDeviceId(resultSet.getString("harman_id"));
        }
        if (Boolean.TRUE.equals(hasVinDetailsTableInfo)) {
            factoryData.setVin(resultSet.getString("vin"));
        }
        return factoryData;
    }

    /**
     * Checks if the given string is a valid UUID.
     *
     * @param string The string to be checked.
     * @return The input string if it is not a valid UUID, otherwise null.
     */
    private String isUuid(String string) {
        try {
            UUID.fromString(string);
            return null;
        } catch (Exception ex) {
            return string;
        }
    }
}
