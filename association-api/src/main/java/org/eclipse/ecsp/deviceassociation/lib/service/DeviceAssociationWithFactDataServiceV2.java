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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.ecsp.common.ErrorUtils;
import org.eclipse.ecsp.deviceassociation.dto.AssociationUpdateDto;
import org.eclipse.ecsp.deviceassociation.dto.M2Mterminate;
import org.eclipse.ecsp.deviceassociation.lib.config.DeviceAssocationProperty;
import org.eclipse.ecsp.deviceassociation.lib.enums.DeviceAttributeEnums;
import org.eclipse.ecsp.deviceassociation.lib.exception.InvalidUserAssociation;
import org.eclipse.ecsp.deviceassociation.lib.exception.NoSuchEntityException;
import org.eclipse.ecsp.deviceassociation.lib.exception.ObserverMessageProcessFailureException;
import org.eclipse.ecsp.deviceassociation.lib.model.AssociationStatus;
import org.eclipse.ecsp.deviceassociation.lib.model.DeviceAssociation;
import org.eclipse.ecsp.deviceassociation.lib.model.DeviceAssociationHistory;
import org.eclipse.ecsp.deviceassociation.lib.model.SimDetails;
import org.eclipse.ecsp.deviceassociation.lib.model.wam.SimTransactionStatus;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociateDeviceRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociateDeviceResponse;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociationDetailsRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociationDetailsResponse;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.CurrentDeviceDataPojo;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DelegateAssociationRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceStatusRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.FactoryData;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.ReplaceDeviceDataPojo;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.ReplaceFactoryDataRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.StateChange;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.StateChangeRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.support.HttpPlatformUtil;
import org.eclipse.ecsp.deviceassociation.lib.rest.support.SpringAuthTokenGenerator;
import org.eclipse.ecsp.exception.shared.ApiNotificationException;
import org.eclipse.ecsp.exception.shared.ApiPreConditionFailedException;
import org.eclipse.ecsp.exception.shared.ApiResourceNotFoundException;
import org.eclipse.ecsp.exception.shared.ApiTechnicalException;
import org.eclipse.ecsp.exception.shared.ApiValidationFailedException;
import org.eclipse.ecsp.notification.lib.model.nc.UserProfile;
import org.eclipse.ecsp.notification.lib.rest.NotificationCenterClient;
import org.eclipse.ecsp.services.clientlib.HcpRestClientLibrary;
import org.eclipse.ecsp.services.device.dao.DeviceDao;
import org.eclipse.ecsp.services.device.model.Device;
import org.eclipse.ecsp.services.deviceactivation.model.DeviceActivationState;
import org.eclipse.ecsp.services.factorydata.domain.DeviceInfoFactoryData;
import org.eclipse.ecsp.services.factorydata.domain.DeviceState;
import org.eclipse.ecsp.services.shared.db.HcpInfo;
import org.eclipse.ecsp.services.shared.deviceinfo.model.DeviceInfo;
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
import org.springframework.web.client.HttpClientErrorException;

import javax.management.InvalidAttributeValueException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.eclipse.ecsp.deviceassociation.lib.model.AssociationStatus.ASSOCIATED;
import static org.eclipse.ecsp.deviceassociation.lib.model.AssociationStatus.ASSOCIATION_INITIATED;
import static org.eclipse.ecsp.deviceassociation.lib.model.AssociationStatus.DISASSOCIATED;
import static org.eclipse.ecsp.deviceassociation.lib.model.AssociationStatus.SUSPENDED;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.ASSOCIATION_DATA_DOES_NOT_EXIST_FOR_GIVEN_INPUT;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.ASSO_DATA_NOT_FOUND;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.ASSO_DETAILS_NOT_FOUND;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.ASSO_INTEGRITY_ERROR;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.ASSO_NOTIF_ERROR;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.BASIC_DATA_MANDATORY;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.BASIC_MANDATORY_STATE_CHANGE;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.DATABASE_INTEGRITY_ERROR;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.DELIGATION_ASSOCIATION_TYPE_VALIDATION_FAILED;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.DEVICE_ASSO_INTEGRITY_ERR;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.DE_REGISTER_WITH_SPRINGAUTH_FAILED;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.FACTORY_DATA_NO_DATA_FOUND;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.INVALID_CURRENT_FACTORY_DATA_FOR_REPLACE;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.INVALID_DEVICE_REPLACEMENT_CURRENT_DATA_STATE;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.INVALID_DEVICE_REPLACEMENT_REPLACE_DATA_STATE;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.INVALID_INACTIVATED_DEVICE_FOR_REPLACEMENT;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.INVALID_REPLACE_REQUEST_DATA;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.INVALID_USER_DETAILS;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.M2M_ASSOC_INTEGRITY_ERROR;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.NEW_ASSOCIATION_TYPE_CANNOT_BE_UPDATED_TO_OWNER;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.OWNER_ASSO_NOT_FOUND;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.OWNER_TERMINATION_VALIDATION_FAILED;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.OWNER_VALIDATION_FAILED;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.REGISTER_WITH_SPRINGAUTH_FAILED;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.SIM_ACTIVATION_PENDING;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.SIM_SUSPEND_CONDITION_FAILED;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.START_END_TIME_VALIDATION_FAILED;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.STATE_MANDATORY;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.SUSPEND_FAILED;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.USER_ID_TYPE_INVALID;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.USER_NOT_OWNER_OF_DEVICE;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.USER_NOT_WHITELISTED;
import static org.springframework.http.HttpStatus.OK;

/**
 * This class represents a service for device association with fact data.
 * It extends the AbstractDeviceAssociationService class and provides methods for associating devices.
 * The class is annotated with @Service to indicate that it is a Spring service component.
 * It is also annotated with @Transactional to enable transaction management for the methods in this class.
 */
@Service
@Transactional
public class DeviceAssociationWithFactDataServiceV2 extends AbstractDeviceAssociationService {

    private static final String EXTERNAL = "external";
    private static final String INTERNAL = "internal";
    private static final String USER_NAME = "userName";
    private static final String USER_VEHICLE_AUTH_STATUS = "userVehicleAuthStatus";
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceAssociationWithFactDataServiceV2.class);
    private static final String CARRIAGE_AND_NEWLINE_REGEX = "[\r\n]";
    private static final String STOLEN_OR_FAULTY = "STOLEN_OR_FAULTY";
    private static final String PROVISIONED = "PROVISIONED";
    private static final String PROVISIONED_ALIVE = "PROVISIONED_ALIVE";
    private static final String HARMAN_ID = "HarmanId";
    private static final String UPDATE_REGISTERED_CLIENT_SPRING_AUTH =
            "Updating registered client in Spring Auth, deviceId: {}";
    private static final String APPROVED = "approved";
    private static final String UPDATE_REGISTERED_CLIENT_SPRING_AUTH_SUCCEEDED =
            "Successfully updated registered client in Spring Auth, deviceId: {}";
    public static final int COUNT = -1;
    public static final int ITER = 2;
    public static final int COUNT_64 = 64;

    @Autowired
    @Lazy
    protected SpringAuthTokenGenerator springAuthTokenGenerator;

    @Value("${base_service_api_url:http://docker_host:8080/haa-api-dev}")
    private String baseApiUrl;

    @Value("${reset_device_url_segment:v1/device/deviceOperation}")
    private String resetDeviceUrlSegment;

    @Value("${send_reset_device_enabled:false}")
    private boolean isSendResetDeviceEnabled;

    @Value("${vin_association_enabled:false}")
    private boolean vinAssocEnabled;

    @Value("${sim_suspend_check:false}")
    private boolean simSuspendCheck;

    @Value("#{'${supported_device_info_params}'.split(',')}")
    private String[] supportedDeviceItems;

    @Value("${user_id_type:internal}")
    private String userIdType;

    @Autowired
    private SpringAuthRestClient springAuthRestClient;

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private HcpRestClientLibrary hcpRestClientLibrary1;

    @Autowired
    private NotificationCenterClient ncClient;

    @Autowired
    private DeviceAssociationService deviceAssociationService;

    @Autowired
    private SimStateManager simStateManager;

    @Autowired
    private UserManagementClient userManagerService;

    @Autowired
    private DeviceAssociationTypeService deviceAssociationTypeService;

    @Value("${pin.validation:false}")
    private boolean pinValidation;

    @Value("${current.device.provisioning:false}")
    private boolean isCurrentDeviceToBeMovedToProvisioned;

    @Value("${forbid_assoc_after_terminate:false}")
    private boolean forbidAssocAfterTerminate;

    @Value("${notification_center_base_url}")
    private String ncBaseUrl;

    @Value("${terminate_notification_id:Terminate}")
    private String notificationId;

    /**
     * defaultAssociationType from charts.
     *
     * <p>if defaultAssociationType is still "defaultOwner" then M2M is not enabled,
     * it should be changed to anything other than "defaultOwner"(e.g:"owner")
     * And this value should match with what is in association Type Charts.
     */
    @Value("${default_association_type:defaultOwner}")
    private String defaultAssociationType;

    /**
     * Associates a device with the provided device request.
     *
     * @param associateDeviceRequest The request object containing the device information.
     * @return The response object containing the associated device information.
     * @throws ApiValidationFailedException If the basic data (BSSID, IMEI, and Serial Number) is missing.
     */
    public AssociateDeviceResponse associateDevice(AssociateDeviceRequest associateDeviceRequest) {
        String associateDeviceRequestData =
                associateDeviceRequest.toString().replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "");
        LOGGER.debug("## associateDevice - START associateDeviceRequest: {}", associateDeviceRequestData);
        if (StringUtils.isEmpty(associateDeviceRequest.getBssid())
            && StringUtils.isEmpty(associateDeviceRequest.getImei())
            && StringUtils.isEmpty(associateDeviceRequest.getSerialNumber())) {
            throw new ApiValidationFailedException(ApiMessageEnum.BASIC_DATA_MANDATORY.getCode(),
                ApiMessageEnum.BASIC_DATA_MANDATORY.getMessage(),
                ApiMessageEnum.BASIC_DATA_MANDATORY.getGeneralMessage());
        }
        DeviceAssociation deviceAssociation = associate(associateDeviceRequest);
        LOGGER.debug("## associateDevice - END deviceAssociation: {}", deviceAssociation);
        return new AssociateDeviceResponse(deviceAssociation.getId(), ASSOCIATION_INITIATED);
    }

    /**
     * Associates a device with the provided device request.
     *
     * @param associateDeviceRequest The device request containing the necessary information for association.
     * @return The device association object.
     * @throws ApiValidationFailedException If there is an error in validating the request.
     * @throws ApiPreConditionFailedException If the device is terminated and cannot be associated again.
     * @throws IllegalArgumentException If the state passed for association is invalid.
     */
    private DeviceAssociation associate(AssociateDeviceRequest associateDeviceRequest) {
        LOGGER.debug("## associate Service - START imei: {}, serialNumber: {}", associateDeviceRequest.getImei(),
            associateDeviceRequest.getSerialNumber());
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        List<FactoryData> fetchFactoryData = deviceAssociationDao.constructAndFetchFactoryData(associateDeviceRequest);
        String fetchState;
        deviceAssociation.setUserId(associateDeviceRequest.getUserId());
        if (!fetchFactoryData.isEmpty() && fetchFactoryData.get(0) != null && fetchFactoryData.get(0).getId() != 0) {
            LOGGER.debug("## Factory data retrieved successfully, ID (PK): {}", fetchFactoryData.get(0).getId());
            deviceAssociation.setFactoryId(fetchFactoryData.get(0).getId());
            deviceAssociation.setSerialNumber(fetchFactoryData.get(0).getSerialNumber());
            fetchState = fetchFactoryData.get(0).getState();
            if (fetchFactoryData.get(0).isFaulty() || fetchFactoryData.get(0).isStolen()) {
                fetchState = STOLEN_OR_FAULTY;
            }
        } else {
            throw new ApiValidationFailedException(ApiMessageEnum.FETCHING_FACTORY_DATA_ERROR.getCode(),
                ApiMessageEnum.FETCHING_FACTORY_DATA_ERROR.getMessage(),
                ApiMessageEnum.FETCHING_FACTORY_DATA_ERROR.getGeneralMessage());
        }
        LOGGER.debug("## forbidAssocAfterTerminate flag: {} ", forbidAssocAfterTerminate);
        if (forbidAssocAfterTerminate && (!"".equals(fetchState)
            && (PROVISIONED.equals(fetchState) || PROVISIONED_ALIVE.equals(fetchState)))) {
            boolean isTerminated = deviceAssociationDao.isDeviceTerminated(fetchFactoryData.get(0).getId());
            if (isTerminated) {
                LOGGER.debug("The device with serial number: {} got terminated, hence can not be associated again",
                    fetchFactoryData.get(0).getSerialNumber());
                throw new ApiPreConditionFailedException(ApiMessageEnum.DEVICE_TERMINATED_MSG.getCode(),
                    ApiMessageEnum.DEVICE_TERMINATED_MSG.getMessage(),
                    ApiMessageEnum.DEVICE_TERMINATED_MSG.getGeneralMessage());
            }
        }
        LOGGER.info("## Device STATE: {} before proceed with device association", fetchState);
        switch (fetchState) {
            case STOLEN_OR_FAULTY:
                throw new ApiValidationFailedException(ApiMessageEnum.STOLEN_OR_FAULTY_MSG.getCode(),
                    ApiMessageEnum.STOLEN_OR_FAULTY_MSG.getMessage(),
                    ApiMessageEnum.STOLEN_OR_FAULTY_MSG.getGeneralMessage());
            case PROVISIONED, PROVISIONED_ALIVE:
                saveDeviceAssociation(deviceAssociation);
                LOGGER.info(
                    "## User association completed successfully with table updates, factoryId: {}, SerialNumber: {}",
                    deviceAssociation.getFactoryId(), deviceAssociation.getSerialNumber());
                break;
            default:
                String userId = getUserId(associateDeviceRequest.getImei(), associateDeviceRequest.getSerialNumber());
                LOGGER.error("## Invalid state passed for association. fetchState: {}, userId: {}", fetchState, userId);
                throw new ApiValidationFailedException(ApiMessageEnum.INVALID_FACTORY_STATE.getCode(),
                    ApiMessageEnum.INVALID_FACTORY_STATE.getMessage(),
                    ApiMessageEnum.INVALID_FACTORY_STATE.getGeneralMessage());
        }
        LOGGER.debug("## associate Service - END");
        return deviceAssociation;
    }

    /**
     * Retrieves the user ID associated with the given IMEI and serial number.
     *
     * @param imei The IMEI of the device.
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
     * @param userId the user ID to be masked
     * @return a masked string with asterisks (*) replacing characters in the user ID
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
     * Saves the device association information.
     *
     * @param deviceAssociation The device association object to be saved.
     */
    private void saveDeviceAssociation(DeviceAssociation deviceAssociation) {
        LOGGER.debug("## saveDeviceAssociation - START ");
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
        LOGGER.debug("## saveDeviceAssociation - END  device_association table factory_data (FK): {}",
            deviceAssociation.getFactoryId());
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
                        deviceAssociation.setDeviceType(factoryData.getDeviceType());
                        deviceAssociation.setMetadata(
                            getMetaData(deviceAssociation.getHarmanId(), deviceAssociation.getAssociationStatus()));

                        //use case to get vin and SIM details
                        getVinAndSetSimDetails(factoryData, deviceAssociation);
                    }
                }
            }
        }
        return deviceAssociations;
    }

    /**
     * Retrieves the metadata for a given Harman ID and association status.
     *
     * @param harmanId The Harman ID to retrieve metadata for.
     * @param associationStatus The association status to retrieve metadata for.
     * @return A map containing the metadata for the specified Harman ID and association status.
     */
    private Map<String, Object> getMetaData(String harmanId, AssociationStatus associationStatus) {

        List<DeviceInfo> deviceItems = deviceAssociationDao.findDeviceInfoByName(harmanId, supportedDeviceItems);
        Map<String, Object> deviceItemMap = deviceItems.stream()
            .collect(Collectors.toMap(DeviceInfo::getName, DeviceInfo::getValue));
        setDefaultValues(associationStatus, deviceItemMap);
        return deviceItemMap;
    }

    /**
     * Sets default values for the given association status and device item map.
     *
     * @param associationStatus The association status.
     * @param deviceItemMap The device item map.
     */
    private void setDefaultValues(AssociationStatus associationStatus, Map<String, Object> deviceItemMap) {
        if (!deviceItemMap.containsKey(USER_VEHICLE_AUTH_STATUS) && isSupportedParams(USER_VEHICLE_AUTH_STATUS)) {
            if (AssociationStatus.ASSOCIATED.getNotificationEventName()
                .equals(associationStatus.getNotificationEventName())) {
                deviceItemMap.put(USER_VEHICLE_AUTH_STATUS, "true");
            } else {
                deviceItemMap.put(USER_VEHICLE_AUTH_STATUS, "false");
            }
        }
    }

    /**
     * Checks if the given parameter name is supported.
     *
     * @param name the parameter name to check
     * @return true if the parameter name is supported, false otherwise
     */
    private boolean isSupportedParams(String name) {
        for (String param : supportedDeviceItems) {
            if (param.equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves the latest transaction state for a given association ID and IMSI.
     *
     * @param associationId The ID of the association.
     * @param imsi The IMSI (International Mobile Subscriber Identity) number.
     * @return The latest transaction state of the given association ID and IMSI, or null if no transaction exists.
     */
    private String getLatestSimTransactionState(long associationId, String imsi) {
        try {
            SimDetails simDetails = deviceAssociationDao.findLatestSimTransactionStatus(associationId);
            if (simDetails != null) {
                String currentTransactionStatus = simDetails.getTranStatus();
                if (SimTransactionStatus.IN_PROGRESS.getSimTransactionStatus().equals(currentTransactionStatus)
                    || SimTransactionStatus.PENDING.getSimTransactionStatus().equals(currentTransactionStatus)
                    || SimTransactionStatus.FAILED.getSimTransactionStatus().equals(currentTransactionStatus)) {
                    //Step-1 Call WAM api to find status of transactionId
                    String simTransactionId = simDetails.getTranId();
                    String region = deviceAssociationService.getRegion(associationId);
                    SimTransactionStatusDto simTransactionStatus =
                        simStateManager.pollTransactionStatus(region, imsi, simTransactionId);
                    String latestTransactionStatus = simTransactionStatus.getStatus();
                    //Step-2 Update sim_details table and return latest status of given transactionId
                    if (!currentTransactionStatus.equals(latestTransactionStatus)) {
                        deviceAssociationDao.updateTransactionStatus(latestTransactionStatus, simTransactionId,
                            associationId);
                    }
                    return latestTransactionStatus;
                } else {
                    return currentTransactionStatus;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            LOGGER.error("## Internal WAM error - Error message: {}", e.getMessage());
            return SimTransactionStatus.FAILED.getSimTransactionStatus();
        }
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
     * Terminates the association of a device.
     *
     * @param deviceStatusRequest The device status request containing the necessary information.
     * @return The number of devices terminated.
     * @throws ApiPreConditionFailedException If there is an error in the association data or if the SIM activation is
     *      pending or the SIM suspend condition failed.
     */
    public int terminateAssociation(DeviceStatusRequest deviceStatusRequest) {
        String deviceStatusRequestData =
                deviceStatusRequest.toString().replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "");
        LOGGER.info("## terminateAssociation Service - START deviceStatusRequest: {}", deviceStatusRequestData);
        List<AssociationStatus> statusList = new ArrayList<>();
        statusList.add(AssociationStatus.ASSOCIATED);
        statusList.add(AssociationStatus.ASSOCIATION_INITIATED);
        statusList.add(AssociationStatus.SUSPENDED);
        // Find existing association with user
        List<DeviceAssociation> deviceAssociationList = validateGetDeviceAssociation(deviceStatusRequest, statusList);
        if (deviceAssociationList.size() > 1) {
            LOGGER.error("## Association data - Database Integrity Error. There is more than one record.");
            throw new ApiPreConditionFailedException(ASSO_INTEGRITY_ERROR.getCode(), ASSO_INTEGRITY_ERROR.getMessage(),
                ASSO_INTEGRITY_ERROR.getGeneralMessage());
        }
        DeviceAssociation deviceAssociation = deviceAssociationList.get(0);
        // making sure that sim suspend is completed before by client (E.g.DM Portal).
        if (simSuspendCheck) {
            String activateTranStatus = deviceAssociationDao
                .getActivateTranStatus(deviceAssociation.getId()); //null ,if vin assoc not done
            //false if vin assoc not done or activation not done(may be) | true if vin assoc done
            if (activateTranStatus != null) {
                //user can perform terminate even without vin association
                if (!SimTransactionStatus.COMPLETED
                    .getSimTransactionStatus().equals(activateTranStatus)) {
                    throw new ApiPreConditionFailedException(SIM_ACTIVATION_PENDING.getCode(),
                        SIM_ACTIVATION_PENDING.getMessage(),
                        SIM_ACTIVATION_PENDING.getGeneralMessage());
                }
                String tranStatus = deviceAssociationDao.getTerminateTranStatus(deviceAssociation.getId());
                LOGGER.info("## TransactionId: {}, TransactionStatus: {}", deviceAssociation.getId(), tranStatus);
                if (!SimTransactionStatus.COMPLETED.getSimTransactionStatus().equals(tranStatus)) {
                    throw new ApiPreConditionFailedException(SIM_SUSPEND_CONDITION_FAILED.getCode(),
                        SIM_SUSPEND_CONDITION_FAILED.getMessage(),
                        SIM_SUSPEND_CONDITION_FAILED.getGeneralMessage());
                }
            }
        }
        deviceAssociation.setUserId(deviceStatusRequest.getUserId());
        UserProfile userProfile = ncClient.getUserProfile(deviceAssociation.getUserId(), ncBaseUrl);
        deviceAssociation.setTerminateFor(deviceStatusRequest.getRequiredFor());
        int updatedCount = performTerminate(deviceAssociation);
        if (userProfile != null && updatedCount > 0) {
            ncClient.callNotifCenterNonRegisteredUserApi(userProfile, ncBaseUrl, notificationId);
        } else {
            LOGGER.info(
                "Skipped sending Terminate notification, UserProfile {} for userId {} not found or terminate failed",
                userProfile,
                deviceStatusRequest.getUserId());
        }
        LOGGER.info("## Terminate Association Service - END No. of device terminated: {}", updatedCount);
        return updatedCount;
    }

    /**
     * Performs the termination of a device association.
     *
     * <p>This method deactivates the device, updates the association status to "DISASSOCIATED",
     * sets the disassociated by and modified by fields to the user ID, and records the
     * disassociation timestamp. It also updates the device association record in the database
     * and logs the updated count. If the device has a Harman ID, it is de-registered from
     * Spring Auth. Finally, it notifies the observers about the device de-activation.
     *
     * @param deviceAssociation The device association to be terminated.
     * @return The number of updated device association records.
     * @throws ApiTechnicalException If an error occurs while de-registering the device from Spring Auth.
     * @throws ApiNotificationException If an error occurs while notifying the observers.
     */
    private int performTerminate(DeviceAssociation deviceAssociation) {
        LOGGER.debug("## performTerminate - START");
        deviceAssociation.setDeviceAuthV2Deactivate(true);
        deviceAssociation.setAssociationStatus(AssociationStatus.DISASSOCIATED);
        deviceAssociation.setDisassociatedBy(deviceAssociation.getUserId());
        deviceAssociation.setModifiedBy(deviceAssociation.getUserId());
        deviceAssociation.setDisassociatedOn(new Timestamp(System.currentTimeMillis()));
        int updatedCount;
        updatedCount = deviceAssociationDao.updateForDisassociationById(deviceAssociation);
        LOGGER.debug("## Updated device association record Count: {}", updatedCount);
        LOGGER.info("## Successfully terminated: {} previous associations ", updatedCount);
        if (deviceAssociation.getHarmanId() != null || !StringUtils.isEmpty(deviceAssociation.getHarmanId())) {
            try {
                deRegisterFromSpringAuth(deviceAssociation.getHarmanId());
                LOGGER.info("Deleted (De-registered) device from Spring Auth successfully");
            } catch (SpringAuthClientException e) {
                Map<Object, Object> details = new LinkedHashMap<>();
                details.put(HARMAN_ID, deviceAssociation.getHarmanId());
                LOGGER.error("{}", ErrorUtils.buildError("## Error has occurred while de-registering "
                        + "device from Spring Auth",
                    e, details));
                throw new ApiTechnicalException(DE_REGISTER_WITH_SPRINGAUTH_FAILED.getCode(),
                    DE_REGISTER_WITH_SPRINGAUTH_FAILED.getMessage());
            }
        }
        LOGGER.info("De-register device from Spring Auth completed, now notifying the observers !!!");
        try {
            /*
             * We perform following steps in notify method
             * 1) Call Device-Auth - deactivate device api v2 to perform device de-activation
             * 2) Send message to Device using device message about de-activating device
             * 3) Send event to topic: "notification" for NC component, where NC will store userId and PDID into their
             *  state store
             */
            observable.notify(deviceAssociation);
        } catch (ObserverMessageProcessFailureException e) {
            revertSpringAuthChanges(deviceAssociation);
            throw new ApiNotificationException(ASSO_NOTIF_ERROR.getCode(), ASSO_NOTIF_ERROR.getMessage(),
                ASSO_NOTIF_ERROR.getGeneralMessage(), e);
        }
        LOGGER.debug("## performTerminate - END");
        return updatedCount;
    }

    /**
     * Reverts the Spring Auth changes for the given device association.
     *
     * @param deviceAssociation The device association to revert the changes for.
     */
    private void revertSpringAuthChanges(DeviceAssociation deviceAssociation) {
        try {
            Device device = deviceDao.findByDeviceId(deviceAssociation.getHarmanId());
            LOGGER.error("Registering back to Spring Auth on Terminate as notify have failed. deviceId :{}",
                device.getHarmanId());
            String authToken = springAuthTokenGenerator.fetchSpringAuthToken();
            LOGGER.info(UPDATE_REGISTERED_CLIENT_SPRING_AUTH, device.getHarmanId());
            springAuthRestClient.updateRegisteredClient(authToken, device.getHarmanId(), device.getPasscode(),
                deviceAssociation.getDeviceType(), APPROVED);
            LOGGER.info(UPDATE_REGISTERED_CLIENT_SPRING_AUTH_SUCCEEDED, device.getHarmanId());
        } catch (javax.naming.directory.InvalidAttributeValueException ex) {
            Map<Object, Object> details = new LinkedHashMap<>();
            details.put(HARMAN_ID, deviceAssociation.getHarmanId());
            LOGGER.error("{}", ErrorUtils.buildError("## Error has occurred while registering device with "
                    + "Spring Auth", ex, details));
            throw new ApiTechnicalException(REGISTER_WITH_SPRINGAUTH_FAILED.getCode(),
                REGISTER_WITH_SPRINGAUTH_FAILED.getMessage(), REGISTER_WITH_SPRINGAUTH_FAILED.getGeneralMessage());
        }
    }

    /**
     * Restores the association of a device based on the provided device status request.
     *
     * @param deviceStatusRequest The device status request containing the necessary information.
     * @return The number of associations restored.
     * @throws ApiPreConditionFailedException If there is a database integrity error or if the precondition fails.
     * @throws ApiTechnicalException If an error occurs while registering the device with Spring Auth.
     */
    public int restoreAssociation(DeviceStatusRequest deviceStatusRequest) {
        List<AssociationStatus> statusList = new ArrayList<>();
        statusList.add(AssociationStatus.SUSPENDED);
        List<DeviceAssociation> deviceAssociationList = validateGetDeviceAssociation(deviceStatusRequest, statusList);

        if (deviceAssociationList.size() > 1) {
            LOGGER.error("Association data - Database Integrity Error. There is more than one record.");
            throw new ApiPreConditionFailedException(DEVICE_ASSO_INTEGRITY_ERR.getCode(),
                DEVICE_ASSO_INTEGRITY_ERR.getMessage(), DEVICE_ASSO_INTEGRITY_ERR.getGeneralMessage());
        }
        DeviceAssociation deviceAssociation = deviceAssociationList.get(0);
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATED);
        deviceAssociation.setModifiedBy(deviceStatusRequest.getUserId());
        int updatedCount = deviceAssociationDao.updateDeviceAssociationStatusToRestore(deviceAssociation);
        Device device = deviceDao.findByDeviceId(deviceAssociation.getHarmanId());
        LOGGER.debug("registering device :{} ", device);
        if (null != device) {
            try {
                String authToken = springAuthTokenGenerator.fetchSpringAuthToken();
                LOGGER.info(UPDATE_REGISTERED_CLIENT_SPRING_AUTH, device.getHarmanId());
                springAuthRestClient.updateRegisteredClient(authToken, device.getHarmanId(), device.getPasscode(),
                    deviceAssociation.getDeviceType(), APPROVED);
                LOGGER.info(UPDATE_REGISTERED_CLIENT_SPRING_AUTH_SUCCEEDED, device.getHarmanId());
            } catch (javax.naming.directory.InvalidAttributeValueException e) {
                Map<Object, Object> details = new LinkedHashMap<>();
                details.put(HARMAN_ID, deviceAssociation.getHarmanId());
                LOGGER.error("{}", ErrorUtils.buildError("## Error has occurred while registering device"
                        + " with Spring Auth", e, details));
                throw new ApiTechnicalException(REGISTER_WITH_SPRINGAUTH_FAILED.getCode(),
                    REGISTER_WITH_SPRINGAUTH_FAILED.getMessage(), REGISTER_WITH_SPRINGAUTH_FAILED.getGeneralMessage());
            }
        }
        return updatedCount;
    }

    /**
     * Validates the device association based on the provided device status request and association status list.
     *
     * @param deviceStatusRequest The device status request object containing the device information.
     * @param statusList          The list of association statuses to filter the device associations.
     * @return The list of validated device associations.
     * @throws ApiValidationFailedException If the basic data (deviceId, imei, serialNumber) is missing in the request.
     * @throws ApiResourceNotFoundException If no device associations are found based on the provided criteria.
     */
    private List<DeviceAssociation> validateGetDeviceAssociation(DeviceStatusRequest deviceStatusRequest,
                                                                 List<AssociationStatus> statusList) {
        LOGGER.info("## validateGetDeviceAssociation - START deviceStatusRequest: {}, statusList: {}",
            deviceStatusRequest, statusList);
        String deviceId = deviceStatusRequest.getDeviceId();
        String imei = deviceStatusRequest.getImei();
        String serialNumber = deviceStatusRequest.getSerialNumber();
        Long associationId = deviceStatusRequest.getAssociationId();
        if (StringUtils.isEmpty(deviceId) && StringUtils.isEmpty(imei) && StringUtils.isEmpty(serialNumber)) {
            throw new ApiValidationFailedException(BASIC_DATA_MANDATORY.getCode(), BASIC_DATA_MANDATORY.getMessage(),
                BASIC_DATA_MANDATORY.getGeneralMessage());
        }
        String userId = deviceStatusRequest.getUserId();
        List<DeviceAssociation> deviceAssociationList = deviceAssociationDao
            .constructAndFetchDeviceAssociationData(serialNumber, deviceId, imei, userId, associationId, statusList);
        if (deviceAssociationList == null || deviceAssociationList.isEmpty()) {
            throw new ApiResourceNotFoundException(ASSO_DATA_NOT_FOUND.getCode(), ASSO_DATA_NOT_FOUND.getMessage(),
                ASSO_DATA_NOT_FOUND.getGeneralMessage());
        }
        return deviceAssociationList;
    }

    /**
     * Suspends a device by updating its association status to suspended.
     *
     * @param deviceStatusRequest The request object containing the device status information.
     * @return An instance of AssociateDeviceResponse representing the suspended device.
     * @throws Exception If an error occurs during the suspension process.
     */
    public AssociateDeviceResponse suspendDevice(DeviceStatusRequest deviceStatusRequest) {
        LOGGER.info("Inside suspendDevice method");
        List<AssociationStatus> statusList = new ArrayList<>();
        statusList.add(AssociationStatus.ASSOCIATED);
        List<DeviceAssociation> deviceAssociationList = validateGetDeviceAssociation(deviceStatusRequest, statusList);
        if (deviceAssociationList.size() > 1) {
            LOGGER.error("Association data - Database Integrity Error. There is more than one record.");
            throw new ApiPreConditionFailedException(DEVICE_ASSO_INTEGRITY_ERR.getCode(),
                DEVICE_ASSO_INTEGRITY_ERR.getMessage(), DEVICE_ASSO_INTEGRITY_ERR.getGeneralMessage());
        }
        DeviceAssociation deviceAssociation = deviceAssociationList.get(0);
        int updatedCount = deviceAssociationDao.updateDeviceAssociationStatusToSuspended(deviceAssociation);
        if (updatedCount <= 0) {
            throw new ApiTechnicalException(SUSPEND_FAILED.getCode(), SUSPEND_FAILED.getMessage(),
                SUSPEND_FAILED.getGeneralMessage());
        }
        try {
            deRegisterFromSpringAuth(deviceAssociation.getHarmanId());
        } catch (SpringAuthClientException e) {
            Map<Object, Object> details = new LinkedHashMap<>();
            details.put(HARMAN_ID, deviceAssociation.getHarmanId());
            LOGGER.error("{}", ErrorUtils.buildError("## Error has occurred while de-registering device"
                    + " from Spring Auth", e, details));
            throw new ApiTechnicalException(DE_REGISTER_WITH_SPRINGAUTH_FAILED.getCode(),
                DE_REGISTER_WITH_SPRINGAUTH_FAILED.getMessage(),
                DE_REGISTER_WITH_SPRINGAUTH_FAILED.getGeneralMessage());
        }
        return new AssociateDeviceResponse(deviceAssociation.getId(), AssociationStatus.SUSPENDED);
    }

    /**
     * Checks if the given ReplaceFactoryDataRequest is valid for replacement.
     *
     * @param replaceDeviceRequest The ReplaceFactoryDataRequest to be validated.
     * @return true if the ReplaceFactoryDataRequest is valid, false otherwise.
     * @throws InvalidAttributeValueException if the ReplaceFactoryDataRequest contains invalid attribute values.
     */
    private boolean isValidReplaceRequestData(ReplaceFactoryDataRequest replaceDeviceRequest) {
        if (replaceDeviceRequest.getCurrentValue() == null || replaceDeviceRequest.getReplaceWith() == null) {
            return false;
        }
        return !isInValidCurrentData(replaceDeviceRequest.getCurrentValue())
                && !isInValidReplaceWithData(replaceDeviceRequest.getReplaceWith());
    }

    /**
     * Checks if the given current device data is invalid.
     *
     * @param currentValueData The current device data to be checked.
     * @return true if the current device data is invalid, false otherwise.
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
     * Replaces a device with new factory data.
     *
     * @param replaceDeviceRequest The request object containing the replace device data.
     * @param userId The ID of the user performing the device replacement.
     * @throws Exception If an error occurs during the device replacement process.
     */
    public void replaceDevice(ReplaceFactoryDataRequest replaceDeviceRequest, String userId) {
        if (isValidReplaceRequestData(replaceDeviceRequest)) {
            List<DeviceInfoFactoryData> listCurrentValueData = deviceInfoFactoryDataDao
                .constructAndFetchFactoryData(convertToDeviceInfo(replaceDeviceRequest.getCurrentValue()));
            if (CollectionUtils.isEmpty(listCurrentValueData)) {
                throw new ApiValidationFailedException(INVALID_CURRENT_FACTORY_DATA_FOR_REPLACE.getCode(),
                    INVALID_CURRENT_FACTORY_DATA_FOR_REPLACE.getMessage(),
                    INVALID_CURRENT_FACTORY_DATA_FOR_REPLACE.getGeneralMessage());
            }
            DeviceInfoFactoryData currentDataFromDb = validateGetDeviceInfoFactoryData(listCurrentValueData, userId);
            validateCurrentDataFromDb(currentDataFromDb);

            DeviceInfoFactoryData replaceDataFromDb = validateListReplaceValueData(replaceDeviceRequest);

            if (!replaceDataFromDb.getState().equals(DeviceState.PROVISIONED.toString())) {
                throw new ApiPreConditionFailedException(INVALID_DEVICE_REPLACEMENT_REPLACE_DATA_STATE.getCode(),
                    INVALID_DEVICE_REPLACEMENT_REPLACE_DATA_STATE.getMessage(),
                    INVALID_DEVICE_REPLACEMENT_REPLACE_DATA_STATE.getGeneralMessage());
            }

            // Fetch the data from HCPInfo table for the current data factoryId
            HcpInfo hcpInfo = hcpInfoDao.findActiveHcpInfo(currentDataFromDb.getId());
            if (hcpInfo == null) {
                // No active device found. Exit replace device activity.
                throw new ApiPreConditionFailedException(INVALID_INACTIVATED_DEVICE_FOR_REPLACEMENT.getCode(),
                    INVALID_INACTIVATED_DEVICE_FOR_REPLACEMENT.getMessage(),
                    INVALID_INACTIVATED_DEVICE_FOR_REPLACEMENT.getGeneralMessage());
            }

            disableActivationStateForDevice(hcpInfo.getFactoryId(), userId);

            // Update the HCPInfo table with new details
            hcpInfo.setFactoryId(String.valueOf(replaceDataFromDb.getId()));
            hcpInfo.setSerialNumber(replaceDataFromDb.getSerialNumber());
            LOGGER.info("DeviceReplace: Updating device details in HCPInfo table");
            hcpInfoDao.updateForReplaceDevice(hcpInfo);

            // Update Device table with new details
            String harmanId = hcpInfo.getHarmanId();
            String passcode = getPassCode();
            LOGGER.info("DeviceReplace: New Passcode generated for the HarmanID : {}", harmanId);
            Device newDevice = new Device(harmanId, null, passcode, 0);
            deviceDao.updateForReplaceDevice(newDevice);

            deviceActivationStateDao.disableActivationReadyByFacotryId(replaceDataFromDb.getId());
            // Update Activation table with new details
            updateActivationTable(userId, replaceDataFromDb);

            DeviceAssociation deviceAssociation = deviceAssociationDao
                .findValidAssociations(currentDataFromDb.getSerialNumber());
            if (deviceAssociation == null) {
                throw new ApiPreConditionFailedException(ASSOCIATION_DATA_DOES_NOT_EXIST_FOR_GIVEN_INPUT.getCode(),
                    ASSOCIATION_DATA_DOES_NOT_EXIST_FOR_GIVEN_INPUT.getMessage(),
                    ASSOCIATION_DATA_DOES_NOT_EXIST_FOR_GIVEN_INPUT.getGeneralMessage());
            }
            deviceAssociationDao.updateForReplaceDevice(deviceAssociation.getId(), replaceDataFromDb.getSerialNumber(),
                userId, replaceDataFromDb.getId());

            // Update the factory data state to activated
            deviceInfoFactoryDataDao.changeDeviceState(replaceDataFromDb.getId(), DeviceState.ACTIVE.toString(),
                "New Device is activated for the old device with imei - " + currentDataFromDb.getImei());

            updateDeviceState(currentDataFromDb);

            performSpringAuthRegistration(harmanId, newDevice, deviceAssociation);

            if (isSendResetDeviceEnabled) {
                resetDevice(currentDataFromDb.getImei());
            }
        } else {
            throw new ApiValidationFailedException(INVALID_REPLACE_REQUEST_DATA.getCode(),
                INVALID_REPLACE_REQUEST_DATA.getMessage(), INVALID_REPLACE_REQUEST_DATA.getGeneralMessage());
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
        LOGGER.info("DeviceReplace: Updating activation table with the details : {}", deviceActivation);
        deviceActivationStateDao.insert(deviceActivation);
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
            LOGGER.error("Failed resetting device: imei: {}", imei);
        }
    }

    /**
     * Generates a passcode by combining a random alphanumeric string with the current system time.
     *
     * @return the generated passcode
     */
    private String getPassCode() {
        return RandomStringUtils.randomAlphanumeric(COUNT_64) + System.currentTimeMillis();
    }

    /**
     * Converts an object to a DeviceInfoFactoryData object.
     *
     * @param object the object to be converted
     * @return the converted DeviceInfoFactoryData object
     */
    private DeviceInfoFactoryData convertToDeviceInfo(Object object) {
        DeviceInfoFactoryData deviceInfoData = new DeviceInfoFactoryData();
        BeanUtils.copyProperties(object, deviceInfoData);
        return deviceInfoData;
    }

    /**
     * Performs a state change operation based on the provided state change request.
     *
     * @param stateChangeRequest The state change request containing the necessary information for the operation.
     * @throws Exception If an error occurs during the state change operation.
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
                    throw new ApiTechnicalException(USER_NOT_WHITELISTED.getCode(), USER_NOT_WHITELISTED.getMessage(),
                        USER_NOT_WHITELISTED.getGeneralMessage());
                } else {
                    LOGGER.debug("White listed user : {}. Continuing with state change process.",
                        stateChangeRequest.getUserId());
                }
            } else {
                LOGGER.debug("No white listed users found");
                throw new ApiTechnicalException(USER_NOT_WHITELISTED.getCode(), USER_NOT_WHITELISTED.getMessage(),
                    USER_NOT_WHITELISTED.getGeneralMessage());
            }
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set(MessageConstants.USER_ID, stateChangeRequest.getUserId());
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
            LOGGER.info("Exiting device info query service with response: {}", response.getStatusCode());
        } catch (HttpClientErrorException exp) {
            String response = exp.getResponseBodyAsString();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(response);
            JsonNode message = jsonNode.path("error").path("message");
            throw new NoSuchEntityException(message.asText(), exp);
        } catch (Exception ex) {
            throw new NoSuchEntityException(ex);
        }
    }

    /**
     * Validates the input data for a state change request.
     *
     * @param stateChangeRequest The state change request to validate.
     * @throws ApiValidationFailedException If the state change request is invalid or missing mandatory fields.
     */
    private void validateInputData(StateChangeRequest stateChangeRequest) {
        if (StringUtils.isEmpty(stateChangeRequest.getState())) {
            throw new ApiValidationFailedException(STATE_MANDATORY.getCode(), STATE_MANDATORY.getMessage());
        }
        if (StringUtils.isEmpty(stateChangeRequest.getImei())
            && StringUtils.isEmpty(stateChangeRequest.getDeviceId())) {
            throw new ApiValidationFailedException(BASIC_MANDATORY_STATE_CHANGE.getCode(),
                BASIC_MANDATORY_STATE_CHANGE.getMessage());
        }
    }

    /**
     * Fetches the factory ID based on the given state change request.
     *
     * @param stateChangeRequest The state change request containing the IMEI or device ID.
     * @return The factory ID associated with the state change request.
     * @throws ApiResourceNotFoundException If no factory data is found for the given IMEI or device ID.
     */
    private Long fetchFactoryId(StateChangeRequest stateChangeRequest) {
        Long data = Long.valueOf(0);
        if (StringUtils.isNotEmpty(stateChangeRequest.getImei())) {
            DeviceInfoFactoryData factoryData = deviceInfoFactoryDataDao
                .findByFactoryImei(stateChangeRequest.getImei());
            data = factoryData.getId();
            // check if factory data is available from db or not.
            if (data.longValue() == 0) {
                throw new ApiResourceNotFoundException(FACTORY_DATA_NO_DATA_FOUND.getCode(),
                    FACTORY_DATA_NO_DATA_FOUND.getMessage());
            }
        } else if (StringUtils.isNotEmpty(stateChangeRequest.getDeviceId())) {
            HcpInfo hcpData = hcpInfoDao.findByDeviceId(stateChangeRequest.getDeviceId());
            if (StringUtils.isEmpty(hcpData.getFactoryId())) {
                throw new ApiResourceNotFoundException(FACTORY_DATA_NO_DATA_FOUND.getCode(),
                    FACTORY_DATA_NO_DATA_FOUND.getMessage());
            }
            data = Long.valueOf(hcpData.getFactoryId());
        }
        return data;
    }

    /**
     * De-registers the specified device from Spring Auth.
     *
     * @param deviceId the ID of the device to be de-registered
     * @throws SpringAuthClientException if an error occurs while de-registering the device from Spring Auth
     */
    private void deRegisterFromSpringAuth(String deviceId) {
        try {
            String authToken = springAuthTokenGenerator.fetchSpringAuthToken();
            LOGGER.info("Deleting registered client from Spring Auth, deviceId: {}", deviceId);
            springAuthRestClient.deleteRegisteredClient(authToken, deviceId);
            LOGGER.info("Deleted registered client from Spring Auth successfully, deviceId: {}", deviceId);
        } catch (Exception e) {
            throw new SpringAuthClientException("Error while deRegisterFromSpringAuth " + e.getMessage(), e);
        }
    }

    /**
     * Validates and retrieves the DeviceInfoFactoryData based on the provided list of data values and user ID.
     *
     * @param listDataValues The list of DeviceInfoFactoryData values to validate.
     * @param userId The user ID to check association with the DeviceInfoFactoryData.
     * @return The validated DeviceInfoFactoryData object.
     * @throws InvalidUserAssociation if no associated device is found with the provided user ID.
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
     * @param imei    the IMEI of the device
     * @param orderby the field to order the results by (default: "desc")
     * @param sortby  the field to sort the results by (default: "user_id")
     * @param page    the page number of the results
     * @param size    the number of results per page
     * @return a list of DeviceAssociationHistory objects representing the association history
     * @throws ApiResourceNotFoundException if the association history is not found
     */
    public List<DeviceAssociationHistory> getAssociationHistory(String imei, String orderby, String sortby, int page,
                                                                int size) {

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

        Long factoryId;
        try {
            factoryId = getDeviceInfoFactoryDataByImei(imei);
        } catch (IncorrectResultSizeDataAccessException ex) {
            throw new ApiResourceNotFoundException(ApiMessageEnum.ASSOCIATION_HISTORY_NOT_FOUND.getCode(),
                ApiMessageEnum.ASSOCIATION_HISTORY_NOT_FOUND.getMessage());
        }
        return deviceAssociationDao.getAssociationDetails(factoryId, orderby, sortby, page, size);
    }

    /**
     * Retrieves the total count of association history for a given IMEI.
     *
     * @param imei The IMEI of the device.
     * @return The total count of association history.
     * @throws NoSuchEntityException If no data is found for the given IMEI.
     */
    public int getAssociationHistoryTotalCount(String imei) throws NoSuchEntityException {
        Long factoryId = null;
        try {
            factoryId = getDeviceInfoFactoryDataByImei(imei);
        } catch (IncorrectResultSizeDataAccessException ex) {
            throw new NoSuchEntityException(MessageConstants.NO_DATA_FOUND);
        }
        return deviceAssociationDao.findAssociationCountForFactoryId(factoryId);
    }

    /**
     * Retrieves the factory data ID associated with the given IMEI.
     *
     * @param imei The IMEI of the device.
     * @return The factory data ID associated with the given IMEI, or null if not found.
     */
    public Long getDeviceInfoFactoryDataByImei(String imei) {
        return deviceInfoFactoryDataDao.findIdByFactoryImei(imei);
    }

    /**
     * Delegates the association of a device to another user.
     *
     * @param delegateAssociationRequest The request object containing the necessary information for delegation.
     * @param isAdmin                    A boolean value indicating whether the user is an admin or not.
     * @return The response object containing the result of the delegation.
     * @throws ApiValidationFailedException If the validation of the request fails.
     */
    public AssociateDeviceResponse delegateAssociation(DelegateAssociationRequest delegateAssociationRequest,
                                                       boolean isAdmin) {
        String delegateAssociationRequestData =
                delegateAssociationRequest.toString().replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "");
        LOGGER.info("delegateAssociation service start: {}", delegateAssociationRequestData);
        delegationPreConditionCheck(delegateAssociationRequest);
        // validate Requester is Owner
        if (deviceAssociationDao
            .validUserAssociation(getconditionalMap(isAdmin, delegateAssociationRequest, defaultAssociationType))) {

            DeviceAssociation deviceAssociation = deviceAssociationDao
                .findAssociation(getconditionalMap(isAdmin, delegateAssociationRequest, defaultAssociationType));
            deviceAssociation.setAssociatedBy(delegateAssociationRequest.getUserId());
            deviceAssociation.setAssociatedOn(new Timestamp(System.currentTimeMillis()));
            deviceAssociation.setModifiedOn(new Timestamp(System.currentTimeMillis()));
            deviceAssociation.setModifiedBy(delegateAssociationRequest.getUserId());
            deviceAssociation.setUserId(delegateAssociationRequest.getDelegationUserId());
            deviceAssociation.setStartTimeStamp(delegateAssociationRequest.getStartTimestamp());
            deviceAssociation.setEndTimeStamp(delegateAssociationRequest.getEndTimestamp());
            deviceAssociation.setAssociationType(delegateAssociationRequest.getAssociationType());
            deviceAssociationDao.insertM2M(deviceAssociation);
            LOGGER.info("delegateAssociation service end: {}", delegateAssociationRequestData);
            return new AssociateDeviceResponse(deviceAssociation.getId(), AssociationStatus.ASSOCIATED);

        } else {
            if (isAdmin) {
                throw new ApiValidationFailedException(OWNER_ASSO_NOT_FOUND.getCode(),
                    OWNER_ASSO_NOT_FOUND.getMessage(),
                    OWNER_ASSO_NOT_FOUND.getGeneralMessage());
            }
            throw new ApiValidationFailedException(OWNER_VALIDATION_FAILED.getCode(),
                OWNER_VALIDATION_FAILED.getMessage(), OWNER_VALIDATION_FAILED.getGeneralMessage());
        }
    }

    /**
     * Delegates the association process by an admin.
     *
     * @param delegateAssociationRequest The request object containing the association details.
     * @return The response object containing the result of the association process.
     */
    public AssociateDeviceResponse delegateAssociationByAdmin(DelegateAssociationRequest delegateAssociationRequest) {

        //route it to either delegation or self association based on Association Type
        if (delegateAssociationRequest.getAssociationType().equals(defaultAssociationType)
            && deviceAssociationTypeService.isAssocTypeExist(delegateAssociationRequest.getAssociationType())) {
            AssociateDeviceRequest associateDeviceRequest = delegateAssociationRequest.toAssociationRequest();
            LOGGER.info("Invoke self association API associateDeviceRequest:{}", associateDeviceRequest);
            return associateDeviceForSelf(associateDeviceRequest, delegateAssociationRequest.getUserId());
        } else {
            return delegateAssociation(delegateAssociationRequest, true);
        }
    }

    /**
     * Performs pre-condition checks for delegation association.
     *
     * @param delegateAssociationRequest The delegate association request object.
     * @throws ApiValidationFailedException If the pre-condition checks fail.
     */
    private void delegationPreConditionCheck(DelegateAssociationRequest delegateAssociationRequest) {

        // Check if the association type is valid
        if ("defaultOwner".equals(defaultAssociationType)
            || delegateAssociationRequest.getAssociationType().equals(defaultAssociationType)
            || !deviceAssociationTypeService.isAssocTypeExist(delegateAssociationRequest.getAssociationType())) {
            throw new ApiValidationFailedException(DELIGATION_ASSOCIATION_TYPE_VALIDATION_FAILED.getCode(),
                DELIGATION_ASSOCIATION_TYPE_VALIDATION_FAILED.getMessage(),
                DELIGATION_ASSOCIATION_TYPE_VALIDATION_FAILED.getGeneralMessage());
        }

        // Validate user details from user management

        if (EXTERNAL.equals(userIdType)) {
            // make sure that userId is provided compulsorily
            if (StringUtils.isBlank(delegateAssociationRequest.getDelegationUserId())) {
                throw new ApiValidationFailedException(INVALID_USER_DETAILS.getCode(),
                    INVALID_USER_DETAILS.getMessage(), INVALID_USER_DETAILS.getGeneralMessage());
            }
        } else if (INTERNAL.equals(userIdType)) {
            String delegateUserName = userManagerService.getUserDetail(delegateAssociationRequest.getDelegationUserId(),
                delegateAssociationRequest.getEmail(), USER_NAME);
            LOGGER.debug("## UserName for which association is being deligated to {}", delegateUserName);
            if ((StringUtils.isEmpty(delegateAssociationRequest.getDelegationUserId())
                && StringUtils.isEmpty(delegateAssociationRequest.getEmail()))
                || StringUtils.isEmpty(delegateUserName)) {
                throw new ApiValidationFailedException(INVALID_USER_DETAILS.getCode(),
                    INVALID_USER_DETAILS.getMessage(), INVALID_USER_DETAILS.getGeneralMessage());
            }
            delegateAssociationRequest.setDelegationUserId(delegateUserName);
        } else {
            throw new ApiValidationFailedException(USER_ID_TYPE_INVALID.getCode(), USER_ID_TYPE_INVALID.getMessage(),
                USER_ID_TYPE_INVALID.getGeneralMessage());
        }
        validateStartEndTime(delegateAssociationRequest);
    }

    /**
     * Validates the start and end time of the delegate association request.
     * If the start time is not provided, it sets the current system time as the start time.
     * If the end time is provided and the start time is greater than or equal to the end time,
     * it throws an ApiValidationFailedException.
     *
     * @param delegateAssociationRequest the delegate association request to validate
     * @throws ApiValidationFailedException if the start and end time validation fails
     */
    private void validateStartEndTime(DelegateAssociationRequest delegateAssociationRequest) {

        long endTime = delegateAssociationRequest.getEndTimestamp();
        long startTime = delegateAssociationRequest.getStartTimestamp();

        if (startTime == 0) {
            startTime = System.currentTimeMillis();
            delegateAssociationRequest.setStartTimestamp(startTime);
        }

        if (endTime != 0 && startTime >= endTime) {
            throw new ApiValidationFailedException(START_END_TIME_VALIDATION_FAILED.getCode(),
                START_END_TIME_VALIDATION_FAILED.getMessage(),
                START_END_TIME_VALIDATION_FAILED.getGeneralMessage());
        }
    }

    /**
     * Validates the start and end time.
     *
     * @param endTime   the end time to validate
     * @param startTime the start time to validate
     * @throws ApiValidationFailedException if the start time is greater than or equal to the end time
     */
    private void validateStartEndTime(long endTime, long startTime) {
        if (startTime >= endTime) {
            throw new ApiValidationFailedException(START_END_TIME_VALIDATION_FAILED.getCode(),
                START_END_TIME_VALIDATION_FAILED.getMessage(),
                START_END_TIME_VALIDATION_FAILED.getGeneralMessage());
        }

    }

    /**
     * Returns a map containing the query conditions based on the provided parameters.
     *
     * @param isAdmin                   a boolean indicating whether the user is an admin
     * @param delegateAssociationRequest the delegate association request object
     * @param associationType           the association type
     * @return a map containing the query conditions
     */
    private Map<String, Object> getconditionalMap(boolean isAdmin,
                                                  DelegateAssociationRequest delegateAssociationRequest,
                                                  String associationType) {

        Map<String, Object> queryConditionMap = new LinkedHashMap<>();

        if (!isAdmin && StringUtils.isNotEmpty(delegateAssociationRequest.getUserId())) {
            queryConditionMap.put(DeviceAttributeEnums.USER_ID.getString(), delegateAssociationRequest.getUserId());
        }
        if (StringUtils.isNotEmpty(delegateAssociationRequest.getImei())) {
            queryConditionMap.put(DeviceAttributeEnums.IMEI.getString(), delegateAssociationRequest.getImei());
        }
        if (StringUtils.isNotEmpty(delegateAssociationRequest.getSerialNumber())) {
            queryConditionMap.put(DeviceAttributeEnums.SERIAL_NUMBER.getString(),
                delegateAssociationRequest.getSerialNumber());
        }
        if (StringUtils.isNotEmpty(delegateAssociationRequest.getSsid())) {
            queryConditionMap.put(DeviceAttributeEnums.SSID.getString(), delegateAssociationRequest.getSsid());
        }
        if (StringUtils.isNotEmpty(delegateAssociationRequest.getIccid())) {
            queryConditionMap.put(DeviceAttributeEnums.ICCID.getString(), delegateAssociationRequest.getIccid());
        }
        if (StringUtils.isNotEmpty(delegateAssociationRequest.getMsisdn())) {
            queryConditionMap.put(DeviceAttributeEnums.MSISDN.getString(), delegateAssociationRequest.getMsisdn());
        }
        if (StringUtils.isNotEmpty(delegateAssociationRequest.getImsi())) {
            queryConditionMap.put(DeviceAttributeEnums.IMSI.getString(), delegateAssociationRequest.getImsi());
        }
        if (StringUtils.isNotEmpty(delegateAssociationRequest.getBssid())) {
            queryConditionMap.put(DeviceAttributeEnums.BSSID.getString(), delegateAssociationRequest.getBssid());
        }
        if (StringUtils.isNotEmpty(associationType)) {
            queryConditionMap.put(DeviceAttributeEnums.ASSOCIATION_TYPE.getString(), associationType);
        }

        return queryConditionMap;
    }

    /**
     * Associates a device for self with the provided device request and admin user ID.
     *
     * @param associateDeviceRequest The device request containing the device information.
     * @param adminUserId            The ID of the admin user.
     * @return The response containing the ID of the associated device and the association status.
     * @throws ApiValidationFailedException If the basic data (BSSID, IMEI, and serial number) is empty.
     */
    public AssociateDeviceResponse associateDeviceForSelf(AssociateDeviceRequest associateDeviceRequest,
                                                          String adminUserId) {
        String associateDeviceRequestData =
                associateDeviceRequest.toString().replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "");
        LOGGER.debug("## associateDeviceForSelf - START associateDeviceRequest: {}", associateDeviceRequestData);
        if (StringUtils.isEmpty(associateDeviceRequest.getBssid())
            && StringUtils.isEmpty(associateDeviceRequest.getImei())
            && StringUtils.isEmpty(associateDeviceRequest.getSerialNumber())) {
            throw new ApiValidationFailedException(ApiMessageEnum.BASIC_DATA_MANDATORY.getCode(),
                ApiMessageEnum.BASIC_DATA_MANDATORY.getMessage(),
                ApiMessageEnum.BASIC_DATA_MANDATORY.getGeneralMessage());
        }
        DeviceAssociation deviceAssociation = associateForSelf(associateDeviceRequest, adminUserId);
        LOGGER.debug("## associateDevice - END deviceAssociation: {}", deviceAssociation);
        return new AssociateDeviceResponse(deviceAssociation.getId(), AssociationStatus.ASSOCIATION_INITIATED);
    }

    private DeviceAssociation associateForSelf(AssociateDeviceRequest associateDeviceRequest, String adminUserId) {
        LOGGER.debug("## associate Service - START imei: {}, serialNumber: {}", associateDeviceRequest.getImei(),
            associateDeviceRequest.getSerialNumber());
        DeviceAssociation deviceAssociation = new DeviceAssociation();
        List<FactoryData> fetchFactoryData = deviceAssociationDao.constructAndFetchFactoryData(associateDeviceRequest);
        String fetchState;
        deviceAssociation.setUserId(associateDeviceRequest.getUserId());
        if (!fetchFactoryData.isEmpty() && fetchFactoryData.get(0) != null && fetchFactoryData.get(0).getId() != 0) {
            LOGGER.debug("## Factory data retrieved successfully, ID (PK): {}", fetchFactoryData.get(0).getId());
            deviceAssociation.setFactoryId(fetchFactoryData.get(0).getId());
            deviceAssociation.setSerialNumber(fetchFactoryData.get(0).getSerialNumber());
            fetchState = fetchFactoryData.get(0).getState();
            if (fetchFactoryData.get(0).isFaulty() || fetchFactoryData.get(0).isStolen()) {
                fetchState = STOLEN_OR_FAULTY;
            }
        } else {
            throw new ApiValidationFailedException(ApiMessageEnum.FETCHING_FACTORY_DATA_ERROR.getCode(),
                ApiMessageEnum.FETCHING_FACTORY_DATA_ERROR.getMessage(),
                ApiMessageEnum.FETCHING_FACTORY_DATA_ERROR.getGeneralMessage());
        }
        LOGGER.debug("## forbidAssocAfterTerminate flag: {} ", forbidAssocAfterTerminate);

        if (forbidAssocAfterTerminate && (!"".equals(fetchState)
            && (PROVISIONED.equals(fetchState) || PROVISIONED_ALIVE.equals(fetchState)))) {
            boolean isTerminated = deviceAssociationDao.isDeviceTerminated(fetchFactoryData.get(0).getId());
            if (isTerminated) {
                LOGGER.debug("The device with serial number: {} got terminated, hence can not be associated again",
                    fetchFactoryData.get(0).getSerialNumber());
                throw new ApiPreConditionFailedException(ApiMessageEnum.DEVICE_TERMINATED_MSG.getCode(),
                    ApiMessageEnum.DEVICE_TERMINATED_MSG.getMessage(),
                    ApiMessageEnum.DEVICE_TERMINATED_MSG.getGeneralMessage());
            }
        }

        LOGGER.info("## Device STATE: {} before proceed with device association", fetchState);
        switch (fetchState) {
            case STOLEN_OR_FAULTY:
                throw new ApiValidationFailedException(ApiMessageEnum.STOLEN_OR_FAULTY_MSG.getCode(),
                    ApiMessageEnum.STOLEN_OR_FAULTY_MSG.getMessage(),
                    ApiMessageEnum.STOLEN_OR_FAULTY_MSG.getGeneralMessage());
            case PROVISIONED, PROVISIONED_ALIVE:
                saveDeviceAssociationForSelf(deviceAssociation, adminUserId);
                LOGGER.info(
                    "## User association completed successfully with table updates, factoryId: {}, SerialNumber: {}",
                    deviceAssociation.getFactoryId(), deviceAssociation.getSerialNumber());
                break;
            default:
                String userId = getUserId(associateDeviceRequest.getImei(), associateDeviceRequest.getSerialNumber());
                LOGGER.error("## Invalid state passed for association. fetchState: {}, userId: {}", fetchState, userId);
                throw new ApiValidationFailedException(ApiMessageEnum.INVALID_FACTORY_STATE.getCode(),
                    ApiMessageEnum.INVALID_FACTORY_STATE.getMessage(),
                    ApiMessageEnum.INVALID_FACTORY_STATE.getGeneralMessage());
        }
        LOGGER.debug("## associate Service - END");
        return deviceAssociation;
    }

    /**
     * Saves the device association for self.
     *
     * @param deviceAssociation The device association object to be saved.
     * @param adminUserId The admin user ID performing the action, or null if self is performing.
     * @throws ApiValidationFailedException If the default association type is invalid.
     */
    private void saveDeviceAssociationForSelf(DeviceAssociation deviceAssociation, String adminUserId) {
        LOGGER.debug("## saveDeviceAssociation - START ");
        if (!"defaultOwner".equals(defaultAssociationType)) {
            if (!deviceAssociationTypeService.isAssocTypeExist(defaultAssociationType)) {
                throw new ApiValidationFailedException(ApiMessageEnum.ASSOC_TYPE_VALIDATION_FAILURE.getCode(),
                    ApiMessageEnum.ASSOC_TYPE_VALIDATION_FAILURE.getMessage(),
                    ApiMessageEnum.ASSOC_TYPE_VALIDATION_FAILURE.getGeneralMessage());
            }
            deviceAssociation.setAssociationType(defaultAssociationType);
        }
        deviceAssociation.setStartTimeStamp(System.currentTimeMillis());
        deviceAssociation.setEndTimeStamp(0L);
        deviceAssociation.setAssociationStatus(AssociationStatus.ASSOCIATION_INITIATED);
        deviceAssociation.setModifiedOn(new Timestamp(System.currentTimeMillis()));
        //Check if admin is performing or self is performing
        if (StringUtils.isBlank(adminUserId)) {
            deviceAssociation.setModifiedBy(deviceAssociation.getUserId());
            deviceAssociation.setAssociatedBy(deviceAssociation.getUserId());
        } else {
            deviceAssociation.setModifiedBy(adminUserId);
            deviceAssociation.setAssociatedBy(adminUserId);
        }
        deviceAssociation.setAssociatedOn(new Timestamp(System.currentTimeMillis()));

        deviceAssociation.setDisassociatedBy(null);
        deviceAssociation.setDisassociatedOn(null);
        deviceAssociationDao.insertM2M(deviceAssociation);
        deviceAssociationDao.insertDeviceState(deviceAssociation);
        deviceInfoFactoryDataDao.changeDeviceState(deviceAssociation.getFactoryId(),
            DeviceState.READY_TO_ACTIVATE.toString(),
            "Association initiated with user ");
        LOGGER.debug("## saveDeviceAssociation - END  device_association table factory_data (FK): {}",
            deviceAssociation.getFactoryId());
    }

    /**
     * Terminates the M2M association for a device.
     *
     * @param deviceStatusRequest The device status request object.
     * @param userIdFromHeader The user ID from the header.
     * @param adminUserId The admin user ID.
     * @param isAdmin Flag indicating if the user is an admin.
     * @return The number of updated associations.
     * @throws ApiPreConditionFailedException If there is a database integrity error.
     */
    public int terminateM2Massociation(DeviceStatusRequest deviceStatusRequest, String userIdFromHeader,
                                       String adminUserId, boolean isAdmin) {
        String deviceStatusRequestData =
                deviceStatusRequest.toString().replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "");
        LOGGER.info("## terminateM2MAssociation Service - START deviceStatusRequest: {}", deviceStatusRequestData);
        List<AssociationStatus> statusList = Arrays.asList(ASSOCIATED, ASSOCIATION_INITIATED, SUSPENDED);
        // Find existing association with user
        List<DeviceAssociation> deviceAssociationList = validateGetDeviceAssociation(deviceStatusRequest, statusList);
        if (deviceAssociationList.size() > 1) {
            LOGGER.error("## Association data - Database Integrity Error. There is more than one record.");
            throw new ApiPreConditionFailedException(ASSO_INTEGRITY_ERROR.getCode(), ASSO_INTEGRITY_ERROR.getMessage(),
                ASSO_INTEGRITY_ERROR.getGeneralMessage());
        }
        DeviceAssociation deviceAssociation = deviceAssociationList.get(0);
        deviceAssociation.setUserId(deviceStatusRequest.getUserId());
        String associationType =
            StringUtils.isEmpty(deviceAssociation.getAssociationType()) ? null : deviceAssociation.getAssociationType();

        int updatedCount;
        if (StringUtils.isEmpty(associationType) || associationType.equals(defaultAssociationType)) {
            // making sure that sim suspend is completed before by client (E.g.DM Portal).
            performTerminationWithoutVinAssociation(deviceAssociation);
            UserProfile userProfile = ncClient.getUserProfile(deviceAssociation.getUserId(), ncBaseUrl);
            deviceAssociation.setTerminateFor(deviceStatusRequest.getRequiredFor());
            updatedCount = performM2Mterminate(deviceAssociation, userIdFromHeader, adminUserId, isAdmin);
            if (userProfile != null && updatedCount > 0) {
                ncClient.callNotifCenterNonRegisteredUserApi(userProfile, ncBaseUrl, notificationId);
            } else {
                LOGGER.info(
                    "Skipped sending Terminate notification, UserProfile {} for userId {} not found or terminate"
                        + " failed", userProfile, deviceStatusRequest.getUserId());
            }
            LOGGER.info("## Terminate M2MAssociation Service END");
            return updatedCount;
        } else {
            LOGGER.info("## Terminate M2MAssociation Service END");
            return performDisassociation(deviceAssociation, userIdFromHeader, adminUserId, isAdmin);
        }
    }

    /**
     * Performs the disassociation of a device association.
     *
     * @param deviceAssociation The device association to be disassociated.
     * @param userIdFromHeader  The user ID from the header.
     * @param adminUserId       The admin user ID.
     * @param isAdmin           Flag indicating if the user is an admin.
     * @return The number of previous associations successfully disassociated.
     */
    private int performDisassociation(DeviceAssociation deviceAssociation, String userIdFromHeader, String adminUserId,
                                      boolean isAdmin) {
        LOGGER.info("## performDisassociation - START");
        deviceAssociation.setAssociationStatus(DISASSOCIATED);
        deviceAssociation.setDisassociatedBy(isAdmin ? adminUserId : userIdFromHeader);
        deviceAssociation.setModifiedBy(isAdmin ? adminUserId : userIdFromHeader);
        deviceAssociation.setDisassociatedOn(new Timestamp(System.currentTimeMillis()));
        deviceAssociation.setEndTimeStamp(new Timestamp(System.currentTimeMillis()).getTime());
        int updatedCount = deviceAssociationDao.updateForM2MdisassociationById(deviceAssociation);
        LOGGER.info("## performDisassociation - END | Successfully disassociated: {} previous associations ",
            updatedCount);
        return updatedCount;
    }

    /**
     * Performs M2M termination for the given device association.
     *
     * @param deviceAssociation The device association to perform termination on.
     * @param userIdFromHeader The user ID from the header.
     * @param adminUserId The admin user ID.
     * @param isAdmin Flag indicating if the user is an admin.
     * @return The number of updated device association records.
     * @throws ApiTechnicalException If an error occurs while de-registering the device from Spring Auth.
     * @throws ApiNotificationException If an error occurs during the notification process.
     */
    private int performM2Mterminate(DeviceAssociation deviceAssociation, String userIdFromHeader, String adminUserId,
                                    boolean isAdmin) {
        LOGGER.debug("## performM2MTerminate - START");
        deviceAssociation.setDeviceAuthV2Deactivate(true);
        deviceAssociation.setAssociationStatus(DISASSOCIATED);
        deviceAssociation.setDisassociatedBy(isAdmin ? adminUserId : userIdFromHeader);
        deviceAssociation.setModifiedBy(isAdmin ? adminUserId : userIdFromHeader);
        deviceAssociation.setDisassociatedOn(new Timestamp(System.currentTimeMillis()));
        deviceAssociation.setEndTimeStamp(new Timestamp(System.currentTimeMillis()).getTime());
        int updatedCount;
        boolean isAssociated = deviceAssociation.getAssociationStatus().equals(AssociationStatus.ASSOCIATION_INITIATED);
        updatedCount = isAssociated ? deviceAssociationDao.updateForM2MdisassociationById(deviceAssociation) :
            deviceAssociationDao.updateForDisassociationByDeviceId(deviceAssociation);
        LOGGER.debug("## Updated device association record Count: {}", updatedCount);
        LOGGER.info("## Successfully terminated: {} previous associations ", updatedCount);
        if (!StringUtils.isEmpty(deviceAssociation.getHarmanId())) {
            try {
                deRegisterFromSpringAuth(deviceAssociation.getHarmanId());
                LOGGER.info("Deleted (De-registered) device from Spring Auth successfully");
            } catch (SpringAuthClientException e) {
                Map<Object, Object> details = new LinkedHashMap<>();
                details.put(HARMAN_ID, deviceAssociation.getHarmanId());
                LOGGER.error("{}", ErrorUtils.buildError("## Error has occurred while de-registering"
                        + " device from Spring Auth", e, details));
                throw new ApiTechnicalException(DE_REGISTER_WITH_SPRINGAUTH_FAILED.getCode(),
                    DE_REGISTER_WITH_SPRINGAUTH_FAILED.getMessage());
            }
        }
        LOGGER.info("De-register device from Spring Auth completed, now notifying the observers !!!");
        try {
            /*
             * We perform following steps in notify method
             * 1) Call Device-Auth - deactivate device api v2 to perform device de-activation
             * 2) Send message to Device using device message about de-activating device
             * 3) Send event to topic: "notification" for NC component, where NC will store userId and PDID into their
             *  state store
             */
            observable.notify(deviceAssociation);
        } catch (ObserverMessageProcessFailureException e) {
            throw new ApiNotificationException(ASSO_NOTIF_ERROR.getCode(), ASSO_NOTIF_ERROR.getMessage(),
                ASSO_NOTIF_ERROR.getGeneralMessage(), e);
        }
        LOGGER.debug("## performM2MTerminate - END");
        return updatedCount;
    }

    /**
     * Validates and performs termination or disassociation of a device association for a given user.
     *
     * @param userId              The ID of the user performing the operation.
     * @param deviceStatusRequest The request object containing device status information.
     * @param isAdmin             A flag indicating whether the user is an admin.
     * @return The M2Mterminate object indicating whether the termination or disassociation should be performed.
     * @throws ApiValidationFailedException     If the basic data (deviceId, imei, serialNumber) is missing.
     * @throws ApiValidationFailedException     If a non-admin user is trying to disassociate a delegated user.
     * @throws ApiValidationFailedException     If the owner termination validation fails.
     * @throws ApiPreConditionFailedException   If there are multiple device associations for the given criteria.
     */
    public M2Mterminate validatePerformTerminate(String userId, DeviceStatusRequest deviceStatusRequest,
                                                 boolean isAdmin) {

        List<AssociationStatus> statusList = Arrays.asList(ASSOCIATED, ASSOCIATION_INITIATED, SUSPENDED);

        String serialNumber = deviceStatusRequest.getSerialNumber();
        String imei = deviceStatusRequest.getImei();
        String deviceId = deviceStatusRequest.getDeviceId();

        if (StringUtils.isEmpty(deviceId) && StringUtils.isEmpty(imei) && StringUtils.isEmpty(serialNumber)) {
            throw new ApiValidationFailedException(BASIC_DATA_MANDATORY.getCode(), BASIC_DATA_MANDATORY.getMessage(),
                BASIC_DATA_MANDATORY.getGeneralMessage());
        }

        //Validate if correct owner is trying to disassociate delegated user
        if (!isAdmin && StringUtils.isNotEmpty(deviceStatusRequest.getUserId())
            && !userId.equalsIgnoreCase(deviceStatusRequest.getUserId())) {
            List<DeviceAssociation> deviceAssociations =
                deviceAssociationDao.constructAndFetchDeviceAssociationData(serialNumber, deviceId, imei, userId, null,
                    statusList);

            boolean isOwner = deviceAssociations.stream().anyMatch(
                deviceAssociation -> deviceAssociation.getAssociationType().equalsIgnoreCase(defaultAssociationType));
            if (!isOwner) {
                throw new ApiValidationFailedException(OWNER_TERMINATION_VALIDATION_FAILED.getCode(),
                    OWNER_TERMINATION_VALIDATION_FAILED.getMessage(),
                    OWNER_TERMINATION_VALIDATION_FAILED.getGeneralMessage());
            }
        }

        LOGGER.info("## Validation Start whether userId needs to be terminated or disassociated");

        String userIdToBeTerminatedOrDisassociated =
            StringUtils.isEmpty(deviceStatusRequest.getUserId()) ? userId : deviceStatusRequest.getUserId();

        //Validate if the userId needs to be terminated or disassociated
        Long associationId = deviceStatusRequest.getAssociationId();
        List<DeviceAssociation> deviceAssociations =
            deviceAssociationDao.constructAndFetchDeviceAssociationData(serialNumber, deviceId, imei,
                userIdToBeTerminatedOrDisassociated, associationId, statusList);
        M2Mterminate m2mTerminate = new M2Mterminate();
        if (deviceAssociations.size() == 1) {
            String associationType = EMPTY;
            try {
                associationType = deviceAssociations.get(0).getAssociationType();

            } catch (NullPointerException e) {
                // Do Nothing : associationType can be null in case of vehicle Owner
            }
            
            boolean performTerminate =
                StringUtils.isEmpty(associationType) || associationType.equals(defaultAssociationType);
            m2mTerminate.setPerformTerminate(performTerminate);

            if (m2mTerminate.isPerformTerminate()) {
                LOGGER.info("## UserId {} DeviceId {} is eligible for termination", userIdToBeTerminatedOrDisassociated,
                    deviceId);
            } else {
                LOGGER.info("## UserId {} DeviceId {} is eligible for disassociation",
                    userIdToBeTerminatedOrDisassociated, deviceId);
            }
        } else {
            throw new ApiPreConditionFailedException(M2M_ASSOC_INTEGRITY_ERROR.getCode(),
                M2M_ASSOC_INTEGRITY_ERROR.getMessage(),
                M2M_ASSOC_INTEGRITY_ERROR.getGeneralMessage());
        }
        LOGGER.info("## Validate Perform Terminate End");
        return m2mTerminate;
    }

    /**
     * Validates an admin request by checking if the user ID is empty.
     *
     * @param deviceStatusRequest The device status request object.
     * @return true if the user ID is empty, false otherwise.
     */
    public boolean validateAdminRequest(DeviceStatusRequest deviceStatusRequest) {
        return StringUtils.isEmpty(deviceStatusRequest.getUserId());
    }

    /**
     * Updates the association with the provided AssociationUpdateDto.
     *
     * @param associationUpdateDto The AssociationUpdateDto containing the updated association details.
     * @throws ApiPreConditionFailedException If the new association type is set to owner or if the user is not the
     *      owner of the device.
     * @throws ApiResourceNotFoundException If the association ID is not found.
     */
    public void updateAssociation(AssociationUpdateDto associationUpdateDto) {

        long assocId = associationUpdateDto.getAssocId();

        DeviceAssociation association = deviceAssociationDao.fetchAssociationById(assocId);

        String userId = associationUpdateDto.getUserId();
        String newAssocType = associationUpdateDto.getAssociationUpdateRequest().getAssocType();
        long newStartTime = associationUpdateDto.getAssociationUpdateRequest().getStartTime();
        long newEndTime = associationUpdateDto.getAssociationUpdateRequest().getEndTime();

        // update if association exists
        if (association != null) {
            // RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE Sonar bug
            String serialNumber = association.getSerialNumber();
            // validate startTime and endTime
            validateNewTimeWithOld(newStartTime, newEndTime, association.getStartTimeStamp(),
                association.getEndTimeStamp());

            // newAssociationType cannot be owner
            if (newAssocType.equals(defaultAssociationType)) {
                throw new ApiPreConditionFailedException(NEW_ASSOCIATION_TYPE_CANNOT_BE_UPDATED_TO_OWNER.getCode(),
                    NEW_ASSOCIATION_TYPE_CANNOT_BE_UPDATED_TO_OWNER.getMessage(),
                    NEW_ASSOCIATION_TYPE_CANNOT_BE_UPDATED_TO_OWNER.getGeneralMessage());
            } else {
                // user should be owner of device
                if (deviceAssociationDao.validateUserIsOwnerOfDevice(defaultAssociationType, userId, serialNumber)) {
                    // update dao
                    LinkedHashMap<String, Object> updatedMap = getUpdateHashMap(newAssocType, newStartTime, newEndTime);
                    LinkedHashMap<String, Object> conditionMap = new LinkedHashMap<>();
                    conditionMap.put("id", assocId);
                    deviceAssociationDao.update(updatedMap, conditionMap);
                } else {
                    // user not owner of device
                    throw new ApiPreConditionFailedException(USER_NOT_OWNER_OF_DEVICE.getCode(),
                        USER_NOT_OWNER_OF_DEVICE.getMessage(),
                        USER_NOT_OWNER_OF_DEVICE.getGeneralMessage());
                }
            }

        } else { // associationId not found
            LOGGER.info("Association Id not found");
            throw new ApiResourceNotFoundException(ASSO_DETAILS_NOT_FOUND.getCode(),
                ASSO_DETAILS_NOT_FOUND.getMessage(),
                ASSO_DETAILS_NOT_FOUND.getGeneralMessage());
        }
    }

    /**
     * Retrieves the usage count of a specific association type.
     *
     * @param assocType the association type to retrieve the usage count for
     * @param requestId the unique identifier for the request
     * @return the usage count of the association type
     */
    public Integer getAssociationTypeUsageCount(String assocType, String requestId) {
        LOGGER.info("## getAssociationTypeUsageCount - Service Layer - RequestId {} ",
            (requestId == null) ? null : requestId.replaceAll(CARRIAGE_AND_NEWLINE_REGEX, ""));
        return deviceAssociationDao.getAssociationTypeUsageCount(assocType);
    }

    /**
     * Returns a LinkedHashMap containing the update information for device association.
     *
     * @param assocType    the association type
     * @param newStartTime the new start timestamp
     * @param newEndTime   the new end timestamp
     * @return a LinkedHashMap containing the update information
     */
    private LinkedHashMap<String, Object> getUpdateHashMap(String assocType, long newStartTime, long newEndTime) {
        LinkedHashMap<String, Object> updateMap = new LinkedHashMap<>();
        if (StringUtils.isNotEmpty(assocType)) {
            updateMap.put("association_type", assocType);
        }
        if (newStartTime != 0L) {
            updateMap.put("start_timestamp", new Timestamp(newStartTime));
        }
        if (newEndTime != 0L) {
            updateMap.put("end_timestamp", new Timestamp(newEndTime));
        }
        return updateMap;
    }

    /**
     * Validates the new time range with the old time range.
     *
     * @param newStartTime The start time of the new time range.
     * @param newEndTime   The end time of the new time range.
     * @param oldStartTime The start time of the old time range.
     * @param oldEndTime   The end time of the old time range.
     */
    private void validateNewTimeWithOld(long newStartTime, long newEndTime, long oldStartTime, long oldEndTime) {
        if (newStartTime != 0L && newEndTime != 0L) {
            validateStartEndTime(newEndTime, newStartTime);
        } else if (newStartTime != 0L) {
            validateStartEndTime(oldEndTime, newStartTime);
        } else if (newEndTime != 0L) {
            validateStartEndTime(newEndTime, oldStartTime);
        }
    }

    /**
     * Retrieves the VIN (Vehicle Identification Number) associated with the given factory data
     * and sets the SIM details for the device association.
     *
     * @param factoryData The factory data containing the serial number of the device.
     * @param deviceAssociation The device association object to update with the VIN and SIM details.
     */
    private void getVinAndSetSimDetails(FactoryData factoryData, DeviceAssociation deviceAssociation) {
        if (vinAssocEnabled) {
            String vin = deviceAssociationDao.getAssociatedVin(factoryData.getSerialNumber());
            deviceAssociation.setVin(vin);
            if (vin != null && (AssociationStatus.ASSOCIATED.equals(deviceAssociation.getAssociationStatus())
                || AssociationStatus.ASSOCIATION_INITIATED.equals(deviceAssociation
                .getAssociationStatus()))) {
                String transactionStatus = getLatestSimTransactionState(deviceAssociation.getId(),
                    deviceAssociation.getImsi());
                deviceAssociation.setSimTranStatus(transactionStatus);
            }
        }
    }

    /**
     * Validates the current data retrieved from the database for device association.
     * If the current device is not to be moved to the provisioned state, it checks if the current data
     * from the database indicates a faulty or stolen device. If not, it throws an exception.
     *
     * @param currentDataFromDb the current data retrieved from the database
     * @throws ApiPreConditionFailedException if the current data state is invalid for device replacement
     */
    private void validateCurrentDataFromDb(DeviceInfoFactoryData currentDataFromDb) {
        if (!isCurrentDeviceToBeMovedToProvisioned && !currentDataFromDb.getFaulty()
                && !currentDataFromDb.getStolen()) {
            throw new ApiPreConditionFailedException(INVALID_DEVICE_REPLACEMENT_CURRENT_DATA_STATE.getCode(),
                    INVALID_DEVICE_REPLACEMENT_CURRENT_DATA_STATE.getMessage(),
                    INVALID_DEVICE_REPLACEMENT_CURRENT_DATA_STATE.getGeneralMessage());
        }
    }

    /**
     * Validates the list of replace value data for a device.
     *
     * @param replaceDeviceRequest The request object containing the data to replace with.
     * @return The validated DeviceInfoFactoryData object.
     * @throws ApiPreConditionFailedException If the current factory data for replace is invalid.
     * @throws ApiTechnicalException If there is a database integrity error.
     */
    private DeviceInfoFactoryData validateListReplaceValueData(ReplaceFactoryDataRequest replaceDeviceRequest) {
        List<DeviceInfoFactoryData> listReplaceValueData = deviceInfoFactoryDataDao
            .constructAndFetchFactoryData(convertToDeviceInfo(replaceDeviceRequest.getReplaceWith()));
        if (CollectionUtils.isEmpty(listReplaceValueData)) {
            throw new ApiPreConditionFailedException(INVALID_CURRENT_FACTORY_DATA_FOR_REPLACE.getCode(),
                INVALID_CURRENT_FACTORY_DATA_FOR_REPLACE.getMessage(),
                INVALID_CURRENT_FACTORY_DATA_FOR_REPLACE.getGeneralMessage());
        }
        if (listReplaceValueData.size() > 1) {
            LOGGER.error("Replace factory data - Database Integrity Error. There is more than one record.");
            throw new ApiTechnicalException(DATABASE_INTEGRITY_ERROR.getCode(),
                DATABASE_INTEGRITY_ERROR.getMessage(),
                ApiMessageEnum.DATABASE_INTEGRITY_ERROR.getGeneralMessage());
        }
        return listReplaceValueData.get(0);
    }

    /**
     * Performs the Spring Auth registration for a device.
     *
     * @param harmanId          The Harman ID of the device.
     * @param newDevice         The new device object.
     * @param deviceAssociation The device association object.
     * @throws ApiTechnicalException If an error occurs during the registration process.
     */
    private void performSpringAuthRegistration(String harmanId, Device newDevice,
                                               DeviceAssociation deviceAssociation) {
        LOGGER.debug("Deleting the device :{} from Spring Auth", harmanId);
        try {
            deRegisterFromSpringAuth(harmanId);
        } catch (SpringAuthClientException e) {
            Map<Object, Object> details = new LinkedHashMap<>();
            details.put(HARMAN_ID, deviceAssociation.getHarmanId());
            LOGGER.error("{}", ErrorUtils.buildError("## Error has occurred while de-registering device"
                    + " from Spring Auth", e, details));
            throw new ApiTechnicalException(DE_REGISTER_WITH_SPRINGAUTH_FAILED.getCode(),
                DE_REGISTER_WITH_SPRINGAUTH_FAILED.getMessage(),
                DE_REGISTER_WITH_SPRINGAUTH_FAILED.getGeneralMessage());
        }
        try {
            LOGGER.debug("Registering the device :{} with Spring Auth again with new passcode", harmanId);
            String authToken = springAuthTokenGenerator.fetchSpringAuthToken();
            LOGGER.info(UPDATE_REGISTERED_CLIENT_SPRING_AUTH, newDevice.getHarmanId());
            springAuthRestClient.updateRegisteredClient(authToken, newDevice.getHarmanId(), newDevice.getPasscode(),
                deviceAssociation.getDeviceType(), APPROVED);
            LOGGER.info(UPDATE_REGISTERED_CLIENT_SPRING_AUTH_SUCCEEDED, newDevice.getHarmanId());
        } catch (javax.naming.directory.InvalidAttributeValueException e) {
            Map<Object, Object> details = new LinkedHashMap<>();
            details.put(HARMAN_ID, deviceAssociation.getHarmanId());
            LOGGER.error("{}",
                ErrorUtils.buildError("## Error has occurred while registering application/device with Spring Auth", e,
                    details));
            throw new ApiTechnicalException(REGISTER_WITH_SPRINGAUTH_FAILED.getCode(),
                REGISTER_WITH_SPRINGAUTH_FAILED.getMessage(), REGISTER_WITH_SPRINGAUTH_FAILED.getGeneralMessage());
        }
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
                    "Old Device is Provisioned with its imei - " + currentDataFromDb.getImei());
            } else if (DeviceState.STOLEN.getValue().equals(currentState)
                || DeviceState.FAULTY.getValue().equals(currentState)) {
                deviceInfoFactoryDataDao.changeDeviceStateForStolenOrFaulty(currentDataFromDb.getId(),
                    DeviceState.PROVISIONED.getValue(),
                    "Old Device is Provisioned with its imei - " + currentDataFromDb.getImei());
            } else {
                LOGGER.debug("Update of current device state is not required while Current Device State is :{}",
                    currentState);
            }
        }
    }

    /**
     * Performs termination without VIN association for the given device association.
     * If the simSuspendCheck flag is true, it checks the activation and termination transaction statuses.
     * If the activation transaction is not completed or the termination transaction is not completed,
     * it throws an ApiPreConditionFailedException with the corresponding error code and message.
     *
     * @param deviceAssociation The device association to perform termination without VIN association.
     */
    private void performTerminationWithoutVinAssociation(DeviceAssociation deviceAssociation) {
        if (simSuspendCheck) {
            String activateTranStatus = deviceAssociationDao
                .getActivateTranStatus(deviceAssociation.getId()); //null ,if vin assoc not done
            //false if vin assoc not done or activation not done(may be) | true if vin assoc done
            if (activateTranStatus != null) {
                //user can perform terminate even without vin association
                if (!SimTransactionStatus.COMPLETED
                    .getSimTransactionStatus().equals(activateTranStatus)) {
                    throw new ApiPreConditionFailedException(SIM_ACTIVATION_PENDING.getCode(),
                        SIM_ACTIVATION_PENDING.getMessage(),
                        SIM_ACTIVATION_PENDING.getGeneralMessage());
                }
                String tranStatus = deviceAssociationDao.getTerminateTranStatus(deviceAssociation.getId());
                LOGGER.info("## TransactionId: {}, TransactionStatus: {}", deviceAssociation.getId(), tranStatus);
                if (!SimTransactionStatus.COMPLETED.getSimTransactionStatus().equals(tranStatus)) {
                    throw new ApiPreConditionFailedException(SIM_SUSPEND_CONDITION_FAILED.getCode(),
                        SIM_SUSPEND_CONDITION_FAILED.getMessage(),
                        SIM_SUSPEND_CONDITION_FAILED.getGeneralMessage());
                }
            }
        }
    }
}

