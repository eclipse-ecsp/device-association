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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.ecsp.services.factorydata.domain.DeviceInfoAggregateFactoryData;
import org.eclipse.ecsp.services.factorydata.domain.DeviceInfoFactoryData;
import org.eclipse.ecsp.services.factorydata.domain.DeviceInfoFactoryDataWithSubscription;
import org.eclipse.ecsp.services.factorydata.domain.DeviceState;
import org.eclipse.ecsp.services.factorydata.domain.DeviceStateAggregateData;
import org.eclipse.ecsp.services.factorydata.domain.DeviceStateHistory;
import org.eclipse.ecsp.services.shared.util.SqlUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.eclipse.ecsp.common.CommonConstants.JOIN_CONDITION;
import static org.eclipse.ecsp.common.CommonConstants.SELECT_FROM_DEVICE_INFO_FACTORY_DATA;
import static org.eclipse.ecsp.common.CommonConstants.SELECT_FROM_DEVICE_INFO_FACTORY_DATA_AND_ASSOCIATION;
import static org.eclipse.ecsp.common.CommonConstants.SELECT_FROM_DEVICE_INFO_FACTORY_DATA_WHERE;
import static org.eclipse.ecsp.common.CommonConstants.SELECT_ID_FROM_DEVICE_INFO_FACTORY_DATA_WHERE;
import static org.eclipse.ecsp.common.CommonConstants.VIN_DETAILS_JOIN_CONDITION;
import static org.eclipse.ecsp.services.factorydata.dao.DeviceInfoFactoryDataMapper.getDeviceInfoFactoryDataMapper;
import static org.eclipse.ecsp.services.factorydata.dao.DeviceInfoFactoryDataMapperWithSubscription.getDeviceInfoFactoryDataWithSubscriptionMapper;
import static org.eclipse.ecsp.services.factorydata.dao.DeviceStateHistoryDataMapper.getDeviceSatetHistoryDataMapper;

/**
 * The DAO (Data Access Object) class for managing device information factory data.
 * This class provides methods for CRUD (Create, Read, Update, Delete) operations on the factory data.
 */
@Repository
@Slf4j
public class DeviceInfoFactoryDataDao {
    private static final int INDEX_2 = 2;
    private static final int INDEX_3 = 3;
    private static final int INDEX_4 = 4;
    private static final int INDEX_5 = 5;
    private static final int INDEX_6 = 6;
    private static final int INDEX_7 = 7;
    private static final int INDEX_8 = 8;
    private static final int INDEX_9 = 9;
    private static final int INDEX_10 = 10;
    private static final int INDEX_11 = 11;
    private static final int INDEX_12 = 12;
    private static final int INDEX_13 = 13;
    private static final int INDEX_14 = 14;
    private static final int INDEX_15 = 15;
    private static final int INDEX_16 = 16;
    private static final int INDEX_17 = 17;
    private static final int INDEX_18 = 18;
    private static final int INDEX_19 = 19;
    private static final String ID = "\"ID\"";
    private static final String IMEI = "imei";
    private static final String SERIAL_NUMBER = "serial_number";
    private static final String STATE = "state";
    private static final String GET_MODEL_BY_IMEI =
        "select \"model\" from public.\"DeviceInfoFactoryData\" where \"imei\" = ?";
    private static final String VIN_BY_SERIAL_NUMBER =
        "select b.vin from \"DeviceInfoFactoryData\" a, vin_details b where a.\"ID\"=b.reference_id and a" 
            +            ".serial_number=?";
    private static final String VIN_BY_IMEI =
        "select vin from vin_details v  left join \"DeviceInfoFactoryData\" d on v.reference_id=d.\"ID\" where d" 
            +            ".imei=?";

    public final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd");
    private static final String WHERE = " WHERE ";
    private static final String GET_COUNT_FROM_DIFD = "select count(*) from public.\"DeviceInfoFactoryData\" ";
    private static final String GROUP_BY_STATE = "group BY state ";
    private static final String GET_STATE_AND_STATE_COUNT_FROM_DIFD =
            "select state, count(state) as count from public.\"DeviceInfoFactoryData\" ";
    private static final String AND = " and ";
    private static final String PAGE_FILTER = "LIMIT :limit OFFSET :offset";
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParamJdbcTemplate;

    /**
     * Appends the "and" keyword to the given query creator if the isAdded flag is true.
     *
     * @param isAdded        a boolean flag indicating whether the "and" keyword should be added
     * @param queryCreator   the StringBuilder object representing the query creator
     * @return               the modified query creator with the "and" keyword appended, or null if isAdded is false
     */
    private static StringBuilder appendAnd(boolean isAdded, StringBuilder queryCreator) {
        if (isAdded) {
            return queryCreator.append("and ");
        } else {
            return null;
        }
    }

    /**
     * Deletes factory data based on the provided IMEI, serial number, and current data.
     *
     * @param imei           the IMEI of the device (optional)
     * @param serialnumber   the serial number of the device (optional)
     * @param currentData    the current device information factory data
     * @throws InvalidParameterException if the factory data cannot be deleted because the device is not in the
     *      provisioned state
     */
    public void deletefactoryData(String imei, String serialnumber, DeviceInfoFactoryData currentData) {
        Map<String, Object> orderedMap = new LinkedHashMap<>();
        orderedMap.put(STATE, DeviceState.PROVISIONED.getValue());
        if (!StringUtils.isEmpty(imei)) {
            orderedMap.put(IMEI, imei);
        }
        if (!StringUtils.isEmpty(serialnumber)) {
            orderedMap.put(SERIAL_NUMBER, serialnumber);
        }
        String prefix = "delete from  public.\"DeviceInfoFactoryData\" where ";
        String deletesql = SqlUtility.getPreparedSql(prefix, AND, orderedMap);
        Object[] values = SqlUtility.getArrayValues(orderedMap);
        int deleted = jdbcTemplate.update(deletesql, values);
        if (deleted == 0) {
            log.error("factory data can't be deleted as the device is not in  :{} state", DeviceState.PROVISIONED);
            throw new InvalidParameterException(
                String.format("factory data can't be deleted as the device is not in  : %s state",
                    DeviceState.PROVISIONED));
        }

        // update history table
        updateHistoryTable(currentData, DeviceState.DEACTIVATED.getValue());

    }

    /**
     * Changes the state of a device for a given factory ID.
     *
     * @param factoryId the ID of the factory
     * @param state the new state of the device
     * @param action the action performed on the device
     * @throws InvalidParameterException if the factory data is not found for the given factory ID
     */
    // @Transactional
    public void changeDeviceState(long factoryId, String state, final String action) {
        DeviceState deviceState = DeviceState.valueOf(state);
        String sql;
        int updated;
        log.debug("Changing device state for factory id {} and state {}", factoryId, state);
        switch (deviceState) {
            case STOLEN:
                sql = "update public.\"DeviceInfoFactoryData\" set isstolen=true where \"ID\"=?";
                updated = jdbcTemplate.update(sql, factoryId);
                break;
            case FAULTY:
                sql = "update public.\"DeviceInfoFactoryData\" set isfaulty=true where \"ID\"=?";
                updated = jdbcTemplate.update(sql, factoryId);
                break;
            case ACTIVE:
            default:
                sql =
                    "update public.\"DeviceInfoFactoryData\" set state=?, isstolen=false, isfaulty=false where " 
                    +                        "\"ID\"=?";
                updated = jdbcTemplate.update(sql, state, factoryId);
                break;
        }
        log.debug("Number of records updated for state change  : {}", updated);
        if (updated == 0) {
            log.error("factory data not found for factoryId :{}", factoryId);
            throw new InvalidParameterException(String.format("factory data not found for factoryId : %s", factoryId));
        }
        log.debug("Updating the history table for factory id : {}", factoryId);
        createHistoryTableEntry(factoryId, action);
    }

    /**
     * Changes the state of a device to "Stolen" or "Faulty" in the device factory data.
     *
     * @param factoryId the ID of the device factory data
     * @param state the new state to set for the device ("Stolen" or "Faulty")
     * @param action the action performed on the device
     * @throws InvalidParameterException if the factory data is not found for the given factory ID with the specified
     *      state
     */
    public void changeDeviceStateForStolenOrFaulty(long factoryId, String state, final String action) {
        String sql = "update public.\"DeviceInfoFactoryData\" set state=?  where \"ID\"=?";
        int updated = jdbcTemplate.update(sql, state, factoryId);

        log.debug("Number of records updated  : {}", updated);
        if (updated == 0) {
            log.error("Factory data not found for the factoryId: {} with Stolen or Faulty state", factoryId);
            throw new InvalidParameterException(
                String.format("Factory data not found for the factoryId: %s with Stolen or Faulty state", factoryId));
        } else {
            log.debug("Updating the history table for factory id : {}", factoryId);
            createHistoryTableEntry(factoryId, action);
        }
    }

    /**
     * Creates a history table entry for the given device information factory data and action.
     *
     * @param deviceInfoFactoryData The device information factory data.
     * @param action The action performed.
     */
    public void createHistoryTableEntry(DeviceInfoFactoryData deviceInfoFactoryData, String action) {
        updateHistoryTable(deviceInfoFactoryData, action);
    }

    /**
     * Creates a history table entry for the given factory ID and action.
     *
     * @param factoryId The ID of the factory.
     * @param action The action performed.
     * @throws InvalidParameterException if factory data is not found for the given factory ID.
     */
    private void createHistoryTableEntry(long factoryId, final String action) {
        final DeviceInfoFactoryData deviceInfoFactoryData = findByFactoryId(factoryId);
        if (deviceInfoFactoryData != null) {
            log.debug("Updating the history table");
            updateHistoryTable(deviceInfoFactoryData, action);
            log.debug("Done with history update");
        } else {
            throw new InvalidParameterException(String.format("factory data not found for factoryId : %s", factoryId));
        }
    }

    /**
     * Updates the state of the factory data with the given factory ID.
     *
     * @param factoryId the ID of the factory data to update
     * @param state the new state to set for the factory data
     * @return the number of rows affected by the update operation
     */
    public int updateFactoryDataState(long factoryId, String state) {

        String sql = "update public.\"DeviceInfoFactoryData\" set state=? where \"ID\"=?";
        return jdbcTemplate.update(sql, state, factoryId);

    }

    /**
     * Updates the history table with the provided device information factory data and action.
     *
     * @param deviceInfoFactoryData The device information factory data to be inserted into the history table.
     * @param action                The action performed on the device information factory data.
     */
    private void updateHistoryTable(final DeviceInfoFactoryData deviceInfoFactoryData, final String action) {
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String insertStatement =
                    "INSERT INTO public.\"DeviceInfoFactoryDataHistory\"(\"factory_id\", \"manufacturing_date\", "
                        + "\"model\",\"imei\",\"serial_number\",\"platform_version\",\"iccid\",\"ssid\",\"bssid\","
                        + "\"msisdn\",\"imsi\",\"record_date\",\"factory_created_date\",\"factory_admin\",\"state\","
                        + "\"action\",\"created_timestamp\",\"package_serial_number\",\"device_type\") "
                        + "    VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                PreparedStatement preparedStatement = con.prepareStatement(insertStatement);
                preparedStatement.setLong(1, deviceInfoFactoryData.getId());
                preparedStatement.setTimestamp(INDEX_2, deviceInfoFactoryData.getManufacturingDate());
                preparedStatement.setString(INDEX_3, deviceInfoFactoryData.getModel());
                preparedStatement.setString(INDEX_4, deviceInfoFactoryData.getImei());
                preparedStatement.setString(INDEX_5, deviceInfoFactoryData.getSerialNumber());
                preparedStatement.setString(INDEX_6, deviceInfoFactoryData.getPlatformVersion());
                preparedStatement.setString(INDEX_7, deviceInfoFactoryData.getIccid());
                preparedStatement.setString(INDEX_8, deviceInfoFactoryData.getSsid());
                preparedStatement.setString(INDEX_9, deviceInfoFactoryData.getBssid());
                preparedStatement.setString(INDEX_10, deviceInfoFactoryData.getMsisdn());
                preparedStatement.setString(INDEX_11, deviceInfoFactoryData.getImsi());
                preparedStatement.setTimestamp(INDEX_12, deviceInfoFactoryData.getRecordDate());
                preparedStatement.setTimestamp(INDEX_13, deviceInfoFactoryData.getCreatedDate());
                preparedStatement.setString(INDEX_14, deviceInfoFactoryData.getFactoryAdmin());
                preparedStatement.setString(INDEX_15, deviceInfoFactoryData.getState());
                preparedStatement.setString(INDEX_16, action);
                preparedStatement.setTimestamp(INDEX_17, new Timestamp(Calendar.getInstance().getTimeInMillis()));
                preparedStatement.setString(INDEX_18, deviceInfoFactoryData.getPackageSerialNumber());
                preparedStatement.setString(INDEX_19, deviceInfoFactoryData.getDeviceType());
                log.info("History Prepared Statement : " + preparedStatement);
                return preparedStatement;
            }
        });
    }

    /**
     * Converts a string representation of a date to a Timestamp object.
     *
     * @param date the string representation of the date in the format "yyyy-MM-dd hh:mm:ss.SSS"
     * @return the Timestamp object representing the given date string, or null if the conversion fails
     */
    public Timestamp getTimestamp(String date) {
        Timestamp timestamp = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            Date parsedDate = dateFormat.parse(date);
            timestamp = new java.sql.Timestamp(parsedDate.getTime());
        } catch (Exception e) {
            log.error("Error while convert string date to Timestamp : {}", e.getMessage());
        }
        return timestamp;
    }

    /**
     * Finds the device information factory data by factory ID.
     *
     * @param factoryId The ID of the factory.
     * @return The DeviceInfoFactoryData object if found, or null if not found.
     */
    public DeviceInfoFactoryData findByFactoryId(long factoryId) {
        Map<String, Object> orderedMap = new LinkedHashMap<>();
        orderedMap.put(ID, factoryId);

        String preparedSql =
            SqlUtility.getPreparedSql(SELECT_FROM_DEVICE_INFO_FACTORY_DATA_WHERE, AND, orderedMap);
        Object[] arrayValues = SqlUtility.getArrayValues(orderedMap);
        if (preparedSql == null || arrayValues == null) {
            return null;
        }
        List<DeviceInfoFactoryData> deviceInfoFactoryDataList = jdbcTemplate
            .query(preparedSql, arrayValues, new DeviceInfoFactoryDataMapper());

        if (!deviceInfoFactoryDataList.isEmpty()) {
            return deviceInfoFactoryDataList.get(0);
        }
        log.error("Factory data not found for factoryId: {}", factoryId);
        return null;
    }

    /**
     * Finds the device information factory data by the factory IMEI.
     *
     * @param imei The factory IMEI to search for.
     * @return The DeviceInfoFactoryData object if found, or null if not found.
     */
    public DeviceInfoFactoryData findByFactoryImei(String imei) {
        Map<String, Object> orderedMap = new LinkedHashMap<>();
        orderedMap.put(IMEI, imei);

        String sql = SqlUtility.getPreparedSql(SELECT_FROM_DEVICE_INFO_FACTORY_DATA_WHERE, AND, orderedMap);

        Object[] values = SqlUtility.getArrayValues(orderedMap);
        if (sql != null) {
            List<DeviceInfoFactoryData> deviceInfoFactoryDataList = jdbcTemplate.query(sql, values,
                new DeviceInfoFactoryDataMapper());

            if (!deviceInfoFactoryDataList.isEmpty()) {
                return deviceInfoFactoryDataList.get(0);
            }
        } else {
            log.error("Factory data not found for Imei : {}", imei);
        }
        return null;
    }

    /**
     * Finds the ID of a device by its factory IMEI.
     *
     * @param imei the factory IMEI of the device
     * @return the ID of the device, or null if not found
     */
    public Long findIdByFactoryImei(String imei) {
        Map<String, Object> orderedMap = new LinkedHashMap<>();
        orderedMap.put(IMEI, imei);
        String sql = SqlUtility.getPreparedSql(SELECT_ID_FROM_DEVICE_INFO_FACTORY_DATA_WHERE, AND, orderedMap);
        Object[] values = SqlUtility.getArrayValues(orderedMap);
        Long factoryId = null;
        if (sql != null) {
            factoryId = jdbcTemplate.queryForObject(sql, values,
                Long.class);

        }
        return factoryId;
    }

    /**
     * Finds the device information factory data by factory ID and IMEI.
     *
     * @param factoryId the factory ID
     * @param imei the IMEI
     * @return the device information factory data, or null if not found
     */
    public DeviceInfoFactoryData findByFactoryIdAndImei(long factoryId, String imei) {
        Map<String, Object> orderedMap = new LinkedHashMap<>();
        orderedMap.put(IMEI, imei);
        orderedMap.put(ID, factoryId);

        String sql = SqlUtility.getPreparedSql(SELECT_FROM_DEVICE_INFO_FACTORY_DATA_WHERE, AND, orderedMap);

        Object[] values = SqlUtility.getArrayValues(orderedMap);
        if (sql != null) {
            List<DeviceInfoFactoryData> deviceInfoFactoryDataList = jdbcTemplate.query(sql, values,
                new DeviceInfoFactoryDataMapper());

            if (!deviceInfoFactoryDataList.isEmpty()) {
                return deviceInfoFactoryDataList.get(0);
            }
        } else {
            log.error("Factory data not found for Imei : {} and factory Id :{}", imei, factoryId);
        }
        return null;
    }

    /**
     * Finds the factory data for a device based on the given serial number.
     *
     * @param serialNumber The serial number of the device.
     * @return The DeviceInfoFactoryData object representing the factory data for the device, or null if not found.
     */
    public DeviceInfoFactoryData findFactoryDataBySerialNumber(String serialNumber) {
        Map<String, Object> orderedMap = new LinkedHashMap<>();
        orderedMap.put(SERIAL_NUMBER, serialNumber);

        String sql = SqlUtility.getPreparedSql(SELECT_FROM_DEVICE_INFO_FACTORY_DATA_WHERE, AND, orderedMap);

        Object[] values = SqlUtility.getArrayValues(orderedMap);
        if (sql != null) {
            List<DeviceInfoFactoryData> deviceInfoFactoryDataList = jdbcTemplate.query(sql, values,
                new DeviceInfoFactoryDataMapper());

            if (!deviceInfoFactoryDataList.isEmpty()) {
                return deviceInfoFactoryDataList.get(0);
            }
        } else {
            log.error("Factory data not found for serialNumber : {}", serialNumber);
        }
        return null;
    }

    /**
     * Constructs and fetches factory data based on the provided DeviceInfoFactoryData object.
     *
     * @param factoryData The DeviceInfoFactoryData object containing the data to construct and fetch.
     * @return A list of DeviceInfoFactoryData objects matching the provided criteria.
     */
    public List<DeviceInfoFactoryData> constructAndFetchFactoryData(DeviceInfoFactoryData factoryData) {
        MapSqlParameterSource mapSqlParameter = new MapSqlParameterSource();
        StringBuilder queryCreator = new StringBuilder(SELECT_FROM_DEVICE_INFO_FACTORY_DATA);
        queryCreator.append(WHERE);
        boolean isAdded = false;
        if (!StringUtils.isEmpty(factoryData.getImei())) {
            queryCreator.append("imei = :imei ");
            mapSqlParameter.addValue("imei", factoryData.getImei());
            isAdded = true;
        }
        if (!StringUtils.isEmpty(factoryData.getImsi())) {
            appendAnd(isAdded, queryCreator);
            queryCreator.append("imsi = :imsi ");
            mapSqlParameter.addValue("imsi", factoryData.getImsi());
            isAdded = true;
        }
        if (!StringUtils.isEmpty(factoryData.getMsisdn())) {
            appendAnd(isAdded, queryCreator);
            queryCreator.append("msisdn = :msisdn ");
            mapSqlParameter.addValue("msisdn", factoryData.getMsisdn());
            isAdded = true;
        }
        if (!StringUtils.isEmpty(factoryData.getSsid())) {
            appendAnd(isAdded, queryCreator);
            queryCreator.append("ssid = :ssid ");
            mapSqlParameter.addValue("ssid", factoryData.getSsid());
            isAdded = true;
        }
        if (!StringUtils.isEmpty(factoryData.getIccid())) {
            appendAnd(isAdded, queryCreator);
            queryCreator.append("iccid = :iccid ");
            mapSqlParameter.addValue("iccid", factoryData.getIccid());
            isAdded = true;
        }
        if (!StringUtils.isEmpty(factoryData.getBssid())) {
            appendAnd(isAdded, queryCreator);
            queryCreator.append("bssid = :bssid ");
            mapSqlParameter.addValue("bssid", factoryData.getBssid());
            isAdded = true;
        }
        if (!StringUtils.isEmpty(factoryData.getSerialNumber())) {
            appendAnd(isAdded, queryCreator);
            queryCreator.append("serial_number = :serialNumber ");
            mapSqlParameter.addValue("serialNumber", factoryData.getSerialNumber());
        }
        log.info("Constructed query to fire : {} ", queryCreator);
        log.info("DeviceData from constructFetchData : {} ", factoryData);
        return namedParamJdbcTemplate.query(queryCreator.toString(), mapSqlParameter,
            new DeviceInfoFactoryDataMapper());

    }

    /**
     * Fetches the DeviceInfoFactoryData based on the provided ordered map of attributes and values.
     *
     * @param orderedMap a map containing the attributes and values used to filter the DeviceInfoFactoryData
     * @return the DeviceInfoFactoryData object that matches the provided attributes and values, or null if not found
     */
    public DeviceInfoFactoryData fetchDeviceInfoFactoryData(Map<String, Object> orderedMap) {
        String sql = SqlUtility.getPreparedSql(SELECT_FROM_DEVICE_INFO_FACTORY_DATA_WHERE, AND, orderedMap);

        Object[] values = SqlUtility.getArrayValues(orderedMap);
        log.info("PreparedSql : {}", sql);
        if (values != null) {
            log.info("Values Size: {}", values.length);
        }
        if (sql != null) {
            List<DeviceInfoFactoryData> deviceInfoFactoryDataList = jdbcTemplate.query(sql, values,
                new DeviceInfoFactoryDataMapper());

            if (!CollectionUtils.isEmpty(deviceInfoFactoryDataList)) {
                log.info("device is found with above condition: {}", deviceInfoFactoryDataList.get(0));
                return deviceInfoFactoryDataList.get(0);
            }
        } else {
            log.error("Factory data not found for attribute:value : {}", orderedMap);
        }
        return null;
    }

    /**
     * Updates the records in the "DeviceInfoFactoryData" table based on the provided conditional and ordered maps.
     *
     * @param conditionalOrderedMap A map containing the conditional values to be used in the WHERE clause of the SQL
     *                              statement.
     * @param orderedMap            A map containing the new values to be updated in the table.
     * @return The number of rows affected by the update operation.
     */
    public int update(Map<String, Object> conditionalOrderedMap, Map<String, Object> orderedMap) {

        String sql = "update public.\"DeviceInfoFactoryData\" set ";
        String operator = " , ";
        sql = SqlUtility.getPreparedSql(sql, operator, orderedMap);
        sql = sql + " where ";
        operator = AND;
        sql = SqlUtility.getPreparedSql(sql, operator, conditionalOrderedMap);
        log.info("update->FinalPreparedSql: {} ", sql);
        for (Map.Entry<String, Object> entry : conditionalOrderedMap.entrySet()) {
            orderedMap.put(entry.getKey(), entry.getValue());
        }

        Object[] values = SqlUtility.getArrayValues(orderedMap);
        if (values != null) {
            log.info("update->FinalValuesSize: {}", values.length);
        }
        int row = jdbcTemplate.update(sql, values);
        log.info("rownumber updated: {}", row);
        return row;
    }

    /**
     * Constructs and fetches factory data for devices based on the specified parameters.
     *
     * @param size          The number of records to fetch per page.
     * @param page          The page number of the records to fetch.
     * @param asc           The ascending order field for sorting the records.
     * @param desc          The descending order field for sorting the records.
     * @param serialNumber  The serial number of the device to filter the records.
     * @param imei          The IMEI number of the device to filter the records.
     * @return              A list of {@link DeviceInfoFactoryData} objects containing the fetched factory data.
     */
    public List<DeviceInfoFactoryData> constructFetchFactoryData(int size, int page, String asc, String desc,
                                                                 String serialNumber, String imei) {
        log.info("Inside constructFetchFactoryData method");

        StringBuilder queryBuilder = new StringBuilder(SELECT_FROM_DEVICE_INFO_FACTORY_DATA);

        queryBuilder.append(constructImeiSerialNumberFilter(serialNumber, imei));
        queryBuilder.append(constructOrderByFilter(asc, desc));
        queryBuilder.append(constructPageFilter());

        MapSqlParameterSource parameters = constructParameterSource(size, page);
        DeviceInfoFactoryDataMapper dataMapper = getDeviceInfoFactoryDataMapper();

        log.info("Query generated: {}", queryBuilder);

        List<DeviceInfoFactoryData> deviceInfo =
            namedParamJdbcTemplate.query(queryBuilder.toString(), parameters, dataMapper);

        log.info("Exit constructAndFetchFactoryData method");

        return deviceInfo;
    }

    /**
     * Constructs and executes a query to fetch factory data for devices based on the specified parameters.
     *
     * @param type              The input type for searching (IMEI, SERIAL_NUMBER, DEVICE_ID, VIN, STATE).
     * @param searchKey         The search key to filter the results.
     * @param sizeValue         The number of results to fetch per page.
     * @param pageValue         The page number of the results to fetch.
     * @param sortby            The field to sort the results by.
     * @param orderBy           The order in which to sort the results (ASC or DESC).
     * @param deviceVinEnabled  Indicates whether to include VIN details in the query.
     * @return A list of DeviceInfoFactoryDataWithSubscription objects containing the fetched factory data.
     */
    public List<DeviceInfoFactoryDataWithSubscription> constructFetchFactoryData(DeviceDetailsInputTypeEnum type,
                                                                                 String searchKey,
                                                                                 int sizeValue, int pageValue,
                                                                                 String sortby, String orderBy,
                                                                                 boolean deviceVinEnabled) {

        String filterQuery = "";
        StringBuilder queryCreator = new StringBuilder(SELECT_FROM_DEVICE_INFO_FACTORY_DATA);
        switch (type) {
            case IMEI:
                filterQuery = constructImeiFilter(searchKey);
                break;
            case SERIAL_NUMBER:
                filterQuery = constructSerialNumberFilter(searchKey);
                break;
            case DEVICE_ID:
                queryCreator = new StringBuilder(SELECT_FROM_DEVICE_INFO_FACTORY_DATA_AND_ASSOCIATION);
                filterQuery = constructDeviceIdFilter(searchKey);
                break;
            case VIN:
                queryCreator = new StringBuilder(SELECT_FROM_DEVICE_INFO_FACTORY_DATA);
                filterQuery = constructVinFilter(searchKey);
                break;
            case STATE:
                filterQuery = constructStateFilter(searchKey);
                break;
            default:
                break;
        }

        if (deviceVinEnabled) {
            queryCreator.append(VIN_DETAILS_JOIN_CONDITION);
        }

        if (!filterQuery.isEmpty()) {
            queryCreator.append(WHERE + filterQuery);
        }

        if (type.equals(DeviceDetailsInputTypeEnum.DEVICE_ID) || type.equals(DeviceDetailsInputTypeEnum.VIN)) {
            queryCreator.append(SqlUtility.prepareSortByAndOrderByQuery(sortby, orderBy, "DeviceInfoFactoryData"));
        } else {
            queryCreator.append(SqlUtility.prepareSortByAndOrderByQuery(sortby, orderBy));
        }
        queryCreator.append(constructPageFilter());

        MapSqlParameterSource parameters = constructParameterSource(sizeValue, pageValue);
        DeviceInfoFactoryDataMapperWithSubscription dataMapper = getDeviceInfoFactoryDataWithSubscriptionMapper();

        return namedParamJdbcTemplate.query(queryCreator.toString(), parameters, dataMapper);
    }

    /**
     * Constructs and fetches the aggregate device state count based on the provided parameters.
     *
     * @param containsLikeFieldList  The list of fields to match using the "LIKE" operator.
     * @param containsLikeValueList  The list of values to match using the "LIKE" operator.
     * @param rangeFieldList         The list of fields to match using the range operator.
     * @param rangeValueList         The list of values to match using the range operator.
     * @return The aggregated device state count.
     */
    public DeviceInfoAggregateFactoryData.StateCount constructFetchAgrigateDeviceState(
        List<String> containsLikeFieldList,
        List<String> containsLikeValueList,
        List<String> rangeFieldList, List<String> rangeValueList) {
        log.debug("Inside constructFetchAgrigateFactoryData method");

        StringBuilder queryCreator = new StringBuilder(GET_STATE_AND_STATE_COUNT_FROM_DIFD);
        String likeQuery = SqlUtility.prepareLikeQuery(containsLikeFieldList, containsLikeValueList);
        String rangeQuery = SqlUtility.prepareRangeQuery(rangeFieldList, rangeValueList);

        if (!likeQuery.isEmpty() && !rangeQuery.isEmpty()) {
            queryCreator.append(WHERE + likeQuery + AND + rangeQuery);
        } else if ((likeQuery.isEmpty() && !rangeQuery.isEmpty()) || (!likeQuery.isEmpty() && rangeQuery.isEmpty())) {
            queryCreator.append(WHERE + likeQuery + rangeQuery);
        }

        queryCreator.append(GROUP_BY_STATE);
        log.debug("V3constructFetchAgrigateDeviceState->query:{}", queryCreator.toString());
        List<DeviceStateAggregateData> deviceStateMap = namedParamJdbcTemplate.query(queryCreator.toString(),
            Collections.emptyMap(), new DeviceStateAggregateDataMapper());
        DeviceInfoAggregateFactoryData.StateCount stateCount = new DeviceInfoAggregateFactoryData.StateCount();
        DeviceState.convertStateCount(stateCount, deviceStateMap);
        log.debug("Exiting V3 constructFetchAgrigateDeviceState");
        return stateCount;
    }

    /**
     * Constructs and fetches the aggregate device state count based on the provided serial number and IMEI.
     *
     * @param serialNumber The serial number of the device.
     * @param imei The IMEI number of the device.
     * @return The aggregate device state count.
     */
    public DeviceInfoAggregateFactoryData.StateCount constructFetchAgrigateDeviceState(String serialNumber,
                                                                                       String imei) {
        log.info("Inside constructFetchAgrigateFactoryData method");

        StringBuilder queryCreator = new StringBuilder(GET_STATE_AND_STATE_COUNT_FROM_DIFD);
        queryCreator.append(constructImeiSerialNumberFilter(serialNumber, imei));
        queryCreator.append(GROUP_BY_STATE);
        List<DeviceStateAggregateData> deviceStateMap = namedParamJdbcTemplate.query(queryCreator.toString(),
            Collections.emptyMap(), new DeviceStateAggregateDataMapper());
        DeviceInfoAggregateFactoryData.StateCount stateCount = new DeviceInfoAggregateFactoryData.StateCount();
        DeviceState.convertStateCount(stateCount, deviceStateMap);
        return stateCount;
    }

    /**
     * Constructs and executes a query to fetch the total count of factory data for a device
     * based on the provided serial number and IMEI.
     *
     * @param serialNumber The serial number of the device.
     * @param imei The IMEI (International Mobile Equipment Identity) of the device.
     * @return The total count of factory data for the device.
     */
    public Long constructFetchTotalFactoryData(String serialNumber, String imei) {
        log.info("Inside constructFetchTotalFactoryData method");
        StringBuilder queryCreator = new StringBuilder(GET_COUNT_FROM_DIFD);
        queryCreator.append(constructImeiSerialNumberFilter(serialNumber, imei));
        log.info("Query generated :: {}", queryCreator);
        Long deviceCount = namedParamJdbcTemplate.queryForObject(queryCreator.toString(),
            new MapSqlParameterSource(), Long.class);
        log.info("Exit constructFetchTotalFactoryData method");
        return deviceCount;
    }

    /**
     * Constructs and executes a query to fetch the total count of factory data for devices.
     *
     * @param containsLikeFieldList A list of fields to be used in the 'LIKE' query.
     * @param containsLikeValueList A list of values to be used in the 'LIKE' query.
     * @param rangeFieldList A list of fields to be used in the range query.
     * @param rangeValueList A list of values to be used in the range query.
     * @return The total count of factory data for devices.
     */
    public Long constructFetchTotalFactoryData(List<String> containsLikeFieldList, List<String> containsLikeValueList,
                                               List<String> rangeFieldList, List<String> rangeValueList) {
        log.debug("Inside constructFetchTotalFactoryData method");

        StringBuilder queryCreator = new StringBuilder(GET_COUNT_FROM_DIFD);
        String likeQuery = SqlUtility.prepareLikeQuery(containsLikeFieldList, containsLikeValueList);
        String rangeQuery = SqlUtility.prepareRangeQuery(rangeFieldList, rangeValueList);
        if (!likeQuery.isEmpty() && !rangeQuery.isEmpty()) {
            queryCreator.append(WHERE + likeQuery + AND + rangeQuery);
        } else if ((likeQuery.isEmpty() && !rangeQuery.isEmpty()) || (!likeQuery.isEmpty() && rangeQuery.isEmpty())) {
            queryCreator.append(WHERE + rangeQuery + likeQuery);
        }

        log.debug("constructFetchTotalFactoryDataV3 -> finalquery :: {}", queryCreator.toString());
        Long deviceCount = namedParamJdbcTemplate.queryForObject(queryCreator.toString(),
            new MapSqlParameterSource(), Long.class);
        log.debug("Exit constructFetchTotalFactoryDataV3 method");
        return deviceCount;
    }

    /**
     * Constructs a filter query based on the provided serial number and IMEI.
     *
     * @param serialNumber The serial number to filter by.
     * @param imei The IMEI to filter by.
     * @return A string representing the constructed filter query.
     */
    private String constructImeiSerialNumberFilter(String serialNumber, String imei) {
        StringBuilder stringQueryFilter = new StringBuilder();
        if (!StringUtils.isEmpty(imei)) {
            stringQueryFilter = new StringBuilder("WHERE ");
            stringQueryFilter.append("imei LIKE '%");
            stringQueryFilter.append(imei.replace("'", "''").replace("_", "__"));
            stringQueryFilter.append("%' ");
        }
        if (!StringUtils.isEmpty(serialNumber)) {
            if (!StringUtils.isEmpty(imei)) {
                stringQueryFilter.append("and ");
            } else {
                stringQueryFilter = new StringBuilder("WHERE ");
            }
            stringQueryFilter.append("serial_number LIKE '%");
            stringQueryFilter.append(serialNumber.replace("'", "''").replace("_", "__"));
            stringQueryFilter.append("%' ");
        }
        return stringQueryFilter.toString();
    }

    /**
     * Constructs the ORDER BY filter based on the given ascending and descending values.
     *
     * @param asc  The ascending value to be used in the ORDER BY filter.
     * @param desc The descending value to be used in the ORDER BY filter.
     * @return The constructed ORDER BY filter as a string.
     */
    private String constructOrderByFilter(String asc, String desc) {
        StringBuilder queryOrderBy = new StringBuilder();

        if (StringUtils.isNotEmpty(asc)) {
            queryOrderBy.append(" ORDER BY \"");
            queryOrderBy.append(asc);
            queryOrderBy.append("\" COLLATE \"C\" ASC ");
        } else if (StringUtils.isNotEmpty(desc)) {
            queryOrderBy.append(" ORDER BY \"");
            queryOrderBy.append(desc);
            queryOrderBy.append("\" COLLATE \"C\" DESC ");
        } else {
            queryOrderBy.append(" ORDER BY \"ID\" ASC ");
        }

        return queryOrderBy.toString();
    }

    /**
     * Constructs a page filter for pagination.
     *
     * @return The constructed page filter.
     */
    private String constructPageFilter() {
        return PAGE_FILTER;
    }

    /**
     * Constructs a MapSqlParameterSource object with the specified size and page parameters.
     *
     * @param size The number of items to retrieve per page.
     * @param page The page number.
     * @return A MapSqlParameterSource object containing the constructed parameters.
     */
    private MapSqlParameterSource constructParameterSource(int size, int page) {
        page = page - 1;
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        parameters.addValue("limit", size);
        parameters.addValue("offset", page * size);

        return parameters;
    }

    /**
     * Constructs and executes a query to fetch the total factory data count for device details based on the specified
     * input type and value.
     *
     * @param inputType      the input type for filtering the device details (IMEI, SERIAL_NUMBER, DEVICE_ID, VIN,
     *                       STATE)
     * @param inputTypeValue the value corresponding to the input type for filtering the device details
     * @return the total count of factory data for the device details
     */
    public Long constructFetchTotalFactoryDataForDeviceDetails(DeviceDetailsInputTypeEnum inputType,
                                                               String inputTypeValue) {

        StringBuilder queryCreator;
        String filterQuery = "";
        queryCreator = new StringBuilder(GET_COUNT_FROM_DIFD);
        switch (inputType) {
            case IMEI:
                filterQuery = constructImeiFilter(inputTypeValue);
                break;
            case SERIAL_NUMBER:
                filterQuery = constructSerialNumberFilter(inputTypeValue);
                break;
            case DEVICE_ID:
                queryCreator = new StringBuilder("select count(*) from public.\"device_association\" ");
                filterQuery = constructDeviceIdFilter(inputTypeValue);
                break;
            case VIN:
                queryCreator = new StringBuilder("select count(*) from public.\"vin_details\" ");
                filterQuery = constructVinFilter(inputTypeValue);
                break;
            case STATE:
                filterQuery = constructStateFilter(inputTypeValue);
                break;
            default:
                break;
        }

        if (!filterQuery.isEmpty()) {
            queryCreator.append(WHERE + filterQuery);
        }

        return (namedParamJdbcTemplate.queryForObject(queryCreator.toString(), new MapSqlParameterSource(),
            Long.class));

    }

    /**
     * Constructs and fetches the aggregate factory data for the device information.
     *
     * @param type      The type of device details input.
     * @param searchKey The search key for filtering the device information.
     * @return The state count of the device information aggregate factory data.
     */
    public DeviceInfoAggregateFactoryData.StateCount constructFetchAggregrateFactoryData(
        DeviceDetailsInputTypeEnum type,
        String searchKey) {

        StringBuilder queryCreator =
            new StringBuilder(GET_STATE_AND_STATE_COUNT_FROM_DIFD);
        String filterQuery = "";
        switch (type) {
            case IMEI:
                filterQuery = constructImeiFilter(searchKey);
                break;
            case SERIAL_NUMBER:
                filterQuery = constructSerialNumberFilter(searchKey);
                break;
            case DEVICE_ID:
                queryCreator = new StringBuilder(
                    "select state, count(state) as count from \"DeviceInfoFactoryData\", \"device_association\"");
                queryCreator.append(JOIN_CONDITION);
                filterQuery = constructDeviceIdFilter(searchKey);
                break;
            case VIN:
                queryCreator = new StringBuilder(
                    "select state, count(state) as count from public.\"DeviceInfoFactoryData\" df left outer join " 
            +                        "public.\"vin_details\" vd on df.\"ID\"=vd.\"reference_id\"");
                filterQuery = constructVinFilter(searchKey);
                break;
            case STATE:
                filterQuery = constructStateFilter(searchKey);
                break;
            default:
                break;
        }

        if (!filterQuery.isEmpty()) {
            if (type.equals(DeviceDetailsInputTypeEnum.DEVICE_ID)) {
                queryCreator.append(" AND " + filterQuery);
            } else {
                queryCreator.append(WHERE + filterQuery);
            }
        }

        queryCreator.append(GROUP_BY_STATE);
        log.debug("constructFetchAggregrateFactoryData: Query generated :: {}", queryCreator);
        List<DeviceStateAggregateData> deviceStateMap = namedParamJdbcTemplate.query(queryCreator.toString(),
            Collections.emptyMap(), new DeviceStateAggregateDataMapper());
        DeviceInfoAggregateFactoryData.StateCount stateCount = new DeviceInfoAggregateFactoryData.StateCount();
        DeviceState.convertStateCount(stateCount, deviceStateMap);
        log.debug("Exit constructFetchAggregrateFactoryData method");
        return stateCount;
    }

    /**
     * Constructs a filter query for searching device information by IMEI.
     *
     * @param imei The IMEI to filter by.
     * @return A string representing the filter query.
     */
    private String constructImeiFilter(String imei) {
        StringBuilder stringQueryFilter = new StringBuilder();
        if (!StringUtils.isEmpty(imei)) {
            stringQueryFilter = new StringBuilder(" imei LIKE '%");
            stringQueryFilter.append(imei.replace("'", "''").replace("_", "__"));
            stringQueryFilter.append("%' ");
        }

        return stringQueryFilter.toString();
    }

    /**
     * Constructs a filter string based on the provided serial number.
     *
     * @param serialNumber The serial number to be used for filtering.
     * @return The constructed filter string.
     */
    private String constructSerialNumberFilter(String serialNumber) {
        StringBuilder stringQueryFilter = new StringBuilder();
        if (!StringUtils.isEmpty(serialNumber)) {
            stringQueryFilter = new StringBuilder(" serial_number LIKE '%");
            stringQueryFilter.append(serialNumber.replace("'", "''").replace("_", "__"));
            stringQueryFilter.append("%' ");
        }

        return stringQueryFilter.toString();
    }

    /**
     * Constructs a filter string based on the provided device ID.
     *
     * @param deviceId The device ID to filter by.
     * @return A filter string that can be used in a database query.
     */
    private String constructDeviceIdFilter(String deviceId) {
        StringBuilder stringQueryFilter = new StringBuilder();
        if (!StringUtils.isEmpty(deviceId)) {
            stringQueryFilter = new StringBuilder(" harman_id LIKE '%");
            stringQueryFilter.append(deviceId.replace("'", "''").replace("_", "__"));
            stringQueryFilter.append("%' ");
        }

        return stringQueryFilter.toString();
    }

    /**
     * Constructs a filter string based on the provided VIN.
     *
     * @param vin The VIN to be used for constructing the filter.
     * @return The filter string based on the provided VIN.
     */
    private String constructVinFilter(String vin) {
        StringBuilder stringQueryFilter = new StringBuilder();
        if (!StringUtils.isEmpty(vin)) {
            stringQueryFilter = new StringBuilder(" vin LIKE '%");
            stringQueryFilter.append(vin.replace("'", "''").replace("_", "__"));
            stringQueryFilter.append("%' ");
        }

        return stringQueryFilter.toString();
    }

    /**
     * Constructs a filter string based on the provided state.
     *
     * @param state The state to filter by.
     * @return The constructed filter string.
     */
    private String constructStateFilter(String state) {
        StringBuilder stringQueryFilter = new StringBuilder();
        if (!StringUtils.isEmpty(state)) {
            stringQueryFilter = new StringBuilder(" state = '");
            stringQueryFilter.append(state.replace("'", "''"));
            stringQueryFilter.append("'");
        }

        return stringQueryFilter.toString();
    }

    /**
     * Finds the total device state for a given IMEI.
     *
     * @param imei The IMEI of the device.
     * @return The total device state count.
     */
    public long findTotalDeviceState(String imei) {

        log.debug("Inside findTotalDeviceState method");
        String query = "select count(*) from public.\"DeviceInfoFactoryDataHistory\" where \"imei\"='" + imei + "';";
        log.debug("findTotalDeviceState -> finalquery :: {}", query);
        Long count = namedParamJdbcTemplate.queryForObject(query,
            new MapSqlParameterSource(), Long.class);
        long deviceCount = count != null ? count : 0;
        log.debug("Exit findTotalDeviceState method");
        return deviceCount;
    }

    /**
     * Constructs and fetches the device states based on the provided parameters.
     *
     * @param size         The number of device states to fetch.
     * @param page         The page number of the device states to fetch.
     * @param sortingOrder The sorting order of the device states.
     * @param sortBy       The field to sort the device states by.
     * @param imei         The IMEI number of the device.
     * @return A list of DeviceStateHistory objects representing the device states.
     */
    public List<DeviceStateHistory> constructAndFetchDeviceStates(int size, int page, String sortingOrder,
                                                                  String sortBy, String imei) {

        log.debug("Inside constructAndFetchTimelineDetails method");
        String query = "select * from public.\"DeviceInfoFactoryDataHistory\" where \"imei\"='" + imei + "'";
        StringBuilder queryBuilder = new StringBuilder(query);
        queryBuilder.append(SqlUtility.prepareSortByAndOrderByQuery(sortBy, sortingOrder));
        queryBuilder.append(constructPageFilter());
        MapSqlParameterSource parameters = constructParameterSource(size, page);
        log.debug("SQL generated {}", queryBuilder.toString());
        DeviceStateHistoryDataMapper dataMapper = getDeviceSatetHistoryDataMapper();
        List<DeviceStateHistory> deviceHistory =
            namedParamJdbcTemplate.query(queryBuilder.toString(), parameters, dataMapper);
        log.debug("Exit constructAndFetchDeviceStates method");
        return deviceHistory;
    }

    /**
     * Retrieves the model associated with the given IMEI.
     *
     * @param imei The IMEI (International Mobile Equipment Identity) number of the device.
     * @return The model associated with the given IMEI.
     */
    public String getModelByImei(String imei) {
        return jdbcTemplate.queryForObject(GET_MODEL_BY_IMEI, new Object[]{imei}, String.class);
    }

    /**
     * Finds the VIN (Vehicle Identification Number) either by IMEI (International Mobile Equipment Identity) or
     * serial number.
     *
     * @param imei          The IMEI of the device.
     * @param serialNumber  The serial number of the device.
     * @return              The VIN associated with the given IMEI or serial number, or null if not found.
     * @throws IllegalArgumentException if both imei and serial number are empty.
     */
    public String findVinEitherByImeiOrSerialNumber(String imei, String serialNumber) {
        String vin = null;
        try {
            log.debug("## findVinEitherByImeiOrSerialNumber - START imei: {}, serialNumber: {}", imei, serialNumber);
            if (StringUtils.isEmpty(serialNumber) && StringUtils.isEmpty(imei)) {
                throw new IllegalArgumentException("Either imei or serial number is mandatory");
            }
            List<String> strLst;
            if (StringUtils.isNotEmpty(serialNumber)) {
                log.debug("# VIN_BY_SERIAL_NUMBER: {}", VIN_BY_SERIAL_NUMBER);
                strLst = jdbcTemplate.query(VIN_BY_SERIAL_NUMBER, new Object[]{serialNumber}, new RowMapper<String>() {
                    @Override
                    public String mapRow(ResultSet resultSet, int i) throws SQLException {
                        return resultSet.getString(1);
                    }
                });
            } else if (StringUtils.isNotEmpty(imei)) {
                log.debug("# VIN_BY_IMEI: {}", VIN_BY_IMEI);
                strLst = jdbcTemplate.query(VIN_BY_IMEI, new Object[]{imei}, new RowMapper<String>() {
                    @Override
                    public String mapRow(ResultSet resultSet, int i) throws SQLException {
                        return resultSet.getString(1);
                    }
                });
            } else {
                throw new IllegalArgumentException("Either imei or serial number is mandatory");
            }
            log.debug("##deviceFactory list size: {}", strLst.size());
            vin = strLst.size() == 1 ? strLst.get(0) : null;
        } catch (DataAccessException e) {
            log.error(
                "## Exception occurred while trying to find the vin, could be that no record found for given input: ",
                e);
        }
        log.debug("## findVinEitherByImeiOrSerialNumber - END vin: {}", vin);
        return vin;
    }

    /**
     * Inserts the given device information factory data into the database.
     *
     * @param factoryData The device information factory data to be inserted.
     * @param userId The user ID associated with the factory data.
     * @param manufacturingDate The manufacturing date of the device.
     * @param recordDate The record date of the device.
     */
    public void insertIntoDeviceInfoFactoryData(final DeviceInfoFactoryData factoryData, String userId,
                                                String manufacturingDate, String recordDate) {
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {

                log.info("Factory Data : " + factoryData);

                String insertStatement =

                    "insert into public.\"DeviceInfoFactoryData\" (\"manufacturing_date\",\"model\",\"imei\"," 
                    +    "\"serial_number\",\"platform_version\",\"iccid\",\"ssid\",\"bssid\",\"msisdn\",\"imsi\","
                    +    "\"record_date\",\"factory_admin\",\"created_date\",\"state\",\"package_serial_number\") "
                    +              "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                PreparedStatement ps = con.prepareStatement(insertStatement);
                Timestamp manufacturedTime;
                Timestamp recordDateTime;
                try {
                    manufacturedTime = new Timestamp(dateFormatter.parse(manufacturingDate).getTime());
                    recordDateTime = new Timestamp(dateFormatter.parse(recordDate).getTime());
                } catch (ParseException e) {
                    log.info("Manufacturing Date : " + manufacturingDate);
                    log.info("Record Date : " + recordDate);
                    throw new SQLException("Invalid date format passed. Valid format is yyyy/MM/dd");
                }

                ps.setTimestamp(1, manufacturedTime);
                ps.setString(INDEX_2, factoryData.getModel());
                ps.setString(INDEX_3, factoryData.getImei());
                ps.setString(INDEX_4, factoryData.getSerialNumber());
                ps.setString(INDEX_5, factoryData.getPlatformVersion());
                ps.setString(INDEX_6, factoryData.getIccid());
                ps.setString(INDEX_7, factoryData.getSsid());
                ps.setString(INDEX_8, factoryData.getBssid());
                ps.setString(INDEX_9, factoryData.getMsisdn());
                ps.setString(INDEX_10, factoryData.getImsi());
                ps.setTimestamp(INDEX_11, recordDateTime);

                ps.setString(INDEX_12, factoryData.getFactoryAdmin());
                ps.setTimestamp(INDEX_13, factoryData.getCreatedDate());
                ps.setString(INDEX_14, DeviceState.PROVISIONED.toString());
                ps.setString(INDEX_15, factoryData.getPackageSerialNumber());
                log.info("Prepared Statement : " + ps.toString());
                return ps;

            }
        });
    }

    /**
     * Inserts the given device factory data into the database.
     *
     * @param factoryData The device factory data to be inserted.
     * @param userId The ID of the user performing the insertion.
     */
    @Transactional
    public void insertData(final DeviceInfoFactoryData factoryData, final String userId) {

        KeyHolder keyHolder = new GeneratedKeyHolder();
        log.info("Inserting data into DeviceInfoFactory");
        jdbcTemplate.update(new PreparedStatementCreator() {

            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                    "insert into public.\"DeviceInfoFactoryData\" (\"manufacturing_date\",\"model\",\"imei\"," 
                    +   "\"serial_number\",\"platform_version\",\"iccid\",\"ssid\",\"bssid\",\"msisdn\",\"imsi\","
                    +   "\"record_date\",\"factory_admin\",\"created_date\",\"state\",\"package_serial_number\") "
                    +             "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
                updatePreparedStatement(userId, ps, factoryData);

                return ps;
            }
        }, keyHolder);

        log.info("Successfully inserted data into DeviceInfoFactory table");
        DeviceInfoFactoryData factoryDataFromTable = null;
        factoryDataFromTable = findFactoryDataBySerialNumber(factoryData.getSerialNumber());

        createHistoryTableEntry(factoryDataFromTable, DeviceState.PROVISIONED.toString());

        log.info("Successfully inserted Data into DeviceInfoFactoryHistory table");

    }

    /**
     * Updates the prepared statement with the provided factory data and user ID.
     *
     * @param userId      The ID of the user performing the update.
     * @param ps          The prepared statement to update.
     * @param factoryData The factory data to set in the prepared statement.
     * @throws SQLException If an error occurs while setting the values in the prepared statement.
     */
    private void updatePreparedStatement(final String userId, PreparedStatement ps,
                                         DeviceInfoFactoryData factoryData) throws SQLException {
        final Timestamp createdTimestamp = new Timestamp(new Date().getTime());

        ps.setTimestamp(1, factoryData.getManufacturingDate());
        ps.setString(INDEX_2, factoryData.getModel());
        ps.setString(INDEX_3, factoryData.getImei());
        ps.setString(INDEX_4, factoryData.getSerialNumber());
        ps.setString(INDEX_5, factoryData.getPlatformVersion());
        ps.setString(INDEX_6, factoryData.getIccid());
        ps.setString(INDEX_7, factoryData.getSsid());
        ps.setString(INDEX_8, factoryData.getBssid());
        ps.setString(INDEX_9, factoryData.getMsisdn());
        ps.setString(INDEX_10, factoryData.getImsi());
        ps.setTimestamp(INDEX_11, factoryData.getRecordDate());
        ps.setString(INDEX_12, userId);
        ps.setTimestamp(INDEX_13, createdTimestamp);
        ps.setString(INDEX_14, DeviceState.PROVISIONED.toString());
        ps.setString(INDEX_15, factoryData.getPackageSerialNumber());
    }


    /**
     * Enum representing the different types of device details input.
     * The available input types are:
     * - IMEI
     * - SERIAL_NUMBER
     * - DEVICE_ID
     * - VIN
     * - STATE
     */
    public enum DeviceDetailsInputTypeEnum {
        IMEI, SERIAL_NUMBER, DEVICE_ID, VIN, STATE;
    }


}
