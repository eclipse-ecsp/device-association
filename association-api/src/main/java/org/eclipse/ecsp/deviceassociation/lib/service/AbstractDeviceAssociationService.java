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

package org.eclipse.ecsp.deviceassociation.lib.service;

import org.eclipse.ecsp.auth.lib.dao.DeviceInfoSharedDao;
import org.eclipse.ecsp.common.config.EnvConfig;
import org.eclipse.ecsp.deviceassociation.lib.config.DeviceAssocationProperty;
import org.eclipse.ecsp.deviceassociation.lib.dao.DeviceAssociationDao;
import org.eclipse.ecsp.deviceassociation.lib.exception.DuplicateDeviceAssociationRequestException;
import org.eclipse.ecsp.deviceassociation.lib.exception.NoSuchEntityException;
import org.eclipse.ecsp.deviceassociation.lib.exception.ObserverMessageProcessFailureException;
import org.eclipse.ecsp.deviceassociation.lib.model.AssociationStatus;
import org.eclipse.ecsp.deviceassociation.lib.model.DeviceAssociation;
import org.eclipse.ecsp.deviceassociation.lib.observer.DeviceAssociationObservable;
import org.eclipse.ecsp.deviceassociation.lib.observer.KafkaDeviceNotificationObserver;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociateDeviceRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociateDeviceResponse;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociationDetailsRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociationDetailsResponse;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociationResponse;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceState;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.User;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.UserProfile;
import org.eclipse.ecsp.deviceassociation.lib.rest.support.HttpPlatformUtil;
import org.eclipse.ecsp.services.clientlib.HcpRestClientLibrary;
import org.eclipse.ecsp.services.deviceactivation.dao.DeviceActivationStateDao;
import org.eclipse.ecsp.services.factorydata.dao.DeviceInfoFactoryDataDao;
import org.eclipse.ecsp.services.shared.dao.HcpInfoDao;
import org.eclipse.ecsp.services.shared.deviceinfo.model.DeviceAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * This abstract class represents a device association service.
 * It provides common functionality and abstract methods that need to be implemented by concrete subclasses.
 */
@Service
@Transactional
public abstract class AbstractDeviceAssociationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDeviceAssociationService.class);
    private static final String CARRIAGE_AND_NEWLINE_REGEX = "[\r\n]";
    private static int count = -1;

    /**
     * The Data Access Object (DAO) for managing device associations.
     * This is a protected field that allows subclasses to interact with
     * the persistence layer for device association-related operations.
     *
     * <p>It is automatically injected by the Spring framework using the
     * {@code @Autowired} annotation.
     */
    @Autowired
    protected DeviceAssociationDao deviceAssociationDao;

    /**
     * The observable instance used to monitor and notify changes in device associations.
     * This is an autowired dependency, which means it will be automatically injected
     * by the Spring framework at runtime.
     */
    @Autowired
    protected DeviceAssociationObservable observable;

    /**
     * The environment configuration for device association properties.
     * This is an autowired dependency that provides access to configuration
     * properties specific to device association.
     *
     * @see EnvConfig
     * @see DeviceAssocationProperty
     */
    @Autowired
    protected EnvConfig<DeviceAssocationProperty> envConfig;

    /**
     * The HcpRestClientLibrary instance used for making REST API calls.
     * This is an autowired dependency, which means it will be automatically
     * injected by the Spring framework at runtime.
     */
    @Autowired
    protected HcpRestClientLibrary hcpRestClientLibrary;

    /**
     * The Data Access Object (DAO) for accessing and managing device information
     * factory data. This is used to interact with the underlying database or
     * persistence layer to perform CRUD operations related to device information.
     *
     * <p>This field is automatically injected by the Spring Framework using the
     * {@code @Autowired} annotation.
     */
    @Autowired
    protected DeviceInfoFactoryDataDao deviceInfoFactoryDataDao;


    /**
     * Data Access Object (DAO) for accessing HCP (Health Care Provider) information.
     * This DAO is used to interact with the database for operations related to HCP data.
     * It is automatically injected by the Spring framework using the @Autowired annotation.
     */
    @Autowired
    protected HcpInfoDao hcpInfoDao;
    /**
     * The Data Access Object (DAO) for managing device activation states.
     * This is an autowired dependency, which means it will be automatically
     * injected by the Spring framework at runtime.
     */
    @Autowired
    protected DeviceActivationStateDao deviceActivationStateDao;
    /**
     * The service for managing device associations with factory data.
     * This is an autowired dependency, which means it will be automatically
     * injected by the Spring framework at runtime.
     */
    @Autowired
    protected DeviceAssociationWithFactDataServiceV2 deviceAssocFactoryService;
    /**
     * The service for managing device activations.
     * This is an autowired dependency, which means it will be automatically
     * injected by the Spring framework at runtime.
     */
    @Autowired
    protected DeviceActivationService deviceActivationService;
    /**
     * The Data Access Object (DAO) for managing device information.
     * This is an autowired dependency, which means it will be automatically
     * injected by the Spring framework at runtime.
     */
    @Autowired
    protected DeviceInfoSharedDao deviceInfoDao;
    /**
     * The Kafka device notification observer used for sending notifications to Kafka.
     * This is an autowired dependency, which means it will be automatically
     * injected by the Spring framework at runtime.
     */
    @Autowired
    protected KafkaDeviceNotificationObserver kafkaDeviceNotifier;

    
    /**
     * Associates a device with the given request details.
     *
     * @param associateDeviceRequest The request object containing details for the device association.
     * @return An {@link AssociateDeviceResponse} containing the result of the association operation.
     * @throws NoSuchEntityException If the entity specified in the request does not exist.
     * @throws DuplicateDeviceAssociationRequestException If a duplicate association request is detected.
     * @throws ObserverMessageProcessFailureException If there is a failure in processing observer messages
     *      during the association.
     */
    public abstract AssociateDeviceResponse associateDevice(AssociateDeviceRequest associateDeviceRequest)
        throws NoSuchEntityException, DuplicateDeviceAssociationRequestException,
            ObserverMessageProcessFailureException;

    /**
     * Retrieves a list of associated devices for a given user.
     *
     * @param userId the ID of the user
     * @return a list of DeviceAssociation objects representing the associated devices
     */
    public abstract List<DeviceAssociation> getAssociatedDevicesForUser(String userId);

    /**
     * Retrieves the association details for the given association ID and user ID.
     *
     * @param associationId The ID of the association.
     * @param userId The ID of the user.
     * @return The DeviceAssociation object containing the association details.
     */
    public abstract DeviceAssociation getAssociationDetails(long associationId, String userId);

    /**
     * Retrieves the association details based on the provided association details request.
     *
     * @param associationDetailsRequest The association details request object.
     * @return The association details response object, or null if the request is null or no association details are
     *      found.
     */
    public AssociationDetailsResponse getAssociationDetails(AssociationDetailsRequest associationDetailsRequest) {
        if (null == associationDetailsRequest) {
            return null;
        }
        LinkedHashMap<String, Object> orderedMap = generateMapFromAssociationRequest(associationDetailsRequest);
        if (orderedMap.isEmpty()) {
            return null;
        }
        LOGGER.debug("Going to fetchAssociationDetails withMap: {}", orderedMap);
        final List<AssociationDetailsResponse> associationDetailsResponseList =
            deviceAssociationDao.fetchAssociationDetails(orderedMap, true);
        if (CollectionUtils.isEmpty(associationDetailsResponseList)) {
            LOGGER.error("No element found: {}", associationDetailsResponseList);
            return null;
        } else {
            /*
             * Defect: 251473 fix
             *
             * Due to existing Device Replace api:
             * IF associationDetailsResponseList have both "ASSOCIATED" and "DISASSOCIATED" association status
             * THEN
             *      RETURN AssociationDetailsResponse object which has associationStatus = 'ASSOCIATED'
             * ELSE
             *      RETURN AssociationDetailsResponse object which has associationStatus =  'DISASSOCIATED'
             * FI
             */
            for (AssociationDetailsResponse adr : associationDetailsResponseList) {
                if (Constants.ASSOCIATED.equals(adr.getAssociationStatus())
                    && Constants.ACTIVE.equals(adr.getDeviceDetail().getState())) {
                    return adr;
                }
            }
            return associationDetailsResponseList.get(0);
        }
    }

    /**
     * Disassociates a device from a user.
     *
     * @param associationId The ID of the association.
     * @param userId The ID of the user.
     * @return The number of associations disassociated.
     * @throws ObserverMessageProcessFailureException If an error occurs during the disassociation process.
     */
    public int disassociate(long associationId, String userId) throws ObserverMessageProcessFailureException {
        LOGGER.debug("## disassociate - START associationID: {}, userID: {}", associationId, userId);
        DeviceAssociation deviceAssociation = deviceAssociationDao.find(associationId, userId);
        if (deviceAssociation == null || !deviceAssociation.getUserId().equals(userId)) {
            return count;
        }
        return disassociate(deviceAssociation, userId);
    }

    /**
     * Disassociates the given device association with the specified requester.
     *
     * @param deviceAssociation The device association to be disassociated.
     * @param requestedBy The requester who initiated the disassociation.
     * @return The number of updated device associations.
     * @throws ObserverMessageProcessFailureException If an error occurs during the observer message processing.
     */
    private int disassociate(DeviceAssociation deviceAssociation, String requestedBy)
        throws ObserverMessageProcessFailureException {
        int updatedCount = count;
        if (!deviceAssociation.getAssociationStatus().equals(AssociationStatus.DISASSOCIATED)) {
            deviceAssociation.setAssociationStatus(AssociationStatus.DISASSOCIATED);
            deviceAssociation.setDisassociatedBy(requestedBy);
            deviceAssociation.setModifiedBy(requestedBy);
            deviceAssociation.setDisassociatedOn(new Timestamp(System.currentTimeMillis()));
            deviceAssociation.setDeviceAuthV2Deactivate(true);
            observable.notify(deviceAssociation);
            updatedCount = deviceAssociationDao.updateDeviceAssociation(deviceAssociation);
        }
        return updatedCount;
    }

    /**
     * Retrieves the user details associated with a specific vehicle.
     *
     * @param harmanId The unique identifier of the vehicle.
     * @return A list of User objects containing the user details associated with the vehicle.
     */
    public List<User> getUserDetailsOfVehicle(String harmanId) {
        List<String> userIds = deviceAssociationDao.getUserDetails(harmanId);
        if (userIds == null || userIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<User> users = new ArrayList<>();
        for (String userId : userIds) {
            users.add(new User(userId));
        }
        return users;

    }

    /**
     * Checks if a device with the given serial number is already associated to any user.
     *
     * @param serialNumber the serial number of the device to check
     * @return an AssociationResponse object indicating whether the device is already associated or not
     */
    public AssociationResponse isDeviceAlreadyAssociated(String serialNumber) {

        return (deviceAssociationDao.isDeviceCurrentlyAssociatedToAnyUser(serialNumber))
            ? new AssociationResponse(true) : new AssociationResponse(false);

    }

    /**
     * Handles the device state change event.
     *
     * @param deviceState The new state of the device.
     * @param userId The ID of the user associated with the device.
     * @throws ObserverMessageProcessFailureException If an error occurs during the observer message processing.
     * @throws NoSuchEntityException If the entity specified in the request does not exist.
     */
    public void deviceStateChanged(DeviceState deviceState, String userId)
            throws ObserverMessageProcessFailureException, NoSuchEntityException {
        if (deviceState == null || deviceState.getState() == null) {
            return;
        }
        String deviceStateData =
                deviceState.toString().replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "");
        LOGGER.debug("## deviceStateChanged Service START deviceState: {}", deviceStateData);

        if (deviceState.getState().equals(DeviceState.State.DEACTIVATED)) {
            disassociateExistingUsers(deviceState.getSerialNumber(), userId, true);
            return;
        }
        if (deviceState.getState().equals(DeviceState.State.ACTIVATED)) {
            List<DeviceAssociation> retrieveAssociatedList =
                deviceAssociationDao.retrieveAssociatedUser(deviceState.getSerialNumber(),
                    deviceState.isReactivationFlag());
            if (validateRetrievedAssociatedList(retrieveAssociatedList, deviceState, userId)) {
                return;
            }
            DeviceAssociation dbDeviceAssociation = retrieveAssociatedList.get(0);

            int updatedCount = ((deviceState.isReactivationFlag()) ? 1 :
                deviceAssociationDao.updateHarmaId(deviceState.getHarmanId(), dbDeviceAssociation.getId(), userId));

            LOGGER.debug("data count :: {}", updatedCount);
            if (updatedCount > 0) {
                DeviceAssociation deviceAssociation = new DeviceAssociation();
                deviceAssociation.setId(dbDeviceAssociation.getId());
                deviceAssociation.setHarmanId(deviceState.getHarmanId());
                deviceAssociation.setSerialNumber(deviceState.getSerialNumber());
                deviceAssociation.setUserId(dbDeviceAssociation.getUserId());
                deviceAssociation.setSoftwareVersion(deviceState.getSoftwareVersion());
                deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
                deviceAssociation.setAssociatedOn(new Timestamp(System.currentTimeMillis()));
                deviceAssociation.setDeviceType(deviceState.getDeviceType());

                if (deviceState.isReactivationFlag()) {
                    kafkaDeviceNotifier.sendReactivationVinEventToKafka(deviceAssociation);
                } else {
                    observable.notify(deviceAssociation);
                }
            }
            LOGGER.debug("number of records updated for newly activated device, updatedCount: {}", updatedCount);
        }

    }

    /**
     * Validates the retrieved associated list.
     *
     * @param retrieveAssociatedList The list of retrieved device associations.
     * @param deviceState The device state.
     * @param userId The user ID.
     * @return {@code true} if the retrieved associated list is empty, {@code false} otherwise.
     * @throws NoSuchEntityException If there is a database integrity error.
     */
    private boolean validateRetrievedAssociatedList(List<DeviceAssociation> retrieveAssociatedList,
                                                    DeviceState deviceState, String userId)
        throws NoSuchEntityException {
        String deviceStateData = deviceState.toString().replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "");
        if (CollectionUtils.isEmpty(retrieveAssociatedList)) {
            LOGGER.debug("Mostly a head unit activation with out association: deviceState: {}, userID: {}",
                deviceStateData,
                (userId == null) ? null : userId.replaceAll(CARRIAGE_AND_NEWLINE_REGEX, ""));
            return true;
        }
        if (retrieveAssociatedList.size() > 1) {
            LOGGER.error("Association data - Database Integrity Error. There is more than one record.");
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Error in activating device - ").append(MessageConstants.DATABASE_INTEGRITY_ERROR);
            throw new NoSuchEntityException(errorMessage.toString());
        }
        return false;
    }

    /**
     * Disassociates existing users/owners for a given device.
     *
     * @param serialNumber The serial number of the device.
     * @param userId The ID of the user to disassociate.
     * @param isStateChangeRequest Indicates whether the disassociation is a state change request.
     * @throws ObserverMessageProcessFailureException If an error occurs during the disassociation process.
     */
    protected void disassociateExistingUsers(String serialNumber, String userId, boolean isStateChangeRequest)
        throws ObserverMessageProcessFailureException {

        DeviceAssociation deviceAssociation = deviceAssociationDao.findValidAssociations(serialNumber);

        if (deviceAssociation != null) {
            LOGGER.debug("deviceAssociation in db: {}", deviceAssociation);
            if (isStateChangeRequest) {
                deviceAssociation.setAuthsRequest(true);
            }
            disassociate(deviceAssociation, userId);
        }
        // disassociate existing users/owners for this device

    }

    /**
     * Retrieves the device attributes for the specified Harman ID.
     *
     * @param harmanId The Harman ID of the device.
     * @return The device attributes, or null if the Harman ID is null.
     */
    protected DeviceAttributes getDeviceAttributes(String harmanId) {
        if (harmanId == null) {
            return null;
        }
        DeviceAttributes deviceAttributes = new DeviceAttributes();

        String baseUrl = envConfig.getStringValue(DeviceAssocationProperty.SERVICE_DEVICEINFO_REST_URL_BASE);
        String deviceInfoPath = envConfig.getStringValue(DeviceAssocationProperty.SERVICE_DEVICEINFO_REST_DEVICE_INFO);
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            ResponseEntity<DeviceAttributes> responseEntity = hcpRestClientLibrary.doGet(
                baseUrl + deviceInfoPath + harmanId,
                HttpPlatformUtil.generateAuthHeader(envConfig, hcpRestClientLibrary),
                DeviceAttributes.class);
            if (responseEntity.getBody() != null && responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                DeviceAttributes body = responseEntity.getBody();
                deviceAttributes = body != null ? body : deviceAttributes;
                mask(deviceAttributes);
            }
        } catch (Exception e) {
            LOGGER.error("exception occurred while trying to retrieve device details for " + harmanId, e);
        }
        return deviceAttributes;
    }

    /**
     * Masks the sensitive attributes of the given DeviceAttributes object.
     * Sets the following attributes to null: bodytype, HarmanId, lastlogintime, series, vehicletype, country, and
     * manufacturer.
     *
     * @param deviceAttributes The DeviceAttributes object to be masked.
     */
    private void mask(DeviceAttributes deviceAttributes) {
        deviceAttributes.setBodytype(null);
        deviceAttributes.setHarmanId(null);
        deviceAttributes.setLastlogintime(null);
        deviceAttributes.setSeries(null);
        deviceAttributes.setVehicletype(null);
        deviceAttributes.setCountry(null);
        deviceAttributes.setManufacturer(null);

    }

    /**
     * Updates the user details using the provided token and user ID.
     *
     * @param token The authentication token for the user.
     */
    public void updateUserDetails(String token) {
        try {
            String sdpUrlBase = envConfig.getStringValue(DeviceAssocationProperty.SERVICE_SDP_PROFILE_URL_BASE);
            String getProfileUrl = envConfig.getStringValue(DeviceAssocationProperty.SERVICE_SDP_REST_PROFILE);
            hcpRestClientLibrary.doGet(sdpUrlBase + getProfileUrl, generateSdpHttpHeader(token), UserProfile.class);
        } catch (Exception e) {
            LOGGER.warn("unable to fetch the latest profile information..", e);
        }

    }

    /**
     * Generates the HTTP headers for SDP requests.
     *
     * @param token The authorization token to be included in the headers.
     * @return The generated HttpHeaders object containing the authorization header.
     */
    private HttpHeaders generateSdpHttpHeader(String token) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        return httpHeaders;
    }

    /**
     * Generates a LinkedHashMap from the given AssociationDetailsRequest object.
     *
     * @param associationDetailsRequest The AssociationDetailsRequest object containing the association details.
     * @return A LinkedHashMap representing the association details.
     */
    private LinkedHashMap<String, Object> generateMapFromAssociationRequest(
        AssociationDetailsRequest associationDetailsRequest) {
        LinkedHashMap<String, Object> orderedMap = new LinkedHashMap<>();
        if (associationDetailsRequest.getImei() != null && !associationDetailsRequest.getImei().isEmpty()) {
            orderedMap.put("difd.imei", associationDetailsRequest.getImei());
        }
        if (associationDetailsRequest.getSerialNumber() != null
            && !associationDetailsRequest.getSerialNumber().isEmpty()) {
            orderedMap.put("difd.serial_number", associationDetailsRequest.getSerialNumber());
        }
        if (associationDetailsRequest.getDeviceId() != null && !associationDetailsRequest.getDeviceId().isEmpty()) {
            orderedMap.put("da.harman_id", associationDetailsRequest.getDeviceId());
        }
        return orderedMap;
    }

    /**
     * Retrieves the association ID for a given device ID.
     *
     * @param deviceId the ID of the device
     * @return the association ID associated with the device ID
     */
    public long getAssociationIdByDeviceId(String deviceId) {
        return deviceAssociationDao.getAssociationIdByDeviceId(deviceId);
    }

    /**
     * Retrieves the model associated with the given IMEI number.
     *
     * @param imei The IMEI number of the device.
     * @return The model associated with the given IMEI number.
     */
    public String getModelByImei(String imei) {
        return deviceInfoFactoryDataDao.getModelByImei(imei);
    }

    /**
     * Saves the VIN details for a device association.
     *
     * @param vin     the VIN (Vehicle Identification Number) to be saved
     * @param region  the region associated with the VIN
     * @param assocId the association ID of the device
     */
    public void saveVinDetails(String vin, String region, long assocId) {
        deviceAssociationDao.saveVinDetails(vin, region, assocId);
    }

    /**
     * Saves the SIM details for a device association.
     *
     * @param tranId         the transaction ID
     * @param assocId        the association ID
     * @param tranStatus     the transaction status
     * @param userAction     the user action
     * @param createdOn      the timestamp when the SIM details were created
     * @param lastUpdatedOn  the timestamp when the SIM details were last updated
     */
    public void saveSimDetails(String tranId, long assocId, String tranStatus, String userAction, Timestamp createdOn,
                               Timestamp lastUpdatedOn) {
        deviceAssociationDao.saveSimDetails(tranId, assocId, tranStatus, userAction, createdOn, lastUpdatedOn);
    }

    /**
     * Checks if an association exists for the given user ID and IMEI.
     *
     * @param userId the user ID
     * @param imei the IMEI
     * @return the association ID if an association exists, or null otherwise
     */
    public Long associationExists(String userId, String imei) {
        return deviceAssociationDao.associationExists(userId, imei);
    }

    /**
     * Checks if an association exists for the given device ID.
     *
     * @param deviceId the ID of the device to check association for
     * @return true if an association exists for the device, false otherwise
     */
    public boolean associationByDeviceExists(String deviceId) {
        return deviceAssociationDao.associationByDeviceExists(deviceId);
    }

    /**
     * Checks if a VIN association exists for the given association ID.
     *
     * @param assocId the association ID to check
     * @return true if a VIN association exists, false otherwise
     */
    public boolean getVinAssociation(long assocId) {
        return deviceAssociationDao.vinAssociationExists(assocId);
    }

    /**
     * Checks if the given VIN (Vehicle Identification Number) is already associated with a device.
     *
     * @param vin the VIN to check
     * @return true if the VIN is already associated with a device, false otherwise
     */
    public boolean vinAlreadyAssociated(String vin) {
        return deviceAssociationDao.vinAlreadyAssociated(vin);
    }

    /**
     * Replaces the VIN (Vehicle Identification Number) associated with the given association ID.
     *
     * @param assocId the ID of the association
     * @param vin the new VIN to replace the existing one
     */
    public void replaceVin(long assocId, String vin) {
        deviceAssociationDao.replaceVin(assocId, vin);

    }
}
