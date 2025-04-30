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

package org.eclipse.ecsp.deviceassociation.springmvc.rest.support;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for the {@link DeviceAssociationDetailsValidator} class.
 *
 * <p>
 * This test class validates the behavior of the methods in the 
 * DeviceAssociationDetailsValidator class to ensure they correctly handle 
 * valid and invalid input data for device ID, serial number, and IMEI.
 * </p>
 * Test cases include:
 * - Validation of valid and invalid device IDs.
 * - Validation of valid and invalid serial numbers.
 * - Validation of valid and invalid IMEI numbers.
 *
 * <p>
 * Each test ensures that valid inputs are accepted without exceptions and 
 * invalid inputs throw the expected {@link IllegalArgumentException}.
 * </p>
 * Test methods:
 * - {@code validateDeviceIdRequestDataTest}: Tests valid device ID input.
 * - {@code validateDeviceIdRequestDataTestWithException}: Tests invalid device ID input.
 * - {@code validateSerialNumberRequestDataTest}: Tests valid serial number input.
 * - {@code validateSerialNumberRequestDataTestWithException}: Tests invalid serial number input.
 * - {@code validateImeiRequestDataTest}: Tests valid IMEI input.
 * - {@code validateImeiRequestDataTestWithException}: Tests invalid IMEI input.
 *
 * <p>
 * The tests use JUnit annotations such as {@code @Test} and {@code @Test(expected = ...)} 
 * to verify the expected behavior of the validation methods.
 * </p>
 */
public class DeviceAssociationDetailsValidatorTest {

    @Test
    public void validateDeviceIdRequestDataTest() {
        String value = "dsvcn12e2e";
        DeviceAssociationDetailsValidator.validateDeviceIdRequestData(value);
        Assert.assertEquals("dsvcn12e2e", value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateDeviceIdRequestDataTestWithException() {
        String value = "dsvcn12e2e&&";
        DeviceAssociationDetailsValidator.validateDeviceIdRequestData(value);
        Assert.assertEquals("dsvcn12e2e&&", value);
    }

    @Test
    public void validateSerialNumberRequestDataTest() {
        String value = "1234567890aa";
        DeviceAssociationDetailsValidator.validateSerialNumberRequestData(value);
        Assert.assertEquals("1234567890aa", value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateSerialNumberRequestDataTestWithException() {
        String value = "1234567890aa!";
        DeviceAssociationDetailsValidator.validateSerialNumberRequestData(value);
        Assert.assertEquals("1234567890aa!", value);
    }

    @Test
    public void validateImeiRequestDataTest() {
        String value = "1234567890";
        DeviceAssociationDetailsValidator.validateImeiRequestData(value);
        Assert.assertEquals("1234567890", value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateImeiRequestDataTestWithException() {
        String value = "1234567890a";
        DeviceAssociationDetailsValidator.validateImeiRequestData(value);
        Assert.assertEquals("1234567890a", value);
    }

}
