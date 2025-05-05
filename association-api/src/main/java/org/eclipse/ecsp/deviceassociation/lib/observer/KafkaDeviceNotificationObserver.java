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
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.config.internals.BrokerSecurityConfigs;
import org.eclipse.ecsp.common.config.EnvConfig;
import org.eclipse.ecsp.deviceassociation.dto.TriggerKafkaEventRequestDto;
import org.eclipse.ecsp.deviceassociation.lib.config.DeviceAssocationProperty;
import org.eclipse.ecsp.deviceassociation.lib.dao.DeviceAssociationDao;
import org.eclipse.ecsp.deviceassociation.lib.exception.ObserverMessageProcessFailureException;
import org.eclipse.ecsp.deviceassociation.lib.model.DeviceAssociation;
import org.eclipse.ecsp.deviceassociation.lib.model.EnvironmentProfile;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssetActivationEventData;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceAssociationEventData;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceInfo;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceInfoEvent;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceUserVinEventData;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceVinEventData;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceinfoEventData;
import org.eclipse.ecsp.deviceassociation.lib.service.Constants;
import org.eclipse.ecsp.deviceassociation.lib.service.VehicleProfileService;
import org.eclipse.ecsp.deviceassociation.lib.util.Pair;
import org.eclipse.ecsp.domain.Version;
import org.eclipse.ecsp.entities.AbstractEventData;
import org.eclipse.ecsp.entities.IgniteEventImpl;
import org.eclipse.ecsp.services.clientlib.HcpRestClientLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Properties;

/**
 * This class is an implementation of the DeviceAssociationObserver interface and serves as a Kafka device notification
 * observer.
 * It handles the state changes of device associations and sends corresponding events to Kafka.
 */
@Service("kafkaDeviceNotif")
public class KafkaDeviceNotificationObserver implements DeviceAssociationObserver {
    /**
     * Constant representing the key for vehicle specification in the Kafka message.
     * This key is used to identify and process vehicle specification data.
     */
    public static final String VEHICLESPECIFICATION = "vehiclespecification";
    /**
     * The name of the HTTP header used for authorization.
     * This header typically contains credentials for authenticating a request.
     */
    public static final String HEADER_NAME_AUTHORIZATION = "Authorization";
    /**
     * The format string for the Authorization header value.
     * This constant is used to construct the Authorization header
     * value in the format "Basic credentials" where credentials
     * is typically a Base64-encoded username and password.
     */
    public static final String HEADER_VALUE_AUTHORIZATION = "Basic %s";
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaDeviceNotificationObserver.class);
    private static String eventsSync = "device.events.sync.puts";
    private static String registerSyncPuts = "register.sync.puts";
    private static String noPuts = "no.puts";
    private static String numPutRetries = "num.put.retries";
    private static String retryBackOffMs = "retry.backoff.ms";
    private static String reconnectBackOffMaxMs = "reconnect.backoff.max.ms";
    private static String reconnectBackOffMs = "reconnect.backoff.ms";
    private static int numPutRetriesValue = 4000;
    private static int msTimeout30000 = 30000;
    private static int ms60000 = 60000;
    private static int sendBufferConfigValue = 817889280;
    private static int requestTimeoutMsConfigValue = 600000;
    private static int reconnectBackOffMaxMsValue = 100000;
    private static final String ERROR_CONVERTING_OBJECT_TO_JSON_STRING =
            "## Exception occurred while trying to convert the object to json string";
    private static final String KAFKA_VIN_TOPIC = "## Kafka vin topic: {}";

    /**
     * The Data Access Object (DAO) for managing device associations.
     * This is used to interact with the underlying database or persistence layer
     * to perform CRUD operations related to device associations.
     *
     * <p>This field is automatically injected by the Spring Framework using the
     * {@code @Autowired} annotation.
     */
    @Autowired
    protected DeviceAssociationDao deviceAssociationDao;
    /**
     * Service for managing vehicle profiles. This service is used to interact with
     * and retrieve information related to vehicle profiles.
     */
    @Autowired
    VehicleProfileService vehicleProfileService;
    @Autowired
    private DeviceAssociationObservable observable;
    @Autowired
    private EnvConfig<DeviceAssocationProperty> envConfig;
    @Autowired
    private HcpRestClientLibrary hcpRestClientLibrary;
    private Producer<String, String> producer;
    @Value("${vin_association_enabled:false}")
    private boolean vinAssocEnabled;
    @Value("${real_vehicle_profile_creation_for}")
    private String realVehicleProfileCreationFor;
    private ObjectMapper objectMapper;
    // private

    /**
     * Loads the Kafka properties and initializes the Kafka producer.
     * This method is called after the bean is constructed and all dependencies are injected.
     * It checks the profile and if it is not AWS Native, it loads the Kafka publisher.
     * It sets up the Kafka properties including the bootstrap servers, serializers, and other configurations.
     * If SSL is enabled, it also sets up the SSL configurations.
     * Finally, it initializes the Kafka producer and registers the observer.
     */
    @PostConstruct
    public void loadKafkaProperties() {
        String profile = envConfig.getStringValue(DeviceAssocationProperty.ENV_PROFILE);
        if (profile == null || !profile.equalsIgnoreCase(EnvironmentProfile.ENV_AWS_NATIVE.name())) {
            LOGGER.info("Loading Kafka publisher: {}", profile);
            String kafkaHost = envConfig.getStringValue(DeviceAssocationProperty.SERVICE_NOTIFICATION_KAFKA_HOST);

            Properties kafkaProperties = new Properties();

            kafkaProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost);
            kafkaProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
            kafkaProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
            kafkaProperties.put(eventsSync, false);
            kafkaProperties.put(registerSyncPuts, false);
            kafkaProperties.put(noPuts, false);
            kafkaProperties.put(numPutRetries, numPutRetriesValue);
            kafkaProperties.put(ProducerConfig.ACKS_CONFIG, "all");
            kafkaProperties.put(ProducerConfig.LINGER_MS_CONFIG, 0);
            kafkaProperties.put(ProducerConfig.BATCH_SIZE_CONFIG, 0);
            kafkaProperties.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, msTimeout30000);
            kafkaProperties.put(ProducerConfig.SEND_BUFFER_CONFIG, sendBufferConfigValue);
            kafkaProperties.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
            kafkaProperties.put(retryBackOffMs, ms60000);
            kafkaProperties.put(ProducerConfig.METADATA_MAX_AGE_CONFIG, ms60000);
            kafkaProperties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeoutMsConfigValue);
            kafkaProperties.put(reconnectBackOffMaxMs, reconnectBackOffMaxMsValue);
            kafkaProperties.put(reconnectBackOffMs, msTimeout30000);

            if (Boolean.parseBoolean(
                envConfig.getStringValue(DeviceAssocationProperty.SERVICE_NOTIFICATION_KAFKA_SSL_ENABLE))) {
                kafkaProperties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
                kafkaProperties.put(BrokerSecurityConfigs.SSL_CLIENT_AUTH_CONFIG,
                    envConfig.getStringValue(DeviceAssocationProperty.SERVICE_NOTIFICATION_KAFKA_SSL_CLIENT_AUTH));
                kafkaProperties.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG,
                    envConfig.getStringValue(DeviceAssocationProperty.SERVICE_NOTIFICATION_KAFKA_KEYSTORE));
                kafkaProperties.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG,
                    envConfig.getStringValue(DeviceAssocationProperty.SERVICE_NOTIFICATION_KAFKA_TRUSTSTORE));
                kafkaProperties.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, envConfig
                        .getStringValue(DeviceAssocationProperty.SERVICE_NOTIFICATION_KAFKA_TRUSTSTORE_PASSWORD));
                kafkaProperties.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, envConfig
                        .getStringValue(DeviceAssocationProperty.SERVICE_NOTIFICATION_KAFKA_KEYSTORE_PASSWORD));
                kafkaProperties.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG,
                        envConfig.getStringValue(DeviceAssocationProperty.SERVICE_NOTIFICATION_KAFKA_KEY_PASSWORD));
            }
            producer = new KafkaProducer<>(kafkaProperties);
            LOGGER.info("Producer initialized");

            objectMapper = new ObjectMapper();
            objectMapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));
            observable.register(this);
        }
    }

    /**
     * This method is called when the state of a device association changes.
     * It processes the device association object and performs specific actions based on the association status.
     *
     * @param deviceAssociation The device association object representing the changed state.
     * @throws ObserverMessageProcessFailureException If an error occurs while processing the message.
     */
    @Override
    public void stateChanged(DeviceAssociation deviceAssociation) throws ObserverMessageProcessFailureException {
        if (deviceAssociation == null) {
            return;
        }
        LOGGER.info("stateChanged: {}", deviceAssociation);
        try {
            switch (deviceAssociation.getAssociationStatus()) {
                case ASSOCIATED:
                    sendAssocOrDissassociateEventsToKafka(deviceAssociation);
                    sendSoftwareVersionToKafka(deviceAssociation);
                    sendVinEventToKafka(deviceAssociation);
                    sendAssetActivationEventToKafka(deviceAssociation);
                    LOGGER.debug("## realVehicleProfileCreationFor: {}", realVehicleProfileCreationFor);
                    if (VEHICLESPECIFICATION.equals(realVehicleProfileCreationFor)) {
                        String vin = deviceAssociationDao
                            .findAssociatedFactoryDataVin(deviceAssociation.getSerialNumber());
                        LOGGER.debug("## VIN: {} from db", vin);
                        if (StringUtils.isNotEmpty(vin)) {
                            sendVehicleSpecificationVinToKafka(deviceAssociation, vin);
                        } else {
                            LOGGER.debug("Vin is null or empty vin: {}", vin);
                        }
                    } else {
                        LOGGER.debug("### DID not set");
                    }
                    if (vinAssocEnabled) {
                        String vin = deviceAssociationDao.getAssociatedVin(deviceAssociation.getSerialNumber());
                        if (StringUtils.isNotEmpty(vin)) {
                            sendVinEventToKafkaWithRealVin(deviceAssociation, vin);
                        }
                    }
                    break;
                case DISASSOCIATED:
                    sendAssocOrDissassociateEventsToKafka(deviceAssociation);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            LOGGER.error("exception occurred while trying to send the message to kafka service - association");
            throw new ObserverMessageProcessFailureException(e.getMessage());
        }

    }

    /**
     * Sends association or disassociation events to Kafka.
     *
     * @param deviceAssociation The device association object containing the necessary information.
     */
    private void sendAssocOrDissassociateEventsToKafka(DeviceAssociation deviceAssociation) {
        sendMessage(
            new DeviceAssociationEventData(deviceAssociation.getUserId(), deviceAssociation.getHarmanId()),
            deviceAssociation.getHarmanId(),
            deviceAssociation.getAssociationStatus().getNotificationEventName(),
            deviceAssociation.getAssociatedOn(),
            envConfig.getStringValue(DeviceAssocationProperty.SERVICE_NOTIFICATION_KAFKA_TOPICNAME), Version.V1_0);
    }

    /**
     * Sends the software version of a device to Kafka.
     *
     * @param deviceAssociation The device association object containing the software version.
     */
    private void sendSoftwareVersionToKafka(DeviceAssociation deviceAssociation) {
        DeviceinfoEventData eventData = new DeviceinfoEventData();
        eventData.setValue(deviceAssociation.getSoftwareVersion());
        String kafkatopic = envConfig.getStringValue(DeviceAssocationProperty.KAFKA_EVENT_TOPIC);
        LOGGER.info("Kafka topic: {} ", kafkatopic);
        sendMessage(eventData, deviceAssociation.getHarmanId(), Constants.FIRMWARE_VERSION_EVENT_ID,
            deviceAssociation.getAssociatedOn(),
            kafkatopic, Version.V1_0);
    }

    /**
     * Sends a message to a Kafka topic with the specified parameters.
     *
     * @param eventdData  The event data to be sent.
     * @param key         The key associated with the message.
     * @param eventId     The ID of the event.
     * @param timestamp   The timestamp of the event.
     * @param topicName   The name of the Kafka topic.
     * @param version     The version of the event.
     */
    private void sendMessage(AbstractEventData eventdData, String key, String eventId,
                             Timestamp timestamp, String topicName, Version version) {

        String message = null;
        try {
            IgniteEventImpl igniteEvent = new IgniteEventImpl();
            igniteEvent.setEventData(eventdData);
            igniteEvent.setEventId(eventId);
            igniteEvent.setVersion(version);
            igniteEvent.setTimestamp(timestamp.getTime());
            igniteEvent.setVehicleId(key);
            message = objectMapper.writeValueAsString(igniteEvent);
            LOGGER.info("## SendMessage JSON: {} with the key: {}", message, key);
        } catch (JsonGenerationException | JsonMappingException e) {
            LOGGER.error(ERROR_CONVERTING_OBJECT_TO_JSON_STRING, e);
        }  catch (IOException e) {
            LOGGER.error(ERROR_CONVERTING_OBJECT_TO_JSON_STRING, e);
        }
        sendToKafkaTopic(topicName, key, message);
    }

    /**
     * Sends a message to a Kafka topic.
     *
     * @param topicName The name of the Kafka topic.
     * @param key The key of the message.
     * @param message The message to be sent.
     */
    private void sendToKafkaTopic(String topicName, String key, String message) {
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topicName, key, message);
        try {
            producer.send(producerRecord);
        } catch (Exception e) {
            LOGGER.error("## Error occurred while trying to send message to kafka", e);
        }
    }

    /**
     * Sends a VIN event to Kafka for the given device association.
     *
     * @param deviceAssociation The device association object.
     */
    private void sendVinEventToKafka(DeviceAssociation deviceAssociation) {
        DeviceUserVinEventData deviceVinEventData = new DeviceUserVinEventData();
        deviceVinEventData.setDummy(Boolean.TRUE);
        deviceVinEventData.setValue(Constants.PLATFORM_GENERATED_VIN);
        deviceVinEventData.setType(Constants.TYPE_UNAVAILABLE);
        deviceVinEventData.setUserId(deviceAssociation.getUserId());
        deviceVinEventData.setDeviceType(deviceAssociation.getDeviceType());
        String kafkaTopic = envConfig.getStringValue(DeviceAssocationProperty.KAFKA_VIN_TOPIC);
        LOGGER.debug(KAFKA_VIN_TOPIC, kafkaTopic);
        sendMessage(deviceVinEventData, deviceAssociation.getHarmanId(), Constants.VIN_EVENT_ID,
            deviceAssociation.getAssociatedOn(), kafkaTopic, Version.V1_0);
    }

    /**
     * Sends a reactivation VIN event to Kafka.
     *
     * @param deviceAssociation The device association object.
     * @throws ObserverMessageProcessFailureException If an exception occurs while sending the message to Kafka.
     */
    public void sendReactivationVinEventToKafka(DeviceAssociation deviceAssociation)
        throws ObserverMessageProcessFailureException {
        try {
            LOGGER.info("## Inside sendReactivationVINEventToKafka: START");
            sendVinEventToKafka(deviceAssociation);
            LOGGER.info("## Inside sendReactivationVINEventToKafka: END");
        } catch (Exception e) {
            LOGGER.error(
                "exception occurred while trying to send the message to kafka service - association during"
                    + " reactivation");
            throw new ObserverMessageProcessFailureException(e.getMessage());
        }
    }

    /**
     * Sends an asset activation event to Kafka.
     *
     * @param deviceAssociation The device association object containing the necessary information.
     */
    private void sendAssetActivationEventToKafka(DeviceAssociation deviceAssociation) {

        AssetActivationEventData assetActivationEventData = new AssetActivationEventData();
        assetActivationEventData.setHarmanId(deviceAssociation.getHarmanId());
        assetActivationEventData.setSerialNumber(deviceAssociation.getSerialNumber());
        assetActivationEventData.setUserId(deviceAssociation.getUserId());
        //read country if vin enabled else set it to null/empty
        assetActivationEventData.setCountry(getCountryCode(deviceAssociation.getId()));
        assetActivationEventData.setDeviceType(deviceAssociation.getDeviceType());
        String kafkaTopic = envConfig.getStringValue(DeviceAssocationProperty.KAFKA_ASSET_ACTIVATION_TOPIC);
        LOGGER.debug("## Kafka Asset Activation topic: {}", kafkaTopic);
        sendMessage(assetActivationEventData, deviceAssociation.getHarmanId(), Constants.ASSET_ACTIVATIONEVENT_ID,
            deviceAssociation.getAssociatedOn(), kafkaTopic, Version.V1_0);
    }

    /**
     * Retrieves the country code associated with the given ID.
     *
     * @param id The ID of the device.
     * @return The country code associated with the device, or null if not found.
     */
    private String getCountryCode(long id) {
        String country = null;
        if (vinAssocEnabled) {
            country = deviceAssociationDao.getCountryCode(id);
        }
        return country;
    }

    /**
     * Sends the vehicle specification VIN to Kafka.
     *
     * @param deviceAssociation The device association object.
     * @param vin The VIN (Vehicle Identification Number) to be sent.
     */
    private void sendVehicleSpecificationVinToKafka(DeviceAssociation deviceAssociation, String vin) {
        LOGGER.debug("## sendVehicleSpecificationVinToKafka - START vin: {}", vin);
        DeviceVinEventData deviceVinEventData = new DeviceVinEventData();
        deviceVinEventData.setDummy(Boolean.FALSE);
        deviceVinEventData.setValue(vin);
        deviceVinEventData.setType(Constants.TYPE_UNAVAILABLE);
        String kafkaTopic = envConfig.getStringValue(DeviceAssocationProperty.KAFKA_VIN_TOPIC);
        LOGGER.info(KAFKA_VIN_TOPIC, kafkaTopic);
        sendMessage(deviceVinEventData, deviceAssociation.getHarmanId(), Constants.VIN_EVENT_ID,
            deviceAssociation.getAssociatedOn(), kafkaTopic, Version.V1_0);
        LOGGER.debug("## sendVehicleSpecificationVinToKafka - END");
    }

    /**
     * Sends a VIN event to Kafka with the real VIN.
     *
     * @param deviceAssociation The device association object.
     * @param vin The VIN (Vehicle Identification Number) to send.
     */
    private void sendVinEventToKafkaWithRealVin(DeviceAssociation deviceAssociation, String vin) {
        LOGGER.info("## sendVINEventToKafkaWithRealVin - START vin: {}", vin);
        DeviceVinEventData deviceVinEventData = new DeviceVinEventData();
        deviceVinEventData.setDummy(Boolean.FALSE);
        deviceVinEventData.setValue(vin);
        deviceVinEventData.setType(Constants.TYPE_UNAVAILABLE);
        LOGGER.info("## Decode vin: {}", vin);
        Pair<String, String> decodeVinPair = vehicleProfileService.decodeVin(vin);
        String modelCode;
        String modelNameAfterDecodeVin;
        if (decodeVinPair != null) {
            modelCode = decodeVinPair.getElement1();
            modelNameAfterDecodeVin = decodeVinPair.getElement2();
            LOGGER.info("## After Decode vin: {}, modelCode: {}, modelName:{} ", vin, modelCode,
                modelNameAfterDecodeVin);
        } else {
            LOGGER.error("## Unable to decode vin");
            // Functional: It should be null if vin decoding fails.
            modelNameAfterDecodeVin = null;
        }
        deviceVinEventData.setModelName(modelNameAfterDecodeVin);
        String kafkaTopic = envConfig.getStringValue(DeviceAssocationProperty.KAFKA_VIN_TOPIC);
        LOGGER.info(KAFKA_VIN_TOPIC, kafkaTopic);
        sendMessage(deviceVinEventData, deviceAssociation.getHarmanId(), Constants.VIN_EVENT_ID,
            deviceAssociation.getAssociatedOn(), kafkaTopic, Version.V1_0);
    }

    /**
     * Sends an event to Kafka.
     *
     * @param triggerKafkaEventRequestDto The request DTO containing the device information and Kafka topic.
     */
    public void sendEventToKafka(TriggerKafkaEventRequestDto triggerKafkaEventRequestDto) {

        DeviceInfoEvent deviceInfoEvent = new DeviceInfoEvent();
        DeviceInfo deviceInfo = triggerKafkaEventRequestDto.getDeviceInfo();
        deviceInfoEvent.setSerialNumber(deviceInfo.getSerialNumber());
        deviceInfoEvent.setDeviceType(deviceInfo.getDeviceType());
        deviceInfoEvent.setHarmanId(deviceInfo.getHarmanId());
        deviceInfoEvent.setImei(deviceInfo.getImei());
        deviceInfoEvent.setSoftwareVersion(deviceInfo.getSoftwareVersion());
        deviceInfoEvent.setVin(deviceInfo.getVin());
        deviceInfoEvent.setSsid(deviceInfo.getSsid());
        deviceInfoEvent.setIccid(deviceInfo.getIccid());
        deviceInfoEvent.setBssid(deviceInfo.getBssid());
        deviceInfoEvent.setMsisdn(deviceInfo.getMsisdn());
        deviceInfoEvent.setImsi(deviceInfo.getImsi());
        deviceInfoEvent.setProductType(deviceInfo.getProductType());
        deviceInfoEvent.setHardwareVersion(deviceInfo.getHardwareVersion());
        String kafkaTopic = triggerKafkaEventRequestDto.getTopic();
        LOGGER.debug("## sendEventToKafka topic: {}", kafkaTopic);
        sendMessage(deviceInfoEvent, triggerKafkaEventRequestDto.getKey(), triggerKafkaEventRequestDto.getEventId(),
            new Timestamp(System.currentTimeMillis()), kafkaTopic, Version.V1_0);

    }
}