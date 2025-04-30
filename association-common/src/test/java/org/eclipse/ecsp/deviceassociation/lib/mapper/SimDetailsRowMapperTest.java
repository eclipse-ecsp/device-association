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

package org.eclipse.ecsp.deviceassociation.lib.mapper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for SimDetailsRowMapper.
 */
public class SimDetailsRowMapperTest {
    public static final int ROW = 2;

    @InjectMocks
    private SimDetailsRowMapper simDetailsRowMapper;

    @Mock
    private ResultSet resultSet;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void mapRowTest() throws SQLException {
        int rowNum = ROW;
        assertNotNull(simDetailsRowMapper.mapRow(resultSet, rowNum));
    }
}