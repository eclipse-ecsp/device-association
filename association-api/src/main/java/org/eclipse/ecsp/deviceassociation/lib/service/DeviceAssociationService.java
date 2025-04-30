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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.ecsp.deviceassociation.dto.TriggerKafkaEventRequestDto;
import org.eclipse.ecsp.deviceassociation.lib.exception.DuplicateDeviceAssociationRequestException;
import org.eclipse.ecsp.deviceassociation.lib.exception.ObserverMessageProcessFailureException;
import org.eclipse.ecsp.deviceassociation.lib.exception.WipeDataFailureException;
import org.eclipse.ecsp.deviceassociation.lib.model.AssociationStatus;
import org.eclipse.ecsp.deviceassociation.lib.model.DeviceAssociation;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociateDeviceRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociateDeviceResponse;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DelegateAssociationRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceItem;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceItemDto;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceItemResult;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceItemStatus;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceItems;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceStatusRequest;
import org.eclipse.ecsp.deviceassociation.lib.util.QualifierGenerator;
import org.eclipse.ecsp.deviceassociation.lib.util.QualifierGenerator.Qualifier;
import org.eclipse.ecsp.exception.shared.ApiPreConditionFailedException;
import org.eclipse.ecsp.exception.shared.ApiValidationFailedException;
import org.eclipse.ecsp.services.shared.deviceinfo.model.DeviceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.DEVICE_INFO_SAVE_VALIDATION_FAILED;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.UPDATE_DUMMY_VALIDATION_FAILED;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.WIPE_DATA_NO_ASSOC_FOUND;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.WIPE_DATA_NO_ASSOC_FOUND_FOR_SOME_DEVICE;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.WIPE_DATA_NO_ASSOC_STATE_FOUND;

/**
 * This class represents a service for device association.
 * It provides methods for associating devices, retrieving associated devices,
 * and performing operations related to device association.
 */
@Service
@Transactional
public class DeviceAssociationService extends AbstractDeviceAssociationService {
    private static final String SUCCESS = "SUCCESS";

    private static final String FAILED = "FAILED";

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceAssociationService.class);
    @Autowired
    protected DeviceAssociationWithFactDataServiceV2 deviceAssocFactoryServiceV2;
    @Value("#{'${supported_device_info_params}'.split(',')}")
    private String[] deviceInfoParams;
    @Value("${supported_device_info_request_size:20}")
    private String supportedDeviceInfoRequestSize;
    @Value("${default_association_type:defaultOwner}")
    private String defaultAssociationType;

    /**
     * Associates a device with a user.
     *
     * @param associateDeviceRequest The request object containing the device and user information.
     * @return The response object containing the ID of the device association and the association status.
     * @throws Exception If an error occurs during the device association process.
     */
    public AssociateDeviceResponse associateDevice(AssociateDeviceRequest associateDeviceRequest)
        throws DuplicateDeviceAssociationRequestException, ObserverMessageProcessFailureException {
        LOGGER.info("associateDevice - start: {}", associateDeviceRequest);
        if (deviceAssociationDao.isDeviceCurrentlyAssociatedToUser(associateDeviceRequest.getSerialNumber(),
            associateDeviceRequest.getUserId())) {
            throw new DuplicateDeviceAssociationRequestException();
        }

        disassociateExistingUsers(associateDeviceRequest.getSerialNumber(), associateDeviceRequest.getUserId(), false);

        DeviceAssociation deviceAssociation = associate(associateDeviceRequest);

        LOGGER.info("associateDevice -exit: {}", deviceAssociation);

        return new AssociateDeviceResponse(deviceAssociation.getId(), AssociationStatus.ASSOCIATION_INITIATED);
    }


    /**
     * Associates a device with a user.
     *
     * @param userId The ID of the user.
     * @param deviceAssociationWithAssociatedStatus The device association object with associated status.
     * @param isOwner A boolean value indicating whether the user is the owner of the device.
     * @throws WipeDataFailureException If there is a failure in associating the device with the user.
     */
    private void associateDevice(String userId, DeviceAssociation deviceAssociationWithAssociatedStatus,
                                 boolean isOwner) {
        try {
            //if the user id owner for the serialnumber, or it is not using many to many, then perform self association
            if (isOwner || defaultAssociationType.equals("defaultOwner")) {
                AssociateDeviceRequest associateDeviceRequest = new AssociateDeviceRequest();
                associateDeviceRequest.setUserId(userId);
                associateDeviceRequest.setSerialNumber(deviceAssociationWithAssociatedStatus.getSerialNumber());
                AssociateDeviceResponse associateDeviceResponse;
                associateDeviceResponse =
                    deviceAssocFactoryServiceV2.associateDeviceForSelf(associateDeviceRequest, null);
                LOGGER.debug("associateDeviceResponse - : {}", associateDeviceResponse.getAssociationId());
            } //else do nothing
        } catch (Exception e) {
            throw new WipeDataFailureException(ApiMessageEnum.WIPE_DATA_ASSOCIATION_FAILURE.getCode(),
                ApiMessageEnum.WIPE_DATA_ASSOCIATION_FAILURE.getMessage(),
                ApiMessageEnum.WIPE_DATA_ACTIVATION_FAILURE.getGeneralMessage(), e);
        }
    }

    /**
     * Associates a device with the provided device request.
     *
     * @param associateDeviceRequest The request object containing the necessary information for device association.
     * @return The device association object after successful association.
     */
    private DeviceAssociation associate(AssociateDeviceRequest associateDeviceRequest) {
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        deviceAssociation.setSerialNumber(associateDeviceRequest.getSerialNumber());
        deviceAssociation.setUserId(associateDeviceRequest.getUserId());
        deviceAssociation.setModifiedOn(new Timestamp(System.currentTimeMillis()));
        deviceAssociation.setModifiedBy(associateDeviceRequest.getUserId());
        deviceAssociation.setAssociatedOn(new Timestamp(System.currentTimeMillis()));
        deviceAssociation.setAssociatedBy(associateDeviceRequest.getUserId());
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATION_INITIATED);
        deviceAssociationDao.insert(deviceAssociation);
        deviceAssociationDao.insertDeviceState(deviceAssociation);
        return deviceAssociation;
    }

    /**
     * Retrieves the list of associated devices for a given user.
     *
     * @param userId the ID of the user
     * @return the list of associated devices
     */
    public List<DeviceAssociation> getAssociatedDevicesForUser(String userId) {
        return deviceAssociationDao.fetchAssociatedDevices(userId);
    }


    /**
     * Retrieves the association details for a given association ID and user ID.
     *
     * @param associationId The ID of the association.
     * @param userId The ID of the user.
     * @return The DeviceAssociation object containing the association details, or null if the association is not found
     *      or the Harman ID is null.
     */
    @Override
    public DeviceAssociation getAssociationDetails(long associationId, String userId) {
        DeviceAssociation deviceAssociation = deviceAssociationDao.find(associationId, userId);
        if (deviceAssociation == null || deviceAssociation.getHarmanId() == null) {
            return deviceAssociation;
        }

        deviceAssociation.setDeviceAttributes(getDeviceAttributes(deviceAssociation.getHarmanId()));
        return deviceAssociation;
    }

    /**
     * Retrieves the IMSI (International Mobile Subscriber Identity) associated with the given IMEI (International
     * Mobile Equipment Identity).
     *
     * @param imei The IMEI for which to retrieve the IMSI.
     * @return The IMSI associated with the given IMEI, or null if no association exists.
     */
    public String getImsi(String imei) {
        return deviceAssociationDao.getImsi(imei);
    }

    /**
     * Retrieves the region for the given reference ID.
     *
     * @param referenceId the reference ID of the device
     * @return the region associated with the reference ID
     */
    public String getRegion(Long referenceId) {
        return deviceAssociationDao.getCountryCode(referenceId);
    }

    /**
     * Retrieves the active transaction status for the given reference ID.
     *
     * @param referenceId The reference ID of the transaction.
     * @return The active transaction status.
     */
    public String getActiveTranStatus(Long referenceId) {
        return deviceAssociationDao.getActivateTranStatus(referenceId);
    }

    /**
     * Wipes the data of devices associated with the given user.
     *
     * @param userId         the ID of the user
     * @param serialNumbers  the list of serial numbers of devices to be wiped
     * @return               the list of IDs of the wiped devices
     */
    @Transactional(propagation = Propagation.NEVER)
    public List<String> wipeDevices(String userId, List<String> serialNumbers)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        LOGGER.debug("wipeDevices() start with userid:{} ", (userId == null) ? null : userId.replaceAll("[\r\n]", ""));
        // fetch all associations
        List<DeviceAssociation> deviceAssociationsWithAssociatedStatus = fetchAllAssociations(userId);

        List<String> newDeviceIds = new ArrayList<>();
        if (!deviceAssociationsWithAssociatedStatus.isEmpty()) {

            // check and remove all other associated devices for which wipe data
            // is not requested.
            // e.g: If there are 10 associations and request is for 4 then remove remaining 6 associations
            List<String> filteredSerialNumbers =
                filterSerialNumbers(deviceAssociationsWithAssociatedStatus, serialNumbers);

            // check and throw exception if device association not found for one or more requested devices.
            Set<String> uniqueAssociations = new HashSet<>();
            for (DeviceAssociation deviceAssociation : deviceAssociationsWithAssociatedStatus) {
                uniqueAssociations.add(deviceAssociation.getSerialNumber());
            }

            if (!CollectionUtils.isEmpty(filteredSerialNumbers)
                && uniqueAssociations.size() != filteredSerialNumbers.size()) {
                throw new ApiPreConditionFailedException(WIPE_DATA_NO_ASSOC_FOUND_FOR_SOME_DEVICE.getCode(),
                    WIPE_DATA_NO_ASSOC_FOUND_FOR_SOME_DEVICE.getMessage(),
                    WIPE_DATA_NO_ASSOC_FOUND_FOR_SOME_DEVICE.getGeneralMessage());
            } else {

                List<ActivationResponse> activationResponses = new ArrayList<>();

                //remove duplicate assoc with same serialnumber only keep the primary owner.
                //remove if multiple associations for same serial number and assoc type is not owner.
                List<DeviceAssociation> duplicateRemoved = getUniqueAssociation(deviceAssociationsWithAssociatedStatus);
                List<String> userIds = performAssociation(duplicateRemoved, userId, activationResponses);

                LOGGER.debug("total reactivated : {}", deviceAssociationsWithAssociatedStatus.size());
                activationResponses.forEach(activResp -> newDeviceIds.add(activResp.getDeviceId()));

                //old association update will work as generic for one to many or many to many
                updatetoDummyValues(filteredSerialNumbers, userIds);
            }
        } else {
            // no device found with status associated
            throw new ApiPreConditionFailedException(WIPE_DATA_NO_ASSOC_STATE_FOUND.getCode(),
                WIPE_DATA_NO_ASSOC_STATE_FOUND.getMessage(),
                WIPE_DATA_NO_ASSOC_STATE_FOUND.getGeneralMessage());
        }

        String newDeviceIdsString = Arrays.toString(newDeviceIds.toArray());
        LOGGER.debug("wipeDevices() - exit: {}", newDeviceIdsString);
        return !newDeviceIds.isEmpty() ? newDeviceIds : null;

    }

    /**
     * Returns a list of unique device associations by removing duplicates based on serial number and association type.
     *
     * @param deviceAssociationsWithAssociatedStatus The list of device associations with associated status.
     * @return A list of unique device associations.
     */
    private List<DeviceAssociation> getUniqueAssociation(
        List<DeviceAssociation> deviceAssociationsWithAssociatedStatus) {
        List<DeviceAssociation> duplicateRemoved = new ArrayList<>();
        List<String> srnos = new ArrayList<>();
        for (DeviceAssociation deviceAssociation : deviceAssociationsWithAssociatedStatus) {
            if (!srnos.contains(deviceAssociation.getSerialNumber())
                    && (deviceAssociation.getAssociationType() == null
                    || deviceAssociation.getAssociationType().equals(defaultAssociationType))) {
                duplicateRemoved.add(deviceAssociation);
                srnos.add(deviceAssociation.getSerialNumber());
            }
        }
        for (DeviceAssociation deviceAssociation : deviceAssociationsWithAssociatedStatus) {
            if (!srnos.contains(deviceAssociation.getSerialNumber())) {
                duplicateRemoved.add(deviceAssociation);
                srnos.add(deviceAssociation.getSerialNumber());
            }
        }
        return duplicateRemoved;
    }

    /**
     * Performs the association process for a list of device associations.
     *
     * @param duplicateRemoved      The list of device associations with duplicate entries removed.
     * @param userId                The ID of the user performing the association.
     * @param activationResponses   The list of activation responses for the associated devices.
     * @return                      The list of user IDs associated with the devices.
     */
    private List<String> performAssociation(List<DeviceAssociation> duplicateRemoved, String userId,
                                            List<ActivationResponse> activationResponses)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        List<String> userIds = new ArrayList<>();
        for (DeviceAssociation deviceAssociationWithAssociatedStatus : duplicateRemoved) {

            //get the primary association and secondary associations(if any)
            List<DeviceAssociation> deviceAssociationList = deviceAssociationDao
                .getAllM2Massociations(deviceAssociationWithAssociatedStatus.getSerialNumber());

            /*
             * terminate for all with status associated
             * if the user is primary owner(or defaultOwner or null) of the device, call terminate all
             * else call disassociated
             * */
            performTerminateDevice(deviceAssociationList, deviceAssociationWithAssociatedStatus, userId);
            /*
             * associate only primary owner
             * if the user is primary owner(or defaultOwner or null) of the device, call self associate
             * else do nothing
             * */
            boolean isOwner = true;
            if (!defaultAssociationType.equals("defaultOwner")) {
                //get own association and check association type
                isOwner = deviceAssociationList.stream().anyMatch(
                    assoc -> (assoc.getAssociationType().equals(defaultAssociationType)
                        && assoc.getUserId().equals(userId)));
            }
            associateDevice(userId, deviceAssociationWithAssociatedStatus, isOwner);

            /*
             * activate
             * if the user is primary owner(or defaultOwner or null)  of the device, call activate
             * else do nothing
             * */
            if (isOwner) {
                activateDevice(activationResponses, deviceAssociationWithAssociatedStatus);

                /*
                 * if the user is primary owner of the device, call associate delegate endpoint
                 * else if defaultOwner or null then do nothing (as for old one to many flow , there is no secondary
                 *  associations)
                 * else do nothing
                 * */
                performDelegation(deviceAssociationList, userId);
            }

            for (DeviceAssociation da : deviceAssociationList) {
                userIds.add(da.getUserId());
            }

        }
        return userIds;
    }

    /**
     * Performs delegation for the given list of device associations and a user ID.
     * If the size of the deviceAssociationList is greater than 1, it iterates through each device association.
     * For each device association, if the association type is not equal to the defaultAssociationType,
     * it creates a DelegateAssociationRequest and sets the necessary attributes.
     * Then, it calls the delegateAssociation method of the deviceAssocFactoryServiceV2 with the created request.
     *
     * @param deviceAssociationList The list of device associations to perform delegation on.
     * @param userId The user ID for the delegation.
     */
    private void performDelegation(List<DeviceAssociation> deviceAssociationList, String userId) {
        if (deviceAssociationList.size() > 1) {
            for (DeviceAssociation deviceassoc : deviceAssociationList) {

                if (!deviceassoc.getAssociationType().equals(defaultAssociationType)) {
                    DelegateAssociationRequest delegateAssociationRequest = new DelegateAssociationRequest();
                    delegateAssociationRequest.setSerialNumber(deviceassoc.getSerialNumber());
                    delegateAssociationRequest.setAssociationType(deviceassoc.getAssociationType());
                    delegateAssociationRequest.setDelegationUserId(deviceassoc.getUserId());
                    delegateAssociationRequest.setStartTimestamp(deviceassoc.getStartTimeStamp());
                    delegateAssociationRequest.setEndTimestamp(deviceassoc.getEndTimeStamp());
                    delegateAssociationRequest.setUserId(userId);
                    deviceAssocFactoryServiceV2.delegateAssociation(delegateAssociationRequest, false);
                }
            }
        }
    }

    /**
     * Performs the termination of a device association.
     *
     * @param deviceAssociationList              the list of device associations
     * @param deviceAssociationWithAssociatedStatus the device association with associated status
     * @param userId                             the user ID
     */
    private void performTerminateDevice(List<DeviceAssociation> deviceAssociationList,
                                        DeviceAssociation deviceAssociationWithAssociatedStatus, String userId) {
        if (deviceAssociationList.size() > 1) {
            boolean isOwner = deviceAssociationList.stream().anyMatch(
                assoc -> (assoc.getAssociationType().equals(defaultAssociationType)
                    && assoc.getUserId().equals(userId)));
            if (isOwner) {
                terminateDevice(userId, deviceAssociationWithAssociatedStatus);
            } else {
                //call terminate for all secondary assoc type with associd.
                terminateAllSecondaryDevice(deviceAssociationList, userId);
            }
        } else {
            terminateDevice(userId, deviceAssociationWithAssociatedStatus);
        }
    }

    /**
     * Filters the given list of device associations based on the provided serial numbers.
     *
     * @param deviceAssociationsWithAssociatedStatus The list of device associations with associated status.
     * @param serialNumbers The list of serial numbers to filter the device associations.
     * @return The filtered list of serial numbers.
     */
    private List<String> filterSerialNumbers(List<DeviceAssociation> deviceAssociationsWithAssociatedStatus,
                                             List<String> serialNumbers) {
        Iterator<DeviceAssociation> iterator = deviceAssociationsWithAssociatedStatus.iterator();
        if (!CollectionUtils.isEmpty(serialNumbers)) {
            Set<String> srno = new HashSet<>(serialNumbers);
            serialNumbers.clear();
            serialNumbers.addAll(srno);
            while (iterator.hasNext()) {
                DeviceAssociation deviceAssociation = iterator.next();
                if (!serialNumbers.contains(deviceAssociation.getSerialNumber())) {
                    iterator.remove();
                }
            }
        }
        return serialNumbers;
    }

    /**
     * Fetches all device associations for a given user.
     *
     * @param userId the ID of the user
     * @return a list of DeviceAssociation objects representing the associations
     * @throws ApiPreConditionFailedException if no associations are found
     */
    private List<DeviceAssociation> fetchAllAssociations(String userId) {
        List<DeviceAssociation> deviceAssociations = getAssociatedDevicesForUser(userId);
        if (deviceAssociations == null || deviceAssociations.isEmpty()) {
            // no association found
            throw new ApiPreConditionFailedException(WIPE_DATA_NO_ASSOC_FOUND.getCode(),
                WIPE_DATA_NO_ASSOC_FOUND.getMessage(),
                WIPE_DATA_NO_ASSOC_FOUND.getGeneralMessage());
        }

        // fetch only associations with status associated
        List<DeviceAssociation> deviceAssociationsWithAssociatedStatus = new ArrayList<>();
        for (DeviceAssociation deviceAssociation : deviceAssociations) {
            if (deviceAssociation.getAssociationStatus().toString().equals(AssociationStatus.ASSOCIATED.toString())) {
                deviceAssociationsWithAssociatedStatus.add(deviceAssociation);
            }
        }
        return deviceAssociationsWithAssociatedStatus;
    }


    /**
     * Updates the associations and activation state of devices with dummy values.
     *
     * @param serialNumbers The list of serial numbers of the devices.
     * @param userIds The list of user IDs associated with the devices.
     * @throws ApiPreConditionFailedException If the update operation fails.
     */
    private void updatetoDummyValues(List<String> serialNumbers, List<String> userIds) {
        try {
            int updatedAssociations = deviceAssociationDao.updateUserIdWithDummyValue(userIds, serialNumbers);
            int updatedActivationState = deviceAssociationDao.updateActivationStateWithDummy(userIds, serialNumbers);
            LOGGER.debug("wipeDevices updated associations: {}, activationState: {}", updatedAssociations,
                updatedActivationState);
        } catch (Exception e) {
            throw new ApiPreConditionFailedException(UPDATE_DUMMY_VALIDATION_FAILED.getCode(),
                UPDATE_DUMMY_VALIDATION_FAILED.getMessage(), UPDATE_DUMMY_VALIDATION_FAILED.getGeneralMessage());
        }
    }

    /**
     * Activates a device by sending an activation request and adding the activation response to the provided list.
     *
     * @param activationResponses                the list to which the activation response will be added
     * @param deviceAssociationWithAssociatedStatus the device association with associated status
     * @throws InvalidKeyException                if an invalid key is encountered
     * @throws NoSuchAlgorithmException           if a requested cryptographic algorithm is not available
     * @throws NoSuchPaddingException             if a padding scheme is not available
     * @throws InvalidAlgorithmParameterException if an invalid algorithm parameter is encountered
     * @throws IllegalBlockSizeException          if an illegal block size is encountered
     * @throws BadPaddingException                if a padding mechanism is incorrect
     */
    private void activateDevice(List<ActivationResponse> activationResponses,
                                DeviceAssociation deviceAssociationWithAssociatedStatus)
        throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
        InvalidAlgorithmParameterException,
        IllegalBlockSizeException, BadPaddingException {
        try {
            ActivationRequest activationRequest = new ActivationRequest();
            Qualifier qualifier = QualifierGenerator
                .generateQualifierAndVin(deviceAssociationWithAssociatedStatus.getSerialNumber());
            activationRequest.setSerialNumber(deviceAssociationWithAssociatedStatus.getSerialNumber());
            activationRequest.setQualifier(qualifier.getQualifier());
            activationRequest.setVin(qualifier.getVin());
            // set sw version and hw version for the device
            //        ASOC XSS - wipe API
            if (StringUtils.isNotBlank(deviceAssociationWithAssociatedStatus.getHarmanId())) {
                List<DeviceInfo> deviceInfoList = deviceAssociationDao
                    .findDeviceInfo(deviceAssociationWithAssociatedStatus.getHarmanId());
                for (DeviceInfo deviceInfo : deviceInfoList) {
                    if (deviceInfo.getName().equals("SW-Version")) {
                        activationRequest.setSwVersion(deviceInfo.getValue());
                    } else if (deviceInfo.getName().equals("HW-Version")) {
                        activationRequest.setHwVersion(deviceInfo.getValue());
                    }
                }
            }
            activationRequest.setProductType("TestProductType");
            activationRequest.setDeviceType("ObdDongle");
            activationRequest.setAad("yes");
            ActivationResponse activationResponse = deviceActivationService.activateDevice(activationRequest);
            LOGGER.debug("activationResponse - : {}", activationResponse.getDeviceId());
            activationResponses.add(activationResponse);
        } catch (Exception e) {
            throw new WipeDataFailureException(ApiMessageEnum.WIPE_DATA_ACTIVATION_FAILURE.getCode(),
                ApiMessageEnum.WIPE_DATA_ACTIVATION_FAILURE.getMessage(),
                ApiMessageEnum.WIPE_DATA_ACTIVATION_FAILURE.getGeneralMessage(), e);
        }
    }

    /**
     * Terminates the device association for the given user and device.
     *
     * @param userId The ID of the user.
     * @param deviceAssociationWithAssociatedStatus The device association with associated status.
     * @throws WipeDataFailureException If there is a failure in terminating the device association.
     */
    private void terminateDevice(String userId, DeviceAssociation deviceAssociationWithAssociatedStatus) {
        try {
            DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
            deviceStatusRequest.setSerialNumber(deviceAssociationWithAssociatedStatus.getSerialNumber());
            deviceStatusRequest.setUserId(userId);
            deviceStatusRequest.setAssociationId(deviceAssociationWithAssociatedStatus.getId());
            LOGGER.debug("## associationid- : {}", deviceAssociationWithAssociatedStatus.getId());
            deviceStatusRequest.setRequiredFor(MessageConstants.TERMINATE_REQUIRED_FOR);
            //call /v1/associations/self/terminate , this is backward compatible with old terminate apis
            // and this will terminate primary as well as secondary association
            int updateCount = deviceAssocFactoryServiceV2
                .terminateM2Massociation(deviceStatusRequest, userId, "", false);
            LOGGER.debug("updateCount - : {}", updateCount);
        } catch (Exception e) {
            throw new WipeDataFailureException(ApiMessageEnum.WIPE_DATA_TERMINATION_FAILURE.getCode(),
                ApiMessageEnum.WIPE_DATA_TERMINATION_FAILURE.getMessage(),
                ApiMessageEnum.WIPE_DATA_ACTIVATION_FAILURE.getGeneralMessage(), e);
        }
    }

    /**
     * Terminates all secondary devices associated with the given user ID.
     *
     * @param deviceAssociationList the list of device associations
     * @param userId the user ID
     * @throws WipeDataFailureException if there is a failure in terminating the devices
     */
    private void terminateAllSecondaryDevice(List<DeviceAssociation> deviceAssociationList, String userId) {
        try {
            for (DeviceAssociation deviceAssociation : deviceAssociationList) {
                if (deviceAssociation.getUserId().equals(userId)) {
                    DeviceStatusRequest deviceStatusRequest = new DeviceStatusRequest();
                    deviceStatusRequest.setSerialNumber(deviceAssociation.getSerialNumber());
                    deviceStatusRequest.setUserId(userId);
                    deviceStatusRequest.setAssociationId(deviceAssociation.getId());
                    LOGGER.debug("## associationid - : {}", deviceAssociation.getId());
                    deviceStatusRequest.setRequiredFor(MessageConstants.TERMINATE_REQUIRED_FOR);
                    int updateCount = deviceAssocFactoryServiceV2
                        .terminateM2Massociation(deviceStatusRequest, deviceAssociation.getUserId(), "", false);
                    LOGGER.debug("updateCount - : {}", updateCount);
                }

            }
        } catch (Exception e) {
            throw new WipeDataFailureException(ApiMessageEnum.WIPE_DATA_TERMINATION_FAILURE.getCode(),
                ApiMessageEnum.WIPE_DATA_TERMINATION_FAILURE.getMessage(),
                ApiMessageEnum.WIPE_DATA_ACTIVATION_FAILURE.getGeneralMessage(), e);
        }
    }

    /**
     * Saves the device item based on the provided device item DTO.
     *
     * @param deviceItemDto The device item DTO containing the data to be saved.
     * @return The device item result after saving the device item.
     */
    public DeviceItemResult saveDeviceItem(DeviceItemDto deviceItemDto) {

        ArrayList<DeviceItemStatus> deviceItemStatusList = new ArrayList<>();

        AssociationValidator.validateDeviceInfoRequestSize(deviceItemDto.getData().size(),
            Integer.parseInt(supportedDeviceInfoRequestSize));

        DeviceItemResult deviceItemResult = new DeviceItemResult();
        for (DeviceItems deviceItems : deviceItemDto.getData()) {

            if (!saveDeviceInfoByDeviceId(deviceItemStatusList, deviceItems)) {
                deviceItemResult.setExceptionOccured(true);
            }
        }

        deviceItemResult.setDeviceInfoStatusList(deviceItemStatusList);
        return deviceItemResult;
    }

    /**
     * Saves device information for a given device ID.
     *
     * @param deviceItemStatusList The list of device item statuses.
     * @param deviceItems The device items containing the information to be saved.
     * @return True if the device information is saved successfully, false otherwise.
     * @throws ApiValidationFailedException If the validation of the device information fails.
     */
    private boolean saveDeviceInfoByDeviceId(ArrayList<DeviceItemStatus> deviceItemStatusList,
                                             DeviceItems deviceItems) {

        try {
            //validate for association exists
            if (!associationByDeviceExists(deviceItems.getDeviceId())) {
                LOGGER.error("Failed to save DeviceItems for Device :{} as association does not exist for this Device",
                    deviceItems.getDeviceId());
                throw new ApiValidationFailedException(DEVICE_INFO_SAVE_VALIDATION_FAILED.getCode(),
                    DEVICE_INFO_SAVE_VALIDATION_FAILED.getMessage(),
                    DEVICE_INFO_SAVE_VALIDATION_FAILED.getGeneralMessage());
            } else {
                //validate that all the item names are in supported list
                List<String> nameList = deviceItems.getItems().stream().map(DeviceItem::getName).toList();
                AssociationValidator.validateSupportedParam(nameList, deviceInfoParams);
                //Save all the items
                for (DeviceItem deviceInfo : deviceItems
                    .getItems()) {
                    deviceInfoDao.updateDeviceInfo(deviceItems.getDeviceId(), deviceInfo.getName(),
                        String.valueOf(deviceInfo.getValue()));
                }
                deviceItemStatusList.add(new DeviceItemStatus(deviceItems.getDeviceId(), SUCCESS));
            }
            return true;
        } catch (ApiValidationFailedException e) {
            LOGGER.error("Failed to save DeviceItems for Device :{} with ErrMsg: {}", deviceItems.getDeviceId(),
                e.getMessage());
            deviceItemStatusList.add(new DeviceItemStatus(deviceItems.getDeviceId(), FAILED));
            return true;
        } catch (Exception e) {
            LOGGER.error("Failed to save DeviceItems for Device :{} with ErrMsg: {}", deviceItems.getDeviceId(),
                e.getMessage());
            deviceItemStatusList.add(new DeviceItemStatus(deviceItems.getDeviceId(), FAILED));
            return false;
        }
    }

    /**
     * Triggers a Kafka event by sending the provided event data to Kafka.
     *
     * @param triggerKafkaEventRequestDto The data for the Kafka event.
     */
    public void triggerKafkaEvent(TriggerKafkaEventRequestDto triggerKafkaEventRequestDto) {

        kafkaDeviceNotifier.sendEventToKafka(triggerKafkaEventRequestDto);

    }

}