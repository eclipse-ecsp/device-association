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

package org.eclipse.ecsp.userauth.lib.dao;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.ecsp.userauth.lib.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.List;

import static org.eclipse.ecsp.common.CommonConstants.EMAIL;
import static org.eclipse.ecsp.common.CommonConstants.FIRST_NAME;
import static org.eclipse.ecsp.common.CommonConstants.ID;
import static org.eclipse.ecsp.common.CommonConstants.LAST_NAME;
import static org.eclipse.ecsp.common.CommonConstants.SELECT_OEMID_WHERE_USERID;
import static org.eclipse.ecsp.common.CommonConstants.SELECT_USERID_GROUP_BY_USERID;
import static org.eclipse.ecsp.common.CommonConstants.SELECT_USER_ID_FROM_USER_OEM;
import static org.eclipse.ecsp.common.CommonConstants.USER_ID;

/**
 * This class represents a data access object for the User table.
 * It provides methods to retrieve user details based on user ID,
 * get OEMs associated with a user, get created by ID list for a user,
 * get created by user ID list for a user, get created by user ID long list,
 * and insert into the user approval queue.
 */
@Configurable
@Component
@Slf4j
public class UserCommonDao {
    /**
     * A constant representing a default return value.
     * This value is typically used to indicate an error or a special condition
     * where no valid result could be returned.
     */
    public static final long RETURN_VALUE = -1L;

    /**
     * A constant representing the default index value.
     * This is typically used as a placeholder or an indicator
     * for an uninitialized or invalid index.
     */
    public static final int INDEX = -1;
    private static final String INTERSECT = " intersect ";
    private static final String UNION = " union ";
    private static final String SELECT_ID_FROM_OEM = "select \"ID\" from \"OEM\" where \"Name\"='Harman'";
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * Retrieves a user by their user ID.
     *
     * @param userId The user ID to search for.
     * @return The User object if found, or null if not found.
     */
    public User findbyUserId(String userId) {

        log.info("UserCommonDAO: findbyUserId for userId = {}", userId);
        User user = null;
        String sql = "select * from public.\"User\" where \"UserID\" = ? and \"IsValid\"=true";
        List<User> users = jdbcTemplate.query(sql, new Object[]{userId}, new UserMapper());
        if (users != null && !users.isEmpty()) {
            log.info("findByID: user:{}", users);
            user = users.get(0);
        }
        log.info("UserCommonDAO: findbyUserId: end");
        return user;


    }

    /**
     * Retrieves the list of OEMs associated with a user.
     *
     * @param id The ID of the user.
     * @return The list of OEM IDs associated with the user.
     */
    public List<Long> getOemsOfUser(long id) {
        return jdbcTemplate.queryForList(SELECT_OEMID_WHERE_USERID, new Object[]{id}, Long.class);
    }

    /**
     * Retrieves a list of user IDs for the users who created the specified user.
     *
     * @param id The ID of the user.
     * @return A list of user IDs for the users who created the specified user.
     */
    public List<Long> getCreatedByIdListForUser(long id) {
        List<Long> createdByIds = null;
        List<Long> oemIds = null;
        Long oemId = jdbcTemplate.query(SELECT_ID_FROM_OEM, new ResultSetExtractor<Long>() {

            @Override
            public Long extractData(ResultSet rs) throws SQLException {
                if (rs != null && rs.next()) {
                    return rs.getLong(1);
                }
                return RETURN_VALUE;
            }
        });
        long harmanOemId = oemId != null ? oemId : 0;
        oemIds = jdbcTemplate.queryForList(SELECT_OEMID_WHERE_USERID, new Object[]{id}, Long.class);

        if (oemIds.indexOf(harmanOemId) != INDEX) {
            String forHarmanUserSql = SELECT_USER_ID_FROM_USER_OEM + SELECT_OEMID_WHERE_USERID + ")  " 
                +                UNION
                +                SELECT_USERID_GROUP_BY_USERID;
            createdByIds =
                jdbcTemplate.queryForList(forHarmanUserSql, new Object[]{id}, new int[]{Types.BIGINT}, Long.class);
        } else {
            String forOemUserSql = SELECT_USER_ID_FROM_USER_OEM + SELECT_OEMID_WHERE_USERID + ")  " 
                +                INTERSECT
                +                SELECT_USERID_GROUP_BY_USERID;
            createdByIds =
                jdbcTemplate.queryForList(forOemUserSql, new Object[]{id}, new int[]{Types.BIGINT}, Long.class);
        }
        log.info("getCreatedByIdListForUser for user with ID = {} = {}", id, createdByIds);
        return createdByIds;
    }

    /**
     * Retrieves a list of user IDs who created the user with the specified ID.
     *
     * @param id The ID of the user.
     * @return A list of user IDs who created the user.
     */
    public List<String> getCreatedByUserIdListForUser(long id) {
        List<String> createdByUserIds = null;
        List<Long> oemIds = null;
        Long oemId = jdbcTemplate.query(SELECT_ID_FROM_OEM, new ResultSetExtractor<Long>() {

            @Override
            public Long extractData(ResultSet rs) throws SQLException {
                if (rs != null && rs.next()) {
                    return rs.getLong(1);
                }
                return RETURN_VALUE;
            }
        });
        long harmanOemId = oemId != null ? oemId : 0;
        oemIds = jdbcTemplate.queryForList(SELECT_OEMID_WHERE_USERID, new Object[]{id}, Long.class);

        if (oemIds.indexOf(harmanOemId) != INDEX) {
            String forHarmanUserSql = "SELECT \"UserID\" FROM \"User\" WHERE \"ID\" IN (" 
                +                SELECT_USER_ID_FROM_USER_OEM + SELECT_OEMID_WHERE_USERID + ")  " 
                +                UNION
                +                SELECT_USERID_GROUP_BY_USERID 
                +                ")";
            createdByUserIds =
                jdbcTemplate.queryForList(forHarmanUserSql, new Object[]{id}, new int[]{Types.BIGINT}, String.class);
        } else {
            String forOemUserSql = "SELECT \"UserID\" FROM \"User\" WHERE \"ID\" IN (" 
                +                SELECT_USER_ID_FROM_USER_OEM + SELECT_OEMID_WHERE_USERID + ")  " 
                +                INTERSECT
                +                SELECT_USERID_GROUP_BY_USERID 
                +                ")";
            createdByUserIds =
                jdbcTemplate.queryForList(forOemUserSql, new Object[]{id}, new int[]{Types.BIGINT}, String.class);
        }
        log.info("getCreatedByUserIdListForUser for user with ID = {} = {}", id, createdByUserIds);
        return createdByUserIds;
    }

    /**
     * Retrieves a list of user IDs who created the specified user with the given ID.
     *
     * @param id The ID of the user.
     * @return A list of user IDs who created the specified user.
     */
    public List<Long> getCreatedByUserIdLongList(long id) {
        List<Long> createdByUserIds = null;
        List<Long> oemIds = null;
        Long oemId = jdbcTemplate.query(SELECT_ID_FROM_OEM, new ResultSetExtractor<Long>() {

            @Override
            public Long extractData(ResultSet rs) throws SQLException {
                if (rs != null && rs.next()) {
                    return rs.getLong(1);
                }
                return RETURN_VALUE;
            }
        });
        long harmanOemId = oemId != null ? oemId : 0;
        oemIds = jdbcTemplate.queryForList(SELECT_OEMID_WHERE_USERID, new Object[]{id}, Long.class);

        if (oemIds.indexOf(harmanOemId) != INDEX) {
            String forHarmanUserSql = "SELECT \"ID\" FROM \"User\" WHERE \"ID\" IN (" 
                +                SELECT_USER_ID_FROM_USER_OEM + SELECT_OEMID_WHERE_USERID + ")  " 
                +                UNION
                +                SELECT_USERID_GROUP_BY_USERID 
                +                ")";
            createdByUserIds =
                jdbcTemplate.queryForList(forHarmanUserSql, new Object[]{id}, new int[]{Types.BIGINT}, Long.class);
        } else {
            String forOemUserSql = "SELECT \"ID\" FROM \"User\" WHERE \"ID\" IN (" 
                +                SELECT_USER_ID_FROM_USER_OEM + SELECT_OEMID_WHERE_USERID + ")  " 
                +                INTERSECT
                +                SELECT_USERID_GROUP_BY_USERID 
                +                ")";
            createdByUserIds =
                jdbcTemplate.queryForList(forOemUserSql, new Object[]{id}, new int[]{Types.BIGINT}, Long.class);
        }
        log.info("getCreatedByUserIdListForUser for user with ID = {} = {}", id, createdByUserIds);
        return createdByUserIds;
    }

    /**
     * Adds a user to the approval queue with the specified details.
     *
     * @param admin              The admin user who is adding the user to the approval queue.
     * @param user               The user to be added to the approval queue.
     * @param appNames           The list of application names requested by the user.
     * @param url                The approval URL for the user.
     * @param adminUserIdsString The string representation of admin user IDs.
     */
    public void addToApprovalQueue(User admin, User user, List<String> appNames, String url,
                                   String adminUserIdsString) {
        String sql =
            "INSERT INTO user_approval_queue(admin_user_id,admin_user_name,user_id,user_name,first_name,last_name," 
                +                "approve_url,admin_info,apps_requested,created_at) values (?,?,?,?,?,?,?,?,?,?)";
        Timestamp timestamp = new Timestamp(new Date().getTime());
        int rows = jdbcTemplate.update(sql,
            new Object[]{admin.getId(), admin.getUserId(), user.getId(), user.getUserId(), user.getFirstName(),
                user.getLastName(), url, adminUserIdsString, appNames.toString(), timestamp},
            new int[]{Types.BIGINT, Types.VARCHAR, Types.BIGINT, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
                Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP});
        log.debug("addToApprovalQueue : Updated {} rows", rows);

    }

    /**
     * This class is responsible for mapping the result set of a database query to a User object.
     */
    static class UserMapper implements RowMapper<User> {

        /**
         * Maps a row from the ResultSet to a User object.
         *
         * @param rs     the ResultSet containing the data
         * @param rowNum the row number
         * @return the mapped User object
         * @throws SQLException if a database access error occurs
         */
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setUserId(rs.getString(USER_ID) != null ? rs.getString(USER_ID) : "");
            user.setFirstName(rs.getString(FIRST_NAME) != null ? rs.getString(FIRST_NAME) : "");
            user.setLastName(rs.getString(LAST_NAME) != null ? rs.getString(LAST_NAME) : "");
            user.setId(rs.getLong(ID));
            user.setPassword(rs.getString("Password") != null ? rs.getString("Password") : "");
            user.setValid(rs.getBoolean("IsValid"));
            user.setEmail(rs.getString(EMAIL) != null ? rs.getString(EMAIL) : "");
            user.setChangePasswordSerial(rs.getLong("ChangePasswordSerial"));
            return user;
        }
    }

    /**
     * Mapper class for mapping a ResultSet to a User object.
     */
    static class UserInfoMapper implements RowMapper<User> {

        /**
         * Maps a row of the ResultSet to a User object.
         *
         * @param rs The ResultSet containing the data for the current row.
         * @param rowNum The row number of the current row.
         * @return The User object mapped from the ResultSet row.
         * @throws SQLException If an error occurs while accessing the ResultSet.
         */
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setUserId(rs.getString(USER_ID) != null ? rs.getString(USER_ID) : "");
            user.setFirstName(rs.getString(FIRST_NAME) != null ? rs.getString(FIRST_NAME) : "");
            user.setLastName(rs.getString(LAST_NAME) != null ? rs.getString(LAST_NAME) : "");
            user.setId(rs.getLong(ID));
            user.setEmail(rs.getString(EMAIL) != null ? rs.getString(EMAIL) : "");
            return user;
        }
    }
}
