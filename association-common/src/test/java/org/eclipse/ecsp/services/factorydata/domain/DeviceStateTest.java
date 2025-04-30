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

package org.eclipse.ecsp.services.factorydata.domain;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for DeviceState.
 */
public class DeviceStateTest {
    public static final long COUNT = 2L;

    @Test
    public void provisioned() {
        assertEquals("PROVISIONED", DeviceState.PROVISIONED.getValue());
    }

    @Test
    public void provisionedNewStateStolen() {

        assertEquals(true, DeviceState.PROVISIONED.isValidTransition(DeviceState.STOLEN));
    }

    @Test
    public void provisionedNewStateFaulty() {

        assertEquals(true, DeviceState.PROVISIONED.isValidTransition(DeviceState.FAULTY));
    }

    @Test
    public void provisionedInvalidNewState() {

        assertEquals(false, DeviceState.PROVISIONED.isValidTransition(DeviceState.ACTIVE));
    }

    @Test
    public void readyToActivate() {

        assertEquals(false, DeviceState.READY_TO_ACTIVATE.isValidTransition(DeviceState.ACTIVE));
    }

    @Test
    public void activeNewStateStolen() {

        assertEquals(true, DeviceState.ACTIVE.isValidTransition(DeviceState.STOLEN));
    }

    @Test
    public void activeNewStateFaulty() {

        assertEquals(true, DeviceState.ACTIVE.isValidTransition(DeviceState.FAULTY));
    }

    @Test
    public void activeInvalidNewState() {

        assertEquals(false, DeviceState.ACTIVE.isValidTransition(DeviceState.ACTIVE));
    }

    @Test
    public void stolenNewStateActive() {

        assertEquals(true, DeviceState.STOLEN.isValidTransition(DeviceState.ACTIVE));
    }

    @Test
    public void stolenNewStateProvisioned() {

        assertEquals(true, DeviceState.STOLEN.isValidTransition(DeviceState.PROVISIONED));
    }

    @Test
    public void stolenInvalidNewState() {

        assertEquals(false, DeviceState.STOLEN.isValidTransition(DeviceState.DEACTIVATED));
    }

    @Test
    public void faultyNewStateActive() {

        assertEquals(true, DeviceState.FAULTY.isValidTransition(DeviceState.ACTIVE));
    }

    @Test
    public void faultyNewStateProvisioned() {

        assertEquals(true, DeviceState.FAULTY.isValidTransition(DeviceState.PROVISIONED));
    }

    @Test
    public void faultyNewStateActive2() {

        assertEquals(true, DeviceState.FAULTY.isValidTransition(DeviceState.ACTIVE));
    }

    @Test
    public void faultyInvalidNewState() {

        assertEquals(false, DeviceState.FAULTY.isValidTransition(DeviceState.DEACTIVATED));
    }

    @Test
    public void provisionedAlive() {

        assertEquals(false, DeviceState.PROVISIONED_ALIVE.isValidTransition(DeviceState.DEACTIVATED));
    }

    @Test
    public void deactivated() {

        assertEquals(false, DeviceState.DEACTIVATED.isValidTransition(DeviceState.ACTIVE));
    }

    @Test
    public void convertStateCountTestProvisioned() {

        DeviceStateAggregateData deviceStateAggregateData = new DeviceStateAggregateData();
        deviceStateAggregateData.setState("PROVISIONED");
        deviceStateAggregateData.setCount(1L);

        List<DeviceStateAggregateData> deviceStateMap = new ArrayList<>();
        deviceStateMap.add(deviceStateAggregateData);

        DeviceInfoAggregateFactoryData.StateCount stateCount = new DeviceInfoAggregateFactoryData.StateCount();
        DeviceState.convertStateCount(stateCount, deviceStateMap);
        Assertions.assertNotNull(deviceStateAggregateData.getState());
    }

    @Test
    public void convertStateCountTestActive() {

        DeviceStateAggregateData deviceStateAggregateData1 = new DeviceStateAggregateData();
        deviceStateAggregateData1.setState("ACTIVE");
        deviceStateAggregateData1.setCount(COUNT);

        List<DeviceStateAggregateData> deviceStateMap = new ArrayList<>();
        deviceStateMap.add(deviceStateAggregateData1);

        DeviceInfoAggregateFactoryData.StateCount stateCount = new DeviceInfoAggregateFactoryData.StateCount();
        DeviceState.convertStateCount(stateCount, deviceStateMap);
        Assertions.assertNotNull(deviceStateAggregateData1.getState());
    }

    @Test
    public void convertStateCountTestFaulty() {

        DeviceStateAggregateData deviceStateAggregateData2 = new DeviceStateAggregateData();
        deviceStateAggregateData2.setState("FAULTY");
        deviceStateAggregateData2.setCount(COUNT);

        List<DeviceStateAggregateData> deviceStateMap = new ArrayList<>();
        deviceStateMap.add(deviceStateAggregateData2);

        DeviceInfoAggregateFactoryData.StateCount stateCount = new DeviceInfoAggregateFactoryData.StateCount();
        DeviceState.convertStateCount(stateCount, deviceStateMap);
        Assertions.assertNotNull(deviceStateAggregateData2.getState());
    }

    @Test
    public void convertStateCountTestStolen() {

        DeviceStateAggregateData deviceStateAggregateData3 = new DeviceStateAggregateData();
        deviceStateAggregateData3.setState("STOLEN");
        deviceStateAggregateData3.setCount(COUNT);

        List<DeviceStateAggregateData> deviceStateMap = new ArrayList<>();
        deviceStateMap.add(deviceStateAggregateData3);

        DeviceInfoAggregateFactoryData.StateCount stateCount = new DeviceInfoAggregateFactoryData.StateCount();
        DeviceState.convertStateCount(stateCount, deviceStateMap);
        Assertions.assertNotNull(deviceStateAggregateData3.getState());
    }
}