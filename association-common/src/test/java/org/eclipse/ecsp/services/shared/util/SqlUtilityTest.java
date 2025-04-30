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

package org.eclipse.ecsp.services.shared.util;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Test class for SqlUtility.
 */
public class SqlUtilityTest {

    private static final String STATE = "PROVISIONED";
    private static final String IMEI = "123";
    private static final String SERIAL_NUMBER = "009";

    @Test
    public void getPreparedSqlNullCheck() {
        assertNull(SqlUtility.getPreparedSql(null, null, Collections.emptyMap()));
    }

    @Test
    public void getPreparedSql() {
        Map<String, Object> orderedMap = new LinkedHashMap<>();
        orderedMap.put("state", STATE);
        if (!StringUtils.isEmpty(IMEI)) {
            orderedMap.put("imei", IMEI);
        }
        if (!StringUtils.isEmpty(SERIAL_NUMBER)) {
            orderedMap.put("serial_number", SERIAL_NUMBER);
        }
        String prefix = "delete from  public.\"device_association\" where ";
        String operator = " and ";
        assertNotNull(SqlUtility.getPreparedSql(prefix, operator, orderedMap));
    }

    @Test
    public void getArrayValuesMapNullCheck() {
        assertNotNull(SqlUtility.getArrayValues((Map<String, Object>) null));
    }

    @Test
    public void getArrayValuesListNullCheck() {
        assertNotNull(SqlUtility.getArrayValues((List<Map<String, Object>>) null));
    }

    @Test
    public void getArrayValuesMapEmptyCheck() {
        assertNotNull(SqlUtility.getArrayValues(Collections.emptyMap()));
    }

    @Test
    public void getArrayValuesListEmptyCheck() {
        assertNotNull(SqlUtility.getArrayValues(Collections.emptyList()));
    }

    @Test
    public void getArrayValuesMap() {
        Map<String, Object> orderedMap = new LinkedHashMap<>();
        orderedMap.put("state", STATE);
        if (!StringUtils.isEmpty(IMEI)) {
            orderedMap.put("imei", IMEI);
        }
        if (!StringUtils.isEmpty(SERIAL_NUMBER)) {
            orderedMap.put("serial_number", SERIAL_NUMBER);
        }
        assertNotNull(SqlUtility.getArrayValues(orderedMap));
    }

    @Test
    public void getArrayValuesList() {
        Map<String, Object> orderedMap = new LinkedHashMap<>();
        orderedMap.put("state", STATE);
        if (!StringUtils.isEmpty(IMEI)) {
            orderedMap.put("imei", IMEI);
        }
        if (!StringUtils.isEmpty(SERIAL_NUMBER)) {
            orderedMap.put("serial_number", SERIAL_NUMBER);
        }
        List<Map<String, Object>> orderedMapList = new ArrayList<>();
        orderedMapList.add(orderedMap);
        assertNotNull(SqlUtility.getArrayValues(orderedMapList));
    }

    @Test
    public void prepareLikeQueryEmptyCheck() {
        assertEquals(StringUtils.EMPTY, SqlUtility.prepareLikeQuery(Collections.emptyList(), Collections.emptyList()));
    }

    @Test
    public void prepareLikeQueryNotSameListSize() {
        List<String> containsLikeFieldList = SqlUtility.getList("a", SharedConstants.COMMA);
        List<String> containsLikeValueList = SqlUtility.getList("1,2", SharedConstants.COMMA);
        assertEquals(StringUtils.EMPTY, SqlUtility.prepareLikeQuery(containsLikeFieldList, containsLikeValueList));
    }

    @Test
    public void prepareLikeQuery() {
        List<String> containsLikeFieldList = SqlUtility.getList("a,b", SharedConstants.COMMA);
        List<String> containsLikeValueList = SqlUtility.getList("1,2", SharedConstants.COMMA);
        assertNotNull(SqlUtility.prepareLikeQuery(containsLikeFieldList, containsLikeValueList));
    }

    @Test
    public void prepareRangeQueryEmptyCheck() {
        assertEquals(StringUtils.EMPTY, SqlUtility.prepareRangeQuery(Collections.emptyList(), Collections.emptyList()));
    }

    @Test
    public void prepareRangeQueryNotSameListSize() {
        List<String> rangeFieldList = SqlUtility.getList("a_b,c_d", SharedConstants.COMMA);
        List<String> rangeValueList = SqlUtility.getList("1_1", SharedConstants.COMMA);
        assertEquals(StringUtils.EMPTY, SqlUtility.prepareRangeQuery(rangeFieldList, rangeValueList));
    }

    @Test(expected = NullPointerException.class)
    public void prepareRangeQueryOutOfRange() {
        List<String> rangeFieldList = SqlUtility.getList("a_b", SharedConstants.COMMA);
        List<String> rangeValueList = SqlUtility.getList("1_1_1", SharedConstants.COMMA);
        SqlUtility.prepareRangeQuery(rangeFieldList, rangeValueList);
    }

    @Test
    public void prepareRangeQuery() {
        List<String> rangeFieldList = SqlUtility.getList("a_b", SharedConstants.COMMA);
        List<String> rangeValueList = SqlUtility.getList("1_2", SharedConstants.COMMA);
        assertNotNull(SqlUtility.prepareRangeQuery(rangeFieldList, rangeValueList));
    }

    @Test
    public void prepareOrderByQueryNull() {
        assertNotNull(SqlUtility.prepareOrderByQuery(StringUtils.EMPTY, StringUtils.EMPTY));
    }

    @Test
    public void prepareOrderByQuerySortByAsc() {
        assertNotNull(SqlUtility.prepareOrderByQuery("asc", "imei"));
    }

    @Test
    public void prepareOrderByQuerySortByDesc() {
        assertNotNull(SqlUtility.prepareOrderByQuery("desc", "imei"));
    }

    @Test
    public void prepareSortByAndOrderByQueryEmpty() {
        assertEquals(StringUtils.EMPTY, StringUtils.EMPTY,
            SqlUtility.prepareSortByAndOrderByQuery(StringUtils.EMPTY, StringUtils.EMPTY));
    }

    @Test
    public void prepareSortByAndOrderByQuerySortByAsc() {
        assertNotNull(SqlUtility.prepareSortByAndOrderByQuery("imei", "asc"));
    }

    @Test
    public void prepareSortByAndOrderByQuerySortByDesc() {
        assertNotNull(SqlUtility.prepareSortByAndOrderByQuery("imei", "desc"));
    }

    @Test
    public void prepareSortByAndOrderByQuerySortBy() {
        assertNotNull(SqlUtility.prepareSortByAndOrderByQuery("imei", "order"));
    }

    @Test
    public void prepareSortByAndOrderByQueryEmptyInput() {
        assertNotNull(SqlUtility.prepareSortByAndOrderByQuery(StringUtils.EMPTY, StringUtils.EMPTY));
    }

    @Test
    public void prepareSortByAndOrderByQuerySortByAscOrder() {
        assertNotNull(SqlUtility.prepareSortByAndOrderByQuery("imei", "asc", "table"));
    }

    @Test
    public void prepareSortByAndOrderByQuerySortByDescOrder() {
        assertNotNull(SqlUtility.prepareSortByAndOrderByQuery("imei", "desc", "table"));
    }

    @Test
    public void prepareSortByAndOrderByQuerySortByDummyOrder() {
        assertNotNull(SqlUtility.prepareSortByAndOrderByQuery("imei", "order", "table"));
    }

    @Test
    public void getList() {
        assertEquals(Collections.EMPTY_LIST, SqlUtility.getList(StringUtils.EMPTY, SharedConstants.COMMA));
    }
}