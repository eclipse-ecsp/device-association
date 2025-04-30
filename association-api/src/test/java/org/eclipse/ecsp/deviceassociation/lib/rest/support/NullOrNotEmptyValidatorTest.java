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
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Unit tests for the {@link NullOrNotEmptyValidator} class.
 * This test class verifies the behavior of the isValid method in various scenarios.
 * 
 * <p>Test Scenarios:
 * <ul>
 *   <li>Null value: Ensures that the validator returns true when the input is null.</li>
 *   <li>Non-empty value: Ensures that the validator returns true when the input is a non-empty string.</li>
 *   <li>Empty value: Ensures that the validator returns false when the input is an empty string.</li>
 * </ul>
 * 
 * <p>Dependencies:
 * <ul>
 *   <li>{@link org.mockito.InjectMocks}: Used to inject the {@link NullOrNotEmptyValidator} instance.</li>
 *   <li>{@link org.mockito.Mock}: Used to mock the {@link ConstraintValidatorContext} dependency.</li>
 *   <li>{@link org.mockito.MockitoAnnotations#initMocks(Object)}: Initializes the mocks before each test.</li>
 *   <li>{@link org.junit.Assert}: Used for assertions in the test cases.</li>
 * </ul>
 * 
 * <p>Test Methods:
 * <ul>
 *   <li>{@code isValidTestWithNullValue()}: Tests the behavior of the validator with a null input.</li>
 *   <li>{@code isValidTestWithNotEmptyValue()}: Tests the behavior of the validator with a non-empty string input.</li>
 *   <li>{@code isValidTestWithEmptyValue()}: Tests the behavior of the validator with an empty string input.</li>
 * </ul>
 */
public class NullOrNotEmptyValidatorTest {

    @InjectMocks
    NullOrNotEmptyValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void isValidTestWithNullValue() {
        boolean result = validator.isValid(null, context);
        Assert.assertTrue(result);
    }

    @Test
    public void isValidTestWithNotEmptyValue() {
        boolean result = validator.isValid("xyz", context);
        Assert.assertTrue(result);
    }

    @Test
    public void isValidTestWithEmptyValue() {
        boolean result = validator.isValid("", context);
        Assert.assertFalse(result);
    }
}
