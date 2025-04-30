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

import org.eclipse.ecsp.services.factorydata.domain.DeviceInfoAggregateFactoryData;
import org.eclipse.ecsp.services.factorydata.domain.DeviceInfoFactoryData;
import org.eclipse.ecsp.services.factorydata.domain.DeviceInfoFactoryDataWithSubscription;
import org.eclipse.ecsp.services.factorydata.domain.DeviceStateHistory;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.security.InvalidParameterException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for DeviceInfoFactoryDataDao.
 */
public class DeviceInfoFactoryDataDaoTest {
    public static final long ID = 1234L;
    @InjectMocks
    DeviceInfoFactoryDataDao deviceInfoFactoryDataDao;
    @Mock
    NamedParameterJdbcTemplate namedParamJdbcTemplate;
    DeviceInfoFactoryData deviceInfoFactoryData;
    @Mock
    private JdbcTemplate jdbcTemplate;

    /**
     * run before each test.
     */
    @Before
    public void beforeEach() {
        initMocks(this);

        deviceInfoFactoryData = new DeviceInfoFactoryData();
        deviceInfoFactoryData.setId(ID);
        deviceInfoFactoryData.setManufacturingDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        deviceInfoFactoryData.setModel("Mx1234");
        deviceInfoFactoryData.setImei("1234");
        deviceInfoFactoryData.setSerialNumber("12345");
        deviceInfoFactoryData.setPlatformVersion("V1");
        deviceInfoFactoryData.setIccid("1234");
        deviceInfoFactoryData.setSsid("1234");
        deviceInfoFactoryData.setBssid("1234");
        deviceInfoFactoryData.setMsisdn("1234");
        deviceInfoFactoryData.setImsi("1234");
        deviceInfoFactoryData.setRecordDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        deviceInfoFactoryData.setCreatedDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        deviceInfoFactoryData.setFactoryAdmin("Owner");
        deviceInfoFactoryData.setState("PROVISIONED");
        deviceInfoFactoryData.setPackageSerialNumber("1234");
        deviceInfoFactoryData.setDeviceType("Dongle");
    }

    @Test(expected = InvalidParameterException.class)
    public void deletefactoryDataTest_ZeroDeleted() {


        Mockito.doReturn(0).when(jdbcTemplate).update(Mockito.anyString(), Mockito.anyString());
        deviceInfoFactoryDataDao.deletefactoryData("1234", "989899", new DeviceInfoFactoryData());
    }

    @Test(expected = InvalidParameterException.class)
    public void deletefactoryDataTest_EmptyImei() {

        Mockito.doReturn(0).when(jdbcTemplate).update(Mockito.anyString(), Mockito.anyString());
        deviceInfoFactoryDataDao.deletefactoryData(null, "989899", new DeviceInfoFactoryData());
    }

    @Test(expected = InvalidParameterException.class)
    public void deletefactoryDataTest_EmptyImeiAndSerialNumber() {

        Mockito.doReturn(0).when(jdbcTemplate).update(Mockito.anyString(), Mockito.anyString());
        deviceInfoFactoryDataDao.deletefactoryData(null, "", new DeviceInfoFactoryData());
    }

    @Test(expected = InvalidParameterException.class)
    public void changeDeviceStateTest_Stolen() {

        String state = "STOLEN";
        Mockito.doReturn(0).when(jdbcTemplate).update(Mockito.anyString(), Mockito.anyString());
        deviceInfoFactoryDataDao.changeDeviceState(ID, state, "Action");
    }

    @Test(expected = InvalidParameterException.class)
    public void changeDeviceStateTest_Faulty() {

        Mockito.doReturn(0).when(jdbcTemplate).update(Mockito.anyString(), Mockito.anyString());
        deviceInfoFactoryDataDao.changeDeviceState(ID, "FAULTY", "Action");
    }

    @Test(expected = InvalidParameterException.class)
    public void changeDeviceStateTest_Active() {

        String state = "ACTIVE";
        Mockito.doReturn(0).when(jdbcTemplate).update(Mockito.anyString(), Mockito.anyString());
        deviceInfoFactoryDataDao.changeDeviceState(ID, state, "Action");
    }

    @Test(expected = InvalidParameterException.class)
    public void changeDeviceStateTest_Default() {

        Mockito.doReturn(0).when(jdbcTemplate).update(Mockito.anyString(), Mockito.anyString());
        deviceInfoFactoryDataDao.changeDeviceState(ID, "PROVISIONED", "Action");
    }

    @Test(expected = InvalidParameterException.class)
    public void changeDeviceStateForStolenOrFaultyTest_Updated0() {

        Mockito.doReturn(0).when(jdbcTemplate).update(Mockito.anyString(), Mockito.anyString());
        deviceInfoFactoryDataDao.changeDeviceStateForStolenOrFaulty(ID, "ACTIVE", "action");
    }

    @Test(expected = InvalidParameterException.class)
    public void changeDeviceStateForStolenOrFaultyTest_Updated1() {

        Mockito.doReturn(1).when(jdbcTemplate).update(Mockito.anyString(), (Object[]) Mockito.anyObject());
        deviceInfoFactoryDataDao.changeDeviceStateForStolenOrFaulty(ID, "ACTIVE", "action");
    }

    @Test
    public void createHistoryTableEntryTest() {

        jdbcTemplate.update(Mockito.anyString());
        deviceInfoFactoryDataDao.createHistoryTableEntry(deviceInfoFactoryData, "action");
        Assertions.assertEquals(0, jdbcTemplate.update(Mockito.anyString()));
    }

    @Test
    public void updateFactoryDataStateTest() {

        Mockito.doReturn(0).when(jdbcTemplate).update(Mockito.anyString());
        deviceInfoFactoryDataDao.updateFactoryDataState(ID, "action");
        Assertions.assertEquals(0, jdbcTemplate.update(Mockito.anyString()));
    }

    @Test
    public void getTimestampTest() {

        Timestamp timestamp = deviceInfoFactoryDataDao.getTimestamp("29/03/2019");
        Assertions.assertNull(timestamp);
    }

    @Test
    public void getTimestampTest1() {

        Timestamp timestamp = deviceInfoFactoryDataDao.getTimestamp("2019-03-20 11:11:11.001");
        Assertions.assertNotNull(timestamp);
    }

    @Test
    public void findByFactoryIdTest() {

        DeviceInfoFactoryData deviceInfoFactoryData = deviceInfoFactoryDataDao.findByFactoryId(ID);
        Assertions.assertNull(deviceInfoFactoryData);
    }

    @Test
    public void findByFactoryIdTest_JdbcTemplate() {

        List<DeviceInfoFactoryData> deviceInfoFactoryDataList = new ArrayList<>();
        deviceInfoFactoryDataList.add(deviceInfoFactoryData);

        Mockito.doReturn(deviceInfoFactoryDataList).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyObject(), Mockito.any(DeviceInfoFactoryDataMapper.class));

        DeviceInfoFactoryData deviceInfoFactoryData = deviceInfoFactoryDataDao.findByFactoryId(ID);
        Assertions.assertNotNull(deviceInfoFactoryData);
    }

    @Test
    public void findByFactoryImeiTest() {

        List<DeviceInfoFactoryData> deviceInfoFactoryDataList = new ArrayList<>();
        deviceInfoFactoryDataList.add(deviceInfoFactoryData);

        Mockito.doReturn(deviceInfoFactoryDataList).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyObject(), Mockito.any(DeviceInfoFactoryDataMapper.class));
        DeviceInfoFactoryData deviceInfoFactoryData = deviceInfoFactoryDataDao.findByFactoryImei("12345");
        Assertions.assertNotNull(deviceInfoFactoryData);
    }

    @Test
    public void findByFactoryImeiTest_EmptyFactoryDataList() {

        List<DeviceInfoFactoryData> deviceInfoFactoryDataList = new ArrayList<>();
        Mockito.doReturn(deviceInfoFactoryDataList).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyObject(), Mockito.any(DeviceInfoFactoryDataMapper.class));
        DeviceInfoFactoryData deviceInfoFactoryData = deviceInfoFactoryDataDao.findByFactoryImei("12345");
        Assertions.assertNull(deviceInfoFactoryData);
    }

    @Test
    public void findIdByFactoryImeiTest() {

        Mockito.doReturn(ID).when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.anyObject(), Mockito.eq(Long.class));
        Long factoryId = deviceInfoFactoryDataDao.findIdByFactoryImei("1234");
        Assertions.assertEquals(ID, factoryId);
    }

    @Test
    public void findByFactoryIdAndImeiTest() {

        List<DeviceInfoFactoryData> deviceInfoFactoryDataList = new ArrayList<>();
        deviceInfoFactoryDataList.add(deviceInfoFactoryData);

        Mockito.doReturn(deviceInfoFactoryDataList).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyObject(), Mockito.any(DeviceInfoFactoryDataMapper.class));
        DeviceInfoFactoryData deviceInfoFactoryData = deviceInfoFactoryDataDao.findByFactoryIdAndImei(ID, "12345");
        Assertions.assertNotNull(deviceInfoFactoryData);
    }

    @Test
    public void findFactoryDataBySerialNumberTest() {

        List<DeviceInfoFactoryData> deviceInfoFactoryDataList = new ArrayList<>();
        deviceInfoFactoryDataList.add(deviceInfoFactoryData);

        Mockito.doReturn(deviceInfoFactoryDataList).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyObject(), Mockito.any(DeviceInfoFactoryDataMapper.class));
        DeviceInfoFactoryData deviceInfoFactoryData = deviceInfoFactoryDataDao.findFactoryDataBySerialNumber("12345");
        Assertions.assertNotNull(deviceInfoFactoryData);
    }

    @Test
    public void constructAndFetchFactoryDataTest() {

        List<DeviceInfoFactoryData> deviceInfoFactoryDataList =
                deviceInfoFactoryDataDao.constructAndFetchFactoryData(deviceInfoFactoryData);
        Assertions.assertNotNull(deviceInfoFactoryDataList);
    }

    @Test
    public void constructAndFetchFactoryDataTest_EmptyData() {
        DeviceInfoFactoryData deviceInfoFactoryData2 = new DeviceInfoFactoryData();
        deviceInfoFactoryData2.setId(ID);
        deviceInfoFactoryData2.setModel("Mx1234");
        deviceInfoFactoryData2.setPlatformVersion("V1");
        deviceInfoFactoryData2.setFactoryAdmin("Owner");
        deviceInfoFactoryData2.setState("PROVISIONED");
        deviceInfoFactoryData2.setPackageSerialNumber("1234");
        deviceInfoFactoryData2.setDeviceType("Dongle");
        List<DeviceInfoFactoryData> deviceInfoFactoryDataList =
                deviceInfoFactoryDataDao.constructAndFetchFactoryData(deviceInfoFactoryData2);
        Assertions.assertNotNull(deviceInfoFactoryDataList);
    }

    @Test
    public void fetchDeviceInfoFactoryDataTest_NoFactoryDataFound() {

        Map<String, Object> orderedMap = new HashMap<>();
        DeviceInfoFactoryData deviceInfoFactoryData = deviceInfoFactoryDataDao.fetchDeviceInfoFactoryData(orderedMap);
        Assertions.assertNull(deviceInfoFactoryData);
    }

    @Test
    public void fetchDeviceInfoFactoryDataTest_NullList() {

        Map<String, Object> orderedMap = new HashMap<>();
        orderedMap.put("serial_number", "1234");
        Mockito.doReturn(null).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.any(), Mockito.any(DeviceInfoFactoryDataMapper.class));
        DeviceInfoFactoryData deviceInfoFactoryData = deviceInfoFactoryDataDao.fetchDeviceInfoFactoryData(orderedMap);
        Assertions.assertNull(deviceInfoFactoryData);
    }

    @Test
    public void fetchDeviceInfoFactoryDataTest_ValidList() {

        Map<String, Object> orderedMap = new HashMap<>();
        orderedMap.put("serial_number", "1234");
        List<DeviceInfoFactoryData> deviceInfoFactoryDataList = new ArrayList<>();
        deviceInfoFactoryDataList.add(deviceInfoFactoryData);
        Mockito.doReturn(deviceInfoFactoryDataList).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.any(), Mockito.any(DeviceInfoFactoryDataMapper.class));
        DeviceInfoFactoryData deviceInfoFactoryData = deviceInfoFactoryDataDao.fetchDeviceInfoFactoryData(orderedMap);
        Assertions.assertNotNull(deviceInfoFactoryData);
    }

    @Test
    public void updateTest_EmptyOrderedMap() {
        Map<String, Object> conditionalOrderedMap = new HashMap<>();
        Map<String, Object> orderedMap = new HashMap<>();
        int updatedCount = deviceInfoFactoryDataDao.update(conditionalOrderedMap, orderedMap);
        Assertions.assertEquals(0, updatedCount);
    }

    @Test
    public void updateTest() {
        Map<String, Object> conditionalOrderedMap = new HashMap<>();
        conditionalOrderedMap.put("serial_number", "1234");
        Map<String, Object> orderedMap = new HashMap<>();
        orderedMap.put("serial_number", "1234");
        int updatedCount = deviceInfoFactoryDataDao.update(conditionalOrderedMap, orderedMap);
        Assertions.assertEquals(0, updatedCount);
    }

    @Test
    public void constructFetchFactoryDataTest() {

        List<DeviceInfoFactoryData> deviceInfoFactoryDataList = new ArrayList<>();
        deviceInfoFactoryDataList.add(deviceInfoFactoryData);
        String asc = "imei";
        Mockito.doReturn(deviceInfoFactoryDataList).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class),
                Mockito.any(DeviceInfoFactoryDataMapper.class));
        List<DeviceInfoFactoryData> actualDeviceInfoFactoryData = deviceInfoFactoryDataDao.constructFetchFactoryData(
                1, 1, asc, asc, "12345", "12345");
        Assertions.assertEquals(deviceInfoFactoryDataList, actualDeviceInfoFactoryData);
    }

    @Test
    public void constructFetchFactoryDataTest_EmptyAsc() {

        List<DeviceInfoFactoryData> deviceInfoFactoryDataList = new ArrayList<>();
        deviceInfoFactoryDataList.add(deviceInfoFactoryData);
        String asc = "";
        Mockito.doReturn(deviceInfoFactoryDataList).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class),
                Mockito.any(DeviceInfoFactoryDataMapper.class));
        List<DeviceInfoFactoryData> actualDeviceInfoFactoryData = deviceInfoFactoryDataDao.constructFetchFactoryData(
                1, 1, asc, "imei", "12345", "12345");
        Assertions.assertEquals(deviceInfoFactoryDataList, actualDeviceInfoFactoryData);
    }

    @Test
    public void constructFetchFactoryDataTest_EmptyDesc() {

        List<DeviceInfoFactoryData> deviceInfoFactoryDataList = new ArrayList<>();
        deviceInfoFactoryDataList.add(deviceInfoFactoryData);

        Mockito.doReturn(deviceInfoFactoryDataList).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class),
                Mockito.any(DeviceInfoFactoryDataMapper.class));
        List<DeviceInfoFactoryData> actualDeviceInfoFactoryData = deviceInfoFactoryDataDao.constructFetchFactoryData(
                1, 1, "imei", "", "12345", "12345");
        Assertions.assertEquals(deviceInfoFactoryDataList, actualDeviceInfoFactoryData);
    }

    @Test
    public void constructFetchFactoryDataTest_Empty() {

        List<DeviceInfoFactoryData> deviceInfoFactoryDataList = new ArrayList<>();
        deviceInfoFactoryDataList.add(deviceInfoFactoryData);

        Mockito.doReturn(deviceInfoFactoryDataList).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class),
                Mockito.any(DeviceInfoFactoryDataMapper.class));
        List<DeviceInfoFactoryData> actualDeviceInfoFactoryData = deviceInfoFactoryDataDao.constructFetchFactoryData(
                1, 1, "", "", "12345", "12345");
        Assertions.assertEquals(deviceInfoFactoryDataList, actualDeviceInfoFactoryData);
    }

    @Test
    public void constructFetchAgrigateDeviceStateTest() {

        DeviceInfoAggregateFactoryData.StateCount stateCount =
                deviceInfoFactoryDataDao.constructFetchAgrigateDeviceState("12345", "1234");
        Assertions.assertNotNull(stateCount);
    }

    @Test
    public void constructFetchTotalFactoryDataTest() {

        Long deviceCount = deviceInfoFactoryDataDao.constructFetchTotalFactoryData(
                "12345", "1234");
        Assertions.assertNull(deviceCount);
    }

    @Test
    public void constructFetchTotalFactoryDataTest_Empty() {

        Long deviceCount = deviceInfoFactoryDataDao.constructFetchTotalFactoryData("", "");
        Assertions.assertNull(deviceCount);
    }

    @Test
    public void constructFetchTotalFactoryDataTest_second_EmptyLists() {

        List<String> containsLikeFieldList = new ArrayList<>();
        List<String> containsLikeValueList = new ArrayList<>();
        List<String> rangeFieldList = new ArrayList<>();
        List<String> rangeValueList = new ArrayList<>();

        Long deviceCount = deviceInfoFactoryDataDao.constructFetchTotalFactoryData(containsLikeFieldList,
                containsLikeValueList, rangeFieldList, rangeValueList);
        Assertions.assertNull(deviceCount);
    }

    @Test
    public void constructFetchTotalFactoryDataTest_second() {

        List<String> containsLikeFieldList = new ArrayList<>();
        List<String> containsLikeValueList = new ArrayList<>();
        List<String> rangeFieldList = new ArrayList<>();
        containsLikeFieldList.add("likefield1");
        containsLikeValueList.add("likevalue1");
        rangeFieldList.add("rangefield1");
        List<String> rangeValueList = new ArrayList<>();
        rangeValueList.add("r_1");
        Long deviceCount = deviceInfoFactoryDataDao.constructFetchTotalFactoryData(containsLikeFieldList,
                containsLikeValueList, rangeFieldList, rangeValueList);
        Assertions.assertNull(deviceCount);
    }

    @Test
    public void constructFetchTotalFactoryDataTest_second_EmptyLikeList() {

        List<String> containsLikeFieldList = new ArrayList<>();
        List<String> containsLikeValueList = new ArrayList<>();
        List<String> rangeFieldList = new ArrayList<>();
        List<String> rangeValueList = new ArrayList<>();
        rangeFieldList.add("rangefield1");
        rangeValueList.add("r_1");
        Long deviceCount = deviceInfoFactoryDataDao.constructFetchTotalFactoryData(containsLikeFieldList,
                containsLikeValueList, rangeFieldList, rangeValueList);
        Assertions.assertNull(deviceCount);
    }

    @Test
    public void constructFetchTotalFactoryDataTest_second_EmptyRangeList() {

        List<String> containsLikeFieldList = new ArrayList<>();
        List<String> containsLikeValueList = new ArrayList<>();
        List<String> rangeFieldList = new ArrayList<>();
        List<String> rangeValueList = new ArrayList<>();
        containsLikeFieldList.add("likefield1");
        containsLikeValueList.add("likevalue1");
        Long deviceCount = deviceInfoFactoryDataDao.constructFetchTotalFactoryData(containsLikeFieldList,
                containsLikeValueList, rangeFieldList, rangeValueList);
        Assertions.assertNull(deviceCount);
    }

    @Test
    public void constructFetchAgrigateDeviceStateTest_Second_EmptyLists() {

        List<String> containsLikeFieldList = new ArrayList<>();
        List<String> containsLikeValueList = new ArrayList<>();
        List<String> rangeFieldList = new ArrayList<>();
        List<String> rangeValueList = new ArrayList<>();

        DeviceInfoAggregateFactoryData.StateCount stateCount =
                deviceInfoFactoryDataDao.constructFetchAgrigateDeviceState(containsLikeFieldList,
                        containsLikeValueList, rangeFieldList, rangeValueList);
        Assertions.assertNotNull(stateCount);
    }

    @Test
    public void constructFetchAgrigateDeviceStateTest_Second() {

        List<String> containsLikeFieldList = new ArrayList<>();
        List<String> containsLikeValueList = new ArrayList<>();
        List<String> rangeFieldList = new ArrayList<>();
        containsLikeFieldList.add("likefield1");
        containsLikeValueList.add("likevalue1");
        rangeFieldList.add("rangefield1");
        List<String> rangeValueList = new ArrayList<>();
        rangeValueList.add("r_1");
        DeviceInfoAggregateFactoryData.StateCount stateCount =
                deviceInfoFactoryDataDao.constructFetchAgrigateDeviceState(containsLikeFieldList,
                        containsLikeValueList, rangeFieldList, rangeValueList);
        Assertions.assertNotNull(stateCount);
    }

    @Test
    public void constructFetchAgrigateDeviceStateTest_Second_EmptyLikeList() {

        List<String> containsLikeFieldList = new ArrayList<>();
        List<String> containsLikeValueList = new ArrayList<>();
        List<String> rangeFieldList = new ArrayList<>();
        List<String> rangeValueList = new ArrayList<>();
        rangeFieldList.add("rangefield1");
        rangeValueList.add("r_1");
        DeviceInfoAggregateFactoryData.StateCount stateCount =
                deviceInfoFactoryDataDao.constructFetchAgrigateDeviceState(containsLikeFieldList,
                        containsLikeValueList, rangeFieldList, rangeValueList);
        Assertions.assertNotNull(stateCount);
    }

    @Test
    public void constructFetchAgrigateDeviceStateTest_Second_EmptyRangeList() {

        List<String> containsLikeFieldList = new ArrayList<>();
        List<String> containsLikeValueList = new ArrayList<>();
        List<String> rangeFieldList = new ArrayList<>();
        List<String> rangeValueList = new ArrayList<>();
        containsLikeFieldList.add("likefield1");
        containsLikeValueList.add("likevalue1");
        DeviceInfoAggregateFactoryData.StateCount stateCount =
                deviceInfoFactoryDataDao.constructFetchAgrigateDeviceState(containsLikeFieldList,
                        containsLikeValueList, rangeFieldList, rangeValueList);
        Assertions.assertNotNull(stateCount);
    }

    @Test
    public void constructFetchTotalFactoryDataForDeviceDetailsTest_Imei() {

        Long total = deviceInfoFactoryDataDao.constructFetchTotalFactoryDataForDeviceDetails(
                DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.IMEI, "inputTypeValue");
        Assertions.assertNull(total);
    }

    @Test
    public void constructFetchTotalFactoryDataForDeviceDetailsTest_SerialNumber() {

        Long total = deviceInfoFactoryDataDao.constructFetchTotalFactoryDataForDeviceDetails(
                DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.SERIAL_NUMBER, "inputTypeValue");
        Assertions.assertNull(total);
    }

    @Test
    public void constructFetchTotalFactoryDataForDeviceDetailsTest_DeviceId() {

        Long total = deviceInfoFactoryDataDao.constructFetchTotalFactoryDataForDeviceDetails(
                DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.DEVICE_ID, "inputTypeValue");
        Assertions.assertNull(total);
    }

    @Test
    public void constructFetchTotalFactoryDataForDeviceDetailsTest_State() {

        Long total = deviceInfoFactoryDataDao.constructFetchTotalFactoryDataForDeviceDetails(
                DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.STATE, "inputTypeValue");
        Assertions.assertNull(total);
    }

    @Test
    public void constructFetchTotalFactoryDataForDeviceDetailsTest_Vin() {

        Long total = deviceInfoFactoryDataDao.constructFetchTotalFactoryDataForDeviceDetails(
                DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.VIN, "inputTypeValue");
        Assertions.assertNull(total);
    }

    @Test
    public void constructFetchFactoryDataTest_Imei() {

        List<DeviceInfoFactoryDataWithSubscription> deviceInfoFactoryDataWithSubscriptions =
                deviceInfoFactoryDataDao.constructFetchFactoryData(
                        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.IMEI, "searchKey", 1,
                        1, "asc", "desc", true);
        Assertions.assertNotNull(deviceInfoFactoryDataWithSubscriptions);
    }

    @Test
    public void constructFetchFactoryDataTest_Vin() {

        List<DeviceInfoFactoryDataWithSubscription> deviceInfoFactoryDataWithSubscriptions =
                deviceInfoFactoryDataDao.constructFetchFactoryData(
                        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.VIN, "searchKey", 1,
                        1, "asc", "desc", true);
        Assertions.assertNotNull(deviceInfoFactoryDataWithSubscriptions);
    }

    @Test
    public void constructFetchFactoryDataTest_SerialNumber() {

        List<DeviceInfoFactoryDataWithSubscription> deviceInfoFactoryDataWithSubscriptions =
                deviceInfoFactoryDataDao.constructFetchFactoryData(
                        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.VIN, "searchKey", 1,
                        1, "asc", "desc", true);
        Assertions.assertNotNull(deviceInfoFactoryDataWithSubscriptions);
    }

    @Test
    public void constructFetchFactoryDataTest_State() {

        List<DeviceInfoFactoryDataWithSubscription> deviceInfoFactoryDataWithSubscriptions =
                deviceInfoFactoryDataDao.constructFetchFactoryData(
                        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.STATE, "searchKey", 1,
                        1, "asc", "desc", true);
        Assertions.assertNotNull(deviceInfoFactoryDataWithSubscriptions);
    }

    @Test
    public void constructFetchFactoryDataTest_DeviceId() {

        List<DeviceInfoFactoryDataWithSubscription> deviceInfoFactoryDataWithSubscriptions =
                deviceInfoFactoryDataDao.constructFetchFactoryData(
                        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.DEVICE_ID, "searchKey", 1,
                        1, "asc", "desc", false);
        Assertions.assertNotNull(deviceInfoFactoryDataWithSubscriptions);
    }

    @Test
    public void constructFetchAggregrateFactoryDataTest_Imei() {

        DeviceInfoAggregateFactoryData.StateCount stateCount =
                deviceInfoFactoryDataDao.constructFetchAggregrateFactoryData(
                        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.SERIAL_NUMBER, "key");
        Assertions.assertNotNull(stateCount);
    }

    @Test
    public void constructFetchAggregrateFactoryDataTest_Serial_Number() {

        DeviceInfoAggregateFactoryData.StateCount stateCount =
                deviceInfoFactoryDataDao.constructFetchAggregrateFactoryData(
                        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.SERIAL_NUMBER, "key");
        Assertions.assertNotNull(stateCount);
    }

    @Test
    public void constructFetchAggregrateFactoryDataTest_Device_Id() {

        DeviceInfoAggregateFactoryData.StateCount stateCount =
                deviceInfoFactoryDataDao.constructFetchAggregrateFactoryData(
                        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.DEVICE_ID, "key");
        Assertions.assertNotNull(stateCount);
    }

    @Test
    public void constructFetchAggregrateFactoryDataTest_Vin() {

        DeviceInfoAggregateFactoryData.StateCount stateCount =
                deviceInfoFactoryDataDao.constructFetchAggregrateFactoryData(
                        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.VIN, "key");
        Assertions.assertNotNull(stateCount);
    }

    @Test
    public void constructFetchAggregrateFactoryDataTest_State() {

        DeviceInfoAggregateFactoryData.StateCount stateCount =
                deviceInfoFactoryDataDao.constructFetchAggregrateFactoryData(
                        DeviceInfoFactoryDataDao.DeviceDetailsInputTypeEnum.STATE, "key");
        Assertions.assertNotNull(stateCount);
    }

    @Test
    public void findTotalDeviceStateTest() {

        Mockito.doReturn(1L).when(namedParamJdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class), Mockito.eq(Long.class));
        long deviceCount = deviceInfoFactoryDataDao.findTotalDeviceState("1234");
        Assertions.assertEquals(1L, deviceCount);
    }

    @Test
    public void constructAndFetchDeviceStatesTest() {

        List<DeviceStateHistory> deviceHistory = new ArrayList<>();
        Mockito.doReturn(deviceHistory).when(namedParamJdbcTemplate)
            .query(Mockito.anyString(), Mockito.any(MapSqlParameterSource.class),
                Mockito.any(DeviceStateHistoryDataMapper.class));
        List<DeviceStateHistory> actualDeviceHistory = deviceInfoFactoryDataDao.constructAndFetchDeviceStates(1, 1,
                "asc", "imei", "1234");
        Assertions.assertEquals(deviceHistory, actualDeviceHistory);
    }

    @Test
    public void getModelByImeiTest() {

        Mockito.doReturn("value").when(jdbcTemplate)
            .queryForObject(Mockito.anyString(), Mockito.any(), Mockito.eq(String.class));
        String model = deviceInfoFactoryDataDao.getModelByImei("1234");
        Assertions.assertEquals("value", model);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findVinEitherByImeiOrSerialNumberTest_EmptyInput() {

        deviceInfoFactoryDataDao.findVinEitherByImeiOrSerialNumber("", "");
    }

    @Test
    public void findVinEitherByImeiOrSerialNumberTest_EmptyImei() {

        String vin = deviceInfoFactoryDataDao.findVinEitherByImeiOrSerialNumber("", "123456");
        Assertions.assertNull(vin);
    }

    @Test
    public void findVinEitherByImeiOrSerialNumberTest_EmptySerialNumber() {

        String vin = deviceInfoFactoryDataDao.findVinEitherByImeiOrSerialNumber("1234", "");
        Assertions.assertNull(vin);
    }

    @Test
    public void insertIntoDeviceInfoFactoryDataTest() {

        deviceInfoFactoryDataDao.insertIntoDeviceInfoFactoryData(deviceInfoFactoryData, "userId", "23/04/2019",
            "23/04/2019");
        Assertions.assertNotNull(deviceInfoFactoryData);
    }

}
