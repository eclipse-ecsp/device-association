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

import org.eclipse.ecsp.userauth.lib.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for UserCommonDao.
 */
public class UserCommonDaoTest {
    public static final long ID_1 = -1L;
    public static final long ID_2 = 2L;
    public static final long ID_3 = 3L;
    public static final long ID_4 = 4L;

    @InjectMocks
    UserCommonDao userCommonDao;

    @Mock
    JdbcTemplate jdbcTemplate;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void findbyUserIdTest() {
        User user = new User();
        user.setUserId("user123");
        user.setFirstName("ABC");
        user.setId(1L);
        user.setLastName("XYZ");
        List<User> users = new ArrayList<>();
        users.add(user);
        String userId = "user123";
        Mockito.doReturn(users).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyObject(), Mockito.any(UserCommonDao.UserMapper.class));
        User actualUser = userCommonDao.findbyUserId(userId);
        Assertions.assertEquals(user, actualUser);
    }

    @Test
    public void findbyUserIdTest_NullUsers() {
        String userId = "user123";
        Mockito.doReturn(null).when(jdbcTemplate)
            .query(Mockito.anyString(), (Object[]) Mockito.anyObject(), Mockito.any(UserCommonDao.UserMapper.class));
        User actualUser = userCommonDao.findbyUserId(userId);
        Assertions.assertNull(actualUser);
    }

    @Test
    public void getOemsOfUserTest() {

        Assertions.assertNotNull(userCommonDao.getOemsOfUser(ID_1));
    }

    @Test
    public void getCreatedByIdListForUserTest() {
        List<Long> oemIds = new ArrayList<>();
        oemIds.add(1L);
        oemIds.add(ID_2);
        List<Long> createdByIds = new ArrayList<>();
        createdByIds.add(ID_3);
        createdByIds.add(ID_4);
        Mockito.doReturn(1L).when(jdbcTemplate).query(Mockito.anyString(), (ResultSetExtractor<Object>) Mockito.any());
        Mockito.doReturn(oemIds).when(jdbcTemplate)
            .queryForList(Mockito.anyString(), Mockito.any(), (Class<Long>) Mockito.any());
        Mockito.doReturn(createdByIds).when(jdbcTemplate)
            .queryForList(Mockito.anyString(), Mockito.any(), Mockito.any(), (Class<Long>) Mockito.any());
        Assertions.assertNotNull(userCommonDao.getCreatedByIdListForUser(1L));
    }

    @Test
    public void getCreatedByIdListForUserTest2() {
        List<Long> oemIds = new ArrayList<>();
        oemIds.add(ID_1);
        oemIds.add(ID_2);
        List<Long> createdByIds = new ArrayList<>();
        createdByIds.add(ID_3);
        createdByIds.add(ID_4);
        Mockito.doReturn(1L).when(jdbcTemplate).query(Mockito.anyString(), (ResultSetExtractor<Object>) Mockito.any());
        Mockito.doReturn(oemIds).when(jdbcTemplate)
            .queryForList(Mockito.anyString(), Mockito.any(), (Class<Long>) Mockito.any());
        Mockito.doReturn(createdByIds).when(jdbcTemplate)
            .queryForList(Mockito.anyString(), Mockito.any(), Mockito.any(), (Class<Long>) Mockito.any());
        Assertions.assertNotNull(userCommonDao.getCreatedByIdListForUser(1L));
    }

    @Test
    public void getCreatedByUserIdListForUserTest() {
        List<Long> oemIds = new ArrayList<>();
        oemIds.add(1L);
        oemIds.add(ID_2);
        List<Long> createdByIds = new ArrayList<>();
        createdByIds.add(ID_3);
        createdByIds.add(ID_4);
        Mockito.doReturn(1L).when(jdbcTemplate).query(Mockito.anyString(), (ResultSetExtractor<Object>) Mockito.any());
        Mockito.doReturn(oemIds).when(jdbcTemplate)
            .queryForList(Mockito.anyString(), Mockito.any(), (Class<Long>) Mockito.any());
        Mockito.doReturn(createdByIds).when(jdbcTemplate)
            .queryForList(Mockito.anyString(), Mockito.any(), Mockito.any(), (Class<Long>) Mockito.any());
        Assertions.assertNotNull(userCommonDao.getCreatedByIdListForUser(1L));
    }

    @Test
    public void getCreatedByUserIdListForUserTest2() {
        List<Long> oemIds = new ArrayList<>();
        oemIds.add(ID_1);
        oemIds.add(ID_2);
        List<Long> createdByIds = new ArrayList<>();
        createdByIds.add(ID_3);
        createdByIds.add(ID_4);
        Mockito.doReturn(1L).when(jdbcTemplate).query(Mockito.anyString(), (ResultSetExtractor<Object>) Mockito.any());
        Mockito.doReturn(oemIds).when(jdbcTemplate)
            .queryForList(Mockito.anyString(), Mockito.any(), (Class<Long>) Mockito.any());
        Mockito.doReturn(createdByIds).when(jdbcTemplate)
            .queryForList(Mockito.anyString(), Mockito.any(), Mockito.any(), (Class<Long>) Mockito.any());
        Assertions.assertNotNull(userCommonDao.getCreatedByIdListForUser(1L));
    }

    @Test
    public void getCreatedByUserIdLongListTest() {
        List<Long> oemIds = new ArrayList<>();
        oemIds.add(1L);
        oemIds.add(ID_2);
        List<Long> createdByIds = new ArrayList<>();
        createdByIds.add(ID_3);
        createdByIds.add(ID_4);
        Mockito.doReturn(1L).when(jdbcTemplate).query(Mockito.anyString(), (ResultSetExtractor<Object>) Mockito.any());
        Mockito.doReturn(oemIds).when(jdbcTemplate)
            .queryForList(Mockito.anyString(), Mockito.any(), (Class<Long>) Mockito.any());
        Mockito.doReturn(createdByIds).when(jdbcTemplate)
            .queryForList(Mockito.anyString(), Mockito.any(), Mockito.any(), (Class<Long>) Mockito.any());
        Assertions.assertNotNull(userCommonDao.getCreatedByUserIdLongList(1L));
    }

    @Test
    public void getCreatedByUserIdLongListTest2() {
        List<Long> oemIds = new ArrayList<>();
        oemIds.add(ID_1);
        oemIds.add(ID_2);
        List<Long> createdByIds = new ArrayList<>();
        createdByIds.add(ID_3);
        createdByIds.add(ID_4);
        Mockito.doReturn(1L).when(jdbcTemplate).query(Mockito.anyString(), (ResultSetExtractor<Object>) Mockito.any());
        Mockito.doReturn(oemIds).when(jdbcTemplate)
            .queryForList(Mockito.anyString(), Mockito.any(), (Class<Long>) Mockito.any());
        Mockito.doReturn(createdByIds).when(jdbcTemplate)
            .queryForList(Mockito.anyString(), Mockito.any(), Mockito.any(), (Class<Long>) Mockito.any());
        Assertions.assertNotNull(userCommonDao.getCreatedByUserIdLongList(1L));
    }

    @Test
    public void addToApprovalQueueTest() {
        User admin = new User();
        admin.setId(1L);
        admin.setUserId("admin1");
        admin.setFirstName("admin");
        User user = new User();
        user.setId(ID_2);
        user.setUserId("user123");
        user.setFirstName("ABC");
        user.setLastName("XYZ");
        List<String> appNames = new ArrayList<>();
        appNames.add("app1");
        appNames.add("app2");
        String url = "url";
        String adminUserIdsString = "admin101";
        Mockito.doReturn(1).when(jdbcTemplate).update(Mockito.anyString(), Mockito.any(), Mockito.any());
        userCommonDao.addToApprovalQueue(admin, user, appNames, url, adminUserIdsString);
        Assertions.assertEquals(1, jdbcTemplate.update(Mockito.anyString(), Mockito.any(), Mockito.any()));
    }
}