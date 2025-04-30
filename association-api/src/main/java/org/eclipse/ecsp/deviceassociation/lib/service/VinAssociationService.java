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

import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.ecsp.common.ErrorUtils;
import org.eclipse.ecsp.common.config.EnvConfig;
import org.eclipse.ecsp.deviceassociation.dto.ModelsInfo;
import org.eclipse.ecsp.deviceassociation.dto.WhiteListedModels;
import org.eclipse.ecsp.deviceassociation.lib.config.DeviceAssocationProperty;
import org.eclipse.ecsp.deviceassociation.lib.exception.ObserverMessageProcessFailureException;
import org.eclipse.ecsp.deviceassociation.lib.model.wam.SimTransactionStatus;
import org.eclipse.ecsp.deviceassociation.lib.model.wam.SimUserAction;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.SimSuspendRequest;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.VinDetails;
import org.eclipse.ecsp.deviceassociation.lib.util.Pair;
import org.eclipse.ecsp.exception.shared.ApiPreConditionFailedException;
import org.eclipse.ecsp.exception.shared.ApiTechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.DIS_ASSOC_FAILED_DURING_SIM_ACTIVATION;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.GENERAL_ERROR;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.MODEL_NOT_FOUND_IN_WHITELISTED_MODELS_EXCEPTION;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.SIM_ACTIVATION_FAILED;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.SIM_SUSPEND_FAILED;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.VIN_ALREADY_ASSO;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.VIN_ALREADY_ASSO_WITH_OTHER_DEVICE;
import static org.eclipse.ecsp.deviceassociation.lib.service.ApiMessageEnum.WHITELISTED_MODELS_IS_EMPTY;

/**
 * The `VinAssociationService` class is responsible for handling VIN association operations.
 * It provides methods to check preconditions for VIN association and SIM suspend, as well as
 * perform VIN association along with SIM activation.
 */
@Service
@Transactional
public class VinAssociationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(VinAssociationService.class);
    private static final String COUNTRY = "country";
    private static final String CARRIAGE_AND_NEWLINE_REGEX = "[\r\n]";

    @Resource(name = "envConfig")
    protected EnvConfig<DeviceAssocationProperty> config;
    @Autowired
    VehicleProfileService vehicleProfileService;
    @Autowired
    SaasApiService saasApiService;
    @Autowired
    SimStateManager simStateManager;
    @Autowired
    private DeviceAssociationService deviceAssociationService;
    @Value("${wam_enabled:false}")
    private boolean wamEnabled;
    @Value("${decode_type:CODE_VALUE}")
    private String decodeType;
    @Autowired
    private UserManagementClient userManagementClient;

    /**
     * Checks the preconditions for VIN association.
     *
     * @param userId The user ID.
     * @param vin The VIN (Vehicle Identification Number).
     * @param imei The IMEI (International Mobile Equipment Identity).
     * @return The device association ID.
     * @throws ApiPreConditionFailedException If any of the preconditions fail.
     */
    public Long checkVinAssociationPreconditions(String userId, String vin, String imei) {

        //Check if vin is present in the whitelist model
        vinExistsInWhiteListModels(vin, userId);

        // Check if the device is associated with the user
        Long deviceAssociationId = deviceAssociationService.associationExists(userId, imei);
        if (deviceAssociationId == null) {
            throw new ApiPreConditionFailedException(ApiMessageEnum.ASSO_NOT_FOUND.getCode(),
                ApiMessageEnum.ASSO_NOT_FOUND.getMessage());
        }
        // Check if vin-association already done for the device
        boolean vinAssociated = deviceAssociationService.getVinAssociation(deviceAssociationId);
        if (vinAssociated) {
            throw new ApiPreConditionFailedException(ApiMessageEnum.VIN_ALREADY_ASSO.getCode(),
                ApiMessageEnum.VIN_ALREADY_ASSO.getMessage(), VIN_ALREADY_ASSO.getGeneralMessage());
        }
        // Check if the vin is already in use
        boolean vinExists = deviceAssociationService.vinAlreadyAssociated(vin);
        if (vinExists) {
            throw new ApiPreConditionFailedException(ApiMessageEnum.VIN_ALREADY_ASSO_WITH_OTHER_DEVICE.getCode(),
                ApiMessageEnum.VIN_ALREADY_ASSO_WITH_OTHER_DEVICE.getMessage(),
                VIN_ALREADY_ASSO_WITH_OTHER_DEVICE.getGeneralMessage());
        }
        LOGGER.debug("## checkVinAssociationPreconditions - END deviceAssociationId: {}", deviceAssociationId);
        return deviceAssociationId;
    }

    /**
     * Checks if the given VIN (Vehicle Identification Number) exists in the whitelist models for a specific user.
     *
     * @param vin    The VIN to check.
     * @param userId The ID of the user.
     * @throws ApiTechnicalException           If the whitelist models list is empty.
     * @throws ApiPreConditionFailedException  If the VIN is not found in the whitelist models.
     */
    public void vinExistsInWhiteListModels(String vin, String userId) {

        String region = userManagementClient.getUserDetail(userId, COUNTRY);
        String regionData = region.replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "");
        String userData = userId.replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "");
        LOGGER.info("found user country : {}, for userId : {}", regionData, userData);

        // Get Whitelist models from systemParameters
        WhiteListedModels whiteListedModels = saasApiService.getWhiteListedModels();
        LOGGER.info("fetched white Listed Models list : {}", whiteListedModels);

        // Hit Vehicle Profile VIN Decode API to decode vin and get model details
        VinDecodeResponse vinDecodeResponse = vehicleProfileService.decodeVinByType(vin, decodeType);

        if (CollectionUtils.isEmpty(whiteListedModels.getWhiteListedModels())) {
            throw new ApiTechnicalException(WHITELISTED_MODELS_IS_EMPTY.getCode(),
                WHITELISTED_MODELS_IS_EMPTY.getMessage(),
                WHITELISTED_MODELS_IS_EMPTY.getGeneralMessage());
        }

        // If its not part of whitelist models throw error
        boolean presentInWhitelistModel = whiteListedModels.getWhiteListedModels().stream().anyMatch(whiteListedModel ->
            whiteListedModel.getModelCode().equalsIgnoreCase(vinDecodeResponse.getModelCode())
                && whiteListedModel.getModelName().equalsIgnoreCase(vinDecodeResponse.getModelName())
                && region.equalsIgnoreCase(whiteListedModel.getCountryCode()));

        LOGGER.info("presentInWhitelistModel : {}", presentInWhitelistModel);

        if (!presentInWhitelistModel) {
            throw new ApiPreConditionFailedException(MODEL_NOT_FOUND_IN_WHITELISTED_MODELS_EXCEPTION.getCode(),
                MODEL_NOT_FOUND_IN_WHITELISTED_MODELS_EXCEPTION.getMessage(),
                MODEL_NOT_FOUND_IN_WHITELISTED_MODELS_EXCEPTION.getGeneralMessage());
        }
    }

    /**
     * Checks the preconditions for suspending a SIM card.
     *
     * @param userId the user ID
     * @param imei the IMEI number of the device
     * @return the ID of the device association
     * @throws ApiPreConditionFailedException if the device association is not found or if the SIM activation is not
     *      completed
     */
    public Long checkSimSuspendPreconditions(String userId, String imei) {
        LOGGER.debug("## checkSimSuspendPreconditions - START userId: {}, imei: {}", userId, imei);
        // Check if the device is imei to user associated
        Long deviceAssociationId = deviceAssociationService.associationExists(userId, imei);
        if (deviceAssociationId == null) {
            throw new ApiPreConditionFailedException(ApiMessageEnum.ASSO_NOT_FOUND.getCode(),
                ApiMessageEnum.ASSO_NOT_FOUND.getMessage());
        }
        // check if sim activation is completed
        String tranStatus = deviceAssociationService.getActiveTranStatus(deviceAssociationId);
        LOGGER.debug("## TransactionId: {}, TransactionStatus: {}", deviceAssociationId, tranStatus);
        if (!SimTransactionStatus.COMPLETED.getSimTransactionStatus().equals(tranStatus)) {
            throw new ApiPreConditionFailedException(SIM_SUSPEND_FAILED.getCode(), SIM_SUSPEND_FAILED.getMessage(),
                SIM_SUSPEND_FAILED.getGeneralMessage());
        }
        LOGGER.debug("## checkSimSuspendPreconditions - END deviceAssociationId: {}", deviceAssociationId);
        return deviceAssociationId;
    }

    /**
     * Performs a pre-condition check for decoding a VIN (Vehicle Identification Number).
     *
     * @param vin The VIN to be decoded.
     * @param factoryDataModel The factory data model.
     * @param userId The user ID.
     * @param assocId The association ID.
     */
    public void decodingPreConditionCheck(String vin, String factoryDataModel, String userId, Long assocId) {
        LOGGER.debug("## decodingPreConditionCheck - START vin: {}, assocId: {}", vin, assocId);
        Pair<String, String> decodeVinPair = vehicleProfileService.decodeVin(vin);
        if (decodeVinPair != null) {
            String modelCode = decodeVinPair.getElement1();
            String modelName = decodeVinPair.getElement2();
            String countryName = "Thailand";
            // Dongle Color check
            String preDefinedDongleType = getPreDefinedDongleType(modelCode, modelName, countryName);
            String factoryDongleType = getFactoryDongleType(factoryDataModel);
            if (preDefinedDongleType == null || !preDefinedDongleType.equals(factoryDongleType)) {
                LOGGER.error("Predefined dongle type is not matched with factory dongle type, preDefinedDongleType: {} "
                        + "factoryDongleType: {}  for modelCode: {}  and ModelName:{}", preDefinedDongleType,
                    factoryDongleType,
                    modelCode, modelName);
                //Disassociate device
                try {
                    deviceAssociationService.disassociate(assocId, userId);
                } catch (ObserverMessageProcessFailureException e) {
                    throw new ApiTechnicalException(ApiMessageEnum.DONGLE_TYPE_MISMATCHED.getCode(),
                        ApiMessageEnum.DONGLE_TYPE_MISMATCHED.getMessage(),
                        ApiMessageEnum.DONGLE_TYPE_MISMATCHED.getGeneralMessage(),
                        e);
                }
            }
        }
        LOGGER.debug("## decodingPreConditionCheck - END");
    }

    /**
     * Retrieves the factory dongle type based on the given factory data model.
     *
     * @param factoryDataModel The factory data model to determine the dongle type.
     * @return The factory dongle type. Possible values are "ORANGE", "GREEN", or an empty string.
     */
    private String getFactoryDongleType(String factoryDataModel) {
        LOGGER.debug("## getFactoryDongleType - START factoryDataModel: {}", factoryDataModel);
        String factoryDongleType;
        if (factoryDataModel.contains("HSA-15TN-SA")) {
            factoryDongleType = "ORANGE";
        } else if (factoryDataModel.contains("HSA-15TN-SB")) {
            factoryDongleType = "GREEN";
        } else {
            factoryDongleType = "";
        }
        LOGGER.debug("## getFactoryDongleType - END factoryDongleType: {}", factoryDongleType);
        return factoryDongleType;
    }

    /**
     * Retrieves the pre-defined dongle type based on the given model code, model name, and country name.
     *
     * @param modelCode    The model code of the device.
     * @param modelName    The model name of the device.
     * @param countryName  The country name.
     * @return The pre-defined dongle type associated with the given model code and model name, or null if not found.
     */
    private String getPreDefinedDongleType(String modelCode, String modelName, String countryName) {
        LOGGER.debug("## getPreDefinedDongleType - START modelCode: {}, modelName: {}, countryName: {}", modelCode,
            modelName, countryName);
        List<ModelsInfo> modelDetailsList = saasApiService.getStaticModelDetailsFromSystemParameter(countryName);
        if (modelDetailsList != null && !modelDetailsList.isEmpty()) {
            for (ModelsInfo modelsInfo : modelDetailsList) {
                String staticModelCode = modelsInfo.getModelCode();
                String staticModelName = modelsInfo.getModelName();
                String staticDongleType = modelsInfo.getModelCode();
                if (modelCode.equals(staticModelCode) && modelName.equals(staticModelName)) {
                    return staticDongleType;
                }
            }
        }
        LOGGER.debug("## getPreDefinedDongleType - END");
        return null;
    }

    /**
     * Associates a VIN (Vehicle Identification Number) with a user and a device.
     *
     * @param userId              The ID of the user.
     * @param request             The VIN details containing the VIN, IMEI, and region.
     * @param deviceAssociationId The ID of the device association.
     */
    public void vinAssociate(String userId, VinDetails request, Long deviceAssociationId) {
        String imei = request.getImei();
        String vin = request.getVin();
        String region = request.getRegion();
        String deviceAssociationIdData =
                deviceAssociationId.toString().replaceAll(CARRIAGE_AND_NEWLINE_REGEX, "");
        LOGGER.debug("## vinAssociate - START userId: {}, vin: {}, imei: {}, deviceAssociationId: {}",
            (userId == null) ? null : userId.replaceAll(CARRIAGE_AND_NEWLINE_REGEX, ""),
            (vin == null) ? null : vin.replaceAll(CARRIAGE_AND_NEWLINE_REGEX, ""),
            (imei == null) ? null : imei.replaceAll(CARRIAGE_AND_NEWLINE_REGEX, ""),
            deviceAssociationIdData);

        if (wamEnabled) {
            String imsi = deviceAssociationService.getImsi(imei);
            String transactionId = simStateManager.changeSimState(imsi, region, SimState.ACTIVE); //bypass this
            LOGGER.info("## Vin association flow - SIM activation initiated via WAM with transactionId: {}",
                transactionId);
            if (transactionId != null && !"".equals(transactionId)) {
                deviceAssociationService.saveSimDetails(transactionId, deviceAssociationId,
                    SimTransactionStatus.IN_PROGRESS.getSimTransactionStatus(),
                    SimUserAction.ACTIVATE.getSimUserAction(),
                    new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
                LOGGER.info("## Vin association completed successfully.");
            } else {
                LOGGER.error("## transactionId is null so disassociating device");
                try {
                    deviceAssociationService.disassociate(deviceAssociationId, userId);
                } catch (ObserverMessageProcessFailureException e) {
                    Map<Object, Object> details = new LinkedHashMap<>();
                    details.put("imei", imei);
                    details.put("userId", userId);
                    details.put(ErrorUtils.ERROR_CODE_KEY, GENERAL_ERROR.getCode());
                    LOGGER.error("## disassociate failed when MNO transaction id is null or empty.");
                    LOGGER.error("{}",
                        ErrorUtils.buildError(
                            "## Error has occurred while performing device disassociation when MNO transaction"
                                + " id is null or empty", e, details));
                    throw new ApiPreConditionFailedException(DIS_ASSOC_FAILED_DURING_SIM_ACTIVATION.getCode(),
                        DIS_ASSOC_FAILED_DURING_SIM_ACTIVATION.getMessage(),
                        DIS_ASSOC_FAILED_DURING_SIM_ACTIVATION.getGeneralMessage());
                }
                throw new ApiPreConditionFailedException(SIM_ACTIVATION_FAILED.getCode(),
                    SIM_ACTIVATION_FAILED.getMessage());
            }
        }
        deviceAssociationService.saveVinDetails(vin, region, deviceAssociationId);
        LOGGER.debug("## vinAssociate - END");
    }

    /**
     * Suspends the SIM card associated with the given user ID, using the provided SIM suspend request and device
     * association ID.
     *
     * @param userId              The ID of the user.
     * @param request             The SIM suspend request.
     * @param deviceAssociationId The ID of the device association.
     * @throws ApiTechnicalException If an error occurs during the SIM suspension process.
     */
    public void simSuspend(String userId, SimSuspendRequest request, Long deviceAssociationId) {
        LOGGER.debug("## simSuspend - START userId: {}",
                (userId == null) ? null : userId.replaceAll(CARRIAGE_AND_NEWLINE_REGEX, ""));
        String imsi = deviceAssociationService.getImsi(request.getImei());
        //region is equivalent to countryCode in vin_details table
        String region = deviceAssociationService.getRegion(deviceAssociationId);
        String transactionId = null;
        if (StringUtils.isNotEmpty(imsi) && StringUtils.isNotEmpty(region)) {
            transactionId = simStateManager.changeSimState(imsi, region, SimState.SUSPEND);
        }
        if (transactionId != null && !"".equals(transactionId)) {
            deviceAssociationService
                .saveSimDetails(transactionId, deviceAssociationId,
                    SimTransactionStatus.IN_PROGRESS.getSimTransactionStatus(),
                    SimUserAction.TERMINATE.getSimUserAction(),
                    new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
            LOGGER.debug("## sim Suspend completed successfully.");
        } else {
            LOGGER.debug("## transactionId is null so cannot suspend sim");
            throw new ApiTechnicalException(GENERAL_ERROR.getCode(),
                GENERAL_ERROR.getMessage(),
                GENERAL_ERROR.getGeneralMessage());
        }
        LOGGER.debug("## simSuspend - END");
    }
}