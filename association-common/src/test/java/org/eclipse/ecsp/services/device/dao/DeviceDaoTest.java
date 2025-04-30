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

package org.eclipse.ecsp.services.device.dao;

import org.eclipse.ecsp.services.device.model.Device;
import org.eclipse.ecsp.services.device.model.DeviceMapper;
import org.eclipse.ecsp.services.shared.util.HealthCheckConstants;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.spy;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for DeviceDao.
 */
public class DeviceDaoTest {
    public static final long ID = 12345L;
    public static final long COUNT = -1;
    private static final String SQL_FOR_UPDATE_DEVICE = "update public.\"Device\" set \"HarmanID\"=? where \"ID\"=?";
    private static final String SQL_FOR_UPDATE_REGISTERED_SCOPE_ID_BY_ID =
        "update public.\"Device\" set \"registered_scope_id\"=? where \"ID\"=?";
    private static final String SQL_FOR_UPDATE_REGISTERED_SCOPE_ID_BY_HARMAN_ID =
        "update public.\"Device\" set \"registered_scope_id\"=? where \"HarmanID\"=?";
    private static final String SQL_FOR_DEACTIVATE_HARMAN_ID =
        "update public.\"Device\" set \"IsActive\"=false where \"HarmanID\"=?";
    private static final String SQL_FOR_DEACTIVATE =
        "update \"Device\" set \"IsActive\"=false where \"HarmanID\" in (select \"HarmanID\" from \"HCPInfo\" where " 
            +            "\"SerialNumber\"=?)";
    private static final String SQL_FOR_HEALTH_CHECK = "SELECT " + HealthCheckConstants.DB_CONN_SUCCESS_CODE;

    @InjectMocks
    private DeviceDao deviceDao;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void updateDeviceTest() {
        String deviceId = "HU1";
        long id = ID;
        Mockito.doReturn(1).when(jdbcTemplate).update(SQL_FOR_UPDATE_DEVICE, new Object[]{deviceId, id});
        int actualResult = deviceDao.updateDevice(deviceId, id);
        assertEquals(1, actualResult);
    }

    @Test
    public void updateRegisteredScopeIdByIdTest() {
        long id = ID;
        String registerScopeId = "test";
        Mockito.doReturn(1).when(jdbcTemplate)
            .update(SQL_FOR_UPDATE_REGISTERED_SCOPE_ID_BY_ID, new Object[]{registerScopeId, id});
        int actualResult = deviceDao.updateRegisteredScopIdById(id, registerScopeId);
        assertEquals(1, actualResult);
    }

    @Test
    public void updateRegisteredScopIdByHarmanIdTest() {
        String harmanId = "HU1";
        String registerScopeId = "test";
        Mockito.doReturn(1).when(jdbcTemplate)
            .update(SQL_FOR_UPDATE_REGISTERED_SCOPE_ID_BY_HARMAN_ID, new Object[]{registerScopeId, harmanId});
        int actualResult = deviceDao.updateRegisteredScopIdByHarmanId(harmanId, registerScopeId);
        assertEquals(1, actualResult);
    }

    @Test
    public void deactivateHarmanIdTest() {
        String harmanId = "HU1";
        Mockito.doReturn(1).when(jdbcTemplate)
            .update(SQL_FOR_DEACTIVATE_HARMAN_ID, new Object[]{harmanId}, new int[]{Types.VARCHAR});
        int actualResult = deviceDao.deactivateHarmanId(harmanId);
        assertEquals(1, actualResult);
    }

    @Test
    public void deactivateTest() {
        String serialNumber = "12345";
        Mockito.doReturn(1).when(jdbcTemplate).update(SQL_FOR_DEACTIVATE, serialNumber);
        int actualResult = deviceDao.deactivate(serialNumber);
        assertEquals(1, actualResult);
    }

    @Test
    public void healthCheckTest() {
        Mockito.doReturn(1).when(jdbcTemplate).queryForObject(SQL_FOR_HEALTH_CHECK, Integer.class);
        int actualResult = deviceDao.healthCheck();
        assertEquals(1, actualResult);
    }

    @Test
    public void findByDeviceIdTest1() {
        String deviceId = "HU123";
        Mockito.doReturn(null).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyObject(), Mockito.any(DeviceMapper.class));
        Device actualResponse = deviceDao.findByDeviceId(deviceId);
        assertNull(actualResponse);
    }

    @Test
    public void findByDeviceIdTest2() {
        Device device = new Device();
        Timestamp date = new Timestamp(1L);
        device.setId(1L);
        device.setActivationDate(date);
        device.setPasscode("24828rh2yr2");
        device.setHarmanId("HUKF9EETO2OQ00");
        device.setRandomNumber(1L);
        device.setRegisteredScopeId("scope1");
        List<Device> devices = new ArrayList<>();
        devices.add(device);
        Mockito.doReturn(devices).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyObject(), Mockito.any(DeviceMapper.class));
        String deviceId = "HUKF9EETO2OQ00";
        Device actualResponse = deviceDao.findByDeviceId(deviceId);
        assertNotNull(actualResponse);
    }

    @Test
    public void checkIfActivatedAlreadyTest1() {
        String vin = "vin123";
        String serialNumber = "123";
        Mockito.doReturn(null).when(jdbcTemplate)
            .queryForList(Mockito.anyString(), Mockito.anyObject(), (int[]) Mockito.any());
        Device actualResponse = deviceDao.checkIfActivatedAlready(vin, serialNumber);
        assertNull(actualResponse);
    }

    @Test
    public void checkIfActivatedAlreadyTest2() {
        Map<String, Object> deviceMap = new HashMap<>();
        deviceMap.put("HarmanID", "HUKF9EETO2OQ00");
        deviceMap.put("RandomNumber", ID);
        List<Map<String, Object>> devices = new ArrayList<>();
        devices.add(deviceMap);
        Mockito.doReturn(devices).when(jdbcTemplate)
            .queryForList(Mockito.anyString(), Mockito.anyObject(), (int[]) Mockito.any());
        String vin = "vin123";
        String serialNumber = "123";
        Device actualResponse = deviceDao.checkIfActivatedAlready(vin, serialNumber);
        assertNotNull(actualResponse);
    }

    @Test
    public void updateForReplaceDeviceTest() {
        Device device = new Device();
        device.setId(1L);
        device.setHarmanId("HU1");
        device.setRandomNumber(ID);
        device.setPasscode("1ehue");
        Mockito.doReturn(0).when(jdbcTemplate).update(Mockito.anyString(), (Object) Mockito.any());
        int response = deviceDao.updateForReplaceDevice(device);
        assertEquals(0, response);
    }

    @Test
    public void checkLoginTest1() {
        String deviceId = "HU1";
        String passcode = "24gdrd";
        Mockito.doReturn(null).when(jdbcTemplate)
            .queryForList(Mockito.anyString(), Mockito.anyObject(), (int[]) Mockito.any());
        Device response = deviceDao.checkLogin(deviceId, passcode);
        assertNull(response);
    }

    @Test
    public void checkLoginTest2() {
        Map<String, Object> deviceMap = new HashMap<>();
        deviceMap.put("HarmanID", "HUKF9EETO2OQ00");
        deviceMap.put("PassCode", "24gdrd");
        List<Map<String, Object>> devices = new ArrayList<>();
        devices.add(deviceMap);
        Mockito.doReturn(devices).when(jdbcTemplate)
            .queryForList(Mockito.anyString(), Mockito.anyObject(), (int[]) Mockito.any());
        String deviceId = "HU1";
        String passcode = "24gdrd";
        Device response = deviceDao.checkLogin(deviceId, passcode);
        assertNotNull(response);
    }

    @Test
    public void checkIfActivatedAlreadyTest3() {
        long factoryDataId = 1L;
        Mockito.doReturn(null).when(jdbcTemplate).queryForList(Mockito.anyString(), (Object[]) Mockito.anyObject());
        Device response = deviceDao.checkIfActivatedAlready(factoryDataId);
        assertNull(response);
    }

    @Test
    public void checkIfActivatedAlreadyTest4() {
        Map<String, Object> deviceMap = new HashMap<>();
        deviceMap.put("HarmanID", "HUKF9EETO2OQ00");
        deviceMap.put("RandomNumber", ID);
        List<Map<String, Object>> devices = new ArrayList<>();
        devices.add(deviceMap);
        Mockito.doReturn(devices).when(jdbcTemplate).queryForList(Mockito.anyString(), (Object[]) Mockito.anyObject());
        long factoryDataId = 1L;
        Device response = deviceDao.checkIfActivatedAlready(factoryDataId);
        assertNotNull(response);
    }

    @Test
    public void findActiveDeviceTest() {
        long factoryDataId = 1L;
        List<Map<String, Object>> devices = new ArrayList<>();
        Mockito.doReturn(devices).when(jdbcTemplate).queryForList(Mockito.anyString(), (Object[]) Mockito.anyObject());
        List<Device> response = deviceDao.findActiveDevice(factoryDataId);
        assertNull(response);
    }

    @Test
    public void findActiveDeviceTest2() {
        Map<String, Object> deviceMap = new HashMap<>();
        deviceMap.put("HarmanID", "HUKF9EETO2OQ00");
        deviceMap.put("RandomNumber", ID);
        deviceMap.put("ID", ID);
        List<Map<String, Object>> devices = new ArrayList<>();
        devices.add(deviceMap);
        Mockito.doReturn(devices).when(jdbcTemplate).queryForList(Mockito.anyString(), (Object[]) Mockito.anyObject());
        long factoryDataId = 1L;
        List<Device> response = deviceDao.findActiveDevice(factoryDataId);
        assertNotNull(response);
    }

    @Test
    public void updatePasscodeTest() {
        Device device = new Device();
        device.setId(1L);
        device.setHarmanId("HU1");
        device.setRandomNumber(ID);
        device.setPasscode("1ehue");
        DeviceDao deviceDaoMock = spy(deviceDao);
        Mockito.doReturn(0).when(deviceDaoMock).updateForReplaceDevice(device);
        deviceDao.updatePasscode(device);
        assertEquals(0, deviceDaoMock.updateForReplaceDevice(device));
    }

    @Test
    public void insertTest() {
        Device device = new Device();
        device.setId(1L);
        device.setHarmanId("HU1");
        device.setRandomNumber(ID);
        device.setPasscode("1ehue");

        Mockito.when(
                jdbcTemplate.update(Mockito.any(PreparedStatementCreator.class), Mockito.any(GeneratedKeyHolder.class)))
            .thenAnswer(invocation -> {
                Object[] args = invocation.getArguments();
                Map<String, Object> keyMap = new HashMap<>();
                keyMap.put("ID", ID);
                ((GeneratedKeyHolder) args[1]).getKeyList().add(keyMap);

                return 1;
            }).thenReturn(1);

        int updatedCount = deviceDao.insert(device, true);
        assertEquals(1, updatedCount);
    }

    @Test
    public void insertTest_ZeroReturn() {
        Device device = new Device();
        device.setId(1L);
        device.setHarmanId("HU1");
        device.setRandomNumber(ID);
        device.setPasscode("1ehue");

        Mockito.when(
                jdbcTemplate.update(Mockito.any(PreparedStatementCreator.class), Mockito.any(GeneratedKeyHolder.class)))
            .thenAnswer(invocation -> {
                Object[] args = invocation.getArguments();
                Map<String, Object> keyMap = new HashMap<>();
                keyMap.put("ID", ID);
                ((GeneratedKeyHolder) args[1]).getKeyList().add(keyMap);

                return 0;
            }).thenReturn(0);

        int updatedCount = deviceDao.insert(device, true);
        assertEquals(COUNT, updatedCount);
    }
}
