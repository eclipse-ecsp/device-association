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

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for filter.
 */
public class FilterTest {

    @InjectMocks
    Filter filter;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void matchWhereClauseByTestEquals() {

        assertEquals("EQUALS", Filter.MatchWhereClauseBy.EQUALS.name());
    }

    @Test
    public void matchWhereClauseByTestLike() {

        assertEquals("LIKE", Filter.MatchWhereClauseBy.LIKE.name());
    }

    @Test
    public void matchWhereClauseByTestIlike() {

        assertEquals("ILIKE", Filter.MatchWhereClauseBy.ILIKE.name());
    }

    @Test
    public void buildWhereClauseTestNull() {

        Assertions.assertNotNull(filter.buildWhereClause(null));
    }

    @Test
    public void buildWhereClauseTest() {

        Assertions.assertNotNull(filter.buildWhereClause(FilterField.class));
    }

    @Test
    public void addFilterFieldTestEmptyStringBuilder() {

        filter.addFilterField(new StringBuilder(), "name", new Object(), FilterField.Range.MIN,
            Filter.MatchWhereClauseBy.EQUALS);
        Assertions.assertNotNull(filter);
    }

    @Test
    public void addFilterFieldTestNullObject() {

        filter.addFilterField(new StringBuilder("StringBuilder"), "name", null, FilterField.Range.MIN,
            Filter.MatchWhereClauseBy.EQUALS);
        Assertions.assertNotNull(filter);
    }

    @Test
    public void addFilterFieldTestArrayObject() {

        filter.addFilterField(new StringBuilder("StringBuilder"), "name", new Object[]{"a", "b"}, FilterField.Range.MIN,
            Filter.MatchWhereClauseBy.EQUALS);
        Assertions.assertNotNull(filter);
    }

    @Test
    public void isNoValueTestNullValue() {

        Assertions.assertTrue(filter.isNoValue(null, FilterField.NULL));
    }

    @Test
    public void isNoValueTestStringValue() {

        Assertions.assertTrue(filter.isNoValue(new String(), FilterField.NULL));
    }

    @Test
    public void isNoValueTestDateValue() {

        Assertions.assertFalse(filter.isNoValue(new Date(), FilterField.NULL));
    }

    @Test
    public void isNoValueTestIntegerValue() {

        Assertions.assertFalse(filter.isNoValue(new Integer(1), FilterField.NULL));
    }

    @Test
    public void isNoValueTestLongValue() {

        Assertions.assertFalse(filter.isNoValue(new Long(1), FilterField.NULL));
    }

    @Test
    public void isNoValueTestFloatValue() {

        Assertions.assertFalse(filter.isNoValue(new Float(1), FilterField.NULL));
    }

    @Test
    public void isNoValueTestBooleanValue() {

        Assertions.assertFalse(filter.isNoValue(new Boolean(true), FilterField.NULL));
    }

    @Test
    public void isNoValueTestDoubleValue() {

        Assertions.assertFalse(filter.isNoValue(new Double(1), FilterField.NULL));
    }

    @Test
    public void addPrimitiveTest() {

        filter.addPrimitive(new StringBuilder(), "name", new Date(), FilterField.Range.MIN);
        Assertions.assertNotNull(filter);
    }

    @Test
    public void addPrimitiveTestStringObject() {

        filter.addPrimitive(new StringBuilder(), "name", new String(), FilterField.Range.MIN);
        Assertions.assertNotNull(filter);
    }

    @Test
    public void addPrimitiveTestNone() {

        filter.addPrimitive(new StringBuilder(), "name", new Date(), FilterField.Range.NONE);
        Assertions.assertNotNull(filter);
    }

    @Test
    public void addPrimitiveTestStringObjectNone() {

        filter.addPrimitive(new StringBuilder(), "name", new String(), FilterField.Range.NONE);
        Assertions.assertNotNull(filter);
    }

    @Test
    public void addPrimitiveTestStringObjectElse() {

        filter.addPrimitive(new StringBuilder(), "name", new String(), FilterField.Range.MAX);
        Assertions.assertNotNull(filter);
    }

    @Test
    public void addListTestNullObjectArray() {

        filter.addList(new StringBuilder(), "name", new Object[]{}, Filter.MatchWhereClauseBy.EQUALS);
        Assertions.assertNotNull(filter);
    }

    @Test
    public void addListTestLike() {

        filter.addList(new StringBuilder(), "name", new Object[]{"a", "b"}, Filter.MatchWhereClauseBy.LIKE);
        Assertions.assertNotNull(filter);
    }

    @Test
    public void addListTestIlike() {

        filter.addList(new StringBuilder(), "name", new Object[]{"a"}, Filter.MatchWhereClauseBy.ILIKE);
        Assertions.assertNotNull(filter);
    }
}
