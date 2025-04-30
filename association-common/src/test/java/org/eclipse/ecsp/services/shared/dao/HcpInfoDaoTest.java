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

import org.eclipse.ecsp.services.shared.db.HcpInfo;
import org.eclipse.ecsp.services.shared.db.HcpInfoMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for HcpInfoDao.
 */
public class HcpInfoDaoTest {
    public static final long ID = 1234L;
    public static final int RETURN_VALUE_INT = 10;
    public static final long RETURN_VALUE_LONG = 12L;
    public static final int EXPECTED_VALUE = -1;

    @InjectMocks
    private HcpInfoDao hcpInfoDao;

    @Mock
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void insertTest() {
        final String vin = "vin@123";
        final String serialNumber = "123456";
        Mockito.doReturn(0).when(jdbcTemplate).update((PreparedStatementCreator) Mockito.any(), Mockito.any());
        Long response = hcpInfoDao.insert(vin, serialNumber);
        assertEquals(EXPECTED_VALUE, response);
    }

    @Test
    public void insertTest2() {
        final long factoryDataId = ID;
        final String vin = "vin@123";
        final String serialNumber = "123456";
        Mockito.doReturn(0).when(jdbcTemplate).update((PreparedStatementCreator) Mockito.any(), Mockito.any());
        Long response = hcpInfoDao.insert(factoryDataId, serialNumber, vin);
        assertEquals(EXPECTED_VALUE, response);
    }

    @Test
    public void findByHarmandIdsTest() {
        List<String> harmandIds = new ArrayList<>();
        harmandIds.add("Harman1");
        harmandIds.add("Harman2");
        Mockito.doReturn(null).when(namedParameterJdbcTemplate)
            .query(Mockito.any(), (Map) Mockito.any(), (ResultSetExtractor<Object>) Mockito.any());
        List<HcpInfo> hcpInfoList = hcpInfoDao.findByHarmandIds(harmandIds);
        assertNotNull(hcpInfoList);
    }

    @Test
    public void updateHarmanIdTest() {
        String harmanId = "Harman@123";
        Mockito.doReturn(RETURN_VALUE_INT).when(jdbcTemplate).update(Mockito.any(), (Object) Mockito.any());
        int updatedCount = hcpInfoDao.updateHarmanId(harmanId, ID);
        Assertions.assertEquals(RETURN_VALUE_INT, updatedCount);
    }

    @Test
    public void getTempGroupSizeTest() {
        long tempGroupId = 1L;
        Mockito.doReturn(RETURN_VALUE_LONG).when(jdbcTemplate)
            .queryForObject(Mockito.any(), (Class<Object>) Mockito.any(), Mockito.any());
        long response = hcpInfoDao.getTempGroupSize(tempGroupId);
        Assertions.assertEquals(RETURN_VALUE_LONG, response);
    }

    @Test
    public void deleteByHarmanIdTest() {
        String harmanId = "Harman@123";
        Mockito.doReturn(RETURN_VALUE_INT).when(jdbcTemplate).update(Mockito.anyString(), (Object) Mockito.any());
        int response = hcpInfoDao.deleteByHarmanId(harmanId);
        assertEquals(RETURN_VALUE_INT, response);
    }

    @Test
    public void deleteByFactoryIdTest() {
        Long factoryId = ID;
        Mockito.doReturn(RETURN_VALUE_INT).when(jdbcTemplate).update(Mockito.anyString(), (Object) Mockito.any());
        int response = hcpInfoDao.deleteByFactoryId(factoryId);
        assertEquals(RETURN_VALUE_INT, response);
    }

    @Test
    public void updateForReplaceDeviceTest() {
        HcpInfo hcpInfo = new HcpInfo();
        hcpInfo.setId(ID);
        hcpInfo.setFactoryId("1234");
        hcpInfo.setHarmanId("HU1");
        hcpInfo.setVin("vin@123");
        hcpInfo.setSerialNumber("123456");
        Mockito.doReturn(RETURN_VALUE_INT).when(jdbcTemplate).update(Mockito.anyString(), (Object) Mockito.any());
        int updatedCount = hcpInfoDao.updateForReplaceDevice(hcpInfo);
        Assertions.assertEquals(RETURN_VALUE_INT, updatedCount);
    }

    @Test
    public void mapHarmanIdsForVinsTest() {
        long tempGroupId = 1L;
        Mockito.doReturn(RETURN_VALUE_INT).when(jdbcTemplate).update(Mockito.any(), Mockito.any(), Mockito.any());
        int response = hcpInfoDao.mapHarmanIdsForVins(tempGroupId);
        Assertions.assertEquals(RETURN_VALUE_INT, response);
    }

    @Test
    public void getVinsToPreactivateTest() {
        long tempGroupId = 1L;
        Mockito.doReturn(null).when(jdbcTemplate)
            .queryForList(Mockito.any(), (Class<Object>) Mockito.any(), Mockito.any());
        List<String> vins = hcpInfoDao.getVinsToPreactivate(tempGroupId);
        Assertions.assertNotNull(vins);
    }

    @Test
    public void updateTempDeviceGroupTest() {
        long factoryDataId = ID;
        String vin = "vin@12345";
        String harmanId = "harman@12345";
        Mockito.doReturn(1).when(jdbcTemplate).update(Mockito.any(), Mockito.any(), Mockito.any());
        int response = hcpInfoDao.updateTempDeviceGroup(harmanId, vin, factoryDataId);
        assertEquals(1, response);
    }

    @Test
    public void findByVinTest_NullReturn() {

        Mockito.doReturn(null).when(jdbcTemplate)
            .query(Mockito.anyString(), (ResultSetExtractor<Object>) Mockito.any(), Mockito.any());
        HcpInfo response = hcpInfoDao.findByVin("vin@12345");
        assertNull(response);
    }

    @Test
    public void findByVinTest() {

        HcpInfo hcpInfo = new HcpInfo();
        hcpInfo.setFactoryId("1234");
        hcpInfo.setId(ID);
        hcpInfo.setHarmanId("harmanid");
        List<HcpInfo> hcpInfos = new ArrayList<>();
        hcpInfos.add(hcpInfo);

        String sqlQuery =
            "select * from \"HCPInfo\" h,\"Device\" d where h.\"HarmanID\"=d.\"HarmanID\" and d.\"IsActive\"=true and" 
            +                " \"VIN\"=?";
        String[] vins = {"vin"};


        Mockito.doReturn(hcpInfos).when(jdbcTemplate).query(sqlQuery, vins, new HcpInfoMapper());
        HcpInfo actualHcpInfo = hcpInfoDao.findByVin("vin@12345");
        assertNull(actualHcpInfo);
    }

    @Test
    public void findByVinTest_EmptyHcpInfo() {

        List<HcpInfo> hcpInfos = new ArrayList<>();

        Mockito.doReturn(hcpInfos).when(jdbcTemplate)
            .query(Mockito.anyString(), (ResultSetExtractor<Object>) Mockito.any(), Mockito.any());
        HcpInfo actualHcpInfo = hcpInfoDao.findByVin("vin@12345");
        assertNull(actualHcpInfo);
    }

    @Test
    public void findActiveHcpInfoTest() {
        HcpInfo hcpInfo = new HcpInfo();
        hcpInfo.setFactoryId("1234");
        hcpInfo.setId(ID);
        hcpInfo.setHarmanId("harmanid");
        List<HcpInfo> hcpInfos = new ArrayList<>();
        hcpInfos.add(hcpInfo);

        String sql =
            " select * from \"HCPInfo\" h,\"Device\" d where d.\"HarmanID\"=h.\"HarmanID\" and d.\"IsActive\"=true " 
            +                "and h.\"factory_data\"=?";
        Long[] vins = {ID};

        Mockito.doReturn(hcpInfos).when(jdbcTemplate).query(sql, vins, new HcpInfoMapper());
        HcpInfo actualHcpInfo = hcpInfoDao.findActiveHcpInfo(ID);
        assertNull(actualHcpInfo);
    }

    @Test
    public void findActiveHcpInfoTest_NullHcpInfo() {

        Mockito.doReturn(null).when(jdbcTemplate)
            .query(Mockito.anyString(), (ResultSetExtractor<Object>) Mockito.any(), Mockito.any());
        HcpInfo actualHcpInfo = hcpInfoDao.findActiveHcpInfo(ID);
        assertNull(actualHcpInfo);
    }

    @Test
    public void findActiveHcpInfoTest_EmptyHcpInfo() {
        List<HcpInfo> hcpInfos = new ArrayList<>();

        Mockito.doReturn(hcpInfos).when(jdbcTemplate)
            .query(Mockito.anyString(), (ResultSetExtractor<Object>) Mockito.any(), Mockito.any());
        HcpInfo actualHcpInfo = hcpInfoDao.findActiveHcpInfo(ID);
        assertNull(actualHcpInfo);
    }

    @Test
    public void findByDeviceIdTest_EmptyHcpInfo() {

        List<HcpInfo> hcpInfos = new ArrayList<>();
        Mockito.doReturn(hcpInfos).when(namedParameterJdbcTemplate)
            .query(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class), Mockito.any(HcpInfoMapper.class));
        HcpInfo actualHcpInfo = hcpInfoDao.findByDeviceId("deviceID");
        assertNull(actualHcpInfo);
    }

    @Test
    public void findByDeviceIdTest() {

        HcpInfo hcpInfo = new HcpInfo();
        hcpInfo.setFactoryId("1234");
        hcpInfo.setId(ID);
        hcpInfo.setHarmanId("harmanid");
        List<HcpInfo> hcpInfos = new ArrayList<>();
        hcpInfos.add(hcpInfo);

        Mockito.doReturn(hcpInfos).when(namedParameterJdbcTemplate)
            .query(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class), Mockito.any(HcpInfoMapper.class));
        HcpInfo actualHcpInfo = hcpInfoDao.findByDeviceId("deviceID");
        assertNotNull(actualHcpInfo);
    }
}
