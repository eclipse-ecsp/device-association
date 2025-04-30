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

package org.eclipse.ecsp.services.shared.dao;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.ecsp.services.shared.db.HcpInfo;
import org.eclipse.ecsp.services.shared.db.HcpInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.eclipse.ecsp.common.CommonConstants.HARMAN_ID;
import static org.eclipse.ecsp.common.CommonConstants.ID;

/**
 * The `HcpInfoDao` class is responsible for interacting with the HCPInfo table in the database.
 * It provides methods for inserting, updating, and retrieving data from the table.
 */
@Component
@Slf4j
public class HcpInfoDao {
    public static final int INDEX_2 = 2;
    public static final int INDEX_3 = 3;
    public static final int INDEX_4 = 4;
    public static final int INDEX_5 = 5;
    public static final int RETURN_VALUE = -1;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * Inserts a new record into the "HCPInfo" table with the provided VIN and serial number.
     *
     * @param vin The VIN (Vehicle Identification Number) to be inserted.
     * @param serialNumber The serial number to be inserted.
     * @return The generated ID of the inserted record, or -1 if the insertion failed.
     */
    public long insert(final String vin, final String serialNumber) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rows = jdbcTemplate.update(new PreparedStatementCreator() {

            @Override
            public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                PreparedStatement ps = conn.prepareStatement(
                    "insert into public.\"HCPInfo\"(\"HarmanID\",\"VIN\",\"SerialNumber\",\"CreatedAt\"," 
                    +                        "\"UpdatedAt\") values(?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
                Timestamp timestamp = new Timestamp(new Date().getTime());
                ps.setString(1, vin + serialNumber);
                ps.setString(INDEX_2, vin);
                ps.setString(INDEX_3, serialNumber);
                ps.setTimestamp(INDEX_4, timestamp);
                ps.setTimestamp(INDEX_5, timestamp);
                return ps;
            }
        }, keyHolder);

        // 0 row affected, return -1 to indicate failure
        if (rows == 0) {
            return RETURN_VALUE;
        }
        Map<String, Object> keys = keyHolder.getKeys();
        return (Long) (keys != null ? keys : Collections.emptyMap()).get(ID);
    }

    /**
     * Inserts data into the HCPInfo table.
     *
     * @param factoryDataId The ID of the factory data.
     * @param serialNumber The serial number.
     * @param vin The vehicle identification number.
     * @return The generated ID of the inserted record, or -1 if the insertion failed.
     */
    public long insert(final long factoryDataId, final String serialNumber, final String vin) {
        log.debug("Inside to insert data in HCPInfo for factoryData:{}, serialNumber:{} , vin:{}",
                factoryDataId, serialNumber, vin);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rows = jdbcTemplate.update(new PreparedStatementCreator() {

            @Override
            public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                PreparedStatement ps = conn.prepareStatement(
                    "insert into public.\"HCPInfo\"(\"HarmanID\",\"factory_data\",\"SerialNumber\",\"CreatedAt\"," 
                    +                        "\"UpdatedAt\") values(?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
                Timestamp timestamp = new Timestamp(new Date().getTime());
                ps.setString(1, vin + serialNumber);
                ps.setLong(INDEX_2, factoryDataId);
                ps.setString(INDEX_3, serialNumber);
                ps.setTimestamp(INDEX_4, timestamp);
                ps.setTimestamp(INDEX_5, timestamp);

                return ps;
            }
        }, keyHolder);
        // 0 row affected, return -1 to indicate failure
        if (rows == 0) {
            return RETURN_VALUE;
        }
        Map<String, Object> keys = keyHolder.getKeys();
        return (Long) (keys != null ? keys : Collections.emptyMap()).get(ID);
    }

    /**
     * Retrieves a list of HcpInfo objects based on the provided Harman IDs.
     *
     * @param harmandIds The list of Harman IDs to search for.
     * @return A list of HcpInfo objects matching the provided Harman IDs.
     */
    public List<HcpInfo> findByHarmandIds(List<String> harmandIds) {
        String sql = "select * from \"HCPInfo\" where \"HarmanID\" in (:fields)";
        Map<String, List<String>> params = Collections.singletonMap("fields",
            harmandIds);
        return namedParameterJdbcTemplate.query(sql, params,
            new HcpInfoMapper());

    }

    /**
     * Updates the "HarmanID" field of the "HCPInfo" table with the given Harman ID for the specified ID.
     *
     * @param harmanId The new Harman ID to be updated.
     * @param id The ID of the record to be updated.
     * @return The number of rows affected by the update operation.
     */
    public int updateHarmanId(String harmanId, long id) {
        String sql = "update public.\"HCPInfo\" set \"HarmanID\"=? where \"ID\"=?";
        return jdbcTemplate.update(sql, harmanId, id);
    }

    /**
     * Retrieves the size of a temporary device group.
     *
     * @param tempGroupId The ID of the temporary device group.
     * @return The size of the temporary device group.
     */
    public long getTempGroupSize(long tempGroupId) {
        String sql = "select count(*) from \"TempDeviceGroup\" where \"GroupID\"=? ";
        Long group = jdbcTemplate.queryForObject(sql, Long.class, tempGroupId);
        return group != null ? group : 0;
    }

    /**
     * Deletes a record from the "HCPInfo" table based on the given Harman ID.
     *
     * @param harmanId the Harman ID of the record to be deleted
     * @return the number of rows affected by the delete operation
     */
    public int deleteByHarmanId(String harmanId) {
        String sql = "delete from \"HCPInfo\" where \"HarmanID\"=?";
        return jdbcTemplate.update(sql, harmanId);
    }

    /**
     * Deletes HCPInfo records based on the given factory ID.
     *
     * @param factoryId the factory ID to delete HCPInfo records for
     * @return the number of rows affected by the delete operation
     */
    public int deleteByFactoryId(Long factoryId) {

        String sql = "delete from \"HCPInfo\" where factory_data=?";
        return jdbcTemplate.update(sql, factoryId);
    }

    /**
     * Updates the HCPInfo record for replacing a device.
     *
     * @param hcpInfo The HCPInfo object containing the updated information.
     * @return The number of rows affected by the update operation.
     */
    public int updateForReplaceDevice(HcpInfo hcpInfo) {

        String sql =
            "update \"HCPInfo\" set \"SerialNumber\"=?, \"UpdatedAt\"=now(), \"factory_data\"=? where \"HarmanID\"=?";
        return jdbcTemplate.update(sql,
            hcpInfo.getSerialNumber(), Long.parseLong(hcpInfo.getFactoryId()), hcpInfo.getHarmanId());
    }

    /**
     * Maps Harman IDs for VINs in the TempDeviceGroup table.
     *
     * @param tempGroupId The group ID to map Harman IDs for.
     * @return The number of rows affected by the update operation.
     */
    public int mapHarmanIdsForVins(long tempGroupId) {

        String sql =
            "update \"TempDeviceGroup\" set \"HarmanID\" = \"HCPInfo\".\"HarmanID\",\"IsActive\"=true from " 
            +      "\"HCPInfo\",\"Device\" d WHERE "
            +      "  d.\"HarmanID\"=\"HCPInfo\".\"HarmanID\" and \"TempDeviceGroup\".\"VIN\"=\"HCPInfo\".\"VIN\" and "
            +      "\"GroupID\"=? and d.\"IsActive\"=true ";
        return jdbcTemplate.update(sql, new Object[]{tempGroupId}, new int[]{Types.BIGINT});
    }

    /**
     * Retrieves a list of VINs that need to be preactivated for a given temporary group ID.
     *
     * @param tempGroupId The ID of the temporary device group.
     * @return A list of VINs that need to be preactivated.
     */
    public List<String> getVinsToPreactivate(long tempGroupId) {
        List<String> vins = null;
        String sql = "select \"VIN\" from \"TempDeviceGroup\" where \"HarmanID\" is null and \"GroupID\"=?";
        vins = jdbcTemplate.queryForList(sql, new Object[]{tempGroupId}, String.class);
        return vins;
    }

    /**
     * Updates the temporary device group with the specified Harman ID for the given VIN and group ID.
     *
     * @param harmanId   the Harman ID to update
     * @param vin        the VIN of the device group
     * @param tempGroupId the temporary group ID
     * @return the number of rows affected by the update operation
     */
    public int updateTempDeviceGroup(String harmanId, String vin, long tempGroupId) {
        String sql = "update \"TempDeviceGroup\" set  \"HarmanID\"=? where \"GroupID\"=? and \"VIN\"=?";
        return jdbcTemplate.update(sql, new Object[]{harmanId, tempGroupId, vin}, new int[]{Types.VARCHAR, Types.BIGINT,
            Types.VARCHAR});
    }

    /**
     * Finds an HcpInfo object by VIN (Vehicle Identification Number).
     *
     * @param vin The VIN to search for.
     * @return The HcpInfo object matching the VIN, or null if not found.
     */
    public HcpInfo findByVin(String vin) {
        String sqlQuery =
            "select * from \"HCPInfo\" h,\"Device\" d where h.\"HarmanID\"=d.\"HarmanID\" and d.\"IsActive\"=true and" 
            +                " \"VIN\"=?";
        String[] vins = {vin};
        List<HcpInfo> hcpInfos = jdbcTemplate.query(sqlQuery, vins, new HcpInfoMapper());
        if (hcpInfos.isEmpty()) {
            return null;
        }

        return hcpInfos.get(0);
    }

    /**
     * Finds the active HcpInfo based on the given factory ID.
     *
     * @param factoryId the factory ID to search for
     * @return the active HcpInfo, or null if not found
     */
    public HcpInfo findActiveHcpInfo(Long factoryId) {
        String sql =
            " select * from \"HCPInfo\" h,\"Device\" d where d.\"HarmanID\"=h.\"HarmanID\" and d.\"IsActive\"=true " 
            +                "and h.\"factory_data\"=?";
        Long[] vins = {factoryId};
        List<HcpInfo> hcpInfos = jdbcTemplate.query(sql, vins, new HcpInfoMapper());
        if (hcpInfos.isEmpty()) {
            return null;
        }
        return hcpInfos.get(0);
    }

    /**
     * Retrieves the HcpInfo object associated with the given device ID.
     *
     * @param deviceId The device ID to search for.
     * @return The HcpInfo object associated with the device ID, or null if not found.
     */
    public HcpInfo findByDeviceId(String deviceId) {
        log.debug("getting hcpinfo by deviceID {}", deviceId);
        String sql = "select * from \"HCPInfo\" where \"HarmanID\"=:HarmanID";
        MapSqlParameterSource params = new MapSqlParameterSource(HARMAN_ID, deviceId);
        List<HcpInfo> hcpInfos = namedParameterJdbcTemplate.query(sql, params, new HcpInfoMapper());
        if (CollectionUtils.isEmpty(hcpInfos)) {
            return null;
        }
        return hcpInfos.get(0);
    }

}
