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

package org.eclipse.ecsp.auth.lib.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

/**
 * This class represents a Data Access Object (DAO) for the DeviceInfo table.
 * It provides methods to insert, update, and retrieve data from the DeviceInfo table.
 */
@Repository
@Slf4j
public class DeviceInfoSharedDao {
    public static final int RETURN_VALUE = -1;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Inserts a new record into the DeviceInfo table.
     *
     * @param harmanId the harmanId
     * @param name the name
     * @param value the value
     * @return the updated count
     */
    public int insert(final String harmanId, final String name, final String value) {
        log.debug("Inside DeviceInfoDAO:insert(): name = {}:: value = :{}", name, value);
        String sql = "insert into public.\"DeviceInfo\"(\"HarmanID\", \"Name\", \"Value\") values(?,?,?)";
        int updated = jdbcTemplate.update(sql, harmanId, name, value);
        // 0 row affected, return -1 to indicate failure
        if (updated == 0) {
            log.info(
                "Inside DeviceInfoDAO:insert() - 0 row affected, returning -1 to indicate failure, possibly not " 
                + "unique item, insertion attempted in DeviceInfo Table.");
            return RETURN_VALUE;
        }
        return updated;
    }

    /**
     * Updates the last login time for a device.
     *
     * @param harmanId the harmanId
     * @param date the date
     */
    public void updateLastLoginTime(String harmanId, String date) {
        log.debug("Inside DeviceInfoDAO:updateLastLoginTime().");
        // Check if it exists. If so, update it, else create new entry.
        String sql =
            "select * from public.\"DeviceInfo\" where \"DeviceInfo\".\"HarmanID\"=? AND \"DeviceInfo\"" 
            +                ".\"Name\"='Lastlogintime'";

        List<String> strLst = jdbcTemplate.query(sql, new Object[]{harmanId}, new RowMapper<String>() {
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString("Value");
            }
        });

        if (strLst.isEmpty()) {
            String sqlInsertStmt =
                "insert into public.\"DeviceInfo\"(\"HarmanID\", \"Name\", \"Value\") values(?,'Lastlogintime',?)";
            int updated = jdbcTemplate.update(sqlInsertStmt, harmanId, date);
            // 0 row affected, return -1 to indicate failure
            if (updated == 0) {
                log.info(
                    "Inside DeviceInfoDAO:updateLastLoginTime() - 0 row affected, returning -1 to indicate failure, " 
                    + "possibly not unique item, insertion attempted in DeviceInfo Table.");
            }
        } else if (strLst.size() == 1) { // list contains exactly 1 element
            String sqlUpdateStmt =
                "update public.\"DeviceInfo\" SET \"Value\"=? where \"DeviceInfo\".\"HarmanID\"=? AND \"DeviceInfo\"" 
                +                    ".\"Name\"='Lastlogintime'";
            int updated = jdbcTemplate.update(sqlUpdateStmt, date, harmanId);
            if (updated == 0) {
                log.info(
                    "Inside DeviceInfoDAO:updateLastLoginTime() - 0 row affected, returning -1 to indicate failure, " 
                    +    "possibly not unique item, insertion attempted in DeviceInfo Table.");
            }
        } else {
            log.debug(
                "Inside DeviceInfoDAO:updateLastLoginTime(), more than one row exists for Lastlogintime for this " 
                +                    "HarmanID:- {}", harmanId);

        }

    }

    /**
     * Retrieves the OEM Id of a device.
     *
     * @param manufacturer the manufacturer
     * @param hwSerialNum the hardware serial number
     * @return the OEM Id
     */
    public long getOemIdOfDevice(String manufacturer, String hwSerialNum) {
        log.debug("getOEMIDOfDevice:{}:{}", manufacturer, hwSerialNum);
        String sql = "select getOEMIDOfDevice(?,?)";
        log.debug("jdbcTemplate:{}", jdbcTemplate);
        Long oemId = jdbcTemplate.queryForObject(sql, new Object[]{manufacturer, hwSerialNum},
            new int[]{Types.VARCHAR, Types.VARCHAR}, Long.class);
        return oemId != null ? oemId : 0L;
    }

    /**
     * Updates the DeviceInfo table.
     *
     * @param harmanId the harmanId
     * @param name the name
     * @param value the value
     * @return the updated count
     */
    public int updateDeviceInfo(String harmanId, String name, String value) {
        log.debug("Inside DeviceInfoDAO: updateSoftwareVersion().");
        // Check if it exists. If so, update it, else create new entry.
        String sql =
            "select * from public.\"DeviceInfo\" where \"DeviceInfo\".\"HarmanID\"=? AND \"DeviceInfo\".\"Name\"=?";

        List<String> strLst = jdbcTemplate.query(sql, new Object[]{harmanId, name}, new RowMapper<String>() {
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString("Value");
            }
        });
        int updated = 0;

        if (strLst.isEmpty()) {
            String sqlInsertStmt = "insert into public.\"DeviceInfo\"(\"HarmanID\", \"Name\", \"Value\") values(?,?,?)";
            updated = jdbcTemplate.update(sqlInsertStmt, harmanId, name, value);
            // 0 row affected, return -1 to indicate failure
            if (updated == 0) {
                log.info(
                    "Inside DeviceInfoDAO:updateSoftwareVersion() - 0 row affected, returning -1 to indicate failure," 
                    +            " possibly not unique item, insertion attempted in DeviceInfo Table.");
            }
        } else if (strLst.size() == 1) { // list contains exactly 1 element
            String sqlUpdateStmt =
                "update public.\"DeviceInfo\" SET \"Value\"=? where \"DeviceInfo\".\"HarmanID\"=? AND \"DeviceInfo\"" 
                +                    ".\"Name\"=?";
            updated = jdbcTemplate.update(sqlUpdateStmt, value, harmanId, name);
            if (updated == 0) {
                log.info(
                    "Inside DeviceInfoDAO:updateSoftwareVersion() - 0 row affected, returning -1 to indicate failure," 
                    +                        " possibly not unique item, insertion attempted in DeviceInfo Table.");
            }
        } else {
            log.debug(
                "Inside DeviceInfoDAO:updateSoftwareVersion(), more than one row exists for {} for this HarmanID:- {}",
                name, harmanId);

        }
        return updated;

    }

    /**
     * Deletes records from the DeviceInfo table based on the harmanId.
     *
     * @param harmanId the harmanId
     * @return the number of rows affected
     */
    public int deleteByHarmanId(String harmanId) {
        String sql = "delete from public.\"DeviceInfo\" where \"HarmanID\"=?";
        return jdbcTemplate.update(sql, harmanId);
    }

    /**
     * Retrieves the list of device attributes to update.
     *
     * @return the list of attributes
     */
    public List<String> getDeviceAttributesToUpdate() {
        List<String> attributes = null;
        String sql = "select name from device_info_attr";
        attributes = jdbcTemplate.queryForList(sql, String.class);
        return attributes;
    }
}