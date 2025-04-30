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

package org.eclipse.ecsp.deviceassociation.lib.dao;

import org.eclipse.ecsp.deviceassociation.lib.mapper.AssociationDetailsResponseMapper;
import org.eclipse.ecsp.deviceassociation.lib.mapper.DeviceAssociationHistoryRowMapper;
import org.eclipse.ecsp.deviceassociation.lib.mapper.DeviceAssociationRowMapper;
import org.eclipse.ecsp.deviceassociation.lib.mapper.DeviceInfoRowMapper;
import org.eclipse.ecsp.deviceassociation.lib.mapper.SimDetailsRowMapper;
import org.eclipse.ecsp.deviceassociation.lib.model.AssociationStatus;
import org.eclipse.ecsp.deviceassociation.lib.model.DeviceAssociation;
import org.eclipse.ecsp.deviceassociation.lib.model.DeviceAssociationHistory;
import org.eclipse.ecsp.deviceassociation.lib.model.SimDetails;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociateDeviceRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociationDetailsResponse;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.FactoryData;
import org.eclipse.ecsp.services.shared.deviceinfo.model.DeviceInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for DeviceAssociationDao.
 */
public class DeviceAssociationDaoTest {

    public static final long ID = 4L;
    public static final long ASSOCIATION_ID = 2222L;
    public static final int INDEX_13 = 11;
    public static final int RETURN_VALUE = 10;
    public static final int COUNT = -1;
    @InjectMocks
    DeviceAssociationDao deviceAssociationDao;

    @Mock
    JdbcTemplate jdbcTemplate;

    @Mock
    NamedParameterJdbcTemplate namedParamJdbcTemplate;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void insertTest() {

        DeviceAssociation deviceAssociation = new DeviceAssociation();

        Mockito.when(
                jdbcTemplate.update(Mockito.any(PreparedStatementCreator.class), Mockito.any(GeneratedKeyHolder.class)))
            .thenAnswer(invocation -> {
                Object[] args = invocation.getArguments();
                Map<String, Object> keyMap = new HashMap<>();
                keyMap.put("id", ID);
                ((GeneratedKeyHolder) args[1]).getKeyList().add(keyMap);

                return 1;
            }).thenReturn(1);
        deviceAssociationDao.insert(deviceAssociation);
        Assertions.assertEquals(0, jdbcTemplate.update(Mockito.any(PreparedStatementCreator.class),
                Mockito.any(GeneratedKeyHolder.class)));
    }


    @Test
    public void insertM2Mtest() {

        Mockito.when(
                jdbcTemplate.update(Mockito.any(PreparedStatementCreator.class), Mockito.any(GeneratedKeyHolder.class)))
            .thenAnswer(invocation -> {
                Object[] args = invocation.getArguments();
                Map<String, Object> keyMap = new HashMap<>();
                keyMap.put("id", ID);
                ((GeneratedKeyHolder) args[1]).getKeyList().add(keyMap);

                return 1;
            }).thenReturn(1);
        deviceAssociationDao.insertM2M(new DeviceAssociation());
        Assertions.assertEquals(0, jdbcTemplate.update(Mockito.any(PreparedStatementCreator.class),
                Mockito.any(GeneratedKeyHolder.class)));
    }

    @Test
    public void updateHarmanIdTest() {

        Mockito.when(jdbcTemplate.update(Mockito.anyString(), (Object) Mockito.anyObject())).thenReturn(1);
        deviceAssociationDao.updateHarmanId("harmanId", "1234");
        Assertions.assertEquals(1, deviceAssociationDao.updateHarmanId("harmanID", "1234"));
    }

    @Test
    public void isDeviceCurrentlyAssociatedToUserTest() {

        Mockito.doReturn(true).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.eq(Boolean.class), Mockito.anyString(), Mockito.anyString());
        deviceAssociationDao.isDeviceCurrentlyAssociatedToUser("123456", "userId");
        Assertions.assertTrue(deviceAssociationDao.isDeviceCurrentlyAssociatedToUser("123456", "userId"));
    }

    @Test
    public void isDeviceCurrentlyAssociatedToUserTest_null() {

        Mockito.doReturn(null).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.eq(Boolean.class), Mockito.anyString(), Mockito.anyString());
        boolean associated = deviceAssociationDao.isDeviceCurrentlyAssociatedToUser("123456", "userId");
        assertFalse(associated);
    }

    @Test
    public void getUserDetailsTest() {

        Mockito.doReturn(new ArrayList<String>()).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.eq(Boolean.class), Mockito.anyString(), Mockito.anyString());
        List<String> userDetails = deviceAssociationDao.getUserDetails("harmanId");
        Assertions.assertNotNull(userDetails);
    }

    @Test
    public void fetchAssociatedDevicesTest() {

        Mockito.doReturn(new ArrayList<DeviceAssociation>()).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyObject(), Mockito.any(DeviceAssociationRowMapper.class));
        List<DeviceAssociation> deviceAssociations = deviceAssociationDao.fetchAssociatedDevices("userId");
        Assertions.assertNotNull(deviceAssociations);
    }

    @Test
    public void findValidAssociationsTest() {
        Mockito.doReturn(new DeviceAssociation()).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.any(DeviceAssociationRowMapper.class), Mockito.anyString());
        DeviceAssociation deviceAssociation = deviceAssociationDao.findValidAssociations("1234");
        Assertions.assertNotNull(deviceAssociation);
    }

    @Test
    public void findValidAssociationsTest_throwEmptyResultDataAccessException() {

        Mockito.doThrow(EmptyResultDataAccessException.class).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.any(DeviceAssociationRowMapper.class), Mockito.anyString());
        DeviceAssociation deviceAssociation = deviceAssociationDao.findValidAssociations("1234");
        Assertions.assertNull(deviceAssociation);
    }

    @Test
    public void findTest() {

        Mockito.doReturn(new DeviceAssociation()).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.anyObject(), Mockito.any(DeviceAssociationRowMapper.class));
        DeviceAssociation deviceAssociation = deviceAssociationDao.find(ID, "userId");
        Assertions.assertNotNull(deviceAssociation);
    }

    @Test
    public void findTest_throwEmptyResultDataAccessException() {


        Mockito.doThrow(
                new EmptyResultDataAccessException(1)).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.anyObject(), Mockito.any(DeviceAssociationRowMapper.class));
        DeviceAssociation deviceAssociation = deviceAssociationDao.find(ID, "userId");
        Assertions.assertNull(deviceAssociation);
    }

    @Test
    public void updateHarmanIdTest2() {

        Mockito.doReturn(1).when(jdbcTemplate)
            .update(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyLong());
        int updatedCount = deviceAssociationDao.updateHarmanId("harmanId", "12345");
        Assertions.assertEquals(0, updatedCount);
    }

    @Test
    public void retrieveAssociatedUserTest() {

        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();
        deviceAssociationList.add(new DeviceAssociation());

        Mockito.doReturn(deviceAssociationList).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyObject(), Mockito.any(DeviceAssociationRowMapper.class));
        List<DeviceAssociation> actualDeviceAssociations = deviceAssociationDao.retrieveAssociatedUser("1234", true);
        Assertions.assertEquals(deviceAssociationList, actualDeviceAssociations);
    }

    @Test
    public void retrieveAssociatedUserTest_FalseReactivationFlag() {

        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();
        deviceAssociationList.add(new DeviceAssociation());

        Mockito.doReturn(deviceAssociationList).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyObject(), Mockito.any(DeviceAssociationRowMapper.class));
        List<DeviceAssociation> actualDeviceAssociations = deviceAssociationDao.retrieveAssociatedUser("1234", false);
        Assertions.assertEquals(deviceAssociationList, actualDeviceAssociations);
    }

    @Test
    public void retrieveAssociatedUserTest_throwException() {

        Mockito.doThrow(IllegalStateException.class).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyObject(), Mockito.any(DeviceAssociationRowMapper.class));
        deviceAssociationDao.retrieveAssociatedUser("1234", false);
        Assertions.assertNotNull(deviceAssociationDao);
    }

    @Test
    public void constructAndFetchFactoryDataTest() {

        AssociateDeviceRequest associateReq = new AssociateDeviceRequest();
        associateReq.setSerialNumber("12345");
        associateReq.setImei("1111");
        associateReq.setBssid("2222");
        associateReq.setImsi("3333");
        associateReq.setMsisdn("4444");
        associateReq.setSsid("5555");
        associateReq.setIccid("6666");

        List<FactoryData> factoryData = deviceAssociationDao.constructAndFetchFactoryData(associateReq);
        Assertions.assertNotNull(factoryData);
    }

    //@Test
    //    public void constructAndFetchFactoryDataTest_throw() {
    //
    //        AssociateDeviceRequest associateReq = new AssociateDeviceRequest();
    //        associateReq.setSerialNumber("12345");
    //        associateReq.setImei("1111");
    //        associateReq.setBssid("2222");
    //        associateReq.setImsi("3333");
    //        associateReq.setMsisdn("4444");
    //        associateReq.setSsid("5555");
    //        associateReq.setIccid("6666");
    //
    //        Mockito.doThrow(new RuntimeException("Error while fetching factory data")).when(namedParamJdbcTemplate)
    //            .query(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class),
    //                (ResultSetExtractor<Object>) Mockito.any());
    //        deviceAssociationDao.constructAndFetchFactoryData(associateReq);
    //    }

    @Test
    public void updateDeviceAssociationTest() {

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATION_INITIATED);
        deviceAssociation.setDisassociatedBy("user1");
        deviceAssociation.setModifiedBy("user2");
        deviceAssociation.setId(ID);
        deviceAssociation.setUserId("userId");

        Mockito.doReturn(1).when(jdbcTemplate).update(Mockito.anyString(), Mockito.anyString(),
            Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
            Mockito.anyString());
        int updatedCount = deviceAssociationDao.updateDeviceAssociation(deviceAssociation);
        Assertions.assertEquals(0, updatedCount);
    }

    @Test
    public void updateDeviceAssociationStatusToRestoreTest() {

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATION_INITIATED);
        deviceAssociation.setDisassociatedBy("user1");
        deviceAssociation.setModifiedBy("user2");
        deviceAssociation.setId(ID);
        deviceAssociation.setUserId("userId");

        Mockito.doReturn(1).when(jdbcTemplate).update(Mockito.anyString(), Mockito.anyString(),
            Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        int updatedCount = deviceAssociationDao.updateDeviceAssociation(deviceAssociation);
        Assertions.assertEquals(0, updatedCount);
    }

    @Test
    public void updateDeviceAssociationStatusToSuspendedTest() {
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATION_INITIATED);
        deviceAssociation.setDisassociatedBy("user1");
        deviceAssociation.setModifiedBy("user2");
        deviceAssociation.setId(ID);
        deviceAssociation.setUserId("userId");

        Mockito.doReturn(1).when(jdbcTemplate).update(Mockito.anyString(), Mockito.anyString(),
            Mockito.anyLong(), Mockito.anyLong());
        int updatedCount = deviceAssociationDao.updateDeviceAssociationStatusToSuspended(deviceAssociation);
        Assertions.assertEquals(1, updatedCount);
    }

    @Test
    public void isDeviceCurrentlyAssociatedToAnyUserTest() {

        Mockito.doReturn(true).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.eq(Boolean.class), Mockito.anyString());
        Assertions.assertTrue(deviceAssociationDao.isDeviceCurrentlyAssociatedToAnyUser("12345"));
    }

    @Test
    public void isDeviceCurrentlyAssociatedToAnyUserTest_null() {

        Mockito.doReturn(null).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.eq(Boolean.class), Mockito.anyString());
        boolean associated = deviceAssociationDao.isDeviceCurrentlyAssociatedToAnyUser("12345");
        assertFalse(associated);
    }

    @Test
    public void updateTransactionStatusTest() {

        Mockito.doReturn(1).when(jdbcTemplate).update(Mockito.anyString(), Mockito.anyString(),
            Mockito.anyString(), Mockito.anyLong());
        int updatedCount = deviceAssociationDao.updateTransactionStatus("status", "1111", ASSOCIATION_ID);
        Assertions.assertEquals(1, updatedCount);
    }

    @Test
    public void findLatestSimTransactionStatusTest() {

        SimDetails simDetails = new SimDetails();

        Mockito.doReturn(simDetails).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.any(SimDetailsRowMapper.class), Mockito.anyLong());
        SimDetails actualSimDetails = deviceAssociationDao.findLatestSimTransactionStatus(ID);
        Assertions.assertEquals(simDetails, actualSimDetails);
    }

    @Test
    public void findLatestSimTransactionStatusTest_throwEmptyResultDataAccessException() {

        Mockito.doThrow(new EmptyResultDataAccessException(1)).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.any(SimDetailsRowMapper.class), Mockito.anyLong());
        SimDetails simDetails = deviceAssociationDao.findLatestSimTransactionStatus(ID);
        Assertions.assertNull(simDetails);
    }

    @Test
    public void insertDeviceStateTest() {

        jdbcTemplate.update(Mockito.any(PreparedStatementCreator.class));
        deviceAssociationDao.insertDeviceState(new DeviceAssociation());
        Assertions.assertEquals(0, jdbcTemplate.update(Mockito.any(PreparedStatementCreator.class)));
    }

    @Test
    public void fetchFactoryDataTest() {

        List<FactoryData> factoryDataList = new ArrayList<>();
        factoryDataList.add(new FactoryData());

        Mockito.doReturn(factoryDataList).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyObject(), Mockito.any(DeviceAssociationRowMapper.class));
        List<FactoryData> actualFactoryList = deviceAssociationDao.fetchFactoryData(ID);
        Assertions.assertNotNull(actualFactoryList);
    }

    @Test
    public void constructAndFetchDeviceAssociationDataTest() {

        List<AssociationStatus> statusList = new ArrayList<>();
        statusList.add(AssociationStatus.ASSOCIATION_INITIATED);
        statusList.add(AssociationStatus.ASSOCIATED);

        namedParamJdbcTemplate.query(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class),
            Mockito.any(DeviceAssociationRowMapper.class));
        List<DeviceAssociation> deviceAssociationList = deviceAssociationDao.constructAndFetchDeviceAssociationData(
                "1234", "DeviceId", "9899", "har@123", ASSOCIATION_ID, statusList);
        Assertions.assertNotNull(deviceAssociationList);
    }

    @Test
    public void fetchAssociationDetailsTest_SortById() {

        Map<String, Object> attributeMap = new HashMap<>();
        attributeMap.put("key", new Object());

        jdbcTemplate.query(Mockito.anyString(), (Object[]) Mockito.anyObject(),
            Mockito.any(AssociationDetailsResponseMapper.class));
        List<AssociationDetailsResponse> associationDetailsResponses = deviceAssociationDao.fetchAssociationDetails(
                attributeMap, false);
        Assertions.assertNotNull(associationDetailsResponses);
    }

    @Test
    public void fetchAssociationDetailsTest() {

        Map<String, Object> attributeMap = new HashMap<>();
        attributeMap.put("key", new Object());

        jdbcTemplate.query(Mockito.anyString(), (Object[]) Mockito.anyObject(),
            Mockito.any(AssociationDetailsResponseMapper.class));
        List<AssociationDetailsResponse> associationDetailsResponses = deviceAssociationDao.fetchAssociationDetails(
                attributeMap, false);
        Assertions.assertNotNull(associationDetailsResponses);
    }

    @Test
    public void checkAssociatedDeviceWithFactDataTest() {

        Mockito.doReturn(true).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.eq(Boolean.class), Mockito.anyLong(), Mockito.anyString());
        assertTrue(deviceAssociationDao.checkAssociatedDeviceWithFactData(ID, "userId"));
    }

    @Test
    public void checkAssociatedDeviceWithFactDataTest_null() {

        Mockito.doReturn(null).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.eq(Boolean.class), Mockito.anyLong(), Mockito.anyString());
        boolean associated = deviceAssociationDao.checkAssociatedDeviceWithFactData(ID, "userId");
        assertFalse(associated);
    }

    @Test
    public void checkAssociatedDeviceWithFactDataNotDisassociatedTest() {

        Mockito.doReturn(true).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.eq(Boolean.class), Mockito.anyLong(), Mockito.anyString());
        assertTrue(deviceAssociationDao.checkAssociatedDeviceWithFactDataNotDisassociated(ID, "userId"));
    }

    @Test
    public void checkAssociatedDeviceWithFactDataNotDisassociatedTest_null() {

        Mockito.doReturn(null).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.eq(Boolean.class), Mockito.anyLong(), Mockito.anyString());
        boolean notDisassociated =
            deviceAssociationDao.checkAssociatedDeviceWithFactDataNotDisassociated(ID, "userId");
        assertFalse(notDisassociated);
    }

    @Test
    public void updateForDisassociationByIdTest() {

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATION_INITIATED);
        deviceAssociation.setDisassociatedBy("user1");
        deviceAssociation.setModifiedBy("user2");
        deviceAssociation.setId(ID);
        deviceAssociation.setUserId("userId");
        deviceAssociation.setDisassociatedOn(new Timestamp(System.currentTimeMillis()));

        Mockito.doReturn(1).when(jdbcTemplate)
            .update(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong());
        int updatedCount = deviceAssociationDao.updateForDisassociationById(deviceAssociation);
        Assertions.assertEquals(0, updatedCount);
    }

    @Test
    public void updateForM2MdisassociationByIdTest() {

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATION_INITIATED);
        deviceAssociation.setDisassociatedBy("user1");
        deviceAssociation.setModifiedBy("user2");
        deviceAssociation.setId(ID);
        deviceAssociation.setUserId("userId");
        deviceAssociation.setDisassociatedOn(new Timestamp(System.currentTimeMillis()));

        Mockito.doReturn(1).when(jdbcTemplate)
            .update(Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.anyString(), Mockito.anyString(),
                Mockito.any(Timestamp.class), Mockito.anyLong());
        int updatedCount = deviceAssociationDao.updateForDisassociationById(deviceAssociation);
        Assertions.assertEquals(0, updatedCount);
    }

    @Test
    public void updateTest_null() {

        LinkedHashMap<String, Object> updatedMap = new LinkedHashMap<>();
        LinkedHashMap<String, Object> conditionMap = new LinkedHashMap<>();

        Mockito.doReturn(1).when(jdbcTemplate).update(Mockito.anyString(), Mockito.anyString());
        int updatedCount = deviceAssociationDao.update(updatedMap, conditionMap);
        Assertions.assertEquals(COUNT, updatedCount);
    }

    @Test
    public void updateTest() {

        LinkedHashMap<String, Object> updatedMap = new LinkedHashMap<>();
        updatedMap.put("updated", new Object());
        LinkedHashMap<String, Object> conditionMap = new LinkedHashMap<>();
        conditionMap.put("condition", new Object());

        Mockito.doReturn(1).when(jdbcTemplate).update(Mockito.anyString(), Mockito.anyString());
        int updatedCount = deviceAssociationDao.update(updatedMap, conditionMap);
        Assertions.assertEquals(0, updatedCount);
    }

    @Test
    public void updateForReplaceDeviceTest() {

        Mockito.doReturn(1).when(jdbcTemplate)
            .update(Mockito.anyString(), Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyLong());
        int updatedCount = deviceAssociationDao.updateForReplaceDevice(ID, "1234", "userId", ASSOCIATION_ID);
        Assertions.assertEquals(1, updatedCount);
    }

    @Test
    public void findAssociationCountForFactoryIdTest() {

        Mockito.doReturn(1).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.any(), Mockito.eq(Integer.class));
        int count = deviceAssociationDao.findAssociationCountForFactoryId(ID);
        Assertions.assertEquals(1, count);
    }

    @Test
    public void findAssociationCountForFactoryIdTest_null() {

        Mockito.doReturn(null).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.any(), Mockito.eq(Integer.class));
        int count = deviceAssociationDao.findAssociationCountForFactoryId(ID);
        assertEquals(0, count);
    }

    @Test
    public void isDeviceTerminatedTest() {
        Mockito.doReturn(1).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.any(), Mockito.eq(Integer.class));
        assertTrue(deviceAssociationDao.isDeviceTerminated(ID));
    }

    @Test
    public void isDeviceTerminatedTest_null() {
        Mockito.doReturn(null).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.any(), Mockito.eq(Integer.class));
        boolean terminated = deviceAssociationDao.isDeviceTerminated(ID);
        assertEquals(false, terminated);
    }

    @Test
    public void getAssociationDetailsTest() {

        String orderBy = "desc";
        namedParamJdbcTemplate.query(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class),
            Mockito.any(DeviceAssociationHistoryRowMapper.class));
        List<DeviceAssociationHistory> deviceAssociationHistories = deviceAssociationDao.getAssociationDetails(
                ID, orderBy, orderBy, 1, 1);
        assertNotNull(deviceAssociationHistories);
    }

    @Test
    public void getAssociationDetailsTest_Asc() {

        String orderBy = "asc";
        namedParamJdbcTemplate.query(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class),
            Mockito.any(DeviceAssociationHistoryRowMapper.class));
        List<DeviceAssociationHistory> deviceAssociationHistories = deviceAssociationDao.getAssociationDetails(
                ID, orderBy, "asc", 1, 1);
        assertNotNull(deviceAssociationHistories);
    }

    @Test
    public void getAssociationDetailsTest_Asc1() {

        String orderBy = "asc1";
        namedParamJdbcTemplate.query(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class),
            Mockito.any(DeviceAssociationHistoryRowMapper.class));
        List<DeviceAssociationHistory> deviceAssociationHistories = deviceAssociationDao.getAssociationDetails(
                ID, orderBy, "asc1", 1, 1);
        assertNotNull(deviceAssociationHistories);
    }

    @Test
    public void associationExistsTest() {

        Mockito.doReturn(1L).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.any(), Mockito.any(ResultSetExtractor.class));
        Long assocId = deviceAssociationDao.associationExists("userId", "1234");
        Assertions.assertEquals(1L, assocId);
    }

    @Test
    public void getAssociationIdByDeviceIdTest() {
        Mockito.doReturn(1).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.anyObject(), Mockito.eq(Integer.class));
        Long assocId = deviceAssociationDao.getAssociationIdByDeviceId("deviceId");
        Assertions.assertEquals(1L, assocId);
    }

    @Test
    public void associationByDeviceExistsTest() {
        Mockito.doReturn(1).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.anyObject(), Mockito.eq(Integer.class));
        assertTrue(deviceAssociationDao.associationByDeviceExists("deviceId"));
    }

    @Test
    public void associationByDeviceExistsTest_Else() {
        Mockito.doReturn(RETURN_VALUE).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.anyObject(), Mockito.eq(Integer.class));
        assertFalse(deviceAssociationDao.associationByDeviceExists("deviceId"));
    }

    @Test
    public void associationByDeviceExistsTest_null() {
        Mockito.doReturn(null).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.anyObject(), Mockito.eq(Integer.class));
        assertFalse(deviceAssociationDao.associationByDeviceExists("deviceId"));
    }

    @Test
    public void saveSimDetailsTest() {

        jdbcTemplate.update(Mockito.anyString(), (Object[]) Mockito.any());
        deviceAssociationDao.saveSimDetails("1234", ID, "tranStatus", "userAction",
            new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
        Assertions.assertEquals(0, jdbcTemplate.update(Mockito.anyString(), (Object[]) Mockito.any()));
    }

    @Test
    public void saveVinDetailsTest() {

        jdbcTemplate.update(Mockito.anyString(), (Object[]) Mockito.any());
        deviceAssociationDao.saveVinDetails("1234", "TH", ID);
        Assertions.assertEquals(0, jdbcTemplate.update(Mockito.anyString(), (Object[]) Mockito.any()));
    }

    @Test
    public void vinAssociationExistsTest() {

        Mockito.doReturn(1L).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.any(), Mockito.eq(Long.class));
        assertTrue(deviceAssociationDao.vinAssociationExists(ID));
    }

    @Test
    public void vinAssociationExistsTest_else() {

        Mockito.doReturn(ID).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.any(), Mockito.eq(Long.class));
        assertFalse(deviceAssociationDao.vinAssociationExists(ID));
    }

    @Test
    public void vinAssociationExistsTest_null() {

        Mockito.doReturn(null).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.any(), Mockito.eq(Long.class));
        assertFalse(deviceAssociationDao.vinAssociationExists(ID));
    }

    @Test
    public void vinAlreadyAssociatedTest() {

        Mockito.doReturn(0L).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.any(), Mockito.eq(Long.class));
        assertFalse(deviceAssociationDao.vinAlreadyAssociated("1111"));
    }

    @Test
    public void vinAlreadyAssociatedTest_else() {

        Mockito.doReturn(ID).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.any(), Mockito.eq(Long.class));
        assertTrue(deviceAssociationDao.vinAlreadyAssociated("1111"));
    }

    @Test
    public void vinAlreadyAssociatedTest_null() {

        Mockito.doReturn(null).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.any(), Mockito.eq(Long.class));
        assertFalse(deviceAssociationDao.vinAlreadyAssociated("1111"));
    }

    @Test
    public void getAssociatedVinTest() {

        String vin = "vin111";
        Mockito.doReturn(vin).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.anyObject(), Mockito.eq(String.class));
        String actualVin = deviceAssociationDao.getAssociatedVin("1234");
        Assertions.assertEquals(vin, actualVin);
    }

    @Test
    public void getAssociatedVinTest_throwDataAccessException() {

        Mockito.doThrow(new EmptyResultDataAccessException(1)).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.anyObject(), Mockito.eq(String.class));
        String vin = deviceAssociationDao.getAssociatedVin("1234");
        assertNull(vin);
    }

    @Test
    public void findAssociatedFactoryDataVinTest() {
        List<String> strLst = new ArrayList<>();
        strLst.add("1111");

        Mockito.doReturn(strLst).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyObject(), Mockito.any(RowMapper.class));
        String vin = deviceAssociationDao.findAssociatedFactoryDataVin("1234");
        assertEquals("1111", vin);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findAssociatedFactoryDataVinTest_throwIllegalArgumentException() {

        Mockito.doThrow(IllegalArgumentException.class).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyObject(), Mockito.any(RowMapper.class));
        deviceAssociationDao.findAssociatedFactoryDataVin("");
    }

    @Test
    public void findAssociatedFactoryDataVinTest_throwDataAccessException() {

        Mockito.doThrow(new EmptyResultDataAccessException(1)).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyObject(), Mockito.any(RowMapper.class));
        String vin = deviceAssociationDao.findAssociatedFactoryDataVin("11");
        assertNull(vin);
    }

    @Test
    public void replaceVinTest() {

        jdbcTemplate.update(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong());
        deviceAssociationDao.replaceVin(ID, "vin123");
        assertEquals(0, jdbcTemplate.update(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong()));
    }

    @Test
    public void replaceReferenceIdInVinDetailsTest() {

        jdbcTemplate.update(Mockito.anyString(), Mockito.anyLong(), Mockito.anyString());
        deviceAssociationDao.replaceReferenceIdInVinDetails(ID, "vin123");
        assertEquals(0, jdbcTemplate.update(Mockito.anyString(), Mockito.anyString(), Mockito.anyLong()));
    }

    @Test
    public void findCountByDetailsTest() {
        Map<String, Object> queryConditionMap = new HashMap<>();
        queryConditionMap.put("key", new Object());
        queryConditionMap.put("key1", new Object());

        Mockito.doReturn(true).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.eq(Boolean.class), Mockito.any());
        assertTrue(deviceAssociationDao.findCountByDetails(queryConditionMap));
    }

    @Test
    public void findCountByDetailsTest_null() {
        Map<String, Object> queryConditionMap = new HashMap<>();
        queryConditionMap.put("key", new Object());
        queryConditionMap.put("key1", new Object());

        Mockito.doReturn(null).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.eq(Boolean.class), Mockito.any());
        assertFalse(deviceAssociationDao.findCountByDetails(queryConditionMap));
    }

    @Test
    public void getImsiTest() {

        Mockito.doReturn("122222").when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.anyObject(), Mockito.eq(String.class));
        String imsi = deviceAssociationDao.getImsi("1234");
        assertEquals("122222", imsi);
    }

    @Test
    public void getImsiTest_throw() {

        Mockito.doThrow(new EmptyResultDataAccessException(1)).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.anyObject(), Mockito.eq(String.class));
        String imsi = deviceAssociationDao.getImsi("1234");
        assertNull(imsi);
    }

    @Test
    public void getCountryCodeTest() {

        Mockito.doReturn("TH").when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.anyObject(), Mockito.eq(String.class));
        String countryCode = deviceAssociationDao.getCountryCode(ID);
        assertEquals("TH", countryCode);
    }

    @Test
    public void getCountryCodeTest_throw() {

        Mockito.doThrow(new EmptyResultDataAccessException(1)).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.anyObject(), Mockito.eq(String.class));
        String countryCode = deviceAssociationDao.getCountryCode(ID);
        assertNull(countryCode);
    }

    @Test
    public void getTerminateTranStatusTest() {

        Mockito.doReturn("tranStatus").when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.anyObject(), Mockito.eq(String.class));
        String transStatus = deviceAssociationDao.getTerminateTranStatus(ID);
        assertEquals("tranStatus", transStatus);
    }

    @Test
    public void getTerminateTranStatusTest_throw() {

        Mockito.doThrow(new EmptyResultDataAccessException(1)).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.anyObject(), Mockito.eq(String.class));
        String transStatus = deviceAssociationDao.getTerminateTranStatus(ID);
        assertNull(transStatus);
    }

    @Test
    public void getActivateTranStatusTest() {

        Mockito.doReturn("tranStatus").when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.anyObject(), Mockito.eq(String.class));
        String transStatus = deviceAssociationDao.getActivateTranStatus(ID);
        assertNotNull(transStatus);
    }

    @Test
    public void getActivateTranStatusTest_throw() {

        Mockito.doThrow(new EmptyResultDataAccessException(1)).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.anyObject(), Mockito.eq(String.class));
        String transStatus = deviceAssociationDao.getActivateTranStatus(ID);
        assertNull(transStatus);
    }

    @Test
    public void getAssociatedVinForReplaceApiTest() {

        Mockito.doReturn("tranStatus").when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.anyObject(), Mockito.eq(String.class));
        String transStatus = deviceAssociationDao.getAssociatedVinForReplaceApi("1234");
        assertEquals("tranStatus", transStatus);
    }

    @Test
    public void getAssociatedVinForReplaceApiTest_throw() {

        Mockito.doThrow(new EmptyResultDataAccessException(1)).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.anyObject(), Mockito.eq(String.class));
        String vin = deviceAssociationDao.getAssociatedVinForReplaceApi("1234");
        assertNull(vin);
    }

    @Test
    public void updateUserIdWithDummyValueTest() {

        List<String> userIds = new ArrayList<>();
        userIds.add("user123");
        List<String> serialNumbers = new ArrayList<>();
        serialNumbers.add("1234");

        Mockito.doReturn(1).when(jdbcTemplate).update(Mockito.anyString());
        int updatedCount = deviceAssociationDao.updateUserIdWithDummyValue(userIds, serialNumbers);
        assertEquals(1, updatedCount);
    }

    @Test
    public void updateUserIdWithDummyValueTest_NullUserId() {

        List<String> serialNumbers = new ArrayList<>();
        serialNumbers.add("1234");

        Mockito.doReturn(1).when(jdbcTemplate).update(Mockito.anyString());
        int updatedCount = deviceAssociationDao.updateUserIdWithDummyValue(null, serialNumbers);
        assertEquals(1, updatedCount);
    }

    @Test
    public void updateActivationStateWithDummyTest() {

        List<String> userIds = new ArrayList<>();
        userIds.add("user123");
        List<String> serialNumbers = new ArrayList<>();
        serialNumbers.add("1234");

        Mockito.doReturn(1).when(jdbcTemplate).update(Mockito.anyString());
        int updatedCount = deviceAssociationDao.updateUserIdWithDummyValue(userIds, serialNumbers);
        assertEquals(1, updatedCount);
    }

    @Test
    public void updateActivationStateWithDummyTest_NullUserId() {

        List<String> serialNumbers = new ArrayList<>();
        serialNumbers.add("1234");

        Mockito.doReturn(1).when(jdbcTemplate).update(Mockito.anyString());
        int updatedCount = deviceAssociationDao.updateUserIdWithDummyValue(null, serialNumbers);
        assertEquals(1, updatedCount);
    }

    @Test
    public void getAllM2MassociationsTest() {

        List<DeviceAssociation> deviceAssociationList = new ArrayList<>();

        Mockito.doReturn(deviceAssociationList).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class),
                Mockito.any(DeviceAssociationRowMapper.class));
        List<DeviceAssociation> deviceAssociations = deviceAssociationDao.getAllM2Massociations("1234");
        assertEquals(deviceAssociationList, deviceAssociations);
    }

    @Test
    public void findDeviceInfoTest() {
        List<DeviceInfo> deviceInfoList = new ArrayList<>();
        Mockito.doReturn(deviceInfoList).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyObject(), Mockito.any(DeviceInfoRowMapper.class));
        List<DeviceInfo> deviceInfos = deviceAssociationDao.findDeviceInfo("harmanId");
        assertEquals(deviceInfoList, deviceInfos);
    }

    @Test
    public void findDeviceInfoByNameTest() {

        String[] names = {"name1", "name2"};
        List<DeviceInfo> deviceInfoList = new ArrayList<>();
        deviceInfoList.add(new DeviceInfo());

        Mockito.doReturn(deviceInfoList).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyObject(), Mockito.any(DeviceInfoRowMapper.class));
        List<DeviceInfo> deviceInfos = deviceAssociationDao.findDeviceInfoByName("harmanId", names);
        assertEquals(deviceInfoList, deviceInfos);
    }

    @Test
    public void findAssociationTest() {
        Map<String, Object> queryConditionMap = new HashMap<>();
        queryConditionMap.put("key", new Object());
        DeviceAssociation deviceAssociation = new DeviceAssociation();

        Mockito.doReturn(deviceAssociation).when(jdbcTemplate)
            .queryForObject("selectQuery", new DeviceAssociationRowMapper(),
                queryConditionMap.values().toArray(new Object[1]));
        DeviceAssociation deviceAssociations = deviceAssociationDao.findAssociation(queryConditionMap);
        assertNull(deviceAssociations);
    }

    //@Test
    //    public void findAssociationTest_throwEmptyResultDataAccessException() {
    //        Map<String, Object> queryConditionMap = new HashMap<>();
    //        queryConditionMap.put("key", new Object());
    //
    //        Mockito.doThrow(DataAccessException.class).when(jdbcTemplate)
    //            .queryForObject("selectQuery", new DeviceAssociationRowMapper(),
    //                queryConditionMap.values().toArray(new Object[1]));
    //        deviceAssociationDao.findAssociation(queryConditionMap);
    //    }

    @Test
    public void validUserAssociationTest() {
        Map<String, Object> queryConditionMap = new HashMap<>();
        queryConditionMap.put("key", Integer.class);
        queryConditionMap.put("key1", new Object());
        queryConditionMap.put("key2", new Object());

        Mockito.doReturn(true).when(jdbcTemplate)
            .queryForObject("selectQuery", Boolean.class, queryConditionMap.values().toArray(new Object[1]));
        assertFalse(deviceAssociationDao.validUserAssociation(queryConditionMap));
    }

    @Test
    public void validUserAssociationTest_null() {
        Map<String, Object> queryConditionMap = new HashMap<>();
        queryConditionMap.put("key", Integer.class);
        queryConditionMap.put("key1", new Object());
        queryConditionMap.put("key2", new Object());

        Mockito.doReturn(null).when(jdbcTemplate)
            .queryForObject("selectQuery", Boolean.class, queryConditionMap.values().toArray(new Object[1]));
        assertFalse(deviceAssociationDao.validUserAssociation(queryConditionMap));
    }

    @Test
    public void updateForDisassociationByDeviceIdTest() {
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATION_INITIATED);
        deviceAssociation.setDisassociatedBy("user1");
        deviceAssociation.setModifiedBy("user2");
        deviceAssociation.setId(ID);
        deviceAssociation.setHarmanId("harmanId");
        deviceAssociation.setDisassociatedOn(new Timestamp(System.currentTimeMillis()));
        int updatedCount = deviceAssociationDao.updateForDisassociationByDeviceId(deviceAssociation);
        assertEquals(0, updatedCount);
    }

    @Test
    public void fetchAssociationByIdTest() {

        Mockito.doReturn(new DeviceAssociation()).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.any(DeviceAssociationRowMapper.class), Mockito.anyLong());
        DeviceAssociation deviceAssociation = deviceAssociationDao.fetchAssociationById(ID);
        assertNotNull(deviceAssociation);
    }

    @Test
    public void fetchAssociationByIdTest_throwEmptyResultDataAccessException() {

        Mockito.doThrow(EmptyResultDataAccessException.class).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.any(DeviceAssociationRowMapper.class), Mockito.anyLong());
        DeviceAssociation deviceAssociation = deviceAssociationDao.fetchAssociationById(ID);
        assertNull(deviceAssociation);
    }

    @Test
    public void validateUserIsOwnerOfDeviceTest() {

        Mockito.doReturn(true).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.eq(Boolean.class), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString());
        assertTrue(deviceAssociationDao.validateUserIsOwnerOfDevice("assocType", "userId", "1234"));
    }

    @Test
    public void validateUserIsOwnerOfDeviceTest_null() {

        Mockito.doReturn(null).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.eq(Boolean.class), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString());
        assertFalse(deviceAssociationDao.validateUserIsOwnerOfDevice("assocType", "userId", "1234"));
    }

    @Test
    public void getAssociationTypeUsageCountTest() {

        Integer usageCount = Integer.valueOf(1);
        Mockito.doReturn(usageCount).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.eq(Integer.class), Mockito.anyString());
        Integer actualUsageCount = deviceAssociationDao.getAssociationTypeUsageCount("assocType");
        assertEquals(usageCount, actualUsageCount);
    }


}

