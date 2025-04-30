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

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for DeviceInfoFactoryDataMapperWithSubscription.
 */
public class DeviceInfoFactoryDataMapperWithSubscriptionTest {
    public static final int ROW = 5;
    public static final int ROW_2 = 10;
    @InjectMocks
    private DeviceInfoFactoryDataMapperWithSubscription deviceInfoFactoryDataMapperWithSubscription;

    @Mock
    private ResultSet resultSet;

    @Mock
    private ResultSetMetaData resultSetMetaData;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void mapRowTest1() throws SQLException {
        int rowNum = ROW;
        Mockito.doReturn(resultSetMetaData).when(resultSet).getMetaData();
        assertNotNull(deviceInfoFactoryDataMapperWithSubscription.mapRow(resultSet, rowNum));
    }

    @Test
    public void mapRowTest2() throws SQLException {
        Mockito.doReturn(ROW).when(resultSetMetaData).getColumnCount();
        Mockito.doReturn(false).when(resultSetMetaData).isAutoIncrement(Mockito.anyInt());
        Mockito.doReturn(false).when(resultSetMetaData).isCaseSensitive(Mockito.anyInt());
        Mockito.doReturn(false).when(resultSetMetaData).isSearchable(Mockito.anyInt());
        Mockito.doReturn(false).when(resultSetMetaData).isCurrency(Mockito.anyInt());
        Mockito.doReturn(0).when(resultSetMetaData).isNullable(Mockito.anyInt());
        Mockito.doReturn(false).when(resultSetMetaData).isSigned(Mockito.anyInt());
        Mockito.doReturn(ROW).when(resultSetMetaData).getColumnDisplaySize(Mockito.anyInt());
        Mockito.doReturn("label1").when(resultSetMetaData).getColumnLabel(Mockito.anyInt());
        Mockito.doReturn("harman_id").when(resultSetMetaData).getColumnName(Mockito.anyInt());
        Mockito.doReturn(null).when(resultSetMetaData).getSchemaName(Mockito.anyInt());
        Mockito.doReturn(0).when(resultSetMetaData).getPrecision(Mockito.anyInt());
        Mockito.doReturn(0).when(resultSetMetaData).getScale(Mockito.anyInt());
        Mockito.doReturn(null).when(resultSetMetaData).getTableName(Mockito.anyInt());
        Mockito.doReturn(null).when(resultSetMetaData).getCatalogName(Mockito.anyInt());
        Mockito.doReturn(0).when(resultSetMetaData).getColumnType(Mockito.anyInt());
        Mockito.doReturn(null).when(resultSetMetaData).getColumnTypeName(Mockito.anyInt());
        Mockito.doReturn(false).when(resultSetMetaData).isReadOnly(Mockito.anyInt());
        Mockito.doReturn(false).when(resultSetMetaData).isWritable(Mockito.anyInt());
        Mockito.doReturn(false).when(resultSetMetaData).isDefinitelyWritable(Mockito.anyInt());
        Mockito.doReturn(null).when(resultSetMetaData).getColumnClassName(Mockito.anyInt());
        Mockito.doReturn(null).when(resultSetMetaData).unwrap(Mockito.any());
        Mockito.doReturn(false).when(resultSetMetaData).isWrapperFor(Mockito.any());
        Mockito.doReturn(resultSetMetaData).when(resultSet).getMetaData();
        assertNotNull(deviceInfoFactoryDataMapperWithSubscription.mapRow(resultSet, ROW));
    }

    @Test
    public void mapRowTest3() throws SQLException {
        Mockito.doReturn(ROW_2).when(resultSetMetaData).getColumnCount();
        Mockito.doReturn(false).when(resultSetMetaData).isAutoIncrement(Mockito.anyInt());
        Mockito.doReturn(false).when(resultSetMetaData).isCaseSensitive(Mockito.anyInt());
        Mockito.doReturn(false).when(resultSetMetaData).isSearchable(Mockito.anyInt());
        Mockito.doReturn(false).when(resultSetMetaData).isCurrency(Mockito.anyInt());
        Mockito.doReturn(0).when(resultSetMetaData).isNullable(Mockito.anyInt());
        Mockito.doReturn(false).when(resultSetMetaData).isSigned(Mockito.anyInt());
        Mockito.doReturn(ROW).when(resultSetMetaData).getColumnDisplaySize(Mockito.anyInt());
        Mockito.doReturn("label1").when(resultSetMetaData).getColumnLabel(Mockito.anyInt());
        Mockito.doReturn("vin").when(resultSetMetaData).getColumnName(Mockito.anyInt());
        Mockito.doReturn(null).when(resultSetMetaData).getSchemaName(Mockito.anyInt());
        Mockito.doReturn(0).when(resultSetMetaData).getPrecision(Mockito.anyInt());
        Mockito.doReturn(0).when(resultSetMetaData).getScale(Mockito.anyInt());
        Mockito.doReturn(null).when(resultSetMetaData).getTableName(Mockito.anyInt());
        Mockito.doReturn(null).when(resultSetMetaData).getCatalogName(Mockito.anyInt());
        Mockito.doReturn(0).when(resultSetMetaData).getColumnType(Mockito.anyInt());
        Mockito.doReturn(null).when(resultSetMetaData).getColumnTypeName(Mockito.anyInt());
        Mockito.doReturn(false).when(resultSetMetaData).isReadOnly(Mockito.anyInt());
        Mockito.doReturn(false).when(resultSetMetaData).isWritable(Mockito.anyInt());
        Mockito.doReturn(false).when(resultSetMetaData).isDefinitelyWritable(Mockito.anyInt());
        Mockito.doReturn(null).when(resultSetMetaData).getColumnClassName(Mockito.anyInt());
        Mockito.doReturn(null).when(resultSetMetaData).unwrap(Mockito.any());
        Mockito.doReturn(false).when(resultSetMetaData).isWrapperFor(Mockito.any());
        Mockito.doReturn(resultSetMetaData).when(resultSet).getMetaData();
        assertNotNull(deviceInfoFactoryDataMapperWithSubscription.mapRow(resultSet, ROW_2));
    }

    @Test
    public void getDeviceInfoFactoryDataWithSubscriptionMapperTest() {

        DeviceInfoFactoryDataMapperWithSubscription deviceInfoFactoryDataMapperWithSubscription =
                DeviceInfoFactoryDataMapperWithSubscription.getDeviceInfoFactoryDataWithSubscriptionMapper();
        assertNotNull(deviceInfoFactoryDataMapperWithSubscription);
    }

}
