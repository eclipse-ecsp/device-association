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

package org.eclipse.ecsp.services.shared.db;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * The Filter class provides utility methods to build a WHERE clause for filtering data.
 */
@Slf4j
public class Filter {
    /**
     * The format string for timestamps used in the application.
     *
     * <p>This format follows the pattern "yyyy-MM-dd HH:mm:ss.SSS Z", where:
     * <ul>
     *   <li><code>yyyy</code> - Year in four digits</li>
     *   <li><code>MM</code> - Month of the year (01-12)</li>
     *   <li><code>dd</code> - Day of the month (01-31)</li>
     *   <li><code>HH</code> - Hour of the day in 24-hour format (00-23)</li>
     *   <li><code>mm</code> - Minutes (00-59)</li>
     *   <li><code>ss</code> - Seconds (00-59)</li>
     *   <li><code>SSS</code> - Milliseconds (000-999)</li>
     *   <li><code>Z</code> - Time zone offset from UTC</li>
     * </ul>
     * This format is commonly used for precise timestamp representation
     * including date, time, and time zone information.
     */
    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS Z";

    /**
     * Private constructor to prevent instantiation.
     */
    private Filter() {

    }

    /**
     * Builds a WHERE clause based on the provided filter bean using the default match type (EQUALS).
     *
     * @param filterBean The filter bean object.
     * @return The generated WHERE clause.
     */
    public static String buildWhereClause(Object filterBean) {
        return buildWhereClause(filterBean, MatchWhereClauseBy.EQUALS);
    }

    /**
     * Builds a WHERE clause based on the provided filter bean and match type.
     *
     * @param filterBean   The filter bean object.
     * @param matchByLike  The match type for string comparisons.
     * @return The generated WHERE clause.
     */
    public static String buildWhereClause(Object filterBean, MatchWhereClauseBy matchByLike) {
        if (filterBean == null) {
            return "";
        }
        Field[] fields = filterBean.getClass().getDeclaredFields();
        StringBuilder sb = new StringBuilder();
        for (Field fld : fields) {
            if (fld.isAnnotationPresent(FilterField.class)) {
                FilterField ff = fld.getAnnotation(FilterField.class);
                String dbname = ff.dbname();
                String name = fld.getName();
                if (dbname == null || dbname.equals(FilterField.NULL)) {
                    dbname = name;
                }
                String novalue = ff.novalue();
                FilterField.Range range = ff.range();
                StringBuilder msb = new StringBuilder("get");
                msb.append(Character.toUpperCase(name.charAt(0)));
                msb.append(name.substring(1));
                String getterName = msb.toString();
                try {
                    Method getter = filterBean.getClass().getMethod(getterName, null);
                    Object val = getter.invoke(filterBean, null);
                    addFilterFieldValue(novalue, sb, dbname, val, range, matchByLike);
                } catch (RuntimeException e) { // 2.33 Release - Sonar REC_CATCH_EXCEPTION code smell fix
                    log.error("Cannot find method: {} in the class: {} . Error message: {} ", getterName,
                        filterBean.getClass().getName(), e.getMessage());
                } catch (Exception e) {
                    log.error("Cannot find method {} in the class {}", getterName, filterBean.getClass().getName());
                }
            }
        }
        return sb.toString();
    }

    /**
     * Adds a filter field value to the given StringBuilder based on the provided parameters.
     *
     * @param noValue        The value indicating no value.
     * @param sb             The StringBuilder to append the filter field value to.
     * @param dbname         The name of the database field.
     * @param val            The value of the filter field.
     * @param range          The range of the filter field.
     * @param matchByLike    The matching method for the filter field.
     */
    private static void addFilterFieldValue(String noValue, StringBuilder sb, String dbname, Object val,
                                            FilterField.Range range, MatchWhereClauseBy matchByLike) {
        if (!isNoValue(val, noValue)) {
            addFilterField(sb, dbname, val, range, matchByLike);
        }
    }

    /**
     * Adds a filter field to the given StringBuilder object based on the provided parameters.
     *
     * @param sb            The StringBuilder object to which the filter field will be added.
     * @param name          The name of the filter field.
     * @param value         The value of the filter field.
     * @param range         The range of the filter field.
     * @param whereClauseBy The MatchWhereClauseBy object specifying how the filter field should be matched.
     */
    protected static void addFilterField(StringBuilder sb, String name, Object value, FilterField.Range range,
                                         MatchWhereClauseBy whereClauseBy) {

        if (sb.length() == 0) {
            sb.append("WHERE ");
        } else {
            sb.append(" AND ");
        }

        if (value == null) {
            sb.append(name);
            sb.append(" is NULL ");
            return;
        }

        if (value.getClass().isArray() || value instanceof List) {
            addList(sb, name, value, whereClauseBy);
        } else {
            addPrimitive(sb, name, value, range);
        }
    }

    /**
     * Checks if the given value is equal to the specified no value.
     *
     * @param value    the value to be checked
     * @param novalue  the no value to compare against
     * @return true if the value is equal to the no value, false otherwise
     */
    protected static boolean isNoValue(Object value, String novalue) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat(TIMESTAMP_FORMAT);
        boolean ret = false;
        if (value == null) {
            return FilterField.NULL.equals(novalue);
        }
        ret = evaluateNumericData(value, novalue);
        if (value instanceof String) {
            ret = value.toString().equals(novalue);
        } else if (value instanceof Date date) {
            ret = dateFormat.format(date).equals(novalue);
        } else if (value.getClass().isAssignableFrom(Boolean.class)) {
            Boolean bnoval = !novalue.equals(FilterField.NULL) && Boolean.parseBoolean(novalue);
            ret = ((Boolean) value).booleanValue() == bnoval;
        }
        return ret;
    }

    /**
     * Adds a primitive filter to the given StringBuilder.
     *
     * @param sb    the StringBuilder to which the filter will be added
     * @param name  the name of the filter
     * @param val   the value of the filter
     * @param range the range of the filter (NONE, MIN, or MAX)
     */
    protected static void addPrimitive(StringBuilder sb, String name, Object val, FilterField.Range range) {
        sb.append(name);
        if (range == FilterField.Range.NONE) {
            sb.append("=");
        } else if (range == FilterField.Range.MIN) {
            sb.append(">=");
        } else {
            sb.append("<=");
        }
        addValue(sb, val);
    }

    /**
     * Adds a list of values to a StringBuilder object based on the specified name, value, and match condition.
     *
     * @param sb The StringBuilder object to which the values will be added.
     * @param name The name of the column or field.
     * @param val The value(s) to be added. It can be an array or a List.
     * @param whereClauseBy The match condition for the values.
     */
    protected static void addList(StringBuilder sb, String name, Object val, MatchWhereClauseBy whereClauseBy) {

        List<Object> lst = (val.getClass().isArray()) ? Arrays.asList(val) : (List) val;
        if (lst.isEmpty()) {
            return;
        }

        sb.append(name);
        StringBuilder colNameAndLikeKeyword = new StringBuilder(name);

        switch (whereClauseBy) {
            case ILIKE:
                sb.append(" ilike ");
                colNameAndLikeKeyword.append(" ilike ");
                break;
            case LIKE:
                sb.append(" like ");
                colNameAndLikeKeyword.append(" like ");
                break;
            case EQUALS:
                sb.append(" in ");
                break;
            default:
                sb.append(" in ");
                break;
        }

        if (whereClauseBy != MatchWhereClauseBy.LIKE
            && whereClauseBy != MatchWhereClauseBy.ILIKE) {
            sb.append("(");
        }

        for (int i = 0; i < lst.size(); i++) {

            if (i > 0) {
                sb.append((whereClauseBy == MatchWhereClauseBy.LIKE || whereClauseBy == MatchWhereClauseBy.ILIKE)
                    ? " or " + colNameAndLikeKeyword
                    : ", ");
            }

            addValue(sb, lst.get(i));
        }
        if (whereClauseBy != MatchWhereClauseBy.LIKE
            && whereClauseBy != MatchWhereClauseBy.ILIKE) {
            sb.append(") ");
        }
    }

    /**
     * Adds a value to the given StringBuilder.
     *
     * @param sb The StringBuilder to which the value will be added.
     * @param val The value to be added.
     */
    protected static void addValue(StringBuilder sb, Object val) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat(TIMESTAMP_FORMAT);
        if (isPrimitive(val)) {
            sb.append(val.toString());
        } else if (val instanceof String) {
            sb.append("'");
            sb.append(val.toString());
            sb.append("'");
        } else if (val instanceof Date date) {
            sb.append("'");
            sb.append(dateFormat.format(date));
            sb.append("'");
        }
    }

    /**
     * Checks if the given value is a primitive type.
     *
     * @param value the value to check
     * @return true if the value is a primitive type, false otherwise
     */
    protected static boolean isPrimitive(Object value) {
        boolean ret = false;
        if (value.getClass().isAssignableFrom(boolean.class) || value.getClass().isAssignableFrom(Boolean.class)
            || value.getClass().isAssignableFrom(int.class) || value.getClass().isAssignableFrom(Integer.class) 
            || value.getClass().isAssignableFrom(float.class) || value.getClass().isAssignableFrom(Float.class) 
            || value.getClass().isAssignableFrom(long.class) || value.getClass().isAssignableFrom(Long.class) 
            || value.getClass().isAssignableFrom(double.class) || value.getClass().isAssignableFrom(Double.class)) {
            ret = true;
        }
        return ret;
    }

    /**
     * Evaluates the numeric data based on the given value and novalue.
     *
     * @param value   The value to be evaluated.
     * @param novalue The novalue to be compared with the value.
     * @return true if the value matches the novalue, false otherwise.
     */
    private static boolean evaluateNumericData(Object value, String novalue) {
        boolean ret = false;
        if (value.getClass().isAssignableFrom(Integer.class)) {
            int inoval = novalue.equals(FilterField.NULL) ? 0 : Integer.parseInt(novalue);
            ret = ((Integer) value).intValue() == inoval;
        } else if (value.getClass().isAssignableFrom(Long.class)) {
            long lnoval = novalue.equals(FilterField.NULL) ? 0L : Long.parseLong(novalue);
            ret = ((Long) value).longValue() == lnoval;
        } else if (value.getClass().isAssignableFrom(Float.class)) {
            float fnoval = novalue.equals(FilterField.NULL) ? 0f : Float.parseFloat(novalue);
            ret = ((Float) value).floatValue() == fnoval;
        } else if (value.getClass().isAssignableFrom(Double.class)) {
            double dnoval = novalue.equals(FilterField.NULL) ? 0d : Double.parseDouble(novalue);
            ret = ((Double) value).doubleValue() == dnoval;
        }
        return ret;
    }

    /**
     * The MatchWhereClauseBy enum represents the match types for string comparisons in the WHERE clause.
     */
    public enum MatchWhereClauseBy {
        /**
         * Represents an equality condition in the "WHERE" clause.
         * For example, column = value.
         */
        EQUALS(), 
        /**
         * Represents a "LIKE" condition in the "WHERE" clause.
         * For example, column LIKE value.
         */
        LIKE(), 
        /**
         * Represents an "ILIKE" condition in the "WHERE" clause.
         * For example, column ILIKE value.
         */
        ILIKE();
    }
}
