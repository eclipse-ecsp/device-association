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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * The SqlUtility class provides utility methods for working with SQL queries and operations.
 * It contains static methods for generating prepared SQL statements, retrieving array values from maps,
 * preparing like queries, preparing range queries, preparing order by queries, and more.
 */
@Slf4j
public abstract class SqlUtility {
    public static final int RANGE = 2;
    private static final String ORDER_BY = " ORDER BY \"";

    /**
     * Private constructor to prevent instantiation of the {@code SqlUtility} class.
     */
    private SqlUtility() {

    }

    /**
     * Constructs a prepared SQL statement based on the given prefix, operator, and attribute-value map.
     *
     * @param prefix The prefix to be added to the SQL statement.
     * @param operator The operator to be used in the SQL statement.
     * @param attributeValueMap The map containing attribute-value pairs.
     * @return The prepared SQL statement.
     */
    public static String getPreparedSql(String prefix, String operator, Map<String, Object> attributeValueMap) {
        String sql;
        if (attributeValueMap == null || attributeValueMap.isEmpty() || prefix == null || prefix.isEmpty()
            || operator == null
            || operator.isEmpty()) {
            log.error("getPreparedSql:attributeValueMap or prefix or operator can not be null or empty");
            return null;
        }
        int count = 0;
        StringBuilder preparedSuffix = new StringBuilder();
        for (String attribute : attributeValueMap.keySet()) {
            if (count == 0) {
                preparedSuffix.append(attribute).append(" = ? ").toString();
            } else {
                preparedSuffix.append(operator).append(attribute).append(" = ?").toString();
            }
            count++;
            log.debug("preparedSuffix:::{}", preparedSuffix);
        }
        sql = prefix + preparedSuffix;
        log.debug("finalSQl: {}", sql);
        return sql;
    }

    /**
     * Retrieves the values from the provided ordered map and returns them as an array.
     *
     * @param orderedMap the ordered map containing the values to retrieve
     * @return an array containing the values from the ordered map
     * @throws NullPointerException if the provided ordered map is null or empty
     */
    public static Object[] getArrayValues(Map<String, Object> orderedMap) {
        if (orderedMap == null || orderedMap.isEmpty()) {
            log.error("getArrayValues:orderedMap can not be null or empty");
            return ArrayUtils.EMPTY_OBJECT_ARRAY;
        }
        Object[] array = new Object[orderedMap.size()];
        int i = 0;

        for (Entry<String, Object> attribute : orderedMap.entrySet()) {
            array[i] = attribute.getValue();
            i++;
        }
        log.debug("Values from Map: {}", orderedMap);
        return array;
    }

    /**
     * Converts a list of maps into an array of values.
     *
     * @param orderedMapList the list of maps to be converted
     * @return an array of values extracted from the maps
     */
    public static Object[] getArrayValues(List<Map<String, Object>> orderedMapList) {
        int length = 0;
        if (null == orderedMapList || orderedMapList.isEmpty()) {
            return ArrayUtils.EMPTY_OBJECT_ARRAY;
        }
        for (Map<String, Object> orderedMap : orderedMapList) {
            length = length + orderedMap.size();
        }

        Object[] array = new Object[length];
        int index = 0;

        for (Map<String, Object> orderedMap : orderedMapList) {
            for (Entry<String, Object> attribute : orderedMap.entrySet()) {
                array[index] = attribute.getValue();
                index++;
            }
        }
        return array;
    }

    /**
     * Prepares a SQL query with LIKE conditions based on the provided field and value lists.
     *
     * @param containsLikeFieldList A list of fields to be used in the LIKE conditions.
     * @param containsLikeValueList A list of values to be used in the LIKE conditions.
     * @return The prepared SQL query as a string.
     */
    public static String prepareLikeQuery(List<String> containsLikeFieldList, List<String> containsLikeValueList) {
        if (containsLikeFieldList == null || containsLikeValueList == null || containsLikeFieldList.isEmpty()
            || containsLikeValueList.isEmpty()) {
            return "";
        }
        if (containsLikeFieldList.size() != containsLikeValueList.size()) {
            log.warn("containsLikeFieldList and containsLikeValueList should be of same size");
            return "";
        }
        StringBuilder stringQueryFilter = null;
        for (int i = 0; i < containsLikeFieldList.size(); i++) {
            String field = containsLikeFieldList.get(i);
            String value = containsLikeValueList.get(i);
            if (stringQueryFilter == null) {
                stringQueryFilter = new StringBuilder();
            } else {
                stringQueryFilter.append(" and ");
            }
            stringQueryFilter.append(" \"" + field + "\" LIKE '%");
            stringQueryFilter.append(value.replace("'", "''"));
            stringQueryFilter.append("%' ");
        }
        return stringQueryFilter.toString();
    }

    /**
     * Prepares a range query based on the given range field list and range value list.
     *
     * @param rangeFieldList  the list of range fields
     * @param rangeValueList  the list of range values
     * @return the prepared range query as a string
     */
    public static String prepareRangeQuery(List<String> rangeFieldList, List<String> rangeValueList) {
        if (rangeFieldList == null || rangeValueList == null || rangeFieldList.isEmpty() || rangeValueList.isEmpty()) {
            return "";
        }
        if (rangeFieldList.size() != rangeValueList.size()) {
            log.warn("rangeFieldList and rangeValueList should be of same size");
            return "";
        }
        StringBuilder stringQueryFilter = null;
        for (int i = 0; i < rangeFieldList.size(); i++) {
            String field = rangeFieldList.get(i);
            String range = rangeValueList.get(i);
            if (range.split(SharedConstants.UNDERSCORE).length != RANGE) {
                log.warn("Inavalid range value can not be used in query:{}", range);
                continue;
            }
            String start = range.split(SharedConstants.UNDERSCORE)[0];
            String end = range.split(SharedConstants.UNDERSCORE)[1];
            if (stringQueryFilter == null) {
                stringQueryFilter = new StringBuilder();
            } else {
                stringQueryFilter.append(" and ");
            }
            stringQueryFilter.append(
                " \"" + field + "\" >= TO_TIMESTAMP(" + start + "/1000) and \"" + field + "\" <= TO_TIMESTAMP(" + end
                    + "/1000) ");
        }
        log.debug("prepareRangeQuery:{}", stringQueryFilter.toString());
        return stringQueryFilter.toString();
    }

    /**
     * Prepares an SQL query string with an ORDER BY clause based on the provided sorting order and sort by column.
     *
     * @param sortingOrder The sorting order, either "asc" for ascending or "desc" for descending.
     * @param sortBy The column to sort by.
     * @return The SQL query string with the ORDER BY clause.
     */
    public static String prepareOrderByQuery(String sortingOrder, String sortBy) {
        StringBuilder queryOrderBy = new StringBuilder();

        if (StringUtils.isNotEmpty(sortBy) && StringUtils.isNotEmpty(sortingOrder)
            && sortingOrder.equalsIgnoreCase("asc")) {
            queryOrderBy.append(ORDER_BY);
            queryOrderBy.append(sortBy);
            queryOrderBy.append("\" COLLATE \"C\" ASC ");
        } else if (StringUtils.isNotEmpty(sortBy) && StringUtils.isNotEmpty(sortingOrder)
            && sortingOrder.equalsIgnoreCase("desc")) {
            queryOrderBy.append(ORDER_BY);
            queryOrderBy.append(sortBy);
            queryOrderBy.append("\" COLLATE \"C\" DESC ");
        } else {
            queryOrderBy.append(" ORDER BY \"ID\" ASC ");
        }

        return queryOrderBy.toString();
    }

    /**
     * Splits the given parameter value into a list of strings using the specified separator.
     *
     * @param paramValue The parameter value to be split.
     * @param separator The separator to be used for splitting the parameter value.
     * @return A list of strings obtained by splitting the parameter value using the separator.
     */
    public static List<String> getList(String paramValue, String separator) {
        if (StringUtils.isEmpty(paramValue)) {
            return Collections.emptyList();
        }
        paramValue = paramValue.replace(" ", "");
        return new ArrayList<>(Arrays.asList(paramValue.split(separator)));

    }

    /**
     * Prepares the SQL query for sorting the result set based on the given sort by and order by parameters.
     *
     * @param sortBy  the field to sort by
     * @param orderBy the order in which to sort the field (asc or desc)
     * @return the SQL query for sorting the result set
     */
    public static String prepareSortByAndOrderByQuery(String sortBy, String orderBy) {
        StringBuilder queryOrderBy = new StringBuilder();

        if (StringUtils.isNotEmpty(sortBy) && StringUtils.isNotEmpty(orderBy)) {
            queryOrderBy.append(ORDER_BY);
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
     * Prepares a SQL query string with the specified sort by and order by parameters.
     *
     * @param sortBy  the column name to sort by
     * @param orderBy the sort order ("asc" for ascending, "desc" for descending)
     * @param table   the name of the table to sort
     * @return the SQL query string with the sort by and order by parameters
     */
    public static String prepareSortByAndOrderByQuery(String sortBy, String orderBy, String table) {
        StringBuilder queryOrderBy = new StringBuilder();

        if (StringUtils.isNotEmpty(sortBy) && StringUtils.isNotEmpty(orderBy)) {
            queryOrderBy.append(ORDER_BY + table + "\"" + ".\"");
            queryOrderBy.append(sortBy);
            if ("asc".equalsIgnoreCase(orderBy)) {
                queryOrderBy.append("\" ASC ");
            } else {
                queryOrderBy.append("\" DESC ");
            }
        }
        return queryOrderBy.toString();
    }

}