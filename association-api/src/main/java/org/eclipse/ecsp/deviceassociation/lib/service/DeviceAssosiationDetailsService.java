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

import com.amazonaws.util.CollectionUtils;
import com.amazonaws.util.StringUtils;
import org.eclipse.ecsp.deviceassociation.lib.dao.DeviceAssociationDao;
import org.eclipse.ecsp.deviceassociation.lib.exception.NoSuchEntityException;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.AssociationDetailsResponse;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceAssosiationDetails;
import org.eclipse.ecsp.deviceassociation.lib.rest.model.DeviceDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.eclipse.ecsp.deviceassociation.lib.dao.DeviceAssociationDao.USER_ID_FIELD;
import static org.eclipse.ecsp.deviceassociation.lib.service.MessageConstants.USER_ID_MANDATORY;

/**
 * This class provides the service for fetching device association details.
 */
@Service
public class DeviceAssosiationDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceAssosiationDetailsService.class);
    /**
     * The Data Access Object (DAO) for managing device association data.
     * This is an autowired dependency, which means it will be automatically
     * injected by the Spring framework at runtime.
     */
    @Autowired
    protected DeviceAssociationDao deviceAssociationDao;

    /**
     * Retrieves the device association details for a given user ID.
     *
     * @param userId The ID of the user.
     * @return A list of DeviceAssosiationDetails objects representing the association details.
     * @throws NoSuchEntityException If the user ID is null or empty.
     */
    public List<DeviceAssosiationDetails> getDeviceAssosiationDetails(String userId) throws NoSuchEntityException {
        if (StringUtils.isNullOrEmpty(userId)) {
            throw new NoSuchEntityException(USER_ID_MANDATORY);
        }

        Map<String, Object> attributeMap = new LinkedHashMap<>();
        attributeMap.put(USER_ID_FIELD, userId);

        final List<AssociationDetailsResponse> detailsList =
            deviceAssociationDao.fetchAssociationDetails(attributeMap, false);

        if (CollectionUtils.isNullOrEmpty(detailsList) || detailsList.get(0) == null) {
            return Collections.emptyList();
        }

        List<DeviceAssosiationDetails> assosiationDetails = new LinkedList<>();

        detailsList.forEach(details -> {
            DeviceDetail deviceDetail = details.getDeviceDetail();

            if (deviceDetail != null) {
                DeviceAssosiationDetails associationDetails = new DeviceAssosiationDetails();

                associationDetails.setAssociatedOn(details.getAssociatedOn());
                associationDetails.setAssociationStatus(details.getAssociationStatus());
                associationDetails.setDisassociatedOn(details.getDisassociatedOn());
                associationDetails.setSerialNumber(details.getSerialNumber());
                associationDetails.setAssociationId(details.getId());
                associationDetails.setBssid(deviceDetail.getBssid());
                associationDetails.setDeviceId(deviceDetail.getHarmanId());
                associationDetails.setVehicleId(details.getVehicleId());
                associationDetails.setIccid(deviceDetail.getIccid());
                associationDetails.setImei(deviceDetail.getImei());
                associationDetails.setImsi(deviceDetail.getImsi());
                associationDetails.setMsisdn(deviceDetail.getMsisdn());
                associationDetails.setSsid(deviceDetail.getSsid());

                assosiationDetails.add(associationDetails);
            } else {
                LOGGER.warn("The device association is without required device data: {}.", details);
            }
        });

        return assosiationDetails;
    }
}
