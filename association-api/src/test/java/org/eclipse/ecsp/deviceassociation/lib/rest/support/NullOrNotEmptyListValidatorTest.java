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

package org.eclipse.ecsp.deviceassociation.lib.rest.support;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.mockito.MockitoAnnotations.initMocks;


/**
 * Unit tests for the {@link NullOrNotEmptyListValidator} class.
 * This test class verifies the behavior of the validator to ensure it correctly
 * validates lists based on the following conditions:
 * 
 * <ul>
 *   <li>A null list is considered valid.</li>
 *   <li>A non-empty list is considered valid.</li>
 *   <li>An empty list is considered invalid.</li>
 * </ul>
 *
 * <p>
 * Test Methods:
 * <ul>
 *   <li>{@code isValidTestWithNullValue()} - Verifies that a null list is valid.</li>
 *   <li>{@code isValidTestWithNotEmptyValue()} - Verifies that a non-empty list is valid.</li>
 *   <li>{@code isValidTestWithEmptyValue()} - Verifies that an empty list is invalid.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Annotations:
 * <ul>
 *   <li>{@code @InjectMocks} - Injects the {@link NullOrNotEmptyListValidator} instance under test.</li>
 *   <li>{@code @Mock} - Mocks the {@link ConstraintValidatorContext} dependency.</li>
 *   <li>{@code @Before} - Initializes mocks before each test method execution.</li>
 *   <li>{@code @Test} - Marks methods as test cases.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Dependencies:
 * <ul>
 *   <li>{@link org.mockito.MockitoAnnotations#initMocks(Object)} - Initializes the mocks.</li>
 *   <li>{@link org.junit.Assert} - Provides assertion methods for test validation.</li>
 * </ul>
 * </p>
 */
public class NullOrNotEmptyListValidatorTest {

    @InjectMocks
    NullOrNotEmptyListValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void isValidTestWithNullValue() {
        List<String> stringList = null;
        boolean result = validator.isValid(stringList, context);
        Assert.assertTrue(result);
    }

    @Test
    public void isValidTestWithNotEmptyValue() {
        List<String> stringList = Arrays.asList("xyz");
        boolean result = validator.isValid(stringList, context);
        Assert.assertTrue(result);
    }

    @Test
    public void isValidTestWithEmptyValue() {
        List<String> stringList = new ArrayList<String>();
        boolean result = validator.isValid(stringList, context);
        Assert.assertFalse(result);
    }
}
