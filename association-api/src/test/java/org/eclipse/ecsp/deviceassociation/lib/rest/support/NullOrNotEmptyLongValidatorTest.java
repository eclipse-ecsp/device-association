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
 * Unit tests for the {@link NullOrNotEmptyLongValidator} class.
 * This test class verifies the behavior of the validator when handling
 * null, valid, and invalid long values.
 * 
 * <p>Test Scenarios:</p>
 * <ul>
 *   <li>{@code isValidTestWithNullValue}: Ensures that the validator returns {@code true} 
 *       when the input value is {@code null}.</li>
 *   <li>{@code isValidTestWithNotEmptyValue}: Ensures that the validator returns {@code true} 
 *       when the input value is a valid non-empty {@code Long}.</li>
 *   <li>{@code isValidTestWithEmptyValue}: Ensures that the validator returns {@code false} 
 *       when the input value is an invalid {@code Long} (e.g., -1).</li>
 * </ul>
 * 
 * <p>Dependencies:</p>
 * <ul>
 *   <li>{@link org.mockito.InjectMocks}: Used to inject the {@link NullOrNotEmptyLongValidator} instance.</li>
 *   <li>{@link org.mockito.Mock}: Used to mock the {@link ConstraintValidatorContext} dependency.</li>
 *   <li>{@link org.mockito.MockitoAnnotations#initMocks(Object)}: Initializes the mocks before each test.</li>
 *   <li>{@link org.junit.Before}: Sets up the test environment.</li>
 *   <li>{@link org.junit.Test}: Marks the test methods.</li>
 *   <li>{@link org.junit.Assert}: Provides assertions to validate test outcomes.</li>
 * </ul>
 */
public class NullOrNotEmptyLongValidatorTest {

    @InjectMocks
    NullOrNotEmptyLongValidator validator;

    @Mock
    private ConstraintValidatorContext context;
    
    private static final Long INVALID_VALUE = -1L; 

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
        boolean result = validator.isValid(1L, context);
        Assert.assertTrue(result);
    }

    @Test
    public void isValidTestWithEmptyValue() {
        boolean result = validator.isValid(INVALID_VALUE, context);
        Assert.assertFalse(result);
    }
}
