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

package org.eclipse.ecsp.deviceassociation.lib.observer;

import org.eclipse.ecsp.common.config.EnvConfig;
import org.eclipse.ecsp.deviceassociation.lib.config.DeviceAssocationProperty;
import org.eclipse.ecsp.deviceassociation.lib.exception.ObserverMessageProcessFailureException;
import org.eclipse.ecsp.deviceassociation.lib.model.AssociationStatus;
import org.eclipse.ecsp.deviceassociation.lib.model.DeviceAssociation;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceMessageService;
import org.eclipse.ecsp.exception.shared.ApiTechnicalException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for DeviceMessageStateObserver.
 */
public class DeviceMessageStateObserverTest {
    private static final long FACTORY_ID = 2L;
    
    @InjectMocks
    DeviceMessageStateObserver deviceMessageStateObserver;

    @Mock
    EnvConfig envConfig;

    @Mock
    DeviceAssociationObservable observable;

    @Mock
    DeviceMessageService deviceMessageService;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void stateChangedTest_DeviceMessageEnabledFalse() throws ObserverMessageProcessFailureException {
        Mockito.doReturn(false).when(envConfig).getBooleanValue(DeviceAssocationProperty.DEVICE_MESSAGE_ENABLED);
        deviceMessageStateObserver.init();
        deviceMessageStateObserver.stateChanged(null);
        assertFalse(envConfig.getBooleanValue(DeviceAssocationProperty.DEVICE_MESSAGE_ENABLED));
    }

    @Test
    public void stateChangedTest_DeviceMessageEnabledTrue_NullDeviceAssociation()
        throws ObserverMessageProcessFailureException {
        Mockito.doReturn(true).when(envConfig).getBooleanValue(DeviceAssocationProperty.DEVICE_MESSAGE_ENABLED);
        Mockito.doNothing().when(observable).register(Mockito.any());
        deviceMessageStateObserver.init();
        deviceMessageStateObserver.stateChanged(null);
        assertTrue(envConfig.getBooleanValue(DeviceAssocationProperty.DEVICE_MESSAGE_ENABLED));
    }

    @Test
    public void stateChangedTest_Associated() throws ObserverMessageProcessFailureException {
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(1L);
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setHarmanId("H1234");
        deviceAssociation.setVehicleId("V1234");
        deviceAssociation.setFactoryId(FACTORY_ID);
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        Mockito.doReturn(true).when(envConfig).getBooleanValue(DeviceAssocationProperty.DEVICE_MESSAGE_ENABLED);
        Mockito.doNothing().when(observable).register(Mockito.any());
        deviceMessageStateObserver.init();
        deviceMessageStateObserver.stateChanged(deviceAssociation);
        assertTrue(envConfig.getBooleanValue(DeviceAssocationProperty.DEVICE_MESSAGE_ENABLED));
    }

    @Test
    public void stateChangedTest_Disassociated_wipeData() throws Exception {
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(1L);
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setHarmanId("H1234");
        deviceAssociation.setVehicleId("V1234");
        deviceAssociation.setFactoryId(FACTORY_ID);
        deviceAssociation.setAssociationStatus(AssociationStatus.DISASSOCIATED);
        deviceAssociation.setTerminateFor("wipeData");
        Mockito.doReturn(true).when(envConfig).getBooleanValue(DeviceAssocationProperty.DEVICE_MESSAGE_ENABLED);
        Mockito.doNothing().when(observable).register(Mockito.any());
        Mockito.doNothing().when(deviceMessageService)
            .publishMessage(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyString(), Mockito.any());
        deviceMessageStateObserver.init();
        deviceMessageStateObserver.stateChanged(deviceAssociation);
        assertTrue(envConfig.getBooleanValue(DeviceAssocationProperty.DEVICE_MESSAGE_ENABLED));
    }

    @Test
    public void stateChangedTest_Disassociated_NotwipeData() throws Exception {
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(1L);
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setHarmanId("H1234");
        deviceAssociation.setVehicleId("V1234");
        deviceAssociation.setFactoryId(FACTORY_ID);
        deviceAssociation.setAssociationStatus(AssociationStatus.DISASSOCIATED);
        deviceAssociation.setTerminateFor("xyz");
        Mockito.doReturn(true).when(envConfig).getBooleanValue(DeviceAssocationProperty.DEVICE_MESSAGE_ENABLED);
        Mockito.doNothing().when(observable).register(Mockito.any());
        Mockito.doNothing().when(deviceMessageService)
            .publishMessage(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyString(), Mockito.any());
        deviceMessageStateObserver.init();
        deviceMessageStateObserver.stateChanged(deviceAssociation);
        assertTrue(envConfig.getBooleanValue(DeviceAssocationProperty.DEVICE_MESSAGE_ENABLED));
    }

    @Test
    public void stateChangedTest_Disassociated_Exception() throws Exception {
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(1L);
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setHarmanId("H1234");
        deviceAssociation.setVehicleId("V1234");
        deviceAssociation.setFactoryId(FACTORY_ID);
        deviceAssociation.setAssociationStatus(AssociationStatus.DISASSOCIATED);
        deviceAssociation.setTerminateFor("xyz");
        Mockito.doReturn(true).when(envConfig).getBooleanValue(DeviceAssocationProperty.DEVICE_MESSAGE_ENABLED);
        Mockito.doNothing().when(observable).register(Mockito.any());
        Mockito.doThrow(
                new ApiTechnicalException("Unknown exception while publishing config")).when(deviceMessageService)
            .publishMessage(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyString(), Mockito.any());
        deviceMessageStateObserver.init();
        assertThrows(ObserverMessageProcessFailureException.class,
            () -> deviceMessageStateObserver.stateChanged(deviceAssociation));
    }
}