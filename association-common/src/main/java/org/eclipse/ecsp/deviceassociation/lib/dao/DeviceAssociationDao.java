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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import org.eclipse.ecsp.exception.shared.ApiTechnicalException;
import org.eclipse.ecsp.services.shared.deviceinfo.model.DeviceInfo;
import org.eclipse.ecsp.services.shared.tables.Postgres;
import org.eclipse.ecsp.services.shared.util.SqlUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.eclipse.ecsp.common.CommonConstants.ID;
import static org.eclipse.ecsp.common.CommonConstants.SELECT_FROM_DEVICE_INFO_FACTORY_DATA;

/**
 * The DeviceAssociationDao class is responsible for handling database operations related to device associations.
 * It provides methods for inserting, updating, and retrieving device association records.
 */
@Configurable
@Repository
@Slf4j
public class DeviceAssociationDao {

    public static final String USER_ID_FIELD = "da.user_id";
    private static final String UNPREDICTABLE_USER_ID = "!@#?<$%>^&*";
    public static final int INDEX_2 = 2;
    public static final int INDEX_3 = 3;
    public static final int INDEX_4 = 4;
    public static final int INDEX_5 = 5;
    public static final int INDEX_6 = 6;
    public static final int INDEX_7 = 7;
    public static final int INDEX_8 = 8;
    public static final int INDEX_9 = 9;
    public static final int INDEX_10 = 10;
    public static final int INDEX_11 = 11;
    public static final int INDEX_12 = 12;
    public static final int INDEX_13 = 13;
    public static final int INDEX_14 = 14;
    public static final int RETURN_VALUE = -1;
    private static final String INSERT_QUERY_IN_DEVICE_ASSOCIATION =
            "insert into device_association(serial_number,user_id,harman_id,association_status,associated_on,";
    private static final String INSERT_DEVICE_ASSOCIATION = INSERT_QUERY_IN_DEVICE_ASSOCIATION
            + "associated_by,disassociated_on,disassociated_by,modified_on,modified_by) values(?,?,?,?,?,?,?,?,?,?)";
    private static final String INSERT_DEVICE_ASSOCIATION_M2M = INSERT_QUERY_IN_DEVICE_ASSOCIATION
                        + "associated_by,disassociated_on,disassociated_by,modified_on,modified_by,association_type,"
                        + "start_timestamp,end_timestamp) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String FIND_VALID_ASSOCIATIONS =
        "select * from device_association where serial_number=? and association_status not in ('"
            + AssociationStatus.DISASSOCIATED.name() + "')";
    private static final String GET_COUNT_FROM_ASSOCIATION =
            "select count(*)>0 from device_association where association_status in ('";
    private static final String IS_DEVICE_CURRENTLY_ASSOCIATED_TO_THE_USER = GET_COUNT_FROM_ASSOCIATION
            + AssociationStatus.ASSOCIATED + "','" + AssociationStatus.ASSOCIATION_INITIATED + "', '"
            + AssociationStatus.SUSPENDED + "') and serial_number=? and user_id=?";
    private static final String ASSOCIATED_DEVICES_TO_A_USER =
        "SELECT da.associated_by, da.associated_on, da.association_status, "
        + "da.disassociated_by, da.disassociated_on, da.harman_id, da.id, da.modified_by, da.modified_on, da"
        + ".serial_number, "
        + "da.user_id, da.factory_data, da.association_type, da.start_timestamp, da.end_timestamp, di.\"Value\" as "
        + "software_version "
        + "FROM device_association da LEFT JOIN \"DeviceInfo\" di ON (da.harman_id=di.\"HarmanID\" AND di"
        + ".\"Name\"='SW-Version') " + "WHERE da.user_id=? order by da.associated_on desc";
    private static final String UPDATE_ASSOCIATION_STATE_TO_SUSPENDED =
        "update device_association set association_status=? where factory_data=? and id=?";
    private static final String FIND_BY_ID =
        "SELECT da.associated_by, da.associated_on, da.association_status, da.disassociated_by, "
         + "da.disassociated_on, da.harman_id, da.id, da.modified_by, da.modified_on, da.serial_number, da.user_id, "
         + "da.factory_data, di.\"Value\" as software_version "
         + "FROM device_association da LEFT JOIN \"DeviceInfo\" di ON (da.harman_id=di.\"HarmanID\" AND di"
         + ".\"Name\"='SW-Version') " + "WHERE da.id = ? AND da.user_id=?";
    private static final String UPDATE_ASSOCIATION_STATE =
        "update device_association set association_status=?, disassociated_on=now(),disassociated_by=?,"
                        + "modified_on=now(),modified_by=? where id=? and user_id=?";
    private static final String UPDATE_FACTORYID_FOR_ASSOCIATION =
        "update device_association set factory_data=?, serial_number=?, modified_on=now(), modified_by=? where id=?";
    private static final String RESTORE_ASSOCIATION_STATE =
        "update device_association set association_status=?, modified_on=now(), modified_by=? where id=? and user_id=?";
    private static final String FIND_USERS_FOR_DEVICE = "select user_id from device_association where harman_id=?";
    private static final String IS_DEVICE_CURRENTLY_ASSOCIATED_TO_ANY_USER =
        GET_COUNT_FROM_ASSOCIATION
            + AssociationStatus.ASSOCIATED + "','" + AssociationStatus.ASSOCIATION_INITIATED + "') and serial_number=?";
    private static final String UPDATE_WITH_HARMANID_AND_STATUS =
        "update device_association set association_status=?, harman_id=?, modified_on=now(), modified_by=? where id=?"
                        + " and association_status=?";
    private static final String ASSOCICATED_USER_OF_ACTIVATED_DEVICE =
        "select * from device_association where serial_number=? and association_status='"
            + AssociationStatus.ASSOCIATION_INITIATED + "'";
    private static final String ASSOCIATED_USER_OF_REACTIVATED_DEVICE =
        "select * from device_association where serial_number=? and association_status='"
            + AssociationStatus.ASSOCIATED + "'";
    private static final String DEVICES_FACTORY_DATA = "select * from public.\"DeviceInfoFactoryData\" where \"ID\"=?";
    private static final String INSERT_DEVICE_ASSOCIATION_FACTORY_ID =
        INSERT_QUERY_IN_DEVICE_ASSOCIATION
             + "associated_by,disassociated_on,disassociated_by,modified_on,modified_by,factory_data) values(?,?,?,?,?,"
             + "?,?,?,?,?,?)";
    private static final String INSERT_DEVICE_ASSOCIATION_FACTORY_ID_M2M =
        INSERT_QUERY_IN_DEVICE_ASSOCIATION
                        + "associated_by,disassociated_on,disassociated_by,modified_on,modified_by,association_type,"
                        + "start_timestamp,end_timestamp,factory_data) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String INSERT_DEVICE_ACT_STATE_FACTORY =
        "insert into device_activation_state(serial_number,activation_initiated_on,activation_initiated_by,"
                        + "activation_ready,factory_data) values(?,?,?,?,?)";
    private static final String INSERT_DEVICE_ACT_STATE =
        "insert into device_activation_state(serial_number,activation_initiated_on,activation_initiated_by,"
                        + "activation_ready) values(?,?,?,?)";
    private static final String IS_DEVICE_WITH_FACTORY_DATA_ASSOC_TO_THE_USER =
        GET_COUNT_FROM_ASSOCIATION
            + AssociationStatus.ASSOCIATED + "','" + AssociationStatus.ASSOCIATION_INITIATED
            + "') and factory_data=? and user_id=?";
    private static final String IS_DEVICE_WITH_FACTORY_DATA_ASSOC_TO_THE_USER_NOT_DISASSOCIATED =
        "select count(*)>0 from device_association where association_status not in ('"
            + AssociationStatus.DISASSOCIATED + "','" + AssociationStatus.ASSOCIATION_FAILED
            + "') and factory_data=? and user_id=?";
    private static final String UPDATE_ASSOCIATION_QUERY =
            "update device_association set association_status=?, disassociated_on=?,disassociated_by=?,"
                    + "modified_on=now(),";
    private static final String UPDATE_ASSOCIATION_FOR_DISASSOCIATION_BY_ID =
        UPDATE_ASSOCIATION_QUERY + "modified_by=? where id=?";
    private static final String UPDATE_M2M_ASSOCIATION_FOR_DISASSOCIATION_BY_ID =
            UPDATE_ASSOCIATION_QUERY + "modified_by=?,end_timestamp=? where id=?";
    private static final String GET_ASSOCIATION_COUNT =
        "select count(*) from public.device_association where factory_data = ?";
    private static final String FIND_ASSOCIATION_HISTORY_SQL =
        "select id,serial_number,user_id,harman_id,association_status,associated_on,associated_by,disassociated_on,"
                        + "disassociated_by,modified_on,modified_by from device_association ";
    private static final String ASSOCIATED_FOR_DEVICE =
        "select id from device_association where harman_id = ? and  association_status = 'ASSOCIATED'";
    private static final String GET_ASSOCIATION_ID_BY_IMEI =
        "select id from device_association where user_id = ? and  factory_data = (select \"ID\" from public"
             + ".\"DeviceInfoFactoryData\" where imei = ?) and  association_status in ('ASSOCIATION_INITIATED', "
             + "'ASSOCIATED')";
    private static final String GET_ASSOCIATION_ID_BY_DEVICE_ID =
        "select count(*) from device_association where harman_id = ? and  association_status = 'ASSOCIATED'";
    private static final String GET_VIN_ASSOCIATION_DETAILS = "select count(*) from vin_details where reference_id = ?";
    private static final String INSERT_INTO_VIN_DETAILS =
        "insert into vin_details(vin,region,reference_id) values(?,?,?)";
    private static final String UPDATE_VIN_IN_VIN_DETAILS = "update vin_details set vin=? where reference_id=?";
    private static final String UPDATE_REFERENCE_ID_IN_VIN_DETAILS =
        "update vin_details set reference_id=? where vin=?";
    private static final String INSERT_INTO_SIM_DETAILS =
        "insert into sim_details(tran_id,reference_id,tran_status,user_action,createdOn, lastUpdatedOn) values (?,?,"
                        + "?,?,?,?)";
    private static final String IS_VIN_ALREADY_IN_USE =
        "select count(*) from \"device_association\" where id in(select \"reference_id\" from \"vin_details\" where "
                        + "\"vin\"= ? ) and \"association_status\" in ('ASSOCIATED', 'ASSOCIATION_INITIATED');";
    private static final String GET_ASSOCIATED_VIN =
        "select vin from vin_details v  left join device_association d on v.reference_id=d.id where d.serial_number=?"
                        + " and d.association_status in ('ASSOCIATED','ASSOCIATION_INITIATED')";
    private static final String GET_ASSOCIATED_REPLACE_VIN =
        "select vin from vin_details v  left join device_association d on v.reference_id=d.factory_data where d"
                        + ".serial_number=? and d.association_status in ('ASSOCIATED','ASSOCIATION_INITIATED')";
    private static final String GET_ASSOCIATED_FACTORY_DATA_VIN =
        "select a.vin from vin_details a, \"DeviceInfoFactoryData\" b where a.reference_id=b.\"ID\" and b"
                        + ".serial_number=?";
    private static final String IS_DEVICE_TERMINATED =
        "select count(*) from public.device_association where factory_data = ? and association_status = "
                        + "'DISASSOCIATED'";
    private static final String GET_IMSI = "select imsi from \"DeviceInfoFactoryData\" where imei = ?";
    private static final String GET_COUNTRY_CODE = "select region from vin_details where reference_id = ?";
    private static final String GET_TRAN_STATUS_TERMINATE =
        "select tran_status from sim_details where user_action='Terminate' and reference_id = ?";
    private static final String GET_TRAN_STATUS_ACTIVATE =
        "select tran_status from sim_details where user_action='Activate' and reference_id = ?";
    private static final String UPDATE_TRAN_STATUS =
        "update sim_details set tran_status=?,lastupdatedon=now() where tran_id=? and reference_id =?";
    private static final String FIND_LAST_TRANSACTION_DETAIL =
        "select tran_id, tran_status from sim_details where reference_id = ? order by createdon desc limit 1";
    private static final String FIND_ASSOCIATION_BY_ASSOCIATION_ID = "select * from device_association where id = ?";
    private static final String VALIDATE_USER_IS_OWNER_OF_DEVICE =
        "select count(*)=1 from device_association where association_status not in ('"
            + AssociationStatus.DISASSOCIATED + "') and  serial_number = ? and user_id=? and association_type = ?";
    private static final String UPDATE_ASSOCIATION_FOR_DISASSOCIATION_BY_DEVICE_ID =
        UPDATE_ASSOCIATION_QUERY
             + "modified_by=?,end_timestamp=? where harman_id=? and association_status not in ('DISASSOCIATED')";
    private static final String ASSOCIATION_TYPE_USAGE_COUNT =
        "select count(*) from device_association where association_type = ? and association_status not in ('"
            + AssociationStatus.DISASSOCIATED + "')";
    private static final String WHERE = "WHERE ";
    private static final String SERIAL_NUMBER = "serialNumber";
    private static final String AND = " AND ";
    private static final String PAGE_FILTER = "LIMIT :limit OFFSET :offset";
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParamJdbcTemplate;

    /**
     * Appends the "AND" keyword to the given query creator if needed.
     *
     * @param needAnd        a boolean indicating whether the "AND" keyword is needed
     * @param queryCreator   the StringBuilder representing the query creator
     * @return               the modified query creator with the "AND" keyword appended, or null if not needed
     */
    private static StringBuilder appendAnd(boolean needAnd, StringBuilder queryCreator) {
        if (needAnd) {
            return queryCreator.append(AND);
        } else {
            return null;
        }
    }

    /**
     * Prepares the ORDER BY query based on the provided sort by and order by parameters.
     *
     * @param sortBy  the field to sort by
     * @param orderBy the order in which to sort (asc or desc)
     * @return the prepared ORDER BY query
     */
    private static String prepareOrderByQuery(String sortBy, String orderBy) {
        StringBuilder queryOrderBy = new StringBuilder();

        if (StringUtils.isNotEmpty(sortBy) && StringUtils.isNotEmpty(orderBy)) {
            queryOrderBy.append(" ORDER BY \"");
            queryOrderBy.append(sortBy);
            if ("asc".equalsIgnoreCase(orderBy)) {
                queryOrderBy.append("\" ASC ");
            } else {
                queryOrderBy.append("\" DESC ");
            }
        }
        return queryOrderBy.toString();
    }

    /**
     * Inserts a new device association into the database.
     *
     * @param deviceAssociation The device association to be inserted.
     */
    public void insert(final DeviceAssociation deviceAssociation) {
        log.debug("insert - start: {} ", deviceAssociation);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {

            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement preparedStatement;
                if (deviceAssociation.getFactoryId() == 0) {
                    preparedStatement =
                        con.prepareStatement(INSERT_DEVICE_ASSOCIATION, Statement.RETURN_GENERATED_KEYS);
                } else {
                    preparedStatement =
                        con.prepareStatement(INSERT_DEVICE_ASSOCIATION_FACTORY_ID, Statement.RETURN_GENERATED_KEYS);
                }

                preparedStatement.setString(1, deviceAssociation.getSerialNumber());
                preparedStatement.setString(INDEX_2, deviceAssociation.getUserId());
                preparedStatement.setString(INDEX_3, deviceAssociation.getHarmanId());
                preparedStatement.setString(INDEX_4, deviceAssociation.getAssociationStatus().name());
                preparedStatement.setTimestamp(INDEX_5, deviceAssociation.getAssociatedOn());
                preparedStatement.setString(INDEX_6, deviceAssociation.getAssociatedBy());
                preparedStatement.setTimestamp(INDEX_7, deviceAssociation.getDisassociatedOn());
                preparedStatement.setString(INDEX_8, deviceAssociation.getDisassociatedBy());
                preparedStatement.setTimestamp(INDEX_9, deviceAssociation.getModifiedOn());
                preparedStatement.setString(INDEX_10, deviceAssociation.getModifiedBy());
                if (deviceAssociation.getFactoryId() != 0) {
                    preparedStatement.setLong(INDEX_11, deviceAssociation.getFactoryId());
                }
                return preparedStatement;
            }
        }, keyHolder);
        Map<String, Object> keys = keyHolder.getKeys();
        if (keys != null) {
            long associationId = (long) keys.get("id");
            deviceAssociation.setId(associationId);
        }
        log.debug("insert - end: {} ", deviceAssociation);

    }

    /**
     * Inserts a DeviceAssociation object into the database using the M2M (many-to-many) association.
     *
     * @param deviceAssociation The DeviceAssociation object to be inserted.
     */
    public void insertM2M(final DeviceAssociation deviceAssociation) {
        log.debug("insertM2M - start: {} ", deviceAssociation);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {

            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement preparedStatement;
                if (deviceAssociation.getFactoryId() == 0) {
                    preparedStatement =
                        con.prepareStatement(INSERT_DEVICE_ASSOCIATION_M2M, Statement.RETURN_GENERATED_KEYS);
                } else {
                    preparedStatement =
                        con.prepareStatement(INSERT_DEVICE_ASSOCIATION_FACTORY_ID_M2M, Statement.RETURN_GENERATED_KEYS);
                }

                preparedStatement.setString(1, deviceAssociation.getSerialNumber());
                preparedStatement.setString(INDEX_2, deviceAssociation.getUserId());
                preparedStatement.setString(INDEX_3, deviceAssociation.getHarmanId());
                preparedStatement.setString(INDEX_4, deviceAssociation.getAssociationStatus().name());
                preparedStatement.setTimestamp(INDEX_5, deviceAssociation.getAssociatedOn());
                preparedStatement.setString(INDEX_6, deviceAssociation.getAssociatedBy());
                preparedStatement.setTimestamp(INDEX_7, deviceAssociation.getDisassociatedOn());
                preparedStatement.setString(INDEX_8, deviceAssociation.getDisassociatedBy());
                preparedStatement.setTimestamp(INDEX_9, deviceAssociation.getModifiedOn());
                preparedStatement.setString(INDEX_10, deviceAssociation.getModifiedBy());
                preparedStatement.setString(INDEX_11, deviceAssociation.getAssociationType());
                preparedStatement.setTimestamp(INDEX_12, new Timestamp(deviceAssociation.getStartTimeStamp()));
                preparedStatement.setTimestamp(INDEX_13,
                    deviceAssociation.getEndTimeStamp() == 0L ? null :
                        new Timestamp(deviceAssociation.getEndTimeStamp()));
                if (deviceAssociation.getFactoryId() != 0) {
                    preparedStatement.setLong(INDEX_14, deviceAssociation.getFactoryId());
                }
                return preparedStatement;
            }
        }, keyHolder);
        Map<String, Object> keys = keyHolder.getKeys();
        if (keys != null) {
            long associationId = (long) keys.get("id");
            deviceAssociation.setId(associationId);
            log.info("###Final insertM2M :{}", associationId);
        }
        log.debug("insert - end: {} ", deviceAssociation);

    }

    /**
     * Updates the device association table with the specified Harman ID for the given serial number.
     *
     * @param harmanId      the Harman ID to update
     * @param serialNumber  the serial number of the device
     * @return the number of rows updated in the device association table
     */
    public int updateHarmanId(String harmanId, String serialNumber) {
        log.debug("Updating device association table with harman id for factory_data {}", serialNumber);
        String sql = "update public.\"device_association\" set \"harman_id\"=? where \"serial_number\"=?";
        int updated = jdbcTemplate.update(sql, harmanId, serialNumber);
        log.debug("updated the association table with harmanId {} and serialNumber {}", harmanId, serialNumber);
        return updated;
    }

    /**
     * Checks if a device is currently associated to a user.
     *
     * @param serialNumber The serial number of the device.
     * @param userId The ID of the user.
     * @return {@code true} if the device is currently associated to the user, {@code false} otherwise.
     */
    public boolean isDeviceCurrentlyAssociatedToUser(String serialNumber, String userId) {
        Boolean currentlyAssociated =
            jdbcTemplate.queryForObject(IS_DEVICE_CURRENTLY_ASSOCIATED_TO_THE_USER, Boolean.class, serialNumber,
                userId);
        return currentlyAssociated != null ? currentlyAssociated : false;
    }

    /**
     * Retrieves the user details associated with the given Harman ID.
     *
     * @param harmanId the Harman ID of the device
     * @return a list of user details associated with the device
     */
    public List<String> getUserDetails(String harmanId) {
        return jdbcTemplate.queryForList(FIND_USERS_FOR_DEVICE, String.class, harmanId);
    }

    /**
     * Fetches the associated devices for a given user.
     *
     * @param userId the ID of the user
     * @return a list of DeviceAssociation objects representing the associated devices
     */
    public List<DeviceAssociation> fetchAssociatedDevices(String userId) {
        return jdbcTemplate.query(ASSOCIATED_DEVICES_TO_A_USER, new Object[]{userId},
            new DeviceAssociationRowMapper());
    }

    /**
     * Finds valid associations for a given serial number.
     *
     * @param serialNumber The serial number of the device.
     * @return The DeviceAssociation object representing the valid association, or null if no valid association is
     *      found.
     */
    public DeviceAssociation findValidAssociations(String serialNumber) {
        DeviceAssociation deviceAssociation = null;
        try {
            deviceAssociation =
                jdbcTemplate.queryForObject(FIND_VALID_ASSOCIATIONS, new DeviceAssociationRowMapper(), serialNumber);
        } catch (EmptyResultDataAccessException e) {
            log.info("no results found for findValidAssociations for serial number : {}", serialNumber);
        }
        return deviceAssociation;
    }

    /**
     * Finds a device association by its ID and user ID.
     *
     * @param id     the ID of the device association
     * @param userId the ID of the user
     * @return the device association found, or null if no association is found
     */
    public DeviceAssociation find(long id, String userId) {
        DeviceAssociation deviceAssociation = null;
        try {
            deviceAssociation =
                jdbcTemplate.queryForObject(FIND_BY_ID, new Object[]{id, userId}, new DeviceAssociationRowMapper());
        } catch (EmptyResultDataAccessException e) {
            log.info("no results found for find for id: {}", id);
        }
        return deviceAssociation;
    }

    /**
     * Updates the Harman ID for a device association.
     *
     * @param harmanId The new Harman ID to be updated.
     * @param id The ID of the device association.
     * @param userId The user ID associated with the device association.
     * @return The number of rows affected by the update operation.
     */
    public int updateHarmaId(String harmanId, long id, String userId) {
        log.debug("updateHarmanID : harmanId : {} , id:{} ", harmanId, id);
        return jdbcTemplate.update(UPDATE_WITH_HARMANID_AND_STATUS, AssociationStatus.ASSOCIATED.name(), harmanId,
            userId, id, AssociationStatus.ASSOCIATION_INITIATED.name());
    }

    /**
     * Retrieves the associated user(s) for a given serial number.
     *
     * @param serialNumber    The serial number of the device.
     * @param reactivationFlag A flag indicating whether the device is being reactivated.
     *                         If set to true, retrieves associations with status ASSOCIATED.
     *                         If set to false, retrieves associations with status ACTIVATED.
     * @return A list of DeviceAssociation objects representing the associated user(s).
     */
    public List<DeviceAssociation> retrieveAssociatedUser(String serialNumber, boolean reactivationFlag) {
        List<DeviceAssociation> deviceAssociationlist = null;
        try {
            if (reactivationFlag) {
                deviceAssociationlist = jdbcTemplate.query(ASSOCIATED_USER_OF_REACTIVATED_DEVICE,
                    new Object[]{serialNumber}, new DeviceAssociationRowMapper());
            } else {
                deviceAssociationlist = jdbcTemplate.query(ASSOCICATED_USER_OF_ACTIVATED_DEVICE,
                    new Object[]{serialNumber}, new DeviceAssociationRowMapper());
            }
        } catch (Exception e) {
            log.info("Actual Error message " + e.getMessage());
            log.error("No association found in db for serialNumber: {} ", serialNumber);
        }
        return deviceAssociationlist;
    }

    /**
     * Constructs and fetches factory data based on the provided AssociateDeviceRequest.
     *
     * @param associateReq The AssociateDeviceRequest object containing the device information.
     * @return A list of FactoryData objects matching the provided device information.
     * @throws RuntimeException if there is an error while fetching the factory data.
     */
    public List<FactoryData> constructAndFetchFactoryData(AssociateDeviceRequest associateReq) {
        MapSqlParameterSource mapSqlParameter = new MapSqlParameterSource();
        StringBuilder queryCreator = new StringBuilder(SELECT_FROM_DEVICE_INFO_FACTORY_DATA);
        queryCreator.append(WHERE);
        boolean isWhereClause = false;
        if (!StringUtils.isEmpty(associateReq.getImei())) {
            isWhereClause = true;
            queryCreator.append("imei = :imei ");
            mapSqlParameter.addValue("imei", associateReq.getImei());
        }
        if (!StringUtils.isEmpty(associateReq.getSerialNumber())) {
            if (isWhereClause) {
                queryCreator.append("and ");
            }
            queryCreator.append("serial_number = :serialNumber ");
            mapSqlParameter.addValue(SERIAL_NUMBER, associateReq.getSerialNumber());
            // set to true if imei is empty
            isWhereClause = true;
        }
        if (!StringUtils.isEmpty(associateReq.getBssid())) {
            if (isWhereClause) {
                queryCreator.append("and ");
            }
            queryCreator.append("bssid = :bssid ");
            mapSqlParameter.addValue("bssid", associateReq.getBssid());
        }

        // no need to check and condition further as either any one of above
        // data (IMEI||SERIALNUMBER||BSSID) need to be present .
        if (!StringUtils.isEmpty(associateReq.getImsi())) {
            queryCreator.append("and ");
            queryCreator.append("imsi = :imsi ");
            mapSqlParameter.addValue("imsi", associateReq.getImsi());
        }
        if (!StringUtils.isEmpty(associateReq.getMsisdn())) {
            queryCreator.append("and ");
            queryCreator.append("msisdn = :msisdn ");
            mapSqlParameter.addValue("msisdn", associateReq.getMsisdn());
        }
        if (!StringUtils.isEmpty(associateReq.getSsid())) {
            queryCreator.append("and ");
            queryCreator.append("ssid = :ssid ");
            mapSqlParameter.addValue("ssid", associateReq.getSsid());
        }
        if (!StringUtils.isEmpty(associateReq.getIccid())) {
            queryCreator.append("and ");
            queryCreator.append("iccid = :iccid ");
            mapSqlParameter.addValue("iccid", associateReq.getIccid());
        }
        log.debug("Inside constructAndFetchFactoryData method query generated : {} ", queryCreator.toString());
        try {
            return namedParamJdbcTemplate.query(queryCreator.toString(), mapSqlParameter,
                new FactoryDataMapper());
        } catch (DataAccessException e) {
            throw new ApiTechnicalException("Error while fetching factory data", e.getMessage());
        }
    }

    /**
     * Updates the device association in the database with the provided deviceAssociation object.
     *
     * @param deviceAssociation The DeviceAssociation object containing the updated association details.
     * @return The number of rows affected by the update operation.
     */
    public int updateDeviceAssociation(final DeviceAssociation deviceAssociation) {
        return jdbcTemplate.update(UPDATE_ASSOCIATION_STATE, deviceAssociation.getAssociationStatus().name(),
            deviceAssociation.getDisassociatedBy(), deviceAssociation.getModifiedBy(), deviceAssociation.getId(),
            deviceAssociation.getUserId());
    }

    /**
     * Updates the association status of a device to "Restore" in the database.
     *
     * @param deviceAssociation The DeviceAssociation object containing the updated association status.
     * @return The number of rows affected by the update operation.
     */
    public int updateDeviceAssociationStatusToRestore(final DeviceAssociation deviceAssociation) {
        return jdbcTemplate.update(RESTORE_ASSOCIATION_STATE, deviceAssociation.getAssociationStatus().name(),
            deviceAssociation.getModifiedBy(), deviceAssociation.getId(), deviceAssociation.getUserId());
    }

    /**
     * Updates the status of a device association to "Suspended".
     *
     * @param deviceAssociation The device association to update.
     * @return The number of rows affected by the update operation.
     */
    public int updateDeviceAssociationStatusToSuspended(final DeviceAssociation deviceAssociation) {
        return jdbcTemplate.update(UPDATE_ASSOCIATION_STATE_TO_SUSPENDED, AssociationStatus.SUSPENDED.name(),
            deviceAssociation.getFactoryId(), deviceAssociation.getId());
    }

    /**
     * Checks if a device is currently associated to any user.
     *
     * @param serialNumber the serial number of the device
     * @return true if the device is currently associated to any user, false otherwise
     */
    public boolean isDeviceCurrentlyAssociatedToAnyUser(String serialNumber) {
        Boolean currentlyAssociated =
            jdbcTemplate.queryForObject(IS_DEVICE_CURRENTLY_ASSOCIATED_TO_ANY_USER, Boolean.class, serialNumber);
        return currentlyAssociated != null ? currentlyAssociated : false;
    }

    /**
     * Updates the transaction status for a device association.
     *
     * @param status              the new transaction status
     * @param transactionId       the transaction ID
     * @param deviceAssociationId the device association ID
     * @return the number of rows affected by the update
     */
    public int updateTransactionStatus(String status, String transactionId, Long deviceAssociationId) {
        return jdbcTemplate.update(UPDATE_TRAN_STATUS, status,
            transactionId, deviceAssociationId);
    }

    /**
     * Finds the latest transaction status of a SIM based on the given reference ID.
     *
     * @param referenceId The reference ID used to search for the SIM transaction.
     * @return The SimDetails object representing the latest transaction status of the SIM, or null if no results are
     *      found.
     */
    public SimDetails findLatestSimTransactionStatus(long referenceId) {
        SimDetails simDetails = null;
        try {
            simDetails =
                jdbcTemplate.queryForObject(FIND_LAST_TRANSACTION_DETAIL, new SimDetailsRowMapper(), referenceId);
        } catch (EmptyResultDataAccessException e) {
            log.error("no results found for getLatestSimTransactionDetail for id : {}", referenceId);
        }
        return simDetails;
    }

    /**
     * Fetches the factory data for a given factory ID.
     *
     * @param factoryId the ID of the factory
     * @return a list of FactoryData objects containing the fetched data
     */
    public List<FactoryData> fetchFactoryData(long factoryId) {
        return jdbcTemplate.query(DEVICES_FACTORY_DATA, new Object[]{factoryId},
            new FactoryDataMapper());
    }

    /**
     * Inserts the device state into the database.
     *
     * @param deviceAssociation The device association object containing the device state information.
     */
    public void insertDeviceState(final DeviceAssociation deviceAssociation) {
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection paramConnection) throws SQLException {
                PreparedStatement ps;
                if (deviceAssociation.getFactoryId() == 0) {
                    ps = paramConnection.prepareStatement(INSERT_DEVICE_ACT_STATE);
                } else {
                    ps = paramConnection.prepareStatement(INSERT_DEVICE_ACT_STATE_FACTORY);
                }
                ps.setString(1, deviceAssociation.getSerialNumber());
                ps.setTimestamp(INDEX_2, new Timestamp(Calendar.getInstance().getTimeInMillis()));
                ps.setString(INDEX_3, deviceAssociation.getUserId());
                ps.setBoolean(INDEX_4, true);
                if (deviceAssociation.getFactoryId() != 0) {
                    ps.setLong(INDEX_5, deviceAssociation.getFactoryId());
                }
                return ps;
            }
        });
    }

    /**
     * Constructs and fetches device association data based on the provided parameters.
     *
     * @param serialNumber The serial number of the device.
     * @param deviceId The device ID.
     * @param imei The IMEI number of the device.
     * @param userId The user ID.
     * @param associationId The association ID.
     * @param statusList The list of association statuses.
     * @return A list of DeviceAssociation objects containing the fetched device association data.
     */
    public List<DeviceAssociation> constructAndFetchDeviceAssociationData(final String serialNumber,
                                                                          final String deviceId,
                                                                          final String imei, final String userId,
                                                                          final Long associationId,
                                                                          final List<AssociationStatus> statusList) {
        log.info("Inside constructAndFetchDeviceAssociationData method");

        final MapSqlParameterSource mapSqlParameter = new MapSqlParameterSource();
        final StringBuilder queryCreator = new StringBuilder("SELECT * FROM public.device_association da WHERE ");
        boolean needAnd = false;

        if (!StringUtils.isEmpty(deviceId)) {
            queryCreator.append("da.harman_id = :harman_id ");
            mapSqlParameter.addValue("harman_id", deviceId);
            needAnd = true;
        }
        if (!ObjectUtils.isEmpty(associationId)) {
            appendAnd(needAnd, queryCreator);
            queryCreator.append("da.id = :id ");
            mapSqlParameter.addValue("id", associationId);
            needAnd = true;
        }
        if (!StringUtils.isEmpty(serialNumber)) {
            appendAnd(needAnd, queryCreator);
            queryCreator.append("da.serial_number = :serialNumber ");
            mapSqlParameter.addValue(SERIAL_NUMBER, serialNumber);
            needAnd = true;
        }
        appendAnd(needAnd, queryCreator);
        queryCreator.append("da.user_id = :user_id ");
        mapSqlParameter.addValue("user_id", userId);
        needAnd = true;

        if (statusList != null && !statusList.isEmpty()) {
            appendAnd(needAnd, queryCreator);
            queryCreator.append("da.association_status IN ('");
            String prefix = "";
            for (AssociationStatus status : statusList) {
                queryCreator.append(prefix);
                prefix = "','";
                queryCreator.append(status);
            }
            queryCreator.append("')");
            needAnd = true;
        }

        if (!StringUtils.isEmpty(imei)) {
            appendAnd(needAnd, queryCreator);
            queryCreator.append(
                "da.factory_data IN (SELECT df.\"ID\" FROM public.\"DeviceInfoFactoryData\" df WHERE df.imei = :imei)"
                            +         " AND da.disassociated_on is NULL");
            mapSqlParameter.addValue("imei", imei);
        }

        log.info("Query generated :: {}", queryCreator.toString());
        final List<DeviceAssociation> deviceAssociationList =
            namedParamJdbcTemplate.query(queryCreator.toString(), mapSqlParameter,
                new DeviceAssociationRowMapper());
        log.info("Exit constructAndFetchDeviceAssociationData method");
        return deviceAssociationList;
    }

    /**
     * Fetches the association details based on the provided attribute map and sort order.
     *
     * @param attributeMap The attribute map containing the search criteria.
     * @param sortById     A boolean flag indicating whether to sort the results by ID in descending order.
     * @return A list of AssociationDetailsResponse objects containing the fetched association details.
     */
    public List<AssociationDetailsResponse> fetchAssociationDetails(final Map<String, Object> attributeMap,
                                                                    final boolean sortById) {
        final String prefix =
            "SELECT da.id, da.user_id, da.serial_number, da.association_status, da.associated_on, da"
             +     ".disassociated_on, "
             +     "da.harman_id, di.\"Value\" as software_version, difd.imei, difd.ssid, difd.iccid, difd.msisdn, difd"
             +     ".imsi, difd.bssid, " + "difd.state, difd.isstolen as \"isStolen\", difd.isfaulty as \"isFaulty\" "
             +     "FROM ((device_association da LEFT JOIN \"DeviceInfoFactoryData\" difd ON da.factory_data = difd"
             +     ".\"ID\") LEFT JOIN \"DeviceInfo\" di ON (da.harman_id=di.\"HarmanID\" AND"
            + " di.\"Name\"='SW-Version')) WHERE ";
        final String operator = AND;
        final String sql = SqlUtility.getPreparedSql(prefix, operator, attributeMap);
        String finalSql;
        if (sortById) {
            finalSql = sql + " order by da.id desc";
        } else {
            finalSql = sql + "";
        }
        log.debug("Prepared final sql : {}", finalSql);
        final Object[] values = SqlUtility.getArrayValues(attributeMap);
        log.debug("Values.length: {}", values.length);
        return jdbcTemplate.query(
            finalSql, new PreparedStatementSetter() {
                public void setValues(PreparedStatement preparedStatement) throws SQLException {
                    for (int i = 0; i < values.length; i++) {
                        preparedStatement.setObject(i + 1, values[i]);
                    }
                }
            }, new AssociationDetailsResponseMapper());
    }

    /**
     * Checks if there is an associated device with factory data for the given factory ID and user ID.
     *
     * @param factoryId the ID of the factory
     * @param userId the ID of the user
     * @return true if there is an associated device with factory data, false otherwise
     */
    public boolean checkAssociatedDeviceWithFactData(long factoryId, String userId) {
        Boolean associated =
            jdbcTemplate.queryForObject(IS_DEVICE_WITH_FACTORY_DATA_ASSOC_TO_THE_USER, Boolean.class, factoryId,
                userId);
        return associated != null ? associated : false;
    }

    /**
     * Checks if there is an associated device with factory data that has not been disassociated for the given
     * factory ID and user ID.
     *
     * @param factoryId the ID of the factory
     * @param userId the ID of the user
     * @return true if there is an associated device with factory data that has not been disassociated, false otherwise
     */
    public boolean checkAssociatedDeviceWithFactDataNotDisassociated(long factoryId, String userId) {
        Boolean associated =
            jdbcTemplate.queryForObject(IS_DEVICE_WITH_FACTORY_DATA_ASSOC_TO_THE_USER_NOT_DISASSOCIATED, Boolean.class,
                factoryId, userId);
        return associated != null ? associated : false;
    }

    /**
     * Updates the association status, disassociated on, disassociated by, and modified by fields
     * for a device association with the given ID.
     *
     * @param deviceAssociation The DeviceAssociation object containing the updated values.
     * @return The number of rows affected by the update operation.
     */
    public int updateForDisassociationById(final DeviceAssociation deviceAssociation) {
        return jdbcTemplate.update(UPDATE_ASSOCIATION_FOR_DISASSOCIATION_BY_ID,
            deviceAssociation.getAssociationStatus().name(),
            deviceAssociation.getDisassociatedOn(), deviceAssociation.getDisassociatedBy(),
            deviceAssociation.getModifiedBy(),
            deviceAssociation.getId());
    }

    /**
     * Updates the association status, disassociated on, disassociated by, modified by, and end timestamp
     * for a given device association by its ID.
     *
     * @param deviceAssociation The DeviceAssociation object containing the updated values.
     * @return The number of rows affected by the update operation.
     */
    public int updateForM2MdisassociationById(final DeviceAssociation deviceAssociation) {
        return jdbcTemplate.update(UPDATE_M2M_ASSOCIATION_FOR_DISASSOCIATION_BY_ID,
            deviceAssociation.getAssociationStatus().name(),
            deviceAssociation.getDisassociatedOn(), deviceAssociation.getDisassociatedBy(),
            deviceAssociation.getModifiedBy(),
            new Timestamp(deviceAssociation.getEndTimeStamp()), deviceAssociation.getId());
    }

    /**
     * Updates the records in the database table based on the provided updatedMap and conditionMap.
     *
     * @param updatedMap   a LinkedHashMap containing the updated values for the columns to be updated
     * @param conditionMap a LinkedHashMap containing the conditions for the update operation
     * @return the number of rows affected by the update operation
     */
    public int update(LinkedHashMap<String, Object> updatedMap, LinkedHashMap<String, Object> conditionMap) {

        String prefix = "update public." + Postgres.DEVICE_ASSOCIATION + " set ";
        String operator = " , ";
        String sql = SqlUtility.getPreparedSql(prefix, operator, updatedMap);
        sql = sql + " where ";
        operator = AND;
        sql = SqlUtility.getPreparedSql(sql, operator, conditionMap);
        log.debug("Preparedsql for device_association: {}", sql);

        List<Map<String, Object>> orderMapList = new ArrayList<>();
        orderMapList.add(updatedMap);
        orderMapList.add(conditionMap);
        Object[] values = SqlUtility.getArrayValues(orderMapList);

        if (null == sql || null == values) {
            return RETURN_VALUE;
        }
        return jdbcTemplate.update(sql, values);
    }

    /**
     * Updates the factory ID for a device association when replacing a device.
     *
     * @param associationId The ID of the device association.
     * @param serialNumber The serial number of the new device.
     * @param userId The ID of the user performing the update.
     * @param factoryId The ID of the new factory.
     * @return The number of rows affected by the update operation.
     */
    public int updateForReplaceDevice(Long associationId, String serialNumber, String userId, Long factoryId) {
        return jdbcTemplate.update(UPDATE_FACTORYID_FOR_ASSOCIATION, factoryId, serialNumber,
            userId, associationId);

    }

    /**
     * Retrieves the count of associations for a given factory ID.
     *
     * @param id The ID of the factory.
     * @return The count of associations for the given factory ID.
     */
    public int findAssociationCountForFactoryId(long id) {
        Integer associationCount = jdbcTemplate.queryForObject(GET_ASSOCIATION_COUNT, new Object[]{id},
            Integer.class);
        return associationCount != null ? associationCount : 0;
    }

    /**
     * Checks if a device is terminated.
     *
     * @param id The ID of the device to check.
     * @return {@code true} if the device is terminated, {@code false} otherwise.
     */
    public boolean isDeviceTerminated(long id) {
        /*
         * Check if device got disassociated. When device state is provisioned
         * or provisioned_alive, then there can be only one association status
         * possible (if any), which is 'DISASSOCIATED'
         */
        Integer terminated = jdbcTemplate.queryForObject(IS_DEVICE_TERMINATED, new Object[]{id},
            Integer.class);
        int count = terminated != null ? terminated : 0;
        return count > 0;
    }

    /**
     * Retrieves the association details based on the provided parameters.
     *
     * @param factoryData The factory data to filter the association details. Pass null or 0 to retrieve all
     *                    association details.
     * @param orderby The field to order the association details by.
     * @param sortby The sort order for the association details. Valid values are "ASC" for ascending order and "DESC"
     *               for descending order.
     * @param page The page number of the association details to retrieve.
     * @param size The number of association details to retrieve per page.
     * @return A list of DeviceAssociationHistory objects containing the association details.
     */
    public List<DeviceAssociationHistory> getAssociationDetails(Long factoryData, String orderby, String sortby,
                                                                int page, int size) {
        log.debug("Inside getAssociationDetails method");
        MapSqlParameterSource mapSqlParameter = new MapSqlParameterSource();
        StringBuilder queryCreator = new StringBuilder(FIND_ASSOCIATION_HISTORY_SQL);
        queryCreator.append(WHERE);
        if (factoryData != null && factoryData != 0) {
            queryCreator.append("factory_data  = :factory_data ");
            mapSqlParameter.addValue("factory_data", factoryData);
        }
        queryCreator.append(prepareOrderByQuery(sortby, orderby));
        queryCreator.append(constructPageFilter());
        mapSqlParameter.addValue("limit", size);
        mapSqlParameter.addValue("offset", (page - 1) * size);
        log.debug("Association History Query generated :: {} ", queryCreator);
        List<DeviceAssociationHistory> deviceAssociationHistoryList =
            namedParamJdbcTemplate.query(queryCreator.toString(), mapSqlParameter,
                new DeviceAssociationHistoryRowMapper());
        log.debug("Exit constructAndFetchFactoryData method");
        return deviceAssociationHistoryList;
    }

    private String constructPageFilter() {
        return PAGE_FILTER;
    }

    /**
     * Checks if an association exists for the given user ID and IMEI.
     *
     * @param userId the user ID
     * @param imei the IMEI
     * @return the association ID if an association exists, or null otherwise
     */
    public Long associationExists(String userId, String imei) {
        return jdbcTemplate.query(GET_ASSOCIATION_ID_BY_IMEI,
            new Object[]{userId, imei}, new ResultSetExtractor<Long>() {
                @Override
                public Long extractData(ResultSet rs) throws SQLException,
                    DataAccessException {
                    return rs.next() ? rs.getLong("id") : null;
                }
            });
    }

    /**
     * Retrieves the association ID for a given device ID.
     *
     * @param deviceId The ID of the device.
     * @return The association ID if found, or 0 if not found.
     */
    public long getAssociationIdByDeviceId(String deviceId) {
        Integer associationId = jdbcTemplate.queryForObject(ASSOCIATED_FOR_DEVICE,
            new Object[]{deviceId}, Integer.class);
        return associationId != null ? associationId : 0;
    }

    /**
     * Checks if an association exists for the given device ID.
     *
     * @param deviceId the ID of the device to check association for
     * @return true if an association exists for the device, false otherwise
     */
    public boolean associationByDeviceExists(String deviceId) {
        Integer associationByDeviceExists = jdbcTemplate.queryForObject(GET_ASSOCIATION_ID_BY_DEVICE_ID,
            new Object[]{deviceId}, Integer.class);
        int count = associationByDeviceExists != null ? associationByDeviceExists : 0;

        return count == 1;
    }

    /**
     * Saves the VIN details to the database.
     *
     * @param vin     the VIN (Vehicle Identification Number) to be saved
     * @param region  the region associated with the VIN
     * @param accosId the ID of the associated account
     */
    public void saveVinDetails(String vin, String region, long accosId) {
        jdbcTemplate.update(INSERT_INTO_VIN_DETAILS, vin, region, accosId);
    }

    /**
     * Saves the SIM details to the database.
     *
     * @param tranId         the transaction ID
     * @param accosId        the association ID
     * @param tranStatus     the transaction status
     * @param userAction     the user action
     * @param createdOn      the timestamp of when the SIM details were created
     * @param lastUpdatedOn  the timestamp of when the SIM details were last updated
     */
    public void saveSimDetails(String tranId, long accosId, String tranStatus, String userAction, Timestamp createdOn,
                               Timestamp lastUpdatedOn) {
        jdbcTemplate.update(INSERT_INTO_SIM_DETAILS,
                tranId, accosId, tranStatus, userAction, createdOn, lastUpdatedOn);
    }

    /**
     * Checks if a VIN association exists for the given association ID.
     *
     * @param assocId the association ID to check
     * @return true if a VIN association exists, false otherwise
     */
    public boolean vinAssociationExists(long assocId) {
        Long vinAssociationExists = jdbcTemplate.queryForObject(GET_VIN_ASSOCIATION_DETAILS,
            new Object[]{assocId}, Long.class);
        long vinAssoc = vinAssociationExists != null ? vinAssociationExists : 0L;

        return vinAssoc == 1;
    }

    /**
     * Checks if a VIN (Vehicle Identification Number) is already associated with a device.
     *
     * @param vin The VIN to check for association.
     * @return {@code true} if the VIN is already associated with a device, {@code false} otherwise.
     */
    public boolean vinAlreadyAssociated(String vin) {
        Long vinAlreadyAssociated = jdbcTemplate.queryForObject(IS_VIN_ALREADY_IN_USE,
            new Object[]{vin}, Long.class);
        long vinAssoc = vinAlreadyAssociated != null ? vinAlreadyAssociated : 0L;

        return vinAssoc > 0;
    }

    /**
     * Retrieves the associated VIN (Vehicle Identification Number) for the given serial number.
     *
     * @param serialNumber The serial number of the device.
     * @return The associated VIN, or null if no VIN is associated with the serial number.
     */
    public String getAssociatedVin(String serialNumber) {
        String vin = null;
        try {
            vin = jdbcTemplate.queryForObject(GET_ASSOCIATED_VIN, new Object[]{serialNumber}, String.class);
        } catch (DataAccessException e) {
            log.error("Error has occurred while retrieving vin for given serialNumber: {}, ErrorMsg: {}", serialNumber,
                e.getMessage());
        }
        return vin;
    }

    /**
     * Finds the associated factory data VIN for a given serial number.
     *
     * @param serialNumber The serial number of the device.
     * @return The associated factory data VIN, or null if not found.
     * @throws IllegalArgumentException If the serial number is null or empty.
     */
    public String findAssociatedFactoryDataVin(String serialNumber) {
        log.debug("## findAssociatedFactoryDataVin - START");
        String vin = null;
        try {
            List<String> strLst;
            if (StringUtils.isNotEmpty(serialNumber)) {
                log.debug("## GET_ASSOCIATED_FACTORY_DATA_VIN: {}", GET_ASSOCIATED_FACTORY_DATA_VIN);
                strLst = jdbcTemplate.query(GET_ASSOCIATED_FACTORY_DATA_VIN, new Object[]{serialNumber},
                    new RowMapper<String>() {
                        @Override
                        public String mapRow(ResultSet resultSet, int i) throws SQLException {
                            return resultSet.getString(1);
                        }
                    });
            } else {
                log.error("## Serial number is either null of empty serialNumber: {}", serialNumber);
                throw new IllegalArgumentException("## Serial number is mandatory");
            }
            if (strLst != null && !strLst.isEmpty()) {
                vin = strLst.get(0);
            }
        } catch (DataAccessException e) {
            log.error(
                "Exception occurred while trying to retrieve the vin, could be that no record found for the serial{}",
                serialNumber, e);
        }
        log.debug("## findAssociatedFactoryDataVin - END vin: {}", vin);
        return vin;
    }

    /**
     * Replaces the VIN (Vehicle Identification Number) associated with the given association ID.
     *
     * @param assocId the association ID of the device
     * @param vin the new VIN to be associated with the device
     */
    public void replaceVin(long assocId, String vin) {
        jdbcTemplate.update(UPDATE_VIN_IN_VIN_DETAILS, vin, assocId);
    }

    /**
     * Replaces the reference ID in the VIN details with the given factory ID and VIN.
     *
     * @param factoryId the factory ID to replace the reference ID with
     * @param vin the VIN (Vehicle Identification Number) to update
     */
    public void replaceReferenceIdInVinDetails(long factoryId, String vin) {
        jdbcTemplate.update(UPDATE_REFERENCE_ID_IN_VIN_DETAILS, factoryId, vin);
    }

    /**
     * Finds the count of device associations based on the provided query conditions.
     *
     * @param queryConditionMap a map containing the query conditions
     * @return true if there are device associations matching the query conditions, false otherwise
     */
    public boolean findCountByDetails(Map<String, Object> queryConditionMap) {
        String queryPrefix =
            "select count(*)>0 from public.\"DeviceInfoFactoryData\" a LEFT JOIN device_association b ON (a.\"ID\"=b"
                        +     ".factory_data) where b.association_status in ('"
                        +     AssociationStatus.ASSOCIATED + "','" + AssociationStatus.ASSOCIATION_INITIATED + "','"
                        +     AssociationStatus.SUSPENDED + "')" + AND;
        String operator = AND;
        String selectQuery = SqlUtility.getPreparedSql(queryPrefix, operator, queryConditionMap);
        Boolean findCount =
            jdbcTemplate.queryForObject(selectQuery, Boolean.class, queryConditionMap.values().toArray(new Object[1]));
        return findCount != null ? findCount : false;
    }

    /**
     * Retrieves the IMSI (International Mobile Subscriber Identity) associated with the given
     * IMEI (International Mobile Equipment Identity).
     *
     * @param imei The IMEI for which to retrieve the IMSI.
     * @return The IMSI associated with the given IMEI, or null if no record is found.
     */
    public String getImsi(String imei) {
        String imsi = null;
        try {
            imsi = jdbcTemplate.queryForObject(GET_IMSI, new Object[]{imei}, String.class);
        } catch (DataAccessException e) {
            log.error(
                "Exception occured while trying to retrieve the imsi, could be that no record found for the imei{}",
                imei, e);
        }
        return imsi;
    }

    /**
     * Retrieves the country code associated with the given reference ID.
     *
     * @param referenceId The reference ID used to retrieve the country code.
     * @return The country code associated with the reference ID, or null if no record is found.
     */
    public String getCountryCode(Long referenceId) {
        String countryCode = null;
        try {
            countryCode = jdbcTemplate.queryForObject(GET_COUNTRY_CODE, new Object[]{referenceId}, String.class);
        } catch (DataAccessException e) {
            log.error(
                "Exception occured while trying to retrieve the region, could be that no record found for the "
                            +         "referenceId {}",
                referenceId, e);
        }
        return countryCode;
    }

    /**
     * Retrieves the termination status for a given reference ID.
     *
     * @param referenceId The reference ID for which the termination status is to be retrieved.
     * @return The termination status as a String.
     */
    public String getTerminateTranStatus(Long referenceId) {
        String tranStatus = null;
        try {
            tranStatus =
                jdbcTemplate.queryForObject(GET_TRAN_STATUS_TERMINATE, new Object[]{referenceId}, String.class);
        } catch (DataAccessException e) {
            log.error(
                "Exception occured while trying to retrieve the termination status, could be that no record found for"
                            +         " the referenceId {}",
                referenceId, e);
        }
        return tranStatus;
    }

    /**
     * Retrieves the activation transaction status for a given reference ID.
     *
     * @param referenceId The reference ID of the transaction.
     * @return The activation transaction status.
     */
    public String getActivateTranStatus(Long referenceId) {
        String tranStatus = null;
        try {
            tranStatus = jdbcTemplate.queryForObject(GET_TRAN_STATUS_ACTIVATE, new Object[]{referenceId}, String.class);
        } catch (DataAccessException e) {
            log.error(
                "Exception occured while trying to retrieve the termination status, could be that no record found for"
                            +         " the referenceId {}",
                referenceId, e);
        }
        return tranStatus;
    }

    /**
     * Retrieves the associated VIN for the given serial number.
     *
     * @param serialNumber The serial number of the device.
     * @return The associated VIN, or null if not found.
     */
    public String getAssociatedVinForReplaceApi(String serialNumber) {
        String vin = null;
        try {
            vin = jdbcTemplate.queryForObject(GET_ASSOCIATED_REPLACE_VIN, new Object[]{serialNumber}, String.class);
        } catch (DataAccessException e) {
            log.error("## Error has occurred while retrieving vin for given serialNumber: {}, ErrorMsg: {}",
                serialNumber, e.getMessage());
        }
        return vin;
    }

    /**
     * Updates the user IDs with a dummy value in the device_association table.
     *
     * @param userIds        The list of user IDs to update.
     * @param serialNumbers  The list of serial numbers to filter the update.
     * @return               The number of rows affected by the update.
     */
    public int updateUserIdWithDummyValue(final List<String> userIds, List<String> serialNumbers) {
        final StringBuilder queryCreator = new StringBuilder(
            "update device_association set user_id = '" + UNPREDICTABLE_USER_ID + "' ,associated_by = '"
                + UNPREDICTABLE_USER_ID + "' ,modified_by = '" + UNPREDICTABLE_USER_ID + "', disassociated_by = '"
                + UNPREDICTABLE_USER_ID + "' ,serial_number = '" + UNPREDICTABLE_USER_ID
                + "', factory_data = (SELECT \"ID\" FROM public.\"DeviceInfoFactoryData\" where state='DUMMY' and "
                + "factory_admin='HCP SCRIPTS')"
                + " where association_status = 'DISASSOCIATED' ");
        if (serialNumbers != null && !serialNumbers.isEmpty()) {
            queryCreator.append("and serial_number in ('");
            String prefix = "";
            for (String serialNumber : serialNumbers) {
                queryCreator.append(prefix);
                prefix = "','";
                queryCreator.append(serialNumber);
            }
            queryCreator.append("')");

        }
        if (userIds != null && !userIds.isEmpty()) {
            queryCreator.append(" and user_id in ('");
            String prefix = "";
            for (String userId : userIds) {
                queryCreator.append(prefix);
                prefix = "','";
                queryCreator.append(userId);
            }
            queryCreator.append("')");
        }
        log.info("created query for device_association userid update: {}", queryCreator.toString());
        log.info("updating the association table with userId {}",
            ((userIds != null && !userIds.isEmpty()) ? userIds.toArray() : null));
        return jdbcTemplate.update(queryCreator.toString());
    }

    /**
     * Updates the activation state of devices with dummy data.
     *
     * @param userIds        The list of user IDs to filter the devices.
     * @param serialNumbers  The list of serial numbers to filter the devices.
     * @return The number of devices whose activation state was updated.
     */
    public int updateActivationStateWithDummy(final List<String> userIds, List<String> serialNumbers) {

        final StringBuilder queryCreator = new StringBuilder(
            "update device_activation_state set activation_initiated_by='" + UNPREDICTABLE_USER_ID
                + "',deactivation_initiated_by='" + UNPREDICTABLE_USER_ID
                + "',factory_data=(SELECT \"ID\" FROM public.\"DeviceInfoFactoryData\" where state='DUMMY' and "
                + "factory_admin='HCP SCRIPTS'),serial_number='"
                + UNPREDICTABLE_USER_ID + "' where  activation_ready='false'");

        if (serialNumbers != null && !serialNumbers.isEmpty()) {
            queryCreator.append("and serial_number in ('");
            String prefix = "";
            for (String serialNumber : serialNumbers) {
                queryCreator.append(prefix);
                prefix = "','";
                queryCreator.append(serialNumber);
            }
            queryCreator.append("')");

        }
        if (userIds != null && !userIds.isEmpty()) {
            queryCreator.append(" and activation_initiated_by in ('");
            String prefix = "";
            for (String userId : userIds) {
                queryCreator.append(prefix);
                prefix = "','";
                queryCreator.append(userId);
            }
            queryCreator.append("')");
        }
        log.info("created query for device_association userid update: {}", queryCreator.toString());
        log.info("updating the association table with userId {}",
            ((userIds != null && !userIds.isEmpty()) ? userIds.toArray() : null));
        return jdbcTemplate.update(queryCreator.toString());
    }

    /**
     * Retrieves a list of all M2M associations for a given serial number.
     *
     * @param serialNumber The serial number of the device.
     * @return A list of DeviceAssociation objects representing the M2M associations.
     */
    public List<DeviceAssociation> getAllM2Massociations(final String serialNumber) {
        log.info("Inside getAllM2MAssociations method");

        final MapSqlParameterSource mapSqlParameter = new MapSqlParameterSource();
        final StringBuilder queryCreator = new StringBuilder("SELECT * FROM public.device_association da WHERE ");
        queryCreator.append("da.serial_number = :serialNumber ");
        mapSqlParameter.addValue(SERIAL_NUMBER, serialNumber);
        queryCreator.append(AND);
        queryCreator.append("da.association_status IN ('ASSOCIATED')");
        log.info("Query generated :: {}", queryCreator.toString());
        final List<DeviceAssociation> deviceAssociationList =
            namedParamJdbcTemplate.query(queryCreator.toString(), mapSqlParameter,
                new DeviceAssociationRowMapper());
        log.info("Exit constructAndFetchDeviceAssociationData method");
        return deviceAssociationList;

    }

    /**
     * Retrieves a list of DeviceInfo objects based on the provided Harman ID.
     *
     * @param harmandId the Harman ID to search for
     * @return a list of DeviceInfo objects matching the Harman ID
     */
    public List<DeviceInfo> findDeviceInfo(String harmandId) {
        String sql = "select * from \"DeviceInfo\" where \"HarmanID\" = ?";
        return jdbcTemplate.query(sql, new Object[]{harmandId}, new DeviceInfoRowMapper());
    }

    /**
     * Retrieves a list of DeviceInfo objects based on the given Harman ID and names.
     *
     * @param harmandId The Harman ID to search for.
     * @param names     An array of names to filter the results by.
     * @return A list of DeviceInfo objects matching the given Harman ID and names.
     */
    public List<DeviceInfo> findDeviceInfoByName(String harmandId, String[] names) {
        StringBuilder sql = new StringBuilder("select * from \"DeviceInfo\" where \"HarmanID\" = ?");
        if (names != null && names.length > 0) {
            sql.append(" and \"Name\" in ('");
            String prefix = "";
            for (String name : names) {
                sql.append(prefix);
                prefix = "','";
                sql.append(name);

            }
            sql.append("')");
        }

        return jdbcTemplate.query(sql.toString(), new Object[]{harmandId}, new DeviceInfoRowMapper());
    }

    /**
     * Finds a device association based on the given query conditions.
     *
     * @param queryConditionMap A map containing the query conditions.
     * @return The DeviceAssociation object representing the found association, or null if no association is found.
     */
    public DeviceAssociation findAssociation(Map<String, Object> queryConditionMap) {

        String findValidAssociation = "SELECT b.associated_by, b.associated_on, b.association_status, "
            + "b.disassociated_by, b.disassociated_on, b.harman_id, b.modified_by, b.modified_on, b.serial_number, "
            + "b.user_id, b.factory_data, b.id "
            + "FROM public.\"DeviceInfoFactoryData\" a LEFT JOIN device_association b ON (a.\"ID\"=b.factory_data) "
            + "WHERE b.association_status in ('"
            + AssociationStatus.ASSOCIATED.name() + "')" + AND;
        String selectQuery = SqlUtility.getPreparedSql(findValidAssociation, AND, queryConditionMap);
        DeviceAssociation deviceAssociation = null;
        try {
            log.info("###Final findAssociation :{}", selectQuery);
            deviceAssociation = jdbcTemplate.queryForObject(selectQuery, new DeviceAssociationRowMapper(),
                queryConditionMap.values().toArray(new Object[1]));
        } catch (EmptyResultDataAccessException e) {
            log.error("no results found for findAssociation");
        }
        return deviceAssociation;
    }

    /**
     * Checks if the user association is valid based on the given query condition map.
     *
     * @param queryConditionMap a map containing the query conditions
     * @return true if the user association is valid, false otherwise
     */
    public boolean validUserAssociation(Map<String, Object> queryConditionMap) {

        String queryPrefix =
            "select count(*) > 0 from public.\"DeviceInfoFactoryData\" a LEFT JOIN device_association b ON (a"
                + ".\"ID\"=b.factory_data) where b.association_status in ('"
                + AssociationStatus.ASSOCIATED + "')" + " and ";
        String operator = " and ";
        String selectQuery = SqlUtility.getPreparedSql(queryPrefix, operator, queryConditionMap);
        log.info("###Final queryvalidUserAssociation :{}", selectQuery);
        Boolean validUser = jdbcTemplate.queryForObject(selectQuery, Boolean.class,
            queryConditionMap.values().toArray(new Object[1]));
        return validUser != null && validUser;
    }

    /**
     * Updates the association status, disassociation details, and modified information for a device association
     * identified by the device ID.
     *
     * @param deviceAssociation The DeviceAssociation object containing the updated information.
     * @return The number of rows affected by the update operation.
     */
    public int updateForDisassociationByDeviceId(final DeviceAssociation deviceAssociation) {
        return jdbcTemplate.update(UPDATE_ASSOCIATION_FOR_DISASSOCIATION_BY_DEVICE_ID,
            deviceAssociation.getAssociationStatus().name(),
            deviceAssociation.getDisassociatedOn(), deviceAssociation.getDisassociatedBy(),
            deviceAssociation.getModifiedBy(),
            new Timestamp(deviceAssociation.getEndTimeStamp()), deviceAssociation.getHarmanId());
    }

    /**
     * Fetches a device association by its association ID.
     *
     * @param assocId the ID of the association to fetch
     * @return the device association with the specified ID, or null if not found
     */
    public DeviceAssociation fetchAssociationById(long assocId) {
        DeviceAssociation deviceAssociation = null;
        try {
            deviceAssociation =
                jdbcTemplate.queryForObject(FIND_ASSOCIATION_BY_ASSOCIATION_ID, new DeviceAssociationRowMapper(),
                    assocId);
        } catch (EmptyResultDataAccessException e) {
            log.error("no results found for Association ID : {}", assocId);
        }
        return deviceAssociation;
    }

    /**
     * Validates whether the specified user is the owner of the device.
     *
     * @param assocType     the association type
     * @param userId        the user ID
     * @param serialNumber  the serial number of the device
     * @return true if the user is the owner of the device, false otherwise
     */
    public boolean validateUserIsOwnerOfDevice(String assocType, String userId, String serialNumber) {
        Boolean deviceOwner =
            jdbcTemplate.queryForObject(VALIDATE_USER_IS_OWNER_OF_DEVICE, Boolean.class, serialNumber, userId,
                assocType);
        return deviceOwner != null ? deviceOwner : false;
    }

    /**
     * Retrieves the usage count of a specific association type.
     *
     * @param assocType the association type to retrieve the usage count for
     * @return the usage count of the association type
     */
    public Integer getAssociationTypeUsageCount(String assocType) {
        return jdbcTemplate.queryForObject(ASSOCIATION_TYPE_USAGE_COUNT, Integer.class, assocType);
    }

    /**
     * This class is responsible for mapping the result set of a database query to a FactoryData object.
     */
    private static class FactoryDataMapper implements RowMapper<FactoryData> {

        /**
         * Maps a single row of the result set to a FactoryData object.
         *
         * @param rs     the result set containing the data to be mapped
         * @param rowNum the current row number
         * @return the mapped FactoryData object
         * @throws SQLException if a database access error occurs
         */
        @Override
        public FactoryData mapRow(ResultSet rs, int rowNum) throws SQLException {
            log.debug("Inside FactoryDataMapper method");
            FactoryData data = new FactoryData();
            data.setSsid(rs.getString("ssid"));
            data.setIccid(rs.getString("iccid"));
            data.setImei(rs.getString("imei"));
            data.setMsisdn(rs.getString("msisdn"));
            data.setSerialNumber(rs.getString("serial_number"));
            data.setId(rs.getLong(ID));
            data.setBssid(rs.getString("bssid"));
            data.setModel(rs.getString("model"));
            data.setRecordDate(rs.getTimestamp("record_date"));
            data.setPlatformVersion(rs.getString("platform_version"));
            data.setManufacturingDate(rs.getTimestamp("manufacturing_date"));
            data.setImsi(rs.getString("imsi"));
            data.setState(rs.getString("state"));
            data.setFaulty(rs.getBoolean("isfaulty"));
            data.setStolen(rs.getBoolean("isstolen"));
            data.setDeviceType(rs.getString("device_type"));
            log.debug("Exiting FactoryDataMapper method Data:: {} ", data);
            return data;
        }

    }

}