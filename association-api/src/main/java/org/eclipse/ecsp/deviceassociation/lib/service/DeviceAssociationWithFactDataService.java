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

import com.amazonaws.services.cloudwatch.model.InternalServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.ecsp.deviceassociation.lib.config.DeviceAssocationProperty;
import org.eclipse.ecsp.deviceassociation.lib.exception.DeviceReplaceException;
import org.eclipse.ecsp.deviceassociation.lib.exception.InvalidPinException;
import org.eclipse.ecsp.deviceassociation.lib.exception.InvalidUserAssociation;
import org.eclipse.ecsp.deviceassociation.lib.exception.NoSuchEntityException;
import org.eclipse.ecsp.deviceassociation.lib.exception.ObserverMessageProcessFailureException;
import org.eclipse.ecsp.deviceassociation.lib.exception.UpdateDeviceException;
import org.eclipse.ecsp.deviceassociation.lib.model.AssociationStatus;
import org.eclipse.ecsp.deviceassociation.lib.model.DeviceAssociation;
import org.eclipse.ecsp.deviceassociation.lib.model.DeviceAssociationHistory;
import org.eclipse.ecsp.deviceassociation.lib.model.swm.SwmRequest;
import org.eclipse.ecsp.deviceassociation.lib.model.swm.SwmUpdateVehicleRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociateDeviceRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociateDeviceResponse;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociationDetailsRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociationDetailsResponse;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.CurrentDeviceDataPojo;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceStatusRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.FactoryData;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.ReplaceDeviceDataPojo;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.ReplaceFactoryDataRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.StateChange;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.StateChangeRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.support.HttpPlatformUtil;
import org.eclipse.ecsp.deviceassociation.lib.rest.support.SpringAuthTokenGenerator;
import org.eclipse.ecsp.deviceassociation.lib.service.swm.IswmCrudService;
import org.eclipse.ecsp.services.clientlib.HcpRestClientLibrary;
import org.eclipse.ecsp.services.device.dao.DeviceDao;
import org.eclipse.ecsp.services.device.model.Device;
import org.eclipse.ecsp.services.deviceactivation.model.DeviceActivationState;
import org.eclipse.ecsp.services.factorydata.domain.DeviceInfoFactoryData;
import org.eclipse.ecsp.services.factorydata.domain.DeviceState;
import org.eclipse.ecsp.services.shared.db.HcpInfo;
import org.eclipse.ecsp.services.shared.rest.support.SimpleResponseMessage;
import org.eclipse.ecsp.springauth.client.exception.SpringAuthClientException;
import org.eclipse.ecsp.springauth.client.rest.SpringAuthRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.HttpClientErrorException;

import javax.management.InvalidAttributeValueException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.eclipse.ecsp.deviceassociation.lib.service.Constants.BSSID;
import static org.eclipse.ecsp.deviceassociation.lib.service.Constants.ICCID;
import static org.eclipse.ecsp.deviceassociation.lib.service.Constants.IMEI;
import static org.eclipse.ecsp.deviceassociation.lib.service.Constants.IMSI;
import static org.eclipse.ecsp.deviceassociation.lib.service.Constants.MANUFACTURING_DATE;
import static org.eclipse.ecsp.deviceassociation.lib.service.Constants.MODEL;
import static org.eclipse.ecsp.deviceassociation.lib.service.Constants.MSISDN;
import static org.eclipse.ecsp.deviceassociation.lib.service.Constants.PACKAGE_SERIAL_NUMBER;
import static org.eclipse.ecsp.deviceassociation.lib.service.Constants.PLATFORM_VERSION;
import static org.eclipse.ecsp.deviceassociation.lib.service.Constants.RECORD_DATE;
import static org.eclipse.ecsp.deviceassociation.lib.service.Constants.SERIAL_NUMBER;
import static org.eclipse.ecsp.deviceassociation.lib.service.Constants.SSID;
import static org.eclipse.ecsp.deviceassociation.lib.service.Constants.VIN;
import static org.eclipse.ecsp.deviceassociation.lib.service.MessageConstants.ASSOCIATION_DATA_DOES_NOT_EXIST_FOR_GIVEN_INPUT;
import static org.eclipse.ecsp.deviceassociation.lib.service.MessageConstants.ASSOCIATION_HISTORY_NOT_FOUND;
import static org.eclipse.ecsp.deviceassociation.lib.service.MessageConstants.BASIC_DATA_MANDATORY;
import static org.eclipse.ecsp.deviceassociation.lib.service.MessageConstants.BASIC_MANDATORY_STATE_CHANGE;
import static org.eclipse.ecsp.deviceassociation.lib.service.MessageConstants.DEVICE_TERMINATED_MSG;
import static org.eclipse.ecsp.deviceassociation.lib.service.MessageConstants.INVALID_CURRENT_FACTORY_DATA_FOR_REPLACE;
import static org.eclipse.ecsp.deviceassociation.lib.service.MessageConstants.INVALID_DEVICE_REPLACEMENT_CURRENT_DATA_STATE;
import static org.eclipse.ecsp.deviceassociation.lib.service.MessageConstants.INVALID_DEVICE_REPLACEMENT_REPLACE_DATA_STATE;
import static org.eclipse.ecsp.deviceassociation.lib.service.MessageConstants.INVALID_INACTIVATED_DEVICE_FOR_REPLACEMENT;
import static org.eclipse.ecsp.deviceassociation.lib.service.MessageConstants.INVALID_REPLACE_REQUEST_DATA;
import static org.eclipse.ecsp.deviceassociation.lib.service.MessageConstants.INVALID_USER_DEVICE_DETAILS;
import static org.eclipse.ecsp.deviceassociation.lib.service.MessageConstants.NO_DATA_FOUND;
import static org.eclipse.ecsp.deviceassociation.lib.service.MessageConstants.REGISTRATION_FAILURE_MSG;
import static org.eclipse.ecsp.deviceassociation.lib.service.MessageConstants.STATE_MANDATORY;
import static org.eclipse.ecsp.deviceassociation.lib.service.MessageConstants.STOLEN_OR_FAULTY_MSG;
import static org.eclipse.ecsp.deviceassociation.lib.service.MessageConstants.TERMINATION_MSG;
import static org.eclipse.ecsp.deviceassociation.lib.service.MessageConstants.USER_ID;
import static org.springframework.http.HttpStatus.OK;

/**
 * This class represents a service for device association with fact data.
 * It extends the AbstractDeviceAssociationService class.
 * It provides methods for associating devices, fetching associated devices for a user, and other related operations.
 */
@Service
@Transactional(rollbackFor = ObserverMessageProcessFailureException.class)
/*
 * Note that – by default, rollback happens for runtime, unchecked exceptions
 * only. The checked exception does not trigger a rollback of the transaction.
 * Here ObserverMessageProcessFailureException is checked exception and we added
 * in @Transactional annotation using rollbackFor
 */
public class DeviceAssociationWithFactDataService extends AbstractDeviceAssociationService {

    public static final String VEHICLE_MODEL_YEAR = "vehicleModelYear";
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceAssociationWithFactDataService.class);
    public final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd");
    private static final String ASSOC_DATA_DATABASE_INTEGRITY_ERROR =
            "Association data - Database Integrity Error. There is more than one record.";
    private static final String UPDATING_REG_CLIENT_IN_SPRING_AUTH =
            "Updating registered client in Spring Auth, deviceId: {}";
    private static final String APPROVED = "approved";
    private static final String SUCCESSFULLY_UPDATED_REG_CLIENT_IN_SPRING_AUTH =
            "Successfully updated registered client in Spring Auth, deviceId: {}";
    private static final String OLD_DEVICE_PROVISION_WITH_IMEI =
            "Old Device is Provisioned with its imei - ";
    public static final int COUNT = -1;
    public static final int ITER = 2;
    public static final int COUNT_64 = 64;

    @Value("${swm_integration_enabled:true}")
    public boolean swmIntegrationEnabled;
    @Autowired
    @Lazy
    protected SpringAuthTokenGenerator springAuthTokenGenerator;
    @Value("${base_service_api_url:http://docker_host:8080/haa-api-dev}")
    private String baseApiUrl;
    @Value("${reset_device_url_segment:v1/device/deviceOperation}")
    private String resetDeviceUrlSegment;
    @Value("${send_reset_device_enabled:false}")
    private boolean isSendResetDeviceEnabled;
    @Autowired
    private SpringAuthRestClient springAuthRestClient;
    @Autowired
    private DeviceDao deviceDao;
    @Autowired
    private HcpRestClientLibrary hcpRestClientLibrary1;
    @Value("${pin.validation:false}")
    private boolean pinValidation;
    @Value("${current.device.provisioning:false}")
    private boolean isCurrentDeviceToBeMovedToProvisioned;
    @Value("${forbid_assoc_after_terminate:false}")
    private boolean forbidAssocAfterTerminate;
    @Autowired
    private IswmCrudService<SwmRequest> swmService;

    /**
     * Associates a device with the provided device request.
     *
     * @param associateDeviceRequest The request object containing the device information.
     * @return The response object containing the ID of the associated device and the association status.
     * @throws Exception If an error occurs during the device association process.
     */
    public AssociateDeviceResponse associateDevice(AssociateDeviceRequest associateDeviceRequest)
            throws NoSuchEntityException {
        // 2.33 Release - Sonar RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE codesmell fix
        String associateDeviceRequestData =
                    associateDeviceRequest.toString().replaceAll("[\r\n]", "");
        LOGGER.info("associateDevice - start: {}", associateDeviceRequestData);
        if (StringUtils.isEmpty(associateDeviceRequest.getBssid())
            && StringUtils.isEmpty(associateDeviceRequest.getImei())
            && StringUtils.isEmpty(associateDeviceRequest.getSerialNumber())) {
            LOGGER.error(BASIC_DATA_MANDATORY);
            throw new NoSuchEntityException("Either BSSID or IMEI or serial number is mandatory????");
        }

        validatePin(associateDeviceRequest);

        DeviceAssociation deviceAssociation = associate(associateDeviceRequest);

        LOGGER.info("associateDevice - exit: {}", deviceAssociation);

        return new AssociateDeviceResponse(deviceAssociation.getId(), AssociationStatus.ASSOCIATION_INITIATED);
    }

    /**
     * Associates a device with the provided device request.
     *
     * @param associateDeviceRequest The request object containing the necessary data for device association.
     * @return The device association object.
     * @throws NoSuchEntityException If there is no entity found for the provided data or if the device is already
     *      assigned to a different email account.
     */
    private DeviceAssociation associate(AssociateDeviceRequest associateDeviceRequest) throws NoSuchEntityException {

        DeviceAssociation deviceAssociation = new DeviceAssociation();
        List<FactoryData> fetchFactoryData = deviceAssociationDao.constructAndFetchFactoryData(associateDeviceRequest);
        String fetchState = "";
        deviceAssociation.setUserId(associateDeviceRequest.getUserId());
        if (fetchFactoryData != null && !fetchFactoryData.isEmpty() && fetchFactoryData.get(0) != null
            && fetchFactoryData.get(0).getId() != 0) {
            deviceAssociation.setFactoryId(fetchFactoryData.get(0).getId());
            deviceAssociation.setSerialNumber(fetchFactoryData.get(0).getSerialNumber());
            fetchState = fetchFactoryData.get(0).getState();
            if (fetchFactoryData.get(0).isFaulty() || fetchFactoryData.get(0).isStolen()) {
                fetchState = "STOLEN_OR_FAULTY";
            }
        } else {
            throw new NoSuchEntityException("Please provide valid data to fetch factory data from database");
        }

        LOGGER.info("forbidAssocAfterTerminate: {} ", forbidAssocAfterTerminate);

        SimpleResponseMessage simpleResponseMessage;

        if (forbidAssocAfterTerminate && !"".equals(fetchState)
                && ("PROVISIONED".equals(fetchState) || "PROVISIONED_ALIVE".equals(fetchState))) {

            boolean isTerminated = deviceAssociationDao.isDeviceTerminated(fetchFactoryData.get(0).getId());
            if (isTerminated) {
                LOGGER.info("The device with serial number: {} got terminated, hence can not be associated again",
                        fetchFactoryData.get(0).getSerialNumber());
                simpleResponseMessage = new SimpleResponseMessage(TERMINATION_MSG, DEVICE_TERMINATED_MSG);
                throw new NoSuchEntityException(simpleResponseMessage);
            }
        }

        switch (fetchState) {
            case "STOLEN_OR_FAULTY":
                throw new NoSuchEntityException(
                    new SimpleResponseMessage(REGISTRATION_FAILURE_MSG, STOLEN_OR_FAULTY_MSG));
            case "PROVISIONED", "PROVISIONED_ALIVE":
                saveData(deviceAssociation);
                break;
            default:
                String userId = getUserId(associateDeviceRequest.getImei(), associateDeviceRequest.getSerialNumber());
                LOGGER.info("Invalid state passed for association fetchState: {}, userId: {}", fetchState, userId);
                throw new NoSuchEntityException(
                    "Device already assigned to account. This device is already assigned to a different email account."
                        + " Please go to Help & Support and tap the Call Now button for Support");
        }
        return deviceAssociation;
    }

    /**
     * Retrieves the user ID associated with the given IMEI and serial number.
     *
     * @param imei The IMEI (International Mobile Equipment Identity) of the device.
     * @param serialNumber The serial number of the device.
     * @return The user ID associated with the device.
     */
    public String getUserId(String imei, String serialNumber) {
        AssociationDetailsRequest associationDetailsRequest = new AssociationDetailsRequest();
        associationDetailsRequest.setImei(imei);
        associationDetailsRequest.setSerialNumber(serialNumber);
        AssociationDetailsResponse response = getAssociationDetails(associationDetailsRequest);
        String userId = response.getUserId();

        return getMaskedString(userId);
    }

    /**
     * Returns a masked string based on the given user ID.
     *
     * @param userId The user ID to be masked.
     * @return A masked string where all characters except the first and the characters before the '@' symbol are
     *      replaced with '*'.
     */
    private String getMaskedString(String userId) {
        char[] chars = userId.toCharArray();
        int size;

        // calculate size if userId contains @ symbol
        if (userId.indexOf('@') != COUNT) {
            size = userId.indexOf('@') - 1;
        } else {
            size = userId.length() - 1;
        }

        for (int i = ITER; i < size; i++) {
            chars[i] = '*';
        }
        return new String(chars);
    }

    /**
     * Saves the device association data.
     *
     * @param deviceAssociation The device association object to be saved.
     */
    private void saveData(DeviceAssociation deviceAssociation) {
        LOGGER.info("Inside saveData method");
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATION_INITIATED);
        deviceAssociation.setModifiedOn(new Timestamp(System.currentTimeMillis()));
        deviceAssociation.setModifiedBy(deviceAssociation.getUserId());
        deviceAssociation.setAssociatedOn(new Timestamp(System.currentTimeMillis()));
        deviceAssociation.setAssociatedBy(deviceAssociation.getUserId());
        deviceAssociation.setDisassociatedBy(null);
        deviceAssociation.setDisassociatedOn(null);
        deviceAssociationDao.insert(deviceAssociation);
        deviceAssociationDao.insertDeviceState(deviceAssociation);
        deviceInfoFactoryDataDao.changeDeviceState(deviceAssociation.getFactoryId(),
            DeviceState.READY_TO_ACTIVATE.toString(),
            "Association initiated with user ");
        LOGGER.info("Exiting saveData method");
    }

    /**
     * Retrieves the list of associated devices for a given user.
     *
     * @param userId the ID of the user
     * @return a list of DeviceAssociation objects representing the associated devices
     */
    public List<DeviceAssociation> getAssociatedDevicesForUser(String userId) {
        List<DeviceAssociation> deviceAssociations = deviceAssociationDao.fetchAssociatedDevices(userId);

        if (!CollectionUtils.isEmpty(deviceAssociations)) {
            for (DeviceAssociation deviceAssociation : deviceAssociations) {
                if (deviceAssociation.getFactoryId() != 0) {
                    List<FactoryData> data = deviceAssociationDao.fetchFactoryData(deviceAssociation.getFactoryId());
                    if (data != null && data.get(0) != null) {

                        FactoryData factoryData = data.get(0);
                        deviceAssociation.setIccid(factoryData.getIccid());
                        deviceAssociation.setImei(factoryData.getImei());
                        deviceAssociation.setSsid(factoryData.getSsid());
                        deviceAssociation.setMsisdn(factoryData.getMsisdn());
                        deviceAssociation.setImsi(factoryData.getImsi());
                        deviceAssociation.setBssid(factoryData.getBssid());
                        deviceAssociation.setManufacturingDate(factoryData.getManufacturingDate());
                        deviceAssociation.setModel(factoryData.getModel());
                        deviceAssociation.setRecordDate(factoryData.getRecordDate());
                        deviceAssociation.setPlatformVersion(factoryData.getPlatformVersion());
                    }
                }

            }

        }
        return deviceAssociations;

    }

    /**
     * Retrieves the details of a device association.
     *
     * @param associationId The ID of the association.
     * @param userId The ID of the user.
     * @return The DeviceAssociation object containing the association details, or null if the association is not found.
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
     * Terminates the association for a device based on the provided device status request.
     *
     * @param deviceStatusRequest The device status request containing the necessary information.
     * @return The number of associations terminated.
     * @throws Exception If an error occurs during the termination process.
     */
    public int terminateAssociation(DeviceStatusRequest deviceStatusRequest)
            throws NoSuchEntityException, ObserverMessageProcessFailureException {
        LOGGER.debug("Inside terminateAssociation method DeviceAssociationWithFactDataService class\n");
        List<AssociationStatus> statusList = new ArrayList<>();
        statusList.add(AssociationStatus.ASSOCIATED);
        statusList.add(AssociationStatus.ASSOCIATION_INITIATED);
        statusList.add(AssociationStatus.SUSPENDED);
        List<DeviceAssociation> deviceAssociationList = validateGetDeviceAssociation(deviceStatusRequest, statusList);

        if (deviceAssociationList.size() > 1) {
            LOGGER.error(ASSOC_DATA_DATABASE_INTEGRITY_ERROR);
            throw new InvalidUserAssociation();
        }
        DeviceAssociation deviceAssociation = deviceAssociationList.get(0);
        deviceAssociation.setUserId(deviceStatusRequest.getUserId());
        int updatedCount = performTerminate(deviceAssociation);
        LOGGER.debug(
            "Exit terminateAssociation method DeviceAssociationWithFactDataService class : count {}", updatedCount);
        return updatedCount;
    }

    /**
     * Performs the termination of a device association.
     *
     * @param deviceAssociation The device association to be terminated.
     * @return The number of associations that were successfully terminated.
     * @throws ObserverMessageProcessFailureException If an error occurs during the termination process.
     */
    private int performTerminate(DeviceAssociation deviceAssociation)
            throws ObserverMessageProcessFailureException {
        LOGGER.debug("Inside performTerminate method \n");
        deviceAssociation.setDeviceAuthV2Deactivate(true);
        deviceAssociation.setAssociationStatus(AssociationStatus.DISASSOCIATED);
        deviceAssociation.setDisassociatedBy(deviceAssociation.getUserId());
        deviceAssociation.setModifiedBy(deviceAssociation.getUserId());
        deviceAssociation.setDisassociatedOn(new Timestamp(System.currentTimeMillis()));
        int updatedCount;
        updatedCount = deviceAssociationDao.updateForDisassociationById(deviceAssociation);
        LOGGER.debug("Count :: {}", updatedCount);
        LOGGER.info("Successfully terminated {} previous associations ", updatedCount);
        if (deviceAssociation.getHarmanId() != null || !StringUtils.isEmpty(deviceAssociation.getHarmanId())) {
            deRegisterFromSpringAuth(deviceAssociation.getHarmanId());
        }
        observable.notify(deviceAssociation);
        LOGGER.debug("\nAfter notifying !!!");
        return updatedCount;
    }

    /**
     * Restores the association of a device based on the provided device status request.
     *
     * @param deviceStatusRequest The device status request containing the necessary information.
     * @return The number of associations restored.
     * @throws InvalidAttributeValueException If an error occurs during the association restoration process.
     */
    public int restoreAssociation(DeviceStatusRequest deviceStatusRequest)
        throws NoSuchEntityException, javax.naming.directory.InvalidAttributeValueException {
        List<AssociationStatus> statusList = new ArrayList<>();
        statusList.add(AssociationStatus.SUSPENDED);
        List<DeviceAssociation> deviceAssociationList = validateGetDeviceAssociation(deviceStatusRequest, statusList);

        if (deviceAssociationList.size() > 1) {
            LOGGER.error(ASSOC_DATA_DATABASE_INTEGRITY_ERROR);
            throw new InvalidUserAssociation();
        }
        DeviceAssociation deviceAssociation = deviceAssociationList.get(0);
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        deviceAssociation.setModifiedBy(deviceStatusRequest.getUserId());
        int updatedCount = deviceAssociationDao.updateDeviceAssociationStatusToRestore(deviceAssociation);
        Device device = deviceDao.findByDeviceId(deviceAssociation.getHarmanId());
        LOGGER.debug("registering device :{} ", device);
        if (null != device) {
            String authToken = springAuthTokenGenerator.fetchSpringAuthToken();
            LOGGER.info(UPDATING_REG_CLIENT_IN_SPRING_AUTH, device.getHarmanId());
            springAuthRestClient.updateRegisteredClient(authToken, device.getHarmanId(), device.getPasscode(),
                deviceAssociation.getDeviceType(), APPROVED);
            LOGGER.info(SUCCESSFULLY_UPDATED_REG_CLIENT_IN_SPRING_AUTH, device.getHarmanId());
        }

        return updatedCount;
    }

    /**
     * Validates the device association based on the provided device status request and association status list.
     *
     * @param deviceStatusRequest The device status request object containing the device ID, IMEI, and serial number.
     * @param statusList          The list of association statuses to validate against.
     * @return The list of validated device associations.
     * @throws NoSuchEntityException If no device association exists for the given input.
     */
    private List<DeviceAssociation> validateGetDeviceAssociation(DeviceStatusRequest deviceStatusRequest,
                                                                 List<AssociationStatus> statusList)
        throws NoSuchEntityException {

        LOGGER.info("Inside validateGetDeviceAssociation method");
        if (StringUtils.isEmpty(deviceStatusRequest.getDeviceId()) && StringUtils.isEmpty(deviceStatusRequest.getImei())
            && StringUtils.isEmpty(deviceStatusRequest.getSerialNumber())) {
            throw new NoSuchEntityException("Either BSSID or IMEI or serial number is mandatory????");
        }
        List<DeviceAssociation> deviceAssociationList = deviceAssociationDao.constructAndFetchDeviceAssociationData(
            deviceStatusRequest.getSerialNumber(), deviceStatusRequest.getDeviceId(), deviceStatusRequest.getImei(),
            deviceStatusRequest.getUserId(), deviceStatusRequest.getAssociationId(), statusList);

        if (deviceAssociationList == null || deviceAssociationList.isEmpty()) {
            LOGGER.error("Association data does not exist for given input.");
            throw new NoSuchEntityException("Association data does not exist for given input.");
        }
        return deviceAssociationList;
    }

    /**
     * Suspends a device by updating its association status to SUSPENDED in the device_association table.
     *
     * @param deviceStatusRequest The request object containing the device status information.
     * @return An instance of AssociateDeviceResponse representing the suspended device.
     * @throws NoSuchEntityException If there is an error suspending the device.
     */
    public AssociateDeviceResponse suspendDevice(DeviceStatusRequest deviceStatusRequest)
            throws NoSuchEntityException {
        LOGGER.info("Inside suspendDevice method");
        List<AssociationStatus> statusList = new ArrayList<>();
        statusList.add(AssociationStatus.ASSOCIATED);
        List<DeviceAssociation> deviceAssociationList = validateGetDeviceAssociation(deviceStatusRequest, statusList);
        if (deviceAssociationList.size() > 1) {
            LOGGER.error(ASSOC_DATA_DATABASE_INTEGRITY_ERROR);
            throw new InvalidUserAssociation();
        }
        DeviceAssociation deviceAssociation = deviceAssociationList.get(0);
        int updatedCount = deviceAssociationDao.updateDeviceAssociationStatusToSuspended(deviceAssociation);
        if (updatedCount <= 0) {
            LOGGER.error("Update of status to SUSPENDED in device_association table was unsuccesful");
            throw new InternalServiceException(
                "Update of status to SUSPENDED in device_association table was unsuccesful");
        }

        deRegisterFromSpringAuth(deviceAssociation.getHarmanId());
        return new AssociateDeviceResponse(deviceAssociation.getId(), AssociationStatus.SUSPENDED);

    }

    /**
     * Checks if the given ReplaceFactoryDataRequest is valid.
     *
     * @param replaceDeviceRequest The ReplaceFactoryDataRequest to validate.
     * @return true if the ReplaceFactoryDataRequest is valid, false otherwise.
     */
    private boolean isValidReplaceRequestData(ReplaceFactoryDataRequest replaceDeviceRequest) {

        if (replaceDeviceRequest.getCurrentValue() == null || replaceDeviceRequest.getReplaceWith() == null) {
            return false;
        }
        return !isInValidCurrentData(replaceDeviceRequest.getCurrentValue())
                && !isInValidReplaceWithData(replaceDeviceRequest.getReplaceWith());
    }

    /**
     * Checks if the provided ReplaceFactoryDataRequest is valid for IVI replacement.
     *
     * @param replaceDeviceRequest The ReplaceFactoryDataRequest to validate.
     * @return true if the ReplaceFactoryDataRequest is valid for IVI replacement, false otherwise.
     */
    private boolean isValidIviReplaceRequestData(ReplaceFactoryDataRequest replaceDeviceRequest) {
        return !StringUtils.isEmpty(replaceDeviceRequest.getSerialNumber())
            && !ObjectUtils.isEmpty(replaceDeviceRequest.getReplaceWith());
    }

    /**
     * Checks if the given current device data is invalid.
     *
     * @param currentValueData The current device data to be checked.
     * @return true if the current device data is invalid, false otherwise.
     * @throws InvalidAttributeValueException if an invalid attribute value is encountered.
     */
    private boolean isInValidCurrentData(CurrentDeviceDataPojo currentValueData) {
        return (currentValueData.getImei() == null && currentValueData.getSerialNumber() == null
                && currentValueData.getMsisdn() == null && currentValueData.getImsi() == null);
    }

    /**
     * Checks if the given ReplaceDeviceDataPojo object is invalid for replacement with data.
     * The object is considered invalid if all of its properties (imei, serialNumber, iccid, bssid, msisdn, imsi) are
     * null.
     *
     * @param replaceWithData The ReplaceDeviceDataPojo object to be checked.
     * @return true if the object is invalid for replacement with data, false otherwise.
     */
    private boolean isInValidReplaceWithData(ReplaceDeviceDataPojo replaceWithData) {
        return (replaceWithData.getImei() == null && replaceWithData.getSerialNumber() == null
                && replaceWithData.getMsisdn() == null && replaceWithData.getImsi() == null);
    }

    /**
     * Disables the activation state for a device.
     *
     * @param factoryId The factory ID of the device.
     * @param userId The user ID performing the action.
     */
    private void disableActivationStateForDevice(String factoryId, String userId) {
        long activeRecordId = deviceActivationStateDao.findActiveDevice(Long.parseLong(factoryId));
        if (activeRecordId > 0) {
            deviceActivationStateDao.disableRecord(activeRecordId, userId);
        }
    }

    /**
     * Replaces the device with new factory data and updates the necessary tables.
     *
     * @param replaceDeviceRequest The request object containing the new factory data for replacement.
     * @param userId The ID of the user performing the device replacement.
     * @throws DeviceReplaceException If there is an error during the device replacement process.
     */
    public void replaceDevice(ReplaceFactoryDataRequest replaceDeviceRequest, String userId) {
        try {
            DeviceInfoFactoryData currentDataFromDb =
                validateReplaceRequestAndCurrentValueData(replaceDeviceRequest, userId);
            DeviceInfoFactoryData replaceDataFromDb = validateListReplaceValueData(replaceDeviceRequest);
            if (!replaceDataFromDb.getState().equals(DeviceState.PROVISIONED.toString())) {
                throw new DeviceReplaceException("Passed Replace factory data is in " + replaceDataFromDb.getState()
                    + "  " + INVALID_DEVICE_REPLACEMENT_REPLACE_DATA_STATE);
            }
            // Fetch the data from HCPInfo table for the current data factoryId
            HcpInfo hcpInfo = hcpInfoDao.findActiveHcpInfo(currentDataFromDb.getId());
            if (hcpInfo == null) {
                // No active device found. Exit replace device activity.
                throw new DeviceReplaceException(INVALID_INACTIVATED_DEVICE_FOR_REPLACEMENT);
            }
            disableActivationStateForDevice(hcpInfo.getFactoryId(), userId);

            // Update the HCPInfo table with new details
            hcpInfo.setFactoryId(String.valueOf(replaceDataFromDb.getId()));
            hcpInfo.setSerialNumber(replaceDataFromDb.getSerialNumber());
            LOGGER.info("## DeviceReplace: Updating device details in HCPInfo table");
            hcpInfoDao.updateForReplaceDevice(hcpInfo);

            // Update Device table with new details
            String passcode = getPassCode();
            String harmanId = hcpInfo.getHarmanId();
            LOGGER.info("## DeviceReplace: New Passcode generated for the HarmanID : {}", harmanId);
            Device newDevice = new Device(harmanId, null, passcode, 0);
            deviceDao.updateForReplaceDevice(newDevice);
            deviceActivationStateDao.disableActivationReadyByFacotryId(replaceDataFromDb.getId());
            // Update Activation table with new details
            updateActivationTable(userId, replaceDataFromDb);

            DeviceAssociation deviceAssociation = deviceAssociationDao
                .findValidAssociations(currentDataFromDb.getSerialNumber());
            if (deviceAssociation == null) {
                throw new DeviceReplaceException(ASSOCIATION_DATA_DOES_NOT_EXIST_FOR_GIVEN_INPUT);
            }

            deviceAssociationDao.updateForReplaceDevice(deviceAssociation.getId(), replaceDataFromDb.getSerialNumber(),
                userId, replaceDataFromDb.getId());

            // Update the factory data state to activated
            deviceInfoFactoryDataDao.changeDeviceState(replaceDataFromDb.getId(), DeviceState.ACTIVE.toString(),
                "New Device is activated for the old device with imei - " + currentDataFromDb.getImei());

            LOGGER.info("## isCurrentDeviceToBeMovedToProvisioned: {}", isCurrentDeviceToBeMovedToProvisioned);
            if (isCurrentDeviceToBeMovedToProvisioned) {
                // Update current device state to PROVISIONED
                updateToProvisioned(currentDataFromDb);
            }
            LOGGER.info("## Deleting the device :{} from Spring Auth", harmanId);
            deRegisterFromSpringAuth(harmanId);

            LOGGER.info("## Registering the device :{} with Spring Auth again with new passcode", harmanId);
            replaceSpringAuth(newDevice, deviceAssociation);

            if (isSendResetDeviceEnabled) {
                resetDevice(currentDataFromDb.getImei());
            }
        } catch (javax.naming.directory.InvalidAttributeValueException e) {
            throw new DeviceReplaceException(e);
        }
    }

    /**
     * Updates the activation table with the provided user ID and device information.
     *
     * @param userId The ID of the user initiating the activation.
     * @param replaceDataFromDb The device information to be replaced in the activation table.
     */
    private void updateActivationTable(String userId, DeviceInfoFactoryData replaceDataFromDb) {
        Timestamp activationDate = new Timestamp(new Date().getTime());
        DeviceActivationState deviceActivation = new DeviceActivationState();
        deviceActivation.setActivationInitiatedOn(activationDate);
        deviceActivation.setActivationInitiatedBy(userId);
        deviceActivation.setActivationReady(true);
        deviceActivation.setFactoryDataId(replaceDataFromDb.getId());
        deviceActivation.setSerialNumber(replaceDataFromDb.getSerialNumber());
        LOGGER.info("## DeviceReplace: Updating activation table with the details : {}", deviceActivation);
        deviceActivationStateDao.insert(deviceActivation);
    }

    /**
     * Updates the device state to PROVISIONED for the given device information.
     * If the current state is ACTIVE or READY_TO_ACTIVATE, the device state is changed to PROVISIONED.
     * If the current state is STOLEN or FAULTY, the device state is changed to PROVISIONED.
     * If the current state is neither of the above, no update is performed.
     *
     * @param currentDataFromDb the current device information from the database
     */
    private void updateToProvisioned(DeviceInfoFactoryData currentDataFromDb) {
        String currentState = currentDataFromDb.getState();
        LOGGER.info("## Update current device state:{} to PROVISIONED", currentState);
        if (DeviceState.ACTIVE.getValue().equals(currentState)
            || DeviceState.READY_TO_ACTIVATE.getValue().equals(currentState)) {
            deviceInfoFactoryDataDao.changeDeviceState(currentDataFromDb.getId(),
                DeviceState.PROVISIONED.getValue(),
                OLD_DEVICE_PROVISION_WITH_IMEI + currentDataFromDb.getImei());
        } else if (DeviceState.STOLEN.getValue().equals(currentState)
            || DeviceState.FAULTY.getValue().equals(currentState)) {
            deviceInfoFactoryDataDao.changeDeviceStateForStolenOrFaulty(currentDataFromDb.getId(),
                DeviceState.PROVISIONED.getValue(),
                OLD_DEVICE_PROVISION_WITH_IMEI + currentDataFromDb.getImei());
        } else {
            LOGGER.info("## Update of current device state is not required while Current Device State is :{}",
                currentState);
        }
    }

    /**
     * Replaces the Spring Auth information for a device with the provided new device information.
     * This method fetches a Spring Auth token, updates the registered client in Spring Auth with the new device
     * information,
     * and logs the success message if the update is successful.
     *
     * @param newDevice The new device information.
     * @param deviceAssociation The device association information.
     * @throws javax.naming.directory.InvalidAttributeValueException If an invalid attribute value is encountered.
     */
    private void replaceSpringAuth(Device newDevice, DeviceAssociation deviceAssociation)
        throws javax.naming.directory.InvalidAttributeValueException {
        String authToken = springAuthTokenGenerator.fetchSpringAuthToken();
        LOGGER.info(UPDATING_REG_CLIENT_IN_SPRING_AUTH, newDevice.getHarmanId());
        springAuthRestClient.updateRegisteredClient(authToken, newDevice.getHarmanId(), newDevice.getPasscode(),
            deviceAssociation.getDeviceType(), APPROVED);
        LOGGER.info(SUCCESSFULLY_UPDATED_REG_CLIENT_IN_SPRING_AUTH, newDevice.getHarmanId());
    }

    /**
     * Resets the device with the specified IMEI.
     *
     * @param imei The IMEI of the device to reset.
     */
    private void resetDevice(String imei) {
        String url = String.format("%s/%s", baseApiUrl, resetDeviceUrlSegment);
        String body = String.format("{\"imei\":\"%s\", \"deviceAction\":\"RESET\"}", imei);

        HttpHeaders headers = HttpPlatformUtil.generateAuthHeader(envConfig, hcpRestClientLibrary1);

        ResponseEntity<?> response = hcpRestClientLibrary1.doPost(url, headers, body, String.class);

        if (response.getStatusCode() != OK) {
            LOGGER.error("Failed reseting device: imei - {}", imei);
        }
    }

    /**
     * Generates a passcode by combining a random alphanumeric string with the current system time.
     *
     * @return The generated passcode.
     */
    private String getPassCode() {
        return RandomStringUtils.randomAlphanumeric(COUNT_64) + System.currentTimeMillis();
    }

    /**
     * Converts the given object to a DeviceInfoFactoryData object.
     *
     * @param object the object to be converted
     * @return the converted DeviceInfoFactoryData object
     */
    private DeviceInfoFactoryData convertToDeviceInfo(Object object) {
        DeviceInfoFactoryData deviceInfoData = new DeviceInfoFactoryData();
        BeanUtils.copyProperties(object, deviceInfoData);
        LOGGER.info("Device Info Data : {}", deviceInfoData);
        return deviceInfoData;
    }


    /**
     * Converts the given object to a DeviceInfoFactoryData object by copying properties.
     *
     * @param object the object to be converted
     * @return the converted DeviceInfoFactoryData object
     */
    private DeviceInfoFactoryData convertToDeviceInfoFromReplaceDeviceDataPojo(Object object) {
        DeviceInfoFactoryData deviceInfoData = new DeviceInfoFactoryData();
        BeanUtils.copyProperties(object, deviceInfoData, MANUFACTURING_DATE, RECORD_DATE);
        LOGGER.info("Device Info Data : {}", deviceInfoData);
        return deviceInfoData;
    }

    /**
     * Performs a state change for a device association.
     *
     * @param stateChangeRequest The request object containing the necessary data for the state change.
     * @throws NoSuchEntityException JsonProcessingException If an error occurs during the state change process.
     */
    public void stateChange(StateChangeRequest stateChangeRequest)
            throws NoSuchEntityException, JsonProcessingException {
        validateInputData(stateChangeRequest);
        Long data = fetchFactoryId(stateChangeRequest);
        if (!deviceAssociationDao.checkAssociatedDeviceWithFactDataNotDisassociated(data,
            stateChangeRequest.getUserId())) {
            String internalUserCsv = envConfig.getStringValue(DeviceAssocationProperty.WHITE_LIST_USERS);
            if (internalUserCsv != null && !internalUserCsv.isEmpty()) {
                List<String> internalUserList = new ArrayList<>(Arrays.asList(internalUserCsv.split(",")));
                if (internalUserList.stream().noneMatch(stateChangeRequest.getUserId()::contains)) {
                    LOGGER.debug("user is not white listed");
                    throw new InvalidUserAssociation();
                } else {
                    LOGGER.debug("White listed user : {}. Continuing with state change process.",
                        stateChangeRequest.getUserId());
                }
            } else {

                LOGGER.debug("No white listed users found");
                throw new InvalidUserAssociation();
            }
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set(USER_ID, stateChangeRequest.getUserId());
        String baseUrl = envConfig.getStringValue(DeviceAssocationProperty.SERVICE_DEVICEINFO_REST_URL_BASE);
        String deviceInfoPath = envConfig
            .getStringValue(DeviceAssocationProperty.SERVICE_DEVICEINFO_REST_DEVICE_INFO_CHANGE);

        try {
            StateChange requestBody = new StateChange();
            requestBody.setFactoryId(data);
            // to make sure state passed is valid or not.
            requestBody.setState(DeviceState.valueOf(stateChangeRequest.getState()));
            LOGGER.info("Calling device info query service with data : {}", requestBody);
            ResponseEntity<Object> response = hcpRestClientLibrary1.doPut(baseUrl + deviceInfoPath, httpHeaders,
                requestBody, Object.class);
            LOGGER.info("Exiting device info query service with response {}", response.getStatusCode());
        } catch (HttpClientErrorException exp) {
            String response = exp.getResponseBodyAsString();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(response);
            JsonNode message = jsonNode.path("error").path("message");
            LOGGER.error("Exiting with exception from device info query service with exception {} ", message);
            throw new NoSuchEntityException(message.asText());
        } catch (Exception ex) {
            LOGGER.error("Exiting with exception from device info query service ");
            throw new NoSuchEntityException(ex.getMessage());
        }

    }

    /**
     * Validates the input data for a state change request.
     *
     * @param stateChangeRequest The state change request to validate.
     * @throws NoSuchEntityException If the state or basic information is missing in the request.
     */
    private void validateInputData(StateChangeRequest stateChangeRequest) throws NoSuchEntityException {
        if (StringUtils.isEmpty(stateChangeRequest.getState())) {
            throw new NoSuchEntityException(STATE_MANDATORY);
        }
        if (StringUtils.isEmpty(stateChangeRequest.getImei())
            && StringUtils.isEmpty(stateChangeRequest.getDeviceId())) {
            throw new NoSuchEntityException(BASIC_MANDATORY_STATE_CHANGE);
        }
    }

    /**
     * Fetches the factory ID based on the given state change request.
     *
     * @param stateChangeRequest The state change request containing the IMEI or device ID.
     * @return The factory ID associated with the state change request.
     * @throws NoSuchEntityException If no factory data is found for the given IMEI or device ID.
     */
    private Long fetchFactoryId(StateChangeRequest stateChangeRequest) throws NoSuchEntityException {
        Long data = Long.valueOf(0);
        if (StringUtils.isNotEmpty(stateChangeRequest.getImei())) {
            DeviceInfoFactoryData factoryData = deviceInfoFactoryDataDao
                .findByFactoryImei(stateChangeRequest.getImei());
            data = factoryData.getId();
            // check if factory data is available from db or not.
            if (data.longValue() == 0) {
                throw new NoSuchEntityException(NO_DATA_FOUND);
            }
        } else if (StringUtils.isNotEmpty(stateChangeRequest.getDeviceId())) {

            HcpInfo hcpData = hcpInfoDao.findByDeviceId(stateChangeRequest.getDeviceId());
            if (StringUtils.isEmpty(hcpData.getFactoryId())) {
                throw new NoSuchEntityException(NO_DATA_FOUND);
            }
            data = Long.valueOf(hcpData.getFactoryId());
        }
        return data;
    }

    /**
     * De-registers the specified device from Spring Auth.
     *
     * @param deviceId the ID of the device to be de-registered
     * @throws javax.naming.directory.InvalidAttributeValueException if the device ID is invalid
     * @throws SpringAuthClientException if an error occurs while de-registering the device from Spring Auth
     */
    private void deRegisterFromSpringAuth(String deviceId) {
        try {
            String authToken = springAuthTokenGenerator.fetchSpringAuthToken();
            LOGGER.info("Deleting registered client from Spring Auth, deviceId: {}", deviceId);
            springAuthRestClient.deleteRegisteredClient(authToken, deviceId);
            LOGGER.info("Deleted registered client from Spring Auth successfully, deviceId: {}", deviceId);
        } catch (Exception e) {
            LOGGER.error("Error while deRegisterFromSpringAuth");
            throw new SpringAuthClientException("Error while deRegisterFromSpringAuth {}", e);
        }
    }

    /**
     * Validates and retrieves the DeviceInfoFactoryData based on the provided list of data values and user ID.
     *
     * @param listDataValues The list of DeviceInfoFactoryData values to validate against.
     * @param userId The user ID to check association with the DeviceInfoFactoryData.
     * @return The DeviceInfoFactoryData object if associated with the user ID.
     * @throws InvalidUserAssociation If no associated DeviceInfoFactoryData is found.
     */
    private DeviceInfoFactoryData validateGetDeviceInfoFactoryData(List<DeviceInfoFactoryData> listDataValues,
                                                                   String userId) {

        for (DeviceInfoFactoryData factoryData : listDataValues) {
            if (deviceAssociationDao.checkAssociatedDeviceWithFactData(factoryData.getId(), userId)) {
                return factoryData;
            }
        }
        throw new InvalidUserAssociation();
    }

    /**
     * Validates the PIN provided in the AssociateDeviceRequest.
     * If PIN validation is enabled and the PIN is invalid, an InvalidPinException is thrown.
     *
     * @param associateDeviceRequest The AssociateDeviceRequest containing the PIN to be validated.
     * @throws InvalidPinException If PIN validation is enabled and the PIN is invalid.
     */
    private void validatePin(AssociateDeviceRequest associateDeviceRequest) {
        if (pinValidation) {
            throw new InvalidPinException(
                    MessageFormat.format("Failed PIN validation: {0}", associateDeviceRequest));
        }
    }

    /**
     * Checks if the associated device is in the same state as the expected state.
     *
     * @param associateDeviceRequest The AssociateDeviceRequest object containing the device information.
     * @param expectedState The expected state of the device.
     * @return true if the associated device is in the same state as the expected state, false otherwise.
     */
    public boolean isInSameState(AssociateDeviceRequest associateDeviceRequest, String expectedState) {
        List<FactoryData> fetchFactoryData = deviceAssociationDao.constructAndFetchFactoryData(associateDeviceRequest);
        return (fetchFactoryData != null && !fetchFactoryData.isEmpty() && fetchFactoryData.get(0) != null
                && expectedState.equalsIgnoreCase(fetchFactoryData.get(0).getState()));
    }

    /**
     * Retrieves the association history for a given IMEI.
     *
     * @param imei     the IMEI of the device
     * @param orderby  the field to order the results by (default: "desc")
     * @param sortby   the field to sort the results by (default: "user_id")
     * @param page     the page number of the results
     * @param size     the number of results per page
     * @return a list of DeviceAssociationHistory objects representing the association history
     * @throws NoSuchEntityException if the association history is not found
     */
    public List<DeviceAssociationHistory> getAssociationHistory(String imei, String orderby, String sortby, int page,
                                                                int size) throws NoSuchEntityException {

        if (StringUtils.isBlank(sortby)) {
            sortby = "user_id";
        } else if ("userid".equalsIgnoreCase(sortby)) {

            sortby = "user_id";
        } else if ("associationstatus".equalsIgnoreCase(sortby)) {

            sortby = "association_status";
        }

        if (StringUtils.isBlank(orderby)) {
            orderby = "desc";
        }

        Long factoryId = null;
        try {
            factoryId = getDeviceInfoFactoryDataByImei(imei);
        } catch (IncorrectResultSizeDataAccessException ex) {
            throw new NoSuchEntityException(ASSOCIATION_HISTORY_NOT_FOUND);
        }
        return deviceAssociationDao.getAssociationDetails(factoryId, orderby, sortby, page, size);
    }

    /**
     * Retrieves the total count of association history for a given IMEI.
     *
     * @param imei The IMEI of the device.
     * @return The total count of association history.
     * @throws NoSuchEntityException If no entity is found for the given IMEI.
     */
    public int getAssociationHistoryTotalCount(String imei) throws NoSuchEntityException {
        Long factoryId = null;
        try {
            factoryId = getDeviceInfoFactoryDataByImei(imei);
        } catch (IncorrectResultSizeDataAccessException ex) {
            throw new NoSuchEntityException(NO_DATA_FOUND);
        }
        return deviceAssociationDao.findAssociationCountForFactoryId(factoryId);
    }

    /**
     * Retrieves the factory data ID associated with the given IMEI.
     *
     * @param imei The IMEI (International Mobile Equipment Identity) number of the device.
     * @return The factory data ID associated with the given IMEI, or null if not found.
     */
    public Long getDeviceInfoFactoryDataByImei(String imei) {
        return deviceInfoFactoryDataDao.findIdByFactoryImei(imei);
    }

    /**
     * Performs a SWM vehicle update based on the provided replaceDeviceRequest and VIN.
     *
     * @param replaceDeviceRequest The replace device request containing the replacement data.
     * @param vin The VIN (Vehicle Identification Number) of the device to be updated.
     * @throws InvalidAttributeValueException If the chassis number or production week is empty or null.
     * @throws UpdateDeviceException If there is an error updating the device in the SWM (Server Workload Manager)
     *      system.
     */
    public void performSwmVehicleUpdate(ReplaceFactoryDataRequest replaceDeviceRequest, String vin)
        throws InvalidAttributeValueException {

        String replaceWithChassisNumber = replaceDeviceRequest.getReplaceWith().getChassisNumber();
        String replaceWithProductionWeek = replaceDeviceRequest.getReplaceWith().getProductionWeek();
        LOGGER.debug("## replaceWithChassisNumber: {}", replaceWithChassisNumber);
        LOGGER.debug("## replaceWithProductionWeek: {}", replaceWithProductionWeek);
        if (!(StringUtils.isNotEmpty(replaceWithChassisNumber)
            && StringUtils.isNotEmpty(replaceWithProductionWeek))) {
            throw new InvalidAttributeValueException("Chassis number and production week is mandatory");
        }

        if (vin != null) {
            SwmUpdateVehicleRequest swmUpdateVehicleRequest = new SwmUpdateVehicleRequest();
            swmUpdateVehicleRequest.setChassisNumber(replaceDeviceRequest.getReplaceWith().getChassisNumber());
            swmUpdateVehicleRequest.setProductionWeek(replaceDeviceRequest.getReplaceWith().getProductionWeek());
            swmUpdateVehicleRequest.setPlant(replaceDeviceRequest.getReplaceWith().getPlant());
            swmUpdateVehicleRequest.setVin(replaceDeviceRequest.getReplaceWith().getVin());
            Map<String, String> specificAttributesMap = new HashMap<>();
            specificAttributesMap.put(VEHICLE_MODEL_YEAR, replaceDeviceRequest.getReplaceWith().getVehicleModelYear());
            swmUpdateVehicleRequest.setSpecificAttributes(specificAttributesMap);
            try {
                boolean deleteDeviceStatus = swmService.updateVehicle(swmUpdateVehicleRequest);
                if (deleteDeviceStatus) {
                    LOGGER.info("## Device deleted from SWM successfully");
                } else {
                    throw new UpdateDeviceException("Cannot update device due to swm internal server error");
                }
            } catch (Exception e) {
                throw new UpdateDeviceException("Cannot update device due to swm internal server error", e);
            }
        } else {
            throw new UpdateDeviceException("Cannot update device due vin does not exist in db!");
        }
    }

    /**
     * Replaces an IVI device with a new device based on the provided request data.
     *
     * @param replaceDeviceRequest The request data for replacing the device.
     * @param userId The ID of the user performing the device replacement.
     * @throws DeviceReplaceException If there is an error during the device replacement process.
     */
    public void replaceIviDevice(ReplaceFactoryDataRequest replaceDeviceRequest, String userId) {
        try {
            String vin = deviceAssociationDao.getAssociatedVinForReplaceApi(replaceDeviceRequest.getSerialNumber());

            LOGGER.info("## Vin : {}", vin);

            if (!isValidIviReplaceRequestData(replaceDeviceRequest)) {
                throw new DeviceReplaceException(INVALID_REPLACE_REQUEST_DATA);
            }
            DeviceInfoFactoryData currentDataFromDb = deviceInfoFactoryDataDao
                .findFactoryDataBySerialNumber(replaceDeviceRequest.getSerialNumber());
            LOGGER.info("Current Data From DB {} for the serial number {} ", currentDataFromDb,
                replaceDeviceRequest.getSerialNumber());

            validateCurrentDataFromDb(currentDataFromDb, userId);

            //It will replace the null values of replace device with the original values of the previous device
            replaceIviDeviceDetails(userId, replaceDeviceRequest, currentDataFromDb);

            List<DeviceInfoFactoryData> listReplaceValueData = deviceInfoFactoryDataDao
                .constructAndFetchFactoryData(convertToDeviceInfo(replaceDeviceRequest.getReplaceWith()));

            LOGGER.info("## List Replace Value Data: {}", listReplaceValueData);
            if (CollectionUtils.isEmpty(listReplaceValueData)) {
                throw new DeviceReplaceException(INVALID_CURRENT_FACTORY_DATA_FOR_REPLACE);
            }
            if (listReplaceValueData.size() > 1) {
                throw new DeviceReplaceException(
                    "Replace factory data - Database Integrity Error. There is more than one record");
            }
            DeviceInfoFactoryData replaceDataFromDb = listReplaceValueData.get(0);
            replaceDataFromDb.setVin(vin);
            if (!replaceDataFromDb.getState().equals(DeviceState.PROVISIONED.toString())) {
                throw new DeviceReplaceException("Passed Replace factory data is in " + replaceDataFromDb.getState()
                    + "  " + INVALID_DEVICE_REPLACEMENT_REPLACE_DATA_STATE);
            }

            // Fetch the data from HCPInfo table for the current data factoryId
            HcpInfo hcpInfo = hcpInfoData(currentDataFromDb);

            disableActivationStateForDevice(hcpInfo.getFactoryId(), userId);

            updateHcpInfoDetails(replaceDataFromDb, hcpInfo);
            LOGGER.info("## HcpInfo Details updated successfully");

            deviceActivationStateDao.disableActivationReadyByFacotryId(replaceDataFromDb.getId());

            updateDeviceActivationStateDetails(replaceDataFromDb, userId);
            LOGGER.info("## DeviceActivationState Details updated successfully");

            DeviceAssociation deviceAssociation =
                updateDeviceAssociationDetails(currentDataFromDb, replaceDataFromDb, userId);
            LOGGER.info("## Device association details updated successfully");

            if (ObjectUtils.isEmpty(deviceAssociation)) {
                throw new DeviceReplaceException(ASSOCIATION_DATA_DOES_NOT_EXIST_FOR_GIVEN_INPUT);
            }
            deviceAssociationDao.replaceReferenceIdInVinDetails(replaceDataFromDb.getId(), vin);

            // Update the factory data state to activated
            deviceInfoFactoryDataDao.changeDeviceState(replaceDataFromDb.getId(), DeviceState.ACTIVE.toString(),
                "New Device is activated for the old device with imei - " + currentDataFromDb.getImei());

            String harmanId = hcpInfo.getHarmanId();
            Device newDevice = updateDeviceDetails(harmanId);
            LOGGER.info("## NewDevice Details updated successfully");

            updateDeviceState(currentDataFromDb);

            performSpringAuthRegistration(harmanId, newDevice, deviceAssociation);

            if (isSendResetDeviceEnabled) {
                resetDevice(currentDataFromDb.getImei());
            }
            //SWM SKIP
            performSwmIntegration(replaceDeviceRequest, vin);
        } catch (javax.naming.directory.InvalidAttributeValueException | InvalidAttributeValueException
            | ParseException e) {
            throw new DeviceReplaceException(e);
        }
    }

    /**
     * Replaces the IVI device details with the provided data.
     *
     * @param userId              The ID of the user performing the replacement.
     * @param replaceDeviceRequest The request object containing the replacement data.
     * @param currentDataFromDb    The current device information data from the database.
     * @throws ParseException If there is an error parsing the date strings.
     */
    private void replaceIviDeviceDetails(String userId, ReplaceFactoryDataRequest replaceDeviceRequest,
                                         DeviceInfoFactoryData currentDataFromDb) throws ParseException {
        ReplaceDeviceDataPojo replaceDeviceDataPojo = recreateReplaceDevicePojo(currentDataFromDb,
            replaceDeviceRequest.getReplaceWith());
        LOGGER.info("REPLACE DEVICE DATA POJO {} ", replaceDeviceDataPojo);

        DeviceInfoFactoryData replaceDevice = convertToDeviceInfoFromReplaceDeviceDataPojo(replaceDeviceDataPojo);
        Timestamp createdTimestamp = new Timestamp(new Date().getTime());
        replaceDevice.setCreatedDate(createdTimestamp);
        replaceDevice.setFactoryAdmin(userId);

        Timestamp manufacturedTime;
        Timestamp recordDateTime;
        String manufacturingDate = replaceDeviceRequest.getReplaceWith().getManufacturingDate();
        String recordDate = replaceDeviceRequest.getReplaceWith().getRecordDate();

        if (StringUtils.isNotEmpty(manufacturingDate)) {
            manufacturedTime = new Timestamp(dateFormatter.parse(manufacturingDate).getTime());
            replaceDevice.setManufacturingDate(manufacturedTime);
        } else {
            replaceDevice.setManufacturingDate(currentDataFromDb.getManufacturingDate());
        }
        if (StringUtils.isNotEmpty(recordDate)) {
            recordDateTime = new Timestamp(dateFormatter.parse(recordDate).getTime());
            replaceDevice.setRecordDate(recordDateTime);
        } else {
            replaceDevice.setRecordDate(currentDataFromDb.getRecordDate());
        }
        LOGGER.info("## Replace Device Details: {}", replaceDevice);
        deviceInfoFactoryDataDao.insertData(replaceDevice, userId);
    }

    /**
     * Retrieves the HcpInfo object associated with the given DeviceInfoFactoryData.
     *
     * @param currentDataFromDb The DeviceInfoFactoryData object representing the current data from the database.
     * @return The HcpInfo object associated with the given DeviceInfoFactoryData.
     * @throws DeviceReplaceException If no active device is found for replacement.
     */
    private HcpInfo hcpInfoData(DeviceInfoFactoryData currentDataFromDb) {
        HcpInfo hcpInfo = hcpInfoDao.findActiveHcpInfo(currentDataFromDb.getId());
        if (hcpInfo == null) {
            LOGGER.debug("No Active device found, Exit replace device activity");
            throw new DeviceReplaceException(INVALID_INACTIVATED_DEVICE_FOR_REPLACEMENT);
        }
        return hcpInfo;
    }

    /**
     * Updates the device association details in the database.
     *
     * @param currentDataFromDb   The current device information from the database.
     * @param replaceDataFromDb   The replacement device information from the database.
     * @param userId              The user ID associated with the update.
     * @return                    The updated DeviceAssociation object.
     */
    private DeviceAssociation updateDeviceAssociationDetails(DeviceInfoFactoryData currentDataFromDb,
                                                             DeviceInfoFactoryData replaceDataFromDb, String userId) {
        DeviceAssociation deviceAssociation = deviceAssociationDao
            .findValidAssociations(currentDataFromDb.getSerialNumber());

        deviceAssociationDao.updateForReplaceDevice(deviceAssociation.getId(), replaceDataFromDb.getSerialNumber(),
            userId, replaceDataFromDb.getId());

        return deviceAssociation;
    }

    /**
     * Updates the device activation state details in the Activation table.
     *
     * @param replaceDataFromDb The DeviceInfoFactoryData object containing the data to be replaced.
     * @param userId The ID of the user initiating the activation.
     */
    private void updateDeviceActivationStateDetails(DeviceInfoFactoryData replaceDataFromDb, String userId) {
        // Update Activation table with new details
        Timestamp activationDate = new Timestamp(new Date().getTime());
        DeviceActivationState deviceActivation = new DeviceActivationState();
        deviceActivation.setActivationInitiatedOn(activationDate);
        deviceActivation.setActivationInitiatedBy(userId);
        deviceActivation.setActivationReady(true);
        deviceActivation.setFactoryDataId(replaceDataFromDb.getId());
        deviceActivation.setSerialNumber(replaceDataFromDb.getSerialNumber());
        LOGGER.info("DeviceReplace: Updating activation table with the details : {}", deviceActivation);
        deviceActivationStateDao.insertReplaceDeviceActivationState(deviceActivation);
    }

    /**
     * Updates the device details in the Device table with new information.
     * Generates a new passcode for the device and logs the action.
     *
     * @param harmanId the Harman ID of the device
     * @return the updated Device object
     */
    private Device updateDeviceDetails(String harmanId) {
        // Update Device table with new details
        String passcode = getPassCode();
        LOGGER.info("DeviceReplace: New Passcode generated for the HarmanID : {}", harmanId);
        Device newDevice = new Device(harmanId, null, passcode, 0);
        deviceDao.updateForReplaceDevice(newDevice);
        return newDevice;
    }

    /**
     * Updates the HCPInfo details with the provided DeviceInfoFactoryData.
     *
     * @param replaceDataFromDb The DeviceInfoFactoryData object containing the updated details.
     * @param hcpInfo The HcpInfo object to be updated.
     */
    private void updateHcpInfoDetails(DeviceInfoFactoryData replaceDataFromDb, HcpInfo hcpInfo) {
        // Update the HCPInfo table with new details
        hcpInfo.setFactoryId(String.valueOf(replaceDataFromDb.getId()));
        hcpInfo.setSerialNumber(replaceDataFromDb.getSerialNumber());
        LOGGER.info("DeviceReplace: Updating device details in HCPInfo table");
        hcpInfoDao.updateForReplaceDevice(hcpInfo);
    }

    /**
     * Recreates the ReplaceDeviceDataPojo object by copying properties from the currentDataFromDb object,
     * while ignoring certain properties specified in the ignoreList.
     *
     * @param currentDataFromDb The DeviceInfoFactoryData object containing the current data from the database.
     * @param replaceWith The ReplaceDeviceDataPojo object to be recreated.
     * @return The recreated ReplaceDeviceDataPojo object.
     */
    private ReplaceDeviceDataPojo recreateReplaceDevicePojo(DeviceInfoFactoryData currentDataFromDb,
                                                            ReplaceDeviceDataPojo replaceWith) {

        List<String> ignoreList = new LinkedList<>();

        ignoreList.add(SERIAL_NUMBER);
        ignoreList.add(MANUFACTURING_DATE);
        ignoreList.add(RECORD_DATE);

        if (replaceWith.getManufacturingDate() == null) {
            replaceWith.setManufacturingDate(currentDataFromDb.getManufacturingDate().toString());
        }
        if (replaceWith.getRecordDate() == null) {
            replaceWith.setRecordDate(currentDataFromDb.getManufacturingDate().toString());
        }

        if (StringUtils.isNotEmpty(replaceWith.getModel())) {
            ignoreList.add(MODEL);
            replaceWith.setModel(replaceWith.getModel());
        }
        if (StringUtils.isNotEmpty(replaceWith.getImei())) {
            ignoreList.add(IMEI);
            replaceWith.setImei(replaceWith.getImei());
        }
        if (StringUtils.isNotEmpty(replaceWith.getPlatformVersion())) {
            ignoreList.add(PLATFORM_VERSION);
            replaceWith.setPlatformVersion(replaceWith.getPlatformVersion());
        }
        if (StringUtils.isNotEmpty(replaceWith.getIccid())) {
            ignoreList.add(ICCID);
            replaceWith.setIccid(replaceWith.getIccid());
        }
        if (StringUtils.isNotEmpty(replaceWith.getSsid())) {
            ignoreList.add(SSID);
            replaceWith.setSsid(replaceWith.getSsid());
        }
        if (StringUtils.isNotEmpty(replaceWith.getBssid())) {
            ignoreList.add(BSSID);
            replaceWith.setBssid(replaceWith.getBssid());
        }
        if (StringUtils.isNotEmpty(replaceWith.getMsisdn())) {
            ignoreList.add(MSISDN);
            replaceWith.setMsisdn(replaceWith.getMsisdn());
        }
        if (StringUtils.isNotEmpty(replaceWith.getImsi())) {
            ignoreList.add(IMSI);
            replaceWith.setImsi(replaceWith.getImsi());
        }
        if (StringUtils.isNotEmpty(replaceWith.getPackageSerialNumber())) {
            ignoreList.add(PACKAGE_SERIAL_NUMBER);
            replaceWith.setPackageSerialNumber(replaceWith.getPackageSerialNumber());
        }
        if (StringUtils.isNotEmpty(replaceWith.getVin())) {
            ignoreList.add(VIN);
            replaceWith.setVin(replaceWith.getVin());
        }

        String[] ignoreArray = ignoreList.toArray(new String[0]);

        BeanUtils.copyProperties(currentDataFromDb, replaceWith, ignoreArray);

        return replaceWith;
    }

    /**
     * Validates the replace request and current value data.
     *
     * @param replaceDeviceRequest The replace factory data request.
     * @param userId The user ID.
     * @return The validated device info factory data.
     */
    private DeviceInfoFactoryData validateReplaceRequestAndCurrentValueData(
        ReplaceFactoryDataRequest replaceDeviceRequest, String userId) {
        if (!isValidReplaceRequestData(replaceDeviceRequest)) {
            throw new DeviceReplaceException(INVALID_REPLACE_REQUEST_DATA);
        }
        List<DeviceInfoFactoryData> listCurrentValueData = deviceInfoFactoryDataDao
            .constructAndFetchFactoryData(convertToDeviceInfo(replaceDeviceRequest.getCurrentValue()));

        if (CollectionUtils.isEmpty(listCurrentValueData)) {
            throw new DeviceReplaceException(INVALID_CURRENT_FACTORY_DATA_FOR_REPLACE);
        }
        DeviceInfoFactoryData currentDataFromDb = validateGetDeviceInfoFactoryData(listCurrentValueData, userId);
        if (!isCurrentDeviceToBeMovedToProvisioned && !currentDataFromDb.getFaulty()
                && !currentDataFromDb.getStolen()) {
            throw new DeviceReplaceException("Passed Current factory data is in " + currentDataFromDb.getState()
                    + INVALID_DEVICE_REPLACEMENT_CURRENT_DATA_STATE);
        }
        return currentDataFromDb;
    }

    /**
     * Validates the list of replace value data for device association.
     *
     * @param replaceDeviceRequest The request object containing the data to replace with.
     * @return The validated DeviceInfoFactoryData object.
     * @throws DeviceReplaceException If the current factory data for replace is invalid or if there is a database
     *      integrity error.
     */
    private DeviceInfoFactoryData validateListReplaceValueData(ReplaceFactoryDataRequest replaceDeviceRequest) {
        List<DeviceInfoFactoryData> listReplaceValueData = deviceInfoFactoryDataDao
            .constructAndFetchFactoryData(convertToDeviceInfo(replaceDeviceRequest.getReplaceWith()));
        if (CollectionUtils.isEmpty(listReplaceValueData)) {
            throw new DeviceReplaceException(INVALID_CURRENT_FACTORY_DATA_FOR_REPLACE);
        }
        if (listReplaceValueData.size() > 1) {
            throw new DeviceReplaceException(
                "Replace factory data - Database Integrity Error. There is more than one record");
        }
        return listReplaceValueData.get(0);
    }

    /**
     * Validates the current data retrieved from the database for device association.
     *
     * @param currentDataFromDb The current data retrieved from the database.
     * @param userId The user ID associated with the device.
     * @throws DeviceReplaceException If the current factory data is invalid or the user device details are invalid.
     */
    private void validateCurrentDataFromDb(DeviceInfoFactoryData currentDataFromDb, String userId) {
        if (ObjectUtils.isEmpty(currentDataFromDb)) {
            throw new DeviceReplaceException(INVALID_CURRENT_FACTORY_DATA_FOR_REPLACE);
        }

        if (!deviceAssociationDao.checkAssociatedDeviceWithFactData(currentDataFromDb.getId(), userId)) {
            throw new DeviceReplaceException(INVALID_USER_DEVICE_DETAILS);
        }

        if (!isCurrentDeviceToBeMovedToProvisioned && !currentDataFromDb.getFaulty()
                && !currentDataFromDb.getStolen()) {
            throw new DeviceReplaceException("Passed Current factory data is in " + currentDataFromDb.getState()
                    + INVALID_DEVICE_REPLACEMENT_CURRENT_DATA_STATE);
        }
    }

    /**
     * Performs the Spring Auth registration for a device.
     *
     * @param harmanId The Harman ID of the device.
     * @param newDevice The new device object.
     * @param deviceAssociation The device association object.
     * @throws javax.naming.directory.InvalidAttributeValueException If an invalid attribute value is encountered.
     */
    private void performSpringAuthRegistration(String harmanId, Device newDevice,
                                               DeviceAssociation deviceAssociation)
        throws javax.naming.directory.InvalidAttributeValueException {
        LOGGER.info("## Deleting the device :{} from Spring Auth", harmanId);
        deRegisterFromSpringAuth(harmanId);
        LOGGER.info("## Re-Registering the device: {} with Spring Auth again with new passcode", harmanId);
        String authToken = springAuthTokenGenerator.fetchSpringAuthToken();
        LOGGER.info(UPDATING_REG_CLIENT_IN_SPRING_AUTH, newDevice.getHarmanId());
        springAuthRestClient.updateRegisteredClient(authToken, newDevice.getHarmanId(), newDevice.getPasscode(),
            deviceAssociation.getDeviceType(), APPROVED);
        LOGGER.info(SUCCESSFULLY_UPDATED_REG_CLIENT_IN_SPRING_AUTH, newDevice.getHarmanId());
    }

    /**
     * Updates the state of the current device based on the value of the isCurrentDeviceToBeMovedToProvisioned flag.
     * If the flag is true, the current device state is updated to PROVISIONED.
     *
     * @param currentDataFromDb The DeviceInfoFactoryData object containing the current device information from the
*                          database.
     */
    private void updateDeviceState(DeviceInfoFactoryData currentDataFromDb) {
        LOGGER.info("isCurrentDeviceToBeMovedToProvisioned: {}", isCurrentDeviceToBeMovedToProvisioned);
        if (isCurrentDeviceToBeMovedToProvisioned) {
            // Update current device state to PROVISIONED
            String currentState = currentDataFromDb.getState();
            LOGGER.debug("Update current device state:{} to PROVISIONED", currentState);
            if (DeviceState.ACTIVE.getValue().equals(currentState)
                || DeviceState.READY_TO_ACTIVATE.getValue().equals(currentState)) {
                deviceInfoFactoryDataDao.changeDeviceState(currentDataFromDb.getId(),
                    DeviceState.PROVISIONED.getValue(),
                    OLD_DEVICE_PROVISION_WITH_IMEI + currentDataFromDb.getImei());
            } else if (DeviceState.STOLEN.getValue().equals(currentState)
                || DeviceState.FAULTY.getValue().equals(currentState)) {
                deviceInfoFactoryDataDao.changeDeviceStateForStolenOrFaulty(currentDataFromDb.getId(),
                    DeviceState.PROVISIONED.getValue(),
                    OLD_DEVICE_PROVISION_WITH_IMEI + currentDataFromDb.getImei());
            } else {
                LOGGER.debug("Update of current device state is not required while Current Device State is :{}",
                    currentState);
            }
        }
    }

    /**
     * Performs SWM integration for replacing factory data.
     *
     * @param replaceDeviceRequest The ReplaceFactoryDataRequest object containing the replacement device data.
     * @param vin The VIN (Vehicle Identification Number) of the vehicle.
     * @throws InvalidAttributeValueException If there is an invalid attribute value.
     */
    private void performSwmIntegration(ReplaceFactoryDataRequest replaceDeviceRequest, String vin)
        throws InvalidAttributeValueException {
        if (swmIntegrationEnabled) {
            LOGGER.info("## Updating vehicle into SWM -START ");
            // SWM UPDATE VEHICLE
            String replaceWithChassisNumber = replaceDeviceRequest.getReplaceWith().getChassisNumber();
            String replaceWithProductionWeek = replaceDeviceRequest.getReplaceWith().getProductionWeek();
            LOGGER.debug("## replaceWithChassisNumber: {}", replaceWithChassisNumber);
            LOGGER.debug("## replaceWithProductionWeek: {}", replaceWithProductionWeek);
            if (StringUtils.isNotEmpty(replaceWithChassisNumber)
                && StringUtils.isNotEmpty(replaceWithProductionWeek)) {
                performSwmVehicleUpdate(replaceDeviceRequest, vin);
            } else {
                LOGGER.info(
                    "Skipping Swm Vehicle Update since mandatory fields : ChassisNumber or ProductionWeek is empty");
            }
        } else {
            LOGGER.info("Skipping Swm Update since swm integration enabled flag is false");
        }
    }
}