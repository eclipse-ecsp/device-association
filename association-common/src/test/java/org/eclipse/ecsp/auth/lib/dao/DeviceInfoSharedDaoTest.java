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

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for DeviceInfoSharedDao.
 */
public class DeviceInfoSharedDaoTest {
    public static final int RETURN_VALUE = -1;

    private static final String SQL_FOR_DELETE_BY_HARMAN_ID = "delete from public.\"DeviceInfo\" where \"HarmanID\"=?";
    private static final String SQL_GET_DEVICE_ATTRIBUTES_TO_UPDATE = "select name from device_info_attr";
    private static final String SQL_FOR_UPDATE_DEVICEINFO =
        "select * from public.\"DeviceInfo\" where \"DeviceInfo\".\"HarmanID\"=? AND \"DeviceInfo\".\"Name\"=?";
    private static final String SQL_INSERT_STMT =
        "insert into public.\"DeviceInfo\"(\"HarmanID\", \"Name\", \"Value\") values(?,?,?)";
    private static final String SQL_UPDATE_STMT =
        "update public.\"DeviceInfo\" SET \"Value\"=? where \"DeviceInfo\".\"HarmanID\"=? AND \"DeviceInfo\"" 
            +            ".\"Name\"=?";
    private static final String SQL_FOR_GET_OEM_ID_OF_DEVICE = "select getOEMIDOfDevice(?,?)";
    private static final String SQL_FOR_UPDATE_LAST_LOGIN_TIME =
        "select * from public.\"DeviceInfo\" where \"DeviceInfo\".\"HarmanID\"=? AND \"DeviceInfo\"" 
            +            ".\"Name\"='Lastlogintime'";
    private static final String SQL_INSERT_STMT_FOR_UPDATE_LAST_LOGIN_TIME =
        "insert into public.\"DeviceInfo\"(\"HarmanID\", \"Name\", \"Value\") values(?,'Lastlogintime',?)";
    private static final String SQL_UPDATE_STMT_FOR_UPDATE_LAST_LOGIN_TIME =
        "update public.\"DeviceInfo\" SET \"Value\"=? where \"DeviceInfo\".\"HarmanID\"=? AND \"DeviceInfo\"" 
            +            ".\"Name\"='Lastlogintime'";
    private static final String SQL_FOR_INSERTION =
        "insert into public.\"DeviceInfo\"(\"HarmanID\", \"Name\", \"Value\") values(?,?,?)";

    @InjectMocks
    private DeviceInfoSharedDao deviceInfoSharedDao;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void deleteByHarmanIdTest() {
        String harmanId = "HarmanID";
        Mockito.doReturn(1).when(jdbcTemplate).update(SQL_FOR_DELETE_BY_HARMAN_ID, new Object[]{harmanId});
        int actualResponse = deviceInfoSharedDao.deleteByHarmanId(harmanId);
        assertEquals(1, actualResponse);
    }

    @Test
    public void getDeviceAttributesToUpdateTest() {
        List<String> attributes = new ArrayList<>();
        attributes.add("attribute");
        Mockito.doReturn(attributes).when(jdbcTemplate).queryForList(SQL_GET_DEVICE_ATTRIBUTES_TO_UPDATE, String.class);
        List<String> actualResponse = deviceInfoSharedDao.getDeviceAttributesToUpdate();
        assertNotNull(actualResponse);
    }

    @Test
    public void updateDeviceInfoInsertTest1() {
        String harmanId = "HarmanID";
        String name = "name";
        String value = "value";
        List<String> strLst = new ArrayList<>();
        Mockito.doReturn(strLst).when(jdbcTemplate)
            .query(SQL_FOR_UPDATE_DEVICEINFO, new Object[]{harmanId, name}, new RowMapper<String>() {
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getString("Value");
                }
            });
        Mockito.doReturn(0).when(jdbcTemplate).update(SQL_INSERT_STMT, new Object[]{harmanId, name, value});
        int actualResponse = deviceInfoSharedDao.updateDeviceInfo(harmanId, name, value);
        assertEquals(0, actualResponse);
    }

    @Test
    public void updateDeviceInfoInsertTest2() {
        String harmanId = "HarmanID";
        String name = "name";
        String value = "value";
        List<String> strLst = new ArrayList<>();
        Mockito.doReturn(strLst).when(jdbcTemplate)
            .query(SQL_FOR_UPDATE_DEVICEINFO, new Object[]{harmanId, name}, new RowMapper<String>() {
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getString("value");
                }
            });
        Mockito.doReturn(1).when(jdbcTemplate).update(SQL_INSERT_STMT, new Object[]{harmanId, name, value});
        int actualResponse = deviceInfoSharedDao.updateDeviceInfo(harmanId, name, value);
        assertEquals(1, actualResponse);
    }

    @Test
    public void updateDeviceInfoUpdateTest1() {
        String harmanId = "HarmanID";
        String name = "name";
        String value = "value";
        List<String> strLst = new ArrayList<>();
        strLst.add("val");
        Mockito.doReturn(strLst).when(jdbcTemplate)
            .query(Mockito.any(), (Object[]) Mockito.any(), (RowMapper<Object>) Mockito.any());
        Mockito.doReturn(0).when(jdbcTemplate).update(SQL_UPDATE_STMT, new Object[]{value, harmanId, name});
        int actualResponse = deviceInfoSharedDao.updateDeviceInfo(harmanId, name, value);
        assertEquals(0, actualResponse);
    }

    @Test
    public void updateDeviceInfoUpdateTest2() {
        String harmanId = "HarmanID";
        String name = "name";
        String value = "value";
        List<String> strLst = new ArrayList<>();
        strLst.add("val");
        Mockito.doReturn(strLst).when(jdbcTemplate)
            .query(Mockito.any(), (Object[]) Mockito.any(), (RowMapper<Object>) Mockito.any());
        Mockito.doReturn(1).when(jdbcTemplate).update(SQL_UPDATE_STMT, new Object[]{value, harmanId, name});
        int actualResponse = deviceInfoSharedDao.updateDeviceInfo(harmanId, name, value);
        assertEquals(1, actualResponse);
    }

    @Test
    public void updateDeviceInfoMultipleRowsTest() {
        List<String> strLst = new ArrayList<>();
        strLst.add("val");
        strLst.add("val2");
        Mockito.doReturn(strLst).when(jdbcTemplate)
            .query(Mockito.any(), (Object[]) Mockito.any(), (RowMapper<Object>) Mockito.any());
        String harmanId = "HarmanID";
        String name = "name";
        String value = "value";
        int actualResponse = deviceInfoSharedDao.updateDeviceInfo(harmanId, name, value);
        assertEquals(0, actualResponse);
    }

    @Test
    public void getOemIdOfDeviceTest() {
        String manufacturer = "manufacturer";
        String hwSerialNum = "hwSerialNum";
        Mockito.doReturn(1L).when(jdbcTemplate)
            .queryForObject(SQL_FOR_GET_OEM_ID_OF_DEVICE, new Object[]{manufacturer, hwSerialNum},
                new int[]{Types.VARCHAR, Types.VARCHAR}, Long.class);
        long actualResponse = deviceInfoSharedDao.getOemIdOfDevice(manufacturer, hwSerialNum);
        assertEquals(1L, actualResponse);
    }

    @Test
    public void getOemIdOfDeviceTest_null() {
        String manufacturer = "manufacturer";
        String hwSerialNum = "hwSerialNum";
        Mockito.doReturn(null).when(jdbcTemplate)
            .queryForObject(SQL_FOR_GET_OEM_ID_OF_DEVICE, new Object[]{manufacturer, hwSerialNum},
                new int[]{Types.VARCHAR, Types.VARCHAR}, Long.class);
        long actualResponse = deviceInfoSharedDao.getOemIdOfDevice(manufacturer, hwSerialNum);
        assertEquals(0L, actualResponse);
    }

    @Test
    public void updateLastLoginTimeForInsertTest1() {
        String harmanId = "HarmanID";
        String date = "date";
        List<String> strLst = new ArrayList<>();
        Mockito.doReturn(strLst).when(jdbcTemplate)
            .query(SQL_FOR_UPDATE_LAST_LOGIN_TIME, new Object[]{harmanId}, new RowMapper<String>() {
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getString("value");
                }
            });
        Mockito.doReturn(0).when(jdbcTemplate)
            .update(SQL_INSERT_STMT_FOR_UPDATE_LAST_LOGIN_TIME, new Object[]{harmanId, date});
        deviceInfoSharedDao.updateLastLoginTime(harmanId, date);
        assertEquals(0L, jdbcTemplate.update(SQL_INSERT_STMT_FOR_UPDATE_LAST_LOGIN_TIME,
                new Object[] { harmanId, date }));
    }

    @Test
    public void updateLastLoginTimeForInsertTest2() {
        String harmanId = "HarmanID";
        String date = "date";
        List<String> strLst = new ArrayList<>();
        Mockito.doReturn(strLst).when(jdbcTemplate)
            .query(SQL_FOR_UPDATE_LAST_LOGIN_TIME, new Object[]{harmanId}, new RowMapper<String>() {
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getString("value");
                }
            });
        Mockito.doReturn(1).when(jdbcTemplate)
            .update(SQL_INSERT_STMT_FOR_UPDATE_LAST_LOGIN_TIME, new Object[]{harmanId, date});
        deviceInfoSharedDao.updateLastLoginTime(harmanId, date);
        assertEquals(1L, jdbcTemplate.update(SQL_INSERT_STMT_FOR_UPDATE_LAST_LOGIN_TIME,
                new Object[] { harmanId, date }));
    }

    @Test
    public void updateLastLoginTimeForUpdateTest1() {
        String harmanId = "HarmanID";
        String date = "date";
        List<String> strLst = new ArrayList<>();
        strLst.add("val");
        Mockito.doReturn(strLst).when(jdbcTemplate)
            .query(Mockito.any(), (Object[]) Mockito.any(), (RowMapper<Object>) Mockito.any());
        Mockito.doReturn(0).when(jdbcTemplate)
            .update(SQL_UPDATE_STMT_FOR_UPDATE_LAST_LOGIN_TIME, new Object[]{date, harmanId});
        deviceInfoSharedDao.updateLastLoginTime(harmanId, date);
        assertEquals(0L, jdbcTemplate.update(SQL_INSERT_STMT_FOR_UPDATE_LAST_LOGIN_TIME,
                new Object[] { harmanId, date }));
    }

    @Test
    public void updateLastLoginTimeUpdateTest2() {
        String harmanId = "HarmanID";
        String date = "date";
        List<String> strLst = new ArrayList<>();
        strLst.add("val");
        Mockito.doReturn(strLst).when(jdbcTemplate)
            .query(Mockito.any(), (Object[]) Mockito.any(), (RowMapper<Object>) Mockito.any());
        Mockito.doReturn(1).when(jdbcTemplate)
            .update(SQL_UPDATE_STMT_FOR_UPDATE_LAST_LOGIN_TIME, new Object[]{date, harmanId});
        deviceInfoSharedDao.updateLastLoginTime(harmanId, date);
        assertEquals(0L, jdbcTemplate.update(SQL_UPDATE_STMT_FOR_UPDATE_LAST_LOGIN_TIME,
                new Object[] { harmanId, date }));
    }

    @Test
    public void updateLastLoginTimeMultipleRowsTest() {
        List<String> strLst = new ArrayList<>();
        strLst.add("val");
        strLst.add("val1");
        Mockito.doReturn(strLst).when(jdbcTemplate)
            .query(Mockito.any(), (Object[]) Mockito.any(), (RowMapper<Object>) Mockito.any());
        String harmanId = "HarmanID";
        String date = "date";
        deviceInfoSharedDao.updateLastLoginTime(harmanId, date);
        assertEquals(0L, jdbcTemplate.update(SQL_INSERT_STMT_FOR_UPDATE_LAST_LOGIN_TIME,
                new Object[] { harmanId, date }));
    }

    @Test
    public void insertFailedTest() {
        final String harmanId = "HarmanID";
        final String name = "name";
        final String value = "value";
        Mockito.doReturn(0).when(jdbcTemplate).update(SQL_FOR_INSERTION, new Object[]{harmanId, name, value});
        int actualResponse = deviceInfoSharedDao.insert(harmanId, name, value);
        assertEquals(RETURN_VALUE, actualResponse);
    }

    @Test
    public void insertSuccessfulTest() {
        final String harmanId = "HarmanID";
        final String name = "name";
        final String value = "value";
        Mockito.doReturn(1).when(jdbcTemplate).update(SQL_FOR_INSERTION, new Object[]{harmanId, name, value});
        int actualResponse = deviceInfoSharedDao.insert(harmanId, name, value);
        assertEquals(1, actualResponse);
    }
}