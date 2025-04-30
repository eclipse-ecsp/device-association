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

import com.amazonaws.ResponseMetadata;
import com.amazonaws.http.SdkHttpMetadata;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesis.model.PutRecordResult;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.ecsp.common.config.EnvConfig;
import org.eclipse.ecsp.deviceassociation.lib.config.DeviceAssocationProperty;
import org.eclipse.ecsp.deviceassociation.lib.exception.ObserverMessageProcessFailureException;
import org.eclipse.ecsp.deviceassociation.lib.model.AssociationStatus;
import org.eclipse.ecsp.deviceassociation.lib.model.DeviceAssociation;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.sql.Timestamp;

import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for KinesisDeviceNotificationObserver.
 */
public class KinesisDeviceNotificationObserverTest {

    private static final long FACTORY_ID = 2L;
    @Mock
    SdkHttpMetadata sdkHttpMetadata;
    @Mock
    AmazonKinesisClient amazonKinesisClient;
    @InjectMocks
    private KinesisDeviceNotificationObserver kinesisDeviceNotificationObserver;
    @Mock
    private EnvConfig<DeviceAssocationProperty> envConfig;
    @Mock
    private DeviceAssociationObservable observable;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ResponseMetadata sdkResponseMetadata;

    @Before
    public void beforeEach() {
        initMocks(this);
    }

    @Test
    public void loadKinesisPropertiesTest_NullProfile() {
        Mockito.doReturn(null).when(envConfig).getStringValue(DeviceAssocationProperty.ENV_PROFILE);
        kinesisDeviceNotificationObserver.loadKinesisProperties();
        Assertions.assertNull(envConfig.getStringValue(DeviceAssocationProperty.ENV_PROFILE));
    }

    @Test
    public void loadKinesisPropertiesTest_ValidProfile() {
        Mockito.doReturn("ENV_AWS_NATIVE").when(envConfig).getStringValue(DeviceAssocationProperty.ENV_PROFILE);
        Mockito.doNothing().when(observable).register(Mockito.any());
        Mockito.doReturn("us-east-1").when(envConfig).getStringValue(DeviceAssocationProperty.KINESIS_ECS_REGION);
        kinesisDeviceNotificationObserver.loadKinesisProperties();
        Assertions.assertEquals("ENV_AWS_NATIVE", envConfig.getStringValue(DeviceAssocationProperty.ENV_PROFILE));
    }

    @Test
    public void stateChangedTest_NullDeviceAssociation() throws ObserverMessageProcessFailureException {
        kinesisDeviceNotificationObserver.stateChanged(null);
        Assertions.assertNotNull(kinesisDeviceNotificationObserver);
    }

    @Test
    public void stateChangedTest_Associated() throws ObserverMessageProcessFailureException {
        Timestamp assocOn = new Timestamp(1L);
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setUserId("User123");
        deviceAssociation.setId(1L);
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setHarmanId("H1234");
        deviceAssociation.setVehicleId("V1234");
        deviceAssociation.setFactoryId(FACTORY_ID);
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        deviceAssociation.setAssociatedOn(assocOn);
        kinesisDeviceNotificationObserver.stateChanged(deviceAssociation);
        Assertions.assertEquals(AssociationStatus.ASSOCIATED, deviceAssociation.getAssociationStatus());
    }

    @Test
    public void stateChangedTest_Disassociated() throws ObserverMessageProcessFailureException {
        Timestamp disassocOn = new Timestamp(1L);
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setUserId("User123");
        deviceAssociation.setId(1L);
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setHarmanId("H1234");
        deviceAssociation.setVehicleId("V1234");
        deviceAssociation.setFactoryId(FACTORY_ID);
        deviceAssociation.setAssociationStatus(AssociationStatus.DISASSOCIATED);
        deviceAssociation.setDisassociatedOn(disassocOn);
        kinesisDeviceNotificationObserver.stateChanged(deviceAssociation);
        Assertions.assertEquals(AssociationStatus.DISASSOCIATED, deviceAssociation.getAssociationStatus());
    }

    @Test
    public void stateChangedTest_Message() throws JsonProcessingException, ObserverMessageProcessFailureException {
        Timestamp assocOn = new Timestamp(1L);
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setUserId("User123");
        deviceAssociation.setId(1L);
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setHarmanId("H1234");
        deviceAssociation.setVehicleId("V1234");
        deviceAssociation.setFactoryId(FACTORY_ID);
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        deviceAssociation.setAssociatedOn(assocOn);
        Mockito.doReturn("message").when(objectMapper).writeValueAsString(Mockito.any());
        kinesisDeviceNotificationObserver.stateChanged(deviceAssociation);
        Assertions.assertEquals(AssociationStatus.ASSOCIATED, deviceAssociation.getAssociationStatus());
    }

    @Test
    public void stateChangedTest_JsonGenerationException()
        throws JsonProcessingException, ObserverMessageProcessFailureException {
        Timestamp assocOn = new Timestamp(1L);
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setUserId("User123");
        deviceAssociation.setId(1L);
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setHarmanId("H1234");
        deviceAssociation.setVehicleId("V1234");
        deviceAssociation.setFactoryId(FACTORY_ID);
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        deviceAssociation.setAssociatedOn(assocOn);
        Mockito.doThrow(JsonGenerationException.class).when(objectMapper).writeValueAsString(Mockito.any());
        kinesisDeviceNotificationObserver.stateChanged(deviceAssociation);
        Assertions.assertThrows(JsonGenerationException.class, () -> objectMapper.writeValueAsString(Mockito.any()));
    }

    @Test
    public void stateChangedTest_JsonMappingException()
        throws JsonProcessingException, ObserverMessageProcessFailureException {
        Timestamp assocOn = new Timestamp(1L);
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setUserId("User123");
        deviceAssociation.setId(1L);
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setHarmanId("H1234");
        deviceAssociation.setVehicleId("V1234");
        deviceAssociation.setFactoryId(FACTORY_ID);
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        deviceAssociation.setAssociatedOn(assocOn);
        Mockito.doThrow(JsonMappingException.class).when(objectMapper).writeValueAsString(Mockito.any());
        kinesisDeviceNotificationObserver.stateChanged(deviceAssociation);
        Assertions.assertThrows(JsonMappingException.class, () -> objectMapper.writeValueAsString(Mockito.any()));
    }

    @Test
    public void stateChangedTest_IoException() throws JsonProcessingException, ObserverMessageProcessFailureException {
        Timestamp assocOn = new Timestamp(1L);
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setUserId("User123");
        deviceAssociation.setId(1L);
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setHarmanId("H1234");
        deviceAssociation.setVehicleId("V1234");
        deviceAssociation.setFactoryId(FACTORY_ID);
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        deviceAssociation.setAssociatedOn(assocOn);
        PutRecordResult kinesisPutResult = new PutRecordResult();
        kinesisPutResult.setSequenceNumber("sq123");
        kinesisPutResult.setShardId("sh123");
        kinesisPutResult.setSdkHttpMetadata(sdkHttpMetadata);
        kinesisPutResult.setSdkResponseMetadata(sdkResponseMetadata);
        Mockito.doThrow(JsonProcessingException.class).when(objectMapper).writeValueAsString(Mockito.any());
        Mockito.doReturn(kinesisPutResult).when(amazonKinesisClient).putRecord(Mockito.any());
        kinesisDeviceNotificationObserver.stateChanged(deviceAssociation);
        Assertions.assertThrows(JsonProcessingException.class, () -> objectMapper.writeValueAsString(Mockito.any()));
    }

    @Test
    public void stateChangedTest_Exception() throws ObserverMessageProcessFailureException {
        Timestamp assocOn = new Timestamp(1L);
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setUserId("User123");
        deviceAssociation.setId(1L);
        deviceAssociation.setImei("I1234");
        deviceAssociation.setSerialNumber("S1234");
        deviceAssociation.setHarmanId("H1234");
        deviceAssociation.setVehicleId("V1234");
        deviceAssociation.setFactoryId(FACTORY_ID);
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATION_INITIATED);
        deviceAssociation.setAssociatedOn(assocOn);
        kinesisDeviceNotificationObserver.stateChanged(deviceAssociation);
        Assertions.assertEquals(AssociationStatus.ASSOCIATION_INITIATED, deviceAssociation.getAssociationStatus());
    }
}