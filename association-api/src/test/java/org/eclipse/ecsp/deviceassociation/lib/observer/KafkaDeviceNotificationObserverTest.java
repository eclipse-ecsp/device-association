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

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.eclipse.ecsp.common.config.EnvConfig;
import org.eclipse.ecsp.deviceassociation.dto.TriggerKafkaEventRequestDto;
import org.eclipse.ecsp.deviceassociation.lib.config.DeviceAssocationProperty;
import org.eclipse.ecsp.deviceassociation.lib.dao.DeviceAssociationDao;
import org.eclipse.ecsp.deviceassociation.lib.exception.ObserverMessageProcessFailureException;
import org.eclipse.ecsp.deviceassociation.lib.model.AssociationStatus;
import org.eclipse.ecsp.deviceassociation.lib.model.DeviceAssociation;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceInfo;
import org.eclipse.ecsp.deviceassociation.lib.service.VehicleProfileService;
import org.eclipse.ecsp.deviceassociation.lib.util.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Timestamp;
import java.util.concurrent.Future;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Test class for KafkaDeviceNotificationObserverTest.
 *
 * @author sasif
 */

@RunWith(MockitoJUnitRunner.class)
public class KafkaDeviceNotificationObserverTest {
    private static int NUMBER_OF_INVOCATIONS = 4;
    private static int ID = 1234;
    @Mock
    protected DeviceAssociationDao deviceAssociationDao;
    @InjectMocks
    KafkaDeviceNotificationObserver kafkaDeviceNotificationObserver;
    @Mock
    VehicleProfileService vehicleProfileService;
    @Mock
    private EnvConfig<DeviceAssocationProperty> envConfig;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private Producer<String, String> producer;

    /**
     * beforeEach.
     */
    @Before
    public void beforeEach() {
        initMocks(this);

        ReflectionTestUtils.setField(kafkaDeviceNotificationObserver, "vinAssocEnabled", true);
        ReflectionTestUtils.setField(kafkaDeviceNotificationObserver, "realVehicleProfileCreationFor",
            "vehiclespecification");
    }

    @Test
    public void testSendAssetActivationEventToKafka()
        throws ObserverMessageProcessFailureException, JsonProcessingException {

        when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("string");
        when(envConfig.getStringValue(Mockito.any())).thenReturn("activation");
        Future<RecordMetadata> value = null;
        when(producer.send(Mockito.any())).thenReturn(value);
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setHarmanId("HARMANID123");
        deviceAssociation.setSerialNumber("12345");
        deviceAssociation.setUserId("testid");
        deviceAssociation.setSoftwareVersion("swversion");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        deviceAssociation.setAssociatedOn(new Timestamp(System.currentTimeMillis()));
        deviceAssociation.setDeviceType("dongle");
        kafkaDeviceNotificationObserver.stateChanged(deviceAssociation);
        Mockito.verify(producer, times(NUMBER_OF_INVOCATIONS)).send(Mockito.any());
    }

    @Test
    public void stateChangedTest_dissociatedStatus() throws ObserverMessageProcessFailureException {

        when(envConfig.getStringValue(Mockito.any())).thenReturn("activation");
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setHarmanId("HARMANID123");
        deviceAssociation.setSerialNumber("12345");
        deviceAssociation.setUserId("testid");
        deviceAssociation.setSoftwareVersion("swversion");
        deviceAssociation.setAssociationStatus(AssociationStatus.DISASSOCIATED);
        deviceAssociation.setAssociatedOn(new Timestamp(System.currentTimeMillis()));
        deviceAssociation.setDeviceType("dongle");
        kafkaDeviceNotificationObserver.stateChanged(deviceAssociation);
        Assertions.assertEquals("activation", envConfig.getStringValue(Mockito.any()));
    }

    @Test
    public void stateChangedTest_AssociatedStatusEmptyVin() throws ObserverMessageProcessFailureException {

        doReturn("").when(deviceAssociationDao).getAssociatedVin(Mockito.anyString());
        when(envConfig.getStringValue(Mockito.any())).thenReturn("activation");
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setHarmanId("HARMANID123");
        deviceAssociation.setSerialNumber("12345");
        deviceAssociation.setUserId("testid");
        deviceAssociation.setSoftwareVersion("swversion");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        deviceAssociation.setAssociatedOn(new Timestamp(System.currentTimeMillis()));
        deviceAssociation.setDeviceType("dongle");
        kafkaDeviceNotificationObserver.stateChanged(deviceAssociation);
        Assertions.assertEquals(StringUtils.EMPTY, deviceAssociationDao.getAssociatedVin(Mockito.anyString()));
    }

    @Test
    public void stateChangedTest_AssociatedStatusNotEmptyVin()
        throws ObserverMessageProcessFailureException, JsonProcessingException {

        when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("string");
        doReturn("vin1234566").when(deviceAssociationDao).findAssociatedFactoryDataVin(Mockito.anyString());
        when(envConfig.getStringValue(Mockito.any())).thenReturn("activation");
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setHarmanId("HARMANID123");
        deviceAssociation.setSerialNumber("12345");
        deviceAssociation.setUserId("testid");
        deviceAssociation.setSoftwareVersion("swversion");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        deviceAssociation.setAssociatedOn(new Timestamp(System.currentTimeMillis()));
        deviceAssociation.setDeviceType("dongle");
        kafkaDeviceNotificationObserver.stateChanged(deviceAssociation);
        Assertions.assertEquals("vin1234566", deviceAssociationDao.findAssociatedFactoryDataVin(Mockito.anyString()));
    }

    @Test
    public void stateChangedTest_invalidVehicleSpecification()
        throws ObserverMessageProcessFailureException, JsonProcessingException {

        ReflectionTestUtils.setField(kafkaDeviceNotificationObserver, "realVehicleProfileCreationFor",
            "vehiclespecification1");

        when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("string");
        when(envConfig.getStringValue(Mockito.any())).thenReturn("activation");
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setHarmanId("HARMANID123");
        deviceAssociation.setSerialNumber("12345");
        deviceAssociation.setUserId("testid");
        deviceAssociation.setSoftwareVersion("swversion");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        deviceAssociation.setAssociatedOn(new Timestamp(System.currentTimeMillis()));
        deviceAssociation.setDeviceType("dongle");
        kafkaDeviceNotificationObserver.stateChanged(deviceAssociation);
        Assertions.assertEquals(AssociationStatus.ASSOCIATED, deviceAssociation.getAssociationStatus());
    }

    @Test
    public void stateChangedTest_throwJsonGenerationException()
        throws ObserverMessageProcessFailureException, JsonProcessingException {

        when(objectMapper.writeValueAsString(Mockito.any())).thenThrow(JsonGenerationException.class);
        when(envConfig.getStringValue(Mockito.any())).thenReturn("activation");
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setHarmanId("HARMANID123");
        deviceAssociation.setSerialNumber("12345");
        deviceAssociation.setUserId("testid");
        deviceAssociation.setSoftwareVersion("swversion");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        deviceAssociation.setAssociatedOn(new Timestamp(System.currentTimeMillis()));
        deviceAssociation.setDeviceType("dongle");
        kafkaDeviceNotificationObserver.stateChanged(deviceAssociation);
        Mockito.verify(producer, times(NUMBER_OF_INVOCATIONS)).send(Mockito.any(ProducerRecord.class));
    }

    @Test
    public void stateChangedTest_throwJsonMappingException()
        throws ObserverMessageProcessFailureException, JsonProcessingException {

        when(objectMapper.writeValueAsString(Mockito.any())).thenThrow(JsonMappingException.class);
        when(envConfig.getStringValue(Mockito.any())).thenReturn("activation");
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setHarmanId("HARMANID123");
        deviceAssociation.setSerialNumber("12345");
        deviceAssociation.setUserId("testid");
        deviceAssociation.setSoftwareVersion("swversion");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        deviceAssociation.setAssociatedOn(new Timestamp(System.currentTimeMillis()));
        deviceAssociation.setDeviceType("dongle");
        kafkaDeviceNotificationObserver.stateChanged(deviceAssociation);
        Mockito.verify(producer, times(NUMBER_OF_INVOCATIONS)).send(Mockito.any(ProducerRecord.class));
    }

    @Test
    public void stateChangedTest_throwJsonProcessingException()
        throws ObserverMessageProcessFailureException, JsonProcessingException {

        when(objectMapper.writeValueAsString(Mockito.any())).thenThrow(JsonProcessingException.class);
        when(envConfig.getStringValue(Mockito.any())).thenReturn("activation");
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setHarmanId("HARMANID123");
        deviceAssociation.setSerialNumber("12345");
        deviceAssociation.setUserId("testid");
        deviceAssociation.setSoftwareVersion("swversion");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        deviceAssociation.setAssociatedOn(new Timestamp(System.currentTimeMillis()));
        deviceAssociation.setDeviceType("dongle");
        kafkaDeviceNotificationObserver.stateChanged(deviceAssociation);
        Mockito.verify(producer, times(NUMBER_OF_INVOCATIONS)).send(Mockito.any(ProducerRecord.class));
    }

    @Test
    public void stateChangedTest_defaultStatus() throws ObserverMessageProcessFailureException {

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setHarmanId("HARMANID123");
        deviceAssociation.setSerialNumber("12345");
        deviceAssociation.setUserId("testid");
        deviceAssociation.setSoftwareVersion("swversion");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATION_FAILED);
        deviceAssociation.setAssociatedOn(new Timestamp(System.currentTimeMillis()));
        deviceAssociation.setDeviceType("dongle");
        kafkaDeviceNotificationObserver.stateChanged(deviceAssociation);
        Assertions.assertEquals(AssociationStatus.ASSOCIATION_FAILED, deviceAssociation.getAssociationStatus());
    }

    @Test
    public void stateChangedTest_vinAssocEnabledEmpty() throws ObserverMessageProcessFailureException {

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setHarmanId("HARMANID123");
        deviceAssociation.setSerialNumber("12345");
        deviceAssociation.setUserId("testid");
        deviceAssociation.setSoftwareVersion("swversion");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATION_FAILED);
        deviceAssociation.setAssociatedOn(new Timestamp(System.currentTimeMillis()));
        deviceAssociation.setDeviceType("dongle");
        kafkaDeviceNotificationObserver.stateChanged(deviceAssociation);
        Assertions.assertEquals(AssociationStatus.ASSOCIATION_FAILED, deviceAssociation.getAssociationStatus());
    }

    @Test
    public void stateChangedTest_vinAssocEnabledNullVehicleProfileService()
        throws ObserverMessageProcessFailureException {

        when(envConfig.getStringValue(Mockito.any())).thenReturn("activation");
        Mockito.doReturn(null).when(vehicleProfileService).decodeVin(Mockito.anyString());
        doReturn("value").when(deviceAssociationDao).getAssociatedVin(Mockito.anyString());
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setHarmanId("HARMANID123");
        deviceAssociation.setSerialNumber("12345");
        deviceAssociation.setUserId("testid");
        deviceAssociation.setSoftwareVersion("swversion");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        deviceAssociation.setAssociatedOn(new Timestamp(System.currentTimeMillis()));
        deviceAssociation.setDeviceType("dongle");
        kafkaDeviceNotificationObserver.stateChanged(deviceAssociation);
        Assertions.assertNull(vehicleProfileService.decodeVin(Mockito.anyString()));
    }

    @Test
    public void stateChangedTest_vinAssocEnabledNotNullVehicleProfileService()
        throws ObserverMessageProcessFailureException {

        Pair<String, String> decodeVinPair = new Pair<>("Element1", "Element2");

        when(envConfig.getStringValue(Mockito.any())).thenReturn("activation");
        Mockito.doReturn(decodeVinPair).when(vehicleProfileService).decodeVin(Mockito.anyString());
        doReturn("value").when(deviceAssociationDao).getAssociatedVin(Mockito.anyString());
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setHarmanId("HARMANID123");
        deviceAssociation.setSerialNumber("12345");
        deviceAssociation.setUserId("testid");
        deviceAssociation.setSoftwareVersion("swversion");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        deviceAssociation.setAssociatedOn(new Timestamp(System.currentTimeMillis()));
        deviceAssociation.setDeviceType("dongle");
        kafkaDeviceNotificationObserver.stateChanged(deviceAssociation);
        Assertions.assertEquals(decodeVinPair, vehicleProfileService.decodeVin(Mockito.anyString()));
    }

    @Test(expected = Exception.class)
    public void stateChangedTest_throwException() throws ObserverMessageProcessFailureException {

        doThrow(Exception.class).when(envConfig).getStringValue(Mockito.any());
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setHarmanId("HARMANID123");
        deviceAssociation.setSerialNumber("12345");
        deviceAssociation.setUserId("testid");
        deviceAssociation.setSoftwareVersion("swversion");
        deviceAssociation.setAssociationStatus(AssociationStatus.DISASSOCIATED);
        deviceAssociation.setAssociatedOn(new Timestamp(System.currentTimeMillis()));
        deviceAssociation.setDeviceType("dongle");
        kafkaDeviceNotificationObserver.stateChanged(deviceAssociation);
    }

    @Test
    public void testSendEventToKafka() throws JsonProcessingException {

        when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("string");
        Future<RecordMetadata> value = null;
        when(producer.send(Mockito.any())).thenReturn(value);
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setSerialNumber("1234serialNumber");
        deviceInfo.setImei("1234imei");
        TriggerKafkaEventRequestDto triggerKafkaEventRequestDto =
            new TriggerKafkaEventRequestDto(deviceInfo, "activation-error", "eventId", deviceInfo.getImei());
        kafkaDeviceNotificationObserver.sendEventToKafka(triggerKafkaEventRequestDto);
        Mockito.verify(producer, times(1)).send(Mockito.any());
    }

    @Test
    public void sendReactivationVinEventToKafkaTest()
        throws JsonProcessingException, ObserverMessageProcessFailureException {

        when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("string");
        doReturn("return").when(envConfig).getStringValue(Mockito.any());

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setHarmanId("HARMANID123");
        deviceAssociation.setSerialNumber("12345");
        deviceAssociation.setUserId("testid");
        deviceAssociation.setSoftwareVersion("swversion");
        deviceAssociation.setAssociationStatus(AssociationStatus.DISASSOCIATED);
        deviceAssociation.setAssociatedOn(new Timestamp(System.currentTimeMillis()));
        deviceAssociation.setDeviceType("dongle");

        kafkaDeviceNotificationObserver.sendReactivationVinEventToKafka(deviceAssociation);
        Mockito.verify(producer, times(1)).send(Mockito.any());
    }


    @Test(expected = ObserverMessageProcessFailureException.class)
    public void sendReactivationVinEventToKafkaTest_throwException()
        throws ObserverMessageProcessFailureException, JsonProcessingException {

        when(objectMapper.writeValueAsString(Mockito.any())).thenThrow(JsonProcessingException.class);
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setHarmanId("HARMANID123");
        deviceAssociation.setSerialNumber("12345");
        deviceAssociation.setUserId("testid");
        deviceAssociation.setSoftwareVersion("swversion");
        deviceAssociation.setAssociationStatus(AssociationStatus.DISASSOCIATED);
        deviceAssociation.setAssociatedOn(new Timestamp(System.currentTimeMillis()));
        deviceAssociation.setDeviceType("dongle");

        kafkaDeviceNotificationObserver.sendReactivationVinEventToKafka(deviceAssociation);
        Mockito.verify(producer, times(1)).send(Mockito.any());
    }

    @Test
    public void sendReactivationVinEventToKafkaTest_throwProducerException()
        throws ObserverMessageProcessFailureException, JsonProcessingException {

        Mockito.doThrow(IllegalArgumentException.class).when(producer).send(Mockito.any(ProducerRecord.class));
        when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("string");
        doReturn("return").when(envConfig).getStringValue(Mockito.any());

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setId(ID);
        deviceAssociation.setHarmanId("HARMANID123");
        deviceAssociation.setSerialNumber("12345");
        deviceAssociation.setUserId("testid");
        deviceAssociation.setSoftwareVersion("swversion");
        deviceAssociation.setAssociationStatus(AssociationStatus.DISASSOCIATED);
        deviceAssociation.setAssociatedOn(new Timestamp(System.currentTimeMillis()));
        deviceAssociation.setDeviceType("dongle");

        kafkaDeviceNotificationObserver.sendReactivationVinEventToKafka(deviceAssociation);
        Mockito.verify(producer, times(1)).send(Mockito.any());
    }
}
