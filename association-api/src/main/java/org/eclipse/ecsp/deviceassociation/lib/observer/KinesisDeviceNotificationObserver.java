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

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.auth.ContainerCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.amazonaws.services.kinesis.model.PutRecordResult;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.eclipse.ecsp.common.config.EnvConfig;
import org.eclipse.ecsp.deviceassociation.lib.config.DeviceAssocationProperty;
import org.eclipse.ecsp.deviceassociation.lib.exception.ObserverMessageProcessFailureException;
import org.eclipse.ecsp.deviceassociation.lib.model.AssociationStatus;
import org.eclipse.ecsp.deviceassociation.lib.model.DeviceAssociation;
import org.eclipse.ecsp.deviceassociation.lib.model.EnvironmentProfile;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceAssociationEvent;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceAssociationEvent.EventData;
import org.eclipse.ecsp.deviceassociation.lib.util.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for observing device association events and publishing them to a Kinesis stream.
 */
@Service
public class KinesisDeviceNotificationObserver implements DeviceAssociationObserver {

    private static final String ERROR_CONVERTING_OBJECT_TO_STRING =
            "exception occured while trying to conver the object to hson string";
    private static final Logger LOGGER = LoggerFactory.getLogger(KinesisDeviceNotificationObserver.class);
    /**
     * The AmazonKinesisClient instance used to interact with the Amazon Kinesis service.
     */
    public AmazonKinesisClient amazonKinesisClient;
    @Autowired
    private DeviceAssociationObservable observable;
    @Autowired
    private EnvConfig<DeviceAssocationProperty> envConfig;
    private ObjectMapper objectMapper;

    /**
     * Load Kinesis Properties.
     */
    @PostConstruct
    public void loadKinesisProperties() {
        String profile = envConfig.getStringValue(DeviceAssocationProperty.ENV_PROFILE);
        // The environment defaults to non AWS native
        if (profile != null && EnvironmentProfile.ENV_AWS_NATIVE.name().equalsIgnoreCase(profile)) {
            LOGGER.info("Received environment profile {}. Hence, publishing to Kinesis stream", profile);
            observable.register(this);
            objectMapper = new ObjectMapper();
            String regionName = envConfig.getStringValue(DeviceAssocationProperty.KINESIS_ECS_REGION);
            LOGGER.info("Kinesis region: {}", regionName);
            AWSCredentialsProviderChain credProviderChain = new AWSCredentialsProviderChain(
                getCredentialsProviderList());
            AmazonKinesisClientBuilder clientBuilder = AmazonKinesisClient.builder();
            clientBuilder.setCredentials(credProviderChain);
            clientBuilder.setRegion(regionName);
            amazonKinesisClient = (AmazonKinesisClient) clientBuilder.build();

        }

    }

    /**
     * Fetch AWS Credentials Provider list.
     *
     * @return providerList The list of AWS credentials providers.
     */
    public List<AWSCredentialsProvider> getCredentialsProviderList() {

        List<AWSCredentialsProvider> providerList = new ArrayList<>();
        providerList.add(new ClasspathPropertiesFileCredentialsProvider());
        providerList.add(InstanceProfileCredentialsProvider.getInstance());
        // getting the credentials from ECS container
        providerList.add(new ContainerCredentialsProvider());

        return providerList;

    }

    /**
     * Called when the state of device association changes.
     *
     * @param deviceAssociation The device association object.
     * @throws ObserverMessageProcessFailureException If an error occurs while processing the message.
     */
    @Override
    public void stateChanged(DeviceAssociation deviceAssociation) throws ObserverMessageProcessFailureException {
        if (deviceAssociation == null) {
            return;
        }

        LOGGER.info("stateChanged - {}", deviceAssociation);
        try {
            if (deviceAssociation.getAssociationStatus().equals(AssociationStatus.ASSOCIATED)) {
                sendMessage(
                    new DeviceAssociationEvent(deviceAssociation.getAssociationStatus().getNotificationEventName(),
                        DeviceAssociationEvent.VERSION, deviceAssociation.getAssociatedOn().getTime(),
                        DateTimeUtils.getGmtOffset(deviceAssociation.getAssociatedOn()),
                        new EventData(deviceAssociation.getUserId(), deviceAssociation.getHarmanId())),
                    deviceAssociation.getHarmanId());
            }

            if (deviceAssociation.getAssociationStatus().equals(AssociationStatus.DISASSOCIATED)) {
                sendMessage(
                    new DeviceAssociationEvent(deviceAssociation.getAssociationStatus().getNotificationEventName(),
                        DeviceAssociationEvent.VERSION, deviceAssociation.getDisassociatedOn().getTime(),
                        DateTimeUtils.getGmtOffset(deviceAssociation.getDisassociatedOn()),
                        new EventData(deviceAssociation.getUserId(), deviceAssociation.getHarmanId())),
                    deviceAssociation.getUserId());

            }
        } catch (Exception e) {
            LOGGER.error("exception occured while trying to send the message to notificaiton service about association",
                e);
        }

    }

    /**
     * Sends a message to the Kinesis stream.
     *
     * @param deviceAssociationEvent The device association event.
     * @param key                    The partition key.
     */
    private void sendMessage(DeviceAssociationEvent deviceAssociationEvent, String key) {

        String message = null;
        try {
            message = objectMapper.writeValueAsString(new Object[]{deviceAssociationEvent});
            LOGGER.info("sendMessage JSON- {} with the key: {}", message, key);
        } catch (JsonGenerationException | JsonMappingException e) {
            LOGGER.error(ERROR_CONVERTING_OBJECT_TO_STRING, e);
        }  catch (IOException e) {
            LOGGER.error(ERROR_CONVERTING_OBJECT_TO_STRING, e);
        }
        String streamName = envConfig.getStringValue(DeviceAssocationProperty.KINESIS_STREAM_NAME);
        publishSync(streamName, key, message);
    }

    /**
     * Publishes a message synchronously to the Kinesis stream.
     *
     * @param topic The topic of the message.
     * @param key   The partition key.
     * @param value The message value.
     */
    public void publishSync(String topic, String key, Object value) {
        PutRecordRequest kinesisPutRequest = new PutRecordRequest();
        kinesisPutRequest.setStreamName(topic);
        // 2.29 Release - Adding null check to resolve NP_NULL_PARAM_DEREF Sonar bug
        // 2.33 Release - Sonar DM_DEFAULT_ENCODING code smell fix
        kinesisPutRequest.setData(
            value != null ? ByteBuffer.wrap(value.toString().getBytes(StandardCharsets.UTF_8)) : null);
        kinesisPutRequest.setPartitionKey(key);

        PutRecordResult kinesisPutResult = amazonKinesisClient.putRecord(kinesisPutRequest);
        // 2.29 Release - Adding null check in LOGGER.info to resolve NP_NULL_PARAM_DEREF Sonar bug
        LOGGER.info("AWS request ID for putting the msg: {} on kinesis is: {} ",
                (value != null ? value.toString() : null), kinesisPutResult.getSdkResponseMetadata().getRequestId());
        LOGGER.info("AWS HTTP status response for putting the msg: {} on kinesis is {}",
                (value != null ? value.toString() : null), kinesisPutResult.getSdkHttpMetadata().getHttpStatusCode());
        LOGGER.info("{} has been put on to kinesis stream {} with sequence number: {} and shardId is: {} ",
                (value != null ? value.toString() : null), topic, kinesisPutResult.getSequenceNumber(),
                kinesisPutResult.getShardId());
    }
}